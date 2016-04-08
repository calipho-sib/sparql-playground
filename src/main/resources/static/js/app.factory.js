(function (angular, undefined) {'use strict';

/*
 * create snorql service
 */

angular.module('snorql.service',[])
.factory('snorql', snorql);


//
// implement snorql factory
snorql.$inject=["$http", "$q", "$timeout", "$location", "config"]
function snorql($http, $q, $timeout, $location, config) {

  var defaultSnorql={
    property:'SELECT DISTINCT ?resource ?value\n' +
                  'WHERE { ?resource <URI_COMPONENT> ?value }\n' +
                  'ORDER BY ?resource ?value',

    clazz :  'SELECT DISTINCT ?instance\n' +
                  'WHERE { ?instance a <URI_COMPONENT> }\n' +
                  'ORDER BY ?instance',

    describe:'SELECT DISTINCT ?property ?hasValue ?isValueOf\n' +
                  'WHERE {\n' +
                  '  { <URI_COMPONENT> ?property ?hasValue }\n' +
                  '  UNION\n' +
                  '  { ?isValueOf ?property <URI_COMPONENT> }\n' +
                  '}\n' +
                  'ORDER BY (!BOUND(?hasValue)) ?property ?hasValue ?isValueOf',

    description:   'Here is a an example on how to get the first 10 rows of a dataset. \nClick on the examples on the right to continue your journey about learning SPARQL.',


    title:"Extract some data",
    query:'SELECT DISTINCT * WHERE {\n  ?s ?p ?o\n}\nLIMIT 10\n\n\n# When doing such a query it is important to set LIMIT 10.\n# This limit avoids performance issues, if the size of the dataset is unknown.',

    // set your endpoint here
    sparqlEndpoint:config.sparql.endpoint,
    sparqlUrlExamples:config.sparql.examples,
    sparqlUrlPrefixes:config.sparql.prefixes,
    sparqlUrlData:config.sparql.dataURL
  };


  var defaultSparqlParams={
    'default-graph-uri':null,
    'named-graph-uri':null,
     output:'json',
  };

  var defaultAcceptHeaders={
    html:'application/sparql-results+json,*/*',
    json:'application/sparql-results+json,*/*',
    xml:'application/sparql-results+xml,*/*',
    csv:'application/sparql-results+csv,*/*'
  };

  //
  // serialize prefixes
  var query_getPrefixes = function(pfxs) {
    var prefixes = '';
    for (var prefix in pfxs) {
        var uri = pfxs[prefix];
        prefixes = prefixes + 'PREFIX ' + prefix + ': <' + uri + '>\n';
    }
    return prefixes;
  };


  var Snorql=function(){
    //
    // this service depend on two $resources (eg. dao in Java world)
    // this.$dao={queries:$resource('queries.json'), sparqlQuery:$resource('sparql.json')};


    // queries examples
    this.examples=[];
    // examples tags
    this.tags=[];

    //frequently asked questions
    this.faqs=[];

    this.prefixes="";
    
    // ttl data
    this.data="";
    
    // initial sparql result
    this.result={head:[],results:[]};

    // initial sparql query
    this.query=defaultSnorql.query;

    // initial selected query id
    this.selectedQueryId = 0;

    // initial selected query title
    this.queryTitle = defaultSnorql.title;
    
    // initial url for examples
    this.examplesUrl=defaultSnorql.sparqlUrlExamples;

    // initial url for examples
    this.dataURL=defaultSnorql.sparqlUrlData;

    // initial url for prefixes
    this.prefixesUrl=defaultSnorql.sparqlUrlPrefixes;

    
    this.description = defaultSnorql.description;
    //
    // wrap promise to this object
    this.$promise=$q.when(this);

    //
    // manage cancel
    this.canceler = $q.defer();
  };

  Snorql.prototype.reset=function(){
    this.canceler.resolve()
    this.result={head:[],results:[]};
    this.canceler = $q.defer();
  };

  Snorql.prototype.endpoint=function(){
    return defaultSnorql.sparqlEndpoint;
};

  //
  // load sparql examples
  Snorql.prototype.loadExamples=function(){
   var self=this;
   if(this.examples.length){
     return this;
   }
   this.$promise=this.$promise.then(function(){
       return $http({method:'GET',url:self.examplesUrl});
   });

   this.$promise.then(function(config){
      var index=0, rawtags=[];
      self.examples=(config.data);
      self.examples.forEach(function(example){
        example.index=index++;
        if(!example.tags)
          return
        //
        // considering multiple tags
        example.tags.forEach(function(tag){
          if(self.tags.indexOf(tag.trim())==-1){
            self.tags.push(tag)
          }
        })
      })
   });

   return this;
  };

  // load sparql examples
  Snorql.prototype.loadPrefixes=function(){
   var self=this;
   if(this.prefixes != ""){
     return this;
   }
   this.$promise=this.$promise.then(function(){
       return $http({method:'GET',url:self.prefixesUrl});
   });

   this.$promise.then(function(response){
      self.prefixes=(response.data);
   });

   return this;
  };
  
//load sparql examples
  Snorql.prototype.loadFaqs=function(){
	   var self=this;
	   return $http.get(config.sparql.faqsURL).then(function(response){
		     self.faqs=(response.data);
	   })
  };

  // load sparql examples
  Snorql.prototype.loadData=function(){
   var self=this;
   console.log(this.dataURL);
   return $http.get(this.dataURL).then(function(response){
	     self.data=(response.data);
   })

  };

  
  Snorql.prototype.pushData=function(){
	   var self=this;
	   console.log(this.dataURL);
	   return $http({
		   url: this.dataURL,
		   method: 'PUT',
		   params: {data : self.data}}).then(function(response){
		   alert(response.data + " triples successfully loaded");
	   }, function(error){
		  alert("error " + error.data.responseText);
	   })
  };

	  
  // manage default snorql state
  Snorql.prototype.updateQuery=function(params){
    if(params.class){
      this.query=defaultSnorql['class'].replace(/URI_COMPONENT/g,params.class);
    }else
    if(params.property){
      this.query=defaultSnorql['property'].replace(/URI_COMPONENT/g,params.property);
    }else
    if(params.describe){
      this.query=defaultSnorql['describe'].replace(/URI_COMPONENT/g,params.describe);
    }else{
      this.query=params.query||defaultSnorql.query;
    }
    return this.query
  }


  //
  // start a sparql query,
  //  http filter define : query* (default), describe, class, property and output=json* (default)
  Snorql.prototype.executeQuery=function(sparql,filter){
   var self=this;
   if (!sparql||sparql==='')
      return self;

   this.reset();
   var params=angular.extend(defaultSparqlParams,filter, {query:sparql});

   // setup prefixes
   params.query=query_getPrefixes(this.prefixes)+'\n'+params.query

   var accept={'Accept':defaultAcceptHeaders[params.output]};

   var url=defaultSnorql.sparqlEndpoint;


   /*if(params.output!=='html'){
     self.reset();
     var deferred = $q.defer();
     window.location =url+ '?'+$.param(params);
     this.$promise=deferred.promise;
     $timeout(function(){
       deferred.resolve(this);
     },200)
     return self;
   }*/
   
   if(params.output!=='html'){
	   
	   $http({method:'GET', url:url,params:params,headers:accept, timeout: this.canceler.promise}).
	   success(function(data, status, headers, config) {
	      var anchor = angular.element('<a/>');
	      var dataAux = data;
	      if(params.output === "json"){
	    	  dataAux = JSON.stringify(data, null, 2);
	      }
	      var dataaux;
	      anchor.attr({
	          href: 'data:attachment/' +  accept + ';charset=utf-8,' + encodeURI(dataAux),
	          target: '_blank',
	          download: 'result.' + params.output
	      })[0].click();
	   }).
	   error(function(data, status, headers, config) {
	     // handle error
	   });
      return self;

   }

   //
   // html output is done by parsing json
   params.output='json'
   this.$promise=$http({method:'GET', url:url,params:params,headers:accept, timeout: this.canceler.promise});
   this.$promise.then(function(config){
      self.result=(config.data);
      console.log(self.result);
   })
   return this;
  }

  // access the singleton
  Snorql.prototype.getPrefixes=function(){
    return this.prefixes;
  }

  /**
   * SPARQLResultFormatter: Renders a SPARQL/JSON result set into an HTML table.
   */
  Snorql.prototype.SPARQLResultFormatter=function() {
      return new (function(result, namespaces){
        this._json = result;
        this._variables = this._json.head['vars']||{};
        this._boolean = this._json.boolean;
        if(this._boolean === undefined) {
            this._results = this._json.results['bindings']||[];
        }else {
            this._variables = ["boolean"];
        }

        this._namespaces = namespaces;

        this.toDOM = function() {
            var table = document.createElement('table');
            table.className = 'queryresults';
            table.appendChild(this._createTableHeader());
            if(this._boolean === undefined) {
                for (var i = 0; i < this._results.length; i++) {
                    table.appendChild(this._createTableRow(this._results[i], i));
                }
            }else { //ASK query
                table.appendChild(this._createTableBooleanRow(this._boolean));
            }
            return table;
        }

        // TODO: Refactor; non-standard link makers should be passed into the class by the caller
        this._getLinkMaker = function(varName) {
          //console.log(varName);
            if (varName == 'property') {
                return function(uri) { return '?property=' + encodeURIComponent(uri); };
            } else if (varName == 'class') {
                return function(uri) { return '?class=' + encodeURIComponent(uri); };
            } else {
                return function(uri) { return '?describe=' + encodeURIComponent(uri); };
            }
        }

        this._createTableHeader = function() {
            var tr = document.createElement('tr');
            var hasNamedGraph = false;
            for (var i = 0; i < this._variables.length; i++) {
                var th = document.createElement('th');
                th.appendChild(document.createTextNode(this._variables[i]));
                tr.appendChild(th);
                if (this._variables[i] == 'namedgraph') {
                    hasNamedGraph = true;
                }
            }
            if (hasNamedGraph) {
                var th = document.createElement('th');
                th.appendChild(document.createTextNode(' '));
                tr.insertBefore(th, tr.firstChild);
            }
            return tr;
        }
        
        this._createTableBooleanRow = function(boolean) {
            var tr = document.createElement('tr');
            tr.className = 'odd';
            var namedGraph = null;
            var td = document.createElement('td');
            td.appendChild(this._formatNode({value: boolean, type: "literal"}, "boolean"));
            tr.appendChild(td);
            return tr;
        }

        this._createTableRow = function(binding, rowNumber) {
            var tr = document.createElement('tr');
            if (rowNumber % 2) {
                tr.className = 'odd';
            } else {
                tr.className = 'even';
            }
            var namedGraph = null;
            for (var i = 0; i < this._variables.length; i++) {
                var varName = this._variables[i];
                td = document.createElement('td');
                td.appendChild(this._formatNode(binding[varName], varName));
                tr.appendChild(td);
                if (this._variables[i] == 'namedgraph') {
                    namedGraph = binding[varName];
                }
            }
            if (namedGraph) {
                var link = document.createElement('a');
                link.href = 'javascript:snorql.switchToGraph(\'' + namedGraph.value + '\')';
                link.appendChild(document.createTextNode('Switch'));
                var td = document.createElement('td');
                td.appendChild(link);
                tr.insertBefore(td, tr.firstChild);
            }
            return tr;
        }

        this._formatNode = function(node, varName) {
            if (!node) {
                return this._formatUnbound(node, varName);
            }
            if (node.type == 'uri') {
                return this._formatURI(node, varName);
            }
            if (node.type == 'bnode') {
                return this._formatBlankNode(node, varName);
            }
            if (node.type == 'literal') {
                return this._formatPlainLiteral(node, varName);
            }
            if (node.type == 'typed-literal') {
                return this._formatTypedLiteral(node, varName);
            }
            return document.createTextNode('???');
        }

        this._formatURI = function(node, varName) {
            var span = document.createElement('span');
            span.className = 'uri';
            var a = document.createElement('a');
            a.href = this._getLinkMaker(varName)(node.value);
            a.title = '<' + node.value + '>';
            a.className = 'graph-link';
            var qname = this._toQName(node.value);
            if (qname) {
                a.appendChild(document.createTextNode(qname));
                span.appendChild(a);
                if((qname.indexOf('entry') == 0) || (qname.indexOf('iso') == 0)) {

                  var spacer = document.createTextNode(' --- ');
                  span.appendChild(spacer);
                  var a2 = document.createElement('a');
                  a2.href = node.value.replace("rdf","db").replace("isoform","entry");
                  a2.title = '< View in neXtProt >';
                  a2.className = 'url';
                  a2.target = '_blank'; // Opens in new tab
                  a2.appendChild(document.createTextNode(' (neXtProt link) '));
                  span.appendChild(a2);
                }
            } else {
              // embed image object
                match = node.value.match(/\.(png|gif|jpg)(\?.+)?$/);
                if (match) {
                    img = document.createElement('img');
                    img.src =node.value;
                    img.title = node.value;
                    img.className = 'media';

                    a.appendChild(img);
                    span.appendChild(a);
                }else{
                  a.appendChild(document.createTextNode(node.value));
                  span.appendChild(document.createTextNode('<'));
                  span.appendChild(a);
                  span.appendChild(document.createTextNode('>'));

                }

            }
            var match = node.value.match(/^(https?|ftp|mailto|irc|gopher|news):/);
            if (match) {
                span.appendChild(document.createTextNode(' '));
                var externalLink = document.createElement('a');
                externalLink.href = node.value;
                span.appendChild(externalLink);
            }



            return span;
        }

        this._formatPlainLiteral = function(node, varName) {
            var text = '"' + node.value + '"';
            if (node['xml:lang']) {
                text += '@' + node['xml:lang'];
            }
            return document.createTextNode(text);
        }

        this._formatTypedLiteral = function(node, varName) {
            var text = '"' + node.value + '"';
            if (node.datatype) {
                text += '^^' + this._toQNameOrURI(node.datatype);
            }
            if (this._isNumericXSDType(node.datatype)) {
                var span = document.createElement('span');
                span.title = text;
                span.appendChild(document.createTextNode(node.value));
                return span;
            }
            return document.createTextNode(text);
        }

        this._formatBlankNode = function(node, varName) {
            return document.createTextNode('_:' + node.value);
        }

        this._formatUnbound = function(node, varName) {
            var span = document.createElement('span');
            span.className = 'unbound';
            span.title = 'Unbound'
            span.appendChild(document.createTextNode('-'));
            return span;
        }

        this._toQName = function(uri) {
            for (var prefix in this._namespaces) {
                var nsURI = this._namespaces[prefix];
                if (uri.indexOf(nsURI) == 0) {
                    return prefix + ':' + uri.substring(nsURI.length);
                }
            }
            return null;
        }

        this._toQNameOrURI = function(uri) {
            var qName = this._toQName(uri);
            return (qName == null) ? '<' + uri + '>' : qName;
        }

        this._isNumericXSDType = function(datatypeURI) {
            for (var i = 0; i < this._numericXSDTypes.length; i++) {
                if (datatypeURI == this._xsdNamespace + this._numericXSDTypes[i]) {
                    return true;
                }
            }
            return false;
        }
        this._xsdNamespace = 'http://www.w3.org/2001/XMLSchema#';
        this._numericXSDTypes = ['long', 'decimal', 'float', 'double', 'int',
            'short', 'byte', 'integer', 'nonPositiveInteger', 'negativeInteger',
            'nonNegativeInteger', 'positiveInteger', 'unsignedLong',
            'unsignedInt', 'unsignedShort', 'unsignedByte'];
      })(this.result, this.getPrefixes())
  }


  return new Snorql()
};


})(angular);

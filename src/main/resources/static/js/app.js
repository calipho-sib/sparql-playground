(function (angular, undefined) {'use strict';
/**
 * create application snorql and load deps
 */
var app = angular.module('snorql', [
    'ngRoute',
    'ngResource',
    'npHelp',
    'ui.codemirror',
    'snorql.config',
    'snorql.service',
    'snorql.ui',
    'angular-jwt',  // token
    'ipCookie'      // cookie
]).controller('SnorqlCtrl',SnorqlCtrl)
  .factory('errorInterceptor',errorInterceptor)
  .config(appConfig)
  .run(appRun)


// init app
appRun.$inject=['gitHubContent', 'config']
function appRun(gitHubContent, config) {
  gitHubContent.initialize({
        // baseUrl:"http://uat-web2:8080",
        helpPath:'rdfhelp.json',
        helpTitle:'Documentation',
        root:'help', // specify the root of RDF entity routes
        githubRepo: '/',
        githubApi:window.location.origin,
        githubEditPage : "",
        githubToken : null
    });
};

//
// implement controller SnorqlCtrl
SnorqlCtrl.$inject=['$scope','$timeout','$window','$location','snorql','config']
function SnorqlCtrl( $scope,  $timeout, $window, $location,  snorql,  config) {
  //
  // go home link
  $scope.home=config.home;
  $scope.pushState=config.pushState;

  //
  // snorql service provide examples, examples tags, config and executeQuery
  $scope.snorql=snorql;

  //
  // setup default output
  $scope.outputs=['html','json','csv','tsv','xml'];
  $scope.output='html';
  $scope.showPrefixes = false;

  //
  // default message
  $scope.message="Executing query ...";

  $scope.waiting=false;
  $scope.filter=""
  $scope.filterTag = null;

  // codemirror option
  $scope.cmOption = {
    lineNumbers: false,
    indentWithTabs: true,
    uiRefresh:true,
    mode:'sparql'
  };
  
  
  $scope.executeFaq = function (faq){
      $scope.executionTime=false;
      $scope.waiting=false;
      $scope.error=false;
	  $location.url("/");
	  snorql.queryTitle = faq.title;
	  snorql.description = faq.description;
	  snorql.result = null;
	  $location.search("query", faq.sparql);
  };


  // vocabulary query
  var vocSparqlQuery='SELECT DISTINCT * WHERE { ?term rdfs:label ?label ; a ?type . filter(regex(?label,"^__CV__","i")) } LIMIT 30';
  $scope.searchTerm=function(term){
      var time=Date.now();
      $scope.executionTime=false;
      $scope.waiting=true;
      $scope.error=false;
      snorql.executeQuery(vocSparqlQuery.replace('__CV__',term), {output:'html'}).$promise.then(function(){
        $scope.waiting=false;
        $scope.executionTime=(Date.now()-time)/1000;
      },function(reason){
        $scope.error=reason.data.message || reason.data.responseText
        $scope.waiting=false
      });
  }

  $scope.executeQuery=function(sparql,output){
    var time=Date.now();
    $scope.executionTime=false;
    $scope.waiting=true;
    $scope.error=false;
    $location.search('query',sparql)
    $location.search('class',null)
    $location.search('property',null)
    $location.search('describe',null)
    $location.search('output',output)
 
    var params=angular.extend($location.search(),{output:output});
    snorql.executeQuery(sparql, params).$promise.then(function(){
      $scope.waiting=false;
      $scope.executionTime=(Date.now()-time)/1000;
    },function(reason){
      $scope.error=reason.data.message || reason.data.responseText
      $scope.waiting=false
    });
  };

  $scope.selectExample=function(elm, hideSparql){
	  if(hideSparql){
		  snorql.query="SELECT ... ";
	  }else {
		    snorql.query=snorql.examples[elm].sparql;
	  }
	  
    snorql.description=snorql.examples[elm].description;
    snorql.selectedQueryId=snorql.examples[elm].userQueryId;
    snorql.queryTitle=snorql.examples[elm].userQueryId + ") " + snorql.examples[elm].title;
    $scope.qSelected=elm
    $('.row-offcanvas').removeClass('active')
  };

  $scope.setFilterTag=function(tag){
    $scope.filterTag=tag;
  };

  $scope.resetFilters=function(){
    $scope.filterTag=null;
  };

  $scope.reset=function(){
    snorql.reset();
  };

  $scope.login=function(){
    user.login();
  };

  $scope.logout=function(){
    user.logout();
  };
  
  $scope.pushData =function(){
	  snorql.pushData();
   };

  //
  // load api stuff
  snorql.loadPrefixes();
  snorql.loadExamples();
  snorql.loadFaqs();
  snorql.loadData();
  
  //
  // kind of queries,
  // query, describe, class, property
  snorql.updateQuery($location.search())
  // $scope.executeQuery(snorql.updateQuery($location.search()));
  $scope.$on('$locationChangeSuccess',function(url){
    snorql.updateQuery($location.search())

      if($location.path()==='/') {
          $window.document.title = "SPARQL playground";
      }

  })
};


/**
 * ANGULAR BOOTSTRAP
 */
appConfig.$inject=['$routeProvider','$locationProvider','$httpProvider']
function appConfig($routeProvider, $locationProvider, $httpProvider) {


    // intercept errors
    $httpProvider.interceptors.push('errorInterceptor')


    // List of routes of the application
    $routeProvider
        // Home page
        .when('/', {title: 'welcome to snorql', reloadOnSearch: false, templateUrl: '/partials/home.html'})
        //TTL Data
        .when('/data',{title: 'TTL Data', templateUrl: '/partials/data.html'})
        // COPYRIGHT
        .when('/faq',{title: 'Explore data', templateUrl: '/partials/faq.html'})
        // Pages (in nextprot-docs/pages): about, copyright...
        .when('/:article', {title: 'page', templateUrl: '/partials/page.html'})
        //// Help pages
        // GENERALITIES
        .when('/help/doc/:article',{title: 'help for snorql', templateUrl: '/partials/doc.html'})
        // RDF ENTITIES
        .when('/help/entity/:entity',{title: 'help for snorql', templateUrl: '/partials/help.html'})


    // Without serve side support html5 must be disabled.
    $locationProvider.html5Mode(true);
    //$locationProvider.hashPrefix = '!';
};

errorInterceptor.$inject=['$q', '$rootScope', '$location']
function errorInterceptor($q, $rootScope, $location) {
    return {
        request: function (config) {
            return config || $q.when(config);
        },
        requestError: function(request){
            return $q.reject(request);
        },
        response: function (response) {
            return response || $q.when(response);
        },
        responseError: function (response) {
            if (response && response.status === 0) {
              $rootScope.error="The API is not accessible";
            }
            if (response && response.status === 401) {
              $rootScope.error="You are not authorized to access the resource. Please login or review your privileges.";
            }
            if (response && response.status === 404) {
              $rootScope.error="URL not found";
            }
            if (response && response.status >= 500) {
              $rootScope.error="Request Failed";
            }
            return $q.reject(response);
        }
    };
};



})(angular);

package ch.isb.sib.sparql.tutorial.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.http.server.ProtocolUtil;
import org.openrdf.http.server.repository.BooleanQueryResultView;
import org.openrdf.http.server.repository.GraphQueryResultView;
import org.openrdf.http.server.repository.QueryResultView;
import org.openrdf.http.server.repository.RepositoryController;
import org.openrdf.http.server.repository.TupleQueryResultView;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.resultio.BooleanQueryResultWriterRegistry;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;
import org.openrdf.rio.RDFWriterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import ch.isb.sib.sparql.tutorial.domain.SparqlQueryType;
import ch.isb.sib.sparql.tutorial.exception.SparqlTutorialException;
import ch.isb.sib.sparql.tutorial.service.SparqlService;
import info.aduna.lang.FileFormat;
import info.aduna.lang.service.FileFormatServiceRegistry;

@Controller
public class SparqlQueryController extends RepositoryController {

	private static final Log logger = LogFactory.getLog(SparqlController.class);
	@Autowired private SparqlService sparqlService;
	

	/*
	 * public SparqlQueryController(){ super();
	 * //this.setRepositoryManager(repMan); }
	 */

	//Code taken from Sesame (before used to be in SparqlController)
	@RequestMapping(value = "/sparql")
	public ModelAndView sparqlEndpoint(@RequestParam(value = "query", required = true) String queryStr, @RequestParam(value = "output", required = false) String output, HttpServletRequest request,
			HttpServletResponse response) throws QueryEvaluationException, Exception {

		if (queryStr != null) {
			synchronized (this) {

				Query query = sparqlService.getQuery(queryStr);
				SparqlQueryType queryType = SparqlQueryType.getQueryType(query);
				Object queryResult = sparqlService.evaluateQuery(queryStr);

				View view = getView(queryType);
				FileFormatServiceRegistry<? extends FileFormat, ?> registry = getRegistryInstance(queryType);
				
				Object factory = ProtocolUtil.getAcceptableService(request, response, registry);

				Map<String, Object> model = new HashMap<String, Object>();
				model.put(QueryResultView.FILENAME_HINT_KEY, "query-result");
				model.put(QueryResultView.QUERY_RESULT_KEY, queryResult);
				model.put(QueryResultView.FACTORY_KEY, factory);
				model.put(QueryResultView.HEADERS_ONLY, false);

				return new ModelAndView(view, model);
			}
		} else {
			throw new SparqlTutorialException("Missing parameter: ");
		}
	}


	private FileFormatServiceRegistry<? extends FileFormat, ?> getRegistryInstance(SparqlQueryType queryType) {
		switch(queryType){
			case TUPLE_QUERY: return TupleQueryResultWriterRegistry.getInstance();
			case GRAPH_QUERY: return RDFWriterRegistry.getInstance();
			case BOOLEAN_QUERY: return BooleanQueryResultWriterRegistry.getInstance();
		}
		return null;
	}


	private View getView(SparqlQueryType queryType) {
		switch(queryType){
			case TUPLE_QUERY: return TupleQueryResultView.getInstance();
			case GRAPH_QUERY: return GraphQueryResultView.getInstance();
			case BOOLEAN_QUERY: return BooleanQueryResultView.getInstance();
		}
		return null;
	}

	
}

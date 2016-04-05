package ch.isb.sib.sparql.tutorial.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.http.server.HTTPException;
import org.openrdf.http.server.ProtocolUtil;
import org.openrdf.http.server.repository.BooleanQueryResultView;
import org.openrdf.http.server.repository.GraphQueryResultView;
import org.openrdf.http.server.repository.QueryResultView;
import org.openrdf.http.server.repository.RepositoryController;
import org.openrdf.http.server.repository.TupleQueryResultView;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.resultio.BooleanQueryResultWriterRegistry;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;
import org.openrdf.rio.RDFWriterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import ch.isb.sib.sparql.tutorial.exception.SparqlTutorialException;
import ch.isb.sib.sparql.tutorial.repository.SesameRepository;
import info.aduna.lang.FileFormat;
import info.aduna.lang.service.FileFormatServiceRegistry;

@Controller
public class SparqlQueryController extends RepositoryController {

	private static final Log logger = LogFactory.getLog(SparqlController.class);
	@Autowired private SesameRepository repository;
	

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
				Query query = repository.prepareQuery(queryStr);

				View view;
				Object queryResult;
				FileFormatServiceRegistry<? extends FileFormat, ?> registry;

				try {
					if (query instanceof TupleQuery) {
						TupleQuery tQuery = (TupleQuery) query;

						queryResult = tQuery.evaluate();
						registry = TupleQueryResultWriterRegistry.getInstance();
						view = TupleQueryResultView.getInstance();
					} else if (query instanceof GraphQuery) {
						GraphQuery gQuery = (GraphQuery) query;

						queryResult = gQuery.evaluate();
						registry = RDFWriterRegistry.getInstance();
						view = GraphQueryResultView.getInstance();
					} else if (query instanceof BooleanQuery) {
						BooleanQuery bQuery = (BooleanQuery) query;

						queryResult = bQuery.evaluate();
						registry = BooleanQueryResultWriterRegistry.getInstance();
						view = BooleanQueryResultView.getInstance();
					} else {
						throw new SparqlTutorialException("Unsupported query type: " + query.getClass().getName());
					}
				} catch (QueryInterruptedException e) {
					logger.info("Query interrupted", e);
					throw new SparqlTutorialException("Query evaluation took too long");
				} catch (QueryEvaluationException e) {
					logger.info("Query evaluation error", e);
					if (e.getCause() != null && e.getCause() instanceof HTTPException) {
						// custom signal from the backend, throw as
						// HTTPException
						// directly (see SES-1016).
						throw (HTTPException) e.getCause();
					} else {
						throw new SparqlTutorialException("Query evaluation error: " + e.getMessage());
					}
				}
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

	
}

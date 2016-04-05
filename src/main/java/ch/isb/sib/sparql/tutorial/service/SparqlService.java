package ch.isb.sib.sparql.tutorial.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.query.TupleQuery;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.isb.sib.sparql.tutorial.Application;
import ch.isb.sib.sparql.tutorial.controller.SparqlController;
import ch.isb.sib.sparql.tutorial.domain.SparqlQueryType;
import ch.isb.sib.sparql.tutorial.exception.SparqlTutorialException;
import ch.isb.sib.sparql.tutorial.repository.SesameRepository;
import ch.isb.sib.sparql.tutorial.utils.IOUtils;

/**
 * A SPARQL service that adds prefixes to repository
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@Service
public class SparqlService implements InitializingBean {

	private static final Log logger = LogFactory.getLog(SparqlController.class);

	@Autowired
	private SesameRepository repository;

	private Map<String, String> prefixes = null;
	private String prefixesString;
	

	public Query getQuery(String queryStr) throws SparqlTutorialException {
		return repository.prepareQuery(queryStr);
	}

	public Object evaluateQuery(String queryStr) {
		Query query = repository.prepareQuery(queryStr);
		return evaluateQuery(query, SparqlQueryType.getQueryType(query));
	}


	private Object evaluateQuery(Query query, SparqlQueryType queryType) throws SparqlTutorialException {
		try {

			switch(queryType){
			
				case TUPLE_QUERY: return ((TupleQuery)query).evaluate();
				case GRAPH_QUERY: return ((GraphQuery)query).evaluate();
				case BOOLEAN_QUERY: return ((BooleanQuery)query).evaluate();
				default: throw new SparqlTutorialException("Unsupported query type: " + query.getClass().getName());

			}
				//;
		} catch (QueryInterruptedException e) {
			logger.info("Query interrupted", e);
			throw new SparqlTutorialException("Query evaluation took too long");
		} catch (QueryEvaluationException e) {
			logger.info("Query evaluation error", e);
				throw new SparqlTutorialException("Query evaluation error: " + e.getMessage());
		}
	}

	public Map<String, String> getPrefixes() {
		return prefixes;
	}

	public String getPrefixesString() {
		return prefixesString;
	}

	public void setPrefixesString(String prefixesString) {
		this.prefixesString = prefixesString;
	}

	public void setPrefixes(Map<String, String> prefixes) {
		this.prefixes = prefixes;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.prefixesString = IOUtils.readFile(Application.FOLDER + "/prefixes.ttl", "");

		String prefixes[] = this.prefixesString.split("\n");
		Map<String, String> m = new TreeMap<String, String>();
		for(String p : prefixes){
			String[] ptks = p.split(" ");
			m.put(ptks[1].replaceAll(":", ""), ptks[2].replaceAll("<", "").replaceAll(">", ""));
		}
		
		this.setPrefixes(m);
	}

	public void writeData(OutputStream out) {
		if(isDataLoadAllowed()){
			repository.writeTripletsAsTTL(out, prefixes);
		}else {
			try {
				out.write("Loading data is not supported for native store (only available for memory store)".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
				throw new SparqlTutorialException(e);
			}
		}
	}

	public long loadData(String data) {
		if(isDataLoadAllowed()){
			repository.testLoadData(data); //check if data is ok first (returns exception if not)
			repository.clearData();
			repository.loadData(data);
			return repository.countTriplets();
		}else {
			throw new SparqlTutorialException("Loading data is not supported for native store");
		}

	}
	
	public boolean isDataLoadAllowed() {
		return repository.isDataLoadAllowed();
	}

	public long getNumberOfTriplets() {
		return repository.countTriplets();
	}

}

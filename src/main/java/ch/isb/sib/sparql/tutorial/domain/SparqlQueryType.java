package ch.isb.sib.sparql.tutorial.domain;

import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.Query;
import org.openrdf.query.TupleQuery;

import ch.isb.sib.sparql.tutorial.exception.SparqlTutorialException;

/**
 * A sparql query used for the examples 
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public enum SparqlQueryType {
	
	TUPLE_QUERY,
	BOOLEAN_QUERY,
	GRAPH_QUERY;
	
	public static SparqlQueryType getQueryType(Query query){
		
		if (query instanceof TupleQuery) {
			return SparqlQueryType.TUPLE_QUERY;
		}else if (query instanceof GraphQuery) {
			return SparqlQueryType.GRAPH_QUERY;
		} else if (query instanceof BooleanQuery) {
			return SparqlQueryType.BOOLEAN_QUERY;
		} else  throw new SparqlTutorialException("Unsupported query type: " + query.getClass().getName());
		
	}

}

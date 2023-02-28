package swiss.sib.sparql.playground.domain;

import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.TupleQuery;

import swiss.sib.sparql.playground.exception.SparqlTutorialException;

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

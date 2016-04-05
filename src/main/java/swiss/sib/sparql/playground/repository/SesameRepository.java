package swiss.sib.sparql.playground.repository;

import java.io.OutputStream;
import java.util.Map;

import org.openrdf.query.Query;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriter;

/**
 * Interface fos Sesame Repository
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public interface SesameRepository{

	void testLoadTurtleData(String data);
	void loadTurtleData(String data);
	boolean isDataLoadAllowed();
	void clearData();

	void writeTriplesAsTurtle(OutputStream out, Map<String, String> prefixes);

	Query prepareQuery(String sparqlQuery);

	long countTriplets();

	
}

package ch.isb.sib.sparql.tutorial.repository;

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

	TupleQueryResult selectQuery(String queryString);
	boolean askQuery(String queryString);
	
	void selectQuery(String queryString, TupleQueryResultWriter queryResultWriter);
	void askQuery(String queryString, BooleanQueryResultWriter queryResultWriter);

	void testLoadData(String data);

	void clearData();

	long countTriplets();

	void loadData(String data);

	boolean isDataLoadAllowed();

	void writeTripletsAsTTL(OutputStream out, Map<String, String> prefixes);
	Query prepareQuery(String sparqlQuery);

	
}

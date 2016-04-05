package ch.isb.sib.sparql.tutorial;

import ch.isb.sib.sparql.tutorial.service.SparqlService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
public class SparqlServiceIntegrationTest {

	@Autowired
	private SparqlService sparqlService;



	@Test
	public void testAskQuery() throws Exception {
		String query = "ASK {FILTER(2>1)} ";
		Boolean result =  (Boolean) sparqlService.evaluateQuery(query);
		Assert.assertTrue(result);
	}

	@Test
	public void testQueryWithURI() throws Exception {
		String query = "select ?x where { ?x rdf:type <http://example.org/tuto/ontology#Cat> . }";
		TupleQueryResult result = (TupleQueryResult) sparqlService.evaluateQuery(query);
		Assert.assertEquals(countResults(result), 2);

	}

	@Test
	public void testQueryWithNamespaces() throws Exception {
		String query = sparqlService.getPrefixesString();
		query += " select ?x where { ?x rdf:type tto:Cat . }";
		TupleQueryResult result = (TupleQueryResult) sparqlService.evaluateQuery(query);
		Assert.assertEquals(countResults(result), 2);
	}

	//This query stopped working from 2.8.7 upgrade
	//See related issue: https://groups.google.com/forum/#!topic/sesame-users/NpidJt61cCQ

	@Test
	public void testFederatedQueryWithEBI() throws Exception {

		String federatedQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX msi: <http://rdf.ebi.ac.uk/resource/biosamples/msi/>" +
				"SELECT * where { " +
				"	SERVICE <http://www.ebi.ac.uk/rdf/services/biosamples/servlet/query> { " +
				"	msi:GAE-GEOD-25609 rdf:type ?obj " +
				"} }";

		TupleQueryResult result =  sparqlService.executeSelectQuery(federatedQuery);
		Assert.assertEquals(countResults(result), 2);
	}



	@Test
	public void testFederatedQueryWithDBPedia() throws Exception {

		String federatedQuery = "PREFIX dbp:<http://dbpedia.org/property/>\n" +
				"PREFIX tto:<http://example.org/tuto/ontology#>\n" +
				"\n" +
				"select *  where {\n" +
				"    SERVICE <http://dbpedia.org/sparql> {\n" +
				"      select ?person ?birthDate ?occupation ?pet where {\n" +
				"        VALUES ?birthDate { \"1942-07-13\"^^xsd:date }\n" +
				"        ?person dbp:birthDate ?birthDate .\n" +
				"        ?person dbp:occupation ?occupation .\n" +
				"      } \n" +
				"    }\n" +
				"    OPTIONAL { ?person tto:pet ?pet } .\n" +
				"}";

		TupleQueryResult result =  sparqlService.executeSelectQuery(federatedQuery);
		Assert.assertEquals(countResults(result), 6);
	}

	@Test
	public void testFederatedQuery() throws Exception {
		String query = sparqlService.getPrefixesString();
		query += " select ?subj ?pred ?obj where { values (?subj ?pred) { (dbpedia:Harrison_Ford dbo:birthDate) (dbpedia:Harrison_Ford dbp:name) (dbpedia:Harrison_Ford dbp:occupation) } { ?subj ?pred ?obj.  }UNION{ service <http://dbpedia.org/sparql> { ?subj ?pred ?obj.}}}";
		TupleQueryResult result = (TupleQueryResult) sparqlService.evaluateQuery(query);
		Assert.assertEquals(countResults(result), 2);
	}
	
	
	@Test
	public void testCountNumberOfTriples() throws Exception {
		Long n = sparqlService.countNumberOfTriples();
		System.out.println(n + " triples");
		Assert.assertTrue(n > 50);
		Assert.assertTrue(n < 100);

	}

	private long countResults(TupleQueryResult results) {
		try {
			long counter = 0;
			while (results.hasNext()) {
				results.next();
				counter++;
			}
			return counter;
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
			return 0;
		}
	}

}
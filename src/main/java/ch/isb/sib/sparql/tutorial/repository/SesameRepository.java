package ch.isb.sib.sparql.tutorial.repository;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import org.springframework.beans.factory.InitializingBean;

import ch.isb.sib.sparql.tutorial.Application;
import ch.isb.sib.sparql.tutorial.exception.SparqlTutorialException;
import info.aduna.iteration.Iterations;

/**
 * RDF data store for sesame
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@org.springframework.stereotype.Repository
public class SesameRepository implements InitializingBean{
	
	private static final Log logger = LogFactory.getLog(SesameRepository.class);


	// Read documentation: http://rdf4j.org/sesame/2.7/docs/users.docbook?view
	// http://www.cambridgesemantics.com/semantic-university/sparql-by-example

	private Repository rep = null;
	private SailRepository testRepo;
	private RepositoryConnection conn = null;

	public TupleQueryResult query(String sparqlQuery) {

		try {

			TupleQuery q = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
			return q.evaluate();

		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} 

	}
	
	public boolean ask(String sparqlAskQuery) {

		try {

			BooleanQuery bq = conn.prepareBooleanQuery(QueryLanguage.SPARQL, sparqlAskQuery);
			return bq.evaluate();

		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} 

	}

	public void query(String queryString, TupleQueryResultHandler handler) {
		try {
			TupleQuery q = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			q.evaluate(handler);

		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (TupleQueryResultHandlerException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} 
	}

	@PostConstruct
	public void init() throws Exception {

		boolean nativeConfig = (System.getProperty("repository.type") != null) && (System.getProperty("repository.type").equals("native"));
		if(nativeConfig){
			logger.info("Found repository type native property!");
		}

		//If native configuration is set
		File sesameDataFolder = null;
		File sesameDataValueFile = null;
		if (nativeConfig) {
			sesameDataFolder = new File(Application.FOLDER + "/sesame-db");
			sesameDataValueFile = new File(sesameDataFolder.getPath() + "/values.dat");
			logger.info("Initializing native repository in " + sesameDataFolder);
			rep = new SailRepository(new NativeStore(sesameDataFolder));
		} else { //otherwise take memory
			logger.info("Initializing in memory repository");
			rep = new SailRepository(new MemoryStore());
		}

		File ttlFile = new File(Application.FOLDER + "/ttl-data");
		// if if it not native (memory) just load it.
		// if it is native and the data is still not there....
		if (!nativeConfig) {
			rep.initialize();
			logger.info("Loading turtle files from " + ttlFile);
			addTTLFiles(ttlFile, rep.getConnection());
		}else if(nativeConfig && !sesameDataValueFile.exists()){
			rep.initialize();
			logger.info("No previous sesame repository found in " + sesameDataValueFile);
			logger.info("Loading turtle files from " + ttlFile);
			logger.info("Depending on the number of triplets, this may take some time to load the first time, please be patient ....");
			addTTLFiles(ttlFile, rep.getConnection());
		}else{
			rep.initialize();
			logger.info("Sesame repository already found in " + sesameDataValueFile);
			logger.info("Skipping to load turtle files. Remove " + sesameDataFolder + " folder if you want to reload turtle files");
		}
		
		
		//logger.info("Counting number of triplets...");
		//logger.info("Repository contains " + countTriplets() + " triplets");

		testRepo = new SailRepository(new MemoryStore());
		testRepo.initialize();

	}

	private static void addTTLFiles(final File folder, RepositoryConnection conn) throws RDFParseException, RepositoryException, IOException {
		long start = System.currentTimeMillis();
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				logger.debug("Loading " + fileEntry);
				conn.add(fileEntry, "", RDFFormat.TURTLE, new Resource[] {});
			}
		}
		logger.info("Loading turtle files finished in " + (System.currentTimeMillis() - start) + " ms");


	}

	public void writeTripletsAsTTL(OutputStream output, Map<String, String> prefixes) {
		try {

			RepositoryResult<Statement> statements = rep.getConnection().getStatements(null, null, null, true);
			Model model = Iterations.addAll(statements, new LinkedHashModel());
			for (String key : prefixes.keySet()) {
				model.setNamespace(key, prefixes.get(key));
			}
			Rio.write(model, output, RDFFormat.TURTLE);

		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (RDFHandlerException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		}
	}

	public void clearData() {
		try {
			RepositoryResult<Statement> statements = rep.getConnection().getStatements(null, null, null, true);
			rep.getConnection().remove(statements);
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		}
	}

	public void testLoadData(String data) {
		try {

			InputStream stream = new ByteArrayInputStream(data.getBytes());
			RepositoryResult<Statement> statements = testRepo.getConnection().getStatements(null, null, null, true);
			testRepo.getConnection().remove(statements);
			testRepo.getConnection().add(stream, "", RDFFormat.TURTLE, new Resource[] {});

		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (RDFParseException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		}
	}

	public void loadData(String data) {
		try {
			InputStream stream = new ByteArrayInputStream(data.getBytes());
			rep.getConnection().add(stream, "", RDFFormat.TURTLE, new Resource[] {});

		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (RDFParseException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		}
	}

	public long countTriplets() {
		try {
			TupleQueryResult result = this.query("SELECT (COUNT(*) AS ?no) { ?s ?p ?o  }");
			long n = Long.valueOf(result.next().getBinding("no").getValue().stringValue());
			result.close();
			return n;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		}
	}

	public boolean isDataLoadAllowed() {
		return rep.getDataDir() == null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		conn = rep.getConnection();
	}

}

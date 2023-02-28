package swiss.sib.sparql.playground.repository.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.springframework.beans.factory.InitializingBean;

import swiss.sib.sparql.playground.Application;
import swiss.sib.sparql.playground.exception.SparqlTutorialException;
import swiss.sib.sparql.playground.repository.SesameRepository;

/**
 * RDF data store for sesame
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@org.springframework.stereotype.Repository
public class SesameRepositoryImpl implements SesameRepository, InitializingBean {

	private static final Log logger = LogFactory.getLog(SesameRepositoryImpl.class);

	// Read documentation: http://rdf4j.org/sesame/2.7/docs/users.docbook?view
	// http://www.cambridgesemantics.com/semantic-university/sparql-by-example

	private Repository rep = null;
	private SailRepository testRepo;
	private RepositoryConnection conn = null;

	@Override
	public Query prepareQuery(String sparqlQuery) {
		try {
			return conn.prepareQuery(QueryLanguage.SPARQL, sparqlQuery);
		} catch (RepositoryException | MalformedQueryException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		}
	}
	
	@PostConstruct
	public void init() throws Exception {

		boolean nativeConfig = (System.getProperty("repository.type") != null) && (System.getProperty("repository.type").equals("native"));
		if (nativeConfig) {
			logger.info("Found repository type native property!");
		}

		// If native configuration is set
		File sesameDataFolder = null;
		File sesameDataValueFile = null;
		if (nativeConfig) {
			sesameDataFolder = new File(Application.FOLDER + "/sesame-db");
			sesameDataValueFile = new File(sesameDataFolder.getPath() + "/values.dat");
			logger.info("Initializing native repository in " + sesameDataFolder);
			rep = new SailRepository(new NativeStore(sesameDataFolder));
		} else { // otherwise take memory
			logger.info("Initializing in memory repository");
			rep = new SailRepository(new MemoryStore());
		}

		File ttlFile = new File(Application.FOLDER + "/ttl-data");
		// if if it not native (memory) just load it.
		// if it is native and the data is still not there....
		if (!nativeConfig) {
			rep.init();
			logger.info("Loading turtle files from " + ttlFile);
			addTTLFiles(ttlFile, rep.getConnection());
		} else if (nativeConfig && !sesameDataValueFile.exists()) {
			rep.init();
			logger.info("No previous sesame repository found in " + sesameDataValueFile);
			logger.info("Loading turtle files from " + ttlFile);
			logger.info("Depending on the number of triplets, this may take some time to load the first time, please be patient ....");
			addTTLFiles(ttlFile, rep.getConnection());
		} else {
			rep.init();
			logger.info("Sesame repository already found in " + sesameDataValueFile);
			logger.info("Skipping to load turtle files. Remove " + sesameDataFolder + " folder if you want to reload turtle files");
		}

		// logger.info("Counting number of triplets...");
		// logger.info("Repository contains " + countTriplets() + " triplets");

		testRepo = new SailRepository(new MemoryStore());
		testRepo.init();

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

	public void writeTriplesAsTurtle(OutputStream output, Map<String, String> prefixes) {
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

	public void testLoadTurtleData(String data) {
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

	public void loadTurtleData(String data) {
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

			Query query = prepareQuery("SELECT (COUNT(*) AS ?no) { ?s ?p ?o  }");
			TupleQueryResult result = ((TupleQuery) query).evaluate();

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

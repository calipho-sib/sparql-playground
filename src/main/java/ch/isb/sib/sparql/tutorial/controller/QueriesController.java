package ch.isb.sib.sparql.tutorial.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ch.isb.sib.sparql.tutorial.Application;
import ch.isb.sib.sparql.tutorial.domain.SparqlQuery;
import ch.isb.sib.sparql.tutorial.repository.QueryDictionary;
import ch.isb.sib.sparql.tutorial.utils.IOUtils;

/**
 * Controller used to server the SPARQL queries 
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@RestController
public class QueriesController {

	@Autowired
	QueryDictionary queryDictionary;

	@RequestMapping(value = "/queries", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SparqlQuery> queries() throws IOException {
		return queryDictionary.getQueries(Application.FOLDER + "/queries");
	}
	
	@RequestMapping(value = "/queries/{queryId}")
	public @ResponseBody byte[] queryImage(@PathVariable("queryId") String queryId) throws IOException {
		return IOUtils.readImage(Application.FOLDER + "/queries/" + queryId + ".png");
	}

	@RequestMapping(value = "/rdfhelp", produces = MediaType.APPLICATION_JSON_VALUE)
	public String rdfhelp() throws IOException {
		return IOUtils.readFile(Application.FOLDER + "/rdfhelp.json", Charset.defaultCharset());
	}

}
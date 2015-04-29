package ch.isb.sib.sparql.tutorial.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.QueryEvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.isb.sib.sparql.tutorial.service.SparqlService;

/**
 * Sparql controller that provied SPARQL endpoint in (json, xml, csv and tsv)
 * Utilities serviecs to get and load turtle data and retrieve common prefixes 
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@RestController
public class SparqlController {

	@Autowired
	private SparqlService sparqlService;

	@RequestMapping(value = "/sparql")
	public void sparqlEndpoint(@RequestParam(value = "query", required = true) String query, @RequestParam(value = "output", required = false) String output, HttpServletResponse response)
			throws QueryEvaluationException, Exception {
		sparqlService.executeSparql(query, response.getOutputStream(), output);
	}

	@RequestMapping(value = "/prefixes", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> sparqlPrefixes() throws IOException {
		return sparqlService.getPrefixes();
	}

	@RequestMapping(value = "/ttl-data", method = RequestMethod.GET)
	public void sparqlData(HttpServletResponse response) throws QueryEvaluationException, Exception {
		sparqlService.writeData(response.getOutputStream());
	}

	@RequestMapping(value = "/ttl-data", method = RequestMethod.PUT)
	public long sparqlData(@RequestParam(value = "data", required = true) String data) throws QueryEvaluationException, Exception {
		return sparqlService.loadData(data);
	}


}
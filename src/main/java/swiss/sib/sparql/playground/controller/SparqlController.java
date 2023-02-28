package swiss.sib.sparql.playground.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import swiss.sib.sparql.playground.exception.SparqlTutorialException;
import swiss.sib.sparql.playground.service.SparqlService;

/**
 * Sparql controller that provied SPARQL endpoint in (json, xml, csv and tsv)
 * Utilities serviecs to get and load turtle data and retrieve common prefixes 
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@RestController
public class SparqlController {

	//private static final Log logger = LogFactory.getLog(SparqlController.class);

	@Autowired
	private SparqlService sparqlService;


	/* delegated not to SparqlQueryController	
	@RequestMapping(value = "/sparql")
	public void sparqlEndpoint(@RequestParam(value = "query", required = true) String query, @RequestParam(value = "output", required = false) String output, HttpServletRequest request, HttpServletResponse response)
			throws QueryEvaluationException, Exception {
		logger.info("query=" + query + ";format=" + output + ";remote-host=" + request.getRemoteHost());
		sparqlService.executeSparql(query, response.getOutputStream(), output);
	}*/

	@RequestMapping(value = "/prefixes", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> sparqlPrefixes() throws IOException {
		return sparqlService.getPrefixes();
	}

	@RequestMapping(value = "/ttl-data", method = RequestMethod.GET)
	public void sparqlData(HttpServletResponse response) throws QueryEvaluationException, Exception {
		sparqlService.writeData(response.getOutputStream());
	}

	@RequestMapping(value = "/ttl-data", method = RequestMethod.PUT)
	public long sparqlData(@RequestParam(value = "data", required = true) String data, HttpServletRequest request) throws QueryEvaluationException, Exception {

		if((System.getProperty("reload") != null) && (System.getProperty("reload").equalsIgnoreCase("false"))){ //check if loading data is not allowed
			throw new SparqlTutorialException("You must run the application in localhost in order to load data. Download it by clicking on the link below the page");
		}else {
			return sparqlService.loadData(data);
		}
	}


}
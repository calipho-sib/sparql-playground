package swiss.sib.sparql.playground.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import swiss.sib.sparql.playground.domain.SparqlQuery;
import swiss.sib.sparql.playground.repository.QueryDictionary;

/**
 * Controller used to server the FAQ queries 
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@RestController
public class FaqsController {

	@Autowired
	QueryDictionary queryDictionary;

	@RequestMapping(value = "/faqs", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SparqlQuery> queries() throws IOException {
		return queryDictionary.getFaqs();
	}
	

}
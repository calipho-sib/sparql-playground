package ch.isb.sib.sparql.tutorial.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.isb.sib.sparql.tutorial.domain.SparqlQuery;
import ch.isb.sib.sparql.tutorial.repository.QueryDictionary;

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
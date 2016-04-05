package ch.isb.sib.sparql.tutorial;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ch.isb.sib.sparql.tutorial.repository.SesameRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
public class SparqlRepositoryIntegrationTest {

	@Autowired
	private SesameRepository repository;
	
	@Test
	public void testAskQuery() throws Exception {
		String query = "ASK {FILTER(2>1)} ";
		Boolean result = repository.askQuery(query);
		Assert.assertTrue(result);
	}

}
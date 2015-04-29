package ch.isb.sib.sparql.tutorial.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ch.isb.sib.sparql.tutorial.Application;
import ch.isb.sib.sparql.tutorial.utils.IOUtils;

/**
 * Controller used to serve markdown pages (and images) located under pages folder
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@RestController
public class PagesController {

	@RequestMapping(value = "/git/trees/master", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> tree() throws IOException {
		Map<String, Object> tree = new HashMap<String, Object>();
		List<Object> pages = new ArrayList<Object>();
		tree.put("tree", pages);
		for (final File fileEntry : new File(Application.FOLDER + "/pages/").listFiles()) {
			if(fileEntry.isFile()){
				Map<String, String> m = new HashMap<String, String>();
				m.put("path", "generalities/" + fileEntry.getName());
				m.put("type", "blob");
				m.put("url", "");
				pages.add(m);
			}
		}
		return tree;
	}

	@RequestMapping(value = "/contents/{folder}/{page}")
	public String page(@PathVariable("folder") String folder, @PathVariable("page") String page) throws IOException {
		return IOUtils.readFile(Application.FOLDER + "/pages/" + page + ".md", Charset.defaultCharset());
	}
	
	@RequestMapping(value = "/assets/{asset}")
	public @ResponseBody byte[] asset(@PathVariable("asset") String asset) throws IOException {
		return IOUtils.readImage(Application.FOLDER + "/pages/assets/" + asset + ".png");
	}
	

}
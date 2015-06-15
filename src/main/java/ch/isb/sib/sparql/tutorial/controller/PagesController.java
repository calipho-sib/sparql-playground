package ch.isb.sib.sparql.tutorial.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ch.isb.sib.sparql.tutorial.Application;
import ch.isb.sib.sparql.tutorial.service.PageService;

/**
 * Controller used to serve markdown pages (and images) located under pages folder
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@RestController
public class PagesController {
	
	@Autowired PageService pageService;

	@RequestMapping(value = "/git/trees/master", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> tree() throws IOException {
		return pageService.getPagesTree();
	}

	@RequestMapping(value = "/contents/{folder}/{page}")
	public String page(@PathVariable("folder") String folder, @PathVariable("page") String page) throws IOException {
		return pageService.getPage(page);
	}
	
	@RequestMapping(value = "/assets/{asset}.{extension}")
	public @ResponseBody byte[] asset(@PathVariable("asset") String asset, @PathVariable("extension") String extension) throws IOException {
		return pageService.getFileOrTry(Application.FOLDER + "/pages/assets/" + asset + "." + extension, "assets/" + asset + "." + extension, extension);
	}
	

}
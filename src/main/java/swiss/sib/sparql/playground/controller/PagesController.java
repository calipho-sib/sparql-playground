package swiss.sib.sparql.playground.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import swiss.sib.sparql.playground.Application;
import swiss.sib.sparql.playground.service.PageService;
import swiss.sib.sparql.playground.utils.IOUtils;

/**
 * Controller used to serve markdown pages (and images) located under pages folder
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@RestController
public class PagesController {
	
	@Autowired
	PageService pageService;

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
	
	

	@RequestMapping(value = "/assets/{asset}.pdf")
	public void pdfDownload(@PathVariable("asset") String asset, HttpServletResponse response) throws IOException {
        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", asset);
        response.setHeader(headerKey, headerValue);

        IOUtils.streamFile(new File("assets/" + asset + ".pdf"), response.getOutputStream());
		 
	}
	

}
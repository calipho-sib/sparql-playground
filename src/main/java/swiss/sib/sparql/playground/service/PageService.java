package swiss.sib.sparql.playground.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import swiss.sib.sparql.playground.Application;
import swiss.sib.sparql.playground.utils.IOUtils;

/**
 * The page service
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
@Service
public class PageService {
	
	private static final String ABOUT_PAGE = "99_About";

    @Cacheable("page-tree")
	public Map<String, Object> getPagesTree() throws IOException {

		Map<String, Object> tree = new HashMap<String, Object>();
		List<Object> pages = new ArrayList<Object>();
		tree.put("tree", pages);
		for (final File fileEntry : new File(Application.FOLDER + "/pages/").listFiles()) {
			if (fileEntry.isFile()) {
				pages.add(buildPage(fileEntry, fileEntry.getName()));
			}
		}
		
		pages.add(buildPage(new File("README.md"), ABOUT_PAGE));
		return tree;
	}

	private Object buildPage(File f, String name) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("path", "generalities/" + name);
		m.put("type", "blob");
		m.put("url", "");
		return m;
	}

    @Cacheable("page")
	public String getPage(String page) {
    	if(page.equals(ABOUT_PAGE)){
			return IOUtils.readFile("README", null);
		}else {
			return IOUtils.readFile(Application.FOLDER + "/pages/" + page, null);
		}
	}

    @Cacheable("asset")
	public byte[] getFileOrTry(String assetOk, String assetIfFailure, String extension) {

		File f = null;
		f = new File(assetOk);
		if(!f.exists()){
			if(assetIfFailure != null){
				f = new File(assetIfFailure);
			}
		}
		return IOUtils.readImage(extension, f);
	}

}

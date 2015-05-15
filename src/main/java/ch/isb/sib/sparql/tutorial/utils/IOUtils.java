package ch.isb.sib.sparql.tutorial.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import ch.isb.sib.sparql.tutorial.exception.SparqlTutorialException;

/**
 * IO utitly class
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public class IOUtils {

	public static String readFile(String path, String orValue) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded, Charset.forName("UTF-8"));
		} catch (IOException e) {
			if(orValue != null){
				return orValue;
			}else {
				e.printStackTrace();
				throw new SparqlTutorialException(e);
			}
		}
	}

	public static byte[] readImage(File f) {

		try {
			// Retrieve image from the classpath.
			InputStream is = new FileInputStream(f);

			// Prepare buffered image.
			BufferedImage img = ImageIO.read(is);

			// Create a byte array output stream.
			ByteArrayOutputStream bao = new ByteArrayOutputStream();

			// Write to output stream
			ImageIO.write(img, "png", bao);

			return bao.toByteArray();
		} catch (IOException e) {
			throw new SparqlTutorialException(e);
		}
	}

}
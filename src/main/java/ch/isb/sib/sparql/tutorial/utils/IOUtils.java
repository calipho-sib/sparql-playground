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

	public static String readFile(String path, Charset encoding) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded, encoding);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SparqlTutorialException(e);
		}
	}

	public static byte[] readImage(String path) {

		try {
			// Retrieve image from the classpath.
			InputStream is = new FileInputStream(new File(path));

			// Prepare buffered image.
			BufferedImage img = ImageIO.read(is);

			// Create a byte array output stream.
			ByteArrayOutputStream bao = new ByteArrayOutputStream();

			// Write to output stream
			ImageIO.write(img, "png", bao);

			return bao.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
package org.eclipse.scout.boot.tabularius.anagnostes.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.eclipse.scout.boot.tabularius.anagnostes.model.Eval;
import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.springframework.core.io.ClassPathResource;

import com.bsiag.anagnostes.NeuralNetwork;
import com.bsiag.anagnostes.Output;

@ApplicationScoped
public class LeNetMnistAccessor {
	protected final String MODEL_FILE_NAME = "model_numbers.zip";

	protected NeuralNetwork m_net;

	public LeNetMnistAccessor() {
		m_net = new NeuralNetwork();
		try {
			final File tempFile = File.createTempFile(MODEL_FILE_NAME, ".tmp");
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(new ClassPathResource(MODEL_FILE_NAME).getInputStream(), out);
			m_net = new NeuralNetwork.Builder().fromFile(tempFile);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public Eval eval(String imagePath) {
		Output result = m_net.output(getImageFromFileSystem(imagePath));
		Eval eval = new Eval();
		eval.character = result.getCharacter();
		eval.confidence = result.getConfidence();
		return eval;
	}

	protected BufferedImage getImageFromFileSystem(String imagePath) {
		BufferedImage img = null;
		try {
			final URL imageFileUrl = getClass().getClassLoader().getResource(imagePath);
			img = ImageIO.read(new File(imageFileUrl.toURI()));
		} catch (IOException | URISyntaxException e) {
			throw new IllegalStateException(e);
		}
		return img;
	}
}

package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class TemplatePreprocesor {

	public static byte[] applyPreprocessing(TemplatePreprocessor templatePreprocessor, Path templatePath)
			throws IOException {
		byte[] templateByteArray;
		if (templatePreprocessor != null) {
			templateByteArray = templatePreprocessor.process(templatePath);
		} else {
			InputStream inputStream = new FileInputStream(templatePath.toFile());
			templateByteArray = inputStream.readAllBytes();
			inputStream.close();
		}

		return templateByteArray;
	}

}

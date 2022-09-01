package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import io.github.guentherjulian.masterthesis.patterndetection.exception.PreprocessingException;

public class TemplatePreprocessorUtil {

	public static byte[] applyPreprocessing(TemplatePreprocessor templatePreprocessor, Path templatePath)
			throws IOException, PreprocessingException {
		byte[] templateByteArray;
		if (templatePreprocessor != null) {
			templateByteArray = templatePreprocessor.processTemplate(templatePath);
		} else {
			InputStream inputStream = new FileInputStream(templatePath.toFile());
			templateByteArray = inputStream.readAllBytes();
			inputStream.close();
		}

		return templateByteArray;
	}

}

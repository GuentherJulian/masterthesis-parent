package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

abstract class AbstractTemplatePreprocessor implements TemplatePreprocessor {

	abstract String process(String lineToProcess);

	public byte[] process(Path templatePath) throws IOException {
		List<String> lines = Files.readAllLines(templatePath);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		for (String line : lines) {
			String processedLine = line;

			processedLine = this.process(processedLine);
			byteArrayOutputStream.write(processedLine.getBytes());
			byteArrayOutputStream.write(System.lineSeparator().getBytes());
		}

		return byteArrayOutputStream.toByteArray();
	}
}

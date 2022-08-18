package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

abstract class AbstractPreprocessingStep implements PreprocessingStep {

	protected Path inputPath;

	public AbstractPreprocessingStep(Path input) throws Exception {
		if (!Files.exists(input)) {
			throw new Exception("Path could no be found: " + input);
		}

		this.inputPath = input;
	}

	abstract String process(String lineToProcess);

	public byte[] process() throws IOException {
		List<String> lines = Files.readAllLines(inputPath);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		for (String line : lines) {
			String processedLine = line;

			processedLine = this.process(processedLine);
			byteArrayOutputStream.write(processedLine.getBytes());
		}

		return byteArrayOutputStream.toByteArray();
	}
}

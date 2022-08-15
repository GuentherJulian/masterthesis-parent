package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

abstract class AbstractPreprocessingStep implements PreprocessingStep {

	protected Path inputPath;

	public AbstractPreprocessingStep(Path input) throws Exception {
		if (!Files.exists(input)) {
			throw new Exception("Path could no be found: " + input);
		}

		this.inputPath = input;
	}

	public Path process() throws IOException {
		List<String> lines = Files.readAllLines(inputPath);

		Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
		String fileName = this.inputPath.getFileName().toString();
		String extension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		Path tempFile = Files.createTempFile(tempDir, fileName, extension);

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(tempFile)) {
			for (String line : lines) {
				String processedLine = line;

				processedLine = this.process(processedLine);
				bufferedWriter.append(processedLine);
				bufferedWriter.append(System.lineSeparator());
			}

			bufferedWriter.close();
		}

		return tempFile;
	}
}

package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.guentherjulian.masterthesis.patterndetection.exception.PreprocessingException;

abstract class AbstractTemplatePreprocessor implements TemplatePreprocessor {

	private Path templatesRootPath;

	protected abstract String processTemplateLine(String lineToProcess) throws PreprocessingException;

	public byte[] processTemplate(Path templatePath) throws IOException, PreprocessingException {
		if (!Files.exists(templatePath)) {
			throw new PreprocessingException(String.format("The file %s could not be found!", templatePath.toString()));
		}

		byte[] templateByteArray = getFileBytes(templatePath);
		templateByteArray = preprocess(templatePath, templateByteArray);

		// the actual preprocessing
		String[] lines = new String(templateByteArray).split(System.lineSeparator());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		for (String line : lines) {
			String processedLine = line;

			processedLine = this.processTemplateLine(processedLine);
			byteArrayOutputStream.write(processedLine.getBytes());
			byteArrayOutputStream.write(System.lineSeparator().getBytes());
		}

		return byteArrayOutputStream.toByteArray();
	}

	protected byte[] getFileBytes(Path filepath) throws IOException {
		InputStream inputStream = new FileInputStream(filepath.toFile());
		return inputStream.readAllBytes();
	}

	protected byte[] preprocess(Path templatePath, byte[] templateByteArray)
			throws PreprocessingException, IOException {
		return templateByteArray;
	}

	protected Path getReferencedFile(Path templatePath, String includeFile) {
		if ((includeFile.startsWith("\"") && includeFile.endsWith("\""))
				|| (includeFile.startsWith("'") && includeFile.endsWith("'"))) {
			includeFile = includeFile.substring(1, includeFile.length() - 1);
		}
		boolean isAbsolutePath = includeFile.startsWith("/");
		if (isAbsolutePath) {
			return Paths.get(this.templatesRootPath.resolve(includeFile.substring(1)).toUri());
		}
		return Paths.get(templatePath.getParent().resolve(Paths.get(includeFile)).toUri());
	}

	public Path getTemplatesRootPath() {
		return templatesRootPath;
	}

	public void setTemplatesRootPath(Path templatesRootPath) {
		this.templatesRootPath = templatesRootPath;
	}
}

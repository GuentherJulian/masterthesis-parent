package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import io.github.guentherjulian.masterthesis.patterndetection.exception.PreprocessingException;

public interface TemplatePreprocessor {

	public byte[] processTemplate(Path templatePath) throws IOException, PreprocessingException;

	public void setTemplatesRootPath(Path templatesRootPath);

	public Path getTemplatesRootPath();

	public Map<String, Set<String>> getVariables();
}

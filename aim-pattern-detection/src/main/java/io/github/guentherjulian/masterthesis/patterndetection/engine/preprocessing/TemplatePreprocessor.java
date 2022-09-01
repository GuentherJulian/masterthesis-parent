package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.IOException;
import java.nio.file.Path;

import io.github.guentherjulian.masterthesis.patterndetection.exception.PreprocessingException;

public interface TemplatePreprocessor {

	public byte[] processTemplate(Path templatePath) throws IOException, PreprocessingException;

	public void setTemplatesRootPath(Path templatesRootPath);
}

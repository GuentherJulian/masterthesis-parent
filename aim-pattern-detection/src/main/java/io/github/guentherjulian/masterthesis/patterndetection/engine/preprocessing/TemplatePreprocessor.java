package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.IOException;
import java.nio.file.Path;

public interface TemplatePreprocessor {

	public byte[] processTemplate(Path templatePath) throws IOException;
}

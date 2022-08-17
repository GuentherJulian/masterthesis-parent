package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.IOException;
import java.nio.file.Path;

public interface PreprocessingStep {

	public Path process() throws IOException;
}

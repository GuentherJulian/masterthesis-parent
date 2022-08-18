package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.IOException;

public interface PreprocessingStep {

	public byte[] process() throws IOException;
}

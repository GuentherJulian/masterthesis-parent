package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions;

import java.util.List;

public interface TransformationFunction {

	List<String> transform(String substitution);
}

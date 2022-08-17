package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions;

import java.util.List;

import io.github.guentherjulian.masterthesis.patterndetection.exception.PlaceholderTransformationFunctionCallException;

public interface ReverseTransformationFunction {

	List<String> transform(String substitution, String... args) throws PlaceholderTransformationFunctionCallException;
}

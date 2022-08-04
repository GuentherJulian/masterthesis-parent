package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.TransformationFunctionProcessor;

public interface PlaceholderResolver {

	PlaceholderResolutionResult resolvePlaceholder(String placeholder, String substitution) throws Exception;

	String[] getTransformationFunctions(String placeholder);

	String getPlaceholderName(String placeholder);

	TransformationFunctionProcessor getTransformationFunctionProcessor();
}

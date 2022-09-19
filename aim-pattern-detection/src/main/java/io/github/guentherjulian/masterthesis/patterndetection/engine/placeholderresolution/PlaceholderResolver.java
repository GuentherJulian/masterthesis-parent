package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import io.github.guentherjulian.masterthesis.patterndetection.exception.PlaceholderResolutionException;

public interface PlaceholderResolver {

	PlaceholderResolutionResult resolvePlaceholder(String placeholder, String substitution)
			throws PlaceholderResolutionException;

	String getTransformationFunctionPackageName();

	String getTransformationFunctionJavaClassName(String transformationFunction);

	String transformPlaceholderNotation(String orginialPlaceholder);
}

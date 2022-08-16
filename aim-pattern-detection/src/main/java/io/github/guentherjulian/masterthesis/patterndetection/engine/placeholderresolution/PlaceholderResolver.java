package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.PlaceholderResolutionException;

public interface PlaceholderResolver {

	PlaceholderResolutionResult resolvePlaceholder(String placeholder, String substitution)
			throws PlaceholderResolutionException;
}

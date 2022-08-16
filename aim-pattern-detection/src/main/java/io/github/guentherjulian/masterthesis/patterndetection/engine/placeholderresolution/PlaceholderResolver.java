package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

public interface PlaceholderResolver {

	PlaceholderResolutionResult resolvePlaceholder(String placeholder, String substitution) throws Exception;
}

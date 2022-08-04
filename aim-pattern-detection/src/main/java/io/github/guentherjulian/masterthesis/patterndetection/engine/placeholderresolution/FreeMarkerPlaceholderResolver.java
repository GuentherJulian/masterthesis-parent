package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import java.util.Arrays;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.FreeMarkerTransformationFunctionProcessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.TransformationFunctionProcessor;

public class FreeMarkerPlaceholderResolver extends AbstractPlaceholderResolver {

	public String[] getTransformationFunctions(String placeholder) {
		if (this.isPlaceholderAtomic(placeholder)) {
			return null;
		}

		String[] transformationFunctions = placeholder.split("(\\?|#)");
		return Arrays.copyOfRange(transformationFunctions, 1, transformationFunctions.length);
	}

	private boolean isPlaceholderAtomic(String placeholder) {
		return !placeholder.contains("?") && !placeholder.contains("#");
	}

	@Override
	public TransformationFunctionProcessor getTransformationFunctionProcessor() {
		return new FreeMarkerTransformationFunctionProcessor();
	}

	public String getPlaceholderName(String placeholder) {
		if (!this.isPlaceholderAtomic(placeholder)) {
			return placeholder.split("(\\?|#)")[0];
		}
		return placeholder;
	}
}

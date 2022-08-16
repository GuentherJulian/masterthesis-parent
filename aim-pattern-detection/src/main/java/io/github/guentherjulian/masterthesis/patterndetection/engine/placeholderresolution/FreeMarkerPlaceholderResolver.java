package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.FreeMarkerTransformationFunctionProcessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.TransformationFunction;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.TransformationFunctionProcessor;

public class FreeMarkerPlaceholderResolver extends AbstractPlaceholderResolver {

	@Override
	public TransformationFunctionProcessor getTransformationFunctionProcessor() {
		return new FreeMarkerTransformationFunctionProcessor();
	}

	@Override
	public List<TransformationFunction> getTransformationFunctions(String placeholderExpression) {
		if (this.isPlaceholderAtomic(placeholderExpression)) {
			return null;
		}

		String[] transformationFunctions = placeholderExpression.split("(\\?|#)");
		List<TransformationFunction> functions = new ArrayList<>();
		for (int i = 1; i < transformationFunctions.length; i++) {
			if (transformationFunctions[i].matches("\\w+\\(.*\\)")) {
				String[] args = getArgs(transformationFunctions[i]);
				functions.add(new TransformationFunction(
						transformationFunctions[i].substring(0, transformationFunctions[i].indexOf("(")), args));
			} else {
				functions.add(new TransformationFunction(transformationFunctions[i]));
			}
		}
		return functions;
	}

	@Override
	public String getPlaceholderName(String placeholderExpression) {
		if (!this.isPlaceholderAtomic(placeholderExpression)) {
			return placeholderExpression.split("(\\?|#)")[0];
		}
		return placeholderExpression;
	}

	private boolean isPlaceholderAtomic(String placeholderExpression) {
		return !placeholderExpression.contains("?") && !placeholderExpression.contains("#");
	}

	private String[] getArgs(String transformationFunction) {
		Pattern pattern = Pattern.compile(".+(\\(('.+')+\\))");
		Matcher matcher = pattern.matcher(transformationFunction);
		if (!matcher.find()) {
			return null;
		}
		String params = matcher.group(2);

		String[] args = params.split(",");
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].trim();
		}
		return args;
	}
}

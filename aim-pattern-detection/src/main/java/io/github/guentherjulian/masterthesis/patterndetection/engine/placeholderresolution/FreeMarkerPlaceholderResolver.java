package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.TransformationFunction;

public class FreeMarkerPlaceholderResolver extends AbstractPlaceholderResolver {

	private final String TRANSFORMATION_FUNCTION_PACKAGE_NAME = "io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.freemarker";

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

	@Override
	public String getTransformationFunctionPackageName() {
		return this.TRANSFORMATION_FUNCTION_PACKAGE_NAME;
	}

	@Override
	public String getTransformationFunctionJavaClassName(String transformationFunction) {
		String prefix = "FreeMarker";
		String[] functionParts = transformationFunction.split("_");
		functionParts = Arrays.asList(functionParts).stream()
				.map(elem -> new String(Character.toUpperCase(elem.charAt(0)) + elem.substring(1)))
				.toArray(String[]::new);
		return prefix + String.join("", functionParts) + "Function";
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

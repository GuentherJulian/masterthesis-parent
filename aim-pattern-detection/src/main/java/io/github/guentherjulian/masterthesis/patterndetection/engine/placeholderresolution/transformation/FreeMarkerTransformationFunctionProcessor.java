package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation;

import java.util.Arrays;

public class FreeMarkerTransformationFunctionProcessor extends AbstractTransformationFunctionProcessor {

	private final String TRANSFORMATION_FUNCTION_PACKAGE_NAME = "io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.freemarker";

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
}
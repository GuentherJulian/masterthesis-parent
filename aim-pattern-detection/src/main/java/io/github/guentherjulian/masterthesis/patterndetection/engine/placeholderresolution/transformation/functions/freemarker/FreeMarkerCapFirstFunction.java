package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.freemarker;

import java.util.ArrayList;
import java.util.List;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.TransformationFunction;

public class FreeMarkerCapFirstFunction implements TransformationFunction {

	@Override
	public List<String> transform(String substitution) {
		List<String> substitutions = new ArrayList<>();
		substitutions.add(Character.toLowerCase(substitution.charAt(0)) + substitution.substring(1));
		substitutions.add(Character.toUpperCase(substitution.charAt(0)) + substitution.substring(1));
		return substitutions;
	}

}

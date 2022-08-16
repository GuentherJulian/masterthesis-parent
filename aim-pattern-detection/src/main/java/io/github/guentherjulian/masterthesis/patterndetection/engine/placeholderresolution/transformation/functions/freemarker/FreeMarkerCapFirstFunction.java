package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.freemarker;

import java.util.ArrayList;
import java.util.List;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.ReverseTransformationFunction;

public class FreeMarkerCapFirstFunction implements ReverseTransformationFunction {

	@Override
	public List<String> transform(String substitution, String... args) {
		List<String> substitutions = new ArrayList<>();
		substitutions.add(Character.toLowerCase(substitution.charAt(0)) + substitution.substring(1));
		substitutions.add(Character.toUpperCase(substitution.charAt(0)) + substitution.substring(1));
		return substitutions;
	}

}

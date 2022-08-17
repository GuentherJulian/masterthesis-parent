package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.freemarker;

import java.util.ArrayList;
import java.util.List;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.ReverseTransformationFunction;
import io.github.guentherjulian.masterthesis.patterndetection.exception.PlaceholderTransformationFunctionCallException;

public class FreeMarkerReplaceFunction implements ReverseTransformationFunction {

	@Override
	public List<String> transform(String substitution, String... args)
			throws PlaceholderTransformationFunctionCallException {
		if (args.length != 2) {
			throw new PlaceholderTransformationFunctionCallException(
					"FreeMarker replace function needs two arguments. Given arguments: " + args.length);
		}

		List<String> substitutions = new ArrayList<>();

		// remove leading and trailing quotation marks
		args[0] = removeQuotationMarks(args[0]);
		args[1] = removeQuotationMarks(args[1]);

		substitutions.add(substitution.replaceAll(args[1], args[0]));
		return substitutions;
	}

	private String removeQuotationMarks(String argument) {
		String transformedArgument = argument;
		if (argument.startsWith("\"") || argument.startsWith("'")) {
			transformedArgument = transformedArgument.substring(1);
		}
		if (argument.endsWith("\"") || argument.endsWith("'")) {
			transformedArgument = transformedArgument.substring(0, transformedArgument.length() - 1);
		}
		return transformedArgument;
	}
}

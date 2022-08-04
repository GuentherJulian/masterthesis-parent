package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.TransformationFunctionProcessor;

public abstract class AbstractPlaceholderResolver implements PlaceholderResolver {

	@Override
	public PlaceholderResolutionResult resolvePlaceholder(String placeholder, String substitution) throws Exception {
		PlaceholderResolutionResult placeholderResolutionResult = new PlaceholderResolutionResult();
		placeholderResolutionResult.setPlaceholder(getPlaceholderName(placeholder));

		Set<String> substitutions = new HashSet<>();
		String[] transformationFunctions = getTransformationFunctions(placeholder);
		if (transformationFunctions == null) {
			// atomic placeholder
			substitutions.add(substitution);
		} else {
			// call transformation functions
			substitutions.add(substitution);
			for (String transformationFunction : transformationFunctions) {
				TransformationFunctionProcessor transformationFunctionProcessor = getTransformationFunctionProcessor();

				Set<String> newSubstitutions = new HashSet<>();
				for (String sub : substitutions) {
					List<String> possibleSubstitutions = new ArrayList<>();
					try {
						possibleSubstitutions = transformationFunctionProcessor
								.processTransformationFunction(transformationFunction, sub);
					} catch (ClassNotFoundException e) {
						// transformation function is not implemented
						placeholderResolutionResult.setPlaceholder(placeholder);
						Set<String> subs = new HashSet<String>();
						subs.add(substitution);
						placeholderResolutionResult.setSubstitutions(subs);
						return placeholderResolutionResult;
					}

					for (String possibleSubstitution : possibleSubstitutions) {
						newSubstitutions.add(possibleSubstitution);
					}
				}
				substitutions = newSubstitutions;
			}
		}

		placeholderResolutionResult.setSubstitutions(substitutions);
		return placeholderResolutionResult;
	}

}

package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.PlaceholderResolutionException;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.TransformationFunction;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.TransformationFunctionProcessor;

public abstract class AbstractPlaceholderResolver implements PlaceholderResolver {

	abstract List<TransformationFunction> getTransformationFunctions(String placeholderExpression);

	abstract String getPlaceholderName(String placeholderExpression);

	abstract TransformationFunctionProcessor getTransformationFunctionProcessor();

	@Override
	public PlaceholderResolutionResult resolvePlaceholder(String placeholderExpression, String substitution)
			throws PlaceholderResolutionException {
		PlaceholderResolutionResult placeholderResolutionResult = new PlaceholderResolutionResult();
		placeholderResolutionResult.setPlaceholder(getPlaceholderName(placeholderExpression));

		Set<String> substitutions = new HashSet<>();
		List<TransformationFunction> transformationFunctions = getTransformationFunctions(placeholderExpression);
		if (transformationFunctions == null) {
			// atomic placeholder
			substitutions.add(substitution);
		} else {
			// call transformation functions
			substitutions.add(substitution);
			for (TransformationFunction transformationFunction : transformationFunctions) {
				TransformationFunctionProcessor transformationFunctionProcessor = getTransformationFunctionProcessor();

				Set<String> newSubstitutions = new HashSet<>();
				for (String sub : substitutions) {
					List<String> possibleSubstitutions = new ArrayList<>();
					try {
						possibleSubstitutions = transformationFunctionProcessor.processTransformationFunction(
								transformationFunction.getFunctionName(), transformationFunction.getArgs(), sub);
					} catch (ClassNotFoundException e) {
						// transformation function is not implemented
						placeholderResolutionResult.setPlaceholder(placeholderExpression);
						Set<String> subs = new HashSet<String>();
						subs.add(substitution);
						placeholderResolutionResult.setSubstitutions(subs);
						return placeholderResolutionResult;
					} catch (Exception e) {
						throw new PlaceholderResolutionException(e.getMessage());
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
package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.TransformationFunction;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.TransformationFunctionProcessor;
import io.github.guentherjulian.masterthesis.patterndetection.exception.PlaceholderResolutionException;

public abstract class AbstractPlaceholderResolver implements PlaceholderResolver {

	protected abstract List<TransformationFunction> getTransformationFunctions(String placeholderExpression);

	protected abstract String getPlaceholderName(String placeholderExpression);

	@Override
	public PlaceholderResolutionResult resolvePlaceholder(String placeholderExpression, String substitution)
			throws PlaceholderResolutionException {
		PlaceholderResolutionResult placeholderResolutionResult = new PlaceholderResolutionResult();

		String placeholderName = getPlaceholderName(placeholderExpression);
		placeholderResolutionResult.setPlaceholder(placeholderName);
		placeholderResolutionResult.setPlaceholderAtomic(isPlaceholderAtomic(placeholderName));

		Set<String> substitutions = new HashSet<>();
		List<TransformationFunction> transformationFunctions = getTransformationFunctions(placeholderExpression);
		if (transformationFunctions == null) {
			// atomic placeholder
			substitutions.add(substitution);
		} else {
			// call transformation functions
			substitutions.add(substitution);
			for (TransformationFunction transformationFunction : transformationFunctions) {
				TransformationFunctionProcessor transformationFunctionProcessor = new TransformationFunctionProcessor(
						this);

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
						placeholderResolutionResult.setPlaceholderAtomic(false);
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

	protected boolean isPlaceholderAtomic(String placeholderName) {
		return true;
	}

	@Override
	public String transformPlaceholderNotation(String orginialPlaceholder) {
		return orginialPlaceholder;
	}
}
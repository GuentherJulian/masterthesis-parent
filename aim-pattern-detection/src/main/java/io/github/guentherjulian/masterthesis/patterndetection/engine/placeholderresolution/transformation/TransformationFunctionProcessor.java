package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation;

import java.util.List;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.ReverseTransformationFunction;

public class TransformationFunctionProcessor {

	private PlaceholderResolver placeholderResolver;

	public TransformationFunctionProcessor(PlaceholderResolver placeholderResolver) {
		this.placeholderResolver = placeholderResolver;
	}

	public List<String> processTransformationFunction(String transformationFunction, String[] args, String substitution)
			throws Exception {
		String packageName = this.placeholderResolver.getTransformationFunctionPackageName();
		String javaClassName = this.placeholderResolver.getTransformationFunctionJavaClassName(transformationFunction);
		String fullyQualifiedClassName = packageName + "." + javaClassName;

		Class<?> functionClass = Class.forName(fullyQualifiedClassName);
		Object functionObject = functionClass.getDeclaredConstructor().newInstance();
		ReverseTransformationFunction function = (ReverseTransformationFunction) functionClass.cast(functionObject);

		return function.transform(substitution, args);
	}
}

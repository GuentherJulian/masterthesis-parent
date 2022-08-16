package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation;

import java.util.List;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.ReverseTransformationFunction;

abstract class AbstractTransformationFunctionProcessor implements TransformationFunctionProcessor {

	abstract String getTransformationFunctionPackageName();

	abstract String getTransformationFunctionJavaClassName(String transformationFunction);

	@Override
	public List<String> processTransformationFunction(String transformationFunction, String[] args, String substitution)
			throws Exception {
		String packageName = getTransformationFunctionPackageName();
		String javaClassName = getTransformationFunctionJavaClassName(transformationFunction);
		String fullyQualifiedClassName = packageName + "." + javaClassName;

		Class<?> functionClass = Class.forName(fullyQualifiedClassName);
		Object functionObject = functionClass.getDeclaredConstructor().newInstance();
		ReverseTransformationFunction function = (ReverseTransformationFunction) functionClass.cast(functionObject);

		return function.transform(substitution, args);
	}
}

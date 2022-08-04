package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation;

import java.util.List;

import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation.functions.TransformationFunction;

abstract class AbstractTransformationFunctionProcessor implements TransformationFunctionProcessor {

	@Override
	public List<String> processTransformationFunction(String transformationFunction, String substitution)
			throws Exception {
		String packageName = getTransformationFunctionPackageName();
		String javaClassName = getTransformationFunctionJavaClassName(transformationFunction);
		String fullyQualifiedClassName = packageName + "." + javaClassName;

		Class<?> functionClass = Class.forName(fullyQualifiedClassName);
		Object functionObject = functionClass.getDeclaredConstructor().newInstance();
		TransformationFunction function = (TransformationFunction) functionClass.cast(functionObject);

		return function.transform(substitution);
	}
}

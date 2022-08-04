package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation;

import java.util.List;

public interface TransformationFunctionProcessor {

	List<String> processTransformationFunction(String transformationFunction, String substitution) throws Exception;

	String getTransformationFunctionPackageName();

	String getTransformationFunctionJavaClassName(String transformationFunction);

}

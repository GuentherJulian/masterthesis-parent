package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.transformation;

public class TransformationFunction {

	private String functionName;
	private String[] args;

	public TransformationFunction(String functionName) {
		this.functionName = functionName;
	}

	public TransformationFunction(String functionName, String... args) {
		this.functionName = functionName;
		this.args = args;
	}

	public String getFunctionName() {
		return functionName;
	}

	public String[] getArgs() {
		return args;
	}
}

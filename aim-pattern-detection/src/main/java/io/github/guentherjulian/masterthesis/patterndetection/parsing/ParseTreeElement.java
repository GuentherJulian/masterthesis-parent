package io.github.guentherjulian.masterthesis.patterndetection.parsing;

public interface ParseTreeElement {
	String getText();

	default boolean isOptionalElementInTemplate() {
		return false;
	}
}

package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.freemarker;

import java.util.HashMap;
import java.util.Map;

public class FreeMarkerMacro {

	private String macroName;
	private Map<String, String> parameters;
	private String macroContent;

	public String getMacroName() {
		return macroName;
	}

	public void setMacroName(String macroName) {
		this.macroName = macroName;
	}

	public Map<String, String> getParameters() {
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getMacroContent() {
		return macroContent;
	}

	public void setMacroContent(String macroContent) {
		this.macroContent = macroContent;
	}
}

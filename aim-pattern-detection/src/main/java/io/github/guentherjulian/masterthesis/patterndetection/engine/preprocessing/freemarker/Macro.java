package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.freemarker;

import java.util.HashMap;
import java.util.Map;

public class Macro {

	private String macroName;
	private Map<String, String> parameters;
	private String macroText;

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

	public String getMacroText() {
		return macroText;
	}

	public void setMacroText(String macroText) {
		this.macroText = macroText;
	}
}

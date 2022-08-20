package io.github.guentherjulian.masterthesis.patterndetection.aimpattern;

import java.nio.file.Path;

public class AimPatternTemplate {
	private Path templatePath;
	private String instantiationPath;

	public AimPatternTemplate(Path templatePath, String instantiationPath) {
		this.templatePath = templatePath;
		this.instantiationPath = instantiationPath;
	}

	public AimPatternTemplate(Path templatePath, String instantiationPath, byte[] preprocessedTemplateByteArray) {
		this.templatePath = templatePath;
		this.instantiationPath = instantiationPath;
	}

	public Path getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(Path templatePath) {
		this.templatePath = templatePath;
	}

	public String getInstantiationPath() {
		return instantiationPath;
	}

	public void setInstantiationPath(String instantiationPath) {
		this.instantiationPath = instantiationPath;
	}
}

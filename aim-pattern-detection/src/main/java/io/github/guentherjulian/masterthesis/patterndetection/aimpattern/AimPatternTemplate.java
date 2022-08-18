package io.github.guentherjulian.masterthesis.patterndetection.aimpattern;

import java.nio.file.Path;

public class AimPatternTemplate {
	private Path templatePath;
	private String instantiationPath;
	private byte[] preprocessedTemplateByteArray;
	private boolean isPreprocessed;

	public AimPatternTemplate(Path templatePath, String instantiationPath) {
		this.templatePath = templatePath;
		this.instantiationPath = instantiationPath;

		this.setPreprocessedTemplateByteArray(null);
		this.setPreprocessed(false);
	}

	public AimPatternTemplate(Path templatePath, String instantiationPath, byte[] preprocessedTemplateByteArray) {
		this.templatePath = templatePath;
		this.instantiationPath = instantiationPath;

		this.setPreprocessedTemplateByteArray(preprocessedTemplateByteArray);
		this.setPreprocessed(true);
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

	public byte[] getPreprocessedTemplateByteArray() {
		return preprocessedTemplateByteArray;
	}

	public void setPreprocessedTemplateByteArray(byte[] preprocessedTemplateByteArray) {
		this.preprocessedTemplateByteArray = preprocessedTemplateByteArray;
	}

	public boolean isPreprocessed() {
		return isPreprocessed;
	}

	public void setPreprocessed(boolean isPreprocessed) {
		this.isPreprocessed = isPreprocessed;
	}
}

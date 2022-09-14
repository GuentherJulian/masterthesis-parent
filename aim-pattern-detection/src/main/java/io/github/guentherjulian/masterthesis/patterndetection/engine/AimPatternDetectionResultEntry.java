package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;

public class AimPatternDetectionResultEntry {
	private Path compilationUnitPath;
	private Path templatePath;
	private TreeMatch treeMatchResult;
	private boolean isTemplateUnparseable;

	public AimPatternDetectionResultEntry(Path compilationUnitPath, Path templatePath, TreeMatch treeMatchResult) {
		this.compilationUnitPath = compilationUnitPath;
		this.templatePath = templatePath;
		this.treeMatchResult = treeMatchResult;
		this.isTemplateUnparseable = false;
	}

	public Path getCompilationUnitPath() {
		return compilationUnitPath;
	}

	public Path getTemplatePath() {
		return templatePath;
	}

	public TreeMatch getTreeMatchResult() {
		return treeMatchResult;
	}

	public boolean isMatch() {
		if (this.treeMatchResult == null) {
			return false;
		}
		return this.treeMatchResult.isMatch();
	}

	public Map<String, Set<String>> getPlaceholderSubstitutions() {
		if (this.treeMatchResult == null) {
			return null;
		}
		return this.treeMatchResult.getPlaceholderSubstitutions();
	}

	public boolean isTemplateUnparseable() {
		return isTemplateUnparseable;
	}

	public void setTemplateUnparseable(boolean isTemplateUnparseable) {
		this.isTemplateUnparseable = isTemplateUnparseable;
	}
}

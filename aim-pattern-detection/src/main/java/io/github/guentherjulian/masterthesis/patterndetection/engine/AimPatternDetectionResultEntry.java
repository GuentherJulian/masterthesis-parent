package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;

public class AimPatternDetectionResultEntry {
	private Path compilationUnitPath;
	private Path templatePath;
	private TreeMatch treeMatchResult;

	public AimPatternDetectionResultEntry(Path compilationUnitPath, Path templatePath, TreeMatch treeMatchResult) {
		this.compilationUnitPath = compilationUnitPath;
		this.templatePath = templatePath;
		this.treeMatchResult = treeMatchResult;
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
		return this.treeMatchResult.isMatch();
	}

	public Map<String, Set<String>> getPlaceholderSubstitutions() {
		return this.treeMatchResult.getPlaceholderSubstitutions();
	}
}

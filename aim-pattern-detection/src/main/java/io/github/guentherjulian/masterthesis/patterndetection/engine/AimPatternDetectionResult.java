package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;

public class AimPatternDetectionResult {

	private List<AimPatternDetectionResultEntry> results;
	private int numParsedTemplates;
	private int numParsedCompilationUnits;
	private int numComparedFiles;
	private long processingTime;

	public AimPatternDetectionResult() {
		this.results = new ArrayList<>();
	}

	public void addPatternDetectionResultEntry(AimPatternDetectionResultEntry aimPatternDetectionResultEntry) {
		this.results.add(aimPatternDetectionResultEntry);
	}

	public List<AimPatternDetectionResultEntry> getResults() {
		return this.results;
	}

	public List<AimPatternDetectionResultEntry> getSuccessfullyMatchedResults() {
		return this.results.stream().filter(patternDetectionResultEntry -> patternDetectionResultEntry.isMatch())
				.collect(Collectors.toList());
	}

	public List<TreeMatch> getTreeMatches() {
		return this.results.stream().map(AimPatternDetectionResultEntry::getTreeMatchResult)
				.collect(Collectors.toList());
	}

	public int getNumParsedTemplates() {
		return numParsedTemplates;
	}

	public void setNumParsedTemplates(int numParsedTemplates) {
		this.numParsedTemplates = numParsedTemplates;
	}

	public int getNumParsedCompilationUnits() {
		return numParsedCompilationUnits;
	}

	public void setNumParsedCompilationUnits(int numParsedCompilationUnits) {
		this.numParsedCompilationUnits = numParsedCompilationUnits;
	}

	public int getNumComparedFiles() {
		return numComparedFiles;
	}

	public void setNumComparedFiles(int numComparedFiles) {
		this.numComparedFiles = numComparedFiles;
	}

	public long getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(long processingTime) {
		this.processingTime = processingTime;
	}
}

package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;

public class AimPatternDetectionResult {

	private List<AimPatternDetectionResultEntry> results;
	private int numTemplatesTotal;
	private int numCompilationUnitsTotal;
	private int numParsedTemplates;
	private int numParsedCompilationUnits;
	private int numComparedFiles;
	private int numInstantiationPathMatches;
	private int numFileMatches;
	private int numParseableTemplates;
	private int numUnparseableTemplates;
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

	public int getNumInstantiationPathMatches() {
		return numInstantiationPathMatches;
	}

	public void setNumInstantiationPathMatches(int numInstantiationPathMatches) {
		this.numInstantiationPathMatches = numInstantiationPathMatches;
	}

	public int getNumFileMatches() {
		return numFileMatches;
	}

	public void setNumFileMatches(int numFileMatches) {
		this.numFileMatches = numFileMatches;
	}

	public int getNumParseableTemplates() {
		return numParseableTemplates;
	}

	public void setNumParseableTemplates(int numParseableTemplates) {
		this.numParseableTemplates = numParseableTemplates;
	}

	public int getNumUnparseableTemplates() {
		return numUnparseableTemplates;
	}

	public void setNumUnparseableTemplates(int numUnparseableTemplates) {
		this.numUnparseableTemplates = numUnparseableTemplates;
	}

	public int getNumTemplatesTotal() {
		return numTemplatesTotal;
	}

	public void setNumTemplatesTotal(int numTemplatesTotal) {
		this.numTemplatesTotal = numTemplatesTotal;
	}

	public int getNumCompilationUnitsTotal() {
		return numCompilationUnitsTotal;
	}

	public void setNumCompilationUnitsTotal(int numCompilationUnitsTotal) {
		this.numCompilationUnitsTotal = numCompilationUnitsTotal;
	}
}

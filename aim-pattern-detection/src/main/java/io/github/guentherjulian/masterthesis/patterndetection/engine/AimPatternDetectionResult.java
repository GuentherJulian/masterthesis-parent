package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;

public class AimPatternDetectionResult {

	private List<AimPatternDetectionResultEntry> results;
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

	public long getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(long processingTime) {
		this.processingTime = processingTime;
	}
}

package io.github.guentherjulian.masterthesis.patterndetection.aimpattern;

import java.util.ArrayList;
import java.util.List;

public class AimPattern {
	
	private List<AimPatternTemplate> aimPatternTemplates;

	public AimPattern() {
		this.aimPatternTemplates = new ArrayList<AimPatternTemplate>();
	}
	
	public AimPattern(List<AimPatternTemplate> aimPatternTemplates) {
		this.aimPatternTemplates = aimPatternTemplates;
	}

	public List<AimPatternTemplate> getAimPatternTemplates() {
		return aimPatternTemplates;
	}

	public void setAimPatternTemplates(List<AimPatternTemplate> aimPatternTemplates) {
		this.aimPatternTemplates = aimPatternTemplates;
	}
	
	public void addAimPatternTemplate(AimPatternTemplate aimPatternTemplate) {
		if(this.aimPatternTemplates == null) {
			this.aimPatternTemplates = new ArrayList<AimPatternTemplate>();
		}
		this.aimPatternTemplates.add(aimPatternTemplate);
	}
}
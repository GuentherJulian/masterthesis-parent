package io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CLanguageConfiguration extends AbstractObjectLanguageConfiguration {

	private static final Set<String> nonOrderingNodes = new HashSet<>(Arrays.asList("BlockItem"));

	public CLanguageConfiguration(String metaLanguagePrefix) {
		super(metaLanguagePrefix);
	}

	@Override
	public Set<String> getNonOrderingNodes() {
		return this.enrichNonOrderingNodes(nonOrderingNodes);
	}

}

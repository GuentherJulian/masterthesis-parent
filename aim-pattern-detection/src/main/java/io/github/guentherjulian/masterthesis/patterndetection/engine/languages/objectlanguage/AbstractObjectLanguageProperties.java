package io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

abstract class AbstractObjectLanguageProperties implements ObjectLanguageProperties {

	private static final Set<String> nonOrderingNodesPostfixes = new HashSet<>(
			Arrays.asList("Context", "OptContext", "StarContext", "PlusContext"));

	private String metaLanguagePrefix;

	public AbstractObjectLanguageProperties(String metaLanguagePrefix) {
		this.metaLanguagePrefix = metaLanguagePrefix.toLowerCase();
	}

	protected Set<String> enrichNonOrderingNodes(Set<String> nonOrderingNodes) {
		Set<String> nonOrderedNodes = new HashSet<>();
		for (String nonOrderingNode : nonOrderingNodes) {
			nonOrderedNodes.add(nonOrderingNode + "Context");
			for (String nonOrderingNodesPostfix : nonOrderingNodesPostfixes) {
				nonOrderedNodes.add(this.metaLanguagePrefix + Character.toLowerCase(nonOrderingNode.charAt(0))
						+ nonOrderingNode.substring(1) + nonOrderingNodesPostfix);
			}
		}
		return nonOrderedNodes;
	}
}

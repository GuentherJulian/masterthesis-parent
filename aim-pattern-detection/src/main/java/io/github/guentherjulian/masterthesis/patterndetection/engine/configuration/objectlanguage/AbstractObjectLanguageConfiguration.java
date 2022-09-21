package io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

abstract class AbstractObjectLanguageConfiguration implements ObjectLanguageConfiguration {

	private static final Set<String> nonOrderingNodesPostfixes = new HashSet<>(
			Arrays.asList("Context", "OptContext", "StarContext", "PlusContext"));

	private String metaLanguagePrefix;

	public AbstractObjectLanguageConfiguration(String metaLanguagePrefix) {
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

	protected Set<String> enrichOptionalTemplateElements(Set<String> optionalNodes) {
		Set<String> optionalTemplateNodes = new HashSet<>();
		for (String optionalNode : optionalNodes) {
			optionalTemplateNodes.add(optionalNode + "Context");
		}
		return optionalTemplateNodes;
	}

	@Override
	public Set<String> getOptionalNodesForTemplates() {
		return new HashSet<>();
	}
}

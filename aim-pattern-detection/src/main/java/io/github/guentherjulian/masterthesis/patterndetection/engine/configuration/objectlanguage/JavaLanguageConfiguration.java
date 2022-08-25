package io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JavaLanguageConfiguration extends AbstractObjectLanguageConfiguration {

	private static final Set<String> nonOrderingNodes = new HashSet<>(Arrays.asList("ImportDeclaration",
			"InterfaceMemberDeclaration", "ClassMemberDeclaration", "TypeDeclaration", "ClassBodyDeclaration"));

	public JavaLanguageConfiguration(String metaLanguagePrefix) {
		super(metaLanguagePrefix);
	}

	@Override
	public Set<String> getNonOrderingNodes() {
		return this.enrichNonOrderingNodes(nonOrderingNodes);
	}

}

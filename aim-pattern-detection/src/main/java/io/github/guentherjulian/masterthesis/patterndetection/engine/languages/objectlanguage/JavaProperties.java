package io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JavaProperties extends AbstractObjectLanguageProperties {

	private static final Set<String> nonOrderingNodes = new HashSet<>(Arrays.asList("ImportDeclaration",
			"InterfaceMemberDeclaration", "ClassMemberDeclaration", "TypeDeclaration", "ClassBodyDeclaration"));
	// TODO check if ClassBodyDeclaration is a correct member

	public JavaProperties(String placeholderPrefix) {
		super(placeholderPrefix);
	}

	@Override
	public Set<String> getNonOrderingNodes() {
		return this.enrichNonOrderingNodes(nonOrderingNodes);
	}

}

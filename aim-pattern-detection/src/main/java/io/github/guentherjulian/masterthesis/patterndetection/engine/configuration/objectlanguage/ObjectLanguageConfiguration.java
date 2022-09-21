package io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage;

import java.util.Set;

public interface ObjectLanguageConfiguration {
	Set<String> getNonOrderingNodes();

	Set<String> getOptionalNodesForTemplates();
}

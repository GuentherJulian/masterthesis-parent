package io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage;

import java.util.regex.Pattern;

public interface MetaLanguagePattern {

	Pattern getMetaLangPatternPlaceholder();

	Pattern getMetaLangPatternIf();

	Pattern getMetaLangPatternElse();

	Pattern getMetaLangPatternIfElse();

	Pattern getMetaLangPatternIfClose();

	Pattern getMetaLangPatternList();

	Pattern getMetaLangPatternListClose();
}

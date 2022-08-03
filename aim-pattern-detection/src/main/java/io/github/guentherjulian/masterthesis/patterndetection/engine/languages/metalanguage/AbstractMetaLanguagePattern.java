package io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class AbstractMetaLanguagePattern implements MetaLanguagePattern {

	public String match(Pattern pattern, String input) {
		String result = null;
		Matcher m = pattern.matcher(input);
		if (m.matches()) {
			result = m.group(1).trim();
		}
		if (result == null) {
			m = pattern.matcher(input);
			if (m.matches()) {
				result = m.group(1).trim();
			}
		}
		return result;
	}
}

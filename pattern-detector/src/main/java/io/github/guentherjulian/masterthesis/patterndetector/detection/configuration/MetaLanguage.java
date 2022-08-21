package io.github.guentherjulian.masterthesis.patterndetector.detection.configuration;

import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.FreeMarkerLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.FreeMarkerMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.StringTemplateLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.StringTemplateMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.VelocityLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.VelocityMetaLanguagePattern;

public enum MetaLanguage {

	FREEMARKER, VELOCITY, STRINGTEMPLATE;

	public static MetaLanguage getMetaLanguage(String metaLanguageString) {
		MetaLanguage metaLanguage = null;
		if (metaLanguageString.equals("FreeMarker")) {
			metaLanguage = MetaLanguage.FREEMARKER;
		}
		if (metaLanguageString.equals("Velocity")) {
			metaLanguage = MetaLanguage.VELOCITY;
		}
		if (metaLanguageString.equals("StringTemplate")) {
			metaLanguage = MetaLanguage.STRINGTEMPLATE;
		}
		return metaLanguage;
	}

	public static MetaLanguageConfiguration getMetaLanguageConfiguration(MetaLanguage metaLanguage) {
		MetaLanguageLexerRules metaLanguageLexerRules = null;
		MetaLanguagePattern metaLanguagePattern = null;
		if (metaLanguage == MetaLanguage.FREEMARKER) {
			metaLanguageLexerRules = new FreeMarkerLexerRuleNames();
			metaLanguagePattern = new FreeMarkerMetaLanguagePattern();
		}
		if (metaLanguage == MetaLanguage.VELOCITY) {
			metaLanguageLexerRules = new VelocityLexerRuleNames();
			metaLanguagePattern = new VelocityMetaLanguagePattern();
		}
		if (metaLanguage == MetaLanguage.STRINGTEMPLATE) {
			metaLanguageLexerRules = new StringTemplateLexerRuleNames();
			metaLanguagePattern = new StringTemplateMetaLanguagePattern();
		}
		return new MetaLanguageConfiguration(metaLanguageLexerRules, metaLanguagePattern);
	}

}

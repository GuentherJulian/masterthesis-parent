package io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import io.github.guentherjulian.masterthesis.patterndetection.exception.InvalidMetalanguageConfigurationException;

public class MetaLanguageConfiguration {

	private MetaLanguageLexerRules metaLanguageLexerRules;
	private MetaLanguagePattern metaLanguagePattern;
	private String metaLanguagePrefix;

	public MetaLanguageConfiguration(MetaLanguageLexerRules metaLanguageLexerRules,
			MetaLanguagePattern metaLanguagePattern, String metaLanguagePrefix) {
		this.metaLanguageLexerRules = metaLanguageLexerRules;
		this.metaLanguagePattern = metaLanguagePattern;
		this.setMetaLanguagePrefix(metaLanguagePrefix);
	}

	public MetaLanguageConfiguration(Path configPath) throws InvalidMetalanguageConfigurationException {
		this.readMetaLanguageConfiguration(configPath);
	}

	public MetaLanguageLexerRules getMetaLanguageLexerRules() {
		return metaLanguageLexerRules;
	}

	public void setMetaLanguageLexerRules(MetaLanguageLexerRules metaLanguageLexerRules) {
		this.metaLanguageLexerRules = metaLanguageLexerRules;
	}

	public MetaLanguagePattern getMetaLanguagePattern() {
		return metaLanguagePattern;
	}

	public void setMetaLanguagePattern(MetaLanguagePattern metaLanguagePattern) {
		this.metaLanguagePattern = metaLanguagePattern;
	}

	public String getMetaLanguagePrefix() {
		return metaLanguagePrefix;
	}

	public void setMetaLanguagePrefix(String metaLanguagePrefix) {
		this.metaLanguagePrefix = metaLanguagePrefix;
	}

	public void readMetaLanguageConfiguration(Path configFile) throws InvalidMetalanguageConfigurationException {
		if (!Files.exists(configFile)) {
			throw new InvalidMetalanguageConfigurationException(
					"Unable to load configuration. Filepath does not exist: " + configFile);
		}

		try {
			FileInputStream inputStream = new FileInputStream(configFile.toFile());
			Properties properties = new Properties();
			properties.load(inputStream);

			String regexIf = properties.getProperty("METALANGUAGE_REGEX_IF");
			String regexIfElse = properties.getProperty("METALANGUAGE_REGEX_IF_ELSE");
			String regexElse = properties.getProperty("METALANGUAGE_REGEX_ELSE");
			String regexIfClose = properties.getProperty("METALANGUAGE_REGEX_IF_CLOSE");
			String regexList = properties.getProperty("METALANGUAGE_REGEX_LIST");
			String regexListCollectionVar = properties.getProperty("METALANGUAGE_REGEX_LIST_COLLECTION_VAR");
			String regexListIterationVar = properties.getProperty("METALANGUAGE_REGEX_LIST_ITERATION_VAR");
			String regexListClose = properties.getProperty("METALANGUAGE_REGEX_LIST_CLOSE");
			String regexPlaceholder = properties.getProperty("METALANGUAGE_REGEX_PLACEHOLDER");
			String metaLangFileExtension = properties.getProperty("METALANGUAGE_FILE_EXTENSION");

			String lexerRuleNamePlaceholder = properties.getProperty("METALANGUAGE_LEXER_RULE_PLACEHOLDER");
			String lexerRuleNameIf = properties.getProperty("METALANGUAGE_LEXER_RULE_IF");
			String lexerRuleNameIfElse = properties.getProperty("METALANGUAGE_LEXER_RULE_IF_ELSE");
			String lexerRuleNameElse = properties.getProperty("METALANGUAGE_LEXER_RULE_ELSE");
			String lexerRuleNameIfClose = properties.getProperty("METALANGUAGE_LEXER_RULE_IF_CLOSE");
			String lexerRuleNameList = properties.getProperty("METALANGUAGE_LEXER_RULE_LIST");
			String lexerRuleNameListClose = properties.getProperty("METALANGUAGE_LEXER_RULE_LIST_CLOSE");
			String lexerRuleNamePrefix = properties.getProperty("METALANGUAGE_LEXER_RULE_PREFIX");

			this.metaLanguagePattern = new CustomMetaLanguagePattern(regexPlaceholder, regexIf, regexIfElse, regexElse,
					regexIfClose, regexList, regexListCollectionVar, regexListIterationVar, regexListClose,
					metaLangFileExtension);

			this.metaLanguageLexerRules = new CustomLexerRuleNames(lexerRuleNamePrefix, lexerRuleNameIf,
					lexerRuleNameIfElse, lexerRuleNameElse, lexerRuleNameIfClose, lexerRuleNameList,
					lexerRuleNameListClose, lexerRuleNamePlaceholder);
		} catch (IOException e) {
			throw new InvalidMetalanguageConfigurationException(e.getMessage());
		}
	}
}
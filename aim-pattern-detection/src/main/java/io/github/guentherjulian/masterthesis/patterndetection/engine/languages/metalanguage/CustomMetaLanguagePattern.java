package io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage;

import java.util.regex.Pattern;

import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.ConfigurationException;

public class CustomMetaLanguagePattern extends AbstractMetaLanguagePattern {

	private static String META_LANG_PLACEHOLDER;

	private static String META_LANG_IF;

	private static String META_LANG_ELSE;

	private static String META_LANG_IFELSE;

	private static String META_LANG_IF_CLOSE;

	private static String META_LANG_LIST;

	private static String META_LANG_LIST_CLOSE;

	private static String META_LANG_FILE_EXTENSION;

	public CustomMetaLanguagePattern(String patternPlaceholder, String patternIf, String patternIfElse,
			String patternElse, String patternIfClose, String patternList, String patternListClose,
			String fileExtension) throws ConfigurationException {

		if (!checkParametersForValidity(patternPlaceholder, patternIf, patternIfElse, patternElse, patternIfClose,
				patternList, patternListClose, fileExtension)) {
			throw new ConfigurationException("Invalid configuration. You have to pass all parameters.");
		}

		META_LANG_PLACEHOLDER = patternPlaceholder;
		META_LANG_IF = patternIf;
		META_LANG_IFELSE = patternIfElse;
		META_LANG_ELSE = patternElse;
		META_LANG_IF_CLOSE = patternIfClose;
		META_LANG_LIST = patternList;
		META_LANG_LIST_CLOSE = patternListClose;
		META_LANG_FILE_EXTENSION = fileExtension;
	}

	@Override
	public Pattern getMetaLangPatternIf() {
		return Pattern.compile(META_LANG_IF);
	}

	@Override
	public Pattern getMetaLangPatternElse() {
		return Pattern.compile(META_LANG_ELSE);
	}

	@Override
	public Pattern getMetaLangPatternIfElse() {
		return Pattern.compile(META_LANG_IFELSE);
	}

	@Override
	public Pattern getMetaLangPatternIfClose() {
		return Pattern.compile(META_LANG_IF_CLOSE);
	}

	@Override
	public Pattern getMetaLangPatternList() {
		return Pattern.compile(META_LANG_LIST);
	}

	@Override
	public Pattern getMetaLangPatternListClose() {
		return Pattern.compile(META_LANG_LIST_CLOSE);
	}

	@Override
	public Pattern getMetaLangPatternPlaceholder() {
		return Pattern.compile(META_LANG_PLACEHOLDER);
	}

	@Override
	public String getMetaLangFileExtension() {
		return META_LANG_FILE_EXTENSION;
	}

	private boolean checkParametersForValidity(String patternPlaceholder, String patternIf, String patternIfElse,
			String patternElse, String patternIfClose, String patternList, String patternListClose,
			String fileExtension) {

		if (isEmpty(patternPlaceholder) || isEmpty(patternIf) || isEmpty(patternIfElse) || isEmpty(patternElse)
				|| isEmpty(patternIfClose) || isEmpty(patternList) || isEmpty(patternListClose)
				|| isEmpty(fileExtension)) {
			return false;
		}

		return true;
	}

	private boolean isEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return true;
		}
		return false;
	}
}

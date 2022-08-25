package io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage;

import java.util.regex.Pattern;

import io.github.guentherjulian.masterthesis.patterndetection.exception.InvalidMetalanguageConfigurationException;

public class CustomMetaLanguagePattern extends AbstractMetaLanguagePattern {

	private static String META_LANG_PLACEHOLDER;

	private static String META_LANG_IF;

	private static String META_LANG_ELSE;

	private static String META_LANG_IFELSE;

	private static String META_LANG_IF_CLOSE;

	private static String META_LANG_LIST;

	private static String META_LANG_LIST_COLLECTION_VAR;

	private static String META_LANG_LIST_ITERATION_VAR;

	private static String META_LANG_LIST_CLOSE;

	private static String META_LANG_FILE_EXTENSION;

	public CustomMetaLanguagePattern(String patternPlaceholder, String patternIf, String patternIfElse,
			String patternElse, String patternIfClose, String patternList, String patternListCollectionVar,
			String patternListIterationVar, String patternListClose, String fileExtension)
			throws InvalidMetalanguageConfigurationException {

		if (!checkParametersForValidity(patternPlaceholder, patternIf, patternIfElse, patternElse, patternIfClose,
				patternList, patternListCollectionVar, patternListIterationVar, patternListClose, fileExtension)) {
			throw new InvalidMetalanguageConfigurationException(
					"Invalid configuration. You have to pass all parameters.");
		}

		META_LANG_PLACEHOLDER = patternPlaceholder;
		META_LANG_IF = patternIf;
		META_LANG_IFELSE = patternIfElse;
		META_LANG_ELSE = patternElse;
		META_LANG_IF_CLOSE = patternIfClose;
		META_LANG_LIST = patternList;
		META_LANG_LIST_COLLECTION_VAR = patternListCollectionVar;
		META_LANG_LIST_ITERATION_VAR = patternListIterationVar;
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
	public Pattern getMetaLangPatternListCollectionVariable() {
		return Pattern.compile(META_LANG_LIST_COLLECTION_VAR);
	}

	@Override
	public Pattern getMetaLangPatternListIterationVariable() {
		return Pattern.compile(META_LANG_LIST_ITERATION_VAR);
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
			String patternElse, String patternIfClose, String patternList, String patternListCollectionVar,
			String patternListIterationVar, String patternListClose, String fileExtension) {

		if (isEmpty(patternPlaceholder) || isEmpty(patternIf) || isEmpty(patternIfElse) || isEmpty(patternElse)
				|| isEmpty(patternIfClose) || isEmpty(patternList) || isEmpty(patternListCollectionVar)
				|| isEmpty(patternListIterationVar) || isEmpty(patternListClose) || isEmpty(fileExtension)) {
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

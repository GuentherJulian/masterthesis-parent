package io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage;

import java.util.regex.Pattern;

public class FreeMarkerMetaLanguagePattern extends AbstractMetaLanguagePattern {

	private static final String META_LANG_PLACEHOLDER = "\\$\\{#(.+)#\\}";

	private static final String META_LANG_IF = "<#if(.+)>";

	private static final String META_LANG_ELSE = "<#else>";

	private static final String META_LANG_IFELSE = "<#elseif(.+)>";

	private static final String META_LANG_IF_CLOSE = "</#if>";

	private static final String META_LANG_LIST = "<#list(.+) as (.+)>";

	private static final String META_LANG_LIST_COLLECTION_VAR = "<#list (.+) as .+>";

	private static final String META_LANG_LIST_ITERATION_VAR = "<#list .+ as (.+)>";

	private static final String META_LANG_LIST_CLOSE = "</#list>";

	private static final String META_LANG_FILE_EXTENSION = "ftl";

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
}

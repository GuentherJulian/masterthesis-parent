package io.github.guentherjulian.masterthesis.patterndetection.parsing;

import java.util.ArrayList;

public class ParseTreePathList extends ArrayList<ParseTreeElement> implements ParseTreeElement {

	public ListType type;
	public String hint;
	public Boolean isMetaLang;
	public MetaLanguageElement metaLanguageElement;

	public ParseTreePathList(ListType type, String hint, MetaLanguageElement metaLanguageElement) {
		this.hint = hint;
		this.type = type;
		this.metaLanguageElement = metaLanguageElement;
	}

	public void setType(ListType type) {
		this.type = type;
	}

	public ListType getType() {
		return type;
	}

	public String getHint() {
		return hint;
	}

	public boolean isOrdered() {
		return type == ListType.ORDERED || type == ListType.LIST_PATTERN;
	}

	public boolean isAtomic() {
		return type == ListType.ATOMIC;
	}

	public boolean isListPattern() {
		return type == ListType.LIST_PATTERN;
	}

	public boolean isMetaLang() {
		return isMetaLang != null ? isMetaLang : type.isMetaLang();
	}

	public void setIsMetaLang(boolean isMetaLang) {
		this.isMetaLang = isMetaLang;
	}

	public MetaLanguageElement getMetaLanguageElement() {
		return metaLanguageElement;
	}

	public void setMetaLanguageElement(MetaLanguageElement metaLanguageElement) {
		this.metaLanguageElement = metaLanguageElement;
	}
}

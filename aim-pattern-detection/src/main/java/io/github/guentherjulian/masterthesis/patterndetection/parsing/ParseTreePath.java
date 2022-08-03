package io.github.guentherjulian.masterthesis.patterndetection.parsing;

public class ParseTreePath implements ParseTreeElement {

	private String text;
	private String name;
	private ParseTreePath parent;
	private boolean isMetaLanguageElement;
	private boolean containsMetaLanguage;

	public ParseTreePath(String text, String name, ParseTreePath parent, boolean isMetaLanguage) {
		this.text = text;
		this.name = name;
		this.parent = parent;
		this.isMetaLanguageElement = isMetaLanguage;
		this.containsMetaLanguage = this.isMetaLanguageElement
				|| (this.parent != null ? this.parent.containsMetaLanguage() : false);
	}

	public String getText() {
		return text;
	}

	public String getName() {
		return name;
	}

	public ParseTreePath getParent() {
		return parent;
	}

	public boolean isMetaLanguageElement() {
		return isMetaLanguageElement;
	}

	public boolean containsMetaLanguage() {
		return containsMetaLanguage;
	}

	@Override
	public String toString() {
		return getText();
	}
}

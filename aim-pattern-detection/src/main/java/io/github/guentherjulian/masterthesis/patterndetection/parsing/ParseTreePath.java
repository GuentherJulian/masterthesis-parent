package io.github.guentherjulian.masterthesis.patterndetection.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParseTreePath implements ParseTreeElement {

	private String text;
	private String name;
	private ParseTreePath parent;
	private boolean isMetaLanguageElement;
	private boolean containsMetaLanguage;
	private boolean isNonOrderingNode;

	public ParseTreePath(String text, String name, ParseTreePath parent, boolean isMetaLanguage,
			boolean isNonOrderingNode) {
		this.text = text;
		this.name = name;
		this.parent = parent;
		this.isMetaLanguageElement = isMetaLanguage;
		this.containsMetaLanguage = this.isMetaLanguageElement
				|| (this.parent != null ? this.parent.containsMetaLanguage() : false);
		this.isNonOrderingNode = isNonOrderingNode;
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

	public boolean isNonOrderingNode() {
		return isNonOrderingNode;
	}

	public void setNonOrderingNode(boolean isNonOrderingNode) {
		this.isNonOrderingNode = isNonOrderingNode;
	}

	@Override
	public String toString() {
		return getText();
	}

	public String getFullPathText() {
		String fullText = "";
		if (parent != null) {
			fullText = parent.getFullPathText();
		}
		fullText += text;
		return fullText;
	}

	public String getPath() {
		return getPathSegments().stream().collect(Collectors.joining("/"));
	}

	public List<String> getPathSegments() {
		List<String> pathSegments = new ArrayList<>();
		if (parent != null) {
			pathSegments.addAll(parent.getPathSegments());
		}
		pathSegments.add(name);
		return pathSegments;
	}

	private List<ParseTreePath> getPathElements() {
		List<ParseTreePath> pathElements = new ArrayList<>();
		ParseTreePath currParent = parent;
		while (currParent != null) {
			pathElements.add(currParent);
			currParent = currParent.parent;
		}
		// return in reverse order
		return IntStream.rangeClosed(1, pathElements.size()).mapToObj(i -> pathElements.get(pathElements.size() - i))
				.collect(Collectors.toList());
	}

	public Object getPureObjLangPath() {
		return getPathElements().stream().filter(e -> !e.isMetaLanguageElement).map(ParseTreePath::getName)
				.collect(Collectors.joining("/"));
	}
}

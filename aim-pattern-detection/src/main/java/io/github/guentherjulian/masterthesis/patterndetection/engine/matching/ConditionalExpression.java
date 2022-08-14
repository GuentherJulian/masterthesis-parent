package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import io.github.guentherjulian.masterthesis.patterndetection.parsing.MetaLanguageElement;

public class ConditionalExpression {

	private String condition;
	private MetaLanguageElement metaLanguageElement;
	private boolean value;

	public ConditionalExpression(String condition, MetaLanguageElement metaLanguageElement) {
		this.condition = condition;
		this.metaLanguageElement = metaLanguageElement;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public MetaLanguageElement getMetaLanguageElement() {
		return metaLanguageElement;
	}

	public void setMetaLanguageElement(MetaLanguageElement metaLanguageElement) {
		this.metaLanguageElement = metaLanguageElement;
	}

	public boolean isTrue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("[condition=%s, value=%s, metalanguage element=%s]", this.condition, this.value,
				this.metaLanguageElement);
	}
}

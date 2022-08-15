package io.github.guentherjulian.masterthesis.patterndetection.parsing;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.ObjectLanguageProperties;

public class ParseTreeTransformationListener implements ParseTreeListener {

	private static final Logger LOGGER = LogManager.getLogger(ParseTreeTransformationListener.class);

	private ParseTree parseTree;
	private Vocabulary parserVocabulary;
	private Map<String, List<String>> listPatterns;
	private MetaLanguageLexerRules metaLanguageLexerRules;
	private ObjectLanguageProperties objectLanguageProperties;
	// TODO Initialize non ordering nodes only once

	private ParseTreePathList parseTreePaths;
	private Stack<ParseTreePathList> currentCollection = new Stack<>();
	private Stack<Boolean> toPop = new Stack<>();
	private Stack<ParseTreePath> lastParseTreeElement = new Stack<>();
	private Stack<ParseTreePathList> lastPotentialElsePath = new Stack<>();

	public ParseTreeTransformationListener(Vocabulary parserVocabulary, Map<String, List<String>> listPatterns,
			MetaLanguageLexerRules metaLanguageLexerRules, ObjectLanguageProperties objectLanguageProperties) {
		this.parserVocabulary = parserVocabulary;
		this.listPatterns = listPatterns;
		this.metaLanguageLexerRules = metaLanguageLexerRules;
		this.objectLanguageProperties = objectLanguageProperties;
	}

	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
		String ruleName = ctx.getClass().getSimpleName();

		if (this.parseTreePaths == null) {
			this.parseTreePaths = new ParseTreePathList(ListType.ORDERED, ruleName, null);
			this.currentCollection.push(parseTreePaths);
			this.toPop.push(true);
		}
		this.lastParseTreeElement.push(new ParseTreePath(ctx.getText(), ruleName,
				this.lastParseTreeElement.isEmpty() ? null : this.lastParseTreeElement.peek(), false, false));

		if (this.objectLanguageProperties.getNonOrderingNodes().contains(ruleName)) {
			ParseTreePathList parseTreePathList = new ParseTreePathList(ListType.NONORDERED, ruleName, null);
			if (ruleName.toLowerCase()
					.matches(this.metaLanguageLexerRules.getMetaLanguagePrefix() + "(.+)(Opt|Star|Plus)?Context")) {
				parseTreePathList.setIsMetaLang(true);
			}
			currentCollection.peek().add(parseTreePathList);
			currentCollection.push(parseTreePathList);
			toPop.push(true);
		} else {
			toPop.push(false);
		}
	}

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
		this.lastParseTreeElement.pop();
		if (this.toPop.pop()) {
			this.currentCollection.pop();
		}
	}

	@Override
	public void visitTerminal(TerminalNode node) {
		String tokenType = this.parserVocabulary.getSymbolicName(node.getSymbol().getType());
		String parentParseRule = this.getGrammarRuleForTreeNode((ParserRuleContext) node.getParent());

		if (tokenType.equals(this.metaLanguageLexerRules.getPlaceholderTokenLexerRuleName())) {
			// if placeholder go one step above to test token
			tokenType = parentParseRule;
			parentParseRule = this.getGrammarRuleForTreeNode((ParserRuleContext) node.getParent().getParent());
		}

		// create a new ParseTreePathList for list pattern productions
		if (this.listPatterns.containsKey(tokenType) && this.listPatterns.get(tokenType).contains(parentParseRule)
				&& !this.currentCollection.peek().isListPattern()) {
			ParseTreePathList parseTreePathList = new ParseTreePathList(ListType.LIST_PATTERN, tokenType, null);
			this.currentCollection.peek().add(parseTreePathList);
			this.currentCollection.push(parseTreePathList);
		} else {
			// if current ParseTreePathList is list pattern and current terminal does not
			// longer is a list pattern entry, pop it
			if (this.currentCollection.peek().isListPattern() && (!this.listPatterns.containsKey(tokenType)
					|| !this.listPatterns.get(tokenType).contains(parentParseRule))) {
				this.currentCollection.pop();
			}
		}

		if (tokenType.equals(this.metaLanguageLexerRules.getIfTokenLexerRuleName())) {
			// IF case
			ParseTreePathList optionalList = new ParseTreePathList(ListType.OPTIONAL, node.getText(), null);
			this.currentCollection.peek().add(optionalList);
			this.currentCollection.push(optionalList);
			this.lastPotentialElsePath.push(optionalList);
			ParseTreePathList atomic = new ParseTreePathList(ListType.ATOMIC, node.getText(), MetaLanguageElement.IF);
			atomic.setIsMetaLang(true);
			this.currentCollection.peek().add(atomic);
			this.currentCollection.push(atomic);
		} else if (tokenType.equals(this.metaLanguageLexerRules.getElseTokenLexerRuleName())) {
			// ELSE case
			this.lastPotentialElsePath.peek().setType(ListType.ALTERNATIVE);
			this.currentCollection.pop();
			ParseTreePathList atomic = new ParseTreePathList(ListType.ATOMIC, node.getText(), MetaLanguageElement.ELSE);
			atomic.setIsMetaLang(true);
			this.currentCollection.peek().add(atomic);
			this.currentCollection.push(atomic);
		} else if (tokenType.equals(this.metaLanguageLexerRules.getIfElseTokenLexerRuleName())) {
			// IF ELSE case
			this.currentCollection.pop();
			ParseTreePathList atomic = new ParseTreePathList(ListType.ATOMIC, node.getText(),
					MetaLanguageElement.IF_ELSE);
			atomic.setIsMetaLang(true);
			this.currentCollection.peek().add(atomic);
			this.currentCollection.push(atomic);
		} else if (tokenType.equals(this.metaLanguageLexerRules.getIfCloseTokenLexerRuleName())) {
			// IF CLOSE case
			this.currentCollection.pop();
			this.currentCollection.pop();

			/*
			 * ParseTreePathList p = this.currentCollection.pop();
			 * LOGGER.info("Last was {}", p.getType()); p = this.currentCollection.pop();
			 * LOGGER.info("Last was {}", p.getType());
			 * 
			 * p = (ParseTreePathList) this.currentCollection.get(0).get(0);
			 * LOGGER.info("{}", p.getType()); for (ParseTreeElement parseTreeElement : p) {
			 * if (parseTreeElement instanceof ParseTreePathList) { LOGGER.info("{}",
			 * ((ParseTreePathList) parseTreeElement).getType());
			 * 
			 * ParseTreePathList l = (ParseTreePathList) ((ParseTreePathList)
			 * parseTreeElement).get(0); LOGGER.info("l {}", l.getType()); } }
			 */

			this.lastPotentialElsePath.pop();
		} else if (tokenType.equals(this.metaLanguageLexerRules.getListTokenLexerRuleName())) {
			// LIST case
			ParseTreePathList optional = new ParseTreePathList(ListType.OPTIONAL, node.getText(), null);
			this.currentCollection.peek().add(optional);
			this.currentCollection.push(optional);
			ParseTreePathList atomic = new ParseTreePathList(ListType.ATOMIC, node.getText(), null);
			this.currentCollection.peek().add(atomic);
			this.currentCollection.push(atomic);
		} else if (tokenType.equals(this.metaLanguageLexerRules.getListCloseTokenLexerRuleName())) {
			// LIST CLOSE case
			// TODO check if correct
			this.currentCollection.pop();
			this.currentCollection.pop();
		} else {
			boolean isMetaLanguage = tokenType.toLowerCase()
					.startsWith(this.metaLanguageLexerRules.getMetaLanguagePrefix());

			boolean isNonOrderingNode = this.objectLanguageProperties.getNonOrderingNodes()
					.contains(tokenType + "Context");
			this.currentCollection.peek().add(new ParseTreePath(node.getText(), tokenType,
					this.lastParseTreeElement.peek(), isMetaLanguage, isNonOrderingNode));
		}

	}

	@Override
	public void visitErrorNode(ErrorNode node) {
		throw new IllegalStateException(
				"Parse tree contains an error node: " + node.getText() + ", " + node.getSymbol().getText());
	}

	public ParseTree getParseTree() {
		if (this.parseTree == null) {
			this.parseTree = new ParseTree(this.parseTreePaths);
		}

		// LOGGER.info(this.parseTreePaths.toString());
		// printParseTreePathList(this.parseTreePaths);

		return this.parseTree;
	}

	private String getGrammarRuleForTreeNode(ParserRuleContext node) {
		String className = node.getClass().getSimpleName();
		String grammarName = className.replace("Context", "");
		return Character.toLowerCase(grammarName.charAt(0)) + grammarName.substring(1);
	}
}

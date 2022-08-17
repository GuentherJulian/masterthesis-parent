package io.github.guentherjulian.masterthesis.grammargeneration.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.parser.antlr4.ANTLRv4Parser.GrammarSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LexerRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ParserRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4ParserBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStreamRewriter;

/**
 * A class to introduce placeholders of a given meta language into an object
 * language grammar
 */
public class PlaceholderRulesCreator extends ANTLRv4ParserBaseListener {

	private TokenStreamRewriter rewriter;

	private HashSet<String> selectedRules;

	private GrammarSpec grammarSpec;

	private final String phTokenName;

	private List<String> createdLexerRuleList;

	private Set<String> usedPlaceholderRules;

	public PlaceholderRulesCreator(TokenStreamRewriter rewriter, HashSet<String> selectedRules, GrammarSpec grammarSpec,
			List<String> createdLexerRuleList, Set<String> usedPlaceholderRules) {
		this.rewriter = rewriter;
		this.selectedRules = selectedRules;
		this.grammarSpec = grammarSpec;
		phTokenName = grammarSpec.getMetaLangLexerRulePrefix() + grammarSpec.getPlaceHolderLabel();
		this.createdLexerRuleList = createdLexerRuleList;
		this.usedPlaceholderRules = usedPlaceholderRules;
	}

	/**
	 * Adds a typed Placeholder for originalRule
	 *
	 * @param originalRule    The rule to create a type placeholder for
	 * @param placeholderType The name of the original rule
	 */
	private void addTypedPlaceholder(ParserRuleContext originalRule, String placeholderType) {

		String phRuleName = grammarSpec.getMetaLangParserRulePrefix() + placeholderType;
		String optPhRuleName = grammarSpec.getOptPhParserRuleName(placeholderType);
		String starPhRuleName = grammarSpec.getStarPhParserRuleName(placeholderType);
		String plusPhRuleName = grammarSpec.getPlusPhParserRuleName(placeholderType);

		String rulesToBeAdded = "";
		if (usedPlaceholderRules.contains(phRuleName)) {
			rulesToBeAdded += "\n" + phRuleName + ": " + phTokenName + " | "
					+ grammarSpec.createOneRuleContent("(" + phRuleName + " | " + placeholderType + ")") + ";";
		}
		if (usedPlaceholderRules.contains(optPhRuleName)) {
			rulesToBeAdded += "\n" + optPhRuleName + ": " + phTokenName + " | "
					+ grammarSpec.createOptRuleContent("(" + optPhRuleName + " | " + placeholderType + ")?") + ";";
		}
		if (usedPlaceholderRules.contains(starPhRuleName)) {
			rulesToBeAdded += "\n" + starPhRuleName + ": " + phTokenName + " | "
					+ grammarSpec.createStarRuleContent("(" + starPhRuleName + " | " + placeholderType + ")*") + ";";
		}
		if (usedPlaceholderRules.contains(plusPhRuleName)) {
			rulesToBeAdded += "\n" + plusPhRuleName + ": " + phTokenName + " | " + grammarSpec
					.createPlusRuleContent(phTokenName, "(" + plusPhRuleName + " | " + placeholderType + ")*") + ";";
		}

		if (!rulesToBeAdded.isEmpty()) {
			rewriter.insertAfter(originalRule.stop, rulesToBeAdded);
		}
	}

	@Override
	public void exitGrammarSpec(GrammarSpecContext ctx) {

		// insert Placeholder
		for (Map.Entry<String, String> lexerRuleEntry : grammarSpec.getLexerRules().entrySet()) {
			rewriter.insertAfter(ctx.stop, "\n" + lexerRuleEntry.getKey() + ": " + lexerRuleEntry.getValue() + ";");
		}

		// insert implicit tokens
		for (String s : createdLexerRuleList) {
			rewriter.insertAfter(ctx.stop, "\n" + s);
		}

		// rename grammar
		// TODO enable
		// rewriter.replace(ctx.identifier().stop, grammarSpec.getNewGrammarName());
	}

	@Override
	public void exitLexerRuleSpec(LexerRuleSpecContext ctx) {
		String lexerRuleName = ctx.TOKEN_REF().getText();

		if (selectedRules.contains(lexerRuleName)) {
			addTypedPlaceholder(ctx, lexerRuleName);
		}
	}

	@Override
	public void exitParserRuleSpec(ParserRuleSpecContext ctx) {
		String parserRuleName = ctx.RULE_REF().getText();

		if (selectedRules.contains(parserRuleName)) {
			addTypedPlaceholder(ctx, parserRuleName);
		}
	}

	/**
	 * Returns the field 'rewriter'
	 * 
	 * @return value of rewriter
	 */
	public TokenStreamRewriter getRewriter() {
		return rewriter;
	}

}

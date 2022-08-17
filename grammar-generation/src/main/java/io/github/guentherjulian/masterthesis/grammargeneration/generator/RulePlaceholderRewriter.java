package io.github.guentherjulian.masterthesis.grammargeneration.generator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.parser.antlr4.ANTLRv4Parser.AtomContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.EbnfContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.EbnfSuffixContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ElementContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LabeledElementContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ParserRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.RulerefContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.TerminalContext;
import org.antlr.parser.antlr4.ANTLRv4ParserBaseListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * A class to introduce placeholders of a given meta language into an object language grammar
 */
public class RulePlaceholderRewriter extends ANTLRv4ParserBaseListener {

    private TokenStreamRewriter rewriter;

    private Map<String, String> tokenNames;

    private HashSet<String> selectedRules;

    private List<String> multiLexerRules;

    private GrammarSpec grammarSpec;

    private int uniqueNameCounter;

    private List<String> createdLexerRuleList = new LinkedList<>();

    private Set<String> usedPlaceholderRules = new HashSet<>();

    public RulePlaceholderRewriter(CommonTokenStream tokens, Map<String, String> tokenNames,
        HashSet<String> selectedRules, List<String> multiLexerRules, GrammarSpec grammarSpec) {
        rewriter = new TokenStreamRewriter(tokens);
        this.tokenNames = tokenNames;
        this.selectedRules = selectedRules;
        this.multiLexerRules = multiLexerRules;
        this.grammarSpec = grammarSpec;
    }

    /**
     * Extend a reference to rule with the respective placeholder type accordingly to the tactics. Recursive
     * references are ignored in general.
     */
    @Override
    public void exitRuleref(RulerefContext ctx) {
        String referencedRuleName = ctx.getText();

        if (!GrammarUtil.isLeftRecursive(ctx)) {
            if (selectedRules.contains(referencedRuleName)) {
                EbnfSuffixContext ebnfSuffixContext = getElementParent(ctx).ebnfSuffix();
                // ignore chain productions of max occurrence 1
                if (countSiblings(ctx) > 1
                    || (ebnfSuffixContext != null && ebnfSuffixContext.getText().matches("(\\?|\\+)"))) {
                    String ebnfSuffix = ebnfSuffixContext != null ? ebnfSuffixContext.getText() : "";
                    extendRuleRef(ctx, ebnfSuffix);
                }
            }
            // else {
            // // add () to allow condition and loop extension
            // rewriter.insertBefore(ctx.start, "(");
            // rewriter.insertAfter(ctx.stop, ")");
            // }
        }
    }

    private int countSiblings(ParserRuleContext ctx) {
        int elementCount = 0;
        ElementContext elementCtx = getElementParent(ctx);
        for (ParseTree sibling : elementCtx.getParent().children) {
            if (sibling instanceof ElementContext) {
                for (ParseTree elementChild : ((ElementContext) sibling).children) {
                    if (elementChild instanceof AtomContext || elementChild instanceof LabeledElementContext
                        || elementChild instanceof EbnfContext) {
                        elementCount++;
                    }
                }
            }
        }
        return elementCount;
    }

    private ElementContext getElementParent(ParserRuleContext ctx) {
        ParserRuleContext parent = ctx.getParent();
        while (parent != null) {
            if (parent instanceof ElementContext) {
                return (ElementContext) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    @Override
    public void exitTerminal(TerminalContext ctx) {
        // change lexer token to extended rule (add "OrPlaceholder")
        // check for correct rules of manual written words (' ')
        // ignore predefined Token EOF which is without ' '

        String terminal = ctx.getText();
        String tokenName;
        boolean isInParserRule;

        if (terminal.startsWith("'") && terminal.endsWith("'")) {
            // lookup token name (might become null)
            tokenName = tokenNames.get(terminal);

            // check whether terminal occurs in a parser rule
            ParserRuleContext currentParent = ctx.getParent();
            isInParserRule = false;
            while (currentParent != null) {
                if (currentParent instanceof ParserRuleSpecContext) {
                    isInParserRule = true;
                    if (tokenName == null) {
                        System.out
                            .println("Did not find appropriate Tokenname for clean text in a parser rule: " + terminal);
                        uniqueNameCounter++;
                        String newLexerRuleName =
                            grammarSpec.getMetaLangLexerRulePrefix() + "ImplicitToken" + uniqueNameCounter;
                        tokenName = newLexerRuleName;
                        createdLexerRuleList.add(newLexerRuleName + ":" + ctx.getText() + ";");
                    }
                    rewriter.replace(ctx.stop, tokenName + " "); // normalize
                                                                 // grammar

                    break;
                } else {
                    currentParent = currentParent.getParent();
                }
            }

        } else {
            tokenName = terminal;

            // check whether terminal occurs in a parser rule
            ParserRuleContext currentParent = ctx.getParent();
            isInParserRule = false;
            while (currentParent != null) {
                if (currentParent instanceof ParserRuleSpecContext) {
                    isInParserRule = true;
                    break;
                } else {
                    currentParent = currentParent.getParent();
                }
            }
        }

        if (tokenName != null && isInParserRule && selectedRules.contains(tokenName)) {
            if (countSiblings(ctx) > 1) {
                extendTerminal(ctx, tokenName);
            }
        } else if (tokenName != null && isInParserRule && !selectedRules.contains(tokenName)
            && multiLexerRules.contains(tokenName)) {
            // add () to allow condition and loop extension if not fixed token
            rewriter.insertBefore(ctx.start, "(");
            rewriter.insertAfter(ctx.stop, ")");
        }

    }

    private void extendRuleRef(RulerefContext ctx, String cardinality) {
        String ruleName = ctx.getText();
        rewriteRuleRef(ctx, cardinality, ruleName);
    }

    private void rewriteRuleRef(ParserRuleContext ctx, String cardinality, String ruleName) {
        String phRuleName;
        switch (cardinality) {
        case "":
            phRuleName = grammarSpec.getMetaLangParserRulePrefix() + ruleName;

            usedPlaceholderRules.add(phRuleName);
            rewriter.insertBefore(ctx.start, "(" + phRuleName + " | ");
            rewriter.insertAfter(ctx.stop, ")");
            break;
        case "?":
            phRuleName = grammarSpec.getOptPhParserRuleName(ruleName);

            usedPlaceholderRules.add(phRuleName);
            rewriter.insertBefore(ctx.start, "(" + phRuleName + " | ");
            rewriter.insertAfter(ctx.stop, ")");
            break;
        case "*":
            phRuleName = grammarSpec.getStarPhParserRuleName(ruleName);

            usedPlaceholderRules.add(phRuleName);
            rewriter.insertBefore(ctx.start, "(" + phRuleName + " | ");
            rewriter.insertAfter(ctx.stop, ")");
            break;
        case "+":
            phRuleName = grammarSpec.getPlusPhParserRuleName(ruleName);

            usedPlaceholderRules.add(phRuleName);
            rewriter.insertBefore(ctx.start, "(" + phRuleName + " | ");
            rewriter.insertAfter(ctx.stop, ")");
            break;
        default:
            throw new IllegalArgumentException("Unkown EBNF cardinality '" + cardinality + "'");
        }
    }

    private void extendTerminal(TerminalContext ctx, String tokenName) {
        EbnfSuffixContext ebnfSuffixContext = ((ElementContext) ctx.parent.parent).ebnfSuffix();
        String ebnfSuffix = ebnfSuffixContext != null ? ebnfSuffixContext.getText() : "";
        rewriteRuleRef(ctx, ebnfSuffix, tokenName);
    }

    /**
     * Returns the field 'rewriter'
     * @return value of rewriter
     */

    public TokenStreamRewriter getRewriter() {
        return rewriter;
    }

    public Set<String> getUsedPlaceholderRules() {
        return usedPlaceholderRules;
    }

    public List<String> getCreatedLexerRuleList() {
        return createdLexerRuleList;
    }

}

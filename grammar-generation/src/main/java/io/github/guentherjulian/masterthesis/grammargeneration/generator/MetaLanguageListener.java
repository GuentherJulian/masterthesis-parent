package io.github.guentherjulian.masterthesis.grammargeneration.generator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.parser.antlr4.ANTLRv4Parser.AlternativeContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ElementContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LabeledAltContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LexerAltContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LexerElementContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LexerRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ParserRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.TerminalContext;
import org.antlr.parser.antlr4.ANTLRv4ParserBaseListener;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;

/** A class to collect information about a certain meta language */
public class MetaLanguageListener extends ANTLRv4ParserBaseListener {

    /** Token stream of meta language */
    private TokenStream tokenStream;

    /** A capitalized unique prefix usable for the placeholder lexer rule name */
    private String metaLangLexerRulePrefix;

    /** An uncapitalized unique prefix usable for the parser rule names */
    private String metaLangParserRulePrefix;

    /** Mapping from parser production labels to its production */
    private Map<String, String> parserRules = new LinkedHashMap<>();

    /** Mapping from parser production labels to its production */
    private Map<String, String> lexerRules = new LinkedHashMap<>();

    /** ANY Token of meta language */
    private String anyToken;

    /**
     * @param tokenStream
     *            of meta language
     * @param metaLangPrefix
     *            An unique prefix to be used for newly create meta language productions
     * @param anyToken
     *            ANY Token of meta language
     */
    public MetaLanguageListener(TokenStream tokenStream, String metaLangPrefix, String anyToken) {
        this.tokenStream = tokenStream;
        metaLangLexerRulePrefix = metaLangPrefix.toUpperCase();
        metaLangParserRulePrefix = metaLangPrefix.toLowerCase();
        this.anyToken = anyToken;
    }

    @Override
    public void exitLexerRuleSpec(LexerRuleSpecContext ctx) {
        TokenStreamRewriter rewriter = new TokenStreamRewriter(tokenStream);
        rewriteLexerRule(rewriter, ctx.lexerRuleBlock().lexerAltList().lexerAlt());
        if (!ctx.TOKEN_REF().getText().equals(anyToken)) { // skip ANY token copy
            lexerRules.put(metaLangLexerRulePrefix + ctx.TOKEN_REF().getText(), tokenStream
                .getText(new Interval(ctx.lexerRuleBlock().start.getTokenIndex(), ctx.stop.getTokenIndex() - 1)));
        }
    }

    private void rewriteLexerRule(TokenStreamRewriter rewriter, List<LexerAltContext> lexerAlts) {
        for (LexerAltContext alt : lexerAlts) {
            for (LexerElementContext elem : alt.lexerElements().lexerElement()) {
                if (elem.lexerAtom() != null) {
                    TerminalContext terminal = elem.lexerAtom().terminal();
                    if (terminal != null && terminal.TOKEN_REF() != null) {
                        rewriter.replace(terminal.TOKEN_REF().getSymbol(),
                            metaLangParserRulePrefix + terminal.getText());
                    } else if (elem.lexerBlock() != null) {
                        rewriteLexerRule(rewriter, elem.lexerBlock().lexerAltList().lexerAlt());
                    }
                }
            }
        }
    }

    @Override
    public void exitParserRuleSpec(ParserRuleSpecContext ctx) {
        TokenStreamRewriter rewriter = new TokenStreamRewriter(tokenStream);

        List<AlternativeContext> alternatives = new ArrayList<>();
        for (LabeledAltContext labeledAlt : ctx.ruleBlock().ruleAltList().labeledAlt()) {
            alternatives.add(labeledAlt.alternative());
        }
        rewriteParserRule(rewriter, alternatives);

        parserRules.put(metaLangParserRulePrefix + ctx.RULE_REF().getText(),
            rewriter.getText(new Interval(ctx.ruleBlock().start.getTokenIndex(), ctx.stop.getTokenIndex() - 1)));
    }

    private void rewriteParserRule(TokenStreamRewriter rewriter, List<AlternativeContext> parserAlts) {
        for (AlternativeContext alt : parserAlts) {
            for (ElementContext elem : alt.element()) {
                if (elem.atom() != null) {
                    if (elem.atom().ruleref() != null) {
                        TerminalNode ruleRef = elem.atom().ruleref().RULE_REF();
                        rewriter.replace(ruleRef.getSymbol(), metaLangParserRulePrefix + ruleRef.getText());
                    } else if (elem.atom().terminal() != null) {
                        TerminalNode tokenRef = elem.atom().terminal().TOKEN_REF();
                        rewriter.replace(tokenRef.getSymbol(), metaLangLexerRulePrefix + tokenRef.getText());
                    } else if (elem.labeledElement() != null && elem.labeledElement().block() != null) {
                        rewriteParserRule(rewriter, elem.labeledElement().block().altList().alternative());
                    }
                } else if (elem.labeledElement() != null && elem.labeledElement().block() != null) {
                    rewriteParserRule(rewriter, elem.labeledElement().block().altList().alternative());
                } else if (elem.ebnf() != null && elem.ebnf().block() != null) {
                    rewriteParserRule(rewriter, elem.ebnf().block().altList().alternative());
                }
            }
        }
    }

    public Map<String, String> getLexerRules() {
        return lexerRules;
    }

    public Map<String, String> getParserRules() {
        return parserRules;
    }
}

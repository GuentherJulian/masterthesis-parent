package io.github.guentherjulian.masterthesis.grammargeneration.generator;

import java.util.Set;

import org.antlr.parser.antlr4.ANTLRv4Parser.LabeledAltContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.RulerefContext;
import org.antlr.parser.antlr4.ANTLRv4ParserBaseListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

/**
 * A class to introduce placeholders of a given meta language into an object language grammar
 */
public class LeftRecursiveRuleRewriter extends ANTLRv4ParserBaseListener {

    private TokenStreamRewriter rewriter;

    private Set<String> usedPlaceholderRules;

    private GrammarSpec grammarSpec;

    public LeftRecursiveRuleRewriter(CommonTokenStream tokens, Set<String> usedPlaceholderRules,
        GrammarSpec grammarSpec) {
        rewriter = new TokenStreamRewriter(tokens);
        this.grammarSpec = grammarSpec;
        this.usedPlaceholderRules = usedPlaceholderRules;
    }

    /**
     * Extend a reference to rule with the respective placeholder type accordingly to the tactics. Recursive
     * references are ignored in general.
     */
    @Override
    public void exitRuleref(RulerefContext ctx) {
        String referencedRuleName = ctx.getText();

        if (GrammarUtil.isLeftRecursive(ctx)) {
            LabeledAltContext parent = getParent(ctx, LabeledAltContext.class);
            String phRuleName = grammarSpec.getMetaLangParserRulePrefix() + referencedRuleName;
            // replace first as we will just take left recursive occurrences into account
            rewriter.insertBefore(parent.start,
                toString(parent).replaceFirst(referencedRuleName, phRuleName) + "\n  | ");
            usedPlaceholderRules.add(phRuleName);
        }
    }

    public String toString(ParseTree ctx) {
        if (ctx instanceof TerminalNodeImpl) {
            return ctx.getText() + " ";
        }

        if (ctx.getChildCount() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            builder.append(toString(ctx.getChild(i)));
        }

        return builder.toString();
    }

    private <T> T getParent(ParserRuleContext ctx, Class<T> parentClass) {
        if (parentClass.isAssignableFrom(ctx.getClass())) {
            return (T) ctx;
        } else {
            return getParent(ctx.getParent(), parentClass);
        }
    }

    /**
     * Returns the field 'rewriter'
     * @return value of rewriter
     */

    public TokenStreamRewriter getRewriter() {
        return rewriter;
    }

}

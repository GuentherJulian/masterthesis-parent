package io.github.guentherjulian.masterthesis.grammargeneration.generator;

import org.antlr.parser.antlr4.ANTLRv4Parser.AlternativeContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ElementOptionsContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ParserRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.RulerefContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

/** Util for grammar analysis. */
public class GrammarUtil {

    public static boolean isLeftRecursive(RulerefContext ctx) {
        String ruleName = ctx.getText();
        ParserRuleContext currentParent = ctx;
        boolean isRecursive = false;
        do {
            currentParent = currentParent.getParent();
            if (currentParent.getChildCount() > 0 && currentParent instanceof AlternativeContext) {
                for (ParseTree child : currentParent.children) {
                    if (child instanceof ParserRuleContext && !(child instanceof ElementOptionsContext)) {
                        if (!child.getText().equals(ruleName)
                            // differentiate multiple recursive occurrences
                            || (child.getText().equals(ruleName) && ((ParserRuleContext) child).start != ctx.start)) {
                            return false;
                        } else {
                            break;
                        }
                    }
                }
            }

            if (currentParent instanceof ParserRuleSpecContext) {
                ParserRuleSpecContext parentRule = (ParserRuleSpecContext) currentParent;
                if (ruleName.equals(parentRule.RULE_REF().getText())) {
                    isRecursive = true;
                }
                break;
            }
        } while (currentParent != null);
        return isRecursive;
    }

    public static boolean isRecursive(RulerefContext ctx) {
        String ruleName = ctx.getText();
        ParserRuleContext currentParent = ctx.getParent();
        boolean isRecursive = false;
        while (currentParent != null) {
            if (currentParent instanceof ParserRuleSpecContext) {
                ParserRuleSpecContext parentRule = (ParserRuleSpecContext) currentParent;
                if (ruleName.equals(parentRule.RULE_REF().getText())) {
                    isRecursive = true;
                }
                break;
            } else {
                currentParent = currentParent.getParent();
            }
        }
        return isRecursive;
    }
}

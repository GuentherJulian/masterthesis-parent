package io.github.guentherjulian.masterthesis.grammargeneration.parser;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 */
public class ListPatternTransformer implements ParseTreeListener {

    private Map<String, String> listPatternRules = new HashMap<>();

    private Map<String, String> listPatternMetaRules = new HashMap<>();

    public ListPatternTransformer(Map<String, String> listPatternRules) {
        for (Entry<String, String> entry : listPatternRules.entrySet()) {
            String objectRuleClassName =
                Character.toUpperCase(entry.getKey().charAt(0)) + entry.getKey().substring(1) + "Context";
            this.listPatternRules.put(objectRuleClassName, "Fm_" + entry.getValue() + "Context");
            listPatternMetaRules.put(objectRuleClassName, "Fm_" + entry.getKey() + "Context");
        }
    }

    public void transform(ParseTree tree) {
        System.out.println("start list pattern transformation");
        long start = System.currentTimeMillis();
        new ParseTreeWalker().walk(this, tree);
        long stop = System.currentTimeMillis();
        System.out.println("list pattern transformation finished in " + (stop - start) + "ms");
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        String elementName = ctx.getClass().getSimpleName();
        if (listPatternRules.values().contains(elementName)) {
            ParserRuleContext parent = ctx.getParent();
            String listRuleClassName = parent.getClass().getSimpleName();
            if (listPatternRules.get(listRuleClassName) != null
                && listPatternRules.get(listRuleClassName).equals(elementName)) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (parent.getChild(i) == ctx) {

                        String phClassName = parent.getClass().getName().replace(listRuleClassName,
                            listPatternMetaRules.get(listRuleClassName));

                        try {
                            Class<?> phClass = ctx.getClass().getClassLoader().loadClass(phClassName);

                            Constructor<?> constructor = phClass.getConstructor(ParserRuleContext.class, int.class);
                            ParserRuleContext childReplacement =
                                (ParserRuleContext) constructor.newInstance(parent, ctx.invokingState);
                            childReplacement.children = ctx.children;
                            childReplacement.start = ctx.start;
                            childReplacement.stop = ctx.stop;

                            parent.children.set(i, childReplacement);
                            for (ParseTree child : childReplacement.children) {
                                child.setParent(childReplacement);
                            }
                            System.out.println(
                                "Transformed " + ctx.getClass().getSimpleName() + " to " + phClass.getSimpleName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void visitTerminal(TerminalNode node) {
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
    }

}

package io.github.guentherjulian.masterthesis.grammargeneration.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.parser.antlr4.ANTLRv4Parser.AlternativeContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.EbnfSuffixContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.GrammarSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LexerAltListContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LexerAtomContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LexerRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ParserRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.RulerefContext;
import org.antlr.parser.antlr4.ANTLRv4ParserBaseListener;
import org.antlr.v4.runtime.tree.ParseTree;

/** A class to collect information about a certain object language */
public class ObjectLanguageListener extends ANTLRv4ParserBaseListener {

    private List<String> fragmentRules;

    private Map<String, String> tokenNames;

    private List<String> lexerRules;

    private Tactics tactic;

    private HashSet<String> selectedRules;

    private HashSet<String> sequenceParserRules;

    private HashMap<String, ArrayList<String>> maybeSequenceParserRules;

    public ObjectLanguageListener(Tactics tactic) {
        this.tactic = tactic;
        fragmentRules = new LinkedList<>();
        tokenNames = new HashMap<>();
        lexerRules = new LinkedList<>();
        selectedRules = new HashSet<>();
        sequenceParserRules = new HashSet<>();
        maybeSequenceParserRules = new HashMap<>();
    }

    private void analyzeMaybeSequenceParserRules() {
        boolean changeHappend = true;
        while (changeHappend) {
            changeHappend = false;
            Set<String> keySet = new HashSet<>(maybeSequenceParserRules.keySet());

            for (String key : keySet) {

                boolean hasSequence = false;
                for (String referencedRule : maybeSequenceParserRules.get(key)) {
                    if (sequenceParserRules.contains(referencedRule)) {
                        sequenceParserRules.add(key);
                        maybeSequenceParserRules.remove(key);
                        changeHappend = true;
                        hasSequence = true;
                        break;
                    }
                }

                if (!hasSequence) {
                    // if this point is reached it is likely that all referenced rules are without sequences,
                    // if so, select this rule
                    boolean selectNow = true;
                    for (String referencedRule : maybeSequenceParserRules.get(key)) {
                        if (!selectedRules.contains(referencedRule)) {
                            selectNow = false;
                            break;
                        }
                    }
                    if (selectNow) {
                        selectedRules.add(key);
                        maybeSequenceParserRules.remove(key);
                        changeHappend = true;
                    }
                }
            }
        }
        if (!maybeSequenceParserRules.isEmpty()) {
            System.out.println(
                "Could not find out whether the following parser rules contain sequences: " + maybeSequenceParserRules);
        }
    }

    private boolean containsSequence(ParseTree ctx) {

        if (ctx instanceof AlternativeContext) {
            AlternativeContext alternative = (AlternativeContext) ctx;
            if (alternative.getChildCount() > 1) {
                return true;
            }
        } else {
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                if (containsSequence(child)) {
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public void exitGrammarSpec(GrammarSpecContext ctx) {
        analyzeMaybeSequenceParserRules();
    }

    @Override
    public void exitLexerRuleSpec(LexerRuleSpecContext ctx) {
        String lexerRuleName = ctx.TOKEN_REF().getText();

        // collect fragment rule names
        if (!(ctx.FRAGMENT() == null)) {
            fragmentRules.add(lexerRuleName);
        } else {

            // List<ParseTree> children = ctx.children;
            // boolean isMultiple = isMultiple(ctx);

            // collect token names with only one possible text
            // look at all children recursively and check for | * ? + range []
            if (!isAtomToken(ctx)) {
                tokenNames.put(ctx.lexerRuleBlock().lexerAltList().getText(), lexerRuleName);
            } else { // collect token names with multiple possible texts
                lexerRules.add(ctx.TOKEN_REF().getText());
            }

            switch (tactic) {
            case ALL:
                // add all rules, including lexer rules
                selectedRules.add(lexerRuleName);
                break;
            case CUSTOM:
                // add only custom selected rules
                if (tactic.containsToken(lexerRuleName)) {
                    selectedRules.add(lexerRuleName);
                }
                break;
            case INTELLIGENT:
                // add only lexer rules which are variable (e.g. identifier in java)
                if (lexerRules.contains(lexerRuleName)) {
                    selectedRules.add(lexerRuleName);
                }
                break;
            case ONLYPARSER:
                // nothing to add here, ctx is a lexer rule at this point
                break;
            case ONLYLEXER:
                selectedRules.add(lexerRuleName);
                break;
            case ALL_PARSER_CUSTOM_LEXER:
                if (tactic.containsToken(lexerRuleName)) {
                    selectedRules.add(lexerRuleName);
                }
                break;
            }
        }

    }

    @Override
    public void exitParserRuleSpec(ParserRuleSpecContext ctx) {
        String ruleName = ctx.RULE_REF().getText();
        switch (tactic) {
        case ALL:
            selectedRules.add(ruleName);
            break;
        case CUSTOM:
            if (tactic.containsToken(ruleName)) {
                selectedRules.add(ruleName);
            }
            break;
        case INTELLIGENT:
            if (containsSequence(ctx)) {
                // rules that certainly contain a sequence are not selected but stored for further analysis
                sequenceParserRules.add(ruleName);
            } else {
                // rules that do not contain a sequence but contain a parser rule that might contain a
                // sequence
                ArrayList<String> referencedRules = findReferencedParserRuleNames(ctx);
                if (referencedRules.size() == 0) {
                    // only tokens referenced
                    selectedRules.add(ruleName);
                } else {
                    // it cannot be decided whether all referenced rules are without sequence at this point.
                    // All uncertain rules are stored and analyzed at the end (see exitGrammarSpec)
                    maybeSequenceParserRules.put(ruleName, referencedRules);
                }

            }
            break;
        case ONLYPARSER:
        case ALL_PARSER_CUSTOM_LEXER:
            selectedRules.add(ruleName);
            break;
        case ONLYLEXER:
            // nothing to add here, ctx is a parser rule at this point
            break;
        }
    }

    private ArrayList<String> findReferencedParserRuleNames(ParseTree ctx) {
        ArrayList<String> referencedRules = new ArrayList<>();

        if (ctx instanceof RulerefContext) {
            referencedRules.add(ctx.getText());
        } else {
            for (int i = 0; i < ctx.getChildCount(); i++) {
                referencedRules.addAll(findReferencedParserRuleNames(ctx.getChild(i)));
            }
        }
        return referencedRules;
    }

    /**
     * Returns the field 'fragmentRules'
     * @return value of fragmentRules
     */
    public List<String> getFragmentRules() {
        return fragmentRules;
    }

    /**
     * Returns the field 'multiLexerRules'
     * @return value of multiLexerRules
     */
    public List<String> getMultiLexerRules() {
        return lexerRules;
    }

    /**
     * Returns the field 'selectedRules'
     * @return value of selectedRules
     */
    public HashSet<String> getSelectedRules() {
        return selectedRules;
    }

    /**
     * Returns the field 'tokenNames'
     * @return value of tokenNames
     */
    public Map<String, String> getTokenNames() {
        return tokenNames;
    }

    private boolean isAtomToken(ParseTree tree) {

        List<ParseTree> children = new LinkedList<>();
        int childCount = tree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            children.add(tree.getChild(i));
        }

        for (ParseTree child : children) {
            // System.out.println(child.getText() + child.getClass());
            if (child instanceof LexerAltListContext && child.getChildCount() > 1) {
                // System.out.println("is or");
                return true;
            }
            if (child instanceof EbnfSuffixContext) {
                // System.out.println("is EBNF");
                return true;
            }
            // if (child instanceof RangeContext) {
            // System.out.println("range");
            // return true;
            // }
            if (child instanceof LexerAtomContext && child.getText().startsWith("[") && child.getText().endsWith("]")) {
                // System.out.println("set []");
                return true;
            }
            if (isAtomToken(child)) {
                // System.out.println("wg Child");
                return true;
            }
        }

        // if nothing special was detected return false
        // System.out.println("is false");
        return false;
    }

}

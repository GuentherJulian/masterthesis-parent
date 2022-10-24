package io.github.guentherjulian.masterthesis.grammargeneration.generator;

import java.util.Map;

/** A container class for information exchange during template transformation */
public class GrammarSpec {

    private final String anyTokenName;

    private final String ifRuleName = "ifThen";

    private final String ifElseRuleName = "ifElse";

    private final String loopRuleName = "loop";

    private final String loopElseName = "loopElse";

    /** The name for the new grammar */
    private String newGrammarName;

    /** A capitalized unique prefix usable for the placeholder lexer rule name */
    private String metaLangLexerRulePrefix;

    /** An uncapitalized unique prefix usable for the parser rule names */
    private String metaLangParserRulePrefix;

    /** The name for the placeholder in the template grammar */
    private String placeHolderLabel;

    private String ruleBodyPlaceholder;

    /** Grammar rules of the meta language. */
    private Map<String, String> metaLangParserRules;

    /** Grammar rules of the meta language. */
    private Map<String, String> metaLangLexerRules;

    /**
     * @param newGrammarName
     *                            The name for the new grammar
     * @param metaLangPrefix
     *                            An unique prefix to be used for newly create meta
     *                            language productions
     * @param placeHolderLabel
     *                            The name for the placeholder in the template
     *                            grammar
     * @param metaLangParserRules
     *                            Grammar rules of the meta language
     * @param metaLangLexerRules
     *                            Grammar rules of the meta language
     * @param anyTokenName
     *                            ANY Token name
     */
    public GrammarSpec(String newGrammarName, String metaLangPrefix, String placeHolderLabel,
            Map<String, String> metaLangParserRules, Map<String, String> metaLangLexerRules, String anyTokenName) {
        this.newGrammarName = newGrammarName;
        metaLangLexerRulePrefix = metaLangPrefix.toUpperCase();
        metaLangParserRulePrefix = metaLangPrefix.toLowerCase();
        this.placeHolderLabel = placeHolderLabel;
        this.metaLangParserRules = metaLangParserRules;
        this.metaLangLexerRules = metaLangLexerRules;
        this.anyTokenName = anyTokenName;
        ruleBodyPlaceholder = metaLangLexerRulePrefix + anyTokenName;
    }

    /**
     * Returns the field 'newGrammarName'
     * 
     * @return value of newGrammarName
     */
    public String getNewGrammarName() {
        return newGrammarName;
    }

    /**
     * Returns the field 'metaLangPlaceholderPrefix'
     * 
     * @return value of metaLangPlaceholderPrefix
     */
    public String getMetaLangLexerRulePrefix() {
        return metaLangLexerRulePrefix;
    }

    /**
     * Returns the field 'metaLangRulePrefix'
     * 
     * @return value of metaLangRulePrefix
     */
    public String getMetaLangParserRulePrefix() {
        return metaLangParserRulePrefix;
    }

    /**
     * Returns the field 'placeHolderSuffix'
     * 
     * @return value of placeHolderSuffix
     */
    public String getPlaceHolderLabel() {
        return placeHolderLabel;
    }

    public String getOptPhParserRuleName(String atomName) {
        return metaLangParserRulePrefix + atomName + "Opt";
    }

    public String getStarPhParserRuleName(String atomName) {
        return metaLangParserRulePrefix + atomName + "Star";
    }

    public String getPlusPhParserRuleName(String atomName) {
        return metaLangParserRulePrefix + atomName + "Plus";
    }

    public String getParserRule(String label) {
        return metaLangParserRules.get(label);
    }

    public Map<String, String> getLexerRules() {
        return metaLangLexerRules;
    }

    public String getPlaceHolderRule() {
        return metaLangLexerRules.get(placeHolderLabel);
    }

    public String createPlusRuleContent(String phTokenName, String body) {
        String bodyStar = body.substring(0, body.length() - 1) + "*";

        return "(" + getParserRule(metaLangParserRulePrefix + ifRuleName).replace(ruleBodyPlaceholder, bodyStar) + " | "
                + getParserRule(metaLangParserRulePrefix + loopRuleName).replace(ruleBodyPlaceholder, bodyStar) + ")* "

                + "(" + phTokenName + " | " + createOneRuleContent(body) + " | "
                + getParserRule(metaLangParserRulePrefix + loopRuleName).replace(ruleBodyPlaceholder, body) + ") " +

                "(" + getParserRule(metaLangParserRulePrefix + ifRuleName).replace(ruleBodyPlaceholder, bodyStar)
                + " | "
                + getParserRule(metaLangParserRulePrefix + loopRuleName).replace(ruleBodyPlaceholder, bodyStar) + ")*";
    }

    public String createStarRuleContent(String body) {
        return getParserRule(metaLangParserRulePrefix + ifRuleName).replace(ruleBodyPlaceholder, body) + " | "
                + getParserRule(metaLangParserRulePrefix + loopRuleName).replace(ruleBodyPlaceholder, body);
    }

    public String createOptRuleContent(String body) {
        return getParserRule(metaLangParserRulePrefix + ifRuleName).replace(ruleBodyPlaceholder, body);
    }

    public String createOneRuleContent(String body) {
        return getParserRule(metaLangParserRulePrefix + ifElseRuleName).replace(ruleBodyPlaceholder, body);
    }
}

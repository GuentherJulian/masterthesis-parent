package io.github.guentherjulian.masterthesis.antlr4.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.antlr.parser.antlr4.ANTLRv4Lexer;
import org.antlr.parser.antlr4.ANTLRv4Parser;
import org.antlr.parser.antlr4.ANTLRv4Parser.AltListContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.AlternativeContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.AtomContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.BlockContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.EbnfContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ElementContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LabeledAltContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LexerRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ParserRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.RuleAltListContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.RuleBlockContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.RulerefContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.TerminalContext;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Due to the fact, that we do want to remove as much ambiguities as possible in
 * the grammar, we have to transform list elements later on to provide a more
 * generic label. Take this example as a production of the list pattern to be
 * handled by the {@link ListPatternCollector}:
 * <p>
 * {@code AnyList: elem | fm_AnyList 'sep' (elem | fm_elem) | AnyList 'sep' (elem | fm_elem)}
 * </p>
 * If placeholders are parsed as elements of this list, we will get ph_elem
 * placeholders. However, due to common use cases like in the definition of
 * packages, we want to support to potentially insert more than one element into
 * the placeholder ph_elem, i.e., 'a.b.c'. Validating this against the elem
 * production or token, which in case of a package definition is an identifier,
 * will lead to an invalid input evaluation as identifier must not contain a
 * dot. So in case of a list pattern, we want to rewrite the CST to replace
 * ph_elem by ph_AnyList to support arbitrary many list elements to be
 * substituted by one placeholder. This has to be done in a post-processing step
 * as the parser cannot handle this sufficiently without introducing new
 * ambiguities introducing new trees in our algorithm.
 */
// TODO implement extension of implementation of
// https://github.com/antlr/antlr4/blob/master/doc/tree-matching.md
public class ListPatternCollector implements ParseTreeListener {

	/**
	 * Found list pattern occurrences mapping list element and separator to list
	 * parent rule name
	 */
	private MultiMap<String, String> listPatternRules;

	private String grammar;

	public ListPatternCollector(String grammar) {
		this.grammar = grammar;
	}

	public ListPatternCollector(Path grammar) throws IOException {
		try (InputStream grammarStream = Files.newInputStream(grammar);
				InputStreamReader inputStreamReader = new InputStreamReader(grammarStream);
				BufferedReader buffer = new BufferedReader(inputStreamReader)) {
			this.grammar = buffer.lines().collect(Collectors.joining("\n"));
		}
	}

	public MultiMap<String, String> detectListPatternInstantiations() throws IOException {
		try (StringReader reader = new StringReader(grammar)) {
			ANTLRv4Lexer lexer = new ANTLRv4Lexer(CharStreams.fromString(grammar));
			CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			ANTLRv4Parser parser = new ANTLRv4Parser(tokenStream);
			ParseTree parseTree = parser.grammarSpec();
			listPatternRules = new MultiMap<>();
			new ParseTreeWalker().walk(this, parseTree);
			return listPatternRules;
		}
	}

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
		if (ctx instanceof ParserRuleSpecContext || ctx instanceof LexerRuleSpecContext) {
			ParseTree currChild;
			String elementName = null;
			String ruleName = null;

			if (ctx.getChildCount() == 5) {
				currChild = ctx.getChild(2);
				ruleName = ctx.getChild(0).getText();
				if (currChild instanceof RuleBlockContext && currChild.getChildCount() == 1) {
					currChild = currChild.getChild(0);
					if (currChild instanceof RuleAltListContext && currChild.getChildCount() == 5) {
						ParseTree alt = currChild;
						currChild = alt.getChild(0);
						if (currChild instanceof LabeledAltContext && currChild.getChildCount() == 1) {
							currChild = currChild.getChild(0);
							if (currChild instanceof AlternativeContext && currChild.getChildCount() == 1) {
								currChild = currChild.getChild(0);
								if (!representsTerminal(currChild)) {
									return;
								}
								elementName = currChild.getText();
							}
						}

						currChild = alt.getChild(2);
						if (checkListAlternatives(currChild, ruleName, elementName)) {
							currChild = alt.getChild(4);
							if (checkListAlternatives(currChild, ruleName, elementName)) {
								listPatternRules.map(elementName, ruleName);
								// add separator
								listPatternRules.map(currChild.getChild(0).getChild(1).getText(), ruleName);
								// add placeholder element
								listPatternRules.map(currChild.getChild(0).getChild(2).getChild(0).getChild(0)
										.getChild(1).getChild(0).getText(), ruleName);
								// add rulename placeholder
								listPatternRules.map(alt.getChild(2).getChild(0).getChild(0).getText(), ruleName);
							}
						}
					}
				}
			}
		}
	}

	private boolean checkListAlternatives(ParseTree currChild, String ruleName, String elementName) {
		if (currChild instanceof LabeledAltContext && currChild.getChildCount() == 1) {
			currChild = currChild.getChild(0);
			if (currChild instanceof AlternativeContext && currChild.getChildCount() == 3) {
				ParseTree alt2 = currChild;
				currChild = alt2.getChild(0);
				if (!representsTerminal(currChild) || !currChild.getText().endsWith(ruleName)) {
					return false;
				}

				// separator, just existence is of interest
				if (!representsTerminal(alt2.getChild(1))) {
					return false;
				}

				currChild = alt2.getChild(2);
				if (currChild instanceof ElementContext && currChild.getChildCount() == 1) {
					currChild = currChild.getChild(0);
					if (currChild instanceof EbnfContext && currChild.getChildCount() == 1) {
						currChild = currChild.getChild(0);
						if (currChild instanceof BlockContext && currChild.getChildCount() == 3) {
							currChild = currChild.getChild(1); // bracket children around
							if (currChild instanceof AltListContext && currChild.getChildCount() == 3) {
								ParseTree alt3 = currChild;
								currChild = alt3.getChild(0); // first alt = list element placeholder
								if (!representsTerminal(currChild) || !currChild.getText().endsWith(elementName)) {
									return false;
								}

								currChild = alt3.getChild(2); // second alt = list element
								if (!representsTerminal(currChild) || !currChild.getText().equals(elementName)) {
									return false;
								}
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private static boolean representsTerminal(ParseTree root) {
		ParseTree currChild = root;
		if (currChild instanceof AlternativeContext && currChild.getChildCount() == 1) {
			currChild = currChild.getChild(0);
		}
		if (currChild instanceof ElementContext && currChild.getChildCount() == 1) {
			currChild = currChild.getChild(0);
			if (currChild instanceof AtomContext && currChild.getChildCount() == 1) {
				currChild = currChild.getChild(0);
				if (currChild instanceof TerminalContext && currChild.getChildCount() == 1
						|| currChild instanceof RulerefContext && currChild.getChildCount() == 1) {
					return true;
				}
			}
		}
		return false;
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

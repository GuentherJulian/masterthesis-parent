package io.github.guentherjulian.masterthesis.grammargeneration.generator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.antlr.parser.antlr4.ANTLRv4Lexer;
import org.antlr.parser.antlr4.ANTLRv4Parser;
import org.antlr.parser.antlr4.ANTLRv4Parser.GrammarSpecContext;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import freemarker.template.TemplateException;

/**
 * The core of the template transformation tool.
 */
public class GrammarExtenderCore {

	public static Path extendGrammar(Path objectGrammarPath, Path destinationPath, Tactics extensionTactic,
			Path metalanguageGrammarPath, String newGrammarName, String metaLangPrefix, String placeHolderName,
			String targetPackage, String anyTokenName) throws IOException, TemplateException {

		// parse metalanguage
		ANTLRv4Lexer metalanguageGrammarLexer = getLexer(metalanguageGrammarPath);
		CommonTokenStream metaTokens = new CommonTokenStream(metalanguageGrammarLexer);
		ANTLRv4Parser metaParser = new ANTLRv4Parser(metaTokens);
		GrammarSpecContext metaTree = metaParser.grammarSpec();

		// create a standard ANTLR parse tree walker
		ParseTreeWalker metaWalker = new ParseTreeWalker();

		// collect information about meta grammar
		MetaLanguageListener metaCollector = new MetaLanguageListener(metaTokens, metaLangPrefix, anyTokenName);
		metaWalker.walk(metaCollector, metaTree); // walk parse tree

		// create grammarspec using metagrammar infos
		GrammarSpec grammarSpec = new GrammarSpec(newGrammarName, metaLangPrefix, placeHolderName,
				metaCollector.getParserRules(), metaCollector.getLexerRules(), anyTokenName);

		// parse object Language
		ANTLRv4Lexer objectLexer = getLexer(objectGrammarPath);
		CommonTokenStream objectTokens = new CommonTokenStream(objectLexer);
		ANTLRv4Parser objectParser = new ANTLRv4Parser(objectTokens);
		GrammarSpecContext objectTree = objectParser.grammarSpec();

		ParseTreeWalker objectWalker = new ParseTreeWalker();

		// collect information about object grammar
		ObjectLanguageListener objectCollector = new ObjectLanguageListener(extensionTactic);
		objectWalker.walk(objectCollector, objectTree); // walk parse tree
		List<String> multiLexerRules = objectCollector.getMultiLexerRules();
		Map<String, String> tokenNames = objectCollector.getTokenNames();
		HashSet<String> selectedRules = objectCollector.getSelectedRules();

		// extend rules
		RulePlaceholderRewriter ruleRewriter = new RulePlaceholderRewriter(objectTokens, tokenNames, selectedRules,
				multiLexerRules, grammarSpec);
		objectWalker.walk(ruleRewriter, objectTree); // walk parse tree
		String transformedParserGrammar = ruleRewriter.getRewriter().getText();

		// write down to detect left recursions and unfold them if they are starting
		// with an alt-block
		Path newGrammarPath = destinationPath.resolve(grammarSpec.getNewGrammarName() + ".g4");
		printToFile(newGrammarPath, transformedParserGrammar);

		InputStream targetStream = new ByteArrayInputStream(transformedParserGrammar.getBytes());
		ANTLRv4Lexer objectLanguageLexer = getLexer(targetStream);
		CommonTokenStream tokenStream = new CommonTokenStream(objectLanguageLexer);
		ANTLRv4Parser objectLanguageParser = new ANTLRv4Parser(tokenStream);
		objectTree = objectLanguageParser.grammarSpec();
		LeftRecursiveRuleRewriter recRuleRewriter = new LeftRecursiveRuleRewriter(tokenStream,
				ruleRewriter.getUsedPlaceholderRules(), grammarSpec);
		objectWalker.walk(recRuleRewriter, objectTree); // walk parse tree

		PlaceholderRulesCreator rulesCreator = new PlaceholderRulesCreator(recRuleRewriter.getRewriter(), selectedRules,
				grammarSpec, ruleRewriter.getCreatedLexerRuleList(), ruleRewriter.getUsedPlaceholderRules());
		objectWalker.walk(rulesCreator, objectTree); // walk parse tree

		// print manipulated grammar to file
		newGrammarPath = destinationPath.resolve(grammarSpec.getNewGrammarName() + ".g4");
		printToFile(newGrammarPath, rulesCreator.getRewriter().getText());

		return new File(newGrammarPath.toString()).getAbsoluteFile().toPath();
	}

	public static Path extendGrammar(Path objectGrammarPathLexer, Path objectGrammarPathParser, Path destinationPath,
			Tactics extensionTactic, Path metalanguageGrammarPath, String newGrammarName, String metaLangPrefix,
			String placeHolderName, String targetPackage, String anyTokenName) throws IOException, TemplateException {

		// parse metalanguage
		ANTLRv4Lexer metalanguageGrammarLexer = getLexer(metalanguageGrammarPath);
		CommonTokenStream metaTokens = new CommonTokenStream(metalanguageGrammarLexer);
		ANTLRv4Parser metaParser = new ANTLRv4Parser(metaTokens);
		GrammarSpecContext metaTree = metaParser.grammarSpec();

		// create a standard ANTLR parse tree walker
		ParseTreeWalker metaWalker = new ParseTreeWalker();

		// collect information about meta grammar
		MetaLanguageListener metaCollector = new MetaLanguageListener(metaTokens, metaLangPrefix, anyTokenName);
		metaWalker.walk(metaCollector, metaTree); // walk parse tree

		// create grammarspec using metagrammar infos
		GrammarSpec grammarSpec = new GrammarSpec(newGrammarName, metaLangPrefix, placeHolderName,
				metaCollector.getParserRules(), metaCollector.getLexerRules(), anyTokenName);

		// =================
		// =================
		// parse object language lexer
		ANTLRv4Lexer objectLanguageLexerLexer = getLexer(objectGrammarPathLexer);
		CommonTokenStream objectlanguageTokensLexer = new CommonTokenStream(objectLanguageLexerLexer);
		ANTLRv4Parser objectParser = new ANTLRv4Parser(objectlanguageTokensLexer);
		GrammarSpecContext objectTreeLexer = objectParser.grammarSpec();

		ParseTreeWalker objectWalker = new ParseTreeWalker();

		// collect information about object grammar lexer
		ObjectLanguageListener objectCollector = new ObjectLanguageListener(extensionTactic);
		objectWalker.walk(objectCollector, objectTreeLexer); // walk parse tree
		List<String> multiLexerRules = objectCollector.getMultiLexerRules();
		Map<String, String> tokenNames = objectCollector.getTokenNames();
		HashSet<String> selectedRules = objectCollector.getSelectedRules();

		// extend rules
		RulePlaceholderRewriter ruleRewriter = new RulePlaceholderRewriter(objectlanguageTokensLexer, tokenNames,
				selectedRules, multiLexerRules, grammarSpec);
		objectWalker.walk(ruleRewriter, objectTreeLexer); // walk parse tree
		String transformedParserGrammar = ruleRewriter.getRewriter().getText();

		// write down to detect left recursions and unfold them if they are starting
		// with an alt-block
		Path newGrammarPath = destinationPath.resolve(grammarSpec.getNewGrammarName() + ".g4");
		printToFile(newGrammarPath, transformedParserGrammar);

		InputStream targetStream = new ByteArrayInputStream(transformedParserGrammar.getBytes());
		ANTLRv4Lexer objectLanguageLexer = getLexer(targetStream);
		CommonTokenStream tokenStream = new CommonTokenStream(objectLanguageLexer);
		ANTLRv4Parser objectLanguageParser = new ANTLRv4Parser(tokenStream);
		objectTreeLexer = objectLanguageParser.grammarSpec();
		LeftRecursiveRuleRewriter recRuleRewriter = new LeftRecursiveRuleRewriter(tokenStream,
				ruleRewriter.getUsedPlaceholderRules(), grammarSpec);
		objectWalker.walk(recRuleRewriter, objectTreeLexer); // walk parse tree

		PlaceholderRulesCreator rulesCreator = new PlaceholderRulesCreator(recRuleRewriter.getRewriter(), selectedRules,
				grammarSpec, ruleRewriter.getCreatedLexerRuleList(), ruleRewriter.getUsedPlaceholderRules());
		objectWalker.walk(rulesCreator, objectTreeLexer); // walk parse tree

		// print manipulated grammar to file
		newGrammarPath = destinationPath.resolve(grammarSpec.getNewGrammarName() + ".g4");
		printToFile(newGrammarPath, rulesCreator.getRewriter().getText());

		return new File(newGrammarPath.toString()).getAbsoluteFile().toPath();
	}

	private static ANTLRv4Lexer getLexer(Path languageGrammarPath) throws IOException {
		InputStream languageGrammarInputStream = new FileInputStream(languageGrammarPath.toFile());
		return getLexer(languageGrammarInputStream);
	}

	private static ANTLRv4Lexer getLexer(InputStream languageGrammarInputStream) throws IOException {
		CharStream languageGrammarCharStream = CharStreams.fromStream(languageGrammarInputStream);
		ANTLRv4Lexer lexer = new ANTLRv4Lexer(languageGrammarCharStream);
		return lexer;
	}

	private static void printToFile(Path filePath, String data) throws IOException {
		Writer fw = null;
		try {
			File file = new File(filePath.toString());
			file.getParentFile().mkdirs();
			fw = new FileWriter(file);
			fw.write(data);
		} catch (IOException e) {
			throw new IOException("Unable to create new grammar in specified path", e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

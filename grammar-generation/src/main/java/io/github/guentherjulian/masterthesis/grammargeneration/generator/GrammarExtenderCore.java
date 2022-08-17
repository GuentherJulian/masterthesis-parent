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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * The core of the template transformation tool.
 */
public class GrammarExtenderCore {

	public static Path extendGrammarAndGenerateParser(Path objectGrammarPath, Path destinationPath,
			Tactics extensionTactic, Path metalanguageGrammarPath, String newGrammarName, String metaLangPrefix,
			String placeHolderName, String targetPackage, String anyTokenName) throws IOException, TemplateException {

		// parse metalanguage
		InputStream metalanguageGrammarInputStream = new FileInputStream(metalanguageGrammarPath.toFile());
		CharStream metalanguageCharStream = CharStreams.fromStream(metalanguageGrammarInputStream);
		ANTLRv4Lexer metalanguageGrammarLexer = new ANTLRv4Lexer(metalanguageCharStream);
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
		InputStream objectLanguageGrammarInputStream = new FileInputStream(objectGrammarPath.toFile());
		CharStream objectLanguageCharStream = CharStreams.fromStream(objectLanguageGrammarInputStream);
		ANTLRv4Lexer objectLexer = new ANTLRv4Lexer(objectLanguageCharStream);
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
		CharStream c = CharStreams.fromStream(targetStream);
		objectLexer = new ANTLRv4Lexer(c);
		CommonTokenStream tokenStream = new CommonTokenStream(objectLexer);
		objectParser = new ANTLRv4Parser(tokenStream);
		objectTree = objectParser.grammarSpec();
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

	/**
	 * @param destinationPath
	 * @param metaLangPrefix
	 * @param placeHolderName
	 * @param targetPackage
	 * @param grammarSpec
	 * @param destinationFilePath
	 * @throws IOException
	 */
	/*
	 * private static void generateParser(String destinationPath, String
	 * metaLangPrefix, String placeHolderName, String targetPackage, GrammarSpec
	 * grammarSpec, String destinationFilePath) throws IOException { // generate
	 * parser based on new grammar File newGrammarFile = new
	 * File(destinationFilePath); generateParserWithANTLR(newGrammarFile,
	 * targetPackage, metaLangPrefix.toUpperCase() + placeHolderName);
	 * 
	 * // generate PlaceholderDetectorListener using Freemarker try {
	 * generatePlaceholderDetectorListenerWithFreemarker(destinationPath,
	 * grammarSpec, targetPackage); } catch (TemplateException e) {
	 * e.printStackTrace(); } }
	 */

	private static void generatePlaceholderDetectorListenerWithFreemarker(String destinationPath,
			GrammarSpec grammarSpec, String targetPackage) throws IOException, TemplateException {

		/* Create and adjust the configuration singleton */
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setDirectoryForTemplateLoading(new File("src/main/resources"));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setSharedVariable("package", targetPackage);

		/* Create a data-model */
		// just reuse grammarSpec

		/* Get the template (uses cache internally) */
		Template temp = cfg.getTemplate("PlaceholderDetectorListenerTemplate.ftl");

		/* Merge data-model with template */
		Writer out = new FileWriter(
				new File(destinationPath + grammarSpec.getNewGrammarName() + "PlaceholderDetectorListener.java"));
		temp.process(grammarSpec, out);
		out.close();

	}

	/*
	 * private static void generateParserWithANTLR(File grammarFile, String
	 * targetPackage, String placeholderName) throws IOException { String[] args = {
	 * grammarFile.getCanonicalPath(), "-listener", "-o",
	 * grammarFile.getParentFile().getCanonicalPath(), "-package", targetPackage,
	 * "-long-messages", "-metalang-placeholder", placeholderName }; Tool antlr =
	 * new Tool(args); if (args.length == 0) { antlr.help(); }
	 * 
	 * try { antlr.processGrammarsOnCommandLine(); } finally { if (antlr.log) { try
	 * { String logname = antlr.logMgr.save(); System.out.println("wrote " +
	 * logname); } catch (IOException ioe) {
	 * antlr.errMgr.toolError(ErrorType.INTERNAL_ERROR, ioe); } } }
	 * 
	 * if (antlr.errMgr.getNumErrors() > 0) { antlr.exit(1); } }
	 */

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

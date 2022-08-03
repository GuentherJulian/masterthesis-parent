package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;

import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.ObjectLanguageProperties;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTree;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreeTransformer;

public class AimPatternDetectionEngine {

	private List<AimPattern> aimpattern;
	private List<Path> compilationUnits;
	private Class<? extends Parser> parserClass;
	private Class<? extends Lexer> lexerClass;
	private Path templateGrammarPath;
	private MetaLanguagePattern metaLanguagePattern;
	private MetaLanguageLexerRules metaLanguageLexerRules;
	private ObjectLanguageProperties objectLanguageProperties;

	private PredictionMode predictionMode = PredictionMode.LL_EXACT_AMBIG_DETECTION;

	public AimPatternDetectionEngine(List<AimPattern> aimpattern, List<Path> compilationUnits,
			Class<? extends Parser> parserClass, Class<? extends Lexer> lexerClass, Path templateGrammarPath,
			MetaLanguagePattern metaLanguagePattern, MetaLanguageLexerRules metaLanguageLexerRules,
			ObjectLanguageProperties objectLanguageProperties) {
		this.aimpattern = aimpattern;
		this.compilationUnits = compilationUnits;
		this.parserClass = parserClass;
		this.lexerClass = lexerClass;
		this.templateGrammarPath = templateGrammarPath;
		this.metaLanguagePattern = metaLanguagePattern;
		this.metaLanguageLexerRules = metaLanguageLexerRules;
		this.objectLanguageProperties = objectLanguageProperties;
	}

	public AimPatternDetectionResult detect() throws Exception {
		return detect("compilationUnit");
	}

	public AimPatternDetectionResult detect(String startRuleName) throws Exception {

		// Each path only has one parse tree
		Map<Path, ParseTree> compilationUnitParseTrees = new HashMap<Path, ParseTree>();
		// Each path has multiple parse trees
		Map<AimPatternTemplate, List<ParseTree>> aimPatternParseTrees = new HashMap<AimPatternTemplate, List<ParseTree>>();

		Map<String, List<String>> listPatterns = null;
		ParseTreeTransformer parseTreeTransformer = null;

		InputStream grammarInputStream = Files.newInputStream(templateGrammarPath);

		TemplateParser<? extends Parser> templateParser;

		for (Path compilationUnit : this.compilationUnits) {
			Parser parser = createParser(compilationUnit);
			templateParser = new TemplateParser<>(parser, parser.getClass().getMethod(startRuleName),
					grammarInputStream);

			List<ParserRuleContext> parseTrees = templateParser.parseAmbiguties(this.predictionMode);
			ParserRuleContext parseTree = parseTrees.get(0);
			templateParser.showTree(parseTree);

			if (listPatterns == null) {
				listPatterns = templateParser.getListPatterns();
			}

			if (parseTreeTransformer == null) {
				parseTreeTransformer = new ParseTreeTransformer(parser.getVocabulary(),
						templateParser.getListPatterns(), this.metaLanguagePattern, this.metaLanguageLexerRules,
						this.objectLanguageProperties);
			}
			compilationUnitParseTrees.put(compilationUnit, parseTreeTransformer.transform(parseTree));
		}

		// TODO Implement iteration over all aim pattern
		for (AimPatternTemplate aimPatternTemplate : this.aimpattern.get(0).getAimPatternTemplates()) {
			Parser parser = createParser(aimPatternTemplate.getTemplatePath());
			templateParser = new TemplateParser<>(parser, parser.getClass().getMethod(startRuleName),
					grammarInputStream);

			List<ParserRuleContext> parseTrees = templateParser.parseAmbiguties(this.predictionMode);
			List<ParseTree> transformedParseTrees = new ArrayList<>();
			for (ParserRuleContext parseTree : parseTrees) {
				transformedParseTrees.add(parseTreeTransformer.transform(parseTree));
			}
			aimPatternParseTrees.put(aimPatternTemplate, transformedParseTrees);
		}

		return this.match(compilationUnitParseTrees, aimPatternParseTrees);
	}

	private AimPatternDetectionResult match(Map<Path, ParseTree> compilationUnitParseTrees,
			Map<AimPatternTemplate, List<ParseTree>> aimPatternParseTrees) {

		// Matching logic

		return null;
	}

	public PredictionMode getPredictionMode() {
		return predictionMode;
	}

	public void setPredictionMode(PredictionMode predictionMode) {
		this.predictionMode = predictionMode;
	}

	private Parser createParser(Path inputPath) throws IOException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		InputStream inputStream = new FileInputStream(inputPath.toFile());
		CharStream charStream = CharStreams.fromStream(inputStream);

		Constructor<? extends Lexer> lexerConstructor = this.lexerClass.getConstructor(CharStream.class);
		Lexer lexer = lexerConstructor.newInstance(charStream);

		TokenStream tokenStream = new CommonTokenStream(lexer);

		Constructor<? extends Parser> parserConstructor = this.parserClass.getConstructor(TokenStream.class);
		Parser parser = parserConstructor.newInstance(tokenStream);

		return parser;
	}
}

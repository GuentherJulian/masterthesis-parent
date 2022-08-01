package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
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

public class AimPatternDetectionEngine {

	private List<AimPattern> aimpattern;
	private List<Path> compilationUnits;
	private Class<? extends Parser> parserClass;
	private Class<? extends Lexer> lexerClass;
	private Path templateGrammarPath;

	private PredictionMode predictionMode = PredictionMode.LL_EXACT_AMBIG_DETECTION;

	public AimPatternDetectionEngine(List<AimPattern> aimpattern, List<Path> compilationUnits,
			Class<? extends Parser> parserClass, Class<? extends Lexer> lexerClass, Path templateGrammarPath) {
		this.aimpattern = aimpattern;
		this.compilationUnits = compilationUnits;
		this.parserClass = parserClass;
		this.lexerClass = lexerClass;
		this.templateGrammarPath = templateGrammarPath;
	}

	public AimPatternDetectionResult detect() throws Exception {
		return detect("compilationUnit");
	}

	public AimPatternDetectionResult detect(String startRuleName) throws Exception {

		// Each path only has one parse tree
		Map<Path, ParserRuleContext> compilationUnitParseTrees = new HashMap<Path, ParserRuleContext>();
		// Each path has multiple parse trees
		Map<AimPatternTemplate, List<ParserRuleContext>> aimPatternParseTrees = new HashMap<AimPatternTemplate, List<ParserRuleContext>>();

		InputStream grammarInputStream = Files.newInputStream(templateGrammarPath);

		TemplateParser<? extends Parser> templateParser;
		for (Path compilationUnit : this.compilationUnits) {
			Parser parser = createParser(compilationUnit);
			templateParser = new TemplateParser<>(parser, parser.getClass().getMethod(startRuleName),
					grammarInputStream);

			List<ParserRuleContext> parseTrees = templateParser.parseAmbiguties(this.predictionMode);
			compilationUnitParseTrees.put(compilationUnit, parseTrees.get(0));
		}

		// TODO Implement iteration over all aim pattern
		for (AimPatternTemplate aimPatternTemplate : this.aimpattern.get(0).getAimPatternTemplates()) {
			Parser parser = createParser(aimPatternTemplate.getTemplatePath());
			templateParser = new TemplateParser<>(parser, parser.getClass().getMethod(startRuleName),
					grammarInputStream);

			List<ParserRuleContext> parseTrees = templateParser.parseAmbiguties(this.predictionMode);
			aimPatternParseTrees.put(aimPatternTemplate, parseTrees);
		}

		return this.match(compilationUnitParseTrees, aimPatternParseTrees);
	}

	private AimPatternDetectionResult match(Map<Path, ParserRuleContext> compilationUnitParseTrees,
			Map<AimPatternTemplate, List<ParserRuleContext>> aimPatternParseTrees) {

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

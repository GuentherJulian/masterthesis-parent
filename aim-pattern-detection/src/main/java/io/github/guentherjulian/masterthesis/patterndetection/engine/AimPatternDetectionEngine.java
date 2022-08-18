package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.ObjectLanguageProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.InstantiationPathMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.InstantiationPathMatcher;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.ParseTreeMatcher;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTree;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreeTransformer;

public class AimPatternDetectionEngine {

	private static final Logger LOGGER = LogManager.getLogger(AimPatternDetectionEngine.class);

	private List<AimPattern> aimpattern;
	private List<Path> compilationUnits;
	private Class<? extends Parser> parserClass;
	private Class<? extends Lexer> lexerClass;
	private Path templateGrammarPath;
	private MetaLanguageConfiguration metaLanguageConfiguration;
	private ObjectLanguageProperties objectLanguageProperties;
	private PlaceholderResolver placeholderResolver;

	private PredictionMode predictionMode = PredictionMode.LL_EXACT_AMBIG_DETECTION;

	public AimPatternDetectionEngine(List<AimPattern> aimpattern, List<Path> compilationUnits,
			Class<? extends Parser> parserClass, Class<? extends Lexer> lexerClass, Path templateGrammarPath,
			MetaLanguageConfiguration metaLanguageConfiguration, ObjectLanguageProperties objectLanguageProperties,
			PlaceholderResolver placeholderResolver) {
		this.aimpattern = aimpattern;
		this.compilationUnits = compilationUnits;
		this.parserClass = parserClass;
		this.lexerClass = lexerClass;
		this.templateGrammarPath = templateGrammarPath;
		this.metaLanguageConfiguration = metaLanguageConfiguration;
		this.objectLanguageProperties = objectLanguageProperties;
		this.placeholderResolver = placeholderResolver;
	}

	public AimPatternDetectionResult detect() throws Exception {
		return detect("compilationUnit");
	}

	public AimPatternDetectionResult detect(String startRuleName) throws Exception {
		AimPatternDetectionResult result = new AimPatternDetectionResult();
		long processingTimeStart = System.nanoTime();

		// Each path only has one parse tree
		Map<Path, ParseTree> compilationUnitParseTrees = new LinkedHashMap<Path, ParseTree>();

		Map<String, List<String>> listPatterns = null;
		ParseTreeTransformer parseTreeTransformer = null;

		InputStream grammarInputStream = Files.newInputStream(templateGrammarPath);

		TemplateParser<? extends Parser> templateParser;

		LOGGER.info("Parsing compilation units...");
		long startTime = System.nanoTime();
		for (Path compilationUnit : this.compilationUnits) {

			Parser parser = createParser(compilationUnit);
			templateParser = new TemplateParser<>(parser, parser.getClass().getMethod(startRuleName),
					grammarInputStream);

			List<ParserRuleContext> parseTrees = templateParser.parseAmbiguties(this.predictionMode);
			ParserRuleContext parseTree = parseTrees.get(0);
			// templateParser.showTree(parseTree);

			if (listPatterns == null) {
				listPatterns = templateParser.getListPatterns();
			}

			if (parseTreeTransformer == null) {
				parseTreeTransformer = new ParseTreeTransformer(parser.getVocabulary(),
						templateParser.getListPatterns(), this.metaLanguageConfiguration.getMetaLanguageLexerRules(),
						this.objectLanguageProperties);
			}

			compilationUnitParseTrees.put(compilationUnit, parseTreeTransformer.transform(parseTree));
		}
		long endTime = System.nanoTime();
		LOGGER.info("Finished parsing compilation units (took {} ns, {} ms)", (endTime - startTime),
				((endTime - startTime) / 1e6));

		// TODO Implement iteration over all aim pattern
		for (AimPatternTemplate aimPatternTemplate : this.aimpattern.get(0).getAimPatternTemplates()) {

			LOGGER.info("Process AIM pattern template {}", aimPatternTemplate.getTemplatePath());
			// match each template with all compilation units
			for (Entry<Path, ParseTree> entry : compilationUnitParseTrees.entrySet()) {
				Path compilationUnitPath = entry.getKey();
				ParseTree compilationUnitParseTree = entry.getValue();

				InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(
						compilationUnitPath.toString(), aimPatternTemplate.getInstantiationPath(),
						this.metaLanguageConfiguration.getMetaLanguagePattern(), this.placeholderResolver);
				// only try to match if instantiation path matches
				// TODO remove setMatch(true)
				instantiationPathMatch.setMatch(true);
				if (instantiationPathMatch.isMatch()) {
					LOGGER.info("Instantiation path matches with {}", compilationUnitPath);
					// TODO matching of instantiation path
					Map<String, Set<String>> placeholderSubstitutions = instantiationPathMatch
							.getPlaceholderSubstitutions();

					LOGGER.info("Parse template...");
					startTime = System.nanoTime();

					// TODO only parse template once, not for every compilation unit
					Parser parser = null;
					if (!aimPatternTemplate.isPreprocessed()) {
						parser = createParser(aimPatternTemplate.getTemplatePath());
					} else {
						parser = createParser(aimPatternTemplate.getPreprocessedTemplateByteArray());
					}
					templateParser = new TemplateParser<>(parser, parser.getClass().getMethod(startRuleName),
							grammarInputStream);

					List<ParserRuleContext> parseTrees = templateParser.parseAmbiguties(this.predictionMode);
					List<ParseTree> transformedParseTrees = new ArrayList<>();
					for (ParserRuleContext parseTree : parseTrees) {
						// templateParser.showTree(parseTree);
						transformedParseTrees.add(parseTreeTransformer.transform(parseTree));
					}
					endTime = System.nanoTime();
					LOGGER.info("Finished parsing template (took {} ns, {} ms)", (endTime - startTime),
							((endTime - startTime) / 1e6));

					LOGGER.info("Match parse trees of {} and {}...", aimPatternTemplate.getTemplatePath().getFileName(),
							compilationUnitPath.getFileName());
					TreeMatch treeMatch = this.match(compilationUnitParseTree, transformedParseTrees,
							placeholderSubstitutions);

					AimPatternDetectionResultEntry aimPatternDetectionResultEntry = new AimPatternDetectionResultEntry(
							compilationUnitPath, aimPatternTemplate.getTemplatePath(), treeMatch);
					result.addPatternDetectionResultEntry(aimPatternDetectionResultEntry);
				}
			}
		}
		long processingTimeEnd = System.nanoTime();
		result.setProcessingTime(processingTimeEnd - processingTimeStart);

		return result;
	}

	private TreeMatch match(ParseTree compilationUnitParseTree, List<ParseTree> aimPatternParseTrees,
			Map<String, Set<String>> placeholderSubstitutions) {

		TreeMatch treeMatch = null;
		for (ParseTree aimPatternParseTree : aimPatternParseTrees) {
			ParseTreeMatcher parseTreeMatcher = new ParseTreeMatcher(compilationUnitParseTree, aimPatternParseTree,
					this.metaLanguageConfiguration, this.placeholderResolver);
			treeMatch = parseTreeMatcher.match(placeholderSubstitutions);
			if (treeMatch.isMatch())
				break;
		}

		return treeMatch;
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

	private Parser createParser(byte[] preprocessedTemplateByteArray)
			throws IOException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		InputStream inputStream = new ByteArrayInputStream(preprocessedTemplateByteArray);
		CharStream charStream = CharStreams.fromStream(inputStream);

		Constructor<? extends Lexer> lexerConstructor = this.lexerClass.getConstructor(CharStream.class);
		Lexer lexer = lexerConstructor.newInstance(charStream);

		TokenStream tokenStream = new CommonTokenStream(lexer);
		Constructor<? extends Parser> parserConstructor = this.parserClass.getConstructor(TokenStream.class);
		Parser parser = parserConstructor.newInstance(tokenStream);

		return parser;
	}
}

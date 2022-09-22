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
import java.util.HashMap;
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
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.ObjectLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.InstantiationPathMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.InstantiationPathMatcher;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.ParseTreeMatcher;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessorUtil;
import io.github.guentherjulian.masterthesis.patterndetection.engine.utils.ParseTreeUtil;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTree;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreeTransformer;

public class AimPatternDetectionEngine {

	private static final Logger LOGGER = LogManager.getLogger(AimPatternDetectionEngine.class);

	private List<AimPattern> aimpattern;
	private List<Path> compilationUnits;
	private Class<? extends Parser> parserClass;
	private Class<? extends Lexer> lexerClass;
	private Path templateGrammarPath;
	private MetaLanguageConfiguration metaLanguageConfi;
	private ObjectLanguageConfiguration objectLanguageConfig;
	private PlaceholderResolver placeholderResolver;
	private TemplatePreprocessor templatePreprocessor;

	private Map<String, List<String>> listPatterns = null;
	private ParseTreeTransformer parseTreeTransformer = null;

	private PredictionMode predictionMode = PredictionMode.LL_EXACT_AMBIG_DETECTION;

	public AimPatternDetectionEngine(List<AimPattern> aimpattern, List<Path> compilationUnits,
			Class<? extends Parser> parserClass, Class<? extends Lexer> lexerClass, Path templateGrammarPath,
			MetaLanguageConfiguration metaLanguageConfiguration, ObjectLanguageConfiguration objectLanguageProperties,
			PlaceholderResolver placeholderResolver, TemplatePreprocessor templatePreprocessor) {
		this.aimpattern = aimpattern;
		this.compilationUnits = compilationUnits;
		this.parserClass = parserClass;
		this.lexerClass = lexerClass;
		this.templateGrammarPath = templateGrammarPath;
		this.metaLanguageConfi = metaLanguageConfiguration;
		this.objectLanguageConfig = objectLanguageProperties;
		this.placeholderResolver = placeholderResolver;
		this.templatePreprocessor = templatePreprocessor;
	}

	public AimPatternDetectionResult detect() throws Exception {
		return detect("compilationUnit");
	}

	public AimPatternDetectionResult detect(String startRuleName) throws Exception {
		AimPatternDetectionResult result = new AimPatternDetectionResult();
		result.setNumTemplatesTotal(this.aimpattern.get(0).getAimPatternTemplates().size());
		result.setNumCompilationUnitsTotal(this.compilationUnits.size());

		int numParsedTemplates = 0;
		int numParsedCompilationUnits = 0;
		int numComparedFiles = 0;
		int numInstantiationPathMatches = 0;
		int numFileMatches = 0;
		int numSuccessfulParsedTemplates = 0;
		int numUnsuccessfulParsedTemplates = 0;
		long processingTimeStart = System.nanoTime();

		Map<Path, ParseTree> compilationUnitParseTrees = new HashMap<>();
		Map<Path, List<ParseTree>> templateParseTrees = new HashMap<>();
		List<Path> unparseableTemplates = new ArrayList<>();
		boolean isTemplateUnparseable = false;

		InputStream grammarInputStream = Files.newInputStream(templateGrammarPath);
		TemplateParser<? extends Parser> templateParser;

		if (this.templatePreprocessor.getTemplatesRootPath() == null) {
			this.templatePreprocessor.setTemplatesRootPath(this.aimpattern.get(0).getTemplatesRootPath());
		}

		for (Path compilationUnitPath : this.compilationUnits) {
			for (AimPatternTemplate aimPatternTemplate : this.aimpattern.get(0).getAimPatternTemplates()) {

				// match instantiation path
				InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(
						compilationUnitPath.toString(), aimPatternTemplate.getInstantiationPath(),
						this.metaLanguageConfi.getMetaLanguagePattern(), this.placeholderResolver);
				instantiationPathMatch.setMatch(true);

				if (instantiationPathMatch.isMatch()) {
					LOGGER.info("Instantiation path matches: {} <-> {}", compilationUnitPath,
							aimPatternTemplate.getTemplatePath());
					numInstantiationPathMatches++;

					Map<String, Set<String>> placeholderSubstitutions = instantiationPathMatch
							.getPlaceholderSubstitutions();

					isTemplateUnparseable = false;
					if (unparseableTemplates.contains(aimPatternTemplate.getTemplatePath())) {
						isTemplateUnparseable = true;
					}

					if (!isTemplateUnparseable) {
						// parse compilation unit
						if (!compilationUnitParseTrees.containsKey(compilationUnitPath)) {
							LOGGER.info("Parse compilation unit: {}", compilationUnitPath);
							Parser parser = createParser(compilationUnitPath);
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
										templateParser.getListPatterns(),
										this.metaLanguageConfi.getMetaLanguageLexerRules(), this.objectLanguageConfig);
							}

							numParsedCompilationUnits++;
							compilationUnitParseTrees.put(compilationUnitPath,
									parseTreeTransformer.transform(parseTree));
						}

						// parse template
						if (!templateParseTrees.containsKey(aimPatternTemplate.getTemplatePath())) {
							LOGGER.info("Parse template: {}", aimPatternTemplate.getTemplatePath());
							numParsedTemplates++;

							byte[] preprocessedTemplate = TemplatePreprocessorUtil.applyPreprocessing(
									this.templatePreprocessor, aimPatternTemplate.getTemplatePath());

							Parser parser = createParser(preprocessedTemplate);
							if (this.templatePreprocessor.getVariables() != null) {
								for (Entry<String, Set<String>> variable : this.templatePreprocessor.getVariables()
										.entrySet()) {
									placeholderSubstitutions.put(variable.getKey(), variable.getValue());
								}
							}

							templateParser = new TemplateParser<>(parser, parser.getClass().getMethod(startRuleName),
									grammarInputStream);

							List<ParserRuleContext> parseTrees = templateParser.parseAmbiguties(this.predictionMode);
							List<ParseTree> transformedTemplateParseTrees = new ArrayList<>();
							for (ParserRuleContext parseTree : parseTrees) {
								// templateParser.showTree(parseTree);
								int nodes = ParseTreeUtil.countTreeNodes(parseTree);
								int terminalNodes = ParseTreeUtil.countTerminalNodes(parseTree);

								try {
									transformedTemplateParseTrees.add(parseTreeTransformer.transform(parseTree));
								} catch (IllegalStateException e) {
									unparseableTemplates.add(aimPatternTemplate.getTemplatePath());
									isTemplateUnparseable = true;
								}

								if (isTemplateUnparseable) {
									numUnsuccessfulParsedTemplates++;
								} else {
									numSuccessfulParsedTemplates++;
								}
							}
							templateParseTrees.put(aimPatternTemplate.getTemplatePath(), transformedTemplateParseTrees);
						}

						if (!isTemplateUnparseable) {

							if (compilationUnitPath.getFileName().toString().contains("BinaryObject")) {
								System.out.println("");
							}

							LOGGER.info("Match parse trees of {} and {}...",
									aimPatternTemplate.getTemplatePath().getFileName(),
									compilationUnitPath.getFileName());
							TreeMatch treeMatch = this.match(compilationUnitParseTrees.get(compilationUnitPath),
									templateParseTrees.get(aimPatternTemplate.getTemplatePath()),
									placeholderSubstitutions);
							numComparedFiles++;
							if (treeMatch.isMatch()) {
								numFileMatches++;
							}

							AimPatternDetectionResultEntry aimPatternDetectionResultEntry = new AimPatternDetectionResultEntry(
									compilationUnitPath, aimPatternTemplate.getTemplatePath(), treeMatch);
							result.addPatternDetectionResultEntry(aimPatternDetectionResultEntry);
						}
					}

					if (isTemplateUnparseable) {
						AimPatternDetectionResultEntry aimPatternDetectionResultEntry = new AimPatternDetectionResultEntry(
								compilationUnitPath, aimPatternTemplate.getTemplatePath(), null);
						aimPatternDetectionResultEntry.setTemplateUnparseable(true);
						result.addPatternDetectionResultEntry(aimPatternDetectionResultEntry);
					}
				}

			}
		}

		long processingTimeEnd = System.nanoTime();
		result.setProcessingTime(processingTimeEnd - processingTimeStart);
		result.setNumParsedTemplates(numParsedTemplates);
		result.setNumParsedCompilationUnits(numParsedCompilationUnits);
		result.setNumComparedFiles(numComparedFiles);
		result.setNumInstantiationPathMatches(numInstantiationPathMatches);
		result.setNumFileMatches(numFileMatches);
		result.setNumParseableTemplates(numSuccessfulParsedTemplates);
		result.setNumUnparseableTemplates(numUnsuccessfulParsedTemplates);
		return result;
	}

	public AimPatternDetectionResult detectOld(String startRuleName) throws Exception {
		AimPatternDetectionResult result = new AimPatternDetectionResult();
		int numParsedTemplates = 0;
		int numParsedCompilationUnits = 0;
		int numComparedFiles = 0;
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
						templateParser.getListPatterns(), this.metaLanguageConfi.getMetaLanguageLexerRules(),
						this.objectLanguageConfig);
			}

			numParsedCompilationUnits++;
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
						this.metaLanguageConfi.getMetaLanguagePattern(), this.placeholderResolver);
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
					Parser parser = createParser(TemplatePreprocessorUtil.applyPreprocessing(this.templatePreprocessor,
							aimPatternTemplate.getTemplatePath()));
					templateParser = new TemplateParser<>(parser, parser.getClass().getMethod(startRuleName),
							grammarInputStream);

					List<ParserRuleContext> parseTrees = templateParser.parseAmbiguties(this.predictionMode);
					List<ParseTree> transformedParseTrees = new ArrayList<>();
					for (ParserRuleContext parseTree : parseTrees) {
						// templateParser.showTree(parseTree);
						transformedParseTrees.add(parseTreeTransformer.transform(parseTree));
					}
					numParsedTemplates++;
					endTime = System.nanoTime();
					LOGGER.info("Finished parsing template (took {} ns, {} ms)", (endTime - startTime),
							((endTime - startTime) / 1e6));

					LOGGER.info("Match parse trees of {} and {}...", aimPatternTemplate.getTemplatePath().getFileName(),
							compilationUnitPath.getFileName());
					TreeMatch treeMatch = this.match(compilationUnitParseTree, transformedParseTrees,
							placeholderSubstitutions);
					numComparedFiles++;

					AimPatternDetectionResultEntry aimPatternDetectionResultEntry = new AimPatternDetectionResultEntry(
							compilationUnitPath, aimPatternTemplate.getTemplatePath(), treeMatch);
					result.addPatternDetectionResultEntry(aimPatternDetectionResultEntry);
				}
			}
		}
		long processingTimeEnd = System.nanoTime();
		result.setProcessingTime(processingTimeEnd - processingTimeStart);
		result.setNumParsedTemplates(numParsedTemplates);
		result.setNumParsedCompilationUnits(numParsedCompilationUnits);
		result.setNumComparedFiles(numComparedFiles);
		return result;
	}

	private TreeMatch match(ParseTree compilationUnitParseTree, List<ParseTree> aimPatternParseTrees,
			Map<String, Set<String>> placeholderSubstitutions) {

		TreeMatch treeMatch = null;
		for (ParseTree aimPatternParseTree : aimPatternParseTrees) {
			ParseTreeMatcher parseTreeMatcher = new ParseTreeMatcher(compilationUnitParseTree, aimPatternParseTree,
					this.metaLanguageConfi, this.placeholderResolver);
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

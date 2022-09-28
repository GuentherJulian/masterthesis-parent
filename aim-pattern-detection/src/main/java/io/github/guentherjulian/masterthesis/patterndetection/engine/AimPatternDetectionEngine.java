package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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

	private AimPattern aimpattern;
	private List<Path> compilationUnits;
	private Class<? extends Parser> parserClass;
	private Class<? extends Lexer> lexerClass;
	private Path templateGrammarPath;
	private MetaLanguageConfiguration metaLanguageConfig;
	private ObjectLanguageConfiguration objectLanguageConfig;
	private PlaceholderResolver placeholderResolver;
	private TemplatePreprocessor templatePreprocessor;

	// private Map<String, List<String>> listPatterns = null;
	private ParseTreeTransformer parseTreeTransformer = null;

	private PredictionMode predictionMode = PredictionMode.LL_EXACT_AMBIG_DETECTION;

	private boolean forceMatching = false;
	private boolean preprocessTemplates = true;

	public AimPatternDetectionEngine(AimPattern aimpattern, List<Path> compilationUnits,
			Class<? extends Parser> parserClass, Class<? extends Lexer> lexerClass, Path templateGrammarPath,
			MetaLanguageConfiguration metaLanguageConfiguration, ObjectLanguageConfiguration objectLanguageProperties,
			PlaceholderResolver placeholderResolver, TemplatePreprocessor templatePreprocessor) {
		this.aimpattern = aimpattern;
		this.compilationUnits = compilationUnits;
		this.parserClass = parserClass;
		this.lexerClass = lexerClass;
		this.templateGrammarPath = templateGrammarPath;
		this.metaLanguageConfig = metaLanguageConfiguration;
		this.objectLanguageConfig = objectLanguageProperties;
		this.placeholderResolver = placeholderResolver;
		this.templatePreprocessor = templatePreprocessor;
	}

	public AimPatternDetectionResult detect() throws Exception {
		return detect("compilationUnit");
	}

	public AimPatternDetectionResult detect(String startRuleName) throws Exception {
		AimPatternDetectionResult result = new AimPatternDetectionResult();
		result.setNumTemplatesTotal(this.aimpattern.getAimPatternTemplates().size());
		result.setNumCompilationUnitsTotal(this.compilationUnits.size());

		int numParsedTemplates = 0;
		int numParsedCompilationUnits = 0;
		int numComparedFiles = 0;
		int numInstantiationPathMatches = 0;
		int numFileMatches = 0;
		int numSuccessfulParsedTemplates = 0;
		int numUnsuccessfulParsedTemplates = 0;

		int overallComparisions = 0;

		long processingTimeStart = System.nanoTime();

		Map<Path, ParseTree> compilationUnitParseTrees = new HashMap<>();
		Map<Path, List<ParseTree>> templateParseTrees = new HashMap<>();
		List<Path> unparseableTemplates = new ArrayList<>();

		InputStream grammarInputStream = Files.newInputStream(templateGrammarPath);
		TemplateParser<? extends Parser> templateParser;

		if (this.templatePreprocessor.getTemplatesRootPath() == null) {
			this.templatePreprocessor.setTemplatesRootPath(this.aimpattern.getTemplatesRootPath());
		}

		for (AimPatternTemplate aimPatternTemplate : this.aimpattern.getAimPatternTemplates()) {

			Map<String, Set<String>> placeholderSubstitutionsTemplate = new HashMap<>();
			boolean isTemplateUnparseable = false;

			// try to parse template
			LOGGER.info("Parse template: {}", aimPatternTemplate.getTemplatePath());
			numParsedTemplates++;

			byte[] preprocessedTemplate = null;
			if (this.preprocessTemplates) {
				preprocessedTemplate = TemplatePreprocessorUtil.applyPreprocessing(this.templatePreprocessor,
						aimPatternTemplate.getTemplatePath());
			} else {
				preprocessedTemplate = TemplatePreprocessorUtil.applyPreprocessing(null,
						aimPatternTemplate.getTemplatePath());
			}

			Parser parser = createParser(preprocessedTemplate);
			if (this.templatePreprocessor.getVariables() != null) {
				for (Entry<String, Set<String>> variable : this.templatePreprocessor.getVariables().entrySet()) {
					placeholderSubstitutionsTemplate.put(variable.getKey(), variable.getValue());
				}
			}

			templateParser = new TemplateParser<>(parser, parser.getClass().getMethod(startRuleName),
					grammarInputStream);

			if (parseTreeTransformer == null) {
				parseTreeTransformer = new ParseTreeTransformer(parser.getVocabulary(),
						templateParser.getListPatterns(), this.metaLanguageConfig.getMetaLanguageLexerRules(),
						this.objectLanguageConfig);
			}

			// parse template
			List<ParserRuleContext> parseTrees = templateParser.parseAmbiguties(this.predictionMode);
			List<ParseTree> transformedTemplateParseTrees = new ArrayList<>();
			for (ParserRuleContext parseTree : parseTrees) {
				// templateParser.showTree(parseTree);
				int nodes = ParseTreeUtil.countTreeNodes(parseTree);
				int terminalNodes = ParseTreeUtil.countTerminalNodes(parseTree);

				try {
					transformedTemplateParseTrees.add(parseTreeTransformer.transform(parseTree));

					templateParseTrees.put(aimPatternTemplate.getTemplatePath(), transformedTemplateParseTrees);
				} catch (IllegalStateException e) {
					unparseableTemplates.add(aimPatternTemplate.getTemplatePath());
					isTemplateUnparseable = true;
				}
			}

			if (isTemplateUnparseable) {
				numUnsuccessfulParsedTemplates++;

				AimPatternDetectionResultEntry aimPatternDetectionResultEntry = new AimPatternDetectionResultEntry(null,
						aimPatternTemplate.getTemplatePath(), null);
				aimPatternDetectionResultEntry.setTemplateUnparseable(true);
				result.addPatternDetectionResultEntry(aimPatternDetectionResultEntry);
			} else {
				numSuccessfulParsedTemplates++;

				String instantiationPath = aimPatternTemplate.getInstantiationPath();

				// filter possible compilation unit paths
				List<String> pathWithoutPrefix = new LinkedList<>();
				List<String> longestPathWithoutPrefix = new LinkedList<>();

				if (this.placeholderResolver != null) {
					String[] templatePathSegments = instantiationPath
							.split(FileSystems.getDefault().getSeparator().replace("\\", "\\\\"));
					String metaLangFileExtension = this.metaLanguageConfig.getMetaLanguagePattern()
							.getMetaLangFileExtension();
					for (int i = 0; i < templatePathSegments.length; i++) {
						boolean isPlaceholder = this.placeholderResolver.isPlaceholder(templatePathSegments[i]);

						if (!isPlaceholder) {
							if (templatePathSegments[i].endsWith(metaLangFileExtension)) {
								templatePathSegments[i] = templatePathSegments[i].substring(0,
										templatePathSegments[i].lastIndexOf(metaLangFileExtension) - 1);
							}
							pathWithoutPrefix.add(templatePathSegments[i]);

							if (pathWithoutPrefix.size() > longestPathWithoutPrefix.size()) {
								longestPathWithoutPrefix = new LinkedList<>(pathWithoutPrefix);
							}
						} else {
							pathWithoutPrefix = new LinkedList<>();
						}
					}
				}

				List<Path> possibleCompilationUnits = null;
				if (longestPathWithoutPrefix.isEmpty() || forceMatching) {
					possibleCompilationUnits = this.compilationUnits;
				} else {
					String longestPath = String.join(FileSystems.getDefault().getSeparator(), longestPathWithoutPrefix);
					possibleCompilationUnits = this.compilationUnits.stream()
							.filter(compilationUnitPath -> compilationUnitPath.toString().contains(longestPath))
							.collect(Collectors.toList());
				}

				if (possibleCompilationUnits.isEmpty()) {
					AimPatternDetectionResultEntry aimPatternDetectionResultEntry = new AimPatternDetectionResultEntry(
							null, aimPatternTemplate.getTemplatePath(), null);
					aimPatternDetectionResultEntry.setNoCompilationUnitMatchedPath(true);
					result.addPatternDetectionResultEntry(aimPatternDetectionResultEntry);
				} else {
					for (Path compilationUnitPath : possibleCompilationUnits) {
						overallComparisions++;

						// match instantiation path
						InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(
								compilationUnitPath.toString(), aimPatternTemplate.getInstantiationPath(),
								this.metaLanguageConfig.getMetaLanguagePattern(), this.placeholderResolver);
						if (this.forceMatching) {
							instantiationPathMatch.setMatch(true);
						}

						if (instantiationPathMatch.isMatch()) {
							LOGGER.info("Instantiation path matches: {} <-> {}", compilationUnitPath,
									aimPatternTemplate.getTemplatePath());
							numInstantiationPathMatches++;

							Map<String, Set<String>> placeholderSubstitutions = instantiationPathMatch
									.getPlaceholderSubstitutions();
							placeholderSubstitutions.putAll(placeholderSubstitutionsTemplate);

							// parse compilation unit
							if (!compilationUnitParseTrees.containsKey(compilationUnitPath)) {
								LOGGER.info("Parse compilation unit: {}", compilationUnitPath);
								Parser compilationUnitParser = createParser(compilationUnitPath);
								templateParser = new TemplateParser<>(compilationUnitParser,
										compilationUnitParser.getClass().getMethod(startRuleName), grammarInputStream);

								List<ParserRuleContext> compUnitParseTrees = templateParser
										.parseAmbiguties(this.predictionMode);
								ParserRuleContext parseTree = compUnitParseTrees.get(0);
								// templateParser.showTree(parseTree);

								numParsedCompilationUnits++;
								compilationUnitParseTrees.put(compilationUnitPath,
										parseTreeTransformer.transform(parseTree));
							}

							// match path trees
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

	public AimPatternDetectionResult detect2(String startRuleName) throws Exception {
		AimPatternDetectionResult result = new AimPatternDetectionResult();
		result.setNumTemplatesTotal(this.aimpattern.getAimPatternTemplates().size());
		result.setNumCompilationUnitsTotal(this.compilationUnits.size());

		int numParsedTemplates = 0;
		int numParsedCompilationUnits = 0;
		int numComparedFiles = 0;
		int numInstantiationPathMatches = 0;
		int numFileMatches = 0;
		int numSuccessfulParsedTemplates = 0;
		int numUnsuccessfulParsedTemplates = 0;

		int overallComparisions = 0;

		long processingTimeStart = System.nanoTime();

		Map<Path, ParseTree> compilationUnitParseTrees = new HashMap<>();
		Map<Path, List<ParseTree>> templateParseTrees = new HashMap<>();
		List<Path> unparseableTemplates = new ArrayList<>();
		boolean isTemplateUnparseable = false;

		InputStream grammarInputStream = Files.newInputStream(templateGrammarPath);
		TemplateParser<? extends Parser> templateParser;

		if (this.templatePreprocessor.getTemplatesRootPath() == null) {
			this.templatePreprocessor.setTemplatesRootPath(this.aimpattern.getTemplatesRootPath());
		}

		for (Path compilationUnitPath : this.compilationUnits) {
			for (AimPatternTemplate aimPatternTemplate : this.aimpattern.getAimPatternTemplates()) {

				overallComparisions++;

				// match instantiation path
				InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(
						compilationUnitPath.toString(), aimPatternTemplate.getInstantiationPath(),
						this.metaLanguageConfig.getMetaLanguagePattern(), this.placeholderResolver);
				// instantiationPathMatch.setMatch(true);

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

							// if (listPatterns == null) {
							// listPatterns = templateParser.getListPatterns();
							// }

							if (parseTreeTransformer == null) {
								parseTreeTransformer = new ParseTreeTransformer(parser.getVocabulary(),
										templateParser.getListPatterns(),
										this.metaLanguageConfig.getMetaLanguageLexerRules(), this.objectLanguageConfig);
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

	private TreeMatch match(ParseTree compilationUnitParseTree, List<ParseTree> aimPatternParseTrees,
			Map<String, Set<String>> placeholderSubstitutions) {

		TreeMatch treeMatch = null;
		for (ParseTree aimPatternParseTree : aimPatternParseTrees) {
			ParseTreeMatcher parseTreeMatcher = new ParseTreeMatcher(compilationUnitParseTree, aimPatternParseTree,
					this.metaLanguageConfig, this.placeholderResolver);
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

	public void setForceMatching(boolean forceMatching) {
		this.forceMatching = forceMatching;
	}

	public void setPreprocessTemplates(boolean preprocessTemplates) {
		this.preprocessTemplates = preprocessTemplates;
	}
}

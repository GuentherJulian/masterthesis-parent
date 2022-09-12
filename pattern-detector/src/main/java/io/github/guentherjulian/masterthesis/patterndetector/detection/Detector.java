package io.github.guentherjulian.masterthesis.patterndetector.detection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.AimPatternDetectionEngine;
import io.github.guentherjulian.masterthesis.patterndetection.engine.AimPatternDetectionResult;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.ObjectLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.utils.PathUtil;
import io.github.guentherjulian.masterthesis.patterndetector.detection.configuration.DetectorConfigurationUtils;
import io.github.guentherjulian.masterthesis.patterndetector.detection.configuration.MetaLanguage;
import io.github.guentherjulian.masterthesis.patterndetector.detection.configuration.ObjectLanguage;

public class Detector {

	private Path templatesPath;
	private Path compilationUnitPath;
	private Path templateGrammarPath;
	private ObjectLanguage objectLanguage;
	private MetaLanguage metalanguage;
	private String metaLanguagePrefix;

	public Detector(Path templatesPath, Path compilationUnitPath, Path templateGrammarPath, String objectLanguageString,
			String metalanguageString, String metaLanguagePrefix) {
		this.templatesPath = templatesPath;
		this.compilationUnitPath = compilationUnitPath;
		this.templateGrammarPath = templateGrammarPath;
		this.objectLanguage = ObjectLanguage.getObjectLanguage(objectLanguageString);
		this.metalanguage = MetaLanguage.getMetaLanguage(metalanguageString);
		this.metaLanguagePrefix = metaLanguagePrefix;
	}

	public AimPatternDetectionResult detect() throws Exception {
		AimPatternDetectionResult detectionResult = null;
		try {
			// TODO change regex depending on language
			List<AimPatternTemplate> templates = PathUtil.getAimPatternTemplates(this.templatesPath, ".*\\.ftl");
			if (templates.size() == 0) {
				throw new RuntimeException("No templates found in the given path.");
			}
			List<AimPattern> aimPatterns = new ArrayList<>();
			AimPattern aimPattern = new AimPattern(templatesPath);
			aimPattern.setAimPatternTemplates(templates);
			aimPatterns.add(aimPattern);

			List<Path> compilationUnits = getCompilationUnitPaths();
			if (compilationUnits.size() == 0) {
				throw new RuntimeException("No compilation units found in the given path.");
			}

			Class<? extends Parser> parserClass = DetectorConfigurationUtils.getParserClass(this.objectLanguage,
					this.metalanguage);
			Class<? extends Lexer> lexerClass = DetectorConfigurationUtils.getLexerClass(this.objectLanguage,
					this.metalanguage);
			if (parserClass == null || lexerClass == null) {
				throw new RuntimeException("The correct parser or lexer class could not be determined.");
			}

			MetaLanguageConfiguration metaLanguageConfiguration = MetaLanguage
					.getMetaLanguageConfiguration(this.metalanguage, this.metaLanguagePrefix);
			ObjectLanguageConfiguration objectLanguageProperties = ObjectLanguage
					.getObjectLanguageProperties(this.objectLanguage, this.metaLanguagePrefix);
			PlaceholderResolver placeholderResolver = DetectorConfigurationUtils
					.getPlaceholderResolver(this.metalanguage);
			TemplatePreprocessor templatePreprocessor = DetectorConfigurationUtils
					.getTemplatePreprocessor(this.metalanguage);
			templatePreprocessor.setTemplatesRootPath(templatesPath);

			AimPatternDetectionEngine patternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
					compilationUnits, parserClass, lexerClass, this.templateGrammarPath, metaLanguageConfiguration,
					objectLanguageProperties, placeholderResolver, templatePreprocessor);
			detectionResult = patternDetectionEngine.detect();

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		return detectionResult;
	}

	private List<Path> getCompilationUnitPaths() throws IOException {
		String regex = "";
		if (this.objectLanguage == ObjectLanguage.JAVA) {
			regex = ".+\\.java";
		}
		if (this.objectLanguage == ObjectLanguage.C) {
			regex = ".+\\.c";
		}

		if (!regex.isEmpty()) {
			return PathUtil.getAllFiles(this.compilationUnitPath, regex);
		}
		return PathUtil.getAllFiles(this.compilationUnitPath);
	}
}

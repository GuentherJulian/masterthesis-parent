package io.github.guentherjulian.masterthesis.patterndetector.detection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.AimPatternDetectionResult;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.ObjectLanguageProperties;
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

	public Detector(Path templatesPath, Path compilationUnitPath, Path templateGrammarPath, String objectLanguageString,
			String metalanguageString) {
		this.templatesPath = templatesPath;
		this.compilationUnitPath = compilationUnitPath;
		this.templateGrammarPath = templateGrammarPath;
		this.objectLanguage = ObjectLanguage.getObjectLanguage(objectLanguageString);
		this.metalanguage = MetaLanguage.getMetaLanguage(metalanguageString);
	}

	public AimPatternDetectionResult detect() {

		try {
			List<AimPattern> aimPatterns = new ArrayList<>();
			List<AimPatternTemplate> templates = PathUtil.getAimPatternTemplates(this.templatesPath);
			AimPattern aimPattern = new AimPattern();
			aimPattern.setAimPatternTemplates(templates);
			aimPatterns.add(aimPattern);

			List<Path> compilationUnits = getCompilationUnitPaths();

			Class<? extends Parser> parserClass = DetectorConfigurationUtils.getParserClass(this.objectLanguage,
					this.metalanguage);
			Class<? extends Lexer> lexerClass = DetectorConfigurationUtils.getLexerClass(this.objectLanguage,
					this.metalanguage);

			MetaLanguageConfiguration metaLanguageConfiguration = MetaLanguage
					.getMetaLanguageConfiguration(this.metalanguage);
			ObjectLanguageProperties objectLanguageProperties = ObjectLanguage
					.getObjectLanguageProperties(this.objectLanguage, null);
			PlaceholderResolver placeholderResolver;
			TemplatePreprocessor templatePreprocessor;

			// AimPatternDetectionEngine patternDetectionEngine = new
			// AimPatternDetectionEngine(aimPatterns,
			// compilationUnits, parserClass, lexerClass, templateGrammarPath,
			// metaLanguageConfiguration,
			// objectLanguageProperties, placeholderResolver, templatePreprocessor);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	private List<Path> getCompilationUnitPaths() throws IOException {
		String regex = "";
		if (this.objectLanguage.equals("Java")) {
			regex = ".+\\.java";
		}
		if (this.objectLanguage.equals("C")) {
			regex = ".+\\.c";
		}

		if (!regex.isEmpty()) {
			return PathUtil.getAllFiles(this.compilationUnitPath, regex);
		}

		return PathUtil.getAllFiles(this.compilationUnitPath);
	}

}

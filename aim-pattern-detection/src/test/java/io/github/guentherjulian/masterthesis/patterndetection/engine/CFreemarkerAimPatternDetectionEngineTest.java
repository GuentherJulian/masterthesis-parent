package io.github.guentherjulian.masterthesis.patterndetection.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.parser.cfreemarkertemplate.CFreemarkerTemplateLexer;
import org.antlr.parser.cfreemarkertemplate.CFreemarkerTemplateParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.FreeMarkerLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.FreeMarkerMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.CLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.ObjectLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.FreeMarkerPlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.FreeMarkerTemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;

public class CFreemarkerAimPatternDetectionEngineTest extends AbstractAimPatternDetectionEngineTest {

    private final String metaLangPrefix = "fm_";
    private MetaLanguagePattern metaLanguagePattern = new FreeMarkerMetaLanguagePattern();
    private MetaLanguageLexerRules metaLanguageLexerRules = new FreeMarkerLexerRuleNames();
    private MetaLanguageConfiguration metaLanguageConfiguration = new MetaLanguageConfiguration(
            this.metaLanguageLexerRules, this.metaLanguagePattern, metaLangPrefix);
    private ObjectLanguageConfiguration objectLanguageProperties = new CLanguageConfiguration(this.metaLangPrefix);
    private PlaceholderResolver placeholderResolver = new FreeMarkerPlaceholderResolver();
    private TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();

    @BeforeAll
    public static void setupTests() throws URISyntaxException {
        templatesPath = resourcesPath.resolve("templates").resolve("c_freemarker");
        compilationUnitsPath = resourcesPath.resolve("compilation-units").resolve("c");
        grammarPath = grammarPath.resolve("cFreemarkerTemplate").resolve("CFreemarkerTemplate.g4");

        parserClass = CFreemarkerTemplateParser.class;
        lexerClass = CFreemarkerTemplateLexer.class;
    }

    @Test
    void cFreeMarkerSimplePlaceholderTest() throws Exception {

        List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
        aimPatternTemplates
                .add(new AimPatternTemplate(templatesPath.resolve("PlaceholderTemplate.c"), "PlaceholderTemplate.c"));

        AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);

        List<Path> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnitsPath.resolve("SimplePlaceholderCorrect.c"));
        compilationUnits.add(compilationUnitsPath.resolve("SimplePlaceholderIncorrect.c"));

        AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPattern,
                compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
                objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);
        aimPatternDetectionEngine.setForceMatching(true);

        AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
        List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
        assertEquals(treeMatches.size(), 2);
        assertTrue(treeMatches.get(0).isMatch());
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("var1_value"));
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("var1_value").contains("42"));
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("var2"));
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("var2").contains("var2"));
        assertFalse(treeMatches.get(1).isMatch());

        long processingTime = patternDetectionResult.getProcessingTime();
        System.out
                .println(String.format("Pattern detection took %s ns, %s ms", processingTime, (processingTime / 1e6)));
    }

    @Test
    void cFreeMarkerIfElseTest() throws Exception {

        List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
        aimPatternTemplates
                .add(new AimPatternTemplate(templatesPath.resolve("IfElseTemplate.c"), "IfElseTemplate.c"));

        AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);

        List<Path> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnitsPath.resolve("IfElseCorrect1.c"));
        compilationUnits.add(compilationUnitsPath.resolve("IfElseCorrect2.c"));
        compilationUnits.add(compilationUnitsPath.resolve("IfElseIncorrect.c"));

        AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPattern,
                compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
                objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);
        aimPatternDetectionEngine.setForceMatching(true);

        AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
        List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
        assertEquals(treeMatches.size(), 3);
        assertTrue(treeMatches.get(0).isMatch());
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("condition1"));
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("condition1").contains("true"));
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("condition2"));
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("condition2").contains("false"));
        assertTrue(treeMatches.get(1).isMatch());
        assertTrue(treeMatches.get(1).getPlaceholderSubstitutions().containsKey("condition1"));
        assertTrue(treeMatches.get(1).getPlaceholderSubstitutions().get("condition1").contains("false"));
        assertFalse(treeMatches.get(2).isMatch());

        long processingTime = patternDetectionResult.getProcessingTime();
        System.out
                .println(String.format("Pattern detection took %s ns, %s ms", processingTime, (processingTime / 1e6)));
    }

    @Test
    void cFreeMarkerListTest() throws Exception {

        List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
        aimPatternTemplates
                .add(new AimPatternTemplate(templatesPath.resolve("ListTemplate.c"), "ListTemplate.c"));

        AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);

        List<Path> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnitsPath.resolve("ListCorrect.c"));

        AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPattern,
                compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
                objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);
        aimPatternDetectionEngine.setForceMatching(true);

        AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
        List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
        assertEquals(treeMatches.size(), 1);
        assertTrue(treeMatches.get(0).isMatch());
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("vars"));
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("vars").contains("i"));
        assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("vars").contains("j"));
        System.out.println(treeMatches.get(0).getPlaceholderSubstitutions());

        long processingTime = patternDetectionResult.getProcessingTime();
        System.out
                .println(String.format("Pattern detection took %s ns, %s ms", processingTime, (processingTime / 1e6)));
    }

    @Test
    void cFreeMarkeFunctionOrderingTest() throws Exception {

        List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
        aimPatternTemplates
                .add(new AimPatternTemplate(templatesPath.resolve("FunctionOrdering.c"), "FunctionOrdering.c"));

        AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);

        List<Path> compilationUnits = new ArrayList<>();
        compilationUnits.add(compilationUnitsPath.resolve("FunctionOrdering.c"));

        AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPattern,
                compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
                objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);
        aimPatternDetectionEngine.setForceMatching(true);

        AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
        List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
        assertEquals(treeMatches.size(), 1);
        assertTrue(treeMatches.get(0).isMatch());
    }
}

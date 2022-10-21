package io.github.guentherjulian.masterthesis.antlr4.templateparser.rubytemplate;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.ruby.CorundumTemplateLexer;
import org.antlr.parser.ruby.CorundumTemplateParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class RubyFreemarkerTemplateGrammarParserTest extends AbstractParserTest {

    private static Path testResourcesPath;

    private static Path grammar;

    @BeforeAll
    public static void prepare() throws URISyntaxException {
        testResourcesPath = Paths.get(RubyFreemarkerTemplateGrammarParserTest.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).getParent().getParent().resolve("src/test/resources");

        parserClass = CorundumTemplateParser.class;
        lexerClass = CorundumTemplateLexer.class;

        grammar = Paths
                .get(RubyFreemarkerTemplateGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation()
                        .toURI())
                .getParent().resolve("classes").resolve("grammars/rubyFreemarkerTemplate/CorundumTemplate.g4");
    }

    @Test
    void rubyHelloWorldTest() throws Exception {
        Path inputFile = testResourcesPath.resolve("templates/ruby_freemarker/helloworld.rb");

        TemplateParser<CorundumTemplateParser> templateParser = getTemplateParser("prog", inputFile,
                grammar);
        List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
        for (ParserRuleContext tree : trees) {
            templateParser.showTree(tree);
        }
    }
}

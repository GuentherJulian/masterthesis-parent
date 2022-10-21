package io.github.guentherjulian.masterthesis.antlr4.parser;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateParser<P extends Parser> {

    private static final Logger LOG = LogManager.getLogger(TemplateParser.class);

    private P parser;

    private Method parseRule;

    private Map<String, List<String>> listPatterns;

    private String grammar;

    public TemplateParser(P parser, Method parseRule, InputStream grammarStream) throws IOException {
        this.parser = parser;
        this.parseRule = parseRule;

        InputStreamReader inputStreamReader = new InputStreamReader(grammarStream);
        BufferedReader buffer = new BufferedReader(inputStreamReader);
        this.grammar = buffer.lines().collect(Collectors.joining("\n"));
        this.listPatterns = new ListPatternCollector(this.grammar).detectListPatternInstantiations();
    }

    public Parser getParser() {
        return parser;
    }

    public void setParser(P parser) {
        this.parser = parser;
    }

    public Method getParseRule() {
        return parseRule;
    }

    public void setParseRule(Method parseRule) {
        this.parseRule = parseRule;
    }

    public Map<String, List<String>> getListPatterns() {
        return this.listPatterns;
    }

    public ParserRuleContext parse(PredictionMode predictionMode) throws Exception {
        long start = System.nanoTime();

        PredictionMode.resetAmbiguityData();
        Method method = this.parser.getClass().getMethod(this.parseRule.getName());
        ParserRuleContext tree = (ParserRuleContext) method.invoke(this.parser);

        long end = System.nanoTime();

        LOG.info("Tree parsed in {}s", ((end - start) / Math.pow(10, 9)));
        return tree;
    }

    public List<ParserRuleContext> parseAmbiguties(PredictionMode predictionMode) throws Exception {
        return parseAmbiguties(predictionMode, true);
    }

    public List<ParserRuleContext> parseAmbiguties(PredictionMode predictionMode, boolean parseAmbiguties,
            ParseTreeListener listener) throws Exception {
        this.parser.addParseListener(listener);
        return parseAmbiguties(predictionMode, parseAmbiguties);
    }

    public List<ParserRuleContext> parseAmbiguties(PredictionMode predictionMode, boolean parseAmbiguties)
            throws Exception {
        long start = System.nanoTime();
        PredictionMode.resetAmbiguityData();

        this.parser.removeErrorListeners();
        // parser.addErrorListener(new ErrorListener());
        this.parser.getInterpreter().setPredictionMode(predictionMode);

        List<ParserRuleContext> trees = new ArrayList<>();
        int count = 0;

        do {
            Method method = this.parser.getClass().getMethod(this.parseRule.getName());
            ParserRuleContext tree = (ParserRuleContext) method.invoke(this.parser);

            trees.add(tree);
            count++;

            // printToFile(parser, count, tree);

            if (parseAmbiguties) {
                this.parser.reset();
                LOG.info("Number of ambiguities detected: " + PredictionMode.getAmbiguityCounter());
                // PredictionMode.updateAmbiguityDataForNextRun(true);
                PredictionMode.updateAmbiguityDataForNextRun();
            }
        } while (parseAmbiguties && PredictionMode.hasNextRun());

        long end = System.nanoTime();

        LOG.info("{} trees parsed in {}s", count, ((end - start) / Math.pow(10, 9)));
        return trees;
    }

    public void showTree(ParserRuleContext parseTree) throws InterruptedException, ExecutionException {
        Future<JFrame> future = Trees.inspect(parseTree, parser);
        while (future.get().isVisible()) {
            Thread.sleep(100);
        }
    }

    public void printToFile(ParserRuleContext parseTree, TreeVisualization treeVisualization) throws Exception {
        String fileName = (!parser.getSourceName().equals(IntStream.UNKNOWN_SOURCE_NAME) ? parser.getSourceName()
                : parser.getGrammarFileName());

        if (treeVisualization == TreeVisualization.EPS) {
            Trees.save(parseTree, parser, fileName + ".eps");
        }

        if (treeVisualization == TreeVisualization.JPG) {
            TreeViewer treeViewer = new TreeViewer(new ArrayList<>(Arrays.asList(parser.getRuleNames())), parseTree);
            Future<JFrame> future = treeViewer.open();
            future.get();

            BufferedImage bufferedImage = new BufferedImage(treeViewer.getPreferredSize().width,
                    treeViewer.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = bufferedImage.createGraphics();
            treeViewer.paint(graphics);
            graphics.dispose();

            ImageIO.write(bufferedImage, "png", new File(fileName + ".png"));
            future.cancel(true);
        }
    }
}

package io.github.guentherjulian.masterthesis.grammargeneration.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * A class for the analysis of input data. This class have to be instantiated
 * for each template engine
 */
public class FreeMarkerInputAnalysis {

	/** A unique prefix needed for grammar transformation */
	private static String prefix = "fm_";

	private static String[] metaRuleSuffixes = { "Opt", "Star", "Plus" };

	/** Basic FreeMarker configuration */
	private static Configuration cfg;

	static {
		cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
	}

	/**
	 * @return true if the provided input data is valid, false otherwise
	 * @param inputData           the input data to analyze
	 * @param placeholderTypeList a list of expected placeholder types, obtained
	 *                            during template analysis
	 * @param objectLangLexer     a lexer of the ordinary object grammar (not of the
	 *                            template grammar)
	 * @param objectLangParser    a parser of the ordinary object grammar (not of
	 *                            the template grammar)
	 */
	public static boolean isValidInput(Object inputData, List<Map<String, String>> placeholderTypeList,
			Lexer objectLangLexer, Parser objectLangParser) {

		// template without placeholder is valid
		if (placeholderTypeList == null) {
			return true;
		}

		// get actual values from input data
		Map<String, String> placeholderValuesList = new HashMap<>();
		for (String placehoderName : placeholderTypeList.get(0).keySet()) {
			try {
				placeholderValuesList.put(placehoderName, getActualValue(placehoderName, inputData));
			} catch (Exception e) {
				// e.printStackTrace();
				return false;
			}
		}

		for (Map<String, String> typeMap : placeholderTypeList) {
			boolean isValid = true;
			for (String placehoderName : typeMap.keySet()) {
				String expectedRuleName = typeMap.get(placehoderName).replaceFirst(prefix, "");
				for (String suffix : metaRuleSuffixes) {
					if (expectedRuleName.endsWith(suffix)) {
						expectedRuleName = expectedRuleName.substring(0, expectedRuleName.length() - suffix.length());
						break;
					}
				}
				String actualData = placeholderValuesList.get(placehoderName);
				System.out.println(expectedRuleName + " : " + actualData);

				if (Character.isUpperCase(expectedRuleName.charAt(0))) {
					objectLangLexer.setInputStream(new ANTLRInputStream(actualData));
					CommonTokenStream tokens = new CommonTokenStream(objectLangLexer);
					tokens.fill();
					List<Token> tokenList = tokens.getTokens();
					if (tokenList.size() != 2) {
						isValid = false;
						break;
					} else {
						Token actualToken = tokenList.get(0);
						String actualTokenName = objectLangParser.getVocabulary()
								.getSymbolicName(actualToken.getType());
						if (!actualTokenName.equals(expectedRuleName)) {
							System.out.println(
									actualData + " is " + actualTokenName + " but expected " + expectedRuleName);
							isValid = false;
							break;
						}

					}
				} else {
					// it is a parser rule
					objectLangLexer.setInputStream(new ANTLRInputStream(actualData));
					CommonTokenStream tokens = new CommonTokenStream(objectLangLexer);
					objectLangParser.setTokenStream(tokens);
					objectLangParser.removeErrorListeners();
					objectLangParser.addErrorListener(new ErrorListener());
					try {
						Method parsingMethod = objectLangParser.getClass().getMethod(expectedRuleName);
						parsingMethod.invoke(objectLangParser);
					} catch (Exception e) {
						// e.printStackTrace();
						if (e instanceof InvocationTargetException) {
							System.out.println(((ANTLRParseException) e.getCause()).getMsg());
						}
						isValid = false;
						break;
					}

					tokens.fill();
					if (!(objectLangParser.getCurrentToken() == tokens.getTokens()
							.get(tokens.getTokens().size() - 1))) {
						isValid = false;
						break;
					}
				}

			}
			if (isValid) {
				return true;
			}

		}
		return false;

	}

	/**
	 * An internal method to retrieve the actual data from the input data. This
	 * method has to defined manually for each template evaluator
	 *
	 * @param placehoderName the placeholder to retrieve the actual value for
	 * @return the actual value for a certain placeholder name
	 * @param inputData the input data to retrieve the actual value from
	 */
	private static String getActualValue(String placehoderName, Object inputData)
			throws IOException, TemplateException {

		Template template = new Template("temp", new StringReader(placehoderName), cfg);
		try (StringWriter out = new StringWriter()) {
			template.process(inputData, out);
			return out.toString();
		}

	}
}

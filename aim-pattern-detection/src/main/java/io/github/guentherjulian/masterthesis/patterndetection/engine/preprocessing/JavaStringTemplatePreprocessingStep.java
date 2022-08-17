package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaStringTemplatePreprocessingStep extends AbstractPreprocessingStep {

	private static final String REGEX_TOKEN_IF = ".*<if[ ]*\\((.+)\\)[ ]*\\>.*";
	private static final String REGEX_TOKEN_IF_ELSE = ".*<elseif[ ]*\\((.+)\\)[ ]*\\>.*";
	private static final String REGEX_TOKEN_ELSE = ".*<else>.*";
	private static final String REGEX_TOKEN_IF_CLOSE = ".*<endif>.*";
	private static final String REGEX_TOKEN_LIST = ".*(<(.+):[ ]*\\{[ ]*(.+)[ ]*\\|).*";

	private Pattern ifTokenPattern;
	private Pattern ifelseTokenPattern;
	private Pattern elseTokenPattern;
	private Pattern ifCloseTokenPattern;
	private Pattern listTokenPattern;

	public JavaStringTemplatePreprocessingStep(Path input) throws Exception {
		super(input);

		this.ifTokenPattern = Pattern.compile(REGEX_TOKEN_IF);
		this.ifelseTokenPattern = Pattern.compile(REGEX_TOKEN_IF_ELSE);
		this.elseTokenPattern = Pattern.compile(REGEX_TOKEN_ELSE);
		this.ifCloseTokenPattern = Pattern.compile(REGEX_TOKEN_IF_CLOSE);
		this.listTokenPattern = Pattern.compile(REGEX_TOKEN_LIST);
	}

	@Override
	public String process(String lineToProcess) {
		String returnValue = lineToProcess;

		Matcher matcher = this.ifTokenPattern.matcher(returnValue);
		if (matcher.find()) {
			String token = matcher.group(0);
			String condition = matcher.group(1);
			returnValue = returnValue.replace(token, "#if(" + condition + ")");
		}

		matcher = this.ifelseTokenPattern.matcher(returnValue);
		if (matcher.find()) {
			String token = matcher.group(0);
			String condition = matcher.group(1);
			returnValue = returnValue.replace(token, "#ifelse(" + condition + ")");
		}

		matcher = this.elseTokenPattern.matcher(returnValue);
		if (matcher.find()) {
			String token = matcher.group(0);
			returnValue = returnValue.replace(token, "#else");
		}

		matcher = this.ifCloseTokenPattern.matcher(returnValue);
		if (matcher.find()) {
			String token = matcher.group(0);
			returnValue = returnValue.replace(token, "#endif");
		}

		matcher = this.listTokenPattern.matcher(returnValue);
		if (matcher.find()) {
			String token = matcher.group(0);
			// String collectionVariable = matcher.group(1);
			// String iterationVariable = matcher.group(2);
			String replacedToken = token.replace("<", "#").replace("{", "");
			returnValue = returnValue.replace(token, replacedToken);
		}

		return returnValue;
	}

}

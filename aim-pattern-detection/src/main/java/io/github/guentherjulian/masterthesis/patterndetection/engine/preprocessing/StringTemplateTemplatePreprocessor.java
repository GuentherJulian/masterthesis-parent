package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTemplateTemplatePreprocessor extends AbstractTemplatePreprocessor {

	private final String regexTokenIf = ".*<if[ ]*\\((.+)\\)[ ]*\\>.*";
	private final String regexTokenIfElse = ".*<elseif[ ]*\\((.+)\\)[ ]*\\>.*";
	private final String regexTokenElse = ".*<else>.*";
	private final String regexTokenIfClose = ".*<endif>.*";
	private final String regexTokenList = ".*(<(.+):[ ]*\\{[ ]*(.+)[ ]*\\|).*";

	private final Pattern ifTokenPattern = Pattern.compile(regexTokenIf);
	private final Pattern ifelseTokenPattern = Pattern.compile(regexTokenIfElse);
	private final Pattern elseTokenPattern = Pattern.compile(regexTokenElse);
	private final Pattern ifCloseTokenPattern = Pattern.compile(regexTokenIfClose);
	private final Pattern listTokenPattern = Pattern.compile(regexTokenList);

	@Override
	public String processTemplateLine(String lineToProcess) {
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

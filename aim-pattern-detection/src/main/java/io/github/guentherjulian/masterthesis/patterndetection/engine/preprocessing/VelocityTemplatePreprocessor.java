package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.velocity.VelocityMacro;
import io.github.guentherjulian.masterthesis.patterndetection.exception.PreprocessingException;

public class VelocityTemplatePreprocessor extends AbstractTemplatePreprocessor {

	private final String regexContainsPlaceholder = "\\$(.+)";
	private final String regexPlaceholder = ".*(\\$(.+))";
	private final Pattern patternContainsPlaceholder = Pattern.compile(regexContainsPlaceholder);
	private final Pattern patternPlaceholder = Pattern.compile(regexPlaceholder);

	private List<VelocityMacro> macros;

	@Override
	public String processTemplateLine(String lineToProcess) {
		String returnValue = lineToProcess;

		Matcher matcher = patternContainsPlaceholder.matcher(returnValue);
		if (matcher.find()) {
			returnValue = replacePlaceholder(returnValue);
		}

		return returnValue;
	}

	@Override
	protected byte[] preprocess(Path templatePath, byte[] templateByteArray)
			throws PreprocessingException, IOException {
		byte[] preprocessedByteArray = resolveIncludesAndAssignments(templatePath, templateByteArray);
		this.macros = findMacros(preprocessedByteArray);
		preprocessedByteArray = replaceMacros(preprocessedByteArray, this.macros);
		return preprocessedByteArray;
	}

	private List<VelocityMacro> findMacros(byte[] templateByteArray) throws IOException {
		String regexMacroStart = ".*#macro\\((.+?)( .+)*\\).*";
		String regexMacroEnd = ".*#end.*";
		Pattern macroStartPattern = Pattern.compile(regexMacroStart);
		Pattern macroEndPattern = Pattern.compile(regexMacroEnd);

		ByteArrayOutputStream writer = null;
		Stack<VelocityMacro> macros = new Stack<>();
		Stack<Boolean> isMacroTerminalSymbol = new Stack<>();
		String[] lines = new String(templateByteArray).split(System.lineSeparator());
		for (String line : lines) {
			Matcher matcher = macroStartPattern.matcher(line);
			if (matcher.find()) {
				macros.push(new VelocityMacro());
				isMacroTerminalSymbol.push(true);
				String macroName = matcher.group(1);
				macros.peek().setMacroName(macroName.trim());

				String macroParamString = matcher.group(2);
				if (macroParamString != null) {
					// TODO fix, would cause an error if an whitespace is in variable value
					String[] macroParams = macroParamString.trim().split(" ");
					for (String param : macroParams) {
						String[] paramSplitted = param.split("=");
						if (paramSplitted.length == 1) {
							macros.peek().getParameters().put(paramSplitted[0], null);
						} else {
							if (paramSplitted[1].endsWith(",")) {
								paramSplitted[1] = paramSplitted[1].substring(0, paramSplitted[1].length() - 2);
							}
							paramSplitted[1] = paramSplitted[1].replace("\"", "").replace("'", "");
							macros.peek().getParameters().put(paramSplitted[0], paramSplitted[1]);
						}
					}
				}

				writer = new ByteArrayOutputStream();
			} else {
				matcher = macroEndPattern.matcher(line);
				if (matcher.find()) {
					if (isMacroTerminalSymbol.pop()) {
						macros.peek().setMacroContent(new String(writer.toByteArray()));
						writer = null;
					}
				} else {
					if (line.trim().startsWith("#if") || line.trim().startsWith("#foreach")) {
						isMacroTerminalSymbol.push(false);
					}
					if (writer != null) {
						writer.write(line.getBytes());
						writer.write(System.lineSeparator().getBytes());
					}
				}
			}
		}
		return new ArrayList<>(macros);
	}

	private byte[] replaceMacros(byte[] preprocessedByteArray, List<VelocityMacro> macros)
			throws IOException, PreprocessingException {
		String regexMacroStart = ".*#macro\\((.+?)( .+)*\\).*";
		String regexMacroEnd = ".*#end.*";
		Pattern macroStartPattern = Pattern.compile(regexMacroStart);
		Pattern macroEndPattern = Pattern.compile(regexMacroEnd);
		boolean isMacro = false;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		String[] lines = new String(preprocessedByteArray).split(System.lineSeparator());
		for (String line : lines) {
			VelocityMacro macroFound = null;
			Matcher matcherMacroFound = null;
			for (VelocityMacro macro : macros) {
				String regexMacroWithoutBody = ".*#" + macro.getMacroName() + "\\((.*)\\)";
				Pattern patternMacroWithoutBody = Pattern.compile(regexMacroWithoutBody);

				Matcher matcher = patternMacroWithoutBody.matcher(line);
				if (matcher.find()) {
					macroFound = macro;
					matcherMacroFound = matcher;
					break;
				}
			}

			if (macroFound != null) {
				String macroParamString = matcherMacroFound.group(1).trim();
				if (macroParamString != null) {
					// TODO fix, would cause an error if an whitespace is in variable value
					String[] params = macroParamString.trim().split(" ");
					int i = 0;
					for (Entry<String, String> parameterEntry : macroFound.getParameters().entrySet()) {
						String value = params[i++].replace("\"", "").replace("'", "");
						parameterEntry.setValue(value);
					}
				}

				String macroText = macroFound.getMacroContent();
				String[] macroLines = macroText.split(System.lineSeparator());
				for (String macroLine : macroLines) {
					String processedLine = macroLine;
					for (Entry<String, String> parameter : macroFound.getParameters().entrySet()) {
						String param = parameter.getKey();
						if (param.startsWith("$")) {
							param = param.substring(1);
						}
						processedLine = processedLine.replaceAll("\\$\\{" + param + "\\}", parameter.getValue());
						processedLine = processedLine.replaceAll("\\$" + param, parameter.getValue());
					}
					byteArrayOutputStream.write(processedLine.getBytes());
					byteArrayOutputStream.write(System.lineSeparator().getBytes());
				}
			} else {
				Matcher matcher = macroStartPattern.matcher(line);
				if (matcher.find()) {
					isMacro = true;
				}

				if (!isMacro) {
					byteArrayOutputStream.write(line.getBytes());
					byteArrayOutputStream.write(System.lineSeparator().getBytes());
				}

				matcher = macroEndPattern.matcher(line);
				if (matcher.find()) {
					isMacro = false;
				}
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	private byte[] resolveIncludesAndAssignments(Path templatePath, byte[] templateByteArray)
			throws PreprocessingException, IOException {
		String regexInclude = ".*#include\\((.+)\\).*";
		Pattern includePattern = Pattern.compile(regexInclude);
		String regexParse = ".*#parse\\((.+)\\).*";
		Pattern parsePattern = Pattern.compile(regexParse);

		String regexSet = ".*#set\\((.+)\\).*";
		Pattern setPattern = Pattern.compile(regexSet);

		String[] lines = new String(templateByteArray).split(System.lineSeparator());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		for (String line : lines) {
			String includeFiles = "";
			Matcher matcher = includePattern.matcher(line);
			if (matcher.find()) {
				includeFiles = matcher.group(1);
			} else {
				matcher = parsePattern.matcher(line);
				if (matcher.find()) {
					includeFiles = matcher.group(1);
				}
			}

			if (!includeFiles.isEmpty()) {
				if (includeFiles != null && !includeFiles.trim().isEmpty()) {
					String[] files = includeFiles.trim().split(",");
					for (int i = 0; i < files.length; i++) {
						files[i] = files[i].trim();
						if ((files[i].startsWith("\"") && files[i].endsWith("\""))
								|| (files[i].startsWith("'") && files[i].endsWith("'"))) {
							files[i] = files[i].substring(1, files[i].length() - 1);
						}

						Path referencedFile = getReferencedFile(templatePath, files[i]);
						if (!Files.exists(referencedFile)) {
							throw new PreprocessingException(
									String.format("Error while including external file! File %s could not be found.",
											referencedFile.toString()));
						}
						byte[] referencedFileByteArray = getFileBytes(referencedFile);
						referencedFileByteArray = resolveIncludesAndAssignments(templatePath, referencedFileByteArray);
						String[] referencedFileLines = new String(referencedFileByteArray)
								.split(System.lineSeparator());
						for (String referencedFileLine : referencedFileLines) {
							byteArrayOutputStream.write(referencedFileLine.getBytes());
							byteArrayOutputStream.write(System.lineSeparator().getBytes());
						}
					}
				}
			} else {
				matcher = setPattern.matcher(line);
				if (matcher.find()) {
					String variablesString = matcher.group(1);
					// TODO fix, would cause an error if an whitespace is in variable value
					String[] variables = variablesString.trim().split(" ");
					for (String variable : variables) {
						String varName = variable.split("=")[0];
						String varValue = variable.split("=")[1];
						if ((varValue.startsWith("\"") && varValue.endsWith("\""))
								|| (varValue.startsWith("'") && varValue.endsWith("'"))) {
							varValue = varValue.substring(1, varValue.length() - 1);
						}
						varName = varName.replaceAll("\\$", "").replaceAll("\\{", "").replaceAll("\\}", "");
						this.addVariable(varName, varValue);
					}
				} else {
					byteArrayOutputStream.write(line.getBytes());
				}
			}

			byteArrayOutputStream.write(System.lineSeparator().getBytes());
		}
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	protected Path getReferencedFile(Path templatePath, String includeFile) {
		return Paths.get(this.templatesRootPath.resolve(includeFile).toUri());
	}

	private String replacePlaceholder(String str) {
		String replacedString = str;
		String[] arr = replacedString.split(" ");
		boolean isIfStatement = false;
		boolean isIfElseStatement = false;
		boolean isForeachStatement = false;

		isIfStatement = str.trim().startsWith("#if");
		isIfElseStatement = str.trim().startsWith("#elseif");
		isForeachStatement = str.trim().startsWith("#foreach");

		for (int i = 0; i < arr.length; i++) {
			if (!isIfStatement && !isIfElseStatement && !isForeachStatement) {
				Matcher matcherPlaceholder = patternPlaceholder.matcher(arr[i]);
				boolean matchFound = matcherPlaceholder.find();

				if (matchFound) {
					if (!isListPattern(arr[i])) {
						String match = matcherPlaceholder.group(0);
						String placeholder = matcherPlaceholder.group(1);
						String innerPlaceholder = matcherPlaceholder.group(2);
						String replacedPlaceholder;
						// $a --> ${a}
						if (innerPlaceholder.endsWith(";") || innerPlaceholder.endsWith(")")) {
							replacedPlaceholder = "${" + innerPlaceholder.substring(0, innerPlaceholder.length() - 1)
									+ "}" + innerPlaceholder.charAt(innerPlaceholder.length() - 1);
						} else {
							replacedPlaceholder = "${" + innerPlaceholder + "}";
						}

						arr[i] = match.replace(placeholder, replacedPlaceholder);
					} else {
						String[] listPatternParts = arr[i].split("\\.");
						for (int j = 0; j < listPatternParts.length; j++) {
							if (listPatternParts[j].startsWith("$")) {
								if (listPatternParts[j].endsWith(";")) {
									listPatternParts[j] = "${"
											+ listPatternParts[j].substring(1, listPatternParts[j].length() - 1) + "};";
								} else {
									listPatternParts[j] = "${" + listPatternParts[j].substring(1) + "}";
								}
							}
						}
						arr[i] = String.join(".", listPatternParts);
					}
				}
			}
		}
		replacedString = String.join(" ", arr);

		return replacedString;
	}

	private boolean isListPattern(String str) {
		return str.chars().filter(c -> c == '$').count() > 1;
	}
}

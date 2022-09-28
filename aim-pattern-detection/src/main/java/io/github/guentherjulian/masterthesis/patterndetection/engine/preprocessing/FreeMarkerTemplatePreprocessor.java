package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RegExUtils;

import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.freemarker.FreeMarkerMacro;
import io.github.guentherjulian.masterthesis.patterndetection.exception.PreprocessingException;

public class FreeMarkerTemplatePreprocessor extends AbstractTemplatePreprocessor {

	private final String regexPlaceholder = "\\$\\{#(.+?)#\\}";
	private final String regexPrefix = ".*?((\\w+)\\$\\{#(.+)#\\}).*";
	private final String regexSuffix = ".*((\\$\\{#(.+)#\\})(\\w+)).*";
	private final String regexStringWithPlaceholder = ".*((\\\".*)\\$\\{#(.+?)#\\}(.*\\\")).*";

	private final Pattern placeholderPattern = Pattern.compile(regexPlaceholder);
	private final Pattern prefixPattern = Pattern.compile(regexPrefix);
	private final Pattern suffixPattern = Pattern.compile(regexSuffix);
	private final Pattern stringWithPlaceholderPattern = Pattern.compile(regexStringWithPlaceholder);

	private List<FreeMarkerMacro> macros;

	@Override
	public String processTemplateLine(String lineToProcess) throws PreprocessingException {
		String returnValue = lineToProcess;

		returnValue = RegExUtils.replaceAll(returnValue, "\\$\\{(.+?)\\}", "\\$\\{#$1#\\}");

		boolean prefixFound = true;
		do {
			Matcher matcher = this.prefixPattern.matcher(returnValue);
			prefixFound = matcher.find();

			if (prefixFound) {
				String combinedPlaceholder = matcher.group(1);
				String prefix = matcher.group(2);
				String innerPlaceholder = matcher.group(3);

				String replacedValue = "${#'" + prefix + "' + " + innerPlaceholder + "#}";
				returnValue = returnValue.replace(combinedPlaceholder, replacedValue);
			}
		} while (prefixFound);

		boolean suffixFound = true;
		do {
			Matcher matcher = this.suffixPattern.matcher(returnValue);
			suffixFound = matcher.find();

			if (suffixFound) {
				String combinedPlaceholder = matcher.group(1);
				String innerPlaceholder = matcher.group(3);
				String suffix = matcher.group(4);

				String replacedValue = "${#" + innerPlaceholder + " + '" + suffix + "'#}";
				returnValue = returnValue.replace(combinedPlaceholder, replacedValue);
			}
		} while (suffixFound);

		boolean stringWithPlaceholderFound = true;
		do {
			Matcher matcher = this.stringWithPlaceholderPattern.matcher(returnValue);
			stringWithPlaceholderFound = matcher.find();

			if (stringWithPlaceholderFound) {
				String stringWithPlaceholder = matcher.group(1);

				Matcher matcherPlaceholder = this.placeholderPattern.matcher(stringWithPlaceholder);
				List<String> placeholderList = new LinkedList<>();
				while (matcherPlaceholder.find()) {
					placeholderList.add(matcherPlaceholder.group(1));
				}

				if (placeholderList.size() > 1) {
					String[] stringElementsWithoutPlaceholder = stringWithPlaceholder
							.substring(1, stringWithPlaceholder.length() - 1).split(this.regexPlaceholder);
					String replaceValue = "";
					for (int i = 0; i < stringElementsWithoutPlaceholder.length; i++) {
						if (!stringElementsWithoutPlaceholder[i].isEmpty()) {
							if (replaceValue.isEmpty()) {
								replaceValue = "'" + stringElementsWithoutPlaceholder[i] + "' + "
										+ placeholderList.get(i);
							} else {
								replaceValue = replaceValue + " + '" + stringElementsWithoutPlaceholder[i] + "' + "
										+ placeholderList.get(i);
							}
						} else {
							replaceValue = replaceValue + placeholderList.get(i);
						}
					}

					replaceValue = "${#" + replaceValue + "#}";
					returnValue = returnValue.replace(stringWithPlaceholder, replaceValue);
				} else {
					String prefix = matcher.group(2) != null ? matcher.group(2) : "";
					String innerPlaceholder = matcher.group(3);
					String suffix = matcher.group(4) != null ? matcher.group(4) : "";

					String replacedValue = "${#" + (!prefix.isEmpty() ? "'" + prefix + "' + " : "") + innerPlaceholder
							+ (!suffix.isEmpty() ? " + '" + suffix + "'" : "") + "#}";
					returnValue = returnValue.replace(stringWithPlaceholder, replacedValue);
				}
			}
		} while (stringWithPlaceholderFound);

		return returnValue;
	}

	@Override
	protected byte[] preprocess(Path templatePath, byte[] templateByteArray)
			throws PreprocessingException, IOException {
		byte[] preprocessedByteArray = resolveIncludesAndAssignments(templatePath, templateByteArray);
		preprocessedByteArray = removeUnnecessaryLines(preprocessedByteArray);
		this.macros = findMacros(preprocessedByteArray);
		preprocessedByteArray = replaceMacros(preprocessedByteArray, this.macros);
		return preprocessedByteArray;
	}

	private byte[] resolveIncludesAndAssignments(Path templatePath, byte[] templateByteArray)
			throws PreprocessingException, IOException {
		String regexInclude = ".*<#include (.+)>.*";
		Pattern includePattern = Pattern.compile(regexInclude);

		String regexAssign = ".*<#assign( .+)*>.*";
		Pattern assignPattern = Pattern.compile(regexAssign);

		String[] lines = new String(templateByteArray).split(System.lineSeparator());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		for (String line : lines) {
			Matcher matcher = includePattern.matcher(line);
			if (matcher.find()) {
				String include = matcher.group(1);
				Path referencedFile = getReferencedFile(templatePath, include);
				if (!Files.exists(referencedFile)) {
					throw new PreprocessingException(
							String.format("Error while including external file! File %s could not be found.",
									referencedFile.toString()));
				}
				byte[] referencedFileByteArray = getFileBytes(referencedFile);
				referencedFileByteArray = resolveIncludesAndAssignments(templatePath, referencedFileByteArray);
				String[] referencedFileLines = new String(referencedFileByteArray).split(System.lineSeparator());
				for (String referencedFileLine : referencedFileLines) {
					byteArrayOutputStream.write(referencedFileLine.getBytes());
					byteArrayOutputStream.write(System.lineSeparator().getBytes());
				}
			} else {
				matcher = assignPattern.matcher(line);
				if (matcher.find()) {
					String variablesString = matcher.group(1);
					// TODO fix, would cause an error if an whitespace is in variable value
					String[] variables = variablesString.trim().split(" ");
					for (int i = 0; i < variables.length; i++) {
						String varName = "";
						String varValue = "";
						if (variables[i].contains("=")) {
							varName = variables[i].split("=")[0];
							varValue = variables[i].split("=")[1];
						} else {
							varName = variables[i];
							varValue = variables[i + 2];
							i = i + 2;
						}

						if ((varValue.startsWith("\"") && varValue.endsWith("\""))
								|| (varValue.startsWith("'") && varValue.endsWith("'"))) {
							varValue = varValue.substring(1, varValue.length() - 1);
						}
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

	private byte[] removeUnnecessaryLines(byte[] templateByteArray) throws PreprocessingException, IOException {
		String[] lines = new String(templateByteArray).split(System.lineSeparator());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		boolean isComment = false;
		for (String line : lines) {
			if (line.startsWith("<#--")) {
				isComment = true;
			}

			if (!isComment) {
				// Check if there is a comment in the line
				String lineWithoutComment = line;
				if (line.contains("<#--") && line.contains("-->")) {
					lineWithoutComment = line.replaceAll("<#\\-\\-.+\\-\\->", "");
				}

				if (!lineWithoutComment.trim().equals("<#compress>")
						&& !lineWithoutComment.trim().equals("</#compress>")) {
					byteArrayOutputStream.write(lineWithoutComment.getBytes());
					byteArrayOutputStream.write(System.lineSeparator().getBytes());
				}
			}

			if (line.startsWith("-->") || line.endsWith("-->")) {
				isComment = false;
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	private List<FreeMarkerMacro> findMacros(byte[] templateByteArray) throws IOException {
		String regexMacroStart = ".*<#macro (.+?)( .+)*>.*";
		String regexMacroEnd = ".*</#macro>.*";
		Pattern macroStartPattern = Pattern.compile(regexMacroStart);
		Pattern macroEndPattern = Pattern.compile(regexMacroEnd);

		ByteArrayOutputStream writer = null;
		Stack<FreeMarkerMacro> macros = new Stack<>();
		String[] lines = new String(templateByteArray).split(System.lineSeparator());
		for (String line : lines) {
			Matcher matcher = macroStartPattern.matcher(line);
			if (matcher.find()) {
				macros.push(new FreeMarkerMacro());
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
								paramSplitted[1] = paramSplitted[1].substring(0, paramSplitted[1].length() - 1);
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
					macros.peek().setMacroContent(new String(writer.toByteArray()));
					writer = null;
				} else {
					if (writer != null) {
						writer.write(line.getBytes());
						writer.write(System.lineSeparator().getBytes());
					}
				}
			}
		}
		return new ArrayList<>(macros);
	}

	private byte[] replaceMacros(byte[] preprocessedByteArray, List<FreeMarkerMacro> macros)
			throws IOException, PreprocessingException {
		String regexMacro = ".*(<@(.+?)( .+)*\\/>).*";
		Pattern macroPattern = Pattern.compile(regexMacro);

		String regexMacroStart = ".*<#macro (.+?)( .+)*>.*";
		String regexMacroEnd = ".*</#macro>.*";
		Pattern macroStartPattern = Pattern.compile(regexMacroStart);
		Pattern macroEndPattern = Pattern.compile(regexMacroEnd);
		boolean isMacro = false;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		String[] lines = new String(preprocessedByteArray).split(System.lineSeparator());
		for (String line : lines) {
			Matcher matcher = macroPattern.matcher(line);
			if (matcher.find()) {
				String macroName = matcher.group(2).trim();
				List<FreeMarkerMacro> foundMacros = macros.stream().filter(m -> m.getMacroName().equals(macroName))
						.collect(Collectors.toList());
				if (foundMacros.size() == 1) {
					String macroParamString = matcher.group(3);
					if (macroParamString != null) {
						// TODO fix, would cause an error if an whitespace is in variable value
						String[] params = macroParamString.trim().split(" ");
						for (int i = 0; i < params.length; i++) {
							String key = "";
							String value = "";
							if (params[i].contains("=")) {
								key = params[i].split("=")[0];
								value = params[i].split("=")[1];
							} else {
								key = (String) foundMacros.get(0).getParameters().keySet().toArray()[i];
								value = params[i];
							}

							if (value.endsWith(",")) {
								value = value.substring(0, value.length() - 2);
							}
							value = value.replace("\"", "").replace("'", "");
							foundMacros.get(0).getParameters().put(key, value);
						}
					}

					String macroText = foundMacros.get(0).getMacroContent();
					String[] macroLines = macroText.split(System.lineSeparator());
					for (String macroLine : macroLines) {
						String processedLine = macroLine;
						for (Entry<String, String> parameter : foundMacros.get(0).getParameters().entrySet()) {
							processedLine = processedLine.replaceAll("\\$\\{" + parameter.getKey() + "\\}",
									parameter.getValue());
						}
						byteArrayOutputStream.write(processedLine.getBytes());
						byteArrayOutputStream.write(System.lineSeparator().getBytes());
					}
				} else {
					throw new PreprocessingException(String.format("Error whild replacing macro '%s'", macroName));
				}
			} else {
				matcher = macroStartPattern.matcher(line);
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
}

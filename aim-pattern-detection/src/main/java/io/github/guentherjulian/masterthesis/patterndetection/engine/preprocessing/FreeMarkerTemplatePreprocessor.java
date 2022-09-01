package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.freemarker.Macro;
import io.github.guentherjulian.masterthesis.patterndetection.exception.PreprocessingException;

public class FreeMarkerTemplatePreprocessor extends AbstractTemplatePreprocessor {

	private final String regexPrefix = ".*((\\w+)\\$\\{(.+)\\}).*";
	private final String regexSuffix = ".*((\\$\\{(.+)\\})(\\w+)).*";

	private final Pattern prefixPattern = Pattern.compile(regexPrefix);
	private final Pattern suffixPattern = Pattern.compile(regexSuffix);

	private List<Macro> macros;

	@Override
	public String processTemplateLine(String lineToProcess) throws PreprocessingException {
		String returnValue = lineToProcess;

		boolean prefixFound = true;
		do {
			Matcher matcher = this.prefixPattern.matcher(returnValue);
			prefixFound = matcher.find();

			if (prefixFound) {
				String combinedPlaceholder = matcher.group(1);
				String prefix = matcher.group(2);
				String innerPlaceholder = matcher.group(3);

				String replacedValue = "${'" + prefix + "' + " + innerPlaceholder + "}";
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

				String replacedValue = "${" + innerPlaceholder + " + '" + suffix + "'}";
				returnValue = returnValue.replace(combinedPlaceholder, replacedValue);
			}
		} while (suffixFound);

		return returnValue;
	}

	@Override
	protected byte[] preprocess(Path templatePath, byte[] templateByteArray)
			throws PreprocessingException, IOException {
		byte[] preprocessedByteArray = resolveIncludesAndAssigns(templatePath, templateByteArray);
		this.macros = findMacros(preprocessedByteArray);
		preprocessedByteArray = replaceMacros(preprocessedByteArray, this.macros);
		return preprocessedByteArray;
	}

	private byte[] resolveIncludesAndAssigns(Path templatePath, byte[] templateByteArray)
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
				referencedFileByteArray = resolveIncludesAndAssigns(templatePath, referencedFileByteArray);
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
					for (String variable : variables) {
						String varName = variable.split("=")[0];
						String varValue = variable.split("=")[1];
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

	private List<Macro> findMacros(byte[] templateByteArray) throws IOException {
		String regexMacroStart = ".*<#macro (.+?)( .+)*>.*";
		String regexMacroEnd = ".*</#macro>.*";
		Pattern macroStartPattern = Pattern.compile(regexMacroStart);
		Pattern macroEndPattern = Pattern.compile(regexMacroEnd);

		ByteArrayOutputStream writer = null;
		Stack<Macro> macros = new Stack<>();
		String[] lines = new String(templateByteArray).split(System.lineSeparator());
		for (String line : lines) {
			Matcher matcher = macroStartPattern.matcher(line);
			if (matcher.find()) {
				macros.push(new Macro());
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
					macros.peek().setMacroText(new String(writer.toByteArray()));
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

	private byte[] replaceMacros(byte[] preprocessedByteArray, List<Macro> macros)
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
				List<Macro> foundMacros = macros.stream().filter(m -> m.getMacroName().equals(macroName))
						.collect(Collectors.toList());
				if (foundMacros.size() == 1) {
					String macroParamString = matcher.group(3);
					if (macroParamString != null) {
						// TODO fix, would cause an error if an whitespace is in variable value
						String[] params = macroParamString.trim().split(" ");
						for (String param : params) {
							String key = param.split("=")[0];
							String value = param.split("=")[1];
							if (value.endsWith(",")) {
								value = value.substring(0, value.length() - 2);
							}
							value = value.replace("\"", "").replace("'", "");
							foundMacros.get(0).getParameters().put(key, value);
						}
					}

					String macroText = foundMacros.get(0).getMacroText();
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
				}

				matcher = macroEndPattern.matcher(line);
				if (matcher.find()) {
					isMacro = false;
				}
			}
			byteArrayOutputStream.write(System.lineSeparator().getBytes());
		}
		return byteArrayOutputStream.toByteArray();
	}
}

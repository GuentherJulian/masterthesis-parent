package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolutionResult;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.utils.MathUtil;
import io.github.guentherjulian.masterthesis.patterndetection.exception.NoMatchException;
import io.github.guentherjulian.masterthesis.patterndetection.exception.PlaceholderClashException;
import io.github.guentherjulian.masterthesis.patterndetection.exception.PlaceholderResolutionException;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ListType;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.MetaLanguageElement;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTree;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreeElement;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePath;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePathList;

public class ParseTreeMatcher {

	private static final Logger LOGGER = LogManager.getLogger(ParseTreeMatcher.class);

	private TreeMatch treeMatch;
	private ParseTree aimPatternTemplateParseTree;
	private ParseTree compilationUnitParseTree;
	private Map<String, Set<String>> placeholderSubstitutions;
	private List<Match> matches;
	private MetaLanguageConfiguration metaLanguageConfiguration;
	private PlaceholderResolver placeholderResolver;
	private boolean flagMatchedByListPlaceholder = false;
	private Stack<String> listPlaceholderCollectionVariable = new Stack<>();
	private Stack<String> listPlaceholderIterationVariable = new Stack<>();

	public ParseTreeMatcher(ParseTree compilationUnitParseTree, ParseTree aimPatternTemplateParseTree,
			MetaLanguageConfiguration metaLanguageConfiguration, PlaceholderResolver placeholderResolver) {
		this.compilationUnitParseTree = compilationUnitParseTree;
		this.aimPatternTemplateParseTree = aimPatternTemplateParseTree;
		this.metaLanguageConfiguration = metaLanguageConfiguration;
		this.placeholderResolver = placeholderResolver;
	}

	public TreeMatch match(Map<String, Set<String>> placeholderSubstitutions) {
		long startTime = System.nanoTime();

		this.matches = new ArrayList<>();
		if (placeholderSubstitutions != null && placeholderSubstitutions.isEmpty()) {
			this.placeholderSubstitutions = placeholderSubstitutions;
		} else {
			this.placeholderSubstitutions = new HashMap<>();
		}

		this.treeMatch = new TreeMatch();
		this.treeMatch.setTemplateParseTree(this.aimPatternTemplateParseTree);
		this.treeMatch.setCompilationUnitParseTree(this.compilationUnitParseTree);
		this.treeMatch.setPlaceholderSubstitutions(this.placeholderSubstitutions);
		this.treeMatch.setMatches(this.matches);
		this.treeMatch.setMatch(true);

		try {
			matchPathList(this.aimPatternTemplateParseTree.getParseTreePathList(),
					this.compilationUnitParseTree.getParseTreePathList());
		} catch (RuntimeException e) {
			// e.printStackTrace();
			LOGGER.error(e.getMessage());
		} catch (NoMatchException e) {
			this.treeMatch.setMatch(false);
			this.treeMatch.setException(e);
		}

		long endTime = System.nanoTime();
		LOGGER.info("Finished matching parse trees (took {} ns, {} ms)", (endTime - startTime),
				((endTime - startTime) / 1e6));

		return this.treeMatch;
	}

	private boolean matchPathList(ParseTreePathList templateParseTreePathList,
			ParseTreePathList compilationUnitParseTreePathList) throws NoMatchException {

		// separated into unordered and ordered nodes
		List<ParseTreeElement> unorderedTemplatePaths = new ArrayList<>();
		List<ParseTreeElement> orderedTemplatePaths = new ArrayList<>();
		for (ParseTreeElement parseTreeElement : templateParseTreePathList) {
			if ((parseTreeElement instanceof ParseTreePathList && !((ParseTreePathList) parseTreeElement).isOrdered())
					|| (parseTreeElement instanceof ParseTreePath
							&& ((ParseTreePath) parseTreeElement).isNonOrderingNode())) {
				unorderedTemplatePaths.add(parseTreeElement);
			} else if (parseTreeElement instanceof ParseTreePath || (parseTreeElement instanceof ParseTreePathList
					&& ((ParseTreePathList) parseTreeElement).isOrdered())) {
				orderedTemplatePaths.add(parseTreeElement);
			}

		}

		List<ParseTreeElement> unorderedCompilationUnitPaths = new ArrayList<>();
		List<ParseTreeElement> orderedCompilationUnitPaths = new ArrayList<>();
		for (ParseTreeElement parseTreeElement : compilationUnitParseTreePathList) {
			if (parseTreeElement instanceof ParseTreePathList && !((ParseTreePathList) parseTreeElement).isOrdered()) {
				unorderedCompilationUnitPaths.add(parseTreeElement);
			} else if (parseTreeElement instanceof ParseTreePath || (parseTreeElement instanceof ParseTreePathList
					&& ((ParseTreePathList) parseTreeElement).isOrdered())) {
				orderedCompilationUnitPaths.add(parseTreeElement);
			}

		}

		try {
			matchOrderedPaths(orderedTemplatePaths, orderedCompilationUnitPaths);
			matchUnorderedPaths(unorderedTemplatePaths, unorderedCompilationUnitPaths);
		} catch (RuntimeException e) {
			// e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
		/*
		 * catch (NoMatchException e) { // e.printStackTrace();
		 * LOGGER.error(e.getMessage()); return false; }
		 */

		return true;
	}

	private boolean matchOrderedPaths(List<ParseTreeElement> orderedTemplatePaths,
			List<ParseTreeElement> orderedCompilationUnitPaths) throws NoMatchException {

		int j = 0;
		for (int i = 0; i < orderedTemplatePaths.size(); i++) {
			ParseTreeElement parseTreeElementTemplate = orderedTemplatePaths.get(i);

			ParseTreeElement parseTreeElementCompilationUnit;
			boolean matches = false;
			do {
				if (j >= orderedTemplatePaths.size() || j >= orderedCompilationUnitPaths.size()) {
					throw new NoMatchException(String.format("No match for ordered paths: %s -> %s",
							orderedTemplatePaths, orderedCompilationUnitPaths));
				}

				parseTreeElementCompilationUnit = orderedCompilationUnitPaths.get(j);

				matches = true;
				try {
					matchPath(parseTreeElementTemplate, parseTreeElementCompilationUnit);
				} catch (RuntimeException e) {
					matches = false;
				}

				if (!matches) {
					j++;
				} else {
					j++;
				}
			} while (!matches);
		}

		return true;
	}

	private boolean matchUnorderedPaths(List<ParseTreeElement> unorderedTemplatePaths,
			List<ParseTreeElement> unorderedCompilationUnitPaths) throws NoMatchException {

		// first match the non metalanguage elements
		List<ParseTreeElement> nonMetalanguageTemplateElements = unorderedTemplatePaths.stream()
				.filter(parseTreeElement -> (parseTreeElement instanceof ParseTreePathList
						&& !((ParseTreePathList) parseTreeElement).isMetaLang())
						|| (parseTreeElement instanceof ParseTreePath
								&& !((ParseTreePath) parseTreeElement).containsMetaLanguage()))
				.collect(Collectors.toList());

		Iterator<ParseTreeElement> iterator = nonMetalanguageTemplateElements.iterator();
		while (iterator.hasNext()) {
			ParseTreeElement parseTreeElementTemplate = iterator.next();

			List<Boolean> matches = new ArrayList<>();
			for (ParseTreeElement parseTreeElementCompilationUnit : unorderedCompilationUnitPaths) {
				boolean match = false;
				try {

					if (parseTreeElementTemplate instanceof ParseTreePath
							&& parseTreeElementCompilationUnit instanceof ParseTreePath) {
						match = matchPath(parseTreeElementTemplate, parseTreeElementCompilationUnit);
					}
					if (parseTreeElementTemplate instanceof ParseTreePathList
							&& parseTreeElementCompilationUnit instanceof ParseTreePathList) {
						match = matchPathList((ParseTreePathList) parseTreeElementTemplate,
								(ParseTreePathList) parseTreeElementCompilationUnit);
					}
				} catch (RuntimeException e) {
					// e.printStackTrace();
					LOGGER.error(e.getMessage());
				} catch (NoMatchException e) {
					// e.printStackTrace();
					LOGGER.error(e.getMessage());
				}

				if (match) {
					matches.add(match);
					unorderedCompilationUnitPaths.remove(parseTreeElementCompilationUnit);
					break;
				}
			}

			if (!matches.isEmpty()) {
				unorderedTemplatePaths.remove(parseTreeElementTemplate);
				iterator.remove();
			}
		}

		if (unorderedTemplatePaths.isEmpty() && unorderedCompilationUnitPaths.isEmpty()) {
			return true;
		}

		// check if there are still non metalanguage elements left
		int countNonMetalanguageTemplateElements = (int) unorderedTemplatePaths.stream()
				.filter(parseTreeElement -> (parseTreeElement instanceof ParseTreePathList
						&& !((ParseTreePathList) parseTreeElement).isMetaLang())
						|| (parseTreeElement instanceof ParseTreePath
								&& !((ParseTreePath) parseTreeElement).containsMetaLanguage()))
				.count();
		if (countNonMetalanguageTemplateElements > 0) {
			throw new NoMatchException(
					"There are non metalanguage elements in the template that could not be found in the compilation unit: "
							+ unorderedTemplatePaths);
		}

		// try to match the remaining metalanguage elements, start with path list
		// elements
		List<ParseTreeElement> metalanguageTemplateListElements = unorderedTemplatePaths.stream()
				.filter(parseTreeElement -> parseTreeElement instanceof ParseTreePathList
						&& ((ParseTreePathList) parseTreeElement).isMetaLang())
				.collect(Collectors.toList());

		ListIterator<ParseTreeElement> listIterator = metalanguageTemplateListElements.listIterator();
		while (listIterator.hasNext()) {
			ParseTreePathList parseTreeElementTemplate = (ParseTreePathList) listIterator.next();

			List<Boolean> matches = new ArrayList<>();
			for (ParseTreeElement parseTreeElementCompilationUnit : unorderedCompilationUnitPaths) {
				boolean match = false;
				try {
					match = matchMetaLanguagePathList(parseTreeElementTemplate,
							(ParseTreePathList) parseTreeElementCompilationUnit);
				} catch (RuntimeException e) {
					// e.printStackTrace();
					LOGGER.error(e.getMessage());
				} catch (NoMatchException e) {
					// e.printStackTrace();
					LOGGER.error(e.getMessage());
				}

				if (match) {
					matches.add(match);
					unorderedCompilationUnitPaths.remove(parseTreeElementCompilationUnit);
					break;
				}
			}

			if (!matches.isEmpty()) {
				if (!this.flagMatchedByListPlaceholder) {
					unorderedTemplatePaths.remove(parseTreeElementTemplate);
					listIterator.remove();
				} else {
					this.flagMatchedByListPlaceholder = false;
					listIterator.previous();
				}
			}
		}

		if (unorderedTemplatePaths.isEmpty() && unorderedCompilationUnitPaths.isEmpty()) {
			return true;
		}

		// check if there are non optional template path lists
		int countNonOptionalTemplatePathLists = (int) unorderedTemplatePaths.stream()
				.filter(parseTreeElement -> (parseTreeElement instanceof ParseTreePathList
						&& ((ParseTreePathList) parseTreeElement).getType() == ListType.ALTERNATIVE))
				.count();
		if (countNonOptionalTemplatePathLists > 0) {
			throw new NoMatchException("There are non optional template paths that cannot be matched: "
					+ metalanguageTemplateListElements);
		}

		// TODO match remaining template paths or path lists
		// if (!unorderedTemplatePaths.isEmpty() &&
		// unorderedCompilationUnitPaths.isEmpty()) {
		// throw new NoMatchException(
		// "There are metalanguage elements that cannot be matched: " +
		// unorderedTemplatePaths);
		// }

		// Check single placeholder elements
		List<ParseTreeElement> metalanguageTemplateElements = unorderedTemplatePaths.stream()
				.filter(parseTreeElement -> parseTreeElement instanceof ParseTreePath
						&& ((ParseTreePath) parseTreeElement).isMetaLanguageElement())
				.collect(Collectors.toList());
		List<String> placeholders = metalanguageTemplateElements.stream()
				.map(parseTreeElement -> ((ParseTreePath) parseTreeElement).getText()).collect(Collectors.toList());
		List<String> substitutions = unorderedCompilationUnitPaths.stream()
				.map(parseTreeElement -> parseTreeElement.toString()).collect(Collectors.toList());

		// TODO handle all possible permutations of placeholders

		/*
		 * if (!unorderedTemplatePaths.isEmpty()) { if
		 * (!unorderedCompilationUnitPaths.isEmpty()) { throw new NoMatchException(
		 * "The following template paths could not be found: \n" +
		 * unorderedTemplatePaths); } }
		 */

		return true;
	}

	private boolean matchMetaLanguagePathList(ParseTreePathList parseTreePathListTemplate,
			ParseTreePathList parseTreePathListCompilationUnit) throws NoMatchException {

		ListType listType = parseTreePathListTemplate.getType();
		if (listType == ListType.ALTERNATIVE || listType == ListType.OPTIONAL) {
			List<ConditionalExpression> conditions = new ArrayList<>();
			for (ParseTreeElement parseTreeElementChild : parseTreePathListTemplate) {
				String rawHint = ((ParseTreePathList) parseTreeElementChild).getHint();
				MetaLanguageElement metaLanguageElement = ((ParseTreePathList) parseTreeElementChild)
						.getMetaLanguageElement();

				String condition = getConditionForIfExpression(rawHint, metaLanguageElement);
				ConditionalExpression conditionalExpression = new ConditionalExpression(condition, metaLanguageElement);

				boolean match = true;
				try {
					if (((ParseTreePathList) parseTreeElementChild).isMetaLang()) {
						match = matchMetaLanguagePathList((ParseTreePathList) parseTreeElementChild,
								parseTreePathListCompilationUnit);
					} else {
						match = matchPathList((ParseTreePathList) parseTreeElementChild,
								parseTreePathListCompilationUnit);
					}
				} catch (NoMatchException e) {
					// do not return, try other paths
					match = false;
				}
				conditionalExpression.setValue(match);
				conditions.add(conditionalExpression);
			}

			// Check matches
			int countMatches = (int) conditions.stream().filter(condition -> condition.isTrue()).count();
			LOGGER.info("{}, true = {}", conditions, countMatches);

			if (listType == ListType.ALTERNATIVE) {
				// if no of the alternatives matches, throw an error
				if (countMatches == 0) {
					throw new NoMatchException(String.format("None of the alternatives!m %s != %s",
							parseTreePathListTemplate, parseTreePathListCompilationUnit));
				}
			}

			for (ConditionalExpression conditionalExpression : conditions) {
				if (conditionalExpression.isTrue() && !conditionalExpression.getCondition().isEmpty()) {
					this.registerNewPlaceholderSubstitution(conditionalExpression.getCondition(), "true", false);
				}
			}

			// TODO if there is not match this has to be handled nonetheless, even if it is
			// optional. Otherwise it is possible to match different things in
			// matchUnordeeredPaths

		} else if (listType == ListType.ARBITRARY) {
			String rawHint = ((ParseTreePathList) parseTreePathListTemplate).getHint();
			String[] conditions = getConditionForListExpression(rawHint);
			this.listPlaceholderCollectionVariable.push(conditions[0]);
			this.listPlaceholderIterationVariable.push(conditions[1]);

			ParseTreeElement parseTreeElementTemplateChild = parseTreePathListTemplate.get(0);
			boolean match = false;
			try {
				if (((ParseTreePathList) parseTreeElementTemplateChild).isMetaLang()) {
					match = matchMetaLanguagePathList((ParseTreePathList) parseTreeElementTemplateChild,
							parseTreePathListCompilationUnit);
				} else {
					match = matchPathList((ParseTreePathList) parseTreeElementTemplateChild,
							parseTreePathListCompilationUnit);
				}
			} catch (NoMatchException e) {
				// do not return, try other paths
				match = false;
			}

			this.listPlaceholderCollectionVariable.pop();
			this.listPlaceholderIterationVariable.pop();

			if (!match) {
				throw new NoMatchException("Cannot match parse tree: " + parseTreePathListTemplate);
			} else {
				this.flagMatchedByListPlaceholder = true;
			}

		} else if (listType == ListType.ATOMIC) {
			ParseTreePathList child = (ParseTreePathList) parseTreePathListTemplate.get(0);
			if (child.isMetaLang()) {
				return matchMetaLanguagePathList(child, parseTreePathListCompilationUnit);
			} else {
				return matchPathList(child, parseTreePathListCompilationUnit);
			}
		}

		return true;
	}

	private String getConditionForIfExpression(String rawHint, MetaLanguageElement metaLanguageElement) {
		MetaLanguagePattern metaLanguagePattern = this.metaLanguageConfiguration.getMetaLanguagePattern();
		Pattern pattern = null;
		String condition = "";
		if (metaLanguageElement == MetaLanguageElement.IF) {
			pattern = metaLanguagePattern.getMetaLangPatternIf();
		} else if (metaLanguageElement == MetaLanguageElement.IF_ELSE) {
			pattern = metaLanguagePattern.getMetaLangPatternIfElse();
		} else if (metaLanguageElement == MetaLanguageElement.ELSE) {
			pattern = metaLanguagePattern.getMetaLangPatternElse();
		} else if (metaLanguageElement == MetaLanguageElement.IF_CLOSE) {
			pattern = metaLanguagePattern.getMetaLangPatternIfClose();
		}

		if (pattern != null) {
			Matcher matcher = pattern.matcher(rawHint);
			boolean match = matcher.find();
			if (match) {
				String group = "";
				try {
					group = matcher.group(1).trim();
				} catch (Exception e) {
					// else case. ignore, return empty string instead.
				}
				condition = group;
			}
		}
		return condition;
	}

	private String[] getConditionForListExpression(String rawHint) {
		String[] conditions = new String[2];

		MetaLanguagePattern metaLanguagePattern = this.metaLanguageConfiguration.getMetaLanguagePattern();
		Pattern pattern = metaLanguagePattern.getMetaLangPatternListCollectionVariable();
		Matcher matcher = pattern.matcher(rawHint);
		boolean match = matcher.find();
		if (match) {
			conditions[0] = matcher.group(1).trim();
		}

		pattern = metaLanguagePattern.getMetaLangPatternListIterationVariable();
		matcher = pattern.matcher(rawHint);
		match = matcher.find();
		if (match) {
			conditions[1] = matcher.group(1).trim();
		}

		return conditions;
	}

	private boolean matchPath(ParseTreeElement templatePathElement, ParseTreeElement compilationUnitPathElement)
			throws NoMatchException {

		if (templatePathElement instanceof ParseTreePath && compilationUnitPathElement instanceof ParseTreePath) {
			ParseTreePath parseTreePathTemplate = (ParseTreePath) templatePathElement;
			ParseTreePath parseTreePathCompilationUnit = (ParseTreePath) compilationUnitPathElement;

			if (!parseTreePathTemplate.isMetaLanguageElement()) {
				if (parseTreePathTemplate.getText().equals(parseTreePathCompilationUnit.getText())) {
					this.registerNewMatch(parseTreePathTemplate, parseTreePathCompilationUnit);
				} else if (!parseTreePathTemplate.containsMetaLanguage()) {
					throw new RuntimeException("Could not find " + parseTreePathTemplate.getPath() + " in "
							+ parseTreePathCompilationUnit.getPath());
				} else if (parseTreePathCompilationUnit.getPureObjLangPath()
						.equals(parseTreePathTemplate.getPureObjLangPath())
						&& parseTreePathCompilationUnit.getText().equals(parseTreePathTemplate.getText())) {
					this.registerNewMatch(parseTreePathTemplate, parseTreePathCompilationUnit);
				} else {
					throw new RuntimeException("Could not find " + parseTreePathTemplate.getPath() + " in "
							+ parseTreePathCompilationUnit.getPath());
				}
			} else {
				// Metalanguage code in template
				ParseTreePath currentParseTreePathTemplate = parseTreePathTemplate;
				ParseTreePath currentParseTreePathCompilationUnit = parseTreePathCompilationUnit;
				boolean placeholderFound = false;
				do {
					if (currentParseTreePathCompilationUnit == null) {
						throw new RuntimeException("Could not find " + parseTreePathTemplate.getPath() + " in "
								+ parseTreePathCompilationUnit.getPath());
					}

					if (currentParseTreePathTemplate.getName().equals(currentParseTreePathCompilationUnit.getName())) {
						currentParseTreePathCompilationUnit = currentParseTreePathCompilationUnit.getParent();
					} else if (currentParseTreePathTemplate.isMetaLanguageElement()) {
						MetaLanguageLexerRules metaLanguageLexerRules = this.metaLanguageConfiguration
								.getMetaLanguageLexerRules();
						String parserRuleToCheck = Character
								.toUpperCase(metaLanguageLexerRules.getMetaLanguagePrefix().charAt(0))
								+ metaLanguageLexerRules.getMetaLanguagePrefix().substring(1)
								+ currentParseTreePathCompilationUnit.getName() + "Context";
						if (currentParseTreePathTemplate.getParent().getName().matches(parserRuleToCheck)) {
							this.registerNewPlaceholderSubstitution(currentParseTreePathTemplate.getText(),
									currentParseTreePathCompilationUnit.getText(), false);
							this.registerNewMatch(currentParseTreePathTemplate, currentParseTreePathCompilationUnit);
							placeholderFound = true;
							break;
						}
					} else {
						currentParseTreePathCompilationUnit = currentParseTreePathCompilationUnit.getParent();
					}

					currentParseTreePathTemplate = currentParseTreePathTemplate.getParent();
				} while (currentParseTreePathTemplate != null);

				if (!placeholderFound) {
					this.registerNewPlaceholderSubstitution(parseTreePathTemplate.getText(),
							parseTreePathCompilationUnit.getText(), false);
					this.registerNewMatch(parseTreePathTemplate, parseTreePathCompilationUnit);
				}
			}
		} else if (templatePathElement instanceof ParseTreePathList
				&& compilationUnitPathElement instanceof ParseTreePathList) {
			ParseTreePathList parseTreePathListTemplate = (ParseTreePathList) templatePathElement;
			ParseTreePathList parseTreePathListCompilationUnit = (ParseTreePathList) compilationUnitPathElement;

			if (parseTreePathListTemplate.isAtomic() && parseTreePathListCompilationUnit.isAtomic()) {
				matchOrderedPaths(parseTreePathListTemplate, parseTreePathListCompilationUnit);
			} else if (parseTreePathListTemplate.isListPattern() && parseTreePathListCompilationUnit.isListPattern()) {
				matchListPattern(parseTreePathListTemplate, parseTreePathListCompilationUnit);
			} else if (!parseTreePathListTemplate.isAtomic() & !parseTreePathListCompilationUnit.isAtomic()) {
				matchOrderedPaths(parseTreePathListTemplate, parseTreePathListCompilationUnit);
			} else {
				throw new NoMatchException("Unable to match the parse tree lists");
			}
		} else if (templatePathElement instanceof ParseTreePathList
				&& compilationUnitPathElement instanceof ParseTreePath) {
			throw new NoMatchException(String.format("Cannot match ParseTreePathList %s agains ParseTreePath %s",
					templatePathElement, compilationUnitPathElement));
		} else if (templatePathElement instanceof ParseTreePath
				&& compilationUnitPathElement instanceof ParseTreePathList) {
			throw new NoMatchException(String.format("Cannot match ParseTreePath %s agains ParseTreePathList %s",
					templatePathElement, compilationUnitPathElement));
		}
		return true;
	}

	private boolean matchListPattern(ParseTreePathList parseTreePathListTemplate,
			ParseTreePathList parseTreePathListCompilationUnit) throws NoMatchException {

		int numOfPlaceholders = (int) parseTreePathListTemplate.stream()
				.filter(path -> path instanceof ParseTreePath && ((ParseTreePath) path).containsMetaLanguage()).count();
		int maxSubstitutions = parseTreePathListCompilationUnit.size() - parseTreePathListTemplate.size()
				+ numOfPlaceholders;

		// will be small enough to cast to int
		int[][] combinations;
		if (maxSubstitutions == 0) {
			// if there are PHs, than all of them will just replace one element
			combinations = new int[1][numOfPlaceholders];
			Arrays.fill(combinations, new int[] { 1 });
		} else if (maxSubstitutions < numOfPlaceholders) {
			throw new NoMatchException("App path list to short. Could not instantiate all placeholders.");
		} else {
			// n = maxSubstitutions, k = PHs; different buckets, same balls.
			combinations = MathUtil.multichooseMin1(maxSubstitutions, numOfPlaceholders);
		}

		List<Map<String, String>> possibleSubstitutions = new ArrayList<>();
		for (int[] combination : combinations) {
			Map<String, String> variableSubstitutions = new HashMap<>();

			try {
				int observedPhIndex = 0;
				int j = 0;
				for (int i = 0; i < parseTreePathListTemplate.size(); i++) {

					ParseTreeElement parseTreeElementTemplate = parseTreePathListTemplate.get(i);
					if (!(parseTreeElementTemplate instanceof ParseTreePath)) {
						throw new IllegalStateException("ParseTreePathList not supported yet!");
					}

					ParseTreePath parseTreePathTemplate = (ParseTreePath) parseTreeElementTemplate;
					boolean isPh = parseTreePathTemplate.isMetaLanguageElement();
					String parseTreePathTemplateName = parseTreePathTemplate.getName();

					ParseTreeElement parseTreeElementCompilationUnit;
					String appMatch = "";
					boolean matches = false;
					int consumedAppElems = 0;

					do {
						if (j >= parseTreePathListCompilationUnit.size()) {
							// template could not be found entirely in app!
							throw new NoMatchException("Could not find path " + parseTreePathTemplate.getPath());
						}

						parseTreeElementCompilationUnit = parseTreePathListCompilationUnit.get(j);
						if (!(parseTreeElementCompilationUnit instanceof ParseTreePath)) {
							throw new IllegalStateException("ParseTreePathList not supported yet!");
						}

						ParseTreePath parseTreePathCompilationUnit = (ParseTreePath) parseTreeElementCompilationUnit;
						appMatch += parseTreePathCompilationUnit.getText();
						consumedAppElems++;

						if (isPh) {
							if (combination[observedPhIndex] > consumedAppElems) {
								j++;
								continue;
							} else {
								// LOGGER.info("Match tokens {} --> {}", parseTreePathTemplate.getText(),
								// appMatch);
								variableSubstitutions.put(parseTreePathTemplate.getText(), appMatch);
								j++;
								observedPhIndex++;
								break;
							}
						} else {
							matches = parseTreePathTemplateName.equals(parseTreePathCompilationUnit.getName())
									&& parseTreePathTemplate.getText().equals(parseTreePathCompilationUnit.getText());
						}

						if (!matches) {
							if (isPh) {
								j++;
							} else {
								throw new NoMatchException(
										"Could not match template path " + parseTreePathTemplate.getPath());
							}
						} else {
							// LOGGER.info("Match tokens {} --> {}", parseTreePathTemplate.getText(),
							// appMatch);
							j++;
						}
					} while (!matches);
				}

				if (!variableSubstitutions.isEmpty()) {
					possibleSubstitutions.add(variableSubstitutions);
				}
			} catch (NoMatchException e) {
				// ignore, try next
			}
		}

		// no error
		// if (possibleSubstitutions.isEmpty()) {
		// throw new NoMatchException(
		// String.format("Could not match template %s to %s! No possible placeholder
		// substitutions found.",
		// parseTreePathListTemplate, parseTreePathListCompilationUnit));
		// }

		LOGGER.info("Match list pattern {} -> {}", parseTreePathListTemplate, parseTreePathListCompilationUnit);
		if (!possibleSubstitutions.isEmpty()) {
			LOGGER.info("{} possible placeholder substitutions found. {}", possibleSubstitutions.size(),
					possibleSubstitutions);
			List<Map<String, String>> validSubstitutions = new ArrayList<>();
			for (Map<String, String> possiblePlaceholderSubstitution : possibleSubstitutions) {
				boolean isValidSubstitution = true;
				for (Entry<String, String> placeholderSub : possiblePlaceholderSubstitution.entrySet()) {

					isValidSubstitution = checkPlaceholderSubstitution(placeholderSub.getKey(),
							placeholderSub.getValue());
					if (isValidSubstitution)
						break;
				}

				if (isValidSubstitution) {
					validSubstitutions.add(possiblePlaceholderSubstitution);
				}
			}

			LOGGER.info("{} valid placeholder substitutions found. {}", validSubstitutions.size(), validSubstitutions);
			for (Map<String, String> validSubstitution : validSubstitutions) {
				for (Entry<String, String> validSub : validSubstitution.entrySet()) {
					this.registerNewPlaceholderSubstitution(validSub.getKey(), validSub.getValue(), true);
				}
			}
		}

		return true;
	}

	private void registerNewPlaceholderSubstitution(String placeholder, String substitution, boolean addAlways)
			throws PlaceholderResolutionException {
		Pattern pattern = this.metaLanguageConfiguration.getMetaLanguagePattern().getMetaLangPatternPlaceholder();
		Matcher matcher = pattern.matcher(placeholder);
		if (matcher.find()) {
			placeholder = matcher.group(1);
		}

		Set<String> possibleSubstitutions = null;
		if (this.placeholderResolver != null) {
			PlaceholderResolutionResult placeholderResolutionResult = this.placeholderResolver
					.resolvePlaceholder(placeholder, substitution);
			possibleSubstitutions = placeholderResolutionResult.getSubstitutions();
			placeholder = placeholderResolutionResult.getPlaceholder();
		} else {
			possibleSubstitutions = new HashSet<>();
			possibleSubstitutions.add(substitution);
		}

		if (!this.listPlaceholderIterationVariable.isEmpty()
				&& this.listPlaceholderIterationVariable.peek().equals(placeholder)) {
			placeholder = this.listPlaceholderCollectionVariable.peek();
			addAlways = true;
		}

		Set<String> newSubstitutions = null;
		if (!this.placeholderSubstitutions.containsKey(placeholder)) {
			newSubstitutions = possibleSubstitutions;
		} else {
			newSubstitutions = unifyPlaceholderSubstitutions(placeholder, possibleSubstitutions, addAlways);
		}

		if (newSubstitutions == null || newSubstitutions.isEmpty()) {
			throw new PlaceholderClashException(placeholder, this.placeholderSubstitutions.get(placeholder),
					possibleSubstitutions, String.format("Placeholder clash for placeholder %s: %s != %s", placeholder,
							this.placeholderSubstitutions.get(placeholder), possibleSubstitutions));
		}

		LOGGER.info("Placeholder substitution: {} --> {}", placeholder, newSubstitutions);
		this.placeholderSubstitutions.put(placeholder, newSubstitutions);
	}

	private void registerNewMatch(ParseTreePath parseTreePathTemplate, ParseTreePath parseTreePathCompilationUnit) {
		LOGGER.info("New match: {}, {}", parseTreePathTemplate.getText(), parseTreePathCompilationUnit.getText());
		this.matches.add(new Match(parseTreePathTemplate, parseTreePathCompilationUnit));
	}

	private boolean checkPlaceholderSubstitution(String placeholder, String substitution) {
		if (this.placeholderSubstitutions.containsKey(placeholder)) {
			if (!this.placeholderSubstitutions.get(placeholder).contains(substitution)) {
				return false;
			}
		}
		return true;
	}

	private Set<String> unifyPlaceholderSubstitutions(String placeholder, Set<String> possibleSubstitutions,
			boolean addAlways) {
		Set<String> currentSubstitutions = this.placeholderSubstitutions.get(placeholder);
		Set<String> newSubstitutions = new HashSet<>();
		if (addAlways) {
			newSubstitutions.addAll(currentSubstitutions);
		}

		for (String possibleSubstitution : possibleSubstitutions) {
			if (currentSubstitutions.contains(possibleSubstitution) || addAlways) {
				newSubstitutions.add(possibleSubstitution);
			}
		}
		return newSubstitutions;
	}
}

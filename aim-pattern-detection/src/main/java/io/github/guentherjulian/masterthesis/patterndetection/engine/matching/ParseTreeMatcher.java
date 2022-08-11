package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.NoMatchException;
import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.PlaceholderClashException;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTree;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreeElement;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePath;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePathList;

public class ParseTreeMatcher {

	private static final Logger LOGGER = LogManager.getLogger(ParseTreeMatcher.class);

	private TreeMatch match;
	private ParseTree aimPatternTemplateParseTree;
	private ParseTree compilationUnitParseTree;
	private Map<String, Set<String>> placeholderSubstitutions;
	private List<Match> matches;
	private MetaLanguageLexerRules metaLanguageLexerRules;

	public ParseTreeMatcher(ParseTree compilationUnitParseTree, ParseTree aimPatternTemplateParseTree,
			MetaLanguageLexerRules metaLanguageLexerRules) {
		this.compilationUnitParseTree = compilationUnitParseTree;
		this.aimPatternTemplateParseTree = aimPatternTemplateParseTree;
		this.metaLanguageLexerRules = metaLanguageLexerRules;
	}

	public TreeMatch match(Map<String, Set<String>> placeholderSubstitutions) {
		// TODO implement tree matching
		long startTime = System.nanoTime();

		this.matches = new ArrayList<>();
		if (placeholderSubstitutions != null && placeholderSubstitutions.isEmpty()) {
			this.placeholderSubstitutions = placeholderSubstitutions;
		} else {
			this.placeholderSubstitutions = new HashMap<>();
		}

		this.match = new TreeMatch();
		this.match.setTemplateParseTree(this.aimPatternTemplateParseTree);
		this.match.setCompilationUnitParseTree(this.compilationUnitParseTree);
		this.match.setPlaceholderSubstitutions(this.placeholderSubstitutions);
		this.match.setMatches(this.matches);
		this.match.setMatch(true);

		try {
			matchPathList(this.aimPatternTemplateParseTree.getParseTreePathList(),
					this.compilationUnitParseTree.getParseTreePathList());
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (NoMatchException e) {
			this.match.setMatch(false);
			this.match.setException(e);
		}

		long endTime = System.nanoTime();
		LOGGER.info("Finished matching parse trees (took {} ns, {} ms)", (endTime - startTime),
				((endTime - startTime) / 1e6));

		return this.match;
	}

	private TreeMatch matchPathList(ParseTreePathList templateParseTreePathList,
			ParseTreePathList compilationUnitParseTreePathList) throws NoMatchException {

		// separated into unordered and ordered nodes
		List<ParseTreeElement> unorderedTemplatePaths = new ArrayList<>();
		List<ParseTreeElement> orderedTemplatePaths = new ArrayList<>();
		for (ParseTreeElement parseTreeElement : templateParseTreePathList) {
			if (parseTreeElement instanceof ParseTreePathList && !((ParseTreePathList) parseTreeElement).isOrdered()) {
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
			e.printStackTrace();
		}

		return null;
	}

	private TreeMatch matchOrderedPaths(List<ParseTreeElement> orderedTemplatePaths,
			List<ParseTreeElement> orderedCompilationUnitPaths) throws NoMatchException {

		int j = 0;
		for (int i = 0; i < orderedTemplatePaths.size(); i++) {
			ParseTreeElement parseTreeElementTemplate = orderedTemplatePaths.get(i);

			ParseTreeElement parseTreeElementCompilationUnit;
			boolean matches = false;
			do {
				if (j >= orderedTemplatePaths.size()) {
					throw new RuntimeException("Match exception");
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

		return null;
	}

	private TreeMatch matchUnorderedPaths(List<ParseTreeElement> unorderedTemplatePaths,
			List<ParseTreeElement> unorderedCompilationUnitPaths) throws NoMatchException {

		Iterator<ParseTreeElement> iterator = unorderedTemplatePaths.iterator();
		while (iterator.hasNext()) {
			ParseTreeElement parseTreeElementTemplate = iterator.next();

			for (ParseTreeElement parseTreeElementCompilationUnit : unorderedCompilationUnitPaths) {
				try {
					if (parseTreeElementTemplate instanceof ParseTreePath
							&& parseTreeElementCompilationUnit instanceof ParseTreePath) {
						matchPath(parseTreeElementTemplate, parseTreeElementCompilationUnit);
					}
					if (parseTreeElementTemplate instanceof ParseTreePathList
							&& parseTreeElementCompilationUnit instanceof ParseTreePathList) {
						matchPathList((ParseTreePathList) parseTreeElementTemplate,
								(ParseTreePathList) parseTreeElementCompilationUnit);
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				}

				// TODO evaluate match
			}
		}

		return null;
	}

	private TreeMatch matchPath(ParseTreeElement templatePathElement, ParseTreeElement compilationUnitPathElement)
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
						String parserRuleToCheck = Character
								.toUpperCase(this.metaLanguageLexerRules.getMetaLanguagePrefix().charAt(0))
								+ this.metaLanguageLexerRules.getMetaLanguagePrefix().substring(1)
								+ currentParseTreePathCompilationUnit.getName() + "Context";
						if (currentParseTreePathTemplate.getParent().getName().matches(parserRuleToCheck)) {
							this.registerNewPlaceholderSubstitution(currentParseTreePathTemplate.getText(),
									currentParseTreePathCompilationUnit.getText());
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
							parseTreePathCompilationUnit.getText());
					this.registerNewMatch(parseTreePathTemplate, parseTreePathCompilationUnit);
				}
			}
		} else if (templatePathElement instanceof ParseTreePathList
				&& compilationUnitPathElement instanceof ParseTreePathList) {
			ParseTreePathList parseTreePathListTemplate = (ParseTreePathList) templatePathElement;
			ParseTreePathList parseTreePathListCompilationUnit = (ParseTreePathList) compilationUnitPathElement;

			matchOrderedPaths(parseTreePathListTemplate, parseTreePathListCompilationUnit);
		}
		return null;
	}

	private void registerNewPlaceholderSubstitution(String placeholder, String substitution)
			throws PlaceholderClashException {

		if (checkPlaceholderSubstitution(placeholder, substitution)) {
			LOGGER.info("New placeholder substitution {} {}", placeholder, substitution);
			if (!this.placeholderSubstitutions.containsKey(placeholder)) {
				Set<String> set = new HashSet<>();
				set.add(substitution);
				this.placeholderSubstitutions.put(placeholder, set);
			} else {
				Set<String> set = placeholderSubstitutions.get(placeholder);
				set.add(substitution);
				this.placeholderSubstitutions.put(placeholder, set);
			}
		} else {
			throw new PlaceholderClashException(placeholder, substitution,
					this.placeholderSubstitutions.get(placeholder), "Placeholder clash for placeholder " + placeholder);
		}
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
}

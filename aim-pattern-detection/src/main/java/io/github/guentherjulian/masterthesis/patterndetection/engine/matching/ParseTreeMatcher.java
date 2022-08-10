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

import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTree;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreeElement;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePath;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePathList;

public class ParseTreeMatcher {

	private static final Logger LOGGER = LogManager.getLogger(ParseTreeMatcher.class);

	private ParseTree compilationUnitParseTree;
	private ParseTree aimPatternTemplateParseTree;
	private Map<String, Set<String>> placeholderSubstitutions;

	public ParseTreeMatcher(ParseTree compilationUnitParseTree, ParseTree aimPatternTemplateParseTree) {
		this.compilationUnitParseTree = compilationUnitParseTree;
		this.aimPatternTemplateParseTree = aimPatternTemplateParseTree;
	}

	public TreeMatch match(Map<String, Set<String>> placeholderSubstitutions) {
		// TODO implement tree matching
		long startTime = System.nanoTime();

		this.placeholderSubstitutions = placeholderSubstitutions;

		List<ParseTreeElement> unorderedTemplatePaths = new ArrayList<>();
		List<ParseTreeElement> orderedTemplatePaths = new ArrayList<>();
		for (ParseTreeElement parseTreeElement : this.aimPatternTemplateParseTree.getParseTreePathList()) {
			if (parseTreeElement instanceof ParseTreePathList && !((ParseTreePathList) parseTreeElement).isOrdered()) {
				unorderedTemplatePaths.add(parseTreeElement);
			} else if (parseTreeElement instanceof ParseTreePath || (parseTreeElement instanceof ParseTreePathList
					&& ((ParseTreePathList) parseTreeElement).isOrdered())) {
				orderedTemplatePaths.add(parseTreeElement);
			}

		}

		List<ParseTreeElement> unorderedCompilationUnitPaths = new ArrayList<>();
		List<ParseTreeElement> orderedCompilationUnitPaths = new ArrayList<>();
		for (ParseTreeElement parseTreeElement : this.compilationUnitParseTree.getParseTreePathList()) {
			if (parseTreeElement instanceof ParseTreePathList && !((ParseTreePathList) parseTreeElement).isOrdered()) {
				unorderedCompilationUnitPaths.add(parseTreeElement);
			} else if (parseTreeElement instanceof ParseTreePath || (parseTreeElement instanceof ParseTreePathList
					&& ((ParseTreePathList) parseTreeElement).isOrdered())) {
				orderedCompilationUnitPaths.add(parseTreeElement);
			}

		}

		long endTime = System.nanoTime();
		LOGGER.info("Finished matching parse trees (took {} ns, {} ms)", (endTime - startTime),
				((endTime - startTime) / 1e6));

		try {
			matchOrderedPaths(orderedTemplatePaths, orderedCompilationUnitPaths);
			matchUnorderedPaths(unorderedTemplatePaths, unorderedCompilationUnitPaths);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TreeMatch treeMatch = new TreeMatch();
		return treeMatch;
	}

	private TreeMatch matchOrderedPaths(List<ParseTreeElement> orderedTemplatePaths,
			List<ParseTreeElement> orderedCompilationUnitPaths) {

		LOGGER.info(orderedTemplatePaths.hashCode());
		LOGGER.info(orderedCompilationUnitPaths.hashCode());
		LOGGER.info(orderedTemplatePaths.toString());
		LOGGER.info(orderedCompilationUnitPaths.toString());

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
				} catch (Exception e) {
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
			List<ParseTreeElement> unorderedCompilationUnitPaths) {

		Iterator<ParseTreeElement> iterator = unorderedTemplatePaths.iterator();
		while (iterator.hasNext()) {
			ParseTreeElement parseTreeElementTemplate = iterator.next();

			for (ParseTreeElement parseTreeElementCompilationUnit : unorderedCompilationUnitPaths) {
				TreeMatch match;
				try {
					match = matchPath(parseTreeElementTemplate, parseTreeElementCompilationUnit);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// TODO evaluate match
			}
		}

		return null;
	}

	private TreeMatch matchPath(ParseTreeElement templatePathElement, ParseTreeElement compilationUnitPathElement) {
		if (templatePathElement instanceof ParseTreePath && compilationUnitPathElement instanceof ParseTreePath) {
			ParseTreePath parseTreePathTemplate = (ParseTreePath) templatePathElement;
			ParseTreePath parseTreePathCompilationUnit = (ParseTreePath) compilationUnitPathElement;

			if (!parseTreePathTemplate.isMetaLanguageElement()) {
				if (parseTreePathTemplate.getText().equals(parseTreePathCompilationUnit.getText())) {
					LOGGER.info("New match 1: {}, {}", parseTreePathTemplate.getText(),
							parseTreePathCompilationUnit.getText());
					this.registerNewMatch(parseTreePathTemplate, parseTreePathCompilationUnit);
				} else if (!parseTreePathTemplate.containsMetaLanguage()) {
					throw new RuntimeException("Could not find " + parseTreePathTemplate.getPath() + " in "
							+ parseTreePathCompilationUnit.getPath());
				} else if (parseTreePathCompilationUnit.getPureObjLangPath()
						.equals(parseTreePathTemplate.getPureObjLangPath())
						&& parseTreePathCompilationUnit.getText().equals(parseTreePathTemplate.getText())) {
					LOGGER.info("New match 2: {}, {}", parseTreePathTemplate.getText(),
							parseTreePathCompilationUnit.getText());
				} else {
					throw new RuntimeException("Could not find " + parseTreePathTemplate.getPath() + " in "
							+ parseTreePathCompilationUnit.getPath());
				}
			} else {
				// Metalanguage code in template
				ParseTreePath currentParseTreePathTemplate = parseTreePathTemplate;
				ParseTreePath currentParseTreePathCompilationUnit = parseTreePathCompilationUnit;
				do {
					if (currentParseTreePathCompilationUnit == null) {
						throw new RuntimeException("Could not find " + parseTreePathTemplate.getPath() + " in "
								+ parseTreePathCompilationUnit.getPath());
					}

					LOGGER.info("temp: {}, {}, {}, {}", currentParseTreePathTemplate.getText(),
							currentParseTreePathTemplate.getName(),
							currentParseTreePathTemplate.isMetaLanguageElement(),
							currentParseTreePathTemplate.containsMetaLanguage());
					LOGGER.info("app: {}, {}, {}, {}", currentParseTreePathCompilationUnit.getText(),
							currentParseTreePathCompilationUnit.getName(),
							currentParseTreePathCompilationUnit.isMetaLanguageElement(),
							currentParseTreePathCompilationUnit.containsMetaLanguage());

					if (currentParseTreePathTemplate.getName().equals(currentParseTreePathCompilationUnit.getName())) {
						currentParseTreePathCompilationUnit = currentParseTreePathCompilationUnit.getParent();
					} else if (currentParseTreePathTemplate.isMetaLanguageElement()) {
						if (currentParseTreePathTemplate.getParent().getName()
								.matches("Fm_" + currentParseTreePathCompilationUnit.getName() + "Context")) {
							LOGGER.info("New placeholder substitution {} {}", currentParseTreePathTemplate.getText(),
									currentParseTreePathCompilationUnit.getText());
							this.registerNewPlaceholderSubstitution(currentParseTreePathTemplate.getText(),
									currentParseTreePathCompilationUnit.getText());
							break;
						}
					} else {
						currentParseTreePathCompilationUnit = currentParseTreePathCompilationUnit.getParent();
					}

					currentParseTreePathTemplate = currentParseTreePathTemplate.getParent();
				} while (currentParseTreePathTemplate != null);
			}
		} else if (templatePathElement instanceof ParseTreePathList
				&& compilationUnitPathElement instanceof ParseTreePathList) {
			ParseTreePathList parseTreePathListTemplate = (ParseTreePathList) templatePathElement;
			ParseTreePathList parseTreePathListCompilationUnit = (ParseTreePathList) compilationUnitPathElement;

			matchOrderedPaths(parseTreePathListTemplate, parseTreePathListCompilationUnit);
		}
		return null;
	}

	private void registerNewPlaceholderSubstitution(String key, String value) {
		if (this.placeholderSubstitutions == null) {
			this.placeholderSubstitutions = new HashMap<>();
		}
		if (!this.placeholderSubstitutions.containsKey(key)) {
			Set<String> set = new HashSet<>();
			set.add(value);
			this.placeholderSubstitutions.put(key, set);
		} else {
			Set<String> set = placeholderSubstitutions.get(key);
			set.add(value);
			this.placeholderSubstitutions.put(key, set);
		}
	}

	private void registerNewMatch(ParseTreePath parseTreePathTemplate, ParseTreePath parseTreePathCompilationUnit) {

	}
}

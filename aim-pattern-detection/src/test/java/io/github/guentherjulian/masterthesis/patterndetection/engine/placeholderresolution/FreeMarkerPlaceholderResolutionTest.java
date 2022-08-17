package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.exception.PlaceholderResolutionException;

public class FreeMarkerPlaceholderResolutionTest extends AbstractPlaceholderResolutionTest {

	@BeforeAll
	public static void setupTests() throws URISyntaxException {
		placeholderResolver = new FreeMarkerPlaceholderResolver();
	}

	@Test
	void capFirstTest() {
		String placeholder = "name?cap_first";
		String substitution = "Joe";

		PlaceholderResolutionResult placeholderResolutionResult = null;
		try {
			placeholderResolutionResult = placeholderResolver.resolvePlaceholder(placeholder, substitution);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(placeholderResolutionResult);
		assertTrue(placeholderResolutionResult.getSubstitutions().contains("Joe"));
		assertTrue(placeholderResolutionResult.getSubstitutions().contains("joe"));
	}

	@Test
	void replaceValidArgumentsTest() {
		String placeholder = "name?replace('World', 'Joe')";
		String substitution = "Hello Joe";

		PlaceholderResolutionResult placeholderResolutionResult = null;
		try {
			placeholderResolutionResult = placeholderResolver.resolvePlaceholder(placeholder, substitution);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(placeholderResolutionResult);
		assertTrue(placeholderResolutionResult.getSubstitutions().contains("Hello World"));
	}

	@Test
	void replaceInvalidArgumentsTest() {
		String placeholder = "name?replace('World')";
		String substitution = "Hello Joe";

		PlaceholderResolutionResult placeholderResolutionResult = null;
		Exception exception = null;
		try {
			placeholderResolutionResult = placeholderResolver.resolvePlaceholder(placeholder, substitution);
		} catch (Exception e) {
			exception = e;
		}

		assertNull(placeholderResolutionResult);
		assertNotNull(exception);
		assertTrue(exception instanceof PlaceholderResolutionException);
	}
}

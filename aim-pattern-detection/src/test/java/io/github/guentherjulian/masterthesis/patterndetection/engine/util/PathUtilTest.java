package io.github.guentherjulian.masterthesis.patterndetection.engine.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.AbstractAimPatternDetectionEngineTest;
import io.github.guentherjulian.masterthesis.patterndetection.engine.utils.PathUtil;

public class PathUtilTest {

	private static Path resourcesPath;

	@BeforeAll
	public static void setupTests() throws URISyntaxException {
		resourcesPath = Paths.get(AbstractAimPatternDetectionEngineTest.class.getProtectionDomain().getCodeSource()
				.getLocation().toURI());
	}

	@Test
	void testGetPathsWithoutRegex() throws IOException {
		Path project = resourcesPath.resolve("util-tests").resolve("testproject");
		List<Path> paths = PathUtil.getAllFiles(project);

		assertEquals(paths.size(), 4);
	}

	@Test
	void testGetPathsWithRegex() throws IOException {
		Path project = resourcesPath.resolve("util-tests").resolve("testproject");
		List<Path> paths = PathUtil.getAllFiles(project, ".+\\.java");

		assertEquals(paths.size(), 3);
	}

	@Test
	void testGetAimPatternTemplatesWithoutRegex() throws IOException {
		Path project = resourcesPath.resolve("util-tests").resolve("testproject");
		List<AimPatternTemplate> aimPatternTemplates = PathUtil.getAimPatternTemplates(project);

		assertEquals(aimPatternTemplates.size(), 4);
		assertEquals(aimPatternTemplates.get(0).getInstantiationPath(), "pom.xml");
		assertEquals(aimPatternTemplates.get(1).getInstantiationPath(), "src\\main\\java\\JavaClass1.java");
	}

	@Test
	void testGetAimPatternTemplatesWithRegex() throws IOException {
		Path project = resourcesPath.resolve("util-tests").resolve("testproject");
		List<AimPatternTemplate> aimPatternTemplates = PathUtil.getAimPatternTemplates(project, ".+\\.java");

		assertEquals(aimPatternTemplates.size(), 3);
		assertEquals(aimPatternTemplates.get(0).getInstantiationPath(), "src\\main\\java\\JavaClass1.java");
	}
}

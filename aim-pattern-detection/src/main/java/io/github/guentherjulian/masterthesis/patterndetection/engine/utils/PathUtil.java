package io.github.guentherjulian.masterthesis.patterndetection.engine.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;

public class PathUtil {

	public static List<Path> getAllFiles(Path rootDirectory) throws IOException {
		List<Path> paths = new ArrayList<>();
		if (Files.exists(rootDirectory)) {
			Files.walk(rootDirectory).forEach(path -> {
				if (!Files.isDirectory(path)) {
					paths.add(path);
				}
			});
		}
		return paths;
	}

	public static List<Path> getAllFiles(Path rootDirectory, String filenameRegex) throws IOException {
		if (filenameRegex == null || filenameRegex.isEmpty()) {
			return getAllFiles(rootDirectory);
		}

		List<Path> paths = new ArrayList<>();
		Pattern pattern = Pattern.compile(filenameRegex);
		if (Files.exists(rootDirectory)) {
			Files.walk(rootDirectory).forEach(path -> {
				if (!Files.isDirectory(path)) {
					Matcher matcher = pattern.matcher(path.getFileName().toString());
					if (matcher.find()) {
						paths.add(path);
					}
				}
			});
		}
		return paths;
	}

	public static List<AimPatternTemplate> getAimPatternTemplates(Path rootDirectory, String filenameRegex)
			throws IOException {
		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		Pattern pattern = Pattern.compile(filenameRegex);
		if (Files.exists(rootDirectory)) {
			Files.walk(rootDirectory).forEach(path -> {
				if (!Files.isDirectory(path)) {
					Matcher matcher = pattern.matcher(path.getFileName().toString());
					if (matcher.find()) {
						String instantiationPath = rootDirectory.relativize(path).toString();
						AimPatternTemplate aimPatternTemplate = new AimPatternTemplate(path, instantiationPath);
						aimPatternTemplates.add(aimPatternTemplate);
					}
				}
			});
		}
		return aimPatternTemplates;
	}

	public static List<AimPatternTemplate> getAimPatternTemplates(Path rootDirectory) throws IOException {
		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		if (Files.exists(rootDirectory)) {
			Files.walk(rootDirectory).forEach(path -> {
				if (!Files.isDirectory(path)) {
					String instantiationPath = rootDirectory.relativize(path).toString();
					AimPatternTemplate aimPatternTemplate = new AimPatternTemplate(path, instantiationPath);
					aimPatternTemplates.add(aimPatternTemplate);
				}
			});
		}
		return aimPatternTemplates;
	}

}

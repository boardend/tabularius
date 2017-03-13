package org.eclipse.scout.boot.tabularius.numbers.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.scout.boot.tabularius.numbers.model.Contributor;
import org.eclipse.scout.boot.tabularius.numbers.model.ScannedNumber;

public class NumbersFileWalker extends SimpleFileVisitor<Path> {
	private String basePath;
	private Map<String, Contributor> cache = new HashMap<>();

	public NumbersFileWalker(String path) throws IOException {
		basePath = path;
		Files.walkFileTree(Paths.get(path), this);
	}

	public Set<Contributor> getContributors() {
		return new HashSet<>(cache.values());
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
		final String path = minimizePath(dir);
		if (path.equals("")) {
			return FileVisitResult.CONTINUE;
		}
		if (path.matches("/\\d+_[A-Z]{2}[A-Z0-9][A-Z0-9]")) {
			return FileVisitResult.CONTINUE;
		}
		if (path.matches("/.+/\\d")) {
			return FileVisitResult.CONTINUE;
		}
		return FileVisitResult.SKIP_SUBTREE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
		final String path = minimizePath(file);
		Matcher m = Pattern.compile("/(.+)/(\\d)/number-(\\d+)\\..+").matcher(path);
		if (m.find()) {
			String contributor = m.group(1);
			short digit = Short.valueOf(m.group(2));
			short no = Short.valueOf(m.group(3));
			add(contributor, digit, no, path);
		}
		return FileVisitResult.CONTINUE;
	}

	private void add(String contributor, short digit, short no, String path) {
		if (!cache.containsKey(contributor)) {
			cache.put(contributor, parseContributor(contributor));
		}

		Contributor c = cache.get(contributor);
		ScannedNumber n = new ScannedNumber();
		n.contributor = c;
		n.path = path;
		n.digit = digit;
		n.no = no;
		c.numbers.add(n);
	}

	private Contributor parseContributor(String contributor) {
		Matcher m = Pattern.compile("(\\d+)_([A-Z]{2})([A-Z0-9])([A-Z0-9])").matcher(contributor);
		if (m.find()) {
			Contributor c = new Contributor();
			c.name = contributor;
			c.id = Short.valueOf(m.group(1));
			c.country = m.group(2);
			c.age = "X".equals(m.group(3)) ? 0 : Short.valueOf(m.group(3));
			c.sex = m.group(4).charAt(0);
			return c;
		}
		return null;
	}

	public String minimizePath(Path path) {
		return path.toFile().getAbsolutePath().replace(basePath, "");
	}
}
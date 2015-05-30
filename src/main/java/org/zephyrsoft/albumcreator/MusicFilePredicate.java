package org.zephyrsoft.albumcreator;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

/**
 * Which file is a "music" file?
 */
public class MusicFilePredicate implements BiPredicate<Path, java.nio.file.attribute.BasicFileAttributes> {
	
	@Override
	public boolean test(Path path, BasicFileAttributes attributes) {
		return path.getFileName().toString().endsWith(".mp3");
	}
	
}

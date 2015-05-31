package org.zephyrsoft.albumcreator;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.function.BiPredicate;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * Which file is a "music" file?
 */
public class MusicFilePredicate implements BiPredicate<Path, java.nio.file.attribute.BasicFileAttributes> {
	
	/** extensions without periods */
	private static final Set<String> MUSIC_FILE_EXTENSIONS = Sets.newHashSet("mp3", "flac");
	
	public static String removeMusicFileExtension(String input) {
		return input.replaceAll("\\.(" + Joiner.on('|').join(MUSIC_FILE_EXTENSIONS) + ")$", "");
	}
	
	@Override
	public boolean test(Path path, BasicFileAttributes attributes) {
		final String filename = path.getFileName().toString();
		return MUSIC_FILE_EXTENSIONS.stream()
			.map(extension -> filename.endsWith("." + extension))
			.anyMatch(b -> b.booleanValue() == true);
	}
	
}

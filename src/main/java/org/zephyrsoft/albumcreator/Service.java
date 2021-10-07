package org.zephyrsoft.albumcreator;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zephyrsoft.albumcreator.model.Settings;
import org.zephyrsoft.albumcreator.model.SourceFile;
import org.zephyrsoft.albumcreator.model.TargetFile;

import com.google.common.base.StandardSystemProperty;

/**
 * Performs the work.
 */
public class Service {

	private static final Logger LOG = LoggerFactory.getLogger(Service.class);

	private static final String SETTINGS_FILE_NAME = StandardSystemProperty.USER_HOME.value()
		+ StandardSystemProperty.FILE_SEPARATOR.value() + ".albumcreator";
	private static final String SOURCE_DIRECTORY_PROPERTY_NAME = "sourceDirectory";
	private static final String TARGET_DIRECTORY_PROPERTY_NAME = "targetDirectory";

	private final NumberFormat numberFormat;

	public Service() {
		numberFormat = NumberFormat.getIntegerInstance();
		numberFormat.setMinimumIntegerDigits(2);
	}

	public List<SourceFile> readSourceFiles(final Path sourcePath) throws IOException {
		return Files.find(sourcePath, 1, new MusicFilePredicate(), FileVisitOption.FOLLOW_LINKS)
			.sorted((path1, path2) -> {
				int parentComparison = path1.toAbsolutePath().getParent()
					.compareTo(path2.toAbsolutePath().getParent());
				if (parentComparison != 0) {
					// directories are already different
					return parentComparison;
				} else {
					// compare file name case-insensitive
					return path1.toAbsolutePath().getFileName().toString().toLowerCase()
						.compareTo(path2.toAbsolutePath().getFileName().toString().toLowerCase());
				}
			})
			.map(path -> new SourceFile(path))
			.toList();
	}

	public void createScript(final Path targetDir, final List<TargetFile> targetFiles, final LogTarget logTarget) {
		String script = targetFiles.stream()
			.map(
				targetFile -> "mv \"" + absoluteSourcePath(targetFile).toString() + "\" \""
					+ absoluteTargetPath(targetDir, targetFile).toString() + "\"")
			.collect(Collectors.joining("\n"));
		logTarget.log("Script:\n==============\n" + script + "\n==============");
	}

	public boolean moveFiles(final Path targetDir, final List<TargetFile> targetFiles, final LogTarget logTarget) {
		boolean success = true;
		for (TargetFile targetFile : targetFiles) {
			Path absoluteSourcePath = absoluteSourcePath(targetFile);
			Path absoluteTargetPath = absoluteTargetPath(targetDir, targetFile);
			try {
				Files.move(absoluteSourcePath, absoluteTargetPath, StandardCopyOption.ATOMIC_MOVE);
				logTarget.log("moved \"" + absoluteSourcePath.toString() + "\" to \""
					+ absoluteTargetPath.toString() + "\"");
			} catch (AtomicMoveNotSupportedException e) {
				// try again without atomicity
				try {
					LOG.warn("can't move \"{}\" to \"{}\" in an atomic operation, now trying in standard mode",
						absoluteSourcePath.toString(), absoluteTargetPath.toString());
					Files.move(absoluteSourcePath, absoluteTargetPath);
					logTarget.log("moved \"" + absoluteSourcePath.toString() + "\" to \""
						+ absoluteTargetPath.toString() + "\"");
				} catch (FileAlreadyExistsException e1) {
					logTarget.log("ERROR: can't move \"" + absoluteSourcePath.toString() + "\" to \""
						+ absoluteTargetPath.toString() + "\" because the target already exists");
					success = false;
				} catch (IOException e1) {
					logTarget.log("ERROR: can't move \"" + absoluteSourcePath.toString() + "\" to \""
						+ absoluteTargetPath.toString() + "\": " + e1.getMessage());
					success = false;
				}
			} catch (FileAlreadyExistsException e) {
				logTarget.log("ERROR: can't move \"" + absoluteSourcePath.toString() + "\" to \""
					+ absoluteTargetPath.toString() + "\" because the target already exists");
				success = false;
			} catch (IOException e) {
				logTarget.log("ERROR: can't move \"" + absoluteSourcePath.toString() + "\" to \""
					+ absoluteTargetPath.toString() + "\": " + e.getMessage());
				success = false;
			}
		}
		return success;
	}

	public boolean createNewDirectory(final Path path, final LogTarget logTarget) {
		try {
			Files.createDirectories(path);
			return true;
		} catch (FileAlreadyExistsException e) {
			logTarget.log("ERROR: can't create directory " + path.toString()
				+ " because it already exists but as a file");
			return false;
		} catch (IOException e) {
			logTarget.log("ERROR: can't create directory " + path.toString() + ": " + e.getMessage());
			return false;
		}
	}

	private Path absoluteSourcePath(final TargetFile targetFile) {
		return targetFile.getSourceFile().getPath().toAbsolutePath();
	}

	private Path absoluteTargetPath(final Path targetDir, final TargetFile targetFile) {
		Path newFileName = Paths.get(numberFormat.format(targetFile.getTrackNumber()) + " - "
			+ targetFile.getSourceFile().getPath().getFileName().toString());
		return targetDir.resolve(newFileName).toAbsolutePath();
	}

	public Settings loadSettings() {
		Path settingsFile = Paths.get(SETTINGS_FILE_NAME);
		Settings ret = new Settings();
		if (Files.isRegularFile(settingsFile) && Files.isReadable(settingsFile)) {
			// read properties
			try {
				Properties properties = new Properties();
				properties.load(Files.newBufferedReader(settingsFile));
				if (properties.containsKey(SOURCE_DIRECTORY_PROPERTY_NAME)) {
					ret.setSourceDirectory(properties.getProperty(SOURCE_DIRECTORY_PROPERTY_NAME));
				}
				if (properties.containsKey(TARGET_DIRECTORY_PROPERTY_NAME)) {
					ret.setTargetDirectory(properties.getProperty(TARGET_DIRECTORY_PROPERTY_NAME));
				}
			} catch (IOException e) {
				LOG.warn("can't read settings file", e);
			}
		}
		return ret;
	}

	public void saveSettings(final Settings settings) {
		Path settingsFile = Paths.get(SETTINGS_FILE_NAME);
		// read properties
		try {
			Properties properties = new Properties();
			properties.put(SOURCE_DIRECTORY_PROPERTY_NAME, settings.getSourceDirectory());
			properties.put(TARGET_DIRECTORY_PROPERTY_NAME, settings.getTargetDirectory());
			properties.store(Files.newBufferedWriter(settingsFile), "AlbumCreator settings");
		} catch (IOException e) {
			LOG.warn("can't write settings file", e);
		}
	}
}

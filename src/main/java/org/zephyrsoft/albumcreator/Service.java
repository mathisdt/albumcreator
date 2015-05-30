package org.zephyrsoft.albumcreator;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zephyrsoft.albumcreator.model.TargetFile;

/**
 * Performs the work.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class Service {
	
	private static final Logger LOG = LoggerFactory.getLogger(Service.class);
	
	private final NumberFormat numberFormat;
	
	public Service() {
		numberFormat = NumberFormat.getIntegerInstance();
		numberFormat.setMinimumIntegerDigits(2);
	}
	
	public void createScript(Path targetDir, List<TargetFile> targetFiles, LogTarget logTarget) {
		String script = targetFiles.stream()
			.map(
				targetFile -> "mv \"" + absoluteSourcePath(targetFile).toString() + "\" \""
					+ absoluteTargetPath(targetDir, targetFile).toString() + "\"")
			.collect(Collectors.joining("\n"));
		logTarget.log("Script:\n==============\n" + script + "\n==============");
	}
	
	public boolean moveFiles(Path targetDir, List<TargetFile> targetFiles, LogTarget logTarget) {
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
	
	private Path absoluteSourcePath(TargetFile targetFile) {
		return targetFile.getSourceFile().getPath().toAbsolutePath();
	}
	
	private Path absoluteTargetPath(Path targetDir, TargetFile targetFile) {
		Path newFileName = Paths.get(numberFormat.format(targetFile.getTrackNumber()) + " - "
			+ targetFile.getSourceFile().getPath().getFileName().toString());
		return targetDir.resolve(newFileName).toAbsolutePath();
	}
	
}

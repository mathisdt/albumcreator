package org.zephyrsoft.albumcreator.model;

import com.google.common.base.StandardSystemProperty;

public class Settings {
	
	private String sourceDirectory = StandardSystemProperty.USER_HOME.value();
	private String targetDirectory = StandardSystemProperty.JAVA_IO_TMPDIR.value();
	
	public String getSourceDirectory() {
		return sourceDirectory;
	}
	
	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}
	
	public String getTargetDirectory() {
		return targetDirectory;
	}
	
	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}
	
}

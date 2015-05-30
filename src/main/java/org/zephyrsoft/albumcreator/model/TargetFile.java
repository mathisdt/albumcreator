package org.zephyrsoft.albumcreator.model;

public class TargetFile {
	private SourceFile sourceFile;
	/** filled only by {@link TargetTableModel} (1-based) */
	private int trackNumber;
	
	public TargetFile(SourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	public SourceFile getSourceFile() {
		return sourceFile;
	}
	
	public int getTrackNumber() {
		return trackNumber;
	}
	
	void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}
}

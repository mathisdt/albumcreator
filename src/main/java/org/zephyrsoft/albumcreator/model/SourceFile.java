package org.zephyrsoft.albumcreator.model;

import java.nio.file.Path;

public class SourceFile {
	private final Path path;
	private final String artist;
	private final String title;
	
	public SourceFile(Path path) {
		this.path = path;
		
		// parse artist and title
		String fileName = path.getFileName().toString();
		artist = fileName.replaceAll("\\s++-\\s.*$", "").replaceAll("\\.[^\\.]+$", "");
		title = fileName.replaceAll("^.*\\s-\\s++", "").replaceAll("\\.[^\\.]+$", "");
	}
	
	public Path getPath() {
		return path;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceFile other = (SourceFile) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	
}

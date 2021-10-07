package org.zephyrsoft.albumcreator.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TargetTableModel implements TableModel {

	private static final List<String> columns = Arrays.asList("Track", "Artist", "Title");

	private List<TargetFile> files = new ArrayList<>();
	private List<TableModelListener> listeners = new ArrayList<>();

	public void add(final TargetFile targetFile) {
		targetFile.setTrackNumber(files.size() + 1);
		files.add(targetFile);
		listeners.forEach(listener -> listener.tableChanged(new TableModelEvent(this, files.size() - 1)));
	}

	public void remove(final int rowIndex) {
		files.remove(rowIndex);
		// if a track was removed from the middle of the list, we have to renumber all
		// tracks
		for (int i = 0; i < files.size(); i++) {
			files.get(i).setTrackNumber(i + 1);
		}
		listeners.forEach(listener -> listener.tableChanged(new TableModelEvent(this, rowIndex)));
	}

	public void clear() {
		files.clear();
		listeners.forEach(listener -> listener.tableChanged(new TableModelEvent(this)));
	}

	public TargetFile get(final int rowIndex) {
		return files.get(rowIndex);
	}

	public List<TargetFile> getFiles() {
		return Collections.unmodifiableList(files);
	}

	@Override
	public int getRowCount() {
		return files.size();
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return columns.get(columnIndex);
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		TargetFile rowValue = files.get(rowIndex);
		return switch (columnIndex) {
			case 0 -> rowValue.getTrackNumber();
			case 1 -> rowValue.getSourceFile().getArtist();
			case 2 -> rowValue.getSourceFile().getTitle();
			default -> throw new IllegalArgumentException("illegal column index " + columnIndex);
		};
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		throw new UnsupportedOperationException("use add and remove methods");
	}

	@Override
	public void addTableModelListener(final TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTableModelListener(final TableModelListener l) {
		listeners.remove(l);
	}

}

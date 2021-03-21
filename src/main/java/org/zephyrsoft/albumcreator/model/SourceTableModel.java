package org.zephyrsoft.albumcreator.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SourceTableModel implements TableModel {

	private static final List<String> columns = Arrays.asList("Artist", "Title");

	private List<SourceFile> files = new ArrayList<>();
	private List<TableModelListener> listeners = new ArrayList<>();

	public void addAll(List<SourceFile> sourceFiles) {
		files.addAll(sourceFiles);
		listeners.forEach(listener -> listener.tableChanged(new TableModelEvent(this)));
	}

	public void clear() {
		files.clear();
		listeners.forEach(listener -> listener.tableChanged(new TableModelEvent(this)));
	}

	public List<SourceFile> getFiles() {
		return Collections.unmodifiableList(files);
	}

	public SourceFile get(int rowIndex) {
		return files.get(rowIndex);
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
	public String getColumnName(int columnIndex) {
		return columns.get(columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SourceFile rowValue = files.get(rowIndex);
		switch (columnIndex) {
			case 0:
				return rowValue.getArtist();
			case 1:
				return rowValue.getTitle();
			default:
				throw new IllegalArgumentException("illegal column index " + columnIndex);
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		throw new UnsupportedOperationException("use addAll method");
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

}

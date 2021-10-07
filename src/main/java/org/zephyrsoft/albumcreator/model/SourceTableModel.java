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

	public void addAll(final List<SourceFile> sourceFiles) {
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

	public SourceFile get(final int rowIndex) {
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
		SourceFile rowValue = files.get(rowIndex);
		return switch (columnIndex) {
			case 0 -> rowValue.getArtist();
			case 1 -> rowValue.getTitle();
			default -> throw new IllegalArgumentException("illegal column index " + columnIndex);
		};
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		throw new UnsupportedOperationException("use addAll method");
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

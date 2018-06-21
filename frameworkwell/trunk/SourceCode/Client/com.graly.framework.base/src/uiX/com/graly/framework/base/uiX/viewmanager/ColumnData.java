package com.graly.framework.base.uiX.viewmanager;

public class ColumnData {
	private String id;
	private String columnId;
	private String columnLabel;
	private int columnWidth=100;
	private Class<?> tableObjectClass;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getColumnId() {
		return columnId;
	}
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	public String getColumnLabel() {
		return columnLabel;
	}
	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}
	public Class<?> getTableObjectClass() {
		return tableObjectClass;
	}
	public void setTableObjectClass(Class<?> tableObjectClass) {
		this.tableObjectClass = tableObjectClass;
	}
	public int getColumnWidth() {
		return columnWidth;
	}
	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}
}

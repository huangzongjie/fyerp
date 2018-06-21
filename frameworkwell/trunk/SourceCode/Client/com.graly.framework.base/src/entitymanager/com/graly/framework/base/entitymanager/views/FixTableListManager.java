package com.graly.framework.base.entitymanager.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableViewerManager.ColumnSelectionListener;
import com.graly.framework.base.ui.layout.WeightedTableLayout;

public class FixTableListManager extends TableListManager {
	
	public FixTableListManager(ADTable adTable){
		super(adTable);
	}
	
	@Override
	protected void fillColumns(TableViewer tableViewer) {
        String[] columns = getColumns();
        String[] columnsHeaders = getColumnsHeader();
        Integer[] columnsSize = getColumnSize();
        Table table = tableViewer.getTable();
        SelectionListener listener = new ColumnSelectionListener();
        int totleSize = 0;
        if (columns != null) {
            for (int i = 0; i < columns.length; i++) {
                TableColumn column;
                column = new TableColumn(table, SWT.NONE);
                if (columnsHeaders != null) {
                    column.setText(columnsHeaders[i]);
                } else {
                    column.setText(columns[i]);
                }
                column.setData(COLUMN_ID, columns[i]);
                column.setWidth(1);
                column.setResizable(true);
                column.addSelectionListener(listener);
            }
        }
        int[] columnLayout = new int[columnsSize.length];
        for (int i = 0; i < columnsSize.length; i++){
        	columnLayout[i] = (int)columnsSize[i] * 3;
        }
    	table.setLayout(new WeightedTableLayout(null, columnLayout));
    }
}

package com.graly.erp.bj.inv.in.createfrom.po;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.layout.WeightedTableLayout;

public class CheckEntityTableManager extends EntityTableManager {
	private CheckboxTableViewer tv;
	private BJPoLineSelectSection parentPoLineSection;
 
	
	public CheckEntityTableManager(ADTable adTable, BJPoLineSelectSection parentPoLineSection){
		super(adTable);
		this.parentPoLineSection = parentPoLineSection;
	}
	
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit,
			String[] columns, String[] columnsHeaders, int[] columnsSize, int heightHint){
		Table table = toolkit.createTable(parent, style); 
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = heightHint;
		gd.widthHint = 100;
		table.setLayoutData(gd);
	    toolkit.paintBordersFor(parent);   
	    if ((style & SWT.CHECK) != 0) {
	    	tv = new CheckboxTableViewer(table);
	    }
		if(columns != null && columnsHeaders!= null && columnsSize != null && 
				columns.length == columnsHeaders.length && columnsHeaders.length== columnsSize.length){
			fillColumns(tv ,columns, columnsHeaders, columnsSize);
		} else {
			fillColumns(tv);
		}
		return tv;
	}
	
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
                totleSize += columnsSize[i];
                column.setData(columns[i], columns[i]);
                column.setWidth(1);
                column.setResizable(true);
                if(i == 0) {
                	column.addSelectionListener(listener);
                }
            }
        }
        int[] columnLayout = new int[columnsSize.length];
        for (int i = 0; i < columnsSize.length; i++){
        	columnLayout[i] = (int)(columnsSize[i]);
        }
    	table.setLayout(new WeightedTableLayout(columnLayout, columnLayout));
    }
	
	public class ColumnSelectionListener implements SelectionListener {
		private int CheckAll = 1, CheckNull = 0, Checked = 1;

        public void widgetDefaultSelected(SelectionEvent e) {
        	widgetSelected(e);
        }
        public void widgetSelected(SelectionEvent e) {
        	TableColumn column = (TableColumn) e.widget;
            	switch (Checked) {
            	case 0 : {
            		Object[] objs = tv.getCheckedElements();
            		for(Object obj : objs) {
            			tv.setChecked(obj, false);	        			
            		}
            		updateParentSection(false);
            		this.setChecked(CheckAll);
            		break;
            	}
            	case 1 : {
            		tv.setCheckedElements(getObjects());
            		updateParentSection(true);
            		this.setChecked(CheckNull);
            	}
            	}
        }        
        protected Object[] getObjects() {
        	List<Object> lots = new ArrayList<Object>();
        	TableItem[] items = tv.getTable().getItems();
        	for(TableItem it : items) {
        		lots.add(it.getData());
        	}
        	return lots.toArray();
        }        
        public void setChecked(int checked) {
        	this.Checked = checked;
        }
    }
	
	private void updateParentSection(boolean isChecked) {
		if(parentPoLineSection != null) {
			parentPoLineSection.updateParentPage(isChecked);
		}
	}

}

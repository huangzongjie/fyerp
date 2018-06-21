package com.graly.erp.inv.in;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.layout.WeightedTableLayout;

public class LotCheckTableManager extends TableListManager {
//	private CheckboxTableViewer tv;

	public LotCheckTableManager(ADTable adTable, int style) {
		super(adTable, style);
	}

	public LotCheckTableManager(ADTable adTable) {
		super(adTable);
	}
	
//	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit,
//			String[] columns, String[] columnsHeaders, int[] columnsSize, int heightHint){
//		Table table = toolkit.createTable(parent, style); 
//		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
//		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
//		gd.heightHint = heightHint;
//		gd.widthHint = 100;
//		table.setLayoutData(gd);
//	    toolkit.paintBordersFor(parent);   
//	    if ((style & SWT.CHECK) != 0) {
//	    	tv = new CheckboxTableViewer(table);
//	    }
//		if(columns != null && columnsHeaders!= null && columnsSize != null && 
//				columns.length == columnsHeaders.length && columnsHeaders.length== columnsSize.length){
//			fillColumns(tv ,columns, columnsHeaders, columnsSize);
//		} else {
//			fillColumns(tv);
//		}
//		return tv;
//	}
	
    protected void fillColumns(TableViewer tableViewer) {
        String[] columns = getColumns();
        String[] columnsHeaders = getColumnsHeader();
        Integer[] columnsSize = getColumnSize();
        Table table = tableViewer.getTable();
        
        CheckboxTableViewer checkViewer = null;
    	if(tableViewer instanceof CheckboxTableViewer) {
    		checkViewer = (CheckboxTableViewer)tableViewer;
    	}
        SelectionListener listener = createSelectionListener(checkViewer);
        
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
    
    protected SelectionListener createSelectionListener(CheckboxTableViewer checkViewer) {
    	return new ColumnSelectionListener(checkViewer);
    }
	
	public class ColumnSelectionListener implements SelectionListener {
		private int CheckAll = 1, CheckNull = 0, Checked = 1;
		protected CheckboxTableViewer checkViewer;
		
		public ColumnSelectionListener(CheckboxTableViewer checkViewer) {
			this.checkViewer = checkViewer;
		}

        public void widgetDefaultSelected(SelectionEvent e) {
        	widgetSelected(e);
        }
        public void widgetSelected(SelectionEvent e) {
        	if(checkViewer != null) {
        		switch (Checked) {
        		case 0 : {
        			Object[] objs = checkViewer.getCheckedElements();
        			for(Object obj : objs) {
        				checkViewer.setChecked(obj, false);	        			
        			}
        			this.setChecked(CheckAll);
        			break;
        		}
        		case 1 : {
        			checkViewer.setCheckedElements(getObjects());
        			this.setChecked(CheckNull);
        		}
        		}        		
        	}
        }        
        protected Object[] getObjects() {
        	List<Object> lots = new ArrayList<Object>();
        	if(checkViewer != null) {
        		TableItem[] items = checkViewer.getTable().getItems();
        		for(TableItem it : items) {
        			lots.add(it.getData());
        		}
        	}
        	return lots.toArray();        		
        }
        
        public void setChecked(int checked) {
        	this.Checked = checked;
        }
    }

}

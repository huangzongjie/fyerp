package com.graly.promisone.base.entitymanager.views;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import com.graly.promisone.base.ui.layout.WeightedTableLayout;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.activeentity.model.ADField;
import com.graly.promisone.base.entitymanager.editor.EntityEditorInput;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.views.AbstractViewerComparator;
import com.graly.promisone.base.ui.views.ItemAdapter;
import com.graly.promisone.base.ui.views.StructuredViewerManager;

public abstract class TableViewerManager extends StructuredViewerManager {
	
    protected Color evenForeground = new Color(null, 0, 0, 0);
    protected Color evenBackground = new Color(null, 255, 255, 255);
    protected Color oddForeground = new Color(null, 0, 0, 0);
    protected Color oddBackground = new Color(null, 235, 241, 253);
    
	private ADTable adTable;
	protected int style;
	
	public TableViewerManager(ADTable adTable) {
		this();
		this.adTable = adTable;
	}
	
    public TableViewerManager() {
        style = SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
    }
    
    public TableViewerManager(int style) {
        this.style = style;
    }
    
    public int getStyle() {
        return style;
    }
    
    public void setStyle(int style) {
        this.style = style;
    }

    public void addStyle(int style) {
        this.style = this.style | style;
    }
    
    @Override
    protected String[] getColumns() {
    	if (getADTable() instanceof ADTable){
        	List<String> columnsList = new ArrayList<String>();
        	ADTable table = getADTable();
        	for (ADField field : table.getFields()){
        		if (field.getIsMain() && field.getIsDisplay()){
        			columnsList.add(field.getName());
        		}
        	}
        	//return keyFirst ? witchKey(columnsList.toArray(new String[]{})) : columnsList.toArray(new String[]{});
        	return columnsList.toArray(new String[]{});
        }
    	return new String[]{};
    }
    
    @Override
    protected String[] getColumnsHeader() {
    	if (getADTable() instanceof ADTable){
        	List<String> columnsHeaderList = new ArrayList<String>();
        	ADTable table = getADTable();
        	for (ADField field : table.getFields()){
        		if (field.getIsMain() && field.getIsDisplay()){
        			columnsHeaderList.add(I18nUtil.getI18nMessage(field, "label"));
        		}
        	}
        	return columnsHeaderList.toArray(new String[]{});
        }
    	return new String[]{};
    }
    
    protected Integer[] getColumnSize() {
    	if (getADTable() instanceof ADTable){
    		List<Integer> size = new ArrayList<Integer>();
        	ADTable table = getADTable();
        	int i = 0;
        	for (ADField field : table.getFields()){
        		if (field.getIsMain() && field.getIsDisplay()){
        			size.add(new Integer((field.getDisplayLength() == null ? 32 : field.getDisplayLength().intValue())));
        		}
        	}
        	return size.toArray(new Integer[]{});
        }
    	return new Integer[]{};
    }
    
    @Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit,
    			String[] columns, String[] columnsHeaders, int[] columnsSize){
    	Table table = toolkit.createTable(parent, style); 
    	table.setHeaderVisible(true);
    	table.setLinesVisible(true);
    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 200;
		gd.widthHint = 100;
    	table.setLayoutData(gd);
	    toolkit.paintBordersFor(parent);   
	    TableViewer tv;
	    if ((style & SWT.CHECK) != 0) {
	    	tv = new CheckboxTableViewer(table);
	    } else {
	    	tv = new TableViewer(table);
	    }       
        
    	if(columns != null && columnsHeaders!= null && columnsSize != null && 
    			columns.length == columnsHeaders.length && columnsHeaders.length== columnsSize.length){
    		fillColumns(tv ,columns, columnsHeaders, columnsSize);
    	} else {
    		fillColumns(tv);
    	}
    	return tv;
	}
    
    @Override
    protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit) {
    	return newViewer(parent, toolkit, null, null, null);
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
                column.setData("id", columns[i]);
                column.setWidth(1);
                column.setResizable(true);
                column.addSelectionListener(listener);
            }
        }
        int[] columnLayout = new int[columnsSize.length];
        for (int i = 0; i < columnsSize.length; i++){
        	columnLayout[i] = (int)(columnsSize[i] * 100/totleSize);
        }
    	table.setLayout(new WeightedTableLayout(columnLayout));
    }
    
    public void fillColumns(TableViewer tableViewer, String[] columns, 
    							String[] columnsHeaders, int[] columnsSize){
    	Table table = tableViewer.getTable();
        SelectionListener listener = new ColumnSelectionListener();
        int totleSize = 0;
        if (columns != null) {
            for (int i = 0; i < columns.length; i++) {
                TableColumn column;
                column = new TableColumn(table, SWT.NULL);
                if (columnsHeaders != null) {
                    column.setText(columnsHeaders[i]);
                } else {
                    column.setText(columns[i]);
                }
                totleSize += columnsSize[i];
                column.setData("id", columns[i]);
                column.setWidth(1);
                column.setResizable(true);
                column.addSelectionListener(listener);
            }
        }
        int[] columnLayout = new int[columnsSize.length];
        for (int i = 0; i < columnsSize.length; i++){
        	columnLayout[i] = (int)(columnsSize[i] * 100/totleSize);
        }
    	table.setLayout(new WeightedTableLayout(columnLayout));
    }
    
	public ADTable getADTable() {
		return adTable;
	}
	
	public void updateView(StructuredViewer viewer) {
		Table table = ((TableViewer)viewer).getTable();
		table.setRedraw(false);
		int index = 0;
		for (TableItem tableItem : table.getItems()){
            tableItem.setBackground(((index & 1) == 1) ? oddBackground : evenBackground);
            tableItem.setForeground(((index & 1) == 1) ? oddForeground : evenForeground);
            index++;
		}
		table.setRedraw(true);
    }
	
	public class ColumnSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        public void widgetSelected(SelectionEvent e) {
            TableColumn column = (TableColumn) e.widget;
            Table table = column.getParent();
            String columnName = (String) column.getData("id");

            AbstractViewerComparator tableViewerComparator;
            if (viewer.getComparator() == null){
            	tableViewerComparator = new AdapterBasedViewerComparator(null);
            	viewer.setComparator(tableViewerComparator);
            } else {
            	tableViewerComparator = (AbstractViewerComparator)viewer.getComparator();
            }
            tableViewerComparator.doSort(columnName);
            viewer.refresh();
            updateView(viewer);
            table.setSortColumn(column);
            table.setSortDirection((tableViewerComparator.direction == AbstractViewerComparator.ASCENDING) ? SWT.UP
                    : SWT.DOWN);
        }
    }
	
	class AdapterBasedViewerComparator extends AbstractViewerComparator {

	    public AdapterBasedViewerComparator(Hashtable<String, Comparator<Object>> map) {
	        super(map);
	    }

	    @Override
	    protected Object getObjectToCompare(Viewer viewer, Object o) {
	        StructuredViewerManager manager = (StructuredViewerManager) viewer.getData(StructuredViewerManager.DATA_KEY_MANAGER);
	        if ( manager != null){
	            ItemAdapter adapter = manager.adapterFactory.getAdapter(o.getClass());
	            String text = adapter.getText(o, sortingColumn);
	            if (text == null) {
	            	return "";
	            }
	            return text;
	        }
	        return null;
	    }
	}
}

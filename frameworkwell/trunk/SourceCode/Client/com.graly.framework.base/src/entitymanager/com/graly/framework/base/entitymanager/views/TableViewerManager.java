package com.graly.framework.base.entitymanager.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.layout.WeightedTableLayout;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.views.AbstractViewerComparator;
import com.graly.framework.base.ui.views.ItemAdapter;
import com.graly.framework.base.ui.views.StructuredViewerManager;
import com.graly.framework.base.ui.views.AbstractViewerComparator.CompareType;

public abstract class TableViewerManager extends StructuredViewerManager {
	
    protected Color evenForeground = new Color(null, 0, 0, 0);
    protected Color evenBackground = new Color(null, 255, 255, 255);
    protected Color oddForeground = new Color(null, 0, 0, 0);
    protected Color oddBackground = new Color(null, 235, 241, 253);
    
	private ADTable adTable;
	protected int style;
	protected TableQueryDialog queryDialog;
	
	protected AdapterBasedViewerComparator stringComparator;	//字符型比较器
	protected NumericViewerComparator numbericComparator;		//数值型比较器
	
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
    	if(columns != null && columns.length > 0){
    		return columns;
    	}
    	if (getADTable() != null && getADTable() instanceof ADTable){
        	List<String> columnsList = new ArrayList<String>();
        	ADTable table = getADTable();
        	for (ADField field : table.getFields()){
        		if (field.getIsMain() && field.getIsDisplay()){
        			columnsList.add(field.getName());
        		}
        	}
        	//return keyFirst ? witchKey(columnsList.toArray(new String[]{})) : columnsList.toArray(new String[]{});
        	return columnsList.toArray(new String[]{});
        }else{
        	
        }
    	return new String[]{};
    }
    
    @Override
    protected String[] getColumnsHeader() {
    	if(columnsHeaders != null && columnsHeaders.length > 0){
    		return columnsHeaders;
    	}
    	if (getADTable() != null && getADTable() instanceof ADTable){
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
        	for (ADField field : table.getFields()){
        		if (field.getIsMain() && field.getIsDisplay()){
        			size.add(new Integer((field.getDisplayLength() == null ? 32 : field.getDisplayLength().intValue())));
        		}
        	}
        	return size.toArray(new Integer[]{});
        }
    	return new Integer[]{};
    }
    
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit,
    			String[] columns, String[] columnsHeaders, int[] columnsSize, int heightHint){
		this.columns = columns;
		this.columnsHeaders = columnsHeaders;
    	Table table = toolkit.createTable(parent, style); 
    	table.setHeaderVisible(true);
    	table.setLinesVisible(true);
//    	table.addListener(SWT.MeasureItem, new Listener() {
//			public void handleEvent(Event event) {
//				event.height = event.gc.getFontMetrics().getHeight() * 4 / 3;
//			}
//		});
    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = heightHint;
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
    	
    	registerAccelerator(table);
    	
    	return tv;
	}
    
	protected void registerAccelerator(final Table table) {
		table.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 'f') {
					if (queryDialog != null) {
						queryDialog.setVisible(true);
					} else {
						queryDialog =  new TableQueryDialog(UI.getActiveShell(), table);
						queryDialog.open();
					}
		        }			
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	
	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit) {
		return newViewer(parent, toolkit, null, null, null);
	}
	 
    @Override
    protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit, int heightHint) {
    	return newViewer(parent, toolkit, null, null, null, heightHint);
    }
    
    @Override
    protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit,
			String[] columns, String[] columnsHeaders, int[] columnsSize){
    	return newViewer(parent, toolkit, columns, columnsHeaders, columnsSize, 180);
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
                column.setData(COLUMN_ID, columns[i]);
                column.setWidth(1);
                column.setResizable(true);
                column.addSelectionListener(listener);
            }
        }
        int[] columnLayout = new int[columnsSize.length];
        for (int i = 0; i < columnsSize.length; i++){
        	columnLayout[i] = (int)(columnsSize[i]);
        }
    	table.setLayout(new WeightedTableLayout(columnLayout, columnLayout));
    	tableViewer.setColumnProperties(columns);//将每个列与EntityBean的属性对应起来
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
        	columnLayout[i] = (int)(columnsSize[i]);
        }
    	table.setLayout(new WeightedTableLayout(columnLayout, columnLayout));
    	tableViewer.setColumnProperties(columns);//将每个列与EntityBean的属性对应起来
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

	public AdapterBasedViewerComparator getStringComparator() {
		return stringComparator;
	}

	public void setStringComparator(AdapterBasedViewerComparator stringComparator) {
		this.stringComparator = stringComparator;
	}

	public NumericViewerComparator getNumbericComparator() {
		return numbericComparator;
	}

	public void setNumbericComparator(NumericViewerComparator numbericComparator) {
		this.numbericComparator = numbericComparator;
	}
	
	//如果该字段的数据类型为integer或double，或者字段的名称以qty开头或以Qty结尾，则将该字段是为数值类型
    protected boolean isNumbericType(String sortingColumn) {
    	if(adTable != null) {
    		for(ADField f : adTable.getFields()) {
    			if(f.getName().equals(sortingColumn)) {
    				if(NumericViewerComparator.NUMERIC_DOUBLE.equalsIgnoreCase(f.getDataType())
    		    			|| NumericViewerComparator.NUMERIC_INTEGER.equals(f.getDataType()))
    					return true;
    			}
    		}
    	}
    	if(sortingColumn != null) {
    		if(sortingColumn.startsWith(NumericViewerComparator.QTY_PREFIX)
    			|| sortingColumn.endsWith(NumericViewerComparator.QTY_SUFFIX)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    protected void doSort(Table table, TableColumn column, String columnName) {
        AbstractViewerComparator tableViewerComparator;
        CompareType comType;
        /*if (viewer.getComparator() == null) {
        	tableViewerComparator = new AdapterBasedViewerComparator(null);
        	viewer.setComparator(tableViewerComparator);
        } else {
        	tableViewerComparator = (AbstractViewerComparator)viewer.getComparator();
        }*/
        if(isNumbericType(columnName)) {
        	if(getNumbericComparator() == null) {
        		Hashtable<String, Comparator<Object>> ht = new Hashtable<String, Comparator<Object>>();
        		setNumbericComparator(new NumericViewerComparator(ht));
        	}
        	if(!getNumbericComparator().containsColumns(columnName)) {
        		//如果不含有栏位columnName的比较器，则为columnName添加一个比较器
        		getNumbericComparator().putComparatorMap(columnName);            		
        	}
            tableViewerComparator = getNumbericComparator();
            comType = CompareType.NUMBERIC;
        }
        else {
        	if(getStringComparator() == null) {
        		setStringComparator(new AdapterBasedViewerComparator(null));
        	}
        	tableViewerComparator = getStringComparator();
        	comType = CompareType.CHARACTER;
        }
        
        viewer.setComparator(tableViewerComparator);
        tableViewerComparator.doSort(columnName, comType);
        viewer.refresh();
        updateView(viewer);
        table.setSortColumn(column);
        table.setSortDirection((tableViewerComparator.direction == AbstractViewerComparator.ASCENDING) ? SWT.UP
                : SWT.DOWN);
    }
	
	public class ColumnSelectionListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        public void widgetSelected(SelectionEvent e) {
            TableColumn column = (TableColumn) e.widget;
            Table table = column.getParent();
            String columnName = (String) column.getData("id");
            doSort(table, column, columnName);
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

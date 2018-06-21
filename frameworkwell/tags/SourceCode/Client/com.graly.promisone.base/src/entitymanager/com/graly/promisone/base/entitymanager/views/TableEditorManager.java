package com.graly.promisone.base.entitymanager.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADField;
import com.graly.promisone.activeentity.model.ADRefList;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.entitymanager.adapter.EntityItemAdapter;
import com.graly.promisone.base.ui.forms.field.FieldType;
import com.graly.promisone.base.ui.layout.WeightedTableLayout;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.views.ItemAdapterFactory;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.exceptionhandler.ExceptionHandlerManager;

public class TableEditorManager extends TableViewerManager {
	
	Map<String, CellEditor> editorMap = new HashMap<String, CellEditor>();
	
	public TableEditorManager(ADTable adTable) {
		super(adTable);
		super.addStyle( SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
	}
    
	protected CellEditor[] getColumnCellEditor(Table table) {
    	if (getADTable() instanceof ADTable){
    		List<CellEditor> editorList = new ArrayList<CellEditor>();
        	ADTable adTable = getADTable();
        	int i = 0;
        	CellEditor editor;
        	for (ADField adField : adTable.getFields()){
        		if (adField.getIsMain() && adField.getIsDisplay()){
        			String displayText = adField.getDisplayType();
        			if (FieldType.TEXT.equalsIgnoreCase(displayText)){
        				editor = new TextCellEditor(table);	
        			} else if (FieldType.COMBO.equalsIgnoreCase(displayText) || 
	    					FieldType.DROPDOWNLIST.equalsIgnoreCase(displayText) ||
	    					FieldType.LIST.equalsIgnoreCase(displayText) || 
	    					FieldType.RADIO.equalsIgnoreCase(displayText)){
        				List<String> itemList = new ArrayList<String>();
        				try {
	    					if (adField.getReferenceName() != null && !"".equalsIgnoreCase(adField.getReferenceName().trim())){
		    					ADManager entityManager = Framework.getService(ADManager.class);
		    					List<ADRefList> refList = entityManager.getADRefList(0, adField.getReferenceName());
		    					for (ADRefList listItem : refList){
		    						itemList.add(listItem.getName());
		    					}
	    					}
	    		        } catch (Exception e) {
	    		        	ExceptionHandlerManager.asyncHandleException(e);
	    		        }
	    		        if (FieldType.DROPDOWNLIST.equalsIgnoreCase(displayText)){
	    		        	editor = new ComboBoxCellEditor(table, itemList.toArray(new String[]{}), SWT.BORDER | SWT.READ_ONLY);
	    		        } else {
	    		        	editor = new ComboBoxCellEditor(table, itemList.toArray(new String[]{}));
	    		        }
        			} else if (FieldType.BOOLEAN.equalsIgnoreCase(displayText)){
        				editor = new CheckboxCellEditor(table);
        			} else {
        				editor = new TextCellEditor(table);
        			}
        			editorList.add(editor);
        			editorMap.put(adField.getName(), editor);
        		}
        	}
        	//return keyFirst ? witchKey(editorList.toArray(new CellEditor[]{})) : editorList.toArray(new CellEditor[]{});
        	return editorList.toArray(new CellEditor[]{});
        }
    	return new CellEditor[]{};
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
                totleSize += columnsSize[i];
                column.setWidth(1);
                column.setResizable(true);
                column.addSelectionListener(listener);
            }
        }
        int[] columnLayout = new int[columnsSize.length];
        for (int i = 0; i < columnsSize.length; i++){
        	columnLayout[i] = (int)(columnsSize[i] * 100/totleSize);
        }
        tableViewer.setColumnProperties(columns);
        tableViewer.setCellEditors(getColumnCellEditor(table));
        tableViewer.setCellModifier(new TableCellModifier(tableViewer));
    	table.setLayout(new WeightedTableLayout(columnLayout));
    }
    
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
	        factory.registerAdapter(ADBase.class, new EntityItemAdapter());
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }
	
	private class TableCellModifier implements ICellModifier
	{
		private TableViewer viewer ;

		public TableCellModifier(TableViewer viewer){
	        this.viewer = viewer ;
	    }

		public boolean canModify(Object element, String property){
			ADTable adTable = getADTable();
        	for (ADField adField : adTable.getFields()){
        		String name = adField.getName();
        		if (property.equalsIgnoreCase(name)){
        			if (adField.getIsReadonly()){
        				return false;
        			}
        			return true;
        		}
        	}
			return false;
		}

		@SuppressWarnings("unchecked") 
		public Object getValue(Object element, String property) {
			ADBase object = (ADBase)element;
			CellEditor editor = editorMap.get(property);
			if (editor instanceof TextCellEditor){
				return PropertyUtil.getPropertyForIField(object, property);
			} else if (editor instanceof ComboBoxCellEditor){
				String item = (String)PropertyUtil.getPropertyForIField(object, property);
				String[] items = ((ComboBoxCellEditor)editor).getItems();
				for(int i = 0;i < items.length; i++){
					if(items[i].equals(item)){
						return i;
		            }
		        }
				return -1;
			} else if (editor instanceof CheckboxCellEditor){
				return PropertyUtil.getPropertyForIField(object, property);
			} else {
				return null;
			}
		}

		@SuppressWarnings("unchecked") 
		public void modify(Object element, String property, Object value){
			TableItem item = (TableItem)element;
			ADBase object = (ADBase)item.getData();
			CellEditor editor = editorMap.get(property);
			String text = "";
			if (editor instanceof TextCellEditor){
				PropertyUtil.setProperty(object, property, value);
				text = (String)value;
			} else if (editor instanceof ComboBoxCellEditor){
				Integer index = (Integer)value;
	            if(index.intValue() == -1){
	            	return ;
	            }
	            String[] items = ((ComboBoxCellEditor)editor).getItems();
	            String itemValue = items[index];
	            PropertyUtil.setProperty(object, property, itemValue);
	            text = (String)itemValue;
			} else if (editor instanceof CheckboxCellEditor){
				PropertyUtil.setProperty(object, property, value);
				text = (String)value;
			} else {
				text = (String)value;
			}
			String[] columns = getColumns();
			for(int i = 0;i < columns.length; i++){
				if(columns[i].equals(property)){
					item.setText(i, text);
					viewer.update(element, new String[] { property });
	            }
	        }
			
		}
	}
}

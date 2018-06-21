package com.graly.framework.base.entitymanager.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.ListItemAdapter;

public class AdvanceQueryTableManager extends TableViewerManager {
	
	private static final Logger logger = Logger.getLogger(AdvanceQueryTableManager.class);
	
	public String Header_Field = Message.getString("common.field");
	public String Header_Comparator = Message.getString("common.comparator");
	public String Header_Value = Message.getString("common.value");
	
	public static String FIELD_ID_FILED = "field";
	public static String FIELD_ID_COMPARATOR = "comparator";
	public static String FIELD_ID_VALUE = "value";
	
	private CellEditor[] cellEditor;
	protected TableViewer tableViewer;
	private List<String> fieldItems;
	private List<String> comparatorItems;
	private HashMap<String, ADField> fieldMap;
//	private HashMap<String, ADField> comparatorMap;

	public AdvanceQueryTableManager(ADTable adTable) {
		super(adTable);
	}

	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit, int h) {
//		final TableViewer viewer = (TableViewer) super.newViewer(parent, toolkit, 160);
		Table table = toolkit.createTable(parent, style); 
    	table.setHeaderVisible(true);
    	table.setLinesVisible(true);
    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 160;
		gd.widthHint = 300;
    	table.setLayoutData(gd);
	    toolkit.paintBordersFor(parent);   
	    TableViewer viewer;
	    viewer = new TableViewer(table);
    	fillColumns(viewer);
		viewer.getTable().addListener(SWT.MeasureItem, new Listener() {
    		public void handleEvent (Event event) {
    			event.height = event.gc.getFontMetrics().getHeight() * 3/2;
    		}
    	});
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		TableViewerEditor.create(viewer, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
		setCellEditor(viewer);
		return viewer;
	}

	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
        	ListItemAdapter<Object> itemAdapter = new ListItemAdapter<Object>();
	        factory.registerAdapter(List.class, itemAdapter);
	        factory.registerAdapter(AdvanceQueryEntity.class, itemAdapter);
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }
	
	private void setCellEditor(TableViewer tableViewer) {
		int size = this.getColumns().length;
		cellEditor = new CellEditor[size];
		String[] properties = new String[size];
		for (int i = 0; i < size; i++) {
			String column = (String) tableViewer.getTable().getColumn(i).getData(TableViewerManager.COLUMN_ID);
			
			if (FIELD_ID_FILED.equals(column)) {
				ComboBoxCellEditor comboEditor = new ComboBoxCellEditor(tableViewer.getTable(),
						getFiledItems(), SWT.NULL | SWT.READ_ONLY);
				cellEditor[i] = comboEditor;
			} else if(FIELD_ID_COMPARATOR.equals(column)) {
				ComboBoxCellEditor comboEditor = new ComboBoxCellEditor(tableViewer.getTable(),
						getComparatorItems(),  SWT.NULL | SWT.READ_ONLY);
				cellEditor[i] = comboEditor;
			} else if(FIELD_ID_VALUE.equals(column)) {
				cellEditor[i] = new AdvanceQueryTextCellEditor(tableViewer);
			}
			properties[i] = column;
		}
		tableViewer.setColumnProperties(properties);
		tableViewer.setCellEditors(cellEditor);
		AdvanceQueryCellModifier ccm = new AdvanceQueryCellModifier(tableViewer);
		tableViewer.setCellModifier(ccm);
	}
	
	protected String[] getFiledItems() {
		fieldItems = new ArrayList<String>();
		fieldMap = new LinkedHashMap<String, ADField>();
		int i = 0;
		for(ADField adField : getADTable().getFields()) {
			if(adField.getIsAdvanceQuery()) {
				String field = I18nUtil.getI18nMessage(adField, "label");
				fieldMap.put(field, adField);
				fieldItems.add(field);
				i++;
			}
		}
		String[] strs = new String[fieldItems.size()];
		return fieldItems.toArray(strs);
	}
	
	protected String[] getComparatorItems() {
		comparatorItems = new ArrayList<String>();
		String[] coms = new String[] {EntityComparator.ID_EQUALS, EntityComparator.ID_GREATER_THAN,
				EntityComparator.ID_GREATER_THAN_EQUAL_TO, EntityComparator.ID_LESS_THAN,
				EntityComparator.ID_LESS_THAN_EQUAL_TO, EntityComparator.ID_LIKE,
				EntityComparator.ID_NOT_EQUAL, 
				EntityComparator.ID_IS_NULL, EntityComparator.ID_NOT_NULL
				
		};
		for(int i = 0; i < coms.length; i++ ) {
			comparatorItems.add(coms[i]);
		}
		return coms;
	}

	private class AdvanceQueryCellModifier implements ICellModifier {		
		TableViewer tableViewer;
		
		public AdvanceQueryCellModifier(TableViewer tableViewer) {
			this.tableViewer = tableViewer;
		}

		@Override
		public boolean canModify(Object element, String property) {
			return true;
		}

		@Override
		public Object getValue(Object element, String property) {
			if(element instanceof AdvanceQueryEntity) {
				AdvanceQueryEntity aqe = (AdvanceQueryEntity)element;
				if(FIELD_ID_FILED.equals(property)) {
					return getFieldValue(aqe.getField());					
				} else if(FIELD_ID_COMPARATOR.equals(property)) {
					return getComparatorValue(aqe.getComparator());
				} else if(FIELD_ID_VALUE.equals(property) && aqe.getValue() != null) {
					return aqe.getValue();
				}
			}
			return "";
		}

		@Override
		public void modify(Object element, String property, Object value) {
			AdvanceQueryEntity aqe = (AdvanceQueryEntity)((TableItem)element).getData();
			if(FIELD_ID_FILED.equals(property)) {
				// 重新设置栏位(Field)和其对应的ADField
				String newField = fieldItems.get((Integer)value);
				aqe.setField(newField);
				ADField adField = fieldMap.get(newField);
				aqe.setDataType(adField.getDataType());
				aqe.setData(adField);
			} else if(FIELD_ID_COMPARATOR.equals(property)) {
				// 重新设置运算符
				aqe.setComparator(comparatorItems.get((Integer)value));
			}
			tableViewer.refresh();
		}
	}
	
	public Integer getFieldValue(String field) {
		Integer i = new Integer(0);
		if(field != null && fieldItems.indexOf(field) != -1) {
			i = new Integer(fieldItems.indexOf(field));
		}
		return i;
	}
	
	public Integer getComparatorValue(String comparator) {
		Integer i = new Integer(0);
		if(comparator != null && comparatorItems.indexOf(comparator) != -1) {
			i = comparatorItems.indexOf(comparator);
		}
		return i;
	}

	@Override
    protected String[] getColumns() {
    	return new String[]{FIELD_ID_FILED, FIELD_ID_COMPARATOR, FIELD_ID_VALUE};
    }

    @Override
    protected String[] getColumnsHeader() {
    	return new String[]{Header_Field, Header_Comparator, Header_Value};
    }

    protected Integer[] getColumnSize() {
    	return new Integer[]{38, 24, 32};
    }

}

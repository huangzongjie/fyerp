package com.graly.erp.inv.in.approve;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
/**
 * @author Jim
 *	实现在表格中可以输入发票金额
 */
public class ApprovedInvoiceTableManager extends TableListManager {
	protected CellEditor[] cellEditor;
	private int columnCout;

	public ApprovedInvoiceTableManager(ADTable adTable, int style) {
		super(adTable, style);
	}

	public ApprovedInvoiceTableManager(ADTable adTable) {
		super(adTable);
	}
	
	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit, int heightHint) {
		TableViewer viewer = (TableViewer)super.newViewer(parent, toolkit, heightHint);
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

	protected void setCellEditor(TableViewer tableViewer) {
		columnCout = this.getColumns().length;
		cellEditor = new CellEditor[columnCout];
		String[] properties = new String[columnCout];
		HashMap<String, ADField> proMaps = this.getEditableProperties();
		String validateType = "String";
		
		for (int i = 0; i < columnCout; i++) {
			String property = (String)tableViewer.getTable().getColumn(i).getData(COLUMN_ID);
			if(proMaps.keySet().contains(property)) {
				properties[i] = property;
				if(proMaps.get(property) != null && proMaps.get(property).getDataType() != null) {
					validateType = proMaps.get(property).getDataType();
				}
				cellEditor[i] = new ModifyTextCellEditor(tableViewer, property, validateType, i);				
			}
		}
		tableViewer.setColumnProperties(properties);
		tableViewer.setCellEditors(cellEditor);
		EntityPropertyCellModify tcm = new EntityPropertyCellModify(tableViewer,
				new EntityPropertyEdit(proMaps.keySet()));
		tableViewer.setCellModifier(tcm);
	}

	protected HashMap<String, ADField> getEditableProperties() {
		HashMap<String, ADField> prosMap = new LinkedHashMap<String, ADField>();
		if(getADTable() != null) {
			for(ADField adField : getADTable().getFields()) {
				if(adField.getIsEditable()) {
					prosMap.put(adField.getName(), adField);
				}
			}
		}
		return prosMap;
	}
}

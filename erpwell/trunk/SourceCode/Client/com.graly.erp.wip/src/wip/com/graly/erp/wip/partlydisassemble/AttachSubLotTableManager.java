package com.graly.erp.wip.partlydisassemble;

import java.math.BigDecimal;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.wip.workcenter.receive.TextProvider;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.mes.wip.model.Lot;

public class AttachSubLotTableManager extends TableListManager {
	String DisplayName_LotQty = "qtyTransaction";
	String lotType;
	
	private AttachSubLotSection parentSection;
	private CellEditor[] cellEditor;
	private boolean canEdit = true;
	
	public AttachSubLotTableManager(AttachSubLotSection parentSection) {
		super(null);
		super.addStyle( SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		this.parentSection = parentSection;
	}
	
	public AttachSubLotTableManager(ADTable adTable, int style) {
		super(adTable);
		super.addStyle(style);
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
		int columnCout = this.getColumns().length;
		cellEditor = new CellEditor[columnCout];
		String[] properties = new String[columnCout];
		for (int i = 0; i < columnCout; i++) {
			String columnHeader = (String)tableViewer.getTable().getColumn(i).getData(COLUMN_ID);
			if(DisplayName_LotQty.equals(columnHeader)) {
				properties[i] = columnHeader;
				cellEditor[i] = new ModifyTextCellEditor(tableViewer, DisplayName_LotQty);				
			} else {
				properties[i] = columnHeader;
				cellEditor[i] = null;
			}
		}
		tableViewer.setColumnProperties(properties);
		tableViewer.setCellEditors(cellEditor);
		TextCellModifier tcm = new TextCellModifier(tableViewer, "");
		tableViewer.setCellModifier(tcm);
	}
	
    @Override
    protected String[] getColumns() {
    	return new String[]{TextProvider.FieldName_LotId,
    			TextProvider.FieldName_MaterialId,
    			TextProvider.FieldName_MaterialName,
    			TextProvider.FieldName_ConsumeQty};
    }
    
    @Override
    protected String[] getColumnsHeader() {
    	return new String[]{TextProvider.LotId, TextProvider.MaterialId,
    			TextProvider.MaterialName, TextProvider.UsedQty};
    }
    
    protected Integer[] getColumnSize() {
    	return new Integer[]{32, 32, 64, 32};
    }
    
    class ModifyTextCellEditor extends TextCellEditor {
		private TableViewer tableViewer;
		private String cloumnName;
		
		public ModifyTextCellEditor(TableViewer tableViewer, String cloumnName) {
	        super(tableViewer.getTable());
	        this.tableViewer = tableViewer;
	        this.cloumnName = cloumnName;
	    }

		@Override
		protected Control createControl(Composite parent) {
			super.createControl(parent);
			text.setTextLimit(32);
			text.addModifyListener(new ModifyListener(){
	            public void modifyText(ModifyEvent e) {
	            	TableItem[] items = tableViewer.getTable().getSelection();
	            	if (items != null && items.length > 0){
	            		TableItem item = items[0];
	            		Object obj = item.getData();
	            		Lot lot = (Lot)obj;
	            		String value = text.getText();
//	            		if (value != null && value.equals("")) {
//	            			setValue(lot, "");
//						} else {
							if(discernParameter(lot, value)) { 
								if(obj instanceof Lot) {
									setValue(lot, value);
								}
							} else {
								if(DisplayName_LotQty.equals(cloumnName)) {
									tableViewer.editElement(item.getData(), 3);
								}
							}
//						}
	            	}
	            }
	        });
			return text;
		}
		
		// 验证是否为Double类型, 并且输入的消耗数小于lot的当前数量
		public boolean discernParameter(Lot lot, Object object) {
			if(DisplayName_LotQty.equals(cloumnName)) {
				if(validator("Double", "" + object)) {
					// 输入的消耗数量必须小于lot的当前数量
					BigDecimal inputQty = new BigDecimal(object.toString());
					if (inputQty.compareTo(BigDecimal.ZERO) < 0) {
						UI.showError(Message.getString("common.input_error"),
								Message.getString("common.inputerror_title"));
						return false;
					}
					return true;
//					BigDecimal actualQtyCurrent = parentSection.getLotActualCurrentQty(lot);
//					BigDecimal actualQtyCurrent = lot.getQtyCurrent();
//					if(inputQty.compareTo(actualQtyCurrent) > 0) {
//						UI.showError(String.format(Message.getString("wip.inputQty_Greater_than_currentQty"),
//								actualQtyCurrent.toString()));
//					} else {
//						return true;
//					}
				}
			}
			return false;
		}

		public boolean validator(String type, String value) {
			if (value != null){
				if (!ValidatorFactory.isValid(type, value)) {
					UI.showError(Message.getString("common.input_error"),
							Message.getString("common.inputerror_title"));
					return false;
				}
			}
			return true;
		}
		
		public void setValue(Lot lot, String value) {
			lot.setQtyTransaction(new BigDecimal(value));
		}
	}
    
    class TextCellModifier implements ICellModifier {
		private TableViewer tableViewer;
		private Lot lot;

		public TextCellModifier(TableViewer tableViewer, String defValueInt) {
			this.tableViewer = tableViewer;
		}

		@Override
		public boolean canModify(Object element, String property) {
			if(element instanceof Lot) {
				return canEdit;
			}
			return false;
		}

		@Override
		public Object getValue(Object element, String property) {
			lot = (Lot)element;
			if(lot.getQtyCurrent() != null) {
				return lot.getQtyTransaction().toString();
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			tableViewer.refresh();
		}
	}
}

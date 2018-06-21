package com.graly.erp.inv.generatelot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.mes.wip.model.Lot;

public class LotTableManager extends TableListManager {
	private String DisplayName_LotId = "lotId";
	private String DisplayName_LotQty = "qtyCurrent";
	private String lotType;

	private List<String> displayList;
	private HashMap<String, String> headerLabels;
	List<String> columnsHeaderList = new ArrayList<String>();
	List<Integer> size = new ArrayList<Integer>();
	private CellEditor[] cellEditor;
	private int columnCout;
	private boolean isEdit = true;

	public LotTableManager(ADTable adTable, String lotType) {
		super(adTable);
		this.lotType = lotType;
		initDisplayList();
	}

	private void initDisplayList() {
		displayList = new ArrayList<String>();
		displayList.add(DisplayName_LotId);
		if (Lot.LOTTYPE_BATCH.equals(lotType)) {
			displayList.add(DisplayName_LotQty);
		}
	}

	@Override
	protected String[] getColumns() {
		if (getADTable() instanceof ADTable) {
			List<String> columnsList = new ArrayList<String>();
			headerLabels = new LinkedHashMap<String, String>();
			ADTable table = getADTable();
			for (ADField field : table.getFields()) {
				String name = field.getName();
				if (displayList.contains(name)) {
					columnsList.add(field.getName());
					String disLabel = I18nUtil.getI18nMessage(field, "label");
					columnsHeaderList.add(I18nUtil.getI18nMessage(field, "label"));
					size.add(new Integer((field.getDisplayLength() == null ? 32 : field.getDisplayLength().intValue())));
					headerLabels.put(disLabel, field.getName());
				}
			}
			columnCout = columnsList.size();
			return columnsList.toArray(new String[] {});
		}
		return new String[] {};
	}

	@Override
	protected String[] getColumnsHeader() {
		return columnsHeaderList.toArray(new String[] {});
	}

	@Override
	protected Integer[] getColumnSize() {
		return size.toArray(new Integer[] {});
	}

	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit, int heightHint) {
		TableViewer viewer = (TableViewer) super.newViewer(parent, toolkit, heightHint);
		viewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = event.gc.getFontMetrics().getHeight() * 3 / 2;
			}
		});
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		TableViewerEditor.create(viewer, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
		setCellEditor(viewer);
		return viewer;
	}

	private void setCellEditor(TableViewer tableViewer) {
		cellEditor = new CellEditor[columnCout];
		String[] properties = new String[columnCout];
		for (int i = 0; i < columnCout; i++) {
			String columnHeader = tableViewer.getTable().getColumn(i).getText();
			properties[i] = columnHeader;
			cellEditor[i] = new ModifyTextCellEditor(tableViewer, headerLabels.get(columnHeader));
		}
		tableViewer.setColumnProperties(properties);
		tableViewer.setCellEditors(cellEditor);
		TextCellModifier tcm = new TextCellModifier(tableViewer, "");
		tableViewer.setCellModifier(tcm);
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
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					TableItem[] items = tableViewer.getTable().getSelection();
					if (items != null && items.length > 0) {
						TableItem item = items[0];
						Object obj = item.getData();
						Lot lot = (Lot) obj;
						String value = text.getText();
						if (value != null && value.equals("")) {
							// setValue(lot, "");
						} else {
							if (discernParameter(lot, value)) {
								if (obj instanceof Lot) {
									setValue(lot, value);
								}
							} else {
								if (DisplayName_LotId.equals(cloumnName)) {
									tableViewer.editElement(item.getData(), 0);
								} else if (DisplayName_LotQty.equals(cloumnName)) {
									tableViewer.editElement(item.getData(), 1);
								}
							}
						}
					}
				}
			});
			return text;
		}

		public boolean discernParameter(Lot lot, Object object) {
			if (DisplayName_LotId.equals(cloumnName)) {
				return validator("String", (String) object);
			} else if (DisplayName_LotQty.equals(cloumnName)) {
				return validator("Double", (String) object);
			}
			return false;
		}

		public boolean validator(String type, String value) {
			if (value != null) {
				if (value.contains(" ")) {
					UI.showError(Message.getString("inv.char_cannot_null"));
					return false;
				}
				if (!ValidatorFactory.isValid(type, value)) {
					UI.showError(Message.getString("common.input_error"), Message.getString("common.inputerror_title"));
					return false;
				}
			}
			return true;
		}

		public void setValue(Lot lot, String value) {
			if (DisplayName_LotId.equals(cloumnName)) {
				lot.setLotId(value.trim());
			} else if (DisplayName_LotQty.equals(cloumnName)) {
				lot.setQtyCurrent(new BigDecimal(value));
			}
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
			if(isEdit())
				return true;
			return false;
		}

		@Override
		public Object getValue(Object element, String property) {
			String columnName = headerLabels.get(property);
			lot = (Lot) element;
			if (DisplayName_LotId.equals(columnName)) {
				return lot.getLotId();
			} else if (DisplayName_LotQty.equals(columnName)) {
				return lot.getQtyCurrent().toString();
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			tableViewer.refresh();
		}
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}
}

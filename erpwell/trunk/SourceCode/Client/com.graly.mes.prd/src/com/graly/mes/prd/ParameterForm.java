package com.graly.mes.prd;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.mes.prd.model.Parameter;
import com.graly.mes.prd.workflow.context.def.WFParameter;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.runtime.Framework;

public class ParameterForm extends EntityForm {

	private static final Logger logger = Logger.getLogger(ParameterForm.class);
	protected static final String FIELD_ID = "parameters";
	protected static final String PROPERTY_ID = "wfParameters";
	private int columnCout;

	private TableViewer viewer;
	private CellEditor[] cellEditor;
	private IField field;

	public ParameterForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
		super(parent, style, tab, mmng);
	}

	@Override
	public void createForm() {
		super.createForm();
	}

	@Override
	public IField getField(ADField adField) {
		String displayText = adField.getDisplayType();
		String name = adField.getName();
		String displayLabel = I18nUtil.getI18nMessage(adField, "label");

		if (FieldType.TABLESELECT.equalsIgnoreCase(displayText)) {
			try {
				ADManager entityManager = Framework.getService(ADManager.class);
				ADRefTable refTable = new ADRefTable();
				refTable.setObjectRrn(adField.getReftableRrn());
				refTable = (ADRefTable) entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableRrn() == null) {
					return null;
				}
				ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
				if (FieldType.TABLESELECT.equalsIgnoreCase(displayText)) {
					EditableTableManager tableManager = new EditableTableManager(adTable);
					tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
					viewer = (TableViewer) tableManager.createViewer(getShell(),
							new FormToolkit(getShell().getDisplay()));
					this.setCellEditor(viewer);
					viewer.setUseHashlookup(true);
					field = createTableItemFilterField(name, displayLabel, viewer, adTable);
					addField(name, field);

					ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
						@Override
						protected boolean isEditorActivationEvent(
								ColumnViewerEditorActivationEvent event) {
							return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
									|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
									|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
						}
					};
					TableViewerEditor.create(
									viewer,
									actSupport,
									ColumnViewerEditor.TABBING_HORIZONTAL
											| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
											| ColumnViewerEditor.TABBING_VERTICAL
											| ColumnViewerEditor.KEYBOARD_ACTIVATION);

				}
			} catch (Exception e) {
				logger.error("EntityForm : Init tablelist", e);
			}
		}
		return field;
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public boolean saveToObject() {
		if (object != null) {
			IField f = null;
			f = fields.get(FIELD_ID);
			List<Parameter> parameters = (List<Parameter> )f.getValue();
			if (parameters != null) {
				List<WFParameter> variables = new ArrayList<WFParameter>();
				for (Parameter parameter : parameters){
					WFParameter variable = new WFParameter(parameter.getName(), null, null, parameter.getDefValue());
					variable.setIsActive(true);
					variable.setOrgRrn(parameter.getOrgRrn());
					variable.setType(parameter.getType());
					variables.add(variable);
				}
				PropertyUtil.setProperty(object, PROPERTY_ID, variables);
			}
			return true;
		}
		return false;
	}

	@Override
	public void loadFromObject() {
		if (object != null) {
			IField f = fields.get(FIELD_ID);
			List<WFParameter> variables = (List<WFParameter>)PropertyUtil.getPropertyForIField(object, PROPERTY_ID);
			List<Parameter> parameters = new ArrayList<Parameter>();
			if (variables != null) {
	    		for (WFParameter variable : variables) {
	    			Parameter parameter = new Parameter();
	    			parameter.setName(variable.getVariableName());
	    			parameter.setType(variable.getType());
	    			parameter.setDefValue(variable.getDefaultValue());
					parameters.add(parameter);
	    		}
			}
			f.setValue(parameters);
			refresh();
		}
	}

	private void setCellEditor(TableViewer tableViewer) {
		cellEditor = new CellEditor[columnCout];
		String[] properties = new String[columnCout];
		String defValueInt = "";
		String defaultColumn = Message.getString("common.defaultValue");
		for (int i = 0; i < columnCout; i++) {
			String columnHeader = tableViewer.getTable().getColumn(i).getText();
			if (columnHeader.equalsIgnoreCase(defaultColumn)) {
				defValueInt = defaultColumn;
				properties[i] = defaultColumn;
				cellEditor[i] = new ModifyTextCellEditor(tableViewer);
			} else {
				properties[i] = String.valueOf(i);
				cellEditor[i] = null;
			}
		}
		tableViewer.setColumnProperties(properties);
		tableViewer.setCellEditors(cellEditor);
		TextCellModifier tcm = new TextCellModifier(tableViewer, defValueInt);
		tableViewer.setCellModifier(tcm);
	}

	class EditableTableManager extends TableListManager {
		List<String> displayList = Parameter.getDisplayColumns();

		public EditableTableManager(ADTable adTable) {
			super(adTable);
		}

		@Override
		protected String[] getColumns() {
			if (getADTable() instanceof ADTable) {
				List<String> columnsList = new ArrayList<String>();
				ADTable table = getADTable();
				for (ADField field : table.getFields()) {
					String name = field.getName();
					if (displayList.contains(name)) {
						columnsList.add(field.getName());
					}
				}
				columnCout = columnsList.size();
				return columnsList.toArray(new String[] {});
			}
			return new String[] {};
		}

		@Override
		protected String[] getColumnsHeader() {
			if (getADTable() instanceof ADTable) {
				List<String> columnsHeaderList = new ArrayList<String>();
				ADTable table = getADTable();
				for (ADField field : table.getFields()) {
					String name = field.getName();
					if (displayList.contains(name)) {
						columnsHeaderList.add(I18nUtil.getI18nMessage(field, "label"));
					}
				}
				return columnsHeaderList.toArray(new String[] {});
			}
			return new String[] {};
		}

		@Override
		protected Integer[] getColumnSize() {
			if (getADTable() instanceof ADTable) {
				List<Integer> size = new ArrayList<Integer>();
				ADTable table = getADTable();
				for (ADField field : table.getFields()) {
					String name = field.getName();
					if (displayList.contains(name)) {
						size.add(new Integer(
								(field.getDisplayLength() == null ? 32 : field
										.getDisplayLength().intValue())));
					}
				}
				return size.toArray(new Integer[] {});
			}
			return new Integer[] {};
		}
	}

	class TextCellModifier implements ICellModifier {

		private TableViewer tableViewer;
		private Object adParam;
		private String defValueInt;

		public TextCellModifier(TableViewer tableViewer, String defValueInt) {
			this.tableViewer = tableViewer;
			this.defValueInt = defValueInt;
		}

		@Override
		public boolean canModify(Object element, String property) {
			if (property.equals(defValueInt)) {
				return true;
			}
			return false;
		}

		@Override
		public Object getValue(Object element, String property) {
			if (property.equals(defValueInt)) {
				if (element instanceof Parameter) {
					adParam = element;
					if(((Parameter) adParam).getDefValue() != null) {
						return ((Parameter) adParam).getDefValue();
					}
					return "";
				}
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			tableViewer.refresh();
		}
	}

	public TableViewer getTableViewer() {
		if (this.viewer != null) {
			return viewer;
		} else {
			return null;
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.field.setEnabled(enabled);
		if (enabled) {
			viewer.setCellEditors(cellEditor);
		} else {
			viewer.setCellEditors(null);
		}
	}
}
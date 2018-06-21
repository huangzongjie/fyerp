package com.graly.mes.prd.step;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.runtime.Framework;

public class StepOperationsForm extends EntityForm {
	
	private static final Logger logger = Logger.getLogger(StepOperationsForm.class);
	public static final String FIELD_ID = "operations";
	private String fieldId;
	
	public StepOperationsForm (Composite parent, int style, ADTab tab, IMessageManager mmng) {
		super(parent, style, tab, mmng);
	}
	
	@Override
	public void createForm() {
		super.createForm();
	}

	@Override
	public IField getField(ADField adField){
		String name = fieldId = adField.getName();
		String displayLabel = I18nUtil.getI18nMessage(adField, "label");
		IField field = null;
			try{
				ADManager entityManager = Framework.getService(ADManager.class);
				ADRefTable refTable = new ADRefTable();
				refTable.setObjectRrn(adField.getReftableRrn());
				refTable = (ADRefTable)entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableRrn() == null){
					return null;
				}
				ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
				TableListManager tableManager = new TableListManager(adTable);
				tableManager.setStyle(SWT.CHECK);
				TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
				field = createOperationTableField(name, displayLabel, viewer, adTable, SWT.CHECK);
				addField(name, field);
			} catch (Exception e){
				logger.error("StepOperationForm : Init tablelist", e);
			}
		return field;
	}
	
	public OperationTableField createOperationTableField(String id, String label,
	    	TableViewer viewer, ADTable adTable, int style) {
	    	OperationTableField otf = new OperationTableField(id, viewer, adTable, style);
	    	otf.setLabel(label);
	    	otf.setValue(null);
	        return otf;
	}
	
	@Override
    public boolean saveToObject() {
		if (object != null){
			String name = (fieldId != null && !"".equals(fieldId.trim())) ?
					fieldId : FIELD_ID;
			IField f = fields.get(name);
			PropertyUtil.setProperty(object, f.getId(), f.getValue());
			return true;
		}
		return false;
    }
	
	@Override
    public void loadFromObject() {
		if (object != null) {
			String name = (fieldId != null && !"".equals(fieldId.trim())) ?
					fieldId : FIELD_ID;
			IField f = fields.get(name);
			f.setValue(PropertyUtil.getPropertyForIField(object, f.getId()));
			refresh();
		}
    }
}
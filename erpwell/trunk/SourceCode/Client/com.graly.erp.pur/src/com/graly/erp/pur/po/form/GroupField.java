package com.graly.erp.pur.po.form;

import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.base.ui.forms.field.AbstractField;
import com.graly.framework.base.ui.forms.field.IField;

public class GroupField extends AbstractField {

	int mStyle = SWT.NONE;
	List<ADField> groupFields;
	GroupFieldForm groupForm;
	Composite comp;
	IMessageManager mmng;
	Object object;
	int[] lineGrids;
	
	public GroupField(String id) {
		super(id);
	}

	public GroupField(String id, int style, Object object,
			List<ADField> groupFields, IMessageManager mmng, int[] lineGrids) {
		super(id);
		this.mStyle = style;
		this.object = object;
		this.groupFields = groupFields;
		this.mmng = mmng;
		this.lineGrids = lineGrids;
	}
	
	public void createContent(Composite composite, FormToolkit toolkit) {
		String labelStr = getLabel();
		if (labelStr != null) {
			Label label = toolkit.createLabel(composite, labelStr);
			groupForm = new GroupFieldForm(composite, mStyle, object,
					mmng, groupFields, lineGrids);
			mControls = new Control[2];
			mControls[0] = label;
			mControls[1] = groupForm;
		} else {
			groupForm = new GroupFieldForm(composite, mStyle, object,
					mmng, groupFields, lineGrids);
            mControls = new Control[1];
            mControls[0] = groupForm;
        }
		if (getToolTipText() != null) {
			groupForm.setToolTipText(getToolTipText());
		}
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        groupForm.setLayoutData(gd);
	}

    @Override
	public void refresh() {
    	groupForm.refresh();
	}

	@Override
	public void setEnabled(boolean enabled) {
		groupForm.setEnabled();
	}
	
	public boolean saveValue() {
		return groupForm.saveToObject();
	}
	
	public void loadValue() {
		groupForm.loadFromObject();
	}
	
	@Override
	public void enableChanged(boolean enabled) {
    }
	
	public LinkedHashMap<String, IField> getFields() {
		return groupForm.getFields();
	}

	@Override
	public String getFieldType() {
		return "groups";
	}
	
	public void setObject(Object adBase) {
		groupForm.setObject(adBase);
	}
}

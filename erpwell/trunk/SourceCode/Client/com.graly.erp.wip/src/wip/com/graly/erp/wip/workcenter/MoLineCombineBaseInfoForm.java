package com.graly.erp.wip.workcenter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MoLineCombineBaseInfoForm extends EntityForm {
	
	private static final String TABLE_NAME = "WIPManufactureOrderLineCombine";
	private ADTable formAdTable;
	public MoLineCombineBaseInfoForm(Composite parent, int style, Object object) {
		super(parent, style, object, null);
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			 formAdTable = adManager.getADTable(Env.getOrgRrn(), TABLE_NAME);
			this.table = formAdTable;
			this.setGridY(2);
			this.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.setLayout(new FillLayout(SWT.FILL));
			super.createForm();
		}  catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	@Override
	public boolean saveToObject() {
		return super.saveToObject();
	}
	
	@Override
	public void createForm() {
	}

	public ADTable getFormAdTable() {
		return formAdTable;
	}

	public void setFormAdTable(ADTable formAdTable) {
		this.formAdTable = formAdTable;
	}
	
	
	}


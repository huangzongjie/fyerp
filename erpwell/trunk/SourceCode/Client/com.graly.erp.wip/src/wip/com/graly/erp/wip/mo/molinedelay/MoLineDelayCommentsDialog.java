package com.graly.erp.wip.mo.molinedelay;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.wip.model.ManufactureOrderLineDelay;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.util.Message;
/**
 * @author Administrator
 * 用于修改物料需求警报备注信息
 */
public class MoLineDelayCommentsDialog extends InClosableTitleAreaDialog {
	protected ManagedForm managedForm;
	private ADTable adTable;
	private ManufactureOrderLineDelay selectMMoLineDelay;
	private EntityForm entityForm;

	public MoLineDelayCommentsDialog(Shell parentShell, ManufactureOrderLineDelay selectMMoLineDelay,ADTable adTable) {
		super(parentShell);
		this.selectMMoLineDelay = selectMMoLineDelay;
		this.adTable = adTable;
	}	

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("采购人员添加备注");		
		Composite composite = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		entityForm = new EntityForm(body, SWT.NONE, selectMMoLineDelay, adTable, null);
		entityForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		return composite;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			entityForm.saveToObject();
			okPressed();
		}else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.cancel"), false);
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 5;
		layout.marginBottom = 5;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public ManufactureOrderLineDelay getSelectMMoLineDelay() {
		return selectMMoLineDelay;
	}

	public void setSelectMMoLineDelay(ManufactureOrderLineDelay selectMMoLineDelay) {
		this.selectMMoLineDelay = selectMMoLineDelay;
	}
}

package com.graly.erp.wip.workcenter.schedule.purchase3;

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

import com.graly.erp.inv.model.AlarmData;
import com.graly.erp.wip.model.RepScheResult2;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.util.Message;
/**
 * @author Administrator
 * 用于修改质检备注信息
 */
public class PurchaseMaterialModifyDialog extends InClosableTitleAreaDialog {
	protected ManagedForm managedForm;
	private ADTable adTable;
	private RepScheResult2 repScheResult2;
	private EntityForm entityForm;

	public PurchaseMaterialModifyDialog(Shell parentShell, RepScheResult2 repScheResult2,ADTable adTable) {
		super(parentShell);
		this.repScheResult2 = repScheResult2;
		this.adTable = adTable;
	}	

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("备注");		
		Composite composite = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		entityForm = new EntityForm(body, SWT.NONE, repScheResult2, adTable, null);
		entityForm.setLayoutData(new GridData(GridData.FILL_BOTH));
//		createContent(body, toolkit);
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

	public RepScheResult2 getRepScheResult2() {
		return repScheResult2;
	}

	public void setRepScheResult2(RepScheResult2 repScheResult2) {
		this.repScheResult2 = repScheResult2;
	}
}

package com.graly.erp.internalorder;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
/**
 * @author Administrator
 * 用于修改质检备注信息
 */
public class InternalOrderIdSetDialog extends InClosableTitleAreaDialog {
	protected ManagedForm managedForm;
	private EntityForm entityForm;
	private IManagedForm form;
	private Text txtOrderId;
	
	private String  txtValue;

	public InternalOrderIdSetDialog(IManagedForm form,Shell parentShell) {
		super(parentShell);
		this.form = form;
	}	

	@Override
	protected Control createDialogArea(Composite parent) {
        setTitle("临时计划编号");
        setMessage("临时计划编号");
		Composite comp = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
	    Composite body = toolkit.createComposite(comp, SWT.BORDER);
		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 10;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		Label label  = toolkit.createLabel(body, "临时计划编号");
		txtOrderId = toolkit.createText(body, "");
		txtOrderId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return body;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			if(txtOrderId.getText()!=null &&!"".equals(txtOrderId.getText()) ){
				setTxtValue(txtOrderId.getText());
				okPressed();
			}else{
				UI.showError("临时计划编号请输入值....");
			}
			
		}else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.cancel"), false);
	}

	
	public String getTxtValue() {
		return txtValue;
	}

	public void setTxtValue(String txtValue) {
		this.txtValue = txtValue;
	}

}

package com.graly.erp.inv.barcode;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.validator.ValidatorFactory;

public class BatchNumberSetDialog extends InClosableTitleAreaDialog {
	IManagedForm managedForm;
	IMessageManager mmng;
	int lineTotal;
	int batchNumber;
	Text txtBatchNum;
	
	public BatchNumberSetDialog(Shell parent, IManagedForm managedForm, int lineTotal){
		super(parent);
		this.managedForm = managedForm;
		mmng = managedForm.getMessageManager();
		this.lineTotal = lineTotal;
	}

	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		setTitle(Message.getString("inv.batch_number_title"));
        Composite composite = (Composite) super.createDialogArea(parent);
        
        FormToolkit toolkit = managedForm.getToolkit();
        Composite client = toolkit.createComposite(composite, SWT.BORDER);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_BOTH));
        toolkit.createLabel(client, Message.getString("inv.bach_number"));
        txtBatchNum = toolkit.createText(client, "", SWT.BORDER);
        txtBatchNum.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtBatchNum.setTextLimit(9);
        
        return composite;
    }
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			if(validateBatchNumber()) {
				this.batchNumber = Integer.parseInt(txtBatchNum.getText());
				mmng.removeAllMessages();
				okPressed();
			}
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			mmng.removeAllMessages();
			cancelPressed();
		}
	}
	
	private boolean validateBatchNumber() {
		mmng.removeAllMessages();
		String value = txtBatchNum.getText();
		try {
			if (!ValidatorFactory.isInRange("integer", value, "1", String.valueOf(lineTotal))){
				mmng.addMessage("Batch Number" + "common.between", 
						String.format(Message.getString("common.between"), "Batch Number", "1", String.valueOf(lineTotal)),
						null, IMessageProvider.ERROR, txtBatchNum);
				txtBatchNum.setFocus();
				return false;
			}
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	public int getBatchNumber() {
		return batchNumber;
	}
}

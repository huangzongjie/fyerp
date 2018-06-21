package com.graly.erp.wip.workcenter;

import java.math.BigDecimal;

import org.eclipse.jface.dialogs.IDialogConstants;
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

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.security.model.WorkCenter;

public class ManpowerDialog extends InClosableTitleAreaDialog {
	IManagedForm managedForm;
	IMessageManager mmng;
	Text text;
	WorkCenter workCenter;
	ManufactureOrderLine moLine;
	String textValue;
	public ManpowerDialog(Shell parent, IManagedForm managedForm, WorkCenter workCenter , ManufactureOrderLine moLine){
		super(parent);
		this.managedForm = managedForm;
		mmng = managedForm.getMessageManager();
		this.workCenter = workCenter;
		this.moLine = moLine;
	}

	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		setTitle(Message.getString("wip.manpower_manager"));
        Composite composite = (Composite) super.createDialogArea(parent);
        
        FormToolkit toolkit = managedForm.getToolkit();
        Composite client = toolkit.createComposite(composite, SWT.BORDER);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_BOTH));
        toolkit.createLabel(client, Message.getString("wip.manpower"));
        text = toolkit.createText(client, "", SWT.BORDER);
        if(workCenter.getManpower() != null) {
        	text.setText(workCenter.getManpower().toString());
        	text.selectAll();
        }
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        return composite;
    }
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			if(text.getText() == null || "".equals(text.getText()))
				return;
			try {
				new BigDecimal(text.getText());
			} catch (Exception e) {
				this.setErrorMessage(Message.getString("wip.manpower_is_not_null"));
				return;
			}
			setTextValue(text.getText());
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			mmng.removeAllMessages();
			cancelPressed();
		}
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
}

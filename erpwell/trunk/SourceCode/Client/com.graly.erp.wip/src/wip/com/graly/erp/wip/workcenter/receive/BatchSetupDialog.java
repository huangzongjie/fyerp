package com.graly.erp.wip.workcenter.receive;

import org.eclipse.jface.dialogs.Dialog;
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

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.mes.wip.model.Lot;

public class BatchSetupDialog extends InClosableTitleAreaDialog {
	private ManufactureOrderLine moLine;
	private MoLineReceiveForm receiveForm;
	private ManagedForm form;
	private Lot parentLot;
//	private int batchQuantity = 1;

	public BatchSetupDialog(Shell shell) {
		super(shell);
	}
	
	public BatchSetupDialog(Shell shell, ManufactureOrderLine moLine) {
		this(shell);
		this.moLine = moLine;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
        setTitle(Message.getString("wip.batch_qty_setup"));

        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		form = new ManagedForm(toolkit, sForm);
		
		createFormContent(body);
        return body;
	}

	/**
	 * @param composite
	 */
	protected void createFormContent(Composite composite) {
		receiveForm = new MoLineReceiveForm(composite, SWT.NULL, moLine,
				form.getMessageManager(), moLine.getMaterial().getLotType(), true);
		receiveForm.setGridY(2);
		receiveForm.setDisplayLotType(true);
		receiveForm.createFormContent();
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		form.getMessageManager().removeAllMessages();
		if(buttonId == Dialog.OK) {
			if(receiveForm.saveToObject()) {
				parentLot = receiveForm.getParentLot();
				this.okPressed();
			}
		} else {
			super.buttonPressed(buttonId);			
		}
	}
	
	public Lot getParentLot() {
		return this.parentLot;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}

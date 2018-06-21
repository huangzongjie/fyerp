package com.graly.erp.wip.partlydisassemble;

import java.math.BigDecimal;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.mes.wip.model.Lot;

public class QtySetupDialog extends InClosableTitleAreaDialog {
	private QtySetupForm qtySetupForm;
	private ManagedForm form;
	private Lot parentLot;
	protected BigDecimal qtyDisassemble = BigDecimal.ZERO;
	
	public QtySetupDialog(Shell shell) {
		super(shell);
	}
	
	public QtySetupDialog(Shell shell, Lot parentLot) {
		this(shell);
		this.parentLot = parentLot;
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
		qtySetupForm = new QtySetupForm(composite, SWT.NULL, parentLot,
				form.getMessageManager());
		qtySetupForm.setGridY(2);
		qtySetupForm.createFormContent();
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		form.getMessageManager().removeAllMessages();
		if(buttonId == Dialog.OK) {
			if(qtySetupForm.validate()){
				qtyDisassemble =  qtySetupForm.getQtyDisassemble();
			}else{
				setMessage(Message.getString("common.input_error"), IMessageProvider.ERROR);
				return;
			}
		}
		super.buttonPressed(buttonId);			
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

	public BigDecimal getQtyDisassemble() {
		return qtyDisassemble;
	}
}

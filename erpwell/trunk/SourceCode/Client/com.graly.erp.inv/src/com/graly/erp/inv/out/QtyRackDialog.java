package com.graly.erp.inv.out;

import java.math.BigDecimal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.base.ui.util.Message;

public class QtyRackDialog extends TitleAreaDialog {
	protected ManagedForm managedForm;
	protected Text txtInQty;
	protected Text txtInRack;
	protected BigDecimal inputQty, qtyOnhand = BigDecimal.ZERO;
	protected String inputRack;
	
	public QtyRackDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setDialogTitle();		
		Composite composite = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		createContent(body, toolkit);
		
		return composite;
	}
	
	protected void setDialogTitle() {
		setTitle(String.format(Message.getString("inv.qty_setup"),
				getMovementQtyLabel()));
	}

	@Override
	protected void okPressed() {
		inputQty = new BigDecimal(txtInQty.getText());
		inputRack = new String(txtInRack.getText());
		super.okPressed();
	}
	
	protected void createContent(Composite parent, FormToolkit toolkit) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 13;
		
		//qty
		toolkit.createLabel(parent, getMovementQtyLabel(), SWT.NULL);
		txtInQty = toolkit.createText(parent, "", SWT.BORDER);
		txtInQty.setLayoutData(gd);
		txtInQty.setTextLimit(12);
		txtInQty.setFocus();
		txtInQty.selectAll();
		
		//rackid
		toolkit.createLabel(parent, getMovementRackLabel(), SWT.NULL);
		txtInRack = toolkit.createText(parent, "", SWT.BORDER);
		txtInRack.setLayoutData(gd);
		txtInRack.setTextLimit(12);
		txtInRack.setFocus();
		txtInRack.selectAll();
	}
	
	protected void createCurrentQtyOrStorageQtyLabel(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Message.getString("inv.qty_storage"), SWT.NULL);		
	}
	
	protected String getMovementQtyLabel() {
		return Message.getString("inv.out_qty");
	}
	
	protected String getMovementRackLabel() {
		return "»õ¼ÜºÅ";
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), true);
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
	
	public BigDecimal getInputQty() {
		return inputQty;
	}
	
	public String getInputRack() {
		return inputRack;
	}
}

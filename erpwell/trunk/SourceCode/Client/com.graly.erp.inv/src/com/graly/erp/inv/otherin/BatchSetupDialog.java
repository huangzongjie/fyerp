package com.graly.erp.inv.otherin;

import org.eclipse.jface.dialogs.IDialogConstants;
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

import com.graly.erp.inv.model.MovementLine;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
/**
 * @author Administrator
 * 生成批次时，如果为Batch类型，则设置生成的批次数
 */
public class BatchSetupDialog extends InClosableTitleAreaDialog {
	protected ManagedForm managedForm;
	protected MovementLine movementInLine;
	private Text txtBatchQty;
	private int batchQty;

	public BatchSetupDialog(Shell parentShell, MovementLine movementInLine) {
		super(parentShell);
		this.movementInLine = movementInLine;
	}	

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Message.getString("wip.batch_qty_setup"));		
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

	@Override
	protected void okPressed() {
		if(validate()) {
			batchQty = Integer.parseInt(txtBatchQty.getText());
			super.okPressed();
		} else 
			return;
	}
	
	protected void createContent(Composite parent, FormToolkit toolkit) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 13;
		// Lot ID
		toolkit.createLabel(parent, Message.getString("wip.lot_qty_label"), SWT.NULL);
		txtBatchQty = toolkit.createText(parent, "", SWT.BORDER);
		txtBatchQty.setLayoutData(gd);
		txtBatchQty.setFocus();
	}
	
	// 验证输入的入库数量必须大于批次的Batch数量
	protected boolean validate() {
		try {
			setErrorMessage(null);
			batchQty = Integer.parseInt(txtBatchQty.getText());
//			BigDecimal inputQty = new BigDecimal(batchQty);
//			if(movementInLine.getQtyMovement().compareTo(inputQty) >= 0) {
//				return true;
//			} else {
//				setErrorMessage(String.format(Message.getString("inv.batchQty_large_moventQty"),
//						batchQty + "", movementInLine.getQtyMovement().toString()));
//			}
			return true;
		} catch(Exception e) {
			setErrorMessage(Message.getString("common.input_error"));
		}
		return false;
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

	public int getBatchQty() {
		return batchQty;
	}
}

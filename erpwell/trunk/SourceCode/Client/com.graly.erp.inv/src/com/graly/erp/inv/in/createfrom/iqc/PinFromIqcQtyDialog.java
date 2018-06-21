package com.graly.erp.inv.in.createfrom.iqc;

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

import com.graly.erp.inv.model.MovementLine;
import com.graly.framework.base.ui.util.Message;
import com.graly.mes.wip.model.Lot;

public class PinFromIqcQtyDialog extends TitleAreaDialog {
	protected ManagedForm managedForm;
	protected MovementLine movementLine;
	protected Lot lot;
	protected Text txtOutQty;
	protected BigDecimal inputQty;
	protected BigDecimal maxInQty;

	public PinFromIqcQtyDialog(Shell parentShell, Lot lot, BigDecimal maxInQty) {
		super(parentShell);
		this.lot = lot;
		this.maxInQty = maxInQty;
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
		if(validate()) {
			inputQty = new BigDecimal(txtOutQty.getText());
			lot.setQtyCurrent(inputQty);
			super.okPressed();
		} else 
			return;
	}
	
	protected void createContent(Composite parent, FormToolkit toolkit) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 13;
		// Lot ID
		toolkit.createLabel(parent, Message.getString("inv.lotid"), SWT.NULL);
		Text txtLotId = toolkit.createText(parent, lot.getLotId(), SWT.BORDER | SWT.READ_ONLY);
		txtLotId.setBackground(txtLotId.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtLotId.setLayoutData(gd);
		
		// Material ID
		toolkit.createLabel(parent, Message.getString("pdm.material_id"), SWT.NULL);
		Text txtMaterialId = toolkit.createText(parent, lot.getMaterialId(), SWT.BORDER | SWT.READ_ONLY);
		txtMaterialId.setBackground(txtLotId.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtMaterialId.setLayoutData(gd);
		
		// Current Quantity
		createCurrentQtyOrStorageQtyLabel(toolkit, parent);
		Text txtCQty = toolkit.createText(parent, "", SWT.BORDER | SWT.READ_ONLY);
		txtCQty.setBackground(txtLotId.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtCQty.setLayoutData(gd);
		setCurrentQty(txtCQty);
		
		// Out Quantity
		toolkit.createLabel(parent, getMovementQtyLabel(), SWT.NULL);
		txtOutQty = toolkit.createText(parent, "", SWT.BORDER);
		txtOutQty.setLayoutData(gd);
		txtOutQty.setTextLimit(12);
		setQtyContent();
		txtOutQty.setFocus();
		txtOutQty.selectAll();
	}
	
	protected void createCurrentQtyOrStorageQtyLabel(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Message.getString("inv.lot_qty"), SWT.NULL);		
	}
	
	// ��ǰ����Ϊlot��getQtyCurrent����
	protected void setCurrentQty(Text text) {
		if(text == null) return;
		if(maxInQty != null) {
			text.setText(maxInQty.toString());
		} else {
			text.setText("0");
		}
	}
	
	// Ĭ���������Ϊlot��getQtyCurrent����
	protected void setQtyContent() {
		if(lot.getQtyCurrent() != null) {
			txtOutQty.setText(lot.getQtyCurrent().toString());
		} else {
			txtOutQty.setText("0");
		}
	}
	
	// ��֤����ĳ�����������С�����εĵ�ǰ����
	protected boolean validate() {
		try {
			setErrorMessage(null);
			BigDecimal inputQty = new BigDecimal(txtOutQty.getText());
			if(BigDecimal.ZERO.compareTo(inputQty) == 0) {
				setErrorMessage(String.format(Message.getString("common.largerthan"),
						getMovementQtyLabel(), inputQty.toString()));
				return false;
			}
			if(maxInQty != null && maxInQty.compareTo(inputQty) >= 0) {
				return true;
			} else {
				String qty = (maxInQty == null ? "0" : maxInQty.toString());
				setErrorMessage(String.format(Message.getString("common.lessthan"),
						getMovementQtyLabel(), qty));
			}
		} catch(Exception e) {
			setErrorMessage(Message.getString("common.input_error"));
		}
		return false;
	}
	
	protected String getMovementQtyLabel() {
		return Message.getString("inv.in_qty");
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
}

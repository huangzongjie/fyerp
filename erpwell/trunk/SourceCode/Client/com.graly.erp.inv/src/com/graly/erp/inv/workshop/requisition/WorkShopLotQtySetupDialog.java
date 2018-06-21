package com.graly.erp.inv.workshop.requisition;

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

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.Warehouse;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class WorkShopLotQtySetupDialog extends TitleAreaDialog {
	protected ManagedForm managedForm;
	protected MovementWorkShopLine movementLine;
	protected Lot lot;
	protected Text txtOutQty;
	protected BigDecimal inputQty, qtyOnhand = BigDecimal.ZERO;
	protected Warehouse warehouse;

	public WorkShopLotQtySetupDialog(Shell parentShell, MovementWorkShopLine movementLine, Lot lot, Warehouse warehouse) {
		super(parentShell);
		this.movementLine = movementLine;
		this.lot = lot;
		this.warehouse = warehouse;
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
		setCurrentQtyOrStorageQty(txtCQty);
		
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
		toolkit.createLabel(parent, Message.getString("inv.qty_storage"), SWT.NULL);		
	}
	
	// 设置库存数量(出库调拨时)，或当前数量(入库时Lot的当前数量)
	protected void setCurrentQtyOrStorageQty(Text text) {
		if(text == null) return;
		text.setText(getQtyOnhand().toString());
	}
	
	protected void setQtyContent() {
		// 如果warehouse中库存数量大于出库单行数量,则初始值为出库单行数量,否则初始值为warehouse中库存数量
		if(movementLine != null && qtyOnhand != null && qtyOnhand.compareTo(movementLine.getQtyMovement()) > 0) {
			txtOutQty.setText(movementLine.getQtyMovement().toString());
		} else {
			txtOutQty.setText(qtyOnhand.toString());
		}
	}
	
	// 验证输入的出库数量必须小于批次的当前数量
	protected boolean validate() {
		try {
			setErrorMessage(null);
			BigDecimal inputQty = new BigDecimal(txtOutQty.getText());
			if(BigDecimal.ZERO.compareTo(inputQty) == 0) {
				setErrorMessage(String.format(Message.getString("common.largerthan"),
						getMovementQtyLabel(), inputQty.toString()));
				return false;
			}
			if(qtyOnhand != null && ( qtyOnhand.add(inputQty.negate()).compareTo(BigDecimal.ZERO) >= 0 || 
					qtyOnhand.add(inputQty.negate()).compareTo(qtyOnhand) >= 0)) {//或者是出库后库存增加(核销时)
				return true;
			} else {
				String qty = qtyOnhand == null ? "0" : qtyOnhand.toString();
				setErrorMessage(String.format(Message.getString("inv.outQty_large_currentQty"), qty));
			}
		} catch(Exception e) {
			setErrorMessage(Message.getString("common.input_error"));
		}
		return false;
	}
	
	protected String getMovementQtyLabel() {
		return Message.getString("inv.out_qty");
	}
	
	protected BigDecimal getQtyOnhand() {
		try {
			if(warehouse != null || lot != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				qtyOnhand = invManager.getLotStorage(Env.getOrgRrn(),
						lot.getObjectRrn(), warehouse.getObjectRrn(), Env.getUserRrn()).getQtyOnhand();				
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return qtyOnhand;
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

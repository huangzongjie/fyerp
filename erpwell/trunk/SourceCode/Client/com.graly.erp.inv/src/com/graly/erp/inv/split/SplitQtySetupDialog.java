package com.graly.erp.inv.split;

import java.math.BigDecimal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.client.INVManager;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class SplitQtySetupDialog extends InClosableTitleAreaDialog {
	private Lot lot;
	private IManagedForm form;
	private Text txtLotId;
	private Text txtMaterialId;
	private Text txtQty;
	private BigDecimal splitQty;

	public SplitQtySetupDialog(Shell parentShell, Lot lot, IManagedForm form) {
		super(parentShell);
		this.lot = lot;
		this.form = form;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
        setTitle(Message.getString("inv.split"));
        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = form.getToolkit();
        
        Composite content = toolkit.createComposite(comp, SWT.BORDER);
        content.setLayout(new GridLayout(4, false));
        content.setLayoutData(new GridData(GridData.FILL_BOTH));

        
        toolkit.createLabel(content, "批号");
        txtLotId = toolkit.createText(content, lot.getLotId() != null ? lot.getLotId() : "", SWT.BORDER);
        txtLotId.setEnabled(false);
        txtLotId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        toolkit.createLabel(content, "物料编号");
        txtMaterialId = toolkit.createText(content, lot.getMaterialId() != null? lot.getMaterialId() : "", SWT.BORDER);
        txtMaterialId.setEnabled(false);
        txtMaterialId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        
        Composite content2 = toolkit.createComposite(content, SWT.NONE);
        GridLayout gl = new GridLayout(2, false);
        gl.marginTop = 5;
        content2.setLayout(gl);
        GridData gd0 = new GridData(GridData.FILL_BOTH);
        gd0.horizontalSpan = 4;
        content2.setLayoutData(gd0);
        
        Label separator = toolkit.createSeparator(content2, SWT.HORIZONTAL | SWT.SEPARATOR);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan=2;
		separator.setLayoutData(gd);
        
        toolkit.createLabel(content2, Message.getString("inv.split_qty"));
        txtQty = toolkit.createText(content2, "", SWT.BORDER);
        txtQty.setTextLimit(18);
        txtQty.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return comp;
		
	}

	@Override
	protected void buttonPressed(int buttonId) {
		try {
			if(buttonId == IDialogConstants.OK_ID) {
				
				if(this.validate(txtQty.getText())) {
					splitQty = new BigDecimal(txtQty.getText());
				} else {
					txtQty.selectAll();
					return;
				}
			}
			super.buttonPressed(buttonId);			
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	public boolean validate(String value) {
		setErrorMessage(null);
		if(value == null || "".equals(value)) {
			return false;
		}
		// 若value不是double类型或小于等于0, 提示输入值错误
		if (!ValidatorFactory.isValid("double", value)
				|| Double.parseDouble(value) <= 0) {
			this.setErrorMessage(Message.getString("common.input_error"));
			return false;
		}
		if(lot.getQtyCurrent() == null
				|| lot.getQtyCurrent().doubleValue() < Double.parseDouble(value)) {
			this.setErrorMessage(String.format(Message.getString("inv.currentQty_smaller_splitQty"),
					lot.getQtyCurrent().toString(), value));
			return false;
		}
		return true;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}

	public BigDecimal getSplitQty() {
		return splitQty;
	}

}

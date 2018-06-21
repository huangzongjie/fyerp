package com.graly.erp.inv.lotprint;

import javax.print.PrintService;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.base.print.PrintUtil;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class SingleLotPrintDialog extends InClosableTitleAreaDialog {
	private int printCount = 1;
	private Lot lot;
	private Text txtQty;

	public SingleLotPrintDialog(Shell parentShell, Lot lot) {
		super(parentShell);
		this.lot = lot;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
        setTitle(Message.getString("wip.lotId_print_qty_setup"));
        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);		
		createContent(body, toolkit);
		
		return comp;
	}
	
	protected void createContent(Composite parent, FormToolkit toolkit) {
		toolkit.createLabel(parent, Message.getString("wip.lot_print_qty"), SWT.PUSH);
		txtQty = toolkit.createText(parent, printCount + "", SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 13;
		txtQty.setLayoutData(gd);
		txtQty.setTextLimit(9);
		txtQty.setFocus();
	}

	@Override
	protected void okPressed() {
		setErrorMessage(null);
		if(txtQty.getText() != null) {
			if (!ValidatorFactory.isValid("Integer", txtQty.getText())) {
				setErrorMessage(Message.getString("common.input_error"));
				return;
			}
			try {
				printCount = Integer.parseInt(txtQty.getText());
				PrintService service = PrintUtil.getDefaultPrintService();
	
				for(int i = 1; i < printCount + 1; i++) {
					if (i % 2 == 0) {
						PrintUtil.print(service, lot.getLotId(), lot.getLotId());
					}
				}
				if (printCount % 2 != 0) {
					PrintUtil.print(service, lot.getLotId());
				}
				
				super.okPressed();
			} catch (ClientException e) {
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}

	public int getPrintCount() {
		return printCount;
	}
	
	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 10;
		layout.marginBottom = 10;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}

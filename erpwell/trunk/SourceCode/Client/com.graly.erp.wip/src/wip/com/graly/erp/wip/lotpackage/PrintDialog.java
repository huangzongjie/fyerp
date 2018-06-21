package com.graly.erp.wip.lotpackage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.inv.model.WarehouseRack;
import com.graly.erp.wip.model.LargeLot;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PrintDialog extends ExtendDialog{
	private static final Logger logger = Logger.getLogger(PrintDialog.class);
	private int MIN_DIALOG_WIDTH = 250;
	private int MIN_DIALOG_HEIGHT = 150;
	protected ManagedForm managedForm;
	private Text txtPrintNums;
	private Text qtyTxt;
	private Button doublePrint;
	private int repeatTime = 1, printNums = -1;;
	private List<LargeLot> lLots;
	
	public PrintDialog(List<LargeLot> lLots) {
		super();
		this.lLots = lLots;
	}
	
	public PrintDialog(LargeLot lLot) {
		lLots = new ArrayList<LargeLot>();
		lLots.add(lLot);		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		 	setTitleImage(SWTResourceCache.getImage("entity-dialog"));
			setTitle(Message.getString("bas.lot_print"));
	        Composite composite = (Composite) super.createDialogArea(parent);

	        FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
			ScrolledForm sForm = toolkit.createScrolledForm(composite);
			sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
			managedForm = new ManagedForm(toolkit, sForm);
			Composite body = sForm.getForm().getBody();
			configureBody(body);
			
	        final Composite client2 = toolkit.createComposite(body, SWT.NONE);
	        client2.setLayout(new GridLayout(2, false));
	        client2.setLayoutData(new GridData(GridData.FILL_BOTH));
	        
	        Label separator = toolkit.createSeparator(client2, SWT.HORIZONTAL | SWT.SEPARATOR);
	        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	        gd.horizontalSpan=2;
			separator.setLayoutData(gd);
	        
	        toolkit.createLabel(client2, Message.getString("wip.lot_print_qty"));
	        qtyTxt = toolkit.createText(client2, repeatTime + "", SWT.NONE);
			qtyTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        qtyTxt.setTextLimit(9);
	        doublePrint = toolkit.createButton(client2, "Ë«±ß´òÓ¡", SWT.CHECK);
	        doublePrint.setSelection(true);
	        doublePrint.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, true, 2, 1));
	        return composite;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		try {
			if (IDialogConstants.OK_ID == buttonId) {
				if(lLots != null && lLots.size() != 0){
					if(!((qtyTxt.getText() == null) || (qtyTxt.getText().trim().length() == 0))) {
						if (!ValidatorFactory.isValid("Integer", qtyTxt.getText())) {
							setErrorMessage(Message.getString("common.input_error"));
							return;
						}
						repeatTime = Integer.parseInt(qtyTxt.getText());
					}
					
					LargeLotPrintProgressDialog progressDialog = new LargeLotPrintProgressDialog(UI.getActiveShell());
					LargeLotPrintProgress progress = new LargeLotPrintProgress(lLots, getRepeatTime(), doublePrint.getSelection());
					progressDialog.run(true, true, progress);
					if (progress.isFinished()) {
						UI.showInfo(Message.getString("bas.lot_print_finished"));
					}
				}
			} else if (IDialogConstants.CANCEL_ID == buttonId) {
				cancelPressed();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}

	public int getRepeatTime() {
		return repeatTime;
	}

	public void setRepeatTime(int repeatTime) {
		this.repeatTime = repeatTime;
	}

	public int getPrintNums() {
		return printNums;
	}

	public void setPrintNums(int printNums) {
		this.printNums = printNums;
	}
}

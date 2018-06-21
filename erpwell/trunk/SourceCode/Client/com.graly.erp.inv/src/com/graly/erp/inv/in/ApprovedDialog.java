package com.graly.erp.inv.in;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.in.approve.ApprovedInvoiceTableManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class ApprovedDialog extends InClosableTitleAreaDialog {
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 300;
	IManagedForm managedForm;
	IMessageManager mmng;
	Button writeOff;
	
	protected boolean flag;
	protected MovementIn pin;	//采购入库单
	protected boolean isCancelverify; //是否为核销
	protected ADTable adTable;
	
	ApprovedInvoiceTableManager tableManager;
	StructuredViewer viewer;
	
	public ApprovedDialog(Shell parent, IManagedForm managedForm,
			ADTable adTable, MovementIn pin, boolean isCancelverify){
		super(parent);
		this.managedForm = managedForm;
		this.mmng = managedForm.getMessageManager();
		this.adTable = adTable;
		this.pin = pin;
		this.isCancelverify = isCancelverify;
	}

	private void createViewerAction(final CheckboxTableViewer viewer) {
		viewer.addCheckStateListener(new ICheckStateListener(){
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object o = event.getElement();
				if(o instanceof MovementLine){
					MovementLine l = (MovementLine)o;
					if(event.getChecked()){
						l.setInvoiceLineTotal(l.getAssessLineTotal());
					}else{
						l.setInvoiceLineTotal(null);
					}
					viewer.refresh();
				}
			}
		});
	}
	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		setTitle(Message.getString("inv.in_invoice_title"));
		FormToolkit toolkit = managedForm.getToolkit();
        Composite composite = toolkit.createComposite(parent, SWT.BORDER);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createWriteOffContent(composite, toolkit);
        createTableContent(composite, toolkit);
        
        return composite;
    }
	
	protected void createWriteOffContent(Composite parent, FormToolkit toolkit) {
		Composite client = toolkit.createComposite(parent, SWT.NULL);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        writeOff = toolkit.createButton(client, "", SWT.CHECK);
        writeOff.setLayoutData(new GridData(GridData.CENTER));
        if(isCancelverify) {
        	writeOff.setSelection(true);
        	writeOff.setEnabled(false);
        }
        toolkit.createLabel(client, Message.getString("inv.in_invoice_already"));
	}
	
	protected void createTableContent(Composite parent, FormToolkit toolkit) {
		Composite client = toolkit.createComposite(parent, SWT.NULL);
        client.setLayout(new GridLayout(1, false));
        client.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        tableManager = new ApprovedInvoiceTableManager(adTable,SWT.CHECK);
        viewer = tableManager.createViewer(client, toolkit);
        viewer.setInput(pin.getMovementLines());
        createViewerAction((CheckboxTableViewer) viewer);
        tableManager.updateView(viewer);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			if(writeOff != null){
				flag = writeOff.getSelection();
			}
			if(!validate()) {
				return;
			}
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			mmng.removeAllMessages();
			cancelPressed();
		}
	}
	
	protected boolean validate() {
		List<MovementLine> list = (List<MovementLine>)viewer.getInput();
		if(list == null || list.size() == 0)
			return false;
		for(MovementLine line : list) {
			if(line.getInvoiceLineTotal() == null) {
				if(flag) {
					this.setErrorMessage(String.format(
							Message.getString("inv.must_input_invoice"), line.getMaterialId()));
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		Message.getString("common.ok"), false);
        createButton(parent, IDialogConstants.CANCEL_ID,
        		Message.getString("common.cancel"), false);
    }
	
	public boolean isFlag() {
		return flag;
	}

	public MovementIn getPin() {
		return pin;
	}
}

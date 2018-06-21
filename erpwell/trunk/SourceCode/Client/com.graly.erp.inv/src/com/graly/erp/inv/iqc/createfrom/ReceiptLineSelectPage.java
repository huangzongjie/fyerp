package com.graly.erp.inv.iqc.createfrom;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.model.ReceiptLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardDialog;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class ReceiptLineSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(ReceiptLineSelectPage.class);

	private IqcCreateWizard wizard;
	private ReceiptLineSelectSection section;
	private static final String PREVIOUS = "receiptSelect";

	public ReceiptLineSelectPage(String pageName, Wizard wizard,
			String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (IqcCreateWizard) wizard;
	}

	@Override
	public String doNext() {
		try {
			wizard.getContext().setReceiptLines(section.getSelectedReceiptLines());
		} catch(Exception e) {
			logger.error("ReceiptLineSelectPage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return "finish";
	}

	@Override
	public String doPrevious() {
		((FlowWizardDialog)wizard.getContainer()).updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.next"));
		return PREVIOUS;
	}
	
	@Override
	public IWizardPage getPreviousPage() {
		return wizard.getPage(PREVIOUS);
	}

	@Override
	public void createControl(Composite parent) {
		ADTable adTable = wizard.getContext().getTable(CreateIqcContext.TableName_ReceiptLine);
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));		

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite composite = toolkit.createComposite(parent, SWT.NONE);		
		
        // create section
		section = new ReceiptLineSelectSection(adTable, this);
		Receipt receipt = wizard.getContext().getReceipt();
		section.setParentReceipt(receipt);
		section.createContents(managedForm, composite);
		updateLocalPageContent();
		setControl(composite);
		setPageComplete(false);
	}

	public void updateLocalPageContent() {
		this.refresh();
	}

	@Override
	public void refresh() {
		try {
			if(section != null) {
				Receipt receipt = wizard.getContext().getReceipt();
				section.setParentReceipt(receipt);
				section.refresh();
			}
		} catch(Exception e) {
			logger.error("ReceiptLineSelectPage : refresh() ");
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			((FlowWizardDialog)wizard.getContainer()).updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.finish"));		
		}
	}
}

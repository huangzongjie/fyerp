package com.graly.erp.inv.alarm;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.iqc.createfrom.CreateIqcContext;
import com.graly.erp.inv.iqc.createfrom.IqcCreateWizard;
import com.graly.erp.inv.iqc.createfrom.ReceiptLineSelectPage;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.Receipt;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AlarmReceiptSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(AlarmReceiptSelectPage.class);
	private static String RECEIPTLINESELECT_NEXT = "receiptLineSelect";

	private IqcCreateWizard wizard;
	private AlarmReceiptSelectSection section;
	private Receipt selectedReceipt;
	
	public AlarmReceiptSelectPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (IqcCreateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {
		ADTable adTable = wizard.getContext().getTable(CreateIqcContext.TableName_Receipt);
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));		

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite composite = toolkit.createComposite(parent, SWT.NONE);		
		
        // create section
		section = new AlarmReceiptSelectSection(adTable, this,wizard.getContext().getAlarmWhereClause());
		section.createContents(managedForm, composite);
		setControl(composite);
		setPageComplete(false);
	}
	
	@Override
	public void refresh() {
	}

	@Override
	public String doNext() {
		try {
			if(this.selectedReceipt != null) {
				wizard.getContext().setReceipt(selectedReceipt);
				updateNextPage();
				return RECEIPTLINESELECT_NEXT;
			}
		} catch(Exception e) {
			logger.error("ReceiptSelectPage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
			return "";
		}
		return "";
	}
	
	public void updateNextPage() {
		ReceiptLineSelectPage nextPage = ((ReceiptLineSelectPage)wizard.getPage(RECEIPTLINESELECT_NEXT));
		nextPage.updateLocalPageContent();
	}

	public void setSelectedReceipt(Receipt selectedReceipt) {
		if(selectedReceipt != null) {
			this.selectedReceipt = selectedReceipt;
			setPageComplete(true);
		} else {
			this.selectedReceipt = null;
			setPageComplete(false);
		}
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
    }
	
	public String doPrevious() {
		return "";
	}
	
	public IWizardPage getPreviousPage() {
		return null;
	}

	public AlarmReceiptSelectSection getSection() {
		return section;
	}
}

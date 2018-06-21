package com.graly.erp.bj.inv.in.createfrom.po;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.po.model.PurchaseOrder;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BJPoSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(BJPoSelectPage.class);
	private static String POlINESELECT_NEXT = "poLineSelect";

	private BJPoCreateWizard wizard;
	private BJPoSelectSection section;
	private PurchaseOrder selectedPo;
	
	public BJPoSelectSection getSection() {
		return section;
	}

	public BJPoSelectPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (BJPoCreateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {		
		ADTable adTable = wizard.getContext().getTable(BJCreateContext.TableName_Po);
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));		

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite composite = toolkit.createComposite(parent, SWT.NONE);		
		
        // create section
		section = new BJPoSelectSection(adTable, this);
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
			if(this.selectedPo != null) {
				wizard.getContext().setPo(selectedPo);
				updateNextPage();
				return POlINESELECT_NEXT;
			}
		} catch(Exception e) {
			logger.error("MOGeneratePage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
			return "";
		}
		return "";
	}
	
	public void updateNextPage() {
		((BJPoLineSelectPage)wizard.getPage(POlINESELECT_NEXT)).updateLocalPageContent();
	}

	public void setSelectionPo(PurchaseOrder selectedPo) {
		if(selectedPo != null) {
			this.selectedPo = selectedPo;
			setPageComplete(true);
		} else {
			this.selectedPo = null;
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
}

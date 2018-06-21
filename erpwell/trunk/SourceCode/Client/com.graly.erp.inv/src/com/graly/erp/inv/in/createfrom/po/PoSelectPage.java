package com.graly.erp.inv.in.createfrom.po;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.in.createfrom.iqc.CreateContext;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PoSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(PoSelectPage.class);
	private static String POlINESELECT_NEXT = "poLineSelect";

	private PoCreateWizard wizard;
	private PoSelectSection section;
	private PurchaseOrder selectedPo;
	
	public PoSelectSection getSection() {
		return section;
	}

	public PoSelectPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (PoCreateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {		
		ADTable adTable = wizard.getContext().getTable(CreateContext.TableName_Po);
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));		

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite composite = toolkit.createComposite(parent, SWT.NONE);		
		
        // create section
		section = new PoSelectSection(adTable, this);
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
		((PoLineSelectPage)wizard.getPage(POlINESELECT_NEXT)).updateLocalPageContent();
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

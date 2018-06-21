package com.graly.erp.inv.alarm;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.in.createfrom.iqc.CreateContext;
import com.graly.erp.inv.in.createfrom.iqc.IqcCreateWizard;
import com.graly.erp.inv.in.createfrom.iqc.IqcLineSelectPage;
import com.graly.erp.inv.model.Iqc;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AlarmIqcSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(AlarmIqcSelectPage.class);
	private static String IQClINESELECT_NEXT = "iqcLineSelect";

	private IqcCreateWizard wizard;
	private AlarmIqcSelectSection section;
	private Iqc selectedIqc;
	
	public AlarmIqcSelectPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (IqcCreateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {		
		ADTable adTable = wizard.getContext().getTable(CreateContext.TableName_Iqc);
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));		

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite composite = toolkit.createComposite(parent, SWT.NONE);		
		
        // create section
		section = new AlarmIqcSelectSection(adTable, this,wizard.getContext().getAlarmWhereClause());
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
			if(this.selectedIqc != null) {
				wizard.getContext().setIqc(selectedIqc);
				updateNextPage();
				return IQClINESELECT_NEXT;
			}
		} catch(Exception e) {
			logger.error("IqcSelectPage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
			return "";
		}
		return "";
	}
	
	public void updateNextPage() {
		((IqcLineSelectPage)wizard.getPage(IQClINESELECT_NEXT)).updateLocalPageContent();
	}

	public void setSelectionIqc(Iqc selectedIqc) {
		if(selectedIqc != null) {
			this.selectedIqc = selectedIqc;
			setPageComplete(true);
		} else {
			this.selectedIqc = null;
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

	public AlarmIqcSelectSection getSection() {
		return section;
	}
}

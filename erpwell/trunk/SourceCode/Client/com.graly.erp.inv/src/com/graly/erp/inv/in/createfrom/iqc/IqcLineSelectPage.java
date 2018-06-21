package com.graly.erp.inv.in.createfrom.iqc;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.IqcLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class IqcLineSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(IqcLineLotSelectPage.class);
	private static String IQCLINELOT_NEXT = "iqcLineLotSelect";
	private static final String PREVIOUS = "iqcSelect";

	private IqcCreateWizard wizard;
	private IqcLineSelectSection section;
	
	public IqcLineSelectPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (IqcCreateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {		
		ADTable adTable = wizard.getContext().getTable(CreateContext.TableName_IqcLine);
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite composite = toolkit.createComposite(parent, SWT.NONE);
		
        // create section
		section = new IqcLineSelectSection(adTable, this);
		section.createContents(managedForm, composite);
		updateLocalPageContent();
		this.setPageComplete(false);
		setControl(composite);
	}
	
	@Override
	public void refresh() {
		if(section != null) {
			section.setParentIqc(wizard.getContext().getIqc());
			section.refresh();			
		}
	}

	@Override
	public String doNext() {
		try {
			List<IqcLine> lines = section.getSelectedIqcLine();
			if(lines != null && lines.size() > 0) {
				if(validate(lines)) {
					wizard.getContext().setIqcLines(lines);
					updateNextPage();
					return IQCLINELOT_NEXT;
				}
			}
		} catch(Exception e) {
			logger.error("IqcLineSelectPage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return "";		
	}
	
	private boolean validate(List<IqcLine> lines) {
		for(IqcLine iqcLine : lines) {
			Material material = iqcLine.getMaterial();
			if(material == null || !material.getIsLotControl()) {
				this.setErrorMessage(String.format(Message.getString("inv.material_is_not_control_by_lot"),
						iqcLine.getMaterialId()));
				return false;
			}
		}
		return true;
	}
	
	public void updateLocalPageContent() {
		this.refresh();
	}
	
	public void updateNextPage() {
		((IqcLineLotSelectPage)wizard.getPage(IQCLINELOT_NEXT)).updateLocalPageContent();
	}

	@Override
	public boolean canFlipToNextPage() {
        return isPageComplete();
    }
	
	public String doPrevious() {
		this.setErrorMessage(null);
		return PREVIOUS;
	}
	
	public IWizardPage getPreviousPage() {
		return wizard.getPage(PREVIOUS);
	}
}

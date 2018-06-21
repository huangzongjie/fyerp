package com.graly.erp.inv.in.createfrom.po;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.in.createfrom.iqc.CreateContext;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PoLineSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(PoLineLotSelectPage.class);
	private static String POLINELOT_NEXT = "poLineLotSelect";
	private static final String PREVIOUS = "poSelect";

	private PoCreateWizard wizard;
	private PoLineSelectSection section;
	
	public PoLineSelectPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (PoCreateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {		
		ADTable adTable = wizard.getContext().getTable(CreateContext.TableName_PoLine);
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));		

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite composite = toolkit.createComposite(parent, SWT.NONE);
		
       // create section
		section = new PoLineSelectSection(adTable, this);
		section.createContents(managedForm, composite);
		updateLocalPageContent();
		this.setPageComplete(false);
		setControl(composite);
	}
	
	@Override
	public void refresh() {
		if(section != null) {
			section.setParentPo(wizard.getContext().getPo());
			section.refresh();			
		}
	}

	@Override
	public String doNext() {
		try {
			List<PurchaseOrderLine> lines = section.getSelectedPoLine();
			if(lines != null && lines.size() > 0) {
				if(validate(lines)) {
					wizard.getContext().setPoLines(lines);
					updateNextPage();
					return POLINELOT_NEXT;
				}
			}
		} catch(Exception e) {
			logger.error("PoLineSelectPage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return "";		
	}
	
	private boolean validate(List<PurchaseOrderLine> lines) {
		Map<Long, PurchaseOrderLine> poLineMap = new HashMap<Long, PurchaseOrderLine>();
		for(PurchaseOrderLine poLine : lines) {
			if (!poLineMap.containsKey(poLine.getWarehouseRrn())) {
				poLineMap.put(poLine.getWarehouseRrn(), poLine);
			}
			Material material = poLine.getMaterial();
			if(material == null || !material.getIsLotControl()) {
				this.setErrorMessage(String.format(Message.getString("inv.material_is_not_control_by_lot"),
						poLine.getMaterialId()));
				return false;
			}
		}
		if(poLineMap.size() > 1){
			this.setErrorMessage(Message.getString("inv.different_warehouse"));
			return false;
		}
		return true;
	}
	
	public void updateLocalPageContent() {
		this.refresh();
	}
	
	public void updateNextPage() {
		((PoLineLotSelectPage)wizard.getPage(POLINELOT_NEXT)).updateLocalPageContent();
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

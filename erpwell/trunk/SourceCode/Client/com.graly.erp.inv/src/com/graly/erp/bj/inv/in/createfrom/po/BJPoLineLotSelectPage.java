package com.graly.erp.bj.inv.in.createfrom.po;

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
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardDialog;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class BJPoLineLotSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(BJPoLineLotSelectPage.class);
	private static final String PREVIOUS = "poLineSelect";

	private BJPoCreateWizard wizard;
	private BJPoLineLotSelectSection section;
	
	public BJPoLineLotSelectPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (BJPoCreateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {
		ADTable adTablePo = wizard.getContext().getTable(BJCreateContext.TableName_Lot);
		ADTable adTablePoLine = wizard.getContext().getTable(BJCreateContext.TableName_PoLine);
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTablePo, "label")));		

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite composite = toolkit.createComposite(parent, SWT.NONE);		
		
        // create section
		section = new BJPoLineLotSelectSection(adTablePo, this, wizard, adTablePoLine);
		section.createContents(managedForm, composite);
		updateLocalPageContent();
		this.setPageComplete(false);
		setControl(composite);
	}
	
	@Override
	public void refresh() {
		try {
			if(section != null) {
				section.refresh();	
				section.refField_PoLine.setInput(wizard.getContext().getPoLines());
			}
		} catch(Exception e) {
			logger.error("IqcLineLotSelectPage : refresh() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		} finally {
			((FlowWizardDialog)wizard.getContainer()).updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.finish"));		
		}
	}

	@Override
	public String doNext() {
		try {
			if(isMaterilaType()) {
				wizard.getContext().setIn(getInWarehouse());
				return "finish";
			}
			MovementIn in = section.getInWarehouse();
			if(in != null) {
				wizard.getContext().setIn(in);
				List<Lot> lots = section.getLots();
				if(lots != null && lots.size() > 0) {
					wizard.getContext().setLots(lots);
					return "finish";
				}
			}
		} catch(Exception e) {
			logger.error("PoLineLotSelectPage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return "";
	}
	
	public void updateLocalPageContent() {
		this.refresh();
	}
	
	protected MovementIn getInWarehouse() {
		PurchaseOrderLine poLine = wizard.getContext().getPoLines().get(0);
		MovementIn in = new MovementIn();
		in.setOrgRrn(poLine.getOrgRrn());
		in.setDocStatus(MovementIn.STATUS_DRAFTED);
		in.setWarehouseRrn(poLine.getWarehouseRrn());
		return in;
	}

	@Override
	public boolean canFlipToNextPage() {
		if(isMaterilaType())
			return true;
		return isPageComplete();
    }
	
	@Override
	public boolean isJumpOver() {
		//如果入库的全为Material类型物料，则到下一步，无需输入批次
		return isMaterilaType();
	}
	
	public boolean isMaterilaType() {
		List<PurchaseOrderLine> poLines = wizard.getContext().getPoLines();
		if(poLines != null) {
			for(PurchaseOrderLine line : poLines) {
				if(!Lot.LOTTYPE_MATERIAL.equals(line.getLotType())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public String doPrevious() {
		this.setErrorMessage(null);
		((FlowWizardDialog)wizard.getContainer()).updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.next"));
		return PREVIOUS;
	}
	
	public IWizardPage getPreviousPage() {
		return wizard.getPage(PREVIOUS);
	}
}

package com.graly.erp.inv.in.createfrom.iqc;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.erp.inv.model.MovementIn;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardDialog;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class IqcLineLotSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(IqcLineLotSelectPage.class);
	private static final String PREVIOUS = "iqcLineSelect";

	private IqcCreateWizard wizard;
	private IqcLineLotSelectSection section;
	
	public IqcLineLotSelectPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (IqcCreateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {
		ADTable adTable = wizard.getContext().getTable(CreateContext.TableName_Lot);
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));		

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite composite = toolkit.createComposite(parent, SWT.NONE);		
		
        // create section
		section = new IqcLineLotSelectSection(adTable, this, wizard.getContext().getIqc());
		section.createContents(managedForm, composite);
		updateLocalPageContent();
		this.setPageComplete(false);
		setControl(composite);
	}
	
	@Override
	public void refresh() {
		try {
			if(section != null) {
				section.setParentIqc(wizard.getContext().getIqc());
				section.setIqcLines(wizard.getContext().getIqcLines());
				section.initContent();
				section.refresh();
			}
		} catch(Exception e) {
			logger.error("IqcLineLotSelectPage : refresh() ");
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			((FlowWizardDialog)wizard.getContainer()).updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.finish"));		
		}
	}

	@Override
	public String doNext() {
		try {
//			if(this.isMaterilaType()) {
//				wizard.getContext().setIn(getInWarehouse());
//				return "finish";
//			}
			MovementIn in = section.getInWarehouse();
			if(in != null) {
				wizard.getContext().setIn(in);
				List<Lot> lots = section.getSelectedLots();
//				wizard.getContext().setIqcLines(section.getFinallyIqcLines());
				if(lots != null && lots.size() > 0) {
					wizard.getContext().setLots(lots);
					return "finish";
				}
			}
		} catch(Exception e) {
			logger.error("IqcLineLotSelectPage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return "";
	}
	
	public void updateLocalPageContent() {
		this.refresh();
	}
	
	/* 若输入了入库仓库，则新建一个入库单，并将入库仓库数据赋给入库单*/
	protected MovementIn getInWarehouse() {
		Iqc iqc = wizard.getContext().getIqc();
		MovementIn in = new MovementIn();
		in.setOrgRrn(iqc.getOrgRrn());
		in.setDocStatus(MovementIn.STATUS_DRAFTED);
		in.setWarehouseRrn(iqc.getWarehouseRrn());
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
//		return isMaterilaType();
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
	
	public boolean isMaterilaType() {
		List<IqcLine> iqcLines = wizard.getContext().getIqcLines();
		if(iqcLines != null) {
			for(IqcLine line : iqcLines) {
				if(!Lot.LOTTYPE_MATERIAL.equals(line.getLotType())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public List<IqcLine> getSelectedIqcLines() {
		return wizard.getContext().getIqcLines();
	}

	@Override
	public IqcCreateWizard getWizard() {
		return wizard;
	}
}

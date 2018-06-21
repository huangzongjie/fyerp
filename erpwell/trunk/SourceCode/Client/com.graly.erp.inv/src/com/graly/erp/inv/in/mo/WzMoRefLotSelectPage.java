package com.graly.erp.inv.in.mo;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardDialog;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
/**
 * @author Jim
 * 向导第二页，选中此次需要入库的批次
 */
public class WzMoRefLotSelectPage extends FlowWizardPage {
//	private static final Logger logger = Logger.getLogger(WzMoRefLotSelectPage.class);
	private static String PREVIOUS_PAGE = "moSelect";
//	private static String NEXT_PAGE = "moRefLineLot";
	
	protected WzMoInWizard wizard;
	protected WzMoRefLotSelectSection moRefLotSection;

	public WzMoRefLotSelectPage(String pageName, Wizard wizard,
			String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (WzMoInWizard)wizard;
	}

	@Override
	public String doNext() {
		List<Lot> lots = moRefLotSection.getSelectionList();
		wizard.context.setSelectedLots(lots);
		return "finish";
//		if((lots != null && lots.size() > 0)) {
//			wizard.context.setSelectedLots(lots);
//			return "finish";
//		}
//		else {
//			//如果为Batch类型，因为一批可以几次入库，所以当lots为空时，有可能是该批次已经入库了部分，并且入库单已审核
//			//所以在通过getAvailableLot4In得不到在生产线上的批次()，但是仍可以入库
//			if(wizard.getContext().getMo().getMaterial() != null) {
//				if(Lot.LOTTYPE_BATCH.equals(wizard.getContext().getMo().getMaterial().getLotType())) {
//					return "finish";
//				}
//			}
//		}
//		return "";
	}

	@Override
	public String doPrevious() {
		setFinishName(false);
		return PREVIOUS_PAGE;
	}

	@Override
	public void createControl(Composite parent) {
		ADTable adTable = wizard.getContext().getAdTable_LOT();
		setTitle(String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label")));
		
		ManagedForm managedForm = (ManagedForm)wizard.getContext().getManagedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite client = toolkit.createComposite(parent, SWT.NONE);
		client.setLayout(new GridLayout(1, false));
		client.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createSection(adTable, client, managedForm);
		setFinishName(true);
		setControl(client);
	}
	
	protected void createSection(ADTable adTable, Composite client, ManagedForm managedForm) {
		moRefLotSection = new WzMoRefLotSelectSection(adTable, wizard.getContext().getMo());
		moRefLotSection.createContents(managedForm, client);
	}
	
	protected void updateNextPageContent() throws Exception{
//		WzMoRefLineLotPage nexePage = (WzMoRefLineLotPage)wizard.getPage(NEXT_PAGE);
//		if(nexePage != null) {
//		}
	}

	public void updateLocalPageContent() throws Exception {
		if(moRefLotSection != null) {
			ManufactureOrder mo = wizard.getContext().getMo();
			if(mo.getObjectRrn() != null) {
				WipManager wipManager = Framework.getService(WipManager.class);
				List<Lot> lots = wipManager.getAvailableLot4In(mo.getObjectRrn());
				moRefLotSection.setLots(lots);
				refresh();
			}
			setFinishName(true);
		}
	}
	
	private void setFinishName(boolean isFinish) {
		if(isFinish) {
			((FlowWizardDialog)wizard.getContainer()).updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.finish"));
		} else {
			((FlowWizardDialog)wizard.getContainer()).updateButtonName(IDialogConstants.NEXT_ID, Message.getString("common.next"));
		}
	}

	public IWizardPage getPreviousPage() {
		this.setErrorMessage(null);
		return wizard.getPage(PREVIOUS_PAGE);
	}
	
	@Override
	public void refresh() {
		moRefLotSection.refresh();
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
    }
}

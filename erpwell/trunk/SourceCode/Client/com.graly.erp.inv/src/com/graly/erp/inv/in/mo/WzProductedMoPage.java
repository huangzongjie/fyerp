package com.graly.erp.inv.in.mo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

/**
 * @author Jim
 * 向导第一页，显示已经生产的(包括生产完成或只完成一部分生产)工作令列表
 */
public class WzProductedMoPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(WzProductedMoPage.class);
	private static String PREVIOUS_PAGE = null;
	private static String NEXT_PAGE = "moRefLot";
	
	protected WzProductedMoSection productedMoSection;
	protected WzMoInWizard wizard;
	INVManager invManager;

	public WzProductedMoPage(String pageName, Wizard wizard,
			String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (WzMoInWizard)wizard;
	}

	@Override
	public String doNext() {
		try {
			if(productedMoSection.getSelectedMo() != null) {
				ManufactureOrder mo = productedMoSection.getSelectedMo();
				wizard.getContext().setMo(mo);
				if(mo.getMaterial() != null && Lot.LOTTYPE_MATERIAL.equals(mo.getMaterial().getLotType())) {
					if(invManager == null)
						invManager = Framework.getService(INVManager.class);
					Lot lot = invManager.getMaterialLot(mo.getOrgRrn(), mo.getMaterial(), Env.getUserRrn());
					wizard.context.setMaterialType(true);
					List<Lot> selectedLots = new ArrayList<Lot>();
					if(mo.getQtyReceive() != null && mo.getQtyIn() != null) {
						lot.setQtyCurrent(mo.getQtyReceive().subtract(mo.getQtyIn()));
					} else if(mo.getQtyIn() == null) {
						lot.setQtyCurrent(mo.getQtyReceive());
					}
					selectedLots.add(lot);
					wizard.context.setSelectedLots(selectedLots);
					return "finish";
				} else {
					updateNextPageContent();
					return NEXT_PAGE;					
				}
			} else {
				UI.showWarning(Message.getString("inv.entityisnull"));
				return "";
			}
		} catch(Exception e) {
			logger.error("WzProductedMoPage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
			return "";
		}
	}
	
	protected void updateNextPageContent() throws Exception{
		WzMoRefLotSelectPage nexePage = ((WzMoRefLotSelectPage)wizard.getPage(NEXT_PAGE));
		if(nexePage != null) {
			nexePage.updateLocalPageContent();
		}
	}

	@Override
	public String doPrevious() {
		return PREVIOUS_PAGE;
	}

	@Override
	public void createControl(Composite parent) {
		ADTable adTable = wizard.context.getAdTable_ProductedMO();
		setTitle(String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label")));
		
		ManagedForm managedForm = (ManagedForm)wizard.getContext().getManagedForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Composite client = toolkit.createComposite(parent, SWT.NULL);;
		
		createSection(adTable, client, managedForm);
		this.setControl(client);
	}
	
	protected void createSection(ADTable adTable, Composite client, ManagedForm managedForm) {
		productedMoSection = new WzProductedMoSection(new EntityTableManager(adTable));
		productedMoSection.createContent(managedForm, client);
	}

	public IWizardPage getPreviousPage() {
		return null;
	}
}

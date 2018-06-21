package com.graly.erp.wip.mo.create;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
/**
 * @author Administrator
 * 生成MO向导页第一个页面，输入MO的具体信息
 */
public class MOGeneratePage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(MOGeneratePage.class);
	private static String MOGENERATE_NEXT = "alternateSelect";
	
	private MOGenerateSection section;
	private MOGenerateWizard wizard;
	
	public MOGeneratePage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (MOGenerateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {		
		ADTable adTable = wizard.getContext().getAdTable_MO();
		setTitle(String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(adTable, "label")));		

		ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
		Composite composite = null;
		FormToolkit toolkit = null;
		toolkit = managedForm.getToolkit();
		composite = toolkit.createComposite(parent, SWT.NONE);		
		
        // create section
		section = new MOGenerateSection(adTable, this);
		section.createContents(managedForm, composite);
		setControl(composite);
	}
	
	@Override
	public void refresh() {
		
	}
	
	public String doPrevious() {
		return "";
	}
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}

	@Override
	public String doNext() {
		try {
			if(section.isCanSave()) {
				ManufactureOrder mo = (ManufactureOrder)section.getAdObject();
				// 将计划开始和结束日期设为排程的开始和结束日期的初始值
				mo.setDateStart(mo.getDatePlanStart());
				mo.setDateEnd(mo.getDatePlanEnd());
				// 将交货日期设为生产结束日期
				mo.setDateDelivery(mo.getDatePlanEnd());
				wizard.getContext().setManufactureOrder(mo);
				updateNextPageContent();
				return MOGENERATE_NEXT;
			} else {
				return "";
			}
		} catch(Exception e) {
			logger.error("MOGeneratePage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
			return "";
		}
	}
	
	protected void updateNextPageContent() throws Exception{
		MOAlternateSelectPage nexePage = ((MOAlternateSelectPage)wizard.getPage(MOGENERATE_NEXT));
		if(nexePage != null) {
			nexePage.updateLocalPageContent();
		}
	}
	
	public ManufactureOrder getManufactureOrder() {
		return wizard.getContext().getManufactureOrder();
	}
	
	public IWizardPage getPreviousPage() {
		return null;
	}

}

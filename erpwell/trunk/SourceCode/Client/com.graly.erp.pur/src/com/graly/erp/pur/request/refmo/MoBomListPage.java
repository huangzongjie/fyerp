package com.graly.erp.pur.request.refmo;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class MoBomListPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(MoBomListPage.class);
	private static String MOGENERATE_NEXT = "subMOLine";
	private static String PREVIOUS = "alternateSelect";
	
	private MoBomListSection section;
	private MoViewWizard wizard;
	
	public MoBomListPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (MoViewWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {		
		try {
			ADTable adTable = wizard.getContext().getAdTable_MOBom();
			setTitle(I18nUtil.getI18nMessage(adTable, "label"));
			
			ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
			
			Composite composite = null;
			FormToolkit toolkit = null;
			toolkit = managedForm.getToolkit();
			composite = toolkit.createComposite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(1, false));
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			// create section
			MoBomTreeManager treeManager = new MoBomTreeManager(adTable);
			section = new MoBomListSection(treeManager, this);
			section.createContents(managedForm, composite);
			setPageComplete(true);
//			ManufactureOrder mo = wizard.getContext().getManufactureOrder();
			setControl(composite);
			updateLocalPageContent();
		} catch(Exception e) {
			logger.error("MOBomListPage : createControl() ");
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	public String doPrevious() {
		this.setErrorMessage(null);
		return "";
	}
	
	public IWizardPage getPreviousPage() {
		ManufactureOrder mo = wizard.getContext().getManufactureOrder();
		if(mo != null && mo.getObjectRrn() != null) {
			return null;
		}
		return wizard.getPage("");
	}

	@Override
	public String doNext() {
		wizard.getContext().setMoBoms(section.getMOBoms());
		updateNextPageContent();
		return MOGENERATE_NEXT;
	}
	
	@Override
	public void refresh() {
		section.refresh();
	}
	
	public void updateLocalPageContent() throws Exception{
		if(section != null) {
			ManufactureOrder mo = wizard.getContext().getManufactureOrder();
			List<ManufactureOrderBom> moBoms = wizard.getContext().getMoBoms();
			List<ManufactureOrderBom> boms = null;
			WipManager wipManager = Framework.getService(WipManager.class);
			if(mo != null && mo.getObjectRrn() != null) {
				// 如果mo.getObjectRrn()有值且不可编辑,则从数据库中直接获取
				boms = wipManager.getMoBomDetailFromDB(mo);
				this.setPreviousPage(null);
			} else if(mo.getMaterialRrn() != null && wizard.isCanEdit()) {
				boms = wipManager.generateMoBomDetail(mo, moBoms);
			}
			if(boms == null || boms.size() == 0) {
				setPageComplete(false);
			}
			MoBomItemAdapter.setMoBoms(boms);
			section.setInput(boms);
			refresh();
		}
	}
	
	protected void updateNextPageContent() {
		SubMoLinePage nexePage = ((SubMoLinePage)this.getWizard().getPage(MOGENERATE_NEXT));
		if(nexePage != null) {
			nexePage.updateLocalPageContent();
		}
	}
	
	@Override
	public boolean canFlipToNextPage() {
        return isPageComplete();
    }
}

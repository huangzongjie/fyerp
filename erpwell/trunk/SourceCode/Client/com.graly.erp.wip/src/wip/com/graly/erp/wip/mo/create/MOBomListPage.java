package com.graly.erp.wip.mo.create;

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
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
/**
 * @author Administrator
 * ����MO��ҳ������ҳ�棬��ʾ����Bom������Ϻ�ľ����Bom�ṹ���ų̺�ľ���ʱ��
 */
public class MOBomListPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(MOBomListPage.class);
	private static String MOGENERATE_NEXT = "subMOLine";
	private static String PREVIOUS = "alternateSelect";
	
	private MOBomListSection section;
	private MOGenerateWizard wizard;
	
	public MOBomListPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (MOGenerateWizard)wizard;
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
			MOBomTreeManager treeManager = new MOBomTreeManager(adTable);
			section = new MOBomListSection(treeManager, this);
			section.createContents(managedForm, composite);
			setPageComplete(true);
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
		return PREVIOUS;
	}
	
	public IWizardPage getPreviousPage() {
		ManufactureOrder mo = wizard.getContext().getManufactureOrder();
		if(mo != null && mo.getObjectRrn() != null) {
			return null;
		}
		return wizard.getPage(PREVIOUS);
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
	
	public void updateLocalPageContent() throws Exception {
		if(section != null) {
			ManufactureOrder mo = wizard.getContext().getManufactureOrder();
			List<ManufactureOrderBom> moBoms = wizard.getContext().getMoBoms();
			WipManager wipManager = Framework.getService(WipManager.class);
			ADManager adManager = Framework.getService(ADManager.class);
			if(mo.getIsPrepareMo()){
				moBoms = adManager.getEntityList(Env.getOrgRrn(), ManufactureOrderBom.class,Integer.MAX_VALUE,"moRrn ="+mo.getObjectRrn(),null);
			}
			
			List<ManufactureOrderBom> boms = null;
			// ���mo.getObjectRrn()��ֵ�Ҳ��ɱ༭,������ݿ���ֱ�ӻ�ȡ
			if(mo != null && mo.getObjectRrn() != null) {
				if(mo.getIsPrepareMo()){
//					if(mo.getHasFirstCountBOM()== null){
					if(!mo.getHasFirstCountBOM()){
						//��һ�����ɹ�����,����ͳ������BOM��Ϣ,������
						boms = wipManager.generateFirstPrepareMoBomDetail(mo, moBoms);
					}else{
						//�ڶ��ο�ʼ���ɵĹ�����
						boms = wipManager.getPerpareMoBomDetailFromDB(mo, moBoms);
					}
				}else{
					boms = wipManager.getMoBomDetailFromDB(mo);
				}
			} else if(mo.getMaterialRrn() != null && wizard.isCanEdit()) {
				boms = wipManager.generateMoBomDetail(mo, moBoms);
			}
			if(boms == null || boms.size() == 0) {
				setPageComplete(false);
			}
			MOBomItemAdapter.setMoBoms(boms);
			section.setInput(boms);
			refresh();
		}
	}
	
	protected void updateNextPageContent() {
		SubMOLinePage nexePage = ((SubMOLinePage)this.getWizard().getPage(MOGENERATE_NEXT));
		if(nexePage != null) {
			nexePage.updateLocalPageContent();
		}
	}
	
	@Override
	public boolean canFlipToNextPage() {
        return isPageComplete();
    }

	//ֻ�е�moΪDraft״̬���ѱ��浽DB��ʱ�ſ����ڴ˽������ÿ������
	public boolean getIsCanSetAlternateMaterial() {
		ManufactureOrder mo = wizard.getContext().getManufactureOrder();
		if(mo.getObjectRrn() != null && ManufactureOrder.STATUS_DRAFTED.equals(mo.getDocStatus()))
			return true;
		return false;
	}
	
	public ManufactureOrder getManufactureOrder() {
		return wizard.getContext().getManufactureOrder();
	}
}

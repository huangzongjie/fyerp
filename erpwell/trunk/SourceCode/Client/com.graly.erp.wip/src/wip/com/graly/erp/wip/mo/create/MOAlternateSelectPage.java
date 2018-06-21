package com.graly.erp.wip.mo.create;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.ppm.model.TpsLine;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
/**
 * @author Administrator
 * ����MO��ҳ�ڶ���ҳ�棬���ÿ������
 */
public class MOAlternateSelectPage extends FlowWizardPage {
	private static final Logger logger = Logger.getLogger(MOAlternateSelectPage.class);
	private static String MOGENERATE_NEXT = "moBomList";
	private static String PREVIOUS = "moGenerate";
	
	private MOAlternateSelectSection section;
	private MOGenerateWizard wizard;
	private List<TreeItem> treeItems= new ArrayList<TreeItem>();
	
	public MOAlternateSelectPage(String pageName, Wizard wizard, String defaultDirect) {
		super(pageName, wizard, defaultDirect);
		this.wizard = (MOGenerateWizard)wizard;
	}

	@Override
	public void createControl(Composite parent) {		
		try {
			ADTable adTable = wizard.getContext().getAdTable_MOBom();
			setTitle(Message.getString("pdm.bom_list"));
			
			ManagedForm managedForm = (ManagedForm)wizard.getContext().getMangedForm();
			FormToolkit toolkit = managedForm.getToolkit();
			Composite composite = toolkit.createComposite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(1, false));
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			// create section
			section = new MOAlternateSelectSection(adTable, this);
			section.createContents(managedForm, composite);
			setPageComplete(true);
			setControl(composite);
//			if(wizard.getContext().getManufactureOrder() != null) {
//				updateLocalPageContent();
//			}
			updateLocalPageContent();
		} catch(Exception e) {
			logger.error("MOAlternateSelectPage : createControl() ");
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	public String doPrevious() {
		this.setErrorMessage(null);
		return PREVIOUS;
	}
	
	public IWizardPage getPreviousPage() {
		return wizard.getPage(PREVIOUS);
	}

	@Override
	public String doNext() {
		try {
			List<ManufactureOrderBom> moBoms = section.getMOBoms();
			wizard.getContext().setMoBoms(moBoms);
			updateNextPageContent();
			ManufactureOrder mo = wizard.getContext().getManufactureOrder();
			if(mo.getIsPrepareMo()){
				return "finish";
			}
			String catagory = wizard.getContext().getCategory();
			if(catagory!=null && catagory.equals("prepareGenerateMO")){
				//�½�Ԥ����ť,���뺬��Ԥ�������ϲ������
				if(!mo.getIsPrepareMo()){
					UI.showError("�½�Ԥ��������������Ԥ��������");
					return "";
				}
			}
			return MOGENERATE_NEXT;
		} catch(Exception e) {
			logger.error("MOAlternateSelectPage : doNext() ");
			ExceptionHandlerManager.asyncHandleException(e);
			return "";
		}
	}

	protected void updateNextPageContent() throws Exception{
		MOBomListPage nexePage = ((MOBomListPage)wizard.getPage(MOGENERATE_NEXT));
		if(nexePage != null) {
			nexePage.updateLocalPageContent();
		}
	}

	public void updateLocalPageContent() throws Exception{
		if(section != null) {
			ManufactureOrder mo = wizard.getContext().getManufactureOrder();
			if(mo.getMaterialRrn() != null) {
				WipManager wipManager = Framework.getService(WipManager.class);
				List<ManufactureOrderBom> boms = null;
				if(!mo.getIsPrepareMo()){
					boms = wipManager.getMoBom(Env.getOrgRrn(), mo.getMaterialRrn());
				}else{
					//���Ԥ������������⴦��,��ʾ�Ƿ���Ԥ��������
					if(mo.getObjectRrn()==null){
						//��ҵ���߼���������ʱ�ƻ��ļƻ���Ų�ѯ���Ѿ�����ɹ�����Ϣ
						if(mo.getTpsRrn()!=null){
							TpsLine tpsline = new TpsLine();
							tpsline.setObjectRrn(mo.getTpsRrn());
							tpsline.setOrgRrn(mo.getOrgRrn());
							tpsline.setMaterialRrn(mo.getMaterialRrn());
							boms = wipManager.getMoBomFromPrepareTpsLine(Env.getOrgRrn(), Env.getUserRrn(),tpsline);
						}else{
//							long startTime = System.currentTimeMillis();
							boms = wipManager.getMoBom(Env.getOrgRrn(), mo.getMaterialRrn());
//							long endTime = System.currentTimeMillis();
						}
					}else{
						boms = wipManager.getMoBomDetailFromDB(mo);
					}
				}
				if(boms == null || boms.size() == 0) {
					setPageComplete(false);
				}
				MOBomItemAdapter.setMoBoms(boms);
				section.setInput(boms);
				refresh();
				String catagory = wizard.getContext().getCategory();
				if(catagory!=null && catagory.equals("prepareGenerateMO")){
					//�����⹺��������������Ϊ���ƣ������Ƽ�û��BOM�����Ͻ���Ԥ��������ʱ�Զ�����������������ΪY
					if(mo.getObjectRrn()==null){
						//�Զ������BOM
						List<ManufactureOrderBom> selfControlBoms = new ArrayList<ManufactureOrderBom>();
						for(ManufactureOrderBom bom:boms){
							if(bom.getIsSelfControl()){
								selfControlBoms.add(bom);
							}
						}
						//���Ƽ��Զ���������������ΪY
						for(ManufactureOrderBom bom :selfControlBoms){
							TreeViewer treeViewer = (TreeViewer) section.getViewer();
							List allItems = new ArrayList();
							getAllItems(treeViewer.getTree(), allItems);//�������нڵ�
							final int size =allItems.size();
							TreeItem[] arr = (TreeItem[])allItems.toArray(new TreeItem[size]);
							for(TreeItem treeItem : arr){
								ManufactureOrderBom itemBom = (ManufactureOrderBom) treeItem.getData();
								boolean flag =false;
								if(itemBom.getMaterialRrn().equals(bom.getMaterialRrn())&& itemBom.getPath().equals(bom.getPath())){
									treeViewer.getTree().setSelection(treeItem);
									break;
								}
							}
							section.setSelectionMoBom(bom);
							section.prepareAdapter();
						}
					}
				
				}
			}
		}
	}
	
	@Override
	public void refresh() {
		section.refresh();
	}

	@Override
	public boolean canFlipToNextPage() {
		ManufactureOrder mo = wizard.getContext().getManufactureOrder();
		if(mo.getObjectRrn()!=null && mo.getIsPrepareMo()){
			//�ٴε㿪Ԥ�������,��ҳ����һ�����ɼ�
			return false;
		}
        return isPageComplete();
    }
	
	public ManufactureOrder getManufactureOrder() {
		return wizard.getContext().getManufactureOrder();
	}

	//�ݹ�õ���������
	public void getAllChildItems(TreeItem currentItem){
		treeItems.add(currentItem);
		List<TreeItem> childItems = Arrays.asList(currentItem.getItems());
		if(childItems!=null && childItems.size()>0){
			for(TreeItem childItem :childItems){
				getAllChildItems(childItem);
			}
		}
	}
	
	private static void getAllItems(Tree tree, List<TreeItem> allItems)
	{
	    for(TreeItem item : tree.getItems())
	    {
	        getAllItems(item, allItems);
	    }
	}

	private static void getAllItems(TreeItem currentItem, List<TreeItem> allItems)
	{
	    TreeItem[] children = currentItem.getItems();

	    for(int i = 0; i < children.length; i++)
	    {
	        allItems.add(children[i]);

	        getAllItems(children[i], allItems);
	    }
	}
	
}

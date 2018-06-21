package com.graly.erp.wip.mo.create;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.BomDetail;
import com.graly.erp.pdm.model.MaterialAlternate;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class MOAlternateSelectSection {
	private static final Logger logger = Logger.getLogger(MOAlternateSelectSection.class);
	private static final String TABLE_NAME = "PDMAlternate";
	private MOAlternateManager treeManager;
	private StructuredViewer viewer;
	private ADTable table;
	private MOAlternateSelectPage parentPage;
	private Object input;
	private boolean canEdit;
	
	protected Section section;
	protected IFormPart spart;
	protected IManagedForm form;
	protected ToolItem alterItem;
	protected ToolItem revertItem;
	protected ToolItem revertAllItem;
	protected ToolItem deleteItem;
	protected ToolItem editItem;
	protected ToolItem prepareMoLineItem;
	protected ToolItem closePrepareMoLineItem;
	
	protected ManufactureOrderBom selectedBom;
	protected ManufactureOrderBom selectedBom2;//20130508�ڲ�Ӱ��ԭ��ҵ�����߼��Ļ����ϣ��ñ���Ϊѡ��BOM
	protected List<ManufactureOrderBom> allPerpareMoBoms;
	private Material rootMaterial;
	
	protected ADTable alternateADTable;
	protected WipManager wipManager;
	protected Object parentObj;		//ѡ��Bom�ĸ�һ�����󣬿�����BOM��Material
	protected HashMap<ManufactureOrderBom, List<ManufactureOrderBom>> bomListMap;
	protected List<ManufactureOrderBom> alternateBoms;
	
	private List<ManufactureOrderBom> deleteBoms;
	
	public MOAlternateSelectSection(ADTable adTable, MOAlternateSelectPage parentPage) {
		this.table = adTable;
		this.parentPage = parentPage;
		this.canEdit = ((MOGenerateWizard)parentPage.getWizard()).isCanEdit();
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemPrepareMoLineItem(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemClosePrepareMoLineItem(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemEdit(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemAlter(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRevert(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRevertAll(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		section.setTextClient(tBar);
	}
	
	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.TITLE_BAR);
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		
		section = toolkit.createSection(parent, sectionStyle);
		section.setText(Message.getString("pdm.bom_list_detail_info"));
		section.marginWidth = 0;
	    section.marginHeight = 0;
	    toolkit.createCompositeSeparator(section);

	    createToolBar(section);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    
	    spart = new SectionPart(section);    
	    form.addPart(spart);
	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));  
		
	    treeManager = new MOAlternateManager(table);
	    viewer = treeManager.createViewer(client, toolkit);
	    section.setClient(client);
	    createViewAction(viewer);
	}
	
	protected void createViewAction(StructuredViewer viewer){
		 viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			 public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionMoBom(ss.getFirstElement());
				} catch (Exception e){
					logger.error("Error MOSection : createViewAction() " + e);
				}
			}
		});
	}
	
//	protected void setSelectionMoBom(Object obj) {
//		if(obj instanceof ManufactureOrderBom) {
//			this.selectedBom = (ManufactureOrderBom)obj;
//			TreeItem item = ((TreeViewer)viewer).getTree().getSelection()[0];
//			TreeItem parent = item.getParentItem();
//			// ���selectedBomΪ�������ϣ��򲻿����
//			if(parent == null) {
//				selectedBom = null;
//				return;
//			}
//			while(true) {
//				if(parent != null && parent.getParentItem() == null) {
//					ManufactureOrderBom root = (ManufactureOrderBom)parent.getData();
//					this.rootMaterial = root.getMaterial();
//					break;
//				}
//				if(parent != null) parent = parent.getParentItem();
//				else break;
//			}
//		}
//	}
	
	protected void setSelectionMoBom(Object obj) {
		if(obj instanceof ManufactureOrderBom) {
			this.selectedBom = (ManufactureOrderBom)obj;
			this.selectedBom2 = (ManufactureOrderBom)obj;
			TreeItem item = ((TreeViewer)viewer).getTree().getSelection()[0];
			TreeItem parent = item.getParentItem();
			// ���selectedBomΪ�������ϣ��򲻿����
			if(parent == null) {
				selectedBom = null;
				parentObj = null;
				return;
			}
			parentObj = parent.getData();
			while(true) {
				if(parent != null && parent.getParentItem() == null) {
					ManufactureOrderBom root = (ManufactureOrderBom)parent.getData();
					this.rootMaterial = root.getMaterial();
					break;
				}
				if(parent != null) parent = parent.getParentItem();
				else break;
			}
		}
	}
	
	protected void createToolItemAlter(ToolBar tBar) {
		alterItem = new ToolItem(tBar, SWT.PUSH);
		alterItem.setText(Message.getString("wip.alternate"));
		alterItem.setImage(SWTResourceCache.getImage("alternate"));
		alterItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				alterAdapter();
			}
		});
		// �����ɱ༭(��������Drafted״̬����), ��ť������
		if(!canEdit) {
			alterItem.setEnabled(false);
		}
	}
	
	public void refresh(){
		bomListMap = new LinkedHashMap<ManufactureOrderBom, List<ManufactureOrderBom>>();
		alternateBoms = new ArrayList<ManufactureOrderBom>();
		viewer.setInput(input);
		((TreeViewer)viewer).expandAll();
	}
	
	
	protected void createToolItemPrepareMoLineItem(ToolBar tBar) {
		String catagory = ((MOGenerateWizard)parentPage.getWizard()).getContext().getCategory();
		if(catagory!=null && !catagory.equals("prepareGenerateMO") && !catagory.equals("prepareEditMo")){
			return;
		}
		prepareMoLineItem = new ToolItem(tBar, SWT.PUSH);
//		prepareMoLineItem.setText(Message.getString("ppm.revert"));
		prepareMoLineItem.setText("���ô�����");
		prepareMoLineItem.setImage(SWTResourceCache.getImage("optional"));
		prepareMoLineItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				prepareAdapter();
			}
		});
	}
	
	protected void createToolItemClosePrepareMoLineItem(ToolBar tBar) {
		String catagory = ((MOGenerateWizard)parentPage.getWizard()).getContext().getCategory();
		if(catagory!=null && !catagory.equals("prepareGenerateMO") && !catagory.equals("prepareEditMo")){
			return;
		}
		closePrepareMoLineItem = new ToolItem(tBar, SWT.PUSH);
		closePrepareMoLineItem.setText("ȡ��������");
		closePrepareMoLineItem.setImage(SWTResourceCache.getImage("optional"));
		closePrepareMoLineItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				closePrepareAdapter();
			}
		});
	}

	protected void createToolItemRevert(ToolBar tBar) {
		revertItem = new ToolItem(tBar, SWT.PUSH);
		revertItem.setText(Message.getString("ppm.revert"));
		revertItem.setImage(SWTResourceCache.getImage("optional"));
		revertItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				revertAdapter();
			}
		});
	}
	
	protected void createToolItemRevertAll(ToolBar tBar) {
		revertAllItem = new ToolItem(tBar, SWT.PUSH);
		revertAllItem.setText(Message.getString("ppm.revert_all"));
		revertAllItem.setImage(SWTResourceCache.getImage("optional"));
		revertAllItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				revertAllAdapter();
			}
		});
	}
	
	protected void createToolItemDelete(ToolBar tBar) {
		deleteItem = new ToolItem(tBar, SWT.PUSH);
		deleteItem.setText(Message.getString("common.delete"));
		deleteItem.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		deleteItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	protected void createToolItemEdit(ToolBar tBar) {
		editItem = new ToolItem(tBar, SWT.PUSH);
		editItem.setText(Message.getString("common.new"));
		editItem.setImage(SWTResourceCache.getImage("new"));
		editItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}	
	
	protected void alterAdapter() {
		if(selectedBom != null && rootMaterial != null) {
			// ��Bom���࣬������ѡ����,��ʵ��ѡ��
			Long rootMaterialRrn = rootMaterial.getObjectRrn();
			if(rootMaterial.getBomRrn() != null) {
				rootMaterialRrn = rootMaterial.getBomRrn();
			}
			try {
				ManufactureOrderBom cloneBom = (ManufactureOrderBom)selectedBom.clone();
				MOAlternateDialog dialog = new MOAlternateDialog(UI.getActiveShell(), rootMaterial,
						selectedBom, rootMaterialRrn, initAdTableOfAlternate(), MOAlternateDialog.MoType.NEW);
				if(dialog.open() == Dialog.OK) {
					ManufactureOrderBom alterBom = dialog.getUpdateBom();
					List<ManufactureOrderBom> boms = this.getMOBoms();
					if(boms.contains(selectedBom)) {
						int index = boms.indexOf(selectedBom);
						if(wipManager == null)
							wipManager = Framework.getService(WipManager.class);
						//�������ϵ�����BOM�ṹ
						List<ManufactureOrderBom> childBoms = wipManager.getMoBom(alterBom.getOrgRrn(), alterBom.getMaterialRrn());
						//ɾ��������ϼ���������BOM
						boms.remove(selectedBom);
						List<ManufactureOrderBom> delBoms = getAllChildren(boms, cloneBom, new ArrayList<ManufactureOrderBom>());
						if(delBoms != null) {
							if(bomListMap != null)
								bomListMap.put(cloneBom, delBoms);
							alternateBoms.add(cloneBom);
						}
						boms.removeAll(delBoms);
						//������ϼӵ�������ϵ�λ��
						boms.add(index, alterBom);
						//��boms�м�������ϵ���BOM
						boms.addAll(getRemoveParentMoBoms(childBoms, alterBom));
						MOBomItemAdapter.setMoBoms(boms);
						viewer.refresh(parentObj);
//						viewer.refresh(alterBom);
						((TreeViewer)viewer).expandAll();
					}
//				viewer.refresh(dialog.getUpdateBom());
//				((TreeViewer)viewer).expandAll();
				}
			} catch(Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				logger.error("Error at MOAlternateSelectSection : alterAdapter() ", e);
			}
		}
	}
	
	public List<ManufactureOrderBom> getRemoveParentMoBoms(List<ManufactureOrderBom> childBoms, ManufactureOrderBom moBom) {
		List<ManufactureOrderBom> boms = new ArrayList<ManufactureOrderBom>();
		BigDecimal parentQtyBom = moBom.getUnitQty();
		if(childBoms != null && childBoms.size() > 0) {
			for(ManufactureOrderBom currentMoBom : childBoms) {
				if(currentMoBom.getMaterialParentRrn() == null && moBom.getMaterialRrn().equals(currentMoBom.getMaterialRrn())) {
					childBoms.remove(moBom);
//					return childBoms;
				} else {
					currentMoBom.setQtyBom(currentMoBom.getQtyBom().multiply(parentQtyBom));
					currentMoBom.setQtyNeed(BigDecimal.ZERO);
					if(currentMoBom.getPath() != null) {
						currentMoBom.setPath(moBom.getPath() + currentMoBom.getPath());						
					}
					currentMoBom.setPathLevel(moBom.getPathLevel() + currentMoBom.getPathLevel());
					
					currentMoBom.setCreatedBy(Env.getUserRrn());
					currentMoBom.setUpdatedBy(Env.getUserRrn());
					Date now = Env.getSysDate();
					currentMoBom.setCreated(now);
					currentMoBom.setMoRrn(moBom.getMoRrn());
					currentMoBom.setQty(BigDecimal.ZERO);
					boms.add(currentMoBom);
				}
			}
		}
		return boms;
	}
	
	protected List<ManufactureOrderBom> getAllChildren(
			List<ManufactureOrderBom> boms, ManufactureOrderBom parentBom, List<ManufactureOrderBom> childBoms) {
		if(boms != null) {
			long parentRrn = parentBom.getMaterialRrn();
			long childLevel = parentBom.getPathLevel() + 1;
			
			for (ManufactureOrderBom childBom : boms) {
				if (childBom.getMaterialParentRrn() != null
						&& childBom.getMaterialParentRrn() == parentRrn
						&& childBom.getPath().equals((parentBom.getPath() != null ? parentBom.getPath() : "") + parentRrn + "/")
						&& childBom.getPathLevel() == childLevel) {
					childBoms.add(childBom);
					childBoms = getAllChildren(boms, childBom, childBoms);
				}
			}
		}
		return childBoms;
	}
	
	protected ADTable initAdTableOfAlternate() {
		try {
			if(alternateADTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				alternateADTable = entityManager.getADTable(0L, TABLE_NAME);
			}
		} catch(Exception e) {
			logger.error("MOAlternateSelectSection : initAdTableOfAlternate()", e);
		}
		return alternateADTable;
	}
	
	protected void revertAdapter() {
		try {
			if (selectedBom != null && rootMaterial != null) {
				Long rootMaterialRrn = rootMaterial.getObjectRrn();
				if(rootMaterial.getBomRrn() != null) {
					rootMaterialRrn = rootMaterial.getBomRrn();
				}
				ManufactureOrderBom cloneBom = (ManufactureOrderBom)selectedBom.clone();
				// ��������alterRrn���丸���ϵ�parentRrn,
				// ��parentRrnΪ�ձ�ʾΪ��Bom,���ܻ�ԭ,ֱ�ӷ���
				Long parentRrn = selectedBom.getMaterialParentRrn();
				Long alterRrn = selectedBom.getMaterialRrn();
				String path = selectedBom.getPath();
				if(parentRrn == null) return;
				
				// �ӿ�����ϱ����ҵ�������ΪparentRrn,�����ΪalterRrn����PathΪpath������
				String whereClause = " materialRrn = " + rootMaterialRrn
					+ " AND alternateRrn = " + alterRrn
					+ " AND path = '" + path + "' ";
				ADManager adManager = Framework.getService(ADManager.class);
				List<MaterialAlternate> list = adManager.getEntityList(Env.getOrgRrn(),
						MaterialAlternate.class, 1, whereClause, null);
				// ���List����ֵ,��ʾ�����ϲ��ǿ������,��ʾ���軹ԭ������
				if(list == null || list.size() == 0) {
					UI.showError(String.format(Message.getString("ppm.is_not_alter_material"), selectedBom.getMaterialId()));
					return;
				}
				Long sourceMaterialRrn = list.get(0).getChildRrn();
				// ����mpsLine.materialRrn�õ������ϵ�BomDetails, Ȼ���BomDetails����·��Ϊ
				// path,������ΪparentRrn,����ΪsourceMaterialRrn��BomDetail,��תΪMoBom
				PDMManager pdmManager = Framework.getService(PDMManager.class);
				List<BomDetail> children = pdmManager.getActualLastBomDetails(this.parentPage.getManufactureOrder().getMaterialRrn());
				List<ManufactureOrderBom> boms = this.getMOBoms();
				boolean falg = false;
				for(BomDetail bd : children) {
					if(bd.getPath().equals(path) && bd.getParentRrn().equals(parentRrn)
							&& bd.getChildRrn().equals(sourceMaterialRrn)) {
						//��materialRrn,unitQty,description����Ϊԭ����ֵ
						selectedBom.setMaterialRrn(bd.getChildRrn());
						selectedBom.setUnitQty(bd.getUnitQty());
						selectedBom.setDescription(bd.getDescription());
						selectedBom.setMaterial(bd.getChildMaterial());
						falg = true;
						break;
					}
				}
				if(falg) {
					if(boms.contains(selectedBom)) {
						int index = boms.indexOf(selectedBom);
						boms.remove(selectedBom);
						boms.add(index, selectedBom);
						boms.removeAll(this.getAllChildren(boms, cloneBom, new ArrayList<ManufactureOrderBom>()));
						ManufactureOrderBom preBom = getPreBom(selectedBom);
						if(bomListMap != null && bomListMap.get(preBom) != null) {
							boms.addAll(bomListMap.get(preBom));
							bomListMap.remove(selectedBom);
						}
						MOBomItemAdapter.setMoBoms(boms);
						viewer.refresh(parentObj);
						((TreeViewer)viewer).expandAll();
//						this.setInput(boms);
//						this.refresh();
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at BomTreeSection : revertAdapter() ", e);
		}
	}
	
	protected ManufactureOrderBom getPreBom(ManufactureOrderBom alterBom) {
		if(alternateBoms != null) {
			for(ManufactureOrderBom bom : this.alternateBoms) {
				if(bom.getMaterialRrn().equals(alterBom.getMaterialRrn())) {
					if(bom.getPath() != null && alterBom.getPath() != null && bom.getPath().equals(alterBom.getPath())) {
						return bom;
					}
				}
			}
		}
		return alterBom;
	}
	
	protected void revertAllAdapter() {
		try {
			if (rootMaterial != null) {
				boolean isRevertAll = UI.showConfirm(Message
						.getString("common.confirm_revert_all"));
				if (isRevertAll) {
					WipManager wipManager = Framework.getService(WipManager.class);
					List<ManufactureOrderBom> boms = wipManager.getMoBom(Env.getOrgRrn(),
							parentPage.getManufactureOrder().getMaterialRrn());
					if(boms == null || boms.size() == 0) {
						parentPage.setPageComplete(false);
					}
					MOBomItemAdapter.setMoBoms(boms);
					this.setInput(boms);
					this.refresh();					
				}
			}
		} catch (Exception e) {
			logger.error("Error at BomTreeSection : revertAllAdapter() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void deleteAdapter() {
		try {
			//20130509�����޸�ֻ��1�����ϲ���ɾ��
			if (selectedBom != null && rootMaterial != null && selectedBom.getPathLevel().equals(1L)) {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					// ��boms��ɾ��selectedBom������children, Ȼ��δɾ����preListˢ��
					List<ManufactureOrderBom> preList = this.getMOBoms();
					deleteBoms= new ArrayList<ManufactureOrderBom>();
					getChildMoBoms(selectedBom);
					//�ݹ�õ��ӽڵ�
	
					/*1.��ҳ���뵽��һҳ�󣬽������˻ظ�ҳ��������ҳ����޸�List���ݣ�
					 * ������������equals���ȣ����ǻ���List����˲�����remove(Object)����
					 * */
					for(ManufactureOrderBom mlBom : deleteBoms) {
						for(int i=0;i<preList.size();i++){
							if(mlBom.getPath().equals(preList.get(i).getPath())
									&&mlBom.getMaterial().getMaterialId().endsWith(preList.get(i).getMaterial().getMaterialId())
									){
								preList.remove(i);
								continue;
							}
						}
					}
					this.setInput(preList);
					MOBomItemAdapter.setMoBoms(preList);
					viewer.refresh(parentObj);
					this.refresh();
					this.selectedBom2=null;
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at BomTreeSection : deleteAdapter() ", e);
		}
	}
	
	//�÷�������һЩ��bad code��Ϊ�ڲ�Ӱ��ԭҵ���߼�(�磺��Ӱ�������).
	protected void editAdapter() {
		try {
		//20130509�����޸ģ�ֻ�����1��BOM��ԭ���Ĵ���ʽ��֧�����ɾ���༶BOM
		TreeItem item = ((TreeViewer)viewer).getTree().getItem(0);
		this.selectedBom2 = (ManufactureOrderBom)item.getData();
		//�����ϱ༭�Ի���
		PDMManager	pdmManager = Framework.getService(PDMManager.class);
		Bom newBom = pdmManager.newBom(selectedBom2.getMaterial());
		ADManager entityManager = Framework.getService(ADManager.class);
		ADTable table = entityManager.getADTable(0L, "PDMBom");
		MOEditBomDialog bed = new MOEditBomDialog(UI.getActiveShell(), table, newBom, this, selectedBom2,true);
		bed.open("New");
	} catch (Exception e) {
		ExceptionHandlerManager.asyncHandleException(e);
	}
		
		//���ϲ�Ϊ�ɹ��������ܵ����Ի���
//		if(selectedBom2 != null && parentObj !=null&& rootMaterial != null && !selectedBom2.getMaterial().getIsPurchase()) {
//			try {
//				//���ѡ����tree�ڵ㣬��ô��ӵ�ʱ����������ڸýڵ�֮��
//				PDMManager	pdmManager = Framework.getService(PDMManager.class);
//				Bom newBom = pdmManager.newBom(selectedBom2.getMaterial());
//				ADManager entityManager = Framework.getService(ADManager.class);
//				ADTable table = entityManager.getADTable(0L, "PDMBom");
//				MOEditBomDialog bed = new MOEditBomDialog(UI.getActiveShell(), table, newBom, this, selectedBom2,false);
//				bed.open("New");
//			} catch (Exception e) {
//				ExceptionHandlerManager.asyncHandleException(e);
//			}
//		}else if(selectedBom2!=null && parentObj ==null && !selectedBom2.getMaterial().getIsPurchase()){
//			try {
//				//�����ϱ༭�Ի���
//				PDMManager	pdmManager = Framework.getService(PDMManager.class);
//				Bom newBom = pdmManager.newBom(selectedBom2.getMaterial());
//				ADManager entityManager = Framework.getService(ADManager.class);
//				ADTable table = entityManager.getADTable(0L, "PDMBom");
//				MOEditBomDialog bed = new MOEditBomDialog(UI.getActiveShell(), table, newBom, this, selectedBom2,true);
//				bed.open("New");
//			} catch (Exception e) {
//				ExceptionHandlerManager.asyncHandleException(e);
//			}
//		}
	}	
	
	public List<ManufactureOrderBom> getMOBoms() {
		return (List<ManufactureOrderBom>)viewer.getInput();
	}

	public void setInput(Object input) {
		this.input = input;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public StructuredViewer getViewer() {
		return viewer;
	}

	public void setViewer(StructuredViewer viewer) {
		this.viewer = viewer;
	}
	
	//�ݹ�õ���������
	public void getChildMoBoms(ManufactureOrderBom currentBom){
		deleteBoms.add(currentBom);
		List<ManufactureOrderBom> childBoms = MOBomItemAdapter.getChildMoBom(currentBom);
		if(childBoms!=null && childBoms.size()>0){
			for(ManufactureOrderBom childBom :childBoms){
				getChildMoBoms(childBom);
			}
		}
	}
	
	
	protected void prepareAdapter() {
		try {
			TreeItem[] selectItems = ((TreeViewer)viewer).getTree().getSelection();
			if(selectItems==null || selectItems.length ==0){
				UI.showError("��ѡ��һ�м�¼");
				return;
			}
			TreeItem selectItem = ((TreeViewer)viewer).getTree().getSelection()[0];
			ManufactureOrderBom selectBom = (ManufactureOrderBom) selectItem.getData();
			ManufactureOrder mo = ((MOGenerateWizard)parentPage.getWizard()).getContext().getManufactureOrder();
			int selectPathLevel =Integer.parseInt(selectBom.getPathLevel().toString());
			List<TreeItem> parentsAndChilds = new ArrayList<TreeItem>();//ѡ�нڵ���Ҫ���ô���������и��׽ڵ㣬���ӽڵ�,�����Լ�
			TreeItem tempTreeItem = selectItem;
			for(int k = 0 ;k < selectPathLevel-1 ;k++){
				if(tempTreeItem.getParentItem()==null){
					continue;
				}
				parentsAndChilds.add(tempTreeItem.getParentItem());//������и��׽ڵ�,���������ڵ�
				tempTreeItem = tempTreeItem.getParentItem();
			}
			List<TreeItem> childs = new ArrayList<TreeItem>();
			getAllPerpareMoBom(selectItem,childs);//��������ӽڵ�(�����Լ�)
			parentsAndChilds.addAll(childs);
			
			if(mo.getObjectRrn()==null){
				List<TreeItem> treeItems = new ArrayList<TreeItem>();
//				getAllPerpareMoBom(getSelectTreeItemFirstTreeItem(selectItem),  treeItems);//����treeItemsΪѡ���������������ϰ���ѡ������
				List<ManufactureOrderBom> boms = this.getMOBoms();//MO�µ�����BOM����
				for(TreeItem treeItem : parentsAndChilds){
					ManufactureOrderBom moBom = (ManufactureOrderBom) treeItem.getData();
					moBom.setIsPrepareMoLine(true);//���ý������ԽṹTreeItem�����ʾY
					for(ManufactureOrderBom bom : boms){
						String path  = bom.getPath()!=null ?bom.getPath():"";//BOM��·��
						Long materialRrn = bom.getMaterialRrn()!=null?bom.getMaterialRrn() : -1L;//BOM������RRN
						Long pathLevel = bom.getPathLevel()!=null?bom.getPathLevel() : -1L;//BOM�����·��
						if(path.equals(moBom.getPath())
								&& materialRrn.equals(moBom.getMaterialRrn()) 
								&& pathLevel.equals(moBom.getPathLevel())
//								&& moBom.getMaterialParentRrn().equals(selectBom.getMaterialRrn())
								){
							bom.setIsPrepareMoLine(true);//�ҵ�TreeItem��Ӧ������,����ΪY(��Ϊ������ʾ��ʵ���õ�ֵ����ͬһ������Դ)
							break;
						}
					}
				}
				//�����Ͻ�������ΪԤ����
				TreeItem rootItem = ((TreeViewer)viewer).getTree().getItem(0);
				ManufactureOrderBom rootBom = ((ManufactureOrderBom)rootItem.getData());
				rootBom.setIsPrepareMoLine(true);
				mo.setIsPrepareMo(true);
				//����������Դ����ΪԤ����
				for(ManufactureOrderBom bom : boms){
					String path  = bom.getPath()!=null ?bom.getPath():"";
					Long materialRrn = bom.getMaterialRrn()!=null?bom.getMaterialRrn() : -1L;
					Long pathLevel = bom.getPathLevel()!=null?bom.getPathLevel() : -1L;
					if(path.equals(rootBom.getPath())
							&& materialRrn.equals(rootBom.getMaterialRrn()) 
							&& pathLevel.equals(rootBom.getPathLevel())){
						bom.setIsPrepareMoLine(true);
						break;
					}
				}
				((TreeViewer)viewer).getInput();
			}else if(mo.getObjectRrn()!=null && mo.getIsPrepareMo()){
				List<TreeItem> treeItems = new ArrayList<TreeItem>();
				getAllPerpareMoBom(getSelectTreeItemFirstTreeItem(selectItem),  treeItems);
				List<ManufactureOrderBom> boms = this.getMOBoms();
				for(TreeItem treeItem : treeItems){
					ManufactureOrderBom moBom = (ManufactureOrderBom) treeItem.getData();
					if(!moBom.getAgainGenMoLine()){
						UI.showError("�����ϲ������ô�������Ϊ�Ѿ����ɹ�����");
						return;
					}
					moBom.setIsPrepareMoLine(true);
					for(ManufactureOrderBom bom : boms){
						String path  = bom.getPath()!=null ?bom.getPath():"";
						Long materialRrn = bom.getMaterialRrn()!=null?bom.getMaterialRrn() : -1L;
						Long pathLevel = bom.getPathLevel()!=null?bom.getPathLevel() : -1L;
						if(path.equals(moBom.getPath())&& materialRrn.equals(moBom.getMaterialRrn()) && pathLevel.equals(moBom.getPathLevel())){
							bom.setIsPrepareMoLine(true);
							break;
						}
					}
				}
				TreeItem rootItem = ((TreeViewer)viewer).getTree().getItem(0);
				ManufactureOrderBom rootBom = ((ManufactureOrderBom)rootItem.getData());
				rootBom.setIsPrepareMoLine(true);
				mo.setIsPrepareMo(true);
				
				for(ManufactureOrderBom bom : boms){
					String path  = bom.getPath()!=null ?bom.getPath():"";
					Long materialRrn = bom.getMaterialRrn()!=null?bom.getMaterialRrn() : -1L;
					Long pathLevel = bom.getPathLevel()!=null?bom.getPathLevel() : -1L;
					if(path.equals(rootBom.getPath())&& materialRrn.equals(rootBom.getMaterialRrn()) && pathLevel.equals(rootBom.getPathLevel())){
						bom.setIsPrepareMoLine(true);
						break;
					}
				}
				((TreeViewer)viewer).getInput();
			}

			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at BomTreeSection : deleteAdapter() ", e);
		}
	}
	
	protected void closePrepareAdapter() {
		try {
			String catagory = ((MOGenerateWizard)parentPage.getWizard()).getContext().getCategory();
			if(catagory!=null && catagory.equals("prepareGenerateMO")){
				UI.showError("�½�Ԥ���������������ȡ��������,������ȡ�����������ؿ�������");
				return;
			}
			TreeItem[] selectItems = ((TreeViewer)viewer).getTree().getSelection();
			if(selectItems==null || selectItems.length ==0){
				UI.showError("��ѡ��һ�м�¼");
				return;
			}
			TreeItem selectItem = ((TreeViewer)viewer).getTree().getSelection()[0];
			if(selectItem.getData()!=null){
				ManufactureOrderBom selectBom = (ManufactureOrderBom) selectItem.getData();
				if(selectBom.getPathLevel() == 0L){
					UI.showError("��Ʒ����ȡ��������,��ȡ����Ʒ����Ĵ���������,��Ʒ���Զ�ȡ��������");
					return;
				}else if(!selectBom.getIsPrepareMoLine()){
					UI.showError("ѡ�����ϲ�Ϊ����������,��ѡ�������ʹ�øù���");
					return;
				}
			}

//			List<TreeItem> treeItems = new ArrayList<TreeItem>();
//			getAllPerpareMoBom(getSelectTreeItemFirstTreeItem(selectItem),  treeItems);
			List<ManufactureOrderBom> updateBoms = new ArrayList<ManufactureOrderBom>();//���ݿ����ڸ��µ�BOM
			
			//ѡ�нڵ�ĸ��ڵ�����ж�������򸸽ڵ㲻ȡ��������,����ȡ��������.
			//ѡ�нڵ�����е��ӽڵ�ȡ��������
			TreeItem tempTreeItem = selectItem;
			ManufactureOrderBom selectBom = (ManufactureOrderBom) selectItem.getData();
			int selectPathLevel =Integer.parseInt(selectBom.getPathLevel().toString());
			List<TreeItem> parentsAndChilds = new ArrayList<TreeItem>();//ѡ�нڵ���Ҫȡ������������и��׽ڵ㣬���ӽڵ�,�����Լ�
			for(int k = 0 ;k < selectPathLevel-1 ;k++){
				if(tempTreeItem.getParentItem()==null){
//					continue;
					break;
				}
				//����������ϲ�Ϊ�գ����Ҹ����������ȫ��ȡ��������
				int parentPrepareCount =0;//0�����׽ڵ�������������ӽڵ�ĸ���,ֻ��һ���ӽڵ�
				for(TreeItem treeItem : tempTreeItem.getParentItem().getItems()){
					ManufactureOrderBom moBom = (ManufactureOrderBom) treeItem.getData();
					String path  = moBom.getPath()!=null ?moBom.getPath():"";
					Long materialRrn = moBom.getMaterialRrn()!=null?moBom.getMaterialRrn() : -1L;
					Long pathLevel = moBom.getPathLevel()!=null?moBom.getPathLevel() : -1L;
					if(path.equals(selectBom.getPath())&& materialRrn.equals(selectBom.getMaterialRrn())
							&& pathLevel.equals(selectBom.getPathLevel())){
						continue;
					}
					if(moBom.getIsPrepareMoLine()){
						++parentPrepareCount;
					}
				}
				if(parentPrepareCount >0){
					break;
				}else{
					ManufactureOrderBom moBom = (ManufactureOrderBom) tempTreeItem.getParentItem().getData();
					moBom.setIsPrepareMoLine(false);
					moBom.setAgainGenMoLine(true);
					parentsAndChilds.add(tempTreeItem.getParentItem());
					tempTreeItem = tempTreeItem.getParentItem();//������и��׽ڵ�,���������ڵ�
				}
			}
			List<TreeItem> childs = new ArrayList<TreeItem>();
			getAllPerpareMoBom2(selectItem,childs);//��������ӽڵ�(�����Լ�)
			parentsAndChilds.addAll(childs);
			
//			if(1==1) return;
			
			List<ManufactureOrderBom> boms = this.getMOBoms();
			for(TreeItem treeItem : parentsAndChilds){
				ManufactureOrderBom moBom = (ManufactureOrderBom) treeItem.getData();
				moBom.setIsPrepareMoLine(false);
				moBom.setAgainGenMoLine(true);
				updateBoms.add(moBom);
				for(ManufactureOrderBom bom : boms){
					//
					String path  = bom.getPath()!=null ?bom.getPath():"";
					Long materialRrn = bom.getMaterialRrn()!=null?bom.getMaterialRrn() : -1L;
					Long pathLevel = bom.getPathLevel()!=null?bom.getPathLevel() : -1L;
					if(path.equals(moBom.getPath())&& materialRrn.equals(moBom.getMaterialRrn()) && pathLevel.equals(moBom.getPathLevel())){
						bom.setAgainGenMoLine(true);
						bom.setIsPrepareMoLine(false);
						break;
					}
				}
			}
			
			List<ManufactureOrderBom> treeBoms = (List<ManufactureOrderBom>) ((TreeViewer)viewer).getInput();
			boolean isPerpareMo = false;
			for(ManufactureOrderBom moBom : treeBoms){
				if(moBom.getPathLevel()== 0L){
					continue; 
				}
				if(moBom.getIsPrepareMoLine()){
					isPerpareMo = true;
					break;
				}
			}
			
			// ���������ϵ�BOM�ṹ
			ManufactureOrder mo = ((MOGenerateWizard)parentPage.getWizard()).getContext().getManufactureOrder();
			if(!isPerpareMo){
				TreeItem rootItem = ((TreeViewer)viewer).getTree().getItem(0);
				ManufactureOrderBom rootBom = ((ManufactureOrderBom)rootItem.getData());
				rootBom.setAgainGenMoLine(true);
				rootBom.setIsPrepareMoLine(false);
				updateBoms.add(rootBom);
//				ManufactureOrder mo = ((MOGenerateWizard)parentPage.getWizard()).getContext().getManufactureOrder();
//				mo.setIsPrepareMo(null);
				for(ManufactureOrderBom bom : boms){
					String path  = bom.getPath()!=null ?bom.getPath():"";
					Long materialRrn = bom.getMaterialRrn()!=null?bom.getMaterialRrn() : -1L;
					Long pathLevel = bom.getPathLevel()!=null?bom.getPathLevel() : -1L;
					if(path.equals(rootBom.getPath())&& materialRrn.equals(rootBom.getMaterialRrn()) && pathLevel.equals(rootBom.getPathLevel())){
						bom.setAgainGenMoLine(true);
						bom.setIsPrepareMoLine(false);
						break;
					}
				}
			}
			
			WipManager wipManager = Framework.getService(WipManager.class);
			wipManager.updateMoBomCana(Env.getOrgRrn(),updateBoms,mo);
			
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at BomTreeSection : deleteAdapter() ", e);
		}
	}
	
	/**
	 * ��һ��BOM(0��ʼ)�µ���������
	 * */
	protected void getAllPerpareMoBom(ManufactureOrderBom firstLevelMoBom){
		allPerpareMoBoms.add(firstLevelMoBom);
		List<ManufactureOrderBom> childBoms = MOBomItemAdapter.getChildMoBom(firstLevelMoBom);
		if(childBoms!=null && childBoms.size()>0){
			for(ManufactureOrderBom childBom :childBoms){
				getAllPerpareMoBom(childBom);
			}
		}
	}
	/**
	 * ��һ��BOM(0��ʼ)�µ���������
	 * firstTreeItem��Ϊ��һ��BOM�µ���������
	 * */
	protected void getAllPerpareMoBom(TreeItem firstTreeItem,List<TreeItem> treeItems){
		treeItems.add(firstTreeItem);
		TreeItem[] childItems = firstTreeItem.getItems();
//		List<TreeItem> childItems = firstTreeItem.getItems();
		for(TreeItem treeItem : childItems){
			getAllPerpareMoBom(treeItem,treeItems);
		}
	}
	
	protected void getAllPerpareMoBom2(TreeItem firstTreeItem,List<TreeItem> treeItems){
		treeItems.add(firstTreeItem);
		TreeItem[] childItems = firstTreeItem.getItems();
//		List<TreeItem> childItems = firstTreeItem.getItems();
		for(TreeItem treeItem : childItems){
			ManufactureOrderBom orderBom = (ManufactureOrderBom) treeItem.getData();
			if(orderBom.getIsPrepareMoLine()){
				getAllPerpareMoBom(treeItem,treeItems);
			}
		}
	}
	
	
	
	
	
//	/**
//	 * ��һ��BOM(0��ʼ)�µ���������
//	 * */
//	protected void getAllPerpareMoBom(ManufactureOrderBom firstLevelMoBom){
//		allPerpareMoBoms.add(firstLevelMoBom);
//		List<ManufactureOrderBom> childBoms = MOBomItemAdapter.getChildMoBom(firstLevelMoBom);
//		
//		List<ManufactureOrderBom> allPerpareBoms = new ArrayList<ManufactureOrderBom>();
//		if(childBoms!=null && childBoms.size()>0){
//			for(ManufactureOrderBom childBom :childBoms){
//				getAllPerpareBomChildBoms(childBom,allPerpareBoms);
////				getChildMoBoms(childBom);
//			}
//		}
//	}
	//�õ�ѡ���������ڵĵ�һ�����ϣ�Ҳ���Ǹ������µ�����
	protected TreeItem getSelectTreeItemFirstTreeItem(TreeItem selectTreeItem){
		TreeItem parentItem = selectTreeItem.getParentItem();
		if(parentItem!=null){
			ManufactureOrderBom parentData = (ManufactureOrderBom) parentItem.getData();
			if(parentData.getPathLevel().equals(0L)){
				return selectTreeItem;
			}else{
				return getSelectTreeItemFirstTreeItem(parentItem);
			}
		}else{
			return null;
		}
	}
	
	protected ManufactureOrderBom getSelectBomFirstLevelBom(TreeItem selectTreeItem){
		ManufactureOrderBom selectBom = (ManufactureOrderBom) selectTreeItem.getData();
		if(selectBom.getPathLevel() != 1L){
			return getSelectBomFirstLevelBom(selectTreeItem.getParentItem());
		}
		return selectBom;
	}
	
	//�ݹ�õ���������
	public void getAllPerpareBomChildBoms(ManufactureOrderBom currentBom,List<ManufactureOrderBom> allPerpareBoms){
		allPerpareBoms.add(currentBom);
		List<ManufactureOrderBom> childBoms = MOBomItemAdapter.getChildMoBom(currentBom);
		if(childBoms!=null && childBoms.size()>0){
			for(ManufactureOrderBom childBom :childBoms){
				getChildMoBoms(childBom);
			}
		}
	}
	 
	
	public void getParentsBom(TreeItem treeItem,List<TreeItem> parentBoms){
		parentBoms.add(treeItem);
		TreeItem parentItem = treeItem.getParentItem();
//		List<TreeItem> childItems = firstTreeItem.getItems();
		if(parentItem!=null){
			getParentsBom(treeItem,parentBoms);
		}
//		for(TreeItem treeItem : childItems){
//			
//		}
	}
}

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
	protected ManufactureOrderBom selectedBom2;//20130508在不影响原来业务无逻辑的基础上，该变量为选择BOM
	protected List<ManufactureOrderBom> allPerpareMoBoms;
	private Material rootMaterial;
	
	protected ADTable alternateADTable;
	protected WipManager wipManager;
	protected Object parentObj;		//选中Bom的父一级对象，可能是BOM或Material
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
//			// 如果selectedBom为父根物料，则不可替代
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
			// 如果selectedBom为父根物料，则不可替代
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
		// 若不可编辑(即不是以Drafted状态进入), 则按钮不可用
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
		prepareMoLineItem.setText("设置待处理");
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
		closePrepareMoLineItem.setText("取消待处理");
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
			// 有Bom大类，可设置选择料,即实际选料
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
						//获得替代料的所有BOM结构
						List<ManufactureOrderBom> childBoms = wipManager.getMoBom(alterBom.getOrgRrn(), alterBom.getMaterialRrn());
						//删除被替代料及其所有子BOM
						boms.remove(selectedBom);
						List<ManufactureOrderBom> delBoms = getAllChildren(boms, cloneBom, new ArrayList<ManufactureOrderBom>());
						if(delBoms != null) {
							if(bomListMap != null)
								bomListMap.put(cloneBom, delBoms);
							alternateBoms.add(cloneBom);
						}
						boms.removeAll(delBoms);
						//将替代料加到被替代料的位置
						boms.add(index, alterBom);
						//在boms中加上替代料的子BOM
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
				// 获得替代料alterRrn和其父物料的parentRrn,
				// 若parentRrn为空表示为根Bom,不能还原,直接返回
				Long parentRrn = selectedBom.getMaterialParentRrn();
				Long alterRrn = selectedBom.getMaterialRrn();
				String path = selectedBom.getPath();
				if(parentRrn == null) return;
				
				// 从可替代料表中找到父物料为parentRrn,替代料为alterRrn并且Path为path的物料
				String whereClause = " materialRrn = " + rootMaterialRrn
					+ " AND alternateRrn = " + alterRrn
					+ " AND path = '" + path + "' ";
				ADManager adManager = Framework.getService(ADManager.class);
				List<MaterialAlternate> list = adManager.getEntityList(Env.getOrgRrn(),
						MaterialAlternate.class, 1, whereClause, null);
				// 如果List中无值,表示该物料不是可替代料,提示无需还原并返回
				if(list == null || list.size() == 0) {
					UI.showError(String.format(Message.getString("ppm.is_not_alter_material"), selectedBom.getMaterialId()));
					return;
				}
				Long sourceMaterialRrn = list.get(0).getChildRrn();
				// 根据mpsLine.materialRrn得到根物料的BomDetails, 然后从BomDetails查找路径为
				// path,父物料为parentRrn,物料为sourceMaterialRrn的BomDetail,并转为MoBom
				PDMManager pdmManager = Framework.getService(PDMManager.class);
				List<BomDetail> children = pdmManager.getActualLastBomDetails(this.parentPage.getManufactureOrder().getMaterialRrn());
				List<ManufactureOrderBom> boms = this.getMOBoms();
				boolean falg = false;
				for(BomDetail bd : children) {
					if(bd.getPath().equals(path) && bd.getParentRrn().equals(parentRrn)
							&& bd.getChildRrn().equals(sourceMaterialRrn)) {
						//将materialRrn,unitQty,description等设为原来的值
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
			//20130509需求修改只有1级物料才能删除
			if (selectedBom != null && rootMaterial != null && selectedBom.getPathLevel().equals(1L)) {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					// 从boms中删除selectedBom及其子children, 然后将未删除的preList刷新
					List<ManufactureOrderBom> preList = this.getMOBoms();
					deleteBoms= new ArrayList<ManufactureOrderBom>();
					getChildMoBoms(selectedBom);
					//递归得到子节点
	
					/*1.该页进入到下一页后，紧接着退回该页（由于下页会对修改List数据）
					 * 导致树的内容equals不等，但是还是List，因此不能用remove(Object)方法
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
	
	//该方法存在一些，bad code因为在不影响原业务逻辑(如：不影响替代料).
	protected void editAdapter() {
		try {
		//20130509需求修改，只能添加1级BOM，原来的处理方式是支持添加删除多级BOM
		TreeItem item = ((TreeViewer)viewer).getTree().getItem(0);
		this.selectedBom2 = (ManufactureOrderBom)item.getData();
		//根物料编辑对话框
		PDMManager	pdmManager = Framework.getService(PDMManager.class);
		Bom newBom = pdmManager.newBom(selectedBom2.getMaterial());
		ADManager entityManager = Framework.getService(ADManager.class);
		ADTable table = entityManager.getADTable(0L, "PDMBom");
		MOEditBomDialog bed = new MOEditBomDialog(UI.getActiveShell(), table, newBom, this, selectedBom2,true);
		bed.open("New");
	} catch (Exception e) {
		ExceptionHandlerManager.asyncHandleException(e);
	}
		
		//物料不为采购件，才能弹出对话框
//		if(selectedBom2 != null && parentObj !=null&& rootMaterial != null && !selectedBom2.getMaterial().getIsPurchase()) {
//			try {
//				//如果选择了tree节点，那么添加的时候将内容添加在该节点之后
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
//				//根物料编辑对话框
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
	
	//递归得到所有物料
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
				UI.showError("请选中一行记录");
				return;
			}
			TreeItem selectItem = ((TreeViewer)viewer).getTree().getSelection()[0];
			ManufactureOrderBom selectBom = (ManufactureOrderBom) selectItem.getData();
			ManufactureOrder mo = ((MOGenerateWizard)parentPage.getWizard()).getContext().getManufactureOrder();
			int selectPathLevel =Integer.parseInt(selectBom.getPathLevel().toString());
			List<TreeItem> parentsAndChilds = new ArrayList<TreeItem>();//选中节点需要设置待处理的所有父亲节点，和子节点,包括自己
			TreeItem tempTreeItem = selectItem;
			for(int k = 0 ;k < selectPathLevel-1 ;k++){
				if(tempTreeItem.getParentItem()==null){
					continue;
				}
				parentsAndChilds.add(tempTreeItem.getParentItem());//添加所有父亲节点,不包括根节点
				tempTreeItem = tempTreeItem.getParentItem();
			}
			List<TreeItem> childs = new ArrayList<TreeItem>();
			getAllPerpareMoBom(selectItem,childs);//添加所有子节点(包括自己)
			parentsAndChilds.addAll(childs);
			
			if(mo.getObjectRrn()==null){
				List<TreeItem> treeItems = new ArrayList<TreeItem>();
//				getAllPerpareMoBom(getSelectTreeItemFirstTreeItem(selectItem),  treeItems);//设置treeItems为选中物料下所有物料包括选中物料
				List<ManufactureOrderBom> boms = this.getMOBoms();//MO下的所有BOM物料
				for(TreeItem treeItem : parentsAndChilds){
					ManufactureOrderBom moBom = (ManufactureOrderBom) treeItem.getData();
					moBom.setIsPrepareMoLine(true);//设置界面属性结构TreeItem组件显示Y
					for(ManufactureOrderBom bom : boms){
						String path  = bom.getPath()!=null ?bom.getPath():"";//BOM的路径
						Long materialRrn = bom.getMaterialRrn()!=null?bom.getMaterialRrn() : -1L;//BOM的物料RRN
						Long pathLevel = bom.getPathLevel()!=null?bom.getPathLevel() : -1L;//BOM的相对路径
						if(path.equals(moBom.getPath())
								&& materialRrn.equals(moBom.getMaterialRrn()) 
								&& pathLevel.equals(moBom.getPathLevel())
//								&& moBom.getMaterialParentRrn().equals(selectBom.getMaterialRrn())
								){
							bom.setIsPrepareMoLine(true);//找到TreeItem对应的物料,设置为Y(因为界面显示和实际拿的值不是同一个数据源)
							break;
						}
					}
				}
				//根物料界面设置为预处理
				TreeItem rootItem = ((TreeViewer)viewer).getTree().getItem(0);
				ManufactureOrderBom rootBom = ((ManufactureOrderBom)rootItem.getData());
				rootBom.setIsPrepareMoLine(true);
				mo.setIsPrepareMo(true);
				//根物料数据源设置为预处理
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
						UI.showError("该物料不能设置待处理，因为已经生成工作令");
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
				UI.showError("新建预处理工作令不允许将设置取消待处理,如设置取消待处理请重开工作令");
				return;
			}
			TreeItem[] selectItems = ((TreeViewer)viewer).getTree().getSelection();
			if(selectItems==null || selectItems.length ==0){
				UI.showError("请选中一行记录");
				return;
			}
			TreeItem selectItem = ((TreeViewer)viewer).getTree().getSelection()[0];
			if(selectItem.getData()!=null){
				ManufactureOrderBom selectBom = (ManufactureOrderBom) selectItem.getData();
				if(selectBom.getPathLevel() == 0L){
					UI.showError("商品不能取消待处理,请取消商品下面的待处理物料,商品将自动取消待处理");
					return;
				}else if(!selectBom.getIsPrepareMoLine()){
					UI.showError("选中物料不为待处理物料,请选择待物料使用该功能");
					return;
				}
			}

//			List<TreeItem> treeItems = new ArrayList<TreeItem>();
//			getAllPerpareMoBom(getSelectTreeItemFirstTreeItem(selectItem),  treeItems);
			List<ManufactureOrderBom> updateBoms = new ArrayList<ManufactureOrderBom>();//数据库用于更新的BOM
			
			//选中节点的父节点如果有多个物料则父节点不取消待处理,否则父取消待处理.
			//选中节点的所有的子节点取消待处理
			TreeItem tempTreeItem = selectItem;
			ManufactureOrderBom selectBom = (ManufactureOrderBom) selectItem.getData();
			int selectPathLevel =Integer.parseInt(selectBom.getPathLevel().toString());
			List<TreeItem> parentsAndChilds = new ArrayList<TreeItem>();//选中节点需要取消待处理的所有父亲节点，和子节点,包括自己
			for(int k = 0 ;k < selectPathLevel-1 ;k++){
				if(tempTreeItem.getParentItem()==null){
//					continue;
					break;
				}
				//如果父类物料不为空，并且父类的子物料全部取消待处理
				int parentPrepareCount =0;//0代表父亲节点除本身外其他子节点的个数,只跨一级子节点
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
					tempTreeItem = tempTreeItem.getParentItem();//添加所有父亲节点,不包括根节点
				}
			}
			List<TreeItem> childs = new ArrayList<TreeItem>();
			getAllPerpareMoBom2(selectItem,childs);//添加所有子节点(包括自己)
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
			
			// 更新主物料的BOM结构
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
	 * 第一级BOM(0开始)下的所有物料
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
	 * 第一级BOM(0开始)下的所有物料
	 * firstTreeItem做为第一及BOM下的所有物料
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
//	 * 第一级BOM(0开始)下的所有物料
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
	//得到选中物料所在的第一级物料，也就是根物料下的物料
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
	
	//递归得到所有物料
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

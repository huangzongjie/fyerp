package com.graly.erp.wip.mo.create;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.materiallocate.MaterialLocateCompent;
import com.graly.erp.base.materiallocate.MaterialLocateManager;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.BomDetail;
import com.graly.erp.pdm.model.MaterialAlternate;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.workcenter.referencedoc.ReferceDocDialog;
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

public class MOBomListSection implements MaterialLocateManager {
	private static final Logger logger = Logger.getLogger(MOBomListSection.class);
	private static final String TABLE_NAME = "PDMAlternate";
	private MOBomTreeManager treeManager;	
	private MOBomListPage parentPage;
	private Object input;
	private boolean canEdit;
	
	protected StructuredViewer viewer;
	protected Section section;
	protected IFormPart spart;
	protected ToolItem alterItem;
	protected ToolItem revertItem;
	protected ToolItem refreshItem;
	protected ToolItem itemViewBom;
	protected ToolItem itemReferenceDoc;
	protected ADTable adTableDoc;
	
	private Material refDocMaterial;
	private Material rootMaterial;
	protected ManufactureOrderBom selectedBom;
	protected ADTable alternateADTable;
	protected Object parentObj;		//选中Bom的父一级对象，可能是BOM或Material
	PDMManager pdmManager;
	ADManager adManager;
	WipManager wipManager;
	
	List<ManufactureOrderBom> queryBoms;
	Map<ManufactureOrderBom, TreeItem> tiMap;
	
	public MOBomListSection(MOBomTreeManager treeManager, MOBomListPage parentPage) {
		this.treeManager = treeManager;
		this.parentPage = parentPage;
		this.canEdit = ((MOGenerateWizard)parentPage.getWizard()).isCanEdit();
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		if(parentPage.getIsCanSetAlternateMaterial()) {
			createToolItemAlter(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
			createToolItemRevert(tBar);
			new ToolItem(tBar, SWT.SEPARATOR);
		}
		createToolItemReferenceDoc(tBar);
		createToolItemRefresh(tBar);
//		createToolItemViewBom(tBar);
		section.setTextClient(tBar);
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
	
	protected void createToolItemViewBom(ToolBar bar) {
		itemViewBom = new ToolItem(bar, SWT.PUSH);
		itemViewBom.setText(Message.getString("common.view_bom"));
		itemViewBom.setImage(SWTResourceCache.getImage("preview"));
		itemViewBom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				viewBomAdapter();
			}
		});
	}
	
	protected void createToolItemReferenceDoc(ToolBar tBar) {
		itemReferenceDoc = new ToolItem(tBar, SWT.PUSH);
		itemReferenceDoc.setText(Message.getString("bas.refence_doc"));
		itemReferenceDoc.setImage(SWTResourceCache.getImage("search"));
		itemReferenceDoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refreenceDocAdapter();
			}
		});
	}
	
	protected void refreenceDocAdapter() {
		adTableDoc = getRefenceDocTable();
		ReferceDocDialog referceDocDialog = new ReferceDocDialog(UI.getActiveShell(), adTableDoc, refDocMaterial);
		if(referceDocDialog.open() == Dialog.OK){
			
		}
	}

	protected void viewBomAdapter() {
		try {
			String report = "bom_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			List<ManufactureOrderBom> boms = getMOBoms();
			Long moRrn = ((ManufactureOrderBom)boms.get(0)).getMoRrn();
			userParams.put("MO_RRN", String.valueOf(moRrn));
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	
	}

	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.TITLE_BAR);
		createSectionDesc(section);
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		final FormToolkit toolkit = form.getToolkit();
		final ADTable table = treeManager.getADTable();
		
		section = toolkit.createSection(parent, sectionStyle);
		section.setText(I18nUtil.getI18nMessage(table, "label"));
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);

	    createToolBar(section);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    
	    spart = new SectionPart(section);    
	    form.addPart(spart);
	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));  
		
	    new MaterialLocateCompent(this).createMaterialLocateComposite(client, toolkit);
	    viewer = treeManager.createViewer(client, toolkit);
	    section.setClient(client);
	    createViewAction(viewer);
	}
	
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionMOBom(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void setSelectionMOBom(Object obj) {
		if(obj instanceof ManufactureOrderBom) {
			this.selectedBom = (ManufactureOrderBom)obj;
			refDocMaterial = ((ManufactureOrderBom)obj).getMaterial();
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
	
	protected void createToolItemRefresh(ToolBar tBar) {
		refreshItem = new ToolItem(tBar, SWT.PUSH);
		refreshItem.setText(Message.getString("common.refresh"));
		refreshItem.setImage(SWTResourceCache.getImage("refresh"));
		refreshItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refreshAdapter();
			}
		});
	}
	
	public void refresh(){
		viewer.setInput(input);
		((TreeViewer)viewer).expandAll();
	}
	
	protected void refreshAdapter() {
		refresh();
	}
	
	protected void alterAdapter() {
		try {
			if(selectedBom != null && rootMaterial != null) {
				// 有Bom大类，可设置选择料,即实际选料
				Long rootMaterialRrn = rootMaterial.getObjectRrn();
				if(rootMaterial.getBomRrn() != null) {
					rootMaterialRrn = rootMaterial.getBomRrn();
				}
				MOAlternateDialog dialog = new MOAlternateDialog(UI.getActiveShell(), rootMaterial,
						selectedBom, rootMaterialRrn, initAdTableOfAlternate(), MOAlternateDialog.MoType.EDIT);
				ManufactureOrderBom alterBom = dialog.getUpdateBom();
				List<ManufactureOrderBom> boms = this.getMOBoms();
				if(dialog.open() == Dialog.OK) {
					if(boms.contains(selectedBom)) {
						int index = boms.indexOf(selectedBom);
						if(wipManager == null)
							wipManager = Framework.getService(WipManager.class);
						alterBom = wipManager.alternateMoBom(parentPage.getManufactureOrder(),
								selectedBom, dialog.getUpdateBom(), Env.getUserRrn());
						boms.remove(selectedBom);
						boms.removeAll(MOBomItemAdapter.getChildMoBom(selectedBom));
						boms.add(index, alterBom);
						List<ManufactureOrderBom> mobs = wipManager.getMoBomAllChildBom(alterBom.getObjectRrn());
						if(mobs != null) {
							mobs.remove(alterBom);
							boms.addAll(mobs);
						}
						MOBomItemAdapter.setMoBoms(boms);
						viewer.refresh(parentObj);
						//viewer.refresh(dialog.getUpdateBom());
						((TreeViewer)viewer).expandAll();
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MOBomListSection : revertAdapter() ", e);
		}
	}
	
	protected ADTable initAdTableOfAlternate() {
		try {
			if(alternateADTable == null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				alternateADTable = adManager.getADTable(0L, TABLE_NAME);
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
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				List<MaterialAlternate> list = adManager.getEntityList(Env.getOrgRrn(),
						MaterialAlternate.class, 1, whereClause, null);
				// 如果List中无值,表示该物料不是可替代料,提示无需还原并返回
				if(list == null || list.size() == 0) {
					UI.showError(String.format(Message.getString("ppm.is_not_alter_material"), selectedBom.getMaterialId()));
					return;
				}
				Long sourceMaterialRrn = list.get(0).getChildRrn();
				// 根据mpsLine.materialRrn得到根物料的BomDetails, 然后从BomDetails查找路径为
				// path,父物料为parentRrn,物料为sourceMaterialRrn的BomDetail,并转为MpsLineBom
				if(pdmManager == null)
					pdmManager = Framework.getService(PDMManager.class);
				List<BomDetail> children = pdmManager.getActualLastBomDetails(parentPage.getManufactureOrder().getMaterialRrn());
				List<ManufactureOrderBom> boms = this.getMOBoms();
				boolean falg = false;
				ManufactureOrderBom alterBom = null;
				for(BomDetail bd : children) {
					if(bd.getPath().equals(path) && bd.getParentRrn().equals(parentRrn)
							&& bd.getChildRrn().equals(sourceMaterialRrn)) {
						//将materialRrn,unitQty,description等设为原来的值
						alterBom = new ManufactureOrderBom();
						alterBom.setMaterialRrn(bd.getChildRrn());
						alterBom.setUnitQty(bd.getUnitQty());
						alterBom.setDescription(bd.getDescription());
						alterBom.setMaterial(bd.getChildMaterial());
						falg = true;
						break;
					}
				}
				if(falg && alterBom != null) {
					if(boms.contains(selectedBom)) {
						int index = boms.indexOf(selectedBom);
						if(wipManager == null)
							wipManager = Framework.getService(WipManager.class);
						alterBom = wipManager.alternateMoBom(parentPage.getManufactureOrder(),
								selectedBom, alterBom, Env.getUserRrn());
						boms.remove(selectedBom);
						boms.removeAll(MOBomItemAdapter.getChildMoBom(selectedBom));
						boms.add(index, alterBom);
						List<ManufactureOrderBom> mobs = wipManager.getMoBomAllChildBom(alterBom.getObjectRrn());
						if(mobs != null) {
							mobs.remove(alterBom);
							boms.addAll(mobs);
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
			logger.error("Error at MOBomListSection : revertAdapter() ", e);
		}
	}
	
	private ADTable getRefenceDocTable() {
		try {
			if(adManager == null)
				adManager = Framework.getService(ADManager.class);
			adTableDoc = adManager.getADTable(0L, "BASMaterialDoc");
			adTableDoc = adManager.getADTableDeep(adTableDoc.getObjectRrn());
			return adTableDoc;				
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	protected void createSectionDesc(Section section){
		try{ 
		} catch (Exception e){
			logger.error("EntityBlock : createSectionDesc ", e);
		}
	}
	
	public List<ManufactureOrderBom> getMOBoms() {
		return (List<ManufactureOrderBom>)viewer.getInput();
	}

	public void setInput(Object input) {
		this.input = input;
	}

	public MOBomListPage getParentPage() {
		return parentPage;
	}

	public void setParentPage(MOBomListPage parentPage) {
		this.parentPage = parentPage;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	@Override
	public void locateLast(String materialId, int index) {
		if(materialId == null || "".equals(materialId.trim()))
			return;
		if(queryBoms == null || queryBoms.size() == 0 || index < 0 || index > queryBoms.size() - 1)
			return;
		TreeItem ti = tiMap.get(queryBoms.get(index));
		((TreeViewer)viewer).getTree().setSelection(ti);
		ti.setBackground(SWTResourceCache.getColor("Folder"));
	}

	@Override
	public boolean locateMaterial(String materialId) {
		if(queryBoms != null && queryBoms.size() > 0) {
			clearPreLocateMaterials();
		}
		if(materialId == null || "".equals(materialId.trim()))
			return false;
		queryBoms = new ArrayList<ManufactureOrderBom>();
		tiMap = new HashMap<ManufactureOrderBom, TreeItem>();
		getChildrenData(null, materialId);
		if(queryBoms.size() > 0)
			return true;
		return false;
	}
	
	protected void clearPreLocateMaterials() {
		for(ManufactureOrderBom moBom : queryBoms) {
			if(tiMap.get(moBom) != null) {
				tiMap.get(moBom).setBackground(null);
			}
		}
		queryBoms.clear();
	}
	
	protected void getChildrenData(TreeItem parent, String materialId) {
		TreeItem[] its = null;
		if(parent == null) its = ((TreeViewer)viewer).getTree().getItems();
		else its = parent.getItems();
		if(its == null || its.length == 0)
			return;
		
		for(TreeItem ti : its) {
			if(ti.getData() instanceof ManufactureOrderBom) {
				ManufactureOrderBom moBom = (ManufactureOrderBom)ti.getData();
				if(moBom.getMaterialId() != null && moBom.getMaterialId().indexOf(materialId) != -1) {
					queryBoms.add(moBom);
					tiMap.put(moBom, ti);
					ti.setBackground(SWTResourceCache.getColor("Folder"));
				}
			}
			getChildrenData(ti, materialId);
		}
	}

	@Override
	public void locateNext(String materialId, int index) {
		if(materialId == null || "".equals(materialId.trim()))
			return;
		if(queryBoms == null || queryBoms.size() == 0 || index < 0 || index > queryBoms.size() - 1)
			return;
		TreeItem ti = tiMap.get(queryBoms.get(index));
		((TreeViewer)viewer).getTree().setSelection(ti);
		ti.setBackground(SWTResourceCache.getColor("Folder"));
	}
}

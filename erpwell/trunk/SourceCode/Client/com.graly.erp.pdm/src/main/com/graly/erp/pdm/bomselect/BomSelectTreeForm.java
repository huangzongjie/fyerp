package com.graly.erp.pdm.bomselect;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.bomedit.BomTreeDialog;
import com.graly.erp.pdm.bomedit.BomTreeForm;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.MaterialActual;
import com.graly.erp.pdm.model.MaterialUnSelected;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.ui.util.Env;

public class BomSelectTreeForm extends BomTreeForm {
	protected CheckboxTreeViewer checkViewer;
	protected BomSelectTreeManager selectTreeManager;
	private UnSelectBomManager unSelectBomManager;
	ADManager adManager;

	public BomSelectTreeForm(Composite parent, int style, ADBase adBase, IMessageManager mmng, BomTreeDialog btd) {
		super(parent, style, adBase, mmng, btd);
	}

	public BomSelectTreeForm(Composite parent, int style, Object object) {
		super(parent, style, object);
	}

	@Override
	public void addFields() {
		field = new SelectBomTreeField(FIELD_ID);
		addField(FIELD_ID, field);
	}

	// 调用此方法时默认将BomSelectTreeManager作为UnSelectBomManager
	@Override
	public void refresh(Material material) {
		isExpendAll = false;
		setObject(material);
		field.refresh();
		setUnCheckForUnSelectBom();
	}
	
	// 设置Bom大类时会调用此方法, 调用此方法时UnSelectBomManager由应用程序赋入
	public void refreshByOtherUnSelectBomManager(Material material) {
		super.refresh(material);
		setUnCheckForUnSelectBom();
	}
	
	protected void setUnCheckForUnSelectBom() {
		checkViewer.setAllChecked(true);
		if(unSelectBomManager != null && unSelectBomManager.getUnSelectBoms() != null) {
			for(Bom unSelect : unSelectBomManager.getUnSelectBoms()) {
				checkViewer.setChecked(unSelect, false);
			}
		}
	}

	@Override
	public void refreshAll() {
		isExpendAll = true;
		viewer.refresh();
	}

	@Override
	public void refreshEditorBom(Bom editorBom) {
		isExpendAll = false;
		checkViewer.refresh(editorBom);
		checkViewer.setChecked(editorBom, true);
	}
	
	public Material getRootMaterial() {
		return ((List<Material>)checkViewer.getInput()).get(0);
	}
	
	public List<MaterialActual> getActualMaterials(HashMap<Bom, MaterialActual> bomMap) throws Exception {
		if(bomMap == null) return null;
		List<MaterialActual> actualMaterials = new ArrayList<MaterialActual>();		
		MaterialActual ma = null;
		Bom tempBom = null;
		Date now = Env.getSysDate();
		for(Object obj : checkViewer.getCheckedElements()) {
			if(obj instanceof Bom) {
				tempBom = ((Bom)obj);
				if(bomMap.get((Bom)obj) != null) {
					actualMaterials.add(bomMap.get(tempBom));
				}
				// 表示该选择料已经保存到DB中，此时重新创建MaterialActual，因为在保存时会先删除原先的MaterialActual
				else if(Bom.CATEGORY_OPTIONAL.equals(tempBom.getCategory())) {
					ma = new MaterialActual();
					ma.setIsActive(true);
					ma.setOrgRrn(tempBom.getOrgRrn());
					ma.setCreated(now);
					ma.setCreatedBy(Env.getUserRrn());
					ma.setUpdated(now);
					ma.setUpdatedBy(Env.getUserRrn());
					//tempBom中的parentRrn和childRrn都是实际料中的(在调用后台方法getActualChildrenBoms时赋的值)
					ma.setMaterialRrn(tempBom.getParentRrn());
					ma.setActualRrn(tempBom.getChildRrn());
					ma.setChildRrn(tempBom.getBomTypeChildRrn());
					ma.setDescription(tempBom.getDescription());
					ma.setUnitQty(tempBom.getUnitQty());
					actualMaterials.add(ma);
				}
			}
		}
		return actualMaterials;
	}
	
//	private MaterialActual getChildRrn(Long actualMaterialRrn, Long actualRrn) throws Exception {
//		if(adManager == null)
//			adManager = Framework.getService(ADManager.class);
//		List<MaterialActual> mas = adManager.getEntityList(Env.getOrgRrn(), MaterialActual.class, 1,
//				" materialRrn = " + actualMaterialRrn + " AND actualRrn = " + actualRrn, null);
//		if(mas != null && mas.size() > 0)
//			return mas.get(0);
//		return null;
//	}
	
	public List<MaterialUnSelected> getUnSelectedList(List<MaterialUnSelected> uns, TreeItem parent) {
		if(uns == null)
			return null;
		TreeItem[] its = null;
		if(parent == null)
			its = checkViewer.getTree().getItems();
		else
			its = parent.getItems();
		if(its == null || its.length == 0)
			return uns;
		
		MaterialUnSelected mun = null;
		Bom temp = null;
		for(TreeItem it : its) {
			if(it.getData() instanceof Bom) {
				if(!checkViewer.getChecked(it.getData())) {
					temp = (Bom)it.getData();
					mun = new MaterialUnSelected();
					mun.setIsActive(true);
					mun.setOrgRrn(temp.getOrgRrn());
					if(parent == null) {
//						mun.setMaterialRrn(getRootMaterial().getObjectRrn());						
					} else {
						if(parent.getData() instanceof Material) {
							mun.setMaterialRrn(((Material)parent.getData()).getObjectRrn());
						} else {
							mun.setMaterialRrn(((Bom)parent.getData()).getParentRrn());							
						}
					}
					mun.setUnSelectedRrn(temp.getChildRrn());
					uns.add(mun);
				}
			}
			getUnSelectedList(uns, it);
		}
		return uns;
	}

	protected class SelectBomTreeField extends MaterialField {

		public SelectBomTreeField(String id) {
			super(id);
		}

		@Override
		protected void createTreeContent(Composite composite) {
			selectTreeManager = new BomSelectTreeManager(SWT.NULL | SWT.CHECK | SWT.LINE_DASHDOTDOT | SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL,
						new BomExpendAll());
			treeManager = selectTreeManager;
			checkViewer = (CheckboxTreeViewer)treeManager.createViewer(composite,
	        		toolkit);
	        viewer = checkViewer;
	        checkViewer.addDoubleClickListener(getDoubleClickListener());
		}

		protected IDoubleClickListener getDoubleClickListener() {
			return new IDoubleClickListener() {
		    	public void doubleClick(DoubleClickEvent event) {
		    		((BomSelectEditTreeDialog)btd).optionalAdapter(null);
		    	}
		    };
		}

		@Override
		public void refresh() {
			setUnSelectBomManager((BomSelectTreeManager)treeManager);
			((BomSelectTreeManager)treeManager).clearUnSelectBomContent();
			treeManager.setInput(getInput());
			viewer.expandToLevel(2);
			setUnCheckForUnSelectBom();
		}
		
		protected void selectAdapter(SelectionEvent e) {
			super.selectAdapter(e);
			if(selectedItem != null && selectedItem.getData() instanceof Material) {
				if(!checkViewer.getChecked(selectedItem.getData())){
					checkViewer.setChecked(selectedItem.getData(), true);
				}
			}
		}
	}
	

	public UnSelectBomManager getUnSelectBomManager() {
		return unSelectBomManager;
	}

	public void setUnSelectBomManager(UnSelectBomManager unSelectBomManager) {
		this.unSelectBomManager = unSelectBomManager;
	}
	
	public void setSelectedBomType(Material selectedMateiral) {
		selectTreeManager.setSelectedBomType(selectedMateiral);
	}
	
	public Material getSelectedBomType() {
		return selectTreeManager.getSelectedBomType();
	}
}

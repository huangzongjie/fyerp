package com.graly.erp.pdm.bomselect;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.bomedit.BomConstant;
import com.graly.erp.pdm.bomedit.MaterialItemAdapter;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.MaterialUnSelected;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;

public class BomSelectMaterialItemAdapter extends MaterialItemAdapter {
	private static final Logger logger = Logger.getLogger(BomSelectMaterialItemAdapter.class);
	private List<Bom> unSelectBoms;
	private Material bomTypeMaterial;

	public BomSelectMaterialItemAdapter() {
		super();
		unSelectBoms = new ArrayList<Bom>();
	}

	@Override
	public Object[] getChildren(Object object) {
		if(object instanceof Material){
			Material mr = (Material)object;
			try {
				PDMManager pdmManager = Framework.getService(PDMManager.class);
				List<Bom> list = null;
				
				//通过bomTypeMaterial获得BOM的情况：
				//1.mr没有BOM， 可是用户已经选择了BOM大类但还没有保存
				//2.mr有BOM，但用户在界面上选择了另一个BOM大类
				if(bomTypeMaterial != null) {
					list = pdmManager.getChildrenBoms(bomTypeMaterial.getObjectRrn(), BigDecimal.ONE);
				} else {
					list = pdmManager.getActualChildrenBoms(mr.getObjectRrn(), mr.getObjectRrn(), false);
					
				}
				BomConstant.addAllFristLevelBoms(list);
				
				if(list != null && list.size() > 0) {
					Long materialRrn = mr.getObjectRrn();
//					if(mr.getBomRrn() != null && mr.getBomRrn().longValue() != 0) {
//						materialRrn = mr.getBomRrn();
//					}
					List<MaterialUnSelected> unSelecteds = pdmManager.getUnSelectMaterialList(Env.getOrgRrn(), materialRrn);
					createUnSelectBoms(list, unSelecteds);
				}
				return list.toArray();
			} catch(Exception e) {
				logger.error("Error At BomSelectMaterialItemAdapter : getChildren() " + e.getMessage());
			}
		}
		return EMPTY;
	}
	
	protected void createUnSelectBoms(List<Bom> allBoms, List<MaterialUnSelected> unSelecteds) {
		if(allBoms == null || unSelecteds == null)
			return;
		for (MaterialUnSelected unSelected : unSelecteds) {
			for (Bom actualBom : allBoms) {
				if (unSelected.getUnSelectedRrn().equals(actualBom.getChildRrn())) {
					unSelectBoms.add(actualBom);
				}
			}
		}
	}

	public List<Bom> getUnSelectBoms() {
		return unSelectBoms;
	}

	public void setUnSelectBoms(List<Bom> unSelectBoms) {
		this.unSelectBoms = unSelectBoms;
	}

	public Material getBomTypeMaterial() {
		return bomTypeMaterial;
	}

	public void setBomTypeMaterial(Material bomTypeMaterial) {
		this.bomTypeMaterial = bomTypeMaterial;
	}
}

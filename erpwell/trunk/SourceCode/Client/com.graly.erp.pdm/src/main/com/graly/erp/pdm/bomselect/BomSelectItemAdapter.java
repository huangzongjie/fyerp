package com.graly.erp.pdm.bomselect;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.graly.erp.pdm.bomedit.BomItemAdapter;
import com.graly.erp.pdm.bomedit.EnableExpendAll;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.MaterialUnSelected;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomSelectItemAdapter extends BomItemAdapter {
	private static final Logger logger = Logger.getLogger(BomSelectItemAdapter.class);
	private List<Bom> unSelectBoms;

	public BomSelectItemAdapter(EnableExpendAll eeall) {
		super(eeall);
		unSelectBoms = new ArrayList<Bom>();
	}

	@Override
	public Object[] getChildren(Object object) {
		if(object instanceof Bom) {
			Bom bom = (Bom)object;
			try {
				PDMManager pdmManager = Framework.getService(PDMManager.class);
				List<Bom> list = pdmManager.getActualChildrenBoms(bom.getChildRrn(), bom.getChildRrn(), false);
				
				if(list != null && list.size() > 0) {
					Long materialRrn = bom.getChildRrn();
//					Material cm = bom.getChildMaterial();
//					if(cm != null && cm.getBomRrn() != null && cm.getBomRrn().longValue() != 0) {
//						materialRrn = cm.getBomRrn();
//					}
					List<MaterialUnSelected> unSelecteds = pdmManager.getUnSelectMaterialList(Env.getOrgRrn(), materialRrn);
					createUnSelectBoms(list, unSelecteds);
				}
				return list.toArray();
			} catch(Exception e) {
				logger.error("Error At MaterialItemAd : getChildren() " + e.getMessage());
				ExceptionHandlerManager.asyncHandleException(e);
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

}

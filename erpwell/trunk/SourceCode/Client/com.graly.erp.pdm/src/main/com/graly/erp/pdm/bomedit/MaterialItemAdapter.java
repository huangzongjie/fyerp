package com.graly.erp.pdm.bomedit;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.base.model.Material;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MaterialItemAdapter extends AbstractItemAdapter {
	private static final Logger logger = Logger.getLogger(MaterialItemAdapter.class);
	protected static final Object[] EMPTY = new Object[0];
	
	@Override
	public Object[] getChildren(Object object) {
		if(object instanceof Material){
			Material mr = (Material)object;
			try {
				PDMManager pdmManager = Framework.getService(PDMManager.class);
//				pdmManager.getLastBomDetails(mr.getObjectRrn());
				List<Bom> list = pdmManager.getChildrenBoms(mr.getObjectRrn(), BigDecimal.ONE);
				BomConstant.addAllFristLevelBoms(list);
				return list.toArray();
			} catch(Exception e) {
				logger.error("Error At MaterialItemAdapter : getChildren() " + e.getMessage());
				ExceptionHandlerManager.asyncHandleException(e);
			}
		}
		return EMPTY;
	}

	@Override
	public Object[] getElements(Object object) {
		if(object != null && object instanceof List){
			return ((List)object).toArray();
		} else if(object instanceof Material) {
			return new Object[]{object};
		}
		return EMPTY;
	}

	@Override
	public boolean hasChildren(Object object) {
		return true;
	}

	@Override
	public String getText(Object object, String id) {
		if(object instanceof Material){
			Material material = (Material)object;
			if(id.equalsIgnoreCase(BomConstant.MATERIAL_ID)){
				String bomType = (material.getBomId() != null ? "(" + material.getBomId() + ")" : "");
				return material.getMaterialId() + bomType;
			} else if(id.equalsIgnoreCase(BomConstant.NAME)){
				return material.getName();
			} else if(id.equalsIgnoreCase(BomConstant.MATERIAL_UOM)){
				return material.getInventoryUom();
			} else if(id.equalsIgnoreCase(BomConstant.UNIT_QTY)){
				return "";
			} else if(id.equalsIgnoreCase(BomConstant.COMMENTS)){
				return material.getComments();
			} else if(id.equalsIgnoreCase(BomConstant.VOLUME)){
				if(material.getIsVolumeBasis()){
					return material.getVolume().toString();
				}else{
					return "";
				}
			}
		}
		return "";
	}

	@Override
	public String getText(Object object) {
		return super.getText(object);
	}
}

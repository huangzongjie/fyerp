package com.graly.erp.pdm.bomhistory;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.bomedit.BomConstant;
import com.graly.erp.pdm.bomedit.BomItemAdapter;
import com.graly.erp.pdm.bomedit.EnableExpendAll;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomHisItemAdapter extends AbstractItemAdapter {
	private static final Logger logger = Logger.getLogger(BomHisItemAdapter.class);
	protected Long version;
	protected static final Object[] EMPTY = new Object[0];

	protected EnableExpendAll eeall;
	
	public BomHisItemAdapter(EnableExpendAll eeall,Long version) {
		this.eeall = eeall;
		this.version=version;
	}
	
	@Override
	public Object[] getElements(Object object) {
		if(object != null && object instanceof List){
			return ((List<?>)object).toArray();
		} else if(object instanceof Bom) {
			return new Object[]{object};
		}
		return EMPTY;
	}
	
	@Override
	public Object[] getChildren(Object object) {
		if(object instanceof Bom) {
			Bom bom = (Bom)object;
			try {
				PDMManager pdmManager = Framework.getService(PDMManager.class);
				List<Bom> list = pdmManager.getChildrenBoms(bom.getChildRrn(),bom.getLockVersion(), bom.getUnitQty());
				return list.toArray();
			} catch(Exception e) {
				logger.error("Error At MaterialItemAd : getChildren() " + e.getMessage());
				ExceptionHandlerManager.asyncHandleException(e);
			}
		}
		return EMPTY;
	}

	@Override
	public boolean hasChildren(Object object) {
		if(object instanceof Bom) {
			Bom bom = (Bom)object;
			try {
				PDMManager pdmManager = Framework.getService(PDMManager.class);
				List<Bom> list = pdmManager.getChildrenBomsFirst(bom.getChildRrn(),bom.getLockVersion(), bom.getUnitQty());
				if(list!=null && list.size()>0){
					return true;
				}
			} catch(Exception e) {
				logger.error("Error At MaterialItemAd : getChildren() " + e.getMessage());
				ExceptionHandlerManager.asyncHandleException(e);
			}
		}
		return false;
	}
	
	@Override
	public String getText(Object object, String id) {
		if(object instanceof Bom){
			Bom bom = (Bom)object;
			if(id.equalsIgnoreCase(BomConstant.MATERIAL_ID)){
				return bom.getChildMaterial().getMaterialId();
			} else if(id.equalsIgnoreCase(BomConstant.NAME)){
				return bom.getChildMaterial().getName();
			} else if(id.equalsIgnoreCase(BomConstant.MATERIAL_UOM)){
				return bom.getChildMaterial().getInventoryUom();
			} else if(id.equalsIgnoreCase(BomConstant.UNIT_QTY)){
				return bom.getUnitQty() == null ? "" : bom.getUnitQty().toString();
			} else if(id.equalsIgnoreCase(BomConstant.COMMENTS)){
				return bom.getDescription();
			} else if(id.equalsIgnoreCase(BomConstant.VOLUME)){
				Material childMaterial = bom.getChildMaterial();
				if(childMaterial.getIsVolumeBasis()){
					BigDecimal volume = childMaterial.getVolume();
					if(volume == null){
						return "";
					}
					return volume.toString();
				}else{
					return "";
				}
			}
		}
		return "";
	}
	
	@Override
	public Color getForeground(Object element, String id) {
		Bom bom = (Bom)element;
		if(Bom.CATEGORY_OPTIONAL.equals(bom.getCategory())) {
			return SWTResourceCache.getColor("Function");
		}
		return null;
	}

	@Override
	public String getText(Object object) {
		return super.getText(object);
	}

}

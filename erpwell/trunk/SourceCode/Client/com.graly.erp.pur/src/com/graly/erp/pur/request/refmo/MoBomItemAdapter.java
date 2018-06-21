package com.graly.erp.pur.request.refmo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;

import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.AbstractItemAdapter;

public class MoBomItemAdapter extends AbstractItemAdapter {
	private static List<ManufactureOrderBom> moBoms;

	@Override
	public Object[] getChildren(Object object) {
		if(object instanceof ManufactureOrderBom) {
			List<ManufactureOrderBom> list = getChildMoBom((ManufactureOrderBom)object);	
			return list.toArray();
		}
		return null;
	}
	
	@Override
	public Object[] getElements(Object object) {
		if(object instanceof List) {
			List<ManufactureOrderBom> moBoms = (List<ManufactureOrderBom>)object;
			List<ManufactureOrderBom> masterBoms = new ArrayList<ManufactureOrderBom>();
			for(ManufactureOrderBom bom : moBoms) {
				if(bom.getMaterialParentRrn() == null) {
					masterBoms.add(bom);
				}
			}
			return masterBoms.toArray();
		}
		return new Object[]{object};
	}
	
	@Override
	public Object getParent(Object object) {
		return null;
	}

	@Override
	public String getText(Object object) {
		return null;
	}

	@Override
	public String getText(Object object, String id) {
		if(object instanceof String) {
    		return (String)object;
    	}
		if (object != null && id != null){
			try{
				Object property = PropertyUtil.getPropertyForString(object, id);
				if (property instanceof java.util.Date) {
					property = I18nUtil.formatDateTime((java.util.Date)property, false);
				}
				return (String)property;
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return "";
	}

	@Override
	public boolean hasChildren(Object object) {
		if(object instanceof ManufactureOrderBom) {
			List<ManufactureOrderBom> list = getChildMoBom((ManufactureOrderBom)object);	
			if(list != null && list.size() >= 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Color getForeground(Object element, String id) {
		if(element instanceof ManufactureOrderBom) {
			ManufactureOrderBom moBom = (ManufactureOrderBom)element;
			if(moBom.getIsDateNeed()) {
				// 没有时间生产, 但仍能用最低库存进行上一级物料的生产(即isCanStart为true)
				if(moBom.getIsCanStart()) {
					return SWTResourceCache.getColor("Run");
				}
				// 否则显示红色
				else {
					return SWTResourceCache.getColor("Red");
				}
			} else if(moBom.getIsMaterialNeed()) {
				return SWTResourceCache.getColor("Function");
			}
		}
		return null;
	}
	
	public static void setMoBoms(List<ManufactureOrderBom> moBoms) {
		MoBomItemAdapter.moBoms = moBoms;
	}
	
	public static List<ManufactureOrderBom> getChildMoBom(ManufactureOrderBom parentBom) {
		if(moBoms != null) {
			List<ManufactureOrderBom> childBoms = new ArrayList<ManufactureOrderBom>();
			long parentMaterialRrn = parentBom.getMaterialRrn();
			long childLevel = parentBom.getPathLevel() + 1;
			
			for (ManufactureOrderBom childBom : moBoms) {
				if (childBom.getMaterialParentRrn() != null
						&& childBom.getMaterialParentRrn() == parentMaterialRrn
						&& childBom.getPathLevel() == childLevel) {
					childBoms.add(childBom);
				}
			}
			return childBoms;
		}
		return null;
	}
}

package com.graly.erp.wip.mo.create;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;

import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.workcenter.receive.TextProvider;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.AbstractItemAdapter;

public class MOBomItemAdapter extends AbstractItemAdapter {
	public static final String FIELD_STAND_TIME = "standTime";
	public static final String FIELD_QTY = "qty";
	public static final String FIELD_QTY_NEED = "qtyNeed";
	public static final String SUFFIX_MINUTE = "分钟";
	public static final String SUFFIX_DAY = "天";
	public static final String SUFFIX_HOUR = "小时";
	public static final BigDecimal MINUTE_60 = new BigDecimal("60");
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
		ManufactureOrderBom moBom = (ManufactureOrderBom)object;
		if(TextProvider.FieldName_StandTime.equals(id)) {
			if(moBom.getStandTime() != null) {
				if(moBom.getIsMaterialNeed()) {
					return moBom.getStandTime() + SUFFIX_DAY;
				} else {
					return moBom.getStandTime() + SUFFIX_MINUTE;
				}
			}
		} else if(TextProvider.FieldName_TotalTime.equals(id)) {
			if(moBom.getIsProduct()) {
				if(moBom.getStandTime() != null && moBom.getQty() != null)
					return String.valueOf(moBom.getStandTime()
							.multiply(moBom.getQty().divide(MINUTE_60, RoundingMode.UP)).doubleValue()) + SUFFIX_HOUR;
				else return "0" + SUFFIX_HOUR;
			}
		} else if(FIELD_QTY.equals(id)) {
			return String.valueOf(((ManufactureOrderBom)object).getQty().doubleValue());
		} else if(FIELD_QTY_NEED.equals(id)) {
			return String.valueOf(((ManufactureOrderBom)object).getQtyNeed().doubleValue());
		}
		else if (object != null && id != null){
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
			if(moBom.getQtyOnHand() != null && moBom.getQtyAllocation() != null) {
				if(moBom.getQtyOnHand().compareTo(moBom.getQtyAllocation()) < 0) {
					return SWTResourceCache.getColor("Alarm");//橙色
				}
			}
		}
		return null;
	}
	
	public static void setMoBoms(List<ManufactureOrderBom> moBoms) {
		MOBomItemAdapter.moBoms = moBoms;
	}
	
	public static List<ManufactureOrderBom> getChildMoBom(ManufactureOrderBom parentBom) {
		if(moBoms != null) {
			List<ManufactureOrderBom> childBoms = new ArrayList<ManufactureOrderBom>();
			long parentRrn = parentBom.getMaterialRrn();
			long childLevel = parentBom.getPathLevel() + 1;
			
			for (ManufactureOrderBom childBom : moBoms) {
				if (childBom.getMaterialParentRrn() != null
						&& childBom.getMaterialParentRrn() == parentRrn
						&& childBom.getPath().equals((parentBom.getPath() != null ? parentBom.getPath() : "") + parentRrn + "/")
						&& childBom.getPathLevel() == childLevel) {
					childBoms.add(childBom);
				}
			}
			return childBoms;
		}
		return null;
	}
}

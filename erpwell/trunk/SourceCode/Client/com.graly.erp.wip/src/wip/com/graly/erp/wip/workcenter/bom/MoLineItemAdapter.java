package com.graly.erp.wip.workcenter.bom;

import java.util.List;

import org.eclipse.swt.graphics.Color;

import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.workcenter.receive.TextProvider;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.AbstractItemAdapter;

public class MoLineItemAdapter extends AbstractItemAdapter {

	public MoLineItemAdapter() {
		super();
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof List) {
			return ((List<?>)inputElement).toArray();			
		}
		return new Object[]{inputElement};
    }
	
	@Override
	public Object[] getChildren(Object object) {
		return null;
	}
	
	@Override
	public boolean hasChildren(Object object) {
		return false;
	}
	
	@Override
	public String getText(Object object, String id) {
		try{
			if (object != null && id != null){
				if(object instanceof ManufactureOrderLine) {
					return getTextBy((ManufactureOrderLine)object, id);
				} else if(object instanceof RequisitionLine) {					
					return getTextBy((RequisitionLine)object, id);					
				} else if(object instanceof ManufactureOrderBom) {
					return getTextBy((ManufactureOrderBom)object, id);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	public Color getForeground(Object element, String id) {
		if(element instanceof ManufactureOrderBom) {
			ManufactureOrderBom moBom = (ManufactureOrderBom)element;
			if(moBom.getIsMaterialNeed() || moBom.getRequsitionLineRrn() != null)
				return SWTResourceCache.getColor("Function");
		}
		return null;
	}
	
	private String getTextBy(ManufactureOrderLine moLine, String id) {
		if(TextProvider.FieldName_NeedQty.equals(id)) {
			return moLine.getQty().toString(); // 需求数量默认为生产数量
		} else {
			return getPropertyText(moLine, id);
		}
	}
	
	private String getTextBy(RequisitionLine prLine, String id) {
		if(TextProvider.FieldName_MaterialId.equals(id)
				|| TextProvider.FieldName_MaterialName.equals(id)
				|| TextProvider.FieldName_ProductQty.equals(id)
				|| TextProvider.FieldName_DateEnd.equals(id)) {
			return getPropertyText(prLine, id);
		}
		if(TextProvider.FieldName_FinishedQty.equals(id)) {
			if(prLine.getQtyInventoty() != null)
				return prLine.getQtyInventoty().toString();
		} else if(TextProvider.FieldName_NeedQty.equals(id)) {
			// 需求数量默认为生产(qty)数量
			if(prLine.getQty() != null)
				return prLine.getQty().toString();
		}
		return "";
	}
	
	private String getTextBy(ManufactureOrderBom moBom, String id) {
		if(TextProvider.FieldName_MaterialId.equals(id)
				|| TextProvider.FieldName_MaterialName.equals(id)				
				|| TextProvider.FieldName_StorageQty.equals(id)
				|| TextProvider.FieldName_TransactionQty.equals(id)
				|| TextProvider.FieldName_LineWipQty.equals(id)
				|| TextProvider.FieldName_AllocationQty.equals(id)
				|| TextProvider.FieldName_DateStart.equals(id)
				|| TextProvider.FieldName_DateEnd.equals(id)) {
			return getPropertyText(moBom, id);
		}
		if(TextProvider.FieldName_ProductQty.equals(id)
				|| TextProvider.FieldName_FinishedQty.equals(id)
				|| TextProvider.FieldName_NeedQty.equals(id)) {
			return moBom.getQtyNeed().toString();
		}
		return "";
	}
	
	private String getPropertyText(Object object, String id) {
		Object property = PropertyUtil.getPropertyForString(object, id);
		return (String)property;
	}

}

package com.graly.erp.wip.seelotinfo;

import java.util.List;

import org.apache.log4j.Logger;

import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotComponent;

public class LotItemAdapter extends AbstractItemAdapter {
	private static final Logger logger = Logger.getLogger(LotItemAdapter.class);
	private static final Object[] EMPTY = new Object[0];

	@Override
	public Object[] getChildren(Object object) {
		if(object instanceof Lot && ((Lot)object).getObjectRrn() != null){
			Lot lot = (Lot)object;
			try {
				WipManager wipManager = Framework.getService(WipManager.class);
				List<LotComponent> list = wipManager.getLotComponent(lot.getObjectRrn());
				return list.toArray();
			} catch(Exception e) {
				logger.error("Error At LotItemAdapter : getChildren() " + e.getMessage());
				ExceptionHandlerManager.asyncHandleException(e);
			}
		}
		return EMPTY;
	}

	@Override
	public Object[] getElements(Object object) {
		if(object != null && object instanceof List){
			return ((List)object).toArray();
		} else if(object instanceof Lot) {
			return new Object[]{object};
		}
		return EMPTY;
	}

	@Override
	public boolean hasChildren(Object object) {
		try {
			if(object instanceof Lot && ((Lot)object).getObjectRrn() != null){
				Lot lot = (Lot)object;
				WipManager wipManager = Framework.getService(WipManager.class);
				List<LotComponent> list = wipManager.getLotComponent(lot.getObjectRrn());
				if(list==null||list.size()==0){
					return false;
				}else{
					return true;
				}
			} 
		}catch(Exception e) {
			logger.error("Error At LotItemAdapter : getChildren() " + e.getMessage());
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return false;
	}

	@Override
	public String getText(Object object, String id) {
		if (object != null && id != null) {
			try {
				Object property = PropertyUtil.getPropertyForString(object, id);
				return (String) property;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}
}

package com.graly.erp.wip.disassemblelot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.views.ListItemAdapter;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotComponent;

public class LotComponentItemAdapter<T extends LotComponent> extends ListItemAdapter {
	private static final Logger logger = Logger.getLogger(LotComponentItemAdapter.class);
	private static final Object[] EMPTY = new Object[0];
	private WipManager wipManager;
	private ADManager adManager;

	private Map<String,String> valMap = new HashMap<String, String>();
	{
		valMap.put("lotId", "lotChildId");
		valMap.put("materialId", "materialChildId");
		valMap.put("materialName", "materialChildName");
		valMap.put("qtyConsume", "qtyConsume");
	}
	
	public LotComponentItemAdapter() {
	}

	public LotComponentItemAdapter(List initialElements) {
		super(initialElements);
	}
	
	@Override
	public String getText(Object object, String id) {
    	if(object instanceof String) {
    		return (String)object;
    	}
		if (object != null && id != null){
			try{
				String newId = valMap.get(id);
				Object property = PropertyUtil.getPropertyForString(object, newId);
				return (String) property;
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return "";
	}
	
	@Override
	public boolean hasChildren(Object object) {
		try {
			if(object instanceof LotComponent && ((LotComponent)object).getObjectRrn() != null){
				LotComponent lc = (LotComponent)object;
				// 判断若为Material类型物料，则停止向下展开
				Material material = new Material();
				material.setObjectRrn(lc.getMaterialChildRrn());
				if(adManager == null)
					adManager = this.getFrameworkService(ADManager.class);
				material = (Material)adManager.getEntity(material);
				if(Lot.LOTTYPE_MATERIAL.equals(material.getLotType()))
					return false;
				
				// 若不是Material类型物料，则继续判断是否有子Component
				if(wipManager == null)
					wipManager = this.getFrameworkService(WipManager.class);
				List<LotComponent> list = wipManager.getLotComponent(lc.getLotChildRrn());
				if(list==null||list.size()==0){
					return false;
				}else{
					return true;
				}
			} 
		}catch(Exception e) {
			logger.error("Error At LotComponentItemAdapter : getChildren() " + e.getMessage());
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return false;
	}

	@Override
	public Object[] getChildren(Object object) {
		if(object instanceof LotComponent && ((LotComponent)object).getObjectRrn() != null){
			LotComponent lc = (LotComponent)object;
			try {
				if(wipManager == null)
					wipManager = this.getFrameworkService(WipManager.class);
				List<LotComponent> list = wipManager.getLotComponent(lc.getLotChildRrn());
				return list.toArray();
			} catch(Exception e) {
				logger.error("Error At LotComponentItemAdapter : getChildren() " + e.getMessage());
				ExceptionHandlerManager.asyncHandleException(e);
			}
		}
		return EMPTY;
	}
	
	protected <G> G getFrameworkService(Class<G> clazz) throws Exception {
		return Framework.getService(clazz);
	}
}

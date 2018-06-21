package com.graly.mes.prd.equipment;

import java.math.BigDecimal;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.prd.client.PrdManager;

public class EquipmentDeleteProperties extends EntityProperties {
	@Override
	protected void deleteAdapter(){	
		ADBase oldBase = getAdObject();
		try {		
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if ( getAdObject().getObjectRrn() != null) {
					PrdManager prdManager = Framework.getService(PrdManager.class);
					BigDecimal workCenter_rrn = prdManager.getWorkCenterByEquipment(new BigDecimal(getAdObject().getObjectRrn()));
					if(workCenter_rrn !=null){
						UI.showError("已被工作中心关联，不能删除"); 
					}
					else{
						ADManager entityManager = Framework.getService(ADManager.class);
						entityManager.deleteEntity(getAdObject());
						setAdObject(createAdObject());
						getMasterParent().refreshDelete(oldBase);
					}					
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
}

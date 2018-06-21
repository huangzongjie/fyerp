package com.graly.mes.prd.mould;


import java.math.BigDecimal;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WIPMould;
import com.graly.mes.prd.client.PrdManager;

public class MouldDeleteProperties extends EntityProperties {
	public static final String MOULD_ID = "mouldId";
	public static final String MAINTENANCE = "maintenance";
	@Override
	protected void deleteAdapter(){
		ADBase oldBase = getAdObject();
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if ( getAdObject().getObjectRrn() != null) {
					PrdManager prdManager = Framework.getService(PrdManager.class);
					BigDecimal eqp_rrn = prdManager.getEquipmentByMould(new BigDecimal(getAdObject().getObjectRrn()));
					if(eqp_rrn !=null){
						UI.showError("已被设备关联，不能删除"); 
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
	
	@Override
	protected void saveAdapter() {
		ADBase oldBase = getAdObject();
		boolean saveFlag = save();
		if (saveFlag) {
			ADBase newBase = getAdObject();
			if (oldBase.getObjectRrn() == null) {
				getMasterParent().refreshAdd(newBase);
			} else {
				getMasterParent().refreshUpdate(newBase);
			}
		}
	}
	
	public boolean save() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					WIPMould mould = null;
					WIPMould oldMould = null;
					String oldMaintenance = "";
					String newMaintenance = "";
					for (Form detailForm : getDetailForms()) {
						if(detailForm.getObject() instanceof WIPMould){
							mould = (WIPMould)detailForm.getObject();
							if(detailForm.getFields().containsKey(MOULD_ID) && getAdObject().getObjectRrn() != null){
								ADManager entityManager = Framework.getService(ADManager.class);
								ADBase adBase = entityManager.getEntity(getAdObject());
								if(adBase instanceof WIPMould){
									 oldMould  = (WIPMould)adBase;
									 oldMaintenance = oldMould.getMaintenance()==null ? "":oldMould.getMaintenance().toString().trim();
								}
							}
							if(detailForm.getFields().containsKey(MAINTENANCE)){
								newMaintenance = detailForm.getFields().get(MAINTENANCE).getValue().toString().trim();
								if(!oldMaintenance.equals(newMaintenance) && !"".equals(newMaintenance)){
									mould.setMaintenanceHis(mould.getMaintenanceHis()+newMaintenance+"\r\n");
									mould.setMaintenance(newMaintenance);
								}
							}
						}
						PropertyUtil.copyProperties(getAdObject(), mould, detailForm.getFields());
					}
					ADManager entityManager = Framework.getService(ADManager.class);
					ADBase obj = entityManager.saveEntity(getTable().getObjectRrn(), getAdObject(), Env.getUserRrn());
					setAdObject(entityManager.getEntity(obj));
					UI.showInfo(Message.getString("common.save_successed"));// 弹出提示框
					refresh();
					return true;
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return false;
	}
}

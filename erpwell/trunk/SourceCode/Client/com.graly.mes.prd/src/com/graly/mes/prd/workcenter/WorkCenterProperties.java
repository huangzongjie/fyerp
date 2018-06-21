package com.graly.mes.prd.workcenter;

import java.util.List;

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


public class WorkCenterProperties extends EntityProperties {
	protected static final String Must_Eqp1 = "mustEqp1";
	protected static final String Equipments = "equipments";
	@Override
	protected void saveAdapter (){
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
	
	@SuppressWarnings("unchecked")
	public boolean save() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				boolean eqpmust = false; //用于标志设备是否必须
				List equipments = null;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						if(detailForm.get(Must_Eqp1) != null)
							eqpmust = (Boolean)detailForm.get(Must_Eqp1);
						Object fValue = detailForm.get(Equipments);
						if(fValue != null){
							if(fValue instanceof List){
								equipments = (List)fValue;
							}
						} 
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					if(eqpmust && equipments !=null && equipments.size()>0 || !eqpmust){
						ADManager entityManager = Framework.getService(ADManager.class);
						ADBase obj = entityManager.saveEntity(getTable().getObjectRrn(), getAdObject(), Env.getUserRrn());
						setAdObject(entityManager.getEntity(obj));
						UI.showInfo(Message.getString("common.save_successed"));// 弹出提示框
						refresh();
						return true;
					}
					else{
						UI.showError("设备必须，请添加设备");// 弹出警示框
						return false;
					}
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return false;
	}
}

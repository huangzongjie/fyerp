package com.graly.erp.vdm.vendoryeartarget;

import java.util.List;

import org.apache.log4j.Logger;

import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.erp.vdm.model.VendorYearTarget;
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

public class VendorYearTargetProperties extends EntityProperties {
	private static final Logger logger = Logger.getLogger(VendorYearTargetProperties.class);

	@Override
	protected void saveAdapter() {
		save();
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
					for (Form detailForm : getDetailForms()) {
                        VendorYearTarget vendorYearTarget = (VendorYearTarget) detailForm.getObject();
                        VDMManager vdmManager = Framework.getService(VDMManager. class);
                        List<VendorYearTarget> vendorYearTargets = vdmManager.getVendorYearTarget( vendorYearTarget.getVendorRrn() , vendorYearTarget.getTargetYear() );       
                         if (vendorYearTargets.size() != 0){
                              UI. showInfo( "供应商已有此年度的采购目标" );
                               return false;
                        }

						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
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
			

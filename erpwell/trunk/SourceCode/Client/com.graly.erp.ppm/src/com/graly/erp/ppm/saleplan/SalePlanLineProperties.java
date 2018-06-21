package com.graly.erp.ppm.saleplan;

import org.eclipse.swt.widgets.Composite;

import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.SalePlanLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class SalePlanLineProperties extends ChildEntityProperties {
	public SalePlanLineProperties() {
		super();
	}

	public SalePlanLineProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		super(masterParent, table, parentObject);
	}

	@Override
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		refreshToolItem(false);
	}

	protected void saveAdapter() {
		boolean saveFlag;
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					PPMManager ppmManager = Framework.getService(PPMManager.class);
					SalePlanLine salePlanLine = ppmManager.saveSalePlanLine(getTable().getObjectRrn(), (SalePlanLine) getAdObject(), Env
							.getUserRrn());

					if (salePlanLine != null) {
						ADManager adManager = Framework.getService(ADManager.class);
						setAdObject(adManager.getEntity(salePlanLine));
						UI.showInfo(Message.getString("common.save_successed"));
						refresh();
					} else {
						UI.showInfo(Message.getString("ppm.dateintervalused"));
					}

					getMasterParent().refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	public void refreshToolItem(boolean enable) {
		itemNew.setEnabled(enable);
		itemDelete.setEnabled(enable);
		itemSave.setEnabled(enable);
	}

	public void setParentObject(Object parentObject) {
		if (parentObject != null && parentObject instanceof Mps) {
			Mps setup = (Mps) parentObject;
			if (setup.isFrozen()) {
				refreshToolItem(false);
			} else {
				refreshToolItem(true);
			}
			super.setParentObject(parentObject);
		}
	}
}

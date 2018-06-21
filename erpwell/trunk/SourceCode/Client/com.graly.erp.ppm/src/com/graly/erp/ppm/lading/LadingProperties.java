package com.graly.erp.ppm.lading;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Lading;
import com.graly.erp.ppm.model.Mps;
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

public class LadingProperties extends ChildEntityProperties {
	public LadingProperties() {
		super();
	}

	public LadingProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		super(masterParent, table, parentObject);
	}

	@Override
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		refreshToolItem(false);
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

	protected void saveAdapter() {
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
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					ADManager entityManager = Framework.getService(ADManager.class);
					Lading lading = (Lading) getAdObject();
					String where = "objectRrn='" + lading.getMaterialRrn() + "'";
					List<Material> materials = entityManager.getEntityList(Env.getOrgRrn(), Material.class, 2, where, "");
					if (materials == null && materials.size() == 0) {
						return;
					}
					PPMManager ppmManager = Framework.getService(PPMManager.class);
					Lading newLading = ppmManager.saveLading(getTable().getObjectRrn(), lading, Env.getUserRrn());
					setAdObject(entityManager.getEntity(newLading));
					UI.showInfo(Message.getString("common.save_successed"));// µ¯³öÌáÊ¾¿ò
					refresh();
				}
				getMasterParent().refresh();
			}

			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
}

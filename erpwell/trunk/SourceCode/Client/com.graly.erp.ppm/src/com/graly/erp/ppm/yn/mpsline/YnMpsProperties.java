package com.graly.erp.ppm.yn.mpsline;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
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

public class YnMpsProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(YnMpsProperties.class);
	ADManager adManager;

	public YnMpsProperties() {
		super();
	}

	public YnMpsProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		super(masterParent, table, parentObject);
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
					MpsLine mpsLine = ppmManager.saveMpsLine((getTable().getObjectRrn()), (MpsLine) getAdObject(), Env.getUserRrn());

					if (mpsLine != null) {
						if(mpsLine.getObjectRrn()!=null){
							ppmManager.validateMpsLine(Env.getOrgRrn(), mpsLine);
						}
						ADManager adManager = Framework.getService(ADManager.class);
						setAdObject(adManager.getEntity(mpsLine));
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
			Mps mps = (Mps) parentObject;
			if (mps.isFrozen()) {
				refreshToolItem(false);
			} else {
				refreshToolItem(true);
			}
			super.setParentObject(parentObject);
		}
	}
	
	//判断是否正在生成主计划和生产计划
	private boolean isGeneraging(MpsLine mpsLine) throws Exception {
		if(mpsLine.getMpsId() == null)
			return false;
		if(adManager == null)
			adManager = Framework.getService(ADManager.class);
		List<Mps> list = adManager.getEntityList(
				Env.getOrgRrn(), Mps.class, 1, " mpsId = '" + mpsLine.getMpsId() + "' ", null);
		if(list != null && list.size() > 0) {
			Mps mps = list.get(0);
			if(mps.getIsProcessingMps() || mps.getIsProcessingPp()) {
				return true;
			}
		}
		return false;
	}
	
	private List<?> isValue(Class<?> modelClass) throws Exception {
		MpsLine mpsLine = (MpsLine)this.getAdObject();
		if(mpsLine.getObjectRrn() == null)
			return null;
		if(adManager == null)
			adManager = Framework.getService(ADManager.class);
		String whereClause = " mpsLineRrn = " + mpsLine.getObjectRrn() + " ";
		List<?> mos = adManager.getEntityList(Env.getOrgRrn(), modelClass, Env.getMaxResult(), whereClause, null);
		return mos;
	}

	@Override
	public boolean delete() {
		return super.delete();
	}
	
	
}

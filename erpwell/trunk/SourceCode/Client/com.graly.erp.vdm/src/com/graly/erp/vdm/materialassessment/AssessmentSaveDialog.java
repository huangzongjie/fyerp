package com.graly.erp.vdm.materialassessment;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.MaterialAssessment;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AssessmentSaveDialog extends EntityDialog {
	private String button = "1";//1:not ok button
	private int MIN_DIALOG_WIDTH = 340;
	private int MIN_DIALOG_HEIGHT = 230;
	private static final Logger logger = Logger.getLogger(AssessmentSaveDialog.class);

	public AssessmentSaveDialog(Shell parent, ADTable table, ADBase adObject) {
		super(parent, table, adObject);
	}

	@Override
	protected boolean saveAdapter() {
		managedForm.getMessageManager().setAutoUpdate(false);
		managedForm.getMessageManager().removeAllMessages();
		try {
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
					VDMManager vdmManager = Framework.getService(VDMManager.class);
					MaterialAssessment ma = (MaterialAssessment) getAdObject();
					ma.setOrgRrn(Env.getOrgRrn());
					vdmManager.saveMaterialAssessment(ma, Env.getUserRrn());
					UI.showInfo(Message.getString("common.save_successed"));//µ¯³öÌáÊ¾¿ò
					setButton("0");
					return true;
				}
			}
			managedForm.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			logger.error("Error at AssessmentSaveDialog saveAdapter() : " + e);
			ExceptionHandlerManager.asyncHandleException(e);
			return false;
		}
		return false;
	}

	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	public String getButton() {
		return button;
	}

	public void setButton(String button) {
		this.button = button;
	}
}

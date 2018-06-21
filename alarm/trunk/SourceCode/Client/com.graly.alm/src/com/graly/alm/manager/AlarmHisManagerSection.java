package com.graly.alm.manager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.alm.client.ALMManager;
import com.graly.alm.model.AlarmHis;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AlarmHisManagerSection extends EntitySection{

	public AlarmHisManagerSection(ADTable adTable, ADBase adObject) {
		this.table = adTable;
		this.adObject = adObject;
	}

	public void createContents(IManagedForm form, Composite parent) {
		super.createContents(form, parent);
		refresh();
	}

	public void createToolBar(Section section) {
		final ToolBar toolBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemRefresh(toolBar);
		section.setTextClient(toolBar);
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
					AlarmHis alarmHis = new AlarmHis();
					if (getAdObject() instanceof AlarmHis) {
						alarmHis = (AlarmHis) getAdObject();
					}
					ALMManager almManager = Framework.getService(ALMManager.class);
					almManager.closeAlarmHis((AlarmHis) getAdObject(), Env.getUserRrn());
					ADManager adManager = Framework.getService(ADManager.class);
					setAdObject(adManager.getEntity(alarmHis));
					UI.showInfo(Message.getString("common.save_successed"));// 弹出提示框
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	public void refresh() {
		super.refresh();
		for (Form detailForm : getDetailForms()) {
			/* 控制alarmStatus的显示 */
			boolean isNeedClose = ((AlarmHis) getAdObject()).getIsNeedClose();
			if (isNeedClose) {
				detailForm.getFields().get("state").setEnabled(true);
			} else {
				detailForm.getFields().get("state").setEnabled(false);
			}
		}
	}
}

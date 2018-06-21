package com.graly.erp.inv.racklot;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.RacKMovementLot;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class NewRackLotDialog extends EntityDialog {
	private static final Logger logger = Logger.getLogger(NewRackLotDialog.class);
	protected CTabFolder tabs;
			
	public NewRackLotDialog(Shell parent, ADTable table, ADBase adObject) {
		super(parent, table, adObject);
	}
	
	@Override
	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		managedForm = new ManagedForm(toolkit, sForm);
		final IMessageManager mmng = managedForm.getMessageManager();
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		setTabs(new CTabFolder(body, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		getTabs().setSelectionBackground(new Color(null,new RGB(122,168,243)));
		getTabs().setSelectionForeground(new Color(null,new RGB(220,232,252)));
		toolkit.paintBordersFor(getTabs());
		
		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			NewRackLotForm itemForm = new NewRackLotForm(getTabs(), SWT.NONE, adObject, tab, mmng);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
	}
	
	public void setTabs(CTabFolder tabs) {
		this.tabs = tabs;
	}

	public CTabFolder getTabs() {
		return tabs;
	}
	
	protected boolean saveAdapter() {
		try {
			managedForm.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					INVManager invManager = Framework.getService(INVManager.class);
					RacKMovementLot rLot = invManager.saveRacKMovementLot(Env.getOrgRrn(), (RacKMovementLot) getAdObject(), Env.getUserRrn(),true);
					setAdObject(rLot);
					UI.showInfo(Message.getString("common.save_successed"));
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("Error at EntityDialog saveAdapter() : " + e);
			ExceptionHandlerManager.asyncHandleException(e);
			return false;
		}
		return false;
	}
}

package com.graly.erp.inv.rack;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.WarehouseRack;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class WarehouseRackProperties extends EntityProperties {
	public static final Logger logger = Logger.getLogger(WarehouseRackProperties.class);
	protected ToolItem itemPrint;
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
	}
	
	
	
	private void createToolItemPrint(ToolBar tBar) {
		itemPrint = new ToolItem(tBar, SWT.PUSH);
		itemPrint.setText(Message.getString("common.print"));
		itemPrint.setImage(SWTResourceCache.getImage("print"));
		itemPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				printAdapter();
			}
		});
	}



	protected void printAdapter() {
		ADBase object = this.getAdObject();
		if(object != null && object instanceof WarehouseRack){
			WarehouseRack rack = (WarehouseRack)object;
			if(rack.getObjectRrn() != null){
				PrintDialog pd = new PrintDialog(rack);
				pd.open();
			}
		}
	}
	
	@Override
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
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					
					WarehouseRack rack = (WarehouseRack) getAdObject();
					INVManager invManager = Framework.getService(INVManager.class);
					rack = invManager.saveWarehouseRack(Env.getOrgRrn(), rack, Env.getUserRrn());
					setAdObject(rack);
					UI.showInfo(Message.getString("common.save_successed"));// µ¯³öÌáÊ¾¿ò
					refresh();
					return true;
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			logger.error("WarehouseRackProperties : save()", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return false;
	}
}

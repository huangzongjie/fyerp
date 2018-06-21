package com.graly.erp.inv.modifylotid;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.selectbylot.SearchByLotSection;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class ModifyLotIdSection extends SearchByLotSection {
	private static final Logger logger = Logger.getLogger(ModifyLotIdSection.class);
	private ModifyLotIdForm searchByLotForm;

	public ModifyLotIdSection(ADTable table) {
		super(table);
	}

	@Override
	protected void createSectionContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() }, new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(Message.getString("inv.lot_info"));
			searchByLotForm = new ModifyLotIdForm(getTabs(), SWT.NONE, tab, mmng);
			getDetailForms().add(searchByLotForm);
			item.setControl(searchByLotForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}

		section.setText(String.format(Message.getString("common.detail"),
				 Message.getString("inv.modify_lotid")));
	}
	
	protected void refreshAdapter() {
		setAdObject(new Lot());
		refresh();
		text.setText("");
		IField field = getDetailForms().get(0).getFields().get("newLotId");
		field.setValue("");
		field.refresh();
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new ToolItem(tBar, SWT.PUSH);
		itemSave.setText(Message.getString("inv.savemodifylotid"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
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
					IField fieldNewLotId = getDetailForms().get(0).getFields().get("newLotId");
					String newLotId = (String) fieldNewLotId.getValue();
					if (newLotId != null && newLotId != "") {
						INVManager invManager = Framework.getService(INVManager.class);
						Lot lot = (Lot)getAdObject();
						invManager.modifyLotId(lot.getLotId(), newLotId, Env.getUserRrn(),Env.getOrgRrn());
						UI.showInfo(Message.getString("inv.save_modifylotid_success"));// µ¯³öÌáÊ¾¿ò
						refresh();
					} else {
						UI.showInfo(Message.getString("inv.newlotid_is_null"));
					}
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
}

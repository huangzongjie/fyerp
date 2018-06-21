package com.graly.erp.inv.locator.modify;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.Locator;
import com.graly.erp.inv.selectbylot.SearchByLotSection;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class LocatorModifySection extends SearchByLotSection {

	public LocatorModifySection(ADTable table) {
		super(table);
	}

	public void createContents(IManagedForm form, Composite parent) {
		super.createContents(form, parent);
		section.setText(String.format(Message.getString("common.detail"),
				Message.getString("inv.locator_modify")));
		getDetailForms().get(0).getFields().get("locatorRrn").setEnabled(true);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar,SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
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
			LocatorModifyForm searchByLotForm = new LocatorModifyForm(getTabs(), SWT.NONE, tab, mmng);
			getDetailForms().add(searchByLotForm);
			item.setControl(searchByLotForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}

	}

	public boolean save() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			ADManager entityManager = Framework.getService(ADManager.class);
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
					Lot lot = (Lot)getAdObject();
					if(lot.getIsUsed()){
						UI.showWarning(Message.getString("inv.locator_modify_isused"));
						return false;
					}
					if(!lot.getPosition().equals("INSTOCK")){
						UI.showWarning(Message.getString("inv.locator_modify_posion_not_instock"));
						return false;
					}
					if(lot.getLocatorRrn() != null){
						String where = " objectRrn = '" +lot.getLocatorRrn()+ "' ";
						List<Locator> locators = entityManager.getEntityList(Env.getOrgRrn(),Locator.class,2,where,"");
						String locatorId = locators.size() == 0 ? null : locators.get(0).getLocatorId(); 
						lot.setLocatorId(locatorId);
					}
					ADBase obj = entityManager.saveEntity(getTable().getObjectRrn(), lot, Env.getUserRrn());
					setAdObject(entityManager.getEntity(obj));
					UI.showInfo(Message.getString("common.save_successed"));// µ¯³öÌáÊ¾¿ò
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
	
	class LocatorModifyForm extends EntityForm {
		public LocatorModifyForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
			super(parent, style, tab, mmng);
		}
		
		public IField getField(ADField adField){
			String displayText = adField.getDisplayType();
			String name = adField.getName();
			IField field = null;
			if (FieldType.REFTABLE.equalsIgnoreCase(displayText) && (name.equals("locatorRrn"))) {
				adField.setIsEditable(true);
				adField.setIsReadonly(false);
			} 
			field = super.getField(adField);
			return field;
		}
	}
}

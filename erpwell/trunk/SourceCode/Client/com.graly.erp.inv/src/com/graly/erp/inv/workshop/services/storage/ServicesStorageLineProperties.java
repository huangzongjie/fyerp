package com.graly.erp.inv.workshop.services.storage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Locator;
import com.graly.erp.inv.model.MovementWorkShop;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.MovementWorkShopServices;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityForm;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ServicesStorageLineProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(ServicesStorageLineProperties.class);
	private static final String FieldName_Material = "materialRrn";
	private static final String FieldName_UomId = "uomId";
	private String PREFIX = " movementRrn = '";
	private boolean flag;
	
	public ServicesStorageLineProperties() {
		super();
	}
	
	public ServicesStorageLineProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		this(masterParent, table, parentObject, false);
	}
	
	public ServicesStorageLineProperties(EntityBlock masterParent, ADTable table, Object parentObject, boolean flag) {
		super(masterParent, table, parentObject);
		this.flag = flag;
	}
	
	// 重载实现创建MovementChildForm(使各个控件保存后为只读);为物料单位添加监听器等
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
		getTabs().setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			ChildEntityForm itemForm = new ChildEntityForm(getTabs(), SWT.NONE, null, tab, mmng, parentObject);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		if (parentObject != null) {
			loadFromParent();
		}
		
		addMaterialUomIdListener();
	}
	
	public void refresh() {
		super.refresh();
		ServicesStorageLineEntryBlock block = (ServicesStorageLineEntryBlock)getMasterParent();
		block.setParenObjectStatusChanged();
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void closeOutLineAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null && getAdObject().getObjectRrn() != null) {
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void saveAdapter() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				ServicesStorageLineEntryBlock block = (ServicesStorageLineEntryBlock)getMasterParent();
				if(!block.saveParent()) {
					form.getMessageManager().setAutoUpdate(true);
					return;   // 判断保存父对象是否成功，否则返回
				}
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
						break;
					}
				}
				boolean valiateTrf =false;
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					INVManager invManager = Framework.getService(INVManager.class);
					ADManager adManager = Framework.getService(ADManager.class);
					MovementWorkShopLine line = (MovementWorkShopLine)getAdObject();
					MovementWorkShopServices mm = (MovementWorkShopServices)block.getParentObject();
					mm.setDocType("SER");
					if(isContainsSameMaterialInLines(mm, line)) {
//						newAdapter();
						return;
					}
//					 获得该Line对应的Lots
					line = invManager.saveMovementWorkShopServicesLine(mm, line, Env.getUserRrn());
					// 刷新父子对象，并更新父对象的whereClause
					mm.setObjectRrn(line.getMovementRrn());
					block.setParentObject(adManager.getEntity(mm));
					this.setAdObject(adManager.getEntity(line));  //adManager.getEntity(line)
					getMasterParent().setWhereClause(PREFIX + mm.getObjectRrn() + "' ");
					// 提示保存成功
					UI.showInfo(Message.getString("common.save_successed"));
					refresh();
					this.getMasterParent().refresh();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	// 判断单据中是否已有相同的物料，如果有，则提示直接在原有的line上更改数量，或删除原有的line重建
	protected boolean isContainsSameMaterialInLines(MovementWorkShopServices mm, MovementWorkShopLine line) {
		if(mm.getMovementWorkShopLines() != null) {
			for(MovementWorkShopLine l : mm.getMovementWorkShopLines()) {
				if(l.getMaterialRrn().equals(line.getMaterialRrn()) && line.getObjectRrn() == null) {
					UI.showError(Message.getString("inv.doc_has_same_material"));
					getMasterParent().setSelection(l);
					return true;
				}
			}
		}
		return false;
	}
 
	
	protected void deleteAdapter() {
//		if(!PurchaseOrder.STATUS_DRAFTED.equals( ((MovementWorkShopLine)getAdObject()).getLineStatus()) ) {
//			return;
//		}
//		try {
//			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
//			if (getAdObject().getObjectRrn() != null) {
//				if (confirmDelete) {
//					INVManager invManager = Framework.getService(INVManager.class);
//					invManager.deleteMovementWorkShopDeliveryLine((MovementWorkShopLine)getAdObject(), Env.getUserRrn());
//					// 刷新父对象
//					WorkCenterMRLineEntryBlock block = (WorkCenterMRLineEntryBlock)getMasterParent();
//					MovementWorkShopDelivery mm = (MovementWorkShopDelivery)block.getParentObject();
//					ADManager adManager = Framework.getService(ADManager.class);
//					block.setParentObject(adManager.getEntity(mm));
//					setAdObject(createAdObject());
//					refresh();
//					this.getMasterParent().refresh();
//				}
//			}
//		} catch (Exception e1) {
//			ExceptionHandlerManager.asyncHandleException(e1);
//			return;
//		}
	}
	
	@Override
	public ADBase createAdObject() throws Exception {
		MovementWorkShopLine wsLine = null;
		try {
			ServicesStorageLineEntryBlock block = (ServicesStorageLineEntryBlock)this.getMasterParent();
			if(block.isEnableByParentObject()) {
				INVManager invManager = Framework.getService(INVManager.class);			
				wsLine = invManager.newMovementWorkShopLine((MovementWorkShop) block.getParentObject());
			} else {
				wsLine = new MovementWorkShopLine();
			}
			wsLine.setOrgRrn(Env.getOrgRrn());
			
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return wsLine;
	}

	@Override
	public void setAdObject(ADBase adObject) {
		this.adObject = adObject;
		MovementWorkShopLine outLine = (MovementWorkShopLine)adObject;
		if(outLine != null) {
			setStatusChanged(outLine.getLineStatus());
//			if(outLine.getObjectRrn() != null) {
//				if(itemSave.getEnabled())
//					itemSave.setEnabled(false);
//			}
		} else {
			setStatusChanged("");
		}
	}
	
	public void reputLocator(Object object){
		if(object != null){
			IField locatorField = getIField("locatorRrn");
			if(locatorField instanceof RefTableField) {
				RefTableField tf = (RefTableField)locatorField;
				try{
					ADManager entityManager = Framework.getService(ADManager.class);
					String where = " orgRrn = " + Env.getOrgRrn() + " AND warehouseRrn = '" + object.toString() + "' ";
					List<Locator> list = entityManager.getEntityList(Env.getOrgRrn(), Locator.class, Env.getMaxResult(), where, "");
					list = list.size() == 0 ? new ArrayList<Locator>() : list;
					tf.setInput(list);
					tf.refresh();
				}catch(Exception e){
					ExceptionHandlerManager.asyncHandleException(e);
					return;
				}
			}
		}
	}

	private void addMaterialUomIdListener() {
		IField materialField = getIField(FieldName_Material);
		materialField.addValueChangeListener(getUomIdrChangedListener());
	}
	
	private IField getIField(String fieldId) {
		IField f = null;
		for(Form form : getDetailForms()) {
			f = form.getFields().get(fieldId);
			if(f != null) {
				return f;
			}
		}
		return f;
	}
	
	private IValueChangeListener getUomIdrChangedListener(){
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				IField ifield = getIField(FieldName_UomId);
				try {
					if(ifield instanceof TextField) {
						Material material = null;
						if(newValue instanceof String){
							Long materialRrn = Long.parseLong((String) newValue);
							ADManager adManager = Framework.getService(ADManager.class);
							material.setObjectRrn(materialRrn);
							material = (Material) adManager.getEntity(material);
						}else if(newValue instanceof Material){
							material = (Material)newValue;
						}
						TextField tf = (TextField)ifield;
						if(material != null) {
							tf.setValue(material.getInventoryUom());
						}else {
							tf.setValue("");
						}
						tf.refresh();
					}
				} catch (NumberFormatException e0){
					TextField tf = (TextField)ifield;
					tf.setValue("");
					tf.refresh();
				} catch (Exception e) {
					logger.error(e);
				}
			}
		};
	};
	
	protected void setStatusChanged(String status) {
		if(flag){
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
		} else if(MovementWorkShopServices.STATUS_DRAFTED.equals(status)) {
			itemNew.setEnabled(true);
			itemSave.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if(MovementWorkShopServices.STATUS_APPROVED.equals(status)) {
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}
}

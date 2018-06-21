package com.graly.erp.inv.transfer.hy.dpk;

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
import com.graly.erp.inv.MovementChildForm;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Locator;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.po.model.PurchaseOrder;
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

public class DpkTransferLineProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(DpkTransferLineProperties.class);
	private static final String FieldName_Material = "materialRrn";
	private static final String FieldName_UomId = "uomId";
	private String PREFIX = " movementRrn = '";
	private boolean flag;
	
	public DpkTransferLineProperties() {
		super();
	}
	
	public DpkTransferLineProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		this(masterParent, table, parentObject, false);
	}
	
	public DpkTransferLineProperties(EntityBlock masterParent, ADTable table, Object parentObject, boolean flag) {
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
		//Locator第一次赋值
		DpkTransferLineEntryBlock block = (DpkTransferLineEntryBlock)getMasterParent();
		IField warehouse = (IField)block.getDetailForms().get(0).getFields().get("targetWarehouseRrn");
		Object  warehouseRrn= warehouse.getValue();
		if(warehouseRrn != null){
			reputLocator(warehouseRrn);
		}
	}
	
	public void refresh() {
		super.refresh();
		DpkTransferLineEntryBlock block = (DpkTransferLineEntryBlock)getMasterParent();
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
				DpkTransferLineEntryBlock block = (DpkTransferLineEntryBlock)getMasterParent();
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
				//李佳怡不需要此功能给他带来麻烦,因此加个权限判断总仓到制造权限
				if (Env.getAuthority().contains("INV.Transfer.zcTozz")) {
					valiateTrf =true;
				}
				if(Env.getOrgRrn() == 139420L && valiateTrf){//如果是环保-制造调拨 则申请数可以填值,否则不能
					MovementTransfer trf = (MovementTransfer) getParentObject();
					if(trf !=null ){
						String whouse = trf.getWarehouseRrn()==null?"":trf.getWarehouseRrn().toString();
						String twhouse = trf.getTargetWarehouseRrn()==null?"":trf.getTargetWarehouseRrn().toString();
						MovementLine line = (MovementLine)getAdObject();
						if("151043".equals(whouse)&& "151046".equals(twhouse)){
							if(!"".equals(line.getXzUserRrn())&& line.getXzUserRrn()!=null){
							}else{
								UI.showError("环保调拨到制造仓库,必须输入申请数量...如有疑问请联系IT");
								return;
							}
						}
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					INVManager invManager = Framework.getService(INVManager.class);
					ADManager adManager = Framework.getService(ADManager.class);
					MovementLine line = (MovementLine)getAdObject();
					// 判断是否自制件，如果是警告
					StringBuffer materialIds = new StringBuffer();
						Material m = new Material();
						m.setObjectRrn(line.getMaterialRrn());
						try {
							ADManager manager = Framework.getService(ADManager.class);
							m = (Material) manager.getEntity(m);
						} catch (Exception e) {
							logger.error("TransferLineProperties : saveAdapter()", e);
						}
						if (m.getMaterialCategory1() != null
								&& m.getMaterialCategory2() != null) {
							boolean f1 = "生产物料".equalsIgnoreCase(m
									.getMaterialCategory1());
							boolean f2 = "自制"
									.equalsIgnoreCase(m.getMaterialCategory2());
							if (f1 && f2) {
								materialIds.append(m.getMaterialId());
							}
						}
						if (materialIds.toString().length() > 0) {
							boolean confirm = UI.showConfirm("以下物料 " + materialIds.toString()
									+ " 是自制件，是否确定要继续?");
							if(!confirm) return;
						}
					MovementTransfer mm = (MovementTransfer)block.getParentObject();
					if(isContainsSameMaterialInLines(mm, line)) {
//						newAdapter();
						return;
					}
					// 获得该Line对应的Lots
					line.setMovementLots(getLineLotList(mm, line));

					// 调用savePOLine()方法
					line = invManager.saveMovementTransferLineDpk(mm, line, Env.getUserRrn());
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
	protected boolean isContainsSameMaterialInLines(MovementTransfer mm, MovementLine line) {
		if(mm.getMovementLines() != null) {
			for(MovementLine l : mm.getMovementLines()) {
				if(l.getMaterialRrn().equals(line.getMaterialRrn()) && line.getObjectRrn() == null) {
					UI.showError(Message.getString("inv.doc_has_same_material"));
					getMasterParent().setSelection(l);
					return true;
				}
			}
		}
		return false;
	}
	
	protected List<MovementLineLot> getLineLotList(MovementTransfer mtrs, MovementLine line) throws Exception{
		List<MovementLineLot> lineLots = null;
		if(mtrs.getObjectRrn() == null || line.getObjectRrn() == null)
			return lineLots;
		String whereClause = " movementLineRrn = " + line.getObjectRrn() + " ";
    	ADManager manager = Framework.getService(ADManager.class);
    	lineLots = manager.getEntityList(Env.getOrgRrn(), MovementLineLot.class,
        		Integer.MAX_VALUE, whereClause, null);
		return lineLots;
	}
	
	protected void deleteAdapter() {
		if(!PurchaseOrder.STATUS_DRAFTED.equals( ((MovementLine)getAdObject()).getLineStatus()) ) {
			return;
		}
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (getAdObject().getObjectRrn() != null) {
				if (confirmDelete) {
					INVManager invManager = Framework.getService(INVManager.class);
					invManager.deleteMovementTransferLine((MovementLine)getAdObject(), Env.getUserRrn());
					// 刷新父对象
					DpkTransferLineEntryBlock block = (DpkTransferLineEntryBlock)getMasterParent();
					MovementTransfer mm = (MovementTransfer)block.getParentObject();
					ADManager adManager = Framework.getService(ADManager.class);
					block.setParentObject(adManager.getEntity(mm));
					setAdObject(createAdObject());
					refresh();
					this.getMasterParent().refresh();
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
	
	@Override
	public ADBase createAdObject() throws Exception {
		MovementLine transferLine = null;
		try {
			DpkTransferLineEntryBlock block = (DpkTransferLineEntryBlock)this.getMasterParent();
			if(block.isEnableByParentObject()) {
				INVManager invManager = Framework.getService(INVManager.class);			
				transferLine = invManager.newMovementLine((MovementTransfer)block.getParentObject());
			} else {
				transferLine = new MovementLine();
			}
			transferLine.setOrgRrn(Env.getOrgRrn());
			
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return transferLine;
	}

	@Override
	public void setAdObject(ADBase adObject) {
		this.adObject = adObject;
		MovementLine outLine = (MovementLine)adObject;
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
		} else if(PurchaseOrder.STATUS_DRAFTED.equals(status)) {
			itemNew.setEnabled(true);
			itemSave.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if(PurchaseOrder.STATUS_APPROVED.equals(status)) {
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

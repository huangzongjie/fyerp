package com.graly.erp.xz.pur.po;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.VUserWarehouse;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.po.copyfrom.ApprovedPRListDialog;
import com.graly.erp.pur.request.WarehouseChildEntityForm;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.CalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class XZPOLineProperties extends ChildEntityProperties {
	private static final Logger	logger				= Logger.getLogger(XZPOLineProperties.class);
	private static final String	FieldName_Material	= "materialRrn";
	private static final String	FieldName_Vendor	= "vendorRrn";
	private static final String	FieldName_Purchaser	= "purchaser";
	private static final String	FieldName_UnitPrice	= "unitPrice";
	private static final String	FieldName_UomId		= "uomId";
	private static final String	FieldName_PackageSpec	= "packageSpec";
	private static final String	FieldName_DateEnd	= "dateEnd";			//到货日期
	private static final String FieldName_Urgency   = "urgency";			//紧急度
	private static final String FieldName_DatePromised = "datePromised";	//最新到货日期
	private static final String FieldName_LineTotal = "lineTotal";
	private static final String	TABLE_NAME			= "BJPURPurchaseOrder";

	protected ToolItem			itemClose;
	protected ToolItem			itemSave2;														// 只保存到货日期、紧急度，最新到货日期
	protected ToolItem			itemLineTotalSave;
	protected ToolItem			itemReceiptDate;
	private ADTable				adTable;
	private static String	AUTHORITY_UNITPRICE	= "PUR.PoLine.UnitPrice";
	private static String   AUTHORITY_LINETOTAL = "PUR.PoLine.LineTotal"; 
	private static String   AUTHORITY_REVOKE 	= "PUR.Po.Revoke";
	private int  Operation_Flag = 1;//1:保存事件  2:修改后保存事件  3:行总价后保存事件

	public XZPOLineProperties() {
		super();
	}

	public XZPOLineProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		super(masterParent, table, parentObject);
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
		getTabs().setSelectionBackground(new Color[] { selectedColor,
				toolkit.getColors().getBackground() }, new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			WarehouseChildEntityForm itemForm = new WarehouseChildEntityForm(getTabs(), SWT.NONE, null, tab, mmng, parentObject);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		if (parentObject != null) {
			loadFromParent();
		}

		addMaterialAndVendorListener();
	}

	public void refresh() {
		try {
			if (getAdObject() == null) {
				this.setAdObject(createAdObject());
			}
			//2012-03-07 Simon  在审核后刷新，物料不容许变更
//			PurchaseOrderLine poLine = (PurchaseOrderLine) getAdObject();
//			IField materialField = getIField(FieldName_Material);
//			IField uomId = getIField(FieldName_UomId);
//			if (poLine.getRequisitionLineRrn() != null) {
//				materialField.setEnabled(false);
//				uomId.setEnabled(false);
//			} 
//			else {
//				materialField.setEnabled(true);
//				uomId.setEnabled(true);
//			}
			super.refresh();
			IField unitPriceField = getIField(FieldName_UnitPrice);
			if(!Env.getAuthority().contains(AUTHORITY_UNITPRICE)){//如果没有价格权限，则无法看到单价，也无法改单价
				unitPriceField.setValue("-");
				unitPriceField.setEnabled(false);
				unitPriceField.refresh();
			}
			//设置行总价文本框不可编辑
			IField linetotalField = getIField(FieldName_LineTotal);
			linetotalField.setEnabled(false);
			linetotalField.refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected ADTable getADTableOfPO() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemReceiptDate(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemOpenPR(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemLineTotalSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		createToolItemSave2(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);

		createToolItemClosePOLine(tBar);//撤销
		section.setTextClient(tBar);
	}

	protected void createToolItemReceiptDate(ToolBar tBar) {
		itemReceiptDate = new ToolItem(tBar, SWT.PUSH);
		itemReceiptDate.setText(Message.getString("pur.change_receipt_date"));
		itemReceiptDate.setImage(SWTResourceCache.getImage("edit"));
		itemReceiptDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				receiptDateAdapter();
			}
		});
	}
	
	protected void createToolItemSave2(ToolBar tBar) {
		itemSave2 = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_POLINE_SAVE2);
		itemSave2.setText("修改");
		itemSave2.setImage(SWTResourceCache.getImage("edit"));
		itemSave2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				save2Adapter();
			}
		});
	}

	protected void createToolItemLineTotalSave(ToolBar tBar)
	{
		itemLineTotalSave = new AuthorityToolItem(tBar, SWT.PUSH,AUTHORITY_LINETOTAL);//判断用户是否有该权限按钮
		itemLineTotalSave.setText("行总价");
		itemLineTotalSave.setImage(SWTResourceCache.getImage("edit"));
		itemLineTotalSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				LineTotalSaveAdapter();
			}
		});
	}

	protected void receiptDateAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
						break;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					PurchaseOrderLine poLine = (PurchaseOrderLine) getAdObject();
					ADManager adManager = Framework.getService(ADManager.class);
					if(poLine.getObjectRrn()!=null){
						PurchaseOrderLine oldPoLine = new PurchaseOrderLine();
						oldPoLine.setObjectRrn(poLine.getObjectRrn());
						oldPoLine = (PurchaseOrderLine) adManager.getEntity(oldPoLine);
						oldPoLine.setReceiptDate(poLine.getReceiptDate());
						oldPoLine.setReceiptDateHour(poLine.getReceiptDateHour());
						adManager.saveEntity(oldPoLine, Env.getUserRrn());
						UI.showInfo(Message.getString("common.save_successed"));
					} 
					
					refresh();
					this.getMasterParent().refresh();
//					changedMasterParentStatus();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void save2Adapter() {
		//设置操作标识为 修改后保存
		Operation_Flag = 2;
		//设置输入框编辑状态为false
		for (Form form : getDetailForms()) {
			Iterator<Entry<String,IField>> iter = form.getFields().entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String,IField> element=(Map.Entry<String,IField>)iter.next();
				IField f =(IField)element.getValue();
				if(element.getKey().toString().equals(FieldName_Urgency) || 
						element.getKey().toString().equals(FieldName_DateEnd) || 
						element.getKey().toString().equals(FieldName_DatePromised)){
					f.setEnabled(true);
				}
				else{
					f.setEnabled(false);
				}
			}
		}
		//设置按钮状态为不可用
		itemClose.setEnabled(false);//撤销
		itemRefresh.setEnabled(false);//刷新
		itemLineTotalSave.setEnabled(false);//行总价
		itemSave.setEnabled(true);
	}

	protected void LineTotalSaveAdapter()
	{
		//设置操作标识为 行总价后保存
		Operation_Flag = 3;
		//设置输入框编辑状态为false
		for (Form form : getDetailForms()) {
			Iterator<Entry<String,IField>> iter = form.getFields().entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String,IField> element=(Map.Entry<String,IField>)iter.next();
				IField f =(IField)element.getValue();
				if(!element.getKey().toString().equals(FieldName_LineTotal) ){
					f.setEnabled(false);
				}
				else{
					f.setEnabled(true);
				}
			}
		}
		//设置按钮控件为false
//		itemOpenPR.setEnabled(false);//创建从
		itemClose.setEnabled(false);//撤销
		itemSave2.setEnabled(false);//修改
		itemNew.setEnabled(false);//新建
		itemDelete.setEnabled(false);//删除
		itemRefresh.setEnabled(false);//刷新
		itemSave.setEnabled(true);//保存
		
		IField linetotalField = getIField(FieldName_LineTotal);
		linetotalField.setEnabled(true);
		linetotalField.refresh();
	}
	
	protected void createToolItemClosePOLine(ToolBar tBar) {
		itemClose = new AuthorityToolItem(tBar, SWT.PUSH, AUTHORITY_REVOKE);//new ToolItem(tBar, SWT.PUSH);
		itemClose.setText(Message.getString("common.close"));
		itemClose.setImage(SWTResourceCache.getImage("close"));
		itemClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				closePOLineAdapter();
			}
		});
	}

	protected void createToolItemOpenPR(ToolBar tBar) {
//		itemOpenPR = new ToolItem(tBar, SWT.PUSH);
//		itemOpenPR.setText(Message.getString("pur.copyfrom"));
//		itemOpenPR.setImage(SWTResourceCache.getImage("copy"));
//		itemOpenPR.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				openPRAdapter();
//			}
//		});
	}

	protected void closePOLineAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null && getAdObject().getObjectRrn() != null) {
				if (UI.showConfirm(Message.getString("common.confirm_repeal"))) {
					PURManager pudManager = Framework.getService(PURManager.class);
					PurchaseOrderLine poLine = pudManager.closePOLine((PurchaseOrderLine) getAdObject(), Env.getUserRrn());
					setAdObject(poLine);
					// 重新获取PO, 刷新PO状态(因为关闭POLine可能会引起PO状态改变)
					if (poLine.getPoRrn() != null) {
						XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
						PurchaseOrder po = new PurchaseOrder();
						po.setObjectRrn(poLine.getPoRrn());
						ADManager adManager = Framework.getService(ADManager.class);
						poLineBlock.setParentObject(adManager.getEntity(po));
						getMasterParent().setWhereClause(" poRrn = '"
								+ po.getObjectRrn() + "'");
					}
					UI.showInfo(Message.getString("common.close_successed"));
					refresh();
					getMasterParent().refresh();
					changedMasterParentStatus();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void openPRAdapter() {
		form.getMessageManager().removeAllMessages();
		XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
		PurchaseOrder po = (PurchaseOrder) poLineBlock.getParentObject();
		ApprovedPRListDialog apd = new ApprovedPRListDialog(UI.getActiveShell(), po);
		if (apd.open() == Dialog.CANCEL) {
			poLineBlock.setParentObject(apd.getPo());
			if (apd.getPo().getObjectRrn() != null) {
				poLineBlock.setWhereClause(" poRrn = '"
						+ apd.getPo().getObjectRrn() + "'");
			}
		}
		refresh();
		getMasterParent().refresh();
		changedMasterParentStatus();
	}

	protected void saveAdapter() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
				switch(Operation_Flag){
				case 1://普通保存,保存父节点
					if (!poLineBlock.saveParent()) {
						form.getMessageManager().setAutoUpdate(true);
						return; // 判断保存父对象是否成功，否则返回
					}
					break;
				case 2://修改后保存
					break;
				case 3://行总价后保存
					break;
					default:
						break;
				}
				
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
						break;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					switch(Operation_Flag){
					case 1://普通保存
						setPOlineTotal(); // 计算POLine.lineTotal
						break;
					case 2://修改后保存	
						setIFieldStatus(true);
						break;
					case 3://行总价后保存
						setIFieldStatus(true);
						break;
					default:
						break;
					}
					//标识复位
					Operation_Flag = 1;
					PurchaseOrderLine poLine = (PurchaseOrderLine) getAdObject();
					PurchaseOrder po = (PurchaseOrder) poLineBlock.getParentObject();
					ADManager adManager = Framework.getService(ADManager.class);
					if(poLine.getObjectRrn()!=null){
						PurchaseOrderLine oldPoLine = new PurchaseOrderLine();
						oldPoLine.setObjectRrn(poLine.getObjectRrn());
						oldPoLine = (PurchaseOrderLine) adManager.getEntity(oldPoLine);
						if(!poLine.getMaterialRrn().equals(oldPoLine.getMaterialRrn())){
							UI.showError("新旧物料不一致，如需更换物料，请删除该物料后，重新建立");
							return;
						}
					} 
					// 调用savePOLine()方法
					PURManager purManager = Framework.getService(PURManager.class);
					poLine = purManager.savePOLine(po, poLine, Env.getUserRrn());
					// 刷新父子对象，并更新父对象的whereClause
					po = new PurchaseOrder();
					
					po.setObjectRrn(poLine.getPoRrn());
					po = (PurchaseOrder) adManager.getEntity(po);
					poLineBlock.setParentObject(po);
					this.setParentObject(po);
					this.setAdObject(adManager.getEntity(poLine));
					getMasterParent().setWhereClause(" poRrn = '"
							+ po.getObjectRrn() + "'");
					// 提示保存成功
					UI.showInfo(Message.getString("common.save_successed"));
					refresh();
					this.getMasterParent().refresh();
					changedMasterParentStatus();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	private void copyProperties(Object destBean, Object sourceBean, LinkedHashMap<String, IField> fields){
		PurchaseOrderLine poLine = (PurchaseOrderLine)destBean;
		String datehis = poLine.getDateHisPromised() == null ? "" : poLine.getDateHisPromised() ;
		for (String name : fields.keySet()){
			try {
				if(name != null && !"".equals(name)){
					IField f = fields.get(name);
					if (!(f instanceof SeparatorField)){
						Object obj = PropertyUtils.getProperty(sourceBean, name);
						if(name.equals("datePromised")){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
							if(datehis == ""){
								if(obj!=null) datehis = sdf.format((Date)obj);
							}
							else{
								datehis += "/" + sdf.format((Date)obj);
							}
							poLine.setDateHisPromised(datehis);
						}
						PropertyUtils.setProperty(destBean, name, obj);					
					}
				}
				
			} catch (Exception e) {
				logger.error("PropertyUtil : copyProperties ", e);
			}
		}
	}
	
	protected void setPOlineTotal() {
		PurchaseOrderLine poLine = (PurchaseOrderLine) getAdObject();
		if (poLine.getQty() == null || poLine.getUnitPrice() == null) {
			poLine.setLineTotal(BigDecimal.ZERO);
		} else {
			poLine.setLineTotal(poLine.getQty().multiply(poLine.getUnitPrice()));
		}
	}

	protected void changedMasterParentStatus() {
		XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
		poLineBlock.setParentObjectStatusChanged();
	}

	protected void deleteAdapter() {
		if (!PurchaseOrder.STATUS_DRAFTED.equals(((PurchaseOrderLine) getAdObject()).getLineStatus())) {
			return;
		}
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (getAdObject().getObjectRrn() != null) {
					PURManager purManager = Framework.getService(PURManager.class);
					purManager.deletePOLine((PurchaseOrderLine) getAdObject(), Env.getUserRrn());
					setAdObject(createAdObject());
					refresh();
					// 删除poLine会更新po相关数据,所以要重新刷新获得po并通知Master按钮状态改变
					XZPOLineEntityBlock block = (XZPOLineEntityBlock) getMasterParent();
					PurchaseOrder mm = (PurchaseOrder) block.getParentObject();
					ADManager adManager = Framework.getService(ADManager.class);
					mm = (PurchaseOrder) adManager.getEntity(mm);
					block.setParentObject(mm);
					this.setParentObject(mm);
					getMasterParent().refresh();
					changedMasterParentStatus();
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}

	@Override
	public ADBase createAdObject() throws Exception {
		PurchaseOrderLine poLine = null;
		try {
			XZPOLineEntityBlock block = (XZPOLineEntityBlock) this.getMasterParent();
			if (block.isEnableByParentObject()) {
				PURManager purManager = Framework.getService(PURManager.class);
				XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
				PurchaseOrder po = (PurchaseOrder) poLineBlock.getParentObject();
				poLine = purManager.newPOLine((PurchaseOrder) poLineBlock.getParentObject());

				ADManager adManager = Framework.getService(ADManager.class);
				String whereClause = "VUserWarehouse.userRrn = "
						+ Env.getUserRrn();
				List<VUserWarehouse> wHouses = adManager.getEntityList(Env.getOrgRrn(), VUserWarehouse.class, Integer.MAX_VALUE, whereClause, null);
				for (VUserWarehouse whouse : wHouses) {
					if ("Y".equals(whouse.getIsDefault())) {
						po.setWarehouseRrn(whouse.getObjectRrn());// 带出默认仓库
						poLineBlock.setParentObject(po);
						poLineBlock.refresh();
						break;
					}
				}
			} else {
				poLine = new PurchaseOrderLine();
			}
			poLine.setUrgency(PurchaseOrderLine.URGENCY_NORMAL);// 默认紧急度为正常
			poLine.setOrgRrn(Env.getOrgRrn());
			return poLine;
		} catch (Exception e) {
			logger.error("Error at POLineProperties : createAdObject()" + e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return poLine;
	}

	private void addMaterialAndVendorListener() {
		IField f = getIFieldFromParent(FieldName_Vendor); // 从MaseterParent中获得供应商IField
		IField materialField = getIField(FieldName_Material);

		// 为供应商添加物料监听器,由于是PO(SearchField)和POLine(RefTableField)之间的信息监听用程序实现
		if (materialField instanceof RefTableField) {
			SearchField sf = (SearchField) f;
			RefTableField rtf = (RefTableField) materialField;
			sf.addValueChangeListener(rtf);

			rtf.valueChanged(f, sf.getData());
		}
		// 为物料添加监听器,当物料改变时，以确定单价和单位的改变
		if (materialField != null) {
			materialField.addValueChangeListener(getMaterialChangedListener());
		}
	}

	private IField getIFieldFromParent(String fieldId) {
		IField f = null;
		XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
		for (Form form : poLineBlock.getDetailForms()) {
			f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return f;
	}

	private IField getIField(String fieldId) {
		IField f = null;
		for (Form form : getDetailForms()) {
			f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return f;
	}

	private IValueChangeListener getMaterialChangedListener() {
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				// 根据供应商、物料确定单价
				changedUnitPriceValue(sender, newValue);
				// 根据物料,改变物料单位,暂未实现
				if (sender instanceof RefTableField) {
					refreshUomId(sender, newValue);
					refreshPackageSpec(sender, newValue);
					refreshPurchase(sender, newValue);
				}
			}
		};
	};

	protected void refreshPackageSpec(Object sender, Object newValue) {
		IField ifield = getIField(FieldName_PackageSpec);
		if (ifield instanceof TextField) {
			TextField txtf = (TextField) ifield;

			RefTableField mrField = (RefTableField) sender;
			VendorMaterial vm = (VendorMaterial) mrField.getData();
			
			if (vm != null) {
				Material mater = vm.getMaterial();
				if(mater != null && mater.getPackageSpec() != null && mater.getPackageSpec().trim().length() > 0){
					txtf.setValue(mater.getPackageSpec());
				}else{
					txtf.setValue(vm.getPackageSpec());
				}
			} else {
				txtf.setValue(null);
			}
			txtf.refresh();
		}
	}

	protected void refreshPurchase(Object sender, Object newValue) {
		// 刷新BJPOLineEntityBlock页面上的采购员
		XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
		IField ifield = poLineBlock.getIField(FieldName_Purchaser);
		if (ifield != null && ifield instanceof TextField) {
			TextField rtf = (TextField) ifield;

			RefTableField mrField = (RefTableField) sender;
			VendorMaterial vm = (VendorMaterial) mrField.getData();
			if (vm != null) {
				rtf.setValue(vm.getPurchaser());
			} else {
				// rtf.setValue(null);
			}
			rtf.refresh();
		}
	}

	private void changedUnitPriceValue(Object sender, Object newValue) {
		IField ifield = getIField(FieldName_UnitPrice);
		IField ifieldDateEnd = getIField(FieldName_DateEnd);

		RefTableField refField = (RefTableField) sender;
		VendorMaterial vm = (VendorMaterial) refField.getData();

		if (ifield instanceof TextField) {
			TextField tf = (TextField) ifield;
			if (vm != null) {
				if (vm.getLastPrice() != null) {
					tf.setValue(vm.getLastPrice().toString());// 带出上次价格
				} else if (vm.getReferencedPrice() != null) {
					tf.setValue(vm.getReferencedPrice().toString());
				} else {
					tf.setValue("");
				}
			} else
				tf.setValue("");
			tf.refresh();
		}

		if (ifieldDateEnd instanceof CalendarField) {
			CalendarField calendarField = (CalendarField) ifieldDateEnd;
			if (vm != null && vm.getLeadTime() != null) {
				BASManager basManager;
				try {
					basManager = Framework.getService(BASManager.class);
					BusinessCalendar prCalendar = basManager.getCalendarByDay(Env.getOrgRrn(), BusinessCalendar.CALENDAR_PURCHASE);
					Date now = Env.getSysDate();
					Date dateStart = prCalendar.findStartOfNextDay(now);
					int leadTime = Integer.parseInt(vm.getLeadTime().toString());
					Date dateEnd = prCalendar.addDay(dateStart, leadTime);
					calendarField.setValue(dateEnd);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				calendarField.setValue(null);
			calendarField.refresh();
		}
	}

	private void refreshUomId(Object sender, Object newValue) {
		IField ifield = getIField(FieldName_UomId);
		if (ifield instanceof RefTableField) {
			RefTableField rtf = (RefTableField) ifield;

			RefTableField mrField = (RefTableField) sender;
			VendorMaterial vm = (VendorMaterial) mrField.getData();
			if (vm != null) {
				rtf.setValue(vm.getMaterial().getInventoryUom());
			} else {
				rtf.setValue(null);
			}
			rtf.refresh();
		}
	}

	@Override
	public void setAdObject(ADBase adObject) {
		this.adObject = adObject;
		PurchaseOrderLine poLine = (PurchaseOrderLine) adObject;
		if (poLine != null) {
			setStatusChanged(poLine.getLineStatus());
		} else {
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
		if (XZPOLineEntityBlock.flag) {
//			itemOpenPR.setEnabled(false);
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
			itemRefresh.setEnabled(false);
			itemClose.setEnabled(false);
			itemSave2.setEnabled(false);
			itemLineTotalSave.setEnabled(false);
		} else if (PurchaseOrder.STATUS_DRAFTED.equals(status)) {
//			itemOpenPR.setEnabled(true);
			itemNew.setEnabled(true);
			itemSave.setEnabled(true);
			itemDelete.setEnabled(true);
			itemClose.setEnabled(false);
			itemSave2.setEnabled(false);
			if(Env.getAuthority().contains(AUTHORITY_LINETOTAL)){
				itemLineTotalSave.setEnabled(true);
			}
			setIFieldStatus(true);
		} else if (PurchaseOrder.STATUS_APPROVED.equals(status)) {
//			itemOpenPR.setEnabled(false);
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
			if(Env.getAuthority().contains(AUTHORITY_REVOKE)){//撤销权限
				itemClose.setEnabled(true);
			}
			if(Env.getAuthority().contains(Constants.KEY_POLINE_SAVE2)){
				itemSave2.setEnabled(true);
			}
			if(Env.getAuthority().contains(AUTHORITY_LINETOTAL)){
				itemLineTotalSave.setEnabled(true);
			}
			setIFieldStatus(false);
		} else {
//			itemOpenPR.setEnabled(false);
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
			itemClose.setEnabled(false);
			itemSave2.setEnabled(false);
			itemLineTotalSave.setEnabled(false);
		}
	}

	private void setIFieldStatus(boolean b ){
		IField f = null;
		f = getIField("lineNo");//行号
		f.setEnabled(b);
		f = getIField("materialRrn");//物料编号
		f.setEnabled(b);
		f = getIField("qty");//订单数
		f.setEnabled(b);
		f = getIField("unitPrice");//单价
		f.setEnabled(b);
		f = getIField("urgency");//紧急度
		f.setEnabled(b);
		f = getIField("dateEnd");//到货日期
		f.setEnabled(b);
		f = getIField("datePromised");//最新到货日期
		f.setEnabled(b);
	}
}

class BrowserDialog extends TrayDialog{
	protected String url;
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 500;
	
	public BrowserDialog(Shell parentShell, String url) {
		super(parentShell);
		this.url = url;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Browser browser = new Browser(parent,SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
        gd.minimumWidth = MIN_DIALOG_WIDTH ;
        gd.minimumHeight = MIN_DIALOG_HEIGHT ;
        browser.setUrl(url);
        browser.setLayoutData(gd);
		browser.setUrl(url);
		return browser;
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {}
	
}

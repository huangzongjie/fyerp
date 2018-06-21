package com.graly.erp.xz.pur.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.VUserWarehouse;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.CalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.RefTextField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.ADUser;

public class XZRequisitionLineProperties extends ChildEntityProperties {

	private static final Logger logger = Logger.getLogger(XZRequisitionLineProperties.class);
	private static final String FieldName_Material = "materialRrn";
	private static final String FieldName_Vendor = "vendorRrn";
	private static final String FieldName_UnitPrice = "unitPrice";//单价
	private static final String FieldName_UonId = "uomId";
	private static final String FieldName_DateEnd = "dateEnd";//到货日期
	private static final String FieldName_QtyEconomicSize = "qtyEconomicSize";//经济批量
	private static final String FieldName_Purchaser = "purchaser";
	private static final String FieldName_Qty = "qty";//实际采购量
	private static final String FieldName_AdvancePayment = "advancePayment";//预付款
	private static final String FieldName_AdvanceRatio = "advanceRatio";//预付款率%
	private static final String FieldName_PriceLowest = "priceLowest";//最低采购价
	private static final String FieldName_PriceAverage = "priceAverage";//平均价
	private static final String FieldName_PriceLast = "priceLast";//上次采购价
	private static final String	FieldName_PackageSpec	= "packageSpec";//包装规格

	protected ToolItem itemClose;

	public XZRequisitionLineProperties() {
		super();
	}

	public XZRequisitionLineProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
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
		getTabs().setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
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
		setRefPrice(false); // 将最低价、平均价和上次采购价栏位设置成只读
		addFieldsListeners();
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		section.setTextClient(tBar);
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
					setLineTotal();

					ADManager adManager = Framework.getService(ADManager.class);
					PURManager purManager = Framework.getService(PURManager.class);
					RequisitionLine prLine = purManager.saveXZPRLine((RequisitionLine) getAdObject(), Env.getUserRrn());
					if (prLine.getRequisitionRrn() != null) {
						Requisition pr = new Requisition();
						pr.setObjectRrn(prLine.getRequisitionRrn());
						pr = (Requisition) adManager.getEntity(pr);
						this.setParentObject(pr);
						((XZRequisitionLineEntityBlock) getMasterParent()).setParentObject(pr);
						loadFromParent();
						getMasterParent().setWhereClause(" requisitionRrn = '" + pr.getObjectRrn() + "'");
					}

					UI.showInfo(Message.getString("common.save_successed"));// 弹出提示框
					
					prLine = (RequisitionLine) adManager.getEntity(prLine);
					setAdObject(prLine);
					refresh();
					getMasterParent().refresh();
					changedMasterParentStatus();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	@Override
	public void refresh() {
		try {
			if(getAdObject() == null) {
				this.setAdObject(createAdObject());
			}			
			super.refresh();
		} catch(Exception e) {
		}
	}

	protected void deleteAdapter() {
		if (!Requisition.STATUS_DRAFTED.equals(((RequisitionLine) getAdObject()).getLineStatus())) {
			return;
		}
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (getAdObject().getObjectRrn() != null) {
					ADBase oldBase = getAdObject();
					PURManager purManager = Framework.getService(PURManager.class);
					purManager.deletePRLine((RequisitionLine) getAdObject(), Env.getUserRrn());
					setAdObject(createAdObject());
					refresh();
					getMasterParent().refreshDelete(oldBase);
					// 删除prLine会更新pr相关数据,所以要重新刷新获得pr并通知状态改变
					Requisition pr = (Requisition)this.getParentObject();
					ADManager adManager = Framework.getService(ADManager.class);
					pr = (Requisition)adManager.getEntity(pr);
					((XZRequisitionLineEntityBlock) getMasterParent()).setParentObject(pr);
					this.setParentObject(pr);
					changedMasterParentStatus();
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}

	protected void changedMasterParentStatus() {
		XZRequisitionLineEntityBlock parentBlock = (XZRequisitionLineEntityBlock) getMasterParent();
		parentBlock.setParenObjectStatusChanged();
	}

	@Override
	public ADBase createAdObject() throws Exception {
		RequisitionLine prLine = null;
		try {
			XZRequisitionLineEntityBlock block = (XZRequisitionLineEntityBlock) this.getMasterParent();
			if (block.isEnableByParentObject()) {
				PURManager purManager = Framework.getService(PURManager.class);
				prLine = purManager.newPRLine((Requisition) getParentObject());
				ADManager adManager = Framework.getService(ADManager.class);
//				List<ADUser> users = adManager.getEntityList(Env.getOrgRrn(), ADUser.class);
//				for(ADUser purchaser : users){
//					if(purchaser.getIsPurchaser()){
//						prLine.setPurchaser(purchaser.getUserName());
//						break;
//					}
//				}
				String whereClause = "VUserWarehouse.userRrn = " + Env.getUserRrn();
				List<VUserWarehouse> wHouses = adManager.getEntityList(Env.getOrgRrn(), VUserWarehouse.class, Integer.MAX_VALUE, whereClause, null);
				for(VUserWarehouse whouse : wHouses){
					if("Y".equals(whouse.getIsDefault())){
						prLine.setWarehouseRrn(whouse.getObjectRrn());
						break;
					}
				}
			} else {
				prLine = new RequisitionLine();
			}
			prLine.setOrgRrn(Env.getOrgRrn());
			ADUser user = Env.getUser();
			if(user!=null){
				prLine.setXzCompany(user.getPhone());//公司
				prLine.setXzDepartment(user.getDepartment());//部门
			}
		} catch (Exception e) {
			logger.error("Error at POLineProperties : createAdObject()" + e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return prLine;
	}

	private void setLineTotal() {
		RequisitionLine rl = (RequisitionLine) getAdObject();
		if (rl.getQty() == null || rl.getUnitPrice() == null) {
			rl.setLineTotal(BigDecimal.ZERO);
		} else {
			rl.setLineTotal(rl.getQty().multiply(rl.getUnitPrice()));
		}
	}

	private IValueChangeListener getMaterialChangedListener() {
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				if (sender instanceof SearchField) {
					refreshUomId(newValue); //改变单位
					refreshPackageSpec(sender, newValue);//改变包装规格
					refreshVendor(newValue);
					refreshPurchase(newValue);
				}
			}
		};
	};
	
	protected void refreshPurchase(Object newValue) {
		//刷新采购员
		IField ifieldPurchaser = getIField(FieldName_Purchaser);
		
		if (ifieldPurchaser != null && ifieldPurchaser instanceof TextField) {
			TextField rtf = (TextField) ifieldPurchaser;
			Material material = (Material) newValue;
			try {
				if(material != null && material.getObjectRrn() != null){
					VDMManager vdmManager = Framework.getService(VDMManager.class);
					VendorMaterial vendorMaterial = vdmManager.getPrimaryVendor(material.getObjectRrn());
					if (vendorMaterial != null ) {
						rtf.setValue(vendorMaterial.getPurchaser());
					} else {
						rtf.setValue(null);
					}
					rtf.refresh();
				}
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		}
	}

	protected void refreshVendor(Object newValue) {
		IField ifieldVendor = getIField(FieldName_Vendor);

		if (ifieldVendor instanceof RefTableField) {
			RefTableField rtf = (RefTableField) ifieldVendor;
			Material material = (Material) newValue;
			try {
				if(material != null && material.getObjectRrn() != null){
					VDMManager vdmManager = Framework.getService(VDMManager.class);
					VendorMaterial vendorMaterial = vdmManager.getPrimaryVendor(material.getObjectRrn());
					if (vendorMaterial != null ) {
						rtf.setValue(vendorMaterial.getVendorRrn());
					} else {
						rtf.setValue(null);
					}
					rtf.refresh();
				}
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		}
	}

	private IValueChangeListener getVendorChangedListener() {
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				IField ifieldUnitPrice = getIField(FieldName_UnitPrice);
				IField ifieldDateEnd = getIField(FieldName_DateEnd);
				IField ifieldQtyEconomicSize = getIField(FieldName_QtyEconomicSize);
				IField ifieldPurchaser = getIField(FieldName_Purchaser);
				
				RefTableField refField = (RefTableField) sender;
				VendorMaterial vm = (VendorMaterial) refField.getData();

				if (ifieldUnitPrice instanceof TextField) {
					TextField tf = (TextField) ifieldUnitPrice;
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
				
				if (ifieldQtyEconomicSize instanceof TextField){
					TextField tf = (TextField)ifieldQtyEconomicSize;
					if (vm != null && vm.getLeastQuantity() != null) {
						tf.setValue(vm.getLeastQuantity().toString());
					} else
						tf.setValue("");
					tf.refresh();
				}
				
				if (ifieldPurchaser instanceof TextField) {//根据供应商带出采购员
					TextField purchaserField = (TextField) ifieldPurchaser;
					if (vm != null && vm.getPurchaser() != null) {
						purchaserField.setValue(vm.getPurchaser());
					}
					purchaserField.refresh();
				}
				refreshPackageSpec(sender, newValue);
			}
		};
	};

	private void refreshRefTableField(String value, RefTableField rtf) {
		ADRefTable refTable = rtf.getRefTable();
		String whereClause = " materialRrn = " + value + " ";
		List<ADBase> list = null;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
			list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), whereClause, refTable
					.getOrderByClause());
			if (!((ADField) rtf.getADField()).getIsMandatory()) {
				String className = adTable.getModelClass();
				list.add((ADBase) Class.forName(className).newInstance());
			}
			rtf.setInput(list);
		} catch (Exception e) {
			logger.error("Error at RequisitionLineProperties : " + e);
		}
		if (list != null) {
			for (ADBase adBase : list) {
				VendorMaterial vm = (VendorMaterial) adBase;
				if (vm.getIsPrimary()) {
					rtf.setValue(vm.getObjectRrn().toString());
					rtf.refresh();
					break;
				}
				rtf.setValue("");
				rtf.refresh();
			}
		}
	}

	private void refreshUomId(Object newValue) {
		IField ifield = getIField(FieldName_UonId);
		if (ifield instanceof RefTableField) {
			RefTableField rtf = (RefTableField) ifield;
			Material mr = (Material) newValue;
			if (mr != null) {
				rtf.setValue(mr.getInventoryUom());
			} else {
				rtf.setValue(null);
			}
			rtf.refresh();
		}
	}

	private IField getIField(String fieldId) {
		for (Form form : getDetailForms()) {
			IField f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return null;
	}
	
	private void setRefPrice(boolean enabled) {
		IField lowest = getIField(FieldName_PriceLowest);
		IField average = getIField(FieldName_PriceAverage);
		IField last = getIField(FieldName_PriceLast);
		if(lowest instanceof RefTextField)
			((RefTextField)lowest).getTextControl().setEnabled(enabled);
		if(average instanceof RefTextField)
			((RefTextField)average).getTextControl().setEnabled(enabled);
		if(last instanceof RefTextField)
			((RefTextField)last).getTextControl().setEnabled(enabled);
	}

	private void addFieldsListeners() {
		IField f = getIField(FieldName_Material);
		if (f != null)
			f.addValueChangeListener(getMaterialChangedListener());
		f = getIField(FieldName_Vendor);
		if (f != null)
			f.addValueChangeListener(getVendorChangedListener());
		f = getIField(FieldName_Qty);
		if (f != null)
			f.addValueChangeListener(getQtyChangedListener());
	}

	private IValueChangeListener getQtyChangedListener() {
		return new IValueChangeListener(){
			@Override
			public void valueChanged(Object sender, Object newValue) {
				refreshFieldAdvancePayment();
			}			
		};
	}

	protected void refreshFieldAdvancePayment() {
		IField ifieldAdvancePayment = getIField(FieldName_AdvancePayment);
		IField ifieldUnitPrice = getIField(FieldName_UnitPrice);
		IField ifieldQty = getIField(FieldName_Qty);
		IField ifieldAdvanceRatio = getIField(FieldName_AdvanceRatio);
		
		if (ifieldAdvancePayment == null || ifieldUnitPrice == null 
				|| ifieldAdvanceRatio == null || ifieldQty == null){
			return;
		}
		String qty = String.valueOf(ifieldQty.getValue());//Double.parseDouble(String.valueOf(ifieldQty.getValue()));
		String unitPrice = String.valueOf(ifieldUnitPrice.getValue());//Double.parseDouble(String.valueOf(ifieldUnitPrice.getValue()));
		String advanceRatio = String.valueOf(ifieldAdvanceRatio.getValue());//Double.parseDouble(String.valueOf(ifieldUnitPrice.getValue()));
		//validate values of all involved fields
		if(qty == null || unitPrice == null || advanceRatio == null){
			return;
		}
		
		if(isNumberic(qty) && isNumberic(unitPrice) && isNumberic(advanceRatio)){
			BigDecimal bd_UnitPrice = BigDecimal.valueOf(Double.parseDouble(unitPrice));
			BigDecimal bd_Qty = BigDecimal.valueOf(Double.parseDouble(qty));
			BigDecimal bd_AdvanceRatio = BigDecimal.valueOf(Double.parseDouble(advanceRatio));
			double advancePayment = bd_UnitPrice.multiply(bd_Qty)
										.multiply(bd_AdvanceRatio)
											.divide(BigDecimal.valueOf(100.0))
												.setScale(3,BigDecimal.ROUND_HALF_EVEN).doubleValue();//小数位四舍五入，保留3位小数
			ifieldAdvancePayment.setValue(String.valueOf(advancePayment));
			ifieldAdvancePayment.refresh();
		}
		
		if(advanceRatio == null){
			return;
		}
		
	}
	
	public boolean isNumberic(String str){
		String sign = "^[+-]?";
		String in = "[0-9]+";
		String pa = "(\\.[0-9]{0,3})?";
		String regex = sign + in + pa;
		return Pattern.matches(regex, str);
	}

	@Override
	public void setAdObject(ADBase adObject) {
		this.adObject = adObject;
		RequisitionLine prLine = (RequisitionLine) adObject;
		if (prLine != null) {
			setStatusChanged(prLine.getLineStatus());
		} else {
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
		XZRequisitionLineEntityBlock block = (XZRequisitionLineEntityBlock) this.getMasterParent();
		if (!block.isView()) {
			if (Requisition.STATUS_DRAFTED.equals(status)) {
				itemNew.setEnabled(true);
				itemSave.setEnabled(true);
				itemDelete.setEnabled(true);
			} else if (Requisition.STATUS_APPROVED.equals(status)) {
				itemNew.setEnabled(false);
				itemSave.setEnabled(false);
				itemDelete.setEnabled(false);
			} else {
				itemNew.setEnabled(false);
				itemSave.setEnabled(false);
				itemDelete.setEnabled(false);
			}
		} else {
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
		}
		setEnabledItem();
	}
	/**
	 * 当月已经统计过月度采购订单，不允许新建当月的行政领用计划
	 * */
	protected void setEnabledItem() {
		try{
			ADManager adManager = Framework.getService(ADManager.class);
			StringBuffer sb = new StringBuffer();
			Date sysDate = Env.getSysDate();
			sb.append(" mpsId = 'Y' ");
			sb.append(" AND to_char(created,'YYYY-MM') = to_char(TO_DATE('" +
					I18nUtil.formatDate(sysDate) 
					+"', " +"'YYYY-MM-DD'),'YYYY-MM') ");
			List<Requisition> requisitions = adManager.getEntityList(Env.getOrgRrn(),Requisition.class,Integer.MAX_VALUE,
					sb.toString(),"");
			if(requisitions!=null && requisitions.size() >0){
				itemNew.setEnabled(false);
				itemSave.setEnabled(false);
				itemDelete.setEnabled(false);
			}
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	protected void refreshPackageSpec(Object sender, Object newValue) {
		IField ifield = getIField(FieldName_PackageSpec);
		if (ifield instanceof TextField) {
			TextField txtf = (TextField) ifield;

			if(sender != null && sender instanceof RefTableField){
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
			}else if(sender != null && sender instanceof SearchField){

				SearchField mrField = (SearchField) sender;
				Material mater = (Material) mrField.getData();
				
				if(mater != null && mater.getPackageSpec() != null && mater.getPackageSpec().trim().length() > 0){
					txtf.setValue(mater.getPackageSpec());
				}else{
					txtf.setValue("");
				}
			}
			txtf.refresh();
		}
	}
}

package com.graly.erp.internalorder.po;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.InternalOrder;
import com.graly.erp.ppm.model.InternalOrderLine;
import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.erp.product.model.CanaInnerOrder;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.po.POLineBlockDialog;
import com.graly.erp.pur.po.POSection;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.Vendor;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.forms.ParentChildEntityBlock;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

/**
 * 营运：有完成和取消完成的权限
 * 采购：只能通过生成采购订单来完成内部订货单
 * */
public class InternalLinePoEntryBlock extends ParentChildEntityBlock {
	private static final Logger logger = Logger.getLogger(InternalLinePoEntryBlock.class);
	protected ToolItem itemApprove;
//	protected ToolItem itemPPM;//临时计划
	protected ToolItem itemPO;//采购计划
	protected ToolItem itemExport;//导出
	protected ToolItem itemPPM;//完成
	protected ToolItem itemCloseCompleted;//取消完成
	
	protected InternalOrderLine selectPILine;
	protected boolean flag = false;
	
	
	public InternalLinePoEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable, boolean flag){
		super(parentTable, parentObject, whereClause, childTable);
		this.flag = flag;
		//设置tableManager,原因系统框架初始化构造不支持checkbox
		EntityTableManager tableManager = new EntityTableManager(childTable);
		tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION |SWT.BORDER 
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.HIDE_SELECTION);
		setTableManager(tableManager);
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);	
		refresh();
		// 根据parentObject状态设置itemApprove和itemClose按钮是否可用
		setParenObjectStatusChanged();
	}
	
	protected void createParentContent(Composite client) {
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

		for (ADTab tab : parentTable.getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			EntityForm itemForm = new EntityForm(getTabs(), SWT.NONE, tab, mmng);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
	}
	
	protected void createViewAction(StructuredViewer viewer){
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					Object obj = ((StructuredSelection) event.getSelection()).getFirstElement();
					if(obj instanceof InternalOrderLine) {
						selectPILine = (InternalOrderLine)obj;
					} else {
						selectPILine = null;
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemPO(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		section.setTextClient(tBar);
	}

	 
	public void setParenObjectStatusChanged() {
		InternalOrder pi = (InternalOrder)parentObject;
		String status = "";
		if(pi != null && pi.getObjectRrn() != null) {
			status = pi.getDocStatus();			
		}
	}
	
	
	protected void setChildObjectStatusChanged() {
		InternalLinePoProperties page = (InternalLinePoProperties)this.detailsPart.getCurrentPage();
		page.setStatusChanged(((InternalOrder)parentObject).getDocStatus());
	}
	
  
	protected void createToolItemPO(ToolBar tBar) {
		itemPO = new ToolItem(tBar, SWT.PUSH);
		itemPO.setText("生成采购订单");
		itemPO.setImage(SWTResourceCache.getImage("approve"));
		itemPO.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				poAdapter2();
			}
		});
	}
	
	protected void poAdapter2() {
	try {
		form.getMessageManager().removeAllMessages();
		InternalOrderVendorQueryDialog vendorDialog = new InternalOrderVendorQueryDialog(form, UI.getActiveShell());
		if(vendorDialog.open() == Dialog.OK) {
			Vendor vendor = vendorDialog.getVendor();
			ADManager adManager = Framework.getService(ADManager.class);
			vendor = (Vendor) adManager.getEntity(vendor);
			InternalOrder innerOrder = (InternalOrder)parentObject;
			CheckboxTableViewer checkTableViewer = (CheckboxTableViewer) this.viewer;
			Object[] checkObjects = checkTableViewer.getCheckedElements();//选中的PILine
			List<InternalOrderLine> piLines = new ArrayList<InternalOrderLine>();
			List<PurchaseOrderLine> poLines = new ArrayList<PurchaseOrderLine>();
			if(checkObjects.length<1){
				UI.showError("请选中PI在进行操作");
				return;
			}
			PurchaseOrder po = new PurchaseOrder();
			po.setDeliveryAddress(POSection.DELIVERY_ADDRESS.get(Env.getOrgRrn()));
			po.setOrgRrn(Env.getOrgRrn());
			po.setPaymentRule11("Y");//默认开具发票
			po.setInvoiceType(PurchaseOrder.INVOICE_TYPE_REGULAR);//发票类型默认为普通发票
			po.setVendor(vendor);
			po.setVendorRrn(vendor.getObjectRrn());
			po.setPiId(innerOrder.getPiNo());
			po.setInternalOrderId(innerOrder.getOrderId());
			po.setWarehouseRrn(151043L);
			po.setWarehouseId("环保-良品");
			setValue(po,vendor);
			long i =0;
			for(Object object : checkObjects){
				InternalOrderLine piLine = (InternalOrderLine) object;
				piLines.add(piLine);
				
				if(!InternalOrderLine.LINESTATUS_APPROVED.equals(piLine.getLineStatus())){
					UI.showError("只有审核状态的PI才能转PO");
					return;
				}
				
				PurchaseOrderLine poLine = new PurchaseOrderLine();
				poLine.setIsActive(true);
				poLine.setOrgRrn(Env.getOrgRrn());
				poLine.setLineNo(i+10);
				poLine.setMaterialRrn(piLine.getMaterialRrn());
				poLine.setQty(piLine.getQty());
				VDMManager vdmManager = Framework.getService(VDMManager.class);
//				VendorMaterial vm = (VendorMaterial) vdmManager.getPrimaryVendor(poLine.getMaterialRrn());
				VendorMaterial vm = vdmManager.getVendorMaterial(vendor.getObjectRrn(), poLine.getMaterialRrn());
				if(vm ==null || vm.getObjectRrn() ==null){
					UI.showError("不存在物料的供应商");
					return;
				}
				if(!vm.getVendorId().equals(vendor.getVendorId())){
					UI.showError("物料:"+piLine.getMaterialId()+"的供应商与选择的供应商不一致");
					return;
				}
				//设置采购订单的采购人员
				if(po.getPurchaser() ==null || "".equals(po.getPurchaser())){
					po.setPurchaser(vm.getPurchaser());
				}
				
				//设置单价
				if (vm.getLastPrice() != null) {
					poLine.setUnitPrice(vm.getLastPrice());// 带出上次价格
				} else if (vm.getReferencedPrice() != null) {
					poLine.setUnitPrice(vm.getReferencedPrice());
				} else {
					poLine.setUnitPrice(BigDecimal.ZERO);
				}
				//收货时间
				if (vm != null && vm.getLeadTime() != null) {
					BASManager basManager;
					try {
						basManager = Framework.getService(BASManager.class);
						BusinessCalendar prCalendar = basManager.getCalendarByDay(Env.getOrgRrn(), BusinessCalendar.CALENDAR_PURCHASE);
						Date now = Env.getSysDate();
						Date dateStart = prCalendar.findStartOfNextDay(now);
						int leadTime = Integer.parseInt(vm.getLeadTime().toString());
						Date dateEnd = prCalendar.addDay(dateStart, leadTime);
						poLine.setDateEnd(dateEnd);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else{
					poLine.setDateEnd(null);
				}
				//单位
				if (vm != null) {
					poLine.setUomId(vm.getMaterial().getInventoryUom());
				} else {
					poLine.setUomId(null);
				}
				//设置行总价
				if (poLine.getQty() == null || poLine.getUnitPrice() == null) {
					poLine.setLineTotal(BigDecimal.ZERO);
				} else {
					poLine.setLineTotal(poLine.getQty().multiply(poLine.getUnitPrice()));
				}
				
				poLines.add(poLine);
			}
			
			if(poLines.size()<1){
				UI.showError("请选中PI在进行操作");
				return;
			}
			PURManager purManager = Framework.getService(PURManager.class);
			po = purManager.savePOLine(po, poLines, Env.getUserRrn());
			po = (PurchaseOrder) adManager.getEntity(po);
			
			for(Object object : checkObjects){
				InternalOrderLine piLine = (InternalOrderLine) object;
				piLine = (InternalOrderLine) adManager.getEntity(piLine);
				piLine.setLineStatus(InternalOrder.STATUS_COMPLETED);
				adManager.saveEntity(piLine, Env.getUserRrn());
			}
			InternalOrder io = new InternalOrder();
			io = (InternalOrder) adManager.getEntity(innerOrder);
			boolean flag =true;
			for(InternalOrderLine ioline:io.getIoLines()){
				if(!InternalOrderLine.LINESTATUS_COMPLETED.equals(ioline.getLineStatus())){
					flag = false;
				}
			}
			if(flag){
				io.setDocStatus(InternalOrder.STATUS_COMPLETED);
				adManager.saveEntity(io, Env.getUserRrn());
			}
			try {
				if (po != null && po.getObjectRrn() != null) {
					ADTable adTable = getADTableOfPOLine();
					po = (PurchaseOrder)adManager.getEntity(po);
					String whereClause = (" poRrn = '" + po.getObjectRrn().toString() + "' ");
					POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), getADTableOfPO(), whereClause, po,
							adTable);
					if (cd.open() == Dialog.CANCEL) {
					}
				}
				refresh();
			} catch(Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				logger.error("Error at POSection : editAdapter() " + e);
			}
		}
	 
	}catch (Exception e) {
		ExceptionHandlerManager.asyncHandleException(e);
		return;
	}
	}
	
	protected ADTable getADTableOfPOLine() {
		try {
			ADTable adTable = null;
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, "PURPurchaseOrderLine");
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	protected ADTable getADTableOfPO() {
		try {
			ADTable adTable = null;
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, "PURPurchaseOrder");
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	
	
	 
	 


	/* 在其对应的properties中调用此方法, 会根据parentObject的状态来初始化properties按钮是否可用*/
	public boolean isEnableByParentObject() {
//		MovementOut out = (MovementOut)this.getParentObject();
//		if(out == null) {
//			return false;
//		}
//		String status = out.getDocStatus();
//		if(Requisition.STATUS_CLOSED.equals(status)
//				|| Requisition.STATUS_APPROVED.equals(status)
//				|| Requisition.STATUS_COMPLETED.equals(status)
//				|| Requisition.STATUS_INVALID.equals(status)
//				|| flag) {
//			return false;
//		}
		return true;
	}

	public boolean isViewOnly() {
		return flag;
	}
	
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class klass = Class.forName(table.getModelClass());
			detailsPart.registerPage(klass, new InternalLinePoProperties(this, table, getParentObject(), flag));
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}
	
	public void setValue(PurchaseOrder po, Vendor vendor) {
		String paymentRule1 = vendor.getPaymentRule1();
		String paymentRule2 = vendor.getPaymentRule2();
		String paymentRule3 = vendor.getPaymentRule3();
		String paymentRule4 = vendor.getPaymentRule4();
		String paymentRule5 = vendor.getPaymentRule5();
		String paymentRule6 = vendor.getPaymentRule6();
		String paymentRule7 = vendor.getPaymentRule7();
		String paymentRule8 = vendor.getPaymentRule8();
		String paymentRule9 = vendor.getPaymentRule9();
		String paymentRule10 = vendor.getPaymentRule10();
		String paymentRule11 = vendor.getPaymentRule11();
		String paymentRule12 = vendor.getPaymentRule12();
		String paymentRule13 = vendor.getPaymentRule13();
		String paymentRule14 = vendor.getPaymentRule14();
		String paymentRule15 = vendor.getPaymentRule15();
		String paymentRule16 = vendor.getPaymentRule16();

		po.setPaymentRule1(paymentRule1);
		po.setPaymentRule2(paymentRule2);
		po.setPaymentRule3(paymentRule3);
		po.setPaymentRule4(paymentRule4);
		po.setPaymentRule5(paymentRule5);
		po.setPaymentRule6(paymentRule6);
		po.setPaymentRule7(paymentRule7);
		po.setPaymentRule8(paymentRule8);
		po.setPaymentRule9(paymentRule9);
		po.setPaymentRule10(paymentRule10);
		po.setPaymentRule11(paymentRule11);
		po.setPaymentRule12(paymentRule12);
		po.setPaymentRule13(paymentRule13);
		po.setPaymentRule14(paymentRule14);
		po.setPaymentRule15(paymentRule15);
		po.setPaymentRule16(paymentRule16);
		po.setDeliveryRule(vendor.getShipmentCode());// 带出送货方式

		// 带出是否开具发票、以及发票类型，若为增值税发票则带出增值税率
		if (vendor.getIsIssueInvoice2() != null
				&& vendor.getIsIssueInvoice2().trim().length() > 0) {
			po.setPaymentRule11(vendor.getIsIssueInvoice2());
		} else {
			po.setPaymentRule11(vendor.getIsIssueInvoice() == true ? "Y" : "N");
		}
		if (vendor.getInvoiceType2() != null
				&& vendor.getInvoiceType2().trim().length() > 0) {
			po.setInvoiceType(vendor.getInvoiceType2());
		} else {
			po.setInvoiceType(vendor.getInvoiceType());
		}
		if (vendor.getVatRate2() != null) {
			BigDecimal vatRate = new BigDecimal(String.valueOf(vendor
					.getVatRate2().doubleValue()));
			po.setVatRate(vatRate);
		} else {
			BigDecimal vatRate = new BigDecimal(String.valueOf(vendor
					.getVatRate2().doubleValue()));
			po.setVatRate(vatRate);
		}
	}
}

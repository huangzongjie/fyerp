package com.graly.erp.pur.po;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.product.client.CANAManager;
import com.graly.erp.product.model.CanaProduct;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.pur.request.ColorEntityTableManager;
import com.graly.erp.pur.request.RequisitionLineBlockDialog;
import com.graly.erp.vdm.model.Vendor;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.erp.pur.po.BrowserDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.ParentChildEntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
public class POLineEntityBlock extends ParentChildEntityBlock {

	private static final Logger logger = Logger.getLogger(POLineEntityBlock.class);
	public static final String ID_VENDORRrn = "vendorRrn";
	public static final String ID_PURCHASER = "purchaser";
	public static final String ID_DELIVERY_RULE = "deliveryRule";
	
	public static final String PAYMENT_RULE1 = "paymentRule1"; // 支票
	public static final String PAYMENT_RULE2 = "paymentRule2"; // 现金
	public static final String PAYMENT_RULE3 = "paymentRule3"; // 汇款
	
	
	public static final String PAYMENT_RULE4 = "paymentRule4"; // 预付款
	public static final String PAYMENT_RULE5 = "paymentRule5"; // 月结
	public static final String PAYMENT_RULE6 = "paymentRule6"; // 验收后
	public static final String PAYMENT_RULE7 = "paymentRule7"; // ___日支付
	
	
	public static final String PAYMENT_RULE8 = "paymentRule8";
	public static final String PAYMENT_RULE9 = "paymentRule9";
	public static final String PAYMENT_RULE10 = "paymentRule10";
	public static final String PAYMENT_RULE11 = "paymentRule11";
	public static final String PAYMENT_RULE12 = "paymentRule12";
	public static final String PAYMENT_RULE13 = "paymentRule13";
	public static final String PAYMENT_RULE14 = "paymentRule14";
	public static final String PAYMENT_RULE15 = "paymentRule15";
	public static final String PAYMENT_RULE16 = "paymentRule16";
	
	public static final String KEY_PO_VIEW_CONTRACT = "PUR.Po.ViewContract";

	
		
	public static final String IsIssueInvoice = "paymentRule11"; // 是否开具发票
	public static final String InvoiceType = "invoiceType"; // 发票类型
	public static final String VatRate = "vatRate"; // 增值税率
	public static final String FIELD_NAME_UNITPRICE = "unitPrice"; // 单价
	public static final String FIELD_NAME_LINETOTAL = "lineTotal"; // 行总价
	
	public static final String FINANCIAL_FIELD_ID = "financialNote";//财务备注栏位ID
	public static final String IS_PAYMENT_FULL= "ispaymentFull";
	private static String	AUTHORITY_UNITPRICE	= "PUR.PoLine.UnitPrice";
	
	protected static boolean flag;
	protected ToolItem itemPreApprove;
	protected ToolItem itemCancelPreApprove;
	protected ToolItem itemApprove;
	protected ToolItem itemCancelApprove;
	protected ToolItem itemFinancialNote;//财务备注
	protected ToolItem itemLargeAmountApprove;//大金额审核(目前指金额总数超过1万元的)
	protected ToolItem itemClose;
	protected ToolItem itemPreview;
	protected ToolItem itemApproveUnitPrice;
	protected ToolItem relationShipItem;
	protected ToolItem itemSendEmail;//发送邮件
	protected ToolItem itemSendPDFEmail;
	protected ToolItem itemSavePDF;
	
	protected ToolItem itemViewContract;
	protected ToolItem itemLotPrint;
	protected static String URL = "http://192.168.0.235:81/system/compact_erp_list.jsp?vender_code=";
	//采购合同
	
	private Menu menu;
	protected String where;
	private PurchaseOrderLine selectedPOline;
	protected ADTable adTable;
	private CANAManager canaManager;
	private WipManager wipManager;
	
	public POLineEntityBlock(ADTable parentTable, Object parentObject, String whereClause, ADTable childTable, boolean flag) {
		super(parentTable, parentObject, whereClause, childTable);
		if(!Env.getAuthority().contains(AUTHORITY_UNITPRICE)){//如果没有价格权限，则无法看到单价和行总价
			for(ADField f : childTable.getFields()){
				if(FIELD_NAME_UNITPRICE.equals(f.getName())){
					f.setIsMain(false);
				}else if(FIELD_NAME_LINETOTAL.equals(f.getName())){
					f.setIsMain(false);
				}
			}
		}
		this.setTableManager(new ColorEntityTableManager(childTable));
		this.flag = flag;
	}

	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
		((ColorEntityTableManager)this.getTableManager()).setPoLineBlock(this);
		// 根据parentObject状态设置itemApprove和itemClose按钮是否可用
		setParentObjectStatusChanged();
		setItemStatus();
		compareWithRelValue();
		compareWithCrmPrice2();
		compareWithQtyKefenpei();
	}

	protected void createViewAction(StructuredViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
					setItemStatus();
					refresh();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof PurchaseOrderLine) {
			selectedPOline = (PurchaseOrderLine) obj;
		} else {
			selectedPOline = null;
		}
	}

	private void setItemStatus() {
//		if (viewer != null && viewer instanceof TableViewer) {
//			Table table = ((TableViewer) viewer).getTable();
//			if (table.getSelection().length > 0) {
//				TableItem ti = table.getSelection()[0];
//				Object obj = ti.getData();
//				if (obj instanceof PurchaseOrderLine) {
//					PurchaseOrderLine pol = (PurchaseOrderLine) obj;
//					if (pol.getRefUnitPrice() != null && pol.getUnitPrice().doubleValue() > pol.getRefUnitPrice().doubleValue()) {
//						itemApproveUnitPrice.setEnabled(true);
//						return;
//					}
//				}
//			}
//		}
//		itemApproveUnitPrice.setEnabled(false);
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
		getTabs().setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() }, new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : parentTable.getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			POForm itemForm = new POForm(getTabs(), SWT.NONE, parentObject, tab, mmng);
			this.getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		// 根据供应商带出送货方式等相关信息
		addListenterToVendor();		
	}

	@Override
	public void createToolBar(Section section) {
		final ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSavePDF(tBar);
		createToolItemSendPDFEmail(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSendEmail(tBar);
		createToolItemPrintLot(tBar);//批次打印
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRelationShip(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreApprove(tBar);
		createToolItemCancelPreApprove(tBar);
		createToolItemApprove(tBar);
		createToolItemLargeAmountApprove(tBar);
		createToolItemCancelApprove(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemFinancialNote(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApproveUnitPrice(tBar);
		
		createToolitemViewContract(tBar);
		
		createToolItemPreview(tBar);	
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemClose(tBar);
		
		createMenu(tBar);
		section.setTextClient(tBar);
	}
	
	private void createToolItemSavePDF(ToolBar bar){
		itemSavePDF = new ToolItem(bar, SWT.PUSH);
		itemSavePDF.setText("保存PDF");
		itemSavePDF.setImage(SWTResourceCache.getImage("save"));
		itemSavePDF.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				savePDFAdapter();
			}
		});
		
	}
	
	private void createToolItemSendPDFEmail(ToolBar bar){
		itemSendPDFEmail = new ToolItem(bar, SWT.PUSH);
		itemSendPDFEmail.setText("发送PDF邮件");
		itemSendPDFEmail.setImage(SWTResourceCache.getImage("email"));
		itemSendPDFEmail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sendPDFEmailAdapter();
			}
		});
		
	}
	
	private void createToolItemSendEmail(ToolBar bar){
		itemSendEmail = new ToolItem(bar, SWT.PUSH);
		itemSendEmail.setText("发送邮件");
		itemSendEmail.setImage(SWTResourceCache.getImage("email"));
		itemSendEmail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sendEmailAdapter();
			}
		});
		
	}
	
	private void createToolItemPrintLot(ToolBar bar){
		itemLotPrint = new ToolItem(bar, SWT.PUSH);
		itemLotPrint.setText("条码打印");
		itemLotPrint.setImage(SWTResourceCache.getImage("print"));
		itemLotPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				lotPrintAdapter();
			}
		});
		
	}
	
	private void createToolItemFinancialNote(ToolBar bar) {
		itemFinancialNote = new AuthorityToolItem(bar, SWT.PUSH, Constants.KEY_PO_FINANCIALNOTE);
		itemFinancialNote.setText(Message.getString("pur.financial_note"));
		itemFinancialNote.setImage(SWTResourceCache.getImage("note"));
		itemFinancialNote.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				noteAdapter();
			}
		});
	}

	protected void noteAdapter() {
		try {
			IField field = getIField(FINANCIAL_FIELD_ID);
			if(field == null) return;
			Object val = field.getValue();
			PURManager purManager = Framework.getService(PURManager.class);
			PurchaseOrder po = (PurchaseOrder) getParentObject();
			isPaymentFull(po);
			if(val != null){
				parentObject = purManager.updatePOFinancialNote(po, String.valueOf(val), Env.getUserRrn());
			}else{
				parentObject = purManager.updatePOFinancialNote(po, null, Env.getUserRrn());
			}
			
			UI.showInfo(Message.getString("common.save_successed"));
			refresh();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			ExceptionHandlerManager.asyncHandleException(e);
		} 
	}
	
	protected void isPaymentFull(PurchaseOrder purchaseOrder) {
		IField field = getIField(IS_PAYMENT_FULL);
		if(field == null) return;
		Boolean val = (Boolean)field.getValue();
		purchaseOrder.setIspaymentFull(val);
		
	
	}

	protected void createToolItemRelationShip(final ToolBar tBar) {
		relationShipItem = new ToolItem(tBar, SWT.DROP_DOWN);
		relationShipItem.setText(Message.getString("inv.relationship"));
		relationShipItem.setImage(SWTResourceCache.getImage("search"));
		relationShipItem.setToolTipText(Message.getString("inv.relationship_tip"));
		relationShipItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (event.detail == SWT.ARROW) {
					Rectangle bounds = relationShipItem.getBounds();
					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu.setLocation(point);
					menu.setVisible(true);
				}
			}
		});
	}

	/* 创建dropDownMenu及监听事件 */
	private void createMenu(ToolBar toolBar) {
		menu = new Menu(UI.getActiveShell(), SWT.POP_UP);
		MenuItem menuItemPO = new MenuItem(menu, SWT.PUSH);
		menuItemPO.setText(Message.getString("inv.relationship_pr"));
		new MenuItem(menu, SWT.SEPARATOR);
		new MenuItem(menu, SWT.PUSH).setText(Message.getString("common.cancel"));

		menuItemPO.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e1) {
				menuPrAdapter();
			}
		});
	}

	protected void createToolItemPreview(ToolBar tBar) {
		itemPreview = new ToolItem(tBar, SWT.PUSH);
		itemPreview.setText(Message.getString("common.print"));
		itemPreview.setImage(SWTResourceCache.getImage("print"));
		itemPreview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewAdapter();
			}
		});
	}

	protected void createToolItemApproveUnitPrice(ToolBar tBar) {
		itemApproveUnitPrice = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_PRICE_APPROVED);
		itemApproveUnitPrice.setText(Message.getString("common.approve_unitprice"));
		itemApproveUnitPrice.setImage(SWTResourceCache.getImage("preview"));
		itemApproveUnitPrice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				unitPriceApproveAdapter();
			}
		});
	}
	
	
	private void createToolitemViewContract(ToolBar tBar) {
		itemViewContract = new AuthorityToolItem(tBar, SWT.PUSH, KEY_PO_VIEW_CONTRACT);	
		itemViewContract.setText(Message.getString("common.view_contract"));
		itemViewContract.setImage(SWTResourceCache.getImage("viewcontract"));
		itemViewContract.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				itemViewContractAdapter();
			}
		});
	}
	
	
	

	protected void createToolItemPreApprove(ToolBar tBar) {
		itemPreApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_PREAPPROVED);
		itemPreApprove.setText(Message.getString("common.preapprove"));
		itemPreApprove.setImage(SWTResourceCache.getImage("approve"));
		itemPreApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				preApproveAdapter();
			}
		});
	}
	
	
	protected void createToolItemCancelPreApprove(ToolBar tBar) {
		itemCancelPreApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_CANCELPREAPPROVED);
		itemCancelPreApprove.setText(String.format(Message.getString("common.cancel_do"), Message.getString("common.preapprove")));
		itemCancelPreApprove.setImage(SWTResourceCache.getImage("cancel"));
		itemCancelPreApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cancelPreApproveAdapter();
			}
		});
	}
	
	protected void preApproveAdapter() {//预审
		try {
			if (!validatePayRules()){//预审的时候检验付款方式
				return;
			}
			form.getMessageManager().removeAllMessages();
			PurchaseOrder po = (PurchaseOrder) parentObject;
			if (po != null && po.getObjectRrn() != null) {
				PURManager purManager = Framework.getService(PURManager.class);
				parentObject = purManager.preApprovePO(po, Env.getUserRrn());
				UI.showInfo(Message.getString("common.approve_successed"));

				setParentObjectStatusChanged();
				setChildObjectStatusChanged();
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void cancelPreApproveAdapter(){
		try {
			form.getMessageManager().removeAllMessages();
			PurchaseOrder po = (PurchaseOrder) parentObject;
			if (po != null && po.getObjectRrn() != null) {
				PURManager purManager = Framework.getService(PURManager.class);
				parentObject = purManager.cancelPreApprovedPO(po, Env.getUserRrn());
				UI.showInfo(Message.getString("common.operation_successful"));

				setParentObjectStatusChanged();
				setChildObjectStatusChanged();
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}
	
	
	protected void createToolItemCancelApprove(ToolBar tBar) {
		itemCancelApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_CANCELAPPROVED);
		itemCancelApprove.setText(String.format(Message.getString("common.cancel_do"), Message.getString("common.approve")));
		itemCancelApprove.setImage(SWTResourceCache.getImage("cancel"));
		itemCancelApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cancelApproveAdapter();
			}
		});
	}
	
	protected void createToolItemLargeAmountApprove(ToolBar tBar) {
		itemLargeAmountApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_LARGEAMOUNT_APPROVED);
		itemLargeAmountApprove.setText(Message.getString("common.large_amount_approve"));
		itemLargeAmountApprove.setImage(SWTResourceCache.getImage("approve"));
		itemLargeAmountApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}

	protected void createToolItemClose(ToolBar tBar) {
		itemClose = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_REVOKE);
		itemClose.setText(Message.getString("common.close"));
		itemClose.setImage(SWTResourceCache.getImage("close"));
		itemClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				closeAdapter();
			}
		});
	}

	private void menuPrAdapter() {
		if (selectedPOline == null) {
			UI.showWarning(Message.getString("inv.poline_select_is_null"));
			return;
		}
		try {
			if (selectedPOline != null) {
				PurchaseOrderLine poLine = (PurchaseOrderLine) selectedPOline;
				if (poLine.getRequisitionLineRrn() != null) {
					ADManager entityManager = Framework.getService(ADManager.class);
					adTable = entityManager.getADTable(0L, "PURRequisitionLine");
					adTable = entityManager.getADTableDeep(adTable.getObjectRrn());

					String where = " objectRrn = '" + poLine.getRequisitionLineRrn() + "' ";
					List<RequisitionLine> prLines = entityManager.getEntityList(Env.getOrgRrn(), RequisitionLine.class, 2, where, "");
					if (prLines.size() == 0) {
						UI.showError(Message.getString("wip.prLine_is_deleted"));
						return;
					}
					if (prLines.size() > 0) {
						RequisitionLine prLine = prLines.get(0);
						Requisition pr = new Requisition();
						pr.setObjectRrn(prLine.getRequisitionRrn());
						String whereClause = (" requisitionRrn = " + prLine.getRequisitionRrn() + " AND objectRrn = "
								+ prLine.getObjectRrn() + " ");
						RequisitionLineBlockDialog cd = new RequisitionLineBlockDialog(UI.getActiveShell(), adTable, whereClause, pr,true);
						if (cd.open() == Dialog.CANCEL) {
						}
					}
				} else {
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
			}
		} catch (Exception e) {
			logger.error("POLineEntityBlock : prViewAdapter()", e);
		}
	}
	
	protected void viewVendorHisAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			String report = "po_vendor_chart.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);

			HashMap<String, String> userParams = new HashMap<String, String>();

			Table table = ((TableViewer) viewer).getTable();
			TableItem[] selects = table.getSelection();
			TableItem ti = selects[0];
			PurchaseOrderLine pol = (PurchaseOrderLine) ti.getData();
			if(pol == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long materialRrn = pol.getMaterialRrn();
			userParams.put("MATERIAL_RRN", String.valueOf(materialRrn));

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void unitPriceApproveAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			String report = "po_unitprice_chart.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);

			HashMap<String, String> userParams = new HashMap<String, String>();

			PurchaseOrderLine pol = selectedPOline;
			if(pol == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = pol.getObjectRrn();
//			userParams.put("POL_OBJECT_RRN", String.valueOf(objectRrn));
//			userParams.put("POL_USER_RRN", String.valueOf(Env.getUserRrn()));
//
//			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
//			dialog.open();
			PoUnitPriceChartDialog printQueryDialog=null;
			if (printQueryDialog != null) {
				printQueryDialog.setReportName(report);
				printQueryDialog.setVisible(true);
			} else {
				String TABLE_NAME = "POUnitpriceChart";
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable printADTable = adManager.getADTable(Env.getOrgRrn(), TABLE_NAME);
				EntityTableManager tableManager = new EntityTableManager(printADTable);
				printQueryDialog =  new PoUnitPriceChartDialog(UI.getActiveShell(), tableManager, this,objectRrn);
				printQueryDialog.setReportName(report);
				printQueryDialog.open();
			}
			
			
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	

	protected void itemViewContractAdapter() {
		PurchaseOrder po = (PurchaseOrder) getParentObject();
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), URL + po.getVendorId());
		bd.open();
	}

	protected void previewAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			
			//保存打印次数
			PurchaseOrder po = (PurchaseOrder) getParentObject();
			Long time = po.getPrintTime();
			if(time == null){
				po.setPrintTime(1L);
			}else{
				po.setPrintTime(time + 1L);
			}
			ADManager manager = Framework.getService(ADManager.class);
			parentObject = manager.saveEntity(po, Env.getUserRrn());
			
			String report = "po_report.rptdesign";
			
			List<PurchaseOrderLine> poLines = po.getPoLines();
			for(PurchaseOrderLine line : poLines){
				if(line != null && line.getMaterial() != null && Constants.KEY_MATERIAL_CATEGORY2.equals(line.getMaterial().getMaterialCategory2())){
					report = "po_report2.rptdesign";//打印委外加工单
					break;
				}
			}
			//开能有barCode的用barCode模板,没有的不用。如果是委外的不用barcode
			if(Env.getOrgRrn() == 139420L || Env.getOrgRrn()==2501932L || Env.getOrgRrn()==63506125L){
				if(report.equals("po_report.rptdesign")){
					for(PurchaseOrderLine line : poLines){
						if(line != null && line.getBarCode()!=null){
							report = "po_report_barcode.rptdesign";//
							break;
						}
					}
				}
			}
			if(Env.getOrgRrn() == 41673024L){
				if(report.equals("po_report.rptdesign")){
					for(PurchaseOrderLine line : poLines){
						if(line != null && line.getBarCode()!=null){
							report = "po_report_barcode_lt.rptdesign";//
							break;
						}
					}
				}
			}
			
			if(Env.getOrgRrn() == 12644730L){
				if(report.equals("po_report.rptdesign")){
					for(PurchaseOrderLine line : poLines){
						if(line != null && line.getBarCode()!=null){
							report = "po_report_barcode_bt.rptdesign";//
							break;
						}
					}
				}
			}
			if(Env.getOrgRrn() == 68088906L){
				report = "po_report_YN.rptdesign";//
			}

			if(Env.getOrgRrn() == 69573429L){
				report = "po_report_YNZZ.rptdesign";//
			}
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			if(po == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = po.getObjectRrn();
			userParams.put("PO_OBJECT_RRN", String.valueOf(objectRrn));

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void setParentObjectStatusChanged() {
		PurchaseOrder po = (PurchaseOrder) getParentObject();
		String status = "";
		Boolean isPreApproved = false;

		if (po != null && po.getObjectRrn() != null) {
			status = po.getDocStatus();
			isPreApproved = po.getIsPreApproved();
		}
		
		if (PurchaseOrder.STATUS_APPROVED.equals(status)) {
			itemPreApprove.setEnabled(false);
			itemCancelPreApprove.setEnabled(false);
			
			itemApprove.setEnabled(false);
			itemCancelApprove.setEnabled(true);
			
			itemLargeAmountApprove.setEnabled(false);
			itemClose.setEnabled(true);
		} else if (PurchaseOrder.STATUS_DRAFTED.equals(status)) {
			if(isPreApproved){
				itemPreApprove.setEnabled(false);
				itemCancelPreApprove.setEnabled(true);
				
				itemApprove.setEnabled(true);
				itemCancelApprove.setEnabled(false);
				
				itemLargeAmountApprove.setEnabled(true);
				itemClose.setEnabled(false);
			}else{
				itemPreApprove.setEnabled(true);
				itemCancelPreApprove.setEnabled(false);
				
				itemApprove.setEnabled(false);
				itemCancelApprove.setEnabled(false);
				
				itemLargeAmountApprove.setEnabled(false);
				itemClose.setEnabled(false);
			}
		} else if (PurchaseOrder.STATUS_CLOSED.equals(status)) {
			itemPreApprove.setEnabled(false);
			itemCancelPreApprove.setEnabled(false);
			
			itemApprove.setEnabled(false);
			itemCancelApprove.setEnabled(false);
			
			itemLargeAmountApprove.setEnabled(false);
			itemClose.setEnabled(false);
		} else {
			itemPreApprove.setEnabled(false);
			itemCancelPreApprove.setEnabled(false);
			
			itemApprove.setEnabled(false);
			itemCancelApprove.setEnabled(false);
			
			itemLargeAmountApprove.setEnabled(false);
			itemClose.setEnabled(false);
		}
		
		if(po != null){//判断是否万元审
			if(po.getTotal().compareTo(BigDecimal.valueOf(10000)) >= 0){
				itemApprove.setEnabled(false);
			}else{
				itemLargeAmountApprove.setEnabled(false);
			}
		}
		
		if(po != null && po.getObjectRrn() != null){
			itemFinancialNote.setEnabled(true);
		}else{
			itemFinancialNote.setEnabled(false);
		}
		
		IField f = getIField("financialNote");
		f.setEnabled(itemFinancialNote.getEnabled());
		
		// 根据po是否已保存判断是否可以预览
		if (po != null && po.getObjectRrn() != null) {
			itemPreview.setEnabled(true);
		} else {
			itemPreview.setEnabled(false);
		}
		// flag用于关联查看时控制按钮显示
		if (flag) {
			itemClose.setEnabled(false);
			itemPreview.setEnabled(false);
			itemApprove.setEnabled(false);
			itemLargeAmountApprove.setEnabled(false);
		}
	}

	/*
	 * 刷新properties中line对象时,即使在Block中选中了Line,也将properties中line对象置为空
	 * 若要对line进行操作,则必须重新选中Block中line行
	 */
	protected void setChildObjectStatusChanged() {
		POLineProperties page = (POLineProperties) this.detailsPart.getCurrentPage();
		try {
			selectedPOline = null;
			page.setAdObject(null);
			page.refresh();
			page.setStatusChanged(((PurchaseOrder) parentObject).getDocStatus());
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void approveAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			PurchaseOrder po = (PurchaseOrder) parentObject;
			if (po != null && po.getObjectRrn() != null) {
				PURManager pudManager = Framework.getService(PURManager.class);
				parentObject = pudManager.approvePO(po, Env.getUserRrn());
				UI.showInfo(Message.getString("common.approve_successed"));

				setParentObjectStatusChanged();
				setChildObjectStatusChanged();
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void cancelApproveAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			PurchaseOrder po = (PurchaseOrder) parentObject;
			if (po != null && po.getObjectRrn() != null) {
				PURManager pudManager = Framework.getService(PURManager.class);
				parentObject = pudManager.cancelApprovedPO(po, Env.getUserRrn());
				UI.showInfo(Message.getString("common.operation_successful"));
				
				setParentObjectStatusChanged();
				setChildObjectStatusChanged();
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void closeAdapter() {
		try {
			PurchaseOrder purOrder = (PurchaseOrder) parentObject;
			ADManager adManager = Framework.getService(ADManager.class);
			List<Receipt> receipts =adManager.getEntityList(Env.getOrgRrn(),Receipt.class,Integer.MAX_VALUE,"poId ='"+purOrder.getDocId()+"'",null);
			//判断采购订单对应的收货单是否全部为CLOSED
			if(receipts.size()>0){
				boolean receiptIsClosed = true;
				for(Receipt receipt : receipts){
					if(receipt.getDocStatus().equals("CLOSED"))
						continue;
					else
						receiptIsClosed = false;
				}
				
				if(!receiptIsClosed){
					UI.showError("对不起，存在收货单没有被撤销");
					return;
				}
			}
			form.getMessageManager().removeAllMessages();
			if (parentObject != null) {
				if(UI.showConfirm(Message.getString("common.confirm_repeal"))){
					PURManager pudManager = Framework.getService(PURManager.class);
					parentObject = pudManager.closePO((PurchaseOrder) parentObject, Env.getUserRrn());
					UI.showInfo(Message.getString("common.close_successed"));
	
					setParentObjectStatusChanged();
					setChildObjectStatusChanged();
					refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}

	}

	public boolean isEnableByParentObject() {
		PurchaseOrder po = (PurchaseOrder) this.getParentObject();
		if (po == null) {
			return false;
		}
		String status = po.getDocStatus();
		if (Requisition.STATUS_CLOSED.equals(status) || Requisition.STATUS_APPROVED.equals(status)
				|| Requisition.STATUS_COMPLETED.equals(status) || Requisition.STATUS_INVALID.equals(status)) {
			return false;
		}
		return true;
	}

	@Override
	public void refresh() {
		super.refresh();
		compareWithRelValue();
		compareWithCrmPrice2();
		compareWithQtyKefenpei();
		setItemStatus();
	}

	public void compareWithRelValue() {
		if (viewer instanceof TableViewer) {
			TableViewer tViewer = (TableViewer) viewer;
			Table table = tViewer.getTable();
			PurchaseOrder po = (PurchaseOrder) this.getParentObject();

			for (TableItem item : table.getItems()) {
				Object obj = item.getData();
				if (obj instanceof PurchaseOrderLine) {
					PurchaseOrderLine pol = (PurchaseOrderLine) obj;
					if(pol.getFirstProcurement()){//如果是首次采购，用绿色显示
						item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
					}
					//如果定义了参考价格并且实际采购价高于参考价用红色反显
					if (pol.getRefUnitPrice() != null && pol.getUnitPrice().doubleValue() > pol.getRefUnitPrice().doubleValue()) {
						item.setBackground(5,Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					}
					//如果定义了参考供应商并且实际供应商不是该供应商用黄色反显
					if (pol.getRefVendorRrn() != null && po != null && po.getVendorRrn().compareTo(pol.getRefVendorRrn()) != 0) {
						item.setBackground(2,Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
					}
					//如果行总价不为空,并且行总价不等于(订货物*单价)，则行总价显示黄色
					if (pol.getLineTotal()!=null && pol.getLineTotal().compareTo(pol.getQty().multiply(pol.getUnitPrice())) !=0) {
						item.setBackground(6,Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
					}					
				}
			}
		}
	}
	
	public void compareWithCrmPrice2() {
		try {
			if(canaManager == null){
				canaManager = Framework.getService(CANAManager.class);
			}
			if (viewer instanceof TableViewer) {
				TableViewer tViewer = (TableViewer) viewer;
				Table table = tViewer.getTable();
				
				for (TableItem item : table.getItems()) {
					Object obj = item.getData();
					if (obj instanceof PurchaseOrderLine) {
						PurchaseOrderLine pol = (PurchaseOrderLine) obj;
						CanaProduct product = canaManager.getCanaProduct(pol.getMaterialId());
						//采购价与crm中bom不一致 
						if(product!=null && product.getPrice2Low() != null && product.getPrice2Low().compareTo(pol.getUnitPrice()) != 0){
							Display display = Display.getCurrent();
//							item.setBackground(5,Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
							item.setForeground(5,display.getSystemColor(SWT.COLOR_DARK_RED));
							Font font1 = new Font(display, "Tahoma", 10, SWT.BOLD|SWT.ITALIC);
							item.setFont(5,font1);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("POLineEntityBlock : compareWithCrmPrice2", e);
		}
	}
	
	public void compareWithQtyKefenpei() {
//		try {
//			if(wipManager == null){
//				wipManager = Framework.getService(WipManager.class);
//			}
//			
//			if (viewer instanceof TableViewer) {
//				TableViewer tViewer = (TableViewer) viewer;
//				Table table = tViewer.getTable();
//				
//				for (TableItem item : table.getItems()) {
//					Object obj = item.getData();
//					if (obj instanceof PurchaseOrderLine) {
//						PurchaseOrderLine pol = (PurchaseOrderLine) obj;
//						MaterialSum ms = wipManager.getMaterialSum (Env.getOrgRrn(), pol.getMaterialRrn(), false, false);
//						if(ms!=null && ms.getQtyOnHand()!=null&&ms.getQtyAssignable()!=null&&ms.getQtyOnHand().compareTo(ms.getQtyAssignable())<0){
//							item.setBackground(0,Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error("POLineEntityBlock : compareWithQtyKefenpei", e);
//		}
	}
	
	public IField getIField(String fieldId) {
		IField f = null;
		for (Form form : getDetailForms()) {
			f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return f;
	}
	
	private void addListenterToVendor() {
		ValueChangedNotify vcn = new ValueChangedNotify();
		IField vendor = getIField(ID_VENDORRrn);
		if(vendor != null) {
			vendor.addValueChangeListener(vcn);
		}
	}
	
	class ValueChangedNotify implements IValueChangeListener {
		@Override
		public void valueChanged(Object sender, Object newValue) {
			if(sender instanceof SearchField) {
				SearchField sf = (SearchField)sender;
				if(sf.getData() instanceof Vendor) {
					Vendor vendor = (Vendor)sf.getData();
					IField field1 = getIField(PAYMENT_RULE1);
					IField field2 = getIField(PAYMENT_RULE2);
					IField field3 = getIField(PAYMENT_RULE3);
					IField field4 = getIField(PAYMENT_RULE4);
					IField field5 = getIField(PAYMENT_RULE5);
					IField field6 = getIField(PAYMENT_RULE6);
					IField field7 = getIField(PAYMENT_RULE7);
					IField field8 = getIField(PAYMENT_RULE8);
					IField field9 = getIField(PAYMENT_RULE9);
					IField field10 = getIField(PAYMENT_RULE10);
					IField field11 = getIField(PAYMENT_RULE11);
					IField field12 = getIField(PAYMENT_RULE12);
					IField field13 = getIField(PAYMENT_RULE13);
					IField field14 = getIField(PAYMENT_RULE14);
					IField field15 = getIField(PAYMENT_RULE15);
					IField field16 = getIField(PAYMENT_RULE16);
					
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
					 
					if( paymentRule1 != null && paymentRule1.trim().length() >0){
						field1.setValue(paymentRule1);
						field1.refresh();  
					}    
					if( paymentRule2 != null && paymentRule2.trim().length() >0){
						field2.setValue(paymentRule2);
						field2.refresh();  
					}    
					if( paymentRule3 != null && paymentRule3.trim().length() >0){
						field3.setValue(paymentRule3);
						field3.refresh();  
					}    
					if( paymentRule4 != null && paymentRule4.trim().length() >0){
						field4.setValue(paymentRule4);
						field4.refresh();  
					}    
					if( paymentRule5 != null && paymentRule5.trim().length() >0){
						field5.setValue(paymentRule5);
						field5.refresh();  
					}    
					if( paymentRule6 != null && paymentRule6.trim().length() >0){
						field6.setValue(paymentRule6);
						field6.refresh();  
					}    
					if( paymentRule7 != null && paymentRule7.trim().length() >0){
						field7.setValue(paymentRule7);
						field7.refresh();  
					}    
					if( paymentRule8 != null && paymentRule8.trim().length() >0){
						field8.setValue(paymentRule8);
						field8.refresh();  
					}    
					if( paymentRule9 != null && paymentRule9.trim().length() >0){
						field9.setValue(paymentRule9);
						field9.refresh();  
					}    
					if( paymentRule10 != null && paymentRule10.trim().length() >0){
						field10.setValue(paymentRule10);
						field10.refresh();  
					}    
					if( paymentRule11 != null && paymentRule11.trim().length() >0){
						field11.setValue(paymentRule11);
						field11.refresh();  
					}    
					if( paymentRule12 != null && paymentRule12.trim().length() >0){
						field12.setValue(paymentRule12);
						field12.refresh();  
					}    
					if( paymentRule13 != null && paymentRule13.trim().length() >0){
						field13.setValue(paymentRule13);
						field13.refresh();  
					}    
					if( paymentRule14 != null && paymentRule14.trim().length() >0){
						field14.setValue(paymentRule14);
						field14.refresh();  
					}    
					if( paymentRule15 != null && paymentRule15.trim().length() >0){
						field15.setValue(paymentRule15);
						field15.refresh();  
					}   
					if( paymentRule16 != null && paymentRule16.trim().length() >0){
						field16.setValue(paymentRule16);
						field16.refresh();  
					}  					


					
					// 带出送货方式
					IField deliveryRuleField = getIField(ID_DELIVERY_RULE);
					if(deliveryRuleField != null) {
						deliveryRuleField.setValue(vendor.getShipmentCode());
						deliveryRuleField.refresh();
					}
					
					
					//带出是否开具发票、以及发票类型，若为增值税发票则带出增值税率
					IField isII = getIField(IsIssueInvoice);
					IField it = getIField(InvoiceType);
					IField vr = getIField(VatRate);
					if(isII != null) {
						if(vendor.getIsIssueInvoice2() != null && vendor.getIsIssueInvoice2().trim().length() >0){
							isII.setValue(vendor.getIsIssueInvoice2());
						}else{
							isII.setValue(vendor.getIsIssueInvoice());
						}
						isII.refresh();
					}
					if(it != null) {
						if(vendor.getInvoiceType2() != null && vendor.getInvoiceType2().trim().length() >0){
							it.setValue(vendor.getInvoiceType2());
						}else{
							it.setValue(vendor.getInvoiceType());
						}
						it.refresh();
					}
					if(PurchaseOrder.INVOICE_TYPE_VAT.equals(vendor.getInvoiceType())) {
						if(vr != null) {
							if(vendor.getVatRate2() != null){
								vr.setValue(String.valueOf(vendor.getVatRate2().doubleValue()));
							}else{
								vr.setValue(String.valueOf(vendor.getVatRate().doubleValue()));
							}
							vr.refresh();
						}
					}
				}
			}
		}
	}
	
	public boolean validatePayRules(){
		IField field1 = getIField(PAYMENT_RULE1);//支票
		IField field2 = getIField(PAYMENT_RULE2);//现金
		IField field3 = getIField(PAYMENT_RULE3);//汇款
		
		IField field4 = getIField(PAYMENT_RULE4);//预付款
		IField field5 = getIField(PAYMENT_RULE5);//月结
		IField field6 = getIField(PAYMENT_RULE6);//验收后
		IField field7 = getIField(PAYMENT_RULE7);//__日支付
		
		IField field11 = getIField(IsIssueInvoice);//是否开具发票
		IField field12 = getIField(InvoiceType);//发票类型
		
		IField field13 = getIField(PAYMENT_RULE13);
		
		IField field14 = getIField(PAYMENT_RULE14);//收货后
		IField field15 = getIField(PAYMENT_RULE15);//__日支付
		
		Boolean flag1 = (field1.getValue() == null ? false : (Boolean)field1.getValue());
		Boolean flag2 = (field2.getValue() == null ? false : (Boolean)field2.getValue());
		Boolean flag3 = (field3.getValue() == null ? false : (Boolean)field3.getValue());
		
		Boolean flag4 = (field4.getValue() == null ? false : (Boolean)field4.getValue());
		Boolean flag5 = (field5.getValue() == null ? false : (Boolean)field5.getValue());
		Boolean flag6 = (field6.getValue() == null ? false : (Boolean)field6.getValue());
		Boolean flag7 = (field7.getValue() != null && !"".equals(String.valueOf(field7.getValue())));
		
		Boolean flag11 = (field11.getValue() == null ? false : (Boolean)field11.getValue());;
		Boolean flag12 = (field12.getValue() == null || "".equals(String.valueOf(field12.getValue()).trim()));//发票类型是否为空
		
		Boolean flag13 = (field13.getValue())==null?false:(Boolean)field13.getValue();
		
		Boolean flag14 = (field14.getValue() == null ? false : (Boolean)field14.getValue());
		Boolean flag15 = (field15.getValue() != null && !"".equals(String.valueOf(field15.getValue())));
		
		form.getMessageManager().setAutoUpdate(true);
		IMessageManager mmng = form.getMessageManager();
		if(!flag1 && !flag2 && !flag3){//支票、现金、汇款 必选一
			//提示 支票、现金、汇款 三者必须选择一个
			
			mmng.addMessage(field1.getId(), "支票、现金、汇款 三者必须选择一个", null,IMessageProvider.ERROR, 
					field1.getControls()[field1.getControls().length - 1]);
		}
		
//		if(!flag4 && !flag5 && !flag6 && !flag7 && !flag13 && !flag14 && !flag15){//预付款、月结、验收后____日支付 必选一
		if(!(flag4 || flag5 || (flag6 && flag7) || flag13 || (flag14 && flag15))){//预付款、月结、验收后____日支付 必选一
			//提示 预付款、月结、验收后___日支付 三者必须选择一个
			mmng.addMessage(field4.getId(), "预付款、月结、双月结、收货后___日支付、验收后___日支付 必须选择一个", null,IMessageProvider.ERROR, 
					field4.getControls()[field4.getControls().length - 1]);
		}
		
		if(!flag11){
			//提示没有选择开具发票
			mmng.addMessage(field11.getId(), "必须选择开具发票", null,IMessageProvider.ERROR, 
					field11.getControls()[field11.getControls().length - 1]);
		}else if(flag12){//选择了开具发票，但是发票类型没有选
			//提示没有选择发票类型
			mmng.addMessage(field12.getId(), "发票类型不能为空", null,IMessageProvider.ERROR, 
					field12.getControls()[field12.getControls().length - 1]);
		}
		
		int f = 0;
		if(flag4){
			f++;
		}
		if(flag5){
			f++;
		}
		if(flag6 && flag7){
			f++;
		}
		if(flag13){
			f++;
		}
		if(flag14 && flag15){
			f++;
		}
		if(f>1){
			mmng.addMessage(field4.getId(), "预付款、月结、双月结、收货后___日支付、验收后___日支付 中只能选一种", null,IMessageProvider.ERROR, 
					field4.getControls()[field4.getControls().length - 1]);
			return false;
		}
		return (flag1 || flag2 || flag3) && (flag4 || flag5 || (flag6 && flag7) || flag13 || (flag14 && flag15)) && flag11 && !flag12;
	}
	
	protected void lotPrintAdapter() {
		PurchaseOrder selectedPO = (PurchaseOrder) parentObject;
		if (selectedPO != null) {
			try {
				List<Lot> lots = new ArrayList<Lot>();
				ADManager adManager = Framework.getService(ADManager.class);
				List<PurchaseOrderLine> polines = adManager.getEntityList(Env.getOrgRrn(), PurchaseOrderLine.class,Integer.MAX_VALUE,"poRrn = "+selectedPO.getObjectRrn(),null);
				for(PurchaseOrderLine poLine : polines){
					String whereClause ="";
	    			if(Lot.LOTTYPE_SERIAL.equals(poLine.getLotType())){
        				String[] spiltBarCode = poLine.getBarCode().split("\\;");
        				if(spiltBarCode.length==1){
        					StringBuffer whereLotId = new StringBuffer();
        					whereLotId.append("'");
        					whereLotId.append(poLine.getBarCode());
        					whereLotId.append("'");
        					whereClause= whereLotId.toString();
        				}else{
        					StringBuffer whereLotId = new StringBuffer();
        					for(String barCode : spiltBarCode){
        						whereLotId.append("'");
            					whereLotId.append(barCode);
            					whereLotId.append("',");
        					}
        					whereClause= whereLotId.substring(0, whereLotId.length()-1);
        				}
	    			}else if(Lot.LOTTYPE_BATCH.equals(poLine.getLotType())){
    					StringBuffer whereLotId = new StringBuffer();
    					whereLotId.append("'");
    					whereLotId.append(poLine.getBarCode());
    					whereLotId.append("'");
    					whereClause= whereLotId.toString();
	    			}else if(Lot.LOTTYPE_MATERIAL.equals(poLine.getLotType())){
    					StringBuffer whereLotId = new StringBuffer();
    					whereLotId.append("'");
    					whereLotId.append(poLine.getMaterialId());
    					whereLotId.append("'");
    					whereClause= whereLotId.toString();
	    			}
					
					
					List<Lot> poLineLots = adManager.getEntityList(Env.getOrgRrn(), Lot.class,Integer.MAX_VALUE,"lotId in("+whereClause+")",null);
					if(poLineLots!=null && poLineLots.size() >0){
						for(Lot lot :poLineLots){
							if(!Lot.LOTTYPE_SERIAL.equals(lot.getLotType())){
								lot.setQtyCurrent(poLine.getQty());
							}else{
								lot.setQtyCurrent(BigDecimal.ONE);
							}
							lots.add(lot);
						}
//						lots.addAll(poLineLots);
					}
				}
				if(lots != null && lots.size() != 0){
//					LotPrintDialog printDialog = new LotPrintDialog(lots, null);
//					printDialog.open();
					LotDialog lotDialog = new LotDialog(UI.getActiveShell());
					lotDialog.setLots(lots);
					lotDialog.open();
				}
				
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}

	protected void sendEmailAdapter() {
		try {
				File file = new File("C:\\erppdf\\wkhtmltopdf\\bin\\wkhtmltopdf.exe ");
				if(!file.exists()) {
					UI.showError("您没安装ERP发送邮件系统,请联系IT部门....");
					return;
				}
				PurchaseOrder po = (PurchaseOrder) getParentObject();
				
				String report = "po_report.rptdesign";
				if(po.getVendor().getEmail()==null || "".equals(po.getVendor().getEmail())){
					UI.showError("供应商上没有设置EMAIL，请设置后在发送EMAIL");
					return;
				}
				
				List<PurchaseOrderLine> poLines = po.getPoLines();
				for(PurchaseOrderLine line : poLines){
					if(line != null && line.getMaterial() != null && Constants.KEY_MATERIAL_CATEGORY2.equals(line.getMaterial().getMaterialCategory2())){
						report = "po_report2.rptdesign";//打印委外加工单
						break;
					}
				}
					//开能有barCode的用barCode模板,没有的不用。如果是委外的不用barcode
				if(Env.getOrgRrn() == 139420L){
					if(report.equals("po_report.rptdesign")){
						for(PurchaseOrderLine line : poLines){
							if(line != null && line.getBarCode()!=null){
								report = "po_report_barcode_new.rptdesign";//
								break;
							}
						}
					}
				}

				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
				HashMap<String, String> userParams = new HashMap<String, String>();

				if(po == null){
					UI.showWarning(Message.getString("common.choose_one_record"));
					return;
				}
				Long objectRrn = po.getObjectRrn();
				userParams.put("PO_OBJECT_RRN", String.valueOf(objectRrn));

				String url = ReportUtil.createURL(report, params, userParams);
				url = url+"&__showtitle=false&__toolbar=false&__navigationbar=false";
				PoMail poMail = new PoMail();
				String fileName = po.getDocId()+".pdf";
				poMail.savePdfFile(url, fileName);
				poMail.sendMail(po.getVendor().getEmail(), fileName);
				UI.showInfo("发送成功");
		} catch (Exception e) {
			StringBuffer sf = new StringBuffer();
			sf.append("发送失败,原因提示：");
			e.printStackTrace();
			sf.append(e.getLocalizedMessage());
			sf.append("下面为系统提示原因,请联系IT");
			sf.append("\n");
			for(StackTraceElement element: e.getStackTrace()){
				sf.append(element.getClassName());
				sf.append(".");
				sf.append(element.getMethodName());
				sf.append(element.getLineNumber());
				sf.append("\n");
			}
			UI.showError(sf.toString());
		} 
	}
	
	protected void sendPDFEmailAdapter() {
		try {
			PurchaseOrder po = (PurchaseOrder) getParentObject();
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			if(po == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = po.getObjectRrn();
			userParams.put("PO_OBJECT_RRN", String.valueOf(objectRrn));

			PoMail poMail = new PoMail();
			String fileName = po.getDocId()+".pdf";
			File file = new File("C://"+fileName);
			if(!file.exists()){
				UI.showError("PDF文件不存在");
				return;
			}
			poMail.sendMail(po.getVendor().getEmail(), fileName);
			UI.showInfo("发送成功");
			
		} catch (Exception e) {
			StringBuffer sf = new StringBuffer();
			sf.append("发送失败,原因提示：");
			e.printStackTrace();
			sf.append(e.getLocalizedMessage());
			sf.append("下面为系统提示原因,请联系IT");
			sf.append("\n");
			for(StackTraceElement element: e.getStackTrace()){
				sf.append(element.getClassName());
				sf.append(".");
				sf.append(element.getMethodName());
				sf.append(element.getLineNumber());
				sf.append("\n");
			}
			UI.showError(sf.toString());
		} 
	}
	
	protected void savePDFAdapter() {
		try {
			PurchaseOrder po = (PurchaseOrder) getParentObject();
			
			String report = "po_report_barcode.rptdesign";
			
			List<PurchaseOrderLine> poLines = po.getPoLines();
			for(PurchaseOrderLine line : poLines){
				if(line != null && line.getMaterial() != null && Constants.KEY_MATERIAL_CATEGORY2.equals(line.getMaterial().getMaterialCategory2())){
					report = "po_report2.rptdesign";//打印委外加工单
					break;
				}
			}
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			if(po == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = po.getObjectRrn();
			userParams.put("PO_OBJECT_RRN", String.valueOf(objectRrn));

			String path = Message.getString("chrome.path");
//			String url = ReportUtil.createURL(report, params, userParams);
//			url = url+"&__showtitle=false&__toolbar=false&__navigationbar=false";
			String url ="http://192.168.0.193:8080/ERPreport/output?__report=po_report_barcode.rptdesign&__format=html&__resourceFolder=&PO_OBJECT_RRN=" +
					+objectRrn+"&&__dpi=96&__pageoverflow=0&__overwrite=false";
			String command = path+" "+url;
			Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		} 
	}
}

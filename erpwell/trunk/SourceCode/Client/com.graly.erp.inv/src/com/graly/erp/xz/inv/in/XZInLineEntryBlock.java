package com.graly.erp.xz.inv.in;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.ApprovedDialog;
import com.graly.erp.inv.in.SeeLotDialog;
import com.graly.erp.inv.in.WarehouseEntityForm;
import com.graly.erp.inv.iqc.IqcLineDialog;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.receipt.ReceiptLineDialog;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.pur.po.POLineBlockDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.ParentChildEntityBlock;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
public class XZInLineEntryBlock extends ParentChildEntityBlock {
	Logger logger = Logger.getLogger(XZInLineEntryBlock.class);
	private static final String TABLE_NAME_MOVEMENT_IN = "INVMovementIn";
	private static final String TABLE_NAME_LINELOT = "INVMovementLineLot";
	private static final String TABLE_NAME_RECEIPTLINE = "INVReceiptLine";
	private static final String TABLE_NAME_IQCLINE = "INVIqcLine";
	private static final String TABLE_NAME_POLINE = "PURPurchaseOrderLine";
	private static final String TABLE_NAME_PO = "PURPurchaseOrder";
	private static final String TABLE_NAME_INVOICE_MOVEMENTLINE = "INVFinanceMovementLine";
	
	protected ToolItem itemApprove;
	protected ToolItem relationShipItem;
	protected ToolItem lotSelect;
	protected MovementLine selectMovementLine;
	protected ToolItem itemPreview;
	private ADTable adTable;
	private ADTable movementLineTable; //审核或冲销时的movement line Table
	protected TableListManager listTableManager;
	protected int style = SWT.FULL_SELECTION | SWT.BORDER;
	private ToolItem itemWriteOff;
	private String where;
	private Menu menu;
	protected boolean flag;
	ADManager entityManager;
	protected ToolItem itemPreviewAgain;

	public XZInLineEntryBlock(ADTable parentTable, Object parentObject, String whereClause, ADTable childTable,boolean flag) {
		super(parentTable, parentObject, whereClause, childTable);
		this.parentObject = parentObject;
		this.flag = flag;
	}

	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
		refresh();
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setMovementLineSelect(ss.getFirstElement());
				selectLotAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setMovementLineSelect(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

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
			WarehouseEntityForm itemForm = new WarehouseEntityForm(getTabs(), SWT.NONE, tab, mmng);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
	}
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class klass = Class.forName(table.getModelClass());
			detailsPart.registerPage(klass, new XZInLineProperties(this, table, getParentObject()));
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}

	@Override
	public void createToolBar(Section section) {
		final ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApprove(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreview(tBar);
		createMenu(tBar);
		section.setTextClient(tBar);
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
	
	protected void createToolItemPreviewAgain(ToolBar tBar) {
		itemPreviewAgain = new AuthorityToolItem(tBar, SWT.PUSH,"INV.Pin.PrintAgain");
		itemPreviewAgain.setText("继续打印");
		itemPreviewAgain.setImage(SWTResourceCache.getImage("print"));
		itemPreviewAgain.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewAdapterAgain();
			}
		});
	}
	
	protected void createToolItemRelationShip(final ToolBar tBar) {
		relationShipItem = new ToolItem(tBar, SWT.DROP_DOWN);
		relationShipItem.setText(Message.getString("inv.relationship"));
		relationShipItem.setImage(SWTResourceCache.getImage("search"));
		relationShipItem.setToolTipText(Message.getString("inv.relationship_tip"));
		relationShipItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
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
	private void createMenu(final ToolBar toolBar) {
		menu = new Menu(UI.getActiveShell(), SWT.POP_UP);
		MenuItem menuItemPO = new MenuItem(menu, SWT.PUSH);
		menuItemPO.setText(Message.getString("inv.relationship_po"));
		MenuItem menuItemReceipt = new MenuItem(menu, SWT.PUSH);
		menuItemReceipt.setText(Message.getString("inv.relationship_receipt"));
		MenuItem menuItemIQC = new MenuItem(menu, SWT.PUSH);
		menuItemIQC.setText(Message.getString("inv.relationship_iqc"));
		new MenuItem(menu, SWT.SEPARATOR);
		new MenuItem(menu, SWT.PUSH).setText(Message.getString("common.cancel"));

		menuItemPO.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuPoAdapter();
			}
		});
		menuItemReceipt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuReceiptAdapter();
			}
		});

		menuItemIQC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuIqcAdapter();
			}
		});
	}

	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PIN_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}

	protected void createToolItemWriteOff(ToolBar tBar) {
		itemWriteOff = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PIN_WRITEOFF);
		itemWriteOff.setText(Message.getString("inv.in_write_off"));
		itemWriteOff.setImage(SWTResourceCache.getImage("voice"));
		itemWriteOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				writeOffAdapter();
			}
		});
	}
	
	protected void createToolItemLotSelect(ToolBar tBar) {
		lotSelect = new ToolItem(tBar, SWT.PUSH);
		lotSelect.setText(Message.getString("inv.barcode"));
		lotSelect.setImage(SWTResourceCache.getImage("barcode"));
		lotSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectLotAdapter();
			}
		});
	}
	
	//将暂估金额和发票金额列合计显示在viewer的最下面
	public void doViewerAggregation(){
		Table table = ((TableViewer)viewer).getTable();
		MovementLine movementLineSum = new MovementLine();
		movementLineSum.setLineStatus(Message.getString("inv.total"));
		BigDecimal assessLineSum = BigDecimal.ZERO;
		BigDecimal invoiceLineSum = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof MovementLine) {
				MovementLine movementLine = (MovementLine)obj;
				if (movementLine.getAssessLineTotal() != null) {
					assessLineSum = assessLineSum.add(movementLine.getAssessLineTotal());
				}
				if (movementLine.getInvoiceLineTotal() != null) {
					invoiceLineSum = invoiceLineSum.add(movementLine.getInvoiceLineTotal());
				}
			}
		}
		movementLineSum.setAssessLineTotal(assessLineSum);
		movementLineSum.setInvoiceLineTotal(invoiceLineSum);
		TableViewer tv = (TableViewer)viewer;
		tv.insert(movementLineSum, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"宋体",10,SWT.BOLD); 
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}

	public void refresh() {
		super.refresh();
		doViewerAggregation();
		if (parentObject instanceof MovementIn) {
			MovementIn parent = (MovementIn) parentObject;
			refreshToolItem(parent.getDocStatus());
		}
	}

	protected void refreshToolItem(String status) {
		if (!(MovementIn.STATUS_DRAFTED.equals(status))) {
			itemApprove.setEnabled(false);
		}
		if (MovementIn.STATUS_APPROVED.equals(status)) {
//			itemWriteOff.setEnabled(true);
		} else {
//			itemWriteOff.setEnabled(false);
		}
	}

	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if (!confirm)
				return;
			if (parentObject != null) {
				form.getMessageManager().removeAllMessages();
				MovementIn in = (MovementIn) parentObject;
				INVManager invManager = Framework.getService(INVManager.class);
				invManager.bjApproveMovementIn(in, MovementIn.InType.PIN,
						false, false, Env.getUserRrn(), true);
				ADManager adManager = Framework.getService(ADManager.class);
				parentObject = adManager.getEntity(in);
				UI.showInfo(Message.getString("common.approve_successed"));
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void writeOffAdapter() {
		try {
			if (parentObject != null) {
				ApprovedDialog approvedDialog = new ApprovedDialog(UI.getActiveShell(),
						form, getInvoiceMovementLineTable(), (MovementIn)parentObject, true);
				if(approvedDialog.open() == Dialog.OK) {
					form.getMessageManager().removeAllMessages();
					MovementIn in = (MovementIn)approvedDialog.getPin();
					
					INVManager invManager = Framework.getService(INVManager.class);
					parentObject = invManager.writeOffMovementIn(in, Env.getUserRrn());
					UI.showInfo(Message.getString("inv.in_write_off_successful"));
					itemWriteOff.setEnabled(false);
					refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected void selectLotAdapter() {
		if (selectMovementLine == null) {
			UI.showWarning(Message.getString("inv.entityisnull"));
			return;
		}
		String where = "movementLineRrn = '" + selectMovementLine.getObjectRrn() + "'";
		adTable = getADTableOfRequisition(TABLE_NAME_LINELOT);
		listTableManager = new TableListManager(adTable);
		SeeLotDialog seeLotDialog = new SeeLotDialog(listTableManager, null, where, style);
		if (seeLotDialog.open() == Dialog.CANCEL) {
			refresh();
		}
	}

	protected void previewAdapter() {
		try {
			MovementIn mi = (MovementIn) parentObject;
			//不能超过2次打印
			Long time = mi.getPrintTime();
//			if(time!=null && time>=2){
//				UI.showError("打印次数操作2次,不允许再打印,请选择继续打印功能");
//				return;
//			}
			
			//弹出对话框，让用户选择需要合并打印的单据
			ADTable adTable = getADTableOfRequisition(TABLE_NAME_MOVEMENT_IN);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createDate = sdf.format(mi.getDateCreated());
			String whereClause = "  docStatus in ('DRAFTED','APPROVED') and to_char(dateCreated, 'yyyy-mm-dd') = '" + createDate + "' and vendorRrn = " + mi.getVendorRrn();
			BeforeOpenReportDialog bord = new BeforeOpenReportDialog(Display.getCurrent().getActiveShell(),  new EntityTableManager(adTable, SWT.CHECK), mi, whereClause);
			StringBuffer sb = new StringBuffer("");
			if(bord.open() == Dialog.OK){
				sb = bord.getSb();
			}
			
			form.getMessageManager().removeAllMessages();
	
			//保存打印次数
//			Long time = mi.getPrintTime();
			if(time == null){
				mi.setPrintTime(1L);
			}else{
				mi.setPrintTime(time + 1L);
			}
			ADManager manager = Framework.getService(ADManager.class);
			parentObject = manager.saveEntity(mi, Env.getUserRrn());			
			
			String report = "spares_movement_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			if(!(Movement.STATUS_APPROVED.equals(mi.getDocStatus()) || Movement.STATUS_COMPLETED.equals(mi.getDocStatus()))){
				UI.showWarning(Message.getString("common.is_not_approved")+","+Message.getString("common.can_not_print"));
				return;
			}
			if(mi == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = mi.getObjectRrn();
			if(sb.toString().trim().length() > 0){
				sb.append("," + objectRrn);
			}else{
				sb.append(objectRrn);
			}
			userParams.put("OBJECT_RRN", sb.toString());

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void previewAdapterAgain() {
		try {
			MovementIn mi = (MovementIn) parentObject;
			//弹出对话框，让用户选择需要合并打印的单据
			ADTable adTable = getADTableOfRequisition(TABLE_NAME_MOVEMENT_IN);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createDate = sdf.format(mi.getDateCreated());
			String whereClause = "  docStatus in ('DRAFTED','APPROVED') and to_char(dateCreated, 'yyyy-mm-dd') = '" + createDate + "' and vendorRrn = " + mi.getVendorRrn();
			BeforeOpenReportDialog bord = new BeforeOpenReportDialog(Display.getCurrent().getActiveShell(),  new EntityTableManager(adTable, SWT.CHECK), mi, whereClause);
			StringBuffer sb = new StringBuffer("");
			if(bord.open() == Dialog.OK){
				sb = bord.getSb();
			}
			
			form.getMessageManager().removeAllMessages();
			
			//保存打印次数
			Long time = mi.getPrintTime();
			if(time == null){
				mi.setPrintTime(1L);
			}else{
				mi.setPrintTime(time + 1L);
			}
			ADManager manager = Framework.getService(ADManager.class);
			parentObject = manager.saveEntity(mi, Env.getUserRrn());			
			
			String report = "spares_movement_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			if(!(Movement.STATUS_APPROVED.equals(mi.getDocStatus()) || Movement.STATUS_COMPLETED.equals(mi.getDocStatus()))){
				UI.showWarning(Message.getString("common.is_not_approved")+","+Message.getString("common.can_not_print"));
				return;
			}
			if(mi == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = mi.getObjectRrn();
			if(sb.toString().trim().length() > 0){
				sb.append("," + objectRrn);
			}else{
				sb.append(objectRrn);
			}
			userParams.put("OBJECT_RRN", sb.toString());

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			if(entityManager == null)
				entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("InLineEntityBlock : getADTableOfRequisition()", e);
		}
		return null;
	}

	private void menuPoAdapter() {
		ADTable adTablePO = getADTableOfRequisition(TABLE_NAME_PO);
		ADTable adTablePOLine = getADTableOfRequisition(TABLE_NAME_POLINE);
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			if (parentObject != null) {
				MovementIn in = (MovementIn) parentObject;
				if (in.getPoRrn() == null) {
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
				where = " objectRrn='" + in.getPoRrn() + "'";

				List<PurchaseOrder> listReceipt = adManager.getEntityList(Env.getOrgRrn(), PurchaseOrder.class, 2, where, "");
				PurchaseOrder po = new PurchaseOrder();
				if (listReceipt.size() > 0) {
					po = listReceipt.get(0);
				}
				where = (" poRrn = '" + po.getObjectRrn().toString() + "' ");
				POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), adTablePO, where, po, adTablePOLine, true);
				if (cd.open() == Dialog.CANCEL) {
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}

	private void menuReceiptAdapter() {
		adTable = getADTableOfRequisition(TABLE_NAME_RECEIPTLINE);
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			if (parentObject != null) {
				MovementIn in = (MovementIn) parentObject;
				if (in.getReceiptRrn() == null) {
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
				where = " objectRrn='" + in.getReceiptRrn() + "'";

				List<Receipt> listReceipt = adManager.getEntityList(Env.getOrgRrn(), Receipt.class, 2, where, "");
				Receipt receipt = new Receipt();
				if (listReceipt.size() > 0) {
					receipt = listReceipt.get(0);
				}

				where = " receiptId='" + receipt.getDocId().toString() + "'";
				ReceiptLineDialog receiptlineDialog = new ReceiptLineDialog(UI.getActiveShell(), adTable, where, receipt, true);
				if (receiptlineDialog.open() == Dialog.CANCEL) {
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}

	private void menuIqcAdapter() {
		adTable = getADTableOfRequisition(TABLE_NAME_IQCLINE);
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			if (parentObject != null) {
				MovementIn in = (MovementIn) parentObject;
				if (in.getIqcRrn() == null) {
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
				where = " objectRrn='" + in.getIqcRrn() + "'";

				List<Iqc> listIqc = adManager.getEntityList(Env.getOrgRrn(), Iqc.class, 2, where, "");
				Iqc iqc = new Iqc();
				if (listIqc.size() > 0) {
					iqc = listIqc.get(0);
				}
				where = " iqcId='" + iqc.getDocId().toString() + "'";
				IqcLineDialog iqcLineDialog = new IqcLineDialog(UI.getActiveShell(), adTable, where, iqc);
				if (iqcLineDialog.open() == Dialog.CANCEL) {
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
	}
	
	private void setMovementLineSelect(Object obj) {
		if (obj instanceof MovementLine) {
			selectMovementLine = (MovementLine) obj;
		} else {
			selectMovementLine = null;
		}
	}
	
	protected void setParenObjectStatusChanged() {
		MovementIn in = (MovementIn)parentObject;
		String status = "";
		if(in != null && in.getObjectRrn() != null) {
			status = in.getDocStatus();			
		}
		if(MovementIn.STATUS_APPROVED.equals(status)) {
			itemApprove.setEnabled(false);
			itemPreview.setEnabled(true);
		} else if(MovementIn.STATUS_DRAFTED.equals(status)) {
			itemApprove.setEnabled(true);
			itemPreview.setEnabled(true);
		} else if(MovementIn.STATUS_CLOSED.equals(status)) {
			itemApprove.setEnabled(false);
			itemPreview.setEnabled(false);
		} else {
			itemApprove.setEnabled(false);
		}
		if(flag){
			itemApprove.setEnabled(false);
			itemPreview.setEnabled(false);
			itemWriteOff.setEnabled(false);
		}
	}
	
	/* 在其对应的properties中调用此方法, 会根据parentObject的状态来初始化properties按钮是否可用*/
	public boolean isEnableByParentObject() {
		MovementIn in = (MovementIn)this.getParentObject();
		if(in == null) {
			return false;
		}
		String status = in.getDocStatus();
		if(Movement.STATUS_CLOSED.equals(status)
				|| Movement.STATUS_APPROVED.equals(status)
				|| Movement.STATUS_COMPLETED.equals(status)
				|| Movement.STATUS_INVALID.equals(status) || flag) {
			return false;
		}
		return true;
	}
	
	protected ADTable getInvoiceMovementLineTable() throws Exception {
//		try {
			if (movementLineTable == null) {
				ADManager adManager = Framework.getService(ADManager.class);
				movementLineTable = adManager.getADTable(0L, TABLE_NAME_INVOICE_MOVEMENTLINE);
			}
			return movementLineTable;
//		} catch (Exception e1) {
//			ExceptionHandlerManager.asyncHandleException(e1);
//		}
	}
}


class BeforeOpenReportDialog extends EntityDialog{
	protected TableViewerManager tableManager;
	protected StructuredViewer viewer;
	private String whereClause;
	private StringBuffer sb;
	
	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public TableViewerManager getTableManager() {
		return tableManager;
	}

	public void setTableManager(TableViewerManager tableManager) {
		this.tableManager = tableManager;
	}

	public BeforeOpenReportDialog(Shell parent, ADTable table, ADBase adObject) {
		super(parent, table, adObject);
	}
	
	public BeforeOpenReportDialog(Shell parent, TableViewerManager tableManager, ADBase adObject, String whereClause){
		super(parent, tableManager.getADTable(), adObject);
		this.tableManager = tableManager;
		this.whereClause = whereClause;
	}
	
	@Override
	protected void createFormContent(Composite composite) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
	    setTitle("请选择需要合并打印的采购入库单");
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		managedForm = new ManagedForm(toolkit, sForm);
		final IMessageManager mmng = managedForm.getMessageManager();
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		viewer = getTableManager().createViewer(body, managedForm.getToolkit());
		viewer.setInput(new EntityItemInput(getTableManager().getADTable(), getWhereClause(), ""));	
		tableManager.updateView(viewer);
		if(viewer != null && viewer instanceof CheckboxTableViewer){
			((CheckboxTableViewer)viewer).remove(getAdObject());
		}
	}
	
	@Override
	protected void okPressed() {
		sb = new StringBuffer(" ");
		if(viewer != null && viewer instanceof CheckboxTableViewer){
			Object[] checkedObjs = ((CheckboxTableViewer)viewer).getCheckedElements();
			for(Object obj : checkedObjs){
				if(obj instanceof MovementIn){
					MovementIn mi = (MovementIn) obj;
					sb.append(mi.getObjectRrn());
					sb.append(",");
				}
			}
			sb = new StringBuffer(sb.substring(0, sb.length()-1));
		}
		super.okPressed();
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	
	public StringBuffer getSb() {
		return sb;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.ok"), false);
	}
}


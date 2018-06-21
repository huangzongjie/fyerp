package com.graly.erp.xz.pur.request;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Storage;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.pur.po.POSection;
import com.graly.erp.pur.request.refmo.RefMoDialog;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.ChildEntityBlock;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
public class XZRequisitionLineEntityBlock extends ChildEntityBlock {

	private static final Logger logger = Logger.getLogger(XZRequisitionLineEntityBlock.class);
	// 设置是否只是查看,默认为false,当其它模块关联要查看PR时,可设置此值为true
	private boolean isView = false;
	protected CheckboxTableViewer tViewer;
	protected ToolItem itemImport;
	protected ToolItem itemApprove;
	protected ToolItem itemPreview;
	protected ToolItem itemClose;
	protected ToolItem relationShipItem;
	protected ToolItem itemCreate;
	protected ToolItem itemMonthlyConsume;//月用量
	protected ToolItem itemPurchaseOrder;//生成采购订单
	protected ToolItem itemCancleApprove;

	private static final String TABLE_NAME_POLINE = "PURPurchaseOrderLine";
	private static final String TABLE_NAME_PO = "PURPurchaseOrder";
	private static final String TABLE_NAME_MONTHLYCONSUME = "PURMonthlyConsume";
	private int i=0;
	private Menu menu;
	private ADTable adTable;
	protected String where;
	private RequisitionLine selectedPRline;
	protected ColorEntityTableManager tableManager;
	protected EntityQueryDialog monthlyConsumeDialog;
	private XZRequisitionLineProperties XZRequisitionLineProperties;

	public XZRequisitionLineEntityBlock(ColorEntityTableManager tableManager, String whereClause, Object parentObject) {
		super(tableManager, whereClause, parentObject);
	}

	public XZRequisitionLineEntityBlock(ColorEntityTableManager tableManager, String whereClause, Object parentObject, boolean isView) {
		super(tableManager, whereClause, parentObject);
		this.isView = isView;
		this.tableManager = tableManager;
	}

	public XZRequisitionLineEntityBlock(ColorEntityTableManager tableManager, String whereClause, Object parentObject, boolean isView ,int i) {
		super(tableManager, whereClause, parentObject);
		this.isView = isView;
		this.tableManager = tableManager;
		this.i=i;
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
		tableManager.setPrLineBlock(this);
		// 根据parentObject状态设置itemApprove和itemClose按钮是否可用
		setParenObjectStatusChanged();
		changedLineCheckBox();
	}

	private void changedLineCheckBox() {
		if (viewer instanceof CheckboxTableViewer) {
			tViewer = (CheckboxTableViewer) viewer;
			tViewer.addCheckStateListener(new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
					boolean isChecked = event.getChecked();
					if(isChecked){
						Object object = event.getElement();
						RequisitionLine prLine = (RequisitionLine)object;
						if(RequisitionLine.LINESTATUS_CLOSED.equals(prLine.getLineStatus())){
							return;
						}
						if(prLine.getQtyOrdered() != null && prLine.getQty() != null && prLine.getQty().compareTo(prLine.getQtyOrdered()) <= 0){
							return;					
						}
					}
				}			
			});
		}
	}

	@Override
	public void refresh() {
		super.refresh();
	}

	@Override
	public void createToolBar(Section section) {
		final ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApprove(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemCancleApprove(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		new ToolItem(tBar, SWT.SEPARATOR);
		createMenu(tBar);
		section.setTextClient(tBar);
	}
	
	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new SpecialQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
//	protected void createToolItemConsume(ToolBar tBar) {
//		itemPurchaseOrder = new AuthorityToolItem(tBar, SWT.PUSH, KEY_XZ_PR_UNAPPROVED);
//		itemPurchaseOrder.setText("生成采购定单");
//		itemPurchaseOrder.setImage(SWTResourceCache.getImage("new"));
//		itemPurchaseOrder.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				purchaseOrderAdapter();
//			}
//		});
//	}

	protected void createToolItemCreate(ToolBar tBar) {
		itemCreate = new ToolItem(tBar, SWT.PUSH);
		itemCreate.setText(Message.getString("pur.create_po"));
		itemCreate.setImage(SWTResourceCache.getImage("new"));
		itemCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				createAdapter();
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
		MenuItem menuItemPo = new MenuItem(menu, SWT.PUSH);
		menuItemPo.setText(Message.getString("inv.relationship_po"));
//		new MenuItem(menu, SWT.SEPARATOR);
//		MenuItem menuItemMo = new MenuItem(menu, SWT.PUSH);
//		menuItemMo.setText(Message.getString("pur.relation_mo"));
		
		// 创建关联PO监听器
		menuItemPo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuPoAdapter();
			}
		});
//		// 创建关联MO监听器
//		menuItemMo.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				menuMoAdapter();
//			}
//		});
	}

	protected void createAdapter() {
		List<RequisitionLine> list = (List<RequisitionLine>)getItemsCheck();
		if(list.size() == 0){
			UI.showWarning(Message.getString("inv.prline_select_is_null"));
			return;
		}
		for (RequisitionLine requisitionLine : list) {
			if(!((requisitionLine.getLineStatus()).equals(RequisitionLine.LINESTATUS_APPROVED))){
				UI.showError(Message.getString("pur.selected_should_be_approved"));
				return;
			}
		}
		PurchaseOrder po = new PurchaseOrder();
		po.setOrgRrn(Env.getOrgRrn());
		po.setDeliveryAddress(POSection.DELIVERY_ADDRESS.get(Env.getOrgRrn()));
		// 设置pr仓库为po仓库，因为pr.warehouse为空，在此设置无意义
//		Requisition pr = (Requisition)this.parentObject;
//		po.setWarehouseId(pr.getWarehouseId());
//		po.setWarehouseRrn(pr.getWarehouseRrn());
		try {
			PURManager purManager = Framework.getService(PURManager.class);
			ADManager adManager = Framework.getService(ADManager.class);
			
			po = purManager.createPOFromPR(po, list, Env.getUserRrn());
			po = (PurchaseOrder)adManager.getEntity(po);
			if(po.getPurchaser() == null || po.getPurchaser() == ""){
				for (RequisitionLine prLine : list) {
					if(prLine.getPurchaser() != null){
						po.setPurchaser(prLine.getPurchaser());
						adManager.saveEntity(po, Env.getUserRrn());
					}
				}
			}
			UI.showInfo(String.format(Message.getString("pur.create_po_successful"),po.getDocId()));
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected List<RequisitionLine> getItemsCheck(){
		List<RequisitionLine> selectedItems  = new ArrayList<RequisitionLine>();
		if (viewer instanceof StructuredViewer) {
			CheckboxTableViewer tableViewer = (CheckboxTableViewer) viewer;
			Object[] os = tableViewer.getCheckedElements();
			if(os.length != 0) {
				for(Object o : os) {
					RequisitionLine select = (RequisitionLine)o;
					selectedItems.add(select);
				}
			}
		}
		return selectedItems;
	}

	protected void createViewAction(StructuredViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof RequisitionLine) {
			selectedPRline = (RequisitionLine) obj;
		} else {
			selectedPRline = null;
		}
	}

	
	protected void setParenObjectStatusChanged() {
		Requisition pr = (Requisition) getParentObject();
		String status = "";
		if (pr != null && pr.getObjectRrn() != null) {
			status = pr.getDocStatus();
		}
		if (!isView) {
			if (Requisition.STATUS_APPROVED.equals(status)) {
				itemApprove.setEnabled(false);
			} else if (Requisition.STATUS_DRAFTED.equals(status)) {
				itemApprove.setEnabled(true);
			} else if (Requisition.STATUS_CLOSED.equals(status)) {
				itemApprove.setEnabled(false);
				itemCreate.setEnabled(false);
			} else {
				itemApprove.setEnabled(false);
			}
//			if (pr != null && pr.getObjectRrn() != null) {
//				itemPreview.setEnabled(true);
//			} else {
//				itemPreview.setEnabled(false);
//			}
		} else {
			itemApprove.setEnabled(false);
//			itemPreview.setEnabled(false);
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
				itemApprove.setEnabled(false);
				itemCancleApprove.setEnabled(false);
			}
		}catch(Exception e ){
			e.printStackTrace();
		}
	}

	/*
	 * 刷新properties中line对象时,即使在Block中选中了Line,也将properties中line对象置为空
	 * 若要对line进行操作,则必须重新选中Block中line行
	 */
	protected void setChildObjectStatusChanged() {
		XZRequisitionLineProperties page = (XZRequisitionLineProperties) this.detailsPart.getCurrentPage();
		try {
			selectedPRline = null;
			page.setAdObject(null);
			page.refresh();
			page.setStatusChanged(((Requisition) getParentObject()).getDocStatus());
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}
	
	protected void createToolItemCancleApprove(ToolBar tBar) {
		itemCancleApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_REVOKE);
		itemCancleApprove.setText("取消审核");
		itemCancleApprove.setImage(SWTResourceCache.getImage("cancel"));
		itemCancleApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cancleApproveAdapter();
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

	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			form.getMessageManager().removeAllMessages();
			Requisition pr = (Requisition) getParentObject();
			if (pr != null && pr.getObjectRrn() != null) {
				PURManager pudManager = Framework.getService(PURManager.class);
				pudManager.approvePR((Requisition) getParentObject(), Env.getUserRrn());
				UI.showInfo(Message.getString("common.approve_successed"));

				ADManager adManager = Framework.getService(ADManager.class);
				Requisition parentObject = (Requisition)adManager.getEntity((Requisition) getParentObject());
				setParentObject(parentObject);
				setParenObjectStatusChanged();
				setChildObjectStatusChanged();
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void cancleApproveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			form.getMessageManager().removeAllMessages();
			Requisition pr = (Requisition) getParentObject();
			if (pr != null && pr.getObjectRrn() != null) {
				PURManager pudManager = Framework.getService(PURManager.class);
				pudManager.unApprovePR((Requisition) getParentObject(), Env.getUserRrn());
				UI.showInfo("取消审核成功");

				ADManager adManager = Framework.getService(ADManager.class);
				Requisition parentObject = (Requisition)adManager.getEntity((Requisition) getParentObject());
				setParentObject(parentObject);
				setParenObjectStatusChanged();
				setChildObjectStatusChanged();
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}


	public boolean isEnableByParentObject() {
		Requisition pr = (Requisition) this.getParentObject();
		if (pr == null) {
			return true;
		}
		String status = pr.getDocStatus();
		if (isView)
			return false;
		if (Requisition.STATUS_CLOSED.equals(status) || Requisition.STATUS_APPROVED.equals(status)
				|| Requisition.STATUS_COMPLETED.equals(status) || Requisition.STATUS_INVALID.equals(status)) {
			return false;
		}
		return true;
	}

	protected void previewAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			String report = "pr_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			Requisition rq = (Requisition) getParentObject();
			if(rq == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = rq.getObjectRrn();
			userParams.put("PR_OBJECT_RRN", String.valueOf(objectRrn));

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("InLineEntityBlock : getADTableOfRequisition()", e);
		}
		return null;
	}
	protected ADTable getADTableByTableName(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable t = entityManager.getADTable(0L, tableName);
			t = entityManager.getADTableDeep(t.getObjectRrn());
			return t;
		} catch (Exception e) {
			logger.error("RequisitionLineEntityBlock : getADTableByTableName()", e);
		}
		return null;
	}

	protected void menuPoAdapter() {
//		if (selectedPRline == null) {
//			UI.showWarning(Message.getString("inv.prline_select_is_null"));
//			return;
//		}
//		ADTable adTablePO = getADTableOfRequisition(TABLE_NAME_PO);
//		ADTable adTablePOLine = getADTableOfRequisition(TABLE_NAME_POLINE);
//		ADManager adManager;
//		try {
//			adManager = Framework.getService(ADManager.class);
//			RequisitionLine prLine = (RequisitionLine) selectedPRline;
//			where = " requisitionLineRrn = '" + prLine.getObjectRrn() + "' ";
//			List<PurchaseOrderLine> poLines = adManager.getEntityList(Env.getOrgRrn(), PurchaseOrderLine.class, Integer.MAX_VALUE,
//					where, "");
//
//			if (poLines.size() == 0) {
//				UI.showInfo(Message.getString("inv.relationship_is_null"));
//				return;
//			}
//			if (poLines.size() > 0) {
//				Map<Long, PurchaseOrderLine> poLineMap = new HashMap<Long, PurchaseOrderLine>();
//				for (PurchaseOrderLine purchaseOrderLine : poLines) {
//					poLineMap.put(purchaseOrderLine.getPoRrn(), purchaseOrderLine);
//				}
//				if (poLineMap.size() == 1) {
//					PurchaseOrderLine poLine = (PurchaseOrderLine) poLines.get(0);
//					where = " objectRrn = '" + poLine.getPoRrn() + "' ";
//					List<PurchaseOrder> pos = adManager.getEntityList(Env.getOrgRrn(), PurchaseOrder.class, 2, where, "");
//					if (pos != null) {
//						PurchaseOrder po = pos.get(0);
//						where = (" poRrn = '" + poLine.getPoRrn() + "' AND requisitionLineRrn = '" + prLine.getObjectRrn() + "' ");
//						POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), adTablePO, where, po, adTablePOLine, true);
//						if (cd.open() == Dialog.OK) {
//						}
//					}
//				} else {
//					POLineListDialog poLineListDialog = new POLineListDialog(UI.getActiveShell(), adTablePOLine, adTablePO, poLines);
//					if (poLineListDialog.open() == Dialog.OK) {
//					}
//				}
//			}
//		} catch (Exception e1) {
//			return;
//		}
	}
	
	protected void menuMoAdapter() {
		try {
			if (selectedPRline == null) {
				UI.showWarning(Message.getString("inv.prline_select_is_null"));
				return;
			}
			PURManager purManager = Framework.getService(PURManager.class);
			List<ManufactureOrder> list = purManager.getMoListByPrLine(Env.getOrgRrn(), selectedPRline.getObjectRrn());
			if(list == null || list.size() == 0) {
				UI.showError(Message.getString("inv.relationship_is_null"));
				return;
			}
			RefMoDialog dialog = new RefMoDialog(list);
			dialog.open();
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
	
	public boolean isView() {
		return isView;
	}

	public void setIsView(boolean isView) {
		this.isView = isView;
	}
	
	public RequisitionLine getSelectedPRline() {
		return selectedPRline;
	}

	public void setSelectedPRline(RequisitionLine selectedPRline) {
		this.selectedPRline = selectedPRline;
	}
	
	private class SpecialQueryDialog extends EntityQueryDialog{

		public SpecialQueryDialog(Shell parent,
				EntityTableManager tableManager, XZRequisitionLineEntityBlock refresh) {
			super(parent, tableManager, refresh);
		}

		public SpecialQueryDialog(Shell parent) {
			super(parent);
		}

		private boolean isFit(Object bean){
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			boolean flag = true;
			
	        for(IField f : fields.values()) {
				Object value = PropertyUtil.getPropertyForString(bean, f.getId());
				Object t = f.getValue();
				if (t instanceof Date && value instanceof Date) {
					Date cc = (Date)t;
					if(cc != null) {						
						flag = flag && I18nUtil.formatDate(cc).equals(value);
					}
				} else if(t instanceof String && value instanceof String) {
					String txt = (String)t;
					if(!txt.trim().equals("") && txt.length() != 0) {						
						flag = flag && txt.equals(value);
					}
				} else if(t instanceof Boolean) {
					flag = flag && (Boolean)t;
				} else if(t instanceof Long && value instanceof Long) {
					long l1 = (Long)t;
					long l2 = (Long)value;
					flag = flag && (l1 == l2);
				} else {
					flag = false;
				}
	        }
	        return flag;
		}
		
		@Override
		protected void okPressed() {
			iRefresh.refresh();
			setReturnCode(OK);
			XZRequisitionLineEntityBlock block = (XZRequisitionLineEntityBlock)iRefresh;
			RequisitionLine selectedLine = block.getSelectedPRline();
			int index = 0;			
			CheckboxTableViewer curViewer = (CheckboxTableViewer)block.viewer;
			EntityItemInput input = (EntityItemInput) curViewer.getInput();
			try {
				ADManager manager = Framework.getService(ADManager.class);
				List<ADBase> list = manager.getEntityList(Env.getOrgRrn(), input.getTable().getObjectRrn(), 
						Env.getMaxResult(), input.getWhereClause(), input.getOrderByClause());
//				if(selectedLine != null){
//					index = list.indexOf(selectedLine);
//				}
				boolean flag = true;
//				for(int i=index; i<list.size(); i++){//原来从选中处开始往下查,现开为从头查
				for(int i=0; i<list.size(); i++){
					RequisitionLine rl = (RequisitionLine) list.get(i);
					if(isFit(rl)){
						if(flag){
							//定位到查到的第一条记录
							flag = false;
							StructuredSelection ss = new StructuredSelection(rl);
							curViewer.setSelection(ss);
							curViewer.getTable().setSelection(i);
						}
						curViewer.setChecked(rl, true);//查找到的结果全打勾
					}
				}
				curViewer.getTable().forceFocus();//强制获得焦点为了反显第一条查询结果
			} catch (Exception e) {
				logger.error("okPressed() of SpecialQueryDialog in RequisitionLineEntityBlock", e);
			}
		    this.setVisible(false);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class klass = Class.forName(table.getModelClass());
			XZRequisitionLineProperties = new XZRequisitionLineProperties(this,table, getParentObject());
			detailsPart.registerPage(klass, XZRequisitionLineProperties);
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}
//	/**
//	 * 生成采购订单
//	 * 每月25号之后不允许添加采购申请，因为这时在采购也来不急了
//	 * 判断所有Approved
//	 * 一次性查询库存
//	 * */
//	protected void purchaseOrderAdapter() {
//		try {
//			ADManager adManager = Framework.getService(ADManager.class);
//			INVManager invManager = Framework.getService(INVManager.class);
//			List<Requisition>  requisitions = adManager.getEntityList(Env.getOrgRrn(), 
//					Requisition.class,Integer.MAX_VALUE,
//					" docStatus ='APPROVED' AND (mpsId <> 'Y' or mpsId is null )","created");
//			HashMap<Long,Storage> storageMap = new LinkedHashMap<Long,Storage>();
//			for(Requisition requisition : requisitions){
//				for(RequisitionLine requisitionLine : requisition.getPrLines()){
//					Storage storage = invManager.getMaterialStorage(Env.getOrgRrn(), requisitionLine.getMaterialRrn(), 43005950L, Env.getUserRrn());
//					if(storage!=null && storageMap.get(storage.getObjectRrn())!=null){
//						storage = storageMap.get(storage.getObjectRrn());
//					}else{
//						storageMap.put(storage.getObjectRrn(), storage);
//					}
//					BigDecimal qtyOnhand = storage.getQtyOnhand();
//					if(qtyOnhand.compareTo(BigDecimal.ZERO)==1){
//						
//					}
//					
//				}
//			}
//			refresh();
////			PURManager purmanager = Framework.getService(PURManager.class);
////			purmanager.genMovementOut(geta, Env.getOrgRrn(), arg2);
//			
//		} catch (Exception e) {
//			ExceptionHandlerManager.asyncHandleException(e);
//			return;
//		}
//	}
}

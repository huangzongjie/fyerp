package com.graly.erp.inv.iqc;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.InLineEntryBlock;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.receipt.ReceiptLineDialog;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.pur.po.POLineBlockDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.ChildEntityBlock;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.erp.base.model.Constants;
public class IqcLineEntityBlock extends ChildEntityBlock {
	Logger logger = Logger.getLogger(InLineEntryBlock.class);

	private static final String TABLE_NAME_RECEIPTLINE = "INVReceiptLine";
	private static final String TABLE_NAME_POLINE = "PURPurchaseOrderLine";
	private static final String TABLE_NAME_PO = "PURPurchaseOrder";
	private ToolItem relationShipItem, itemApprove;
	protected ToolItem itemClose;
	private ToolItem itemPreview;
	private String where;
	private Menu menu;
	private ADTable adTable;
	protected Iqc selectedRec;
	
	public IqcLineEntityBlock(EntityTableManager tableManager, String whereClause, Object parentObject) {
		super(tableManager, whereClause, parentObject);
		this.parentObject = parentObject;
	}

	@Override
	public void createToolBar(Section section) {
		final ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);		
		createToolItemRelationShip(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApprove(tBar);
		createToolItemClose(tBar);
		createToolItemPreview(tBar);
		createMenu(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemClose(ToolBar tBar) {
		itemClose = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_IQC_REVOKE);
		itemClose.setText(Message.getString("common.close"));
		itemClose.setImage(SWTResourceCache.getImage("close"));
		itemClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				closeAdapter();
			}
		});
	}
	
	protected void closeAdapter() {
		try {
			Iqc iqcMovement = (Iqc) parentObject;
			ADManager adManager = Framework.getService(ADManager.class);
			List<Movement> movements = adManager.getEntityList(Env.getOrgRrn(),Movement.class,Integer.MAX_VALUE,"poId = '"+iqcMovement.getPoId() +"' and iqcId='"+iqcMovement.getDocId()+"'",null);
			//判断IQC检验单对应的入库单是否全部CLOSED
			if(movements.size()>0){
			boolean movementIsClosed = true;
			for (Movement movement : movements) {
					if (movement.getDocStatus().equals("CLOSED"))
						continue;
					else
						movementIsClosed = false;
				}
				if (!movementIsClosed) {
					UI.showError("对不起，存在入库单没有被撤销");
					return;
				}
			}
			form.getMessageManager().removeAllMessages();
			Iqc iqc = (Iqc) getParentObject();
			if (iqc != null && iqc.getObjectRrn() != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				if(UI.showConfirm(Message.getString("common.confirm_repeal"))){
					invManager.closeIqc(iqc, Env.getUserRrn());
					UI.showInfo(Message.getString("common.close_successed"));
	
					setParentObject(adManager.getEntity((Iqc) getParentObject()));
					setParenObjectStatusChanged();
					refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
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
	
	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_IQC_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
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
	}

	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
		setParenObjectStatusChanged();
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

	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			form.getMessageManager().removeAllMessages();
			if (parentObject != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				parentObject = invManager.approveIqc((Iqc) parentObject, Env.getUserRrn());
				UI.showInfo(Message.getString("common.approve_successed"));
				refresh();
				IDetailsPage page = this.detailsPart.getCurrentPage();
				if (page instanceof IqcLineProperties) {
					((IqcLineProperties) page).refreshToolItem(false);
				}
				setParenObjectStatusChanged();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void previewAdapter() {
		try {
		    form.getMessageManager().removeAllMessages();
			String report = "iqc_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			Iqc rq = (Iqc)parentObject;
			if(rq == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			String docID = rq.getDocId();			
			userParams.put("IQCDOCID", docID);
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	private void menuPoAdapter() {
		ADTable adTablePO = getADTableOfRequisition(TABLE_NAME_PO);
		ADTable adTablePOLine = getADTableOfRequisition(TABLE_NAME_POLINE);
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			if (parentObject != null) {
				Iqc iqc = (Iqc) parentObject;
				if (iqc.getPoRrn() == null) {
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
				where = " objectRrn='" + iqc.getPoRrn() + "'";

				List<PurchaseOrder> listPO = adManager.getEntityList(Env.getOrgRrn(), PurchaseOrder.class, 2, where, "");
				PurchaseOrder po = new PurchaseOrder();
				if (listPO.size() > 0) {
					po = listPO.get(0);
				}
				where = (" poRrn = '" + po.getObjectRrn().toString() + "' ");
				POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), adTablePO, where, po, adTablePOLine, true);
				if (cd.open() == Dialog.CANCEL) {
				}
			}
		} catch (Exception e1) {
			return;
		}
	}

	private void menuReceiptAdapter() {
		adTable = getADTableOfRequisition(TABLE_NAME_RECEIPTLINE);
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			if (parentObject != null) {
				Iqc iqc = (Iqc) parentObject;
				if (iqc.getReceiptRrn() == null) {
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
				where = " objectRrn='" + iqc.getReceiptRrn() + "'";

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
			return;
		}
	}
	
	protected void setParenObjectStatusChanged() {
		Iqc iqc = (Iqc) getParentObject();
		String status = "";
		if (iqc != null && iqc.getObjectRrn() != null) {
			status = iqc.getDocStatus();
		}

		if (Iqc.STATUS_APPROVED.equals(status)) {
			itemApprove.setEnabled(false);
			itemClose.setEnabled(true);
		} else if (Iqc.STATUS_DRAFTED.equals(status)) {
			itemApprove.setEnabled(true);
			itemClose.setEnabled(false);
		} else if (Iqc.STATUS_CLOSED.equals(status)) {
			itemApprove.setEnabled(false);
			itemClose.setEnabled(false);
		} else {
			itemApprove.setEnabled(false);
			itemClose.setEnabled(false);
		}
		
		if (iqc != null && iqc.getObjectRrn() != null) {
			itemPreview.setEnabled(true);
		} else {
			itemPreview.setEnabled(false);
		}	
	}
}

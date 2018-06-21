package com.graly.erp.inv.receipt;

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
import com.graly.erp.inv.model.Receipt;
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
public class ReceiptLineEntityBlock extends ChildEntityBlock {
	Logger logger = Logger.getLogger(InLineEntryBlock.class);

	private static final String TABLE_NAME_POLINE = "PURPurchaseOrderLine";
	private static final String TABLE_NAME_PO = "PURPurchaseOrder";
	private ToolItem relationShipItem, itemApprove, itemPreview;
	private String where;
	private Menu menu;
	private ADTable adTable;
	protected ToolItem itemClose;
	protected static boolean flag;

	public ReceiptLineEntityBlock(EntityTableManager tableManager, String whereClause, Object parentObject, boolean flag) {
		super(tableManager, whereClause, parentObject);
		this.parentObject = parentObject;
		this.flag = flag;
	}

	@Override
	public void createToolBar(Section section) {
		final ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemRelationShip(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApprove(tBar);
		createToolItemClose(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreview(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createMenu(tBar);
		section.setTextClient(tBar);
	}
	protected void createToolItemClose(ToolBar tBar) {
		itemClose = new AuthorityToolItem(tBar, SWT.PUSH,
				Constants.KEY_IQC_REVOKE);
		itemClose.setText(Message.getString("common.close"));
		itemClose.setImage(SWTResourceCache.getImage("close"));
		itemClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				closeAdapter();
			}
		});
	}
	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_REC_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
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
		new MenuItem(menu, SWT.SEPARATOR);
		new MenuItem(menu, SWT.PUSH).setText(Message.getString("common.cancel"));

		menuItemPO.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuPoAdapter();
			}
		});
	}
	//撤销
	protected void closeAdapter() {
		try {
			Receipt receiptIQC = (Receipt) parentObject;
			ADManager adManager = Framework.getService(ADManager.class);
			List<Iqc> iqcDatas = adManager.getEntityList(Env.getOrgRrn(),
					Iqc.class, Integer.MAX_VALUE, "poId = '"
							+ receiptIQC.getPoId()+"' and receiptId ='"+receiptIQC.getDocId()+"'", null);
			// 判断收货单对应的IQC检验单是否全部CLOSED
			if(iqcDatas.size()>0){
			boolean iqcIsClosed = true;
			for (Iqc iqc : iqcDatas) {
					if (iqc.getDocStatus().equals("CLOSED"))
						continue;
					else
						iqcIsClosed = false;
				}

				if (!iqcIsClosed) {
					UI.showError("对不起，存在收获检验单没有被撤销");
					return;
				}
			}
			form.getMessageManager().removeAllMessages();
			if (parentObject != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				if (UI.showConfirm(Message.getString("common.confirm_repeal"))) {
					invManager.closeReceipt((Receipt) parentObject, Env
							.getUserRrn());
					UI.showInfo(Message.getString("common.close_successed"));

					setParentObject(adManager.getEntity((Receipt) getParentObject()));
					refresh();
				}
			}

		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	protected void previewAdapter() {
		 try {
			form.getMessageManager().removeAllMessages();
			String report = "inv_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			Receipt rt = (Receipt)parentObject;
			if(rt == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = rt.getObjectRrn();
			userParams.put("OBJECT_RRN", String.valueOf(objectRrn));

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
		String status = ((Receipt) parentObject).getDocStatus();
		refreshToolItem(status);
	}

	protected void refreshToolItem(String status) {
		if (Receipt.STATUS_DRAFTED.equals(status)) {
			itemApprove.setEnabled(true);
		} else {
			itemApprove.setEnabled(false);
		}
		if (flag) {
			itemApprove.setEnabled(false);
			itemPreview.setEnabled(false);
		}
	}

	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			form.getMessageManager().removeAllMessages();
			if (parentObject != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				parentObject = invManager.approveReceipt((Receipt) parentObject, Env.getUserRrn());
				UI.showInfo(Message.getString("common.approve_successed"));
				refresh();
				refreshToolItem(Receipt.STATUS_APPROVED);
				
				//控制DetailsPart按钮显示
				IDetailsPage page = this.detailsPart.getCurrentPage();
				if (page instanceof ReceiptLineProperties) {
					((ReceiptLineProperties) page).refreshToolItem(false);
				}
			}
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

	private void menuPoAdapter() {
		ADTable adTablePO = getADTableOfRequisition(TABLE_NAME_PO);
		ADTable adTablePOLine = getADTableOfRequisition(TABLE_NAME_POLINE);
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			if (parentObject != null) {
				Receipt receipt = (Receipt) parentObject;
				if (receipt.getPoRrn() == null) {
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
				where = " objectRrn='" + receipt.getPoRrn() + "'";
				List<PurchaseOrder> listPO = adManager.getEntityList(Env.getOrgRrn(), PurchaseOrder.class, 2, where, "");
				if(listPO.size() == 0){
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
				PurchaseOrder po = listPO.get(0);
				where = (" poRrn = '" + po.getObjectRrn().toString() + "' ");
				POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), adTablePO, where, po, adTablePOLine, true);
				if (cd.open() == Dialog.CANCEL) {
				}
			}
		} catch (Exception e) {
			return;
		}
	}
}

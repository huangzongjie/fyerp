package com.graly.erp.inv.receipt;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.model.ReceiptLine;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityBlock;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ReceiptLineProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(ReceiptLineProperties.class);
	private ToolItem poSearch;
	private ADTable adTable;
	private ReceiptLine receiptLine;
	private static final String TABLE_NAME = "INVPOLine";
	protected TableListManager listTableManager;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

	public ReceiptLineProperties() {
		super();
	}

	public ReceiptLineProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		super(masterParent, table, parentObject);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemPOSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemPOSearch(ToolBar tBar) {
		poSearch = new ToolItem(tBar, SWT.PUSH);
		poSearch.setText(Message.getString("pur.copyfrom"));
		poSearch.setImage(SWTResourceCache.getImage("copy"));
		poSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				poSearchAdapter();
			}
		});
	}

	protected void poSearchAdapter() {
		ReceiptLineEntityBlock receiptBlock = (ReceiptLineEntityBlock) getMasterParent();
		Receipt receipt = (Receipt) receiptBlock.getParentObject();
		String where = " poId= '" + receipt.getPoId() + "'";
		adTable = getADTableOfRequisition();
		listTableManager = new TableListManager(adTable);
		NewReceiptDialog newReceiptDialog = new NewReceiptDialog(listTableManager, null, where, style, receipt);
		if (newReceiptDialog.open() == Dialog.OK) {
			receiptBlock.setParentObject(newReceiptDialog.getReceipt());
			receiptBlock.setWhereClause(" receiptId= '" + receipt.getDocId() + "'");
		}
		getMasterParent().refresh();
		refresh();
	}

	protected void saveAdapter() {

		try {
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
					INVManager invManager = Framework.getService(INVManager.class);
					ReceiptLineEntityBlock receiptBlock = (ReceiptLineEntityBlock) getMasterParent();
					Receipt receipt = (Receipt) parentObject;
					if (((ReceiptLine) getAdObject()).getMaterialId() == "") {
						UI.showWarning(Message.getString("inv.entityisnull"));
						return;
					}
					receiptLine = invManager.saveReceiptLine(receipt, (ReceiptLine) getAdObject(), Env.getUserRrn());
					ADManager adManager = Framework.getService(ADManager.class);
					setAdObject(adManager.getEntity(receiptLine));
					UI.showInfo(Message.getString("common.save_successed"));
					
					receipt = (Receipt) adManager.getEntity(receipt);
					this.setParentObject(receipt);
					((ChildEntityBlock)getMasterParent()).setParentObject(receipt);
					refresh();
					getMasterParent().refresh();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void deleteAdapter() {
		try {
			if (((ReceiptLine) getAdObject()).getMaterialId() == "") {
				UI.showWarning(Message.getString("inv.entityisnull"));
				return;
			}
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (getAdObject().getObjectRrn() != null) {
					INVManager invManager = Framework.getService(INVManager.class);
					invManager.deleteReceiptLine((ReceiptLine) getAdObject(), Env.getUserRrn());
					setAdObject(createAdObject());
					refresh();
				}
			}
			getMasterParent().refresh();
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}

	protected ADTable getADTableOfRequisition() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("ReceiptLineProperties : getADTableOfRequisition()", e);
		}
		return null;
	}

	@Override
	protected void createSectionContent(Composite client) {
		super.createSectionContent(client);
		refreshByParentObject();
	}

	public void refreshToolItem(boolean enable) {
		poSearch.setEnabled(enable);
		itemSave.setEnabled(enable);
		itemDelete.setEnabled(enable);
	}

	public void refreshByParentObject() {
		if (parentObject != null && parentObject instanceof Receipt) {
			String status = ((Receipt) parentObject).getDocStatus();
			if (Receipt.STATUS_DRAFTED.equals(status)) {
				refreshToolItem(true);
			} else {
				refreshToolItem(false);
			}
		}
	}
}

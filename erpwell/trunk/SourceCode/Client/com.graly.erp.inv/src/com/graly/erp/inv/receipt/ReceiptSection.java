package com.graly.erp.inv.receipt;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.MaterialQueryDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ReceiptSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(ReceiptSection.class);

	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;

	protected Receipt selectedRec;
	private ADTable adTable;
	protected TableListManager listTableManager;
	private static final String TABLE_NAME_POLine = "INVPOLine";
	private static final String TABLE_NAME_ReceiptLine = "INVReceiptLine";
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

	public ReceiptSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				editAdapter();
			}
		});
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

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemEdit(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		// Add by BruceYou 2012-03-14
		createToolItemExport(tBar);
		
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_REC_CREATEFROM);
		itemNew.setText(Message.getString("pur.copyfrom"));
		itemNew.setImage(SWTResourceCache.getImage("copy"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				newAdapter();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	protected void createToolItemEdit(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_REC_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}

	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_REC_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}

	protected void newAdapter() {
		String where = " lineStatus='" + PurchaseOrder.STATUS_APPROVED + "'";
		adTable = getADTableOfRequisition(TABLE_NAME_POLine);
		listTableManager = new TableListManager(adTable);
		NewReceiptDialog poQueryDialog = new NewReceiptDialog(listTableManager, null, where, style);
		if (poQueryDialog.open() == Dialog.CANCEL) {
			refreshSection();
		}
		refresh();
	}

	protected void editAdapter() {
		try {
			if(selectedRec != null && selectedRec.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedRec = (Receipt)adManager.getEntity(selectedRec);
				Shell shell = Display.getCurrent().getActiveShell();
				adTable = getADTableOfRequisition(TABLE_NAME_ReceiptLine);
				String where = " receiptId='" + selectedRec.getDocId().toString() + "'";
				ReceiptLineDialog receiptlineDialog = new ReceiptLineDialog(shell, adTable, where, selectedRec);
				if (receiptlineDialog.open() == Dialog.CANCEL) {
					refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MOSection : editAdapter() " + e);
		}
	}

	protected void deleteAdapter() {
		if (selectedRec != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedRec.getObjectRrn() != null) {
						INVManager invManager = Framework.getService(INVManager.class);
						invManager.deleteReceipt(selectedRec, Env.getUserRrn());
						this.selectedRec = null;
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}

	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("ReceiptSection : getADTableOfRequisition()", e);
		}
		return null;
	}

	protected void refreshSection() {
		refresh();
		try {
			if (selectedRec != null && selectedRec.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedRec = (Receipt) adManager.getEntity(selectedRec);
				setStatusChanged(selectedRec.getDocStatus());
			} else {
				setStatusChanged("");
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			selectedRec = null;
			return;
		}
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof Receipt) {
			selectedRec = (Receipt) obj;
			setStatusChanged(selectedRec.getDocStatus());
		} else {
			selectedRec = null;
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
		if (Receipt.STATUS_DRAFTED.equals(status)) {
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}

	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_REC);
			queryDialog.open();
		}
	}
}

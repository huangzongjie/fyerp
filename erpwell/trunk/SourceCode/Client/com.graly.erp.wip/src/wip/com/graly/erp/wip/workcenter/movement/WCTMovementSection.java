package com.graly.erp.wip.workcenter.movement;

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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.MaterialWCAndInvoiceQueryDialog;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.po.POLineBlockDialog;
import com.graly.erp.pur.po.POSection;
import com.graly.erp.wip.model.WCTMovement;
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
import com.graly.mes.wip.client.WipManager;

public class WCTMovementSection extends MasterSection {

	private static final Logger logger = Logger.getLogger(WCTMovementSection.class);
	public static final String TABLE_NAME = "WCTMovementLine";
	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;
	protected TableListManager listTableManager;
	protected ADTable adTable;
	protected WCTMovement selectedMovement;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	ADManager adManager;
	
	public WCTMovementSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionWCTMovement(ss.getFirstElement());
				editAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionWCTMovement(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNew(tBar);
		createToolItemEdit(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}


	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WCTMOVEMENT_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}

	protected void createToolItemEdit(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WCTMOVEMENT_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}

	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WCTMOVEMENT_DELETE);
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
		String where = " 1!=1 ";
		WCTMovement wctmovement = new WCTMovement();
		wctmovement.setOrgRrn(Env.getOrgRrn());
		wctmovement.setGiver(Env.getUserRrn());
		WCTMovementLineDialog wctm = new WCTMovementLineDialog(UI.getActiveShell(), this.getTableManager().getADTable(), where, wctmovement,
				getADTableOfWCTMovementLine());
		if (wctm.open() == Dialog.CANCEL) {
			refreshSection();
		}
	}

	protected void editAdapter() {
		try {
			if (selectedMovement != null && selectedMovement.getObjectRrn() != null) {
				ADTable adTable = getADTableOfWCTMovementLine();
				ADManager adManager = Framework.getService(ADManager.class);
				selectedMovement = (WCTMovement)adManager.getEntity(selectedMovement);
				String whereClause = (" movementRrn = '" + selectedMovement.getObjectRrn().toString() + "' ");
				WCTMovementLineDialog movementLineDialog = new WCTMovementLineDialog(UI.getActiveShell(), this.getTableManager().getADTable(), whereClause,
						selectedMovement, adTable, false);
				if (movementLineDialog.open() == Dialog.CANCEL) {
					refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at POSection : editAdapter() " + e);
		}
	}

	protected void deleteAdapter() {
		if (selectedMovement != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedMovement.getObjectRrn() != null) {
						WipManager wipManager = Framework.getService(WipManager.class);
						wipManager.deleteWCTMovement(selectedMovement, Env.getUserRrn());
						this.selectedMovement = null;
						refreshSection();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialWCAndInvoiceQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_OIN);
			queryDialog.open();
		}
	}
	
	protected void refreshSection() {
		try {
			refresh();
			if (selectedMovement != null && selectedMovement.getObjectRrn() != null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				selectedMovement = (WCTMovement) adManager.getEntity(selectedMovement);
			} else {
				setStatusChanged("");
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			selectedMovement = null;
		}
	}

	protected ADTable getADTableOfWCTMovementLine() {
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

	private void setSelectionWCTMovement(Object obj) {
		if (obj instanceof WCTMovement) {
			selectedMovement = (WCTMovement) obj;
		} else {
			selectedMovement = null;
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
		if (Requisition.STATUS_DRAFTED.equals(status)) {
			itemNew.setEnabled(true);
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if (Requisition.STATUS_CLOSED.equals(status)) {
			itemNew.setEnabled(false);
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		} else if(status.equals("")){
			itemNew.setEnabled(true);
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		}
		else{
			itemNew.setEnabled(true);
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}
}

package com.graly.erp.inv.workshop.reclaim;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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

import com.graly.erp.base.MaterialQueryDialog;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.MovementWorkShopReclaim;
import com.graly.erp.inv.out.OutSection;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.SingleEntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class WorkShopReclaimSection extends MasterSection {
	
	private static final Logger logger = Logger.getLogger(OutSection.class);
	protected ToolItem itemNew;
	protected ToolItem itemTrs;
	protected ToolItem itemEdit;
	protected ToolItem itemDelete;
	private static final String TABLE_NAME = "INVMovementWSReclaimLine";
	private ADTable adTable;
	private MovementWorkShopReclaim selectedWSReclaim;
	
	public static final String KEY_WS_LOTALLOCATE = "INV.WorkShopReclaim.LotAllocate";
	public static final String KEY_WS_NEW = "INV.WorkShopReclaim.New";
	public static final String KEY_WS_EDIT = "INV.WorkShopReclaim.Edit";
	public static final String KEY_WS_DELETE = "INV.WorkShopReclaim.Delete";
	
	public WorkShopReclaimSection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionMovementOut(ss.getFirstElement());
	    		editAdapter();
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionMovementOut(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemLotDelivery(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNew(tBar);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemLotDelivery(ToolBar tBar) {
		itemTrs = new AuthorityToolItem(tBar, SWT.PUSH,KEY_WS_LOTALLOCATE);
		itemTrs.setText(Message.getString("inv.transfer"));
		itemTrs.setImage(SWTResourceCache.getImage("barcode"));
		itemTrs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				lotDeliveryAdapter();
			}
		});
	}
	
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, KEY_WS_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}
	
	protected void createToolItemEditor(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, KEY_WS_EDIT);
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
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, KEY_WS_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}

	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_TRF);
			queryDialog.open();
		}
	}

	protected ADTable getADTableOfMovementLine() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch(Exception e) {
			logger.error("TransferSection : getADTableOfMovementLine()", e);
		}
		return null;
	}
	
	
	protected void lotDeliveryAdapter() {
		ReclaimLotTrsDialog olbd = new ReclaimLotTrsDialog(UI.getActiveShell());
		if(olbd.open() == Dialog.CANCEL) {
			MovementWorkShopReclaim newTrs = ((ReclaimLotTrsSection)olbd.getLotMasterSection()).getWsReclaim();
			if(newTrs != null && newTrs.getObjectRrn() != null) {
				this.selectedWSReclaim = newTrs;
				if(selectedWSReclaim != null && selectedWSReclaim.getObjectRrn() != null)
					refreshAdd(selectedWSReclaim);
				editAdapter();
			}
		}
	}

	protected void newAdapter() {
		String whereClause = " 1!=1 ";
		MovementWorkShopReclaim wsDelivery = new MovementWorkShopReclaim();
		wsDelivery.setOrgRrn(Env.getOrgRrn());
		wsDelivery.setDocType(MovementWorkShopReclaim.DOCTYPE_HUI);
		wsDelivery.setUserCreated(Env.getUserName());
		
		WorkShopReclaimLineDialog olbd = new WorkShopReclaimLineDialog(UI.getActiveShell(),
				this.getTableManager().getADTable(), whereClause, wsDelivery, getADTableOfMovementLine());
		if(olbd.open() == Dialog.CANCEL) {
			wsDelivery = (MovementWorkShopReclaim)olbd.getParentObject();
			if (wsDelivery != null && wsDelivery.getObjectRrn() != null) {
				selectedWSReclaim = wsDelivery;
				refreshSection();
				refreshAdd(selectedWSReclaim);
			}
		}
	}
	
	protected void deleteAdapter() {
		if(selectedWSReclaim != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedWSReclaim.getObjectRrn() != null) {
						INVManager invManager = Framework.getService(INVManager.class);
						invManager.deleteMovementWorkShop(selectedWSReclaim, Env.getUserRrn());
						this.refreshDelete(selectedWSReclaim);
						this.selectedWSReclaim = null;
						refreshSection();							
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
			}
		}
	}
	
	protected void editAdapter() {
		try {
			if(selectedWSReclaim != null && selectedWSReclaim.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedWSReclaim = (MovementWorkShopReclaim)adManager.getEntity(selectedWSReclaim);
				String whereClause = ( " movementRrn = " + selectedWSReclaim.getObjectRrn().toString() + " ");
				WorkShopReclaimLineDialog cd = new WorkShopReclaimLineDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, selectedWSReclaim, getADTableOfMovementLine());
				if(cd.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedWSReclaim);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at TransferSection : editAdapter() " + e);
		}
	}
	
	protected void refreshSection() {
		try {
//			refresh();
			if(selectedWSReclaim != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedWSReclaim = (MovementWorkShopReclaim)adManager.getEntity(selectedWSReclaim);
				this.setStatusChanged(selectedWSReclaim.getDocStatus());
			}
		} catch(Exception e) {
			logger.error("Error at OutSection : refreshSection() " + e);
		}
	}
	
	private void setSelectionMovementOut(Object obj) {
		if(obj instanceof MovementWorkShopReclaim) {
			selectedWSReclaim = (MovementWorkShopReclaim)obj;
			setStatusChanged(selectedWSReclaim.getDocStatus());
		} else {
			selectedWSReclaim = null;
			setStatusChanged("");
		}
	}
	
	protected void setStatusChanged(String status) {
		if(MovementOut.STATUS_DRAFTED.equals(status)) {
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if(MovementOut.STATUS_CLOSED.equals(status)) {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}

}

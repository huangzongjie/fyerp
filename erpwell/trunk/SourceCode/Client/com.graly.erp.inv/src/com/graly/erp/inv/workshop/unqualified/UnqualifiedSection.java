package com.graly.erp.inv.workshop.unqualified;

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
import com.graly.erp.inv.DelInvMovementAuthorityManager;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.MovementWorkShop;
import com.graly.erp.inv.model.MovementWorkShopUnqualified;
import com.graly.erp.inv.out.OutSection;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
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

public class UnqualifiedSection extends MasterSection {
	
	private static final Logger logger = Logger.getLogger(OutSection.class);
	protected ToolItem itemNew;
	protected ToolItem itemTrs;
	protected ToolItem itemEdit;
	protected ToolItem itemDelete;
	private static final String TABLE_NAME = "INVMovementWSDeliveryLine";
	private ADTable adTable;
	private MovementWorkShopUnqualified selectedWSDelivery;
	
	public static final String KEY_WS_LOTALLOCATE = "INV.WorkShopDelivery.LotAllocate";
	public static final String KEY_WS_NEW = "INV.WorkShopDelivery.New";
	public static final String KEY_WS_EDIT = "INV.WorkShopDelivery.Edit";
	public static final String KEY_WS_DELETE = "INV.WorkShopDelivery.Delete";
	
	public UnqualifiedSection(EntityTableManager tableManager){
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
//		createToolItemLotDelivery(tBar);
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
		itemTrs = new AuthorityToolItem(tBar, SWT.PUSH,  KEY_WS_LOTALLOCATE);
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
		UnqualifiedLotTrsDialog olbd = new UnqualifiedLotTrsDialog(UI.getActiveShell());
		if(olbd.open() == Dialog.CANCEL) {
			MovementWorkShopUnqualified newTrs = ((UnqualifiedLotTrsSection)olbd.getLotMasterSection()).getWsDelivery();
			if(newTrs != null && newTrs.getObjectRrn() != null) {
				this.selectedWSDelivery = newTrs;
				if(selectedWSDelivery != null && selectedWSDelivery.getObjectRrn() != null)
					refreshAdd(selectedWSDelivery);
				editAdapter();
			}
		}
	}

	protected void newAdapter() {
		String whereClause = " 1!=1 ";
		MovementWorkShopUnqualified wsDelivery = new MovementWorkShopUnqualified();
		wsDelivery.setOrgRrn(Env.getOrgRrn());
		wsDelivery.setDocType(MovementWorkShopUnqualified.DOCTYPE_UNQ);
		wsDelivery.setUserCreated(Env.getUserName());
		
		UnqualifiedLineDialog olbd = new UnqualifiedLineDialog(UI.getActiveShell(),
				this.getTableManager().getADTable(), whereClause, wsDelivery, getADTableOfMovementLine());
		if(olbd.open() == Dialog.CANCEL) {
			wsDelivery = (MovementWorkShopUnqualified)olbd.getParentObject();
			if (wsDelivery != null && wsDelivery.getObjectRrn() != null) {
				selectedWSDelivery = wsDelivery;
				refreshSection();
				refreshAdd(selectedWSDelivery);
			}
		}
	}
	
	protected void deleteAdapter() {
		if(selectedWSDelivery != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedWSDelivery.getObjectRrn() != null) {
						INVManager invManager = Framework.getService(INVManager.class);
						invManager.deleteMovementWorkShop(selectedWSDelivery, Env.getUserRrn());
						this.refreshDelete(selectedWSDelivery);
						this.selectedWSDelivery = null;
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
			if(selectedWSDelivery != null && selectedWSDelivery.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedWSDelivery = (MovementWorkShopUnqualified)adManager.getEntity(selectedWSDelivery);
				String whereClause = ( " movementRrn = " + selectedWSDelivery.getObjectRrn().toString() + " ");
				UnqualifiedLineDialog cd = new UnqualifiedLineDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, selectedWSDelivery, getADTableOfMovementLine());
				if(cd.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedWSDelivery);
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
			if(selectedWSDelivery != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedWSDelivery = (MovementWorkShopUnqualified)adManager.getEntity(selectedWSDelivery);
				this.setStatusChanged(selectedWSDelivery.getDocStatus());
			}
		} catch(Exception e) {
			logger.error("Error at OutSection : refreshSection() " + e);
		}
	}
	
	private void setSelectionMovementOut(Object obj) {
		if(obj instanceof MovementWorkShopUnqualified) {
			selectedWSDelivery = (MovementWorkShopUnqualified)obj;
			setStatusChanged(selectedWSDelivery.getDocStatus());
		} else {
			selectedWSDelivery = null;
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

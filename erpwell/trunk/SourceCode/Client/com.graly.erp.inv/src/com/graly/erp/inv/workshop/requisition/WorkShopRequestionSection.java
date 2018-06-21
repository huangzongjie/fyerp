package com.graly.erp.inv.workshop.requisition;

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
import com.graly.erp.inv.model.MovementWorkShopRequestion;
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

public class WorkShopRequestionSection extends MasterSection {
	
	private static final Logger logger = Logger.getLogger(OutSection.class);
	protected ToolItem itemNew;
	protected ToolItem itemTrs;
	protected ToolItem itemEdit;
	protected ToolItem itemDelete;
	private static final String TABLE_NAME = "INVMovementWSRequestionLine";
	private ADTable adTable;
	private MovementWorkShopRequestion selectedWSRequestion;
	
	public static final String KEY_WS_LOTALLOCATE = "INV.WorkShopRequisition.LotAllocate";
	public static final String KEY_WS_NEW = "INV.WorkShopRequisition.New";
	public static final String KEY_WS_EDIT = "INV.WorkShopRequisition.Edit";
	public static final String KEY_WS_DELETE = "INV.WorkShopRequisition.Delete";
	
	public WorkShopRequestionSection(EntityTableManager tableManager){
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
		createToolItemTrs(tBar);
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

	protected void createToolItemTrs(ToolBar tBar) {
		itemTrs = new AuthorityToolItem(tBar, SWT.PUSH, KEY_WS_LOTALLOCATE);
		itemTrs.setText(Message.getString("inv.transfer"));
		itemTrs.setImage(SWTResourceCache.getImage("barcode"));
		itemTrs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				trsAdapter();
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
	
	
	protected void trsAdapter() {
		WorkShopReqLotTrsDialog olbd = new WorkShopReqLotTrsDialog(UI.getActiveShell());
		if(olbd.open() == Dialog.CANCEL) {
			MovementWorkShopRequestion newTrs = ((WorkShopReqLotTrsSection)olbd.getLotMasterSection()).getWsRequestion();
			if(newTrs != null && newTrs.getObjectRrn() != null) {
				this.selectedWSRequestion = newTrs;
				if(selectedWSRequestion != null && selectedWSRequestion.getObjectRrn() != null)
					refreshAdd(selectedWSRequestion);
				editAdapter();
			}
		}
	}

	protected void newAdapter() {
		String whereClause = " 1!=1 ";
		MovementWorkShopRequestion wsRequestion = new MovementWorkShopRequestion();
		wsRequestion.setOrgRrn(Env.getOrgRrn());
		wsRequestion.setDocType(MovementWorkShopRequestion.DOCTYPE_MAN);
		wsRequestion.setUserCreated(Env.getUserName());
		
		WorkShopRequestionLineDialog olbd = new WorkShopRequestionLineDialog(UI.getActiveShell(),
				this.getTableManager().getADTable(), whereClause, wsRequestion, getADTableOfMovementLine());
		if(olbd.open() == Dialog.CANCEL) {
			wsRequestion = (MovementWorkShopRequestion)olbd.getParentObject();
			if (wsRequestion != null && wsRequestion.getObjectRrn() != null) {
				selectedWSRequestion = wsRequestion;
				refreshSection();
				refreshAdd(selectedWSRequestion);
			}
		}
	}
	
	protected void deleteAdapter() {
		if(selectedWSRequestion != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedWSRequestion.getObjectRrn() != null) {
						INVManager invManager = Framework.getService(INVManager.class);
						invManager.deleteMovementWorkShop(selectedWSRequestion, Env.getUserRrn());
						this.refreshDelete(selectedWSRequestion);
						this.selectedWSRequestion = null;
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
			if(selectedWSRequestion != null && selectedWSRequestion.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedWSRequestion = (MovementWorkShopRequestion)adManager.getEntity(selectedWSRequestion);
				String whereClause = ( " movementRrn = " + selectedWSRequestion.getObjectRrn().toString() + " ");
				WorkShopRequestionLineDialog cd = new WorkShopRequestionLineDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, selectedWSRequestion, getADTableOfMovementLine());
				if(cd.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedWSRequestion);
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
			if(selectedWSRequestion != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedWSRequestion = (MovementWorkShopRequestion)adManager.getEntity(selectedWSRequestion);
				this.setStatusChanged(selectedWSRequestion.getDocStatus());
			}
		} catch(Exception e) {
			logger.error("Error at OutSection : refreshSection() " + e);
		}
	}
	
	private void setSelectionMovementOut(Object obj) {
		if(obj instanceof MovementWorkShopRequestion) {
			selectedWSRequestion = (MovementWorkShopRequestion)obj;
			setStatusChanged(selectedWSRequestion.getDocStatus());
		} else {
			selectedWSRequestion = null;
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

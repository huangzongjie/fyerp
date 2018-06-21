package com.graly.erp.inv.transfer.hy.dpk;

import java.math.BigDecimal;

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
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.inv.DelInvMovementAuthorityManager;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.out.OutSection;
import com.graly.erp.wip.model.ManufactureOrder;
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

public class DpkTransferSection extends MasterSection {
	
	private static final Logger logger = Logger.getLogger(OutSection.class);
	protected ToolItem itemNew;
	protected ToolItem itemTrs;
	protected ToolItem itemEdit;
	protected ToolItem itemDelete;
	protected ToolItem itemDanzi;
	private static final String TABLE_NAME = "INVMovementTransferLine";
	private ADTable adTable;
	private MovementTransfer selectedMt;
	
	public DpkTransferSection(EntityTableManager tableManager){
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
		new ToolItem(tBar, SWT.SEPARATOR);
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
		itemTrs = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_TRS_LOTALLOCATE);
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
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_TRS_NEW);
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
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_TRS_EDIT);
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
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_TRS_DELETE);
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
		DpkLotTrsDialog olbd = new DpkLotTrsDialog(UI.getActiveShell());
		if(olbd.open() == Dialog.CANCEL) {
			MovementTransfer newTrs = ((DpkLotTrsSection)olbd.getLotMasterSection()).getTransfer();
			if(newTrs != null && newTrs.getObjectRrn() != null) {
				this.selectedMt = newTrs;
				if(selectedMt != null && selectedMt.getObjectRrn() != null)
					refreshAdd(selectedMt);
				editAdapter();
			}
		}
	}

	protected void newAdapter() {
		String whereClause = " 1!=1 ";
		MovementTransfer mt = new MovementTransfer();
		mt.setOrgRrn(Env.getOrgRrn());
		DpkTransferLineDialog olbd = new DpkTransferLineDialog(UI.getActiveShell(),
				this.getTableManager().getADTable(), whereClause, mt, getADTableOfMovementLine());
		if(olbd.open() == Dialog.CANCEL) {
			mt = (MovementTransfer)olbd.getParentObject();
			if (mt != null && mt.getObjectRrn() != null) {
				selectedMt = mt;
				refreshSection();
				refreshAdd(selectedMt);
			}
		}
	}
	
	protected void deleteAdapter() {
		if(selectedMt != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedMt.getObjectRrn() != null) {
						if(DelInvMovementAuthorityManager.hasDeleteAuthority(Env.getUserRrn(),
								selectedMt.getWarehouseRrn(), selectedMt.getWarehouseId())) {
							INVManager invManager = Framework.getService(INVManager.class);
							invManager.deleteMovementTransfer(selectedMt, Env.getUserRrn());
							this.refreshDelete(selectedMt);
							this.selectedMt = null;
							refreshSection();							
						}
						
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
			}
		}
	}
	
	protected void editAdapter() {
		try {
			if(selectedMt != null && selectedMt.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedMt = (MovementTransfer)adManager.getEntity(selectedMt);
				String whereClause = ( " movementRrn = " + selectedMt.getObjectRrn().toString() + " ");
				DpkTransferLineDialog cd = new DpkTransferLineDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, selectedMt, getADTableOfMovementLine());
				if(cd.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedMt);
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
			if(selectedMt != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedMt = (MovementTransfer)adManager.getEntity(selectedMt);
				this.setStatusChanged(selectedMt.getDocStatus());
			}
		} catch(Exception e) {
			logger.error("Error at OutSection : refreshSection() " + e);
		}
	}
	
	private void setSelectionMovementOut(Object obj) {
		if(obj instanceof MovementTransfer) {
			selectedMt = (MovementTransfer)obj;
			setStatusChanged(selectedMt.getDocStatus());
		} else {
			selectedMt = null;
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

	public MovementTransfer getSelectedMt() {
		return selectedMt;
	}

	public void setSelectedMt(MovementTransfer selectedMt) {
		this.selectedMt = selectedMt;
	}
 
}

package com.graly.erp.wip.virtualhouse;

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
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.MovementWorkShopVirtualHouse;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class VirtualHouseSection extends MasterSection  {	
	private static final Logger logger = Logger.getLogger(VirtualHouseSection.class);
	private static final String TABLE_NAME = "VirtualHouseMovementLine";//平台管理
	private ADTable adTable;
	protected ToolItem itemScanLot;
	protected ToolItem itemNew;
	protected ToolItem itemEdit;
	protected ToolItem itemDelete;
	protected ToolItem itemZongCang;
	protected ToolItem itemInputQty;
	protected ADManager adManager;
	protected MovementWorkShopVirtualHouse selectedVirtualHouse;
	
	public VirtualHouseSection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemByInputQty(tBar);
		createToolItemByLotOut(tBar);
		createToolItemComplete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void newAdapter() {
		String where = " 1!=1 ";
		MovementWorkShopVirtualHouse virtualHouse = new MovementWorkShopVirtualHouse();
		virtualHouse.setOrgRrn(Env.getOrgRrn());
		VirtualHouseLineBlockDialog olbd = new VirtualHouseLineBlockDialog(UI.getActiveShell(),
				this.getTableManager().getADTable(), where, virtualHouse, getADTableOfPOLine());
		if(olbd.open() == Dialog.CANCEL) {
			virtualHouse = (MovementWorkShopVirtualHouse)olbd.getParentObject();
			if (virtualHouse != null && virtualHouse.getObjectRrn() != null) {
				selectedVirtualHouse = virtualHouse;
				refreshSection();
				refreshAdd(virtualHouse);
			}
		}
	}
	protected void refreshSection() {
		try {
			if(selectedVirtualHouse != null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				selectedVirtualHouse = (MovementWorkShopVirtualHouse)adManager.getEntity(selectedVirtualHouse);
				this.setStatusChanged(selectedVirtualHouse.getDocStatus());
			}
		} catch(Exception e) {
			logger.error("Error at OutSection : refreshSection() " + e);
		}
	}
	protected void setStatusChanged(String status) {
		if(MovementWorkShopVirtualHouse.STATUS_APPROVED.equals(status)) {
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
			itemZongCang.setEnabled(true);
		} else if(MovementWorkShopVirtualHouse.STATUS_CLOSED.equals(status)) {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
			itemZongCang.setEnabled(false);
		} else {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
			itemZongCang.setEnabled(false);
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	protected ADTable getADTableOfPOLine() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch(Exception e) {
			logger.error("AdjustOutSection : getADTableOfPOLine()", e);
		}
		return null;
	}
	
	protected void createToolItemByInputQty(ToolBar tBar) {
		itemInputQty = new ToolItem(tBar, SWT.PUSH);
		itemInputQty.setText("扫描输数");
		itemInputQty.setImage(SWTResourceCache.getImage("barcode"));
		itemInputQty.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				inputQtyAdapter();
			}
		});
	}
	
	protected void createToolItemByLotOut(ToolBar tBar) {
		itemScanLot = new ToolItem(tBar, SWT.PUSH);
		itemScanLot.setText("扫描条码");
		itemScanLot.setImage(SWTResourceCache.getImage("barcode"));
		itemScanLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				scanLotAdapter();
			}
		});
	}
	
	protected void createToolItemComplete(ToolBar tBar) {
		itemZongCang = new AuthorityToolItem(tBar, SWT.PUSH, "INV.VirtualHouse.confirm");
		itemZongCang.setText("总仓点单");
		itemZongCang.setImage(SWTResourceCache.getImage("barcode"));
		itemZongCang.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				completeAdapter();
			}
		});
	}
	
	protected void inputQtyAdapter() {
		ByLotVirtualHouseInputQtyDialog olbd = new ByLotVirtualHouseInputQtyDialog(UI.getActiveShell());
		if(olbd.open() == Dialog.CANCEL) {
//			MovementWorkShopVirtualHouse out = ((ByLotVirtualHouseSection)olbd.getVirtualHouseLotSection()).getVirtualHouse();
//			if(out != null && out.getObjectRrn() != null) {
//				this.selectedVirtualHouse = out;
//				if(selectedVirtualHouse != null && selectedVirtualHouse.getObjectRrn() != null)
//					refreshAdd(selectedVirtualHouse);
//				editAdapter();
//			}
			
		}
		refresh();
	}
	 
	protected void scanLotAdapter() {
		ByLotVirtualHouseDialog olbd = new ByLotVirtualHouseDialog(UI.getActiveShell());
		if(olbd.open() == Dialog.CANCEL) {
//			MovementWorkShopVirtualHouse out = ((ByLotVirtualHouseSection)olbd.getVirtualHouseLotSection()).getVirtualHouse();
//			if(out != null && out.getObjectRrn() != null) {
//				this.selectedVirtualHouse = out;
//				if(selectedVirtualHouse != null && selectedVirtualHouse.getObjectRrn() != null)
//					refreshAdd(selectedVirtualHouse);
//				editAdapter();
//			}
			
		}
		refresh();
	}

	protected void editAdapter() {
		try {
			if(selectedVirtualHouse != null && selectedVirtualHouse.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedVirtualHouse = (MovementWorkShopVirtualHouse)adManager.getEntity(selectedVirtualHouse);
				String whereClause = ( " movementRrn = '" + selectedVirtualHouse.getObjectRrn().toString() + "' ");
				VirtualHouseLineBlockDialog cd = new VirtualHouseLineBlockDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, selectedVirtualHouse, getADTableOfPOLine());
				if(cd.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedVirtualHouse);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at AdjustOutSection : editAdapter() " + e);
		}
	}

	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OOU_NEW);
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
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH,"INV.VirtualHouse.Edit");
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
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, "INV.VirtualHouse.Delete");
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
	private void setSelectionMovementOut(Object obj) {
		if(obj instanceof MovementWorkShopVirtualHouse) {
			selectedVirtualHouse = (MovementWorkShopVirtualHouse)obj;
			setStatusChanged(selectedVirtualHouse.getDocStatus());
		} else {
			selectedVirtualHouse = null;
			setStatusChanged("");
		}
	}
	
	protected void deleteAdapter() {
		if (selectedVirtualHouse != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedVirtualHouse.getObjectRrn() != null) {
						INVManager invManager = Framework.getService(INVManager.class);
						invManager.deleteMovementWorkShopVirtualHouse(selectedVirtualHouse, false, Env.getUserRrn());
						this.refreshDelete(selectedVirtualHouse);
						this.selectedVirtualHouse = null;
						refreshSection();							
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	
	protected void completeAdapter() {
		if (selectedVirtualHouse != null) {
			try {
				boolean confirmDelete = UI.showConfirm("确定点单");
				if (confirmDelete) {
					if (selectedVirtualHouse.getObjectRrn() != null) {
						INVManager invManager = Framework.getService(INVManager.class);
						invManager.deleteMovementWorkShopVirtualHouse(selectedVirtualHouse, true, Env.getUserRrn());
						this.refreshDelete(selectedVirtualHouse);
						this.selectedVirtualHouse = null;
						refreshSection();							
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
}

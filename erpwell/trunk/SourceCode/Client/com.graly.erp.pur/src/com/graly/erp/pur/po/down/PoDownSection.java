package com.graly.erp.pur.po.down;

import java.util.HashMap;
import java.util.Map;

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
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.po.query.PoQueryDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;


public class PoDownSection extends MasterSection {
	public static final Map<Long, String> DELIVERY_ADDRESS = new HashMap<Long, String>();
	static {
		DELIVERY_ADDRESS.put(139420L, "上海市浦东新区川大路518号");
		DELIVERY_ADDRESS.put(12644730L, "上海市浦东新区川展路588号");
	}
	private static final Logger logger = Logger.getLogger(PoDownSection.class);
	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;
	protected PurchaseOrder selectedPO;
	private static final String TABLE_NAME = "PURPurchaseOrderLine";
	private ADTable adTable;
	protected PoDownDialog poDownDialog;
	public PoDownSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionPO(ss.getFirstElement());
				editAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionPO(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
	//	createToolItemNew(tBar);
	//	createToolItemEdit(tBar);
	//	new ToolItem(tBar, SWT.SEPARATOR);
	//	createToolItemDelete(tBar);
	//	new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		
		// Add by BruceYou 2012-03-14
		createToolItemExport(tBar);
		
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_NEW);
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
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_EDIT);
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
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_DELETE);
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
		PurchaseOrder po = new PurchaseOrder();
		po.setDeliveryAddress(PoDownSection.DELIVERY_ADDRESS.get(Env.getOrgRrn()));
		po.setOrgRrn(Env.getOrgRrn());
		po.setPaymentRule11("Y");//默认开具发票
		po.setInvoiceType(PurchaseOrder.INVOICE_TYPE_REGULAR);//发票类型默认为普通发票
		PoDownLineBlockDialog cd = new PoDownLineBlockDialog(UI.getActiveShell(), this.getTableManager().getADTable(), where, po,
				getADTableOfPOLine());
		if (cd.open() == Dialog.CANCEL) {
			refreshSection();
		}
	}

	protected void editAdapter() {
		try {
			if (selectedPO != null && selectedPO.getObjectRrn() != null) {
				ADTable adTable = getADTableOfPOLine();
				ADManager adManager = Framework.getService(ADManager.class);
				selectedPO = (PurchaseOrder)adManager.getEntity(selectedPO);
				String whereClause = (" poRrn = '" + selectedPO.getObjectRrn().toString() + "' ");
				PoDownLineBlockDialog cd = new PoDownLineBlockDialog(UI.getActiveShell(), this.getTableManager().getADTable(), whereClause, selectedPO,
						adTable);
				if (cd.open() == Dialog.CANCEL) {
					refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at POSection : editAdapter() " + e);
		}
	}

	protected void deleteAdapter() {
		if (selectedPO != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedPO.getObjectRrn() != null) {
						PURManager purManager = Framework.getService(PURManager.class);
						purManager.deletePO(selectedPO, Env.getUserRrn());
						this.selectedPO = null;
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	

	protected void refreshSection() {
		try {
			refresh();
			if (selectedPO != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedPO = (PurchaseOrder) adManager.getEntity(selectedPO);
				this.setStatusChanged(selectedPO.getDocStatus());
			}
		} catch (Exception e) {
			logger.error("Error at POSection : refreshSection() " + e);
		}
	}

	protected ADTable getADTableOfPOLine() {
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

	private void setSelectionPO(Object obj) {
		if (obj instanceof PurchaseOrder) {
			selectedPO = (PurchaseOrder) obj;
			setStatusChanged(selectedPO.getDocStatus());
		} else {
			selectedPO = null;
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
		if (PurchaseOrder.STATUS_DRAFTED.equals(status)) {
//			itemEdit.setEnabled(true);
//			itemDelete.setEnabled(true);
		} else if (PurchaseOrder.STATUS_CLOSED.equals(status)) {
//			itemEdit.setEnabled(false);
//			itemDelete.setEnabled(false);
		} else {
//			itemEdit.setEnabled(false);
//			itemDelete.setEnabled(false);
		}
	}

//	@Override
//	protected void queryAdapter() {
//		if (queryDialog != null) {
//			queryDialog.setVisible(true);
//		} else {
//			queryDialog =  new PoDownMoreQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_TPO);
//			queryDialog.open();
//		}
//	}
	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} 
//		else {
//			queryDialog =  new PoDownDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_TPO);
//			queryDialog.open();
//		}
	}
	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof PoDownDialog) {
			this.poDownDialog = (PoDownDialog)dialog;
		} else {
			this.poDownDialog = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	private static final Logger logger = Logger.getLogger(PoDownSection.class);
//	protected PoDownLineBlockDialog poDownQueryDialog;
//	protected PoDownMoreQueryDialog poDownDialog;
//	protected PurchaseOrder selectedPO;
//	private ADTable adTable;
//	private static final String TABLE_NAME = "PURPurchaseOrderLine";
//
//	public PoDownSection(EntityTableManager tableManager) {
//		super(tableManager);
//	}
//	@Override
//	protected void createViewAction(StructuredViewer viewer) {
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
////				StructuredSelection ss = (StructuredSelection) event.getSelection();
////				VPoDown v =(VPoDown)ss.getFirstElement();
////				long i =v.getPoRrn();
////				v.setObjectRrn(i);
////				PurchaseOrder po = new PurchaseOrder();
////				po.setObjectRrn(i);
////				Object obj = null;
////				try {
////					ADManager adManager = Framework.getService(ADManager.class);
////					 obj = adManager.getEntity(po);
////				} catch (Exception e) {
////					e.printStackTrace();
////				}
////				setSelectionPO(obj);
//				StructuredSelection ss = (StructuredSelection) event.getSelection();
//				setSelectionPO(ss.getFirstElement());
//
//				editAdapter();
//			}
//		});
//		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent event) {
//				try {
//					StructuredSelection ss = (StructuredSelection) event.getSelection();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//	private void setSelectionPO(Object obj) {
//		if (obj instanceof PurchaseOrder) {
//			selectedPO = (PurchaseOrder) obj;
////			setStatusChanged(selectedPO.getDocStatus());
//		} else {
//			selectedPO = null;
//			//setStatusChanged("");
//		}
//	}
//	protected void editAdapter() {
//		try {
//			if (selectedPO != null && selectedPO.getObjectRrn() != null) {
//				ADTable adTable = getADTableOfPOLine();
//				ADManager adManager = Framework.getService(ADManager.class);
//				selectedPO = (PurchaseOrder)adManager.getEntity(selectedPO);
//				String whereClause = (" poRrn = '" + selectedPO.getObjectRrn().toString() + "' ");
//				PoDownLineBlockDialog cd = new PoDownLineBlockDialog(UI.getActiveShell(), this.getTableManager().getADTable(), whereClause, selectedPO,
//						adTable);
//				if (cd.open() == Dialog.CANCEL) {
//					refreshSection();
//				}
//			}
//		} catch(Exception e) {
//			ExceptionHandlerManager.asyncHandleException(e);
//			logger.error("Error at POSection : editAdapter() " + e);
//		}
//	}
//	protected ADTable getADTableOfPOLine() {
//		try {
//			if (adTable == null) {
//				ADManager entityManager = Framework.getService(ADManager.class);
//				adTable = entityManager.getADTable(0L, TABLE_NAME);
//				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
//			}
//			return adTable;
//		} catch (Exception e) {
//			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
//		}
//		return null;
//	}
//	protected void refreshSection() {
//		try {
//			refresh();
//			if (selectedPO != null) {
//				ADManager adManager = Framework.getService(ADManager.class);
//				selectedPO = (PurchaseOrder) adManager.getEntity(selectedPO);
//			}
//		} catch (Exception e) {
//			logger.error("Error at POSection : refreshSection() " + e);
//		}
//	}
//	public void createToolBar(Section section) {
//		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemSearch(tBar);
//		section.setTextClient(tBar);
//	}
//	
//
//	@Override
//	protected void queryAdapter() {
//		if (queryDialog != null) {
//			queryDialog.setVisible(true);
//		} else {
//			queryDialog =  new PoDownMoreQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_TPO);
//			queryDialog.open();
//		}
//	}
	
	
}

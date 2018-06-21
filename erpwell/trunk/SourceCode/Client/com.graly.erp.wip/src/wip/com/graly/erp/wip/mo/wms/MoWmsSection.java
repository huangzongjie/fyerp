package com.graly.erp.wip.mo.wms;

import org.apache.log4j.Logger;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.StockIn;
import com.graly.erp.inv.model.StockOut;
import com.graly.erp.wip.mo.wms.out.WmsMoLineInDialog;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.workcenter.receive.WmsMoLineOutDialog;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class MoWmsSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MoWmsSection.class);
	protected ToolItem itemDelete;
	protected StockIn selectSi;

	
	public MoWmsSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemDelete(tBar);
		createToolItemIn(tBar);
		createToolItemOut(tBar);
		createToolItemExport(tBar);
		createToolItemSearch(tBar);
		section.setTextClient(tBar);
	}

	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
//	protected void createNewViewer(Composite client, final IManagedForm form){
//		viewer = listTableManager.createViewer(client, form.getToolkit());
//	}
//	
//	 
//	protected ADTable getADTable() {
//		return listTableManager.getADTable();
//	}
	protected void createToolItemOut(ToolBar tBar) {
		ToolItem itemOut = new ToolItem(tBar, SWT.PUSH);
		itemOut.setText("半成品出库");
		itemOut.setImage(SWTResourceCache.getImage("save"));
		itemOut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				wmsOutAdapter();
			}
		});
	}
	protected void createToolItemDelete(ToolBar tBar) {
 
//		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, table.getAuthorityKey() + "." + ITEM_DELETE);	
		itemDelete = new ToolItem(tBar, SWT.PUSH);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	public boolean deleteAdapter() {
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (selectSi!=null && selectSi.getObjectRrn() != null && selectSi.getWmsRead()==1L) {
					ADManager entityManager = Framework.getService(ADManager.class);
					selectSi.setWmsRead(3L);
					entityManager.saveEntity(selectSi, Env.getUserRrn());
					refresh();
					return true;
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
		return false;
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionStockIn(ss.getFirstElement());
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionStockIn(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private void setSelectionStockIn(Object obj) {
		if(obj instanceof StockIn){
			selectSi = (StockIn) obj;
		}else{
			selectSi =null;
		}
		
	}
	
	protected void wmsOutAdapter() {

		String tableName = "WMSSubMoLineOut";
		ADTable invAdTable = null;
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			invAdTable = adManager.getADTable(0L, tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TableListManager listTableManager = new TableListManager(invAdTable);
		int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		StockOut out = new StockOut();
		out.setOrgRrn(Env.getOrgRrn());
		out.setIsActive(true);
		WmsMoLineOutDialog outDialog = new WmsMoLineOutDialog(listTableManager, null, null,
				style, out);
		if (outDialog.open() == IDialogConstants.OK_ID) {

		}
		outDialog.setOut(null);
	}
	
	protected void createToolItemIn(ToolBar tBar) {
		ToolItem itemIn = new ToolItem(tBar, SWT.PUSH);
		itemIn.setText("半成品入库");
		itemIn.setImage(SWTResourceCache.getImage("save"));
		itemIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				wmsInAdapter();
			}
		});
	}
	protected void wmsInAdapter() {

		String tableName = "WMSSubMoLine";
		ADTable invAdTable = null;
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			invAdTable = adManager.getADTable(0L, tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TableListManager listTableManager = new TableListManager(invAdTable);
		int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		ManufactureOrderLine moLine = new ManufactureOrderLine();
		WmsMoLineInDialog invDialog = new WmsMoLineInDialog(listTableManager, null, null,
				style, moLine,new Lot());
		if (invDialog.open() == IDialogConstants.OK_ID) {

		}
		invDialog.setParentLot(null);
		invDialog.setSelectMoLine(null);
		invDialog.setMoQty(null);
	}
}

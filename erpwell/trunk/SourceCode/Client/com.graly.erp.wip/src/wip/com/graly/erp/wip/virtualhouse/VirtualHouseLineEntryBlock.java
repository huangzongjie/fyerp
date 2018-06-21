package com.graly.erp.wip.virtualhouse;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.MovementWorkShopVirtualHouse;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ParentChildEntityBlock;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
public class VirtualHouseLineEntryBlock extends ParentChildEntityBlock {
	protected boolean flag = false;
	
	
	public VirtualHouseLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable) {
		super(parentTable, parentObject, whereClause, childTable);
	}
	
	public VirtualHouseLineEntryBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable, boolean flag){
		super(parentTable, parentObject, whereClause, childTable);
		this.flag = flag;
	}

	private static final Logger logger = Logger.getLogger(VirtualHouseLineEntryBlock.class);
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
		refresh();
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
//				setMovementLineSelect(ss.getFirstElement());
				lotAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
//					setMovementLineSelect(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
//		MovementIn movementIn = (MovementIn) parentObject;
//		refreshAll(movementIn.getDocStatus());
	}
	
	
	private void setMovementWorkShopVSelect(Object obj) {
//		if (obj instanceof MovementWLine) {
//			selectMovementLine = (MovementLine) obj;
//		} else {
//			selectMovementLine = null;
//		}
	}
	protected ToolItem itemLot;
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemGenerateLot(tBar);
		section.setTextClient(tBar);
	}
	protected void createToolItemGenerateLot(ToolBar tBar) {
		itemLot = new ToolItem(tBar, SWT.PUSH);
		itemLot.setText(Message.getString("inv.lot"));
		itemLot.setImage(SWTResourceCache.getImage("barcode"));
		itemLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				lotAdapter();
			}
		});
	}
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class<?> klass = Class.forName(table.getModelClass());
			detailsPart.registerPage(klass, new VirtualHouseLineProperties(this, table, getParentObject(),flag));
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}
	
	public void refresh() {
		super.refresh();
//		doViewerAggregation();
//		if (parentObject instanceof MovementIn) {
//			MovementIn parent = (MovementIn) parentObject;
//			refreshToolItem(parent.getDocStatus());
//		}
	}
	
	protected void lotAdapter() {
		try {
			MovementWorkShopVirtualHouse virtualHouse = (MovementWorkShopVirtualHouse)parentObject;
			if (virtualHouse != null && virtualHouse.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				virtualHouse = (MovementWorkShopVirtualHouse)adManager.getEntity(virtualHouse);
				parentObject = virtualHouse;
				List<MovementWorkShopLine> lines = new ArrayList<MovementWorkShopLine>();
//				if (selectMovementLine != null) {
//					selectMovementLine = (MovementLine)adManager.getEntity(selectMovementLine);					
//				}
				lines = adManager.getEntityList(Env.getOrgRrn(), MovementWorkShopLine.class, 
						Env.getMaxResult(), getWhereClause(), null);
				if(lines == null || lines.size() == 0)
					return;
				VirtualHouseLineLotDialog dialog =new VirtualHouseLineLotDialog(UI.getActiveShell(),(MovementWorkShopVirtualHouse)parentObject,null, lines, flag);

				if (dialog.open() == Dialog.CANCEL) {
					virtualHouse = (MovementWorkShopVirtualHouse)adManager.getEntity((MovementWorkShopVirtualHouse)virtualHouse);
					parentObject = virtualHouse;
//					selectMovementLine = null;
					this.viewer.setSelection(null);
					refresh();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
}

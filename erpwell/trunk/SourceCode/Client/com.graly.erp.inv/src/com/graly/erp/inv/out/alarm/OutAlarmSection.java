package com.graly.erp.inv.out.alarm;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.OutLineBlockDialog;
import com.graly.erp.sal.client.SALManager;
import com.graly.erp.sal.model.SalesOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class OutAlarmSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(OutAlarmSection.class);

	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;

	protected SalesOrder salesOrder;
	protected TableListManager listTableManager;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

	public OutAlarmSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionMovementOut(ss.getFirstElement());
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionMovementOut(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemGenMovementOut(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemGenMovementOut(ToolBar tBar) {
		String authorityToolItem = "Alarm.Iqc.Note";
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH,authorityToolItem);
		itemDelete.setText("生成销货出库单");
		itemDelete.setImage(SWTResourceCache.getImage("save"));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				movementOutAdapter();
			}
		});
	}

	protected void movementOutAdapter() {
		if (salesOrder != null && salesOrder.getSerialNumber()!=null) {
			try {
				boolean confirmGen =  UI.showConfirm("是否根据选中的销售订单生成销售出库单");
				if(confirmGen){
					SALManager salManager = Framework.getService(SALManager.class);
					MovementOut out = salManager.createMovementOutFromSo(Env.getOrgRrn(), salesOrder.getSerialNumber(), Env.getUserRrn());
					INVManager invManager = Framework.getService(INVManager.class);
					out = invManager.saveMovementOutLine(out, out.getMovementLines(), MovementOut.OutType.SOU, Env.getUserRrn());
//					UI.showInfo("成功"+out.getDocId());
					if(out != null) {
						String whereClause = " movementRrn = " + out.getObjectRrn() + " ";
						out.setOrgRrn(Env.getOrgRrn());
						OutLineBlockDialog olbd = new OutLineBlockDialog(UI.getActiveShell(),
								getMovementOutADTable(), whereClause, out, getADTableOfPOLine());
						olbd.setSouFlag(true);
						if(olbd.open() == Dialog.CANCEL) {
						}
					}
				}
				refresh();
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				refresh();
				return;
			}
		}
	}
	
	@Override
	public void refresh() {
		List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();//界面上显示的AlarmData数据
		try {
			SALManager salManager = Framework.getService(SALManager.class);
			salesOrders  =  salManager.getSelesOrderList(1000, getADTable().getWhereClause(), " deliverDate DESC ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewer.setInput(salesOrders);
		this.createSectionDesc(salesOrders);
	}

	private void setSelectionMovementOut(Object obj) {
		if (obj instanceof SalesOrder) {
			salesOrder = (SalesOrder) obj;
		} else {
			salesOrder = null;
		}
	}


	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	protected void createSectionDesc(List<SalesOrder> salesOrders){
		try{ 
			String text = Message.getString("common.totalshow");
			long count = salesOrders.size();
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("MasterSection : createSectionDesc ", e);
		}
	}
	
	protected void createSectionDesc(Section section){
	}
	
	
	protected ADTable getADTableOfPOLine() {
		try {
			ADTable adTable = null;
			String TABLE_NAME = "INVMovementOutLine";
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, TABLE_NAME);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	protected ADTable getMovementOutADTable() {
		try {
			ADTable adTable = null;
			String TABLE_NAME = "INVMovementOut";
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, TABLE_NAME);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
}

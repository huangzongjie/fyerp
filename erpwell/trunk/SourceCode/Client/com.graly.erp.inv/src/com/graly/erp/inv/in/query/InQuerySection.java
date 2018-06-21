package com.graly.erp.inv.in.query;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.QuerySection;
import com.graly.erp.inv.in.InLineDialog;
import com.graly.erp.inv.in.MoInOfLotDialog;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.VInDetail;
import com.graly.erp.inv.otherin.OtherInLineDialog;
import com.graly.erp.inv.out.adjust.AdjustOutLineBlockDialog;
import com.graly.erp.inv.transfer.TransferLineDialog;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class InQuerySection extends QuerySection {
	private static final Logger logger = Logger.getLogger(InQuerySection.class);
	
	protected final String PIN_LINE_TABLE = "INVMovementLine";
	protected final String PIN_TABLE = "INVMovementIn";
	
	protected final String OIN_LINE_TABLE = "INVOINMovementLine";
	protected final String OIN_TABLE = "INVOtherIn";
	
	protected final String WIN_LINE_TABLE = "WIPMOMovementInLine";
	protected final String WIN_TABLE = "WIPMOMovementIn";
	
	protected final String TRF_LINE_TABLE = "INVMovementTransferLine";
	protected final String TRF_TABLE = "INVMovementTransfer";
	
	//Add by BruceYou 2012-03-19
	protected final String AOU_LINE_TABLE = "INVMovementAdjustOutLine";
	protected final String AOU_TABLE = "INVMovementAdjustOut";
	
	protected ADTable parentAdTable;
	protected ADTable childAdTable;
	protected Movement selectedIn;	

	
	public InQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1");
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				detailAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void setSelectionRequisition(Object obj) {
		if (obj instanceof VInDetail) {
			selectedIn = getMovementInFromVInDetail((VInDetail) obj);
		} else {
			selectedIn = null;
		}
	}

	@Override
	protected void detailAdapter() {
		if(selectedIn.getObjectRrn() == null) return;
		setWhereClause(" movementId='" + selectedIn.getDocId().toString() + "'");
		if((MovementIn.InType.PIN.toString()).equals(selectedIn.getDocType())){
			parentAdTable = getADTableOfRequisition(PIN_TABLE);
			childAdTable = getADTableOfRequisition(PIN_LINE_TABLE);
			InLineDialog inLineDialog = new InLineDialog(UI.getActiveShell(), parentAdTable, getWhereClause(), selectedIn,
					childAdTable, true);
			inLineDialog.open();
		}else if((MovementIn.InType.OIN.toString()).equals(selectedIn.getDocType())){
			parentAdTable = getADTableOfRequisition(OIN_TABLE);
			childAdTable = getADTableOfRequisition(OIN_LINE_TABLE);
			OtherInLineDialog otherInLineDialog = new OtherInLineDialog(UI.getActiveShell(), parentAdTable, getWhereClause(), selectedIn,
					childAdTable,true);
			otherInLineDialog.open();
		}else if((MovementIn.InType.WIN.toString()).equals(selectedIn.getDocType())){
//			parentAdTable = getADTableOfRequisition(WIN_TABLE);
//			childAdTable = getADTableOfRequisition(WIN_LINE_TABLE);
			ManufactureOrder mo = new ManufactureOrder();
			ADManager adManager;
			try {
				adManager = Framework.getService(ADManager.class);
				setWhereClause(" docId = '" + selectedIn.getMoId() + "'");
				List<ManufactureOrder> moList = adManager.getEntityList(Env.getOrgRrn(), ManufactureOrder.class, 1, getWhereClause(), "");
				if (moList != null)
					mo = moList.get(0);

				MoInOfLotDialog moInOfLotDialog = new MoInOfLotDialog(UI.getActiveShell(), mo, (MovementIn) selectedIn);
				moInOfLotDialog.open();
			} catch (Exception e) {
				e.printStackTrace();
				ExceptionHandlerManager.asyncHandleException(e);
			}
		}else if(MovementIn.DOCTYPE_TRF.equals(selectedIn.getDocType())){
			parentAdTable = getADTableOfRequisition(TRF_TABLE);
			childAdTable = getADTableOfRequisition(TRF_LINE_TABLE);
			setWhereClause(" movementRrn = " + selectedIn.getObjectRrn().toString() + " ");
			TransferLineDialog cd = new TransferLineDialog(UI.getActiveShell(),	parentAdTable, getWhereClause(), selectedIn, childAdTable, true);
			cd.open();
		}
		// Add By BruceYou 2012-03-22
		//调整出库数量为负数，视为调整入库，故此处添加此种类型的入库明细查询
		else if("AOU".equals(selectedIn.getDocType())){
			String whereClause = " movementRrn='" + selectedIn.getObjectRrn().toString() + "'";
			parentAdTable = getADTableOfRequisition(AOU_TABLE);
			childAdTable = getADTableOfRequisition(AOU_LINE_TABLE);
			AdjustOutLineBlockDialog cd = new AdjustOutLineBlockDialog(UI.getActiveShell(),
					parentAdTable, whereClause, selectedIn, childAdTable,true);
			cd.open();
		}
	}
	
	protected ADTable getADTableOfRequisition(String tableName) {
		ADTable adTable = null;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("InSection : getADTableOfRequisition()", e);
		}
		return null;
	}
	
	private Movement getMovementInFromVInDetail(VInDetail object){
		Movement mov = new Movement();
		mov.setObjectRrn(object.getMovmentRrn());
		try {
			ADManager manager = Framework.getService(ADManager.class);
			if(mov.getObjectRrn() != null)
				mov = (Movement) manager.getEntity(mov);
		} catch (Exception e) {
			logger.error("error in Method getMovementInFromVInDetail() : InQuerySection",e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return mov;
	}
	
	@Override
	protected void createToolItemDetail(ToolBar tBar) {
		detailItem = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_INQUERY_VIEWDETAIL);
		detailItem.setText(Message.getString("inv.see_details"));
		detailItem.setImage(SWTResourceCache.getImage("lines"));
		detailItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				detailAdapter();
			}
		});
	}
	
	@Override
	public void refresh(){
		super.refresh();
		Table table = ((TableViewer)viewer).getTable();
		VInDetail totalDetail = new VInDetail();
		totalDetail.setDocId(Message.getString("inv.total"));
		BigDecimal totalQty = BigDecimal.ZERO;
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof VInDetail) {
				VInDetail inDetail = (VInDetail)obj;
				if (inDetail.getQtyMovement() != null) {
					totalQty = totalQty.add(inDetail.getQtyMovement());
				}
				if (inDetail.getLineTotal() != null) {
					totalPrice = totalPrice.add(inDetail.getLineTotal());
				}
			}
		}
		totalDetail.setQtyMovement(totalQty);
		totalDetail.setLineTotal(totalPrice);
		TableViewer tv = (TableViewer)viewer;
		tv.insert(totalDetail, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"宋体",10,SWT.BOLD); 
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}
}

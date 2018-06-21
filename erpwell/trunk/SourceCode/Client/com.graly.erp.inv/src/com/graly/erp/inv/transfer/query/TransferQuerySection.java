package com.graly.erp.inv.transfer.query;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.graly.erp.inv.QuerySection;
import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.in.InLineDialog;
import com.graly.erp.inv.in.MoInOfLotDialog;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.VInDetail;
import com.graly.erp.inv.model.VOutDetail;
import com.graly.erp.inv.model.VTrfDetail;
import com.graly.erp.inv.otherin.OtherInLineDialog;
import com.graly.erp.inv.transfer.TransferLineDialog;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class TransferQuerySection extends QuerySection {
	private static final Logger logger = Logger.getLogger(TransferQuerySection.class);
	
	protected final String TRF_LINE_TABLE = "INVMovementTransferLine";
	protected final String TRF_TABLE = "INVMovementTransfer";
		
	protected ADTable parentAdTable;
	protected ADTable childAdTable;
	protected Movement selectedIn;	
	
	public TransferQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1 ");
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
		if (obj instanceof VTrfDetail) {
			selectedIn = getMovementInFromVTrfDetail((VTrfDetail) obj);
		} else {
			selectedIn = null;
		}
	}

	@Override
	protected void detailAdapter() {
		if(selectedIn.getObjectRrn() == null) return;
		setWhereClause(" movementId='" + selectedIn.getDocId().toString() + "'");
		if(MovementIn.DOCTYPE_TRF.equals(selectedIn.getDocType())){
			parentAdTable = getADTableOfRequisition(TRF_TABLE);
			childAdTable = getADTableOfRequisition(TRF_LINE_TABLE);
			setWhereClause(" movementRrn = " + selectedIn.getObjectRrn().toString() + " ");
			TransferLineDialog cd = new TransferLineDialog(UI.getActiveShell(),	parentAdTable, getWhereClause(), selectedIn, childAdTable, true);
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
			logger.error("TransferQuerySection : getADTableOfRequisition()", e);
		}
		return null;
	}
	
	private Movement getMovementInFromVTrfDetail(VTrfDetail object){
		Movement mov = new Movement();
		mov.setObjectRrn(object.getMovmentRrn());
		try {
			ADManager manager = Framework.getService(ADManager.class);
			if(mov.getObjectRrn() != null)
				mov = (Movement) manager.getEntity(mov);
		} catch (Exception e) {
			logger.error("error in Method getMovementInFromVInDetail() : TransferQuerySection",e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return mov;
	}
	
	@Override
	public void refresh() {
		super.refresh();
		Table table = ((TableViewer)viewer).getTable();
		VTrfDetail totalDetail = new VTrfDetail();
		totalDetail.setDocId(Message.getString("inv.total"));
		BigDecimal totalQty = BigDecimal.ZERO;
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof VTrfDetail) {
				VTrfDetail trfDetail = (VTrfDetail)obj;
				if (trfDetail.getQtyMovement() != null) {
					totalQty = totalQty.add(trfDetail.getQtyMovement());
				}
				if (trfDetail.getLineTotal() != null) {
					totalPrice = totalPrice.add(trfDetail.getLineTotal());
				}
			}
		}
		totalDetail.setQtyMovement(totalQty);
		totalDetail.setLineTotal(totalPrice);
		TableViewer tv = (TableViewer)viewer;
		tv.insert(totalDetail, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"ËÎÌו",10,SWT.BOLD); 
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}
}

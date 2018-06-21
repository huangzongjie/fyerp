package com.graly.erp.inv.lot;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.erp.inv.in.InLineDialog;
import com.graly.erp.inv.in.MoInOfLotDialog;
import com.graly.erp.inv.in.query.InQuerySection;
import com.graly.erp.inv.model.InvLot;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.otherin.OtherInLineDialog;
import com.graly.erp.inv.out.adjust.AdjustOutLineBlockDialog;
import com.graly.erp.inv.transfer.TransferLineDialog;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class InvLotQuerySection extends MasterSection{
	private static final Logger logger = Logger.getLogger(InvLotQuerySection.class);
	
	protected final String PIN_LINE_TABLE = "INVMovementLine";
	protected final String PIN_TABLE = "INVMovementIn";
	
	protected final String OIN_LINE_TABLE = "INVOINMovementLine";
	protected final String OIN_TABLE = "INVOtherIn";
	
	protected final String WIN_LINE_TABLE = "WIPMOMovementInLine";
	protected final String WIN_TABLE = "WIPMOMovementIn";
	
	protected final String TRF_LINE_TABLE = "INVMovementTransferLine";
	protected final String TRF_TABLE = "INVMovementTransfer";
	
	protected final String AOU_LINE_TABLE = "INVMovementAdjustOutLine";
	protected final String AOU_TABLE = "INVMovementAdjustOut";
	
	protected ADTable parentAdTable;
	protected ADTable childAdTable;
	protected Movement selectedIn;	
	public InvLotQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1 ");
	}
	
	@Override
	protected void createNewViewer(Composite client, final IManagedForm form){
		final ADTable table = getTableManager().getADTable();
		viewer = getTableManager().createViewer(client, form.getToolkit());
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
							detailAdapter();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
				
				
				
				//detailAdapter();
//				if (event.getSelection().isEmpty()) {
//					try{
//						Object obj = Class.forName(table.getModelClass()).newInstance();
//						if (obj instanceof ADBase) {
//							((ADBase)obj).setOrgRrn(Env.getOrgRrn());
//						}
//						form.fireSelectionChanged(spart, new StructuredSelection(new Object[] {obj}));
//					} catch (Exception e){
//						e.printStackTrace();
//					}
//				} else {
//					form.fireSelectionChanged(spart, event.getSelection());
//				}
	//	}
	//});
//	    String whereClause = this.getWhereClause();
//	    String initWhereClause = this.getTableManager().getADTable().getInitWhereClause();
//	    if(whereClause == null){
//	    	whereClause = " 1=1 ";
//	    	setWhereClause(whereClause);
//	    }
//	    if(initWhereClause != null && initWhereClause.trim().length() > 0){
//	    	StringBuffer sb = new StringBuffer(whereClause);
//	    	sb.append(" and " + initWhereClause);
//	    	setWhereClause(sb.toString());
//	    }	    
	    refresh();
	}
	
	private void setSelectionRequisition(Object obj) {
		if (obj instanceof InvLot) {
			selectedIn = getMovementInFromInvLot((InvLot) obj);
		} else {
			selectedIn = null;
		}
	}
	
	
	
	private Movement getMovementInFromInvLot(InvLot object){
		Movement mov = new Movement();
		mov.setObjectRrn(object.getMovementRrn());
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
	
	
	
	public void detailAdapter(){
		if(selectedIn.getObjectRrn() == null) return;
		setWhereClause(" movementId='" + selectedIn.getDocId().toString() + "'");
		if((MovementIn.InType.PIN.toString()).equals(selectedIn.getDocType())){
			parentAdTable = getADTableByTableName(PIN_TABLE);
			childAdTable = getADTableByTableName(PIN_LINE_TABLE);
			InLineDialog inLineDialog = new InLineDialog(UI.getActiveShell(), parentAdTable, getWhereClause(), selectedIn,
					childAdTable, true);
			inLineDialog.open();
		}else if((MovementIn.InType.OIN.toString()).equals(selectedIn.getDocType())){
			parentAdTable = getADTableByTableName(OIN_TABLE);
			childAdTable = getADTableByTableName(OIN_LINE_TABLE);
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
			parentAdTable = getADTableByTableName(TRF_TABLE);
			childAdTable = getADTableByTableName(TRF_LINE_TABLE);
			setWhereClause(" movementRrn = " + selectedIn.getObjectRrn().toString() + " ");
			TransferLineDialog cd = new TransferLineDialog(UI.getActiveShell(),	parentAdTable, getWhereClause(), selectedIn, childAdTable, true);
			cd.open();
		}
		// Add By BruceYou 2012-03-22
		//调整出库数量为负数，视为调整入库，故此处添加此种类型的入库明细查询
		else if("AOU".equals(selectedIn.getDocType())){
			String whereClause = " movementRrn='" + selectedIn.getObjectRrn().toString() + "'";
			parentAdTable = getADTableByTableName(AOU_TABLE);
			childAdTable = getADTableByTableName(AOU_LINE_TABLE);
			AdjustOutLineBlockDialog cd = new AdjustOutLineBlockDialog(UI.getActiveShell(),
					parentAdTable, whereClause, selectedIn, childAdTable,true);
			cd.open();
		}
	}
	 
	protected ADTable getADTableByTableName(String tableName) {
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
	

	@Override
	public void refresh(){
		super.refresh();
	}
}

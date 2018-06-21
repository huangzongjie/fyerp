package com.graly.erp.inv.out.query;

import java.math.BigDecimal;
import java.util.Date;
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
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.QuerySection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.VInDetail;
import com.graly.erp.inv.model.VOutDetail;
import com.graly.erp.inv.out.OutLineBlockDialog;
import com.graly.erp.inv.out.adjust.AdjustOutLineBlockDialog;
import com.graly.erp.inv.outother.OtherOutLineBlockDialog;
import com.graly.erp.inv.querydialog.QueryDialog;
import com.graly.erp.inv.transfer.TransferLineDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class OutQuerySection extends QuerySection {
private static final Logger logger = Logger.getLogger(OutQuerySection.class);
	
	protected final String SOU_LINE_TABLE = "INVMovementOutLine";
	protected final String SOU_TABLE = "INVMovementOut";
	
	protected final String OOU_LINE_TABLE = "INVMovementOutOtherLine";
	protected final String OOU_TABLE = "INVMovementOutOther";
	
	protected final String AOU_LINE_TABLE = "INVMovementAdjustOutLine";
	protected final String AOU_TABLE = "INVMovementAdjustOut";
	
	protected final String TRF_LINE_TABLE = "INVMovementTransferLine";
	protected final String TRF_TABLE = "INVMovementTransfer";

	protected ADTable parentAdTable;
	protected ADTable childAdTable;
	protected Movement selectedOut;	

	public OutQuerySection(EntityTableManager tableManager) {
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
		if (obj instanceof VOutDetail) {
			selectedOut = getMovementOutFromVOutDetail((VOutDetail) obj);
		} else {
			selectedOut = null;
		}
	}

	@Override
	protected void detailAdapter() {
		if(selectedOut.getObjectRrn() == null) return;
		String whereClause = " movementRrn='" + selectedOut.getObjectRrn().toString() + "'";
		if((MovementOut.OutType.SOU.toString()).equals(selectedOut.getDocType())){
			parentAdTable = getADTableOfRequisition(SOU_TABLE);
			childAdTable = getADTableOfRequisition(SOU_LINE_TABLE);
			OutLineBlockDialog cd = new OutLineBlockDialog(UI.getActiveShell(),parentAdTable, whereClause, selectedOut, childAdTable,true);
			cd.open();
		}else if((MovementOut.OutType.OOU.toString()).equals(selectedOut.getDocType())){
			parentAdTable = getADTableOfRequisition(OOU_TABLE);
			childAdTable = getADTableOfRequisition(OOU_LINE_TABLE);
			OtherOutLineBlockDialog cd = new OtherOutLineBlockDialog(UI.getActiveShell(),
					parentAdTable, whereClause, selectedOut, childAdTable,true);
			cd.open();
		}else if((MovementOut.OutType.AOU.toString()).equals(selectedOut.getDocType())){
			parentAdTable = getADTableOfRequisition(AOU_TABLE);
			childAdTable = getADTableOfRequisition(AOU_LINE_TABLE);
			AdjustOutLineBlockDialog cd = new AdjustOutLineBlockDialog(UI.getActiveShell(),
					parentAdTable, whereClause, selectedOut, childAdTable,true);
			cd.open();
		}else if(MovementIn.DOCTYPE_TRF.equals(selectedOut.getDocType())){
			parentAdTable = getADTableOfRequisition(TRF_TABLE);
			childAdTable = getADTableOfRequisition(TRF_LINE_TABLE);
			setWhereClause(" movementRrn = " + selectedOut.getObjectRrn().toString() + " ");
			TransferLineDialog cd = new TransferLineDialog(UI.getActiveShell(),	parentAdTable, getWhereClause(), selectedOut, childAdTable, true);
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
	
	private Movement getMovementOutFromVOutDetail(VOutDetail object){
		Movement mov = new Movement();
		mov.setObjectRrn(object.getMovmentRrn());
		try {
			ADManager manager = Framework.getService(ADManager.class);
			if(mov.getObjectRrn() != null)
				mov = (Movement) manager.getEntity(mov);
		} catch (Exception e) {
			logger.error("error in Method getMovementOutFromVInDetail() : OutQuerySection",e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return mov;
	}
	
	@Override
	protected void createToolItemExport(ToolBar tBar) {
		itemQuery = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OUTQUERY_EXPORT);
		itemQuery.setText(Message.getString("common.export"));
		itemQuery.setImage(SWTResourceCache.getImage("export"));
		itemQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				exportAdapter();
			}
		});
	}
	
	@Override
	protected void createToolItemDetail(ToolBar tBar) {
		detailItem = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OUTQUERY_VIEWDETAIL);
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
	public void refresh() {
//		super.refresh();
		viewer.setInput(getEntityList());	
		tableManager.updateView(viewer);
		createSectionDesc(section);
		
		Table table = ((TableViewer)viewer).getTable();
		VOutDetail totalDetail = new VOutDetail();
		totalDetail.setDocId(Message.getString("inv.total"));
		BigDecimal totalQty = BigDecimal.ZERO;
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof VOutDetail) {
				VOutDetail outDetail = (VOutDetail)obj;
				if (outDetail.getQtyMovement() != null) {
					totalQty = totalQty.add(outDetail.getQtyMovement());
				}
				if (outDetail.getLineTotal() != null) {
					totalPrice = totalPrice.add(outDetail.getLineTotal());
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

	private List<VOutDetail> getEntityList() {
		try{
			INVManager invManager = Framework.getService(INVManager.class);
			boolean flag =false;//物料是否必须,消除物料不存在引起的查询让JOBSS卡死的BUG
			if(getQueryDialog() != null){
				List<ADField> adFileds = getQueryDialog().getTableManager().getADTable().getFields();
				for(ADField f :adFileds){
					if("materialRrn".equals(f.getName())){
						flag =f.getIsMandatory();
						break;
					}
				}
			}
			String where = getWhereClause();
			if(where != null){
				if(flag){
					int position = where.indexOf("materialRrn");
					if(position == -1){
						return null;
					}
				}
				where = where.replaceFirst("materialRrn", "MATERIAL_RRN");
			}
			if(getQueryDialog() != null){
				StringBuffer whereClause = new StringBuffer(getADTable().getWhereClause());
				whereClause.append(" AND ORG_RRN = ");
				whereClause.append(Env.getOrgRrn());
				return invManager.getOutDetails(getQueryDialog().getQueryKeys(), whereClause.toString());
			}else{
				return null;
			}
		}catch (Exception e){
			logger.error("OutQuerySection : getEntityList()", e);
		}
		return null;
	}
	
	@Override
	protected void createSectionDesc(Section section) {
	}
}

package com.graly.erp.inv.transfer.notransfer.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.QuerySection;
import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.InLineDialog;
import com.graly.erp.inv.in.MoInOfLotDialog;
import com.graly.erp.inv.model.AlarmData;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.VInDetail;
import com.graly.erp.inv.model.VOutDetail;
import com.graly.erp.inv.model.VTrfDetail;
import com.graly.erp.inv.otherin.OtherInLineDialog;
import com.graly.erp.inv.transfer.TransferLineDialog;
import com.graly.erp.js.client.JSManager;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class NoTransferQuerySection extends QuerySection {
	private static final Logger logger = Logger.getLogger(NoTransferQuerySection.class);
	
	protected final String TRF_LINE_TABLE = "INVMovementTransferLine";
	protected final String TRF_TABLE = "INVMovementTransfer";
		
	protected ADTable parentAdTable;
	protected ADTable childAdTable;
	protected Movement selectedIn;	
	
	private Map<String,Object>	queryKeys;
	
	public NoTransferQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1 ");
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	
//	@Override
//	protected void createViewAction(StructuredViewer viewer) {
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				StructuredSelection ss = (StructuredSelection) event.getSelection();
//				setSelectionRequisition(ss.getFirstElement());
//				detailAdapter();
//			}
//		});
//		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent event) {
//				try {
//					StructuredSelection ss = (StructuredSelection) event.getSelection();
//					setSelectionRequisition(ss.getFirstElement());
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//	
//	private void setSelectionRequisition(Object obj) {
//		if (obj instanceof VTrfDetail) {
//			selectedIn = getMovementInFromVTrfDetail((VTrfDetail) obj);
//		} else {
//			selectedIn = null;
//		}
//	}
//
//	@Override
//	protected void detailAdapter() {
//		if(selectedIn.getObjectRrn() == null) return;
//		setWhereClause(" movementId='" + selectedIn.getDocId().toString() + "'");
//		if(MovementIn.DOCTYPE_TRF.equals(selectedIn.getDocType())){
//			parentAdTable = getADTableOfRequisition(TRF_TABLE);
//			childAdTable = getADTableOfRequisition(TRF_LINE_TABLE);
//			setWhereClause(" movementRrn = " + selectedIn.getObjectRrn().toString() + " ");
//			TransferLineDialog cd = new TransferLineDialog(UI.getActiveShell(),	parentAdTable, getWhereClause(), selectedIn, childAdTable, true);
//			cd.open();
//		}
//	}
//	
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
		try {
			StringBuffer sb =new StringBuffer();
			if(queryDialog != null){
				queryKeys = queryDialog.getQueryKeys();
			 if(queryKeys instanceof Map){//只可能是FromToCalendarField
				Map m = (Map)queryKeys.get("dateApproved");
				Date from = (Date) m.get(FromToCalendarField.DATE_FROM);
				Date to = (Date) m.get(FromToCalendarField.DATE_TO);
				if(from != null) {
					sb.append("  trunc(");
					sb.append(" im.date_approved ");
					sb.append(") >= TO_DATE('" + I18nUtil.formatDate(from) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
				}
				if(to != null){
					sb.append(" AND trunc(");
					sb.append(" im.date_approved ");
					sb.append(") <= TO_DATE('" + I18nUtil.formatDate(to) + "', '" + I18nUtil.getDefaultDatePattern() + "') ");
				}
			}
			}else{
				long oneDayTime = 1000*3600*24;
				Date nowDate = Env.getSysDate();//默认查询昨天
				Date yesterday = new Date(nowDate.getTime()-oneDayTime);
				sb.append("  trunc(");
				sb.append(" im.date_approved ");
				sb.append(") = TO_DATE('" + I18nUtil.formatDate(yesterday) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
			}
			List ls = new ArrayList();
//			if(true) return;
			INVManager invManager =  Framework.getService(INVManager.class);
			ls = invManager.getVInvNoTransfer(sb.toString());
//			JSManager jsManager = Framework.getService(JSManager.class);
//			if(queryKeys != null){
//				if(!queryKeys.isEmpty() && queryKeys.get("serialNumber")!= null ){
//					String  serialNumber =   queryKeys.get("serialNumber").toString();
//					ls = jsManager.getMaterialQtyQueryList(Integer.MAX_VALUE, " serialNumber like '"+serialNumber+"'", null);
//				}else{
//					ls = jsManager.getMaterialQtyQueryList(Integer.MAX_VALUE,null, null);
//				}
//			}
			viewer.setInput(ls);
			tableManager.updateView(viewer);
//			createSectionDesc(section);
			createSectionDesc(ls);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	protected void createSectionDesc(Section section){
	}
	//总共多少条记录
	protected void createSectionDesc(List list){
		try{ 
			String text = Message.getString("common.totalshow");
			long count = list.size();
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
//	@Override
//	public void refresh() {
//		super.refresh();
//		Table table = ((TableViewer)viewer).getTable();
//		VTrfDetail totalDetail = new VTrfDetail();
//		totalDetail.setDocId(Message.getString("inv.total"));
//		BigDecimal totalQty = BigDecimal.ZERO;
//		BigDecimal totalPrice = BigDecimal.ZERO;
//		for (int i = 0; i < table.getItemCount(); i++) {
//			TableItem item = table.getItem(i);
//			Object obj = item.getData();
//			if (obj instanceof VTrfDetail) {
//				VTrfDetail trfDetail = (VTrfDetail)obj;
//				if (trfDetail.getQtyMovement() != null) {
//					totalQty = totalQty.add(trfDetail.getQtyMovement());
//				}
//				if (trfDetail.getLineTotal() != null) {
//					totalPrice = totalPrice.add(trfDetail.getLineTotal());
//				}
//			}
//		}
//		totalDetail.setQtyMovement(totalQty);
//		totalDetail.setLineTotal(totalPrice);
//		TableViewer tv = (TableViewer)viewer;
//		tv.insert(totalDetail, table.getItemCount());
//		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
//		table.getItems()[table.getItemCount()-1].setBackground(color);
//		Font font = new Font(Display.getDefault(),"宋体",10,SWT.BOLD); 
//		table.getItems()[table.getItemCount()-1].setFont(font);
//		table.redraw();
//	}
}

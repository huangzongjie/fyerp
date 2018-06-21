package com.graly.erp.inv.sum.lotconsume;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.material.EntityQueryDialog4WC;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.VLotConsumeDetailByMO;
import com.graly.erp.inv.model.VLotConsumeDetailByPM;
import com.graly.erp.inv.model.VLotConsumeSumByMO;
import com.graly.erp.inv.model.VLotConsumeSumByPM;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class LotConsumeQuerySection extends MasterSection {
private static final Logger logger = Logger.getLogger(LotConsumeQuerySection.class);
	protected ToolItem itemView;

	public LotConsumeQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1");
	}
	
//	protected ADTable getADTableOfRequisition(String tableName) {
//		ADTable adTable = null;
//		try {
//			ADManager entityManager = Framework.getService(ADManager.class);
//			adTable = entityManager.getADTable(0L, tableName);
//			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
//			return adTable;
//		} catch (Exception e) {
//			logger.error("LotConsumeQuerySection : getADTableOfRequisition()", e);
//		}
//		return null;
//	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
//		createToolItemView(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog4WC(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	private void createToolItemView(ToolBar tBar) {
		itemView = new ToolItem(tBar, SWT.PUSH);
		itemView.setText(Message.getString("common.print"));
		itemView.setImage(SWTResourceCache.getImage("preview"));
		itemView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				viewAdapter();
			}
		});
	}

	protected void viewAdapter() {}
	
	@Override
	public void refresh() {
		super.refresh();
		doViewerAggregation();
	}
	
	public void doViewerAggregation(){
		Table table = ((TableViewer)viewer).getTable();
		ADTable adTable = tableManager.getADTable();
		String modelClass = adTable.getModelClass();
		
		BigDecimal qtySum = BigDecimal.ZERO;
		BigDecimal amountSum = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof VLotConsumeSumByMO) {
				VLotConsumeSumByMO o = (VLotConsumeSumByMO)obj;
				if (o.getSumQtyConsume() != null) {
					qtySum = qtySum.add(o.getSumQtyConsume());
				}
				if (o.getSumSumNum() != null) {
					amountSum = amountSum.add(o.getSumSumNum());
				}
			}else
			if (obj instanceof VLotConsumeSumByPM) {
				VLotConsumeSumByPM o = (VLotConsumeSumByPM)obj;
				if (o.getInSum() != null) {
					qtySum = qtySum.add(o.getInSum());
				}
				if (o.getSumNum() != null) {
					amountSum = amountSum.add(o.getSumNum());
				}
			}else
			if (obj instanceof VLotConsumeDetailByMO) {
				VLotConsumeDetailByMO o = (VLotConsumeDetailByMO)obj;
				if (o.getQtyConsume() != null) {
					qtySum = qtySum.add(o.getQtyConsume());
				}
				if (o.getConsumePrice() != null) {
					amountSum = amountSum.add(o.getConsumePrice());
				}
			}else
			if (obj instanceof VLotConsumeDetailByPM) {
				VLotConsumeDetailByPM o = (VLotConsumeDetailByPM)obj;
				if (o.getSumAllconsume() != null) {
					qtySum = qtySum.add(o.getSumAllconsume());
				}
				if (o.getSumSumnum() != null) {
					amountSum = amountSum.add(o.getSumSumnum());
				}
			}
		}
		
		Object sumObj = null;
		
		if(modelClass.equalsIgnoreCase(VLotConsumeSumByMO.class.getName())){
			sumObj = new VLotConsumeSumByMO();
			((VLotConsumeSumByMO)sumObj).setMoId(Message.getString("inv.total"));
			((VLotConsumeSumByMO)sumObj).setSumQtyConsume(qtySum);
			((VLotConsumeSumByMO)sumObj).setSumSumNum(amountSum);
		}else
		if(modelClass.equalsIgnoreCase(VLotConsumeSumByPM.class.getName())){
			sumObj = new VLotConsumeSumByPM();
			((VLotConsumeSumByPM)sumObj).setMaterialId(Message.getString("inv.total"));
			((VLotConsumeSumByPM)sumObj).setInSum(qtySum);
			((VLotConsumeSumByPM)sumObj).setSumNum(amountSum);
		}else
		if(modelClass.equalsIgnoreCase(VLotConsumeDetailByMO.class.getName())){
			sumObj = new VLotConsumeDetailByMO();
			((VLotConsumeDetailByMO)sumObj).setMoId(Message.getString("inv.total"));
			((VLotConsumeDetailByMO)sumObj).setQtyConsume(qtySum);
			((VLotConsumeDetailByMO)sumObj).setConsumePrice(amountSum);
		}else
		if(modelClass.equalsIgnoreCase(VLotConsumeDetailByPM.class.getName())){
			sumObj = new VLotConsumeDetailByPM();
			((VLotConsumeDetailByPM)sumObj).setPromaterial(Message.getString("inv.total"));
			((VLotConsumeDetailByPM)sumObj).setSumAllconsume(qtySum);
			((VLotConsumeDetailByPM)sumObj).setSumSumnum(amountSum);
		}
		
		if(sumObj == null){
			return;
		}
		
		TableViewer tv = (TableViewer)viewer;
		tv.insert(sumObj, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"ËÎÌו",10,SWT.BOLD); 
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}

}

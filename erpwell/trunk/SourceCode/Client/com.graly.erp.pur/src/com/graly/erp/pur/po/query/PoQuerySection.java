package com.graly.erp.pur.po.query;

import java.math.BigDecimal;

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
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.VPoLine;
import com.graly.erp.pur.po.POLineBlockDialog;
import com.graly.erp.pur.request.query.PrQueryDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PoQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(PoQuerySection.class);

	protected PoQueryDialog onlineDialog;
	protected PurchaseOrder selectedPO;
	private static final String TABLE_POLINE_NAME = "PURPurchaseOrderLine";
	private static final String TABLE_PO_NAME = "PURPurchaseOrder";
	protected ADTable adTablePo;
	protected ADTable adTablePoLine;
	
	public PoQuerySection(EntityTableManager tableManager) {
		super(tableManager);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		section.setTextClient(tBar);
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
	
	protected void editAdapter() {
		try {
			if (selectedPO != null && selectedPO.getObjectRrn() != null) {
				adTablePo = getADTableOfPO();
				adTablePoLine = getADTableOfPOLine();
				ADManager adManager = Framework.getService(ADManager.class);
				selectedPO = (PurchaseOrder)adManager.getEntity(selectedPO);
				String whereClause = (" poRrn = '" + selectedPO.getObjectRrn().toString() + "' ");
				POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), adTablePo, whereClause, selectedPO,
						adTablePoLine, false);
				if (cd.open() == Dialog.CANCEL) {
					refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at PoQuerySection : editAdapter() " + e);
		}
	}
	
	protected ADTable getADTableOfPOLine() {
		try {
			if (adTablePoLine == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTablePoLine = entityManager.getADTable(0L, getTABLE_POLINE_NAME());
				adTablePoLine = entityManager.getADTableDeep(adTablePoLine.getObjectRrn());
			}
			return adTablePoLine;
		} catch (Exception e) {
			logger.error("PoQuerySection : getADTableOfPOLine()", e);
		}
		return null;
	}

	protected String getTABLE_POLINE_NAME() {
		return TABLE_POLINE_NAME;
	}
	
	protected ADTable getADTableOfPO() {
		try {
			if (adTablePo == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTablePo = entityManager.getADTable(0L, TABLE_PO_NAME);
				adTablePo = entityManager.getADTableDeep(adTablePo.getObjectRrn());
			}
			return adTablePo;
		} catch (Exception e) {
			logger.error("PoQuerySection : getADTableOfPO", e);
		}
		return null;
	}
	
	@Override
	public void refresh() {
		super.refresh();
		TableViewer tv = (TableViewer)viewer;
		Table table = tv.getTable();
		BigDecimal totalQty = BigDecimal.ZERO;//订货总数
		BigDecimal totalLineTotal = BigDecimal.ZERO;//总价
		BigDecimal totalQtyDelivered = BigDecimal.ZERO;//收货数汇总
		BigDecimal totalQtyTested= BigDecimal.ZERO;//检验数汇总
		BigDecimal totalQtyIn= BigDecimal.ZERO;//入库数汇总
		for(TableItem ti : table.getItems()){
			Object obj = ti.getData();
			if(obj instanceof VPoLine){
				VPoLine vobj = (VPoLine)obj;
				totalQty = vobj.getQty().add(totalQty);
				totalLineTotal = vobj.getLineTotal().add(totalLineTotal);
				totalQtyDelivered = vobj.getQtyDelivered().add(totalQtyDelivered);
				totalQtyTested = vobj.getQtyTested().add(totalQtyTested);
				totalQtyIn = vobj.getQtyIn().add(totalQtyIn);
			}
		}
		
		VPoLine totalObj = new VPoLine();
		totalObj.setPoId(Message.getString("inv.total"));
		totalObj.setUnitPrice(null);
		totalObj.setQty(totalQty);
		totalObj.setLineTotal(totalLineTotal);
		totalObj.setQtyDelivered(totalQtyDelivered);
		totalObj.setQtyTested(totalQtyTested);
		totalObj.setQtyIn(totalQtyIn);
		tv.insert(totalObj, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"宋体",10,SWT.BOLD); 
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}
	
	protected void refreshSection() {
		try {
			refresh();
			if (selectedPO != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedPO = (PurchaseOrder) adManager.getEntity(selectedPO);
			}
		} catch (Exception e) {
			logger.error("Error at PoQuerySection : refreshSection() " + e);
		}
	}
	
	private void setSelectionPO(Object obj) {
		try {
			if (obj instanceof VPoLine) {
				VPoLine vpoLine = (VPoLine)obj;
				Long poRrn = vpoLine.getPoRrn();
				PurchaseOrder po = new PurchaseOrder();
				po.setObjectRrn(poRrn);
				ADManager adManager = Framework.getService(ADManager.class);
				selectedPO = (PurchaseOrder) adManager.getEntity(po);
			} else {
				selectedPO = null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			if(!queryDialog.isOpen){
				queryDialog.open();
			}else{
				queryDialog.setVisible(true);
			}
		} else if(this.onlineDialog != null && onlineDialog.getEntityQueryDialog() != null) {
			queryDialog = onlineDialog.getEntityQueryDialog();
			queryDialog.setVisible(true);
		} else {
			// 此种情况一般不会出现,因为在VendorAssess.open()已将queryDialog设置过来.之所以用
			// VendorAssessDialog(false)表示不创建queryDialog.而是显示调用VendorAssessQueryDialog.
			// 以便传入tableManager,否则会因为在vaDialog无tableId而导致调用getEntityTableManager时出错.
			PoQueryDialog vaDialog = new PoQueryDialog(false);
			queryDialog = vaDialog.new PoInternalQueryDialog(UI.getActiveShell(),
					getADTable(), this);
			vaDialog.setEntityQueryDialog(queryDialog);
			queryDialog.open();
		}
	}

	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof PrQueryDialog) {
			this.onlineDialog = (PoQueryDialog)dialog;
		} else {
			this.onlineDialog = null;
		}
	}
}

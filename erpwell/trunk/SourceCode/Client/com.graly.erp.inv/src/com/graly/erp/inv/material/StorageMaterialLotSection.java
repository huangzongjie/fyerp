package com.graly.erp.inv.material;

import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.lotprint.LotPrintDialog;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class StorageMaterialLotSection extends LotMasterSection {
	private VStorageMaterial selectedLine;
	protected ToolItem itemPrint;
	
	public StorageMaterialLotSection(){
		super();
	}
	
	public StorageMaterialLotSection(ADTable adTable, LotDialog parentDialog, VStorageMaterial selectedLine) {
		super(adTable, parentDialog);
		this.selectedLine = selectedLine;
	}

	@Override
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
		//覆盖父类中的代码
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		//屏蔽父类中的代码
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new TableListManager(adTable);
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
		lotManager.updateView(viewer);
	}
	
	@Override
	protected void initTableContent() {
		List<Lot> list = null;
		try {
//			if(!Lot.LOTTYPE_SERIAL.equals(selectedLine.getLotType())) {
				INVManager invManager = Framework.getService(INVManager.class);
				list = invManager.getLotStorage(selectedLine.getWarehouseRrn(), selectedLine.getMaterialRrn());
//			} else {
//				ADManager manager = Framework.getService(ADManager.class);
//				list = manager.getEntityList(Env.getOrgRrn(), Lot.class, 
//						Env.getMaxResult(), getWhereClause(), getOrderByClause());
//			}		
			for(Lot lot : list){
				if(lot.getDateProduct() == null){
					lot.setDateProduct(lot.getDateIn());
				}
			}
            setLots(list);
            refresh();
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        }
	}
	
	protected void createToolItemPrint(ToolBar tBar) {
		itemPrint = new ToolItem(tBar, SWT.PUSH);
		itemPrint.setText(Message.getString("common.print"));
		itemPrint.setImage(SWTResourceCache.getImage("print"));
		itemPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				printAdapter();
			}
		});
	}
	
	protected void printAdapter() {
		try {
			lots = (List<Lot>)viewer.getInput();
			if(viewer.getTable().getSelection().length > 0) {
				selectLot = (Lot)viewer.getTable().getSelection()[0].getData();
			}
			if(lots != null && lots.size() != 0){
				LotPrintDialog printDialog = new LotPrintDialog(lots, this.selectLot);
				printDialog.open();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
//现在不分serial和batch了，此方法无用
//	@Override
//	protected String getWhereClause() {
//		StringBuilder sb = new StringBuilder();
//		String modelName = adTable.getModelName() + ".";
//		sb.append(modelName);
//		sb.append("materialRrn");
//		sb.append(" = '" + selectedLine.getMaterialRrn() + "' ");
//		sb.append(" AND ");
//		sb.append(modelName);
//		sb.append("warehouseRrn");
//		sb.append(" = " + selectedLine.getWarehouseRrn() + " ");
//		sb.append(" AND ");
//		sb.append(modelName);
//		sb.append("position");
//		sb.append(" = " + "'INSTOCK' ");
//		sb.append(" AND ");
//		sb.append(modelName);
//		sb.append("isUsed");
//		sb.append(" = " + "'N' ");
//		return sb.toString();
//	}
}

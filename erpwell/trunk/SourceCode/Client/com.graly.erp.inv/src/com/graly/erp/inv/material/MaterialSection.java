package com.graly.erp.inv.material;

import java.math.BigDecimal;
import java.util.HashMap;
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
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;



public class MaterialSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MaterialSection.class);
	protected ToolItem itemBarcode;
	protected VStorageMaterial selectedLine;
	protected ToolItem itemFinancialOverseas;
	protected ToolItem itemFinancialOverseasDetail;
	protected MaterialNewSection materialNewSection;
	protected ToolItem itemWms;

	public MaterialSection(EntityTableManager tableManager,MaterialNewSection materialNewSection) {
		super(tableManager);
		this.materialNewSection = materialNewSection;
		setWhereClause("1<>1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemFinancialOverseas(tBar);//财务袁学伟海外报表
//		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemFinancialOverseasDetail(tBar);//财务袁学伟海外报表
//		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemWorkShopBarcode(tBar);
		
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemBarcode(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemWmsStorage(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemFinancialOverseas(ToolBar tBar) {
		itemFinancialOverseas = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SEARCHMATERIAL_LOT);
		itemFinancialOverseas.setText("财务海外报表");
		itemFinancialOverseas.setImage(SWTResourceCache.getImage("preview"));
		itemFinancialOverseas.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				foAdapter();
			}
		});
	}
	
	protected void createToolItemFinancialOverseasDetail(ToolBar tBar) {
		itemFinancialOverseasDetail = new ToolItem(tBar, SWT.PUSH);
		itemFinancialOverseasDetail.setText("财务海外报表详细");
		itemFinancialOverseasDetail.setImage(SWTResourceCache.getImage("preview"));
		itemFinancialOverseasDetail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				fodAdapter();
			}
		});
	}
	
//	protected void createToolItemWorkShopBarcode(ToolBar tBar) {
//		itemBarcode = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SEARCHMATERIAL_LOT);
//		itemBarcode.setText("车间批次");
//		itemBarcode.setImage(SWTResourceCache.getImage("barcode"));
//		itemBarcode.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
////				barcodeWorkShopAdapter();
//			}
//		});
//	}
	
	protected void createToolItemBarcode(ToolBar tBar) {
		itemBarcode = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SEARCHMATERIAL_LOT);
		itemBarcode.setText(Message.getString("inv.barcode"));
		itemBarcode.setImage(SWTResourceCache.getImage("barcode"));
		itemBarcode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				barcodeAdapter();
			}
		});
	}
	protected void createToolItemWmsStorage(ToolBar tBar) {
		itemWms = new ToolItem(tBar, SWT.PUSH);
		itemWms.setText("WMS库存");
		itemWms.setImage(SWTResourceCache.getImage("barcode"));
		itemWms.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				wmsStrorageAdapter();
			}
		});
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionLine(ss.getFirstElement());
	    		barcodeAdapter();
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionLine(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	private void setSelectionLine(Object obj) {
		if(obj instanceof VStorageMaterial) {
			selectedLine = (VStorageMaterial)obj;
		} else {
			selectedLine = null;
		}
	}
	
	protected void barcodeAdapter() {
		if(selectedLine != null) {
			StorageMaterialLotDialog ld = new StorageMaterialLotDialog(UI.getActiveShell(), selectedLine);
			ld.open();
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialQueryDialog(UI.getActiveShell(), tableManager, this,this);
			queryDialog.open();
		}
	}
	
	@Override
	public void refresh() {
		super.refresh();
		if(Env.getOrgRrn()==139420L&&queryDialog!=null ){
			String materialId =(String) queryDialog.getQueryKeys().get("materialId");
			if(materialId!=null && materialId.length() >0){
				PDMManager pdmManager;
				try {
					pdmManager = Framework.getService(PDMManager.class);
					List<Material> materials = pdmManager.getMaterialById(materialId, Env.getOrgRrn());
					if(materials!=null && materials.size() > 0 ){
						TableViewer tv = (TableViewer) viewer;
						TableItem[] items = tv.getTable().getItems();
					 	for(TableItem it : items) {
					 		VStorageMaterial vm = (VStorageMaterial) it.getData();
					 		if("环保-良品".equals(vm.getWarehouseId())){
					 			INVManager invManager = Framework.getService(INVManager.class);
					 			BigDecimal wmsQty = invManager.getQtyInWmsStorage(materialId,"环保良品");
					 			vm.setQtyWmsOnhand(wmsQty);
					 			viewer.refresh(it.getData());
							}
					 		if("制造车间良品".equals(vm.getWarehouseId())){
					 			INVManager invManager = Framework.getService(INVManager.class);
					 			BigDecimal wmsQty = invManager.getQtyInWmsStorage(materialId,"制造车间");
					 			vm.setQtyWmsOnhand(wmsQty);
					 			viewer.refresh(it.getData());
					 		}
			        	}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
		}
	}
	protected void refreshSection() {
		refresh();
	}
	
	protected void foAdapter() {
		try {
			String report = "kn_overseas_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, null);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void fodAdapter() {
		try {
			String report = "kn_overseas_detail_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, null);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
//	protected void barcodeWorkShopAdapter() {
//		if(selectedLine != null) {
//			WorkShopStorageLotDialog ld = new WorkShopStorageLotDialog(UI.getActiveShell(), selectedLine);
//			ld.open();
//		}
//	}

	public MaterialNewSection getMaterialNewSection() {
		return materialNewSection;
	}

	public void setMaterialNewSection(MaterialNewSection materialNewSection) {
		this.materialNewSection = materialNewSection;
	}
	
	protected void wmsStrorageAdapter() {
		if(selectedLine != null){
			String warehouseId ="";
			if(selectedLine.getWarehouseRrn().equals(151043L)){
				warehouseId = "环保良品";
			}else if(selectedLine.getWarehouseRrn().equals(151046L)){
				warehouseId = "制造车间";
			}
			WmsStorageDialog wsd = new WmsStorageDialog(UI.getActiveShell(), selectedLine.getMaterialId(),warehouseId);
			wsd.open();
		}
//		if(selectedLine != null) {
//			StorageMaterialLotDialog ld = new StorageMaterialLotDialog(UI.getActiveShell(), selectedLine);
//			ld.open();
//		}
	}
	
}

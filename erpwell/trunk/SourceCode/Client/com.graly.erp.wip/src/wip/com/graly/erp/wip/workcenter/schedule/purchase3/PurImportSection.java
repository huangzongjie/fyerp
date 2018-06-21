package com.graly.erp.wip.workcenter.schedule.purchase3;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.wip.model.TmpHjdImport;
import com.graly.erp.wip.workcenter.schedule.into.ErrorLogDisplayDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;



public class PurImportSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(PurImportSection.class);
	protected ToolItem itemBarcode;
	protected VStorageMaterial selectedLine;
	protected ToolItem itemFinancialOverseas;
	protected ToolItem itemFinancialOverseasDetail;
	protected PurchaseMaterialSection purMaterialSection;
	protected ToolItem itemWms;

	public PurImportSection(EntityTableManager tableManager,PurchaseMaterialSection purMaterialSection) {
		super(tableManager);
		this.purMaterialSection = purMaterialSection;
		setWhereClause("1<>1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolRunTotal4(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolRunTotal(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemImport(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	protected ToolItem itemRunTotal;//领用单
	protected ToolItem itemRunTotal4;//商品运算
	protected void createToolRunTotal4(ToolBar tBar) {
		itemRunTotal4 = new ToolItem(tBar, SWT.PUSH);
		itemRunTotal4.setText("商品运算");
		itemRunTotal4.setImage(SWTResourceCache.getImage("barcode"));
		itemRunTotal4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				runTotalAdapter4();
			}
		});
	}
	protected void createToolRunTotal(ToolBar tBar) {
		itemRunTotal = new ToolItem(tBar, SWT.PUSH);
		itemRunTotal.setText("装配体运算");
		itemRunTotal.setImage(SWTResourceCache.getImage("barcode"));
		itemRunTotal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				runTotalAdapter();
			}
		});
	}
	protected ToolItem itemImport;
	protected void createToolItemImport(ToolBar tBar) {
		itemImport = new ToolItem(tBar, SWT.PUSH);
		itemImport.setText(Message.getString("oou.import"));
		itemImport.setImage(SWTResourceCache.getImage("export"));
		itemImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				uploadAdapter2();
			}
		});
	}
	
	protected void runTotalAdapter() {
		try{
			//审核并且统计过采购情况并且没有生成出库单
			WipManager wipManager = Framework.getService(WipManager.class);
			wipManager.runSchedulePurchase3(Env.getOrgRrn(),Env.getUserRrn());
			UI.showInfo("操作成功");
			purMaterialSection.getPurRunDetailSection().refreshSection();
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	protected void runTotalAdapter4() {
		try{
			//审核并且统计过采购情况并且没有生成出库单
			WipManager wipManager = Framework.getService(WipManager.class);
			wipManager.runSchedulePurchase4(Env.getOrgRrn(),Env.getUserRrn());
			UI.showInfo("操作成功");
			purMaterialSection.getPurRunDetailSection().refreshSection();
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	   
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionLine(ss.getFirstElement());
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
	
  
	protected void refreshSection() {
		refresh();
	}
	
//	
//	protected void importAdapter() {
//		try {
//			FileDialog fileDialog = new FileDialog(UI.getActiveShell(),
//					SWT.OPEN);
//			// 设置初始路径
//			fileDialog.setFilterPath("C:/");
//			// 设置扩展名过滤
//			String[] filterExt = { "*.xls;*.xlsx" };
//			fileDialog.setFilterExtensions(filterExt);
//			// 打开文件对话框，返回选择的文件
//			String selectedFile = fileDialog.open();
//			if (selectedFile != null) {
//				if (!selectedFile.contains(".xls")) {
//					UI.showWarning(Message
//							.getString("ppm.upload_file_type_not_support"));
//					return;
//				} else {
//
//					File file = new File(selectedFile);
//
//					HSSFWorkbook workbook = new HSSFWorkbook(
//							new FileInputStream(file));
//					// 获得第一张sheet
//					HSSFSheet sheet = workbook.getSheetAt(0);
//					// 获得sheet总行数
//					int rowCount = sheet.getLastRowNum();
//					logger.info("found excel rows count:" + rowCount);
//					if (rowCount < 1) {
//						return;
//					}
//
//					Map<String,List<MovementLine>> maps = new HashMap<String,List<MovementLine>>();
//					for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {//从第二行开始遍历,第一行是标题
//						// 获得行对象
//						HSSFRow row = sheet.getRow(rowIndex);
//						if (row != null) {
//							String key = getCellString(row.getCell(2))+"_调整入库_"+getCellString(row.getCell(3));
//							List<MovementLine> lines = null;
//							if(maps.containsKey(key)){
//								lines = maps.get(key);
//							}else{
//								lines = new ArrayList<MovementLine>();
//							}
//							MovementLine ml = new MovementLine();
//							ml.setOrgRrn(Env.getOrgRrn());
//							//物料编号
//							ml.setMaterialId(getCellString(row.getCell(0)));
//							PDMManager pdmManager = Framework.getService(PDMManager.class);
//							
//							List<Material> mlst = pdmManager.getMaterialById(ml.getMaterialId(), Env.getOrgRrn());
//							if(mlst != null && mlst.size() > 0){
//								Material material = mlst.get(0);
//								ml.setMaterialRrn(material.getObjectRrn());
//								ml.setMaterialName(material.getName());
//							}else{
//								System.out.println(ml.getMaterialId());
//							}
//							//物料名称
//							//仓库
//							//相关单位
//							//数量
//							ml.setQtyMovement(new BigDecimal(getCellString(row.getCell(4))));
//							ml.setUnitPrice(new BigDecimal(getCellString(row.getCell(5))));
//							lines.add(ml);
//							ml.setLineNo(10L*(1+lines.indexOf(ml)));
//							maps.put(key, lines);
//						}
//					}
//					
//					INVManager invManager = Framework.getService(INVManager.class);
//
//					for(String kee : maps.keySet()){
//						String[] strs = kee.split("_",-1);
//						String warehouseId = strs[0];//仓库
//						Warehouse warehouse = invManager.getWarehouseById(warehouseId, Env.getOrgRrn());
//						String outType = strs[1];//出库类型
//						String kind = strs[2];//相关单位
//						MovementIn in = new MovementIn();
//						in.setOrgRrn(Env.getOrgRrn());
//						in.setWarehouseRrn(warehouse.getObjectRrn());
//						in.setInType(outType);
//						in.setKind(kind);
//						
//						List<MovementLine> lines = maps.get(kee);
//						ADManager adManager  =Framework.getService(ADManager.class);
//						adManager.sav
//						invManager.saveMovementInLine(in, lines, MovementIn.InType.OIN, Env.getUserRrn());
//					}
//				}
//				UI.showInfo("导入成功");
//			}
//		} catch (Exception e) {
//			UI.showError("导入失败");
//			logger.error("Error at AdjustOutSection : importAdapter() " + e);
//		}
//		
//		refreshAdapter();
//	}
	
	private String getCellString(HSSFCell cell) {
		String result = "";
		if (cell != null) {
			// 单元格类型：Numeric:0,String:1,Formula:2,Blank:3,Boolean:4,Error:5
			int cellType = cell.getCellType();
			switch (cellType) {
				case HSSFCell.CELL_TYPE_STRING:
					result = cell.getRichStringCellValue().getString();
					break;
				case HSSFCell.CELL_TYPE_NUMERIC:
					result = String.valueOf(cell.getNumericCellValue());
					break;
				case HSSFCell.CELL_TYPE_FORMULA:
					result = String.valueOf(cell.getNumericCellValue());
					break;
				case HSSFCell.CELL_TYPE_BOOLEAN:
					result = String.valueOf(cell.getBooleanCellValue());
					break;
				case HSSFCell.CELL_TYPE_BLANK:
					result = "";
					break;
				case HSSFCell.CELL_TYPE_ERROR:
					result = "";
					break;
				default:
					break;
			}
		}
		return result;
	}
	
	protected void uploadAdapter2() {
		try {
			FileDialog fileDialog = new FileDialog(UI.getActiveShell(), SWT.OPEN);
			// 设置初始路径
			fileDialog.setFilterPath("C:/");
			// 设置扩展名过滤
			String[] filterExt = { "*.xls"};
			fileDialog.setFilterExtensions(filterExt);
			// 打开文件对话框，返回选择的文件
			String selectedFile = fileDialog.open();
			if (selectedFile != null) {
				if (!selectedFile.contains(".xls")) {
					UI.showWarning(Message.getString("ppm.upload_file_type_not_support"));
					return;
				}
				PurImportDialog progressDialog = new PurImportDialog(UI.getActiveShell());
				PurImportProgress progress = new PurImportProgress(null,
						selectedFile, this.getTableManager().getADTable(), this);
				progressDialog.run(true,true,progress);
				// 提示已成功导入或失败
				if (progress.isFinished()) {
					if(progress.isSuccess()) {
						ADManager adManager =Framework.getService(ADManager.class);
//						DateFormat dd=new SimpleDateFormat("yyyy-MM");
//						String date = dd.format(this.getScheduleDate());
//						String whereClause = " to_char(scheduleDate,'yyyy-mm') = '"+date+"'" + " AND moId is null    and workcenterRrn="+this.getWorkCenterRrn();
						List<TmpHjdImport> wses = adManager.getEntityList(Env.getOrgRrn(),TmpHjdImport.class,Integer.MAX_VALUE, null ,null);
						for(TmpHjdImport ws : wses){
							adManager.deleteEntity(ws);
						}
						for(TmpHjdImport hjdImport : progress.getHjdImports()){
							adManager.saveEntity(this.getTableManager().getADTable().getObjectRrn(), hjdImport, Env.getUserRrn());
						}
						UI.showInfo(Message.getString("ppm.upload_successful"));
					} else {
						List<PasErrorLog> errlogs = progress.getErrLogs();
						boolean viewErr = UI.showConfirm(String.format(Message.getString("ppm.upload_data_has_error"), errlogs.size()));
						if(viewErr) {
							ErrorLogDisplayDialog dialog = new ErrorLogDisplayDialog(errlogs, UI.getActiveShell());
							dialog.open();
						}
					}
				}
				refreshAdapter();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
}

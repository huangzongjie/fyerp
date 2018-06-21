package com.graly.erp.pdm.material.firstbom;

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
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
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
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.TempMaterial;
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



public class MaterialImportSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MaterialImportSection.class);
	protected ToolItem itemImport;
	protected VStorageMaterial selectedLine;
	protected ToolItem itemFinancialOverseas;
	protected ToolItem itemFinancialOverseasDetail;
	protected FirstBomSection firstBomSection;

	public MaterialImportSection(EntityTableManager tableManager,FirstBomSection firstBomSection) {
		super(tableManager);
		this.firstBomSection = firstBomSection;
		setWhereClause("1<>1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemBarcode(tBar);
		createToolItemRun(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	 
	protected void createToolItemDelete(ToolBar tBar) {
		itemImport = new ToolItem(tBar, SWT.PUSH);
		itemImport.setText("清空");
		itemImport.setImage(SWTResourceCache.getImage("barcode"));
		itemImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	protected void createToolItemBarcode(ToolBar tBar) {
		itemImport = new ToolItem(tBar, SWT.PUSH);
		itemImport.setText("导入");
		itemImport.setImage(SWTResourceCache.getImage("barcode"));
		itemImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				importAdapter();
			}
		});
	}
	
	protected void createToolItemRun(ToolBar tBar) {
		itemImport = new ToolItem(tBar, SWT.PUSH);
		itemImport.setText("运算");
		itemImport.setImage(SWTResourceCache.getImage("barcode"));
		itemImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				runAdapter();
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
//	protected void queryAdapter() {
//		if (queryDialog != null) {
//			queryDialog.setVisible(true);
//		} else {
//			queryDialog =  new MaterialQueryDialog(UI.getActiveShell(), tableManager, this,this);
//			queryDialog.open();
//		}
//	}
	
	protected void refreshSection() {
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			List<TempMaterial> tms = adManager.getEntityList(Env.getOrgRrn(), TempMaterial.class,Integer.MAX_VALUE,"1=1",null);
			this.viewer.setInput(tms);
			this.viewer.refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
	}
	
	protected void runAdapter() {
		PDMManager pdmManager;
		try {
			pdmManager = Framework.getService(PDMManager.class);
			pdmManager.runTempFirstBom();
			UI.showInfo("运算成功");
			refresh();
			firstBomSection.getFirstBomResultSection().refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
	}
	
	  

	@Override
	public void refresh() {
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			List<TempMaterial> tms = adManager.getEntityList(Env.getOrgRrn(), TempMaterial.class,Integer.MAX_VALUE,"1=1",null);
			this.viewer.setInput(tms);
			//this.viewer.refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FirstBomSection getMaterialNewSection() {
		return firstBomSection;
	}

	public void setMaterialNewSection(FirstBomSection firstBomSection) {
		this.firstBomSection = firstBomSection;
	}
	
	

	protected void importAdapter() {
		try {
			FileDialog fileDialog = new FileDialog(UI.getActiveShell(),
					SWT.OPEN);
			// 设置初始路径
			fileDialog.setFilterPath("C:/");
			// 设置扩展名过滤
			String[] filterExt = { "*.xls;*.xlsx" };
			fileDialog.setFilterExtensions(filterExt);
			// 打开文件对话框，返回选择的文件
			String selectedFile = fileDialog.open();
			if (selectedFile != null) {
				if (!selectedFile.contains(".xls")) {
					UI.showWarning(Message
							.getString("ppm.upload_file_type_not_support"));
					return;
				} else {

					File file = new File(selectedFile);

					HSSFWorkbook workbook = new HSSFWorkbook(
							new FileInputStream(file));
					// 获得第一张sheet
					HSSFSheet sheet = workbook.getSheetAt(0);
					// 获得sheet总行数
					int rowCount = sheet.getLastRowNum();
					logger.info("found excel rows count:" + rowCount);
					if (rowCount < 1) {
						return;
					}

					Map<String,List<MovementLine>> maps = new HashMap<String,List<MovementLine>>();
					List<TempMaterial> tms = new ArrayList<TempMaterial>();
					for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {//从第二行开始遍历,第一行是标题
						// 获得行对象
						HSSFRow row = sheet.getRow(rowIndex);
						if (row != null) {
						 
							
							 
							TempMaterial tm = new TempMaterial();
							tm.setOrgRrn(Env.getOrgRrn());
							tm.setIsActive(true);
							tm.setMaterialId(getCellString(row.getCell(0)));
							//物料编号
							tm.setQty(new BigDecimal(getCellString(row.getCell(1))));
							tms.add(tm);
						}
					}
					ADManager  adManager = Framework.getService(ADManager.class);
					for(TempMaterial tm : tms){
						adManager.saveEntity(tm,Env.getUserRrn());
					}
				}
				UI.showInfo("导入成功");
			}
		} catch (Exception e) {
			UI.showError("导入失败");
			logger.error("Error at AdjustInSection : importAdapter() " + e);
		}
		
		refreshAdapter();
	}
	

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
	
	protected void deleteAdapter() {
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			List<TempMaterial> tms = (List<TempMaterial>) viewer.getInput();
			for(TempMaterial tm:tms){
				adManager.deleteEntity(tm);
			}
			UI.showInfo("删除成功");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 
	}
}

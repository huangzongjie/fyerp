package com.graly.erp.wip.workcenter.schedule.purchase2;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
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

import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.wip.model.TmpChfImport;
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
		setWhereClause("1=1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemImport(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
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
						List<TmpChfImport> wses = adManager.getEntityList(Env.getOrgRrn(),TmpChfImport.class,Integer.MAX_VALUE, null ,null);
						for(TmpChfImport ws : wses){
							adManager.deleteEntity(ws);//
						}
						for(TmpChfImport chfImport : progress.getChfImports()){
							adManager.saveEntity(this.getTableManager().getADTable().getObjectRrn(), chfImport, Env.getUserRrn());
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

package com.graly.erp.bj.inv.spares;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.inv.QuerySection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.SparesMaterialUse;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BJSparesSection extends QuerySection {
	private Logger logger = Logger.getLogger(BJSparesSection.class);
	
	private Map<String,Object>	queryKeys;
	protected ToolItem itemExport2;//导出详细
	public BJSparesSection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1 ");
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExport2(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
 
	protected void createToolItemExport(ToolBar tBar) {
		itemExport = new ToolItem(tBar, SWT.PUSH);
		itemExport.setText(Message.getString("common.export"));
		itemExport.setImage(SWTResourceCache.getImage("export"));
		itemExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				exportAdapter();
			}
		});
	}
	
	protected void createToolItemExport2(ToolBar tBar) {
		itemExport2 = new ToolItem(tBar, SWT.PUSH);
		itemExport2.setText("导出详细信息");
		itemExport2.setImage(SWTResourceCache.getImage("export"));
		itemExport2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				exportAdapter2();
			}
		});
	}
	
	@Override
	public void refresh() {
		try {
			if(queryDialog != null){
				queryKeys = queryDialog.getQueryKeys();
			}
			List ls = new ArrayList();
			INVManager invManager = Framework.getService(INVManager.class);
//			StringBuffer searchWhereClause =  new StringBuffer();
//			searchWhereClause.append(" AND ");
			String whereClause= " AND"+getWhereClause();
			whereClause = whereClause.replace("equipmentRrn", "equipment_rrn");
			whereClause = whereClause.replace("SparesMaterialUse", "iml");

//			StringBuffer sqlWhereClause = new StringBuffer();
//			if (queryKeys != null && !queryKeys.isEmpty()) {
//				if (queryKeys.get("equipmentRrn") != null) {
//					long equipmentRrn = Long.valueOf((String) queryKeys.get("equipmentRrn"));
//					sqlWhereClause.append(" AND iml.equipment_rrn = ");
//					sqlWhereClause.append(equipmentRrn);
//				}
//				if(queryKeys.get("updated")!=null){
//					Map m = (Map)queryKeys.get("updated");
//					Date dateStart = (Date) m.get(FromToCalendarField.DATE_FROM);
//					//Add dateEnd by BruceYou 2012-3-5
//					Date dateEnd = (Date) m.get(FromToCalendarField.DATE_TO);
//					String str_dateStart="";
//					String str_dateEnd="";
//					if(dateStart!=null){
//						str_dateStart=new SimpleDateFormat("yyyy-MM-dd").format(dateStart);
//					}
//					if(dateEnd!=null){
//						str_dateEnd=new SimpleDateFormat("yyyy-MM-dd").format(dateEnd);
//					}
//					
//					if(str_dateStart!=null&&str_dateStart.trim().length()>0){
//						sqlWhereClause.append(" and iml.updated >= ").append("to_date('").append(str_dateStart).append("','yyyy-MM-dd' )");
//					}
//					if(str_dateEnd!=null&&str_dateEnd.trim().length()>0){
//						sqlWhereClause.append(" and iml.updated <= ").append("to_date('").append(str_dateEnd).append("','yyyy-MM-dd' )");
//					}
//				}
//			}
			ls = invManager.getSparesMaterialUse(Env.getOrgRrn(), whereClause);
			viewer.setInput(ls);		
			tableManager.updateView(viewer);
			this.createSectionDesc1(section);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	//导出详细
	protected void exportAdapter2() {
		try {
			FileDialog dialog = new FileDialog(UI.getActiveShell(), SWT.SAVE);
			dialog.setFilterNames(new String[] { "CSV (*.csv)" });
			dialog.setFilterExtensions(new String[] { "*.csv" }); 
			String fn = dialog.open();
			
			if(queryDialog != null){
				queryKeys = queryDialog.getQueryKeys();
			}
			String whereClause= " AND"+getWhereClause();
			whereClause = whereClause.replace("equipmentRrn", "equipment_rrn");
			whereClause = whereClause.replace("SparesMaterialUse", "iml");
			if (fn != null) {
				INVManager invManager = Framework.getService(INVManager.class);
				List<SparesMaterialUse> materialUses = invManager.getSparesMaterialUseExport(Env.getOrgRrn(), whereClause);
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable adTable = adManager.getADTable(Env.getOrgRrn(), "BJSparesMaterialExport");
				adTable = adManager.getADTableDeep(adTable.getObjectRrn());
				
				String[][] datas = new String[materialUses.size() + 1][adTable.getFields().size()];
				for (int i = 0; i < adTable.getFields().size(); i++) {
					ADField adField = adTable.getFields().get(i);
					datas[0][i] = adField.getLabel_zh();
				}
				for (int i = 0; i < materialUses.size(); i++) {
					SparesMaterialUse use = materialUses.get(i);
//					TableItem item = table.getItem(i);
					for (int j = 0; j < adTable.getFields().size(); j++) {
						switch(j){
							case 0:
								datas[i + 1][j] = use.getMovementId();
								break;
							case 1:
								if(use.getUpdated()!=null){
									String str_dateStart=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(use.getUpdated());
									datas[i + 1][j]	= str_dateStart;
								}else{
									datas[i + 1][j]  ="";
								}
	
								break;
							case 2:
								datas[i + 1][j] = use.getMaterialId();
								break;
							case 3:
								datas[i + 1][j] = use.getEquipmentName();
								break;
							case 4:
								datas[i + 1][j] = use.getQty();
								break;
							case 5:
								datas[i + 1][j] = use.getEquipmentId();
								break;
							case 6:
								datas[i + 1][j] = use.getEquipmentName();
								break;
							case 7:
								if(use.getReferencePrice()!=null){
									datas[i + 1][j] = use.getReferencePrice().toString();
								}else{
									datas[i + 1][j] = "";
								}
								
								break;
							default:
								datas[i + 1][j] = null;
								break;
								
						}
					}
				}
//				for (int i = 0; i < table.getItemCount(); i++) {
//					TableItem item = table.getItem(i);
//					for (int j = 0; j < table.getColumnCount(); j++) {
//						datas[i + 1][j] = item.getText(j);
//					}
//				}
				
				File file = new File(fn);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				CSVWriter writer = new CSVWriter(new FileWriter(file));
		        for (int i = 0; i < datas.length; i++) {
		            writer.writeNext(datas[i]);
		        }
		        writer.close();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
//	@Override
	protected void createSectionDesc1(Section section) {
		try{ 
			String text = Message.getString("common.totalshow");
			long count = ((List)viewer.getInput()).size();
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("EntityBlock : createSectionDesc ", e);
		}
	}
	
	//导出表格
	protected void exportAdapter() {
		try {
			FileDialog dialog = new FileDialog(UI.getActiveShell(), SWT.SAVE);
			dialog.setFilterNames(new String[] { "CSV (*.csv)" });
			dialog.setFilterExtensions(new String[] { "*.csv" }); 
			String fn = dialog.open();
			if (fn != null) {
				Table table = ((TableViewer)viewer).getTable();
				String[][] datas = new String[table.getItemCount() + 1][table.getColumnCount()];
				for (int i = 0; i < table.getColumnCount(); i++) {
					TableColumn column = table.getColumn(i);
					datas[0][i] = column.getText();
				}
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem item = table.getItem(i);
					for (int j = 0; j < table.getColumnCount(); j++) {
						datas[i + 1][j] = item.getText(j);
					}
				}
				
				File file = new File(fn);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				CSVWriter writer = new CSVWriter(new FileWriter(file));
		        for (int i = 0; i < datas.length; i++) {
		            writer.writeNext(datas[i]);
		        }
		        writer.close();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
}


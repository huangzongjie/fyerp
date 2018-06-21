package com.graly.erp.wip.workcenter.schedule.into2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.wip.model.WorkShopSchedule2;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class WorkScheduleImportTwoSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(WorkScheduleImportTwoSection.class);
	protected SashForm sashForm;
	protected ToolItem itemUpload;
	protected IManagedForm form;
	protected WorkScheduleSetImportTwoDialog mbatchDialog;
	protected Long workCenterRrn;
	protected Date scheduleDate;
	
	public WorkScheduleImportTwoSection(IManagedForm form){
		this.form = form;
	}
	public WorkScheduleImportTwoSection(EntityTableManager tableManager) {
		super(tableManager);
//		setWhereClause(" isCompleted  ='N' or  isCompleted  is null");
//		setWhereClause(" docStatus <> 'COMPLETED' ");
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemUpload(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemUpload(ToolBar tBar) {
		itemUpload = new ToolItem(tBar, SWT.PUSH);
		itemUpload.setText("物料导入(注、吹、缠)");
		itemUpload.setImage(SWTResourceCache.getImage("receive"));
		itemUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				uploadAdapter();
			}
		});
	}
	//注、吹、缠导入数据模板
	protected void uploadAdapter() {
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
				WorkScheduleProgressTwoDialog progressDialog = new WorkScheduleProgressTwoDialog(UI.getActiveShell());
				WorkScheduleImportTwoProgress progress = new WorkScheduleImportTwoProgress(null,
						selectedFile, this.getTableManager().getADTable(), this);
				progressDialog.run(true, true, progress);
				// 提示已成功导入或失败
				if (progress.isFinished()) {
					if(progress.isSuccess()) {
						ADManager adManager =Framework.getService(ADManager.class);
						DateFormat dd=new SimpleDateFormat("yyyy-MM");
						String date = dd.format(this.getScheduleDate());
						String whereClause = " to_char(scheduleDate,'yyyy-mm') = '"+date+"'" + "and workcenterRrn="+this.getWorkCenterRrn();
						List<WorkShopSchedule2> wses = adManager.getEntityList(Env.getOrgRrn(),WorkShopSchedule2.class,Integer.MAX_VALUE, whereClause ,null);
						for(WorkShopSchedule2 ws : wses){
							adManager.deleteEntity(ws);
						}
						for(WorkShopSchedule2 workShopSchedule : progress.getWorkShopSchedules()){
							adManager.saveEntity(this.getTableManager().getADTable().getObjectRrn(), workShopSchedule, Env.getUserRrn());
						}
						UI.showInfo(Message.getString("ppm.upload_successful"));
					} else {
						List<PasErrorLog> errlogs = progress.getErrLogs();
						boolean viewErr = UI.showConfirm(String.format(Message.getString("ppm.upload_data_has_error"), errlogs.size()));
						if(viewErr) {
							ErrorLogDisplayTwoDialog dialog = new ErrorLogDisplayTwoDialog(errlogs, UI.getActiveShell());
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
	
	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof WorkScheduleSetImportTwoDialog) {
			this.mbatchDialog = (WorkScheduleSetImportTwoDialog)dialog;
		} else {
			this.mbatchDialog = null;
		}
	}
	
 
	protected ADTable getAdTableByName(String tableName) {
		ADTable adTable = null;;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = (ADTable)entityManager.getADTable(0, tableName);
		} catch(Exception e) {
			logger.error("WorkShopScheduleQuerySection : getAdTableByName()", e);
		}
		return adTable;
	}
	public WorkScheduleImportTwoSection getImportSection() {
		// TODO Auto-generated method stub
		return this;
	}
	public Long getWorkCenterRrn() {
		return workCenterRrn;
	}
	public void setWorkCenterRrn(Long workCenterRrn) {
		this.workCenterRrn = workCenterRrn;
	}
	public Date getScheduleDate() {
		return scheduleDate;
	}
	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
}

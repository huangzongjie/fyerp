package com.graly.erp.wip.workcenter.schedule.into;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.wip.model.WorkShopSchedule;
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

public class WorkScheduleImportSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(WorkScheduleImportSection.class);
	protected SashForm sashForm;
	protected ToolItem itemUpload;
	protected IManagedForm form;
	protected WorkScheduleSetImportDialog mbatchDialog;
	protected Long workCenterRrn;
	protected Date scheduleDate;
	
	public WorkScheduleImportSection(IManagedForm form){
		this.form = form;
	}
	public WorkScheduleImportSection(EntityTableManager tableManager) {
		super(tableManager);
//		setWhereClause(" isCompleted  ='N' or  isCompleted  is null");
//		setWhereClause(" docStatus <> 'COMPLETED' ");
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemUpload(tBar);
		createToolItemUpload2(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemUpload(ToolBar tBar) {
		itemUpload = new ToolItem(tBar, SWT.PUSH);
		itemUpload.setText("�������");
		itemUpload.setImage(SWTResourceCache.getImage("receive"));
		itemUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				uploadAdapter();
			}
		});
	}
	
	protected void createToolItemUpload2(ToolBar tBar) {
		itemUpload = new ToolItem(tBar, SWT.PUSH);
		itemUpload.setText("���Ƽ�����");
		itemUpload.setImage(SWTResourceCache.getImage("receive"));
		itemUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				uploadAdapter2();
			}
		});
	}
	
	protected void uploadAdapter() {
		try {
			FileDialog fileDialog = new FileDialog(UI.getActiveShell(), SWT.OPEN);
			// ���ó�ʼ·��
			fileDialog.setFilterPath("C:/");
			// ������չ������
			String[] filterExt = { "*.xls"};
			fileDialog.setFilterExtensions(filterExt);
			// ���ļ��Ի��򣬷���ѡ����ļ�
			String selectedFile = fileDialog.open();
			if (selectedFile != null) {
				if (!selectedFile.contains(".xls")) {
					UI.showWarning(Message.getString("ppm.upload_file_type_not_support"));
					return;
				}
				WorkScheduleProgressDialog progressDialog = new WorkScheduleProgressDialog(UI.getActiveShell());
				WorkScheduleImportProgress progress = new WorkScheduleImportProgress(null,
						selectedFile, this.getTableManager().getADTable(), this);
				progressDialog.run(true, true, progress);
				// ��ʾ�ѳɹ������ʧ��
				if (progress.isFinished()) {
					if(progress.isSuccess()) {
						ADManager adManager =Framework.getService(ADManager.class);
						DateFormat dd=new SimpleDateFormat("yyyy-MM");
						String date = dd.format(this.getScheduleDate());
						String whereClause = " to_char(scheduleDate,'yyyy-mm') = '"+date+"'" + "  AND moId is not null and workcenterRrn="+this.getWorkCenterRrn();
						List<WorkShopSchedule> wses = adManager.getEntityList(Env.getOrgRrn(),WorkShopSchedule.class,Integer.MAX_VALUE, whereClause ,null);
						for(WorkShopSchedule ws : wses){
							adManager.deleteEntity(ws);
						}
						for(WorkShopSchedule workShopSchedule : progress.getWorkShopSchedules()){
							adManager.saveEntity(this.getTableManager().getADTable().getObjectRrn(), workShopSchedule, Env.getUserRrn());
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
	
	/*
	 * ����ˮ�����������Ʒ����Զ�������İ��Ʒ�ų̵���
	 * û�й�����
	 * */
	protected void uploadAdapter2() {
		try {
			FileDialog fileDialog = new FileDialog(UI.getActiveShell(), SWT.OPEN);
			// ���ó�ʼ·��
			fileDialog.setFilterPath("C:/");
			// ������չ������
			String[] filterExt = { "*.xls"};
			fileDialog.setFilterExtensions(filterExt);
			// ���ļ��Ի��򣬷���ѡ����ļ�
			String selectedFile = fileDialog.open();
			if (selectedFile != null) {
				if (!selectedFile.contains(".xls")) {
					UI.showWarning(Message.getString("ppm.upload_file_type_not_support"));
					return;
				}
				WorkScheduleProgressDialog progressDialog = new WorkScheduleProgressDialog(UI.getActiveShell());
				WorkScheduleImportProgress2 progress = new WorkScheduleImportProgress2(null,
						selectedFile, this.getTableManager().getADTable(), this);
				progressDialog.run(true, true, progress);
				// ��ʾ�ѳɹ������ʧ��
				if (progress.isFinished()) {
					if(progress.isSuccess()) {
						ADManager adManager =Framework.getService(ADManager.class);
						DateFormat dd=new SimpleDateFormat("yyyy-MM");
						String date = dd.format(this.getScheduleDate());
						String whereClause = " to_char(scheduleDate,'yyyy-mm') = '"+date+"'" + " AND moId is null    and workcenterRrn="+this.getWorkCenterRrn();
						List<WorkShopSchedule> wses = adManager.getEntityList(Env.getOrgRrn(),WorkShopSchedule.class,Integer.MAX_VALUE, whereClause ,null);
						for(WorkShopSchedule ws : wses){
							adManager.deleteEntity(ws);
						}
						for(WorkShopSchedule workShopSchedule : progress.getWorkShopSchedules()){
							adManager.saveEntity(this.getTableManager().getADTable().getObjectRrn(), workShopSchedule, Env.getUserRrn());
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
	
	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof WorkScheduleSetImportDialog) {
			this.mbatchDialog = (WorkScheduleSetImportDialog)dialog;
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
	public WorkScheduleImportSection getImportSection() {
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

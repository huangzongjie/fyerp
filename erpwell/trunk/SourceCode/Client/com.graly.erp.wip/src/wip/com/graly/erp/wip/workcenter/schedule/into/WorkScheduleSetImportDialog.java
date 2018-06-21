package com.graly.erp.wip.workcenter.schedule.into;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class WorkScheduleSetImportDialog extends ExtendDialog {
	private PrInternalQueryDialog queryDialog;
	private EntityTableManager tableManager;
	protected boolean isCreateQuery;
	private Text txtLotId;
	public WorkScheduleSetImportDialog() {
		super();
		queryDialog = new PrInternalQueryDialog(UI.getActiveShell());
	}
	
	public WorkScheduleSetImportDialog(boolean isCreateQuery) {
		this.isCreateQuery = isCreateQuery;
	}

	@Override
	public int open() {
		int id = queryDialog.open();
		setQueryDialogToSection();
		return id;
	}

	// 调用该方法必须保证tableId不为空
	protected EntityTableManager getEntityTableManager() {
		ADManager manager;
		try {
			manager = Framework.getService(ADManager.class);
			tableManager = new EntityTableManager(manager.getADTable(Long.valueOf(getTableId())));
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return tableManager;
	}
	
	protected void setQueryDialogToSection() {
		if(getParent() instanceof EntityEditor){
			EntityEditor editor = (EntityEditor) getParent();
			if(editor.getActivePageInstance() instanceof WorkScheduleImportSection){
				WorkScheduleImportSection page = (WorkScheduleImportSection) editor.getActivePageInstance();
				WorkScheduleImportSection section = page.getImportSection();
			//	queryDialog.setIRefresh(section);
				section.setQueryDialog(queryDialog);
				((WorkScheduleImportSection)section).setExtendDialog(this);
			}
		}
	}

	public PrInternalQueryDialog getEntityQueryDialog() {
		return this.queryDialog;
	}
	
	public void setEntityQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = (PrInternalQueryDialog)queryDialog;
	}

	class PrInternalQueryDialog extends EntityQueryDialog {
		ADTable adTable;
		public PrInternalQueryDialog(Shell parent) {
			super(parent);
		}

		public PrInternalQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public PrInternalQueryDialog(Shell parent,
				ADTable adTable, IRefresh refresh) {
			super(parent, null, refresh);
			this.adTable = adTable;
		}
		
		@Override
	    protected Control createDialogArea(Composite parent) {
	        setTitleImage(SWTResourceCache.getImage("search-dialog"));
	        setTitle(Message.getString("common.search_Title"));
	        setMessage(Message.getString("common.keys"));
	        Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.verticalSpacing = 0;
			layout.horizontalSpacing = 0;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite.setFont(parent.getFont());
			// Build the separator line
			Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL
					| SWT.SEPARATOR);
			titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			if(tableManager == null) {
				setTableManager(getEntityTableManager());
			}
			queryForm = new QueryForm(composite, SWT.NONE, tableManager.getADTable());
	        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	        return composite;
	    }

		@Override
		protected void createAdvanceButtonBar(Composite parent) {}

		@Override
		protected void okPressed() {
			WorkScheduleImportEditor editor = (WorkScheduleImportEditor) getParent();
			WorkScheduleImportEntryPage page = (WorkScheduleImportEntryPage) editor.getActivePageInstance();
			WorkScheduleImportSection section = page.getImportSection();
			Object workCenterRrn = this.queryForm.getFields().get("workcenterRrn").getValue();
			Date scheduleDateValue = (Date) this.queryForm.getFields().get("scheduleDate").getValue();
			if(workCenterRrn==null){
				UI.showError("车间不能为空");
				return;
			}
			section.setWorkCenterRrn(Long.valueOf(workCenterRrn.toString()));
			section.setScheduleDate(scheduleDateValue);
			section.setWhereClause(" workcenterRrn="+section.getWorkCenterRrn());
			section.refresh();
//			okPressed();
			setReturnCode(OK);
//			iRefresh.refresh();
	       this.setVisible(false);
		}
		
		public EntityTableManager getTableManager(){
			return super.tableManager;
		}
		
		public void setTableManager(EntityTableManager tableManager){
			super.tableManager = tableManager;
		}
		
		public ADTable getADTable() {
			if(tableManager != null)
				return tableManager.getADTable();
			return adTable;
		}
	}
}

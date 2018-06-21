package com.graly.erp.ppm.shortage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ShortageQueryDialog extends ExtendDialog {
	private InnerQueryDialog queryDialog;
	
	public ShortageQueryDialog() {
		super();
		queryDialog = new InnerQueryDialog(UI.getActiveShell());
	}

	public ShortageQueryDialog(Shell parent) {
		super(parent);
	}

	public ShortageQueryDialog(String tableId, Object parent) {
		super(tableId, parent);
	}

	@Override
	public int open() {
		return queryDialog.open();
	}

	@Override
	public void setTableId(String tableId) {
		super.setTableId(tableId);
		ADManager manager;
		try {
			manager = Framework.getService(ADManager.class);
			EntityTableManager tableManager = new EntityTableManager(manager.getADTable(Long.valueOf(getTableId())));
			queryDialog.setTableManager(tableManager);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	@Override
    protected void okPressed() {
		if(getParent() != null){
			if(getParent() instanceof EntityEditor){
				EntityEditor editor = (EntityEditor) getParent();
				if(editor.getActivePageInstance() instanceof SectionEntryPage){
					SectionEntryPage page = (SectionEntryPage) editor.getActivePageInstance();
					MasterSection section = page.getMasterSection();
					queryDialog.setIRefresh(section);
					
					section.setQueryDialog(queryDialog);
				}
			}
		}
    }
	
	class InnerQueryDialog extends EntityQueryDialog{
		
		ADTable adTable;
		protected Button	isIncludeTransit;
		public InnerQueryDialog(Shell parent) {
			super(parent);
		}
		
		public InnerQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public InnerQueryDialog(Shell parent,
				ADTable adTable, IRefresh refresh) {
			super(parent, null, refresh);
			this.adTable = adTable;
		}

		@Override
		protected void createDialogForm(Composite composite) {
			composite.setBackgroundMode(SWT.INHERIT_FORCE);
			queryForm = new QueryForm(composite, SWT.NONE, tableManager.getADTable());
	        queryForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			isIncludeTransit = new Button(queryForm.getFormBody(), SWT.CHECK);
			isIncludeTransit.setText("ÊÇ·ñ¿¼ÂÇÔÚÍ¾");
			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			isIncludeTransit.setLayoutData(gd);
		}
		
		@Override
		protected void createAdvanceButtonBar(Composite parent) {}
		
		@Override
		protected void okPressed() {
			ShortageQueryDialog.this.okPressed();
			createWhereClause();
			if("1=1".equals(sb.toString().trim()))
				return;
			setReturnCode(OK);
			iRefresh.setWhereClause(sb.toString());
			iRefresh.refresh();
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
		
		public boolean getIsIncludeTransit(){
			return isIncludeTransit.getSelection();
		}
}

}

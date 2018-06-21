package com.graly.erp.pdm.material.query;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MaterialQueryDialog extends ExtendDialog {
	private InnerQueryDialog queryDialog;
	private EntityTableManager tableManager;
	
	public MaterialQueryDialog() {
		super();
		queryDialog = new InnerQueryDialog(UI.getActiveShell());
	}
	
	@Override
	public int open() {
		return queryDialog.open();
	}

	@Override
	public void setTableId(String tableId) {
		super.setTableId(tableId);
		if(queryDialog.getTableManager() == null){
			ADManager manager;
			try {
				manager = Framework.getService(ADManager.class);
				tableManager = new EntityTableManager(manager.getADTableDeep(Long.valueOf(tableId)));
				queryDialog.setTableManager(tableManager);
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}

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
		
		public InnerQueryDialog(Shell parent) {
			super(parent);
		}

		@Override
		protected void okPressed() {
			MaterialQueryDialog.this.okPressed();
			super.okPressed();
		}	
		
		public EntityTableManager getTableManager(){
			return super.tableManager;
		}
		
		public void setTableManager(EntityTableManager tableManager){
			super.tableManager = tableManager;
		}
	}

	public Map<String, Object> getQueryKeys() {
		return queryDialog.getQueryKeys();
	}

	public void setQueryKeys(Map<String, Object> queryKeys) {
		queryDialog.setQueryKeys(queryKeys);
	}
}

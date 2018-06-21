package com.graly.framework.base.entitymanager.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.activeentity.client.ADManager;

public class EntityEditorInput implements IEditorInput {
	
	private long tableId;
	private ADTable table;
	private String authorityKey;
	
	public EntityEditorInput(long tableId, String authorityKey) {
		try {
			this.tableId = tableId;
			this.authorityKey = authorityKey;
			ADManager manager = Framework.getService(ADManager.class);
			setTable(manager.getADTableDeep(tableId));		
			getTable().setAuthorityKey(authorityKey);
		} catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        }
    }
	
	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Error";
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		// TODO Auto-generated method stub
		return " ";
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTable(ADTable table) {
		this.table = table;
	}

	public ADTable getTable() {
		return table;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	public long getTableId() {
		return tableId;
	}

	public void setAuthorityKey(String authorityKey) {
		this.authorityKey = authorityKey;
	}

	public String getAuthorityKey() {
		return authorityKey;
	}

}

package com.graly.promisone.base.entitymanager.editor;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.promisone.security.client.SecurityManager;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.activeentity.client.ADManager;

public class EntityEditorInput implements IEditorInput {
	
	private long tableId;
	private ADTable table;
	
	public EntityEditorInput(long tableId) {
		try {
			this.setTableId(tableId);
			ADManager manager = Framework.getService(ADManager.class);
			setTable(manager.getADTableDeep(tableId));		
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
		return "ffffff";
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

}

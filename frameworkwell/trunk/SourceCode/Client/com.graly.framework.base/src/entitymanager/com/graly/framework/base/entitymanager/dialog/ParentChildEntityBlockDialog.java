package com.graly.framework.base.entitymanager.dialog;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.ChildEntityBlock;
import com.graly.framework.base.entitymanager.forms.ParentChildEntityBlock;

public class ParentChildEntityBlockDialog extends EntityBlockDialog {
	
	protected String whereClause;
	private Object parentObject;
	protected ADTable childTable;
	
	public ParentChildEntityBlockDialog(Shell parent) {
        super(parent);
    }
	
	public ParentChildEntityBlockDialog(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable);
		this.whereClause = whereClause;
		this.setParentObject(parentObject);
		this.childTable = childTable;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new ParentChildEntityBlock(adTable, getParentObject(), whereClause, childTable);
	}

	public void setParentObject(Object parentObject) {
		this.parentObject = parentObject;
	}

	public Object getParentObject() {
		if(block instanceof ParentChildEntityBlock)
			return ((ParentChildEntityBlock)block).getParentObject();
		return parentObject;
	}
}

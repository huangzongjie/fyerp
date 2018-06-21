package com.graly.framework.base.entitymanager.dialog;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.ChildEntityBlock;

public class ChildEntityBlockDialog extends EntityBlockDialog {
	
	protected String whereClause;
	protected Object parentObject;
	
	public ChildEntityBlockDialog(Shell parent) {
        super(parent);
    }
	
	public ChildEntityBlockDialog(Shell parent, ADTable table, String whereClause, Object parentObject){
		super(parent, table);
		this.whereClause = whereClause;
		this.parentObject = parentObject;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new ChildEntityBlock(new EntityTableManager(adTable), whereClause, parentObject);
	}
}

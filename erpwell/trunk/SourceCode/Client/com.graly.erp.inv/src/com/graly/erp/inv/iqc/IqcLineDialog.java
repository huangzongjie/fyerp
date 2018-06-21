package com.graly.erp.inv.iqc;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityBlockDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class IqcLineDialog extends EntityBlockDialog {
	protected String whereClause;
	protected Object parentObject;
	
	public IqcLineDialog(Shell parent) {
        super(parent);
    }
	
	public IqcLineDialog(Shell parent, ADTable table, String whereClause, Object parentObject){
		super(parent, table);
		this.whereClause = whereClause;
		this.parentObject = parentObject;
	}
	
	protected void createBlock(ADTable adTable) {
		block = new IqcLineEntityBlock(new EntityTableManager(adTable), whereClause, parentObject);
	}
}
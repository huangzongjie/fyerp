package com.graly.erp.xz.pur.request;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ChildEntityBlockDialog;

public class XZRequisitionLineBlockDialog extends ChildEntityBlockDialog {
	// 设置是否只是查看,默认为false,当其它模块关联要查看PR时,可设置此值为true
	private boolean isView = false;
	private int i=0;
//	protected int mStyle = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	protected int mStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

	public XZRequisitionLineBlockDialog(Shell parent) {
        super(parent);
    }
	
	public XZRequisitionLineBlockDialog(Shell parent, ADTable table, String whereClause, Object parentObject){
		super(parent, table, whereClause, parentObject);
		this.isView = false;
	}
	
	public XZRequisitionLineBlockDialog(Shell parent, ADTable table, String whereClause, Object parentObject,int i){
		super(parent, table, whereClause, parentObject);
		this.isView = false;
		this.i=i;
	}
	
	public XZRequisitionLineBlockDialog(Shell parent, ADTable table, String whereClause, Object parentObject, boolean isView){
		super(parent, table, whereClause, parentObject);
		this.isView = isView;
	}
	
	protected void createBlock(ADTable adTable) {
		ColorEntityTableManager entityTableManager = new ColorEntityTableManager(adTable);
		entityTableManager.setStyle(mStyle);		
		block = new XZRequisitionLineEntityBlock(entityTableManager, whereClause, parentObject, isView,i);	
	}

	public boolean isView() {
		return isView;
	}

	public void setIsView(boolean isView) {
		this.isView = isView;
		if(block != null)
			((XZRequisitionLineEntityBlock)block).setIsView(true);
	}

}

package com.graly.erp.ppm.saleplan.temp.prepare2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ParentChildEntityBlockDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;

public class TpsPrepareDialog2 extends ParentChildEntityBlockDialog{
	public TpsPrepareDialog2(Shell parent) {
        super(parent);
    }
	
	public TpsPrepareDialog2(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	public TpsPrepareDialog2(Shell parent, ADTable parentTable, 
			String whereClause, Object parentObject, ADTable childTable,boolean flag){
		super(parent, parentTable, whereClause, parentObject, childTable);
	}
	
	protected void createBlock(ADTable adTable) {
		EntityTableManager tableManager = new EntityTableManager(adTable);
		tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION);
		block = new TpsPrepareEntityBlock2(tableManager);
		StringBuffer whereClause = new StringBuffer();
		whereClause.append(" isGenerate = 'N' and tpsStatus = 'DRAFTED' ");
		whereClause.append(" and orgRrn = ");
		whereClause.append(Env.getOrgRrn());
		TpsLinePrepare tpsLinePrepare = (TpsLinePrepare) getParentObject();
		if(tpsLinePrepare!=null){
			whereClause.append(" and tpsId = '");
			whereClause.append(tpsLinePrepare.getTpsId());
			whereClause.append("' ");
		}
		block.setWhereClause(whereClause.toString());
	}
}

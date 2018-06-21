package com.graly.erp.wip.workcenter.schedule.query;
import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
public class WorkShopScheduleQueryDialog extends EntityQueryDialog{
	private ScheduleSection parentSection;
	public WorkShopScheduleQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh,ScheduleSection parentSection) {
		super(parent, tableManager, refresh);
		this.parentSection = parentSection;
	}

	public WorkShopScheduleQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh,
			Method refreshMethod) {
		super(parent, tableManager, refresh, refreshMethod);
	}

	public WorkShopScheduleQueryDialog(Shell parent) {
		super(parent);
	}

	
	@Override
    protected void okPressed() {
		super.okPressed();
		String workcenterId = (String) this.queryKeys.get("workcenterId");
		NoScheduleSection noScheduleSection = parentSection.getParentSection().getNoScheduleSection();
		if(workcenterId!=null){
			noScheduleSection.setWhereClause("workcenterId ='"+workcenterId+"'");
			noScheduleSection.refresh();
		}else{
			noScheduleSection.setWhereClause("");
			noScheduleSection.refresh();
		}
		
	}
	
}

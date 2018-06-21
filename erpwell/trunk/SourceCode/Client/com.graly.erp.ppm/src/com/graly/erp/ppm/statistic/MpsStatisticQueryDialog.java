package com.graly.erp.ppm.statistic;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;

public class MpsStatisticQueryDialog extends EntityQueryDialog {
	private static final Logger logger = Logger.getLogger(MpsStatisticQueryDialog.class);
	
	public MpsStatisticQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh) {
		super(parent, tableManager, refresh);
	}
	
	@Override
    protected void okPressed() {
		if(!validateQueryKey()) return;
		fillQueryKeys();
		if(iRefresh instanceof MpsStatisticSection){
			MpsStatisticSection parentSection = (MpsStatisticSection)iRefresh;
			parentSection.setQueryKeys(queryKeys);
			parentSection.refresh();
		}
		setReturnCode(OK);
        this.setVisible(false);
    }
}

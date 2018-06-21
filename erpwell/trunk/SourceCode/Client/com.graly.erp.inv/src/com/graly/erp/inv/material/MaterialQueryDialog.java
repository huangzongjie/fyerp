package com.graly.erp.inv.material;
import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
public class MaterialQueryDialog extends EntityQueryDialog4WC{
	private MaterialSection parentSection;
	public MaterialQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh,MaterialSection parentSection) {
		super(parent, tableManager, refresh);
		this.parentSection = parentSection;
	}

	public MaterialQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh) {
		super(parent, tableManager, refresh);
	}


	
	@Override
    protected void okPressed() {
		super.okPressed();
		String materialId = (String) this.queryKeys.get("materialId");
		WorkShopStorageSection noScheduleSection = parentSection.getMaterialNewSection().getWorkShopStorageSection();
		if(materialId!=null){
			noScheduleSection.setWhereClause("materialId ='"+materialId+"'");
			noScheduleSection.refresh();
		}
	}
	
}

package com.graly.erp.inv.material.onhandvswriteoff;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.materialtrace.MaterialTraceSection2;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.MasterSectionDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class MaterialTraceSectionDialog extends MasterSectionDialog {
	protected Map<String,Object> queryKeys;
	
	public MaterialTraceSectionDialog(Shell parent, ADTable table, Map<String,Object> queryKeys) {
		super(parent, table);
		this.queryKeys = queryKeys;
	}
	
	@Override
	protected void createSection(ADTable adTable) {
//		section = new MaterialTraceSection2(new EntityTableManager(adTable), queryKeys);
	}
}

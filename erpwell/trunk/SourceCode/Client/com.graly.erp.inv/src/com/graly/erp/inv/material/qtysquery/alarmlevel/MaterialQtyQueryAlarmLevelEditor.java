package com.graly.erp.inv.material.qtysquery.alarmlevel;

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.erp.inv.materialtrace.MaterialTraceEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MaterialQtyQueryAlarmLevelEditor extends EntityEditor {

	public static final String EDITOR_ID = "com.graly.erp.inv.material.qtysquery.alarmlevel.MaterialQtyQueryAlarmLevelEditor";
	public Logger logger = Logger.getLogger(MaterialQtyQueryAlarmLevelEditor.class);
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MaterialQtyQueryAlarmLevelEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			logger.error(e);
		}
	}
	
	@Override
	public void setFocus() {
		page.setFocus();
	}

}

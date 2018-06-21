package com.graly.erp.inv.material.qtysquery.js;

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class JSMaterialQtyQueryEditor extends EntityEditor {

	public static final String EDITOR_ID = "com.graly.erp.inv.material.qtysquery.js.JSMaterialQtyQueryEditor";
	public Logger logger = Logger.getLogger(JSMaterialQtyQueryEditor.class);
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new JSMaterialQtyQueryEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			logger.error(e);
		} catch(Exception e ){
			
		}
	}
	
	@Override
	public void setFocus() {
		page.setFocus();
	}

}

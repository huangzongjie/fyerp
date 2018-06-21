package com.graly.erp.inv.workshop.services.query;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;



public class ServicesQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(ServicesQuerySection.class);
	protected ToolItem itemBarcode;
	protected VStorageMaterial selectedLine;

	public ServicesQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause("1<>1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	
	protected void refreshSection() {
		refresh();
	}
}

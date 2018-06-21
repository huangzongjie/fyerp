package com.graly.erp.ppm.overouted;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.shortage.ShortageQueryDialog;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;

public class OverOutedSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(OverOutedSection.class);
	
	protected TableListManager tableListManager;
	protected List input;
	
	public OverOutedSection(TableListManager tableListManager) {
		this.tableListManager = tableListManager;
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		createToolItemSearch(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createSectionDesc(Section section) {
		int count = 0;
		if(input != null) count = input.size();
		String text = Message.getString("common.totalshow");
		text = String.format(text, String.valueOf(count), String.valueOf(count));
		section.setDescription("  " + text);
	}
	
	@Override
	protected void createNewViewer(Composite client, IManagedForm form) {
		viewer = tableListManager.createViewer(client, form.getToolkit());
	}

	@Override
	public void refresh() {
		try{
			PPMManager ppmManager = Framework.getService(PPMManager.class);
			String month = ((OverOutedQueryDialog.InnerQueryDialog)queryDialog).getMonth();
			input = ppmManager.getOverOutedMaterials(Env.getOrgRrn(), month, getWhereClause());
			viewer.setInput(input);
			tableListManager.updateView(viewer);
			createSectionDesc(section);
		}catch (Exception e){
			logger.equals(e);
		}
	}
	
	protected ADTable getADTable() {
		return tableListManager.getADTable();
	}
}

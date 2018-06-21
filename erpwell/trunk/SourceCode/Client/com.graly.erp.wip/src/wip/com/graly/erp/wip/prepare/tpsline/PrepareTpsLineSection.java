package com.graly.erp.wip.prepare.tpsline;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.TpsLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class PrepareTpsLineSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(PrepareTpsLineSection.class);
//	protected EntityTableManager tableListManager;
//	protected List input;
	protected ToolItem itemPrepare;
	
	public PrepareTpsLineSection() {
		super();
	}

	public PrepareTpsLineSection(EntityTableManager tableListManager) {
		super(tableListManager);
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemPrepare(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExport(tBar);
		createToolItemSearch(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemPrepare(ToolBar tBar) {
		itemPrepare = new ToolItem(tBar, SWT.PUSH);
		itemPrepare.setText("ÔËËã");
		itemPrepare.setImage(SWTResourceCache.getImage("export"));
		itemPrepare.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				prepareAdapter();
			}
		});
	}
	
	protected void prepareAdapter() {
		try{
			CheckboxTableViewer tableView =  (CheckboxTableViewer) getViewer();
			Object[] objects =   tableView.getCheckedElements();
			if(objects!=null && objects.length >0){
				List tpsLines =  Arrays.asList(objects);
		    	PrepareTpsProgressMonitorDialog progressDiglog = new PrepareTpsProgressMonitorDialog(UI.getActiveShell(),
		    			"",tpsLines);
		    	progressDiglog.run(true, true, progressDiglog.createProgress());
			}
		}catch(Exception e ){
			UI.showError(e.getMessage());
		}
		refresh();
	}
	
//	protected void createSectionDesc(Section section) {
//		int count = 0;
//		if(input != null) count = input.size();
//		String text = Message.getString("common.totalshow");
//		text = String.format(text, String.valueOf(count), String.valueOf(count));
//		section.setDescription("  " + text);
//	}
	
//	@Override
//	protected void createNewViewer(Composite client, IManagedForm form) {
//		viewer = tableListManager.createViewer(client, form.getToolkit());
//	}

//	@Override
//	public void refresh() {
//		try{
//			PPMManager ppmManager = Framework.getService(PPMManager.class);
//			boolean isIncludeTransit = ((PrepareTpsLineQueryDialog.InnerQueryDialog)queryDialog).getIsIncludeTransit();
//			input = ppmManager.getShortageMaterials(Env.getOrgRrn(), getWhereClause(), isIncludeTransit);
//			viewer.setInput(input);
//			tableListManager.updateView(viewer);
//			createSectionDesc(section);
//		}catch (Exception e){
//			logger.equals(e);
//		}
//	}
	
//	protected ADTable getADTable() {
//		return tableListManager.getADTable();
//	}
}

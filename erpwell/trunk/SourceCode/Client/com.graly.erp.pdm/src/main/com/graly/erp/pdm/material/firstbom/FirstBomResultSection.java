package com.graly.erp.pdm.material.firstbom;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.pdm.model.TempFirstBom;
import com.graly.erp.wip.model.VWorkShopStorage;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;



public class FirstBomResultSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(FirstBomResultSection.class);
	protected ToolItem itemBarcode;
	protected VWorkShopStorage selectedWorkShopStorage;
	protected ToolItem itemFinancialOverseas;
	protected ToolItem itemFinancialOverseasDetail;
	protected FirstBomSection firstBomSection;

	public FirstBomResultSection(EntityTableManager tableManager,FirstBomSection firstBomSection) {
		super(tableManager);
		this.firstBomSection = firstBomSection;
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
	
	
	protected void queryAdapter() {
//		if (queryDialog != null) {
//			queryDialog.setVisible(true);
//		} else {
//			queryDialog =  new EntityQueryDialogWS(UI.getActiveShell(), tableManager, this);
//			queryDialog.open();
//		}
	}
	
	protected void refreshSection() {
		refresh();
	}
 
	@Override
	public void refresh() {
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			List<TempFirstBom> tms = adManager.getEntityList(Env.getOrgRrn(), TempFirstBom.class,Integer.MAX_VALUE,"1=1",null);
			this.viewer.setInput(tms);
			//this.viewer.refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

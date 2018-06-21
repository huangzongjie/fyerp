package com.graly.erp.wip.workcenter2;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.ListItemAdapter;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.wip.client.WipManager;

public class InvMaterialSection2 {
	
	private static final Logger logger = Logger.getLogger(InvMaterialSection2.class);
	protected static final String TABLE_NAME = "WIPWorkCenterOnHand";
	protected ADTable adTable;
	protected IManagedForm form;
	protected ToolItem itemExport;
	protected ToolItem itemStats; // 统计物料相关数量
	protected Section section;
	
	private int displayCount = 0;
	private static String PREFIX = "workCenterRrn = ";
	private TableListManager tableManager;
	private StructuredViewer viewer;
	private WorkCenter workCenter;

	public InvMaterialSection2() {
		super();
		getAdTableOfInvMaterial();
    }
	
	public void createContent(IManagedForm form, Composite parent) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		section = toolkit.createSection(parent, Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(adTable, "label")));
		section.marginWidth = 3;
		section.marginHeight = 4;
		toolkit.createCompositeSeparator(section);

		createToolBar(section);

		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 0;
		parent.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = false;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);	 

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);

		createSectionContent(client);

		toolkit.paintBordersFor(section);
		section.setClient(client);

	}
	
	protected void createSectionContent(Composite client) {
		FormToolkit toolkit = form.getToolkit();
		tableManager = new TableListManager(adTable);
		viewer = tableManager.createViewer(client, toolkit);
		viewer.setInput(null);
	}

	public void createToolBar(Section section) {
//		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		section.setTextClient(tBar);
	}
	
	protected void getAdTableOfInvMaterial() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = (ADTable)entityManager.getADTable(0, TABLE_NAME);				
		} catch(Exception e) {
			logger.error("InvMaterialSection : getAdTableOfInvMaterial()", e);
		}
	}
	
	public WorkCenter getWorkCenter() {
		return workCenter;
	}
	
	public void setWorkCenter(WorkCenter workCenter) {
		this.workCenter = workCenter;
	}
	
//	public void refresh() {
//		form.getMessageManager().removeAllMessages();
//		List<MaterialSum> mss = new ArrayList<MaterialSum>();
//		viewer.setInput(mss);
//		displayCount = mss.size();
//		tableManager.updateView(viewer);
//	}
	public void setMoLineOnHand(ManufactureOrderLine moLine){
		if(moLine==null){
			viewer.setInput(null);
			return;
		}
		List<ManufactureOrderLine> moLines = new ArrayList<ManufactureOrderLine>();
		try {
			WipManager wipManager = Framework.getService(WipManager.class);
			moLine = wipManager.getMoLineByWorkCenter2Qty(Env.getOrgRrn(),workCenter.getObjectRrn(),null,moLine);
			moLines.add(moLine);
			viewer.setInput(moLines);
		} catch (Exception e) {
			logger.error("InvMaterialSection2 : setMoLineOnHand()", e);
		}
	}
	
}

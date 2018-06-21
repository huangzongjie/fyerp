package com.graly.erp.wip.workcenter.schedule.query;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.forms.MDSashForm;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class WorkShopScheduleQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(WorkShopScheduleQuerySection.class);
	protected SashForm sashForm;
	
	protected IManagedForm form;
	protected ScheduleSection scheduleSection;
	protected NoScheduleSection noScheduleSection;
	
	public WorkShopScheduleQuerySection(IManagedForm form){
		this.form = form;
	}
	public WorkShopScheduleQuerySection(EntityTableManager tableManager) {
		super(tableManager);
//		setWhereClause(" isCompleted  ='N' or  isCompleted  is null");
		setWhereClause(" docStatus <> 'COMPLETED' ");
	}
	
//	public void createToolBar(Section section) {
//		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemSearch(tBar);
//		createToolItemExport(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemRefresh(tBar);
//		section.setTextClient(tBar);
//	}
	
	@Override
	public void createContents(IManagedForm form, Composite parent) {
		FormToolkit toolkit = form.getToolkit();
		sashForm = new MDSashForm(parent, SWT.NULL);
		sashForm.setData("form", form); //$NON-NLS-1$
		toolkit.adapt(sashForm, false, false);
		sashForm.setMenu(parent.getMenu());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		createScheduleSectionContent(form, sashForm);
		createNoScheduleSectionContent(form, sashForm);
		sashForm.setOrientation(SWT.VERTICAL);
//		createToolBarActions(form);
	}
	
	protected void createScheduleSectionContent(IManagedForm form, Composite parent) {
		ADTable wsScheduleTable = getAdTableByName("WorkShopScheduleQuery");
		EntityTableManager tableManager = new EntityTableManager(wsScheduleTable);
		scheduleSection = new ScheduleSection(tableManager,this);
		scheduleSection.setWhereClause(this.getWhereClause());
		scheduleSection.createContents(form, parent);
	}
	
	protected void createNoScheduleSectionContent(IManagedForm form, Composite parent) {
		ADTable wsScheduleTable = getAdTableByName("WorkShopNoSchedule");
		EntityTableManager tableManager = new EntityTableManager(wsScheduleTable);
		noScheduleSection = new NoScheduleSection(tableManager);
//		noScheduleSection.setWhereClause("1<>1");
		noScheduleSection.createContents(form, parent);
	}
	
	protected ADTable getAdTableByName(String tableName) {
		ADTable adTable = null;;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = (ADTable)entityManager.getADTable(0, tableName);
		} catch(Exception e) {
			logger.error("WorkShopScheduleQuerySection : getAdTableByName()", e);
		}
		return adTable;
	}
	public NoScheduleSection getNoScheduleSection() {
		return noScheduleSection;
	}
	public void setNoScheduleSection(NoScheduleSection noScheduleSection) {
		this.noScheduleSection = noScheduleSection;
	}
}

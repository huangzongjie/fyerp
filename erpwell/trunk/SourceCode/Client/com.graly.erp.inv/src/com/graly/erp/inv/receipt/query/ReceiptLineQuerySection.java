package com.graly.erp.inv.receipt.query;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ReceiptLineQuerySection extends EntitySection {
	private static final Logger logger = Logger.getLogger(ReceiptLineQuerySection.class);
	
	protected TableListManager tableManager;
	protected StructuredViewer viewer;
	protected IFormPart spart;
	protected ToolItem inspectQuery;//待检物料查询
	protected ToolItem arrivedQuery;//已到货未入库物料查询
	protected List input;

	public ReceiptLineQuerySection() {
		super();
	}

	public ReceiptLineQuerySection(ADTable table) {
		super(table);
		this.tableManager = new TableListManager(table);
	}
	
	public void createContents(IManagedForm form, Composite parent) {
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
		createSectionDesc(section);
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		final FormToolkit toolkit = form.getToolkit();
		final ADTable table = getADTable();
		
		section = toolkit.createSection(parent, sectionStyle);
		section.setText(I18nUtil.getI18nMessage(table, "label"));
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);

	    createToolBar(section);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    
	    spart = new SectionPart(section);    
	    form.addPart(spart);

	    createSectionTitle(client);
	    
	    createTableViewer(client, toolkit);
	    section.setClient(client);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemInspectQuery(tBar);
		createToolItemArrivedQuery(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	private void createToolItemArrivedQuery(ToolBar bar) {
		arrivedQuery = new ToolItem(bar, SWT.PUSH);
		arrivedQuery.setText("已到货物料查询");
		arrivedQuery.setImage(SWTResourceCache.getImage("search"));
		arrivedQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				arrivedQueryAdapter();
			}
		});
	}

	protected void arrivedQueryAdapter() {
		try {
			INVManager manager = Framework.getService(INVManager.class);
			input = manager.getArrivedReceiptLines(Env.getOrgRrn());
			refresh();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	private void createToolItemInspectQuery(ToolBar bar) {
		inspectQuery = new ToolItem(bar, SWT.PUSH);
		inspectQuery.setText("待检物料查询");
		inspectQuery.setImage(SWTResourceCache.getImage("search"));
		inspectQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				inspectQueryAdapter();
			}
		});
	}

	protected void inspectQueryAdapter() {
		try {
			INVManager manager = Framework.getService(INVManager.class);
			input = manager.getUnInspectedReceiptLines(Env.getOrgRrn());
			refresh();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		viewer = (TableViewer)tableManager.createViewer(client, toolkit);
	}
	
	protected void createSectionDesc(Section section){
		try{ 
			String text = Message.getString("common.totalshow");
			long count = ( input == null ? 0 : input.size() );
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("ReceiptLineQuerySection : createSectionDesc ", e);
		}
	}
	
	@Override
	protected void refreshAdapter() {
		refresh();
	}
	
	@Override
	public void refresh() {
		if(viewer != null){
			viewer.setInput(input);
			tableManager.updateView(viewer);
		}
		createSectionDesc(section);
	}

	public TableListManager getTableManager() {
		return tableManager;
	}

	public void setTableManager(TableListManager tableManager) {
		this.tableManager = tableManager;
	}
	
	protected ADTable getADTable() {
		return getTableManager().getADTable();
	}
}

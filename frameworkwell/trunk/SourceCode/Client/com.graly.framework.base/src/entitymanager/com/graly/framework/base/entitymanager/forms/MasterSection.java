package com.graly.framework.base.entitymanager.forms;

import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;


public class MasterSection implements IRefresh {
	private static final Logger logger = Logger.getLogger(MasterSection.class);

	protected EntityTableManager tableManager;
	protected StructuredViewer viewer;
	private String whereClause;
	protected Section section;
	protected IFormPart spart;
	protected ToolItem itemQuery;
	protected ToolItem itemExport;
	protected ToolItem itemRefresh;
	
	protected EntityQueryDialog queryDialog;
	
	public MasterSection() {}
	
	public MasterSection(EntityTableManager tableManager) {
		this.setTableManager(tableManager);
	}

	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION|Section.TITLE_BAR);
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
//	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));  

	    createSectionTitle(client);
	    
	    createNewViewer(client, form);
	    section.setClient(client);
	    
	    
	}
	
	protected void createNewViewer(Composite client, final IManagedForm form){
		final ADTable table = getTableManager().getADTable();
		viewer = getTableManager().createViewer(client, form.getToolkit());
	    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					try{
						Object obj = Class.forName(table.getModelClass()).newInstance();
						if (obj instanceof ADBase) {
							((ADBase)obj).setOrgRrn(Env.getOrgRrn());
						}
						form.fireSelectionChanged(spart, new StructuredSelection(new Object[] {obj}));
					} catch (Exception e){
						e.printStackTrace();
					}
				} else {
					form.fireSelectionChanged(spart, event.getSelection());
				}
			}
		});
	    String whereClause = this.getWhereClause();
	    String initWhereClause = this.getTableManager().getADTable().getInitWhereClause();
	    if(whereClause == null){
	    	whereClause = " 1=1 ";
	    	setWhereClause(whereClause);
	    }
	    
	    if(initWhereClause != null && initWhereClause.trim().length() > 0){
	    	StringBuffer sb = new StringBuffer(whereClause);
	    	sb.append(" and " + initWhereClause);
	    	setWhereClause(sb.toString());
	    }
	    refresh();
	    createViewAction(viewer);
	}

	protected void createViewAction(StructuredViewer viewer){
		
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemSearch(ToolBar tBar) {
		itemQuery = new ToolItem(tBar, SWT.PUSH);
		itemQuery.setText(Message.getString("common.search_Title"));
		itemQuery.setImage(SWTResourceCache.getImage("search"));
		itemQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryAdapter();
			}
		});
	}
	
	protected void createToolItemExport(ToolBar tBar) {
		itemExport = new ToolItem(tBar, SWT.PUSH);
		itemExport.setText(Message.getString("common.export"));
		itemExport.setImage(SWTResourceCache.getImage("export"));
		itemExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				exportAdapter();
			}
		});
	}
	
	protected void createToolItemRefresh(ToolBar tBar) {
		itemRefresh = new ToolItem(tBar, SWT.PUSH);
		itemRefresh.setText(Message.getString("common.refresh"));
		itemRefresh.setImage(SWTResourceCache.getImage("refresh"));
		itemRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refreshAdapter();
			}
		});
	}
	
	protected void createSectionDesc(Section section){
		try{ 
			String text = Message.getString("common.totalshow");
			ADManager entityManager = Framework.getService(ADManager.class);
			long count = entityManager.getEntityCount(Env.getOrgRrn(), getTableManager().getADTable().getObjectRrn(), getWhereClause());
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("MasterSection : createSectionDesc ", e);
		}
	}
	
	protected void createSectionTitle(Composite client) {
		
	}
	
	public void setTableManager(EntityTableManager tableManager) {
		this.tableManager = tableManager;
	}

	public EntityTableManager getTableManager() {
		return tableManager;
	}
	
	public void refresh(){
		viewer.setInput(new EntityItemInput(getTableManager().getADTable(), getWhereClause(), ""));		
		tableManager.updateView(viewer);
		createSectionDesc(section);
	}
	
	public void refreshAdd(Object object) {
		if (viewer instanceof TableViewer) {
			TableViewer tv = (TableViewer)viewer;
			tv.insert(object, 0);
		} else {
			refresh();
		}
	}
	
	public void refreshUpdate(Object object) {
		if (viewer instanceof TableViewer) {
			TableViewer tv = (TableViewer)viewer;
			tv.update(object, null);
		} else {
			refresh();
		}
	}
	
	public void refreshDelete(Object object) {
		if (viewer instanceof TableViewer) {
			TableViewer tv = (TableViewer)viewer;
			tv.remove(object);
		} else {
			refresh();
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	protected void exportAdapter() {
		try {
			FileDialog dialog = new FileDialog(UI.getActiveShell(), SWT.SAVE);
			dialog.setFilterNames(new String[] { "CSV (*.csv)" });
			dialog.setFilterExtensions(new String[] { "*.csv" }); 
			String fn = dialog.open();
			if (fn != null) {
				Table table = ((TableViewer)viewer).getTable();
				String[][] datas = new String[table.getItemCount() + 1][table.getColumnCount()];
				for (int i = 0; i < table.getColumnCount(); i++) {
					TableColumn column = table.getColumn(i);
					datas[0][i] = column.getText();
				}
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem item = table.getItem(i);
					for (int j = 0; j < table.getColumnCount(); j++) {
						datas[i + 1][j] = item.getText(j);
					}
				}
				
				File file = new File(fn);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				CSVWriter writer = new CSVWriter(new FileWriter(file));
		        for (int i = 0; i < datas.length; i++) {
		            writer.writeNext(datas[i]);
		        }
		        writer.close();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void refreshAdapter() {
		refresh();
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getWhereClause() {
		return whereClause;
	}
	
	protected ADTable getADTable() {
		return this.getTableManager().getADTable();
	}

	public EntityQueryDialog getQueryDialog() {
		return queryDialog;
	}

	public void setQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = queryDialog;
	}

	public StructuredViewer getViewer() {
		return viewer;
	}
	
	protected ADTable getADTableByTableName(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable t = entityManager.getADTable(0L, tableName);
			t = entityManager.getADTableDeep(t.getObjectRrn());
			return t;
		} catch (Exception e) {
			logger.error("MasterSection : getADTableByTableName()", e);
		}
		return null;
	}
}

package com.graly.framework.base.entitymanager.forms;

import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.statushandlers.StatusManager;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.application.Activator;
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
import com.graly.framework.base.ui.ManagerExtensionPoint;

public class EntityBlock extends MasterDetailsBlock implements IRefresh {

	private static final Logger logger = Logger.getLogger(EntityBlock.class);

	protected static int MONITOR_THRESHOLD = 999;
	
	protected EntityTableManager tableManager;
	protected StructuredViewer viewer;
	private String whereClause;
	protected Section section;
	protected IManagedForm form;
	protected IFormPart spart;
	protected ToolItem itemSearch;
	protected ToolItem itemExport;
	protected ToolItem itemRefresh;
	
	protected EntityQueryDialog queryDialog;
	
	public EntityBlock(EntityTableManager tableManager){
		super();
		this.setTableManager(tableManager);
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		this.form = managedForm;
		
		final ADTable table = getTableManager().getADTable();
		
		FormToolkit toolkit = managedForm.getToolkit();
		section = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
		section.setText(I18nUtil.getI18nMessage(table, "label"));
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout layout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(layout);
	    
	    section.setData("entityBlock", this);//将EntityBlock对象作为参数放在section中，供EntityProperties.java类中调用
	    spart = new SectionPart(section);    
	    managedForm.addPart(spart);
	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));  

	    createToolBar(section);
			    
	    createViewer(managedForm, table, client, toolkit);
	    String whereClause = this.getWhereClause();
	    String initWhereClause = this.getTableManager().getADTable().getInitWhereClause();
	    if (whereClause != null && whereClause.trim().length() > 0) {
	    	if (initWhereClause != null && initWhereClause.trim().length() > 0) {
	    		this.setWhereClause(whereClause + " AND " + initWhereClause);
	    	}
	    	refresh();
//	    	this.setWhereClause(whereClause);
	    } else {
	    	this.setWhereClause(initWhereClause);
	 	    refresh();
//	 	    this.setWhereClause(null);
	    }
	   
	    
	    section.setClient(client);
	    createViewAction(viewer);
	}

	protected void createViewer(final IManagedForm managedForm,
			final ADTable table, Composite client, FormToolkit toolkit) {
		viewer = getTableManager().createViewer(client, toolkit);
	    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					try{
						Object obj = Class.forName(table.getModelClass()).newInstance();
						if (obj instanceof ADBase) {
							((ADBase)obj).setOrgRrn(Env.getOrgRrn());
						}
						managedForm.fireSelectionChanged(spart, new StructuredSelection(new Object[] {obj}));
					} catch (Exception e){
						e.printStackTrace();
					}
				} else {
					managedForm.fireSelectionChanged(spart, event.getSelection());
				}
			}
		});
	}
	
	protected void createViewAction(StructuredViewer viewer){
		
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
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
	
	protected void createToolItemSearch(ToolBar tBar) {
		itemSearch = new ToolItem(tBar, SWT.PUSH);
		itemSearch.setText(Message.getString("common.search"));
		itemSearch.setImage(SWTResourceCache.getImage("search"));
		itemSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryAdapter();
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
	
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();    
	    Action haction = new Action("hor", Action.AS_RADIO_BUTTON) {    
	        public void run() {
	          sashForm.setOrientation(SWT.HORIZONTAL);  
	        }    
	    };    
	    
	    haction.setToolTipText(Message.getString("common.horizontal"));    
	    haction.setImageDescriptor(Activator.getImageDescriptor("horizontal"));    
	    Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) {    
	        public void run() {    
	          sashForm.setOrientation(SWT.VERTICAL);    
	        }    
	    };    
	    
	    vaction.setToolTipText(Message.getString("common.vertical"));
	    vaction.setImageDescriptor(Activator.getImageDescriptor("vertical"));
	    form.getToolBarManager().add(haction);
	    form.getToolBarManager().add(vaction);
	    
	    if (getTableManager().getADTable().getIsVertical()) {
			sashForm.setOrientation(SWT.VERTICAL);  
			sashForm.setWeights(new int[]{5, 5});
			vaction.setChecked(true);
			haction.setChecked(false);    
		} else {
			sashForm.setOrientation(SWT.HORIZONTAL);  
			sashForm.setWeights(new int[]{3, 7});
			vaction.setChecked(false);
			haction.setChecked(true);  
		}
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try{
			ADTable table = getTableManager().getADTable();
			Class klass = Class.forName(table.getModelClass());
			if (ManagerExtensionPoint.getManagerRegistry().get(table.getModelClass()) != null){
				EntityProperties page = ManagerExtensionPoint.getManagerRegistry().get(table.getModelClass());
				page.setTable(table);
				page.setMasterParent(this);
				detailsPart.registerPage(klass, page);
			} else {
				detailsPart.registerPage(klass, new EntityProperties(this, table));
			}
		} catch (Exception e){
			logger.error("EntityBlock : registerPages ", e);
		}
	}
	
	protected long createSectionDesc(Section section){
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
			return count;
		} catch (Exception e){
			logger.error("EntityBlock : createSectionDesc ", e);
		}
		return 0;
	}
	
	public void setTableManager(EntityTableManager tableManager) {
		this.tableManager = tableManager;
	}

	public EntityTableManager getTableManager() {
		return tableManager;
	}
	
	public void refresh(){
		try {
			IRunnableWithProgress refreshRunnableWithProgress = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					try {
						monitor.beginTask(Message.getString("common.refresh_view"), 100);
						if (monitor.isCanceled())
							return;
						monitor.worked(10);
						PlatformUI.getWorkbench().getDisplay().readAndDispatch();
						
						monitor.subTask(Message.getString("common.refresh_record_number"));
						
						monitor.worked(10);
						PlatformUI.getWorkbench().getDisplay().readAndDispatch();
						
						monitor.subTask(Message.getString("common.read_data"));
						viewer.setInput(new EntityItemInput(getTableManager().getADTable(), getWhereClause(), ""));		
						monitor.worked(60);
						PlatformUI.getWorkbench().getDisplay().readAndDispatch();
						
						monitor.subTask(Message.getString("common.update_view"));
						tableManager.updateView(viewer);
						monitor.worked(10);
						PlatformUI.getWorkbench().getDisplay().readAndDispatch();
					} finally {
						monitor.done();
					}
				}
			};
			
			long count = createSectionDesc(section);
			if (count > MONITOR_THRESHOLD) {
				PlatformUI.getWorkbench().getProgressService().run(false, false, refreshRunnableWithProgress);
			} else {
				viewer.setInput(new EntityItemInput(getTableManager().getADTable(), getWhereClause(), ""));		
				tableManager.updateView(viewer);
			}
		
		} catch(Exception e) {
			logger.error("Error at Refresh ", e);
		}
		
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

	public StructuredViewer getViewer() {
		return viewer;
	}
	
	public void setSelection(ADBase adBase) {
		if(viewer != null)
			viewer.setSelection(new StructuredSelection(new Object[] {adBase}), true);
	}

}
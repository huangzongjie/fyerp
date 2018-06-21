package com.graly.promisone.base.entitymanager.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADTab;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.entitymanager.adapter.EntityItemInput;
import com.graly.promisone.base.entitymanager.dialog.EntityQueryDialog;
import com.graly.promisone.base.entitymanager.editor.EntityTableManager;
import com.graly.promisone.base.ui.forms.Form;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.util.SWTResourceCache;
import com.graly.promisone.base.ui.util.UI;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.promisone.security.model.ADOrg;

import com.graly.promisone.security.client.SecurityManager;

public abstract class MasterSection {
	private static final Logger logger = Logger.getLogger(MasterSection.class);

	protected EntityTableManager tableManager;
	protected StructuredViewer viewer;
	protected String whereClause;
	protected Section section;
	protected IFormPart spart;
	protected ToolItem queryItem;
	protected ToolItem refreshItem;
	
	public MasterSection(EntityTableManager tableManager) {
		this.setTableManager(tableManager);
	}
	
	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION|Section.TITLE_BAR);
		createSectionDesc(section);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		final FormToolkit toolkit = form.getToolkit();
		final ADTable table = getTableManager().getADTable();
		
		section = toolkit.createSection(parent, sectionStyle);
		section.setText(I18nUtil.getI18nMessage(table, "label"));
		section.marginWidth = 3;
	    section.marginHeight = 5;
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
	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));  
		
		
	    
	    viewer = getTableManager().createViewer(client, toolkit);
	    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					try{
						Object obj = Class.forName(table.getModelClass()).newInstance();
						if (obj instanceof ADBase) {
							((ADBase)obj).setOrgId(Env.getOrgId());
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
	    EntityItemInput input = new EntityItemInput(getTableManager().getADTable(), "1!=1", "");
	    viewer.setInput(input);
	   
	    getTableManager().updateView(viewer);
	    section.setClient(client);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemQuery(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemQuery(ToolBar tBar) {
		queryItem = new ToolItem(tBar, SWT.PUSH);
		queryItem.setText(Message.getString("common.search_Title"));
		queryItem.setImage(SWTResourceCache.getImage("search"));
		queryItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryAdapter();
			}
		});
	}
	
	protected void createToolItemRefresh(ToolBar tBar) {
		refreshItem = new ToolItem(tBar, SWT.PUSH);
		refreshItem.setText(Message.getString("common.refresh"));
		refreshItem.setImage(SWTResourceCache.getImage("refresh"));
		refreshItem.addSelectionListener(new SelectionAdapter() {
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
			long count = entityManager.getEntityCount(Env.getOrgId(), getTableManager().getADTable().getObjectId(), whereClause);
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("EntityBlock : createSectionDesc ", e);
		}
	}
	
	public void setTableManager(EntityTableManager tableManager) {
		this.tableManager = tableManager;
	}

	public EntityTableManager getTableManager() {
		return tableManager;
	}
	
	public void refresh(){
		viewer.setInput(new EntityItemInput(getTableManager().getADTable(), whereClause, ""));		
		tableManager.updateView(viewer);
		createSectionDesc(section);
	}
	
	protected void queryAdapter() {
		EntityQueryDialog queryUser = new EntityQueryDialog(UI.getActiveShell(), tableManager);
		if(queryUser.open() == IDialogConstants.OK_ID) {
			whereClause = queryUser.getKeys();
			refresh();
		}
	}
	
	protected void refreshAdapter() {
		refresh();
	}
}

package com.graly.promisone.base.entitymanager.forms;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.application.Activator;
import com.graly.promisone.base.entitymanager.adapter.EntityItemInput;
import com.graly.promisone.base.entitymanager.dialog.EntityQueryDialog;
import com.graly.promisone.base.entitymanager.editor.EntityTableManager;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.SWTResourceCache;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.base.ui.ManagerExtensionPoint;

public class EntityBlock extends MasterDetailsBlock {

	private static final Logger logger = Logger.getLogger(EntityBlock.class);

	private EntityTableManager tableManager;
	private StructuredViewer viewer;
	private String whereClause;
	private Section section;
	private IManagedForm managedForm;
	private IFormPart spart;
	
	public EntityBlock(EntityTableManager tableManager){
		super();
		this.setTableManager(tableManager);
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		this.managedForm = managedForm;
		
		final ADTable table = getTableManager().getADTable();
		FormToolkit toolkit = managedForm.getToolkit();
		section = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
		section.setText(I18nUtil.getI18nMessage(table, "label"));
		section.marginWidth = 3;
	    section.marginHeight = 5;
	    toolkit.createCompositeSeparator(section);
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout layout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(layout);
	    
	    section.setData("entityBlock", this);//将EntityBlock对象作为参数放在section中，供EntityProperties.java类中调用
	    spart = new SectionPart(section);    
	    managedForm.addPart(spart);
	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));  

	    ToolBar tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		ToolItem titem = new ToolItem(tbar, SWT.PUSH); 
		titem.setText(Message.getString("common.search_Title"));
		titem.setImage(SWTResourceCache.getImage("search"));
		titem.addSelectionListener(searchSelectionListener);
		
		titem = new ToolItem(tbar, SWT.SEPARATOR);
		
		titem = new ToolItem(tbar, SWT.PUSH);
		titem.setText(Message.getString("common.refresh"));
		titem.setImage(SWTResourceCache.getImage("refresh"));
		titem.addSelectionListener(refreshSelectionListener);
		
		section.setTextClient(tbar);
		createSectionDesc(section);
	    
	    viewer = getTableManager().createViewer(client, toolkit);
	    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					try{
						Object obj = Class.forName(table.getModelClass()).newInstance();
						if (obj instanceof ADBase) {
							((ADBase)obj).setOrgId(Env.getOrgId());
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
	    EntityItemInput input = new EntityItemInput(getTableManager().getADTable(), whereClause, "");
	    viewer.setInput(input);
	   
	    getTableManager().updateView(viewer);
	    section.setClient(client);
	}
	
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();    
	    Action haction = new Action("hor", Action.AS_RADIO_BUTTON) {    
	        public void run() {
	          sashForm.setOrientation(SWT.HORIZONTAL);  
	          //form.reflow(true);    
	        }    
	    };    
	    haction.setChecked(true);    
	    haction.setToolTipText(Message.getString("common.horizontal"));    
	    haction.setImageDescriptor(Activator.getImageDescriptor("horizontal"));    
	        
	    Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) {    
	        public void run() {    
	          sashForm.setOrientation(SWT.VERTICAL);    
	          //form.reflow(true);    
	        }    
	    };    
	    vaction.setChecked(false);
	    vaction.setToolTipText(Message.getString("common.vertical"));
	    vaction.setImageDescriptor(Activator.getImageDescriptor("vertical"));
	    form.getToolBarManager().add(haction);
	    form.getToolBarManager().add(vaction);
	    
    	sashForm.setWeights(new int[]{3, 7});
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
	
	protected SelectionListener searchSelectionListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e){
			EntityQueryDialog queryUser = new EntityQueryDialog(e.widget.getDisplay().getActiveShell(),
					tableManager);
			if(queryUser.open() == IDialogConstants.OK_ID) {
				whereClause = queryUser.getKeys();
				refresh();
			}
		}
	};
	
	protected SelectionListener refreshSelectionListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e){
			refresh();
		}
	};
}
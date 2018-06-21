package com.graly.erp.pur.request.refmo;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
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

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class RefMoSection {
	private TableListManager tableManager;
	private ADTable adTable;
	private RefMoDialog parentDialog;
	private Object input;
	
	protected StructuredViewer viewer;
	protected Section section;
	protected IFormPart spart;
	protected ToolItem itemDetail;
	private ManufactureOrder selectedMo;
	
	public RefMoSection(ADTable adTable, RefMoDialog parentDialog) {
		this.adTable = adTable;
		this.parentDialog = parentDialog;
	}
	
	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
		initTableContent();
		createSectionDesc(section);
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		final FormToolkit toolkit = form.getToolkit();
		tableManager = new TableListManager(adTable);
		
		section = toolkit.createSection(parent, sectionStyle);
		section.setText(I18nUtil.getI18nMessage(adTable, "label"));
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
	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(adTable, "label")));  
		
	    viewer = tableManager.createViewer(client, toolkit);
	    section.setClient(client);
	    createViewAction(viewer);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemDetail(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemDetail(ToolBar bar) {
		itemDetail = new ToolItem(bar, SWT.PUSH);
		itemDetail.setText(Message.getString("inv.seedetials"));
		itemDetail.setImage(SWTResourceCache.getImage("preview"));
		itemDetail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				moDetailAdapter();
			}
		});
	}
	
	protected void moDetailAdapter() {
		try {
			if(selectedMo != null && selectedMo.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedMo = (ManufactureOrder)adManager.getEntity(selectedMo);
				MoViewContext context = new MoViewContext();
				context.setCategory(MoViewContext.CAGEGORY_VIEW_MO);
				context.setManufactureOrder(selectedMo);
				
				MoViewWizard wizard = new MoViewWizard(context);
				wizard.setCanEdit(false);
				MoViewDialog mgd = new MoViewDialog(UI.getActiveShell(), wizard);
				context.setDialog(mgd);
				if(mgd.open() == Dialog.OK) {
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}	
	}
	
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void setSelectionRequisition(Object obj) {
		if (obj instanceof ManufactureOrder) {
			selectedMo = (ManufactureOrder) obj;
		} else {
			selectedMo = null;
		}
	}
	
	protected void initTableContent() {
		setInput(parentDialog.getRefMoList());
		refresh();
	}
	
	public void refresh(){
		viewer.setInput(input);
		tableManager.updateView(viewer);
	}
	
	protected void createSectionDesc(Section section){
		try{
			String text = Message.getString("common.totalshow");
			int length = parentDialog.getRefMoList().size();
			if (length > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(length), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(length), String.valueOf(length));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	public void setInput(Object input) {
		this.input = input;
	}
}
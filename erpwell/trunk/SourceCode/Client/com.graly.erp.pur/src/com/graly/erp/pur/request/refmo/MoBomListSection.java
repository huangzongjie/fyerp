package com.graly.erp.pur.request.refmo;

import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class MoBomListSection {
//	private static final Logger logger = Logger.getLogger(MoBomListSection.class);
	private MoBomTreeManager treeManager;	
	private MoBomListPage parentPage;
	private Object input;
	private boolean canEdit;
	
	protected StructuredViewer viewer;
	protected Section section;
	protected IFormPart spart;
	protected ADTable adTableDoc;
	
	public MoBomListSection(MoBomTreeManager treeManager, MoBomListPage parentPage) {
		this.treeManager = treeManager;
		this.parentPage = parentPage;
		this.canEdit = ((MoViewWizard)parentPage.getWizard()).isCanEdit();
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemReferenceDoc(tBar);
		section.setTextClient(tBar);
	}
	
	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION|Section.TITLE_BAR);
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		final FormToolkit toolkit = form.getToolkit();
		final ADTable table = treeManager.getADTable();
		
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
	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));  
		
	    viewer = treeManager.createViewer(client, toolkit);
	    section.setClient(client);
	}
	
	public void refresh(){
		viewer.setInput(input);
		((TreeViewer)viewer).expandAll();
	}
	
	protected void refreshAdapter() {
		refresh();
	}
	
	public List<ManufactureOrderBom> getMOBoms() {
		return (List<ManufactureOrderBom>)viewer.getInput();
	}

	public void setInput(Object input) {
		this.input = input;
	}
	
	public boolean isCanEdit() {
		return canEdit;
	}
}

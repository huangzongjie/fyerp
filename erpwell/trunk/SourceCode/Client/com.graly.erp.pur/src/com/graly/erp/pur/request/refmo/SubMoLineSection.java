package com.graly.erp.pur.request.refmo;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class SubMoLineSection {
//	private static final Logger logger = Logger.getLogger(SubMoLineSection.class);
	public static Color color = SWTResourceCache.getColor("Function");
	private SubMoLinePage parentPage;
	private MoTreeManager treeManager;
	private TreeViewer viewer;
	private ADTable adTable;
	
	private final int oneRowHeight = 24;
	private Tree tree;
	
	protected Section section;
	protected IManagedForm form;
	protected ToolItem relationItem;
	protected Menu relationMenu;
	
	private boolean canEdit;

	public SubMoLineSection() {}
	
	public SubMoLineSection(ADTable adTable, SubMoLinePage parentPage) {
		this.adTable = adTable;
		this.parentPage = parentPage;
		this.canEdit = ((MoViewWizard)parentPage.getWizard()).isCanEdit();
	}
	
	public void createContents(IManagedForm form, Composite parent) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();		
		section = toolkit.createSection(parent, SWT.FLAT | SWT.HORIZONTAL);
		section.setText(Message.getString("pdm.bom_list_detail_info"));
	    toolkit.createCompositeSeparator(section);
	    createToolBar(section);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    createSectionContents(form, client, toolkit);
	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(adTable, "label")));  

	    section.setClient(client);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemRelation(tBar);
		section.setTextClient(tBar);
	}
		
	protected void createSectionContents(IManagedForm form, Composite parent, FormToolkit toolkit){
		SashForm sf = new SashForm(parent, SWT.HORIZONTAL);
		sf.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite leftTree = toolkit.createComposite(sf, SWT.NULL);
		GridLayout gl = new GridLayout(1, false);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		leftTree.setLayout(gl);
		leftTree.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createMOTreeContent(leftTree, form.getToolkit());
	}
	
	protected void createMOTreeContent(Composite left, FormToolkit toolkit) {
		treeManager = new MoTreeManager(adTable, this);
		treeManager.setParentSection(this);
		//SWT.LINE_DASHDOTDOT | 
		tree = new Tree(left,
        		SWT.FULL_SELECTION | SWT.BORDER |SWT.H_SCROLL | SWT.V_SCROLL);
		viewer = (TreeViewer)treeManager.createViewer(tree,	toolkit);
		tree.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = oneRowHeight;
			}
		});
	}
	
	public void refreshTreeContent(List<DocumentationLine> masterLines) {
		if(masterLines != null) {
			viewer.setInput(masterLines);
			viewer.expandAll();
		}
	}
	
	public SubMoLinePage getParentPage() {
		return parentPage;
	}
	
	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
}

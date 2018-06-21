package com.graly.mes.prd.designer;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.graly.mes.prd.designer.common.editor.ContentProvider;
import com.graly.mes.prd.designer.common.editor.CreationFactory;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.model.SemanticElementFactory;
import com.graly.mes.prd.designer.common.notation.NotationElementFactory;
import com.graly.mes.prd.designer.common.notation.RootContainer;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;
import com.graly.mes.prd.designer.common.xml.XmlAdapterFactory;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;

public abstract class AbstractGraphForm {
	
	private FormToolkit toolkit;
	private Composite parent;
	
	private AbstractGraphicalViewer graphicalViewer;
	private FlowEditDomain editDomain;
	private SemanticElementFactory semanticElementFactory;
	private NotationElementFactory notationElementFactory;
	private RootContainer rootContainer;
	private ContentProvider contentProvider;
	private DocumentProvider documentProvider;
	private XmlAdapter xmlAdapter;
	private IEditorInput input;
	
	public AbstractGraphForm(FormToolkit toolkit, Composite parent, IEditorInput input) {
		this.toolkit = toolkit;
		this.parent = parent;
		this.input = input;
		initEditDomain();
		initSource();
		initGraph();
		createForm();
	}	
	
	private void initEditDomain() {
		editDomain = new FlowEditDomain();
	}
	
	protected void initGraph() {
		getContentProvider().addNotationInfo(getRootContainer(), getEditorInput());
	}
	
	protected void initSource() {
		SemanticElement semanticElement = getSemanticElement();
		CreationFactory factory = new CreationFactory(semanticElement.getElementId(), getSemanticElementFactory(), getNotationElementFactory());
		setRootContainer((RootContainer)factory.getNewObject());
		getRootContainer().setSemanticElement(semanticElement);
		semanticElement.addPropertyChangeListener(getRootContainer());
	}
	
	private SemanticElement getSemanticElement() {
		Document doc = getDocumentProvider().getDocument(getEditorInput());
		Node node = doc.getDocumentElement();
		String ss = ((org.eclipse.wst.xml.core.internal.document.DocumentImpl)doc).getModel().getStructuredDocument().getText();
		//node.getTextContent()
		XmlAdapterFactory factory = new XmlAdapterFactory(node.getOwnerDocument(), getSemanticElementFactory());
		xmlAdapter = factory.adapt(node);
		SemanticElement semanticElement = createMainElement();
		xmlAdapter.initialize(semanticElement);
		return semanticElement;
	}
	
	private void createForm(){
        Section section = toolkit.createSection(parent, Section.TITLE_BAR);
        section.setText(createTitle());
	    section.clientVerticalSpacing = 0;
	    section.descriptionVerticalSpacing = -3;
	    toolkit.createCompositeSeparator(section);
	    
	    createToolBar(section);
	    
	    Composite client = toolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();    
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginLeft = -5;
		gridLayout.marginRight = -5;
		client.setLayout(gridLayout);		
		createToolBar(section);
		
		SashForm sashForm = new SashForm(client, SWT.HORIZONTAL | SWT.BORDER);
		addPalette(sashForm);
		addModelViewer(sashForm);
		sashForm.setWeights(new int[] {15, 85});
		GridData gd = new GridData(GridData.FILL_BOTH);
		sashForm.setLayoutData(gd);
		sashForm.setFocus();
		section.setLayoutData(gd);
		toolkit.paintBordersFor(section);
		section.setClient(client); 
	}
	
	private void createToolBar(Section section){
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		
		ToolItem itemSave = new ToolItem(tBar, SWT.PUSH);
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(getSaveListener());	
		ToolItem itemCancel = new ToolItem(tBar, SWT.PUSH);
		itemCancel.setText(Message.getString("common.cancel"));
		itemCancel.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemCancel.addSelectionListener(getCancelListener());
		section.setTextClient(tBar);
	}

	private void addModelViewer(Composite composite) {
		graphicalViewer = createGraphicalViewer();
		graphicalViewer.initControl(composite);
	}
	
	private void addPalette(Composite composite) {	
		PaletteViewer paletteViewer = new PaletteViewer();
		paletteViewer.createControl(composite);
		editDomain.setPaletteViewer(paletteViewer);
		editDomain.setPaletteRoot(new PaletteRoot(this));
	}
	
	public SemanticElementFactory getSemanticElementFactory() {
		if (semanticElementFactory == null) {
			semanticElementFactory = new SemanticElementFactory(getContributorId());
		}
		return semanticElementFactory;
	}
	
	public NotationElementFactory getNotationElementFactory() {
		if (notationElementFactory == null) {
			notationElementFactory = new NotationElementFactory();
		}
		return notationElementFactory;
	}
	
	public EditDomain getEditDomain() {
		return editDomain;
	}
	
	public AbstractGraphicalViewer getDesignerModelViewer() {
		return graphicalViewer;
	}
	
	public ContentProvider getContentProvider() {
		if (contentProvider == null) {
			contentProvider = createContentProvider();
		}
		return contentProvider;
	}
	
	public DocumentProvider getDocumentProvider() {
		if (documentProvider == null) {
			documentProvider = createDocumentProvider();
		}
		return documentProvider;
	}
	
	public RootContainer getRootContainer() {
		return rootContainer;
	}
	
	public void setRootContainer(RootContainer rootContainer) {
		this.rootContainer = rootContainer;
	}
	
    public IEditorInput getEditorInput() {
        return input;
    }
	
	public boolean doSave() {
		boolean saved = getContentProvider().saveToInput(getEditorInput(), getRootContainer());
		if (saved) {
			return getDocumentProvider().saveToInput(getEditorInput(), xmlAdapter);
		} else {
			return saved;
		}
	}
	
	public void dispose() {
	}
	
	protected abstract String createTitle();
	protected abstract ContentProvider createContentProvider();
	protected abstract DocumentProvider createDocumentProvider();
	protected abstract SemanticElement createMainElement();
	protected abstract AbstractGraphicalViewer createGraphicalViewer();
	protected abstract String getContributorId();
	protected abstract SelectionListener getSaveListener();
	protected abstract SelectionListener getCancelListener();
	
}

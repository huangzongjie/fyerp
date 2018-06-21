package com.graly.erp.pdm.bomhistory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.erp.base.model.Material;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomConTreeDialog extends BomHisTreeDialog {

	protected Long version;
	protected Long newVersion;
	protected BomHisTreeForm bomHisTreeForm;
	protected Material material;
	protected BomHisTreeForm bomConTreeForm;
	public BomConTreeDialog(Shell parent, IManagedForm form, Material material,
			boolean editable, Long version,Long newVersion) {
		super(parent, form, material, editable, version);
		this.version=version;
		this.material=material;
		this.newVersion=newVersion;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)createBaseDialogArea(parent);
        setTitleImage(SWTResourceCache.getImage("bomtitle"));
        setTitle(Message.getString("pdm.bom_list"));
        
        FormToolkit toolkit = form.getToolkit();
        Composite content = toolkit.createComposite(composite, SWT.NULL);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        content.setLayout(new GridLayout(1, false));
        

		section = toolkit.createSection(content, Section.TITLE_BAR);
		section.setText(Message.getString("pdm.bom_list_detail_info"));
		section.marginWidth = 2;
		section.marginHeight = 2;
		toolkit.createCompositeSeparator(section);
		createToolBar(section);
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 2;
		layout.leftMargin = 2;
		layout.rightMargin = 2;
		layout.bottomMargin = 2;
		content.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = true;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section, SWT.NULL);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);
		GridData g = new GridData(GridData.FILL_BOTH);
		client.setLayoutData(g);

		createSectionContent(client);
		
		toolkit.paintBordersFor(section);
		section.setClient(client);
		createViewAction(bomHisTreeForm.getViewer());
		createViewAction(bomConTreeForm.getViewer());
        return composite;
	}
	
	protected Control createBaseDialogArea(Composite parent) {
		// create the top level composite for the dialog area
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setFont(parent.getFont());
		// Build the separator line
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL
				| SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}
	
	protected void CreateB(Composite parent){
		
	}
	@Override
	protected void createSectionContent(Composite section) {
		final IMessageManager mmng = form.getMessageManager();
		GridLayout gl = new GridLayout(2, false);
		section.setLayout(gl);
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		section.setLayoutData(gd);
		toolkit.paintBordersFor(section);
		
		bomHisTreeForm = new BomHisTreeForm(section, SWT.NULL, material, mmng, this, version);
		bomHisTreeForm.setLayoutData(gd);
		
		bomConTreeForm = new BomHisTreeForm(section, SWT.NULL, material, mmng, this, newVersion);
		bomConTreeForm.setLayoutData(gd);
	}
	@Override
	public void createToolBar(Section section) {
		
	}

}

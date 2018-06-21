package com.graly.erp.pdm.bomtype;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.model.VBomType;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomTypeDialog extends InClosableTitleAreaDialog implements IRefresh {
	private static final Logger logger = Logger.getLogger(BomTypeDialog.class);
	protected static final String TABLE_NAME = "VPDMBomType";
	private static int MIN_DIALOG_WIDTH = 500;
	private static int MIN_DIALOG_HEIGHT = 350;
	protected ToolItem itemSearch;
	protected Section section;
	
//	protected Bom object;
	protected EntityQueryDialog queryDialog;
	protected Material material, selectedMaterial;
	protected ADTable adTable;
	protected BomTypeForm bomTypeForm;
	protected IManagedForm form;

	public BomTypeDialog(Shell parent) {
        super(parent);
    }
	
	public BomTypeDialog(Shell parent, Material material,
			IManagedForm managedForm){
		this(parent);
		this.material = material;
		this.form = managedForm;
		initAdTableOfBom();
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
        setTitleImage(SWTResourceCache.getImage("bomtitle"));
        try{
			String editorTitle = String.format(Message.getString("common.editor"),
					I18nUtil.getI18nMessage(adTable, "label"));
			setTitle(editorTitle);
		} catch (Exception e){
		}
		FormToolkit toolkit = form.getToolkit();
		Composite content = toolkit.createComposite(composite, SWT.NULL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        content.setLayoutData(gd);
        content.setLayout(new GridLayout(1, true));
		
        createSectionContent(content, toolkit);
        return composite;
	}
	
	protected void createSectionContent(Composite parent, FormToolkit toolkit) {
		section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(String.format(Message.getString("common.detail"), I18nUtil.getI18nMessage(adTable, "label")));
		section.marginWidth = 3;
		section.marginHeight = 4;
		toolkit.createCompositeSeparator(section);
		createToolBar(section);
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 2;
		layout.rightMargin = 2;
		layout.bottomMargin = 0;
		parent.setLayout(layout);
		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = false;
		section.setLayoutData(td);
		Composite client = toolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);

		bomTypeForm = new BomTypeForm(client, SWT.NULL, adTable, material,this);
        bomTypeForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.paintBordersFor(section);
		section.setClient(client);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		section.setTextClient(tBar);
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
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(),
					new EntityTableManager(adTable), this);
			queryDialog.open();
		}
	}
	
	protected void initAdTableOfBom() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, TABLE_NAME);
		} catch(Exception e) {
			logger.error("OptionalDialog : initAdTableOfBom()", e);
		}
	}

	@Override
    protected void okPressed() {
		try {
			setErrorMessage(null);
			if(selectedMaterial == null) {
				setErrorMessage(Message.getString("pdm.must_select_bom_type"));
				return;
			}
		} catch(Exception e) {
			logger.error("Error at BomTypeDialog : okPressed" + e);
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
        super.okPressed();
    }
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			try {
				createMaterial();
			} catch(Exception e) {
				logger.error("Error at BomTypeDialog : okPressed" + e);
				return;
			}
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	
	public void createMaterial() {
		if(bomTypeForm.getCurrentBomType() != null) {
			VBomType bt = bomTypeForm.getCurrentBomType();
			Material mr = new Material();
			
			mr.setObjectRrn(bt.getObjectRrn());
			mr.setMaterialId(bt.getMaterialId());
			
			this.selectedMaterial = mr;
		}
	}

	public Material getSelectedMaterial() {
		return selectedMaterial;
	}

	public void setSelectedMaterial(Material selectedMaterial) {
		this.selectedMaterial = selectedMaterial;
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		Message.getString("common.ok"), false);
        createButton(parent, IDialogConstants.CANCEL_ID,
        		Message.getString("common.cancel"), false);
    }

	@Override
	public void refresh() {
		bomTypeForm.refresh();
	}

	@Override
	public void setWhereClause(String whereClause) {
		bomTypeForm.setWhereClause(whereClause);
	}

	@Override
	public String getWhereClause() {
		return bomTypeForm.getWhereClause();
	}
}

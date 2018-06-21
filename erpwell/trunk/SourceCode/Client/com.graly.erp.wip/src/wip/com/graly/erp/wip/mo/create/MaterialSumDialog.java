package com.graly.erp.wip.mo.create;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.client.WipManager;

public class MaterialSumDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(MaterialSumDialog.class);
	private static final String TABLE_NAME = "PASMaterialSum";
	private static int MIN_DIALOG_WIDTH = 500;
	private static int MIN_DIALOG_HEIGHT = 300;
	
	protected IManagedForm form;
	protected ADTable adTable;
	private DocumentationLine selectedLine;
	private TableListManager tableManager;
	private StructuredViewer viewer;
	
	public MaterialSumDialog(Shell parent, DocumentationLine selectedLine){
		super(parent);
		this.selectedLine = selectedLine;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
        setTitleImage(SWTResourceCache.getImage("bomtitle"));
        initAdTableOfAlternate();
		String editorTitle = String.format(Message.getString("common.detail"),
				I18nUtil.getI18nMessage(adTable, "label"));
		setTitle(editorTitle);
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		form = new ManagedForm(toolkit, sForm);
		configureBody(body);
		
		Composite content = toolkit.createComposite(body, SWT.NULL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        content.setLayoutData(gd);
        content.setLayout(new GridLayout(1, true));
        createContent(content);
        
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return composite;
	}
	
	protected void createContent(Composite parent) {
		try {
			tableManager = new TableListManager(adTable);
			viewer = tableManager.createViewer(parent, form.getToolkit());
			
			WipManager wipManager = Framework.getService(WipManager.class);
			MaterialSum materialSum = wipManager.getMaterialSum(Env.getOrgRrn(), selectedLine.getMaterialRrn(), false, true);
			if(materialSum != null) {
				List<MaterialSum> input = new ArrayList<MaterialSum>();
				input.add(materialSum);
				viewer.setInput(input);
				tableManager.updateView(viewer);
			}
		} catch(Exception e) {
			logger.error("Error at MaterialSumDialog : createContent() ", e);
		}
	}
	
	protected void initAdTableOfAlternate() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
			}
		} catch(Exception e) {
			logger.error("MOAlternateDialog : initAdTableOfAlternate()", e);
		}
	}

	@Override
    protected void okPressed() {
        super.okPressed();
    }
	
	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
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
        createButton(parent, IDialogConstants.CANCEL_ID,
                Message.getString("common.exit"), false);
    }
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

}

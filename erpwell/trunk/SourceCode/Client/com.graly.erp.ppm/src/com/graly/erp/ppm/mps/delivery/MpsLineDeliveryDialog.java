package com.graly.erp.ppm.mps.delivery;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.ppm.model.VMpsLineDelivery;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;

public class MpsLineDeliveryDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(MpsLineDeliveryDialog.class);
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 450;
	private static String TABLE_NAME = "PPMMpsLineDelivery";
	
	protected ADTable table;
	protected ManagedForm managedForm;
	protected MpsLineDeliverySection prListSection;
	protected VMpsLineDelivery vmpsLineDelivery;

	public MpsLineDeliveryDialog(Shell parent, VMpsLineDelivery vmpsLineDelivery) {
        super(parent);
        this.vmpsLineDelivery = vmpsLineDelivery;
    }
	
	public MpsLineDeliveryDialog(Shell parent, ADTable table,VMpsLineDelivery vmpsLineDelivery){
		this(parent, vmpsLineDelivery);
		this.table = table;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
        getADTableOfPOLine();
        String dialogTitle = String.format(Message.getString("common.editor"),
				I18nUtil.getI18nMessage(table, "label"));
		setTitle(dialogTitle);
        Composite composite = (Composite) super.createDialogArea(parent);
        
        createFormContent(composite);
        return composite;
    }
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout();
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
	
	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);
		
		Composite body = sForm.getForm().getBody();
		configureBody(body);

		prListSection = new MpsLineDeliverySection(new EntityTableManager(table), vmpsLineDelivery, managedForm);
		String whereClause = " mpsId = '"+vmpsLineDelivery.getMpsId()+"' AND materialId = '"+vmpsLineDelivery.getMaterialId()
		+"' AND docStatus = 'APPROVED'";
		
		prListSection.setWhereClause(whereClause);
		prListSection.setParentDilog(this);
		prListSection.createContents(managedForm, body);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}
	
	protected ADTable getADTableOfPOLine() {
		try {
			if(table == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				table = entityManager.getADTable(0L, TABLE_NAME);
			}
			return table;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID,
               Message.getString("common.exit"), false);
    }
	
	protected String validate() {
		return null;
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
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

}

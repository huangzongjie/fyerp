package com.graly.erp.inv.iqc.createfrom;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.wizard.FlowWizard;
import com.graly.framework.base.ui.wizard.FlowWizardDialog;

public class CreateIqcDialog extends FlowWizardDialog implements IRefresh {
	private static final Logger logger = Logger.getLogger(CreateIqcDialog.class);
	
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 400;
	protected EntityQueryDialog queryDialog;
	protected TableListManager listTableManager;
	/*
	 * PAGE_NAME_*的值对应于plugin.xml中配置的com.graly.framework.base.wizard扩展点下的值
	 */
	private static final String PAGE_NAME_RECEIPT = "receiptSelect";
	protected String whereClause;

	public CreateIqcDialog(Shell parentShell, FlowWizard newWizard, TableListManager listTableManager) {
		super(parentShell, newWizard);
		this.listTableManager = listTableManager;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Label titleBarSeparator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite queryComp = new Composite(parent, SWT.NULL);
		queryComp.setLayout(new GridLayout());
		GridData gd_0 = new GridData();
		gd_0.horizontalAlignment = GridData.END;
		queryComp.setLayoutData(gd_0);
		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		ScrolledForm sForm = toolkit.createScrolledForm(parent);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		ManagedForm managedForm = new ManagedForm(toolkit, sForm);
		if(wizard instanceof IqcCreateWizard){
			((IqcCreateWizard)wizard).getContext().setMangedForm(managedForm);
		}

		// Build the Page container
		pageContainer = createPageContainer(body, toolkit);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = -1;
		gd.heightHint = -1;
		pageContainer.setLayoutData(gd);
		pageContainer.setFont(parent.getFont());
		// Insert a progress monitor
		GridLayout pmlayout = new GridLayout();
		pmlayout.numColumns = 1;
		progressMonitorPart = createProgressMonitorPart(body, pmlayout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		progressMonitorPart.setLayoutData(gridData);
		progressMonitorPart.setVisible(false);
		// Build the separator line
		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		applyDialogFont(progressMonitorPart);
		return body;
	}
	
	private Composite createPageContainer(Composite parent, FormToolkit toolkit) {
		Composite result = toolkit.createComposite(parent, SWT.BORDER);
		result.setLayout(new PageContainerFillLayout(5, 5, 1000, 400));
		return result;
	}
	
	public void updateButtons() {
		boolean canFlipToNextPage = false;
		if (backButton != null) {
			boolean canFlipBack = getCurrentPage().getPreviousPage() != null;
			backButton.setEnabled(canFlipBack);
		}
		if (nextButton != null) {
			canFlipToNextPage = this.getCurrentPage().canFlipToNextPage();
			nextButton.setEnabled(canFlipToNextPage);
		}
	}
	
	@Override
	public String getWhereClause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWhereClause(String whereClause) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
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

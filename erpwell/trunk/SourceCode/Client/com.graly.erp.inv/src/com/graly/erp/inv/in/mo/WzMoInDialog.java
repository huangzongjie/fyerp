package com.graly.erp.inv.in.mo;

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

import com.graly.framework.base.ui.wizard.FlowWizard;
import com.graly.framework.base.ui.wizard.FlowWizardDialog;

public class WzMoInDialog extends FlowWizardDialog {
	protected static int MIN_DIALOG_WIDTH = 720;
	protected static int MIN_DIALOG_HEIGHT = 400;

	public WzMoInDialog(Shell parentShell, FlowWizard newWizard) {
		super(parentShell, newWizard);
	}

	protected Control createDialogArea(Composite parent) {
		Label titleBarSeparator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		ScrolledForm sForm = toolkit.createScrolledForm(parent);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		ManagedForm managedForm = new ManagedForm(toolkit, sForm);
		((WzMoInWizard)wizard).getContext().setManagedForm(managedForm);

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
		Composite result = toolkit.createComposite(parent, SWT.NULL);
		result.setLayout(new PageContainerFillLayout(0, 0, 300, 225));
		return result;
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
	
	/*
	 * ȡ��������Ĭ�ϵķ���Next Button��
	 */
	public void updateButtons() {
		boolean canFlipToNextPage = false;
		if (backButton != null) {
			backButton.setEnabled(getCurrentPage().getPreviousPage() != null);
		}
		if (nextButton != null) {
			canFlipToNextPage = getCurrentPage().canFlipToNextPage();
			nextButton.setEnabled(canFlipToNextPage);
		}
	}
	
	protected Control createButtonBar(Composite parent) {
		Composite bar = new Composite(parent, SWT.BORDER);
		setGridLayout(bar, 1);
		bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite temp = new Composite(bar, SWT.NONE);
		setGridLayout(temp, 1);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 0;
		gd.horizontalSpan = 2;
		temp.setLayoutData(gd);
			
		Composite aqComp = new Composite(bar, SWT.NONE);
		GridLayout layout = new GridLayout(0, false);
		layout.makeColumnsEqualWidth = true;
		aqComp.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER
				| GridData.VERTICAL_ALIGN_CENTER);
		aqComp.setLayoutData(data);
		aqComp.setFont(parent.getFont());
		createButtonsForButtonBar(aqComp);

		return bar;
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	private void setGridLayout(Composite content, int num) {
		GridLayout gl = new GridLayout(num, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		content.setLayout(gl);
	}
}

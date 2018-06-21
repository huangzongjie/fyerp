package com.graly.erp.bj.inv.in.createfrom.po;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.wizard.FlowWizard;
import com.graly.framework.base.ui.wizard.FlowWizardDialog;
import com.graly.framework.runtime.Framework;


/**
 * 如果要在其他地方使用CreateDialog需要修改refresh()方法
 *
 */
public class BJCreateDialog extends FlowWizardDialog implements IRefresh{
	private static final Logger LOG = Logger.getLogger(BJCreateDialog.class);
	
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 400;
	protected EntityQueryDialog queryDialog;
	protected TableListManager listTableManager;
	/*
	 * PAGE_NAME_*的值对应于plugin.xml中配置的com.graly.framework.base.wizard扩展点下的值
	 */
	private static final String PAGE_NAME_IQC = "iqcSelect";
	private static final String PAGE_NAME_PO = "poSelect";
	protected String whereClause;

	public BJCreateDialog(Shell parentShell, FlowWizard newWizard, TableListManager listTableManager) {
		super(parentShell, newWizard);
		this.listTableManager = listTableManager;
	}
	
	protected Control createDialogArea(Composite parent) {
		Label titleBarSeparator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite queryComp = new Composite(parent, SWT.NULL);
		queryComp.setLayout(new GridLayout());
		GridData gd_0 = new GridData();
		gd_0.horizontalAlignment = GridData.END;
		queryComp.setLayoutData(gd_0);
		
		ToolBar tBar = new ToolBar(queryComp, SWT.FLAT | SWT.HORIZONTAL);
		ToolItem itemQuery = new ToolItem(tBar, SWT.PUSH);
		itemQuery.setText(Message.getString("common.search_Title"));
		itemQuery.setImage(SWTResourceCache.getImage("search"));
		itemQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryAdapter();
			}
		});
		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		ScrolledForm sForm = toolkit.createScrolledForm(parent);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		ManagedForm managedForm = new ManagedForm(toolkit, sForm);
		if(wizard instanceof BJPoCreateWizard){
			((BJPoCreateWizard)wizard).getContext().setMangedForm(managedForm);
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
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			EntityTableManager tableManager = new EntityTableManager(listTableManager.getADTable());
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}

	private Composite createPageContainer(Composite parent, FormToolkit toolkit) {
		Composite result = toolkit.createComposite(parent, SWT.BORDER);
		result.setLayout(new PageContainerFillLayout(5, 5, 300, 225));
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

	public void updateButtons() {
		boolean canFlipToNextPage = false;
		if (backButton != null) {
			backButton.setEnabled(getCurrentPage().getPreviousPage() != null);
		}
		if (nextButton != null) {
			canFlipToNextPage = this.getCurrentPage().canFlipToNextPage();
			nextButton.setEnabled(canFlipToNextPage);
		}
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
	public String getWhereClause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		TableViewer tableViewer = null;
		if(wizard instanceof BJPoCreateWizard){
			BJPoSelectPage page = (BJPoSelectPage)wizard.getPage(PAGE_NAME_PO);
			BJPoSelectSection section = page.getSection();
			tableViewer = section.getViewer();
		}

		List<ADBase> l = new ArrayList<ADBase>();
		try {
        	ADManager manager = Framework.getService(ADManager.class);
        	long objectId = listTableManager.getADTable().getObjectRrn();
            l = manager.getEntityList(Env.getOrgRrn(), objectId, 
            		Env.getMaxResult(), whereClause, "");
        } catch (Exception e) {
        	LOG.error("Error SingleQueryDialog : refresh() " + e.getMessage(), e);
        }
		
		tableViewer.setInput(l);			
		listTableManager.updateView(tableViewer);
	
	}

	@Override
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	public void setButtonEnabled(int buttonId, boolean enabled) {
		this.getButton(buttonId).setEnabled(enabled);
	}

}

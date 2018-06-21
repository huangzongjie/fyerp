package com.graly.erp.inv.materialtrace;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.inv.client.INVManager;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MaterialTraceDetailDialog extends InClosableTitleAreaDialog {

	protected TableViewerManager	tableManager;
	protected StructuredViewer		viewer;
	protected ManagedForm			managedForm;
	private String					detailType;
	private long 					materialRrn;

	public MaterialTraceDetailDialog(Shell parentShell, String detailType, TableViewerManager tableManager, long materialRrn) {
		super(parentShell);
		this.materialRrn = materialRrn;
		this.detailType = detailType;
		this.tableManager = tableManager;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		String dialogTitle = "物料去向详细信息";
		setTitle(dialogTitle);
		Composite composite = (Composite) super.createDialogArea(parent);
		createForm(composite);
		return composite;
	}

	private void createForm(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);

		Composite body = sForm.getForm().getBody();
		configureBody(body);
		viewer = tableManager.createViewer(body, toolkit);
		viewer.setInput(getViewerInput(detailType));
		tableManager.updateView(viewer);
	}

	private List<?> getViewerInput(String detailType) {
		try {
			INVManager invManager = Framework.getService(INVManager.class);
			List<?> l = invManager.traceMaterialDetail(materialRrn, null, null, detailType);
			return l;
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}

	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, false);
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

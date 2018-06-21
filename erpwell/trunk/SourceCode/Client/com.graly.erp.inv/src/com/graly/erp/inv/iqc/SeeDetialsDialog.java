package com.graly.erp.inv.iqc;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.Receipt;
import com.graly.framework.base.entitymanager.query.SingleEntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class SeeDetialsDialog extends SingleEntityQueryDialog {
	Logger logger = Logger.getLogger(SeeDetialsDialog.class);

	public SeeDetialsDialog(TableListManager listTableManager, IManagedForm managedForm, String whereClause, int style, Object object) {
		super(listTableManager, managedForm, whereClause, style);
		this.object = object;
	}

	protected Point getInitialSize() {
		Point p = super.getInitialSize();
		p.x = 850;
		p.y = 450;
		return p;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("search-dialog"));
		if (object instanceof Receipt) {
			setTitle(Message.getString("inv.iqcdetials"));
		}
		if (object instanceof Iqc) {
			setTitle(Message.getString("inv.in_details_info"));
		}
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite queryComp = new Composite(composite, SWT.NONE);
		queryComp.setLayout(new GridLayout());
		queryComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite resultComp = new Composite(composite, SWT.NONE);
		resultComp.setLayout(new GridLayout());
		resultComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		createSearchTableViewer(resultComp);
		getInitSearchResult();

		createWhereClause();
		refresh(true);
		return composite;
	}

	@Override
	protected void createSearchTableViewer(Composite parent) {
		mStyle = SWT.FULL_SELECTION | SWT.BORDER;
		listTableManager.setStyle(mStyle);
		tableViewer = (TableViewer) listTableManager.createViewer(parent, new FormToolkit(Display.getCurrent()));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), false);
	}
}

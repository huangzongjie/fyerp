package com.graly.erp.wip.mo.create;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.wip.querychart.ChartSection;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;

public class ChartDialog extends TitleAreaDialog {
	private static final Logger logger = Logger.getLogger(ChartDialog.class);
	private static final String TABLE_CHART = "DailyMoMaterial";
	private ManagedForm form;
	private String whereClause;
	
	public ChartDialog(Shell parentShell, String whereClause) {
		super(parentShell);
		this.whereClause = whereClause;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
        setTitle(Message.getString("wip.moload_dialog_title"));
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(parent);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		form = new ManagedForm(toolkit, sForm);
		Composite body = sForm.getForm().getBody();
		configureBody(body);
        ADTable table = getADTableByTableName(TABLE_CHART);
        ChartSection section = new ChartSection(new EntityTableManager(table), whereClause);
        section.createContents(form, body);
		return body;
	}

	
	protected ADTable getADTableByTableName(String tableName) {
		try {
			ADTable adTable = null;
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, tableName);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("ChartDialog : getADTableByTableName()", e);
		}
		return null;
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
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	protected Point getInitialSize() {
		return new Point(1024, 768);
	}
}

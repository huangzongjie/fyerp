package com.graly.alm.alarm;

import java.util.List;

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

import com.graly.alm.model.AlarmDefinition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;

public class AlarmActionDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(AlarmActionDialog.class);
	private ActionViewerSection viewerSection;
	protected ADTable actionTable;
	protected ADTable alarmTable;
	protected ManagedForm managedForm;
	private String where;
	private AlarmDefinition alarm;
	private ADManager adManager;
	private static final String TABLE_NAME_ACTION = "ALMAction";

	public AlarmActionDialog(Shell parent) {
		super(parent);
	}

	public AlarmActionDialog(Shell shell, ADTable parentTable) {
		this(shell);
		alarmTable = parentTable;
	}

	public AlarmActionDialog(Shell shell, ADTable parentTable, String where) {
		this(shell);
		alarmTable = parentTable;
		this.where = where;

	}

	public AlarmActionDialog(Shell shell, ADTable parentTable, AlarmDefinition alarm) {
		this(shell);
		alarmTable = parentTable;
		this.alarm = alarm;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		actionTable = getADTableOfAction();
		String dialogTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(actionTable, "label"));
		setTitle(dialogTitle);
		Composite composite = (Composite) super.createDialogArea(parent);

		createFormContent(composite);
		return composite;
	}

	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		managedForm = new ManagedForm(toolkit, sForm);

		Composite body = sForm.getForm().getBody();
		configureBody(body);
		actionTable = getADTableOfAction();

		if (alarm != null && alarm.getObjectRrn() != null) {
			try {
				adManager = Framework.getService(ADManager.class);
				String formCause = "objectRrn='" + alarm.getObjectRrn() + "'";
				List<AlarmDefinition> alarms = adManager.getEntityList(alarm.getOrgRrn(), AlarmDefinition.class, 2, formCause, "");
				if (alarms.get(0) != null) {
					alarm = (AlarmDefinition) alarms.get(0);
				} else {
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		viewerSection = new ActionViewerSection(actionTable, alarmTable, alarm);
		viewerSection.createContents(managedForm, body);
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

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), false);
	}

	protected ADTable getADTableOfAction() {
		try {
			if (actionTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				actionTable = entityManager.getADTable(0L, TABLE_NAME_ACTION);
				actionTable = entityManager.getADTableDeep(actionTable.getObjectRrn());
			}
			return actionTable;
		} catch (Exception e) {
			logger.error("NewAlarmDialog : getADTableOfAlarmAction()", e);
		}
		return null;
	}

	protected Point getInitialSize() {
		Point p = super.getInitialSize();
		p.x = 1000;
		p.y = 680;
		return p;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}
}

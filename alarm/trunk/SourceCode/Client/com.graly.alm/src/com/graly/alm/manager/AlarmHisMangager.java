package com.graly.alm.manager;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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

import com.graly.alm.model.AlarmHis;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;

public class AlarmHisMangager extends TitleAreaDialog {
	private static final Logger logger = Logger.getLogger(AlarmHisMangager.class);
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 350;
	private AlarmHisManagerSection alarmHisDetailSection;
	protected ADTable adTable;
	protected ManagedForm managedForm;
	private AlarmHis alarmHis;

	public AlarmHisMangager(Shell parent) {
		super(parent);
	}

	public AlarmHisMangager(Shell shell, ADTable adTable, AlarmHis alarmHis) {
		this(shell);
		this.adTable = adTable;
		this.alarmHis = alarmHis;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		String dialogTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
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
		alarmHisDetailSection = new AlarmHisManagerSection(adTable, alarmHis);
		alarmHisDetailSection.createContents(managedForm, body);
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

	@Override
	protected Point getInitialSize() {
		super.getShellStyle();
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
}

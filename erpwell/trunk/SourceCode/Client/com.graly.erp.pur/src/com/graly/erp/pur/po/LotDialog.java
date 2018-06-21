package com.graly.erp.pur.po;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.inv.model.ConditionItem;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.model.Lot;

public class LotDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(LotDialog.class);
	private static int MIN_DIALOG_WIDTH = 500;
	private static int MIN_DIALOG_HEIGHT = 450;
	private static String TABLE_NAME = "INVLot";

	protected ADTable table;
	protected ManagedForm managedForm;
	protected LotSection lotSection;
	private ConditionItem conditionItem;
	private List<Lot> lots;

	public LotDialog(Shell shell) {
		super(shell);
	}

	public LotDialog(Shell shell, ConditionItem conditionItem) {
		super(shell);
		this.conditionItem = conditionItem;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		getADTableLot();
		String dialogTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(table, "label"));
		setTitle(dialogTitle);
		Composite composite = (Composite) super.createDialogArea(parent);

		createFormContent(composite);
		return composite;
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

	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);

		Composite body = sForm.getForm().getBody();
		configureBody(body);

		getADTableLot();
		lotSection = new LotSection(table, conditionItem,lots);
		lotSection.createContents(managedForm, body);
	}

	protected ADTable getADTableLot() {
		try {
			if (table == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				table = entityManager.getADTable(0L, TABLE_NAME);
			}
			return table;
		} catch (Exception e) {
			logger.error("LotDialog : getADTableOfLot()", e);
		}
		return null;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), false);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == Dialog.CANCEL) {
			if(isSureExit()) {
				cancelPressed();
			}
		} else {
			super.buttonPressed(buttonId);
		}
	}
	
	protected boolean isSureExit() {
		return true;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	public List<Lot> getLots() {
		return lots;
	}

	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}
}

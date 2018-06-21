package com.graly.erp.wip.workcenter.reassign;

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

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class ReAssignMOLineDialog extends InClosableTitleAreaDialog {
	private ReAssignMOLineSection moLineSection;
	protected ADTable adTable;
	protected ManagedForm managedForm;
	protected ManufactureOrderLine moLine;
	private int MIN_DIALOG_WIDTH = 1000;
	private int MIN_DIALOG_HEIGHT = 500;

	public ReAssignMOLineDialog(Shell parent) {
		super(parent);
	}

	public ReAssignMOLineDialog(Shell shell, ADTable adTable, ManufactureOrderLine moLine) {
		this(shell);
		this.adTable = adTable;
		this.moLine = moLine;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		String dialogTitle = String.format(Message.getString("common.detail"), I18nUtil.getI18nMessage(adTable, "label"));
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
		moLineSection = new ReAssignMOLineSection(adTable, moLine);
		moLineSection.createContents(managedForm, body);
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

	protected Point getInitialSize() {
		Point p = super.getInitialSize();
		p.x = MIN_DIALOG_WIDTH;
		p.y = MIN_DIALOG_HEIGHT;
		return p;
	}

	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			try {
				if (moLineSection.getAdObject() != null) {
					boolean saveFlag = true;
					for (Form detailForm : moLineSection.getDetailForms()) {
						if (!detailForm.saveToObject()) {
							saveFlag = false;
						}
					}
					if (saveFlag) {
						for (Form detailForm : moLineSection.getDetailForms()) {
							PropertyUtil.copyProperties(moLineSection.getAdObject(), detailForm.getObject(), detailForm.getFields());
						}
						ManufactureOrderLine moLine = (ManufactureOrderLine) moLineSection.getAdObject();
//						ADManager adManager = Framework.getService(ADManager.class);
//						adManager.saveEntity(adTable.getObjectRrn(), moLine, Env.getUserRrn());

						WipManager wipManager = Framework.getService(WipManager.class);
						wipManager.changeWorkCenter(moLine, Env.getUserRrn());
						
					    UI.showInfo(Message.getString("common.save_successed"));
						okPressed();
					}
				}

			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.cancel"), false);
	}
}

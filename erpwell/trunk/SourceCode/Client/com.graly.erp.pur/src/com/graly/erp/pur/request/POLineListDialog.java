package com.graly.erp.pur.request;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
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

import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.po.POLineBlockDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class POLineListDialog extends InClosableTitleAreaDialog {
	protected POLineListSection viewerSection;
	protected ManagedForm managedForm;
	private ADTable adTablePOLine, adTablePO;
	protected List<PurchaseOrderLine> list;
	protected boolean flag = false;

	public POLineListDialog(Shell parent) {
		super(parent);
	}

	public POLineListDialog(Shell shell, ADTable adTablePOLine, ADTable  adTablePO, List<PurchaseOrderLine> list) {
		this(shell);
		this.adTablePOLine = adTablePOLine;
		this.adTablePO = adTablePO;
		this.list = list;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		String dialogTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTablePOLine, "label"));
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
		viewerSection = new POLineListSection(adTablePOLine, list, this);
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
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), false);
	}

	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			if (viewerSection.getSelectedpoLine() == null) {
				UI.showWarning(Message.getString("inv.null"));
				return;
			}
			ADManager adManager;
			try {
				adManager = Framework.getService(ADManager.class);
				PurchaseOrderLine poLine = viewerSection.getSelectedpoLine();
				String where = " objectRrn = '" + poLine.getPoRrn() + "' ";
				List<PurchaseOrder> pos = adManager.getEntityList(Env.getOrgRrn(), PurchaseOrder.class, 2, where, "");
				if (pos != null) {
					PurchaseOrder po = pos.get(0);
					where = (" poRrn = '" + po.getObjectRrn() + "' AND requisitionLineRrn = '" + poLine.getRequisitionLineRrn() + "' ");
					POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), adTablePO, where, po, adTablePOLine, true);
					if (cd.open() == Dialog.CANCEL) {
					}
				}
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
			if (!flag) {
				okPressed();
			}
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	protected Point getInitialSize() {
		Point p = super.getInitialSize();
		p.x = 1000;
		p.y = 680;
		return p;
	}
}

package com.graly.erp.wip.mo;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;

public class MoCommentsDialog extends InClosableTitleAreaDialog {
	private static int MIN_DIALOG_WIDTH = 640;
	private static int MIN_DIALOG_HEIGHT = 360;
	
	ManagedForm managedForm;
	ADTable adTable;
	ManufactureOrder mo;
	MoCommentsSection section;

	public MoCommentsDialog(ADTable adTable, ManufactureOrder mo) {
		super(UI.getActiveShell());
		this.adTable = adTable;
		this.mo = mo;
	}

	@Override
    protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
        setTitle(String.format(Message.getString("common.editor"),
        		I18nUtil.getI18nMessage(adTable, "label")));

        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		managedForm = new ManagedForm(toolkit, sForm);
		section = new MoCommentsSection(adTable, mo);
		section.createContents(managedForm, body);
		
        return body;
	}
	
	@Override
	protected void okPressed() {
		mo = section.getUpdateCommentsMo();
//		if(mo.getComments() == null || "".equals(mo.getComments().trim()))
//			return;
		super.okPressed();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.ok"), true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
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
	
	public ManufactureOrder getUpdateCommentsMo() {
		return mo;
	}
}

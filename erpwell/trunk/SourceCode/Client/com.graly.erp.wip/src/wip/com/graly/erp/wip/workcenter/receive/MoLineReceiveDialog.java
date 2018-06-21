package com.graly.erp.wip.workcenter.receive;

import org.eclipse.jface.dialogs.Dialog;
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

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.wip.model.Lot;

public class MoLineReceiveDialog extends TitleAreaDialog {
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 400;
	
	private ManagedForm form;	
	private MoLineReceiveSection section;
	private ManufactureOrderLine moLine;
	private Lot parentLot;
	private WorkCenter workCenter;
	private String moComments;

	public MoLineReceiveDialog(Shell shell) {
		super(shell);
	}
	
	public MoLineReceiveDialog(Shell shell, ManufactureOrderLine moLine,
			Lot parentLot, WorkCenter workCenter, String moComments) {
		this(shell);
		this.moLine = moLine;
		this.parentLot = parentLot;
		this.workCenter = workCenter;
		this.moComments = moComments;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
        setTitle(Message.getString("wip.receive_mo_line"));

        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		form = new ManagedForm(toolkit, sForm);
		
		section = new MoLineReceiveSection(this, moLine.getMaterial().getLotType(), moComments);
		section.createContent(form, body);
        return body;
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
		if(!section.isSureExit())
			return UI.showConfirm(Message.getString("inv.confirm_save_before_exit"));
		return true;
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
				Message.getString("common.exit"), false);
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
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
	
	public ManufactureOrderLine getMoLine() {
		return moLine;
	}

	public void setMoLine(ManufactureOrderLine moLine) {
		this.moLine = moLine;
	}

	public Lot getParentLot() {
		return parentLot;
	}

	public void setParentLot(Lot parentLot) {
		this.parentLot = parentLot;
	}

	public WorkCenter getWorkCenter() {
		return workCenter;
	}

}

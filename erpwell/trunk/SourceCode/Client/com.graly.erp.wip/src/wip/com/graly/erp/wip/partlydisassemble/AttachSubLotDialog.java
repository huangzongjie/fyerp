package com.graly.erp.wip.partlydisassemble;

import java.util.List;

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

import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.mes.wip.model.Lot;

public class AttachSubLotDialog extends TitleAreaDialog {
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 400;
	
	protected AttachSubLotSection section;
	protected ManagedForm form;
	protected Lot parentLot;
	private List<Lot> inputLots;
	
	public AttachSubLotDialog(Shell parentShell, Lot parentLot) {
		super(parentShell);
		this.parentLot = parentLot;
		
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
		
		section = new AttachSubLotSection(parentLot);
		section.createContent(form, body);
		
		return body;
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
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.save_exit"),
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
	}
	
	@Override
	protected void okPressed() {
		if(section != null && section.validate()){
			inputLots = section.getInputLots();
		}
		super.okPressed();
	}
	@Override
	protected void cancelPressed() {
		UI.showConfirm("是否确定不保存就退出");
		super.cancelPressed();
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

	public List<Lot> getInputLots() {
		return inputLots;
	}
}

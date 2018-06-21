package com.graly.erp.wip.workcenter.receive;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Message;

public class InsufficientBomsDialog extends InClosableTitleAreaDialog {
	private ADTable adTable;
	private TableViewer tViewer;
	private List<ManufactureOrderBom> boms;

	public InsufficientBomsDialog(Shell parentShell, ADTable adTable, List<ManufactureOrderBom> boms) {
		super(parentShell);
		this.adTable = adTable;
		this.boms = boms;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite content = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(content.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(content);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		GridData gd = new GridData(GridData.FILL_BOTH);
		sForm.setLayout(gl);
		sForm.setLayoutData(gd);
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		TableListManager t = new TableListManager(adTable);
		tViewer = (TableViewer) t.createViewer(body, toolkit);
		tViewer.setInput(boms);
		tViewer.refresh(true);
		return content;
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite content = (Composite) super.createContents(parent);
		setTitle(Message.getString("wip.moline_insufficient_boms"));
		setMessage(Message.getString("wip.moline_insufficient_boms"),IMessageProvider.WARNING);
		return content;
		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.continue"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(800,400);
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
}

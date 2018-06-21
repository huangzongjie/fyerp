package com.graly.erp.inv.material.qtysquery.alarmlevel;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.base.ui.util.Env;

public class GuanLianDialog extends InClosableTitleAreaDialog {
	protected int mStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	protected ADTable moTable;
	protected Long materialRrn;
	protected String moWhereClause;
	private static int MIN_DIALOG_WIDTH = 750;
	private static int MIN_DIALOG_HEIGHT = 350;
	
	public GuanLianDialog(Shell parent, ADTable moTable, Long materialRrn){
		super(parent);
		this.moTable = moTable;
		this.materialRrn = materialRrn;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite center = (Composite) super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(center);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		setTitle("使用警报物料的工作令");
		Font font = new Font(Display.getCurrent(),"Arial",8,SWT.BOLD);
		GridData gd = new GridData();
		gd.heightHint = 15;
		Label lblPrLine = new Label(body, SWT.ALPHA);
		lblPrLine.setFont(font);
		lblPrLine.setLayoutData(gd);
		lblPrLine.setText("工作令信息");
		createMoLineViewer(body);
		return body;
	}
	
	protected void createMoLineViewer(Composite parent){
		TableListManager tableManager = new TableListManager(moTable);
		tableManager.setStyle(mStyle);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		StructuredViewer viewer = tableManager.createViewer(parent, toolkit);
		try{
			WipManager wipmanager = Framework.getService(WipManager.class);
			List<ManufactureOrderLine> lots = wipmanager.findMoLinesByChildMaterial(Env.getOrgRrn() , materialRrn);
			viewer.setInput(lots);
		}
		catch(Exception e){
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout();
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
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
	}
}

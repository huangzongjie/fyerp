package com.graly.erp.wip.mo;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
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

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class PrepareBomDialog extends InClosableTitleAreaDialog {
	private static int MIN_DIALOG_WIDTH = 640;
	private static int MIN_DIALOG_HEIGHT = 360;
	public static final String AD_TABLE_NAME = "WIPPrepareBom";
	private ManufactureOrder mo;
	protected ManagedForm form;
	protected TableViewerManager tableManager;
	protected TableViewer viewer;
	protected ADTable adTable;
	protected List<ManufactureOrderBom> input;
	
	public PrepareBomDialog(Shell parentShell, ManufactureOrder mo) {
		super(parentShell);
		this.mo = mo;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
		getADTableOfMoLine();
        setTitle(String.format(Message.getString("common.editor"),
        		I18nUtil.getI18nMessage(adTable, "label")));

        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
//		form = new ManagedForm(toolkit, sForm);
		
		createTableViewer(body, toolkit);
		initTableContent();
        return body;
	}
	
	protected ADTable getADTableOfMoLine() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, AD_TABLE_NAME);
			}
			return adTable;
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		tableManager = new TableListManager(adTable);
		viewer = (TableViewer)tableManager.createViewer(client, toolkit);
		tableManager.updateView(viewer);
	}
	
	protected void initTableContent() {
		try {
//			if(mo != null) {
				WipManager wipManager = Framework.getService(WipManager.class);
//				input = wipManager.getPrepareBoms(Env.getOrgRrn(),null);           
				refresh();				
//			}
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        }
	}
	
	protected void refresh() {
		tableManager.setInput(getInput());
		tableManager.updateView(viewer);
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
	
	public List<ManufactureOrderBom> getInput() {
		return input;
	}
	
	public void setInput(List<ManufactureOrderBom> input) {
		this.input = input;
	}
}

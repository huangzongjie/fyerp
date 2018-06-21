package com.graly.erp.xz.pur.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MergeDialog extends InClosableTitleAreaDialog {
	private TableListManager tableManager;
	private static int MIN_DIALOG_WIDTH = 800;
	private static int MIN_DIALOG_HEIGHT = 450;
	private boolean ignoreDateEnd = true;
	private CheckboxTableViewer viewer;
	private IRefresh iRefresh;
	protected Requisition selectedReq;
	private String title;
	private String message;
	
	public MergeDialog(Shell parentShell, String title, String message, TableListManager tableManager, IRefresh iRefresh) {
		super(parentShell);
		this.tableManager = tableManager;
		this.title = title;
		this.message = message;
		this.iRefresh = iRefresh;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitle(title);
		setMessage(message);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		GridData gd = new GridData(GridData.FILL_BOTH);
		sForm.setLayoutData(gd);
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		viewer = (CheckboxTableViewer) tableManager.createViewer(body, toolkit, 80);
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				selectedReq = (Requisition)ss.getFirstElement();
	    		viewDetailAdapter();
			}
			
		});
		try {
			PURManager purManager = Framework.getService(PURManager.class);
			List list = purManager.getCanMergePr(Env.getOrgRrn());
			viewer.setInput(list);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		Composite client = toolkit.createComposite(body,SWT.NULL);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		GridLayout layout = new GridLayout();
		layout.marginTop = 5;
		layout.marginLeft = 10;
				
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		
		client.setLayout(layout);
		client.setData(gd2);
		

		Button bt1 = toolkit.createButton(client, Message.getString("pur.by_material"), SWT.RADIO);
		
		bt1.addListener (SWT.Selection, new Listener () {
            public void handleEvent (Event event) {
                Button widget = (Button)event.widget;
                if (widget.getSelection()) {
                	ignoreDateEnd = true;
                }
            }
        });
		Button bt2 = toolkit.createButton(client, Message.getString("pur.by_material_and_enddate"), SWT.RADIO);
		
		bt2.addListener (SWT.Selection, new Listener () {
            public void handleEvent (Event event) {
                Button widget = (Button)event.widget;
                if (widget.getSelection()) {
                	ignoreDateEnd = false;
                }
            }
        });
		
		if(ignoreDateEnd){
			bt1.setSelection(true);
		}else{
			bt2.setSelection(true);
		}
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
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(MIN_DIALOG_WIDTH, MIN_DIALOG_HEIGHT);
	}

	@Override
	protected void okPressed() {
		Object[] elements = viewer.getCheckedElements();
		List list = Arrays.asList(elements);
		if(list == null || list.size() == 0){
			UI.showWarning(Message.getString("pur.no_requisition_to_merge"));
			return;
		}
		try {
			PURManager purManager = Framework.getService(PURManager.class);
			Requisition newRequisition = purManager.mergePr(Env.getOrgRrn(), list, ignoreDateEnd, Env.getUserRrn());
			UI.showInfo(Message.getString("pur.merge_succeed") + newRequisition.getDocId());
			iRefresh.refresh();
			super.okPressed();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}		
	}
	
	protected void viewDetailAdapter() {
//		try {
//			if(selectedReq != null && selectedReq.getObjectRrn() != null) {
//				ADManager adManager = Framework.getService(ADManager.class);
//				selectedReq = (Requisition)adManager.getEntity(selectedReq);
//				String whereClause = ( " requisitionRrn = '" + selectedReq.getObjectRrn().toString() + "' ");
//				RequisitionLineBlockDialog cd = new RequisitionLineBlockDialog(UI.getActiveShell(),
//						((XZRequisitionSection)iRefresh).getADTableOfRequisition(), whereClause, selectedReq, true);
//				cd.open();
//			}
//		} catch(Exception e) {
//			ExceptionHandlerManager.asyncHandleException(e);
//		}
	}
	
	 protected void createButtonsForButtonBar(Composite parent) {
	        createButton(parent, IDialogConstants.OK_ID,
	        		Message.getString("common.ok"), false);
	        createButton(parent, IDialogConstants.CANCEL_ID,
	        		Message.getString("common.cancel"), false);
	 }
}

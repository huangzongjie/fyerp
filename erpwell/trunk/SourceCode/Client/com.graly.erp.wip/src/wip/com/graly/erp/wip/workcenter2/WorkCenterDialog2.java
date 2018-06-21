package com.graly.erp.wip.workcenter2;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.ADUser;
import com.graly.framework.security.model.WorkCenter;

public class WorkCenterDialog2 extends ExtendDialog {
	private static final Logger logger = Logger.getLogger(WorkCenterDialog2.class);
	public static final String DIALOG_ID = "com.graly.erp.wip.workcenter.WorkCenterDialog";
	
	private static int MIN_DIALOG_WIDTH = 500;
	private static int MIN_DIALOG_HEIGHT = 300;
	
	protected ADTable adTable;
	protected WorkCenter selectedWC;

	public WorkCenterDialog2() {
		super();
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		initAdTableByTableId();
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
		String editorTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
        setTitle(editorTitle);

        Composite comp = (Composite)super.createDialogArea(parent);        
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());		
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
        
		StructuredViewer viewer = createTableViewer(body, toolkit);
    	createViewAction(viewer);
        return body;
	}
	
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectedWorkCenter(ss.getFirstElement());
	    		buttonPressed(IDialogConstants.OK_ID);
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectedWorkCenter(ss.getFirstElement());
				} catch (Exception e){
					logger.error("WorkCenterDialog : createViewAction()", e);
				}
			}
	    });
	}
	
	private void setSelectedWorkCenter(Object obj) {
		if(obj instanceof WorkCenter) {
			selectedWC = (WorkCenter)obj;
		} else {
			selectedWC = null;
		}
	}
	
	protected void initAdTableByTableId() {
		try {
			Long adTableRrn = Long.parseLong(this.getTableId());
			if(adTableRrn != null) {
				adTable = new ADTable();
				adTable.setObjectRrn(adTableRrn);
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = (ADTable)entityManager.getEntity(adTable);				
			}
		} catch(Exception e) {
			logger.error("WorkCenterDialog : initAdTableByTableId()", e);
		}
	}
	
	public StructuredViewer createTableViewer(Composite parent, FormToolkit toolkit) {
		EntityTableManager tableManager = new EntityTableManager(adTable);
		StructuredViewer viewer = tableManager.createViewer(parent, toolkit);

	    EntityItemInput input = new EntityItemInput(tableManager.getADTable(), getWhereClause(), "");
//		TableListManager tableManager = null;
//		StructuredViewer viewer = null;
//		try {
//			tableManager = new TableListManager(adTable);
//			viewer = tableManager.createViewer(parent, toolkit);
//			ADUser user = new ADUser();
//			user.setObjectRrn(Env.getUserRrn());
//			ADManager manager = Framework.getService(ADManager.class);
//			user = (ADUser) manager.getEntity(user);
//			List<WorkCenter> wcs = user.getWorkCenters();
//			List<WorkCenter> input = new ArrayList<WorkCenter>();
//			for(WorkCenter wc : wcs){
//				if(wc.getOrgRrn().compareTo(Env.getOrgRrn()) == 0){
//					input.add(wc);
//				}
//			}
//			viewer.setInput(input);
//			tableManager.updateView(viewer);
//		} catch (Exception e) {
//			logger.error(e.getMessage(),e);
//			ExceptionHandlerManager.asyncHandleException(e);
//		}
		viewer.setInput(input);
		tableManager.updateView(viewer);
		return viewer;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.OK_ID) {
			if(selectedWC == null)
				return;
			WorkCenterEditor2 editor = (WorkCenterEditor2) getParent();
			WorkCenterEntryPage2 page = (WorkCenterEntryPage2) editor.getActivePageInstance();
			page.setWorkCenter(selectedWC);
			page.refresh();
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	

	public WorkCenter getSelectedWC() {
		return selectedWC;
	}

	public void setSelectedWC(WorkCenter selectedWC) {
		this.selectedWC = selectedWC;
	}
	
	protected String getWhereClause() {
		return "";
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
}

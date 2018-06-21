package com.graly.erp.inv.out.imp;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


import com.graly.erp.inv.model.InvErrorLog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class ErrorLogDisplayDialog extends InClosableTitleAreaDialog {	
	IManagedForm form;
	List<InvErrorLog> errlogs;
	protected EntityTableManager tableManager;
	protected ADTable adTable;
	public static final String AD_TABLE_NAME_MOLINE = "InvErrorLog";
	private static int MIN_DIALOG_WIDTH = 640;
	private static int MIN_DIALOG_HEIGHT = 400;
	protected TableViewer viewer;
	
	
	public ErrorLogDisplayDialog(Shell parentShell,List<InvErrorLog> errlogs) {
		super(parentShell);
		this.errlogs = errlogs;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
		getADTableOfInvErrorLog();
        setTitle(String.format(Message.getString("common.editor"),
        		I18nUtil.getI18nMessage(adTable, "label")));

        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		createTableViewer(body, toolkit);
		return body;
	}
	
protected void createTableViewer(Composite client, FormToolkit toolkit) {
		
		tableManager = new EntityTableManager(adTable);
		viewer = (TableViewer) tableManager.createViewer(client, toolkit);
		viewer.setInput(errlogs);
		tableManager.updateView(viewer);
	}
	
	protected void refresh() {
		tableManager.setInput(getErrlogs());
		tableManager.updateView(viewer);
	}
	
	protected ADTable getADTableOfInvErrorLog() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, AD_TABLE_NAME_MOLINE);
			}
			return adTable;
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), true);
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

	public List<InvErrorLog> getErrlogs() {
		return errlogs;
	}

	public void setErrlogs(List<InvErrorLog> errlogs) {
		this.errlogs = errlogs;
	}
	
//	class ErrorLogTableManager extends TableListManager {
//		public String MpsID = "mpsId";
//		public String MaterialID = "materialId";
//		public String ErrMessage = "errMessage";
//		public String ErrDate = "errDate";
//
//		public String Cloumn_MpsID = Message.getString("ppm.mpsId");
//		public String Cloumn_MaterialID = Message.getString("pdm.material_id");
//		public String Cloumn_ErrMessage = Message.getString("ppm.error_message");
//		public String Cloumn_ErrDate = Message.getString("ppm.error_date");
//		
//		public ErrorLogTableManager() {
//			super(null);
//		}
//		
//	    @Override
//	    protected String[] getColumns() {
//	    	return new String[]{MpsID, MaterialID, ErrMessage, ErrDate};
//	    }
//	    
//	    @Override
//	    protected String[] getColumnsHeader() {
//	    	return new String[]{Cloumn_MpsID, Cloumn_MaterialID, Cloumn_ErrMessage, Cloumn_ErrDate};
//	    }
//	    
//	    protected Integer[] getColumnSize() {
//	    	return new Integer[]{15, 15, 70, 15};
//	    }
//	}

//	@Override
//	protected int getShellStyle() {
//		return super.getShellStyle() | SWT.RESIZE;
//	}
	
	

}
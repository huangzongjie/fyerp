package com.graly.erp.pur.request;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.MaterialQueryDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class RequisitionSection extends MasterSection {
	
	private static final Logger logger = Logger.getLogger(RequisitionSection.class);
	protected ToolItem itemMerge;
	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;
	protected Requisition selectedReq;
	private static final String TABLE_NAME = "PURRequisitionLine";
	private ADTable adTable;
	
	public RequisitionSection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionRequisition(ss.getFirstElement());
	    		editAdapter();
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
		    		setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemEditor(tBar);
		createToolItemMerge(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	private void createToolItemMerge(ToolBar tBar) {
		itemMerge = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_MERGE);
		itemMerge.setText(Message.getString("pur.merge"));
		itemMerge.setImage(SWTResourceCache.getImage("merge"));
		itemMerge.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				mergeAdapter();
			}
		});
	}

	protected void mergeAdapter() {
		TableListManager manager = new TableListManager(tableManager.getADTable(), SWT.CHECK);
		MergeDialog md = new MergeDialog(UI.getActiveShell(), Message.getString("pur.merge_dialog_title"), null, manager, this);
		md.open();
	}

	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}
	
	protected void createToolItemEditor(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}
	
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	protected void newAdapter() {
		int i=1;
		String where = " 1!=1 ";
		RequisitionLineBlockDialog cd = new RequisitionLineBlockDialog(UI.getActiveShell(),
				getADTableOfRequisition(), where, null,i);
		if(cd.open() == Dialog.CANCEL) {
			refreshSection();
		}
	}
	
	protected void editAdapter() {
		try {
			if(selectedReq != null && selectedReq.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedReq = (Requisition)adManager.getEntity(selectedReq);
				String whereClause = ( " requisitionRrn = '" + selectedReq.getObjectRrn().toString() + "' ");
				RequisitionLineBlockDialog cd = new RequisitionLineBlockDialog(UI.getActiveShell(),
						getADTableOfRequisition(), whereClause, selectedReq);
				if(cd.open() == Dialog.CANCEL) {
					refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at RequisitionSection : editAdapter() " + e);
		}
	}
	
	@Override
	protected void queryAdapter() {		
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_TPR);
			queryDialog.open();
		}
	}

	protected void deleteAdapter() {
		if(selectedReq != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedReq.getObjectRrn() != null) {
						PURManager purManager = Framework.getService(PURManager.class);
						purManager.deletePR(selectedReq, Env.getUserRrn());
						this.selectedReq = null;
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	
	protected void refreshSection() {
		try {
			refresh();
			if(selectedReq != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedReq = (Requisition)adManager.getEntity(selectedReq);
				this.setStatusChanged(selectedReq.getDocStatus());
			}
		} catch(Exception e) {
			logger.error("Error at RequisitionSection : refreshSection() " + e);
		}
	}

	protected ADTable getADTableOfRequisition() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	protected void setSelectionRequisition(Object obj) {
		if(obj instanceof Requisition) {
			selectedReq = (Requisition)obj;
			setStatusChanged(selectedReq.getDocStatus());
		} else {
			selectedReq = null;
			setStatusChanged("");
		}
	}
	
	protected void setStatusChanged(String status) {
		if(Requisition.STATUS_DRAFTED.equals(status)) {
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if(Requisition.STATUS_CLOSED.equals(status)) {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}
}

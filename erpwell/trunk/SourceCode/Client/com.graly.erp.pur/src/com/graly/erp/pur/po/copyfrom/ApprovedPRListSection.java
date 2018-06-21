package com.graly.erp.pur.po.copyfrom;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.pur.request.RequisitionSection;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;

public class ApprovedPRListSection extends RequisitionSection {
	protected ManagedForm managedForm;
	protected ToolItem openPRLines;
	protected ApprovedPRListDialog approvedPRDialog;

	public ApprovedPRListSection(EntityTableManager tableManager, ManagedForm managedForm){
		super(tableManager);
		this.managedForm = managedForm;
	}
	
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionRequisition(ss.getFirstElement());
	    		openPRLinesAdapter();
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
		createToolItemPRLine(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemPRLine(ToolBar tBar) {
		openPRLines = new ToolItem(tBar, SWT.PUSH);
		openPRLines.setText(Message.getString("pur.prlines"));
		openPRLines.setImage(SWTResourceCache.getImage("prlines"));
		openPRLines.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				openPRLinesAdapter();
			}
		});
	}
	
	protected void openPRLinesAdapter() {
		if(selectedReq != null) {
			PRLinesDialog lineDialog = new PRLinesDialog(UI.getActiveShell(),
					selectedReq, approvedPRDialog, managedForm);
			if(lineDialog.open() == Dialog.OK) {
				// 关闭已审核的PR列表对话框
				approvedPRDialog.buttonPressed(IDialogConstants.CANCEL_ID);
			}
		}
	}
	
	public ApprovedPRListDialog getApprovedPRDialog() {
		return approvedPRDialog;
	}

	public void setApprovedPRDialog(ApprovedPRListDialog approvedPRDialog) {
		this.approvedPRDialog = approvedPRDialog;
	}
	
	@Override
	protected void setStatusChanged(String status) {
	}
}

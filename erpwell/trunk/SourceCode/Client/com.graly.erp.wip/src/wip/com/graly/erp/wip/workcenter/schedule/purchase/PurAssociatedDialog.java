package com.graly.erp.wip.workcenter.schedule.purchase;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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

import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.po.POLineBlockDialog;
import com.graly.erp.pur.request.ColorEntityTableManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PurAssociatedDialog extends InClosableTitleAreaDialog {
	protected int mStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	protected ADTable prTable;
	protected ADTable poTable;
	protected Long materialRrn;
	protected String prWhereClause, poWhereClause;
	private static int MIN_DIALOG_WIDTH = 750;
	private static int MIN_DIALOG_HEIGHT = 350;
	private PurchaseOrderLine selectPoLine;
	private static final String TABLE_NAME = "PURPurchaseOrderLine";
	private ADTable adTable;
	private static final String TABLE_NAME_PO = "PURPurchaseOrder";
	private ADTable adTablePO;
	
	public PurAssociatedDialog(Shell parent, ADTable prTable, ADTable poTable, Long materialRrn){
		super(parent);
		this.prTable = prTable;
		this.poTable = poTable;
		this.materialRrn = materialRrn;
	}
	
	public PurAssociatedDialog(Shell parent, ADTable prTable, ADTable poTable, Long materialRrn,String prWhereClause, String poWhereClause){
		super(parent);
		this.prTable = prTable;
		this.poTable = poTable;
		this.materialRrn = materialRrn;
		this.prWhereClause = prWhereClause;
		this.poWhereClause = poWhereClause;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite center = (Composite) super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(center);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		setTitle(Message.getString("wip.pur_lines"));
		Font font = new Font(Display.getCurrent(),"Arial",8,SWT.BOLD);
		GridData gd = new GridData();
		gd.heightHint = 15;
		Label lblPrLine = new Label(body, SWT.ALPHA);
		lblPrLine.setFont(font);
		lblPrLine.setLayoutData(gd);
		lblPrLine.setText(I18nUtil.getI18nMessage(prTable, "label"));
		createPrLineViewer(body);
		Label lblPoLine = new Label(body, SWT.NONE);
		lblPoLine.setFont(font);
		lblPoLine.setLayoutData(gd);
		lblPoLine.setText(I18nUtil.getI18nMessage(poTable, "label"));
		createPoLineViewer(body);
		return body;
	}
	
	protected void createPrLineViewer(Composite parent){
		ColorEntityTableManager entityTableManager = new ColorEntityTableManager(prTable);
		entityTableManager.setStyle(mStyle);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		StructuredViewer viewer = entityTableManager.createViewer(parent, toolkit);
		String whereClause = " 1<> 1";
		if(materialRrn != null){
			whereClause =( " materialRrn = " + materialRrn + " AND lineStatus in ('DRAFTED','APPROVED') ");
		}
		
		if(prWhereClause != null && prWhereClause.trim().length() > 0){
			whereClause = whereClause + " AND " + prWhereClause;
		}
		viewer.setInput(new EntityItemInput(prTable, whereClause, ""));
	}
	
	protected void createPoLineViewer(Composite parent){
		try {
			ColorEntityTableManager entityTableManager = new ColorEntityTableManager(poTable);
			entityTableManager.setStyle(mStyle);
			FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
			StructuredViewer viewer = entityTableManager.createViewer(parent, toolkit);
			String whereClause = " 1<>1";
			if(materialRrn != null){
				whereClause =( " materialRrn = " + materialRrn + " AND lineStatus in ('DRAFTED','APPROVED') ");
			}
			
			if(poWhereClause != null && poWhereClause.trim().length() > 0){
				whereClause = whereClause + " AND " + poWhereClause;
			}
			
			viewer.setInput(new EntityItemInput(poTable, whereClause, ""));
			
			
			viewer.addDoubleClickListener(new IDoubleClickListener(){

				@Override
				public void doubleClick(DoubleClickEvent event) {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionPoLine(ss.getFirstElement());
					try {
						if(selectPoLine != null) {
							ADManager adManager = Framework.getService(ADManager.class);
							PurchaseOrder selectedPO = new PurchaseOrder();
							selectedPO.setObjectRrn(selectPoLine.getPoRrn());
							selectedPO = (PurchaseOrder) adManager.getEntity(selectedPO);
							if (selectedPO != null && selectedPO.getObjectRrn() != null) {
								ADTable adTable = getADTableOfPOLine();
								
								
								String whereClause = (" poRrn = '" + selectedPO.getObjectRrn().toString() + "' ");
								POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), getADTableOfPO(), whereClause, selectedPO,
										adTable);
								if (cd.open() == Dialog.CANCEL) {
//										refreshSection();
								}
							}
						}
					} catch(Exception e) {
						ExceptionHandlerManager.asyncHandleException(e);
					}
				}
				
			});
			viewer.addSelectionChangedListener(new ISelectionChangedListener(){
		    	public void selectionChanged(SelectionChangedEvent event) {
					try{
						StructuredSelection ss = (StructuredSelection) event.getSelection();
						setSelectionPoLine(ss.getFirstElement());
					} catch (Exception e){
						e.printStackTrace();
					}
				}
		    });
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected ADTable getADTableOfPOLine() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	protected ADTable getADTableOfPO() {
		try {
			if (adTablePO == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTablePO = entityManager.getADTable(0L, TABLE_NAME_PO);
				adTablePO = entityManager.getADTableDeep(adTablePO.getObjectRrn());
			}
			return adTablePO;
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	
	protected void setSelectionPoLine(Object obj) {
		if(obj instanceof PurchaseOrderLine) {
			selectPoLine = (PurchaseOrderLine)obj;
		} else {
			selectPoLine = null;
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

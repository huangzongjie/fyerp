package com.graly.erp.pur.po;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.inv.model.IqcLine;
import com.graly.erp.inv.model.ReceiptLine;
import com.graly.erp.inv.model.VPoAlarmIqcLine;
import com.graly.erp.inv.model.VPoAlarmMovenetLine;
import com.graly.erp.inv.model.VPoAlarmReceiptLine;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.pur.client.PURManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class POAlarmDialog extends EntityDialog {

	protected TableViewerManager tableManager;
	protected TableViewer viewer;
	private int MIN_DIALOG_WIDTH=600;
	private int MIN_DIALOG_HEIGHT=300;
	private String sortName;//分类
	private String purchaser;//采购人员
	private ADBase selectObject;
	
	public POAlarmDialog(Shell parent, ADTable table, ADBase adObject,String sortName,String purchaser) {
		super(parent, table, adObject);
		this.sortName = sortName;
		this.purchaser = purchaser;
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),shellSize.y));
	}
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
	
	@Override
	protected void createFormContent(Composite composite) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
	    setTitle("采购提醒");
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		managedForm = new ManagedForm(toolkit, sForm);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		try {
			PURManager purManager = Framework.getService(PURManager.class);
			ADManager adManager = Framework.getService(ADManager.class);
			if(sortName!=null && sortName.equals("RECEIPT")){
				String tableNameReceipt =  "POAlarmReceiptLine";
				this.table = adManager.getADTable(0, tableNameReceipt);
			}else if(sortName!=null && sortName.equals("IQC")){
				String tableNameIqc =  "POAlarmIqcLine";
				this.table = adManager.getADTable(0, tableNameIqc);
			}else if(sortName!=null && sortName.equals("INV")){
				String tableNameInv =  "POAlarmMovementLine";
				this.table = adManager.getADTable(0, tableNameInv);
			}
			
			FormToolkit formToolkit  = new FormToolkit(Display.getCurrent());
			tableManager = new EntityTableManager(table);
			viewer = (TableViewer) tableManager.createViewer(body, formToolkit);
			createViewAction();//添加双击监听事件
			if(sortName!=null && sortName.equals("RECEIPT")){
				List<VPoAlarmReceiptLine> receiptLines = purManager.getAlarmReceipts(Env.getOrgRrn(), purchaser);
				viewer.setInput(receiptLines);
			}else if(sortName!=null && sortName.equals("IQC")){
				List<VPoAlarmIqcLine> iqcLines = purManager.getAlarmIqcs(Env.getOrgRrn(), purchaser);
				viewer.setInput(iqcLines);
			}else if(sortName!=null && sortName.equals("INV")){
				List<VPoAlarmMovenetLine> invAlarms = purManager.getAlarmInvs(Env.getOrgRrn(), purchaser);
				viewer.setInput(invAlarms);
			}
//			viewer.setInput(new EntityItemInput(tableManager.getADTable(), "docStatus = 'APPROVED' ", ""));		
//			tableManager.updateView(viewer);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
  
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
//		Button btnOk = createButton(parent, IDialogConstants.OK_ID,
//				Message.getString("common.ok"), false);
//		if (DIALOGTYPE_VIEW.equals(dialogType)) {
//			btnOk.setEnabled(false);
//		}
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}

	public ADBase getSelectObject() {
		return selectObject;
	}

	public void setSelectObject(ADBase selectObject) {
		this.selectObject = selectObject;
	}
	
	protected void createViewAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectObject((ADBase) ss.getFirstElement());
				openPODialog();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectObject((ADBase) ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void openPODialog(){
		try{
			if (selectObject != null && selectObject.getObjectRrn() != null) {
				String poId= null;
				if(selectObject instanceof VPoAlarmReceiptLine){
					poId = ((VPoAlarmReceiptLine)selectObject).getPoId();
				}else if(selectObject instanceof VPoAlarmIqcLine){
					poId = ((VPoAlarmIqcLine)selectObject).getPoId();
				}else if(selectObject instanceof VPoAlarmMovenetLine){
					poId = ((VPoAlarmMovenetLine)selectObject).getPoId();
				}
				ADTable poTable = getADTableOfPO();
				
				ADTable adTable = getADTableOfPOLine();
				ADManager adManager = Framework.getService(ADManager.class);
				List<PurchaseOrder> pos = adManager.getEntityList(Env.getOrgRrn(), PurchaseOrder.class, Integer.MAX_VALUE,"docId = '"+poId+"'",null);
				PurchaseOrder selectedPO  = pos.get(0);
//				selectedPO = (PurchaseOrder)adManager.getEntity(selectedPO);
				String whereClause = (" poRrn = '" + selectedPO.getObjectRrn().toString() + "' ");
				POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), poTable, whereClause, selectedPO,
							adTable);
				if (cd.open() == Dialog.CANCEL) {
				}
			}
		}catch(Exception e){
			
		}
	}
	
	protected ADTable getADTableOfPOLine() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTablePoLine = entityManager.getADTable(0L, "PURPurchaseOrderLine");
			adTablePoLine = entityManager.getADTableDeep(adTablePoLine.getObjectRrn());
			return adTablePoLine;
		} catch (Exception e) {
		}
		return null;
	}
	
	protected ADTable getADTableOfPO() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTablePoLine = entityManager.getADTable(0L, "PURPurchaseOrder");
			adTablePoLine = entityManager.getADTableDeep(adTablePoLine.getObjectRrn());
			return adTablePoLine;
		} catch (Exception e) {
		}
		return null;
	}
	
	
	
	
	
	
	
	
}

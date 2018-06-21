package com.graly.erp.pur.po.copyfrom;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PRLinesDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(PRLinesDialog.class);
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 350;
	private static final String TABLE_NAME = "PURRequisitionLine";

	protected IManagedForm form;
	
	protected ADTable adTable;
	protected Requisition pr;
	protected RequisitionLine prLine;	
	protected PRLineMasterForm masterForm;
	protected ApprovedPRListDialog approvedPRDialog;
	
	public PRLinesDialog(Shell parent) {
        super(parent);
    }
	
	public PRLinesDialog(Shell parent, Requisition pr,
			ApprovedPRListDialog approvedPRDialog, IManagedForm managedForm){
		this(parent);
		this.pr = pr;
		this.approvedPRDialog = approvedPRDialog;
		this.form = managedForm;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
        setTitleImage(SWTResourceCache.getImage("bomtitle"));
        try{
        	getADTableOfRequisitionLine();
			String editorTitle = String.format(Message.getString("common.editor"),
					I18nUtil.getI18nMessage(adTable, "label"));
			setTitle(editorTitle);
		} catch (Exception e){
		}
		FormToolkit toolkit = form.getToolkit();
		Composite content = toolkit.createComposite(composite, SWT.NULL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        content.setLayoutData(gd);
        content.setLayout(new GridLayout(1, true));

        createFormContent(content);
		
        return composite;
	}
	
	protected void createFormContent(Composite content) {
		masterForm = new PRLineMasterForm(content, SWT.NULL, adTable, pr, this);
        masterForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	protected void prLineChanaged(RequisitionLine prLine) {
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			try {
				List<RequisitionLine> prLines = masterForm.getSelectedPRLines();
				if(prLines == null || prLines.size() == 0) {
					return;
				}
				PurchaseOrder po = approvedPRDialog.getPo();
				// 设置pr仓库为po仓库，因为pr.warehouse为空，在此设置无意义
//				po.setWarehouseId(pr.getWarehouseId());
//				po.setWarehouseRrn(pr.getWarehouseRrn());
				PURManager pudManager = Framework.getService(PURManager.class);
				po = pudManager.createPOFromPR(po,
						prLines, Env.getUserRrn());
				
				ADManager adManager = Framework.getService(ADManager.class);
				po = (PurchaseOrder)adManager.getEntity(po);
				
				if(po.getPurchaser() == null || po.getPurchaser() == ""){
					for (RequisitionLine prLine : prLines) {
						if(prLine.getPurchaser() != null){
							po.setPurchaser(prLine.getPurchaser());
							adManager.saveEntity(po, Env.getUserRrn());
						}
					}
				}
					
				po = (PurchaseOrder)adManager.getEntity(po);
				approvedPRDialog.setPo(po);
			} catch(Exception e) {
				logger.error("Error at PRLineMasterForm : buttonPressed" + e);
				ExceptionHandlerManager.asyncHandleException(e);
//				UI.showError(Message.getString("common.save_failure"));
				return;
			}
			UI.showInfo(Message.getString("pur.pr_to_po_success"));
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	
	protected ADTable getADTableOfRequisitionLine() {
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
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		Message.getString("pur.to_po"), false);
        createButton(parent, IDialogConstants.CANCEL_ID,
        		Message.getString("common.cancel"), false);
    }
}

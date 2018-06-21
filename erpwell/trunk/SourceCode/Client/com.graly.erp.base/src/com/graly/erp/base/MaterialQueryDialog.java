package com.graly.erp.base;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Documentation;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Message;

public class MaterialQueryDialog extends EntityQueryDialog {

	Text txtMaterialId;
	String docType;
	
	public MaterialQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh, String docType) {
		super(parent, tableManager, refresh);
		this.docType = docType;
	}		
	
	@Override
	protected void createDialogForm(Composite parent) {
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Composite body = toolkit.createComposite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 10;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.createLabel(body, Message.getString("pdm.material_id"));
		txtMaterialId = toolkit.createText(body, "");
		txtMaterialId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		queryForm = new MaterialQueryForm(parent, SWT.BORDER, tableManager.getADTable());
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public void createWhereClause() {
		super.createWhereClause();
		if (sb != null && sb.toString().trim().length() != 0) {
			String materialId = txtMaterialId.getText().trim();
			String sqlMaterial = "";
			if (Documentation.DOCTYPE_REC.equals(docType)) {
				//收货
				if (materialId != null && materialId.trim().length() != 0) {
					sqlMaterial = " objectRrn IN (SELECT receiptRrn FROM ReceiptLine ReceiptLine WHERE materialRrn IN (SELECT objectRrn FROM Material Material where materialId LIKE '" + materialId + "') )";
					sb.append(" AND " + sqlMaterial);
				}
			}else if (Documentation.DOCTYPE_IQC.equals(docType)) {
				//收货检验
				if (materialId != null && materialId.trim().length() != 0) {
					sqlMaterial = " objectRrn IN (SELECT iqcRrn FROM IqcLine IqcLine WHERE materialRrn IN (SELECT objectRrn FROM Material Material where materialId LIKE '" + materialId + "') )";
					sb.append(" AND " + sqlMaterial);
				}
			}else if (Documentation.DOCTYPE_TPO.equals(docType)) {
				//采购申请
				if (materialId != null && materialId.trim().length() != 0) {
					sqlMaterial = " objectRrn IN (SELECT poRrn FROM PurchaseOrderLine PurchaseOrderLine WHERE materialRrn IN (SELECT objectRrn FROM Material Material where materialId LIKE '" + materialId + "') )";
					sb.append(" AND " + sqlMaterial);
				}
			}else if (Documentation.DOCTYPE_TPR.equals(docType)) {
				//采购订单
				if (materialId != null && materialId.trim().length() != 0) {
					sqlMaterial = " objectRrn IN (SELECT requisitionRrn FROM RequisitionLine RequisitionLine WHERE materialRrn IN (SELECT objectRrn FROM Material Material where materialId LIKE '" + materialId + "') )";
					sb.append(" AND " + sqlMaterial);
				}
			}else if (Documentation.DOCTYPE_PIN.equals(docType) || Documentation.DOCTYPE_OIN.equals(docType)
					|| Documentation.DOCTYPE_RIN.equals(docType) || Documentation.DOCTYPE_SOU.equals(docType)) {
				//采购入库、其他入库、退货入库、销售出库 和 其他出库---使用的是com.graly.erp.inv.in.MaterialWCAndInvoiceQueryDialog
			}else if (Documentation.DOCTYPE_WIN.equals(docType)) {
				//生产入库
				if (materialId != null && materialId.trim().length() != 0) {
					sqlMaterial = " objectRrn IN (SELECT movementRrn FROM MovementLine MovementLine WHERE materialRrn IN (SELECT objectRrn FROM Material Material where materialId LIKE '" + materialId + "') )";
					sb.append(" AND " + sqlMaterial);
				}
			}else if (Documentation.DOCTYPE_TRF.equals(docType)) {
				//调拨
				if (materialId != null && materialId.trim().length() != 0) {
					sqlMaterial = " docType ='" + docType + "' AND objectRrn IN (SELECT movementRrn FROM MovementLine MovementLine WHERE materialRrn IN (SELECT objectRrn FROM Material Material where materialId LIKE '" + materialId + "') )";
					sb.append(" AND " + sqlMaterial);
				}
			}
		}
	}
}

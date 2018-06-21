package com.graly.erp.inv.in;

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
import com.graly.framework.base.ui.util.Message;

public class MaterialWCAndInvoiceQueryDialog extends WCAndInvoiceQueryDialog {
	Text txtMaterialId;
	String docType;
	
	public MaterialWCAndInvoiceQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh irefresh,String docType, boolean flag) {
		super(parent, tableManager, irefresh, flag);
		this.docType = docType;
	}

	public MaterialWCAndInvoiceQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh irefresh, String docType) {
		this(parent, tableManager, irefresh, docType, false);
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
		queryForm = new MoreQueryForm(parent, SWT.NONE, tableManager.getADTable());
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public void createWhereClause() {
		super.createWhereClause();
		if (sb != null && sb.toString().trim().length() != 0) {
			String materialId = txtMaterialId.getText();
			String sqlMaterial = " docType = '" + docType + "'";
			if (Documentation.DOCTYPE_PIN.equals(docType) || Documentation.DOCTYPE_SOU.equals(docType)
					|| Documentation.DOCTYPE_OOU.equals(docType) || Documentation.DOCTYPE_ADOU.equals(docType)
					|| Documentation.DOCTYPE_AOU.equals(docType)) {
				//采购入库、销售出库 和 其他出库 和 营运调整出库
				if (materialId != null && materialId.trim().length() != 0) {
					sqlMaterial += " AND objectRrn IN (SELECT movementRrn FROM MovementLine MovementLine WHERE materialRrn IN (SELECT objectRrn FROM Material Material where materialId LIKE '" + materialId + "') )";
				}
			}else if (Documentation.DOCTYPE_OIN.equals(docType) || Documentation.DOCTYPE_RIN.equals(docType) 
				|| Documentation.DOCTYPE_ADIN.equals(docType)) {
				//其他入库和退货入库 和 营运入库调整
				if (materialId != null && materialId.trim().length() != 0) {
					sqlMaterial += " AND objectRrn IN (SELECT movementRrn FROM MovementLine MovementLine WHERE materialRrn IN (SELECT objectRrn FROM Material Material where materialId LIKE '" + materialId + "') )";
				}
			}
			sb.append(" AND " + sqlMaterial);
		}
	}
	
}

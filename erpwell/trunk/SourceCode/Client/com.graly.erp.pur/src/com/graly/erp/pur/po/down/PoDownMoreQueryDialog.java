package com.graly.erp.pur.po.down;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.MaterialQueryForm;
import com.graly.erp.base.model.Documentation;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Message;

public class PoDownMoreQueryDialog extends EntityQueryDialog {

	Text txtMaterialId;
	String docType;
	
	public PoDownMoreQueryDialog(Shell parent,
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
				if (true) {
					sqlMaterial = "  not exists (select receipt from Receipt receipt where receipt.poRrn =PurchaseOrder.objectRrn )";
//					sqlMaterial = "  not exists (select null from Receipt Receipt where Receipt.poRrn in  (select objectRrn from PurchaseOrder ))";
					sb.append(" AND " + sqlMaterial);
				}
		}
	}
}

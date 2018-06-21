package com.graly.erp.inv.material.locator;

import com.graly.erp.inv.material.EntityQueryDialog4WC;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.util.UI;

public class MaterialLocatorEntityBlock extends EntityBlock {

	public MaterialLocatorEntityBlock(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog4WC(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
}

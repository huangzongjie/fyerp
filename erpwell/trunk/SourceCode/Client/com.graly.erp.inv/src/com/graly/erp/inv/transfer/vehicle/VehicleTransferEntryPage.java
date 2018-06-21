package com.graly.erp.inv.transfer.vehicle;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.erp.inv.transfer.TransferEntryPage;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class VehicleTransferEntryPage extends TransferEntryPage {
	private static final Logger logger = Logger.getLogger(VehicleTransferEntryPage.class);

	public VehicleTransferEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}
	
	public VehicleTransferEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		try {
			masterSection = new VehicleTransferSection(new EntityTableManager(adTable));
			masterSection.setWhereClause(" docStatus in ('APPROVED', 'DRAFTED') and trsType = '车辆领料' ");
		} catch(Exception e) {
			logger.error("Error at VehicleTransferEntryPage : createSection()", e);
		}
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		((EntityEditor)this.getEditor()).setEditorTitle("车辆领料管理");
	}
}

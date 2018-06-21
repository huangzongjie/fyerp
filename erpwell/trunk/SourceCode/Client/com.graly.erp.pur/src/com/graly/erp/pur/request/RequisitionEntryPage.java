package com.graly.erp.pur.request;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.forms.EntitySection;

public class RequisitionEntryPage extends SectionEntryPage {
	
	private static final Logger logger = Logger.getLogger(RequisitionEntryPage.class);
	
	public RequisitionEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public RequisitionEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new RequisitionSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('DRAFTED','APPROVED') and updated > add_Months(sysdate,-1)");
	}

}

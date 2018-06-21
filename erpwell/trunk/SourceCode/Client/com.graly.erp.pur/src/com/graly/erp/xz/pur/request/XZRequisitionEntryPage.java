package com.graly.erp.xz.pur.request;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class XZRequisitionEntryPage extends SectionEntryPage {
	
	private static final Logger logger = Logger.getLogger(XZRequisitionEntryPage.class);
	
	public XZRequisitionEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public XZRequisitionEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new XZRequisitionSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('DRAFTED','APPROVED') and updated > add_Months(sysdate,-3)" +
				" AND description is null");//description记录是否生成领用单
	}

}

package com.graly.erp.ppm.saleplan.temp.prepare2;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class TpsPrepareEntryPage2 extends SectionEntryPage {
	
	private static final Logger logger = Logger.getLogger(TpsPrepareEntryPage2.class);
	
	public TpsPrepareEntryPage2(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public TpsPrepareEntryPage2(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new TpsPrepareSection2(new EntityTableManager(adTable));
		masterSection.setWhereClause(" tps_status = 'DRAFTED' ");
	}
}

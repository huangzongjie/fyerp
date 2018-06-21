package com.graly.erp.wip.workcenter.schedule.purchase2;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class PurchaseMaterialEntryPage extends SectionEntryPage {

	public PurchaseMaterialEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public PurchaseMaterialEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {//4+4用起来后，3+3就没必要进行使用
		if(adTable!=null && "PmcPurResult".equals(adTable.getName())){
			masterSection = new PurchaseMaterialMainSection2(new EntityTableManager(adTable));
			masterSection.setWhereClause("");
		}else{
			masterSection = new PurchaseMaterialMainSection(new EntityTableManager(adTable));
			masterSection.setWhereClause("");
		}
	}

}

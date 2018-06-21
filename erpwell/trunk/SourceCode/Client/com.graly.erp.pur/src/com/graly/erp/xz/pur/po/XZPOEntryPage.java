package com.graly.erp.xz.pur.po;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.ui.util.Env;

public class XZPOEntryPage extends SectionEntryPage {
	public static final String FIELD_NAME_TOTAL = "total"; // 行总价
	
	private static String	AUTHORITY_UNITPRICE	= "PUR.PoLine.UnitPrice";
	
	public XZPOEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public XZPOEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		if(!Env.getAuthority().contains(AUTHORITY_UNITPRICE)){//如果没有价格权限，则无法看到单价和行总价
			for(ADField f : adTable.getFields()){
				if(FIELD_NAME_TOTAL.equals(f.getName())){
					f.setIsMain(false);
				}
			}
		}
		masterSection = new XZPOSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('DRAFTED','APPROVED') and updated > add_Months(sysdate,-1)");
	}
}

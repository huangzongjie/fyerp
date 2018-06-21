package com.graly.erp.bj.pur.po;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.ui.util.Env;

public class BJPOEntryPage extends SectionEntryPage {
	public static final String FIELD_NAME_TOTAL = "total"; // ���ܼ�
	
	private static String	AUTHORITY_UNITPRICE	= "PUR.PoLine.UnitPrice";
	
	public BJPOEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public BJPOEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		if(!Env.getAuthority().contains(AUTHORITY_UNITPRICE)){//���û�м۸�Ȩ�ޣ����޷��������ۺ����ܼ�
			for(ADField f : adTable.getFields()){
				if(FIELD_NAME_TOTAL.equals(f.getName())){
					f.setIsMain(false);
				}
			}
		}
		masterSection = new BJPOSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('DRAFTED','APPROVED') and updated > add_Months(sysdate,-1)");
	}
}

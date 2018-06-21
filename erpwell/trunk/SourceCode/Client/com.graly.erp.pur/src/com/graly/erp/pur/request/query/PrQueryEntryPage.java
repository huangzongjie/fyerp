package com.graly.erp.pur.request.query;

import java.util.List;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.ui.util.Env;

public class PrQueryEntryPage extends SectionEntryPage {
	private static final String KEY_PR_QUERY_LINETOTAL = "PR.QUERY.LineTotal";//行总价权限
	public PrQueryEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public PrQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		List<ADField> fields = adTable.getFields();
		boolean lineTotal = false;//行总价
		if(fields!=null && fields.size()>0){
			if (Env.getAuthority() != null) {
				if (Env.getAuthority().contains(KEY_PR_QUERY_LINETOTAL)) {
					lineTotal = true;
				}
			}
			for(ADField adField :fields){
				if(adField.getName()!=null && "lineTotal".equals(adField.getName())){
					if(!lineTotal){
						adField.setIsMain(false);
					}
				}
			}
		}
		masterSection = new PrQuerySection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" 1 <> 1 ");
	}

}

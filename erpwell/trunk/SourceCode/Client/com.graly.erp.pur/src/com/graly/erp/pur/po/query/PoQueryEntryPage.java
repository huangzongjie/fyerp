package com.graly.erp.pur.po.query;

import java.util.List;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.ui.util.Env;

public class PoQueryEntryPage extends SectionEntryPage {
	private static final String KEY_PO_QUERY_LINETOTAL = "PO.QUERY.LineTotal";//行总价权限
	private static final String KEY_PO_QUERY_UNITPRICE = "PO.QUERY.UnitPrice";//单价权限
	
	public PoQueryEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public PoQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		List<ADField> fields = adTable.getFields();
		boolean lineTotal = false;//行总价
		boolean unitPrice = false;//单价
		if(fields!=null && fields.size()>0){
			if (Env.getAuthority() != null) {
				if (Env.getAuthority().contains(KEY_PO_QUERY_LINETOTAL)) {
					lineTotal = true;
				}
				if(Env.getAuthority().contains(KEY_PO_QUERY_UNITPRICE)){
					unitPrice = true;
				}
			}
			for(ADField adField :fields){
				if(adField.getName()!=null && "unitPrice".equals(adField.getName())){
					if(!unitPrice){
						adField.setIsMain(false);
					}
				}
				if(adField.getName()!=null && "lineTotal".equals(adField.getName())){
					if(!lineTotal){
						adField.setIsMain(false);
					}
				}
			}
		}
		masterSection = new PoQuerySection(new PoQueryTableManager(adTable));
		masterSection.setWhereClause(" 1 <> 1 ");
	}
}
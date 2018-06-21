package com.graly.erp.xz.inv.outother;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.SearchField;

/**
 * ÷ÿ–¥SearchField
 * */
public class XZSearchField extends SearchField {

	public XZSearchField(String id, ADTable adTable, ADRefTable refTable,
			String whereClause, int style) {
		super(id, adTable, refTable, whereClause, style);
	}

	public XZSearchField(String id, TableViewer viewer, int style) {
		super(id, viewer, style);
	}
	
    protected SelectionListener getSelectionListener() {
    	return new SelectionAdapter() {
    		public void widgetSelected(SelectionEvent e) {
    			listTableManager = new TableListManager(adTable);
    			int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
    			| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
    			
    			
    			XZSingleEntityQueryDialog singleDialog = new XZSingleEntityQueryDialog(
    					listTableManager, null, whereClause, style);
//    			StringBuffer likeWhereClause = new StringBuffer();
//    			if(createLikeWhereClause()==null){
//        			likeWhereClause.append(createLikeWhereClause());
//        			likeWhereClause.append(" AND BJWipEquipment.equipmentId <> 'test'");
//    			}else{
//        			likeWhereClause.append("  BJWipEquipment.equipmentId <> 'test'");
//    			}

//    			String setLikeWhereClause = likeWhereClause.toString();
    			// BJWipEquipment.equipmentId LIKE 'K-01-B-133%'
    			
//    			singleDialog.setTempSearchCondition(setLikeWhereClause);
    			singleDialog.setTempSearchCondition(createLikeWhereClause());
    			if(singleDialog.open() == IDialogConstants.OK_ID) {
    				ADBase adBase = singleDialog.getSelectionEntity();
    				if(adBase != null && adBase.getObjectRrn() != null) {
    					setKey(adBase.getObjectRrn().toString(), adBase);
    				}
    				refresh();
    				setFocus();
    			}
    		}
    	};    		
    }
	
}

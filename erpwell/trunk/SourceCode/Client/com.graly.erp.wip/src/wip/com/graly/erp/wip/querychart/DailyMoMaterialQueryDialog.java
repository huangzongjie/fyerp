package com.graly.erp.wip.querychart;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.AdvanceQueryTray;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class DailyMoMaterialQueryDialog extends ExtendDialog {
	private InnerQueryDialog queryDialog;
	private EntityTableManager tableManager;
	
	public DailyMoMaterialQueryDialog() {
		super();
		queryDialog = new InnerQueryDialog(UI.getActiveShell());
	}
	
	@Override
	public int open() {
		return queryDialog.open();
	}

	@Override
	public void setTableId(String tableId) {
		super.setTableId(tableId);
		if(queryDialog.getTableManager() == null){
			ADManager manager;
			try {
				manager = Framework.getService(ADManager.class);
				tableManager = new EntityTableManager(manager.getADTableDeep(Long.valueOf(tableId)));
				queryDialog.setTableManager(tableManager);
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}

		}
	}
	
	@Override
    protected void okPressed() {
		if(getParent() != null){
			if(getParent() instanceof EntityEditor){
				EntityEditor editor = (EntityEditor) getParent();
				if(editor.getActivePageInstance() instanceof ChartEntryPage){
					ChartEntryPage page = (ChartEntryPage) editor.getActivePageInstance();
					ChartSection section = page.getMasterSection();
					queryDialog.setIRefresh(section);
					
					section.setQueryDialog(queryDialog);
				}
			}
		}
    }
	
	class InnerQueryDialog extends EntityQueryDialog{
		
		public InnerQueryDialog(Shell parent) {
			super(parent);
		}

		@Override
		protected void okPressed() {
			DailyMoMaterialQueryDialog.this.okPressed();
			super.okPressed();
		}	
		
		public EntityTableManager getTableManager(){
			return super.tableManager;
		}
		
		public void setTableManager(EntityTableManager tableManager){
			super.tableManager = tableManager;
		}
		
//		@Override
//		public void createWhereClause() {
//			LinkedHashMap<String, IField> fields = queryForm.getFields();
////			String modelName = tableManager.getADTable().getModelName() + ".";
//			String modelName = "";
//			sb = new StringBuffer("");
//			
//			sb.append(" 1=1 ");
//					
//	        for(IField f : fields.values()) {
//				Object t = f.getValue();
//				String fieldId = f.getId();
//				if("materialRrn".equalsIgnoreCase(fieldId.trim())){
//					fieldId = "MATERIAL_RRN";
//				}else{
//					return;
//				}
//				if (t instanceof Date) {
//					Date cc = (Date)t;
//					if(cc != null) {
//						sb.append(" AND ");
//						sb.append("TO_CHAR(");
//						sb.append(modelName);
//						sb.append(fieldId);
//						if(FieldType.SHORTDATE.equals(f.getFieldType())){
//							sb.append(", '" + I18nUtil.getShortDatePattern() + "') = '");
//							sb.append(I18nUtil.formatShortDate(cc));
//						}else{
//							sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
//							sb.append(I18nUtil.formatDate(cc));
//						}
//						sb.append("'");
//					}
//				} else if(t instanceof String) {
//					String txt = (String)t;
//					if(!txt.trim().equals("") && txt.length() != 0) {
//						sb.append(" AND ");
//						sb.append(modelName);
//						sb.append(fieldId);
//						sb.append(" LIKE '");
//						sb.append(txt);
//						sb.append("'");
//					}
//				} else if(t instanceof Boolean) {
//					 Boolean bl = (Boolean)t;
//					 sb.append(" AND ");
//					 sb.append(modelName);
//					 sb.append(fieldId);
//					 sb.append(" = '");
//					 if(bl) {
//						sb.append("Y");
//					 } else if(!bl) {
//						sb.append("N");
//					 }
//					 sb.append("'");
//				} else if(t instanceof Long) {
//					long l = (Long)t;
//					sb.append(" AND ");
//					sb.append(modelName);
//					sb.append(fieldId);
//					sb.append(" = " + l + " ");
//				} else if(t instanceof Map){//只可能是FromToCalendarField
//					Map m = (Map)t;
//					Date from = (Date) m.get(FromToCalendarField.DATE_FROM);
//					Date to = (Date) m.get(FromToCalendarField.DATE_TO);
//					if(from != null) {
//						sb.append(" AND trunc(");
//						sb.append(modelName);
//						sb.append(fieldId);
//						sb.append(") ");
//						if(to != null){
//							sb.append("BETWEEN TO_DATE('" + I18nUtil.formatDate(from) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
//							sb.append("AND TO_DATE('" + I18nUtil.formatDate(to) + "', '" + I18nUtil.getDefaultDatePattern() + "') ");
//						}else{
//							sb.append("> TO_DATE('" + I18nUtil.formatDate(from) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
//						}
//					}
//				}
//	        }
//	        if (getTray() != null) {
//	        	AdvanceQueryTray tray = (AdvanceQueryTray)this.getTray();
//	        	String advance = tray.getAdvaceWhereClause();
//	        	sb.append(advance);
//	        }
//		}
	}

	public Map<String, Object> getQueryKeys() {
		return queryDialog.getQueryKeys();
	}

	public void setQueryKeys(Map<String, Object> queryKeys) {
		queryDialog.setQueryKeys(queryKeys);
	}
}

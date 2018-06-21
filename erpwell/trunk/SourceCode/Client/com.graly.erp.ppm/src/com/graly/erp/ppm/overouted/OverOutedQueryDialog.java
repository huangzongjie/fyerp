package com.graly.erp.ppm.overouted;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
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

public class OverOutedQueryDialog extends ExtendDialog {
	private InnerQueryDialog queryDialog;
	
	public OverOutedQueryDialog() {
		super();
		queryDialog = new InnerQueryDialog(UI.getActiveShell());
	}

	public OverOutedQueryDialog(Shell parent) {
		super(parent);
	}

	public OverOutedQueryDialog(String tableId, Object parent) {
		super(tableId, parent);
	}

	@Override
	public int open() {
		return queryDialog.open();
	}

	@Override
	public void setTableId(String tableId) {
		super.setTableId(tableId);
		ADManager manager;
		try {
			manager = Framework.getService(ADManager.class);
			EntityTableManager tableManager = new EntityTableManager(manager.getADTable(Long.valueOf(getTableId())));
			queryDialog.setTableManager(tableManager);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	@Override
    protected void okPressed() {
		if(getParent() != null){
			if(getParent() instanceof EntityEditor){
				EntityEditor editor = (EntityEditor) getParent();
				if(editor.getActivePageInstance() instanceof SectionEntryPage){
					SectionEntryPage page = (SectionEntryPage) editor.getActivePageInstance();
					MasterSection section = page.getMasterSection();
					queryDialog.setIRefresh(section);
					
					section.setQueryDialog(queryDialog);
				}
			}
		}
    }
	
	class InnerQueryDialog extends EntityQueryDialog{
		private final Logger logger = Logger.getLogger(InnerQueryDialog.class);
		
		ADTable adTable;
		String month;
		public InnerQueryDialog(Shell parent) {
			super(parent);
		}
		
		public InnerQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public InnerQueryDialog(Shell parent,
				ADTable adTable, IRefresh refresh) {
			super(parent, null, refresh);
			this.adTable = adTable;
		}

		@Override
		protected void createAdvanceButtonBar(Composite parent) {}
		
		@Override
		protected void okPressed() {
			OverOutedQueryDialog.this.okPressed();
			createWhereClause();
			setReturnCode(OK);
			iRefresh.setWhereClause(sb.toString());
			iRefresh.refresh();
			this.setVisible(false);
		}
		
		public EntityTableManager getTableManager(){
			return super.tableManager;
		}
		
		public void setTableManager(EntityTableManager tableManager){
			super.tableManager = tableManager;
		}
		
		public ADTable getADTable() {
			if(tableManager != null)
				return tableManager.getADTable();
			return adTable;
		}
		
		@Override
		public void createWhereClause() {
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			String modelName = tableManager.getADTable().getModelName() + ".";
			sb = new StringBuffer("");
			
			sb.append(" 1=1 ");
					
	        for(IField f : fields.values()) {
				Object t = f.getValue();
				if("dateDelivered".equals(f.getId())){//dateDelivered字段只用来传递参数，不作为查询条件
					return;
				}
				if (t instanceof Date) {
					Date cc = (Date)t;
					Class<?> clazz = null;
					Field objProperty = null;
					try {
						clazz = Class.forName(tableManager.getADTable().getModelClass());
						if(clazz != null){
							objProperty = clazz.getDeclaredField(f.getId());
							Field[] fs = clazz.getDeclaredFields();
							assert fs.length != 0;
						}
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
					if(objProperty != null){
						if(objProperty.getType().equals(String.class)){
							//如果对象的属性是String类型的
							if(cc != null) {
								sb.append(" AND ");
								sb.append(modelName);
								sb.append(f.getId());
								if(FieldType.SHORTDATE.equals(f.getFieldType())){
									sb.append(" = '");
									sb.append(I18nUtil.formatShortDate(cc));
								}else{
									sb.append(" = '");
									sb.append(I18nUtil.formatDate(cc));
								}
								sb.append("'");
							}
						}else if(objProperty.getType().equals(Date.class)){
							//如果对象的属性是Date类型的
							if(cc != null) {
								sb.append(" AND ");
								sb.append("TO_CHAR(");
								sb.append(modelName);
								sb.append(f.getId());
								if(FieldType.SHORTDATE.equals(f.getFieldType())){
									sb.append(", '" + I18nUtil.getShortDatePattern() + "') = '");
									sb.append(I18nUtil.formatShortDate(cc));
								}else{
									sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
									sb.append(I18nUtil.formatDate(cc));
								}
								sb.append("'");
							}
						}
					}else{
						if(cc != null) {
							sb.append(" AND ");
							sb.append("TO_CHAR(");
							sb.append(modelName);
							sb.append(f.getId());
							if(FieldType.SHORTDATE.equals(f.getFieldType())){
								sb.append(", '" + I18nUtil.getShortDatePattern() + "') = '");
								sb.append(I18nUtil.formatShortDate(cc));
							}else{
								sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
								sb.append(I18nUtil.formatDate(cc));
							}
							sb.append("'");
						}
					}
				} else if(t instanceof String) {
					String txt = (String)t;
					if(!txt.trim().equals("") && txt.length() != 0) {
						sb.append(" AND ");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(" LIKE '");
						sb.append(txt);
						sb.append("'");
					}
				} else if(t instanceof Boolean) {
					 Boolean bl = (Boolean)t;
					 sb.append(" AND ");
					 sb.append(modelName);
					 sb.append(f.getId());
					 sb.append(" = '");
					 if(bl) {
						sb.append("Y");
					 } else if(!bl) {
						sb.append("N");
					 }
					 sb.append("'");
				} else if(t instanceof Long) {
					long l = (Long)t;
					sb.append(" AND ");
					sb.append(modelName);
					sb.append(f.getId());
					sb.append(" = " + l + " ");
				} else if(t instanceof Map){//只可能是FromToCalendarField
					Map m = (Map)t;
					Date from = (Date) m.get(FromToCalendarField.DATE_FROM);
					Date to = (Date) m.get(FromToCalendarField.DATE_TO);
					if(from != null) {
						sb.append(" AND trunc(");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(") >= TO_DATE('" + I18nUtil.formatDate(from) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
					
					if(to != null){
						sb.append(" AND trunc(");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(") <= TO_DATE('" + I18nUtil.formatDate(to) + "', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
				}
	        }
	        if (getTray() != null) {
	        	AdvanceQueryTray tray = (AdvanceQueryTray)this.getTray();
	        	String advance = tray.getAdvaceWhereClause();
	        	sb.append(advance);
	        }
		}
		
		public String getMonth(){
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			
	        for(IField f : fields.values()) {
	        	if("dateDelivered".equals(f.getId())){//用dateDelivered字段做为查询条件
	        		Object value = f.getValue();
	        		if(value instanceof Date){
	        			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	        			month = sdf.format(value);
	        		}
	        	}
	        }
			return month;
		}
}

}

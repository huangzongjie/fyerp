package com.graly.erp.wip.lothis;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.AdvanceQueryTray;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.base.ui.util.UI;

/**
 * 物料价格曲线报表  采购部唐佳颖提出
 * 同时为了系统性能应该添加一个时间段
 * */
public class MoHisReportDialog extends EntityQueryDialog{
	private String reportName;
	protected ADTable printADTable;
	public MoHisReportDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh) {
		super(parent, tableManager, refresh);
	}
	
	@Override
	protected void okPressed() {
		if(!validateQueryKey()) return;
		fillQueryKeys();
		createWhereClause();
		setReturnCode(OK);
        this.setVisible(false);
        try {
        	HashMap<String, Object> params = new HashMap<String, Object>();
    		params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
    		
    		HashMap<String, String> userParams = new HashMap<String, String>();
    		Map<String, Object> keys = this.getQueryKeys();
    		for(String name : keys.keySet()){
    			if("approvedDate".equals(name)){
    				Map approvedDate = (Map) keys.get(name);
    				Date fromDate = null;
    				Date toDate = null;
    				if(approvedDate != null){
    					fromDate = (Date) approvedDate.get(FromToCalendarField.DATE_FROM);
    					toDate = (Date) approvedDate.get(FromToCalendarField.DATE_TO);
    				}
    				if(fromDate != null){
    					userParams.put("PRODUCT_STARTDATE", I18nUtil.formatDate(fromDate));
    				}
    				
    				if(toDate != null){
    					userParams.put("PRODUCT_ENDDATE", I18nUtil.formatDate(toDate));
    				}
    			}	
    		}

			
    		
    		String parms = convertParams(userParams);
    	    
    		PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), reportName, params, userParams);
    		dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected boolean validateQueryKey() {
		setMessage(Message.getString("common.keys"));
		//如果栏位名以*结尾说明是必填项，则验证之
		LinkedHashMap<String, IField> fields = queryForm.getFields();
		for(IField f : fields.values()) {
			if(f.getLabel() != null){
				if(f.getLabel().trim().endsWith("*")){
					Map approvedDate = (Map) f.getValue();
					if(approvedDate ==null){
						setMessage("开始和结束日期不能为空");
						return false;
					}else{
						Date fromDate = null;
	    				Date toDate = null;
	    				fromDate = (Date) approvedDate.get(FromToCalendarField.DATE_FROM);
	    				toDate = (Date) approvedDate.get(FromToCalendarField.DATE_TO);
	    				if(fromDate == null){
							setMessage("开始和结束日期不能为空");
							return false;
	    				}
	    				if(toDate == null){
							setMessage("开始和结束日期不能为空");
							return false;
	    				}
					}
				}
			}
		}
		return true;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public void createWhereClause() {
		LinkedHashMap<String, IField> fields = queryForm.getFields();
		String modelName = tableManager.getADTable().getModelName() + ".";
		sb = new StringBuffer("");
		
		sb.append(" 1=1 ");
				
        for(IField f : fields.values()) {
			Object t = f.getValue();
			if (t instanceof Date) {
				Date cc = (Date)t;
				Class<?> clazz = null;
				Field objProperty = null;
				try {
					clazz = Class.forName(printADTable.getModelClass());
					if(clazz != null){
						objProperty = clazz.getDeclaredField(f.getId());
						Field[] fs = clazz.getDeclaredFields();
						assert fs.length != 0;
					}
				} catch (Exception e) {
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
	
	
	private static String convertParams(Map<String, String> params) {
		if (params != null && !params.isEmpty()) {
			StringBuffer sb = new StringBuffer();

			for (Entry<String, String> entry : params.entrySet()) {
				sb.append("&").append(entry.getKey()); 

				if (entry.getValue() != null) {
					sb.append("=").append(entry.getValue()); 
				}
			}

			return sb.toString();
		}
		return ""; 
	}
}

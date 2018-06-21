package com.graly.erp.wip.workcenter.schedule.purchase2;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.AdvanceQueryTray;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class PurQueryDialog extends EntityQueryDialog {
	private static final Logger logger = Logger.getLogger(PurQueryDialog.class);
	Text txtMaterialId;
	
	public PurQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh) {
		super(parent, tableManager, refresh);
	}		
	
	@Override
	protected void createDialogForm(Composite parent) {
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Composite body = toolkit.createComposite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 10;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
//		toolkit.createLabel(body, Message.getString("pdm.material_id"));
//		txtMaterialId = toolkit.createText(body, "");
//		txtMaterialId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		queryForm = new QueryForm(parent, SWT.BORDER, tableManager.getADTable());
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public void createWhereClause() {
		if("RepScheResult2".equals(tableManager.getADTable().getName())){
			super.createWhereClause();
			
			int i = sb.indexOf("VRepScheResult.tenQty");
			if(i>0){
				i=0;
				String[] sfs = sb.toString().split("AND");
				StringBuffer sb2 = new StringBuffer();
				for(String sf1 :sfs){
					if(sf1.indexOf("tenQty")>0 ){
						sf1 = sf1.replace("LIKE", "");
						sf1 = sf1.replace("\'", "");
					}
					if(i>0){
						sb2.append(" AND "+sf1);
					}else{
						sb2.append(sf1);
					}
					i++;
				}
				sb = sb2;
				
			}
		}else if("PmcPurResult".equals(tableManager.getADTable().getName())){
			createWhereClause2();
		}
		
	}
	
	public void createWhereClause2() {
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
				if(!txt.trim().equals("") && txt.length() != 0){
					if(txt.indexOf(">") >=0 || txt.indexOf("<")>=0 || txt.indexOf("=") >=0 ){
						sb.append(" AND ");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(txt);

					}else{
						sb.append(" AND ");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(" LIKE '");
						sb.append(txt);
						sb.append("'");
					}
					
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
}

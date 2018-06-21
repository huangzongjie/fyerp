package com.graly.erp.inv.material.nomove;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.AdvanceQueryTray;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.forms.field.CalendarField;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class NoMoveDialog extends ExtendDialog {
	private static String ID_DATEAPPROVED = "dateApproved";
	private static String ID_WAREHOUSE = "warehouseRrn";
	
	private NoMoveQueryDialog queryDialog;
	private EntityTableManager tableManager;
	protected boolean isCreateQuery;
	
	public NoMoveDialog() {
		super();
		queryDialog = new NoMoveQueryDialog(UI.getActiveShell());
	}
	
	public NoMoveDialog(boolean isCreateQuery) {
		this.isCreateQuery = isCreateQuery;
	}

	@Override
	public int open() {
		int id = queryDialog.open();
		setQueryDialogToSection();
		return id;
	}

	// 调用该方法必须保证tableId不为空
	protected EntityTableManager getEntityTableManager() {
		ADManager manager;
		try {
			manager = Framework.getService(ADManager.class);
			tableManager = new EntityTableManager(manager.getADTable(Long.valueOf(getTableId())));
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return tableManager;
	}
	
	protected void setQueryDialogToSection() {
		if(getParent() instanceof EntityEditor){
			EntityEditor editor = (EntityEditor) getParent();
			if(editor.getActivePageInstance() instanceof SectionEntryPage){
				SectionEntryPage page = (SectionEntryPage) editor.getActivePageInstance();
				MasterSection section = page.getMasterSection();
				queryDialog.setIRefresh(section);
				section.setQueryDialog(queryDialog);
				((NoMoveSection)section).setExtendDialog(this);
			}
		}
	}

	public NoMoveQueryDialog getEntityQueryDialog() {
		return this.queryDialog;
	}
	
	public void setEntityQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = (NoMoveQueryDialog)queryDialog;
	}

	protected class NoMoveQueryDialog extends EntityQueryDialog {
		String ModelName = "Material";
		ADTable adTable;
		public NoMoveQueryDialog(Shell parent) {
			super(parent);
		}

		public NoMoveQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public NoMoveQueryDialog(Shell parent,
				ADTable adTable, IRefresh refresh) {
			super(parent, null, refresh);
			this.adTable = adTable;
		}
		
		@Override
	    protected Control createDialogArea(Composite parent) {
	        setTitleImage(SWTResourceCache.getImage("search-dialog"));
	        setTitle(Message.getString("common.search_Title"));
	        setMessage(Message.getString("common.keys"));
	        Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.verticalSpacing = 0;
			layout.horizontalSpacing = 0;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite.setFont(parent.getFont());
			// Build the separator line
			Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL
					| SWT.SEPARATOR);
			titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			if(tableManager == null) {
				setTableManager(getEntityTableManager());
			}
	        queryForm = new NoMoveQueryForm(composite, SWT.NONE, getADTable(), this);
	        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	        return composite;
	    }

		@Override
		protected void createAdvanceButtonBar(Composite parent) {}

		@Override
		protected void okPressed() {
			this.setErrorMessage(null);
			if(queryForm.validate()) {
				NoMoveDialog.this.okPressed();
				// 设置materialRrn到实现IVdmAssess接口
				fillQueryData();
				createWhereClause();
				setReturnCode(OK);
				iRefresh.setWhereClause(sb.toString());
				this.setVisible(false);
				iRefresh.refresh();
			}
		}
		
		protected void fillQueryData() {
			Date dateApproved = null;
			Long warehouseRrn = null;
			HashMap<String, IField> fields = queryForm.getFields();
			if(fields.get(ID_DATEAPPROVED) instanceof CalendarField) {
				CalendarField cf = (CalendarField)fields.get(ID_DATEAPPROVED);
				if(cf.getValue() instanceof Date) {
					dateApproved = (Date)cf.getValue();
				}
			}
			if(fields.get(ID_WAREHOUSE) != null){
				IField fld = fields.get(ID_WAREHOUSE);
				if(fld.getValue() != null && String.valueOf(fld.getValue()).trim().length() > 0){
					warehouseRrn = Long.valueOf((String) fld.getValue());
				}
			}
			if(iRefresh instanceof NoMoveSection) {
				NoMoveSection noMoveSection = (NoMoveSection)iRefresh;
				noMoveSection.setDateApproved(dateApproved);
				noMoveSection.setWarehouseRrn(warehouseRrn);
			}
		}
		
		@Override
		public void createWhereClause() {
			//本方法拼出的sql语句是oracle sql不是hql,不通用
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			String modelName = "M.";//sb会作为参数传给INVManagerBean中的getNoMoveMaterialList方法，故要与其sql语句统一否则不认
			sb = new StringBuffer("");
			
			sb.append(" 1=1 ");
					
	        for(IField f : fields.values()) {
	        	String key = "";
	        	if ("materialId".equals(f.getId())) {
					key = "MATERIAL_ID";
				} else if ("materialCategory1".equals(f.getId())) {
					key = "MATERIAL_CATEGORY1";
				} else if ("materialCategory2".equals(f.getId())) {
					key = "MATERIAL_CATEGORY2";
				} else if ("materialCategory3".equals(f.getId())) {
					key = "MATERIAL_CATEGORY3";
				} else if ("materialCategory4".equals(f.getId())) {
					key = "MATERIAL_CATEGORY4";
				} else {
					continue;
				}
				Object t = f.getValue();
				if (t instanceof Date) {
					Date cc = (Date)t;
					if(cc != null) {
						sb.append(" AND ");
						sb.append("TO_CHAR(");
						sb.append(modelName);
						sb.append(key);
						if(FieldType.SHORTDATE.equals(f.getFieldType())){
							sb.append(", '" + I18nUtil.getShortDatePattern() + "') = '");
							sb.append(I18nUtil.formatShortDate(cc));
						}else{
							sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
							sb.append(I18nUtil.formatDate(cc));
						}
						sb.append("'");
					}
				} else if(t instanceof String) {
					String txt = (String)t;
					if(!txt.trim().equals("") && txt.length() != 0) {
						sb.append(" AND ");
						sb.append(modelName);
						sb.append(key);
						sb.append(" LIKE '");
						sb.append(txt);
						sb.append("'");
					}
				} else if(t instanceof Boolean) {
					 Boolean bl = (Boolean)t;
					 sb.append(" AND ");
					 sb.append(modelName);
					 sb.append(key);
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
					sb.append(key);
					sb.append(" = " + l + " ");
				} else if(t instanceof Map){//只可能是FromToCalendarField
					Map m = (Map)t;
					Date from = (Date) m.get(FromToCalendarField.DATE_FROM);
					Date to = (Date) m.get(FromToCalendarField.DATE_TO);
					if(from != null) {
						sb.append(" AND trunc(");
						sb.append(modelName);
						sb.append(key);
						sb.append(") >= TO_DATE('" + I18nUtil.formatDate(from) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
					if(to != null){
						sb.append(" AND trunc(");
						sb.append(modelName);
						sb.append(key);
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
	}
	
	class NoMoveQueryForm extends QueryForm {
		protected EntityQueryDialog dialog;
		public NoMoveQueryForm(Composite parent, int style, ADTable table, EntityQueryDialog dialog) {
	    	super(parent, style, table);
	    	this.dialog = dialog;
		}
		
		// 重载该方法, 使其提示强制性输入和并可以对其验证
		@Override
		public void addFields() {
			if (allADfields != null && allADfields.size() > 0){
				for (ADField adField : allADfields) {
		    		if (adField.getIsQuery()) {
		    			adField.setIsReadonly(false);
		    			IField field = getField(adField);
		    			if (field == null) {
		    				continue;
		    			}
		    			adFields.put(adField.getName(), adField);
	    				if (field != null) {
	    					field.setADField(adField);
	    				}
		    		}
		    	}
				registeValueChangeListener();
			}
		}
		
		// 重载该方法, 使错误提示信息放Dialog的头部
		@Override
		public boolean validate() {
			boolean validFlag = true;
			for (IField f : fields.values()){
				// 验证强制性输入
				ADField adField = adFields.get(f.getId());
				if(adField != null) {
					if (adField.getIsMandatory()){
						Object value = f.getValue();
						boolean isMandatory = false;
						if (value == null){
							isMandatory = true;
						} else {
							if (value instanceof String){
								if ("".equalsIgnoreCase(value.toString().trim())){
									isMandatory = true;
								}
							}
						}
						if (isMandatory){
							validFlag = false;
							if(dialog != null) {
								dialog.setErrorMessage(
										String.format(Message.getString("common.ismandatory"), I18nUtil.getI18nMessage(adField, "label")));
							}
							return false;
						}
					}
				}
			}
			return validFlag;
		}
	}
}

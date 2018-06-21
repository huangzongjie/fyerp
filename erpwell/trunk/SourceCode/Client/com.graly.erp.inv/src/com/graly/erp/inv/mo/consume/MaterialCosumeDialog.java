package com.graly.erp.inv.mo.consume;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MaterialCosumeDialog extends ExtendDialog {
	public static final String DIALOG_ID = "com.graly.erp.wip.mo.consume.MaterialCosumeDialog";

	private static String ID_MODOCID = "lotId";//通过批号找到MO
	private static String ID_MATERIALID = "materialId";
	private static String ID_DATEINTERVAL = "dateInterval";
	
	private MOQueryDialog queryDialog;
	private EntityTableManager tableManager;
	protected boolean isCreateQuery;
	
	public MaterialCosumeDialog() {
		super();
		queryDialog = new MOQueryDialog(UI.getActiveShell());
	}
	
	public MaterialCosumeDialog(boolean isCreateQuery) {
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
				((MaterialConsumeSection)section).setExtendDialog(this);
			}
		}
	}

	public MOQueryDialog getEntityQueryDialog() {
		return this.queryDialog;
	}
	
	public void setEntityQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = (MOQueryDialog)queryDialog;
	}

	class MOQueryDialog extends EntityQueryDialog {
		ADTable adTable;
		String DatePattern = "'yyyy-MM-dd'";
		public MOQueryDialog(Shell parent) {
			super(parent);
		}

		public MOQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public MOQueryDialog(Shell parent,
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
	        queryForm = new MOQueryForm(composite, SWT.NONE, getADTable(), this);
	        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	        return composite;
	    }

		@Override
		protected void createAdvanceButtonBar(Composite parent) {}

		@Override
		protected void okPressed() {
			this.setErrorMessage(null);
			if(queryForm.validate()) {
				MaterialCosumeDialog.this.okPressed();
				// 设置vendor,materialRrn等数据到实现IVdmAssess接口的对象中
				fillMoIDData();
				super.okPressed();
			}
		}
		
		protected void fillMoIDData() {
			String moId = null;
			Long moRrn = null;
			Date start = null, end = null;
			HashMap<String, IField> fields = queryForm.getFields();
			IField materialIdField = fields.get(ID_MATERIALID);
			String materialId = materialIdField.getValue() == null ? "" : String.valueOf(materialIdField.getValue());
			if(fields.get(ID_MODOCID) instanceof SearchField) {
				SearchField sf = (SearchField)fields.get(ID_MODOCID);
				Object value = sf.getValue();
				if(value != null && !"".equals(String.valueOf(value).trim()))
					moRrn = Long.parseLong(String.valueOf(value));
				if(sf.getData() instanceof ManufactureOrder) {
					ManufactureOrder mo = (ManufactureOrder)sf.getData();
					moId = mo.getDocId();
				}
			}
			if(fields.get(ID_DATEINTERVAL) != null
					&& fields.get(ID_DATEINTERVAL).getValue() instanceof Map) {
				Map<String, Date> invertal = (Map<String, Date>)fields.get(ID_DATEINTERVAL).getValue();
				start = (Date) invertal.get(FromToCalendarField.DATE_FROM);
				end = (Date) invertal.get(FromToCalendarField.DATE_TO);
			}
			MaterialConsumeSection mcs = (MaterialConsumeSection)this.getIRefresh();
			mcs.setMoId(moId);
			mcs.setMoRrn(moRrn);
			mcs.setMaterialId(materialId);
			mcs.setStartDate(start);
			mcs.setEndDate(end);
		}
		
		@Override
		public void createWhereClause() {
			sb = new StringBuffer("");
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
	
	class MOQueryForm extends QueryForm {
		protected EntityQueryDialog dialog;
		public MOQueryForm(Composite parent, int style, ADTable table, EntityQueryDialog dialog) {
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
			// 验证开始日期应早于结束日期
//			Date start = null, end = null;
//			if(fields.get(ID_DATESTART) != null) {
//				start = (Date)fields.get(ID_DATESTART).getValue();
//				if(fields.get(ID_DATEEND) != null) {
//					end = (Date)fields.get(ID_DATEEND).getValue();
//					if(start != null && end != null) {
//						if(start.compareTo(end) > 0) {
//							validFlag = false;
//							dialog.setErrorMessage(Message.getString("vdm.assess_end_before_start"));
//						}
//					}
//				}
//			}
			return validFlag;
		}
	}
}

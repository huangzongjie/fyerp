package com.graly.erp.inv.material.movesum;

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
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MoveSumDialog extends ExtendDialog {
	private static String ID_DATEAPPROVED = "dateApproved";
	private static String ID_WAREHOUSE_RRN = "warehouseRrn";
	private static String ID_MATERIAL_RRN = "materialRrn";

	private MoveSumQueryDialog queryDialog;
	private EntityTableManager tableManager;
	protected boolean isCreateQuery;
	
	public MoveSumDialog() {
		super();
		queryDialog = new MoveSumQueryDialog(UI.getActiveShell());
	}
	
	public MoveSumDialog(boolean isCreateQuery) {
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
				((MoveSumSection)section).setExtendDialog(this);
			}
		}
	}

	public MoveSumQueryDialog getEntityQueryDialog() {
		return this.queryDialog;
	}
	
	public void setEntityQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = (MoveSumQueryDialog)queryDialog;
	}

	protected class MoveSumQueryDialog extends EntityQueryDialog {
		ADTable adTable;
		public MoveSumQueryDialog(Shell parent) {
			super(parent);
		}

		public MoveSumQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public MoveSumQueryDialog(Shell parent,
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
	        queryForm = new MoveSumQueryForm(composite, SWT.NONE, getADTable(), this);
	        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	        return composite;
	    }

		@Override
		protected void createAdvanceButtonBar(Composite parent) {}

		@Override
		protected void okPressed() {
			this.setErrorMessage(null);
			if(queryForm.validate()) {
				MoveSumDialog.this.okPressed();
				// 设置materialRrn到实现IVdmAssess接口
				createWhereClause();
				fillQueryData();
				setReturnCode(OK);
				this.setVisible(false);
				iRefresh.refresh();
			}
		}
		
		protected void fillQueryData() {
			Date start = null, end = null;
			HashMap<String, IField> fields = queryForm.getFields();
			if(fields.get(ID_DATEAPPROVED) instanceof FromToCalendarField) {
				FromToCalendarField ft = (FromToCalendarField)fields.get(ID_DATEAPPROVED);
				if(ft.getValue() instanceof Map) {
					Map<String, Date> m = (Map<String, Date>)ft.getValue();
					start = m.get(FromToCalendarField.DATE_FROM);
					end = m.get(FromToCalendarField.DATE_TO);
				}
			}
			if(iRefresh instanceof MoveSumSection) {
				MoveSumSection movesumSection = (MoveSumSection)iRefresh;
				movesumSection.setApprovedStart(start);
				movesumSection.setApprovedEnd(end);
				movesumSection.setWhereClause(sb.toString());
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
		
		public void createWhereClause() {
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			sb = new StringBuffer("");
			sb.append(" 1=1 ");
			// 如果物料编号不为空，则加上物料编号
	        if(fields.get(ID_MATERIAL_RRN) instanceof SearchField) {
	        	SearchField sf = (SearchField)fields.get(ID_MATERIAL_RRN);
	        	String materialRrn = null;
				if(sf.getValue() instanceof String) {
					String value = (String)sf.getValue();
					if(!"".equals(value.trim())) {
						materialRrn = value;
					}
				} else if(sf.getValue() instanceof Long) {
					materialRrn = String.valueOf(sf.getValue());
				}
				if(materialRrn !=null && !"".equals(materialRrn.trim())) {
					sb.append(" AND ");
					sb.append(" M.OBJECT_RRN");
					sb.append(" = '" + materialRrn + "' ");						
				}
			}
	        // 如果仓库编号不为空，则加上仓库编号
	        if(fields.get(ID_WAREHOUSE_RRN) instanceof RefTableField) {
	        	RefTableField rtf = (RefTableField)fields.get(ID_WAREHOUSE_RRN);
	        	String warehouseRrn = null;
				if(rtf.getValue() instanceof String) {
					String value = (String)rtf.getValue();
					if(!"".equals(value.trim())) {
						warehouseRrn = value;
					}
				} else if(rtf.getValue() instanceof Long) {
					warehouseRrn = String.valueOf(rtf.getValue());
				}
				if(warehouseRrn != null && !"".equals(warehouseRrn.trim())) {
					sb.append(" AND ");
					sb.append(" W.OBJECT_RRN");
					sb.append(" = '" + warehouseRrn + "' ");					
				}
			}
		}
	}
	
	class MoveSumQueryForm extends QueryForm {
		protected EntityQueryDialog dialog;
		public MoveSumQueryForm(Composite parent, int style, ADTable table, EntityQueryDialog dialog) {
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
							} else if(value instanceof Map) {
								Map<String, Date> map = (Map<String, Date>)value;
								if(map.values().contains(null)) {
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

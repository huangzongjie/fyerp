package com.graly.erp.pur.msm;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.base.model.Material;
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
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MinStorageMaterialDialog extends ExtendDialog {
	private static String ID_MATERIALID = "materialId";
	private static String ID_MATERIALNAME = "materialName";
	private static String WHERE_CLAUSE_SUFFIX = " AND isPurchase = 'Y' AND isLotControl = 'Y' ";
	
	private MsmQueryDialog queryDialog;
	private EntityTableManager tableManager;
	protected boolean isCreateQuery;
	protected ADManager manager;
	
	public MinStorageMaterialDialog() {
		super();
		queryDialog = new MsmQueryDialog(UI.getActiveShell());
	}
	
	public MinStorageMaterialDialog(boolean isCreateQuery) {
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
				((MinStorageMaterialSection)section).setExtendDialog(this);
			}
		}
	}

	public MsmQueryDialog getEntityQueryDialog() {
		return this.queryDialog;
	}
	
	public void setEntityQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = (MsmQueryDialog)queryDialog;
	}

	class MsmQueryDialog extends EntityQueryDialog {
		String ModelName = "Material";
		ADTable adTable;
		public MsmQueryDialog(Shell parent) {
			super(parent);
		}

		public MsmQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public MsmQueryDialog(Shell parent,
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
	        queryForm = new MsmQueryForm(composite, SWT.NONE, getADTable(), this);
	        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	        return composite;
	    }

		@Override
		protected void createAdvanceButtonBar(Composite parent) {}

		@Override
		protected void okPressed() {
			this.setErrorMessage(null);
			if(queryForm.validate()) {
				MinStorageMaterialDialog.this.okPressed();
				// 设置materialRrn到实现IVdmAssess接口
				fillQueryData();
				setReturnCode(OK);
				this.setVisible(false);
				iRefresh.refresh();
			}
		}
		
		protected void fillQueryData() {
			List<Material> list = null;
			boolean isQueryAll = false;
			HashMap<String, IField> fields = queryForm.getFields();
			StringBuffer sb = new StringBuffer(" 1 = 1 ");
			if(fields.get(ID_MATERIALID) instanceof TextField) {
				TextField sf = (TextField)fields.get(ID_MATERIALID);
				String materialId = sf.getText();
				if(materialId != null && !"".equals(materialId.trim())) {
					sb.append(" AND " + ModelName + "." + sf.getId() + " LIKE '" + materialId + "' ");
				}
			}
			if(fields.get(ID_MATERIALNAME) instanceof TextField) {
				TextField tf = (TextField) fields.get(ID_MATERIALNAME);
				String materialName = tf.getText();
				if(materialName != null && materialName.trim().length() != 0) {
					sb.append(" AND " + ModelName + ".name" + " LIKE '" + materialName + "' ");
				}
			}
			if(!" 1 = 1 ".equals(sb.toString())) {
			} else {
				isQueryAll = true;
			}
			sb.append(WHERE_CLAUSE_SUFFIX);
			try {
				if(manager == null) {
					manager = Framework.getService(ADManager.class);
				}
				list = manager.getEntityList(Env.getOrgRrn(),
						Material.class, Env.getMaxResult(), sb.toString(), "");
			} catch(Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}				
			if(iRefresh instanceof MinStorageMaterialSection) {
				MinStorageMaterialSection onlineSection = (MinStorageMaterialSection)iRefresh;
				onlineSection.setQueryAll(isQueryAll);
				onlineSection.setMaterials(list);
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
	
	class MsmQueryForm extends QueryForm {
		protected EntityQueryDialog dialog;
		public MsmQueryForm(Composite parent, int style, ADTable table, EntityQueryDialog dialog) {
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
			boolean isAllNull = true;
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
					
					if(f.getValue() != null) {
						if(f.getValue() instanceof String) {
							if(!"".equals(((String)f.getValue()).trim())) {
								isAllNull = false;
							}
						} else {
							isAllNull = false;
						}
					}
				}
			}
			if(isAllNull) {
				queryDialog.setErrorMessage(Message.getString("common.must_input_key_to_query"));
				return false;
			}
			return validFlag;
		}
	}
}

package com.graly.erp.inv.material.online;

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
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class OnlineDialog extends ExtendDialog {
	private static String ID_MATERIALID = "materialRrn";
	private static String ID_MATERIALNAME = "name";
	private static String ID_IS_PURCHASE = "isPurchase";
	
	private OnlineQueryDialog queryDialog;
	private EntityTableManager tableManager;
	protected boolean isCreateQuery;
	
	public OnlineDialog() {
		super();
		queryDialog = new OnlineQueryDialog(UI.getActiveShell());
	}
	
	public OnlineDialog(boolean isCreateQuery) {
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
				((OnlineSection)section).setExtendDialog(this);
			}
		}
	}

	public OnlineQueryDialog getEntityQueryDialog() {
		return this.queryDialog;
	}
	
	public void setEntityQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = (OnlineQueryDialog)queryDialog;
	}

	class OnlineQueryDialog extends EntityQueryDialog {
		String ModelName = "Material";
		ADTable adTable;
		public OnlineQueryDialog(Shell parent) {
			super(parent);
		}

		public OnlineQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public OnlineQueryDialog(Shell parent,
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
	        queryForm = new OnlineQueryForm(composite, SWT.NONE, getADTable(), this);
	        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	        return composite;
	    }

		@Override
		protected void createAdvanceButtonBar(Composite parent) {}

		@Override
		protected void okPressed() {
			this.setErrorMessage(null);
			if(queryForm.validate()) {
				OnlineDialog.this.okPressed();
				// 设置materialRrn到实现IVdmAssess接口
				fillQueryData();
				setReturnCode(OK);
				this.setVisible(false);
				iRefresh.refresh();
			}
		}
		
//		protected void fillQueryData() {
//			Long materialRrn = null;
//			String mId = null;
//			List<Material> list = null;
//			boolean isQueryAll = false;
//			HashMap<String, IField> fields = queryForm.getFields();
//			StringBuffer sb = new StringBuffer(" 1 = 1");
//			if(fields.get(ID_MATERIALID) instanceof SearchField && fields.get(ID_MATERIALNAME) instanceof TextField && fields.get(ID_IS_PURCHASE) instanceof ComboField) {
//				SearchField sf = (SearchField)fields.get(ID_MATERIALID);
//				TextField tf = (TextField) fields.get(ID_MATERIALNAME);
//				ComboField cf = (ComboField) fields.get(ID_IS_PURCHASE);
//				String materialId = sf.getText();
//				String materialName = tf.getText();
//				String isPurchase = (String) cf.getValue();
//				// 如果是模糊查询则查出相关物料，否则直接找到确定的物料
//				if(materialId != null || (materialName != null && materialName.trim().length() != 0) || (isPurchase != null && isPurchase.trim().length()!=0)) {
//					if(materialId != null){
//						sb.append(" AND " + ModelName + "." + sf.getRefTable().getValueField() + " LIKE '" + materialId + "' ");
//					}
//					
//					if(materialName != null && materialName.trim().length() != 0){
//						sb.append(" AND " + ModelName + "." + tf.getId() + " LIKE '" + materialName + "' ");
//					}
//					
//					if(isPurchase != null && isPurchase.trim().length()!=0){
//						sb.append(" AND isPurchase ='"+isPurchase+"'");
//					}
//					
//					try {
//						ADManager manager = Framework.getService(ADManager.class);
//						list = manager.getEntityList(Env.getOrgRrn(),
//								Material.class, Env.getMaxResult(), sb.toString(), "");
//					} catch(Exception e) {
//						ExceptionHandlerManager.asyncHandleException(e);
//					}
//				}
//				else {
//					Object obj = sf.getValue();
//					try {
//						materialRrn = Long.parseLong(obj.toString());
//						mId = ((Material)sf.getData()).getMaterialId();
//					} catch(Exception e) {
//						isQueryAll = true;
//					}					
//				}
//			}
//			if(iRefresh instanceof OnlineSection) {
//				OnlineSection onlineSection = (OnlineSection)iRefresh;
//				onlineSection.setMaterialRrn(materialRrn);
//				onlineSection.setMaterialId(mId);
//				onlineSection.setQueryAll(isQueryAll);
//				onlineSection.setMaterials(list);
//			}
//		}
		
		protected void fillQueryData(){
			Long materialRrn = null;
			String mId = null;
			List<Material> list = null;
			boolean isQueryAll = false;
			HashMap<String, IField> fields = queryForm.getFields();
			StringBuffer sb = new StringBuffer(" 1 = 1");
			boolean fillFlag = false;
			for(IField f : fields.values()){
				Object o = f.getValue();
				if(o != null ){
					if(o instanceof String){
						if(((String)o).trim().length() > 0){
							if(f instanceof SearchField){
								sb.append(" AND " + ModelName + "." + ((SearchField)f).getRefTable().getKeyField() + " = " + o + " ");
							}else{
								sb.append(" AND " + ModelName + "." + f.getId() + " LIKE '" + o + "' ");
							}
							fillFlag = fillFlag || true;
						}else{
							fillFlag = fillFlag || false;
						}
					}else{
						fillFlag = fillFlag || true;
						sb.append(" AND " + ModelName + "." + f.getId() + " LIKE '" + o + "' ");
					}
				}else{
					if(f instanceof SearchField){
						String s = ((SearchField)f).getText();
						if(s != null && s.trim().length() > 0){
							sb.append(" AND " + ModelName + "." + ((SearchField)f).getRefTable().getValueField() + " LIKE '" + s + "' ");
							fillFlag = fillFlag || true;
						}
					}else{
						fillFlag = fillFlag || false;
					}
				}
			}
			
			if(fillFlag){
				try {
					ADManager manager = Framework.getService(ADManager.class);
					list = manager.getEntityList(Env.getOrgRrn(),
							Material.class, Env.getMaxResult(), sb.toString(), "");
				} catch(Exception e) {
					ExceptionHandlerManager.asyncHandleException(e);
				}
			}else{
				isQueryAll = true;
			}
			if(iRefresh instanceof OnlineSection) {
				OnlineSection onlineSection = (OnlineSection)iRefresh;
				if(list != null && list.size() == 1){
					Material material = list.get(0);
					materialRrn = material.getObjectRrn();
					mId = material.getMaterialId();
				}
				onlineSection.setMaterialRrn(materialRrn);
				onlineSection.setMaterialId(mId);
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
	
	class OnlineQueryForm extends QueryForm {
		protected EntityQueryDialog dialog;
		public OnlineQueryForm(Composite parent, int style, ADTable table, EntityQueryDialog dialog) {
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

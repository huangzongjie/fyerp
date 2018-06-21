package com.graly.erp.wip.materialused;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
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

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.client.ADManager;
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
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MaterialUsedExtendDailog extends ExtendDialog {
	public static final String DIALOG_ID = "com.graly.erp.wip.materialused.MaterialUsedExtendDailog";
	public static final String FIELD_ID_MATERIAL_RRN = "materialRrn";
	public static final String FIELD_ID_STATUS = "moStatus";
	public static final String FIELD_ID_DATE_END="dateEnd";
	
	private MaterialUsedQueryDialog queryDialog;
	private EntityTableManager tableManager;
	protected boolean isCreateQuery;
	
	public MaterialUsedExtendDailog() {
		super();
		queryDialog = new MaterialUsedQueryDialog(UI.getActiveShell());
	}
	
	public MaterialUsedExtendDailog(boolean isCreateQuery) {
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
				((MaterialUsedSection)section).setExtendDialog(this);
			}
		}
	}
	
	public MaterialUsedQueryDialog getEntityQueryDialog() {
		return this.queryDialog;
	}
	
	public void setEntityQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = (MaterialUsedQueryDialog)queryDialog;
	}

	class MaterialUsedQueryDialog extends EntityQueryDialog {
		ADTable adTable;
		
		public MaterialUsedQueryDialog(Shell parent) {
			super(parent);
		}

		public MaterialUsedQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public MaterialUsedQueryDialog(Shell parent,
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
	        queryForm = new QueryForm(composite, SWT.NONE, getADTable());
	        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	        return composite;
	    }
		
		@Override
		protected void createAdvanceButtonBar(Composite parent) {}

		@Override
		protected void okPressed() {
			this.setErrorMessage(null);
			MaterialUsedExtendDailog.this.okPressed();
			fillMoIDData();
			super.okPressed();
		}
		
		protected void fillMoIDData() {
			String materialId = null;
			Long materialRrn = null;
			String status = null;
			Date dateStart=null;
			Date dateEnd=null;
			HashMap<String, IField> fields = queryForm.getFields();
			if(fields.get(FIELD_ID_MATERIAL_RRN) instanceof SearchField) {
				SearchField sf = (SearchField)fields.get(FIELD_ID_MATERIAL_RRN);
				String value = String.valueOf(sf.getValue());
				if(value != null && !"".equals(value.trim()))
					materialRrn = Long.parseLong(value);
				if(sf.getData() instanceof Material) {
					Material mu = (Material)sf.getData();
					materialId = mu.getMaterialId();
				}
			}
			if(fields.get(FIELD_ID_STATUS) instanceof ComboField) {
				ComboField cf = (ComboField)fields.get(FIELD_ID_STATUS);
				if(cf.getValue() instanceof String 
						&& !"".equals(((String)cf.getValue()).trim())) {
					status = (String)cf.getValue();
				}
			}
			
			if(fields.get(FIELD_ID_DATE_END) instanceof FromToCalendarField){
				FromToCalendarField df=(FromToCalendarField)fields.get(FIELD_ID_DATE_END);
				Map m = (Map)df.getValue();
				dateStart = (Date) m.get(FromToCalendarField.DATE_FROM);
				dateEnd = (Date) m.get(FromToCalendarField.DATE_TO);
			}
			
			MaterialUsedSection usedSection = (MaterialUsedSection)this.getIRefresh();
			usedSection.setMaterialId(materialId);
			usedSection.setMaterialRrn(materialRrn);
			usedSection.setStatus(status);
			usedSection.setDateStart(dateStart);
			usedSection.setDateEnd(dateEnd);
		}
		
		@Override
		public void createWhereClause() {
			Date dateStart=null;
			Date dateEnd=null;
			String str_dateStart="";
			String str_dateEnd="";
			HashMap<String, IField> fields = queryForm.getFields();
			
			if(fields.get(FIELD_ID_DATE_END) instanceof FromToCalendarField){
				FromToCalendarField df=(FromToCalendarField)fields.get(FIELD_ID_DATE_END);
				Map m = (Map)df.getValue();
				dateStart = (Date) m.get(FromToCalendarField.DATE_FROM);
				//Add dateEnd by BruceYou 2012-3-5
				dateEnd = (Date) m.get(FromToCalendarField.DATE_TO);
				
				if(dateStart!=null){
					str_dateStart=new SimpleDateFormat("yyyy-MM-dd").format(dateStart);
				}
				if(dateEnd!=null){
					str_dateEnd=new SimpleDateFormat("yyyy-MM-dd").format(dateEnd);
				}
			}
			
			//这里拼一下sql,把datestart和dateend加进来
			//and  M.DATE_END BETWEEN to_date('2011-05-01','yyyy-mm-dd') AND to_date('2011-08-01','yyyy-mm-dd') 
			sb = new StringBuffer("");
			if(str_dateStart!=null&&str_dateStart.trim().length()>0){
				sb.append(" and M.DATE_END >= ").append("to_date('").append(str_dateStart).append("','yyyy-MM-dd' )");
			}
			if(str_dateEnd!=null&&str_dateEnd.trim().length()>0){
				sb.append(" and M.DATE_END <= ").append("to_date('").append(str_dateEnd).append("','yyyy-MM-dd' )");
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
}

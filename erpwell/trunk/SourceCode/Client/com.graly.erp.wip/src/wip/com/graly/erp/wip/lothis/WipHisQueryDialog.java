package com.graly.erp.wip.lothis;

import java.util.Date;
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
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class WipHisQueryDialog extends ExtendDialog {
	private WipHisInternalQueryDialog queryDialog;
	private EntityTableManager tableManager;
	protected boolean isCreateQuery;
	
	public WipHisQueryDialog() {
		super();
		queryDialog = new WipHisInternalQueryDialog(UI.getActiveShell());
	}
	
	public WipHisQueryDialog(boolean isCreateQuery) {
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
				((WipHisQuerySection)section).setExtendDialog(this);
			}
		}
	}

	public WipHisInternalQueryDialog getEntityQueryDialog() {
		return this.queryDialog;
	}
	
	public void setEntityQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = (WipHisInternalQueryDialog)queryDialog;
	}

	class WipHisInternalQueryDialog extends EntityQueryDialog {
		ADTable adTable;
		public WipHisInternalQueryDialog(Shell parent) {
			super(parent);
		}

		public WipHisInternalQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public WipHisInternalQueryDialog(Shell parent,
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
			queryForm = new QueryForm(composite, SWT.NONE, tableManager.getADTable());
	        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	        return composite;
	    }

		@Override
		protected void createAdvanceButtonBar(Composite parent) {}

		
		
		@Override
		public void createWhereClause() {
			super.createWhereClause();
			String modelName = tableManager.getADTable().getModelName() + ".";
			sb.append(" AND ");
			sb.append(modelName + "transType = 'RECEIVE' ");
		}

		@Override
		protected void okPressed() {
			createWhereClause();	
			if(!validateDateProduct()){
				return;
			}
			setReturnCode(OK);
			iRefresh.setWhereClause(sb.toString());
			iRefresh.refresh();
			refresh();
	        this.setVisible(false);
		}
		
		public void refresh(){
			setErrorMessage(null);//清除错误提示信息
			setMessage(Message.getString("common.keys"));
		}
		
		public boolean validateDateProduct(){
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			 for(IField f : fields.values()) {
				 Object t = f.getValue();
				 if(f.getId().equals("dateProduct")){//只可能是FromToCalendarField
						Map m = (Map)t;
						Date from = (Date) m.get(FromToCalendarField.DATE_FROM);
						Date to = (Date) m.get(FromToCalendarField.DATE_TO);
						if(from == null) {
							setErrorMessage(String.format(Message.getString("common.ismandatory"),f.getLabel()));
							return false;
						}
					}
			 }
			 return true;
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

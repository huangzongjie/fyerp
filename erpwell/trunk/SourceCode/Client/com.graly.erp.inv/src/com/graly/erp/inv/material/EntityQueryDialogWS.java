package com.graly.erp.inv.material;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class EntityQueryDialogWS extends EntityQueryDialog {
	public static final String FIELD_ID_WAREHOUSE_RRN = "warehouseRrn";

	public EntityQueryDialogWS(Shell parent, EntityTableManager tableManager, IRefresh irefresh) {
		super(parent, tableManager, irefresh);
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
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		queryForm = new QueryForm4WC(composite, SWT.NONE, tableManager.getADTable(), this);
		queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		return composite;
	}
	
	@Override
    protected void okPressed() {
		setErrorMessage(null);
		if(queryForm.validate()) {
			fillQueryKeys();
			createWhereClause();
			setReturnCode(OK);
			iRefresh.setWhereClause(sb.toString());
			iRefresh.refresh();
			this.setVisible(false);
		}
    }

	protected class QueryForm4WC extends QueryForm {
		protected EntityQueryDialog parentDialog;
		protected IMessageManager mmng;
		
		public QueryForm4WC(Composite parent, int style, ADTable table, IMessageManager mmng) {
			super(parent, style, table);
			this.mmng = mmng;
		}
		
		public QueryForm4WC(Composite parent, int style, ADTable table, EntityQueryDialog parentDialog) {
			super(parent, style, table);
			this.parentDialog = parentDialog;
		}
		
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

		public IField getField(ADField adField) {
			String displayText = adField.getDisplayType();
			String name = adField.getName();
			String displayLabel = I18nUtil.getI18nMessage(adField, "label");
			if (adField.getIsMandatory()) {
				displayLabel = displayLabel + "*";
			}
			IField field = null;
			if (FIELD_ID_WAREHOUSE_RRN.equals(name) && FieldType.REFTABLE.equalsIgnoreCase(displayText)) {
				try {
					ADManager entityManager = Framework.getService(ADManager.class);
					ADRefTable refTable = new ADRefTable();
					refTable.setObjectRrn(adField.getReftableRrn());
					refTable = (ADRefTable) entityManager.getEntity(refTable);
					if (refTable == null || refTable.getTableRrn() == null) {
						return null;
					}
					ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer) tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
					String where = " warehouseType='≥µº‰–Èƒ‚ø‚' ";
					List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), where,
							refTable.getOrderByClause());
					if (!adField.getIsMandatory()) {
						String className = adTable.getModelClass();
						list.add((ADBase) Class.forName(className).newInstance());
					}
					viewer.setInput(list);
					field = createRefTableFieldList(name, displayLabel, viewer, refTable);
					addField(name, field);

				} catch (Exception e) {
					e.printStackTrace();
					ExceptionHandlerManager.asyncHandleException(e);
				}
			} else {
				field = super.getField(adField);
			}
			return field;
		}
		
		@Override
		public boolean validate() {
			boolean validFlag = true;
			boolean isAllNull = true;
			for (IField f : fields.values()) {
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
							parentDialog.setErrorMessage(
									String.format(Message.getString("common.ismandatory"), I18nUtil.getI18nMessage(adField, "label")));
						}
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
			if(isAllNull) {
				parentDialog.setErrorMessage(Message.getString("common.must_input_key_to_query"));
				return false;
			}
			return validFlag;
		}
	}
}

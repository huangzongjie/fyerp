package com.graly.erp.pur.po.query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.base.model.Documentation;
import com.graly.erp.pur.client.PURManager;
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
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

/**
 * 根据采购员查其供应商历史采购记录
 * @author Administrator
 *
 */
public class SpecPoQueryDialog extends ExtendDialog {
	private static final String ARRIVALTIME = "arrivalTime";
	private static final String PURCHASER = "purchaser";
	
	private PoInternalQueryDialog queryDialog;
	private EntityTableManager tableManager;
	protected boolean isCreateQuery;
	
	public SpecPoQueryDialog() {
		super();
		queryDialog = new PoInternalQueryDialog(UI.getActiveShell());
	}
	
	public SpecPoQueryDialog(boolean isCreateQuery) {
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
				((PoQuerySection)section).setExtendDialog(this);
			}
		}
	}

	public PoInternalQueryDialog getEntityQueryDialog() {
		return this.queryDialog;
	}
	
	public void setEntityQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = (PoInternalQueryDialog)queryDialog;
	}

	class PoInternalQueryDialog extends EntityQueryDialog {
		ADTable adTable;
		public PoInternalQueryDialog(Shell parent) {
			super(parent);
		}

		public PoInternalQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		public PoInternalQueryDialog(Shell parent,
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
		protected void okPressed() {
			createWhereClause();
			if("1=1".equals(sb.toString().trim()))
				return;
			setReturnCode(OK);
			iRefresh.setWhereClause(sb.toString());
			iRefresh.refresh();
	        this.setVisible(false);
		}
		
		public void createWhereClause() {
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			String modelName = tableManager.getADTable().getModelName() + ".";
			sb = new StringBuffer(" ");
			sb.append(" 1=1 ");
			for (IField f : fields.values()) {
				if (f.getId().equals(ARRIVALTIME) && (!f.getValue().equals(""))) {
					try {
						int days = Integer.parseInt(f.getValue().toString());
						
						sb.append(" AND orgRrn = '" + Env.getOrgRrn() + "' AND lineStatus = '" + Documentation.STATUS_APPROVED + "' ");
						sb.append(" AND ( TRUNC(dateEnd) < TRUNC(sysdate + " + days + ") )");
						sb.append(") ");
					} catch (Exception e) {
						if(!f.getValue().equals("")){
							sb.append(" AND 1 != 1 ");
						}
						break;
					}
					} 
				    else if (f.getId().equals(PURCHASER) && (!f.getValue().equals(""))){

						try {
							PURManager purManager = Framework.getService(PURManager.class);
							List<BigDecimal> vrrnlist = purManager.getVendorByPurchaser(Env.getOrgRrn(),f.getValue().toString());
							if(vrrnlist.size() == 0){
								sb.append(" AND 1 != 1 ");
							}
							
							sb.append(" AND orgRrn = '" + Env.getOrgRrn() + "' AND ( ");
							int i=0;
							for(BigDecimal vrrn : vrrnlist){
								if((i++)>0){
									sb.append(" OR ");
									}
								sb.append(" vendor_rrn = ");
								sb.append(" '"+vrrn+"' ");
							}
							sb.append(" ) ");
						} catch (Exception e) {
							if(!f.getValue().equals("")){
								sb.append(" AND 1 != 1 ");
							}
							break;
						}
						
					} else {
					doWhereCause(modelName, f);
				}
			}
		}
		
		protected void doWhereCause(String modelName, IField f) {
			Object t = f.getValue();
			if (t instanceof Date) {
				Date cc = (Date) t;
				if (cc != null) {
					sb.append(" AND ");
					sb.append("TO_CHAR(");
					sb.append(modelName);
					sb.append(f.getId());
					sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
					sb.append(I18nUtil.formatDate(cc));
					sb.append("'");
				}
			} else if (t instanceof String) {
				String txt = (String) t;
				if (!txt.trim().equals("") && txt.length() != 0) {
					sb.append(" AND ");
					sb.append(modelName);
					sb.append(f.getId());
					sb.append(" LIKE '");
					sb.append(txt);
					sb.append("'");
				}
			} else if (t instanceof Boolean) {
				Boolean bl = (Boolean) t;
				sb.append(" AND ");
				sb.append(modelName);
				sb.append(f.getId());
				sb.append(" = '");
				if (bl) {
					sb.append("Y");
				} else if (!bl) {
					sb.append("N");
				}
				sb.append("'");
			} else if (t instanceof Long) {
				long l = (Long) t;
				sb.append(" AND ");
				sb.append(modelName);
				sb.append(f.getId());
				sb.append(" = " + l + " ");
			}else if(t instanceof Map){//只可能是FromToCalendarField
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
					sb.append(") <= TO_DATE('" + I18nUtil.formatDate(to) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
				}
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

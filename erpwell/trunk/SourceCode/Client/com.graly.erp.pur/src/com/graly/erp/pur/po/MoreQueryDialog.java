package com.graly.erp.pur.po;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MoreQueryDialog extends EntityQueryDialog {
	private static final Logger logger = Logger.getLogger(MoreQueryDialog.class);
	private String ARRIVALTIME = "arrivalTime";
	private IField arrivalTimeField;

	public MoreQueryDialog(Shell parent, EntityTableManager tableManager,IRefresh irefresh) {
		super(parent, tableManager,irefresh);
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
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		queryForm = new MoreQueryForm(composite, SWT.NONE, tableManager.getADTable());
		queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		return composite;
	}

	public void createWhereClause() {
		LinkedHashMap<String, IField> fields = queryForm.getFields();
		String modelName = tableManager.getADTable().getModelName() + ".";
		sb = new StringBuffer(" ");
		sb.append(" 1 = 1 ");
		for (IField f : fields.values()) {
			if (f.getId().equals(ARRIVALTIME) && (!f.getValue().equals(""))) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				try {
					int days = Integer.parseInt(f.getValue().toString());
					Calendar calendar = Calendar.getInstance();
					Date date = Env.getSysDate();
					calendar.setTime(date);
					calendar.add(Calendar.DAY_OF_YEAR, days);
					Date cal = dateFormat.parse(dateFormat.format(calendar.getTime()));
					Date now = dateFormat.parse(dateFormat.format(date));
					sb.append(" AND objectRrn in (SELECT DISTINCT poRrn FROM PurchaseOrderLine PurchaseOrderLine WHERE orgRrn = '"
							+ Env.getOrgRrn() + "' AND lineStatus = 'APPROVED' AND ( dateEnd BETWEEN to_date('" + now.toLocaleString()
							+ "','YYYY-MM-DD hh24:mi:ss') AND to_date('" + cal.toLocaleString() + "','YYYY-MM-DD hh24:mi:ss')))");
				} catch (Exception e) {
					if(!f.getValue().equals("")){
						sb.append("AND 1 != 1");
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
				sb.append(") <= TO_DATE('" + I18nUtil.formatDate(to) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
			}
		}
	}
	
	class MoreQueryForm extends QueryForm {

		public MoreQueryForm(Composite parent, int style, ADTable table) {
			super(parent, style, table);
		}

		@Override
		public void createForm() {
			try {
				if (table != null) {
					for (ADTab tab : table.getTabs()) {
						if (tab != null) {
							if (tab.getGridY() != null) {
								super.setGridY(2);
							} else
								super.setGridY(1);
							break;
						} else {
							super.setGridY(1);
						}
					}
					allADfields = table.getFields();
				}
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}
			addFields();
			createContent();
		}

		@Override
		public void addFields() {
			super.addFields();
			try {
				arrivalTimeField = createText(ARRIVALTIME, Message.getString("pur.arrival_time_less"), "", 32);
				addField(ARRIVALTIME, arrivalTimeField);
			} catch (Exception e) {
				logger.error("MoreQueryForm : Init listItem", e);
			}
		}
	}
}

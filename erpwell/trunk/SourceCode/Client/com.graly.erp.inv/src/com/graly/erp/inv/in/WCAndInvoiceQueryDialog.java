package com.graly.erp.inv.in;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.pur.po.MoreQueryDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class WCAndInvoiceQueryDialog extends MoreQueryDialog {
	private static final Logger logger = Logger.getLogger(WCAndInvoiceQueryDialog.class);
	public String INVOICEARRIVAL = "invoiceArrival";
	public TextField invoicearrivalField;
	public boolean flag = false;

	public WCAndInvoiceQueryDialog(Shell parent, EntityTableManager tableManager, IRefresh irefresh) {
		super(parent, tableManager, irefresh);
	}

	public WCAndInvoiceQueryDialog(Shell parent, EntityTableManager tableManager,IRefresh irefresh, boolean flag) {
		super(parent, tableManager,irefresh);
		this.flag = flag;
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
		createDialogForm(composite);
		return composite;
	}

	/**
	 * @param composite
	 */
	protected void createDialogForm(Composite composite) {
		queryForm = new MoreQueryForm(composite, SWT.NONE, tableManager.getADTable());
		queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void createWhereClause() {
		LinkedHashMap<String, IField> fields = queryForm.getFields();
		String modelName = tableManager.getADTable().getModelName() + ".";
		sb = new StringBuffer(" ");
		sb.append(" 1 = 1 ");
		for (IField f : fields.values()) {
			if (flag) {
				if (f.getId().equals(INVOICEARRIVAL) && (!f.getValue().equals(""))) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					try {
						int days = Integer.parseInt(f.getValue().toString());
						Calendar calendar = Calendar.getInstance();
						Date now = Env.getSysDate();
						calendar.setTime(now);
						calendar.add(Calendar.DAY_OF_YEAR, -days);
						Date cal = dateFormat.parse(dateFormat.format(calendar.getTime()));
						sb.append(" AND (docStatus LIKE 'APPROVED') AND dateApproved <= to_date('" + cal.toLocaleString()
								+ "','YYYY-MM-DD hh24:mi:ss')");
					} catch (Exception e) {
						sb.append(" AND 1 != 1");
						break;
					}
				} else {
					doWhereCause(modelName, f);
				}
			} else {
				doWhereCause(modelName, f);
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
			if (flag) {
				try {
					invoicearrivalField = createText(INVOICEARRIVAL, Message.getString("inv.invoice_time_greater "), "", 32);
					addField(INVOICEARRIVAL, invoicearrivalField);
				} catch (Exception e) {
					logger.error("WorkCenterQueryForm : Init listItem", e);
				}
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
			if (FieldType.REFTABLE.equalsIgnoreCase(displayText)) {
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
					String where = " userRrn = " + Env.getUserRrn()
							+ " AND (isVirtual = 'N' OR isVirtual is null)";
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

	}
}

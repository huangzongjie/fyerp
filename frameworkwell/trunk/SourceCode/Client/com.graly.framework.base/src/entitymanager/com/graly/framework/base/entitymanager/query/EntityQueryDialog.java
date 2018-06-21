package com.graly.framework.base.entitymanager.query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class EntityQueryDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(EntityQueryDialog.class);
	public static final int ADVANCEQUERY_ID = 10000;
	protected StringBuffer sb;
	protected QueryForm queryForm;
	protected EntityTableManager tableManager;
	protected IRefresh iRefresh;
	protected Map<String,Object> queryKeys = new LinkedHashMap<String,Object>();
	protected Method refreshMethod;
	
	public EntityQueryDialog(Shell parent) {
        super(parent);
        this.setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL);
        this.setBlockOnOpen(false);
    }
	
	public EntityQueryDialog(Shell parent, EntityTableManager tableManager, IRefresh iRefresh) {
		this(parent);
		this.tableManager = tableManager;
		this.iRefresh = iRefresh;
	}
	
	public EntityQueryDialog(Shell parent, EntityTableManager tableManager, IRefresh iRefresh, Method refreshMethod) {
		this(parent);
		this.tableManager = tableManager;
		this.iRefresh = iRefresh;
		this.refreshMethod = refreshMethod;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("search-dialog"));
        setTitle(Message.getString("common.search_Title"));
        setMessage(Message.getString("common.keys"));
        Composite composite = (Composite) super.createDialogArea(parent);
        createDialogForm(composite);
        return composite;
    }

	/**
	 * @param composite
	 */
	protected void createDialogForm(Composite composite) {
		queryForm = new QueryForm(composite, SWT.NONE, tableManager.getADTable());
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	@Override
    protected void okPressed() {
		if(!validateQueryKey()) return;
		fillQueryKeys();
		createWhereClause();
		setReturnCode(OK);
		iRefresh.setWhereClause(sb.toString());
		if(refreshMethod != null){
			try {
				refreshMethod.invoke(iRefresh);
			}  catch (Exception e) {
				logger.error("EntityQueryDialog : okPressed", e);
			}
		}else{
			iRefresh.refresh();
		}
        this.setVisible(false);
    }
	
	protected void fillQueryKeys() {
		if(!queryKeys.isEmpty()){
			queryKeys.clear();
		}
		LinkedHashMap<String, IField> fields = queryForm.getFields();
		for(IField f : fields.values()){
			Object t = f.getValue();
			if (t instanceof Date) {
				Date cc = (Date)t;
				if(cc != null) {
					if(FieldType.SHORTDATE.equals(f.getFieldType())){
						queryKeys.put(f.getId(), I18nUtil.formatShortDate(cc));
					}else{
						queryKeys.put(f.getId(), I18nUtil.formatDate(cc));
					}
				}
			} else if(t instanceof String) {
				String txt = (String)t;
				if(txt != null && txt.trim().length() != 0) {
					queryKeys.put(f.getId(), txt);
				}else{
					queryKeys.put(f.getId(), null);
				}
			} else if(t instanceof Boolean) {
				 Boolean bl = (Boolean)t;
				 if(bl) {
					 queryKeys.put(f.getId(), "Y");
				 } else if(!bl) {
					 queryKeys.put(f.getId(), "N");
				 }
			} else if(t instanceof Long) {
				long l = (Long)t;
				queryKeys.put(f.getId(), String.valueOf(l));
			} else if(t instanceof Map){//只可能是FromToCalendarField
				Map m = (Map)t;
				Date from = (Date) m.get(FromToCalendarField.DATE_FROM);
				Date to = (Date) m.get(FromToCalendarField.DATE_TO);
				if(from != null) {
					if(to == null){
						to = Env.getSysDate();;
						m.put(FromToCalendarField.DATE_TO, to);
					}
					queryKeys.put(f.getId(), m);
				}
			}
		}
	}

	protected boolean validateQueryKey() {
		setMessage(Message.getString("common.keys"));
		//如果栏位名以*结尾说明是必填项，则验证之
		LinkedHashMap<String, IField> fields = queryForm.getFields();
		for(IField f : fields.values()) {
			if(f.getLabel() != null){
				if(f.getLabel().trim().endsWith("*")){
					if(f.getValue() == null || "".equals(f.getValue())){
						setMessage(String.format(Message.getString("common.ismandatory"), f.getLabel()), IMessageProvider.ERROR);
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if(ADVANCEQUERY_ID == buttonId) {
			advancePressed();
		} else if (IDialogConstants.OK_ID == buttonId) {
			// 验证高级查询输入值是否合法
			if(!validateAdvanceQuery()) {
				return;
			}
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	protected void cancelPressed() {
		setReturnCode(CANCEL);
		this.setVisible(false);
	}

	protected boolean validateAdvanceQuery() {
		if(this.getTray() != null) {
			AdvanceQueryTray tray = (AdvanceQueryTray)this.getTray();
			return tray.validate();
		}
		return true;
	}
	
	public void createWhereClause() {
		LinkedHashMap<String, IField> fields = queryForm.getFields();
		String modelName = tableManager.getADTable().getModelName() + ".";
		sb = new StringBuffer("");
		
		sb.append(" 1=1 ");
				
        for(IField f : fields.values()) {
			Object t = f.getValue();
			if (t instanceof Date) {
				Date cc = (Date)t;
				Class<?> clazz = null;
				Field objProperty = null;
				try {
					clazz = Class.forName(tableManager.getADTable().getModelClass());
					if(clazz != null){
						objProperty = clazz.getDeclaredField(f.getId());
						Field[] fs = clazz.getDeclaredFields();
						assert fs.length != 0;
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				if(objProperty != null){
					if(objProperty.getType().equals(String.class)){
						//如果对象的属性是String类型的
						if(cc != null) {
							sb.append(" AND ");
							sb.append(modelName);
							sb.append(f.getId());
							if(FieldType.SHORTDATE.equals(f.getFieldType())){
								sb.append(" = '");
								sb.append(I18nUtil.formatShortDate(cc));
							}else{
								sb.append(" = '");
								sb.append(I18nUtil.formatDate(cc));
							}
							sb.append("'");
						}
					}else if(objProperty.getType().equals(Date.class)){
						//如果对象的属性是Date类型的
						if(cc != null) {
							sb.append(" AND ");
							sb.append("TO_CHAR(");
							sb.append(modelName);
							sb.append(f.getId());
							if(FieldType.SHORTDATE.equals(f.getFieldType())){
								sb.append(", '" + I18nUtil.getShortDatePattern() + "') = '");
								sb.append(I18nUtil.formatShortDate(cc));
							}else{
								sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
								sb.append(I18nUtil.formatDate(cc));
							}
							sb.append("'");
						}
					}
				}else{
					if(cc != null) {
						sb.append(" AND ");
						sb.append("TO_CHAR(");
						sb.append(modelName);
						sb.append(f.getId());
						if(FieldType.SHORTDATE.equals(f.getFieldType())){
							sb.append(", '" + I18nUtil.getShortDatePattern() + "') = '");
							sb.append(I18nUtil.formatShortDate(cc));
						}else{
							sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
							sb.append(I18nUtil.formatDate(cc));
						}
						sb.append("'");
					}
				}
			} else if(t instanceof String) {
				String txt = (String)t;
				if(!txt.trim().equals("") && txt.length() != 0) {
					sb.append(" AND ");
					sb.append(modelName);
					sb.append(f.getId());
					sb.append(" LIKE '");
					sb.append(txt);
					sb.append("'");
				}
			} else if(t instanceof Boolean) {
				 Boolean bl = (Boolean)t;
				 sb.append(" AND ");
				 sb.append(modelName);
				 sb.append(f.getId());
				 sb.append(" = '");
				 if(bl) {
					sb.append("Y");
				 } else if(!bl) {
					sb.append("N");
				 }
				 sb.append("'");
			} else if(t instanceof Long) {
				long l = (Long)t;
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
					sb.append(") <= TO_DATE('" + I18nUtil.formatDate(to) + "', '" + I18nUtil.getDefaultDatePattern() + "') ");
				}
			}
        }
        if (getTray() != null) {
        	AdvanceQueryTray tray = (AdvanceQueryTray)this.getTray();
        	String advance = tray.getAdvaceWhereClause();
        	sb.append(advance);
        }
	}
	
	private void advancePressed() {
		DialogTray existingTray = getTray();
		if (existingTray instanceof AdvanceQueryTray) {
			// hide
//			getButton(ADVANCEQUERY_ID).setText(""); 
			closeTray();
		}
		else {
			//show
			if (existingTray != null)
				closeTray();
			AdvanceQueryTray tray = new AdvanceQueryTray(this, tableManager.getADTable());
			openTray(tray);
		}
	}

	// 判断是否设置了查询栏位
	protected boolean hasQueryFields() {
		if(tableManager == null)
			return false;
		List<ADField> queryFields = tableManager.getADTable().getFields();
		for(ADField adField : queryFields) {
			if(adField.getIsAdvanceQuery()) return true;
		}
		return false;
	}

	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		Message.getString("common.ok"), true);
        createButton(parent, IDialogConstants.CANCEL_ID,
        		Message.getString("common.cancel"), false);
    }
	
	protected Control createButtonBar(Composite parent) {
		Composite bar = new Composite(parent, SWT.BORDER);
		setGridLayout(bar, 2);
		bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite temp = new Composite(bar, SWT.NONE);
		setGridLayout(temp, 1);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 0;
		gd.horizontalSpan = 2;
		temp.setLayoutData(gd);
			
		Composite aqComp = new Composite(bar, SWT.NONE);
		GridLayout layout = new GridLayout(0, false);
		layout.makeColumnsEqualWidth = true;
		aqComp.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_CENTER);
		aqComp.setLayoutData(data);
		aqComp.setFont(parent.getFont());
		createAdvanceButtonBar(aqComp);

		Composite composite = new Composite(bar, SWT.NONE);
		GridLayout l = new GridLayout(0, true);
		l.makeColumnsEqualWidth = true;
		composite.setLayout(l);
		GridData data2 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		data2.horizontalAlignment = GridData.END;
		composite.setLayoutData(data2);
		composite.setFont(parent.getFont());
		createButtonsForButtonBar(composite);
		return bar;
	}
	
	private void setGridLayout(Composite content, int num) {
		GridLayout gl = new GridLayout(num, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		content.setLayout(gl);
	}
	
	protected void createAdvanceButtonBar(Composite parent) {
		createButton(parent, ADVANCEQUERY_ID,
        		Message.getString("common.advance_query"), false);
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(150), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(160),
						shellSize.y));
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	public void setVisible (boolean visible) {
		Shell shell = this.getShell();
		shell.setVisible(visible);
	}

	public IRefresh getIRefresh() {
		return iRefresh;
	}

	public void setIRefresh(IRefresh refresh) {
		iRefresh = refresh;
	}

	public Map<String, Object> getQueryKeys() {
		return queryKeys;
	}

	public void setQueryKeys(Map<String, Object> queryKeys) {
		this.queryKeys = queryKeys;
	}

	public EntityTableManager getTableManager() {
		return tableManager;
	}

	public void setTableManager(EntityTableManager tableManager) {
		this.tableManager = tableManager;
	}
}

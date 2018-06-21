package com.graly.framework.base.uiX.viewmanager.forms;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.uiX.viewmanager.IXRefresh;
import com.graly.framework.base.uiX.viewmanager.XTableViewerManager;

public class XQueryDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(XQueryDialog.class);
	public static final int ADVANCEQUERY_ID = 10000;
	protected StringBuffer sb;
	protected QueryForm queryForm;
	protected XTableViewerManager tableManager;
	protected IXRefresh iRefresh;
	protected Map<String,String> queryKeys = new LinkedHashMap<String,String>();
	protected Method refreshMethod;
	
	public XQueryDialog(Shell parent) {
        super(parent);
        this.setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL);
        this.setBlockOnOpen(false);
    }
	
	public XQueryDialog(Shell parent, XTableViewerManager tableManager, IXRefresh iRefresh) {
		this(parent);
		this.tableManager = tableManager;
		this.iRefresh = iRefresh;
	}
	
	public XQueryDialog(Shell parent, XTableViewerManager tableManager, IXRefresh iRefresh, Method refreshMethod) {
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
		queryForm = new QueryForm(composite, SWT.NONE, tableManager.getAdapter().getAdTable());
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	@Override
    protected void okPressed() {
		if(!validateQueryKey()) return;
		fillQueryKeys();
		iRefresh.setQueryKeys(queryKeys);
		setReturnCode(OK);
		if(refreshMethod != null){
			try {
				refreshMethod.invoke(iRefresh);
			}  catch (Exception e) {
				logger.error("XQueryDialog : okPressed", e);
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
			Object fVal = f.getValue();
			if (fVal instanceof Date) {
				Date cc = (Date)fVal;
				if(cc != null) {
					if(FieldType.SHORTDATE.equals(f.getFieldType())){
						queryKeys.put(f.getId(), I18nUtil.formatShortDate(cc));
					}else{
						queryKeys.put(f.getId(), I18nUtil.formatDate(cc));
					}
				}
			} else if(fVal instanceof String) {
				String txt = (String)fVal;
				if(txt != null && txt.trim().length() != 0) {
					queryKeys.put(f.getId(), txt);
				}
			} else if(fVal instanceof Boolean) {
				 Boolean bl = (Boolean)fVal;
				 if(bl) {
					 queryKeys.put(f.getId(), "Y");
				 } else if(!bl) {
					 queryKeys.put(f.getId(), "N");
				 }
			} else if(fVal instanceof Long) {
				long l = (Long)fVal;
				queryKeys.put(f.getId(), String.valueOf(l));
			} else if(fVal instanceof Map){//只可能是FromToCalendarField
				Map m = (Map)fVal;
				Date from = (Date) m.get(FromToCalendarField.DATE_FROM);
				Date to = (Date) m.get(FromToCalendarField.DATE_TO);
				
				if(from != null) {
					if(FieldType.SHORTDATE.equals(f.getFieldType())){
						queryKeys.put(f.getId()+"_FROM", I18nUtil.formatShortDate(from));
					}else{
						queryKeys.put(f.getId()+"_FROM", I18nUtil.formatDate(from));
					}
				}
				
				if(to != null) {
					if(FieldType.SHORTDATE.equals(f.getFieldType())){
						queryKeys.put(f.getId()+"_TO", I18nUtil.formatShortDate(to));
					}else{
						queryKeys.put(f.getId()+"_TO", I18nUtil.formatDate(to));
					}
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
//			advancePressed();
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
//			AdvanceQueryTray tray = (AdvanceQueryTray)this.getTray();
//			return tray.validate();
		}
		return true;
	}
	
//	private void advancePressed() {
//		DialogTray existingTray = getTray();
//		if (existingTray instanceof AdvanceQueryTray) {
//			// hide
////			getButton(ADVANCEQUERY_ID).setText(""); 
//			closeTray();
//		}
//		else {
//			//show
//			if (existingTray != null)
//				closeTray();
//			AdvanceQueryTray tray = new AdvanceQueryTray(this, tableManager.getADTable());
//			openTray(tray);
//		}
//	}

	// 判断是否设置了查询栏位
	protected boolean hasQueryFields() {
		if(tableManager == null)
			return false;
		List<ADField> queryFields = tableManager.getAdapter().getAdTable().getFields();
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

	public IXRefresh getIRefresh() {
		return iRefresh;
	}

	public void setIRefresh(IXRefresh refresh) {
		iRefresh = refresh;
	}

	public Map<String, String> getQueryKeys() {
		return queryKeys;
	}

	public void setQueryKeys(Map<String, String> queryKeys) {
		this.queryKeys = queryKeys;
	}

	public XTableViewerManager getTableManager() {
		return tableManager;
	}

	public void setTableManager(XTableViewerManager tableManager) {
		this.tableManager = tableManager;
	}
}

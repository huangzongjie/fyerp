package com.graly.promisone.base.entitymanager.dialog;

import java.util.Date;
import java.util.LinkedHashMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.graly.promisone.base.entitymanager.editor.EntityTableManager;
import com.graly.promisone.base.entitymanager.forms.QueryForm;
import com.graly.promisone.base.ui.forms.field.IField;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.SWTResourceCache;

public class EntityQueryDialog extends TitleAreaDialog {
	protected StringBuffer sb;
	protected QueryForm queryForm;
	protected EntityTableManager tableManager;
	
	public EntityQueryDialog(Shell parent) {
        super(parent);
    }
	
	public EntityQueryDialog(Shell parent, EntityTableManager tableManager){
		this(parent);
		this.tableManager = tableManager;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("search-dialog"));
        setTitle(Message.getString("common.search_Title"));
        setMessage(Message.getString("common.keys"));
        Composite composite = (Composite) super.createDialogArea(parent);
        queryForm = new QueryForm(composite, SWT.NONE, tableManager.getADTable());
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
        return composite;
    }
	
	@Override
    protected void okPressed() {
		createWhereClause();
        super.okPressed();
    }
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }
	
	protected String validate() {
		return null;
	}
	
	public String getKeys() {
		return sb.toString();
	}
	
	public void  createWhereClause() {
		LinkedHashMap<String, IField> fields = queryForm.getFields();
		String modelName = tableManager.getADTable().getModelName() + ".";
		sb = new StringBuffer("");
        sb.append(" 1=1 ");
        for(IField f : fields.values()) {
			Object t = f.getValue();
			if (t instanceof Date) {
				Date cc = (Date)t;
				if(cc != null) {
					sb.append(" AND ");
					sb.append("TO_CHAR(");
					sb.append(modelName);
					sb.append(f.getId());
					sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
					sb.append(I18nUtil.formatDate(cc));
					sb.append("'");
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
			} else {
			}
        }
	}
}

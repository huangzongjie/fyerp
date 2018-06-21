package com.graly.mes.prd;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.forms.DialogForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;


public class CopyToPrdDialog extends InClosableTitleAreaDialog {
	
	private static final Logger logger = Logger.getLogger(CopyToPrdDialog.class);
	
	private ADField adField;
	private CopyToForm form;
	private String flowId;
	
	public CopyToPrdDialog(Shell parent) {
        super(parent);
    }
	
	public CopyToPrdDialog(Shell parent, ADField adField) {
        super(parent);
        this.adField = adField;
    }
	
	@Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setActive();
        setTitleImage(SWTResourceCache.getImage("selectprd-dialog"));
        Composite dlgArea = (Composite) super.createDialogArea(parent);

        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.horizontalSpan = ((GridLayout) dlgArea.getLayout()).numColumns;

        getShell().setText(Message.getString("common.copyfrom"));
        setMessage(Message.getString("common.copyfrom_desc"));

        // create the form.
        Composite panel = new Composite(dlgArea, SWT.NONE);
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        panel.setLayout(new FillLayout());
        
        createControl(panel);

        return dlgArea;
    }
	
	public void createControl(Composite parent){
		form = new CopyToForm(parent, SWT.NONE, "", adField);
	}
	
    @Override
    protected void okPressed() {
    	if (form.saveToObject()){
    		String prdRrn = (String)form.getObject();
    		if(prdRrn == null || "".equals(prdRrn.trim())) {
    			return;
    		}
    		setFlowId(prdRrn);
	        super.okPressed();
    	}
    }
    
    public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getFlowId() {
		return flowId;
	}

	private class CopyToForm extends DialogForm {
    	
    	private ADField adField;
    	
    	public CopyToForm(Composite parent, int style, Object object, ADField adField) {
    		super(parent, style, object);
    		this.adField = adField;
    		createForm();
        }
    	
    	@Override
    	public void addFields() {
    		IField field = getField(adField);
    		if (field == null) {
				return;
			}
			field.setADField(adField);
    	}
    	
    	@Override
        public boolean saveToObject() {
    		if (fields.values() != null && fields.values().size() > 0){
    			IField f = fields.values().iterator().next();
    			this.object = f.getValue();
    		}
    		return true;
        }
    }
}



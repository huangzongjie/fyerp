package com.graly.mes.prd.step;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.mes.prd.model.Operation;


public class OperationDialog extends InClosableTitleAreaDialog {
	EntityTableManager tableManager;
	private static int MIN_DIALOG_WIDTH = 300;
	private static int MIN_DIALOG_HEIGHT = 200;
	private Text textOperation;
	private String operation;
	private Operation prdOperation = null;
	
	public OperationDialog(Shell parent) {
        super(parent);
    }
	
	public OperationDialog(Shell parent, EntityTableManager tableManager){
		this(parent);
		this.tableManager = tableManager;
	}
	
	public OperationDialog(Shell parent, Operation operation) {
		super(parent);
		this.prdOperation = operation;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("operation-dialog"));
        setTitle(Message.getString("common.operation_title"));
        setMessage(Message.getString("common.operationInfo"));
        Composite composite = (Composite) super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
        Composite content = toolkit.createComposite(composite);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Label label = toolkit.createLabel(content, Message.getString("common.operation_label"), SWT.NULL);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textOperation = toolkit.createText(content, null, SWT.NULL | SWT.WRAP | SWT.V_SCROLL);
        textOperation.setLayoutData(new GridData(GridData.FILL_BOTH));
        if(prdOperation != null) {
        	if(prdOperation.getDesciption() != null) {
        		textOperation.setText(prdOperation.getDesciption());
        		textOperation.setSelection(prdOperation.getDesciption().length());
        	}
        }
        return composite;
    }
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.OK_ID) {
			operation = textOperation.getText();
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.MIN; 
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	public Operation getOperation() {
		if(operation == null || "".equals(operation.trim())) {
			return null;
		}		
		if(prdOperation == null){
			prdOperation = new Operation();
			prdOperation.setDesciption(operation);
		} else {
			prdOperation.setDesciption(operation);
		}
		return prdOperation;
	}
	
	public String getOperationContent() {
		if(operation == null || "".equals(operation.trim())) {
			return null;
		}
		return operation;
	}
}

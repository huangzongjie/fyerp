package com.graly.erp.pdm.batchbom;

import java.math.BigDecimal;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BatchUpdateDialog extends InClosableTitleAreaDialog {
	private BatchBomSection batchBomSection;
	protected Text txtUnitQty;
	protected BigDecimal unitQty = BigDecimal.ZERO;
	
	public BatchUpdateDialog(Shell parentShell, BatchBomSection batchBomSection) {
		super(parentShell);
		this.batchBomSection = batchBomSection;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
        setTitleImage(SWTResourceCache.getImage("search-dialog"));
        setTitle("批量修改BOM");
        setMessage("请输入新的单位用量：");
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite body = new Composite(composite, SWT.BORDER);
        body.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        GridLayout gl = new GridLayout(1, true);
//        gl.verticalSpacing = 40;
        body.setLayout(gl);
        body.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Composite client = new Composite(body, SWT.NULL);
        client.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        GridLayout compGl = new GridLayout(2, false);
        compGl.marginTop = 10;
        compGl.marginBottom = 10;
        compGl.marginHeight = 10;
        client.setLayout(compGl);
        client.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        toolkit.createLabel(client, "单位用量：");
        txtUnitQty = toolkit.createText(client, "");
        txtUnitQty.addVerifyListener(new VerifyListener(){

			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = "0123456789.-".indexOf(e.text) >= 0;
			}
        	
        });
		return body;
	}
	
	@Override
	protected void okPressed() {
		String txt = txtUnitQty.getText();
		if(txt == null || "".equals(txt.trim())){
			setMessage(Message.getString("common.input_error"), IMessageProvider.ERROR);
			return;
		}else{
			try {
				unitQty = BigDecimal.valueOf(Double.valueOf(txt));
			} catch (NumberFormatException e) {
				setMessage(Message.getString("common.input_error"), IMessageProvider.ERROR);
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
			batchBomSection.setNewUnitQty(unitQty);
		}
		super.okPressed();
	}

	public BigDecimal getUnitQty() {
		return unitQty;
	}

	public void setUnitQty(BigDecimal unitQty) {
		this.unitQty = unitQty;
	}
}

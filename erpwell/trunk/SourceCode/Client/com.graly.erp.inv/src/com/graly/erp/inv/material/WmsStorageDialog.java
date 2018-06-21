package com.graly.erp.inv.material;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;


//WMS物料库存对话框
public class WmsStorageDialog extends InClosableTitleAreaDialog {
	private static int MIN_DIALOG_WIDTH = 750;
	private static int MIN_DIALOG_HEIGHT = 350;
	private static String TABLE_NAME = "WmsStorage";
	
	private ManagedForm form;
	private String materialId;
	private String warehouseId;

	public WmsStorageDialog(Shell shell,String materialId,String warehouseId) {
		super(shell);
		this.materialId = materialId;
		this.warehouseId =warehouseId;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
		String editorTitle = String.format("WMS库存查询");
        setTitle(editorTitle);
        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		form = new ManagedForm(toolkit, sForm);
		
		ADTable adTable = getADTable();
		WmsStorageSection section = new WmsStorageSection(adTable,materialId,warehouseId);
		section.createContents(form, body);
		return body;
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	protected ADTable getADTable() {
		try {
			ADTable adTable = null;
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
			}
			return adTable;
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
}

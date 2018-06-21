package com.graly.erp.pur.request.refmo;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class RefMoDialog extends TitleAreaDialog {
	private static final Logger logger = Logger.getLogger(RefMoDialog.class);
	private  int MIN_DIALOG_WIDTH = 700;
	private  int MIN_DIALOG_HEIGHT = 400;
	public static String TableName = "WIPManufactureOrder";
	
	protected ADTable table;
	protected ManagedForm managedForm;
	protected RefMoSection moSection;
	
	protected List<ManufactureOrder> mos;

	public RefMoDialog(List<ManufactureOrder> mos) {
		super(UI.getActiveShell());
		this.mos = mos;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
        getADTableOfInvLot();		
        setTitleMessage();
        Composite composite = (Composite) super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
        // ´´½¨LotSection
		createSection(body);
        return composite;
    }
	
	protected void setTitleMessage() {
		setTitle(Message.getString("inv.barcode_manager"));
	}

	protected void createSection(Composite composite) {
		moSection = new RefMoSection(table, this);
		moSection.createContents(managedForm, composite);
	}
	
	protected ADTable getADTableOfInvLot() {
		try {
			if(table == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				table = entityManager.getADTable(0L, getADTableName());
			}
			return table;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, false);
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
	
	public List<ManufactureOrder> getRefMoList() {
		return mos;
	}
	
	public String getADTableName() {
		return TableName;
	}
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
    	createButton(parent, IDialogConstants.CANCEL_ID,
    			Message.getString("common.exit"), false);
    }
	
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
}
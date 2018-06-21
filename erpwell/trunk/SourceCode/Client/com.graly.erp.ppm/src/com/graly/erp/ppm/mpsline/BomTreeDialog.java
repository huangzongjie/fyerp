package com.graly.erp.ppm.mpsline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.MpsLineBom;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomTreeDialog extends InClosableTitleAreaDialog {
	private BomTreeSection bomTreeSection;
	protected ManagedForm managedForm;
	private ADTable adTable;
	private MpsLine mpsLine;

	public BomTreeDialog(Shell parent) {
		super(parent);
	}

	public BomTreeDialog(Shell parent, ADTable adTable, MpsLine mpsLine) {
		super(parent);
		this.adTable = adTable;
		this.mpsLine = mpsLine;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		String dialogTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
		setTitle(dialogTitle);
		Composite composite = (Composite) super.createDialogArea(parent);

		createFormContent(composite);
		return composite;
	}

	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		managedForm = new ManagedForm(toolkit, sForm);

		Composite body = sForm.getForm().getBody();
		configureBody(body);
		try {
			PPMManager ppmManager = Framework.getService(PPMManager.class);
			List<MpsLineBom> boms = ppmManager.getMpsLineBom(mpsLine);
			if (boms == null || boms.size() == 0) {
				boms = new ArrayList<MpsLineBom>();
			}
			bomTreeSection = new BomTreeSection(adTable, boms, mpsLine);
			bomTreeSection.createContents(managedForm, body);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			this.cancelPressed();
			return;
		}
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

	protected Point getInitialSize() {
		Point p = super.getInitialSize();
		p.x = 1000;
		p.y = 680;
		return p;
	}

	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
//		if (IDialogConstants.OK_ID == buttonId) {
//			try {
//				List<MpsLineBom> mpsLineBoms = bomTreeSection.getMpsLineBoms();
//				PPMManager ppmManager = Framework.getService(PPMManager.class);
//				ppmManager.saveMpsLineBom(mpsLine, mpsLineBoms);
//				
//				UI.showInfo(Message.getString("common.save_successed"));
//				okPressed();
//			} catch (Exception e) {
//				ExceptionHandlerManager.asyncHandleException(e);
//				return;
//			}
//		} else if (IDialogConstants.CANCEL_ID == buttonId) {
//			cancelPressed( );
//		}
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), false);
	}
}

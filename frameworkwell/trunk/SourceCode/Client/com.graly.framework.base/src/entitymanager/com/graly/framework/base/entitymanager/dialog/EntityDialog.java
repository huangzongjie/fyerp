package com.graly.framework.base.entitymanager.dialog;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class EntityDialog extends InClosableTitleAreaDialog {
	
	private static final Logger logger = Logger.getLogger(EntityDialog.class);
	public static String DIALOGTYPE_NEW = "New";
	public static String DIALOGTYPE_EDIT = "Edit";
	public static String DIALOGTYPE_VIEW = "View";
	
	protected ADTable table;
	protected ManagedForm managedForm;
	protected ADBase adObject;
	protected List<Form> detailForms = new ArrayList<Form>();
	protected String dialogType;
	
	public EntityDialog(Shell parent, ADTable table, ADBase adObject){
		super(parent);
		this.table = table;
		this.adObject = adObject;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
        setTitle(String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(table, "label")));
        createFormContent(composite);
        return composite;
    }
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout();
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
	
	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		managedForm = new ManagedForm(toolkit, sForm);
		final IMessageManager mmng = managedForm.getMessageManager();
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		EntityForm itemForm = new EntityForm(body, SWT.NONE, adObject, table, mmng);
		itemForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		getDetailForms().add(itemForm);
		
	}

	@Override
    protected void okPressed() {
		super.okPressed();
    }
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			if(saveAdapter()) {
				okPressed();
			} else {
				return;
			}
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	
	public void open(String dialogType) {
		this.dialogType = dialogType;
		this.open();
	}
	
	protected String validate() {
		return null;
	}
	
	protected boolean saveAdapter() {
		try {
			managedForm.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					ADManager entityManager = Framework.getService(ADManager.class);
					ADBase obj = entityManager.saveEntity(getTable().getObjectRrn(), getAdObject(), Env.getUserRrn());
					setAdObject(entityManager.getEntity(obj));
					UI.showInfo(Message.getString("common.save_successed"));
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("Error at EntityDialog saveAdapter() : " + e);
			ExceptionHandlerManager.asyncHandleException(e);
			return false;
		}
		return false;
	}
	
	public void refresh() {
		for (Form detailForm : getDetailForms()) {
			detailForm.setObject(getAdObject());
			detailForm.loadFromObject();
		}
		managedForm.getMessageManager().removeAllMessages();
	}
	
	public ADTable getTable() {
		return table;
	}
	
	public void setDetailForms(List<Form> detailForms) {
		this.detailForms = detailForms;
	}

	public List<Form> getDetailForms() {
		return detailForms;
	}

	public void setAdObject(ADBase adObject) {
		this.adObject = adObject;
	}

	public ADBase getAdObject() {
		return adObject;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btnOk = createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.ok"), false);
		if (DIALOGTYPE_VIEW.equals(dialogType)) {
			btnOk.setEnabled(false);
		}
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}
}

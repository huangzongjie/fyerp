package com.graly.erp.pdm.optional;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.MaterialActual;
import com.graly.erp.pdm.model.MaterialOptional;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class OptionalDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(OptionalDialog.class);
	public static final int REBACK_ID = 10001;
	private static final String TABLE_NAME_OPTIONAL = "PDMOptional";
//	private static final String TABLE_NAME_ACTUAL = "PDMMaterialActual";
	private static int MIN_DIALOG_WIDTH = 500;
	private static int MIN_DIALOG_HEIGHT = 300;
	
	protected IManagedForm form;
	protected Bom object;
	protected Long parentRrn;
	protected Material root;
	protected ADTable adTable;
	OptionalForm optionalForm;
	OptionalInfoForm infoForm;
	private MaterialActual materialActual;
	private Material optionalMaterial;
	private ADManager adManager;
	private boolean isNeedBack = false;
	
	public OptionalDialog(Shell parent) {
        super(parent);
    }
	
	public OptionalDialog(Shell parent, Material root,
			ADTable adTable, Bom object, Long parentRrn, boolean isNeedBack){
		this(parent);
		this.root = root;
		this.adTable = adTable;
		this.object = object;
		this.parentRrn = parentRrn;
		this.isNeedBack = isNeedBack;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
        setTitleImage(SWTResourceCache.getImage("bomtitle"));
        try{
			String editorTitle = String.format(Message.getString("common.editor"),
					I18nUtil.getI18nMessage(adTable, "label"));
			setTitle(editorTitle);
		} catch (Exception e){
		}
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());		
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		form = new ManagedForm(toolkit, sForm);
		configureBody(body);

		Composite content = toolkit.createComposite(body, SWT.NULL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        content.setLayoutData(gd);
        content.setLayout(new GridLayout(1, true));
		
        optionalForm = new OptionalForm(content, SWT.NULL, adTable,
        		parentRrn, object, this);
		optionalForm.setLayoutData(new GridData(GridData.FILL_BOTH));		
		infoForm = new OptionalInfoForm(content, SWT.NULL, null, adTable,
				form.getMessageManager(), root);
		infoForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		infoForm.setParentDialog(this);
		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return composite;
	}
	
	protected void initAdTableOfBom() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, TABLE_NAME_OPTIONAL);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
		} catch(Exception e) {
			logger.error("OptionalDialog : initAdTableOfBom()", e);
		}
	}
	
	protected void optionalChanaged(MaterialOptional om) {
		infoForm.setObject(om);
		infoForm.loadFromObject();
	}

	@Override
    protected void okPressed() {
        super.okPressed();
    }
	
	@Override
	protected void buttonPressed(int buttonId) {
		try {
			form.getMessageManager().removeAllMessages();
			if (IDialogConstants.OK_ID == buttonId) {
				if (optionalForm.getCurrentOptional() != null) {
					if (infoForm.getObject() != null) {
						boolean saveFlag = true;
						if (!infoForm.saveToObject()) {
							saveFlag = false;
						}
						if (saveFlag) {
							generateMaterialActual();
							okPressed();
						}
					}
				} else {
					UI.showError(Message.getString("pdm.select_optional"));
					return;
				}
			}
			else if(REBACK_ID == buttonId) {
				rebackPressed();
			}
			else if (IDialogConstants.CANCEL_ID == buttonId) {
				cancelPressed();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at OptionalDialog : okPressed " + e);
		}
	}
	
	public void generateMaterialActual() {
		try {
			MaterialOptional mo = optionalForm.getCurrentOptional();
			MaterialOptional moUnitDis = (MaterialOptional)infoForm.getObject();
			
			if(adManager == null)
				adManager = Framework.getService(ADManager.class);
			// 如果已存在实际物料，则更新已存在实际物料的，否则新建一个Actual Material
			long childRrn = object.getBomTypeChildRrn() != null ? object.getBomTypeChildRrn() : mo.getChildRrn();
			String whereClause = " materialRrn = " + root.getObjectRrn() + " AND childRrn = " + childRrn;
			List<MaterialActual> list = adManager.getEntityList(Env.getOrgRrn(), MaterialActual.class, 2, whereClause, null);
			materialActual = null;
			optionalMaterial = mo.getOptionMaterial();;
			if(list != null && list.size() > 0) {
				materialActual = list.get(0);
			} else {
				materialActual = new MaterialActual();
				materialActual.setOrgRrn(mo.getOrgRrn());
				materialActual.setMaterialRrn(root.getObjectRrn());
				if(object.getBomTypeChildRrn() != null) {
					materialActual.setChildRrn(object.getBomTypeChildRrn());				
				} else {
					materialActual.setChildRrn(object.getChildRrn());				
				}
			}
			materialActual.setActualRrn(mo.getOptionRrn());
			materialActual.setUnitQty(moUnitDis.getUnitQty());
			materialActual.setDescription(moUnitDis.getDescription());
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at EnableBomDialog : generateMaterialActual " + e);
		}
	}
	
	public MaterialActual getMaterialActual() {
		return materialActual;
	}
	
	public Material getOptionalMaterial() {
		return optionalMaterial;
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
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		Message.getString("common.ok"), false);
        createButton(parent, IDialogConstants.CANCEL_ID,
                Message.getString("common.cancel"), false);
        createButton(parent, REBACK_ID,
        		Message.getString("pdm.optional_reback"), false);
        if(!isNeedBack) {
        	getButton(REBACK_ID).setEnabled(false);
        }
    }
	
	protected void rebackPressed() {
		setReturnCode(REBACK_ID);
		close();
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

}

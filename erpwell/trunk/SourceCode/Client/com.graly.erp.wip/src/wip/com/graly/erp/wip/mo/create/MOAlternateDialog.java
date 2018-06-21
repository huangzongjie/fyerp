package com.graly.erp.wip.mo.create;

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
import com.graly.erp.pdm.model.MaterialAlternate;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;

public class MOAlternateDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(MOAlternateDialog.class);
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 300;
	
	protected IManagedForm form;
	protected ManufactureOrderBom moBom;	//所选中的MOBom
	protected Long parentRrn;	//根MO对应的物料的objectRrn
	protected Material root;	//根MO对应的物料
	protected ADTable adTable;
	MOAlternateMasterForm masterForm;
	MOAlternateDetailForm detailForm;
	
	public enum MoType { EDIT, NEW };
	private MoType moType;
	
	public MOAlternateDialog(Shell parent) {
        super(parent);
    }
	
	public MOAlternateDialog(Shell parent, Material root,
			ManufactureOrderBom moBom, Long parentRrn, ADTable adTable, MoType moType) {
		this(parent);
		this.root = root;
		this.moBom = moBom;
		this.parentRrn = parentRrn;
		this.adTable = adTable;
		this.moType = moType;
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
		
        masterForm = new MOAlternateMasterForm(content, SWT.NULL, adTable,
        		parentRrn, moBom, this);
        masterForm.setLayoutData(new GridData(GridData.FILL_BOTH));
        detailForm = new MOAlternateDetailForm(content, SWT.NULL, null, adTable,
				form.getMessageManager(), root);
        detailForm.setLayoutData(new GridData(GridData.FILL_BOTH));
        detailForm.setParentDialog(this);
        
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return composite;
	}

	protected void alternateChanaged(MaterialAlternate alternate) {
		detailForm.setObject(alternate);
		detailForm.loadFromObject();
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
				if (masterForm.getCurrentAlternate() != null) {
					if (detailForm.getObject() != null) {
						boolean saveFlag = true;
						// 验证单位用量不能为空
						if (!detailForm.saveToObject()) {
							saveFlag = false;
						}
						if (saveFlag) {
							PropertyUtil.copyProperties(detailForm.getObject(),
									detailForm.getObject(), detailForm.getFields());
							// 将Alternate中数据设置到moBom中
							changeMoBomContent();
							okPressed();
						}
					}
				} else {
					UI.showError(Message.getString("pdm.select_alternate"));
					return;
				}
			} else if (IDialogConstants.CANCEL_ID == buttonId) {
				cancelPressed();
			}
		} catch (Exception e) {
			logger.error("Error at MOAlternateDialog : okPressed " + e);
			return;
		}
	}
	
	public void changeMoBomContent() {
		MaterialAlternate alternate = (MaterialAlternate)detailForm.getObject();
		switch(moType) {
		case NEW :
			moBom.setMaterialRrn(alternate.getAlternateRrn());
			// 单位用量设置?
			moBom.setUnitQty(alternate.getUnitQty());
			moBom.setDescription(alternate.getDescription());
			moBom.setMaterial(alternate.getAlternateMaterial());
			break;
		case EDIT :
			ManufactureOrderBom newBom = new ManufactureOrderBom ();
			newBom.setMaterialRrn(alternate.getAlternateRrn());
			newBom.setUnitQty(alternate.getUnitQty());
			newBom.setQty(moBom.getQty());
			newBom.setDescription(alternate.getDescription());
			newBom.setMaterial(alternate.getAlternateMaterial());
			moBom = newBom;
			break;
		}
	}
	
	public ManufactureOrderBom getUpdateBom() {
		return this.moBom;
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

	public Material getRoot() {
		return this.root;
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
}

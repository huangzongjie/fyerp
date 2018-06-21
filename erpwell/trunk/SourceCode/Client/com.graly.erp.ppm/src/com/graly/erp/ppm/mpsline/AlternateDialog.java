package com.graly.erp.ppm.mpsline;

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
import com.graly.erp.pdm.model.MaterialAlternate;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.MpsLineBom;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AlternateDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(AlternateDialog.class);
	private static final String TABLE_NAME = "PDMAlternate";
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 300;

	protected IManagedForm form;
	protected MpsLineBom mpsLineBom; // 所选中的MpsLineBom
	protected Long parentRrn; // 根MpsLineBom对应的物料的objectRrn
	protected Material root; // 根MpsLineBom对应的物料
	protected ADTable adTable;
	AlternateMasterForm masterForm;
	AlternateDetailForm detailForm;
	List<MpsLineBom> mpsLineBoms;
	MpsLine mpsLine;

	public AlternateDialog(Shell parent) {
		super(parent);
	}

	public AlternateDialog(Shell parent, Material root, MpsLine mpsLine,
			MpsLineBom mpsLineBom, Long parentRrn, List<MpsLineBom> mpsLineBoms) {
		this(parent);
		this.root = root;
		this.mpsLine = mpsLine;
		this.mpsLineBom = mpsLineBom;
		this.parentRrn = parentRrn;
		this.mpsLineBoms = mpsLineBoms;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
		initAdTableOfAlternate();
		try {
			String editorTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
			setTitle(editorTitle);
		} catch (Exception e) {
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

		masterForm = new AlternateMasterForm(content, SWT.NULL, adTable, parentRrn, mpsLineBom, this);
		masterForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		detailForm = new AlternateDetailForm(content, SWT.NULL, null, adTable, form.getMessageManager(), root);
		detailForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		detailForm.setParentDialog(this);

		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}

	protected void initAdTableOfAlternate() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
			}
		} catch (Exception e) {
			logger.error("MOAlternateDialog : initAdTableOfAlternate()", e);
		}
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
							PropertyUtil.copyProperties(detailForm.getObject(), detailForm.getObject(), detailForm.getFields());
							// 将Alternate中数据设置到MpsLineBom中
//							int index = mpsLineBoms.indexOf(mpsLineBom);
							changeBomTreeContent();
//							// 替换掉原有的selectedBom
//							mpsLineBoms.remove(mpsLineBom);
//							mpsLineBoms.add(index, getUpdateBom());
//							// 保存到数据库
//							PPMManager ppmManager = Framework.getService(PPMManager.class);
//							ppmManager.saveMpsLineBom(mpsLine, mpsLineBoms);
//							UI.showInfo(Message.getString("common.save_successed"));
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
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MOAlternateDialog : okPressed " + e);
			return;
		}
	}

	public void changeBomTreeContent() {
		MaterialAlternate alternate = (MaterialAlternate) detailForm.getObject();
		mpsLineBom.setMaterialRrn(alternate.getAlternateRrn());
		// 单位用量设置?
		mpsLineBom.setUnitQty(alternate.getUnitQty());
		mpsLineBom.setDescription(alternate.getDescription());
		mpsLineBom.setMaterial(alternate.getAlternateMaterial());
	}

	public MpsLineBom getUpdateBom() {
		return this.mpsLineBom;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.cancel"), false);
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

//	public List<MpsLineBom> getMpsLineBoms() {
//		return mpsLineBoms;
//	}
	
	// buttonPressed()完成后,mpsLine重新保存到数据库，
	// 所以调用该方法可以获得最新的mpsLine
	public MpsLine getUpdateMpsLine() {
		return mpsLine;
	}

	public Material getRoot() {
		return root;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

}

package com.graly.erp.inv.generatelot;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.ConditionItem;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class GenerateLotDialog extends ExtendDialog {
	public static final int INPUT_ID = 10001;
	private static final Logger logger = Logger.getLogger(GenerateLotDialog.class);
	protected IManagedForm form;
	private static int MIN_DIALOG_WIDTH = 550;
	private static int MIN_DIALOG_HEIGHT = 300;

	protected ManagedForm managedForm;
	protected ConditionItem adObject = new ConditionItem();
	protected List<Form> detailForms = new ArrayList<Form>();
	protected ADTable table;
	protected CTabFolder tabs;
	private Section section;
	private IFormPart spart;
	private String FIELDNAME_LOTTYPE = "lotType";
	private String FIELDNAME_BACH = "batchNumber";
	private String FIELDNAME_IS_LOTCONTROL = "isLotControl";
	private String FIELDNAME_MATERIALRRN = "materialRrn";

	public GenerateLotDialog() {
		super();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		initAdTableByTableId();
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		setTitle(String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(table, "label")));
		createFormContent(composite);
		return composite;
	}

	protected void initAdTableByTableId() {
		try {
			Long adTableRrn = Long.parseLong(this.getTableId());
			if (adTableRrn != null) {
				table = new ADTable();
				table.setObjectRrn(adTableRrn);
				ADManager entityManager = Framework.getService(ADManager.class);
				table = entityManager.getADTableDeep(adTableRrn);
			}
		} catch (Exception e) {
			logger.error("GenerateLotDialog : initAdTableByTableId()", e);
		}
	}

	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		managedForm = new ManagedForm(toolkit, sForm);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);

		createContents(managedForm, body);

		addLotTypeListener();
	}

	public void createContents(IManagedForm form, Composite parent) {
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();

		section = toolkit.createSection(parent, sectionStyle);
		toolkit.createCompositeSeparator(section);
		createToolBar(section);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite client = toolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		layout.numColumns = 1;
		client.setLayout(gridLayout);

		spart = new SectionPart(section);
		form.addPart(spart);
		section.setText(String.format(Message.getString("common.detail"), I18nUtil.getI18nMessage(table, "label")));

		createContent(client, toolkit);
		section.setClient(client);
	}

	private void createContent(Composite client, FormToolkit toolkit) {
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() }, new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : table.getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			EntityForm itemForm = new EntityForm(getTabs(), SWT.NONE, new ConditionItem(), tab, mmng);
			detailForms.add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
	}

	private void createToolBar(Section section2) {
	}

	@Override
	protected void buttonPressed(int buttonId) {
		try {
			if (IDialogConstants.OK_ID == buttonId || INPUT_ID == buttonId) {
				form.getMessageManager().setAutoUpdate(false);
				form.getMessageManager().removeAllMessages();
				ConditionItem conditionItem = new ConditionItem();
					if (getAdObject() != null) {
						boolean saveFlag = true;
						for (Form detailForm : getDetailForms()) {
							if (!detailForm.saveToObject()) {
								saveFlag = false;
							}
						}
						if (saveFlag) {
							for (Form detailForm : getDetailForms()) {
								conditionItem = (ConditionItem) detailForm.getObject();								
								form.getMessageManager().setAutoUpdate(true);
								
								ADManager adManager = Framework.getService(ADManager.class);
								Material material = new Material();
								material.setObjectRrn(conditionItem.getMaterialRrn());
								material = (Material)adManager.getEntity(material);
								if(Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
									UI.showError(String.format(Message.getString("inv.material_is_not_need_generate_lot"),
											material.getMaterialId()));
									return;
								}
								if(IDialogConstants.OK_ID == buttonId) {
									LotDialog lotDialog = new LotDialog(UI.getActiveShell(), conditionItem);
									if (lotDialog.open() == IDialogConstants.OK_ID) {
									}
								} else {
									if (material.getIsLotControl() && material.getLotType() != null) {
										InputLotDialog ild = new InputLotDialog(UI.getActiveShell(),
												conditionItem, material);
										ild.open();
									}
								}
							}
						}
					}
					form.getMessageManager().setAutoUpdate(true);
			} else if (IDialogConstants.CANCEL_ID == buttonId) {
				cancelPressed();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	private void addLotTypeListener() {
		IField lotType = getIField(FIELDNAME_LOTTYPE);
		if (lotType != null)
			lotType.addValueChangeListener(getBathNumberChangedListener());
	}

	private IValueChangeListener getBathNumberChangedListener() {
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				refreshBathNumberName(newValue);
			}
		};
	}

	private void refreshBathNumberName(Object newValue) {
		IField isLotControl = getIField(FIELDNAME_IS_LOTCONTROL);
		IField materialRrn = getIField(FIELDNAME_MATERIALRRN);
		try {
			if (materialRrn == null || materialRrn.getValue() == null) {
				return;
			}
			Material material = new Material();
			ADManager adManager = Framework.getService(ADManager.class);
			String whereClause = " objectRrn ='" + materialRrn.getValue() + "' ";
			List<Material> materials = adManager.getEntityList(Env.getOrgRrn(), Material.class, Integer.MAX_VALUE, whereClause, "");
			if (materials.size() > 0)
				material = materials.get(0);

			if (Boolean.TRUE.equals(material.getIsLotControl())) {
				isLotControl.setValue(true);
			} else {
				isLotControl.setValue(false);
			}
			isLotControl.refresh();

			IField bachNumber = getIField(FIELDNAME_BACH);
			ADField adFieldQty = (ADField) bachNumber.getADField();
			String label = bachNumber.getLabel();
			Label qtyLabel = (Label) bachNumber.getControls()[0];
			qtyLabel.setRedraw(true);
			if (Lot.LOTTYPE_BATCH.equals(newValue)) {
				qtyLabel.setText(Message.getString("wip.lot_qty_label"));
				adFieldQty.setIsMandatory(true);
			} else {
				qtyLabel.setText(label);
				adFieldQty.setIsMandatory(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	private IField getIField(String fieldId) {
		for (Form form : getDetailForms()) {
			IField f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return null;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, INPUT_ID,
				Message.getString("inv.input_lotId"), false);
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("inv.generate_lot"), false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
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

	public void setAdObject(ConditionItem adObject) {
		this.adObject = adObject;
	}

	public ConditionItem getAdObject() {
		return adObject;
	}

	public CTabFolder getTabs() {
		return tabs;
	}

	public void setTabs(CTabFolder tabs) {
		this.tabs = tabs;
	}

	public void setTable(ADTable table) {
		this.table = table;
	}
}

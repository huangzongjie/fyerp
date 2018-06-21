package com.graly.erp.vdm.materialassessment;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.MaterialAssessment;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MaterialAssSection extends MasterSection {
	protected ToolItem saveItem;
	protected static final String TABLE_NAME_MATERIAL_ASSESSMENT = "VDMMaterialAssessment";
	protected static final String TABLE_NAME_MATERIAL_ASSESSMENT_FOR_SEARCH = "VDMMaterialAssessmentForSearch";
	protected static final String OK_ID = "0";
	private static final Logger logger = Logger.getLogger(MaterialAssSection.class);
	private ADTable adTable;
	private Label label;
	private int mStyle = SWT.SINGLE | SWT.BORDER;

	IManagedForm IMform;
	Composite decriptionArea;

	public MaterialAssSection(EntityTableManager tableManager) {
		super(tableManager);
		adTable = tableManager.getADTable();
	}

	@Override
	public void createContents(IManagedForm form, Composite parent) {
		this.IMform = form;
		createContents(form, parent, Section.TITLE_BAR);
		List<MaterialAssessment> assessments = new ArrayList<MaterialAssessment>();
		assessments = getAssessmentLength("");

		decriptionArea = crateUI(assessments);

		section.setDescriptionControl(decriptionArea);
	}

	@Override
	public void refresh() {
		assPanelfresh("");
	}

	public void assPanelfresh(String whereClause) {
		decriptionArea.dispose();
		section.redraw();
		List<MaterialAssessment> assessments = new ArrayList<MaterialAssessment>();
		assessments = getAssessmentLength(whereClause);

		decriptionArea = crateUI(assessments);

		section.setDescriptionControl(decriptionArea);
		section.layout();
		IMform.getForm().reflow(true);
	}

	@Override
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		final FormToolkit toolkit = form.getToolkit();
		final ADTable table = getTableManager().getADTable();
		GridLayout layout1 = new GridLayout();
		layout1.marginWidth = 0;
		layout1.marginHeight = 0;
		parent.setLayout(layout1);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		section = toolkit.createSection(parent, sectionStyle);
		section.setText(I18nUtil.getI18nMessage(table, "label"));
		section.marginWidth = 3;
		section.marginHeight = 5;
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
		section.setLayout(new FillLayout());
		section.setClient(client);
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			ADTable assessmentSearchTable = getADTableOfAssessmentSearch();
			EntityTableManager searchTableManager = new EntityTableManager(assessmentSearchTable);
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), searchTableManager, this){
				@Override
			    protected void okPressed() {
					createWhereClause();
					setReturnCode(OK);
					setWhereClause(sb.toString());
					refresh();
					assPanelfresh(getWhereClause());
			        this.setVisible(false);
			    }
			};
			queryDialog.open();
		}
	}
	
	

	protected void createToolItemSave(ToolBar tBar) {
		saveItem = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_ASSESS);
		saveItem.setText(Message.getString("vdm.comment_material"));
		saveItem.setImage(SWTResourceCache.getImage("new"));
		saveItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				showSaveAdapter(event);
			}
		});
	}

	protected void showSaveAdapter(SelectionEvent event) {
		IMform.getMessageManager().removeAllMessages();
		ADTable adTable = initAdTableOfMaterialAssessment(TABLE_NAME_MATERIAL_ASSESSMENT);
		AssessmentSaveDialog assessDialog = new AssessmentSaveDialog(UI.getActiveShell(), adTable, new MaterialAssessment());
		assessDialog.open(AssessmentSaveDialog.DIALOGTYPE_NEW);
		if (OK_ID.equals(assessDialog.getButton())) {
			assPanelfresh("");
		}
	}

	protected ADTable initAdTableOfMaterialAssessment(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("materialAssSection : initAdTableOfMaterialAssessment()", e);
		}
		return null;
	}

	protected List<MaterialAssessment> getAssessmentLength(String whereClase) {
		try {
			VDMManager vdmManager = Framework.getService(VDMManager.class);
			List<MaterialAssessment> assessments = new ArrayList<MaterialAssessment>();
			assessments = vdmManager.getMaterialAssessment(whereClase);
			return assessments;
		} catch (Exception e) {
			logger.error("MaterialSection : getAssessMentLength", e);
			ExceptionHandlerManager.asyncHandleException(e);
			return null;
		}
	}

	private Composite crateUI(List<MaterialAssessment> assessments) {
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		decriptionArea = toolkit.createComposite(section, SWT.NULL);
		GridLayout layout = new GridLayout(1, true);
		layout.numColumns = 14;
		decriptionArea.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.verticalAlignment = SWT.TOP;
		decriptionArea.setLayoutData(gd);
		int length = 0;
		if (assessments != null) {
			length = assessments.size();
		}
		MaterialAssessment assesment = new MaterialAssessment();
		for (int j = 0; j < length; j++) {
			assesment = assessments.get(j);
			int i = 0;
			for (ADField adField : adTable.getFields()) {
				String displayLabel = I18nUtil.getI18nMessage(adField, "label");
				if (!adField.getIsMain()) {
					continue;
				} else {
					label = toolkit.createLabel(decriptionArea, displayLabel);
				}
				i++;
				if (FieldType.TEXTAREA.equalsIgnoreCase(adField.getDisplayType())) {
					int num = (i - 1) % 2;
					if (num != 0) {
						Label l = toolkit.createLabel(decriptionArea, "");
						GridData sameLine_gd = new GridData();
						sameLine_gd.horizontalSpan = 7;
						l.setLayoutData(sameLine_gd);
						l.moveAbove(label);
					}
					gd = new GridData();
					gd.verticalSpan = 3;
					label.setLayoutData(gd);
					Text textAss;
					textAss = toolkit.createText(decriptionArea, "", SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
					textAss.setBackground(textAss.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
					Object objec = PropertyUtil.getPropertyForString(assesment, adField.getName());
					textAss.setText(objec.toString());
					gd = new GridData(GridData.FILL_BOTH);
					gd.horizontalSpan = 13;
					gd.widthHint = 1;
					gd.heightHint = 80;
					gd.verticalSpan = 3;
					textAss.setLayoutData(gd);
				} else {
					Text textOther;
					textOther = toolkit.createText(decriptionArea, "", mStyle | SWT.READ_ONLY);

					textOther.setBackground(textOther.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
					Object objec = PropertyUtil.getPropertyForString(assesment, adField.getName());
					String value = (objec == null ? "" : objec.toString());
					textOther.setText(value);
					gd = new GridData();
					gd.horizontalAlignment = SWT.FILL;
					gd.grabExcessHorizontalSpace = true;
					gd.horizontalSpan = 6;
					textOther.setLayoutData(gd);

				}
			}
			if (j != length) {
				label = new Label(decriptionArea, SWT.SEPARATOR | SWT.HORIZONTAL);
				gd = new GridData();
				gd.horizontalSpan = 14;
				label.setLayoutData(gd);
			}
		}
		label = toolkit.createLabel(decriptionArea, "");
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 13;
		label.setLayoutData(gridData);
		Button button = toolkit.createButton(decriptionArea, Message.getString("vdm.comment_material"), SWT.FLAT | SWT.TOGGLE);
		button.setVisible(false);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		button.setLayoutData(gridData);
		return decriptionArea;
	}
	
	protected ADTable getADTableOfAssessmentSearch() {
		ADTable assSearchTable;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			assSearchTable = entityManager.getADTable(0L, TABLE_NAME_MATERIAL_ASSESSMENT_FOR_SEARCH);
			assSearchTable = entityManager.getADTableDeep(assSearchTable.getObjectRrn());
			return assSearchTable;
		} catch (Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
}

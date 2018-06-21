package com.graly.erp.wip.mo.molinecreate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.ui.forms.field.DateTimeField;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class NewMOLineSection extends EntitySection{
	
	protected Section section;
	protected IFormPart spart;

	public NewMOLineSection(ADTable adTable, ADBase adObject) {
		super.table = adTable;
		this.adObject = adObject;
	}

	public void createContents(IManagedForm form, Composite parent) {
		super.createContents(form, parent);
		refresh();
	}
	
	public void createToolBar(Section section) {
		
	}

	protected void createSectionContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			NewMOLineForm itemForm = new NewMOLineForm(getTabs(), SWT.NONE, tab, mmng);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
	}
}
	class NewMOLineForm extends EntityForm{

		public NewMOLineForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
			super(parent, style, tab, mmng);
		}

		public IField getField(ADField adField){
			String displayText = adField.getDisplayType();
			String name = adField.getName();
			String displayLabel = I18nUtil.getI18nMessage(adField, "label");
			if(adField.getIsMandatory()) {
				displayLabel = displayLabel + "*";
			}
			IField field = null;
			if (FieldType.DATETIME.equalsIgnoreCase(displayText)){
				int style = SWT.BORDER | SWT.TIME | SWT.SHORT;
				DateTimeField fe = new OnlyTimeField(name , style);
		        fe.setLabel(displayLabel);
		        fe.setValue(null);
		        field = fe;
				addField(name, field);
			} else{
				return super.getField(adField);
			}
			return field;
		}
	}


package com.graly.erp.inv.modifylotid;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;

public class ModifyLotIdForm extends EntityForm {
	private static final Logger logger = Logger.getLogger(ModifyLotIdForm.class);

	private IField fieldNewLotId;
	private static final String NEW_LOTID = "newLotId";

	public ModifyLotIdForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
		super(parent, style, tab, mmng);
	}

	@Override
	public void addFields() {
		super.addFields();
		try {
			IField separator = createSeparatorField("separator", "");
			ADField adField = new ADField();
			adField.setIsMandatory(true);
			adField.setIsActive(true);
			adField.setIsDisplay(true);
			adField.setIsSameline(true);
			separator.setADField(adField);
			addField("separator", separator);
			fieldNewLotId = createText(NEW_LOTID, Message.getString("inv.newlotid"), "", 32);
			addField(NEW_LOTID, fieldNewLotId);
		} catch (Exception e) {
			logger.error("HoldLotForm : Init listItem", e);
		}
	}

	@Override
	public void loadFromObject() {
		if (object != null) {
			for (IField f : fields.values()) {
				if (!(f instanceof SeparatorField) && !f.equals(fieldNewLotId)) {
					Object o = PropertyUtil.getPropertyForIField(object, f.getId());
					f.setValue(o);
				}
			}
			refresh();
			setEnabled();
		}
	}

	@Override
	public void setEnabled() {
		for (IField f : fields.values()) {
			f.setEnabled(false);
		}
		fieldNewLotId.setEnabled(true);
	}
}

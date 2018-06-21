package com.graly.erp.bj.pdm.material;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.util.PropertyUtil;

public class BJLotTypeForm extends EntityForm {
	private static final Logger logger = Logger.getLogger(BJLotTypeForm.class);

	private IField fieldNewLotId;
	private static final String NEW_LOTID = "newLotId";

	public BJLotTypeForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
		super(parent, style, tab, mmng);
	}

 

	@Override
	public void loadFromObject() {
		if (object != null) {
			for (IField f : fields.values()) {
				if(f.getId().equals("lotType")){
					f.setValue("MATERIAL");
				}else if(f.getId().equals("isPurchase")){
					f.setValue(true);
				}else if (!(f instanceof SeparatorField)) {
					Object o = PropertyUtil.getPropertyForIField(object, f.getId());
					f.setValue(o);
				}
			}
			refresh();
			setEnabled();
		}
	}

//	@Override
//	public void setEnabled() {
//		for (IField f : fields.values()) {
//			f.setEnabled(false);
//		}
//		fieldNewLotId.setEnabled(true);
//	}
}

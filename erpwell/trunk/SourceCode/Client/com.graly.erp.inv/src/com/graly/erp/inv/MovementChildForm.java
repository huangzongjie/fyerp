package com.graly.erp.inv;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTextField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.util.PropertyUtil;

/**
 * ʵ�ֵ�����object��, ���object�ѱ��浽DB��, ���ʹ����ĸ����ؼ�Ϊֻ��
 */
public class MovementChildForm extends ChildEntityForm {	
	
	public MovementChildForm(Composite parent, int style, Object object, ADTab tab,
			IMessageManager mmng, Object parentObject) {
		super(parent, style, object, tab, mmng, parentObject);
	}

	public MovementChildForm(Composite parent, int style, Object object,
			ADTable table, IMessageManager mmng, Object parentObject) {
		super(parent, style, object, table, mmng, parentObject);
	}

	/*
	 * ��������object�󣬵���setEnabled(true)ʵ��Form������ĸ����ؼ�Ϊֻ��
	 */
	@Override
	public void loadFromObject() {
		if (object != null){
			for (IField f : fields.values()){
				if (!(f instanceof SeparatorField || f instanceof RefTextField)) {
					Object o = PropertyUtil.getPropertyForIField(object, f.getId());
					f.setValue(o);
				}
			}
			refresh();
			setAllEnabled();
		}
	}
	
	public void setAllEnabled() {
		if (object != null && object instanceof ADBase ) {
			ADBase base = (ADBase)object;
			for (IField f : fields.values()) {
				if (base.getObjectRrn() == null || base.getObjectRrn() == 0) { 
					f.setEnabled(true);
				} else {
					f.setEnabled(false);
				}
			}
		}
	}
}

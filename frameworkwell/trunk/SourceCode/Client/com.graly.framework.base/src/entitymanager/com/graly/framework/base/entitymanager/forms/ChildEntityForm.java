package com.graly.framework.base.entitymanager.forms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.RefTextField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ChildEntityForm extends EntityForm {
	
	private static final Logger logger = Logger.getLogger(ChildEntityForm.class);
	
	private Object parentObject;
	
	public ChildEntityForm(Composite parent, int style, Object object, ADTable table, IMessageManager mmng, Object parentObject) {
		super(parent, style, object, table, mmng);
		this.setParentObject(parentObject);
	}
    
	public ChildEntityForm(Composite parent, int style, Object object, ADTab tab, IMessageManager mmng, Object parentObject) {
		super(parent, style, object, tab, mmng);
		this.setParentObject(parentObject);
    }
	
	public void setEnabled(){
		super.setEnabled();
		for (IField f : fields.values()){
			ADField adField = (ADField)f.getADField();
			if (adField != null && adField.getIsParent() && getParentObject() != null){
				f.setEnabled(false);
			}
		}
	}

	@Override
    public void loadFromObject() {
		if (object != null){
			for (IField f : fields.values()){
				if (!(f instanceof SeparatorField || f instanceof RefTextField
						|| ((ADField)f.getADField()).getIsParent())){
					Object o = PropertyUtil.getPropertyForIField(object, f.getId());
					f.setValue(o);
				}
			}
			refresh();
			setEnabled();
		}
    }
	
	public void loadFromParent() {
		if (getParentObject() != null){
			for (IField f : fields.values()){
				ADField adField = (ADField)f.getADField();
				if (adField != null && adField.getIsParent() && getParentObject() != null){
					Object o = PropertyUtil.getPropertyForIField(getParentObject(), adField.getReferenceRule());
					f.setValue(o);
					f.refresh();
				}
			}
			setEnabled();
		}
	}
	
    @Override
    public void dispose() {
        super.dispose();
        parentObject = null;
    }

	public void setParentObject(Object parentObject) {
		this.parentObject = parentObject;
	}

	public Object getParentObject() {
		return parentObject;
	}
}

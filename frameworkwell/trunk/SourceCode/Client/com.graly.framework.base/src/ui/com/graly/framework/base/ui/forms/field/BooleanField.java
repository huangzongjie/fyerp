package com.graly.framework.base.ui.forms.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.events.SelectionListener;
import com.graly.framework.activeentity.model.ADField;
public class BooleanField extends AbstractField {
	
	Button checkbox;
	
	public BooleanField(String id) {
        super(id);
    }
	
	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		String labelStr = getLabel();
		checkbox = toolkit.createButton(composite, labelStr, SWT.CHECK);
		
		Boolean v = (Boolean)getValue();
        checkbox.setSelection(v.booleanValue());
        checkbox.setEnabled(isEnabled());
        mControls = new Control[1];
        mControls[0] = checkbox;
        checkbox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                setValue(checkbox.getSelection());
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
	}
	
	public Button getCheckboxControl() {
        return (Button)getControls()[0];
    }

    public void setChecked(boolean val) {
        getCheckboxControl().setSelection(val);
    }

    public boolean isChecked() {
        return getCheckboxControl().getSelection();
    }
    
	@Override
	public void refresh() {
		if (getValue() != null){
			setChecked((Boolean)getValue());
		} else {
			setChecked(false);
		}
	}
	
	public String getFieldType() {
		return "boolean";
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		checkbox.setEnabled(enabled);
		super.enableChanged(enabled);
    }
	
	@Override
	public void setValue(Object value) {
		Boolean bool = false;
		if (value instanceof String) {
			if ("Y".equalsIgnoreCase((String)value)) {
				bool = true;
			} else {
				bool = false;
			}
		} else {
			bool = (Boolean)value;
		}
		super.setValue(bool);
	}
}

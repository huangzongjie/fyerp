package com.graly.promisone.base.ui.forms.field;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Control;

import com.graly.promisone.base.ui.forms.field.listener.IValueChangeListener;

public interface IField {
    
	Control[] getControls();
	void createContent(Composite composite, FormToolkit toolkit);
	int getColumnsCount();
	void setLabel(String label);
    String getLabel();
    String getId();
    void setValue(Object value);
    Object getValue();
    void setEnabled(boolean enabled);
    void addValueChangeListener(IValueChangeListener listener);
    void removeValueChangeListener(IValueChangeListener listener);
    Object getADField();
    void setADField(Object adObject);
    String getFieldType();
    void refresh();
}

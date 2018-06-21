package com.graly.promisone.base.ui.forms.field;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.graly.promisone.activeentity.model.ADField;

public class SeparatorField extends AbstractField {

	private int mStyle = SWT.HORIZONTAL;
	Label label;
	
	public SeparatorField(String id) {
		this(id, SWT.NONE);
	}

	public SeparatorField(String id, int style) {
		super(id);
		this.mStyle = mStyle | style;
	}
	
	public void createContent(Composite composite, FormToolkit toolkit) {
		String labelStr = getLabel();
		Label label = toolkit.createSeparator(composite, mStyle);
        mControls = new Control[1];
        mControls[0] = label;
		if (labelStr != null) {
			label.setText(labelStr);
		} 
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        label.setLayoutData(gd);
	}
	
    public int getStyle() {
        return mStyle;
    }

    public void setStyle(int style) {
        mStyle = style;
    }

    public void appendStyle(int style) {
        mStyle |= style;
    }

	public void refresh() {
	}
	
	public String getFieldType() {
		return "separator";
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		super.enableChanged(enabled);
    }
}

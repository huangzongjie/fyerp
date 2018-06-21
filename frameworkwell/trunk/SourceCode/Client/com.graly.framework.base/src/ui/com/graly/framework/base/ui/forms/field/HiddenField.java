package com.graly.framework.base.ui.forms.field;

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

import com.graly.framework.activeentity.model.ADField;

public class HiddenField extends AbstractField {
	
	public HiddenField(String id) {
		super(id);
	}
	
	public void createContent(Composite composite, FormToolkit toolkit) {
		 mControls = new Control[0];
	}

	public void refresh() {
	}
	
	public String getFieldType() {
		return "hidden";
	}
}

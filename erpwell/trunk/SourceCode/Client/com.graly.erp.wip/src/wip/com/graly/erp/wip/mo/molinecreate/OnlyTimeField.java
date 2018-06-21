package com.graly.erp.wip.mo.molinecreate;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.base.ui.forms.field.DateTimeField;
import com.graly.framework.base.ui.util.Env;

public class OnlyTimeField extends DateTimeField {
	DateTime time;

	public OnlyTimeField(String id, int style) {
		super(id, style);
	}

	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		String labelStr = getLabel();
		int i = 0;
		if (labelStr != null) {
			mControls = new Control[2];
			Label label = toolkit.createLabel(composite, labelStr);
			mControls[0] = label;
			i = 1;
		} else {
			mControls = new Control[1];
		}

		Composite parent = toolkit.createComposite(composite);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		parent.setLayoutData(gd);
		parent.setLayout(layout);
		toolkit.adapt(parent);
		toolkit.paintBordersFor(parent);

		GridData gd1;
		layout.numColumns = 1;
		gd1 = new GridData();
		gd1.horizontalAlignment = SWT.FILL;
		gd1.grabExcessHorizontalSpace = true;
		time = new DateTime(parent, SWT.TIME | SWT.BORDER | SWT.SHORT);
		time.setLayoutData(gd1);
		if (this.getADField() != null && ((ADField) this.getADField()).getIsReadonly()) {
			time.setEnabled(false);
		}
		toolkit.adapt(time);
		toolkit.paintBordersFor(time);
		Date val = (Date) getValue();
		if (val != null) {
			time.setHours(val.getHours());
			time.setMinutes(val.getMinutes());
		}
		time.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				DateTime t = (DateTime) e.widget;
				Date tempTime = (Date) getValue();
				if (tempTime == null) {
					tempTime = Env.getSysDate();
				}
				tempTime.setHours(t.getHours());
				tempTime.setMinutes(t.getMinutes());
				setValue(tempTime);
			}
		});
		mControls[i] = parent;

	}

	@Override
	public void refresh() {
		if (getValue() != null) {
			Date val = (Date) getValue();
			time.setHours(val.getHours());
			time.setMinutes(val.getMinutes());
		}else {
			Date val = Env.getSysDate();;
			setValue(val);
		}
	}

	@Override
	public void enableChanged(boolean enabled) {
		time.setEnabled(enabled);
		super.enableChanged(enabled);
	}
}

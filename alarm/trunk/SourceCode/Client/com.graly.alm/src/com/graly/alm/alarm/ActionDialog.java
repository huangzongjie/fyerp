package com.graly.alm.alarm;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ActionDialog extends EntityDialog {
	private static final String FieldName_IsRepeat = "isRepeat";
	private static final String FieldName_RepeatInterval = "repeatInterval";

	public ActionDialog(Shell parent, ADTable table, ADBase adObject) {
		super(parent, table, adObject);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		try {
			if (IDialogConstants.OK_ID == buttonId) {
				managedForm.getMessageManager().removeAllMessages();
				if (getAdObject() != null) {
					boolean saveFlag = true;
					for (Form detailForm : getDetailForms()) {
						if (!detailForm.saveToObject()) {
							saveFlag = false;
							return;
						}
					}
					if (saveFlag) {
						okPressed();
					}
				}
			}else{
				cancelPressed();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		
	}

	protected void createFormContent(Composite composite) {
		super.createFormContent(composite);
		addIsRepeatListener();
		
	}

	private void addIsRepeatListener() {
		IField isRepeat = getIField(FieldName_IsRepeat);
		IField repeatInterval = getIField(FieldName_RepeatInterval);
		if ("true".equals(isRepeat.getValue().toString())) {
			repeatInterval.setEnabled(true);
		} else {
			repeatInterval.setEnabled(false);
		}
		repeatInterval.refresh();
		if (isRepeat != null)
			isRepeat.addValueChangeListener(getIsRepeatChangedListener());
	}

	private IValueChangeListener getIsRepeatChangedListener() {
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				refreshAlarmStatus(newValue);
			}
		};
	};

	private void refreshAlarmStatus(Object newValue) {
		IField repeatInterval = getIField(FieldName_RepeatInterval);
		if ("true".equals(newValue.toString())) {
			repeatInterval.setEnabled(true);
		} else {
			repeatInterval.setEnabled(false);
			repeatInterval.setValue("");
		}
		repeatInterval.refresh();
	}

	private IField getIField(String fieldId) {
		for (Form form : getDetailForms()) {
			IField f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return null;
	}
}

package com.graly.erp.wip.calendarhour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.calendar.model.CalendarHour;
import com.graly.erp.base.client.BASManager;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class WeekTypeDialog extends InClosableTitleAreaDialog {
	IManagedForm managedForm;
	IMessageManager mmng;
	Text text;
	String textValue;
	TableViewer viewer;
	public WeekTypeDialog(Shell parent, IManagedForm managedForm, TableViewer viewer){
		super(parent);
		this.managedForm = managedForm;
		mmng = managedForm.getMessageManager();
		this.viewer = viewer;
	}

	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		setTitle(String.format(Message.getString("common.editor"),Message.getString("wip.weektype")));
        Composite composite = (Composite) super.createDialogArea(parent);
        
        FormToolkit toolkit = managedForm.getToolkit();
        Composite client = toolkit.createComposite(composite, SWT.BORDER);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_BOTH));
        toolkit.createLabel(client, Message.getString("wip.weektype") + "* ");
        text = toolkit.createText(client, "", SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        return composite;
    }
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			String newWeekType = text.getText();
			if(newWeekType == null || ("".equals(newWeekType))){
				UI.showError(Message.getString("wip.weektype_is_not_null"));
				return;
			}else{
				try {
					BASManager basManager = Framework.getService(BASManager.class);
					List<CalendarHour> getInput = (List<CalendarHour>)viewer.getInput();
					Map weekTypeMap = new HashMap<String, String>();
					for (CalendarHour calendarHour : getInput) {
						if(!weekTypeMap.containsKey(calendarHour.getWeekType())){
							weekTypeMap.put(calendarHour.getWeekType(), calendarHour.getWeekType());
						}
					}
					if(weekTypeMap.containsKey(newWeekType)){
						UI.showError(Message.getString("wip.weektype_is_exist"));
						return;
					}
				} catch (Exception e) {
					ExceptionHandlerManager.asyncHandleException(e);
					return;
				}
				setTextValue(newWeekType);
			}
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			mmng.removeAllMessages();
			cancelPressed();
		}
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
}

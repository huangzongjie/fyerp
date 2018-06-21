package com.graly.erp.pur.calendarday;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.base.calendar.model.CalendarDay;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class SearchCalendarDayDialog extends EntityDialog {
	private CalendarDay calendarDay;

	public SearchCalendarDayDialog(Shell parent, ADTable table, ADBase adObject) {
		super(parent, table, adObject);
	}

	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		// Build the separator line
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL| SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        setTitleImage(SWTResourceCache.getImage("search-dialog"));
        setTitle(Message.getString("common.search_Title"));
        setMessage(Message.getString("common.keys"));
        createFormContent(composite);
        return composite;
    }
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					Object obj = getAdObject();
					if (obj instanceof CalendarDay) {
						calendarDay = (CalendarDay) obj;
					}
					okPressed();
					refresh();
				}
			}
			
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	
	protected Point getInitialSize() {
		Point p = super.getInitialSize();
		p.x = 400;
		p.y = 265;
		return p;
	}

	public CalendarDay getCalendarDay() {
		return calendarDay;
	}

	public void setCalendarDay(CalendarDay calendarDay) {
		this.calendarDay = calendarDay;
	}
}

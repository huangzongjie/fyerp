package com.graly.erp.wip.calendarday;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;

import com.graly.erp.base.model.Constants;
import com.graly.erp.pur.calendarday.CalendarDayListManager;
import com.graly.erp.pur.calendarday.CalendarDaySection;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class WipCalendarDaySection extends CalendarDaySection {

	public WipCalendarDaySection(CalendarDayListManager calendarDayListManager,
			String calendarDayType) {
		super(calendarDayListManager, calendarDayType);
	}
	
	@Override
	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIP_CALENDARDAY_SAVE);
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
	}
	
	@Override
	protected void createToolItemNextMonth(ToolBar tBar) {
		itemNextMonth = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIP_CALENDARDAY_NEXTMONTH);
		itemNextMonth.setText(Message.getString("inv.next_month"));
		itemNextMonth.setImage(SWTResourceCache.getImage("next"));
		itemNextMonth.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				nextMonthAdapter();
			}
		});
	}
	
	@Override
	protected void createToolItemPreMonth(ToolBar tBar) {
		itemPrevMonth = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIP_CALENDARDAY_PREVIOUSMONTH);
		itemPrevMonth.setText(Message.getString("inv.per_month"));
		itemPrevMonth.setImage(SWTResourceCache.getImage("back"));
		itemPrevMonth.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				preMonthAdapter();
			}
		});
	}
}

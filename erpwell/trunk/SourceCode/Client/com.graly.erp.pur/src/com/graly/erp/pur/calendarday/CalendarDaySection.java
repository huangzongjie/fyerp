package com.graly.erp.pur.calendarday;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.calendar.model.CalendarDay;
import com.graly.erp.base.calendar.model.CalendarHour;
import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Constants;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class CalendarDaySection {
	private static final Logger logger = Logger.getLogger(MasterSection.class);
	protected ToolItem itemSave;
	protected CalendarDayListManager calendarDayListManager;
	protected StructuredViewer viewer;
	protected String whereClause;
	protected Section section;
	protected IFormPart spart;
	protected ToolItem queryItem;
	protected ToolItem refreshItem;
	protected ADTable adTable;
	protected String TABLE_NAME="BASCalendarDaySearch";
	protected ToolItem itemNextMonth,itemPrevMonth;
	public String calendarDayType; 
	
	public CalendarDaySection(CalendarDayListManager calendarDayListManager, String calendarDayType) {
		this.setCalendarDayListManager(calendarDayListManager);
		this.setCalendarDayType(calendarDayType);
	}

	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION|Section.TITLE_BAR);
		createSectionDesc(section);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		final FormToolkit toolkit = form.getToolkit();
		final ADTable table = getCalendarDayListManager().getADTable();
		
		section = toolkit.createSection(parent, sectionStyle);
		section.setText(I18nUtil.getI18nMessage(table, "label"));
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);

	    createToolBar(section);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    
	    spart = new SectionPart(section);    
	    form.addPart(spart);
	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));  
		
	    viewer = getCalendarDayListManager().createViewer(client, toolkit);
	    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					try{
						Object obj = Class.forName(table.getModelClass()).newInstance();
						if (obj instanceof ADBase) {
							((ADBase)obj).setOrgRrn(Env.getOrgRrn());
						}
						form.fireSelectionChanged(spart, new StructuredSelection(new Object[] {obj}));
					} catch (Exception e){
						e.printStackTrace();
					}
				} else {
					form.fireSelectionChanged(spart, event.getSelection());
				}
			} 
		});
	    initCalendarDayType();
	    BASManager basManager;
		try {
			basManager = Framework.getService(BASManager.class);
			Date now = Env.getSysDate();
			java.sql.Timestamp nowDate = new java.sql.Timestamp(now.getTime());
			List<CalendarDay> input = basManager.selectCalendarDay(nowDate.getYear()+1900, nowDate.getMonth()+1, getCalendarDayType(), Env.getUserRrn(),Env.getOrgRrn());
		    viewer.setInput(input);
		    getCalendarDayListManager().updateView(viewer);
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
		}
		section.setClient(client);
	}

	public void initCalendarDayType() {
		
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemPreMonth(tBar);
		createToolItemNextMonth(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemSearch(ToolBar tBar) {
		queryItem = new ToolItem(tBar, SWT.PUSH);
		queryItem.setText(Message.getString("common.search_Title"));
		queryItem.setImage(SWTResourceCache.getImage("search"));
		queryItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryAdapter();
			}
		});
	}
	
	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PUR_CALENDARDAY_SAVE);
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
	}
	
	protected void createToolItemRefresh(ToolBar tBar) {
		refreshItem = new ToolItem(tBar, SWT.PUSH);
		refreshItem.setText(Message.getString("common.refresh"));
		refreshItem.setImage(SWTResourceCache.getImage("refresh"));
		refreshItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refreshAdapter();
			}
		});
	}
	
	protected void createToolItemNextMonth(ToolBar tBar) {
		itemNextMonth = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PUR_CALENDARDAY_NEXTMONTH);
		itemNextMonth.setText(Message.getString("inv.next_month"));
		itemNextMonth.setImage(SWTResourceCache.getImage("next"));
		itemNextMonth.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				nextMonthAdapter();
			}
		});
	}
	
	protected void createToolItemPreMonth(ToolBar tBar) {
		itemPrevMonth = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PUR_CALENDARDAY_PREVIOUSMONTH);
		itemPrevMonth.setText(Message.getString("inv.per_month"));
		itemPrevMonth.setImage(SWTResourceCache.getImage("back"));
		itemPrevMonth.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				preMonthAdapter();
			}
		});
	}
	
	protected void nextMonthAdapter() {
		BASManager basManager;
	try {
		basManager = Framework.getService(BASManager.class);
		
		List<CalendarDay> getInput=(List<CalendarDay>)viewer.getInput();
		CalendarDay calendarDay=getInput.get(0);
		Date date=calendarDay.getDay();
		int year=date.getYear()+1900;
		int month=date.getMonth()+2;
		
		List<CalendarDay> input=basManager.selectCalendarDay(year,month, getCalendarDayType(), Env.getUserRrn(),Env.getOrgRrn());
		refresh(input);
	}catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void preMonthAdapter() {
		BASManager basManager;
	try {
		basManager = Framework.getService(BASManager.class);
		
		List<CalendarDay> getInput=(List<CalendarDay>)viewer.getInput();
		CalendarDay calendarDay=getInput.get(0);
		Date date=calendarDay.getDay();
		int year=date.getYear()+1900;
		int month=date.getMonth();
		if(month==0){
			year=year-1;
			month=12;
		}
		List<CalendarDay> input=basManager.selectCalendarDay(year,month, getCalendarDayType(), Env.getUserRrn(),Env.getOrgRrn());
		refresh(input);
	}catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void createSectionDesc(Section section){
	}
	
	public void refresh(List<CalendarDay> input){
		viewer.setInput(input);		
		calendarDayListManager.updateView(viewer);
		createSectionDesc(section);
	}
	
	protected void queryAdapter() {
		adTable=getADTableOfCalendarDay();
		SearchCalendarDayDialog searchCalendarDayDialog = new SearchCalendarDayDialog(UI.getActiveShell(),adTable,new CalendarDay());
		if(searchCalendarDayDialog.open() == IDialogConstants.OK_ID) {
			CalendarDay calendarDay=searchCalendarDayDialog.getCalendarDay();
			int year=calendarDay.getYear()==null ? 0 : Integer.parseInt(calendarDay.getYear().toString());
			int month=calendarDay.getMonth()==null ? 0 : Integer.parseInt(calendarDay.getMonth().toString());
		BASManager basManager;
			try {
				basManager = Framework.getService(BASManager.class);
				 List<CalendarDay> input=basManager.selectCalendarDay(year, month, getCalendarDayType(), Env.getUserRrn(),Env.getOrgRrn());
				refresh(input);
				} catch (Exception e) {
				e.printStackTrace();
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		}
	}
	
	public void saveAdapter() {
		BASManager basManager;
		try {
			List<CalendarDay> getInput=(List<CalendarDay>)viewer.getInput();

			basManager = Framework.getService(BASManager.class);
			basManager.saveCalendarDay(Env.getOrgRrn(), getInput, this.getCalendarDayType(), Env.getUserRrn());
			
			CalendarDay calendarDay=getInput.get(0);
			Date date=calendarDay.getDay();
			int year=date.getYear()+1900;
			int month=date.getMonth()+1;
			List<CalendarDay> input=basManager.selectCalendarDay(year,month, this.getCalendarDayType(), Env.getUserRrn(),Env.getOrgRrn());
			refresh(input);
			
			UI.showInfo(Message.getString("common.save_successed"));
			} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	public void refreshAdapter() {
		BASManager basManager;
		try {
			basManager = Framework.getService(BASManager.class);
			List<CalendarDay> getInput=(List<CalendarDay>)viewer.getInput();
			CalendarDay calendarDay=getInput.get(0);
			Date date=calendarDay.getDay();
			int year=date.getYear()+1900;
			int month=date.getMonth()+1;
			List<CalendarDay> input;
			input = basManager.selectCalendarDay(year,month, getCalendarDayType(),Env.getUserRrn(),Env.getOrgRrn());
			refresh(input);
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
		
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public CalendarDayListManager getCalendarDayListManager() {
		return calendarDayListManager;
	}

	public void setCalendarDayListManager(CalendarDayListManager calendarDayListManager) {
		this.calendarDayListManager = calendarDayListManager;
	}
	
	protected ADTable getADTableOfCalendarDay() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
//				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("NewAlarmDialog : getADTableOfAlarmType()", e);
		}
		return null;
	}

	public String getCalendarDayType() {
		return calendarDayType;
	}

	public void setCalendarDayType(String calendarDayType) {
		this.calendarDayType = calendarDayType;
	}
}

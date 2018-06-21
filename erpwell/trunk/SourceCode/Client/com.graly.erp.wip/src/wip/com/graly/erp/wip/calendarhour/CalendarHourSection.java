package com.graly.erp.wip.calendarhour;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.calendar.model.CalendarHour;
import com.graly.erp.base.client.BASManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;

public class CalendarHourSection extends EntitySection{

	protected CalendarHourListManager calendarHourListManager;
	protected StructuredViewer viewer;
	protected Section section;
	protected IFormPart spart;
	protected IManagedForm form;
	public static boolean flag = isFlag();

	public CalendarHourSection(ADTable adTable) {
		calendarHourListManager = new CalendarHourListManager(adTable);
	}

	public void createContents(IManagedForm form, Composite parent) {
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
		this.form = form;
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		final FormToolkit toolkit = form.getToolkit();
		final ADTable table = getCalendarHourListManager().getADTable();

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

		viewer = getCalendarHourListManager().createViewer(client, toolkit, 400);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					try {
						Object obj = Class.forName(table.getModelClass()).newInstance();
						if (obj instanceof ADBase) {
							((ADBase) obj).setOrgRrn(Env.getOrgRrn());
						}
						form.fireSelectionChanged(spart, new StructuredSelection(new Object[] { obj }));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					form.fireSelectionChanged(spart, event.getSelection());
				}
			}
		});
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			List<CalendarHour> input = adManager.getEntityList(Env.getOrgRrn(), CalendarHour.class, Integer.MAX_VALUE, "", "");
			for (CalendarHour calendarHour : input) {
				String weekDay = calendarHour.getWeekDay();
				calendarHour.setWeekDay(Message.getString(weekDay));
			}
			viewer.setInput(input);

			getCalendarHourListManager().updateView(viewer);
			section.setClient(client);
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		createToolItemDelete(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	public void refresh(List<CalendarHour> input) {
		for (CalendarHour calendarHour : input) {
			String weekDay = calendarHour.getWeekDay();
			calendarHour.setWeekDay(Message.getString(weekDay));
		}
		viewer.setInput(input);
		calendarHourListManager.updateView(viewer);
	}

	protected void newAdapter() {
		WeekTypeDialog weekTypeDialog = new WeekTypeDialog(UI.getActiveShell(), form, (TableViewer)viewer);
		if(weekTypeDialog.open() == Dialog.OK){
			String weekType = weekTypeDialog.getTextValue();
			List<CalendarHour> getInput = (List<CalendarHour>) viewer.getInput();
			WeekDayDifferentView(getInput);
			getInput.addAll(generateNewHoursList(weekType));
			refresh(getInput);
		}
	}

	private List<CalendarHour> WeekDayDifferentView(List<CalendarHour> list) {
		for (CalendarHour calendarHour : list) {
			if(Message.getString(CalendarHour.WEEK_MONDAY).equals(calendarHour.getWeekDay())){
				calendarHour.setWeekDay(CalendarHour.WEEK_MONDAY);
			}
			if(Message.getString(CalendarHour.WEEK_TUESDAY).equals(calendarHour.getWeekDay())){
				calendarHour.setWeekDay(CalendarHour.WEEK_TUESDAY);
			}
			if(Message.getString(CalendarHour.WEEK_WEDENSDAY).equals(calendarHour.getWeekDay())){
				calendarHour.setWeekDay(CalendarHour.WEEK_WEDENSDAY);
			}
			if(Message.getString(CalendarHour.WEEK_THURDAY).equals(calendarHour.getWeekDay())){
				calendarHour.setWeekDay(CalendarHour.WEEK_THURDAY);
			}
			if(Message.getString(CalendarHour.WEEK_FRIDAY).equals(calendarHour.getWeekDay())){
				calendarHour.setWeekDay(CalendarHour.WEEK_FRIDAY);
			}
			if(Message.getString(CalendarHour.WEEK_SATURDAY).equals(calendarHour.getWeekDay())){
				calendarHour.setWeekDay(CalendarHour.WEEK_SATURDAY);
			}
			if(Message.getString(CalendarHour.WEEK_SUNDAY).equals(calendarHour.getWeekDay())){
				calendarHour.setWeekDay(CalendarHour.WEEK_SUNDAY);
			}
		}
		
		return list;
	}

	private List<CalendarHour> generateNewHoursList(String weekType) {
		List<CalendarHour> newHours = new ArrayList<CalendarHour>();
		CalendarHour hour1 = new CalendarHour();
		hour1.setWeekDay(CalendarHour.WEEK_MONDAY);
		hour1.setWeekType(weekType);
		hour1.setPart1("8:30-17:00");
		newHours.add(hour1);
		
		CalendarHour hour2 = new CalendarHour();
		hour2.setWeekDay(CalendarHour.WEEK_TUESDAY);
		hour2.setWeekType(weekType);
		hour2.setPart1("8:30-17:00");
		newHours.add(hour2);
		
		CalendarHour hour3 = new CalendarHour();
		hour3.setWeekDay(CalendarHour.WEEK_WEDENSDAY);
		hour3.setWeekType(weekType);
		hour3.setPart1("8:30-17:00");
		newHours.add(hour3);
		
		CalendarHour hour4 = new CalendarHour();
		hour4.setWeekDay(CalendarHour.WEEK_THURDAY);
		hour4.setWeekType(weekType);
		hour4.setPart1("8:30-17:00");
		newHours.add(hour4);
		
		CalendarHour hour5 = new CalendarHour();
		hour5.setWeekDay(CalendarHour.WEEK_FRIDAY);
		hour5.setWeekType(weekType);
		hour5.setPart1("8:30-17:00");
		newHours.add(hour5);
		
		CalendarHour hour6 = new CalendarHour();
		hour6.setWeekDay(CalendarHour.WEEK_SATURDAY);
		hour6.setWeekType(weekType);
		hour6.setPart1("8:30-17:00");
		newHours.add(hour6);
		
		CalendarHour hour7 = new CalendarHour();
		hour7.setWeekDay(CalendarHour.WEEK_SUNDAY);
		hour7.setWeekType(weekType);
		hour7.setPart1("8:30-17:00");
		newHours.add(hour7);
		
		return newHours;
	}
	
	@SuppressWarnings("unchecked")
	protected void saveAdapter() {
		setFlag(true);
		BASManager basManager;
		try {
			List<CalendarHour> getInput = (List<CalendarHour>) viewer.getInput();
			for (CalendarHour calendarHour : getInput) {
				String workhours = calendarHour.getPart1();

				Boolean typeValidate = workhours.matches("\\d{1,2}+\\:\\d{2}+\\-\\d{1,2}+\\:\\d{2}");
				if (!typeValidate) {
					UI.showError(Message.getString("wip_hourinput_error"), Message.getString("common.inputerror_title"));
					return;
				}
				String[] hourSplit = workhours.split("-");
				int hh[] = new int[hourSplit.length + 1];
				int mm[] = new int[hourSplit.length + 1];
				int i = 1;
				for (String hours : hourSplit) {
					String[] times = hours.split(":");
					hh[i] = Integer.parseInt(times[0]);
					mm[i] = Integer.parseInt(times[1]);
					if (hh[i] > 24 || mm[i] > 59 || hh[i] < 0 || mm[i] < 0
							|| (hh[i] == 24 && mm[i] > 0)) {
						UI.showError(Message.getString("wip_hourinput_error"), Message.getString("common.inputerror_title"));
						return;
					}
					i++;
				}
				if (hh[1] > hh[2] || (hh[1] == hh[2] && mm[1] > mm[2])) {
					UI.showError(Message.getString("ppm.dateintervalused"), Message.getString("common.inputerror_title"));
					return;
				}
			}

			basManager = Framework.getService(BASManager.class);
			basManager.saveCalendarHour(Env.getOrgRrn(), WeekDayDifferentView(getInput), Env.getUserRrn());

			ADManager adManager = Framework.getService(ADManager.class);
			List<CalendarHour> hoursList = adManager.getEntityList(Env.getOrgRrn(), CalendarHour.class, Integer.MAX_VALUE, "", "");
			refresh(hoursList);

			UI.showInfo(Message.getString("common.save_successed"));
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void deleteAdapter() {
		try {
			if(viewer instanceof TableViewer){
				Table table = ((TableViewer) viewer).getTable();
				if (table.getSelection().length > 0) {
					TableItem ti = table.getSelection()[0];
					Object obj = ti.getData();
					if(obj != null){
						CalendarHour hour = (CalendarHour)obj;
						if(isCanDelete(hour)) {
							boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
							if(confirmDelete){
								List<CalendarHour> list = (List<CalendarHour>)viewer.getInput();
								List<CalendarHour> deleteHours = new ArrayList<CalendarHour>();
								for (CalendarHour calendarHour : list) {
									if((calendarHour.getWeekType() == null && hour.getWeekType() == null)
											|| calendarHour.getWeekType().equals(hour.getWeekType())){
										deleteHours.add(calendarHour);
									}
								}
								if(hour.getObjectRrn() != null){
									ADManager entityManager = Framework.getService(ADManager.class);
									for (CalendarHour calendarHour2 : deleteHours) {
										entityManager.deleteEntity(calendarHour2);
									}
								}
								list.removeAll(deleteHours);
								refresh(WeekDayDifferentView(list));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	// 当周类型为DEFAULT或周类型被工作中心引用时则不能删除
	protected boolean isCanDelete(CalendarHour hour) throws Exception {
		if(hour == null || hour.getWeekType() == null) return false;
		String weekType = hour.getWeekType();
		
		if(BusinessCalendar.WEEKTYPE_DEFAULT.equals(weekType)){
			UI.showError(String.format(Message.getString("wip.default_weekType_can't_be_deleltd"), weekType));
			return false;
		}
		ADManager adManager = Framework.getService(ADManager.class);
		String whereClause = " weekType = '" + weekType + "' ";
		List<WorkCenter> list = adManager.getEntityList(Env.getOrgRrn(), WorkCenter.class, 2, whereClause, null);
		if(list != null && list.size() > 0) {
			UI.showError(String.format(Message.getString("wip.weekType_has_referenced_by_wc"), weekType));
			return false;
		}
		return true;
	}
	
	protected void refreshAdapter() {
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			List<CalendarHour> input = adManager.getEntityList(Env.getOrgRrn(), CalendarHour.class, Integer.MAX_VALUE, "", "");
			refresh(input);
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	public CalendarHourListManager getCalendarHourListManager() {
		return calendarHourListManager;
	}

	public void setCalendarHourListManager(CalendarHourListManager calendarHourListManager) {
		this.calendarHourListManager = calendarHourListManager;
	}

	public static boolean isFlag() {
		return flag;
	}

	public static void setFlag(boolean flag) {
		CalendarHourSection.flag = flag;
	}
}

package com.graly.alm.alarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.alm.client.ALMManager;
import com.graly.alm.model.Action;
import com.graly.alm.model.ActionType;
import com.graly.alm.model.AlarmDefinition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ActionViewerSection extends EntitySection {
	private static final Logger logger = Logger.getLogger(ActionViewerSection.class);
	protected TableListManager tableManager;
	protected StructuredViewer viewer;
	protected ADTable actionTable, adTable;
	private static String TABLENAME_ACTIONOFMAIL = "ALMActionOfMail";
	private static String TABLENAME_ACTIONOFSMS = "ALMActionOfSms";
	protected Section section;
	protected IFormPart spart;
	private ToolItem dropDownItemOfAction;
	private Menu menu;
	private Action selectedAction, newOrEditorAction;
	protected List<Action> actionList;
	public static Boolean NEW_OR_EDITOR_FLAG = true;
	public static Boolean DELETE_FLAG = false;

	public ActionViewerSection(ADTable actionTable, ADTable alarmTable, ADBase adObject) {
		super(alarmTable);
		this.setAdObject(adObject);
		this.actionTable = actionTable;
		tableManager = new TableListManager(actionTable);
	}

	public void createContents(IManagedForm form, Composite parent) {
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();

		section = toolkit.createSection(parent, sectionStyle);
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
		section.setText(String.format(Message.getString("common.detail"), I18nUtil.getI18nMessage(table, "label")));

		/* 创建Alarm Tab信息 */
		createSectionContent(client);
		/* 创建ActionList信息 */
		createTableViewer(client, toolkit);

		section.setClient(client);
		createViewAction(viewer);
		refresh();
	}

	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		viewer = tableManager.createViewer(client, toolkit);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					try {
						Object obj = Class.forName(actionTable.getModelClass()).newInstance();
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
		AlarmDefinition selectAlarm = (AlarmDefinition) adObject;
		if (selectAlarm != null && selectAlarm.getObjectRrn() != null) {
			actionList = initActionList(selectAlarm);
		}
		viewer.setInput(actionList);
		tableManager.updateView(viewer);
	}

	private List<Action> initActionList(AlarmDefinition selectAlarm) {
		String where = "alarmDefinitionRrn='" + selectAlarm.getObjectRrn() + "'";
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			String orderByClause = "created asc";
			actionList = adManager.getEntityList(Env.getOrgRrn(), Action.class, Integer.MAX_VALUE, where, orderByClause);
			return actionList;
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return null;
		}
	}

	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				String actionType = selectedAction.getActionTypeId();
				NEW_OR_EDITOR_FLAG = false;
				if (selectedAction != null && actionType.equals("Mail")) {
					mailOrSmsAction(selectedAction, actionType, TABLENAME_ACTIONOFMAIL);
				} else if (selectedAction != null && actionType.equals("SMS")) {
					mailOrSmsAction(selectedAction, actionType, TABLENAME_ACTIONOFSMS);
				}
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof Action) {
			selectedAction = (Action) obj;
		} else {
			selectedAction = null;
		}
	}

	public void createToolBar(Section section) {
		final ToolBar toolBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemAddAction(toolBar);
		createToolItemDelete(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemSave(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemRefresh(toolBar);
		section.setTextClient(toolBar);

		createMenu(toolBar);

		dropDownItemOfAction.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.ARROW) {
					Rectangle bounds = dropDownItemOfAction.getBounds();
					Point point = toolBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu.setLocation(point);
					menu.setVisible(true);
				}
			}
		});
	}

	/* 创建dropDownMenu及其监听事件 */
	private void createMenu(final ToolBar toolBar) {
		menu = new Menu(UI.getActiveShell(), SWT.POP_UP);
		MenuItem menuItemEmail = new MenuItem(menu, SWT.PUSH);
		menuItemEmail.setText(Message.getString("alm.action_of_email"));
		MenuItem menuItemSms = new MenuItem(menu, SWT.PUSH);
		menuItemSms.setText(Message.getString("alm.action_of_sms"));
		new MenuItem(menu, SWT.SEPARATOR);
		new MenuItem(menu, SWT.PUSH).setText(Message.getString("common.cancel"));

		menuItemEmail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mailOrSmsAction(new Action(), "Mail", TABLENAME_ACTIONOFMAIL);
			}
		});
		menuItemSms.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mailOrSmsAction(new Action(), "SMS", TABLENAME_ACTIONOFSMS);
			}
		});
	}

	protected void mailOrSmsAction(Action action, String actionType, String tableName) {
		adTable = initAdTableOfAction(tableName);
		ActionDialog actionDialog = new ActionDialog(UI.getActiveShell(), adTable, action);
		if (actionDialog.open() == IDialogConstants.OK_ID) {
			ADManager adManager;
			try {
				adManager = Framework.getService(ADManager.class);
				newOrEditorAction = (Action) actionDialog.getAdObject();
				String whereClause = " actionTypeId = '" + actionType + "'";
				List<ActionType> actionTypes = adManager.getEntityList(action.getOrgRrn(), ActionType.class, 2, whereClause, "");
				newOrEditorAction.setActionTypeRrn(actionTypes.get(0).getObjectRrn());
				newOrEditorAction.setActionTypeId(actionType);

				refreshActionList();
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				NEW_OR_EDITOR_FLAG = true;
				return;
			}
		}
		NEW_OR_EDITOR_FLAG = true;
	}

	protected void createToolItemAddAction(ToolBar tBar) {
		dropDownItemOfAction = new ToolItem(tBar, SWT.DROP_DOWN);
		dropDownItemOfAction.setText(Message.getString("alm.add_action"));
		dropDownItemOfAction.setImage(SWTResourceCache.getImage("new"));
		dropDownItemOfAction.setToolTipText(Message.getString("alm.add_action_tip"));
	}

	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new ToolItem(tBar, SWT.PUSH);
		itemDelete.setText(Message.getString("alm.delete_action"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}

	protected void deleteAdapter() {
		if (selectedAction != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedAction.getObjectRrn() != null) {
						ALMManager almManager = Framework.getService(ALMManager.class);
						almManager.deleteAction(selectedAction, Env.getUserRrn());
					}
					DELETE_FLAG = true;
					refreshActionList();
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				DELETE_FLAG = false;
				return;
			}
		}
		DELETE_FLAG = false;
	}

	protected void saveAdapter() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
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
					AlarmDefinition alarm = null;
					if (getAdObject() instanceof AlarmDefinition) {
						alarm = (AlarmDefinition) getAdObject();
						List<Action> list = (List<Action>) viewer.getInput();
						if (list == null)
							list = new ArrayList<Action>();
						alarm.setActions(list);
					}
					ALMManager almManager = Framework.getService(ALMManager.class);
					AlarmDefinition newAlarm = almManager.saveAlarm(alarm, Env.getUserRrn());
					UI.showInfo(Message.getString("common.save_successed"));
					ADManager adManager = Framework.getService(ADManager.class);
					setAdObject(adManager.getEntity(newAlarm));
					refresh();
					viewer.setInput(initActionList(newAlarm));
					this.tableManager.updateView(viewer);
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void refreshAdapter() {
		refresh();
	}

	public void refreshActionList() {
		actionList = (List<Action>) viewer.getInput();
		if (actionList == null) {
			actionList = new ArrayList<Action>();
		}
		if (!DELETE_FLAG) {
			if (!NEW_OR_EDITOR_FLAG) {
				List<Action> actionEditor = new ArrayList<Action>();
				for (Action action : actionList) {
					if (action.equals(selectedAction)) {
						actionEditor.add(selectedAction);
					} else {
						actionEditor.add(action);
					}
				}
				actionList = actionEditor;
			} else {
				actionList.add(newOrEditorAction);
			}
		} else {
			actionList.remove(selectedAction);
		}

		viewer.setInput(actionList);
		tableManager.updateView(viewer);
	}

	protected ADTable initAdTableOfAction(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("ActionViewerSection : initAdTableOfAction()", e);
			return null;
		}
	}
}

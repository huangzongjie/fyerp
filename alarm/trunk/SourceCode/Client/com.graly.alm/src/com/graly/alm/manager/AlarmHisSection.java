package com.graly.alm.manager;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.alm.client.ALMManager;
import com.graly.alm.model.AlarmHis;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class AlarmHisSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(AlarmHisSection.class);

	protected ToolItem itemAlarmManager;
	protected AlarmHis selectedAlarmHis;
	protected ALMManager almManager;

	private ADTable adTable;

	private static final String TABLE_NAME_ALARMHISMANAGER = "ALMAlarmHisManager";

	public AlarmHisSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				alarmManagerAdapter();
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

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemDelete(ToolBar tBar) {
		itemAlarmManager = new ToolItem(tBar, SWT.PUSH);
		itemAlarmManager.setText(Message.getString("alm.alarm_manager"));
		itemAlarmManager.setImage(SWTResourceCache.getImage("manager"));
		itemAlarmManager.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				alarmManagerAdapter();
			}
		});
	}

	protected void alarmManagerAdapter() {
		if (selectedAlarmHis == null || selectedAlarmHis.getObjectRrn() == null) {
			UI.showWarning(Message.getString("inv.entityisnull"));
			return;
		} else {
			adTable = initAdTableOfDetail(TABLE_NAME_ALARMHISMANAGER);
			AlarmHisMangager alarmHisDetailsManagerDialog = new AlarmHisMangager(UI.getActiveShell(), adTable,
					selectedAlarmHis);
			if (alarmHisDetailsManagerDialog.open() == Dialog.CANCEL) {
			}
		}
		refresh();

	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof AlarmHis) {
			selectedAlarmHis = (AlarmHis) obj;
		} else {
			selectedAlarmHis = null;
		}
	}

	protected ADTable initAdTableOfDetail(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("AlarmHisSection : initAdTableOfDetail()", e);
		}
		return null;
	}
}

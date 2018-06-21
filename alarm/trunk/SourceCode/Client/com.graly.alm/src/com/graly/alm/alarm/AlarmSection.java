package com.graly.alm.alarm;

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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.alm.client.ALMManager;
import com.graly.alm.model.AlarmDefinition;
import com.graly.alm.panel.AlarmPanelListener;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AlarmSection extends MasterSection {
	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;
	protected AlarmDefinition selectedAlarm;
	protected ALMManager almManager;
    protected AlarmPanelListener alarmMessage;
	public AlarmSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				editAdapter();
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
		createToolItemNew(tBar);
		createToolItemEdit(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new ToolItem(tBar, SWT.PUSH);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				newAdapter();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	protected void createToolItemEdit(ToolBar tBar) {
		itemEdit = new ToolItem(tBar, SWT.PUSH);
		itemEdit.setText(Message.getString("common.edit"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}

	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new ToolItem(tBar, SWT.PUSH);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	


	protected  void newAdapter(){	
		try {
			almManager=Framework.getService(ALMManager.class);
//			almManager.doRun("Vendor","供应商警报");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {		
			AlarmActionDialog newAlarmDialog = new AlarmActionDialog(UI.getActiveShell(), this.getTableManager().getADTable(), new AlarmDefinition());		
			if (newAlarmDialog.open() == Dialog.CANCEL) {
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}

		refresh();
	}

	protected void editAdapter() {
		AlarmActionDialog newAlarmDialog = new AlarmActionDialog(UI.getActiveShell(), this.getTableManager().getADTable(), selectedAlarm);
		if (newAlarmDialog.open() == Dialog.CANCEL) {
		}
		refresh();
	}

	protected void deleteAdapter() {
		if (selectedAlarm != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedAlarm.getObjectRrn() != null) {
						almManager = Framework.getService(ALMManager.class);
						almManager.deleteAlarm(selectedAlarm, Env.getUserRrn());
						this.selectedAlarm = null;
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof AlarmDefinition) {
			selectedAlarm = (AlarmDefinition) obj;
		} else {
			selectedAlarm = null;
		}
	}
}

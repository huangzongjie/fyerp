package com.graly.alm.panel;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.WorkbenchJob;

import com.graly.alm.client.ALMManager;
import com.graly.alm.history.AlarmHisDetailDialog;
import com.graly.alm.model.AlarmPanelMessage;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.application.Activator;
import com.graly.framework.base.ui.IMinimize;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class AlarmPanel extends ViewPart implements IMinimize {	
	private static final Logger logger = Logger.getLogger(AlarmPanel.class);

	private static final String ID = "AlarmPanel";
	private static final String TAG_VERTICAL_POSITION = "verticalPosition"; //$NON-NLS-1$
	private static final String TAG_HORIZONTAL_POSITION = "horizontalPosition"; //$NON-NLS-1$

	
	private TableViewer viewer;
	private IMemento memento;
	private ISelectionProvider selectionProvider = new SelectionProviderAdapter();

	protected SelectionProviderAction openAction;
	protected CopyAction copyAction;
	protected SelectionProviderAction deleteAction;
	protected SelectionProviderAction selectAllAction;

	private Clipboard clipboard;
	
	private AlarmPanelManager tableManager;
	private ADTable adTable;
	private boolean minimize = true;
	private boolean firstActive = true;
	
	private static String TABLE_NAME = "AlarmPanelMessage";
//	/**
//	 * The constructor.
//	 */
//	public AlarmPanel() {
//		AlarmPanelListener alarmListener = new AlarmPanelListener(this);
//		alarmListener.run();
//	}
	
	private UpdateJob updateJob = new UpdateJob();

	public void createPartControl(Composite parent) {
		this.setPartName(Message.getString("alm.alarm_panel"));
		clipboard = new Clipboard(parent.getDisplay());
		parent.setLayout(new FillLayout());
		
		if (adTable == null ) {
			try {
				ADManager adManager = Framework.getService(ADManager.class);
				adTable  = adManager.getADTable(Env.getOrgRrn(), TABLE_NAME);
			} catch (Exception e) {
				return;
			}
		}

		tableManager = new AlarmPanelManager(adTable);
		
		setViewer((TableViewer)tableManager.createViewer(parent, new FormToolkit(parent.getDisplay())));
		getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				viewerSelectionChanged(selection);
			}
		});

		createActions();

		Scrollable scrollable = (Scrollable) getViewer().getControl();
		ScrollBar bar = scrollable.getVerticalBar();
		if (bar != null) {
			bar.setSelection(restoreVerticalScrollBarPosition(memento));
		}
		bar = scrollable.getHorizontalBar();
		if (bar != null) {
			bar.setSelection(restoreHorizontalScrollBarPosition(memento));
		}

		MenuManager mgr = initContextMenu();
		Menu menu = mgr.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
		getSite().registerContextMenu(mgr, selectionProvider);

		getSite().setSelectionProvider(selectionProvider);

		IActionBars actionBars = getViewSite().getActionBars();
		initMenu(actionBars.getMenuManager());
		initToolBar(actionBars.getToolBarManager());

		getViewer().addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				handleOpenEvent(event);
			}
		});
		getViewer().getControl().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				handleKeyPressed(e);
			}
		});

		IWorkbenchSiteProgressService progressService = getProgressService();
		if (progressService == null)
			updateJob.schedule();
		else
			getProgressService().schedule(updateJob);
	}

	protected void createActions() {
		openAction = new OpenAction(this, getViewer());
		copyAction = new CopyAction(this, getViewer());
		copyAction.setClipboard(clipboard);
		deleteAction = new DeleteAction(this, getViewer());
		selectAllAction = new SelectAllAction(this);
	}
	
	protected MenuManager initContextMenu() {
		MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				getViewer().cancelEditing();
				fillContextMenu(mgr);
			}
		});
		return mgr;
	}

	protected void fillContextMenu(IMenuManager manager) {
		if (manager == null) {
			return;
		}
		manager.add(openAction);
		
		manager.add(new Separator());
		manager.add(copyAction);
		manager.add(deleteAction);
		manager.add(selectAllAction);
	}

	protected void initMenu(IMenuManager menu) {
	}
	
	protected void initToolBar(IToolBarManager tbm) {	
	}
	
	protected void handleKeyPressed(KeyEvent event) {
		// Default is do nothing.
	}

	protected void handleOpenEvent(OpenEvent event) {
		if (openAction.isEnabled()) {
			openAction.run();
		}
	}
	
	protected void viewerSelectionChanged(IStructuredSelection selection) {
	}
	
	public void onMessage(AlarmPanelMessage message) {
		
	}

	public void setFocus() {
		Viewer viewer = getViewer();
		if (viewer != null && !viewer.getControl().isDisposed()) {

			viewer.getControl().setFocus();
		}
	}

	protected IWorkbenchSiteProgressService getProgressService() {
		IWorkbenchSiteProgressService service = null;
		Object siteService = getSite().getAdapter(
				IWorkbenchSiteProgressService.class);
		if (siteService != null) {
			service = (IWorkbenchSiteProgressService) siteService;
		}
		return service;
	}
	
	@SuppressWarnings("restriction")
	private void toggleFastView(){
		WorkbenchPage page = UI.getWorkbenchPage();
		page.toggleFastView(UI.getViewReference(ID));
	}
	
	private int restoreVerticalScrollBarPosition(IMemento memento) {
		if (memento == null) {
			return 0;
		}
		Integer position = memento.getInteger(TAG_VERTICAL_POSITION);
		return (position == null) ? 0 : position.intValue();
	}

	private int restoreHorizontalScrollBarPosition(IMemento memento) {
		if (memento == null) {
			return 0;
		}
		Integer position = memento.getInteger(TAG_HORIZONTAL_POSITION);
		return (position == null) ? 0 : position.intValue();
	}
	
	public void setViewer(TableViewer viewer) {
		this.viewer = viewer;
	}

	public TableViewer getViewer() {
		return viewer;
	}
	
	private class UpdateJob extends WorkbenchJob {
		
		UpdateJob() {
			super("");
			setSystem(true);
		}
		
		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (getViewer().getControl().isDisposed()) {
				return Status.CANCEL_STATUS;
			}

			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			try {
				ALMManager almManager = Framework.getService(ALMManager.class);
				List<AlarmPanelMessage> messages = almManager.getPanelMessages(Env.getOrgRrn(), Env.getUserRrn());
				getViewer().setInput(messages);
				tableManager.updateView(viewer);
				if (messages.size() != 0) {
					minimize = false;
					toggleFastView();
				}
			} catch (Exception e) {
				logger.error("UpdateJob : " + e.getMessage(), e);
			}
			if (getViewer().getTable().getItemCount() > 0) {
				getViewer().getTable().setSelection(getViewer().getTable().getItem(0));
			}

			return Status.OK_STATUS;
		}
	}

	public boolean getMinimize() {
		return minimize;
	}
	
	public boolean getFirstActive() {
		return firstActive;
	}
	
	public void setFirstActive(boolean firstActive) {
		this.firstActive = firstActive;
	}
}

class SelectionProviderAdapter implements ISelectionProvider {

    List listeners = new ArrayList();

    ISelection theSelection = null;

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        listeners.add(listener);
    }

    public ISelection getSelection() {
        return theSelection;
    }

    public void removeSelectionChangedListener(
            ISelectionChangedListener listener) {
        listeners.remove(listener);
    }

    public void setSelection(ISelection selection) {
        theSelection = selection;
        final SelectionChangedEvent e = new SelectionChangedEvent(this, selection);
        Object[] listenersArray = listeners.toArray();
        
        for (int i = 0; i < listenersArray.length; i++) {
            final ISelectionChangedListener l = (ISelectionChangedListener) listenersArray[i];
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.selectionChanged(e);
                }
            });
		}
    }
}

class OpenAction extends SelectionProviderAction {
	private static final Logger logger = Logger.getLogger(OpenAction.class);
	
	public OpenAction(IWorkbenchPart part, ISelectionProvider provider) {
		super(provider, Message.getString("common.open"));
		setImageDescriptor(Activator.getImageDescriptor("open_enable"));
		setDisabledImageDescriptor(Activator.getImageDescriptor("open_disable"));
		setEnabled(false);
	}
	
	public void run() {
		AlarmPanelMessage panelMessage = (AlarmPanelMessage)getStructuredSelection().getFirstElement();
		AlarmHisDetailDialog alarmHisDetailDialog = new AlarmHisDetailDialog(UI.getActiveShell(),
				getAlarmHisADTable(), panelMessage.getAlarmHis());
		if (alarmHisDetailDialog.open() == Dialog.CANCEL) {
		}
	}
	
	protected ADTable getAlarmHisADTable() {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTable = entityManager.getADTable(0L, "ALMAlarmHis");
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("OpenAction : getAlarmHisADTable()", e);
		}
		return null;
	}
	
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(selection != null && selection.size() == 1);
	}
}

class CopyAction extends SelectionProviderAction {	
	private Clipboard clipboard;
	
	public CopyAction(IWorkbenchPart part, ISelectionProvider provider) {
		super(provider, Message.getString("common.copy"));
		setImageDescriptor(Activator.getImageDescriptor("copy"));
		setEnabled(false);
	}
	
	public void setClipboard(Clipboard clipboard) {
		this.clipboard = clipboard;
	}
	
	public void run() {
		IStructuredSelection section = getStructuredSelection();
		Object[] selection = section.toArray();
		
		setClipboard(selection, createMessageReport(selection));
	}
	
	private void setClipboard(Object[] messages, String messageReport) {
		try {
			Object[] data;
			Transfer[] transferTypes;
			if (messageReport == null) {
				data = new Object[] { messageReport };
				transferTypes = new Transfer[] { TextTransfer.getInstance() };
			} else {
				data = new Object[] { messages, messageReport };
				transferTypes = new Transfer[] { TextTransfer.getInstance(),
						TextTransfer.getInstance() };
			}

			clipboard.setContents(data, transferTypes);
		} catch (SWTError e) {
			if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
				throw e;
			}
		}
	}
	
	String createMessageReport(Object[] messages) {
		return "";
	}
	
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(selection != null && selection.size() == 1);
	}
}

class DeleteAction extends SelectionProviderAction {
	
	public DeleteAction(IWorkbenchPart part, ISelectionProvider provider) {
		super(provider, Message.getString("common.delete"));
		setImageDescriptor(Activator.getImageDescriptor("delete_enable"));
		setDisabledImageDescriptor(Activator.getImageDescriptor("delete_disable"));
		setEnabled(false);
	}
	
	public void run() {
		
	}
	
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(selection != null && selection.size() == 1);
	}
}

class SelectAllAction extends SelectionProviderAction {
	private static final Logger logger = Logger.getLogger(SelectAllAction.class);
	private AlarmPanel panel;
	
	public SelectAllAction(AlarmPanel panel) {
		super(panel.getViewer(), Message.getString("common.selectall"));
		setEnabled(true);
		this.panel = panel;
	}
	
	public void run() {
		List selection = (List)panel.getViewer().getInput();
		PlatformUI.getWorkbench().getDisplay().readAndDispatch();
		getSelectionProvider().setSelection(new StructuredSelection(selection));
//		try {
//			IRunnableWithProgress selectionRunnableWithProgress = new IRunnableWithProgress() {
//				public void run(IProgressMonitor monitor) {
//					try {
//						monitor.beginTask(Message.getString("common.select_all_title"), 100);
//						monitor.subTask(Message.getString("common.select_all_calculating"));
//						if (monitor.isCanceled())
//							return;
//						monitor.worked(10);
//						List selection = (List)panel.getViewer().getInput();
//						monitor.worked(10);
//						monitor.subTask(Message.getString("common.select_all_applying"));
//						PlatformUI.getWorkbench().getDisplay().readAndDispatch();
//						getSelectionProvider().setSelection(new StructuredSelection(selection));
//					} finally {
//						monitor.done();
//					}
//					
//				}
//			};
//			SelectAllProgressDialog dialog = new SelectAllProgressDialog(UI.getActiveShell());
//			dialog.run(true, true, selectionRunnableWithProgress);
//		} catch(Exception e) {
//			logger.error("Error at SelectAllAction : run() ", e);
//		}
	}
	
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(!selection.isEmpty());
	}
}

class SelectAllProgressDialog extends ProgressMonitorDialog {
	private String title = "";

	public SelectAllProgressDialog(Shell parent) {
		super(parent);
	}
	
	public SelectAllProgressDialog(Shell parent, String title) {
		super(parent);
		this.title = title;
	}
	
	// 使取消按钮可以中英文显示
	protected void createCancelButton(Composite parent) {
		cancel = createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
		if (arrowCursor == null) {
			arrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
		}
		cancel.setCursor(arrowCursor);
		setOperationCancelButtonEnabled(enableCancelButton);
	}
	
	// 使对话框标题可以中英文显示
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}

}
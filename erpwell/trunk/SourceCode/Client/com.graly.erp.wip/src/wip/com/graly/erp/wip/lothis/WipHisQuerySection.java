package com.graly.erp.wip.lothis;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Version;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.po.PoUnitPriceChartDialog;
import com.graly.erp.wip.seelotinfo.ComponentTreeManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.AdvanceQueryTray;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wiphis.model.LotHis;

public class WipHisQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(WipHisQuerySection.class);

	protected ToolItem queryByLot;
	protected ToolItem moHisReport;
	protected WipHisQueryDialog onlineDialog;
	private static final String DETAIL_TABLE_NAME = "WIPHisDetailInfo";
	private static final String BY_USED_LOT_QUERY_TABLE = "WIPHisQueryByUsedLot";

	private ByUsedLotWipHisQueryDialog byUsedLotQueryDialog;
	private MoHisReportDialog moHisReportDialog;

	public WipHisQuerySection(EntityTableManager tableManager) {
		super(tableManager);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemMoHisReport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemQueryByLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		section.setTextClient(tBar);
	}
	
	private void createToolItemMoHisReport(ToolBar tBar) {
		moHisReport = new ToolItem(tBar, SWT.PUSH);
		moHisReport.setText("工作令接收历史");
		moHisReport.setImage(SWTResourceCache.getImage("export"));
		moHisReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				moHisReportAdapter();
			}
		});
	}
	
	private void createToolItemQueryByLot(ToolBar tBar) {
		queryByLot = new ToolItem(tBar, SWT.PUSH);
		queryByLot.setText("按批次查");
		queryByLot.setImage(SWTResourceCache.getImage("bylot"));
		queryByLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryByLotAdapter();
			}
		});
	}

	protected void queryByLotAdapter() {
		if(byUsedLotQueryDialog != null){
			byUsedLotQueryDialog.setVisible(true);
		}else{
			EntityTableManager tableManager = new EntityTableManager(getADTable(BY_USED_LOT_QUERY_TABLE));
			byUsedLotQueryDialog = new ByUsedLotWipHisQueryDialog(UI.getActiveShell(), tableManager, this);
			byUsedLotQueryDialog.open();
		}
	}
	
	protected void moHisReportAdapter() {
		String report = "mo_receive_his_report.rptdesign";
		try {
			if (moHisReportDialog != null) {
				moHisReportDialog.setReportName(report);
				moHisReportDialog.setVisible(true);
			} else {
				String TABLE_NAME = "MoHisReport";
				ADManager adManager;

				adManager = Framework.getService(ADManager.class);

				ADTable printADTable = adManager.getADTable(Env.getOrgRrn(),
						TABLE_NAME);
				EntityTableManager tableManager = new EntityTableManager(
						printADTable);
				moHisReportDialog = new MoHisReportDialog(UI.getActiveShell(),
						tableManager, null);
				moHisReportDialog.setReportName(report);
				moHisReportDialog.open();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	void refreshViewer(List contents){
		viewer.setInput(contents);		
		tableManager.updateView(viewer);
		createSectionDescription(contents.size());
	}
	
	private void createSectionDescription(int count){
		try{ 
			String text = Message.getString("common.totalshow");
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("EntityBlock : createSectionDesc ", e);
		}
	}

	protected void createViewAction(StructuredViewer viewer){
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				detailAdapter(ss.getFirstElement());
			}
			
		});
	}
	
	protected void detailAdapter(Object object) {
		ADTable adTable = getADTable(DETAIL_TABLE_NAME);
		DetailDialog dialog = new DetailDialog(object, adTable);
		dialog.open();
	}
	
	protected ADTable getADTable(String tableName) {
		ADTable adTable = null;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
		} catch (Exception e) {
			logger.equals(e);
		}
		return adTable;
	}

	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else if(this.onlineDialog != null && onlineDialog.getEntityQueryDialog() != null) {
			queryDialog = onlineDialog.getEntityQueryDialog();
			queryDialog.setVisible(true);
		} else {
			// 此种情况一般不会出现,因为在VendorAssess.open()已将queryDialog设置过来.之所以用
			// VendorAssessDialog(false)表示不创建queryDialog.而是显式调用VendorAssessQueryDialog.
			// 以便传入tableManager,否则会因为在vaDialog无tableId而导致调用getEntityTableManager时出错.
			WipHisQueryDialog vaDialog = new WipHisQueryDialog(false);
			queryDialog = vaDialog.new WipHisInternalQueryDialog(UI.getActiveShell(), getADTable(), this);
			vaDialog.setEntityQueryDialog(queryDialog);
			queryDialog.open();
		}
	}

	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof WipHisQueryDialog) {
			this.onlineDialog = (WipHisQueryDialog)dialog;
		} else {
			this.onlineDialog = null;
		}
	}
	
	class DetailDialog extends ExtendDialog{
		public static final int COMPONENT_ID = 10000;
		private EntityTableManager tableManager;
		private ADTable adTable;
		protected EntityForm entityForm;
		protected Object object;
		private String TABLE_NAME_LOT = "WIPComponentLot";
		private static final int TRAY_WIDTH = 500;
		private static final int DIALOG_WIDTH = TRAY_WIDTH - 200;
		
		private LotComponentTray tray;

		/*
		 * The tray's control.
		 */
		private Control trayControl;
		
		/*
		 * The separator to the left of the sash.
		 */
		private Label leftSeparator;
		
		/*
		 * The separator to the right of the sash.
		 */
		private Label rightSeparator;
		
		/*
		 * The sash that allows the user to resize the tray.
		 */
		private Sash sash;
		
		public DetailDialog() {
			super();
		}

		public DetailDialog(Object object, ADTable adTable) {
			this();
			this.adTable = adTable;
			this.object = object;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
	        setTitleImage(SWTResourceCache.getImage("search-dialog"));
	        setTitle(Message.getString("wip.history"));
	        setMessage(Message.getString("wip.his_detail"));
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
			Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL
					| SWT.SEPARATOR);
			titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			if(tableManager == null) {
				setTableManager(new EntityTableManager(adTable));
			}
			entityForm = new EntityForm(composite, SWT.NONE, object, tableManager.getADTable(), null);
	        entityForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	        return composite;
	    }

		public EntityTableManager getTableManager() {
			return tableManager;
		}

		public void setTableManager(EntityTableManager tableManager) {
			this.tableManager = tableManager;
		}
		
		@Override
		protected Control createButtonBar(Composite parent) {
			Composite bar = new Composite(parent, SWT.BORDER);
			GridLayout gl1 = new GridLayout(2, false);
			gl1.marginHeight = 0;
			gl1.marginWidth = 0;
			bar.setLayout(gl1);
			bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Composite temp = new Composite(bar, SWT.NONE);
			GridLayout gl2 = new GridLayout(1, false);
			gl2.marginHeight = 0;
			gl2.marginWidth = 0;
			temp.setLayout(gl2);
			
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 0;
			gd.horizontalSpan = 2;
			temp.setLayoutData(gd);
				
			Composite aqComp = new Composite(bar, SWT.NONE);
			GridLayout layout = new GridLayout(0, false);
			layout.makeColumnsEqualWidth = true;
			aqComp.setLayout(layout);
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
					| GridData.VERTICAL_ALIGN_CENTER);
			aqComp.setLayoutData(data);
			aqComp.setFont(parent.getFont());
			createComponentButtonForButtonBar(aqComp);

			Composite composite = new Composite(bar, SWT.NONE);
			GridLayout l = new GridLayout(0, true);
			l.makeColumnsEqualWidth = true;
			composite.setLayout(l);
			GridData data2 = new GridData(GridData.HORIZONTAL_ALIGN_END);
			data2.horizontalAlignment = GridData.END;
			composite.setLayoutData(data2);
			composite.setFont(parent.getFont());
			createButtonsForButtonBar(composite);
			return bar;
		}
		
		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			createButton(parent, IDialogConstants.OK_ID,
					Message.getString("common.ok"), false);
			createButton(parent, IDialogConstants.CANCEL_ID,
					Message.getString("common.cancel"), false);
		}
		
		public void createComponentButtonForButtonBar(Composite parent) {
			createButton(parent, COMPONENT_ID,
	        		Message.getString("wip.component_list"), false);
		}
		
		@Override
		protected void buttonPressed(int buttonId) {
			super.buttonPressed(buttonId);
			if(COMPONENT_ID == buttonId){
				componentPressed();
			}
		}
		
		private void componentPressed(){
			DialogTray existingTray = getTray();
			
			if (existingTray instanceof LotComponentTray) {
				// hide tray
				closeTray();
			}
			else {
				//show tray
				try {
					if (existingTray != null) closeTray();
					LotHis hisLot = (LotHis) object;
					ADManager adManager = Framework.getService(ADManager.class);
					Lot lot = new Lot();
					lot.setObjectRrn(hisLot.getLotRrn());
					lot = (Lot) adManager.getEntity(lot);
					if (lot == null) return;
					if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
						UI.showInfo(Message.getString("wip.can_not_trace_material"));
						return;
					}
					LotComponentTray tray = new LotComponentTray(lot, getLotTable());
					openTray(tray);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					ExceptionHandlerManager.asyncHandleException(e);
				}
			}
		
		}
		
		private ADTable getLotTable() {
			ADTable lotTable = null;
			try {
				ADManager entityManager = Framework.getService(ADManager.class);
				lotTable = entityManager.getADTable(0L, TABLE_NAME_LOT);
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}
			return lotTable;
		}
		
		@Override
		public void openTray(DialogTray tray) throws IllegalStateException, UnsupportedOperationException {
			if (tray == null) {
				throw new NullPointerException("Tray was null"); //$NON-NLS-1$
			}
			if (getTray() != null) {
				throw new IllegalStateException("Tray was already open"); //$NON-NLS-1$
			}
			if (!isCompatibleLayout(getShell().getLayout())) {
				throw new UnsupportedOperationException("Trays not supported with custom layouts"); //$NON-NLS-1$
			}
			final Shell shell = getShell();
			leftSeparator = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);
			leftSeparator.setLayoutData(new GridData(GridData.FILL_VERTICAL));
			sash = new Sash(shell, SWT.VERTICAL);
			sash.setLayoutData(new GridData(GridData.FILL_VERTICAL));
			rightSeparator = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);
			rightSeparator.setLayoutData(new GridData(GridData.FILL_VERTICAL));
			trayControl = ((LotComponentTray) tray).createTrayContents(shell);
			Rectangle clientArea = shell.getClientArea();
			final GridData data = new GridData(GridData.FILL_VERTICAL);
			data.widthHint = TRAY_WIDTH;//重载这个方法的目的只是为了重设宽度
			trayControl.setLayoutData(data);
			Rectangle bounds = shell.getBounds();
			shell.setBounds(bounds.x, bounds.y, bounds.width + DIALOG_WIDTH, bounds.height);
			sash.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if (event.detail != SWT.DRAG) {
						Rectangle clientArea = shell.getClientArea();
						int newWidth = clientArea.width - event.x - (sash.getSize().x + rightSeparator.getSize().x);
						if (newWidth != data.widthHint) {
							data.widthHint = newWidth;
							shell.layout();
						}
					}
				}
			});
			this.tray = (LotComponentTray) tray;
		}
		
		private boolean isCompatibleLayout(Layout layout) {
			if (layout != null && layout instanceof GridLayout) {
				GridLayout grid = (GridLayout)layout;
				return !grid.makeColumnsEqualWidth && (grid.horizontalSpacing == 0) &&
						(grid.marginWidth == 0) && (grid.marginHeight == 0) &&
						(grid.horizontalSpacing == 0) && (grid.numColumns == 5);
			}
			return false;
		}

		@Override
		public DialogTray getTray() {
			return tray;
		}
		
		@Override
		public void closeTray() throws IllegalStateException {
			if (getTray() == null) {
				throw new IllegalStateException("Tray was not open"); //$NON-NLS-1$
			}
			int trayWidth = DIALOG_WIDTH;
			trayControl.dispose();
			trayControl = null;
			tray = null;
			leftSeparator.dispose();
			leftSeparator = null;
			rightSeparator.dispose();
			rightSeparator = null;
			sash.dispose();
			sash = null;
			Shell shell = getShell();
			Rectangle bounds = shell.getBounds();
			shell.setBounds(bounds.x + ((getDefaultOrientation() == SWT.RIGHT_TO_LEFT) ? trayWidth : 0), bounds.y, bounds.width - trayWidth, bounds.height);
		}
	}
	
	class LotComponentTray extends DialogTray{
		private Lot parentLot;
		private ADTable adTable;
		private TreeViewer viewer;
		private ComponentTreeManager treeManager;
		
		public LotComponentTray(Lot parentLot, ADTable adTable) {
			super();
			this.parentLot = parentLot;
			this.adTable = adTable;
		}

		public Control createTrayContents(Composite parent){
			return createContents(parent);
		}
		
		@Override
		protected Control createContents(Composite parent) {
			FormToolkit toolkit = new FormToolkit(Display.getCurrent());
			
			Composite client = toolkit.createComposite(parent);
			configureBody(client);
			
			treeManager = new ComponentTreeManager(SWT.NULL, adTable);
			viewer = (TreeViewer) treeManager.createViewer(client, toolkit);
			
			treeManager.setInput(getInput());
			return client;
		}
		
		private List<Lot> getInput() {
			List<Lot> list = new ArrayList<Lot>();
			list.add(parentLot);
			return list;
		}
		
		protected void configureBody(Composite body) {
			GridLayout layout = new GridLayout(1, true);
			layout.horizontalSpacing = 0;
			layout.verticalSpacing = 0;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.marginTop = 0;
			layout.marginBottom = 0;
			body.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_BOTH);
			body.setLayoutData(gd);
		}
		
	}
}

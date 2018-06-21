package com.graly.erp.inv.in.mo;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class WzProductedMoSection implements IRefresh{
	private static final Logger logger = Logger.getLogger(WzProductedMoSection.class);
	private static String WC_PREFIX = " 1=1 AND qtyReceive > qtyIn ";

	protected EntityTableManager tableManager;
	protected StructuredViewer viewer;
	private String whereClause;
	protected Section section;
	protected IManagedForm form;
	protected IFormPart spart;
	protected ToolItem itemSearch;
	protected ToolItem itemRefresh;
	protected SashForm sashForm;

	private ManufactureOrder selectedMo;
	private EntityQueryDialog queryDialog;

	public WzProductedMoSection(EntityTableManager tableManager) {
		super();
		this.setTableManager(tableManager);
	}

	public void createContent(ManagedForm managedForm, Composite parent) {
		this.form = managedForm;

		final ADTable table = getTableManager().getADTable();

		FormToolkit toolkit = managedForm.getToolkit();
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText(I18nUtil.getI18nMessage(table, "label"));
		section.marginWidth = 2;
		section.marginHeight = 2;
		toolkit.createCompositeSeparator(section);
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 0;
		parent.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = false;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);
		GridLayout ly = new GridLayout();
		layout.numColumns = 1;
		client.setLayout(ly);

		section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));
		createToolBar(section);
		createSectionDesc(section);

		viewer = getTableManager().createViewer(client, toolkit, 300);
		EntityItemInput input = new EntityItemInput(getTableManager().getADTable(), getWhereClause(), "");
		viewer.setInput(input);

		getTableManager().updateView(viewer);
		section.setClient(client);
		createViewAction(viewer);
	}
	
	protected void createViewAction(StructuredViewer viewer) {
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
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemSearch(ToolBar tBar) {
		itemSearch = new ToolItem(tBar, SWT.PUSH);
		itemSearch.setText(Message.getString("common.search"));
		itemSearch.setImage(SWTResourceCache.getImage("search"));
		itemSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				searchAdapter();
			}
		});
	}

	protected void createToolItemRefresh(ToolBar tBar) {
		itemRefresh = new ToolItem(tBar, SWT.PUSH);
		itemRefresh.setText(Message.getString("common.refresh"));
		itemRefresh.setImage(SWTResourceCache.getImage("refresh"));
		itemRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refreshAdapter();
			}
		});
	}

	protected void createSectionDesc(Section section) {
		try {
			String text = Message.getString("common.totalshow");
			ADManager entityManager = Framework.getService(ADManager.class);
			long count = entityManager.getEntityCount(Env.getOrgRrn(), getTableManager().getADTable().getObjectRrn(), getWhereClause());
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e) {
			logger.error("ProductedMoSection : createSectionDesc ", e);
		}
	}

	public void setTableManager(EntityTableManager tableManager) {
		this.tableManager = tableManager;
	}

	public EntityTableManager getTableManager() {
		return tableManager;
	}

	public void refresh() {
		viewer.setInput(new EntityItemInput(getTableManager().getADTable(), getWhereClause(), ""));
		tableManager.updateView(viewer);
		createSectionDesc(section);
	}

	protected void searchAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof ManufactureOrder) {
			selectedMo = (ManufactureOrder) obj;
		} else {
			selectedMo = null;
		}
	}

	protected void refreshAdapter() {
		refresh();
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getWhereClause() {
		if(whereClause != null && !"".equals(whereClause.trim())) {
			return whereClause;
		}
		return WC_PREFIX;
	}

	public ManufactureOrder getSelectedMo() {
		return selectedMo;
	}

	public void setSelectedMo(ManufactureOrder selectedMo) {
		this.selectedMo = selectedMo;
	}
}

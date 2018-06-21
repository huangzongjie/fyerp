package com.graly.erp.pur.lot.query;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.po.down.PoDownDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.model.Lot;

public class MerchandiseBatchSection    {
	private static final Logger logger = Logger.getLogger(MerchandiseBatchSection.class);
	public static final String TABLE_ANME_MO_LINE = "WIPManufactureOrderLine";
	public static final String FIELD_DATE_END = "dateEnd";
	protected Section section;
	protected IManagedForm form;
	protected ADTable   wipLotAdTable;
	private TableListManager tableManager;
	private TableViewer viewer;
	protected MerchandiseBatchDialog mbatchDialog;
	protected String queryLotId;
	protected ToolItem itemQuery;
	protected EntityQueryDialog  queryDialog;
//	protected List<Lot> lots;
	public MerchandiseBatchSection(IManagedForm form){
		this.form = form;
	}
	
	public MerchandiseBatchSection(ADTable moLineAdTable, ADTable wcAdTable) {
		this.wipLotAdTable = wcAdTable;
	}

	public void refresh() {
		tableManager.setInput(getMerchandiseLots(queryLotId));
		tableManager.updateView(viewer);
		createSectionDesc(section);
	}
	protected void createSectionDesc(Section section){
		String text = Message.getString("common.totalshow");
		int count = viewer.getTable().getItemCount();
		if (count > Env.getMaxResult()) {
			text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
		} else {
			text = String.format(text, String.valueOf(count), String.valueOf(count));
		}
		section.setDescription("  " + text);
	}
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		section.setTextClient(tBar);
	}
	protected void createToolItemSearch(ToolBar tBar) {
		itemQuery = new ToolItem(tBar, SWT.PUSH);
		itemQuery.setText(Message.getString("common.search_Title"));
		itemQuery.setImage(SWTResourceCache.getImage("search"));
		itemQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryAdapter();
			}
		});
	}
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		}  
	}
	public void createContent(Composite parent) {
		if(wipLotAdTable == null) {
			wipLotAdTable = getAdTableByName(MerchandiseBatchPage.TABLE_NAME);
		}
		FormToolkit toolkit = form.getToolkit();
//		this.form = form;
		section = toolkit.createSection(parent, Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(wipLotAdTable, "label")));
		section.marginWidth = 3;
		section.marginHeight = 4;
		toolkit.createCompositeSeparator(section);

		createToolBar(section);

//		TableWrapLayout layout = new TableWrapLayout();
//		layout.topMargin = 0;
//		layout.leftMargin = 5;
//		layout.rightMargin = 2;
//		layout.bottomMargin = 0;
//		parent.setLayout(layout);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
//		TableWrapData td = new TableWrapData(TableWrapData.FILL,
//				TableWrapData.FILL);
//		td.grabHorizontal = true;
//		td.grabVertical = false;
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
//		section.setBackground(new Color(null, 255,122,111));

		Composite client = toolkit.createComposite(section);	 

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);

		createSectionContent(client);

		toolkit.paintBordersFor(section);
		section.setClient(client);
		
			createSectionDesc(section);
	}
	
	protected void createSectionContent(Composite client) {
		FormToolkit toolkit = form.getToolkit();
		tableManager = new  TableListManager(wipLotAdTable);
		viewer = (TableViewer) tableManager.createViewer(client, toolkit);
//		refresh();
	}
	

	
	protected ADTable getAdTableByName(String tableName) {
		ADTable adTable = null;;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = (ADTable)entityManager.getADTable(0, tableName);
		} catch(Exception e) {
			logger.error("InvMaterialSection : getAdTableOfInvMaterial()", e);
		}
		return adTable;
	}
	
	public void setIManagedForm(IManagedForm form) {
		this.form = form;
	}
	


	public ADTable getWipLotAdTable() {
		return wipLotAdTable;
	}

	public void setWipLotAdTable(ADTable wipLotAdTable) {
		this.wipLotAdTable = wipLotAdTable;
	}

	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof PoDownDialog) {
			this.mbatchDialog = (MerchandiseBatchDialog)dialog;
		} else {
			this.mbatchDialog = null;
		}
	}

	protected List<Lot> getMerchandiseLots(String lotId) {
		List<Lot>  lots =null;
		 if(lotId !=null && lotId.trim().length() !=0){
			try {
				PURManager purManager = Framework.getService(PURManager.class);
				  lots =purManager.getMerchandiseLots(lotId, Env.getOrgRrn());
				return lots;
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		return lots;
	}

	public String getQueryLotId() {
		return queryLotId;
	}

	public void setQueryLotId(String queryLotId) {
		this.queryLotId = queryLotId;
	}

	public EntityQueryDialog getQueryDialog() {
		return queryDialog;
	}

	public void setQueryDialog(EntityQueryDialog queryDialog) {
		this.queryDialog = queryDialog;
	}


	
}

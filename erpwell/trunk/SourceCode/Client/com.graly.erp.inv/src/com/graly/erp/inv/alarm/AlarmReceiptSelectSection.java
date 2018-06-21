package com.graly.erp.inv.alarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.in.createfrom.iqc.EntityListTableManager;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.model.ReceiptLine;
import com.graly.erp.po.model.PurchaseOrderLine;
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
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AlarmReceiptSelectSection implements IRefresh{
	private static final Logger logger = Logger.getLogger(AlarmReceiptSelectSection.class);
	private static String ReceiptWhereClause = " isIqc != 'Y' " + " AND docStatus='" + Iqc.STATUS_APPROVED + "' ";
	private AlarmReceiptSelectPage parentPage;
	private ADTable adTable;
	private ManagedForm form;
	private EntityListTableManager tableManager;
	private TableViewer viewer;
	
	private Section section;
	private ToolItem itemQuery;
	protected EntityQueryDialog queryDialog;
	protected String whereClause;
	private String alarmWhereClause;

	public AlarmReceiptSelectSection(ADTable table, AlarmReceiptSelectPage parentPage,String alarmWhereClause) {
		this.adTable = table;
		this.parentPage = parentPage;
		this.alarmWhereClause = alarmWhereClause;
	}
	
	public void createContents(ManagedForm form, Composite parent) {
		this.form = form;
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		final FormToolkit toolkit = form.getToolkit();
		
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText(String.format(Message.getString("common.list"),I18nUtil.getI18nMessage(adTable, "label")));
		section.marginWidth = 0;
	    section.marginHeight = 0;
	    toolkit.createCompositeSeparator(section);

	    createToolBar(section);
	    
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    
	    createSectionContent(client);
	    section.setClient(client);
	}

	protected void createSectionContent(Composite client) {
		try {
			tableManager = new EntityListTableManager(adTable);
			viewer = (TableViewer)tableManager.createViewer(client, form.getToolkit());
			viewer.addSelectionChangedListener(getSelectionChangedListener());
			
			ADManager adManager = Framework.getService(ADManager.class);
			List<Receipt> ls = adManager.getEntityList(Env.getOrgRrn(), Receipt.class, Env.getMaxResult(), ReceiptWhereClause+alarmWhereClause, " objectRrn ASC");
			List<Receipt> rps = new ArrayList<Receipt>();
			for(Receipt r : ls){
				boolean needIqc = false;
				String receiptLineWhereClause = " receiptRrn = " + r.getObjectRrn();
				List<ReceiptLine> rpls = adManager.getEntityList(Env.getOrgRrn(), ReceiptLine.class, Env.getMaxResult(), receiptLineWhereClause, null);
				for(ReceiptLine rl : rpls){
					if(!rl.getIsIqc() && PurchaseOrderLine.LINESTATUS_APPROVED.equals(rl.getPoLine().getLineStatus())){//未检验过并且对应的采购订单行是Approved的才取出
						needIqc = true;
						break;
					}
				}
				
				if(needIqc){
					rps.add(r);
				}
			}
			viewer.setInput(rps);
			tableManager.updateView(viewer);
		} catch(Exception e) {
			logger.error("ReceiptSelectSection : createAdObject() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}

	private ISelectionChangedListener getSelectionChangedListener() {
		return new ISelectionChangedListener() {
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					parentPage.setSelectedReceipt((Receipt)ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    };
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemQuery(tBar);
		
		section.setTextClient(tBar);
	}

	private void createToolItemQuery(ToolBar tBar) {
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
		} else {
			EntityTableManager tableManager = new EntityTableManager(adTable);
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}

	public TableViewer getViewer() {
		return viewer;
	}

	@Override
	public String getWhereClause() {
		return whereClause;
	}

	@Override
	public void refresh() {
		viewer.setInput(new EntityItemInput(adTable, getWhereClause(), ""));		
		tableManager.updateView(viewer);
	}

	@Override
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

}

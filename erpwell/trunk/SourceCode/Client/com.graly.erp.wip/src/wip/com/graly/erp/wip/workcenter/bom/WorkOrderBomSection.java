package com.graly.erp.wip.workcenter.bom;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.dialog.PurAssociatedDialog;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.pur.request.RequisitionLineBlockDialog;
import com.graly.erp.wip.mo.create.MaterialSumDialog;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class WorkOrderBomSection {
	private static final Logger logger = Logger.getLogger(WorkOrderBomSection.class);
	public static final String TABLE_NAME_PR_LINE = "WorkCenterRequisitionLine";
	public static final String TABLE_NAME_PO_LINE = "WorkCenterPurchaseOrderLine";
	private ManufactureOrderLine selectedMoLine;
	private WorkOrderBomManager tableManager;
	private TableViewer viewer;
	private ADTable adTable;
	private ADTable poTable;
	
	protected Section section;
	protected IManagedForm form;
//	protected ToolItem relationItem;
	protected ToolItem purView;
	protected ToolItem printItem;
//	protected Menu relationMenu;
	private ManufactureOrderBom selectedBom;
	
	ADManager adManager;

	public WorkOrderBomSection() {}
	
	public WorkOrderBomSection(ADTable adTable, ManufactureOrderLine selectedMoLine) {
		this.adTable = adTable;
		this.selectedMoLine = selectedMoLine;
	}
	
	public void createContents(IManagedForm form, Composite parent){
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();		
		section = toolkit.createSection(parent, SWT.FLAT | SWT.HORIZONTAL);
		section.setText(Message.getString("wip.children_material_list"));
		section.marginWidth = 3;
		section.marginHeight = 4;
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
	    createSectionContents(form, client);

	    section.setClient(client);
	}

	protected void createSectionContents(IManagedForm form, Composite parent){
		tableManager = new WorkOrderBomManager(adTable);
		viewer = (TableViewer)tableManager.createViewer(parent, form.getToolkit());
		viewer.addSelectionChangedListener(getTreeSelectionListener());
		viewer.setInput(getBomList());
		tableManager.updateView(viewer);
		displayRedItems(viewer);
	}
	
	private void displayRedItems(TableViewer v) {//当需求数大于库存数时该行显示为红色
		for(TableItem it : v.getTable().getItems()){
			ManufactureOrderBom mob = (ManufactureOrderBom) it.getData();
			if(mob != null){
				if(mob.getQtyNeed().compareTo(mob.getQtyOnHand()) > 0){
					it.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				}
			}
		}
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemRelation(tBar);
		createToolItemPurView(tBar);
		createPrintItem(tBar);
		section.setTextClient(tBar);
	}
	
//	protected void createToolItemRelation(final ToolBar tBar) {
//		relationItem = new ToolItem(tBar, SWT.DROP_DOWN);
//		relationItem.setText(Message.getString("inv.relationship"));
//		relationItem.setImage(SWTResourceCache.getImage("search"));
//		relationItem.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event event) {
//				if (event.detail == SWT.ARROW) {
//					Rectangle bounds = relationItem.getBounds();
//					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
//					relationMenu.setLocation(point);
//					relationMenu.setVisible(true);
//				}
//			}
//		 });
//		createRelationMenus(tBar);
//	}
	
	protected void createToolItemPurView(ToolBar tBar) {
		printItem = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_BOM);
		printItem.setText(Message.getString("wip.relationship_pur"));
		printItem.setImage(SWTResourceCache.getImage("preview"));
		printItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				purViewAdapter();
			}
		});
	}
	
	protected void createPrintItem(ToolBar tBar) {
		printItem = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_BOM);
		printItem.setText(Message.getString("common.print"));
		printItem.setImage(SWTResourceCache.getImage("preview"));
		printItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				printAdapter();
			}
		});
	}
	
	protected void printAdapter() {
		try {
			String report = "moBom_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			Long moRrn = 0L;
			Long moLineRrn = 0L;
			if(selectedMoLine != null){
				moRrn = selectedMoLine.getMasterMoRrn();
				moLineRrn = selectedMoLine.getObjectRrn();
			}else{
				UI.showError(Message.getString("mo.no_mo_selected"));
				return;
			}
			userParams.put("MO_RRN", String.valueOf(moRrn));
			userParams.put("MO_LINE_RRN", String.valueOf(moLineRrn));
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}	
	}

//	protected void createRelationMenus(final ToolBar toolBar) {
//		relationMenu = new Menu(UI.getActiveShell(), SWT.POP_UP);
////		MenuItem viewWHouse = new MenuItem(relationMenu, SWT.PUSH);
////		viewWHouse.setText(Message.getString("inv.view_storage"));
////		viewWHouse.addSelectionListener(new SelectionAdapter() {
////			@Override
////			public void widgetSelected(SelectionEvent e) {
////				houseViewAdapter();
////			}
////		});
//		
//		MenuItem viewPurLine = new MenuItem(relationMenu, SWT.PUSH);
//		viewPurLine.setText(Message.getString("wip.relationship_pur"));
//		viewPurLine.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				purViewAdapter();
//			}
//		});
//	}
	
	protected void houseViewAdapter() {
		try {
			if(selectedBom != null) {
				MaterialSumDialog dialog = new MaterialSumDialog(UI.getActiveShell(), selectedBom);
				if(dialog.open() == Dialog.OK) {
				}
			}			
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("WorkOrderBomSection : houseViewAdapter()", e);
		}
	}
	
	protected void purViewAdapter() {
		try {
			if(selectedBom != null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				adTable = adManager.getADTable(0L, TABLE_NAME_PR_LINE);
				adTable = adManager.getADTableDeep(adTable.getObjectRrn());
				ADTable poTable = adManager.getADTable(0L, TABLE_NAME_PO_LINE);
				poTable = adManager.getADTableDeep(poTable.getObjectRrn());
				
				Long materialRrn = selectedBom.getMaterialRrn();

				PurAssociatedDialog cd = new PurAssociatedDialog(UI.getActiveShell(),
						adTable, poTable, materialRrn);
				if(cd.open() == Dialog.CANCEL) {
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("WorkOrderBomSection : purViewAdapter()", e);
		}
	}

	protected ISelectionChangedListener  getTreeSelectionListener() {
		return new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (viewer.getTable().getSelectionCount() == 0) return;
				Object obj = viewer.getTable().getSelection()[0].getData();
				if(obj instanceof ManufactureOrderBom) {
					selectedBom = (ManufactureOrderBom)obj;
				}
			}
		};
	}
	
	private List<ManufactureOrderBom> getBomList() {
//		List<DocumentationLine> docLines = new ArrayList<DocumentationLine>();
		try {
			WipManager wipManager = Framework.getService(WipManager.class);
			List<ManufactureOrderBom> moBoms = wipManager.getMoLineBom(selectedMoLine.getObjectRrn());
			
//			ManufactureOrder mo = new ManufactureOrder();
//			mo.setOrgRrn(selectedMoLine.getOrgRrn());
//			if(selectedMoLine.getMasterMoRrn() != null) {
//				mo.setObjectRrn(selectedMoLine.getMasterMoRrn());
//			} else {
//				// objectRrn设为0, 使之在下面调用getMoLine()时可以调用后台getMoLineFromDB()
//				mo.setObjectRrn(0L);
//			}
//			List<DocumentationLine> list = wipManager.getMoLine(mo, moBoms);
//			for(DocumentationLine docLine : list) {
//				if(docLine != null) {
//					docLines.add(docLine);
//				}
//			}
			// 加上moBoms中moLineRrn和prLineRrn都为空的Bom
//			for(ManufactureOrderBom moBom : moBoms) {
//				if(moBom != null && moBom.getMoLineRrn() == null && moBom.getRequsitionLineRrn() == null) {
//					docLines.add(moBom);
//				}
//			}
			// 为每个docLine计算库存数、在途数、在制品数和已分配数
			MaterialSum ms = null;
			for(ManufactureOrderBom moBom : moBoms) {
				ms = wipManager.getMaterialSum(Env.getOrgRrn(), moBom.getMaterialRrn(), false, false);
				if(ms != null) {
					moBom.setQtyAllocation(ms.getQtyAllocation());
					moBom.setQtyMoLineWip(ms.getQtyMoLineWip());
					moBom.setQtyOnHand(ms.getQtyOnHand());
					moBom.setQtyTransit(ms.getQtyTransit());
				}
			}
			return moBoms;
		} catch(Exception e) {
			logger.error("Error at WorkOrderBomSection ：getBomList() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
}

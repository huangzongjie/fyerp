package com.graly.erp.wip.workcenter.schedule.query;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.WorkShopSchedule;
import com.graly.erp.wip.workcenter.WorkShopSchedduleDialog;
import com.graly.erp.wip.workcenter.bom.WorkOrderBomDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
public class ScheduleSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(ScheduleSection.class);
	protected ToolItem itemDetail;
	protected WorkShopSchedule selectSchedule;
	
	protected ToolItem itemStart;
	protected ToolItem itemSuspend;
	protected ToolItem itemShort;
	protected ToolItem itemCompleted;
	protected ToolItem itemReport;
	protected ToolItem itemColor1;
	protected ToolItem itemColor2;
	protected ToolItem itemColor3;
	protected ToolItem itemColor4;
	protected ToolItem itemColor5;
	protected ToolItem itemColor6;
	protected Menu menu1;
	protected Menu menu2;
	protected Menu menu3;
	protected Menu menu4;
	protected Menu menu5;
	protected Menu menu6;
	protected Label labe;
	protected ToolItem itemBomTree;
	
	protected ScheduleReportQueryDialog reportDialog;
	protected WorkShopScheduleQuerySection parentSection;
	public static String description ="绿色：开始  黄色：完成. 红色：缺料   紫色: 暂停.";
	
	public ScheduleSection(EntityTableManager tableManager,WorkShopScheduleQuerySection parentSection) {
		super(tableManager);
		this.parentSection = parentSection;
	}
	
	protected void createNewViewer(Composite client, final IManagedForm form){
		final ADTable table = getTableManager().getADTable();
		getTableManager().setStyle(getTableManager().getStyle()+SWT.MULTI);
		viewer = getTableManager().createViewer(client, form.getToolkit());
	    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					try{
						Object obj = Class.forName(table.getModelClass()).newInstance();
						if (obj instanceof ADBase) {
							((ADBase)obj).setOrgRrn(Env.getOrgRrn());
						}
						form.fireSelectionChanged(spart, new StructuredSelection(new Object[] {obj}));
					} catch (Exception e){
						e.printStackTrace();
					}
				} else {
					form.fireSelectionChanged(spart, event.getSelection());
				}
			}
		});
	    String whereClause = this.getWhereClause();
	    String initWhereClause = this.getTableManager().getADTable().getInitWhereClause();
	    if(whereClause == null){
	    	whereClause = " 1=1 ";
	    	setWhereClause(whereClause);
	    }
	    
	    if(initWhereClause != null && initWhereClause.trim().length() > 0){
	    	StringBuffer sb = new StringBuffer(whereClause);
	    	sb.append(" and " + initWhereClause);
	    	setWhereClause(sb.toString());
	    }
	    refresh();
	    createViewAction(viewer);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		
		createToolItemBomTree(tBar);
		createToolItemReport(tBar);
		createToolItemStart(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSuspend(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemShort(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemCompleted(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDetail(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemColor1(tBar);
		createToolItemColor2(tBar);
		createToolItemColor3(tBar);
		createToolItemColor4(tBar);
		createToolItemColor5(tBar);
		createToolItemColor6(tBar);
		section.setTextClient(tBar);
	}
	

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionWorkShopSchedule(ss.getFirstElement());
				detialAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionWorkShopSchedule(ss.getFirstElement());
					Object[] objects = ss.toArray();
					BigDecimal bigDecimal = BigDecimal.ZERO;
					for(Object object : objects){
						WorkShopSchedule wsSchedule = (WorkShopSchedule) object;
						BigDecimal qtyPlan = wsSchedule.getQtyPlanProcuct()==null?BigDecimal.ZERO:wsSchedule.getQtyPlanProcuct();
						bigDecimal = bigDecimal.add(qtyPlan);
					}
					String text = description+"          统计计划总数:"+bigDecimal.toString();
					labe.setText(text);
					labe.pack();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setSelectionWorkShopSchedule(Object obj) {
		if (obj instanceof WorkShopSchedule) {
			selectSchedule = (WorkShopSchedule) obj;
		} else {
			selectSchedule = null;
		}
	}
	
	protected void createToolItemBomTree(ToolBar tBar) {
		itemBomTree = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_BOM);
		itemBomTree.setText(Message.getString("pdm.bom"));
		itemBomTree.setImage(SWTResourceCache.getImage("bomtree"));
		itemBomTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				bomTreeAdapter();
			}
		});
	}
	
	protected void createToolItemReport(ToolBar tBar) {
		itemReport = new ToolItem(tBar, SWT.PUSH);
		itemReport.setText("报表");
		itemReport.setImage(SWTResourceCache.getImage("export"));
		itemReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				reportAdapter();
			}
		});
	}
	
	protected void createToolItemDetail(ToolBar tBar) {
		itemDetail = new ToolItem(tBar, SWT.PUSH);
		itemDetail.setText("查看");
		itemDetail.setImage(SWTResourceCache.getImage("export"));
		itemDetail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				detialAdapter();
			}
		});
	}
	
	protected void createToolItemColor1(final ToolBar tBar) {
		menu1 = new Menu(UI.getActiveShell(), SWT.POP_UP);
		createMenuItemColor(menu1,"menu1");
		
		itemColor1 = new ToolItem(tBar,  SWT.DROP_DOWN);
		itemColor1.setText("缠绕车间");
		itemColor1.setImage(SWTResourceCache.getImage("export"));
		itemColor1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(event.detail == SWT.ARROW){
					Rectangle bounds = itemColor1.getBounds();
					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu1.setLocation(point);
					menu1.setVisible(true);
				}
			}
		});
		
	
	}
	
	protected void createToolItemColor2(final ToolBar tBar) {
		menu2 = new Menu(UI.getActiveShell(), SWT.POP_UP);
		createMenuItemColor(menu2,"menu2");
		
		itemColor2 = new ToolItem(tBar,  SWT.DROP_DOWN);
		itemColor2.setText("控制阀");
		itemColor2.setImage(SWTResourceCache.getImage("export"));
		itemColor2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(event.detail == SWT.ARROW){
					Rectangle bounds = itemColor2.getBounds();
					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu2.setLocation(point);
					menu2.setVisible(true);
				}
			}
		});
	}
	
	protected void createToolItemColor3(final ToolBar tBar) {
		menu5 = new Menu(UI.getActiveShell(), SWT.POP_UP);
		createMenuItemColor(menu5,"menu5");
		
		itemColor5 = new ToolItem(tBar,  SWT.DROP_DOWN);
		itemColor5.setText("射流器");
		itemColor5.setImage(SWTResourceCache.getImage("export"));
		itemColor5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(event.detail == SWT.ARROW){
					Rectangle bounds = itemColor5.getBounds();
					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu5.setLocation(point);
					menu5.setVisible(true);
				}
			}
		});
	}
	
	protected void createToolItemColor4(final ToolBar tBar) {
		menu6 = new Menu(UI.getActiveShell(), SWT.POP_UP);
		createMenuItemColor(menu6,"menu6");
		
		itemColor6 = new ToolItem(tBar,  SWT.DROP_DOWN);
		itemColor6.setText("吹塑");
		itemColor6.setImage(SWTResourceCache.getImage("export"));
		itemColor6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(event.detail == SWT.ARROW){
					Rectangle bounds = itemColor6.getBounds();
					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu6.setLocation(point);
					menu6.setVisible(true);
				}
			}
		});
	}
	
	protected void createToolItemColor5(final ToolBar tBar) {
		menu3 = new Menu(UI.getActiveShell(), SWT.POP_UP);
		createMenuItemColor(menu3,"menu3");
		
		itemColor3 = new ToolItem(tBar,  SWT.DROP_DOWN);
		itemColor3.setText("切割");
		itemColor3.setImage(SWTResourceCache.getImage("export"));
		itemColor3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(event.detail == SWT.ARROW){
					Rectangle bounds = itemColor3.getBounds();
					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu3.setLocation(point);
					menu3.setVisible(true);
				}
			}
		});
	}
	
	protected void createToolItemColor6(final ToolBar tBar) {
		menu4 = new Menu(UI.getActiveShell(), SWT.POP_UP);
		createMenuItemColor(menu4,"menu4");
		
		itemColor4 = new ToolItem(tBar,  SWT.DROP_DOWN);
		itemColor4.setText("纸箱");
		itemColor4.setImage(SWTResourceCache.getImage("export"));
		itemColor4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(event.detail == SWT.ARROW){
					Rectangle bounds = itemColor4.getBounds();
					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu4.setLocation(point);
					menu4.setVisible(true);
				}
			}
		});
	}
	
	protected void detialAdapter() {
		try {
			if(selectSchedule!=null){
				ADTable adTable = getAdTableByName("WorkShopSchedule");
				String whereClause = "objectRrn ="+selectSchedule.getObjectRrn();
				WorkShopSchedduleDialog olbd = new WorkShopSchedduleDialog(UI.getActiveShell(),
						adTable, whereClause, selectSchedule, null);
				if(olbd.open() == Dialog.CANCEL) {
					refresh();
					this.getParentSection().getNoScheduleSection().refresh();
				}
			}
		 
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected ADTable getAdTableByName(String tableName) {
		ADTable adTable = null;;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = (ADTable)entityManager.getADTable(0, tableName);
			adTable=entityManager.getADTableDeep(adTable.getObjectRrn());
		} catch(Exception e) {
			logger.error("ScheduleSection : getAdTableByName()", e);
		}
		return adTable;
	}
	
	protected void refreshSection() {
		TableViewer tViewer = (TableViewer) viewer;
		Table table = tViewer.getTable();
		for(int i =0;i<table.getItems().length;i++){
			TableItem item = table.getItems()[i];
			WorkShopSchedule workShopSchedule = (WorkShopSchedule) item.getData();
			if(WorkShopSchedule.DOC_STATUS_START.equals(workShopSchedule.getDocStatus())){
				Color greenColor = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
				item.setBackground(2,greenColor);
			}else if(WorkShopSchedule.DOC_STATUS_SHORT.equals(workShopSchedule.getDocStatus())){
				Color redColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
				item.setBackground(2,redColor);
			}else if(WorkShopSchedule.DOC_STATUS_COMPLETED.equals(workShopSchedule.getDocStatus())){
				Color yellowColor = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
				item.setBackground(2,yellowColor);
			}else if(WorkShopSchedule.DOC_STATUS_SUSPEND.equals(workShopSchedule.getDocStatus())){
				Color ziseColor = new Color(null,new RGB(139, 0, 255));
				item.setBackground(2,ziseColor);
			}
			tableItemColor(item, workShopSchedule.getWcolor1(),11,workShopSchedule);
			tableItemColor(item, workShopSchedule.getWcolor2(),12,workShopSchedule);
			tableItemColor(item, workShopSchedule.getWcolor5(),13,workShopSchedule);
			tableItemColor(item, workShopSchedule.getWcolor6(),14,workShopSchedule);
			tableItemColor(item, workShopSchedule.getWcolor3(),15,workShopSchedule);
			tableItemColor(item, workShopSchedule.getWcolor4(),16,workShopSchedule);
		}
	}
	
	public void refresh(){
		super.refresh();
		refreshSection();
	}
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new WorkShopScheduleQueryDialog(UI.getActiveShell(), tableManager, this,this);
			queryDialog.open();
		}
	}
	
	@Override
	protected void createSectionTitle(Composite client) {
		 labe =new Label(client, 0);
		 labe.setText(description);
	}
	
	protected void createToolItemStart(ToolBar tBar) {
		itemStart = new ToolItem(tBar, SWT.PUSH);
		itemStart.setText("开始");
		itemStart.setImage(SWTResourceCache.getImage("feature"));
		itemStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				startAdapter();
			}
		});
	}
	
	protected void createToolItemSuspend(ToolBar tBar) {
		itemQuery = new ToolItem(tBar, SWT.PUSH);
		itemQuery.setText("暂停");
		itemQuery.setImage(SWTResourceCache.getImage("feature"));
		itemQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				suspendAdapter();
			}
		});
	}
	
	protected void createToolItemShort(ToolBar tBar) {
		itemQuery = new ToolItem(tBar, SWT.PUSH);
		itemQuery.setText("缺料");
		itemQuery.setImage(SWTResourceCache.getImage("feature"));
		itemQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				shortAdapter();
			}
		});
	}
	
	protected void createToolItemCompleted(ToolBar tBar) {
		itemQuery = new ToolItem(tBar, SWT.PUSH);
		itemQuery.setText("完成");
		itemQuery.setImage(SWTResourceCache.getImage("feature"));
		itemQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				completedAdapter();
			}
		});
	}
	
	protected void startAdapter() {
		try {
			if(selectSchedule!=null){
				ADManager adManager = Framework.getService(ADManager.class);
				selectSchedule = (WorkShopSchedule) adManager.getEntity(selectSchedule);
				selectSchedule.setDocStatus(WorkShopSchedule.DOC_STATUS_START);
				adManager.saveEntity(selectSchedule, Env.getOrgRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void suspendAdapter() {
		try {
			if(selectSchedule!=null){
				ADManager adManager = Framework.getService(ADManager.class);
				selectSchedule = (WorkShopSchedule) adManager.getEntity(selectSchedule);
				selectSchedule.setDocStatus(WorkShopSchedule.DOC_STATUS_SUSPEND);
				adManager.saveEntity(selectSchedule, Env.getOrgRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void shortAdapter() {
		try {
			if(selectSchedule!=null){
				ADManager adManager = Framework.getService(ADManager.class);
				selectSchedule = (WorkShopSchedule) adManager.getEntity(selectSchedule);
				selectSchedule.setDocStatus(WorkShopSchedule.DOC_STATUS_SHORT);
				adManager.saveEntity(selectSchedule, Env.getOrgRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void completedAdapter() {
		try {
			if(selectSchedule!=null){
				ADManager adManager = Framework.getService(ADManager.class);
				selectSchedule = (WorkShopSchedule) adManager.getEntity(selectSchedule);
				selectSchedule.setDocStatus(WorkShopSchedule.DOC_STATUS_COMPLETED);
				adManager.saveEntity(selectSchedule, Env.getOrgRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void bomTreeAdapter() {
		if (selectSchedule != null && selectSchedule.getObjectRrn() != null) {
			ADManager adManager;
			try {
				adManager = Framework.getService(ADManager.class);
			
			ManufactureOrderLine selectMoLine = new ManufactureOrderLine();
			selectMoLine.setObjectRrn(selectSchedule.getMoLineRrn());
			selectMoLine = (ManufactureOrderLine) adManager.getEntity(selectMoLine);
			WorkOrderBomDialog dialog = new WorkOrderBomDialog(UI.getActiveShell(), selectMoLine, tableManager.getADTable());
			dialog.open();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected void reportAdapter() {
		ADTable adTable = getAdTableByName("WorkShopScheduleQueryReport");
		EntityTableManager tableManager = new EntityTableManager(adTable);
		reportDialog =  new ScheduleReportQueryDialog(UI.getActiveShell(), tableManager, this);
		reportDialog.open();
//		if (reportDialog != null) {
//			reportDialog.setVisible(true);
//		} else {
//			reportDialog =  new ScheduleReportQueryDialog(UI.getActiveShell(), tableManager, this);
//			reportDialog.open();
//		}
	}

	public WorkShopScheduleQuerySection getParentSection() {
		return parentSection;
	}

	public void setParentSection(WorkShopScheduleQuerySection parentSection) {
		this.parentSection = parentSection;
	}
	
	protected void createMenuItemColor(Menu menu,final String menuName){
		MenuItem redItem = new MenuItem(menu, SWT.PUSH);
		redItem.setText("红色");
		MenuItem blueItem = new MenuItem(menu, SWT.PUSH);
		blueItem.setText("蓝色");
		MenuItem blueLightItem = new MenuItem(menu, SWT.PUSH);
		blueLightItem.setText("浅蓝色");
		MenuItem yellowItem = new MenuItem(menu, SWT.PUSH);
		yellowItem.setText("黄色");
		MenuItem brownItem = new MenuItem(menu, SWT.PUSH);
		brownItem.setText("橘色");
		MenuItem nullItem = new MenuItem(menu, SWT.PUSH);
		nullItem.setText("无色");
		redItem.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				colorAdapter(menuName,"RED");
			}
		});
		
		blueItem.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				colorAdapter(menuName,"BLUE");
			}
		});
		
		blueLightItem.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				colorAdapter(menuName,"BLUELIGHT");
			}
		});
		
		yellowItem.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				colorAdapter(menuName,"YELLOW");
			}
		});
		
		brownItem.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				colorAdapter(menuName,"ORANGE");
			}
		});
		nullItem.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				colorAdapter(menuName,null);
			}
		});
	}
	protected void colorAdapter(String menuName,String color) {
		try {
			if(selectSchedule!=null){
				ADManager adManager = Framework.getService(ADManager.class);
				selectSchedule = (WorkShopSchedule) adManager.getEntity(selectSchedule);
				if("menu1".equals(menuName)){
					selectSchedule.setWcolor1(color);
				}else if("menu2".equals(menuName)){
					selectSchedule.setWcolor2(color);
				}else if("menu3".equals(menuName)){
					selectSchedule.setWcolor3(color);
				}else if("menu4".equals(menuName)){
					selectSchedule.setWcolor4(color);
				}else if("menu5".equals(menuName)){
					selectSchedule.setWcolor5(color);
				}else if("menu6".equals(menuName)){
					selectSchedule.setWcolor6(color);
				}
				adManager.saveEntity(selectSchedule, Env.getOrgRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				refresh();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("MaterialProperties : viewBomAdapter()", e);
		}
	}
	
	public void tableItemColor(TableItem tableItem,String color,int index,WorkShopSchedule workShopSchedule){
		if("ORANGE".equals(color)){
			Color orange = new Color(null,new RGB(255,165,0));
//			Color greenColor = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
			tableItem.setBackground(index,orange);
		}else if("RED".equals(color)){
			Color redColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
			tableItem.setBackground(index,redColor);
		}else if("YELLOW".equals(color)){
			Color yellowColor = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
			tableItem.setBackground(index,yellowColor);
		}else if("BLUE".equals(color)){
			Color blueColor = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
			tableItem.setBackground(index,blueColor);
		}else if("BLUELIGHT".equals(color)){
			Color blueLight = new Color(null,new RGB(0,178,238));
			tableItem.setBackground(index,blueLight);
		}else if(color !=null){
			switch(index){
				case 11:
					String[] rgs = workShopSchedule.getWcolor1().split("\\,");
					Color colorRgb = new Color(null,new RGB(Integer.valueOf(rgs[0]),Integer.valueOf(rgs[1]),Integer.valueOf(rgs[2])));
					tableItem.setBackground(index,colorRgb);
					break;
				case 12:
					rgs = workShopSchedule.getWcolor2().split("\\,");
					colorRgb = new Color(null,new RGB(Integer.valueOf(rgs[0]),Integer.valueOf(rgs[1]),Integer.valueOf(rgs[2])));
					tableItem.setBackground(index,colorRgb);
					break;
				case 13:
					rgs = workShopSchedule.getWcolor5().split("\\,");
					colorRgb = new Color(null,new RGB(Integer.valueOf(rgs[0]),Integer.valueOf(rgs[1]),Integer.valueOf(rgs[2])));
					tableItem.setBackground(index,colorRgb);
					break;
				case 14:
					rgs = workShopSchedule.getWcolor6().split("\\,");
					colorRgb = new Color(null,new RGB(Integer.valueOf(rgs[0]),Integer.valueOf(rgs[1]),Integer.valueOf(rgs[2])));
					tableItem.setBackground(index,colorRgb);
					break;
				case 15:
					rgs = workShopSchedule.getWcolor3().split("\\,");
					colorRgb = new Color(null,new RGB(Integer.valueOf(rgs[0]),Integer.valueOf(rgs[1]),Integer.valueOf(rgs[2])));
					tableItem.setBackground(index,colorRgb);
					break;
				case 16:
					rgs = workShopSchedule.getWcolor4().split("\\,");
					colorRgb = new Color(null,new RGB(Integer.valueOf(rgs[0]),Integer.valueOf(rgs[1]),Integer.valueOf(rgs[2])));
					tableItem.setBackground(index,colorRgb);
					break;
			}
		}
	}
}

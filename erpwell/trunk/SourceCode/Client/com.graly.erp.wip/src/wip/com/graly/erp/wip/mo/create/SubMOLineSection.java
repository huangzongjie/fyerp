package com.graly.erp.wip.mo.create;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.nebula.widgets.ganttchart.GanttControlParent;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.materiallocate.MaterialLocateCompent;
import com.graly.erp.base.materiallocate.MaterialLocateManager;
import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.pur.dialog.PurAssociatedDialog;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.pur.request.RequisitionLineBlockDialog;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;

public class SubMOLineSection implements MaterialLocateManager {
	private static final Logger logger = Logger.getLogger(SubMOLineSection.class);
	
	public static final String TABLE_NAME_PR_LINE = "WorkCenterRequisitionLine";
	public static final String TABLE_NAME_PO_LINE = "WorkCenterPurchaseOrderLine";
	public static Color color = SWTResourceCache.getColor("Function");
	private SubMOLinePage parentPage;
	private MOTreeManager treeManager;
	private TreeViewer viewer;
	private ADTable adTable;
	
	private final int oneRowHeight = 24;
	private final int spacer = 2;
	private GanttChart chart;
	private GanttComposite ganttComposite;
	private Tree tree;
	
	protected Section section;
	protected IManagedForm form;
	protected ToolItem relationItem;
	protected ToolItem moLoadItem;
	protected Menu relationMenu;
	private DocumentationLine selectedLine;
	
	private HashMap<DocumentationLine, GanttEvent> geMap;
	private ManufactureOrder mainMo;
	private List<DocumentationLine> moLines;
	private boolean canEdit;
	
	List<DocumentationLine> queryLines;
	Map<DocumentationLine, TreeItem> tiMap;

	public SubMOLineSection() {}
	
	public SubMOLineSection(ADTable adTable, SubMOLinePage parentPage) {
		this.adTable = adTable;
		this.parentPage = parentPage;
		this.canEdit = ((MOGenerateWizard)parentPage.getWizard()).isCanEdit();
	}
	
	public void createContents(IManagedForm form, Composite parent) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();		
		section = toolkit.createSection(parent, SWT.FLAT | SWT.HORIZONTAL);
		section.setText(Message.getString("pdm.bom_list_detail_info"));
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
	    section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(adTable, "label")));  

	    section.setClient(client);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemRelation(tBar);
		createToolItemMoLoad(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemMoLoad(final ToolBar tBar) {
		moLoadItem = new ToolItem(tBar, SWT.PUSH);
		moLoadItem.setText(Message.getString("wip.moload"));
		moLoadItem.setImage(SWTResourceCache.getImage("search"));
		moLoadItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				moLoadAdapter();
			}
		});
	}
	
	
	protected void moLoadAdapter() {
		Shell shell = UI.getActiveShell();
		StringBuffer whereClause = new StringBuffer();
		if(selectedLine != null && selectedLine instanceof ManufactureOrderLine) {
			ManufactureOrderLine moLine = (ManufactureOrderLine)selectedLine;
			whereClause.append(" workcenterRrn LIKE '");
			whereClause.append(moLine.getWorkCenterRrn());
			whereClause.append("'");
			Date now = Env.getSysDate();
			String dateFormat = "yyyy-MM-dd";
			SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
			whereClause.append(" AND trunc(currDate) >= to_date('");
			whereClause.append(sf.format(now));
			whereClause.append("','yyyy-mm-dd') ");
		}else{
			whereClause.append(" 1<> 1 ");
		}
		ChartDialog cd = new ChartDialog(shell,whereClause.toString());
		cd.open();
	}

	protected void createToolItemRelation(final ToolBar tBar) {
		relationItem = new ToolItem(tBar, SWT.DROP_DOWN);
		relationItem.setText(Message.getString("inv.relationship"));
		relationItem.setImage(SWTResourceCache.getImage("search"));
		relationItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.ARROW) {
					Rectangle bounds = relationItem.getBounds();
					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
					relationMenu.setLocation(point);
					relationMenu.setVisible(true);
				}
			}
		 });
		createRelationMenus(tBar);
	}
	
	protected void createRelationMenus(final ToolBar toolBar) {
		relationMenu = new Menu(UI.getActiveShell(), SWT.POP_UP);
		MenuItem viewWHouse = new MenuItem(relationMenu, SWT.PUSH);
		viewWHouse.setText(Message.getString("inv.view_storage"));
		viewWHouse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				houseViewAdapter();
			}
		});
		
		MenuItem viewPRLine = new MenuItem(relationMenu, SWT.PUSH);
		viewPRLine.setText(Message.getString("inv.relationship_pr"));
		viewPRLine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				prViewAdapter();
			}
		});
		
		MenuItem viewPurLine = new MenuItem(relationMenu, SWT.PUSH);
		viewPurLine.setText(Message.getString("wip.relationship_pur"));
		viewPurLine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				purViewAdapter();
			}
		});
		
		MenuItem viewWorkCenter = new MenuItem(relationMenu, SWT.PUSH);
		viewWorkCenter.setText(Message.getString("wip.work_center"));
		viewWorkCenter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				wcViewAdapter();
			}
		});
	}
	
	protected void houseViewAdapter() {
		if(selectedLine != null) {
			MaterialSumDialog dialog = new MaterialSumDialog(UI.getActiveShell(), selectedLine);
			if(dialog.open() == Dialog.OK) {
			}
		}
	}
	
	protected void prViewAdapter() {
		try {
			if(selectedLine instanceof RequisitionLine) {
				RequisitionLine prLine = (RequisitionLine)selectedLine;
				if(prLine.getObjectRrn() != null || prLine.getRequisitionRrn() != null) {
					ADManager entityManager = Framework.getService(ADManager.class);
					adTable = entityManager.getADTable(0L, "PURRequisitionLine");
					adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
					
					Requisition pr = new Requisition();
					pr.setObjectRrn(prLine.getRequisitionRrn());
					String whereClause = ( " requisitionRrn = " + prLine.getRequisitionRrn()
							+ " AND objectRrn = " + prLine.getObjectRrn() + " ");
					RequisitionLineBlockDialog cd = new RequisitionLineBlockDialog(UI.getActiveShell(),
							adTable, whereClause, pr, true);
					if(cd.open() == Dialog.CANCEL) {
					}					
				} else {
					UI.showError(Message.getString("wip.prLine_is_deleted"));
					return;
				}
			}else{
				UI.showError(Message.getString("inv.relationship_is_null"));
				return;
			}
		} catch(Exception e) {
			logger.error("SubMOLineSection : prViewAdapter()", e);
		}
	}
	
	protected void purViewAdapter() {
		try {
			if(selectedLine != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				adTable = adManager.getADTable(0L, TABLE_NAME_PR_LINE);
				adTable = adManager.getADTableDeep(adTable.getObjectRrn());
				ADTable poTable = adManager.getADTable(0L, TABLE_NAME_PO_LINE);
				poTable = adManager.getADTableDeep(poTable.getObjectRrn());
				
				Long materialRrn = selectedLine.getMaterialRrn();

				PurAssociatedDialog cd = new PurAssociatedDialog(UI.getActiveShell(),
						adTable, poTable, materialRrn);
				if(cd.open() == Dialog.CANCEL) {
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("SubMOLineSection : purViewAdapter()", e);
		}
	}
	
	protected void wcViewAdapter() {
		try {
			if(selectedLine instanceof ManufactureOrderLine) {
				ManufactureOrderLine moLine = (ManufactureOrderLine)selectedLine;
				if(moLine.getObjectRrn() != null || moLine.getWorkCenterRrn() != null) {
					ADManager entityManager = Framework.getService(ADManager.class);
					WorkCenter wc = new WorkCenter();
					wc.setObjectRrn(moLine.getWorkCenterRrn());
					wc = (WorkCenter)entityManager.getEntity(wc);
					
					ViewWorkCenterDialog vwcd = new ViewWorkCenterDialog(UI.getActiveShell(), wc);
					vwcd.open();
				}
			} else if(selectedLine != null) {
				UI.showError(Message.getString("wip.select_is_not_moLine"));
				return;
			}
		} catch(Exception e) {
			logger.error("SubMOLineSection : prViewAdapter()", e);
		}
	}
	
	protected void createSectionContents(IManagedForm form, Composite parent){
		new MaterialLocateCompent(this).createMaterialLocateComposite(parent, form.getToolkit());
		
		SashForm sf = new SashForm(parent, SWT.HORIZONTAL);
		sf.setLayoutData(new GridData(GridData.FILL_BOTH));
		// 在左部创建MO树
		GanttControlParent leftTree = new GanttControlParent(sf, SWT.BORDER);
		// 在右部创建Gantt Chart
		chart = new GanttChart(sf, SWT.BORDER);
		ganttComposite = chart.getGanttComposite();
		ganttComposite.setDrawHorizontalLinesOverride(true);
		ganttComposite.setDrawVerticalLinesOverride(true);
		ganttComposite.setFixedRowHeightOverride(oneRowHeight-spacer);
		ganttComposite.setEventSpacerOverride(spacer);
		
		createMOTreeContent(leftTree, form.getToolkit());
		createGanttContent(chart);
		leftTree.setGanttChart(chart);
		sf.setWeights(new int[] {65, 35});
	}
	
	protected void createMOTreeContent(Composite left, FormToolkit toolkit) {
		treeManager = new MOTreeManager(adTable, this);
		treeManager.setParentSection(this);
		//SWT.LINE_DASHDOTDOT | 
		tree = new Tree(left,
        		SWT.FULL_SELECTION | SWT.BORDER |SWT.H_SCROLL | SWT.V_SCROLL);
		viewer = (TreeViewer)treeManager.createViewer(tree,	toolkit);
		tree.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = oneRowHeight;
			}
		});
		tree.addSelectionListener(getTreeSelectionListener());
	}
	
	protected void createGanttContent(GanttChart chart) {
		if(mainMo != null && moLines != null) {
			if(geMap != null) {
				// 更新存储GanttEvent列表,并销毁上次生成的GanttEvents
				chart.getGanttComposite().setEvents(new ArrayList<Object>());
				disposeMapValues();
			}
			geMap = new LinkedHashMap<DocumentationLine, GanttEvent>();

			GanttEvent ge = null;
			for(DocumentationLine line : moLines) {
				if(geMap.get(line) != null) continue;
				GregorianCalendar sc, ec;
				sc = new GregorianCalendar();
				ec = new GregorianCalendar();
				if (line.getDateStart() != null && line.getDateEnd() != null) {
					sc.setTime(line.getDateStart());
					ec.setTime(line.getDateEnd());
					ge = new GanttEvent(chart, line, null, sc, ec, 0);
				}
				else {
					ge = new GanttEvent(chart, line, null, sc, ec, 0);
					ge.setHidden(true);
				}
				ge.setVerticalEventAlignment(SWT.CENTER);
				ge.setMoveable(false);
				ge.setResizable(false);
				// 设置GanttEvent名称
				if(line.getMaterial() != null) {
					ge.setName(line.getMaterial().getName());
				}
				// 设置GanttEvent完成任务的百分比
				ge.setPercentComplete(getGEPercentComplete(line));
				// 为PR时，蓝色显示
				if(line instanceof RequisitionLine) {
					ge.setGradientStatusColor(color);
				}
				geMap.put(line, ge);
			}
			chart.update();
		}
	}
	
	private void disposeMapValues() {
		for(GanttEvent ge : geMap.values()) {
			if(ge != null) {
				ge.dispose();
				ge = null;
			}
		}
	}
	
	public void refreshTreeContent(List<DocumentationLine> masterLines) {
		if(masterLines != null) {
			viewer.setInput(masterLines);
			viewer.expandAll();
		}
	}
	
	public void initGanttChartContent(ManufactureOrder mainMo, List<DocumentationLine> moLines) {
		this.mainMo = mainMo;
		this.moLines = moLines;
		createGanttContent(chart);
	}
	
	public void updateGanttEventBy(DocumentationLine doLine) {
		if(doLine.getDateStart() == null || doLine.getDateEnd() == null) {
//			if(geMap != null && geMap.get(doLine) != null) {
//				GanttEvent ge = geMap.get(doLine);
//				GregorianCalendar sc, ec;
//				sc = ec = new GregorianCalendar();
//				ge.setStartDate(sc);
//				ge.setEndDate(ec);
//				ge.update(true);
//				ge.setHidden(true);
//			}
		} else {
			if(geMap != null) {
				GanttEvent ge = geMap.get(doLine);
				Calendar start = Calendar.getInstance();
				start.setTime(doLine.getDateStart());
				ge.setStartDate(start);
				
				Calendar end = Calendar.getInstance();
				end.setTime(doLine.getDateEnd());
				ge.setEndDate(end);
				if(ge.isHidden()) ge.setHidden(false);
				ge.update(true);
			}
		}
	}
	
	protected SelectionListener getTreeSelectionListener() {
		return new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			
			public void widgetSelected(SelectionEvent e) {
				if (tree.getSelectionCount() == 0) return;
				// set the selection
				Object obj = tree.getSelection()[0].getData();
				if(obj instanceof DocumentationLine) {
					selectedLine = (DocumentationLine)obj;
					if(geMap != null) {
						GanttEvent ge = geMap.get((DocumentationLine)obj);
						ganttComposite.setSelection(ge);
					}
				}
			}
		};
	}
	
	private int getGEPercentComplete(DocumentationLine line) {
		BigDecimal manufactureQty = BigDecimal.ONE;
		BigDecimal completedQty = BigDecimal.ZERO;
		if(line instanceof ManufactureOrderLine) {
			ManufactureOrderLine ml = (ManufactureOrderLine)line;
			if(ml.getQty() != null && ml.getQty().compareTo(BigDecimal.ZERO) != 0) {
				manufactureQty = ml.getQty();
			}
			if(ml.getQtyReceive() != null) {
				completedQty = ml.getQtyReceive();
			}
		} else if(line instanceof RequisitionLine) {
			RequisitionLine rl = (RequisitionLine)line;
			if(rl.getQtyOrdered() != null && rl.getQtyOrdered().compareTo(BigDecimal.ZERO) != 0) {
				manufactureQty = rl.getQty();
			}
			if(rl.getQtyInventoty() != null) {
				completedQty = rl.getQtyInventoty();
			}
		}
		return completedQty.divide(manufactureQty, 2, RoundingMode.UP).multiply(new BigDecimal("100")).intValue();
	}
	
	protected List<DocumentationLine> getMOLines() {
		return (List<DocumentationLine>)viewer.getInput();
	}

	public SubMOLinePage getParentPage() {
		return parentPage;
	}
	
	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	
	protected void getChildrenData(TreeItem parent, String materialId) {
		TreeItem[] its = null;
		if(parent == null) its = ((TreeViewer)viewer).getTree().getItems();
		else its = parent.getItems();
		if(its == null || its.length == 0)
			return;
		
		for(TreeItem ti : its) {
			if(ti.getData() instanceof DocumentationLine) {
				DocumentationLine moLine = (DocumentationLine)ti.getData();
				if(moLine.getMaterialId() != null && moLine.getMaterialId().indexOf(materialId) != -1) {
					queryLines.add(moLine);
					tiMap.put(moLine, ti);
					ti.setBackground(SWTResourceCache.getColor("Folder"));
				}
			}
			getChildrenData(ti, materialId);
		}
	}

	@Override
	public void locateLast(String materialId, int index) {
		if(materialId == null || "".equals(materialId.trim()))
			return;
		if(queryLines == null || queryLines.size() == 0 || index < 0 || index > queryLines.size() - 1)
			return;
		TreeItem ti = tiMap.get(queryLines.get(index));
		((TreeViewer)viewer).getTree().setSelection(ti);
		ti.setBackground(SWTResourceCache.getColor("Folder"));
	}

	@Override
	public boolean locateMaterial(String materialId) {
		if(queryLines != null && queryLines.size() > 0) {
			clearPreLocateMaterials();
		}
		if(materialId == null || "".equals(materialId.trim()))
			return false;
		queryLines = new ArrayList<DocumentationLine>();
		tiMap = new HashMap<DocumentationLine, TreeItem>();
		getChildrenData(null, materialId);
		if(queryLines.size() > 0)
			return true;
		return false;
	}
	
	protected void clearPreLocateMaterials() {
		for(DocumentationLine moBom : queryLines) {
			if(tiMap.get(moBom) != null) {
				tiMap.get(moBom).setBackground(null);
			}
		}
		queryLines.clear();
	}

	@Override
	public void locateNext(String materialId, int index) {
		if(materialId == null || "".equals(materialId.trim()))
			return;
		if(queryLines == null || queryLines.size() == 0 || index < 0 || index > queryLines.size() - 1)
			return;
		TreeItem ti = tiMap.get(queryLines.get(index));
		((TreeViewer)viewer).getTree().setSelection(ti);
		ti.setBackground(SWTResourceCache.getColor("Folder"));
	}

}

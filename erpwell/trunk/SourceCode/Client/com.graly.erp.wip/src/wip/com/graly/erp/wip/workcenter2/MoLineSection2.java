package com.graly.erp.wip.workcenter2;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.crypto.Data;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.base.QueryTimeDialog;
import com.graly.erp.base.BrowserDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.wip.mo.molinecreate.NewMOLineDialog;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.erp.wip.workcenter.GanttChartDialog;
import com.graly.erp.wip.workcenter.ManpowerDialog;
import com.graly.erp.wip.workcenter.MoLineCombineDialog;
import com.graly.erp.wip.workcenter.MoLineDissolveDialog;
import com.graly.erp.wip.workcenter.MpsLineDeliveryDialog;
import com.graly.erp.wip.workcenter.ReceiveDetailDialog;
import com.graly.erp.wip.workcenter.SeeLotDialog;
import com.graly.erp.wip.workcenter.WorkCenterQueryDialog;
import com.graly.erp.wip.workcenter.WorkShopSchedduleDialog;
import com.graly.erp.wip.workcenter.bom.WorkOrderBomDialog;
import com.graly.erp.wip.workcenter.reassign.ReAssignMOLineDialog;
import com.graly.erp.wip.workcenter.receive.BatchSetupDialog;
import com.graly.erp.wip.workcenter.receive.InsufficientBomsDialog;
import com.graly.erp.wip.workcenter.receive.MoLineReceiveDialog;
import com.graly.erp.wip.workcenter.referencedoc.ReferceDocDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotComponent;
public class MoLineSection2 implements IRefresh {
	private static final Logger logger = Logger.getLogger(MoLineSection2.class);
	public static final String WORK_CENTER = "工作中心：";
	public static final String KEY_WORKCENTER_COMBINE="WIP.WorkCenter.MoCombine";
	public static final String KEY_WORKCENTER_DISSOLVE="WIP.WorkCenter.MoDissolve";	
	public static final String KEY_WORKCENTER_TOTAL = "WIP.WorkCenter.Total";
	public static final String KEY_WORKCENTER_CONSUME = "WIP.WorkCenter.Consume";
	public static final String KEY_WORKCENTER_SEEPRODUCT = "WIP.WorkCenter.Seeproduct";
	public static final String KEY_WORKCENTER_WORKNEED = "WIP.WorkCenter.WorkNeed";

	private static String PREFIX = "workCenterRrn = ";
	private static String SUFFIX = " AND lineStatus <> '" + ManufactureOrder.STATUS_DRAFTED + "' ";

	protected int displayCount = 0;
	protected WorkCenter workCenter;
	protected TableListManager tableManager;
	protected EntityTableManager entityTableManager;
	protected StructuredViewer viewer;
	private ManufactureOrderLine selectedMoLine;
	protected Section section;
	protected IManagedForm form;
	protected EntityQueryDialog queryDialog;

	protected ToolItem itemWorkShopSchedule;
	protected ToolItem itemMpsLineNotice;
	protected ToolItem itemExport;
	protected ToolItem itemRun;
	protected ToolItem itemSuspend;
	protected ToolItem itemNew;
	protected ToolItem itemReAssign;
	protected ToolItem itemReceiveDetail;
	protected ToolItem itemReceive;
	protected ToolItem itemDisAssemble;
	protected ToolItem itemChart;
	protected ToolItem itemQuery;
	protected ToolItem itemBomTree;
	protected ToolItem itemRefresh;
	private ToolItem itemClose;
	private ToolItem toolItemSeeLot;
	private ToolItem toolItemConsign;
	private ToolItem toolItemConfirm;
	private ToolItem toolItemTotal;
	private ToolItem toolItemConsume;
	private ToolItem toolItemSeeproduct;
	private ToolItem toolItemWorkNeed;
	private ToolItem toolItemFaWCConfirm;//控制阀车间
	private ToolItem toolItemWCConfirm;
	private ToolItem itemReferenceDoc;
	private ToolItem itemCombine;
	private ToolItem itemDissolve;
	protected ADTable adTable;
	
	protected boolean hasWCAuthority = true;
	protected String whereClause;

	private String TABLE_NAME_INVLOT = "INVLot";
	private String TABLE_NAME_MOLINE = "WIPMoLine";
	private String TABLE_NAME_MANUFACTUREORDERLINE = "WIPManufactureOrderLine";
	private String TABLE_NAME_MANUFACTUREBOM = "WIPManufactureOrderBom";
	private String TABLE_NAME_MATERIAL_DOC = "BASMaterialDoc";
	private ADTable adTableDoc;
	private List<ManufactureOrderLine> moLineList;
	private final String WORKSTATUS="RUNNING";
	
	private WorkCenterSection2 parentSection;
	
	
	ADManager adManager;
	
	BigDecimal totalQty = BigDecimal.ZERO;
	BigDecimal totalQtyReceive = BigDecimal.ZERO;
	private ToolBar toolBar;

	public MoLineSection2(TableListManager tableManager) {
		this.tableManager = tableManager;
	}
	
	public MoLineSection2(TableListManager tableManager, EntityTableManager entityTableManager) {
		this.tableManager = tableManager;
		this.entityTableManager = entityTableManager;
	}

	public void createContent(final IManagedForm managedForm, Composite parent) {
		this.form = managedForm;
		final ADTable table = tableManager.getADTable();

		FormToolkit toolkit = managedForm.getToolkit();
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setForeground(SWTResourceCache.getColor("Function"));
		section.marginWidth = 3;
		section.marginHeight = 4;
		toolkit.createCompositeSeparator(section);

		Composite client = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		client.setLayout(layout);

		section.setData("entityBlock", this);
		section.setText(String.format(Message.getString("common.list"), I18nUtil.getI18nMessage(table, "label")));

		createToolBar(section);
		createSectionDesc(section);
		viewer = tableManager.createViewer(client, toolkit);
		
		//tablec.geti
	//	tablec.getItems()[tablec.getItemCount()].setBackground(SWTResourceCache.getColor("Folder"));
		//tablec.redraw();
//		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
//		tablec.getItems()[tablec.getItemCount()-1].setBackground(color);
//		Font font = new Font(Display.getDefault(),"宋体",10,SWT.BOLD); 
//		tablec.getItems()[tablec.getItemCount()-1].setFont(font);
//		tablec.redraw();
		createViewAction(viewer);

		section.setClient(client);
	}

	protected void createViewAction(StructuredViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				if(hasWCAuthority){
					setSelectedMoLine(ss.getFirstElement());
				}
				getParentSection().getInvSection().setMoLineOnHand((ManufactureOrderLine) ss.getFirstElement());
			}
		});
	}

	private void setSelectedMoLine(Object obj) {
		if (obj instanceof ManufactureOrderLine) {
			selectedMoLine = (ManufactureOrderLine) obj;
			setWorkStatusChanged(selectedMoLine.getWorkStatus());
			setLineStatusChanged(selectedMoLine.getLineStatus());
			boolean flag = selectedMoLine.getQtyReceive().compareTo(BigDecimal.ZERO) > 0;
			setDisAssembleStatusChanged(flag);
		} else {
			selectedMoLine = null;
			setWorkStatusChanged("");
			setLineStatusChanged("");
			setDisAssembleStatusChanged(false);
		}
	}

	private void setDisAssembleStatusChanged(boolean flag) {
		itemDisAssemble.setEnabled(flag);
	}

	protected void createSectionDesc(Section section) {
		try {
			StringBuffer msg = new StringBuffer("  ");
			if(this.workCenter != null) {
//				msg.append(WORK_CENTER);
				msg.append(workCenter.getName());
				msg.append("： ");
			}
			String text = Message.getString("common.totalshow");
			if (displayCount > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(displayCount), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(displayCount), String.valueOf(displayCount));
			}
			msg.append(text);
			msg.append(" ; 生产总数:  ");
			msg.append(totalQty.toString());
			msg.append(" ; 完成总数:  ");
			msg.append(totalQtyReceive.toString());
			section.setDescription(msg.toString());
		} catch (Exception e) {
			logger.error("EntityBlock : createSectionDesc ", e);
		}
	}

	public void createToolBar(Section section) {
		toolBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemmMpsLineDeliveryDialog(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemExport(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemRunning(toolBar);
		createToolItemSuspend(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemNew(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemReAssign(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemReceive(toolBar);
		createToolItemReceiveDetail(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemDisAssemble(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemSeeLot(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemCloseMoLine(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemMoCombine(toolBar);//添加合并工作令按钮
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemMoDissolve(toolBar);//添加撤销合并按钮
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemChart(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemBomTree(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemQuery(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemConsign(toolBar);
		createToolItemFaWCConfirm(toolBar);
		createToolItemWCConfirm(toolBar);
		createToolItemConfirm(toolBar);
		createToolItemReferenceDoc(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemRefresh(toolBar);
		createToolItemTotal(toolBar);
		createToolItemConsume(toolBar);
		createToolItemSeeproduct(toolBar);
		createToolItemWorkNeed(toolBar);
		setInitAuthority(hasWCAuthority && true);
		section.setTextClient(toolBar);
	}
	
	//按钮实现
	protected void createToolItemmMpsLineDeliveryDialog(ToolBar tBar) {
		itemMpsLineNotice = new ToolItem(tBar, SWT.PUSH);
		itemMpsLineNotice.setText("计划通知");
		itemMpsLineNotice.setImage(SWTResourceCache.getImage("split"));
		itemMpsLineNotice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				mpsLineDeliveryAdapter();
			}
		});
	}
	
	//按钮实现
	protected void createToolItemMoCombine(ToolBar tBar) {
		itemCombine = new AuthorityToolItem(tBar, SWT.PUSH, KEY_WORKCENTER_COMBINE);
		itemCombine.setText(Message.getString("wip.mocombine"));
		itemCombine.setImage(SWTResourceCache.getImage("split"));
		itemCombine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				moCombineAdapter();
			}
		});
	}
	
	//按钮事件
	protected void moCombineAdapter() {
		try {
			if(selectedMoLine!=null){
				if(selectedMoLine.getMasterMoRrn() != null && !selectedMoLine.getMasterMoRrn().equals(selectedMoLine.getObjectRrn())){
					Long MoLineMaterialRrn=selectedMoLine.getMaterialRrn();
					ManufactureOrder mo=new ManufactureOrder();
					mo.setObjectRrn(selectedMoLine.getMasterMoRrn());
					adManager=Framework.getService(ADManager.class);
					mo=(ManufactureOrder)adManager.getEntity(mo);
					Long  MoMaterialRrn=mo.getMaterialRrn();
					if(MoLineMaterialRrn.equals(MoMaterialRrn)){
						UI.showError(Message.getString("wip.not_merge"));
						return;
					}

				}
				if(selectedMoLine.getWorkStatus()==null ){
					WipManager wipManager=Framework.getService(WipManager.class);
					moLineList=wipManager.getCanMergeMoLines(selectedMoLine);
					if(moLineList.size()!=0){
						List<ManufactureOrderLine> list=new ArrayList<ManufactureOrderLine>();
						for(ManufactureOrderLine moLine:moLineList){
							if(moLine.getWorkStatus()==null||!moLine.getWorkStatus().equals(WORKSTATUS)){
								list.add(moLine);
							}else{
								continue;
							}
							
						}
						if(list.size()==0){
							UI.showInfo(Message.getString("wip_merge_not_have"));
							return;
						}
						MoLineCombineDialog moLineCombineDialog=new MoLineCombineDialog(UI.getActiveShell(),selectedMoLine,list);
						moLineCombineDialog.open();
						refreshAll();
					}else{
						UI.showInfo(Message.getString("wip_merge_not_have"));
					}
						
					
				}else if(!selectedMoLine.getWorkStatus().equals(WORKSTATUS)){
					WipManager wipManager=Framework.getService(WipManager.class);
					moLineList=wipManager.getCanMergeMoLines(selectedMoLine);
					if(moLineList.size()!=0){
						List<ManufactureOrderLine> list=new ArrayList<ManufactureOrderLine>();
						for(ManufactureOrderLine moLine:moLineList){
							if(moLine.getWorkStatus()==null||!moLine.getWorkStatus().equals(WORKSTATUS)){
								list.add(moLine);
							}else{
								continue;
							}
							
						}
						if(list.size()==0){
							UI.showInfo(Message.getString("wip_merge_not_have"));
							return;
						}
						MoLineCombineDialog moLineCombineDialog=new MoLineCombineDialog(UI.getActiveShell(),selectedMoLine,list);
						moLineCombineDialog.open();
						refreshAll();
					}else{
						UI.showInfo(Message.getString("wip_merge_not_have"));
					}
				}else{
					UI.showError(Message.getString("wip.runing_cannot_merge"));
				}
			}
		} catch (ClientException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createToolItemFaWCConfirm(ToolBar tBar) {
		toolItemFaWCConfirm = new ToolItem(tBar, SWT.PUSH);
		toolItemFaWCConfirm.setText("控制阀车间确认单");
		toolItemFaWCConfirm.setImage(SWTResourceCache.getImage("preview"));
		toolItemFaWCConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				workCenterFaConfirmAdapter();
			}
		});
	}
	
	private void createToolItemWCConfirm(ToolBar tBar) {
		toolItemWCConfirm = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_REFERENCEDOC);
		toolItemWCConfirm.setText("车间确认单");
		toolItemWCConfirm.setImage(SWTResourceCache.getImage("preview"));
		toolItemWCConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				workCenterConfirmAdapter();
			}
		});
	}

	private void createToolItemReceiveDetail(ToolBar tBar) {
		itemReceiveDetail = new ToolItem(tBar, SWT.PUSH);
		itemReceiveDetail.setText(Message.getString("wip.receive_detail"));
		itemReceiveDetail.setImage(SWTResourceCache.getImage("component"));
		itemReceiveDetail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				receiveDetailAdapter();
			}
		});
	}

	protected void receiveDetailAdapter() {
		try{
			if(selectedMoLine == null || selectedMoLine.getObjectRrn() == null){
				UI.showError(Message.getString("wip.select_mo_first"));
				return;
			}
			WipManager wipManager = Framework.getService(WipManager.class);
			List<LotComponent> input = wipManager.receiveDetail(Env.getOrgRrn(), selectedMoLine.getObjectRrn());
			ReceiveDetailDialog rdd = new ReceiveDetailDialog(UI.getActiveShell(), input);
			rdd.open();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected void createToolItemExport(ToolBar tBar) {
//		itemExport = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_START);
		itemExport = new ToolItem(tBar, SWT.PUSH);
		itemExport.setText(Message.getString("common.export"));
		itemExport.setImage(SWTResourceCache.getImage("export"));
		itemExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				exportAdapter();
			}
		});
	}
	
	protected void exportAdapter() {
		try {
			FileDialog dialog = new FileDialog(UI.getActiveShell(), SWT.SAVE);
			dialog.setFilterNames(new String[] { "CSV (*.csv)" });
			dialog.setFilterExtensions(new String[] { "*.csv" }); 
			String fn = dialog.open();
			if (fn != null) {
				Table table = ((TableViewer)viewer).getTable();
				String[][] datas = new String[table.getItemCount() + 1][table.getColumnCount()];
				for (int i = 0; i < table.getColumnCount(); i++) {
					TableColumn column = table.getColumn(i);
					datas[0][i] = column.getText();
				}
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem item = table.getItem(i);
					for (int j = 0; j < table.getColumnCount(); j++) {
						datas[i + 1][j] = item.getText(j);
					}
				}
				
				File file = new File(fn);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				CSVWriter writer = new CSVWriter(new FileWriter(file));
		        for (int i = 0; i < datas.length; i++) {
		            writer.writeNext(datas[i]);
		        }
		        writer.close();

			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void createToolItemSeeLot(ToolBar tBar) {
		toolItemSeeLot = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_LOT);
		toolItemSeeLot.setText(Message.getString("inv.barcode"));
		toolItemSeeLot.setImage(SWTResourceCache.getImage("barcode"));
		toolItemSeeLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				seeLotAdapter();
			}
		});
	}
	
	protected void createToolItemRunning(ToolBar tBar) {
		itemRun = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_START);
		itemRun.setText(Message.getString("wip.running"));
		itemRun.setImage(SWTResourceCache.getImage("run"));
		itemRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				runAdapter();
			}
		});
	}

	protected void createToolItemSuspend(ToolBar tBar) {
		itemSuspend = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_PAUSE);
		itemSuspend.setText(Message.getString("wip.suspend"));
		itemSuspend.setImage(SWTResourceCache.getImage("suspend"));
		itemSuspend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				suspendAdapter();
			}
		});
	}

	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}

	protected void createToolItemReAssign(ToolBar tBar) {
		itemReAssign = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_REDIST);
		itemReAssign.setText(Message.getString("wip.re_assign"));
		itemReAssign.setImage(SWTResourceCache.getImage("reassign"));
		itemReAssign.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				reAssignAdapter();
			}
		});
	}

	protected void createToolItemReceive(ToolBar tBar) {
		itemReceive = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_ERCEIPT);
		itemReceive.setText(Message.getString("wip.receive"));
		itemReceive.setImage(SWTResourceCache.getImage("receive"));
		itemReceive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				receiveAdapter();
			}
		});
	}
	
	protected void createToolItemDisAssemble(ToolBar tBar) {
		itemDisAssemble = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_DISASSEMBLE);
		itemDisAssemble.setText(Message.getString("wip.disassemblelot"));
		itemDisAssemble.setImage(SWTResourceCache.getImage("split"));
		itemDisAssemble.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				disAssembleAdapter();
			}
		});
	}

	protected void disAssembleAdapter() {
		try {
			if(selectedMoLine != null && selectedMoLine.getObjectRrn() != null){
				ADManager manager = Framework.getService(ADManager.class);
				Material material = new Material();
				material.setObjectRrn(selectedMoLine.getMaterialRrn());
				material = (Material) manager.getEntity(material);
				if(Lot.LOTTYPE_MATERIAL.equals(material.getLotType())){
					boolean confirm = UI.showConfirm(String.format(Message.getString("common.confirm_todo"),Message.getString("wip.disassemblelot")));
					if(confirm){
						WipManager wipManager = Framework.getService(WipManager.class);
						wipManager.disassembleMoLine(selectedMoLine.getObjectRrn(),Env.getUserRrn());
						UI.showInfo(Message.getString("wip.disassemblelot_successful"));
						selectedMoLine = (ManufactureOrderLine) manager.getEntity(selectedMoLine);
//						List<ManufactureOrderLine> l = new ArrayList<ManufactureOrderLine>();
//						l.add(selectedMoLine);
						viewer.refresh(selectedMoLine);//只刷新这一个MoLine
					}
				}else{
					UI.showInfo(material.getLotType()+ "类型 请使用拆分功能");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected void createToolItemCloseMoLine(ToolBar tBar) {
		itemClose = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_REVOKE);
		itemClose.setText(Message.getString("common.close"));
		itemClose.setImage(SWTResourceCache.getImage("close"));
		itemClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				closeMoLineAdapter();
			}
		});
	}

	protected void createToolItemChart(ToolBar tBar) {
		itemChart = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_SDCHART);
		itemChart.setText(Message.getString("wip.chart"));
		itemChart.setToolTipText("Mo Line Schedule Chart");
		itemChart.setImage(SWTResourceCache.getImage("ganttchart"));
		itemChart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				chartAdapter();
			}
		});
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
	
	protected void createToolItemQuery(ToolBar tBar) {
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
	
	protected void createToolItemConsign(ToolBar tBar) {
		toolItemConsign = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_REFERENCEDOC);
		toolItemConsign.setText(Message.getString("wip.consign"));
		toolItemConsign.setImage(SWTResourceCache.getImage("preview"));
		toolItemConsign.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				consignAdapter();
			}
		});
	}
	
	protected void consignAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			String report = "consign_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			
			if(selectedMoLine != null){//如果选择不为空就打印
				Long objectRrn = selectedMoLine.getObjectRrn();
				userParams.put("MO_LINE_RRN", String.valueOf(objectRrn));
	
				PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
				dialog.open();
			}else{//否则就打印空的
//				UI.showWarning(Message.getString("common.choose_one_record"));
//				return;
				userParams.put("MO_LINE_RRN", null);
				
				PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
				dialog.open();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void createToolItemConfirm(ToolBar tBar){
		toolItemConfirm = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_REFERENCEDOC);
		toolItemConfirm.setText(Message.getString("wip.confirm"));
		toolItemConfirm.setImage(SWTResourceCache.getImage("preview"));
		toolItemConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				confirmAdapter();
			}
		});
	}
	
	protected void workCenterFaConfirmAdapter() {
		try {
			String report = "fa_workcenter_confirm.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			Long moLineRrn = 0L;
			if(selectedMoLine != null){
				moLineRrn = selectedMoLine.getObjectRrn();
			}else{
				UI.showError(Message.getString("mo.no_mo_selected"));
				return;
			}
			userParams.put("MO_LINE_RRN", String.valueOf(moLineRrn));
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}	
	}
	
	protected void workCenterConfirmAdapter() {
		try {
			String report = "workcenter_confirm.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			Long moLineRrn = 0L;
			if(selectedMoLine != null){
				moLineRrn = selectedMoLine.getObjectRrn();
			}else{
				UI.showError(Message.getString("mo.no_mo_selected"));
				return;
			}
			userParams.put("MO_LINE_RRN", String.valueOf(moLineRrn));
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}	
	}

	protected void confirmAdapter() {
		try {
			String report = "produce_confirm.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			Long moLineRrn = 0L;
			if(selectedMoLine != null){
				moLineRrn = selectedMoLine.getObjectRrn();
			}else{
				UI.showError(Message.getString("mo.no_mo_selected"));
				return;
			}
			userParams.put("MO_LINE_RRN", String.valueOf(moLineRrn));
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}	
	}

	protected void createToolItemReferenceDoc(ToolBar tBar) {
		itemReferenceDoc = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_REFERENCEDOC);
		itemReferenceDoc.setText(Message.getString("bas.refence_doc"));
		itemReferenceDoc.setImage(SWTResourceCache.getImage("search"));
		itemReferenceDoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refreenceDocAdapter();
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

	protected void runAdapter() {
		if (selectedMoLine != null) {
			
			ManpowerDialog manpowerDialog = new ManpowerDialog(UI.getActiveShell(), form, workCenter, selectedMoLine);
			if (manpowerDialog.open() == Dialog.OK) {
//				List l = getInsufficientBomList();//取消该业务--原因控制阀车间胡继东反应开始按钮太慢
//				if(l != null && l.size() > 0 ){
//					//生产物料清单中有库存数小于生产数的 提示库存不足 仍继续开始
//					ADTable adT = getAdTableOfMOLine(TABLE_NAME_MANUFACTUREBOM);
//					BomInfoDialog bid = new BomInfoDialog(UI.getActiveShell(), adT, l);
//					bid.open();
//
//				}
				if (manpowerDialog.getTextValue() != null) {
					try {
						BigDecimal manpower = new BigDecimal(manpowerDialog.getTextValue());
						WipManager wipManager = Framework.getService(WipManager.class);
						selectedMoLine = wipManager.runMoLine(selectedMoLine, manpower);
						refreshAll();
						setSelectedMoLine(selectedMoLine);
					} catch (Exception e) {
						logger.error("MOLineSection : runAdapter()", e);
						ExceptionHandlerManager.asyncHandleException(e);
						return;
					}
				}
			}
		}
	}

	protected void suspendAdapter() {
		try {
			if (selectedMoLine != null) {
				WipManager wipManager = Framework.getService(WipManager.class);
				selectedMoLine = wipManager.suspendMoLine(selectedMoLine);
				refreshAll();
				setSelectedMoLine(selectedMoLine);
			}
		} catch (Exception e) {
			logger.error("MOLineSection : runAdapter()", e);
		}
	}

	protected void newAdapter() {
		try {
			if (workCenter != null && workCenter.getObjectRrn() != 0) {
				adTable = getAdTableOfMOLine(TABLE_NAME_MANUFACTUREORDERLINE);
				NewMOLineDialog newMOLineDialog = new NewMOLineDialog(UI.getActiveShell(), adTable, workCenter);
				if (newMOLineDialog.open() == Dialog.OK) {
					refreshAll();
				}
			}			
		} catch(Exception e) {
			logger.error("MOLineSection : newAdapter()", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected void reAssignAdapter() {
		try {			
			if (selectedMoLine != null) {
				String workStatus = selectedMoLine.getWorkStatus() == null ? "" : selectedMoLine.getWorkStatus();
				if (workStatus.equals(ManufactureOrderLine.WORK_STATUS_RUNNING)) {
					UI.showError(String.format(Message.getString("wip.can_not_reassign_at_running"), selectedMoLine.getMaterialId()));
					return;
				}
				adTable = getAdTableOfMOLine(TABLE_NAME_MOLINE);
				ReAssignMOLineDialog reAssignDialog = new ReAssignMOLineDialog(UI.getActiveShell(), adTable, selectedMoLine);
				if (reAssignDialog.open() == Dialog.OK) {
					refreshAll();
				}
			}
		} catch (Exception e) {
			logger.error("MOLineSection : reAssignAdapter()", e);
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void seeLotAdapter() {
		if(selectedMoLine != null){
			adTable = getAdTableOfMOLine(TABLE_NAME_INVLOT);
			SeeLotDialog seeLotDialog = new SeeLotDialog(UI.getActiveShell(), adTable, selectedMoLine);
			if(seeLotDialog.open() == Dialog.CANCEL){
			}
		}
	}

	private ADTable getAdTableOfMOLine(String tableName) {
		try {
			if(adManager == null)
				adManager = Framework.getService(ADManager.class);
			adTable = adManager.getADTable(0L, tableName);
			adTable = adManager.getADTableDeep(adTable.getObjectRrn());
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return adTable;
	}
	

	protected void receiveAdapter() {
		try {
			if (selectedMoLine != null) {
				Material material = selectedMoLine.getMaterial();
				if (!material.getIsLotControl()) {
					UI.showError(String.format(Message.getString("wip.receive_must_lotcontrol"), material.getMaterialId() + "("
							+ material.getName() + ")"));
					return;
				}

				//得到MO的备注
				ManufactureOrder mo = new ManufactureOrder();
				mo.setComments("");
				if(selectedMoLine.getMasterMoRrn() != null) {
					mo.setObjectRrn(selectedMoLine.getMasterMoRrn());
					if(adManager == null)
						adManager = Framework.getService(ADManager.class);
					try {
						mo = (ManufactureOrder)adManager.getEntity(mo);						
					} catch(Exception e) {}
				}
				BatchSetupDialog bsd = new BatchSetupDialog(UI.getActiveShell(), selectedMoLine);
				if (bsd.open() == Dialog.OK) {
					Lot parentLot = bsd.getParentLot();
					BigDecimal parentLotQty = parentLot.getQtyTransaction();//要接受的父LOT的数量，根据这个数量计算需要消耗的子物料的数量，以此判断子物料批的数量是否够
					
					//get sub BOMs by parentLot
					
					parentLot.setMaterialId(material.getMaterialId());
					parentLot.setMaterialName(material.getName());
					MoLineReceiveDialog mrd = new MoLineReceiveDialog(UI.getActiveShell(),
							selectedMoLine, parentLot, workCenter, mo.getComments());
					if(mrd.open() == Dialog.CANCEL) {
						selectedMoLine = null;
					}
					refreshAll();
				}
//				if (Lot.LOTTYPE_BATCH.equals(material.getLotType()) || Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
//				} else if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
//					Lot parentLot = new Lot();
//					parentLot.setOrgRrn(Env.getOrgRrn());
//					parentLot.setLotType(selectedMoLine.getMaterial().getLotType());
//					parentLot.setMoRrn(selectedMoLine.getMasterMoRrn());
//					parentLot.setMoLineRrn(selectedMoLine.getObjectRrn());
//					parentLot.setMaterialRrn(material.getObjectRrn());
//					parentLot.setMaterialId(material.getMaterialId());
//					parentLot.setMaterialName(material.getName());
//					parentLot.setQtyCurrent(BigDecimal.ONE);
//					parentLot.setQtyTransaction(BigDecimal.ONE);
//					MoLineReceiveDialog mrd = new MoLineReceiveDialog(UI.getActiveShell(), selectedMoLine, parentLot, workCenter);
//					if(mrd.open() == Dialog.CANCEL) {
//						selectedMoLine = null;
//					}
//					refreshAll();
//				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("MOLineSection : receiveAdapter()", e);
		}
	}

	protected void closeMoLineAdapter() {

		try {
			if (selectedMoLine != null) {
				//这里是判断子工作令物料和主工作令物料是否一样，如果一样不允许撤销
				//这里情况比较特殊，因为手动创建的子工作令没有主工作令，所以selectedMoLine.getMasterMoRrn()就是存的它本身的objectRrn
				//所以如果selectedMoLine.getObjectRrn()和selectedMoLine.getMasterMoRrn()相等说明是手动创建的子工作令，就不需要判断了
				if(selectedMoLine.getMasterMoRrn() != null && !selectedMoLine.getMasterMoRrn().equals(selectedMoLine.getObjectRrn())){
					Long MoLineMaterialRrn=selectedMoLine.getMaterialRrn();
					ManufactureOrder mo=new ManufactureOrder();
					mo.setObjectRrn(selectedMoLine.getMasterMoRrn());
					adManager=Framework.getService(ADManager.class);
					mo=(ManufactureOrder)adManager.getEntity(mo);
					Long  MoMaterialRrn=mo.getMaterialRrn();
					if(MoLineMaterialRrn.equals(MoMaterialRrn)){
						UI.showError(Message.getString("common.can_not_close"));
						return;
					}
				}
				
				if(UI.showConfirm(Message.getString("common.confirm_repeal"))){
					WipManager wipManager = Framework.getService(WipManager.class);
					wipManager.closeMoLine(selectedMoLine, Env.getUserRrn());
					UI.showInfo(Message.getString("common.close_successed"));
				}
			}
			refreshAll();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void chartAdapter() {
		if (workCenter != null && tableManager != null) {
			GanttChartDialog dialog = new GanttChartDialog(tableManager.getADTable(), this.workCenter);
			dialog.open();
		}
	}
	
	protected void refreenceDocAdapter() {
		if(selectedMoLine != null){
			adTableDoc = getAdTableOfMOLine(TABLE_NAME_MATERIAL_DOC);
			ReferceDocDialog referceDocDialog = new ReferceDocDialog(UI.getActiveShell(), adTableDoc, selectedMoLine.getMaterial());
			if(referceDocDialog.open() == Dialog.OK){
			}
		}
	}

	protected void bomTreeAdapter() {
		if (selectedMoLine != null && selectedMoLine.getObjectRrn() != null) {
			WorkOrderBomDialog dialog = new WorkOrderBomDialog(UI.getActiveShell(), selectedMoLine, tableManager.getADTable());
			dialog.open();
		}
	}
	
	//获取BOM清单，用于较验BOM中的库存数是否够消耗
	private List<ManufactureOrderBom> getInsufficientBomList() {
		try {
			WipManager wipManager = Framework.getService(WipManager.class);
			List<ManufactureOrderBom> moBoms = wipManager.getMoLineBom(selectedMoLine.getObjectRrn());
			List<ManufactureOrderBom> insufficientBoms = new ArrayList<ManufactureOrderBom>();
			
			MaterialSum ms = null;
			for(ManufactureOrderBom moBom : moBoms) {
				ms = wipManager.getMaterialSum(Env.getOrgRrn(), moBom.getMaterialRrn(), false, false);
				if(ms != null) {
					moBom.setQtyAllocation(ms.getQtyAllocation());
					moBom.setQtyMoLineWip(ms.getQtyMoLineWip());
					moBom.setQtyOnHand(ms.getQtyOnHand());
					moBom.setQtyTransit(ms.getQtyTransit());
				}
				if(moBom.getQtyOnHand() == null || moBom.getQtyOnHand().compareTo(moBom.getQty()) < 0){
					insufficientBoms.add(moBom);
				}
			}
			return insufficientBoms;
		} catch(Exception e) {
			logger.error("Error at MoLineSection ：getBomList() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	class BomInfoDialog extends InClosableTitleAreaDialog{
		static final int MIN_DIALOG_WIDTH = 750;
		static final int MIN_DIALOG_HEIGHT = 350;
		StructuredViewer viewer;
		List input;
		ADTable adTable;
		
		public BomInfoDialog(Shell parentShell, ADTable adTable, List input) {
			super(parentShell);
			this.adTable = adTable;
			this.input = input;
		}		
		
		@Override
		protected Control createDialogArea(Composite parent) {
			String dialogTitle = "以下物料库存不足";
			setTitle(dialogTitle);
			Composite composite = (Composite) super.createDialogArea(parent);
			FormToolkit toolkit = new FormToolkit(composite.getDisplay());		
			ScrolledForm sForm = toolkit.createScrolledForm(composite);
			sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
			Composite body = sForm.getForm().getBody();
			configureBody(body);
			
			tableManager = new TableListManager(adTable);
			viewer = tableManager.createViewer(body, toolkit);
			viewer.setInput(input);
			tableManager.updateView(viewer);
			return composite;
		}
		
		protected void configureBody(Composite body) {
			GridLayout layout = new GridLayout(1, true);
			body.setLayout(layout);
			body.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
		
		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			// create OK and Cancel buttons by default
			createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
					true);
		}
		
		@Override
		protected Point getInitialSize() {
			Point shellSize = super.getInitialSize();
			return new Point(Math.max(
					convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
					Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
							shellSize.y));
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			//Modified by BruceYou 2012-03-07
			//EntityQueryDialog --> WorkCenterQueryDialog
			queryDialog =  new WorkCenterQueryDialog(UI.getActiveShell(), entityTableManager, this);
			queryDialog.open();
		}
	}

	// 按照工作中心查找出该工作中心下所有已审核的子工作令
	protected void refreshAdapter() {
		try {
			refreshAll();
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("MoLineSection : refreshAdapter()", e);
		}
	}
	
	public void refresh() {
		try {
			setAllToolItemEnabled(hasWCAuthority);
			StringBuffer whereClause = new StringBuffer(this.getWhereClause());
			whereClause.append(SUFFIX);
			WipManager wipManager = Framework.getService(WipManager.class);
			
			if(workCenter == null){
				UI.showInfo("请选择工作中心");
				return;
			}
			long time1= System.currentTimeMillis();
			List<ManufactureOrderLine> list = wipManager.getMoLineByWorkCenter2(Env.getOrgRrn(),
					workCenter.getObjectRrn(), whereClause.toString());
			long time2 = System.currentTimeMillis();
			long time3 = time2-time1;
			System.out.println("结束1"+time3);
			
			//将totalQty和totalQtyReceive初始值置空
			totalQty = BigDecimal.ZERO;
			totalQtyReceive = BigDecimal.ZERO;
			if (list != null){
				this.displayCount = list.size();
				for(ManufactureOrderLine mol : list){
					totalQty = totalQty.add(mol.getQty());
					totalQtyReceive = totalQtyReceive.add(mol.getQtyReceive());
				}
			}else{
				displayCount = 0;
			}
			viewer.setInput(list);
//			tableManager.updateView(viewer);
			createSectionDesc(section);	
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	public void refreshAll() {
		setAllToolItemEnabled(hasWCAuthority);
		if (workCenter != null && workCenter.getObjectRrn() != 0) {
			try {
				long time1= System.currentTimeMillis();
				WipManager wipManager = Framework.getService(WipManager.class);
				List<ManufactureOrderLine> list = wipManager.getMoLineByWorkCenter2(Env.getOrgRrn(),
						workCenter.getObjectRrn(), null);
				if (list != null)
					this.displayCount = list.size();
				else
					displayCount = 0;
				
				//将totalQty和totalQtyReceive初始值置空
				totalQty = BigDecimal.ZERO;
				totalQtyReceive = BigDecimal.ZERO;
				if (list != null){
					this.displayCount = list.size();
					for(ManufactureOrderLine mol : list){
						totalQty = totalQty.add(mol.getQty());
						totalQtyReceive = totalQtyReceive.add(mol.getQtyReceive());
					}
				}else{
					displayCount = 0;
				}
				long time2 = System.currentTimeMillis();
				long time3 = time2-time1;
				System.out.println("结束2:"+time3);
				viewer.setSelection(null);
				viewer.setInput(list);
//				tableManager.updateView(viewer);
				createSectionDesc(section);	
				long time4 =  System.currentTimeMillis();
				System.out.println(time4-time2);
			} catch(Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}
		}
	}

	@Override
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public WorkCenter getWorkCenter() {
		return workCenter;
	}

	public void setWorkCenter(WorkCenter workCenter) {
		this.workCenter = workCenter;
		if(workCenter == null || !Env.getWorkCenters().contains(workCenter)){
			hasWCAuthority = false;
		}
	}

	protected void setWorkStatusChanged(String status) {
		if (ManufactureOrderLine.WORK_STATUS_RUNNING.equals(status)) {
			itemRun.setEnabled(false);
			itemSuspend.setEnabled(true);
			itemReceive.setEnabled(true);
			itemReAssign.setEnabled(false);
		} else if (ManufactureOrderLine.WORK_STATUS_SUSPENED.equals(status)) {
			itemRun.setEnabled(true);
			itemSuspend.setEnabled(false);
			itemReceive.setEnabled(false);
			itemReAssign.setEnabled(true);
			itemClose.setEnabled(true);
		} else if (status == null || "".equals(status)) {
			itemRun.setEnabled(true);
			itemSuspend.setEnabled(false);
			itemReceive.setEnabled(false);
			itemReAssign.setEnabled(true);
			itemClose.setEnabled(true);
		} else {
			itemRun.setEnabled(false);
			itemSuspend.setEnabled(false);
			itemReceive.setEnabled(false);
			itemReAssign.setEnabled(true);
		}
	}
	
	// 如果查询出其他状态时,运行、挂起、接收、重分配和撤销都不可用, 如果为Approved状态,
	// 则直接返回, 各按钮是否可用根据setWorkStatusChanged()方法中的设置
	protected void setLineStatusChanged(String status) {
		if (ManufactureOrder.STATUS_APPROVED.equals(status)) {
			return;
		} else {
			itemRun.setEnabled(false);
			itemSuspend.setEnabled(false);
			itemReceive.setEnabled(false);
			itemReAssign.setEnabled(false);
			itemClose.setEnabled(false);
		}
	}
	
	protected void setInitAuthority(boolean enabled) {
		itemNew.setEnabled(enabled);
		itemReceive.setEnabled(enabled);
		itemReAssign.setEnabled(enabled);
		itemClose.setEnabled(enabled);
	}

	public String getWhereClause() {
		if(whereClause != null && !"".equals(whereClause.trim()))
			return whereClause;
		if (workCenter != null && workCenter.getObjectRrn() != 0) {
			return PREFIX + workCenter.getObjectRrn();
		}
		return "1 != 1";
//		return this.whereClause;
	}

	
	//撤销合并按钮实现
	protected void createToolItemMoDissolve(ToolBar tBar) {
		itemDissolve = new AuthorityToolItem(tBar, SWT.PUSH, KEY_WORKCENTER_DISSOLVE);
		itemDissolve.setText(Message.getString("wip.modissolve"));
		itemDissolve.setImage(SWTResourceCache.getImage("split"));
		itemDissolve.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				moDissolveAdapter();
			}
		});
	}
	
	//撤销合并按钮事件
	protected void moDissolveAdapter() {
		try {
			if(selectedMoLine!=null){
				if(selectedMoLine.getWorkStatus()==null ){
					WipManager wipManager=Framework.getService(WipManager.class);
					moLineList=wipManager.getCanDissolveMoLines(selectedMoLine);
					MoLineDissolveDialog moLineDissolveDialog=new MoLineDissolveDialog(UI.getActiveShell(),selectedMoLine,moLineList);
					moLineDissolveDialog.open();
					refreshAll();
					
				}else if(!selectedMoLine.getWorkStatus().equals(WORKSTATUS)){
					WipManager wipManager=Framework.getService(WipManager.class);
					moLineList=wipManager.getCanDissolveMoLines(selectedMoLine);
					MoLineDissolveDialog moLineDissolveDialog=new MoLineDissolveDialog(UI.getActiveShell(),selectedMoLine,moLineList);
					moLineDissolveDialog.open();
					refreshAll();
				}else{
					UI.showError("该工作令正在执行不能撤销合并！");
				}
			}
		} catch (ClientException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void createToolItemTotal(ToolBar tBar){
		toolItemTotal = new AuthorityToolItem(tBar, SWT.PUSH,KEY_WORKCENTER_TOTAL);
		toolItemTotal.setText("往返明细");
		toolItemTotal.setImage(SWTResourceCache.getImage("preview"));
		toolItemTotal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TotalAdapter();
			}
		});
	}

	protected void TotalAdapter() {
		if(workCenter != null){
			long workcenterRrn = workCenter.getObjectRrn();
			String urlfmt = Message.getString("url.total");
			String url = String.format(urlfmt, workcenterRrn);
			System.out.print(url);
			BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
			bd.open();
		}
	}
	
	protected void createToolItemConsume(ToolBar tBar){
		toolItemConsume = new AuthorityToolItem(tBar, SWT.PUSH,KEY_WORKCENTER_CONSUME);
		toolItemConsume.setText("领料及消耗");
		toolItemConsume.setImage(SWTResourceCache.getImage("preview"));
		toolItemConsume.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ConsumeAdapter();
			}
		});
	}

	protected void ConsumeAdapter() {
		if(workCenter != null){
			long workcenterRrn = workCenter.getObjectRrn();
			String urlfmt = Message.getString("url.consume");
			String url = String.format(urlfmt, workcenterRrn);
			System.out.print(url);
			BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
			bd.open();
		}
	}
	
	protected void createToolItemSeeproduct(ToolBar tBar){
		toolItemSeeproduct = new AuthorityToolItem(tBar, SWT.PUSH,KEY_WORKCENTER_SEEPRODUCT);
		toolItemSeeproduct.setText("生产及消耗统计");
		toolItemSeeproduct.setImage(SWTResourceCache.getImage("preview"));
		toolItemSeeproduct.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				SeeproductAdapter();
			}
		});
	}

	protected void SeeproductAdapter() {
		if(workCenter != null){
			QueryTimeDialog qtd=new QueryTimeDialog(UI.getActiveShell());
			if(qtd.open()== Dialog.OK){
				SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
				LinkedHashMap<String,IField> fields = qtd.baseForm.getFields();
				IField startTimeField =fields.get("sDate");
				Date startTime =  (Date)startTimeField.getValue();
				String s1 =  sdf.format(startTime);
				IField endTimeField =fields.get("eDate");
				Date endTime =  (Date)endTimeField.getValue();
				String s2 =  sdf.format(endTime);
				long workcenterRrn = workCenter.getObjectRrn();
				String urlfmt = Message.getString("url.seeproduct");
				String url = String.format(urlfmt, s1,s2,workcenterRrn);
				System.out.print(url);
				BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
				bd.open();
			}
		}
	}

	protected void createToolItemWorkNeed(ToolBar tBar) {
		toolItemWorkNeed = new AuthorityToolItem(tBar, SWT.PUSH,KEY_WORKCENTER_WORKNEED);
		toolItemWorkNeed.setText("需求数");
		toolItemWorkNeed.setImage(SWTResourceCache.getImage("preview"));
		toolItemWorkNeed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				workNeedAdapter();
			}
		});
	}

	protected void workNeedAdapter() {
	    try{
	    	if(workCenter != null){
	    		if(selectedMoLine == null){
	    			UI.showError("请选择对应的工作令行");
	    			return;
	    		}
	    		long material_rrn = selectedMoLine.getMaterialRrn();
	    		String urlfmt = Message.getString("url.workneed");
	    		String url = String.format(urlfmt,material_rrn);
	    		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
	    		bd.open();
	    	}else{
	    		UI.showError("请选择工作中心");
	    		return;
	    	}
	    }catch(Exception e){
	    	UI.showError("AD_MESSAGE 关键字 url.workneed");
	    }
	}	    	

	public StructuredViewer getViewer() {
		return viewer;
	}

	public void setViewer(StructuredViewer viewer) {
		this.viewer = viewer;
	}
	
	public void setAllToolItemEnabled(boolean enabled){
		if(toolBar != null){
			for(ToolItem ti : toolBar.getItems()){
				if(!ti.equals(itemQuery) && !ti.equals(itemRefresh) && !ti.equals(itemExport)){
					ti.setEnabled(enabled);
				}
			}
		}
	}
	
	protected void mpsLineDeliveryAdapter() {
		try {
			if (selectedMoLine != null) {
				//所以如果selectedMoLine.getObjectRrn()和selectedMoLine.getMasterMoRrn()相等说明是手动创建的子工作令，就不需要判断了
				if(selectedMoLine.getMasterMoRrn() != null && !selectedMoLine.getMasterMoRrn().equals(selectedMoLine.getObjectRrn())){
					MpsLineDeliveryDialog mpsLineDeliveryDialog = new MpsLineDeliveryDialog(UI.getActiveShell(), adTable, null,selectedMoLine);
					if (mpsLineDeliveryDialog.open() == Dialog.OK) {
					}
				}
			}
			
		} catch(Exception e) {
			logger.error("MOLineSection : mpsLineDeliveryAdapter()", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	public WorkCenterSection2 getParentSection() {
		return parentSection;
	}

	public void setParentSection(WorkCenterSection2 parentSection) {
		this.parentSection = parentSection;
	}
	
}

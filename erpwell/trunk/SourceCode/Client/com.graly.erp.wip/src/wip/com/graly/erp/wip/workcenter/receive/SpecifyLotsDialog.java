package com.graly.erp.wip.workcenter.receive;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;

public class SpecifyLotsDialog extends TitleAreaDialog {
	private Logger logger = Logger.getLogger(SpecifyLotsDialog.class);
	private List<Lot> currentLots;
	private List<Lot> autoLots;
	private Lot parentLot;
	private   CheckboxTableViewer tableViewer;
	private EntityTableManager viewerManagerAuto;
	protected  List<Lot> specifyLots;
	protected int lotSelectNum;
	protected int lotTotalNum;
	protected int autoLotSelectNum;
	protected int autoLotTotalNum;
	
	
	public SpecifyLotsDialog(Shell parentShell,List<Lot> currentLots,
			List<Lot> autoLots,Lot lot ) {
		super(parentShell);
		this.currentLots = currentLots;
		this.autoLots = autoLots;
		this.parentLot = lot;
		initialSelectTotal();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitle("批次信息保存");
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());

		Composite compositeArea = toolkit.createComposite(composite);
		compositeArea.setLayout(new GridLayout(1, false));
		compositeArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (parentLot.getLotType().equals("BATCH")) {
			Group specifygroup = new Group(compositeArea, SWT.SHADOW_ETCHED_OUT);
			specifygroup.setText( "预生成Batch类型批次总数为"+lotTotalNum+",当前使用"+lotSelectNum+"个");
			specifygroup.setLayout(new GridLayout(1, false));
			specifygroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));


			EntityTableManager viewerManager = new EntityTableManager(null,
					SWT.CHECK |SWT.FULL_SELECTION);
			 tableViewer =   (CheckboxTableViewer) viewerManager
					.createViewer(specifygroup, toolkit, new String[] {
							"lotId", "qtyCurrent" },
							new String[] { "批次号", "数量" }, new int[] { 12, 12 },
							12);
//			tableViewer.setInput(currentLots);
//			viewerManager.updateView(tableViewer);
			Table table = tableViewer.getTable();
			TableItem[] items = table.getItems();
			tableViewer.setAllChecked(true);
			for (final TableItem ti : items) {
				ti.setBackground(Display.getCurrent().getSystemColor(
						SWT.COLOR_GRAY));
			}
			tableViewer.addCheckStateListener(new ICheckStateListener() {

				@Override
				public void checkStateChanged(CheckStateChangedEvent event) {
					tableViewer.setChecked(event.getElement(), !event
							.getChecked());
				}

			});
			tableViewer.setInput(specifyLots);
			if(currentLots != null && currentLots.size() > 0){
					for(Object element : currentLots){
						tableViewer.setChecked(element, true);
					}
			}
			
			List<Lot> parentLots = new ArrayList<Lot>();
			if (currentLots.isEmpty()) {
				parentLots.add(parentLot);
			}
			
			//系统自动生成批次信息的分组框
			
			Group parentLotGroup = new Group(compositeArea,
					SWT.SHADOW_ETCHED_OUT);
			parentLotGroup.setText("系统自动生成Batch类型批次"+autoLotSelectNum+",当前使用"+autoLotTotalNum+"个");
			parentLotGroup.setLayout(new GridLayout(1, false));
			parentLotGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			viewerManagerAuto = new EntityTableManager(null,
					  SWT.FULL_SELECTION);
			TableViewer tableViewerAuto = (TableViewer) viewerManagerAuto
					.createViewer(parentLotGroup, toolkit, new String[] {
							"lotId", "qtyCurrent" },
							new String[] { "批次号", "数量" }, new int[] { 12, 12 },
							12);
			tableViewerAuto.setInput(autoLots);
//			viewerManagerAuto.updateView(tableViewerAuto);
			viewerManagerAuto.updateView(tableViewerAuto);
		}
		
		//serial类型
		
		
		if (parentLot.getLotType().equals("SERIAL")) {
			Group specifygroup = new Group(compositeArea, SWT.SHADOW_ETCHED_OUT);
			specifygroup.setText("预生成Serial类型批次总数为"+lotTotalNum+",当前使用"+lotSelectNum+"个");
			specifygroup.setLayout(new GridLayout(1, false));
			specifygroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));


			EntityTableManager viewerManager = new EntityTableManager(null,SWT.CHECK|SWT.FULL_SELECTION);
			tableViewer =    (CheckboxTableViewer) viewerManager
					.createViewer(specifygroup, toolkit, new String[] {
							"lotId", "qtyCurrent" },
							new String[] { "批次号", "数量" }, new int[] { 12, 12 },
							12);
//			tableViewer.setInput(currentLots);
//			viewerManager.updateView(tableViewer);
			Table table = tableViewer.getTable();
			TableItem[] items = table.getItems();
			tableViewer.setAllChecked(true);
			for (final TableItem ti : items) {
				ti.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			}
			tableViewer.addCheckStateListener(new ICheckStateListener() {

				@Override
				public void checkStateChanged(CheckStateChangedEvent event) {
					tableViewer.setChecked(event.getElement(), !event
							.getChecked());
				}

			});
			tableViewer.setInput(specifyLots);
			if(currentLots != null && currentLots.size() > 0){
					for(Object element : currentLots){
						tableViewer.setChecked(element, true);
					}
			}
			
			
			//系统自动生成批次信息的分组框
			
			Group parentLotGroup = new Group(compositeArea,SWT.SHADOW_ETCHED_OUT);
			parentLotGroup.setText("系统自动生成Serial类型批次"+autoLotSelectNum+",当前使用"+autoLotTotalNum+"个");
			parentLotGroup.setLayout(new GridLayout(1, false));
			parentLotGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		    viewerManagerAuto = new EntityTableManager(null,SWT.FULL_SELECTION);
			TableViewer tableViewerAuto =  (TableViewer) viewerManagerAuto.createViewer(parentLotGroup, toolkit, new String[] {"lotId", "qtyCurrent" },
							new String[] { "批次号", "数量" }, new int[] { 12, 12 },12);
			tableViewerAuto.setInput(autoLots);
			viewerManagerAuto.updateView(tableViewerAuto);

		}
		return composite;

	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("wip.workcenter.receive.specifylots.ok"), true);
		createButton(parent, IDialogConstants.CANCEL_ID,Message.getString("wip.workcenter.receive.specifylots.cancle"), false);
	}
	
	//初始化选择的批次
	public void initialSelectTotal(){
		try {
			WipManager wipManager = Framework.getService(WipManager.class);
			specifyLots = wipManager.getGenLotIds(parentLot);
		
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		if(!specifyLots.isEmpty()){
			lotTotalNum  =specifyLots.size();
		}
		if(!currentLots.isEmpty()){
			lotSelectNum  =currentLots.size();
		}
		if(!autoLots.isEmpty()){
			autoLotSelectNum = autoLots.size();
			autoLotTotalNum = autoLots.size();
		} 
		
	}
	
	
}
 
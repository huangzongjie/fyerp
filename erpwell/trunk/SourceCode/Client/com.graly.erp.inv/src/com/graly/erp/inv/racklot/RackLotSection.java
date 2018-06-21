package com.graly.erp.inv.racklot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.RacKMovementLot;
import com.graly.erp.inv.model.WarehouseRack;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.model.Lot;

public class RackLotSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(RackLotSection.class);
	
	private static final String TABLE_NAME_RACK_LOT = "INVRackLot";
	
	protected ToolItem itemNew;
	protected ToolItem itemCount;
	
	protected INVManager invManager;
	
	public RackLotSection() {
		super();
	}

	public RackLotSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemCount(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	

	protected void createToolItemCount(ToolBar tBar) {
		itemCount = new ToolItem(tBar, SWT.PUSH);
		itemCount.setText("盘点");
		itemCount.setImage(SWTResourceCache.getImage("lines"));
		itemCount.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent event) {
				countAdapter();
			}
		});
	}
	
	protected void countAdapter() {
		FileDialog fileDialog = new FileDialog(UI.getActiveShell(), SWT.OPEN);
		// 设置初始路径
		fileDialog.setFilterPath("C:/");
		// 设置扩展名过滤
		String[] filterExt = { "INV.txt"};
		fileDialog.setFilterExtensions(filterExt);
		// 打开文件对话框，返回选择的文件
		String selectedFile = fileDialog.open();
		if (selectedFile != null) {
			if (!selectedFile.toLowerCase().endsWith(".txt")) {
				UI.showWarning(Message.getString("ppm.upload_file_type_not_support"));
				return;
			}
			
			boolean sure = UI.showConfirm("确定要导入盘点数据吗？");
			if(!sure){
				return;
			}
			
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			boolean flag = true;
			StringBuffer errDetail = new StringBuffer();
			try {
				fis = new FileInputStream(selectedFile);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				String pType = null;
				String rackId = null;
				String lotId = null;
				String qtyStr = null;
				BigDecimal qty = null;
				String line = null;
				List<RacKMovementLot> rLots = new ArrayList<RacKMovementLot>();
				while((line = br.readLine())!=null){
					String[] lineSp = line.split(",");
					for(int i = 0; i < lineSp.length; i++){
						switch(i){
						case 0:
							pType = lineSp[i];
							break;
						case 1:
							rackId = lineSp[i];
							break;
						case 2:
							lotId = lineSp[i];
							break;
						case 3:
							qtyStr = lineSp[i];
							qty = new BigDecimal(qtyStr);
							break;
						}
					}
					
					if(invManager == null){
						invManager = Framework.getService(INVManager.class);
					}
					
					WarehouseRack rack = invManager.getWarehouseRackById(Env.getOrgRrn(), rackId);
					RacKMovementLot rLot = new RacKMovementLot();
					rLot.setLotId(lotId);
					rLot.setWarehouseRrn(rack.getWarehouseRrn());
					rLot.setRackRrn(rack.getObjectRrn());
					rLot.setQty(qty);
					if("01".equals(pType)){
					}else if("02".equals(pType)){
						rLot.setMovementType(RacKMovementLot.MOVEMENT_TYPE_COUNT);
					}
					rLot.setIoType(RacKMovementLot.IO_TYPE_IN);
					
					rLots.add(rLot);
				}
				
				invManager.batchSaveRacKMovementLot(Env.getOrgRrn(), rLots, Env.getUserRrn(),true);
				UI.showInfo(Message.getString("common.save_successed"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				try {
					fis.close();
					isr.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new ToolItem(tBar, SWT.PUSH);
		itemNew.setText("入库");
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}

	protected void newAdapter() {
		NewRackLotDialog2 nrld = new NewRackLotDialog2(UI.getActiveShell(), this.getADTable());
		nrld.open();
		refresh();
	}
	
	private ADTable getTableByName(String tableName){
		return null;
	}
}

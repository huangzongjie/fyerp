package com.graly.erp.inv.out;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.approve.ApprovedInvoiceTableManager;
import com.graly.erp.inv.model.LotStorage;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class LotSelectFromDbSection extends OutLineLotSection {
	private static final Logger logger = Logger.getLogger(LotSelectFromDbSection.class);
	private String beforCondition = "";//定义初始查询条件为空
	protected List<Lot> selectedLots;
	private int rowindex = 0;//双击时选择的行号
	private Label l1 = null ;//显示已选批次数量

	public LotSelectFromDbSection(ADBase parent, ADBase child, ADTable adTable,
			List<Lot> selectedLots, OutLineLotDialog olld) {
		super(parent, child, adTable, olld, false);
		this.selectedLots = selectedLots;
	}
	
	private void dataBonding(){
		// 将Lot转为MovementLineLot
		List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
		if(selectedLots != null) {
			for(Lot lot : selectedLots) {
				MovementLineLot lineLot = pareseMovementLineLot(outLine, lot.getQtyTransaction(), lot);
				lineLots.add(lineLot);
			}
			this.setLineLots(lineLots);
			refresh();
			setDoOprationsTrue();			
		}
	}
	
	@Override
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
//		lotManager = new TableListManager(adTable,SWT.CHECK );
		//2012-03-21 Simon 实现出库数量可编辑
		lotManager = new ApprovedInvoiceTableManager(adTable,SWT.CHECK );
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setLabelCount();
				}
				});
		final Table table = viewer.getTable();//获得数据表
		table.addMouseListener(new MouseAdapter() {
			 @Override
			 public void mouseDoubleClick(MouseEvent e) {
				 int index = table.getSelectionIndex();
				 selectAdapter(index);
			   }
			  });
	}
	
	protected void selectAdapter(int index){
		TableItem[] items= viewer.getTable().getItems();
		if(items.length <= 0){
			return;
		}
		if(rowindex == -1){
			//两次双击事件，选中一个区域，此时会把rowindex赋值-1，从新开始
			rowindex = index;
			items[index].setChecked(true);
			setLabelCount();
			return;
		}
		else if(rowindex == 0){
			//初始化时，rowindex值默认为0			
			for(int i = rowindex ; i <= index ; i++){
				items[i].setChecked(true);
			}
			rowindex = -1;
			setLabelCount();
			return;
		}
		else{
			int start = rowindex >= index ? index : rowindex;
			int end = rowindex >= index ? rowindex : index ;
			for(int i = start ; i <= end ; i++){
				items[i].setChecked(true);
			}
			rowindex = -1;
			setLabelCount();
			return;
		}
	}
	
	@Override
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
		Composite comp = toolkit.createComposite(client, SWT.BORDER);
		comp.setLayout(new GridLayout(3, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gridData);
		Label label = toolkit.createLabel(comp, Message.getString("inv.lotid"));
		label.setForeground(SWTResourceCache.getColor("Folder"));
		label.setFont(SWTResourceCache.getFont("Verdana"));
		txtLotId = toolkit.createText(comp, "", SWT.BORDER);
		txtLotId.setTextLimit(48);
		GridData gd = new GridData();
		gd.heightHint = 13;
		gd.widthHint = 340;
		txtLotId.setLayoutData(gd);
		txtLotId.addKeyListener(getKeyListener());
		
		txtLotId.setFocus();
		txtLotId.setText(outLine.getMaterialId());//初始化文本框
		txtLotId.setSelection(txtLotId.getText().length());
		
		Composite radioComp = toolkit.createComposite(comp, SWT.NONE);
		radioComp.setLayout(new GridLayout(8, false));
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 2;
		radioComp.setLayoutData(gridData2);
		Button b1 = toolkit.createButton(radioComp, "全选", SWT.BUTTON1);
		b1.setSelection(true);
		b1.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				allSelectAdapter();
			}
		});
		Button b2 = toolkit.createButton(radioComp, "反选", SWT.BUTTON1);
		b2.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				reverseSelectAdapter();
			}
		});
		Button b3 = toolkit.createButton(radioComp, "清空", SWT.BUTTON1);
		b3.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteAllAdapter();
			}
		});
		l1 = toolkit.createLabel(radioComp, "已选批次数量  0");
		l1.setAlignment(SWT.CENTER);
		GridData dg3 = new GridData();
		dg3.widthHint = 350;
		l1.setLayoutData(dg3);
	}
	
	@Override
	protected KeyListener getKeyListener(){
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					txtLotId.setForeground(SWTResourceCache.getColor("Black"));
					switch (e.keyCode) {
					case SWT.CR ://回车键
					case SWT.TRAVERSE_RETURN ://小键盘的回车键
						if(beforCondition.equals(txtLotId.getText())){
							return;
						}else{
							margeSelectLot(getSelectLot());
							dataBonding();
							beforCondition = txtLotId.getText();
						}	
						break;
					}
				}
			};
		} catch(Exception e) {
			logger.error("Error at LotSelectFromDbSection ：getKeyListener() ", e);
		}
		return null;
	}
	
	private void margeSelectLot(List<Lot> newlots){
		if(selectedLots == null){
			selectedLots = new ArrayList<Lot>();
		}
		for(Lot temp : newlots){
			if(selectedLots.contains(temp)){
				//不做处理
			}
			else{
				selectedLots.add(temp);
			}
		}
	}
	
	@Override
	protected void initTableContent() {
		//重新初始化表内容，什么都不做
	}
	
	private List<Lot> getSelectLot(){
		try{
			String docType =out.getOutType();
			String position = null;
			
			if("出库调整".equals(docType)) position = "OUT";
			if("入库调整".equals(docType)) position = "INSTOCK";
			
			INVManager invManager = Framework.getService(INVManager.class);
			lots = invManager.getOptionalOutLot(outLine , txtLotId.getText().trim(), position);
			if(lots == null || lots.size() == 0) {
				String name = outLine.getMaterialName();
				UI.showInfo(String.format(Message.getString("inv.material_no_lot"), name));
			}
			return lots;
		}catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return null;
		}
	}
	
	private void allSelectAdapter(){
		for (TableItem tableItem : viewer.getTable().getItems()){
            tableItem.setChecked(true);
		}
		setLabelCount();
	}
	
	private void reverseSelectAdapter(){
		Boolean iSelect;
		for (TableItem tableItem : viewer.getTable().getItems()){
            iSelect = tableItem.getChecked();
            if(iSelect == true){
            	tableItem.setChecked(false);
            }
            else{
            	tableItem.setChecked(true);
            }
		}
		setLabelCount();
	}
	
	private void deleteAllAdapter(){
		lots.clear();
		selectedLots.clear();
		dataBonding();
		setLabelCount();
	}
	
	protected MovementLine isContainsLot(Lot lot) {
		MovementLine l = null;
		if(outLine != null) {
			if (lot.getMaterialRrn().equals(outLine.getMaterialRrn())) {
				l = outLine;
				return l;
			} else {
				UI.showError(String.format(Message.getString("wip.material_does't_exisit_moboms"),
						lot.getLotId(), lot.getMaterialId(), outLine.getMaterialId()));
				return l;				
			}
		}
		return l;
	}

	protected List<MovementLineLot> getLineLots_1() {//重写获得出库批次方法，只选择钩住的出库批次，保存事件调用
		List<MovementLineLot> lis = new ArrayList<MovementLineLot>();
		for(TableItem ti : viewer.getTable().getItems()){
			if(ti.getChecked()){
				MovementLineLot mll = (MovementLineLot)ti.getData();
				lis.add(mll);
			}		
		}
		return lis;
	}
	
	private void setLabelCount(){
		int count = 0;
		for(TableItem ti : viewer.getTable().getItems()){
			if(ti.getChecked()){
				count ++;
			}
		}
		l1.setText("已选批次数量  " + String.valueOf(count));
	}

	@Override
	protected void saveAdapter() {
		try {
			if(outLine != null && (getLineLots_1() != null && getLineLots_1().size() > 0)) {
				if(validate()) {
					outLine.setMovementLots(getLineLots_1());
					INVManager invManager = Framework.getService(INVManager.class);
					//2012-03-23 Simon 销售出库时，防止大批次出库，用专用函数
					if(getOutType().equals(MovementOut.OutType.SOU) == true){
						invManager.saveSalesMovementOutLine(out, outLine, Env.getUserRrn());
					}
					else{
						invManager.saveMovementOutLine(out, outLine, getOutType(), Env.getUserRrn());
					}
					UI.showInfo(Message.getString("common.save_successed"));
					this.setIsSaved(true);
					((OutLineLotDialog)parentDialog).buttonPressed(IDialogConstants.CANCEL_ID);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	/* 验证每个批次的数量是否大于批次的库存数,验证批次总数是否等于出库行出库数量 */
	protected boolean validate() {
		try{
			BigDecimal total = BigDecimal.ZERO;
			INVManager invManager = Framework.getService(INVManager.class);
			for(TableItem ti : viewer.getTable().getItems()){
				if(ti.getChecked() == true){//该行被选中
					//获得该行出库数量
					MovementLineLot lineLot = (MovementLineLot)ti.getData();
					if(lineLot.getMaterialRrn().equals(outLine.getMaterialRrn())){
						//2012-03-24 Simon 验证批次的库存数量和出库数量大小
						LotStorage lotStorage = invManager.getLotStorage(Env.getOrgRrn(), lineLot.getLotRrn(),out.getWarehouseRrn(), Env.getUserRrn());
						if(lotStorage.getQtyOnhand().compareTo(lineLot.getQtyMovement()) >= 0  ){//库存数大于等于批的出库数
							total = total.add(lineLot.getQtyMovement());
						}
						else{
							UI.showError("批次"+lineLot.getLotId()+"库存数量"+lotStorage.getQtyOnhand()+"小于出库数量"+lineLot.getQtyMovement());
							return false;
						}
					}
				}
			}
			if(total.doubleValue() == outLine.getQtyMovement().doubleValue()) {
				return true;
			} 
			else {
				UI.showError(String.format(Message.getString("wip.out_qty_isnot_equal"),
						outLine.getQtyMovement().toString(), String.valueOf(total), outLine.getMaterialName()));
			}
			return false;
		}
		catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return false;
		}
	}
}

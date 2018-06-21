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
	private String beforCondition = "";//�����ʼ��ѯ����Ϊ��
	protected List<Lot> selectedLots;
	private int rowindex = 0;//˫��ʱѡ����к�
	private Label l1 = null ;//��ʾ��ѡ��������

	public LotSelectFromDbSection(ADBase parent, ADBase child, ADTable adTable,
			List<Lot> selectedLots, OutLineLotDialog olld) {
		super(parent, child, adTable, olld, false);
		this.selectedLots = selectedLots;
	}
	
	private void dataBonding(){
		// ��LotתΪMovementLineLot
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
		//2012-03-21 Simon ʵ�ֳ��������ɱ༭
		lotManager = new ApprovedInvoiceTableManager(adTable,SWT.CHECK );
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setLabelCount();
				}
				});
		final Table table = viewer.getTable();//������ݱ�
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
			//����˫���¼���ѡ��һ�����򣬴�ʱ���rowindex��ֵ-1�����¿�ʼ
			rowindex = index;
			items[index].setChecked(true);
			setLabelCount();
			return;
		}
		else if(rowindex == 0){
			//��ʼ��ʱ��rowindexֵĬ��Ϊ0			
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
		txtLotId.setText(outLine.getMaterialId());//��ʼ���ı���
		txtLotId.setSelection(txtLotId.getText().length());
		
		Composite radioComp = toolkit.createComposite(comp, SWT.NONE);
		radioComp.setLayout(new GridLayout(8, false));
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 2;
		radioComp.setLayoutData(gridData2);
		Button b1 = toolkit.createButton(radioComp, "ȫѡ", SWT.BUTTON1);
		b1.setSelection(true);
		b1.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				allSelectAdapter();
			}
		});
		Button b2 = toolkit.createButton(radioComp, "��ѡ", SWT.BUTTON1);
		b2.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				reverseSelectAdapter();
			}
		});
		Button b3 = toolkit.createButton(radioComp, "���", SWT.BUTTON1);
		b3.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteAllAdapter();
			}
		});
		l1 = toolkit.createLabel(radioComp, "��ѡ��������  0");
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
					case SWT.CR ://�س���
					case SWT.TRAVERSE_RETURN ://С���̵Ļس���
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
			logger.error("Error at LotSelectFromDbSection ��getKeyListener() ", e);
		}
		return null;
	}
	
	private void margeSelectLot(List<Lot> newlots){
		if(selectedLots == null){
			selectedLots = new ArrayList<Lot>();
		}
		for(Lot temp : newlots){
			if(selectedLots.contains(temp)){
				//��������
			}
			else{
				selectedLots.add(temp);
			}
		}
	}
	
	@Override
	protected void initTableContent() {
		//���³�ʼ�������ݣ�ʲô������
	}
	
	private List<Lot> getSelectLot(){
		try{
			String docType =out.getOutType();
			String position = null;
			
			if("�������".equals(docType)) position = "OUT";
			if("������".equals(docType)) position = "INSTOCK";
			
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

	protected List<MovementLineLot> getLineLots_1() {//��д��ó������η�����ֻѡ��ס�ĳ������Σ������¼�����
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
		l1.setText("��ѡ��������  " + String.valueOf(count));
	}

	@Override
	protected void saveAdapter() {
		try {
			if(outLine != null && (getLineLots_1() != null && getLineLots_1().size() > 0)) {
				if(validate()) {
					outLine.setMovementLots(getLineLots_1());
					INVManager invManager = Framework.getService(INVManager.class);
					//2012-03-23 Simon ���۳���ʱ����ֹ�����γ��⣬��ר�ú���
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
	
	/* ��֤ÿ�����ε������Ƿ�������εĿ����,��֤���������Ƿ���ڳ����г������� */
	protected boolean validate() {
		try{
			BigDecimal total = BigDecimal.ZERO;
			INVManager invManager = Framework.getService(INVManager.class);
			for(TableItem ti : viewer.getTable().getItems()){
				if(ti.getChecked() == true){//���б�ѡ��
					//��ø��г�������
					MovementLineLot lineLot = (MovementLineLot)ti.getData();
					if(lineLot.getMaterialRrn().equals(outLine.getMaterialRrn())){
						//2012-03-24 Simon ��֤���εĿ�������ͳ���������С
						LotStorage lotStorage = invManager.getLotStorage(Env.getOrgRrn(), lineLot.getLotRrn(),out.getWarehouseRrn(), Env.getUserRrn());
						if(lotStorage.getQtyOnhand().compareTo(lineLot.getQtyMovement()) >= 0  ){//��������ڵ������ĳ�����
							total = total.add(lineLot.getQtyMovement());
						}
						else{
							UI.showError("����"+lineLot.getLotId()+"�������"+lotStorage.getQtyOnhand()+"С�ڳ�������"+lineLot.getQtyMovement());
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

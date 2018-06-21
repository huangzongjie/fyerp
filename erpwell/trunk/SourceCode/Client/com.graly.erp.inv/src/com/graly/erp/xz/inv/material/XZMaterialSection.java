package com.graly.erp.xz.inv.material;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Storage;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.model.ADUser;



public class XZMaterialSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(XZMaterialSection.class);
	protected ToolItem itemBarcode;
	protected VStorageMaterial selectedLine;
	protected ToolItem itemGenMovementOut;//���õ�

	public XZMaterialSection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause("1<>1");//�մ�ʱ��ʾ������
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemGenerate(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemBarcode(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemBarcode(ToolBar tBar) {
		itemBarcode = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SEARCHMATERIAL_LOT);
		itemBarcode.setText(Message.getString("inv.barcode"));
		itemBarcode.setImage(SWTResourceCache.getImage("barcode"));
		itemBarcode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				barcodeAdapter();
			}
		});
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionLine(ss.getFirstElement());
	    		barcodeAdapter();
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionLine(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	private void setSelectionLine(Object obj) {
		if(obj instanceof VStorageMaterial) {
			selectedLine = (VStorageMaterial)obj;
		} else {
			selectedLine = null;
		}
	}
	
	protected void barcodeAdapter() {
		if(selectedLine != null) {
			XZStorageMaterialLotDialog ld = new XZStorageMaterialLotDialog(UI.getActiveShell(), selectedLine);
			ld.open();
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new XZEntityQueryDialog4WC(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	protected void refreshSection() {
		refresh();
	}
	
	protected void createToolItemGenerate(ToolBar tBar) {
		itemGenMovementOut = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_SEARCHMATERIAL_LOT);
		itemGenMovementOut.setText("�������õ�");
		itemGenMovementOut.setImage(SWTResourceCache.getImage("barcode"));
		itemGenMovementOut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				movementOutAdapter();
			}
		});
	}
	
	protected void movementOutAdapter(){
		try{
			//��˲���ͳ�ƹ��ɹ��������û�����ɳ��ⵥ
			String whereClause = " docStatus ='APPROVED' AND mpsId = 'Y' and description is null ";
			PURManager purManager = Framework.getService(PURManager.class);
			purManager.generateMovementOutXZ(Env.getOrgRrn(),Env.getUserRrn(),whereClause);
			UI.showInfo("�����ɹ�");
		}catch(Exception e ){
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
//	//ͳ�����мƻ����������Ƿ��㹻������㹻�򿪳�������
//	protected void movementOutAdapter() {
//		try{
//			ADManager adManager = Framework.getService(ADManager.class);
//			INVManager invManager = Framework.getService(INVManager.class);
//			List<Requisition>  requisitions = adManager.getEntityList(Env.getOrgRrn(), 
//					Requisition.class,Integer.MAX_VALUE,
//					" docStatus ='APPROVED' AND mpsId = 'Y' and description is null ","created asc");
//			HashMap<Long,Storage> storageMap = new LinkedHashMap<Long,Storage>();//ͳ��������������
//			for(Requisition requisition : requisitions){
//				boolean isGenFlag =true;//�����һ�����Ͽ�治��������
//				
//				requisition = (Requisition) adManager.getEntity(requisition);
//				for(RequisitionLine requisitionLine : requisition.getPrLines()){
//					Storage storage = invManager.getMaterialStorage(Env.getOrgRrn(), requisitionLine.getMaterialRrn(), 43005950L, Env.getUserRrn());
//					if(storage!=null && storageMap.get(storage.getObjectRrn())!=null){
//						storage = storageMap.get(storage.getObjectRrn());
//					}else{
//						storageMap.put(storage.getObjectRrn(), storage);
//					}
//					BigDecimal qtyOnhand = storage.getQtyOnhand();
//					if(qtyOnhand.compareTo(BigDecimal.ZERO)<=0){
//						//��治��//���С��������
//						isGenFlag= false;
////						break;
// 
//					}else if(qtyOnhand.compareTo(BigDecimal.ZERO)==1){
//						if(qtyOnhand.subtract(requisitionLine.getQty()).compareTo(BigDecimal.ZERO) >=0){
//							//�����ڵ���������
//							storage.setQtyOnhand(qtyOnhand.subtract(requisitionLine.getQty()));
//						}else{
//							//���С��������
//							storage.setQtyOnhand(BigDecimal.ZERO);
//							isGenFlag= false;
////							break;
//						}
//					}
//					
//				}
//				if(isGenFlag){
//					MovementOut movementOut = new MovementOut();
//					movementOut.setOrgRrn(Env.getOrgRrn());
//					movementOut.setCreatedBy(Env.getUserRrn());
//					movementOut.setDocStatus(MovementOut.STATUS_DRAFTED);
//					Warehouse warehouse = invManager.getDefaultWarehouse(Env.getOrgRrn());
//					
//					movementOut.setWarehouseId(warehouse.getWarehouseId());
//					movementOut.setWarehouseRrn(warehouse.getObjectRrn());
//					movementOut.setUserCreated(Env.getUserName());
//					movementOut.setDocType(MovementOut.DOCTYPE_OOU);
//					movementOut.setTotalLines(1L);
//					movementOut.setDateCreated(new Date());
//					movementOut.setOutType("����");
//					
//					movementOut.setIqcId(requisition.getDocId());
//					movementOut.setIqcRrn(requisition.getObjectRrn());
//					
//					List<MovementLine>  movementLines = new ArrayList<MovementLine>();
//					for(RequisitionLine requestLine : requisition.getPrLines()){
//						MovementLine movementLine = new MovementLine();
//						movementLine.setIsActive(true);
//						movementLine.setEquipmentRrn(requestLine.getObjectRrn());
//						movementLine.setEquipmentId(requestLine.getRequisitionId());
//						movementLine.setMaterialId(requestLine.getMaterialId());
//						movementLine.setMaterialRrn(requestLine.getMaterialRrn());
//						movementLine.setMaterialName(requestLine.getMaterialName());
//						movementLine.setQtyMovement(requestLine.getQty());
//						movementLine.setUomId(requestLine.getUomId());
//						movementLine.setLineNo(requestLine.getLineNo());
//						movementLine.setLineStatus(Movement.STATUS_DRAFTED);
//						movementLine.setLotType(requestLine.getLotType());
//						movementLine.setLocatorId(MovementOut.DOCTYPE_OOU);//���ֶ����ڼ�¼����ERP������ERP�ĳ�������
//						movementLine.setXzUserRrn(requestLine.getXzUserRrn());
//						movementLine.setXzUserName(requestLine.getXzUserName());
//						movementLine.setXzDepartment(requestLine.getXzDepartment());
//						movementLine.setXzCompany(requestLine.getXzCompany());
//						movementLines.add(movementLine);
//					}
//					invManager.saveMovementOutLine(movementOut, movementLines, MovementOut.OutType.OOU, Env.getUserRrn());
//					requisition.setDescription("Y");
//					adManager.saveEntity(requisition, Env.getOrgRrn());
//				}
//			}
//		}catch(Exception e ){
//			
//		}
//	}
}

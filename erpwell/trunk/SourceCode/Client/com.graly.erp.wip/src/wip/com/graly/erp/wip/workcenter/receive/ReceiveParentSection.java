package com.graly.erp.wip.workcenter.receive;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.erp.base.model.Material;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WIPEquipment;
import com.graly.framework.security.model.WIPMould;
import com.graly.mes.wip.client.LotManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotReceiveTemp;

public class ReceiveParentSection {
	private static final Logger logger = Logger.getLogger(ReceiveParentSection.class);
	private static final String TABLE_NAME = "WIPReceiveInfoLot";
	protected IManagedForm form;
	protected ADTable adTable; //用户输入检验员、备注等信息的ADTable
	
	private MoLineReceiveSection parentSection;
	private MoLineReceiveForm receiveForm;
	private ReciveLotInfoForm infoForm;
	private String lotType;
	private BigDecimal qtySerialReceive = BigDecimal.ONE;	//物料为Serial类型时的每次接收数量
	String moComments;
	
	public ReceiveParentSection() {}
	
	public ReceiveParentSection(MoLineReceiveSection parentSection, String lotType, String moComments) {
		this.parentSection = parentSection;
		this.lotType = lotType;
		this.moComments = moComments;
	}
	
	public void createContent(IManagedForm form, Composite parent) {
		this.form = form;
		
		createParentForm(parent);
		createSeprator(parent);
		createForm(parent);
	}
	
	protected void createParentForm(Composite client) {
		Material material = parentSection.getMoLine().getMaterial();
		if(Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
			receiveForm = new MoLineReceiveForm(client, SWT.NULL, parentSection.getMoLine(),
					form.getMessageManager(), material.getLotType(), parentSection.getParentLot());
		} else {
			receiveForm = new MoLineReceiveForm(client, SWT.NULL, parentSection.getMoLine(),
					form.getMessageManager(), material.getLotType(), false);
		}
		receiveForm.setCanEdit(false);
		receiveForm.setCreateComments(true, moComments);
//		if(Lot.LOTTYPE_BATCH.equals(lotType) || Lot.LOTTYPE_MATERIAL.equals(lotType)) {
//		}
		receiveForm.setParentLot(parentSection.getParentLot());
		receiveForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		receiveForm.createFormContent();
	}
	
	protected void createSeprator(Composite parent) {
		Label separator = form.getToolkit().createSeparator(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	protected void createForm(Composite parent) {
		if(adTable == null) {
			initAdTable();
		}
		
		//读取临时保存的设备编号等等与设备相关栏位的数据
		LotReceiveTemp lotReceiveTemp = null;
		try {
			LotManager lotManager = Framework.getService(LotManager.class);
			 lotReceiveTemp =lotManager.getLotReceiveTemp(parentSection.getMoLine().getObjectRrn(), Env.getOrgRrn());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(lotReceiveTemp!=null){
			Lot lot =parentSection.getParentLot();
			lot.setUserQc(lotReceiveTemp.getUserQc());
			lot.setEquipmentId(lotReceiveTemp.getEquipmentId());
			lot.setMoldId(lotReceiveTemp.getMoldId());
			lot.setReverseField1(lotReceiveTemp.getReverseField1());
			lot.setReverseField2(lotReceiveTemp.getReverseField2());
			lot.setReverseField3(lotReceiveTemp.getReverseField3());
			lot.setReverseField4(lotReceiveTemp.getReverseField4());
			lot.setReverseField5(lotReceiveTemp.getReverseField5());
			lot.setLotComment(lotReceiveTemp.getLotComment());
		}
		infoForm = new ReciveLotInfoForm(parent, SWT.NULL, parentSection.getWorkCenter(), parentSection.getParentLot(), adTable,
				form.getMessageManager());
//		infoForm.setGridY(2);
		infoForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		infoForm.createFormContent();
	}
	
	private void initAdTable() {
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			adTable = adManager.getADTable(0L, TABLE_NAME);
		} catch(Exception e) {
			logger.error("ReceiveParentSection : initADTable() ", e);
        	ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	/* 单击保存并进入下一个MoLine的接收, 更新已存在数据 */
	public void refresh() {
		receiveForm.setObject(parentSection.getMoLine());
		if(!Lot.LOTTYPE_MATERIAL.equals(lotType)) {
			Lot parentLot = createNewParentLot();
			receiveForm.setParentLot(parentLot);
			parentSection.setParentLot(parentLot);
		}
		receiveForm.loadFromObject();
		
		infoForm.loadFromObject();
	}
	
	private Lot createNewParentLot() {
		Lot parentLot = new Lot();
		Lot preParentLot = parentSection.getParentLot();
		ManufactureOrderLine moLine = parentSection.getMoLine();
		if(Lot.LOTTYPE_BATCH.equals(lotType)) {
			parentLot.setQtyCurrent(preParentLot.getQtyCurrent());
			parentLot.setQtyTransaction(preParentLot.getQtyTransaction());
		} else if(Lot.LOTTYPE_SERIAL.equals(lotType)) {
			parentLot.setQtyCurrent(qtySerialReceive);
			parentLot.setQtyTransaction(qtySerialReceive);
		}
		parentLot.setOrgRrn(Env.getOrgRrn());
		parentLot.setLotType(moLine.getMaterial().getLotType());
		parentLot.setMoRrn(moLine.getMasterMoRrn());
		parentLot.setMoLineRrn(moLine.getObjectRrn());
		parentLot.setMaterialRrn(moLine.getMaterialRrn());
		parentLot.setMaterialId(preParentLot.getMaterialId());
		parentLot.setMaterialName(preParentLot.getMaterialName());
		return parentLot;
	}

	protected void createParentLotId() {
		this.receiveForm.createLotId();
	}
	
	protected boolean saveToObject() {
		return (this.receiveForm.saveToObject()
				&& this.infoForm.saveToObject());
	}
	
	protected Lot getParentLot() {
		Lot lot = this.receiveForm.getParentLot();
		if(this.infoForm.getObject() instanceof Lot) {
			Lot infoLot = (Lot)this.infoForm.getObject();
			lot.setUserQc(infoLot.getUserQc());
			WIPEquipment eqp = null;
			WIPMould mould = null;
			Long eqpRrn = infoLot.getEquipmentRrn();
			Long mouldRrn = infoLot.getMoldRrn();
			try {
				ADManager adManager = Framework.getService(ADManager.class);
				List<WIPEquipment> eqpList = adManager.getEntityList(Env.getOrgRrn(),
						WIPEquipment.class,Integer.MAX_VALUE, "WIPEquipment.objectRrn = " + eqpRrn , null);
				if(eqpList != null && eqpList.size() > 0){
					eqp = eqpList.get(0);
					lot.setEquipmentRrn(eqp.getObjectRrn());
					lot.setEquipmentId(eqp.getEquipmentId());
				}
				
				List<WIPMould> mouldList = adManager.getEntityList(Env.getOrgRrn(),
						WIPMould.class,Integer.MAX_VALUE, "WIPMould.objectRrn = " + mouldRrn , null);
				if(mouldList != null && mouldList.size() >0){
					mould = mouldList.get(0);
					lot.setMoldRrn(mould.getObjectRrn());
					lot.setMoldId(mould.getMouldId());	
				}
			} 
			catch (Exception e) {
				logger.error("ReceiveParentSection:getParentLot()", e);
			}
			lot.setLotComment(infoLot.getLotComment());			
		}
		return lot;
	}

	public BigDecimal getQtySerialReceive() {
		return qtySerialReceive;
	}

	public void setQtySerialReceive(BigDecimal qtySerialReceive) {
		this.qtySerialReceive = qtySerialReceive;
	}
	
	public Material getMaterial() {
		return parentSection.getMoLine().getMaterial();
	}

	public ReciveLotInfoForm getInfoForm() {
		return infoForm;
	}

	public void setInfoForm(ReciveLotInfoForm infoForm) {
		this.infoForm = infoForm;
	}
	
}

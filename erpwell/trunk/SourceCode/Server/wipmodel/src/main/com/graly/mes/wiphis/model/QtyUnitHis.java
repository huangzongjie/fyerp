package com.graly.mes.wiphis.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.mes.wip.model.QtyUnit;
import com.graly.mes.wip.model.Lot;

@Entity
@Table(name="WIPHIS_QTYUNIT")
public class QtyUnitHis extends ProcessUnitHis {

	public QtyUnitHis(){
	}
	
	public QtyUnitHis(QtyUnit unit){
		this.orgRrn = unit.getOrgRrn();
		this.setIsActive(unit.getIsActive());
		this.updatedBy = unit.getUpdatedBy();
		this.setEquipmentRrn(unit.getEquipmentRrn());
		this.setEquipmentId(unit.getEquipmentId());
		this.setMainQty(unit.getMainQty()); 
		this.setSubQty(unit.getSubQty());
		this.setOperatorRrn(unit.getOperatorRrn());
		this.setOperatorName(unit.getOperatorName());
		this.setParentUnitRrn(unit.getParentUnitRrn());
		this.subUnitType = unit.getSubUnitType();
	}
}

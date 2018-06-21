package com.graly.erp.wiphis.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIPHIS_MO_LINE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TRANS_TYPE", discriminatorType = DiscriminatorType.STRING, length = 32)
public class MoLineHis extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	public static String TRANS_RUN = "RUN";
	public static String TRANS_SUSPEND = "SUSPEND";
	public static String TRANS_CLOSE = "CLOSE";
	public static String TRANS_COMPLETE = "COMPLETE";
	public static String TRANS_DISASSEMBLE = "DISASSEMBLE";
	public static String TRANS_MERGE = "MERGE";
	public static String TRANS_UNMERGE = "UNMERGE";
	
	@Column(name="HISTORY_SEQ")
	private Long hisSeq;
	
	@Column(name="TRANS_TYPE", insertable = false, updatable = false)
	private String transType;
	
	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;

	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY")
	private BigDecimal qty = BigDecimal.ZERO;
	
	@Column(name="DATE_START")
	private Date dateStart;
	
	@Column(name="DATE_END")
	private Date dateEnd;
	
	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice;
	
	@Column(name="LINE_TOTAL")
	private BigDecimal lineTotal = BigDecimal.ZERO;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;

	@Column(name="MASTER_MO_RRN")
	private Long masterMoRrn;
	
	@Column(name="MASTER_MO_ID")
	private String masterMoId;
	
	@Column(name="PARENT_MO_RRN")
	private Long parentMoRrn;

	@Column(name="QTY_RECEIVE")
	private BigDecimal qtyReceive;
	
	@Column(name="WORKCENTER_RRN")
	private Long workCenterRrn;
	
	@Column(name="PATH")
	private String path;
	
	@Column(name="PATH_LEVEL")
	private Long pathLevel;
	
	@Column(name="WORK_STATUS")
	private String workStatus;
	
	@Column(name="MANPOWER")
	private BigDecimal manpower;
	
	public MoLineHis(){
	}
	
	public MoLineHis(ManufactureOrderLine moLine){
		this.setOrgRrn(moLine.getOrgRrn());
		this.setIsActive(moLine.getIsActive());
		this.setUpdatedBy(moLine.getUpdatedBy());
		this.setLineNo(moLine.getLineNo());
		this.setMaterialRrn(moLine.getMaterialRrn());
		this.setUomId(moLine.getUomId());
		this.setQty(moLine.getQty());
		this.setDateStart(moLine.getDateStart());
		this.setDateEnd(moLine.getDateEnd());
		this.setUnitPrice(moLine.getUnitPrice());
		this.setLineTotal(moLine.getLineTotal());
		this.setLineStatus(moLine.getLineStatus());
		this.setDescription(moLine.getDescription());
		this.setMasterMoRrn(moLine.getMasterMoRrn());
		this.setMasterMoId(moLine.getMasterMoId());
		this.setParentMoRrn(moLine.getParentMoRrn());
		this.setQtyReceive(moLine.getQtyReceive());
		this.setWorkCenterRrn(moLine.getWorkCenterRrn());
		this.setPath(moLine.getPath());
		this.setPathLevel(moLine.getPathLevel());
		this.setWorkStatus(moLine.getWorkStatus());
	}
	
	public void setHisSeq(Long hisSeq) {
		this.hisSeq = hisSeq;
	}

	public Long getHisSeq() {
		return hisSeq;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getTransType() {
		return transType;
	}
	
	public Long getLineNo() {
		return lineNo;
	}

	public void setLineNo(Long lineNo) {
		this.lineNo = lineNo;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
	
	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public Date getDateStart() {
		return dateStart;
	}
	
	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}
	
	public Date getDateEnd() {
		return dateEnd;
	}
	
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}
	
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public Long getMasterMoRrn() {
		return masterMoRrn;
	}

	public void setMasterMoRrn(Long masterMoRrn) {
		this.masterMoRrn = masterMoRrn;
	}

	public String getMasterMoId() {
		return masterMoId;
	}

	public void setMasterMoId(String masterMoId) {
		this.masterMoId = masterMoId;
	}

	public Long getParentMoRrn() {
		return parentMoRrn;
	}

	public void setParentMoRrn(Long parentMoRrn) {
		this.parentMoRrn = parentMoRrn;
	}

	public BigDecimal getQtyReceive() {
		return qtyReceive;
	}

	public void setQtyReceive(BigDecimal qtyReceive) {
		this.qtyReceive = qtyReceive;
	}

	public void setWorkCenterRrn(Long workCenterRrn) {
		this.workCenterRrn = workCenterRrn;
	}

	public Long getWorkCenterRrn() {
		return workCenterRrn;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPathLevel(Long pathLevel) {
		this.pathLevel = pathLevel;
	}

	public Long getPathLevel() {
		return pathLevel;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public BigDecimal getManpower() {
		return manpower;
	}

	public void setManpower(BigDecimal manpower) {
		this.manpower = manpower;
	}
}

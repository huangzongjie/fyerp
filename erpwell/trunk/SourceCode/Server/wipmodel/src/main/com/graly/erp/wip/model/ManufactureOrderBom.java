package com.graly.erp.wip.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.vdm.model.VendorMaterial;

@Entity
@Table(name="WIP_MO_BOM")
public class ManufactureOrderBom extends DocumentationLine {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MO_RRN")
	private Long moRrn;
	
	@Column(name="MATERIAL_PARENT_RRN")
	private Long materialParentRrn;

	@Column(name="PATH")
	private String path;
	
	@Column(name="PATH_LEVEL")
	private Long pathLevel;
	
	@Column(name="QTY_BOM")
	private BigDecimal qtyBom;

	@Column(name="QTY_UNIT")
	private BigDecimal unitQty;
	
	@Column(name="QTY_NEED")
	private BigDecimal qtyNeed;
	
	@Column(name="DURATION")
	private Long duration;
	
	@Column(name="WORKCENTER_RRN")
	private Long workCenterRrn;
	
	@Column(name="IS_MATERIAL_NEED")
	private String isMaterialNeed = "N";
	
	@Column(name="IS_DATE_NEED")
	private String isDateNeed = "N";
	
	@Column(name="IS_CAN_START")
	private String isCanStart = "N"; //对于有库存，但小于最低库存的可以安排生产

	@Column(name="REQUISITION_LINE_RRN")
	private Long requsitionLineRrn;
	
	@Column(name="MO_LINE_RRN")
	private Long moLineRrn;

	@Column(name="QTY_MIN")
	private BigDecimal qtyMin = BigDecimal.ZERO;
	
	@Column(name="QTY_ALLOCATION")
	private BigDecimal qtyAllocation = BigDecimal.ZERO;
	
	@Column(name="QTY_ONHAND")
	private BigDecimal qtyOnHand = BigDecimal.ZERO;
	
	@Column(name="QTY_TRANSIT")
	private BigDecimal qtyTransit = BigDecimal.ZERO;
	
	@Column(name="QTY_MOLINE_WIP")
	private BigDecimal qtyMoLineWip = BigDecimal.ZERO;
	
	@Column(name="QTY_SO")
	private BigDecimal qtySo = BigDecimal.ZERO;
	
	@Column(name="QTY_MIN_PRODUCT")
	private BigDecimal qtyMinProduct = BigDecimal.ZERO;
	
	@Column(name="STAND_TIME")
	private BigDecimal standTime;
	
	@Column(name="IS_PREPARE_MO_LINE")
	private String isPrepareMoLine;//是否是待处理
	
	@Column(name="AGAIN_GEN_MO_LINE")
	private String againGenMoLine;//重新生成工作令
	
	@Column(name="DBA_MARK")
	private String dbaMark;//DBK备注
	
	@Column(name="IS_SELF_CONTROL")
	private String isSelfControl;//用户自定义(没有下级物料的自制件和BOM导入设置的待处理)
	
	@Transient
	private String isProduct = "N";
	
	@Transient
	private String weekType;

	@Transient
	private Long iqcLeadTime;
	
	@Transient
	private VendorMaterial vendorMaterial;
	
	@Column(name="REAL_PATH")
	private String realPath;
	
	@Column(name="REAL_PATH_LEVEL")
	private Long realPathLevel;
	

	
	@Transient
	private String moMaterialName;//物料名称
	
	@Transient
	private String moMaterialId;//物料ID
	
	@Transient
	private String moMoId;//工作令编号
	
	@Transient
	private String prepareType;//预处理类型,外购or自制
	
	public void setMoRrn(Long moRrn) {
		this.moRrn = moRrn;
	}

	public Long getMoRrn() {
		return moRrn;
	}

	public void setMaterialParentRrn(Long materialParentRrn) {
		this.materialParentRrn = materialParentRrn;
	}

	public Long getMaterialParentRrn() {
		return materialParentRrn;
	}

	public BigDecimal getQtyBom() {
		return qtyBom;
	}

	public void setQtyBom(BigDecimal qtyBom) {
		this.qtyBom = qtyBom;
	}

	public void setUnitQty(BigDecimal unitQty) {
		this.unitQty = unitQty;
	}

	public BigDecimal getUnitQty() {
		return unitQty;
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

	public void setQtyNeed(BigDecimal qtyNeed) {
		this.qtyNeed = qtyNeed;
	}

	public BigDecimal getQtyNeed() {
		return qtyNeed;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Long getDuration() {
		return duration;
	}
	
	public void setWorkCenterRrn(Long workCenterRrn) {
		this.workCenterRrn = workCenterRrn;
	}

	public Long getWorkCenterRrn() {
		return workCenterRrn;
	}
	
	public Boolean getIsMaterialNeed(){
		return "Y".equalsIgnoreCase(this.isMaterialNeed) ? true : false; 
	}

	public void setIsMaterialNeed(Boolean isMaterialNeed) {
		this.isMaterialNeed = isMaterialNeed ? "Y" : "N";
	}
	
	public Boolean getIsDateNeed(){
		return "Y".equalsIgnoreCase(this.isDateNeed) ? true : false; 
	}

	public void setIsDateNeed(Boolean isDateNeed) {
		this.isDateNeed = isDateNeed ? "Y" : "N";
	}
	
	public void setRequsitionLineRrn(Long requsitionLineRrn) {
		this.requsitionLineRrn = requsitionLineRrn;
	}

	public Long getRequsitionLineRrn() {
		return requsitionLineRrn;
	}

	public void setMoLineRrn(Long moLineRrn) {
		this.moLineRrn = moLineRrn;
	}

	public Long getMoLineRrn() {
		return moLineRrn;
	}

	public Boolean getIsProduct(){
		return "Y".equalsIgnoreCase(this.isProduct) ? true : false; 
	}

	public void setIsProduct(Boolean isProduct) {
		this.isProduct = isProduct ? "Y" : "N";
	}
	
	public Boolean getIsCanStart(){
		return "Y".equalsIgnoreCase(this.isCanStart) ? true : false; 
	}

	public void setIsCanStart(Boolean isCanStart) {
		this.isCanStart = isCanStart ? "Y" : "N";
	}

	public void setWeekType(String weekType) {
		this.weekType = weekType;
	}

	public String getWeekType() {
		return weekType;
	}

	public void setIqcLeadTime(Long iqcLeadTime) {
		this.iqcLeadTime = iqcLeadTime;
	}

	public Long getIqcLeadTime() {
		return iqcLeadTime;
	}

	public void setVendorMaterial(VendorMaterial vendorMaterial) {
		this.vendorMaterial = vendorMaterial;
	}

	public VendorMaterial getVendorMaterial() {
		return vendorMaterial;
	}

	public BigDecimal getQtyMin() {
		return qtyMin;
	}

	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}

	public BigDecimal getQtyAllocation() {
		return qtyAllocation;
	}

	public void setQtyAllocation(BigDecimal qtyAllocation) {
		this.qtyAllocation = qtyAllocation;
	}

	public BigDecimal getQtyOnHand() {
		return qtyOnHand;
	}

	public void setQtyOnHand(BigDecimal qtyOnHand) {
		this.qtyOnHand = qtyOnHand;
	}

	public BigDecimal getQtyTransit() {
		return qtyTransit;
	}

	public void setQtyTransit(BigDecimal qtyTransit) {
		this.qtyTransit = qtyTransit;
	}

	public BigDecimal getQtyMoLineWip() {
		return qtyMoLineWip;
	}

	public void setQtyMoLineWip(BigDecimal qtyMoLineWip) {
		this.qtyMoLineWip = qtyMoLineWip;
	}

	public BigDecimal getQtySo() {
		return qtySo;
	}

	public void setQtySo(BigDecimal qtySo) {
		this.qtySo = qtySo;
	}

	public BigDecimal getQtyMinProduct() {
		return qtyMinProduct;
	}

	public void setQtyMinProduct(BigDecimal qtyMinProduct) {
		this.qtyMinProduct = qtyMinProduct;
	}

	public void setStandTime(BigDecimal standTime) {
		this.standTime = standTime;
	}

	public BigDecimal getStandTime() {
		return standTime;
	}

	public String getRealPath() {
		return realPath;
	}

	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}

	public Long getRealPathLevel() {
		return realPathLevel;
	}

	public void setRealPathLevel(Long realPathLevel) {
		this.realPathLevel = realPathLevel;
	}

	public Boolean getIsPrepareMoLine() {
		return "Y".equalsIgnoreCase(this.isPrepareMoLine) ? true : false; 
	}

	public void setIsPrepareMoLine(Boolean isPrepareMoLine) {
		this.isPrepareMoLine = isPrepareMoLine ? "Y" : "N";
	}

	public void setIsProduct(String isProduct) {
		this.isProduct = isProduct;
	}

	public Boolean getAgainGenMoLine() {
		return "Y".equalsIgnoreCase(this.againGenMoLine) ? true : false; 
	}

	public void setAgainGenMoLine(Boolean againGenMoLine) {
		this.againGenMoLine = againGenMoLine ? "Y" : "N";
	}

	public String getDbaMark() {
		return dbaMark;
	}

	public void setDbaMark(String dbaMark) {
		this.dbaMark = dbaMark;
	}

	public Boolean getIsSelfControl() {
		return "Y".equalsIgnoreCase(this.isSelfControl) ? true : false; 
	}

	public void setIsSelfControl(Boolean isSelfControl) {
		this.isSelfControl = isSelfControl ? "Y" : "N";
	}

	public String getMoMaterialName() {
		return moMaterialName;
	}

	public void setMoMaterialName(String moMaterialName) {
		this.moMaterialName = moMaterialName;
	}

	public String getMoMaterialId() {
		return moMaterialId;
	}

	public void setMoMaterialId(String moMaterialId) {
		this.moMaterialId = moMaterialId;
	}

	public String getMoMoId() {
		return moMoId;
	}

	public void setMoMoId(String moMoId) {
		this.moMoId = moMoId;
	}

	public String getPrepareType() {
		return prepareType;
	}

	public void setPrepareType(String prepareType) {
		this.prepareType = prepareType;
	}
}

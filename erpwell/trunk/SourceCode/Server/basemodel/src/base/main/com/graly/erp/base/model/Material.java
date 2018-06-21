package com.graly.erp.base.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="PDM_MATERIAL")
public class Material extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_ACTIVE = "Active";
	public static final String STATUS_INACTIVE = "InActive";
	
	public static final String CATEGORY_PRODUCT = "A";
	public static final String CATEGORY_SEMI = "B";
	public static final String CATEGORY_OTHER = "C";
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name="MATERIAL_CATEGORY1")
	private String materialCategory1;
	
	@Column(name="MATERIAL_CATEGORY2")
	private String materialCategory2;
	
	@Column(name="MATERIAL_CATEGORY3")
	private String materialCategory3;
	
	@Column(name="MATERIAL_CATEGORY4")
	private String materialCategory4;
	
	
	@Column(name="MATERIAL_CATEGORY5")
	private String materialCategory5;
	
	
	@Column(name="MATERIAL_CATEGORY6")
	private String materialCategory6;
	
	@Column(name="MATERIAL_TYPE")
	private String materialType;
	
	@Column(name="BOM_RRN")
	private Long bomRrn;
	
	@Column(name="BOM_ID")
	private String bomId;
	
	@Column(name="BOM_VERSION")
	private Long bomVersion;
	
	@Column(name="REFERNECT_PRICE")
	private BigDecimal refernectPrice;
	
	@Column(name="SPECIFICATION")
	private String specification;
	
	@Column(name="MODEL")
	private String model;
	
	@Column(name="WEIGHT")
	private String weight;
	
	@Column(name="VOLUME")
	private BigDecimal volume;
	
	@Column(name="INVENTORY_UOM")
	private String inventoryUom;
	
	@Column(name="PURCHASE_UOM")
	private String purchaseUom;
	
	@Column(name="PUR_INV_CONVERSION")
	private BigDecimal purInvConversion;
	
	@Column(name="QTY_MIN")
	private BigDecimal qtyMin;
	
	@Column(name="QTY_MAX")
	private BigDecimal qtyMax;
	
	@Column(name="INVENTORY_PROPERTY")
	private String inventoryProperty;
	
	@Column(name="ABC_CATEGORY")
	private String abcCategory;
	
	@Column(name="IS_LOT_CONTROL")
	private String isLotControl = "Y";
	
	@Column(name="LOT_TYPE")
	private String lotType;
	
	@Column(name="IS_JIT")
	private String isJit;
	
	@Column(name="IS_VIRTUAL")
	private String isVirtual = "N";//默认不是虚拟料
	
	@Column(name="IS_PURCHASE")
	private String isPurchase;
	
	@Column(name="IS_SALE")
	private String isSale;
	
	@Column(name="IS_PRODUCT")
	private String isProduct;
	
	@Column(name="IS_MRP")
	private String isMrp = "Y";
	
//	@Column(name="IS_ISSUE_MO")
//	private String isIssueMo;
		
	@Column(name="STOCK_CAPABILITY")
	private Long stockCapability;
	
	@Column(name="QTY_INITIAL")
	private BigDecimal qtyInitial = new BigDecimal("0.0");
	
	@Column(name="QTY_IN")
	private BigDecimal qtyIn = new BigDecimal("0.0");
	
	@Column(name="QTY_OUT")
	private BigDecimal qtyOut = new BigDecimal("0.0");
	
	@Column(name="QTY_DIFFERENCE")
	private BigDecimal qtyDifference = new BigDecimal("0.0");

	@Column(name="QTY_TRANSIT")
	private BigDecimal qtyTransit = new BigDecimal("0.0");
	
	@Column(name="QTY_ALLOCATION")
	private BigDecimal qtyAllocation = new BigDecimal("0.0");
	
	@Column(name="REFERENCE_DOC1")
	private String referenceDoc1;
	
	@Column(name="REFERENCE_DOC2")
	private String referenceDoc2;
	
	@Column(name="REFERENCE_DOC3")
	private String referenceDoc3;
	
	@Column(name="REFERENCE_DOC4")
	private String referenceDoc4;
	
	@Column(name="REFERENCE_DOC5")
	private String referenceDoc5;
	
	@Column(name="PRODUCTION_CYCLE")
	private String productionCycle;
	
	@Column(name="WORKCENTER_RRN")
	private Long workCenterRrn;
	
	@Transient
	private byte[] photo;  //@Column(name="PHOTO")
	
	@Column(name="BUYER_ID")
	private String buyerId;
	
	@Column(name="PLANNER_ID")
	private String plannerId;
	
	@Column(name="KEEPER_ID")
	private String keeperId;
	
	@Column(name="IQC_ID")
	private String iqcId;
	
	@Column(name="STAND_COST")
	private Long standCost;
	
	@Column(name="ACTRUAL_COST")
	private Long actrualCost;

	@Column(name="STATE")
	private String state;
	
	@Column(name="STAND_Time")
	private BigDecimal standTime;
	
	@Column(name="PROCESS_NAME")
	private String processName;
	
	@Column(name="QTY_MIN_PRODUCT")
	private BigDecimal qtyMinProduct;
	
	@Column(name="IS_SHARE")
	private String isShare;
	
	@Column(name="IQC_LEAD_TIME")
	private Long iqcLeadTime;
	
	@Column(name="BOM_PRICE")
	private BigDecimal bomPrice;
	
	@Column(name="APPLICANT")
	private String applicant;//申请人
	
	@Column(name="DATE_APPLY")//申请日期
	private Date dateApply = new Date();//默认为当前日期
	
	@Column(name="IS_INSPECTION_FREE")//是否免检,默认否
	private String isInspectionFree = "N";
	
	@Column(name="HAS_PURCHASED")
	private String hasPurchased = "N";//是否采购过，默认否
	
	@Transient
	private BigDecimal qtyOnHand;
	
	@Transient
	private BigDecimal qtyWriteOff;
	
	@Column(name="LENGTH")
	private BigDecimal length;
	
	@Column(name="WIDTH")
	private BigDecimal width;
	
	@Column(name="HEIGHT")
	private BigDecimal height;
	
	@Column(name="IS_VOLUME_BASIS")
	private String isVolumeBasis = "N";//是否作为体积计算的依据,默认是否
	
	@Column(name="COST_FORMULA")//成本公式
	private String costFormula;
	
	@Column(name="A_N_S") //Allow Negative Stocks
	private String ans = "Y";//default is yes
	
	@Column(name="LAST_PRICE")
	private BigDecimal lastPrice;//最近采购价，不分供应商，VendorMaterial中的最近采购价是分供应商的
	
	@Column(name="CHILD_MATERIAL_ID")
	private String childMaterialId;
	
	@Column(name="DELEGATION_COST")
	private BigDecimal delegationCost;
	
	@Column(name="PLAN_CATEGORY")
	private String planCategory;
	
	@Column(name="PACKAGE_SPEC")//包装规格
	private String packageSpec;
	
	@Column(name="MOULD_RRN")//模具型号
	private Long mouldRrn;
	
	@Column(name="INSPECT_CYCLE")//检验周期
	private BigDecimal inspectCycle;
	
	@Column(name="IS_HIM")//库存高位监控
	private String isHIM ="N";
	
	@Column(name="QTY_MIN_SERVICE")
	private BigDecimal qtyMinService;//服务公司最小安全库存
	
	@Transient
	private String hasBom;
	
	@Transient
	private String promised;
	
	@Transient
	private String alarmLevel;//报警程度
	
	@Transient
	private BigDecimal totalPrice;//总价
	
	@Column(name="MATERIAL_VERSION")//8位版本
	private String materialVersion;
	
	@Column(name="MATERIAL_CATEGORY7")
	private String materialCategory7;//小谢要求新增分类
	
	@Column(name="MATERIAL_CATEGORY8")
	private String materialCategory8;//小谢要求新增分类
	
	@Column(name="MATERIAL_CATEGORY9")
	private String materialCategory9;//小谢要求新增分类
	
	@Column(name="MATERIAL_CATEGORY10")
	private String materialCategory10;//小谢要求新增分类
	
	@Column(name="MATERIAL_CATEGORY11")
	private String materialCategory11;//小谢要求新增分类
	
	@Column(name="IQC_DEPT")//质检部门
	private String iqcDept;
	
	@Column(name="METER")
	private BigDecimal meter;//范总节拍
	
	@Column(name = "IS_WMSHOUSE")
	private String isWmshouse;//是否WMS仓库
	
	@Column(name="IS_THROUGH")
	private String isThrough;//一键将7、8、9、10、11分类设置为空
	
	@Column(name="RO1")
	private String ro1;

	@Column(name="RO2")
	private String ro2;
	
	@Column(name="MATERIAL_CATEGORY12")
	private String materialCategory12;//小谢要求新增fenlei yanse
	
	public String getPlanCategory() {
		return planCategory;
	}
	
	public void setPlanCategory(String planCategory) {
		this.planCategory = planCategory;
	}
	
	public String getPromised() {
		return promised;
	}

	public void setPromised(String promised) {
		this.promised = promised;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getMaterialCategory1() {
		return materialCategory1;
	}

	public void setMaterialCategory(String materialCategory1) {
		this.materialCategory1 = materialCategory1;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public Long getBomRrn() {
		return bomRrn;
	}

	public void setBomRrn(Long bomRrn) {
		this.bomRrn = bomRrn;
	}

	public void setBomId(String bomId) {
		this.bomId = bomId;
	}

	public String getBomId() {
		return bomId;
	}
	
	public void setBomVersion(Long bomVersion) {
		this.bomVersion = bomVersion;
	}

	public Long getBomVersion() {
		return bomVersion;
	}
	
	public BigDecimal getRefernectPrice() {
		return refernectPrice;
	}

	public void setRefernectPrice(BigDecimal refernectPrice) {
		this.refernectPrice = refernectPrice;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public String getInventoryUom() {
		return inventoryUom;
	}

	public void setInventoryUom(String inventoryUom) {
		this.inventoryUom = inventoryUom;
	}

	public String getPurchaseUom() {
		return purchaseUom;
	}

	public void setPurchaseUom(String purchaseUom) {
		this.purchaseUom = purchaseUom;
	}

	public void setPurInvConversion(BigDecimal purInvConversion) {
		this.purInvConversion = purInvConversion;
	}

	public BigDecimal getPurInvConversion() {
		return purInvConversion;
	}
	
	public String getInventoryProperty() {
		return inventoryProperty;
	}

	public void setInventoryProperty(String inventoryProperty) {
		this.inventoryProperty = inventoryProperty;
	}

	public String getAbcCategory() {
		return abcCategory;
	}

	public void setAbcCategory(String abcCategory) {
		this.abcCategory = abcCategory;
	}

	public Boolean getIsJit(){
		return "Y".equalsIgnoreCase(this.isJit) ? true : false; 
	}

	public void setIsJit(Boolean isJit) {
		this.isJit = isJit ? "Y" : "N";
	}

	public Boolean getIsVirtual(){
		return "Y".equalsIgnoreCase(this.isVirtual) ? true : false; 
	}

	public void setIsVirtual(Boolean isVirtual) {
		this.isVirtual = isVirtual ? "Y" : "N";
	}

	public Boolean getIsPurchase(){
		return "Y".equalsIgnoreCase(this.isPurchase) ? true : false; 
	}

	public void setIsPurchase(Boolean isPurchase) {
		this.isPurchase = isPurchase ? "Y" : "N";
	}
	
	public Boolean getIsSale(){
		return "Y".equalsIgnoreCase(this.isSale) ? true : false; 
	}

	public void setIsSale(Boolean isSale) {
		this.isSale = isSale ? "Y" : "N";
	}
	
	public Boolean getIsProduct(){
		return "Y".equalsIgnoreCase(this.isProduct) ? true : false; 
	}

	public void setIsProduct(Boolean isProduct) {
		this.isProduct = isProduct ? "Y" : "N";
	}

	public Boolean getIsMrp(){
		return "Y".equalsIgnoreCase(this.isMrp) ? true : false; 
	}

	public void setIsMrp(Boolean isMrp) {
		this.isMrp = isMrp ? "Y" : "N";
	}
	
//	public Boolean getIsIssueMo(){
//		return "Y".equalsIgnoreCase(this.isIssueMo) ? true : false; 
//	}
//
//	public void setIsIssueMo(Boolean isIssueMo) {
//		this.isIssueMo = isIssueMo ? "Y" : "N";
//	}

	public Long getStockCapability() {
		return stockCapability;
	}

	public void setStockCapability(Long stockCapability) {
		this.stockCapability = stockCapability;
	}

	public BigDecimal getQtyMin() {
		return qtyMin;
	}

	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}

	public BigDecimal getQtyMax() {
		return qtyMax;
	}

	public void setQtyMax(BigDecimal qtyMax) {
		this.qtyMax = qtyMax;
	}

	public BigDecimal getQtyInitial() {
		return qtyInitial;
	}

	public void setQtyInitial(BigDecimal qtyInitial) {
		this.qtyInitial = qtyInitial;
	}

	public BigDecimal getQtyIn() {
		return qtyIn;
	}

	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
	}

	public BigDecimal getQtyOut() {
		return qtyOut;
	}

	public void setQtyOut(BigDecimal qtyOut) {
		this.qtyOut = qtyOut;
	}

	public BigDecimal getQtyDifference() {
		return qtyDifference;
	}

	public void setQtyDifference(BigDecimal qtyDifference) {
		this.qtyDifference = qtyDifference;
	}

	public BigDecimal getQtyTransit() {
		return qtyTransit;
	}

	public void setQtyTransit(BigDecimal qtyTransit) {
		this.qtyTransit = qtyTransit;
	}

	public BigDecimal getQtyAllocation() {
		return qtyAllocation;
	}

	public void setQtyAllocation(BigDecimal qtyAllocation) {
		this.qtyAllocation = qtyAllocation;
	}

	public String getReferenceDoc1() {
		return referenceDoc1;
	}

	public void setReferenceDoc1(String referenceDoc1) {
		this.referenceDoc1 = referenceDoc1;
	}

	public String getReferenceDoc2() {
		return referenceDoc2;
	}

	public void setReferenceDoc2(String referenceDoc2) {
		this.referenceDoc2 = referenceDoc2;
	}

	public String getReferenceDoc3() {
		return referenceDoc3;
	}

	public void setReferenceDoc3(String referenceDoc3) {
		this.referenceDoc3 = referenceDoc3;
	}

	public String getReferenceDoc4() {
		return referenceDoc4;
	}

	public void setReferenceDoc4(String referenceDoc4) {
		this.referenceDoc4 = referenceDoc4;
	}

	public String getReferenceDoc5() {
		return referenceDoc5;
	}

	public void setReferenceDoc5(String referenceDoc5) {
		this.referenceDoc5 = referenceDoc5;
	}

	public String getProductionCycle() {
		return productionCycle;
	}

	public void setProductionCycle(String productionCycle) {
		this.productionCycle = productionCycle;
	}

	public Long getWorkCenterRrn() {
		return workCenterRrn;
	}

	public void setWorkCenterRrn(Long workCenterRrn) {
		this.workCenterRrn = workCenterRrn;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public String getPlannerId() {
		return plannerId;
	}

	public void setPlannerId(String plannerId) {
		this.plannerId = plannerId;
	}

	public String getKeeperId() {
		return keeperId;
	}

	public void setKeeperId(String keeperId) {
		this.keeperId = keeperId;
	}

	public String getIqcId() {
		return iqcId;
	}

	public void setIqcId(String iqcId) {
		this.iqcId = iqcId;
	}

	public Long getStandCost() {
		return standCost;
	}

	public void setStandCost(Long standCost) {
		this.standCost = standCost;
	}

	public Long getActrualCost() {
		return actrualCost;
	}

	public void setActrualCost(Long actrualCost) {
		this.actrualCost = actrualCost;
	}
	
	public Boolean getIsLotControl(){
		return "Y".equalsIgnoreCase(this.isLotControl) ? true : false; 
	}
	
	public void setIsLotControl(Boolean isLotControl) {
		this.isLotControl = isLotControl ? "Y" : "N";
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setStandTime(BigDecimal standTime) {
		this.standTime = standTime;
	}

	public BigDecimal getStandTime() {
		return standTime;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public BigDecimal getQtyMinProduct() {
		return qtyMinProduct;
	}

	public void setQtyMinProduct(BigDecimal qtyMinProduct) {
		this.qtyMinProduct = qtyMinProduct;
	}
	
	public Boolean getIsShare(){
		return "Y".equalsIgnoreCase(this.isShare) ? true : false; 
	}
	
	public void setIsShare(Boolean isShare) {
		this.isShare = isShare ? "Y" : "N";
	}

	public String getMaterialCategory2() {
		return materialCategory2;
	}

	public void setMaterialCategory2(String materialCategory2) {
		this.materialCategory2 = materialCategory2;
	}

	public String getMaterialCategory3() {
		return materialCategory3;
	}

	public void setMaterialCategory3(String materialCategory3) {
		this.materialCategory3 = materialCategory3;
	}

	public String getMaterialCategory4() {
		return materialCategory4;
	}

	public void setMaterialCategory4(String materialCategory4) {
		this.materialCategory4 = materialCategory4;
	}

	public void setMaterialCategory1(String materialCategory1) {
		this.materialCategory1 = materialCategory1;
	}

	public void setIqcLeadTime(Long iqcLeadTime) {
		this.iqcLeadTime = iqcLeadTime;
	}

	public Long getIqcLeadTime() {
		return iqcLeadTime;
	}

	public BigDecimal getBomPrice() {
		return bomPrice;
	}

	public void setBomPrice(BigDecimal bomPrice) {
		this.bomPrice = bomPrice;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public Date getDateApply() {
		return dateApply;
	}

	public void setDateApply(Date dateApply) {
		this.dateApply = dateApply;
	}

	public Boolean getIsInspectionFree() {
		return "Y".equalsIgnoreCase(isInspectionFree) ? true : false;
	}

	public void setIsInspectionFree(Boolean isInspectionFree) {
		this.isInspectionFree = isInspectionFree ? "Y" : "N";
	}

	public String getMaterialCategory6() {
		return materialCategory6;
	}

	public void setMaterialCategory6(String materialCategory6) {
		this.materialCategory6 = materialCategory6;
	}

	public String getMaterialCategory5() {
		return materialCategory5;
	}

	public void setMaterialCategory5(String materialCategory5) {
		this.materialCategory5 = materialCategory5;
	}

	public BigDecimal getQtyOnHand() {
		return qtyOnHand;
	}

	public void setQtyOnHand(BigDecimal qtyOnHand) {
		this.qtyOnHand = qtyOnHand;
	}

	public BigDecimal getQtyWriteOff() {
		return qtyWriteOff;
	}

	public void setQtyWriteOff(BigDecimal qtyWriteOff) {
		this.qtyWriteOff = qtyWriteOff;
	}

	public Boolean getHasPurchased() {
		return "Y".equalsIgnoreCase(hasPurchased);
	}

	public void setHasPurchased(Boolean hasPurchased) {
		this.hasPurchased = (hasPurchased ? "Y" : "N");
	}

	@Transient
	public void setCopyableMaterialId(String copyableMaterialId) {//为了能让materialId可复制，因为相同的字段不能在同一页面上使用两次，故加此栏位
	}
	
	@Transient
	public String getCopyableMaterialId() {//为了能让materialId可复制，因为相同的字段不能在同一页面上使用两次，故加此栏位
		return materialId;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public Boolean getIsVolumeBasis() {
		return "Y".equalsIgnoreCase(isVolumeBasis);
	}

	public void setIsVolumeBasis(Boolean isVolumeBasis) {
		this.isVolumeBasis = ( isVolumeBasis ? "Y" : "N" );
	}

	public String getCostFormula() {
		return costFormula;
	}

	public void setCostFormula(String costFormula) {
		this.costFormula = costFormula;
	}

	public void setIsLotControl(String isLotControl) {
		this.isLotControl = isLotControl;
	}

	public void setIsJit(String isJit) {
		this.isJit = isJit;
	}

	public void setIsVirtual(String isVirtual) {
		this.isVirtual = isVirtual;
	}

	public void setIsPurchase(String isPurchase) {
		this.isPurchase = isPurchase;
	}

	public void setIsSale(String isSale) {
		this.isSale = isSale;
	}

	public void setIsProduct(String isProduct) {
		this.isProduct = isProduct;
	}

	public void setIsMrp(String isMrp) {
		this.isMrp = isMrp;
	}

	public void setIsShare(String isShare) {
		this.isShare = isShare;
	}

	public void setIsInspectionFree(String isInspectionFree) {
		this.isInspectionFree = isInspectionFree;
	}

	public void setHasPurchased(String hasPurchased) {
		this.hasPurchased = hasPurchased;
	}

	public void setIsVolumeBasis(String isVolumeBasis) {
		this.isVolumeBasis = isVolumeBasis;
	}

	public Boolean getAns() {
		return "Y".equalsIgnoreCase(ans);
	}

	public void setAns(Boolean ans) {
		this.ans = ( ans ? "Y" : "N" );
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public void setAns(String ans) {
		this.ans = ans;
	}

	public String getChildMaterialId() {
		return childMaterialId;
	}

	public void setChildMaterialId(String childMaterialId) {
		this.childMaterialId = childMaterialId;
	}

	public BigDecimal getDelegationCost() {
		return delegationCost;
	}

	public void setDelegationCost(BigDecimal delegationCost) {
		this.delegationCost = delegationCost;
	}

	public boolean isHasBom() {
		return "Y".equalsIgnoreCase(hasBom);
	}

	public void setHasBom(boolean hasBom) {
		this.hasBom = hasBom?"Y":"N";
	}

	public String getPackageSpec() {
		return packageSpec;
	}

	public void setPackageSpec(String packageSpec) {
		this.packageSpec = packageSpec;
	}

	public Long getMouldRrn() {
		return mouldRrn;
	}

	public void setMouldRrn(Long mouldRrn) {
		this.mouldRrn = mouldRrn;
	}

	public BigDecimal getInspectCycle() {
		return inspectCycle;
	}

	public void setInspectCycle(BigDecimal inspectCycle) {
		this.inspectCycle = inspectCycle;
	}

	public Boolean getIsHIM() {
		return "Y".equalsIgnoreCase(isHIM) ? true : false;
	}

	public void setIsHIM(Boolean isHIM) {
		this.isHIM = isHIM ? "Y" : "N";
	}
	public void setIsHIM(String isHIM) {
		this.isHIM = isHIM;
	}

	public String getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getQtyMinService() {
		return qtyMinService;
	}

	public void setQtyMinService(BigDecimal qtyMinService) {
		this.qtyMinService = qtyMinService;
	}

	public String getMaterialVersion() {
		return materialVersion;
	}

	public void setMaterialVersion(String materialVersion) {
		this.materialVersion = materialVersion;
	}

	public String getMaterialCategory7() {
		return materialCategory7;
	}

	public void setMaterialCategory7(String materialCategory7) {
		this.materialCategory7 = materialCategory7;
	}

	public String getMaterialCategory8() {
		return materialCategory8;
	}

	public void setMaterialCategory8(String materialCategory8) {
		this.materialCategory8 = materialCategory8;
	}

	public String getMaterialCategory9() {
		return materialCategory9;
	}

	public void setMaterialCategory9(String materialCategory9) {
		this.materialCategory9 = materialCategory9;
	}

	public String getIqcDept() {
		return iqcDept;
	}

	public void setIqcDept(String iqcDept) {
		this.iqcDept = iqcDept;
	}

	public BigDecimal getMeter() {
		return meter;
	}

	public void setMeter(BigDecimal meter) {
		this.meter = meter;
	}

	public Boolean getIsWmshouse() {
		return "Y".equalsIgnoreCase(this.isWmshouse) ? true : false;
	}

	public void setIsWmshouse(Boolean isWmshouse) {
		this.isWmshouse = isWmshouse ? "Y" : "N";
	}

	public String getMaterialCategory10() {
		return materialCategory10;
	}

	public void setMaterialCategory10(String materialCategory10) {
		this.materialCategory10 = materialCategory10;
	}

	public String getMaterialCategory11() {
		return materialCategory11;
	}

	public void setMaterialCategory11(String materialCategory11) {
		this.materialCategory11 = materialCategory11;
	}
	
	public Boolean getIsThrough(){
		return "Y".equalsIgnoreCase(this.isThrough) ? true : false; 
	}

	public void setIsThrough(Boolean isThrough) {
		this.isThrough = isThrough ? "Y" : "N";
	}

	public String getRo1() {
		return ro1;
	}

	public void setRo1(String ro1) {
		this.ro1 = ro1;
	}

	public String getRo2() {
		return ro2;
	}

	public void setRo2(String ro2) {
		this.ro2 = ro2;
	}

	public String getMaterialCategory12() {
		return materialCategory12;
	}

	public void setMaterialCategory12(String materialCategory12) {
		this.materialCategory12 = materialCategory12;
	}
}

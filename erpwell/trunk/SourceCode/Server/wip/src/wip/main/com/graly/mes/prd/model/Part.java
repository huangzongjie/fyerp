package com.graly.mes.prd.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.mes.prd.workflow.context.def.WFParameter;

@Entity
@Table(name = "PRD_PART")
public class Part extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	public static final String STATUS_FROZNE = "Frozen";
	public static final String STATUS_UNFROZNE = "UnFrozen";
	public static final String STATUS_ACTIVE = "Active";
	public static final String STATUS_INACTIVE = "InActive";

	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="VERSION")
	private Long version;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="ENG_OWNER")
	private String engOwner;

	@Column(name = "PART_TYPE")
	private String partType;

	@Column(name = "PROCESS_RRN")
	private Long processRrn;

	@Column(name = "PROCESS_NAME")
	private String processName;

	@Column(name = "PROCESS_VERSION")
	private Long processVersion;

	@Column(name="CUSTOMER_NAME")
	private String customerName;
	
	@Column(name="MATERIAL_TYPE")
	private String materialType;
	
	@Column(name="ALT_MATERIAL_TYPE1")
	private String altMaterialType1;
	
	@Column(name="ALT_MATERIAL_TYPE2")
	private String altMaterialType2;
	
	@Column(name = "TCARD_RRN")
	private Long tcardRrn;

	@Column(name = "ALT_TCARD_RRN")
	private Long altTcardRrn;

	@Column(name="STD_SIZE")
	private String stdSize;

	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name = "PART_SPEC1")
	private String partSpec1;
	
	@Column(name = "PART_SPEC2")
	private String partSpec2;
	
	@Column(name = "PART_SPEC3")
	private String partSpec3;
	
	@Column(name = "PART_SPEC4")
	private String partSpec4;
	
	@Column(name = "PART_SPEC5")
	private String partSpec5;
	
	@Column(name = "PART_SPEC6")
	private String partSpec6;
	
	@Column(name = "PART_SPEC7")
	private String partSpec7;
	
	@Column(name = "PART_SPEC8")
	private String partSpec8;
	
	@Column(name = "PART_SPEC9")
	private String partSpec9;
	
	@Column(name = "PART_SPEC10")
	private String partSpec10;
	
	@Column(name = "PART_SPEC11")
	private String partSpec11;
	
	@Column(name = "PART_SPEC12")
	private String partSpec12;
	
	@Column(name = "PART_SPEC13")
	private String partSpec13;
	
	@Column(name = "PART_SPEC14")
	private String partSpec14;
	
	@Column(name = "PART_SPEC15")
	private String partSpec15;
	
	@Column(name = "PART_SPEC16")
	private String partSpec16;
	
	@Column(name = "PART_SPEC17")
	private String partSpec17;
	
	@Column(name = "PART_SPEC18")
	private String partSpec18;
	
	@Column(name = "PART_SPEC19")
	private String partSpec19;
	
	@Column(name = "PART_SPEC20")
	private String partSpec20;

	@Column(name = "PART_NAME1")
	private String partName1;
	
	@Column(name = "PART_NAME2")
	private String partName2;
	
	@Column(name = "PART_NAME3")
	private String partName3;
	
	@Column(name = "PART_NAME4")
	private String partName4;
	
	@Column(name = "PART_NAME5")
	private String partName5;
	
	@Column(name = "PART_NAME6")
	private String partName6;
	
	@Column(name = "PART_NAME7")
	private String partName7;
	
	@Column(name = "PART_NAME8")
	private String partName8;
	
	@Column(name = "PART_NAME9")
	private String partName9;
	
	@Column(name = "PART_NAME10")
	private String partName10;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "seqNo ASC")
	@JoinColumn(name = "PART_RRN", referencedColumnName = "OBJECT_RRN")
	protected List<WFParameter> wfParameters = null;

	@Transient
	protected List<Parameter> parameters;
	
	public Part() {
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getVersion() {
		return version;
	}
	
	public String getPartId() {
		return name + "." + version;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	public void setEngOwner(String engOwner) {
		this.engOwner = engOwner;
	}

	public String getEngOwner() {
		return engOwner;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}
	
	public String getPartType() {
		return partType;
	}

	public void setPartType(String partType) {
		this.partType = partType;
	}

	public Long getProcessRrn() {
		return processRrn;
	}

	public void setProcessRrn(Long processRrn) {
		this.processRrn = processRrn;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Long getProcessVersion() {
		return processVersion;
	}

	public void setProcessVersion(Long processVersion) {
		this.processVersion = processVersion;
	}
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setStdSize(String stdSize) {
		this.stdSize = stdSize;
	}

	public String getStdSize() {
		return stdSize;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setAltMaterialType1(String altMaterialType1) {
		this.altMaterialType1 = altMaterialType1;
	}

	public String getAltMaterialType1() {
		return altMaterialType1;
	}

	public void setAltMaterialType2(String altMaterialType2) {
		this.altMaterialType2 = altMaterialType2;
	}

	public String getAltMaterialType2() {
		return altMaterialType2;
	}

	public void setTcardRrn(Long tcardRrn) {
		this.tcardRrn = tcardRrn;
	}

	public Long getTcardRrn() {
		return tcardRrn;
	}
	
	public void setAltTcardRrn(Long altTcardRrn) {
		this.altTcardRrn = altTcardRrn;
	}

	public Long getAltTcardRrn() {
		return altTcardRrn;
	}
	
	public String getPartSpec1() {
		return partSpec1;
	}

	public void setPartSpec1(String partSpec1) {
		this.partSpec1 = partSpec1;
	}

	public String getPartSpec2() {
		return partSpec2;
	}

	public void setPartSpec2(String partSpec2) {
		this.partSpec2 = partSpec2;
	}

	public String getPartSpec3() {
		return partSpec3;
	}

	public void setPartSpec3(String partSpec3) {
		this.partSpec3 = partSpec3;
	}

	public String getPartSpec4() {
		return partSpec4;
	}

	public void setPartSpec4(String partSpec4) {
		this.partSpec4 = partSpec4;
	}

	public String getPartSpec5() {
		return partSpec5;
	}

	public void setPartSpec5(String partSpec5) {
		this.partSpec5 = partSpec5;
	}

	public String getPartSpec6() {
		return partSpec6;
	}

	public void setPartSpec6(String partSpec6) {
		this.partSpec6 = partSpec6;
	}

	public String getPartSpec7() {
		return partSpec7;
	}

	public void setPartSpec7(String partSpec7) {
		this.partSpec7 = partSpec7;
	}

	public String getPartSpec8() {
		return partSpec8;
	}

	public void setPartSpec8(String partSpec8) {
		this.partSpec8 = partSpec8;
	}

	public String getPartSpec9() {
		return partSpec9;
	}

	public void setPartSpec9(String partSpec9) {
		this.partSpec9 = partSpec9;
	}

	public String getPartSpec10() {
		return partSpec10;
	}

	public void setPartSpec10(String partSpec10) {
		this.partSpec10 = partSpec10;
	}

	public String getPartSpec11() {
		return partSpec11;
	}

	public void setPartSpec11(String partSpec11) {
		this.partSpec11 = partSpec11;
	}

	public String getPartSpec12() {
		return partSpec12;
	}

	public void setPartSpec12(String partSpec12) {
		this.partSpec12 = partSpec12;
	}

	public String getPartSpec13() {
		return partSpec13;
	}

	public void setPartSpec13(String partSpec13) {
		this.partSpec13 = partSpec13;
	}

	public String getPartSpec14() {
		return partSpec14;
	}

	public void setPartSpec14(String partSpec14) {
		this.partSpec14 = partSpec14;
	}

	public String getPartSpec15() {
		return partSpec15;
	}

	public void setPartSpec15(String partSpec15) {
		this.partSpec15 = partSpec15;
	}

	public String getPartSpec16() {
		return partSpec16;
	}

	public void setPartSpec16(String partSpec16) {
		this.partSpec16 = partSpec16;
	}

	public String getPartSpec17() {
		return partSpec17;
	}

	public void setPartSpec17(String partSpec17) {
		this.partSpec17 = partSpec17;
	}
	
	public String getPartSpec18() {
		return partSpec18;
	}

	public void setPartSpec18(String partSpec18) {
		this.partSpec18 = partSpec18;
	}
	
	public String getPartSpec19() {
		return partSpec19;
	}

	public void setPartSpec19(String partSpec19) {
		this.partSpec19 = partSpec19;
	}

	public String getPartSpec20() {
		return partSpec20;
	}

	public void setPartSpec20(String partSpec20) {
		this.partSpec20 = partSpec20;
	}

	public String getPartName1() {
		return partName1;
	}

	public void setPartName1(String partName1) {
		this.partName1 = partName1;
	}

	public String getPartName2() {
		return partName2;
	}

	public void setPartName2(String partName2) {
		this.partName2 = partName2;
	}

	public String getPartName3() {
		return partName3;
	}

	public void setPartName3(String partName3) {
		this.partName3 = partName3;
	}

	public String getPartName4() {
		return partName4;
	}

	public void setPartName4(String partName4) {
		this.partName4 = partName4;
	}

	public String getPartName5() {
		return partName5;
	}

	public void setPartName5(String partName5) {
		this.partName5 = partName5;
	}

	public String getPartName6() {
		return partName6;
	}

	public void setPartName6(String partName6) {
		this.partName6 = partName6;
	}

	public String getPartName7() {
		return partName7;
	}

	public void setPartName7(String partName7) {
		this.partName7 = partName7;
	}

	public String getPartName8() {
		return partName8;
	}

	public void setPartName8(String partName8) {
		this.partName8 = partName8;
	}

	public String getPartName9() {
		return partName9;
	}

	public void setPartName9(String partName9) {
		this.partName9 = partName9;
	}

	public String getPartName10() {
		return partName10;
	}

	public void setPartName10(String partName10) {
		this.partName10 = partName10;
	}

	public void setWfParameters(List<WFParameter> wfParameters) {
		if (wfParameters != null) {
			for (int i = 0; i < wfParameters.size(); i++) {
				wfParameters.get(i).setSeqNo(i);
			}
		}
		this.wfParameters = wfParameters;
	}

	public List<WFParameter> getWfParameters() {
		return wfParameters;
	}
	
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}
}
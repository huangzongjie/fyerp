package com.graly.framework.activeentity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="AD_FIELD")
public class ADField extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="TABLE_RRN")
	private Long tableRrn; 
	
	@Column(name="TAB_RRN")
	private Long tabRrn; 

	@Column(name="SEQ_NO")
	private Long seqNo; 
	
	@Column(name="DISPLAY_LENGTH")
	private Long displayLength;
	
	@Column(name="IS_DISPLAY")
	private String isDisplay;
	
	@Column(name="IS_KEY")
	private String isKey;

	@Column(name="IS_MAIN")
	private String isMain;

	@Column(name="IS_QUERY")
	private String isQuery;
	
	@Column(name="IS_ADVANCE_QUERY")
	private String isAdvanceQuery;
	
	@Column(name="IS_READONLY")
	private String isReadonly;
	
	@Column(name="IS_EDITABLE")
	private String isEditable;
	
	@Column(name="IS_SAMELINE")
	private String isSameline;
	
	@Column(name="IS_MANDATORY")
	private String isMandatory;
	
	@Column(name="IS_UPPER")
	private String isUpper;
	
	@Column(name="IS_PARENT")
	private String isParent;
	
	@Column(name="IS_TABLE_FIELD")
	private String isTableField;
	
	@Column(name="DISPLAY_TYPE")
	private String displayType;
	
	@Column(name="DATA_TYPE")
	private String dataType;
	
	@Column(name="MIN_VALUE")
	private String minValue;
	
	@Column(name="MAX_VALUE")
	private String maxValue;

	@Column(name="NAMING_RULE")
	private String namingRule; 

	@Column(name="REFERENCE_NAME")
	private String referenceName; 
	
	@Column(name="REFTABLE_RRN")
	private Long reftableRrn;
	
	@Column(name="USER_REFLIST_NAME")
	private String userReflistName;

	@Column(name="REFERENCE_RULE")
	private String referenceRule; 
	
	@Column(name="LABEL")
	private String label;
	
	@Column(name="LABEL_ZH")
	private String label_zh;

	@ManyToOne
	@JoinColumn(name = "TABLE_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private ADTable table;
	
	@ManyToOne
	@JoinColumn(name = "TAB_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private ADTab tab;
	
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

	public void setTableRrn(Long tableRrn) {
		this.tableRrn = tableRrn;
	}

	public Long getTableRrn() {
		return tableRrn;
	}
	
	public void setTabRrn(Long tabRrn) {
		this.tabRrn = tabRrn;
	}

	public Long getTabRrn() {
		return tabRrn;
	}
	
	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public Long getSeqNo() {
		return seqNo;
	}

	public void setDisplayLength(Long displayLength) {
		this.displayLength = displayLength;
	}

	public Long getDisplayLength() {
		return displayLength;
	}
	
	public void setIsDisplay(Boolean isDisplay) {
		this.isDisplay = isDisplay ? "Y" : "N";
	}

	public Boolean getIsDisplay() {
		return "Y".equalsIgnoreCase(this.isDisplay) ? true : false; 
	}
	
	public void setIsKey(Boolean isKey) {
		this.isKey = isKey ? "Y" : "N";
	}

	public Boolean getIsKey() {
		return "Y".equalsIgnoreCase(this.isKey) ? true : false; 
	}

	public void setIsMain(Boolean isMain) {
		this.isMain = isMain ? "Y" : "N";
	}

	public Boolean getIsMain() {
		return "Y".equalsIgnoreCase(this.isMain) ? true : false; 
	}
	
	public void setIsQuery(Boolean isQuery) {
		this.isQuery = isQuery ? "Y" : "N";
	}

	public Boolean getIsQuery() {
		return "Y".equalsIgnoreCase(this.isQuery) ? true : false; 
	}
	
	public void setIsReadonly(Boolean isReadonly) {
		this.isReadonly = isReadonly ? "Y" : "N";
	}

	public Boolean getIsReadonly() {
		return "Y".equalsIgnoreCase(this.isReadonly) ? true : false; 
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable ? "Y" : "N";
	}

	public Boolean getIsEditable() {
		return "Y".equalsIgnoreCase(this.isEditable) ? true : false; 
	}
	
	public void setIsSameline(Boolean isSameline) {
		this.isSameline = isSameline ? "Y" : "N";
	}

	public Boolean getIsSameline() {
		return "Y".equalsIgnoreCase(this.isSameline) ? true : false; 
	}

	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory ? "Y" : "N";
	}

	public Boolean getIsMandatory() {
		return "Y".equalsIgnoreCase(this.isMandatory) ? true : false; 
	}
	
	public void setIsUpper(Boolean isUpper) {
		this.isUpper = isUpper ? "Y" : "N";
	}

	public Boolean getIsParent() {
		return "Y".equalsIgnoreCase(this.isParent) ? true : false; 
	}
	
	public void setIsParent(Boolean isParent) {
		this.isParent = isParent ? "Y" : "N";
	}

	public Boolean getIsUpper() {
		return "Y".equalsIgnoreCase(this.isUpper) ? true : false; 
	}
	
	public void setIsTableField(Boolean isTableField) {
		this.isTableField = isTableField ? "Y" : "N";
	}

	public Boolean getIsTableField() {
		return "Y".equalsIgnoreCase(this.isTableField) ? true : false; 
	}
	
	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public String getDisplayType() {
		return displayType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataType() {
		return dataType;
	}
	
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setNamingRule(String namingRule) {
		this.namingRule = namingRule;
	}

	public String getNamingRule() {
		return namingRule;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getReferenceName() {
		return referenceName;
	}
	
	public void setReftableRrn(Long reftableRrn) {
		this.reftableRrn = reftableRrn;
	}

	public Long getReftableRrn() {
		return reftableRrn;
	}
	
	public String getUserReflistName() {
		return userReflistName;
	}

	public void setUserReflistName(String userReflistName) {
		this.userReflistName = userReflistName;
	}

	public void setReferenceRule(String referenceRule) {
		this.referenceRule = referenceRule;
	}

	public String getReferenceRule() {
		return referenceRule;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel_zh(String label_zh) {
		this.label_zh = label_zh;
	}

	public String getLabel_zh() {
		return label_zh;
	}

	public void setTable(ADTable table) {
		this.table = table;
	}

	public ADTable getTable() {
		return table;
	}
	
	public void setTab(ADTab tab) {
		this.tab = tab;
	}

	public ADTab getTab() {
		return tab;
	}

	public Boolean getIsAdvanceQuery() {
		return "Y".equals(isAdvanceQuery) ? true : false;
	}

	public void setIsAdvanceQuery(Boolean isAdvanceQuery) {
		this.isAdvanceQuery = isAdvanceQuery ? "Y" : "N";
	}

}

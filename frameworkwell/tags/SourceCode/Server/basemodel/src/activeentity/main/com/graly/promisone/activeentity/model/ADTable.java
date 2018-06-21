package com.graly.promisone.activeentity.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="AD_TABLE")
public class ADTable extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="MODEL_NAME")
	private String modelName;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="TABLE_NAME")
	private String tableName;
	
	@Column(name="IS_VIEW")
	private String isView;
	
	@Column(name="AD_VAL_RULE_ID")
	private String valRuleId;
	
	@Column(name="MODEL_CLASS")
	private String modelClass;

	@Column(name="WHERE_CLAUSE")
	private String whereClause;

	@Column(name="ORDER_BY_CLAUSE")
	private String orderByClause;

	@Column(name="LABEL")
	private String label;
	
	@Column(name="LABEL_ZH")
	private String label_zh;
	
	@OneToMany(mappedBy = "table", fetch=FetchType.LAZY,
			targetEntity = com.graly.promisone.activeentity.model.ADTab.class)
	@OrderBy(value = "seqNo ASC")
	private List<ADTab> tabs;
	
	@OneToMany(mappedBy = "table", fetch=FetchType.LAZY, 
			targetEntity = com.graly.promisone.activeentity.model.ADField.class)
	@OrderBy(value = "seqNo ASC")
	private List<ADField> fields;
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setIsView(String isView) {
		this.isView = isView;
	}

	public String getIsView() {
		return isView;
	}

	public void setValRuleId(String valRuleId) {
		this.valRuleId = valRuleId;
	}

	public String getValRuleId() {
		return valRuleId;
	}

	public void setModelClass(String modelClass) {
		this.modelClass = modelClass;
	}

	public String getModelClass() {
		return modelClass;
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
	
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	public String getOrderByClause() {
		return orderByClause;
	}
	
	public void setTabs(List<ADTab> tabs) {
		this.tabs = tabs;
	}

	public List<ADTab> getTabs() {
		return tabs;
	}

	public void setFields(List<ADField> fields) {
		this.fields = fields;
	}

	public List<ADField> getFields() {
		return fields;
	}


}

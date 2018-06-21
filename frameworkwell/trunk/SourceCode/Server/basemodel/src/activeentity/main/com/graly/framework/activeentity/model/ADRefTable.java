package com.graly.framework.activeentity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="AD_REFTABLE")
public class ADRefTable extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="TABLE_RRN")
	private Long tableRrn;
	
	@Column(name="KEY_FIELD")
	private String keyField;
	
	@Column(name="VALUE_FIELD")
	private String valueField;
	
	@Column(name="WHERE_CLAUSE")
	private String whereClause;
	
	@Column(name="ORDER_BY_CLAUSE")
	private String orderByClause;
	
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
	
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public String getKeyField() {
		return keyField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public String getValueField() {
		return valueField;
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
}

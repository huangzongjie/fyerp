package com.graly.promisone.activeentity.model;

import static javax.persistence.CascadeType.ALL;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="AD_REF_TABLE")
public class ADRefTable extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="AD_TABLE_ID")
	private Long tableId;
	
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

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public Long getTableId() {
		return tableId;
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

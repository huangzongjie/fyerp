package com.graly.promisone.base.entitymanager.adapter;

import com.graly.promisone.activeentity.model.ADTable;

public class EntityItemInput {
	
	private ADTable table;
	private String whereClause;
	private String orderByClause;
	
	public EntityItemInput(ADTable table){
		this.setTable(table);
	}
	
	public EntityItemInput(ADTable table, String whereClause){
		this.setTable(table);
		this.setWhereClause(whereClause);
	}
	
	public EntityItemInput(ADTable table, String whereClause, String orderByClause){
		this.setTable(table);
		this.setWhereClause(whereClause);
		this.setOrderByClause(orderByClause);
	}

	public void setTable(ADTable table) {
		this.table = table;
	}

	public ADTable getTable() {
		return table;
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

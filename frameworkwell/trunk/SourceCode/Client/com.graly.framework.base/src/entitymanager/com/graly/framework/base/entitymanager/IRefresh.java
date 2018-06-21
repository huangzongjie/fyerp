package com.graly.framework.base.entitymanager;

public interface IRefresh {
	
	void refresh();
	void setWhereClause(String whereClause);
	String getWhereClause();
}

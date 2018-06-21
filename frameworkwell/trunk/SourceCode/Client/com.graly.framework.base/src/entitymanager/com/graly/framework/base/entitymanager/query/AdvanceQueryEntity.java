package com.graly.framework.base.entitymanager.query;

import java.io.Serializable;

public class AdvanceQueryEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String field;
	
	private String comparator;
	
	private String dataType;
	
	private String value;
	
	private Object data;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getComparator() {
		return comparator;
	}

	public void setComparator(String comparator) {
		this.comparator = comparator;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Framework QueryEntity: " + this.getField();
	}

}

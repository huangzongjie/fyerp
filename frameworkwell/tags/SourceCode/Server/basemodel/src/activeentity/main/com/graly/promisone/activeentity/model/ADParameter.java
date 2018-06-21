package com.graly.promisone.activeentity.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="AD_PARAMETER")
public class ADParameter extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	public static final String FIELD_PARAMETER = "parameters";
	public static final String DATATYPE_STRING = "string";
	public static final String DATATYPE_DOUBLE = "double";
	public static final String DATATYPE_INTEGER = "integer";
	public static final String FIELDNAME_NAME = "name";
	public static final String FIELDNAME_DATETYPE = "type";
	public static final String FIELDNAME_DEFAULTVALUE = "defValue";
	private static  List<String> DISPLAYCOLUMNS;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="TYPE")
	private String type;
	
	@Column(name="DEFAULT_VALUE")
	private String defValue;
	
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

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public String getDefValue() {
		return defValue;
	}
	
	public static List<String> getDisplayColumns() {
		DISPLAYCOLUMNS = new ArrayList<String>();
		DISPLAYCOLUMNS.add(FIELDNAME_NAME);
		DISPLAYCOLUMNS.add(FIELDNAME_DATETYPE);
		DISPLAYCOLUMNS.add(FIELDNAME_DEFAULTVALUE);
		return DISPLAYCOLUMNS;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof ADParameter)) return false;
		ADParameter o = (ADParameter) obj;
		if (this.objectId == null || o.objectId == null){
			if (this.name != null) {
				return this.name.equals(o.name);
			} else {
				return false;
			}
		}
		return this.objectId.equals(o.objectId);
	}
}
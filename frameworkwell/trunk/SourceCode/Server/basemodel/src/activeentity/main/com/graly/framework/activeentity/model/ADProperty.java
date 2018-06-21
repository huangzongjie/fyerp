package com.graly.framework.activeentity.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name="AD_PROPERTY")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="PROPERTY_TYPE", discriminatorType = DiscriminatorType.STRING, length = 1)
public class ADProperty extends ADBase {
	private static final long serialVersionUID = 1L;
	
	@Column(name="KEY", unique=true)
	protected String key;

	@Column(name="VALUE")
	protected String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

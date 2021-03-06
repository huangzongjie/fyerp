package com.graly.promisone.security.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.graly.promisone.activeentity.model.ADUpdatable;


@Entity
@Table(name = "AD_ORG")
public class ADOrg extends ADUpdatable {
	private final static long serialVersionUID = 1L;

	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

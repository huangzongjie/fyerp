package com.graly.framework.security.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="AD_USERGROUP")
public class ADUserGroup extends ADUpdatable {
	
	@Column(name="USERGROUP_ID")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;

	@ManyToMany(targetEntity = ADUser.class, fetch=FetchType.LAZY)
	@JoinTable(name = "AD_USERGROUP_USER",
			joinColumns = @JoinColumn(name = "USERGROUP_RRN", referencedColumnName = "OBJECT_RRN"),
			inverseJoinColumns = @JoinColumn(name = "USER_RRN", referencedColumnName = "OBJECT_RRN"))
	private List<ADUser> users;
	
	@ManyToMany(targetEntity = ADUser.class, mappedBy = "userGroups")
	private List<ADUser> usersInv;
	
	@ManyToMany(targetEntity = ADAuthority.class, fetch=FetchType.LAZY)
	@JoinTable(name = "AD_USERGROUP_AUTHORITY",
			joinColumns = @JoinColumn(name = "USERGROUP_RRN", referencedColumnName = "OBJECT_RRN"),
			inverseJoinColumns = @JoinColumn(name = "AUTHORITY_RRN", referencedColumnName = "OBJECT_RRN"))
	private List<ADAuthority> authorities;


	
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
	
	public void setUsers(List<ADUser> users) {
		this.users = users;
	}

	public List<ADUser> getUsers() {
		return users;
	}

	public void setAuthorities(List<ADAuthority> authorities) {
		this.authorities = authorities;
	}

	public List<ADAuthority> getAuthorities() {
		return authorities;
	}

}

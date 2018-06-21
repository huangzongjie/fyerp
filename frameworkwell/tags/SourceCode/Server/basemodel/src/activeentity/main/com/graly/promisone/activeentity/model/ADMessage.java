package com.graly.promisone.activeentity.model;

import static javax.persistence.CascadeType.ALL;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="AD_MESSAGE")
public class ADMessage extends ADUpdatable {
	
	private static final long serialVersionUId = 1L;
	
	@Column(name="KEY")
	private String key;
	
	@Column(name="LANGUAGE")
	private String language;
	
	@Column(name="MESSAGE")
	private String message;
	
	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}

package com.graly.alm.panel;

import java.util.ArrayList;
import java.util.List;

import com.graly.framework.core.config.ConfigModule;
import com.graly.framework.core.config.InitException;


public class AlarmPanelCfMod extends ConfigModule {
	
	private static final long serialVersionUID = 1L;

	private String url = null;
	private String subject = null;
	private String user = null;
	private String password = null;

	@Override
	public void init() throws InitException {
		super.init();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

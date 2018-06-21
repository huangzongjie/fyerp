package com.graly.framework.core.config;

import java.util.ArrayList;
import java.util.List;


public class EMailCfMod extends ConfigModule {
	private static final long serialVersionUID = 1L;

	private String mailFrom = null;
	private String[] mailTo = null;
	private String smtpHost = null;
	private String smtpLocalhost = null;
	private String smtpUsername = null;
	private String smtpPassword = null;
	private Integer smtpPort = null;
	private boolean enableTLS = false;
	private boolean authenticate = false;

	@Override
	public void init() throws InitException {
		super.init();
	}

	/**
	 * Get the mailFrom.
	 * @return the mailFrom
	 */
	public String getMailFrom() {
		return mailFrom;
	}

	/**
	 * Set the mailFrom.
	 * @param mailFrom the mailFrom to set
	 */
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	/**
	 * Get the mailTo.
	 * @return the mailTo
	 */
	public String[] getMailTo() {
		return mailTo;
	}

	/**
	 * Set the mailTo.
	 * @param mailTo the mailTo to set
	 */
	public void setMailTo(String[] mailTo) {
		this.mailTo = mailTo;
	}

	/**
	 * Get the smtpHost.
	 * @return the smtpHost
	 */
	public String getSmtpHost() {
		return smtpHost;
	}

	/**
	 * Set the smtpHost.
	 * @param smtpHost the smtpHost to set
	 */
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	/**
	 * Get the smtpLocalhost.
	 * @return the smtpLocalhost
	 */
	public String getSmtpLocalhost() {
		return smtpLocalhost;
	}

	/**
	 * Set the smtpLocalhost.
	 * @param smtpLocalhost the smtpLocalhost to set
	 */
	public void setSmtpLocalhost(String smtpLocalhost) {
		this.smtpLocalhost = smtpLocalhost;
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public Integer getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

	public boolean isEnableTLS() {
		return enableTLS;
	}

	public void setEnableTLS(boolean enableTLS) {
		this.enableTLS = enableTLS;
	}

	public boolean isAuthenticate() {
		return authenticate;
	}

	public void setAuthenticate(boolean authenticate) {
		this.authenticate = authenticate;
	}
}

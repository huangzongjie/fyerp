package com.graly.mes.prd.workflow.configuration;

import com.graly.mes.prd.workflow.JbpmException;

public class ConfigurationException extends JbpmException {

  private static final long serialVersionUID = 1L;

  public ConfigurationException() {
    super();
  }
  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
  public ConfigurationException(String message) {
    super(message);
  }
  public ConfigurationException(Throwable cause) {
    super(cause);
  }
}

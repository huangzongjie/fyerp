package com.graly.mes.prd.workflow.configuration;

import org.w3c.dom.Element;

public class NullInfo extends AbstractObjectInfo {

  private static final long serialVersionUID = 1L;

  public NullInfo(Element nullElement, ObjectFactoryParser configParser) {
    super(nullElement, configParser);
  }
  
  public Object createObject(ObjectFactoryImpl objectFactory) {
    return null;
  }
}

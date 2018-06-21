package com.graly.mes.prd.workflow.configuration;

import org.w3c.dom.Element;

public class BooleanInfo extends AbstractObjectInfo {

  private static final long serialVersionUID = 1L;

  Boolean b = null;
  
  public BooleanInfo(Element booleanElement, ObjectFactoryParser configParser) {
    super(booleanElement, configParser);
    
    if ("true".equalsIgnoreCase(booleanElement.getTagName())) {
      b = Boolean.TRUE;
    } else if ("false".equalsIgnoreCase(booleanElement.getTagName())) {
      b = Boolean.FALSE;
    } else {
      String s = getValueString(booleanElement);
      b = Boolean.valueOf(s);
    }
  }
  public Object createObject(ObjectFactoryImpl objectFactory) {
    return b;
  }
}

package com.graly.mes.prd.workflow.configuration;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class DoubleInfo extends AbstractObjectInfo {

  private static final long serialVersionUID = 1L;

  Double d = null;
  
  public DoubleInfo(Element doubleElement, ObjectFactoryParser configParser) {
    super(doubleElement, configParser);
    
    String contentText = getValueString(doubleElement);
    try {
      d = (new Double(contentText));
    } catch (Exception e) {
      throw new JbpmException("content of "+XmlUtil.toString(doubleElement)+" could not be parsed as a double", e);
    }
  }

  public Object createObject(ObjectFactoryImpl objectFactory) {
    return d;
  }

}

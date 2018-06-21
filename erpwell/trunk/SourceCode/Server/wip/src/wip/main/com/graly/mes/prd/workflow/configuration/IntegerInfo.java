package com.graly.mes.prd.workflow.configuration;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class IntegerInfo extends AbstractObjectInfo {
  
  private static final long serialVersionUID = 1L;
  
  Integer i = null;

  public IntegerInfo(Element integerElement, ObjectFactoryParser configParser) {
    super(integerElement, configParser);
    
    String contentText = getValueString(integerElement);
    try {
      i = new Integer(contentText);
    } catch (Exception e) {
      throw new JbpmException("content of "+XmlUtil.toString(integerElement)+" could not be parsed as a integer", e);
    }
  }

  public Object createObject(ObjectFactoryImpl objectFactory) {
    return i;
  }

}

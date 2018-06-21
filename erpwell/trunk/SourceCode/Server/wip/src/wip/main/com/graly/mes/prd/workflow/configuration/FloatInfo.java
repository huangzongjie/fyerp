package com.graly.mes.prd.workflow.configuration;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class FloatInfo extends AbstractObjectInfo {

  private static final long serialVersionUID = 1L;
  
  Float f = null;

  public FloatInfo(Element floatElement, ObjectFactoryParser configParser) {
    super(floatElement, configParser);
    
    String contentText = getValueString(floatElement);
    try {
      f = (new Float(contentText));
    } catch (Exception e) {
      throw new JbpmException("content of "+XmlUtil.toString(floatElement)+" could not be parsed as a float", e);
    }
  }

  public Object createObject(ObjectFactoryImpl objectFactory) {
    return f;
  }

}

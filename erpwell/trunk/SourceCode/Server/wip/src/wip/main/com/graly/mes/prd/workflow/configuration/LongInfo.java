package com.graly.mes.prd.workflow.configuration;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class LongInfo extends AbstractObjectInfo {

  private static final long serialVersionUID = 1L;
  
  Long l = null;

  public LongInfo(Element longElement, ObjectFactoryParser configParser) {
    super(longElement, configParser);
    
    String contentText = getValueString(longElement);
    try {
      l = new Long(contentText);
    } catch (Exception e) {
      throw new JbpmException("content of "+XmlUtil.toString(longElement)+" could not be parsed as a long", e);
    }
  }

  public Object createObject(ObjectFactoryImpl objectFactory) {
    return l;
  }

}

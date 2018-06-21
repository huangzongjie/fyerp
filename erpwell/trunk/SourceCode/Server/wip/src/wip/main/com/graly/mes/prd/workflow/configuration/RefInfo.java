package com.graly.mes.prd.workflow.configuration;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class RefInfo extends AbstractObjectInfo {

  private static final long serialVersionUID = 1L;
  
  String bean = null;
  
  public RefInfo(Element refElement, ObjectFactoryParser configParser) {
    super(refElement, configParser);

    if (refElement.hasAttribute("bean")) {
      bean = refElement.getAttribute("bean");
    } else {
      throw new JbpmException("element ref must have a 'bean' attribute : "+XmlUtil.toString(refElement));
    }
  }

  public Object createObject(ObjectFactoryImpl objectFactory) {
    return objectFactory.getObject(bean);
  }
}

package com.graly.mes.prd.workflow.configuration;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class CharacterInfo extends AbstractObjectInfo {

  private static final long serialVersionUID = 1L;

  Character c = null;
  
  public CharacterInfo(Element charElement, ObjectFactoryParser configParser) {
    super(charElement, configParser);
    
    String s = getValueString(charElement);
    if (s!=null) {
      s = s.trim();
      if (s.length()==1) {
        c = new Character(s.charAt(0)); 
      }
    }
    if (c==null) {
      throw new JbpmException("improper character format '"+XmlUtil.toString(charElement));
    }
  }

  public Object createObject(ObjectFactoryImpl objectFactory) {
    return c;
  }

}

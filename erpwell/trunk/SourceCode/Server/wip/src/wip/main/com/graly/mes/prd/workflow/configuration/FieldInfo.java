package com.graly.mes.prd.workflow.configuration;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.JbpmException;

public class FieldInfo extends PropertyInfo {

  private static final long serialVersionUID = 1L;
  static final Logger logger = Logger.getLogger(FieldInfo.class);
  
  public FieldInfo(Element fieldElement, ObjectFactoryParser configParser) {
    super(fieldElement, configParser);
  }

  public void injectProperty(Object object, ObjectFactoryImpl objectFactory) {

    Object propertyValue = objectFactory.getObject(propertyValueInfo);
    Field propertyField = findField(object.getClass());
    propertyField.setAccessible(true);
    try {
      propertyField.set(object, propertyValue);
    } catch (Exception e) {
      throw new JbpmException("couldn't set field '"+propertyName+"' on class '"+object.getClass()+"' to value '"+propertyValue+"'", e);
    }
  }

  Field findField(Class clazz) {
    Field field = null;
    
    Class candidateClass = clazz;
    while ( (candidateClass!=null)
            && (field==null)
          ) {

      try {
        field = candidateClass.getDeclaredField(propertyName);
      } catch (Exception e) {
        candidateClass = candidateClass.getSuperclass();
      }
    }

    if (field==null) {
      JbpmException e = new JbpmException("couldn't find field '"+propertyName+"' in class '"+clazz.getName()+"'");
      logger.error(e);
      throw e;
    }
    
    return field;
  }
}

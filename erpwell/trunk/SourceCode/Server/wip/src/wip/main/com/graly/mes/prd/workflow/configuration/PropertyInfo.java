package com.graly.mes.prd.workflow.configuration;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class PropertyInfo implements Serializable {

  private static final long serialVersionUID = 1L;
 
  String propertyName = null;
  String setterMethodName = null;
  ObjectInfo propertyValueInfo = null;

  public PropertyInfo(Element propertyElement, ObjectFactoryParser configParser) {
    // propertyName or setterMethodName
    if (propertyElement.hasAttribute("name")) {
      propertyName = propertyElement.getAttribute("name"); 
    } else if (propertyElement.hasAttribute("setter")) {
      setterMethodName = propertyElement.getAttribute("setter"); 
    } else {
      throw new JbpmException("property must have a 'name' or 'setter' attribute: "+XmlUtil.toString(propertyElement));
    }
    
    // propertyValueInfo
    Element propertyValueElement = XmlUtil.element(propertyElement);
    propertyValueInfo = configParser.parse(propertyValueElement);
  }

  public void injectProperty(Object object, ObjectFactoryImpl objectFactory) {
    Object propertyValue = objectFactory.getObject(propertyValueInfo);
    Method setterMethod = findSetter(object.getClass());
    setterMethod.setAccessible(true);
    try {
      setterMethod.invoke(object, new Object[]{propertyValue});
    } catch (Exception e) {
      throw new JbpmException("couldn't set property '"+propertyName+"' on class '"+object.getClass()+"' to value '"+propertyValue+"'", e);
    }
  }

  public Method findSetter(Class clazz) {
    Method method = null;

    if (setterMethodName==null) {
      if ( (propertyName.startsWith("is"))
           && (propertyName.length()>3) 
           && (Character.isUpperCase(propertyName.charAt(2)))
         ) {
        setterMethodName = "set"+propertyName.substring(2);
      } else {
        setterMethodName = "set"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
      }
    }
    
    Class candidateClass = clazz;
    while ( (candidateClass!=null)
            && (method==null)
          ) {

      Method[] methods = candidateClass.getDeclaredMethods();
      if (methods!=null) {
        for (int i=0; ( (i<methods.length) && (method==null) ); i++) {
          if ( (methods[i].getName().equals(setterMethodName))
               && (methods[i].getParameterTypes()!=null)
               && (methods[i].getParameterTypes().length==1)
             ) {
            method = methods[i];
          }
        }
      }

      if (method==null) {
        candidateClass = candidateClass.getSuperclass();
      }
    }

    if (method==null) {
      throw new JbpmException("couldn't find setter '"+setterMethodName+"' in class '"+clazz.getName()+"'");
    }
    
    return method;
  }
}

package com.graly.mes.prd.workflow.jpdl.el.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BeanMethod {

  Method method;
  
  public BeanMethod(Method method) {
    this.method = method;
  }

  public Object invoke(Object object) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
    return method.invoke(object, null);
  }
}

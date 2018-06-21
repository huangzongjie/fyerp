package com.graly.mes.prd.workflow.context.exe.converter;

import com.graly.mes.prd.workflow.context.exe.Converter;

public class ShortToLongConverter implements Converter {
  
  private static final long serialVersionUID = 1L;

  public boolean supports(Object value) {
    if (value==null) return true;
    return (value.getClass()==Short.class);
  }

  public Object convert(Object o) {
    return new Long( ((Number)o).longValue() );
  }
  
  public Object revert(Object o) {
    return new Short(((Long)o).shortValue());
  }
}

package com.graly.mes.prd.workflow.configuration;

import java.io.Serializable;

public interface ObjectInfo extends Serializable {

  boolean hasName();
  String getName();
  boolean isSingleton();

  Object createObject(ObjectFactoryImpl objectFactory);
}

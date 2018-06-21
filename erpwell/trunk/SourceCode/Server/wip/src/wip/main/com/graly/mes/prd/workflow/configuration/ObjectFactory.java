package com.graly.mes.prd.workflow.configuration;

import java.io.Serializable;

public interface ObjectFactory extends Serializable {

	Object createObject(String name);

	boolean hasObject(String name);
}

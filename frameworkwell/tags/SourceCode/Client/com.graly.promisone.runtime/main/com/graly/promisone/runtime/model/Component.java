package com.graly.promisone.runtime.model;

public interface Component extends Extensible {
	
	void activate(ComponentContext context) throws Exception;
	void deactivate(ComponentContext context) throws Exception;
}

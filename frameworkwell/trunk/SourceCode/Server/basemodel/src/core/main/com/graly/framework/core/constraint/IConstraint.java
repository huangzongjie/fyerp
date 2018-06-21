package com.graly.framework.core.constraint;

import com.graly.framework.core.exception.ClientException;

public interface IConstraint {
	
	void init();
	void start();
	void stop();
	void setChain(ConstraintChain chain);
	IConstraint getNext();
	void invoke(ConstraintContext context) throws ClientException;
}

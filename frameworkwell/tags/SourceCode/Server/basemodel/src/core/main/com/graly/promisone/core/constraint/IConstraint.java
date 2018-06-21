package com.graly.promisone.core.constraint;

import com.graly.promisone.core.exception.ClientException;

public interface IConstraint {
	
	void init();
	void start();
	void stop();
	void setChain(ConstraintChain chain);
	IConstraint getNext();
	void invoke(ConstraintContext context) throws ClientException;
}

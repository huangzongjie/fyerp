package com.graly.framework.core.constraint;

import com.graly.framework.core.exception.ClientException;

public abstract class AbstractConstraint implements IConstraint {
	private ConstraintChain chain;
	
	public void init() {
	}
	
	public void start() {
	}
	
	public void stop() {
	}
	
	public IConstraint getNext() {
		IConstraint task = getChain().getNext();
        getChain().setCurrent(task);
        return task;
	}
	
	public void invoke(ConstraintContext context) throws ClientException {
		if (this.getNext() == null) {
			getChain().stop();
		} else {
			getChain().invoke(context);
		}
	}

	public void setChain(ConstraintChain chain) {
		this.chain = chain;
	}

	public ConstraintChain getChain() {
		return chain;
	}
}

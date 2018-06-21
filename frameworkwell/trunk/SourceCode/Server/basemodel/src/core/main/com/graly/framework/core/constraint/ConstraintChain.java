package com.graly.framework.core.constraint;

import java.util.ArrayList;
import java.util.List;

import com.graly.framework.core.exception.ClientException;

public abstract class ConstraintChain {

	protected List<IConstraint> constraints = new ArrayList<IConstraint>();
	protected IConstraint current;
	
	public ConstraintChain() {
		init();
	}
	
	public void init() {
		for (IConstraint constraint : constraints) {
			constraint.init();
			constraint.setChain(this);
		}
	}
	
	public void start() {
		for (IConstraint constraint : constraints) {
			constraint.start();
		}
	}
	
	public void stop() {
		for (IConstraint constraint : constraints) {
			constraint.stop();
		}
		current = null;
	}
	
	public IConstraint getNext() {
		if (constraints.size() < 1) {
			return null;
		} else if (getCurrent() == null) {
			return constraints.get(0);
		} else {
			int index = constraints.indexOf(getCurrent());
			if (index >= 0 && index < (constraints.size() - 1)) {
				return constraints.get(index + 1);
			}
		}
		return null;
	}

	public void invoke(ConstraintContext context) throws ClientException {
		if (current == null) {
			current = this.getNext();
			if (current == null) {
				this.stop();
				return;
			}
		}
		current.invoke(context);
	}
	
	public void setCurrent(IConstraint current) {
		this.current = current;
	}

	public IConstraint getCurrent() {
		return current;
	}
}

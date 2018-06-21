package com.graly.mes.prd.workflow.jpdl.xml;

import java.io.Serializable;

public interface ProblemListener extends Serializable {

	void addProblem(Problem problem);
	
}

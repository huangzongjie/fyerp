package com.graly.mes.prd.workflow.graph.node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.graly.mes.prd.workflow.action.exe.InstanceAction;
import com.graly.mes.prd.workflow.context.def.WFParameter;
import com.graly.mes.prd.workflow.context.exe.ContextInstance;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.Step;
import com.graly.mes.prd.workflow.graph.def.Transition;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.prd.workflow.graph.exe.ProcessInstance;
import com.graly.mes.prd.workflow.graph.exe.Token;
import com.graly.mes.prd.workflow.jpdl.xml.JpdlXmlReader;

@Entity
@DiscriminatorValue("S")
public class StepState extends Node {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(StepState.class);
	
//	@ManyToOne
//	@JoinColumn(name = "SUB_PROCESS_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	@Transient
	private Step step = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name = "PROCESS_STATE_RRN", referencedColumnName = "OBJECT_RRN")
	protected Set<WFParameter> wfParameters = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "seqNo ASC")
	@JoinColumn(name = "STEP_STATE_RRN", referencedColumnName = "OBJECT_RRN")
	private List<InstanceAction> instanceActions = null;
	
	@Column(name="SUB_PROCSS_NAME")
	private String stepName = null;
	
	@Transient
	private Step usedStep;
	
	public void read(Element stepStateElement, JpdlXmlReader jpdlReader) {
		Element stepElement = stepStateElement.element("step");
		if (stepElement != null) {
			setStepName(stepElement.attributeValue("name"));
		}

		if (getStep() != null) {
			logger.debug("subprocess for process-state '" + name + "' bound to " + getStep());
		} else if (getStepName() != null) {
			logger.debug("subprocess for process-state '" + name + "' will be late bound to " + getStepName());
		} else {
			logger.debug("subprocess for process-state '" + name + "' not yet bound");
		}
		this.wfParameters = new HashSet(jpdlReader.readWfParameters(stepStateElement));
	}
	
	public void execute(ExecutionContext executionContext) {
		Token superProcessToken = executionContext.getToken();
		setUsedStep(getStep());
		// if this process has late binding
		if ((step == null) && (getStepName() != null)) {
			try {
				step = new Step();
				step.setOrgRrn(this.getOrgRrn());
				step.setName(getStepName());
				step = (Step)executionContext.getPrdManager().getActiveProcessDefinition(step);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			setUsedStep(step);
		}
		// create the subprocess
		ProcessInstance subProcessInstance = superProcessToken.createSubProcessInstance(getUsedStep(), null, superProcessToken.getProcessInstance().getInstanceKey());
		
		// feed the readable variableInstances
		if ((wfParameters != null) && (!wfParameters.isEmpty())) {
		    ContextInstance superContextInstance = executionContext.getContextInstance();
		    ContextInstance subContextInstance = subProcessInstance.getContextInstance();
		    subContextInstance.setTransientVariables(superContextInstance.getTransientVariables());

			// loop over all the variable accesses
			Iterator iter = wfParameters.iterator();
			while (iter.hasNext()) {
				WFParameter variableAccess = (WFParameter) iter.next();
				// if this variable access is readable
				if (variableAccess.isReadable()) {
					// the variable is copied from the super process variable name
					// to the sub process mapped name
					String variableName = variableAccess.getVariableName();
					Object value = superContextInstance.getVariable(
							variableName, superProcessToken);
					String mappedName = variableAccess.getMappedName();
					logger.debug("copying super process var '" + variableName
							+ "' to sub process var '" + mappedName + "': "
							+ value);
					if (value != null) {
						subContextInstance.setVariable(mappedName, value);
					}
				}
			}
		}
		// send the signal to start the subprocess
		subProcessInstance.signal();
	}
	
	public void leave(ExecutionContext executionContext, Transition transition) {
		ProcessInstance subProcessInstance = executionContext
				.getSubProcessInstance();
		Token superProcessToken = subProcessInstance.getSuperProcessToken();

		// feed the readable variableInstances
		if ((wfParameters != null) && (!wfParameters.isEmpty())) {
		    ContextInstance superContextInstance = executionContext.getContextInstance();
		    ContextInstance subContextInstance = subProcessInstance.getContextInstance();

			// loop over all the variable accesses
			Iterator iter = wfParameters.iterator();
			while (iter.hasNext()) {
				WFParameter variableAccess = (WFParameter) iter.next();
				// if this variable access is writable
				if (variableAccess.isWritable()) {
					// the variable is copied from the sub process mapped name
					// to the super process variable name
					String mappedName = variableAccess.getMappedName();
					Object value = subContextInstance.getVariable(mappedName);
					String variableName = variableAccess.getVariableName();
					logger.debug("copying sub process var '" + mappedName
							+ "' to super process var '" + variableName + "': "
							+ value);
					if (value != null) {
						superContextInstance.setVariable(variableName, value,
								superProcessToken);
					}
				}
			}
		}

		// remove the subprocess reference
		superProcessToken.setSubProcessInstance(null);

		// call the subProcessEndAction
		super.leave(executionContext, getDefaultLeavingTransition());
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getStepName() {
		return stepName;
	}
	
	public void setStep(Step step) {
		this.step = step;
	}

	public Step getStep() {
		return step;
	}
	
	public void setUsedStep(Step usedStep) {
		this.usedStep = usedStep;
	}

	public Step getUsedStep() {
		return usedStep;
	}

	public void setInstanceActions(List<InstanceAction> instanceActions) {
		this.instanceActions = instanceActions;
	}

	public List<InstanceAction> getInstanceActions() {
		return instanceActions;
	}
	


}

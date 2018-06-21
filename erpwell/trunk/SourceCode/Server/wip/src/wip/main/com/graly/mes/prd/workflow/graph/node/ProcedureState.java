package com.graly.mes.prd.workflow.graph.node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.graly.mes.prd.workflow.context.def.WFParameter;
import com.graly.mes.prd.workflow.context.exe.ContextInstance;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.Procedure;
import com.graly.mes.prd.workflow.graph.def.Transition;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.prd.workflow.graph.exe.ProcessInstance;
import com.graly.mes.prd.workflow.graph.exe.Token;
import com.graly.mes.prd.workflow.jpdl.xml.JpdlXmlReader;

@Entity
@DiscriminatorValue("R")
public class ProcedureState extends Node {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ProcedureState.class);
	
//	@ManyToOne
//	@JoinColumn(name = "SUB_PROCESS_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	@Transient
	protected Procedure procedure = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name = "PROCESS_STATE_RRN", referencedColumnName = "OBJECT_RRN")
	protected Set<WFParameter> wfParameters = null;
	
	@Column(name="SUB_PROCSS_NAME")
	private String procedureName = null;
	
	@Transient
	private Procedure usedProcedure;
	
	public void read(Element procedureStateElement, JpdlXmlReader jpdlReader) {
		Element procedureElement = procedureStateElement.element("procedure");
		if (procedureElement != null) {
			setProcedureName(procedureElement.attributeValue("name"));
		}

		if (procedure != null) {
			logger.debug("subprocess for process-state '" + name + "' bound to " + procedure);
		} else if (getProcedureName() != null) {
			logger.debug("subprocess for process-state '" + name
					+ "' will be late bound to " + getProcedureName());
		} else {
			logger.debug("subprocess for process-state '" + name
					+ "' not yet bound");
		}

		this.wfParameters = new HashSet(jpdlReader.readWfParameters(procedureStateElement));
	}
	
	public void execute(ExecutionContext executionContext) {
		Token superProcessToken = executionContext.getToken();
		setUsedProcedure(procedure);
		// if this process has late binding
		if ((procedure == null) && (getProcedureName() != null)) {
			try {
				procedure = new Procedure();
				procedure.setOrgRrn(this.getOrgRrn());
				procedure.setName(getProcedureName());
				procedure = (Procedure)executionContext.getPrdManager().getActiveProcessDefinition(procedure);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			setUsedProcedure(procedure);
		}

		// create the subprocess
		ProcessInstance subProcessInstance = superProcessToken.createSubProcessInstance(getUsedProcedure(), null, superProcessToken.getProcessInstance().getInstanceKey());
		
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
					Object value = superContextInstance.getVariable(variableName, superProcessToken);
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
		ProcessInstance subProcessInstance = executionContext.getSubProcessInstance();
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
						superContextInstance.setVariable(variableName, value, superProcessToken);
					}
				}
			}
		}

		// remove the subprocess reference
		superProcessToken.setSubProcessInstance(null);

		// call the subProcessEndAction
		super.leave(executionContext, getDefaultLeavingTransition());
	}
	
	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setUsedProcedure(Procedure usedProcedure) {
		this.usedProcedure = usedProcedure;
	}

	public Procedure getUsedProcedure() {
		return usedProcedure;
	}


}

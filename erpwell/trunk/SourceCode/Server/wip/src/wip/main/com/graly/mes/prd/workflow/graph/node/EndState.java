package com.graly.mes.prd.workflow.graph.node;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.dom4j.Element;

import com.graly.mes.prd.workflow.action.def.ProcedureEndAction;
import com.graly.mes.prd.workflow.action.def.ProcessEndAction;
import com.graly.mes.prd.workflow.action.def.StepEndAction;
import com.graly.mes.prd.workflow.action.exe.InstanceAction;
import com.graly.mes.prd.workflow.action.exe.InstanceToken;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.Procedure;
import com.graly.mes.prd.workflow.graph.def.Process;
import com.graly.mes.prd.workflow.graph.def.Step;
import com.graly.mes.prd.workflow.graph.def.Transition;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.prd.workflow.graph.exe.Token;
import com.graly.mes.prd.workflow.jpdl.xml.JpdlXmlReader;

@Entity
@DiscriminatorValue("E")
public class EndState extends Node {
	private static final long serialVersionUID = 1L;
	
	@Transient
	String endCompleteProcess = null;
	
	@Transient
	private InstanceActionResolver resolver = new InstanceActionResolver();

	
	public EndState() {
	}
	
	public EndState(String name) {
		super(name);
	}
	
	public void read(Element nodeElement, JpdlXmlReader jpdlXmlReader) {
		endCompleteProcess = nodeElement.attributeValue("end-complete-process");
	}
	
	public void enter(ExecutionContext executionContext) {
		Token token = executionContext.getToken();
		Token superToken = token.getSuperToken();
		if (superToken != null) {
			Node node = superToken.getNode();
			if (node != null && node instanceof StepState) {
				StepState state = (StepState)node;
				resolver.removeInstanceAction(executionContext);
				List<InstanceAction> instanceActions = resolver.loadInstanceActions(executionContext, state, InstanceAction.ACTIONTYPE_TRACKOUT);
				if (instanceActions != null && instanceActions.size() > 0) {
					InstanceToken instanceToken = new InstanceToken(token);
					instanceToken.setInstanceActions(instanceActions);
					executionContext.getEntityManager().persist(instanceToken);
					instanceToken.init(executionContext);
				}
			}
		}
		super.enter(executionContext);
	}
	
	public void leave(ExecutionContext executionContext) {
		Token token = executionContext.getToken();
		InstanceToken instanceToken = token.getInstanceToken();
		if (instanceToken != null) {
			instanceToken.signal(executionContext);
		}
		
		if (instanceToken != null && !instanceToken.getIsEnd()) {
			return;
		}
		
		if (instanceToken != null && instanceToken.getIsEnd()) {
			token.setInstanceToken(null);
		}
		// leave this node as usual
		if ((endCompleteProcess != null)
				&& (endCompleteProcess.equalsIgnoreCase("true"))) {
			executionContext.getProcessInstance().end();
		} else {
			executionContext.getToken().end();
		}

	}
	
	public void execute(ExecutionContext executionContext) {
		if (this.getProcessDefinition() instanceof Process) {
			ProcessEndAction action = new ProcessEndAction();
			action.execute(executionContext);
		} else if (this.getProcessDefinition() instanceof Procedure) {
			ProcedureEndAction action = new ProcedureEndAction();
			action.execute(executionContext);
		} else if (this.getProcessDefinition() instanceof Step) {
			StepEndAction action = new StepEndAction();
			action.execute(executionContext);
		}
		super.execute(executionContext);
	}
	
	public Transition addLeavingTransition(Transition t) {
		throw new UnsupportedOperationException(
				"can't add a leaving transition to an end-state");
	}
	
}

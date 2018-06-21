package com.graly.mes.prd.workflow.graph.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.dom4j.Element;

import com.graly.mes.prd.workflow.action.def.ProcedureStartAction;
import com.graly.mes.prd.workflow.action.def.ProcessStartAction;
import com.graly.mes.prd.workflow.action.def.StepStartAction;
import com.graly.mes.prd.workflow.action.exe.InstanceAction;
import com.graly.mes.prd.workflow.action.exe.InstanceToken;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.Procedure;
import com.graly.mes.prd.workflow.graph.def.Process;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.def.Step;
import com.graly.mes.prd.workflow.graph.def.Transition;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.prd.workflow.graph.exe.Token;
import com.graly.mes.prd.workflow.jpdl.xml.JpdlXmlReader;

@Entity
@DiscriminatorValue("T")
public class StartState extends Node {
	private static final long serialVersionUID = 1L;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.MERGE)
	@JoinColumn(name = "START_STATE_RRN", referencedColumnName = "OBJECT_RRN")
	private List<ProcessDefinition> belongProcessDefinition = null;
	
	public ProcessDefinition addProcessDefinition(ProcessDefinition pf) {
		if (pf == null)
			throw new IllegalArgumentException("can't add a null pf to a processdefinition");
		setBelongProcessDefinition(new ArrayList<ProcessDefinition>());
		getBelongProcessDefinition().add(pf);
		return pf;
	}
	
	public void setBelongProcessDefinition(List<ProcessDefinition> belongProcessDefinition) {
		this.belongProcessDefinition = belongProcessDefinition;
	}

	public List<ProcessDefinition> getBelongProcessDefinition() {
		return belongProcessDefinition;
	}
	
	@Transient
	private InstanceActionResolver resolver = new InstanceActionResolver();
	
	public StartState() {
	}

	public StartState(String name) {
		super(name);
	}

	public void read(Element startStateElement, JpdlXmlReader jpdlReader) {
	}

	public void write(Element nodeElement) {
	}
	
	public void enter(ExecutionContext executionContext) {
		Token token = executionContext.getToken();
		Token superToken = token.getSuperToken();
		if (superToken != null) {
			Node node = superToken.getNode();
			if (node != null && node instanceof StepState) {
				StepState state = (StepState)node;
				resolver.removeInstanceAction(executionContext);
				List<InstanceAction> instanceActions = resolver.loadInstanceActions(executionContext, state, InstanceAction.ACTIONTYPE_QUEUE);
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
	
	public void leave(ExecutionContext executionContext, Transition transition) {
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
		super.leave(executionContext, transition);

	}

	public void execute(ExecutionContext executionContext) {
		if (this.getProcessDefinition() instanceof Process) {
			ProcessStartAction action = new ProcessStartAction();
			action.execute(executionContext);
		} else if (this.getProcessDefinition() instanceof Procedure) {
			ProcedureStartAction action = new ProcedureStartAction();
			action.execute(executionContext);
		} else if (this.getProcessDefinition() instanceof Step) {
			StepStartAction action = new StepStartAction();
			action.execute(executionContext);
		}
	}
		  
	public Transition addArrivingTransition(Transition t) {
		throw new UnsupportedOperationException(
				"illegal operation : its not possible to add a transition that is arriving in a start state");
	}

	public void setArrivingTransitions(Map arrivingTransitions) {
		if ((arrivingTransitions != null) && (arrivingTransitions.size() > 0)) {
			throw new UnsupportedOperationException(
					"illegal operation : its not possible to set a non-empty map in the arriving transitions of a start state");
		}
	}

}

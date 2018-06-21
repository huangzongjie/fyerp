package com.graly.mes.prd.workflow.graph.def;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.prd.workflow.graph.exe.Token;
import com.graly.mes.prd.workflow.jpdl.el.impl.JbpmExpressionEvaluator;

@Entity
@Table(name="WF_TRANSITION")
public class Transition extends GraphElement {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "FROM_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	protected Node from = null;
	
	@ManyToOne
	@JoinColumn(name = "TO_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	protected Node to = null;
	
	@Column(name = "FROM_SEQ_NO")
	private Integer fromSeqNo;
	
	@Column(name = "CONDITION")
	protected String condition = null;
	
	@ManyToOne(cascade=CascadeType.REFRESH)
	@JoinColumn(name = "PROCESS_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	protected ProcessDefinition processDefinition = null;
	
	transient boolean isConditionEnforced = true;

	// constructors /////////////////////////////////////////////////////////////

	public Transition() {
	}

	public Transition(String name) {
		super(name);
	}

	// from /////////////////////////////////////////////////////////////////////

	public Node getFrom() {
		return from;
	}

	/**
	 * sets the from node unidirectionally.  use {@link Node#addLeavingTransition(Transition)}
	 * to get bidirectional relations mgmt.
	 */
	public void setFrom(Node from) {
		this.from = from;
	}

	// to ///////////////////////////////////////////////////////////////////////

	/**
	 * sets the to node unidirectionally.  use {@link Node#addArrivingTransition(Transition)}
	 * to get bidirectional relations mgmt.
	 */
	public void setTo(Node to) {
		this.to = to;
	}

	public Node getTo() {
		return to;
	}

	/**
	 * the condition expresssion for this transition.
	 */
	public String getCondition() {
		return condition;
	}

	public void setCondition(String conditionExpression) {
		this.condition = conditionExpression;
	}

	public void removeConditionEnforcement() {
		isConditionEnforced = false;
	}
	
	/**
	 * passes execution over this transition.
	 */
	public void take(ExecutionContext executionContext) {
		// update the runtime context information
		executionContext.getToken().setNode(null);

		Token token = executionContext.getToken();

		if ((condition != null) && (isConditionEnforced)) {
			Object result = JbpmExpressionEvaluator.evaluate(condition,	executionContext);
			if (result == null) {
				throw new JbpmException("transition condition " + condition
						+ " evaluated to null");
			} else if (!(result instanceof Boolean)) {
				throw new JbpmException("transition condition " + condition
						+ " evaluated to non-boolean: "
						+ result.getClass().getName());
			} else if (!((Boolean) result).booleanValue()) {
				throw new JbpmException("transition condition " + condition
						+ " evaluated to 'false'");
			}
		}

		// pass the token to the destinationNode node
		to.enter(executionContext);
	}
	// other
	// ///////////////////////////////////////////////////////////////////////////

	public void setName(String name) {
		if (from != null) {
			if (from.hasLeavingTransition(name)) {
				throw new IllegalArgumentException(
						"couldn't set name '"
								+ name
								+ "' on transition '"
								+ this
								+ "'cause the from-node of this transition has already another leaving transition with the same name");
			}
			Map<String, Transition> fromLeavingTransitions = from.getLeavingTransitionsMap();
			fromLeavingTransitions.remove(this.name);
			fromLeavingTransitions.put(name, this);
		}
		this.name = name;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}
		  
	public void setFromSeqNo(Integer fromSeqNo) {
		this.fromSeqNo = fromSeqNo;
	}

	public Integer getFromSeqNo() {
		return fromSeqNo;
	}
	
}

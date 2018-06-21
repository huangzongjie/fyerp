package com.graly.mes.prd.workflow.context.exe;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.mes.prd.workflow.graph.exe.Token;

/**
 * is a jbpm-internal map of variableInstances related to one {@link Token}.  
 * Each token has it's own map of variableInstances, thereby creating 
 * hierarchy and scoping of process variableInstances. 
 */
@Entity
@Table(name="WF_TOKENVARIABLEMAP")
public class TokenVariableMap extends VariableContainer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name = "TOKEN_RRN", referencedColumnName = "OBJECT_RRN")
	protected Token token = null;
	
	@ManyToOne
	@JoinColumn(name = "CONTEXT_INSTANCE_RRN", referencedColumnName = "OBJECT_RRN")
	protected ContextInstance contextInstance = null;

	public TokenVariableMap() {
	}

	public TokenVariableMap(Token token, ContextInstance contextInstance) {
		this.token = token;
		this.contextInstance = contextInstance;
	}

	public void addVariableInstance(VariableInstance variableInstance) {
		super.addVariableInstance(variableInstance);
		variableInstance.setTokenVariableMap(this);
	}

	public String toString() {
		return "TokenVariableMap" + ((token != null)&&(token.getName() != null) ? "[" + token.getName() + "]" : Integer.toHexString(System.identityHashCode(this)));
	}

	// protected ////////////////////////////////////////////////////////////////

	protected VariableContainer getParentVariableContainer() {
		Token parentToken = token.getParent();
		if (parentToken == null) {
			return null;
		}
		return contextInstance.getTokenVariableMap(parentToken);
	}

	// getters and setters //////////////////////////////////////////////////////

	public ContextInstance getContextInstance() {
		return contextInstance;
	}

	public Token getToken() {
		return token;
	}

	public Map<String, VariableInstance> getVariableInstances() {
		return variableInstances;
	}
}

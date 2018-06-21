package com.graly.mes.prd.workflow.context.exe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.apache.log4j.Logger;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.graph.exe.ProcessInstance;
import com.graly.mes.prd.workflow.graph.exe.Token;

@MappedSuperclass
public abstract class VariableContainer extends ADBase {

	static final Logger logger = Logger.getLogger(VariableContainer.class);

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@MapKey(name = "name")
	@JoinColumn(name = "TOKEN_VARIABLE_MAP_RRN", referencedColumnName = "OBJECT_RRN")
	protected Map<String, VariableInstance> variableInstances = null;

	protected abstract VariableContainer getParentVariableContainer();

	public abstract Token getToken();

	// variables ////////////////////////////////////////////////////////////////

	public Object getVariable(String name) {
		Object value = null;
		if (hasVariableLocally(name)) {
			value = getVariableLocally(name);
		} else {
			VariableContainer parent = getParentVariableContainer();
			if (parent != null) {
				// check upwards in the token hierarchy
				value = parent.getVariable(name);
			}
		}
		return value;
	}

	public void setVariable(String name, Object value) {
		VariableContainer parent = getParentVariableContainer();
		if (hasVariableLocally(name) || parent == null) {
			setVariableLocally(name, value);

		} else {
			// so let's action to the parent token's TokenVariableMap
			parent.setVariable(name, value);
		}
	}

	public boolean hasVariable(String name) {
		boolean hasVariable = false;

		// if the variable is present in the variable instances
		if (hasVariableLocally(name)) {
			hasVariable = true;

		} else {
			VariableContainer parent = getParentVariableContainer();
			if (parent != null) {
				hasVariable = parent.hasVariable(name);
			}
		}

		return hasVariable;
	}

	public void deleteVariable(String name) {
		if (name == null) {
			throw new JbpmException("name is null");
		}
		if (hasVariableLocally(name)) {
			deleteVariableLocally(name);
		}
	}

	/**
	 * adds all the given variables to this variable container.
	 * The method {@link #setVariables(Map)} is the same as this method, but 
	 * it was added for naming consitency.
	 */
	public void addVariables(Map<String, Object> variables) {
		setVariables(variables);
	}

	/**
	 * adds all the given variables to this variable container.  It doesn't 
	 * remove any existing variables unless they are overwritten by the given 
	 * variables.
	 * This method is the same as {@link #addVariables(Map)} and this method 
	 * was added for naming consistency. 
	 */
	public void setVariables(Map<String, Object> variables) {
		if (variables != null) {
			Iterator<Map.Entry<String, Object>> iter = variables.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter.next();
				setVariable((String) entry.getKey(), entry.getValue());
			}
		}
	}

	public Map<String, Object> getVariables() {
		Map<String, Object> variables = getVariablesLocally();
		VariableContainer parent = getParentVariableContainer();
		if (parent != null) {
			Map<String, Object> parentVariables = parent.getVariablesLocally();
			parentVariables.putAll(variables);
			variables = parentVariables;
		}
		return variables;
	}

	public Map<String, Object> getVariablesLocally() {
		Map<String, Object> variables = new HashMap<String, Object>();
		if (variableInstances != null) {
			Iterator<Map.Entry<String, VariableInstance>> iter = variableInstances.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, VariableInstance> entry = (Map.Entry<String, VariableInstance>) iter.next();
				String name = (String) entry.getKey();
				VariableInstance variableInstance = (VariableInstance) entry
						.getValue();
				if (!variables.containsKey(name)) {
					variables.put(name, variableInstance.getValue());
				}
			}
		}
		return variables;
	}

	// local variable methods ///////////////////////////////////////////////////

	public boolean hasVariableLocally(String name) {
		return ((variableInstances != null) && (variableInstances
				.containsKey(name)));
	}

	public Object getVariableLocally(String name) {
		Object value = null;

		// if the variable is present in the variable instances
		if (hasVariableLocally(name)) {
			value = getVariableInstance(name).getValue();
		}

		return value;
	}

	public void deleteVariableLocally(String name) {
		deleteVariableInstance(name);
	}

	public void setVariableLocally(String name, Object value) {
		if (name == null) {
			throw new JbpmException("name is null");
		}

		VariableInstance variableInstance = getVariableInstance(name);
		// if there is already a variable instance and it doesn't support the current type...
		if ((variableInstance != null) && (!variableInstance.supports(value))) {
			// delete the old variable instance
			logger.debug("variable type change. deleting '" + name + "' from '"
					+ this + "'");
			deleteVariableInstance(name);
			variableInstance = null;
		}

		if (variableInstance == null) {
			logger.debug("create variable '" + name + "' in '" + this
					+ "' with value '" + value + "'");
			variableInstance = VariableInstance.create(getToken(), name, value);
			addVariableInstance(variableInstance);
		} else {
			logger.debug("update variable '" + name + "' in '" + this
					+ "' to value '" + value + "'");
			variableInstance.setValue(value);
		}
	}

	// local variable instances /////////////////////////////////////////////////

	public VariableInstance getVariableInstance(String name) {
		return (variableInstances != null ? (VariableInstance) variableInstances
				.get(name)
				: null);
	}

	public Map<String, VariableInstance> getVariableInstances() {
		return variableInstances;
	}

	public void addVariableInstance(VariableInstance variableInstance) {
		if (variableInstances == null) {
			variableInstances = new HashMap<String, VariableInstance>();
		}
		variableInstances.put(variableInstance.getName(), variableInstance);
		// only additions are registered in the updated variable containers 
		// because it is only used in the save operation to check wether there 
		// are unpersistable variables added
		addUpdatedVariableContainer();
	}

	public void deleteVariableInstance(String name) {
		if (variableInstances != null) {
			VariableInstance variableInstance = (VariableInstance) variableInstances
					.remove(name);
			if (variableInstance != null) {
				variableInstance.removeReferences();
			}
		}
	}

	void addUpdatedVariableContainer() {
		ContextInstance contextInstance = getContextInstance();
		if (contextInstance != null) {
			if (contextInstance.updatedVariableContainers == null) {
				contextInstance.updatedVariableContainers = new ArrayList<VariableContainer>();
			}
			contextInstance.updatedVariableContainers.add(this);
		}
	}

	public ContextInstance getContextInstance() {
		Token token = getToken();
		ProcessInstance processInstance = (token != null ? token
				.getProcessInstance() : null);
		return (processInstance != null ? processInstance.getContextInstance()
				: null);
	}

	public static Collection<VariableContainer> getUpdatedVariableContainers(
			ProcessInstance processInstance) {
		return processInstance.getContextInstance().updatedVariableContainers;
	}

}

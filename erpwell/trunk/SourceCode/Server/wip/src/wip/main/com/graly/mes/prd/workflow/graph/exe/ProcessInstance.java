package com.graly.mes.prd.workflow.graph.exe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.context.def.WFParameter;
import com.graly.mes.prd.workflow.context.exe.ContextInstance;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.def.Transition;
import com.graly.mes.prd.workflow.module.def.ModuleDefinition;
import com.graly.mes.prd.workflow.module.exe.ModuleInstance;
import com.graly.mes.prd.workflow.util.Clock;
import com.graly.mes.prd.workflow.util.StringUtil;

/**
 * is one execution of a {@link com.graly.mes.prd.workflow.graph.def.ProcessDefinition}.
 * To create a new process execution of a process definition, just use the 
 * {@link #ProcessInstance(ProcessDefinition)}.
 */
@Entity
@Table(name="WF_PROCESSINSTANCE")
public class ProcessInstance extends ADBase {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="INSTANCE_KEY")
	protected Long instanceKey;
	
	@Column(name="START_TIME")
	protected Date startTime;
	
	@Column(name="END_TIME")
	protected Date endTime;
	
	@Column(name="IS_SUSPENDED")
	protected String isSuspended = "N";
	
	@ManyToOne
	@JoinColumn(name = "PROCESS_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	protected ProcessDefinition processDefinition = null;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "ROOT_TOKEN_RRN", referencedColumnName = "OBJECT_RRN")
	private Token rootToken = null;
	
	@ManyToOne
	@JoinColumn(name = "SUPER_PROCESS_TOKEN_RRN", referencedColumnName = "OBJECT_RRN")
	protected Token superProcessToken = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@MapKey(name = "name")
	@JoinColumn(name = "PROCESS_INSTANCE_RRN", referencedColumnName = "OBJECT_RRN")
	protected Map<String, ModuleInstance> instances = null;
	
	@Transient
	protected Map<String, ModuleInstance> transientInstances = null;
	
	@Transient
	protected List<ProcessInstance> cascadeProcessInstances = null;
	  
	public ProcessInstance() {
	}

	/**
	 * creates a new process instance for the given process definition, puts the
	 * root-token (=main path of execution) in the start state and executes the
	 * initial node. In case the initial node is a start-state, it will behave
	 * as a wait state. For each of the optional module definitions contained in
	 * the {@link ProcessDefinition}, the corresponding module instance will be
	 * created.
	 * 
	 * @param variables
	 *            will be inserted into the context variables after the context
	 *            submodule has been created and before the process-start event
	 *            is fired, which is also before the execution of the initial
	 *            node.
	 * @throws JbpmException
	 *             if processDefinition is null.
	 */

	public ProcessInstance(ProcessDefinition processDefinition, Map<String, Object> variables, Long key) {
		this(processDefinition, variables, null, key);
	}
	  
	public ProcessInstance(ProcessDefinition processDefinition, Map<String, Object> variables, Map<String, Object> transientVariables, Long key) {
		if (processDefinition == null)
			throw new JbpmException("can't create a process instance when processDefinition is null");

		// initialize the members
		this.processDefinition = processDefinition;
		this.setRootToken(new Token(this));
		this.setInstanceKey(key);
		this.setOrgRrn(processDefinition.getOrgRrn());
		this.setIsActive(processDefinition.getIsActive());
		
		ExecutionContext.getPrdManager().assignId(this);
		
		// create the optional definitions
		Map<String, ModuleDefinition> definitions = processDefinition.getDefinitions();
		// if the state-definition has optional definitions
		if (definitions != null) {
			instances = new HashMap<String, ModuleInstance>();
			// loop over each optional definition
			Iterator<ModuleDefinition> iter = definitions.values().iterator();
			while (iter.hasNext()) {
				ModuleDefinition definition = (ModuleDefinition) iter.next();
				// and create the corresponding optional instance
				ModuleInstance instance = definition.createInstance();
				if (instance != null) {
					addInstance(instance);
				}
			}
		}
		// set the variables
		ContextInstance contextInstance = getContextInstance();
		if (contextInstance != null) {
			Map<String, Object> variablesMap = new HashMap<String, Object>();
			List<WFParameter> variableSet = processDefinition.getWfParameters();
			if (variableSet != null) {
				for (WFParameter variable : variableSet) {
					variablesMap.put(variable.getVariableName(), 
							StringUtil.toTypeObject(variable.getDefaultValue(), variable.getType()));
				}
			}
			if (variables != null) {
				variablesMap.putAll(variables);
			}
			contextInstance.addVariables(variablesMap);
			if (transientVariables != null) {
				contextInstance.addTransientVariables(transientVariables);
			}
		}
	}
	
	public void init() {
		Node initialNode = getRootToken().getNode();
		// fire the process start event
		if (initialNode != null) {
			ExecutionContext executionContext = new ExecutionContext(getRootToken());
			// execute the start node
			initialNode.enter(executionContext);
		}
	}
	  // optional module instances
		// ////////////////////////////////////////////////

	/**
	 * adds the given optional moduleinstance (bidirectional).
	 */
	public ModuleInstance addInstance(ModuleInstance moduleInstance) {
		if (moduleInstance == null)
			throw new IllegalArgumentException(
					"can't add a null moduleInstance to a process instance");
		if (instances == null)
			instances = new HashMap<String, ModuleInstance>();
		moduleInstance.setName(moduleInstance.getClass().getName());
		instances.put(moduleInstance.getClass().getName(), moduleInstance);
		moduleInstance.setProcessInstance(this);
		return moduleInstance;
	}

	/**
	 * removes the given optional moduleinstance (bidirectional).
	 */
	public ModuleInstance removeInstance(ModuleInstance moduleInstance) {
		ModuleInstance removedModuleInstance = null;
		if (moduleInstance == null)
			throw new IllegalArgumentException(
					"can't remove a null moduleInstance from a process instance");
		if (instances != null) {
			removedModuleInstance = (ModuleInstance) instances.remove(moduleInstance.getClass().getName());
			if (removedModuleInstance != null) {
				moduleInstance.setProcessInstance(null);
			}
		}
		return removedModuleInstance;
	}

	/**
	 * looks up an optional module instance by its class.
	 */
	public ModuleInstance getInstance(Class clazz) {
		ModuleInstance moduleInstance = null;
		if (instances != null) {
			moduleInstance = (ModuleInstance) instances.get(clazz.getName());
		}

		if (moduleInstance == null) {
			if (transientInstances == null)
				transientInstances = new HashMap<String, ModuleInstance>();

			// client requested an instance that is not in the map of instances.
			// so we can safely assume that the client wants a transient
			// instance
			moduleInstance = (ModuleInstance) transientInstances.get(clazz.getName());
			if (moduleInstance == null) {
				try {
					moduleInstance = (ModuleInstance) clazz.newInstance();
					moduleInstance.setProcessInstance(this);

				} catch (Exception e) {
					throw new JbpmException(
							"couldn't instantiate transient module '" + clazz.getName()	+ "' with the default constructor");
				}
				transientInstances.put(clazz.getName(), moduleInstance);
			}
		}
		return moduleInstance;
	}

	/**
	 * process instance extension for process variableInstances.
	 */
	public ContextInstance getContextInstance() {
		return (ContextInstance) getInstance(ContextInstance.class);
	}
		
    /**
	 * instructs the main path of execution to continue by taking the default
	 * transition on the current node.
	 * 
	 * @throws IllegalStateException
	 *             if the token is not active.
	 */
	public void signal() {
		if (hasEnded()) {
			throw new IllegalStateException("couldn't signal token : token has ended");
		}
		getRootToken().signal();
	}

	/**
	 * instructs the main path of execution to continue by taking the specified
	 * transition on the current node.
	 * 
	 * @throws IllegalStateException
	 *             if the token is not active.
	 */
	public void signal(String transitionName) {
		if (hasEnded()) {
			throw new IllegalStateException(
					"couldn't signal token : token has ended");
		}
		getRootToken().signal(transitionName);
	}

	/**
	 * instructs the main path of execution to continue by taking the specified
	 * transition on the current node.
	 * 
	 * @throws IllegalStateException
	 *             if the token is not active.
	 */
	public void signal(Transition transition) {
		if (hasEnded()) {
			throw new IllegalStateException(
					"couldn't signal token : token has ended");
		}
		getRootToken().signal(transition);
	}
	  
	/**
	 * ends (=cancels) this process instance and all the tokens in it.
	 */
	public void end() {
		// end the main path of execution
		getRootToken().end();

		if (getEndTime() == null) {
			// mark this process instance as ended
			setEnd(Clock.getCurrentTime());

			// fire the process-end event
			ExecutionContext executionContext = new ExecutionContext(getRootToken());

			// check if this process was started as a subprocess of a super process
			if (getSuperProcessToken() != null) {
				addCascadeProcessInstance(getSuperProcessToken().getProcessInstance());

				ExecutionContext superExecutionContext = new ExecutionContext(getSuperProcessToken());
				superExecutionContext.setSubProcessInstance(this);
				getSuperProcessToken().signal(superExecutionContext);
			}
		}
	}
	
	/**
	 * suspends this execution. This will make sure that tasks, timers and
	 * messages related to this process instance will not show up in
	 * database queries.
	 * 
	 * @see #resume()
	 */
	public void suspend() {
		setIsSuspended(true);
		getRootToken().suspend();
	}
	
	  /**
		 * resumes a suspended execution. All timers that have been suspended
		 * might fire if the duedate has been passed. If an admin resumes a
		 * process instance, the option should be offered to update, remove and
		 * create the timers and messages related to this process instance.
		 * 
		 * @see #suspend()
		 */
	public void resume() {
		setIsSuspended(false);
		getRootToken().resume();
	}
	
	  /**
	   * tells if this process instance is still active or not.
	   */
	  public boolean hasEnded() {
	    return ( getEndTime() != null );
	  }
	  
	  
	/**
	 * calculates if this process instance has still options to continue.
	 */
	public boolean isTerminatedImplicitly() {
		boolean isTerminatedImplicitly = true;
		if (getEndTime() == null) {
			isTerminatedImplicitly = getRootToken().isTerminatedImplicitly();
		}
		return isTerminatedImplicitly;
	}
	
    /**
	 * looks up the token in the tree, specified by the slash-separated
	 * token path.
	 * 
	 * @param tokenPath is a slash-separated name that specifies a token in the tree.
	 * @return the specified token or null if the token is not found.
	 */
	public Token findToken(String tokenPath) {
		return (getRootToken() != null ? getRootToken().findToken(tokenPath) : null);
	}

	/**
	 * collects all instances for this process instance.
	 */
	public List findAllTokens() {
		List tokens = new ArrayList();
		tokens.add(getRootToken());
		getRootToken().collectChildrenRecursively(tokens);
		return tokens;
	}
	  
	public void addCascadeProcessInstance(ProcessInstance cascadeProcessInstance) {
		if (cascadeProcessInstances == null) {
			cascadeProcessInstances = new ArrayList<ProcessInstance>();
		}
		if (!cascadeProcessInstances.contains(cascadeProcessInstance)) {
			cascadeProcessInstances.add(cascadeProcessInstance);
		}
	}

	public Collection<ProcessInstance> removeCascadeProcessInstances() {
		Collection<ProcessInstance> removed = cascadeProcessInstances;
		cascadeProcessInstances = null;
		return removed;
	}
	
	// equals hack to support comparing hibernate proxies against the real objects
	// since this always falls back to ==, we don't need to overwrite the
	// hashcode
//	public boolean equals(Object o) {
//		return EqualsUtil.equals(this, o);
//	}
	
	public void setIsSuspended(Boolean isSuspended) {
		this.isSuspended = isSuspended ?  "Y" : "N";
	}

	public Boolean getIsSuspended() {
		return "Y".equalsIgnoreCase(this.isSuspended) ? true : false; 
	}
	
	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}
	
	public void setSuperProcessToken(Token superProcessToken) {
		this.superProcessToken = superProcessToken;
	}
	public Token getSuperProcessToken() {
		return superProcessToken;
	}

	public void setInstanceKey(Long instanceKey) {
		this.instanceKey = instanceKey;
	}

	public Long getInstanceKey() {
		return instanceKey;
	}

	public void setRootToken(Token rootToken) {
		this.rootToken = rootToken;
	}

	public Token getRootToken() {
		return rootToken;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setEnd(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return endTime;
	}

}

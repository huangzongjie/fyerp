package com.graly.mes.prd.workflow.graph.exe;

import java.util.Stack;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;

import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.context.exe.ContextInstance;
import com.graly.mes.prd.workflow.graph.def.GraphElement;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.def.Transition;
import com.graly.mes.prd.workflow.module.def.ModuleDefinition;
import com.graly.mes.prd.workflow.module.exe.ModuleInstance;
import com.graly.mes.wip.client.LotManager;
import com.graly.mes.wip.model.Lot;

public class ExecutionContext {

	protected Token token = null;
	protected GraphElement eventSource = null;
	protected Transition transition = null;
	protected Node transitionSource = null;
	private ProcessInstance subProcessInstance = null;
	private static PrdManager prdManager;
	private LotManager lotManager;
	private Lot lot;
	
	public ExecutionContext(Token token) {
		this.setToken(token);
	}

	public ExecutionContext(ExecutionContext other) {
		this.setToken(other.getToken());
	}
	
	public Node getNode() {
		return token.getNode();
	}
	  
	public ProcessDefinition getProcessDefinition() {
		ProcessInstance processInstance = getProcessInstance();
		return (processInstance != null ? processInstance.getProcessDefinition() : null);
	}

	public ProcessInstance getProcessInstance() {
		return token.getProcessInstance();
	}

	public String toString() {
		return "ExecutionContext[" + token + "]";
	}
	  
	/**
	 * set a process variable.
	 */
	public void setVariable(String name, Object value) {
		getContextInstance().setVariable(name, value, token);
	}

	/**
	 * get a process variable.
	 */
	public Object getVariable(String name) {
		return getContextInstance().getVariable(name, token);
	}
	
    /**
	 * leave this node over the default transition. This method is only
	 * available on node actions. Not on actions that are executed on
	 * events. Actions on events cannot change the flow of execution.
	 */
	public void leaveNode() {
		getNode().leave(this);
	}

	/**
	 * leave this node over the given transition. This method is only available
	 * on node actions. Not on actions that are executed on events. Actions on
	 * events cannot change the flow of execution.
	 */
	public void leaveNode(String transitionName) {
		getNode().leave(this, transitionName);
	}

	/**
	 * leave this node over the given transition. This method is only available
	 * on node actions. Not on actions that are executed on events. Actions on
	 * events cannot change the flow of execution.
	 */
	public void leaveNode(Transition transition) {
		getNode().leave(this, transition);
	}
	
	public ModuleDefinition getDefinition(Class clazz) {
		return getProcessDefinition().getDefinition(clazz);
	}
	  
	public ModuleInstance getInstance(Class clazz) {
		ProcessInstance processInstance = (token != null ? token.getProcessInstance() : null);
		return (processInstance != null ? processInstance.getInstance(clazz) : null);
	}

	public ContextInstance getContextInstance() {
		return (ContextInstance) getInstance(ContextInstance.class);
	}
	  
	public void setToken(Token token) {
		this.token = token;
	}

	public Token getToken() {
		return token;
	}
	
	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	public Transition getTransition() {
		return transition;
	}

	public void setTransitionSource(Node transitionSource) {
		this.transitionSource = transitionSource;
	}

	public Node getTransitionSource() {
		return transitionSource;
	}
	
	public void setSubProcessInstance(ProcessInstance subProcessInstance) {
		this.subProcessInstance = subProcessInstance;
	}

	public ProcessInstance getSubProcessInstance() {
		return subProcessInstance;
	}
	
	public static PrdManager getPrdManager() {
		if (prdManager == null) {
			try {
				InitialContext context = new InitialContext();
				prdManager = (PrdManager)context.lookup("MESwell/PrdManagerBean/local");
			} catch (Exception e) {
			}
		}
		return prdManager;
	}
	
	public LotManager getLotManager() {
		if (lotManager == null) {
			try {
				InitialContext context = new InitialContext();
				lotManager = (LotManager)context.lookup("MESwell/LotManagerBean/local");
			} catch (Exception e) {
			}
		}
		return lotManager;
	}
	
	public EntityManager getEntityManager() {
		return getLotManager().getEntityManager();
	}
	
	public Lot getLot() {
		try {
			LotManager lotManager = getLotManager();
			return lotManager.getLot(token.getProcessInstance().getInstanceKey());
		} catch (Exception e) {
		}
		return null;
	}
	
	 // thread local execution context
	static ThreadLocal<Stack<ExecutionContext>> threadLocalContextStack = new ThreadLocal<Stack<ExecutionContext>>();

	static Stack<ExecutionContext> getContextStack() {
		Stack<ExecutionContext> stack = (Stack<ExecutionContext>) threadLocalContextStack.get();
		if (stack == null) {
			stack = new Stack<ExecutionContext>();
			threadLocalContextStack.set(stack);
		}
		return stack;
	}

	public static void pushCurrentContext(ExecutionContext executionContext) {
		getContextStack().push(executionContext);
	}

	public static void popCurrentContext(ExecutionContext executionContext) {
		if (getContextStack().pop() != executionContext) {
			throw new JbpmException(
					"current execution context mismatch.  make sure that every pushed context gets popped");
		}
	}

	public static ExecutionContext currentExecutionContext() {
		ExecutionContext executionContext = null;
		Stack<ExecutionContext> stack = getContextStack();
		if (!stack.isEmpty()) {
			executionContext = (ExecutionContext) stack.peek();
		}
		return executionContext;
	}
}

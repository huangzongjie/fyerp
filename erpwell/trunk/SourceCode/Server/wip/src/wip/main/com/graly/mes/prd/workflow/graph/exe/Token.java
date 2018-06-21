package com.graly.mes.prd.workflow.graph.exe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.action.exe.InstanceToken;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.def.Transition;
import com.graly.mes.prd.workflow.jpdl.el.impl.JbpmExpressionEvaluator;
import com.graly.mes.prd.workflow.util.Clock;

/**
 * represents one path of execution and maintains a pointer to a node 
 * in the {@link com.graly.mes.prd.workflow.graph.def.ProcessDefinition}.  Most common 
 * way to get a hold of the token objects is with {@link ProcessInstance#getRootToken()}
 * or {@link com.graly.mes.prd.workflow.graph.exe.ProcessInstance#findToken(String)}.
 */
@Entity
@Table(name="WF_TOKEN")
public class Token extends ADBase {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(Token.class);

	@Column(name="NAME")
	protected String name = null;
	
	@Column(name="START_TIME")
	protected Date startTime = null;
	
	@Column(name="END_TIME")
	protected Date endTime = null;
	
	@Column(name="NODE_ENTER")
	private Date nodeEnter = null;
	
	@ManyToOne
	@JoinColumn(name = "NODE_RRN", referencedColumnName = "OBJECT_RRN")
	private Node node = null;
	
	@ManyToOne
	@JoinColumn(name = "PARENT_RRN", referencedColumnName = "OBJECT_RRN")
	protected Token parent = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@MapKey(name = "name")
	@JoinColumn(name = "PARENT_RRN", referencedColumnName = "OBJECT_RRN")
	private Map<String, Token> children = null;
	
	@ManyToOne
	@JoinColumn(name = "SUPER_RRN", referencedColumnName = "OBJECT_RRN")
	private Token superToken = null;

	@ManyToOne
	@JoinColumn(name = "PROCESS_INSTANCE_RRN", referencedColumnName = "OBJECT_RRN")
	protected ProcessInstance processInstance = null;
	
	@ManyToOne
	@JoinColumn(name = "SUB_PROCESS_INSTANCE_RRN", referencedColumnName = "OBJECT_RRN")
	protected ProcessInstance subProcessInstance = null;
	
	@Column(name="IS_ABLE_TO_REACTIVATE_PARENT")
	protected String isAbleToReactivateParent = "Y";
	
	@Column(name="IS_TERMINATION_IMPLICIT")
	protected String isTerminationImplicit = "N";
	
	@Column(name="IS_SUSPENDED")
	protected String isSuspended = "N";

	@Column(name="LOCKER")
	String lock = null;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "INSTANCE_TOKEN_RRN", referencedColumnName = "OBJECT_RRN")
	protected InstanceToken instanceToken = null;

	public Token() {
	}

	/**
	 * creates a root token.
	 */
	public Token(ProcessInstance processInstance) {
		this.startTime = Clock.getCurrentTime();
		this.setProcessInstance(processInstance);
		this.setNode(processInstance.getProcessDefinition().getStartState());
		this.setIsTerminationImplicit(processInstance.getProcessDefinition().getIsTerminationImplicit());
	}
	
	/**
	 * creates a child token.
	 */
	public Token(Token parent, String name) {
		this.startTime = Clock.getCurrentTime();
		this.processInstance = parent.getProcessInstance();
		this.name = name;
		this.node = parent.getNode();
		this.parent = parent;
		parent.addChild(this);
		this.setIsTerminationImplicit(parent.getIsTerminationImplicit());

		// assign an id to this token before events get fired
//		Services.assignId(this);
	}
	
	void addChild(Token token) {
		if (getChildren() == null) {
			setChildren(new HashMap<String, Token>());
		}
		getChildren().put(token.getName(), token);
	}

	/**
	 * provides a signal to the token. this method activates this token and
	 * leaves the current state over the default transition.
	 */
	public void signal() {
		if (getNode() == null) {
			throw new JbpmException("token '" + this + "' can't be signalled cause it is currently not positioned in a node");
		}
		if (getNode().getDefaultLeavingTransition() == null) {
			try {
				// fire the event before-signal
				Node signalNode = getNode();

				// start calculating the next state
				getNode().leave(new ExecutionContext(this));

				// if required, check if this token is implicitly terminated
				checkImplicitTermination();

				// fire the event after-signal

			} finally {
			}
			return;
		}
		signal(getNode().getDefaultLeavingTransition(), new ExecutionContext(this));
	}

	/**
	 * provides a signal to the token. this leave the current state over the given
	 * transition name.
	 */
	public void signal(String transitionName) {
		if (getNode() == null) {
			throw new JbpmException("token '" + this + "' can't be signalled cause it is currently not positioned in a node");
		}
		Transition leavingTransition = getNode().getLeavingTransition(transitionName);
		if (leavingTransition == null) {
			throw new JbpmException("transition '" + transitionName	+ "' does not exist on " + getNode());
		}
		signal(leavingTransition, new ExecutionContext(this));
	}

	/**
	 * provides a signal to the token. this leave the current state over the given
	 * transition name.
	 */
	public void signal(Transition transition) {
		signal(transition, new ExecutionContext(this));
	}

	void signal(ExecutionContext executionContext) {
		signal(getNode().getDefaultLeavingTransition(), executionContext);
	}

	void signal(Transition transition, ExecutionContext executionContext) {
		if (transition == null) {
			throw new JbpmException("couldn't signal without specifying  a leaving transition : transition is null");
		}
		if (executionContext == null) {
			throw new JbpmException("couldn't signal without an execution context: executionContext is null");
		}
		 if (getIsSuspended()) {
			throw new JbpmException("can't signal token '" + name + "' (" + name + "): it is suspended");
		}
		if (isLocked()) {
			throw new JbpmException("this token is locked by " + lock);
		}
		    
		try {
			// fire the event before-signal
			Node signalNode = getNode();

			// start calculating the next state
			getNode().leave(executionContext, transition);

			// if required, check if this token is implicitly terminated
			checkImplicitTermination();

			// fire the event after-signal

		} finally {
		}
	}

	/**
	 * a set of all the leaving transitions on the current node for which
	 * the condition expression resolves to true.
	 */
	public Set<Transition> getAvailableTransitions() {
		Set<Transition> availableTransitions = new HashSet<Transition>();
		if (node != null) {
			addAvailableTransitionsOfNode(node, availableTransitions);
		}
		return availableTransitions;
	}

	/**
	 * adds available transitions of that node to the Set and after that calls
	 * itself recursivly for the SuperSate of the Node if it has a super state
	 */
	private void addAvailableTransitionsOfNode(Node currentNode, Set<Transition> availableTransitions) {
		List<Transition> leavingTransitions = currentNode.getLeavingTransitions();
		if (leavingTransitions != null) {
			Iterator<Transition> iter = leavingTransitions.iterator();
			while (iter.hasNext()) {
				Transition transition = (Transition) iter.next();
				String conditionExpression = transition.getCondition();
				if (conditionExpression != null) {
					Object result = JbpmExpressionEvaluator.evaluate(conditionExpression, new ExecutionContext(this));
					if ((result instanceof Boolean)	&& (((Boolean) result).booleanValue())) {
						availableTransitions.add(transition);
					}
				} else {
					availableTransitions.add(transition);
				}
			}
		}
	}
	  
    /**
	 * ends this token and all of its children (if any). this is the last active
	 * (=not-ended) child of a parent token, the parent token will be ended as
	 * well and that verification will continue to propagate.
	 */
	public void end() {
		end(true);
	}

	/**
	 * ends this token with optional parent ending verification.
	 * 
	 * @param verifyParentTermination
	 *            specifies if the parent token should be checked for
	 *            termination. if verifyParentTermination is set to true and
	 *            this is the last non-ended child of a parent token, the parent
	 *            token will be ended as well and the verification will continue
	 *            to propagate.
	 */
	public void end(boolean verifyParentTermination) {
		// if not already ended
		if (endTime == null) {
			// set the end date
			// the end date is also the flag that indicates that this token has
			// ended.
			this.endTime = Clock.getCurrentTime();

			if (getChildren() != null) {
				Iterator iter = getChildren().values().iterator();
				while (iter.hasNext()) {
					Token child = (Token) iter.next();
					if (!child.hasEnded()) {
						child.end();
					}
				}
			}
			
			if (parent!=null) {
			}
			 
			if (getSubProcessInstance() != null) {
				getSubProcessInstance().end();
			}
			if (verifyParentTermination) {
				// if this is the last active token of the parent,
				// the parent needs to be ended as well
				notifyParentOfTokenEnd();
			}
		}
	}
	
	/**
	 * notifies a parent that one of its nodeMap has ended.
	 */
	void notifyParentOfTokenEnd() {
		if (isRoot()) {
			processInstance.end();
		} else {

			if (!parent.hasActiveChildren()) {
				parent.end();
			}
		}
	}
	
	/**
	 * tells if this token has child tokens that have not yet ended.
	 */
	public boolean hasActiveChildren() {
		boolean foundActiveChildToken = false;
		// try and find at least one child token that is
		// still active (= not ended)
		if (getChildren() != null) {
			Iterator<Token> iter = getChildren().values().iterator();
			while ((iter.hasNext()) && (!foundActiveChildToken)) {
				Token child = (Token) iter.next();
				if (!child.hasEnded()) {
					foundActiveChildToken = true;
				}
			}
		}
		return foundActiveChildToken;
	}
	
	public String toString() {
		return "Token(" + getFullName() + ")";
	}

	public boolean hasEnded() {
		return (endTime != null);
	}

	public boolean isRoot() {
		return (parent == null);
	}

	public boolean hasParent() {
		return (parent != null);
	}

	public boolean hasChild(String name) {
		return (getChildren() != null ? getChildren().containsKey(name) : false);
	}

	public Token getChild(String name) {
		Token child = null;
		if (getChildren() != null) {
			child = (Token) getChildren().get(name);
		}
		return child;
	}

	public String getFullName() {
		if (parent == null) return "/";
		if (parent.getParent() == null) return "/"+name;
		return parent.getFullName() + "/" + name;
	}

	public List<Token> getChildrenAtNode(Node aNode) {
		List<Token> foundChildren = new ArrayList<Token>();
		getChildrenAtNode(aNode, foundChildren);
		return foundChildren;
	}

	void getChildrenAtNode(Node aNode, List<Token> foundTokens) {
		if(aNode.equals(node)) {
			foundTokens.add(this);
		}
		else if(getChildren() != null && !getChildren().isEmpty()) {
			for(Iterator<Token> it = getChildren().values().iterator(); it.hasNext();) {
				Token aChild = (Token)it.next();
				aChild.getChildrenAtNode(aNode, foundTokens);
			}
		}
	}

	public void collectChildrenRecursively(List<Token> tokens) {
		if (getChildren()!=null) {
			Iterator<Token> iter = getChildren().values().iterator();
			while (iter.hasNext()) {
				Token child = (Token) iter.next();
				tokens.add(child);
				child.collectChildrenRecursively(tokens);
			}
		}
	}

	public Token findToken(String relativeTokenPath) {
		if (relativeTokenPath == null)
			return null;
		String path = relativeTokenPath.trim();
		if (("".equals(path)) || (".".equals(path))) {
			return this;
		}
		if ("..".equals(path)) {
			return parent;
		}
		if (path.startsWith("/")) {
			Token root = processInstance.getRootToken();
			return root.findToken(path.substring(1));
		}
		if (path.startsWith("./")) {
			return findToken(path.substring(2));
		}
		if (path.startsWith("../")) {
			if (parent != null) {
				return parent.findToken(path.substring(3));
			}
			return null;
		}
		int slashIndex = path.indexOf('/');
		if (slashIndex == -1) {
			return (Token) (getChildren() != null ? getChildren().get(path) : null);
		}
		Token token = null;
		String name = path.substring(0, slashIndex);
		token = (Token) getChildren().get(name);
		if (token != null) {
			return token.findToken(path.substring(slashIndex + 1));
		}
		return null;
	}

	public Map<String, Token> getActiveChildren() {
		Map<String, Token> activeChildren = new HashMap<String, Token>();
		if (getChildren() != null) {
			Iterator<Map.Entry<String, Token>> iter = getChildren().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Token> entry = (Map.Entry<String, Token>) iter.next();
				Token child = (Token) entry.getValue();
				if (!child.hasEnded()) {
					String childName = (String) entry.getKey();
					activeChildren.put(childName, child);
				}
			}
		}
		return activeChildren;
	}

	public void checkImplicitTermination() {
		if (getIsTerminationImplicit() && node.hasNoLeavingTransitions()) {
			end();

			if (processInstance.isTerminatedImplicitly()) {
				processInstance.end();
			}
		}
	}

	public boolean isTerminatedImplicitly() {
		if (endTime != null) return true;

		Map<String, Transition> leavingTransitions = node.getLeavingTransitionsMap();
		if ((leavingTransitions != null) && (leavingTransitions.size() > 0)) {
			// ok: found a non-terminated token
			return false;
		}

		// loop over all active child tokens
		Iterator<Token> iter = getActiveChildren().values().iterator();
		while (iter.hasNext()) {
			Token child = (Token) iter.next();
			if (!child.isTerminatedImplicitly()) {
				return false;
			}
		}
		// if none of the above, this token is terminated implicitly
		return true;
	}

	/**
	 * suspends a process execution.
	 */
	public void suspend() {
		setIsSuspended(true);

		// propagate to child tokens
		if (getChildren()!=null) {
			Iterator<Token> iter = getChildren().values().iterator();
			while (iter.hasNext()) {
				Token child = (Token) iter.next();
				child.suspend(); 
			}
		}
	}

	  /**
		 * resumes a process execution.
		 */
	public void resume() {
		setIsSuspended(false);

		// propagate to child tokens
		if (getChildren() != null) {
			Iterator<Token> iter = getChildren().values().iterator();
			while (iter.hasNext()) {
				Token child = (Token) iter.next();
				child.resume();
			}
		}
	}
//	public boolean equals(Object o) {
//		return EqualsUtil.equals(this, o);
//	}
	
	public ProcessInstance createSubProcessInstance(ProcessDefinition subProcessDefinition, Map variables, Long key) {
		// create the new sub process instance
		subProcessInstance = new ProcessInstance(subProcessDefinition, variables, key);
		// bind the subprocess to the super-process-token
		setSubProcessInstance(subProcessInstance);
		subProcessInstance.setSuperProcessToken(this);
		if (subProcessInstance.getRootToken() != null) {
			subProcessInstance.getRootToken().setSuperToken(this);
		}
		subProcessInstance.init();
		// make sure the process gets saved during super process save
		processInstance.addCascadeProcessInstance(subProcessInstance);
		return subProcessInstance;
	}
	  
	/**
	 * locks a process instance for further execution. A locked token cannot
	 * continue execution. This is a non-persistent operation. This is used to
	 * prevent tokens being propagated during the execution of actions.
	 * 
	 * @see #unlock(String)
	 */
	public void lock(String lockOwnerId) {
		if (lockOwnerId == null) {
			throw new JbpmException("can't lock with null value for the lockOwnerId");
		}
		if ((lock != null) && (!lock.equals(lockOwnerId))) {
			throw new JbpmException("token '" + this.getObjectRrn() + "' can't be locked by '"
					+ lockOwnerId + "' cause it's already locked by '" + lock + "'");
		}
		logger.debug("token[" + this.getObjectRrn() + "] is locked by " + lockOwnerId);
		lock = lockOwnerId;
	}

	/**
	 * @see #lock(String)
	 */
	public void unlock(String lockOwnerId) {
		if (lock == null) {
			logger.warn("lock owner '" + lockOwnerId + "' tries to unlock token '"
					+ this.getObjectRrn() + "' which is not locked");
		} else if (!lock.equals(lockOwnerId)) {
			throw new JbpmException("'" + lockOwnerId
					+ "' can't unlock token '" + this.getObjectRrn()
					+ "' because it was already locked by '" + lock + "'");
		}
		logger.debug("token[" + this.getObjectRrn() + "] is unlocked by " + lockOwnerId);
		lock = null;
	}

	  
	public boolean isLocked() {
		return lock != null;
	}

	public String getName() {
		return name;
	}
	  
	public void setNode(Node node) {
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

	public Token getParent() {
		return parent;
	}

	public void setParent(Token parent) {
		this.parent = parent;
	}
		  
	public void setNodeEnter(Date nodeEnter) {
		this.nodeEnter = nodeEnter;
	}

	public Date getNodeEnter() {
		return nodeEnter;
	}

	public void setInstanceToken(InstanceToken instanceToken) {
		this.instanceToken = instanceToken;
	}

	public InstanceToken getInstanceToken() {
		return instanceToken;
	}
	
	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setSubProcessInstance(ProcessInstance subProcessInstance) {
		this.subProcessInstance = subProcessInstance;
	}

	public ProcessInstance getSubProcessInstance() {
		return subProcessInstance;
	}

	public void setIsAbleToReactivateParent(Boolean isAbleToReactivateParent) {
		this.isAbleToReactivateParent = isAbleToReactivateParent ?  "Y" : "N";
	}

	public Boolean getIsAbleToReactivateParent() {
		return "Y".equalsIgnoreCase(this.isAbleToReactivateParent) ? true : false; 
	}
	
	public void setIsTerminationImplicit(Boolean isTerminationImplicit) {
		this.isTerminationImplicit = isTerminationImplicit ?  "Y" : "N";
	}

	public Boolean getIsTerminationImplicit() {
		return "Y".equalsIgnoreCase(this.isTerminationImplicit) ? true : false; 
	}
	
	public void setIsSuspended(Boolean isSuspended) {
		this.isSuspended = isSuspended ?  "Y" : "N";
	}

	public Boolean getIsSuspended() {
		return "Y".equalsIgnoreCase(this.isSuspended) ? true : false; 
	}

	public void setChildren(Map<String, Token> children) {
		this.children = children;
	}

	public Map<String, Token> getChildren() {
		return children;
	}

	public void setSuperToken(Token superToken) {
		this.superToken = superToken;
	}

	public Token getSuperToken() {
		return superToken;
	}

}

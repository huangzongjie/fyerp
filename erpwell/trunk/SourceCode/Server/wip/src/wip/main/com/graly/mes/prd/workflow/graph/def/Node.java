package com.graly.mes.prd.workflow.graph.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.prd.workflow.graph.exe.Token;
import com.graly.mes.prd.workflow.jpdl.xml.JpdlXmlReader;
import com.graly.mes.prd.workflow.jpdl.xml.Parsable;
import com.graly.mes.prd.workflow.util.Clock;

@Entity
@Table(name="WF_NODE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 1)
@DiscriminatorValue("N")
public class Node extends GraphElement implements Parsable {

	static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(Node.class);
	
	@ManyToOne
	@JoinColumn(name = "PROCESS_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	private ProcessDefinition processDefinition = null;
	
	@Column(name="IS_ASYNC")
	protected String isAsync = "N";
	
	@Column(name="IS_ASYNC_EXCL")
	protected String isAsyncExclusive = "N";
	
	@Column(name="SEQ_NO")
	private Integer seqNo;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "fromSeqNo ASC")
	@JoinColumn(name = "FROM_RRN", referencedColumnName = "OBJECT_RRN")
	protected List<Transition> leavingTransitions = null;
	
	transient Map<String, Transition> leavingTransitionMap = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name = "TO_RRN", referencedColumnName = "OBJECT_RRN")
	protected Set<Transition> arrivingTransitions = null;
		  
	/**
	 * creates an unnamed node.
	 */
	public Node() {
	}

	/**
	 * creates a node with the given name.
	 */
	public Node(String name) {
		super(name);
	}
	
	public void read(Element nodeElement, JpdlXmlReader jpdlXmlReader) {
	}

	public void write(Element nodeElement) {
	}
		  
	// leaving transitions
	public List<Transition> getLeavingTransitions() {
		return leavingTransitions;
	}

	// leaving transitions
	public void setLeavingTransitions(List<Transition> leavingTransitions) {
		this.leavingTransitions = leavingTransitions;
	}
	/**
	 * are the leaving {@link Transition}s, mapped by their name (java.lang.String).
	 */
	public Map<String, Transition> getLeavingTransitionsMap() {
		if ((leavingTransitionMap == null) && (leavingTransitions != null)) {
			// initialize the cached leaving transition map
			leavingTransitionMap = new HashMap<String, Transition>();
			ListIterator<Transition> iter = leavingTransitions.listIterator(leavingTransitions.size());
			while (iter.hasPrevious()) {
				Transition leavingTransition = (Transition) iter.previous();
				leavingTransitionMap.put(leavingTransition.getName(), leavingTransition);
			}
		}
		return leavingTransitionMap;
	}

	/**
	 * creates a bidirection relation between this node and the given leaving transition.
	 * @throws IllegalArgumentException if leavingTransition is null.
	 */
	public Transition addLeavingTransition(Transition leavingTransition) {
		if (leavingTransition == null)
			throw new IllegalArgumentException(
					"can't add a null leaving transition to an node");
		if (leavingTransitions == null)
			leavingTransitions = new ArrayList<Transition>();
		for (int i = 0; i < leavingTransitions.size(); i++) {
			leavingTransitions.get(i).setFromSeqNo(i);
		}
		leavingTransition.setFromSeqNo(leavingTransitions.size());
		leavingTransitions.add(leavingTransition);
		leavingTransition.from = this;
		leavingTransitionMap = null;
		return leavingTransition;
	}

	/**
	 * removes the bidirection relation between this node and the given leaving transition.
	 * @throws IllegalArgumentException if leavingTransition is null.
	 */
	public void removeLeavingTransition(Transition leavingTransition) {
		if (leavingTransition == null)
			throw new IllegalArgumentException(
					"can't remove a null leavingTransition from an node");
		if (leavingTransitions != null) {
			if (leavingTransitions.remove(leavingTransition)) {
				leavingTransition.from = null;
				leavingTransitionMap = null;
			}
		}
	}

	/**
	 * checks for the presence of a leaving transition with the given name.
	 * @return true if this node has a leaving transition with the given name,
	 *         false otherwise.
	 */
	public boolean hasLeavingTransition(String transitionName) {
		if (leavingTransitions == null)
			return false;
		return getLeavingTransitionsMap().containsKey(transitionName);
	}

	/**
	 * retrieves a leaving transition by name. note that also the leaving
	 * transitions of the supernode are taken into account.
	 */
	public Transition getLeavingTransition(String transitionName) {
		Transition transition = null;
		if (leavingTransitions != null) {
			transition = (Transition) getLeavingTransitionsMap().get(
					transitionName);
		}
		return transition;
	}

	/**
	 * true if this transition has leaving transitions. 
	 */
	public boolean hasNoLeavingTransitions() {
		return (((leavingTransitions == null) || (leavingTransitions.size() == 0)));
	}

	/**
	 * generates a new name for a transition that will be added as a leaving transition. 
	 */
	public String generateNextLeavingTransitionName() {
		String name = null;
		if (leavingTransitions != null) {
			if (!containsName(leavingTransitions, null)) {
				name = null;
			} else {
				int n = 1;
				while (containsName(leavingTransitions, Integer.toString(n)))
					n++;
				name = Integer.toString(n);
			}
		}
		return name;
	}

	boolean containsName(List<Transition> leavingTransitions, String name) {
		Iterator<Transition> iter = leavingTransitions.iterator();
		while (iter.hasNext()) {
			Transition transition = (Transition) iter.next();
			if ((name == null) && (transition.getName() == null)) {
				return true;
			} else if ((name != null) && (name.equals(transition.getName()))) {
				return true;
			}
		}
		return false;
	}
	
	public Transition getDefaultLeavingTransition() {
		Transition defaultTransition = null;
		if ((leavingTransitions != null) && (leavingTransitions.size() > 0)) {
			defaultTransition = (Transition) leavingTransitions.get(0);
		} 
		return defaultTransition;
	}
	
	/**
	 * are the arriving transitions.
	 */
	public Set<Transition> getArrivingTransitions() {
		return arrivingTransitions;
	}

	/**
	 * are the arriving transitions.
	 */
	public void setArrivingTransitions(Set<Transition> arrivingTransitions) {
		this.arrivingTransitions = arrivingTransitions;
	}
	
	/**
	 * add a bidirection relation between this node and the given arriving
	 * transition.
	 * @throws IllegalArgumentException if t is null.
	 */
	public Transition addArrivingTransition(Transition arrivingTransition) {
		if (arrivingTransition == null)
			throw new IllegalArgumentException(
					"can't add a null arrivingTransition to a node");
		if (arrivingTransitions == null)
			arrivingTransitions = new HashSet<Transition>();
		arrivingTransitions.add(arrivingTransition);
		arrivingTransition.to = this;
		return arrivingTransition;
	}

	/**
	 * removes the bidirection relation between this node and the given arriving
	 * transition.
	 * @throws IllegalArgumentException if t is null.
	 */
	public void removeArrivingTransition(Transition arrivingTransition) {
		if (arrivingTransition == null)
			throw new IllegalArgumentException(
					"can't remove a null arrivingTransition from a node");
		if (arrivingTransitions != null) {
			if (arrivingTransitions.remove(arrivingTransition)) {
				arrivingTransition.to = null;
			}
		}
	}
	
	/**
	 * called by a transition to pass execution to this node.
	 */
	public void enter(ExecutionContext executionContext) {
		Token token = executionContext.getToken();

		// update the runtime context information
		token.setNode(this);

		// keep track of node entrance in the token, so that a node-log can be
		// generated at node leave time.
		token.setNodeEnter(Clock.getCurrentTime());

		// remove the transition references from the runtime context
		executionContext.setTransition(null);
		executionContext.setTransitionSource(null);

		// execute the node
		if (getIsAsync()) {
//			ExecuteNodeJob job = createAsyncContinuationJob(token);
//			MessageService messageService = (MessageService) Services
//					.getCurrentService(Services.SERVICENAME_MESSAGE);
//			messageService.send(job);
//			token.lock(job.toString());
		} else {
			execute(executionContext);
		}
	}
	
	/**
	 * override this method to customize the node behaviour.
	 */
	public void execute(ExecutionContext executionContext) {
		// let this node handle the token
		// the default behaviour is to leave the node over the default
		// transition.
		leave(executionContext);

	}
	  
	/**
	 * called by the implementation of this node to continue execution over the
	 * default transition.
	 */
	public void leave(ExecutionContext executionContext) {
		leave(executionContext, getDefaultLeavingTransition());
	}

	/**
	 * called by the implementation of this node to continue execution over the specified transition.
	 */
	public void leave(ExecutionContext executionContext, String transitionName) {
		Transition transition = getLeavingTransition(transitionName);
		if (transition == null) {
			throw new JbpmException("transition '" + transitionName
					+ "' is not a leaving transition of node '" + this + "'");
		}
		leave(executionContext, transition);
	}

	/**
	 * called by the implementation of this node to continue execution over the given transition.
	 */
	public void leave(ExecutionContext executionContext, Transition transition) {
		if (transition == null)
			throw new JbpmException("can't leave node '" + this
					+ "' without leaving transition");
		Token token = executionContext.getToken();
		token.setNode(this);
		executionContext.setTransition(transition);
		
		// update the runtime information for taking the transition
		// the transitionSource is used to calculate events on superstates
		executionContext.setTransitionSource(this);

		// take the transition
		transition.take(executionContext);
	}

	public ProcessDefinition getParent() {
		return processDefinition;
	}
	  
	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	public Integer getSeqNo() {
		return seqNo;
	}
	
	public void setIsAsync(Boolean isAsync) {
		this.isAsync = isAsync ?  "Y" : "N";
	}

	public Boolean getIsAsync() {
		return "Y".equalsIgnoreCase(this.isAsync) ? true : false; 
	}
	
	public void setIsAsyncExclusive(Boolean isAsyncExclusive) {
		this.isAsyncExclusive = isAsyncExclusive ?  "Y" : "N";
	}

	public Boolean getIsAsyncExclusive() {
		return "Y".equalsIgnoreCase(this.isAsyncExclusive) ? true : false; 
	}
}

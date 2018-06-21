package com.graly.mes.prd.workflow.jpdl.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.xml.sax.InputSource;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.context.def.WFParameter;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.NodeCollection;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.def.Transition;
import com.graly.mes.prd.workflow.graph.node.NodeTypes;
import com.graly.mes.prd.workflow.graph.node.StartState;

public class JpdlXmlReader implements ProblemListener {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(JpdlXmlReader.class);
	
	protected InputSource inputSource = null;
	protected List<Problem> problems = new ArrayList<Problem>();
	protected ProblemListener problemListener = null;
	protected ProcessDefinition processDefinition = null;
	protected String initialNodeName = null;
	protected Collection<Object[]> unresolvedTransitionDestinations = null;
	protected Collection<Object[]> unresolvedActionReferences = null;

	/**
	 * the parsed process definition as DOM tree (available after
	 * readProcessDefinition)
	 */
	protected Document document;

	public JpdlXmlReader(InputSource inputSource) {
		this.inputSource = inputSource;
	}

	public JpdlXmlReader(InputSource inputSource, ProblemListener problemListener) {
		this.inputSource = inputSource;
		this.problemListener = problemListener;
	}

	public JpdlXmlReader(Reader reader) {
		this(new InputSource(reader));
	}

	public void close() throws IOException {
		InputStream byteStream = inputSource.getByteStream();
		if (byteStream != null)
			byteStream.close();
		else {
			Reader charStream = inputSource.getCharacterStream();
			if (charStream != null)
				charStream.close();
		}
		document = null;
	}

	/**
	 * @deprecated Originally, this class extended java.io.Reader. This method
	 *             is reminiscent of those days.
	 */
	public int read(char[] cbuf, int off, int len) throws IOException {
		return 0;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void addProblem(Problem problem) {
		problems.add(problem);
		if (problemListener != null)
			problemListener.addProblem(problem);
	}

	public void addError(String description) {
		logger.error("invalid process xml: " + description);
		addProblem(new Problem(Problem.LEVEL_ERROR, description));
	}

	public void addError(String description, Throwable exception) {
		logger.error("invalid process xml: " + description, exception);
		addProblem(new Problem(Problem.LEVEL_ERROR, description, exception));
	}

	public void addWarning(String description) {
		logger.warn("process xml warning: " + description);
		addProblem(new Problem(Problem.LEVEL_WARNING, description));
	} 

	public ProcessDefinition readProcessDefinition(ProcessDefinition processDefinition) {
		// create a new definition
		this.processDefinition = processDefinition.createNewProcessDefinition(processDefinition);

		// initialize lists
		problems = new ArrayList<Problem>();
		unresolvedTransitionDestinations = new ArrayList<Object[]>();
		unresolvedActionReferences = new ArrayList<Object[]>();

		try {
			// parse the document into a dom tree
			document = JpdlParser.parse(inputSource, this);
			Element root = document.getRootElement();

			// read the process name
			parseProcessDefinitionAttributes(root);

			// get the process description
			String description = root.elementTextTrim("description");
			if (description != null) {
				processDefinition.setDescription(description);
			}

			// first pass: read most content
			readNodes(root, processDefinition);

			// second pass processing
			resolveTransitionDestinations();

		} catch (Exception e) {
			logger.error("couldn't parse process definition", e);
			addProblem(new Problem(Problem.LEVEL_ERROR, "couldn't parse process definition", e));
		}

		if (Problem.containsProblemsOfLevel(problems, Problem.LEVEL_ERROR)) {
			throw new JbpmException("process parse " + problems.toString());
		}

		if (problems != null) {
			Iterator<Problem> iter = problems.iterator();
			while (iter.hasNext()) {
				Problem problem = (Problem) iter.next();
				logger.warn("process parse warning: " + problem.getDescription());
			}
		}

		return processDefinition;
	}

	protected void parseProcessDefinitionAttributes(Element root) {
		processDefinition.setName(root.attributeValue("name"));
		initialNodeName = root.attributeValue("initial");
	}

	public void readNodes(Element element, NodeCollection nodeCollection) {
		Iterator nodeElementIter = element.elementIterator();
		while (nodeElementIter.hasNext()) {
			Element nodeElement = (Element) nodeElementIter.next();
			String nodeName = nodeElement.getName();
			// get the node type
			Class nodeType = NodeTypes.getNodeType(nodeName);
			if (nodeType != null) {
				Node node = null;
				try {
					// create a new instance
					node = (Node) nodeType.newInstance();
				} catch (Exception e) {
					logger.error("couldn't instantiate node '" + nodeName
							+ "', of type '" + nodeType.getName() + "'", e);
				}

				node.setProcessDefinition(processDefinition);
				node.setIsActive(true);
				node.setOrgRrn(processDefinition.getOrgRrn());
				// check for duplicate start-states
				if ((node instanceof StartState)
						&& (processDefinition.getStartState() != null)) {
					addError("max one start-state allowed in a process");

				} else {
					// read the common node parts of the element
					readNode(nodeElement, node, nodeCollection);
					// if the node is parsable
					// (meaning: if the node has special configuration to parse,
					// other then the
					// common node data)
					node.read(nodeElement, this);
				}
			}
		}
	}

	public List<WFParameter> readWfParameters(Element element) {
		List wfParameters = new ArrayList();
		Iterator iter = element.elementIterator("variable");
		while (iter.hasNext()) {
			Element variableElement = (Element) iter.next();

			String variableName = variableElement.attributeValue("name");
			if (variableName == null) {
				addProblem(new Problem(Problem.LEVEL_WARNING,
						"the name attribute of a variable element is required: "
								+ variableElement.asXML()));
			}
			String access = variableElement.attributeValue("access",
					"read,write");
			String mappedName = variableElement.attributeValue("mapped-name");

			wfParameters.add(new WFParameter(variableName, access,
					mappedName, null));
		}
		return wfParameters;
	}
	
	public void readNode(Element nodeElement, Node node, NodeCollection nodeCollection) {

		// first put the node in its collection. this is done so that the
		// setName later on will be able to differentiate between nodes
		// contained in
		// processDefinitions and nodes contained in superstates
		nodeCollection.addNode(node);

		// get the node name
		String name = nodeElement.attributeValue("name");
		if (name != null) {
			node.setName(name);
			// check if this is the initial node
			if ((initialNodeName != null) && (initialNodeName.equals(node.getName()))) {
				processDefinition.setStartState(node);
			}
		}

		// get the node description
		String description = nodeElement.elementTextTrim("description");
		if (description != null) {
			node.setDescription(description);
		}
		
		String asyncText = nodeElement.attributeValue("async");
		if ("true".equalsIgnoreCase(asyncText)) {
			node.setIsAsync(true);
		} else if ("exclusive".equalsIgnoreCase(asyncText)) {
			node.setIsAsync(true);
			node.setIsAsyncExclusive(true);
		}

		// save the transitions and parse them at the end
		addUnresolvedTransitionDestination(nodeElement, node);
	}
	
	// transition destinations are parsed in a second pass
	// //////////////////////

	public void addUnresolvedTransitionDestination(Element nodeElement, Node node) {
		unresolvedTransitionDestinations.add(new Object[] { nodeElement, node });
	}

	public void resolveTransitionDestinations() {
		Iterator iter = unresolvedTransitionDestinations.iterator();
		while (iter.hasNext()) {
			Object[] unresolvedTransition = (Object[]) iter.next();
			Element nodeElement = (Element) unresolvedTransition[0];
			Node node = (Node) unresolvedTransition[1];
			resolveTransitionDestinations(nodeElement.elements("transition"), node);
		}
	}

	public void resolveTransitionDestinations(List transitionElements, Node node) {
		Iterator iter = transitionElements.iterator();
		while (iter.hasNext()) {
			Element transitionElement = (Element) iter.next();
			resolveTransitionDestination(transitionElement, node);
		}
	}

	/**
	 * creates the transition object and configures it by the read attributes
	 * 
	 * @return the created
	 *         <code>com.graly.mes.prd.workflow.graph.def.Transition</code>
	 *         object (useful, if you want to override this method to read
	 *         additional configuration properties)
	 */
	public Transition resolveTransitionDestination(Element transitionElement, Node node) {
		Transition transition = new Transition();
		transition.setProcessDefinition(processDefinition);
		transition.setIsActive(true);
		transition.setOrgRrn(processDefinition.getOrgRrn());
		transition.setName(transitionElement.attributeValue("name"));
		transition.setDescription(transitionElement.elementTextTrim("description"));

		String condition = transitionElement.attributeValue("condition");
		if (condition == null) {
			Element conditionElement = transitionElement.element("condition");
			if (conditionElement != null) {
				condition = conditionElement.getTextTrim();
				// for backwards compatibility
				if ((condition == null) || (condition.length() == 0)) {
					condition = conditionElement.attributeValue("expression");
				}
			}
		}
		transition.setCondition(condition);

		// add the transition to the node
		node.addLeavingTransition(transition);

		// set destinationNode of the transition
		String toName = transitionElement.attributeValue("to");
		if (toName == null) {
			addWarning("node '"
					+ node.getName()
					+ "' has a transition without a 'to'-attribute to specify its destinationNode");
		} else {
			Node to = ((NodeCollection) node.getParent()).findNode(toName);
			if (to == null) {
				addWarning("transition to='" + toName + "' on node '"
						+ node.getName() + "' cannot be resolved");
			} else {
				to.addArrivingTransition(transition);
			}
		}
		
		return transition;
	}

	public String getProperty(String property, Element element) {
		String value = element.attributeValue(property);
		if (value == null) {
			Element propertyElement = element.element(property);
			if (propertyElement != null) {
				value = propertyElement.getText();
			}
		}
		return value;
	}
}

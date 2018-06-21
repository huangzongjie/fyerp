package com.graly.mes.prd.workflow.graph.def;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.xml.sax.InputSource;

import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.prd.model.Parameter;
import com.graly.mes.prd.workflow.JbpmConfiguration;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.context.def.ContextDefinition;
import com.graly.mes.prd.workflow.context.def.WFParameter;
import com.graly.mes.prd.workflow.file.def.FileDefinition;
import com.graly.mes.prd.workflow.graph.node.StartState;
import com.graly.mes.prd.workflow.jpdl.xml.JpdlXmlReader;
import com.graly.mes.prd.workflow.module.def.ModuleDefinition;
import com.graly.mes.prd.workflow.util.ClassLoaderUtil;

@Entity
@Table(name="WF_PROCESSDEFINITION")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 1)
public abstract class ProcessDefinition extends ADUpdatable implements NodeCollection {
	
	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_FROZNE = "Frozen";
	public static final String STATUS_UNFROZNE = "UnFrozen";
	public static final String STATUS_ACTIVE = "Active";
	public static final String STATUS_INACTIVE = "InActive";
	
	public static final String FLOW_DOCUMENT = "processdefinition.xml";
	public static final String FLOW_CONTENT = "gpd.xml";

	@Column(name = "NAME")
	protected String name;

	@Column(name = "DESCRIPTION")
	protected String description;
	
	@Column(name="VERSION")
	private Long version;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="ENG_OWNER")
	private String engOwner;
	
	@Column(name="COMMENTS")
	private String comments;

	@Column(name="IS_TERMINATION_IMPLICIT")
	private String isTerminationImplicit = "N";
	
	@Column(name="WORKCENTER_RRN")
	private Long workcenterRrn;
	
	@ManyToOne
	@JoinColumn(name = "WORKCENTER_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private WorkCenter workCenter;
	
	@Transient
	private String flowDocument;
	
	@Transient
	private String flowContent;
	
	@Transient
	private Long copyFrom;
	
	@Transient
	private List<Node> children;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name = "START_STATE_RRN", referencedColumnName = "OBJECT_RRN")
	protected Node startState = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "seqNo ASC")
	@JoinColumn(name = "PROCESS_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	protected List<Node> nodes = null;

	transient Map<String, Node> nodesMap = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@MapKey(name = "name")
	@JoinColumn(name = "PROCESS_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	protected Map<String, ModuleDefinition> definitions = null;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "seqNo ASC")
	@JoinColumn(name = "PROCESS_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	protected List<WFParameter> wfParameters = null;
	
	@Transient
	protected List<Parameter> parameters;
	
	public ProcessDefinition() {
	}
	
	
	public ProcessDefinition createNewProcessDefinition(ProcessDefinition processDefinition) {
		processDefinition.startState = null;
		processDefinition.nodes = null;
		processDefinition.definitions = null;
		String resource = JbpmConfiguration.Configs.getString("resource.default.modules");
		Properties defaultModulesProperties = ClassLoaderUtil.getProperties(resource);
		Iterator<Object> iter = defaultModulesProperties.keySet().iterator();
		processDefinition.definitions = new HashMap<String, ModuleDefinition>();
		while (iter.hasNext()) {
			String moduleClassName = (String) iter.next();
			try {
				ModuleDefinition moduleDefinition = (ModuleDefinition) ClassLoaderUtil.loadClass(moduleClassName).newInstance();
				moduleDefinition.setIsActive(true);
				moduleDefinition.setOrgRrn(processDefinition.getOrgRrn());
				processDefinition.addDefinition(moduleDefinition);
			} catch (Exception e) {
				throw new JbpmException("couldn't instantiate default module '"	+ moduleClassName + "'", e);
			}
		}

		return processDefinition;
	}

	/**
	 * parse a process definition from an xml string.
	 * @throws com.graly.mes.prd.workflow.jpdl.JpdlException if parsing reported an error.
	 */
	public static ProcessDefinition parseXmlString(String xml, ProcessDefinition processDefinition) {
		StringReader stringReader = new StringReader(xml);
		JpdlXmlReader jpdlReader = new JpdlXmlReader(new InputSource(stringReader));
		return jpdlReader.readProcessDefinition(processDefinition);
	}
	
	/**
	 * parse a process definition from an xml resource file.
	 * @throws com.graly.mes.prd.workflow.jpdl.JpdlException if parsing reported an error.
	 */
	public static ProcessDefinition parseXmlResource(String xmlResource, ProcessDefinition processDefinition) {
		InputStream resourceStream = ClassLoaderUtil.getStream(xmlResource);
		try {
			return parseXmlInputStream(resourceStream, processDefinition);
		} finally {
			if (resourceStream != null) {
				try {
					resourceStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	/**
	 * parse a process definition from an xml input stream.
	 * @throws com.graly.mes.prd.workflow.jpdl.JpdlException if parsing reported an error.
	 */
	public static ProcessDefinition parseXmlInputStream(InputStream inputStream, ProcessDefinition processDefinition) {
		JpdlXmlReader jpdlReader = new JpdlXmlReader(new InputSource(inputStream));
		return jpdlReader.readProcessDefinition(processDefinition);
	}	
	
	public List<Node> getNodes() {
		return nodes;
	}
	
	public Map<String, Node> getNodesMap() {
		if (nodesMap == null) {
			nodesMap = new HashMap<String, Node>();
			if (nodes != null) {
				Iterator<Node> iter = nodes.iterator();
				while (iter.hasNext()) {
					Node node = (Node) iter.next();
					nodesMap.put(node.getName(), node);
				}
			}
		}
		return nodesMap;
	}
	
	public Node getNode(String name) {
		if (nodes == null)
			return null;
		return (Node) getNodesMap().get(name);
	}
	
	public boolean hasNode(String name) {
		if (nodes == null)
			return false;
		return getNodesMap().containsKey(name);
	}
	
	public Node addNode(Node node) {
		if (node == null)
			throw new IllegalArgumentException("can't add a null node to a processdefinition");
		if (nodes == null) {
			nodes = new ArrayList<Node>();
		}
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setSeqNo(i);
		}
		node.setSeqNo(nodes.size());
		nodes.add(node);
		node.setProcessDefinition(this);
		nodesMap = null;

		if ((node instanceof StartState) && (this.getStartState() == null)) {
			this.setStartState(node);
			StartState state = (StartState)node;
			state.addProcessDefinition(this);
		}
		return node;
	}
	
	public Node removeNode(Node node) {
		Node removedNode = null;
		if (node == null)
			throw new IllegalArgumentException(
					"can't remove a null node from a process definition");
		if (nodes != null) {
			if (nodes.remove(node)) {
				removedNode = node;
				removedNode.setProcessDefinition(null);
				nodesMap = null;
			}
		}

		if (getStartState() == removedNode) {
			setStartState(null);
			StartState state = (StartState)removedNode;
			state.setBelongProcessDefinition(null);
		}
		return removedNode;
	}
	
	public void reorderNode(int oldIndex, int newIndex) {
		if ((nodes != null) && (Math.min(oldIndex, newIndex) >= 0)
				&& (Math.max(oldIndex, newIndex) < nodes.size())) {
			Node o = nodes.remove(oldIndex);
			nodes.add(newIndex, o);
		} else {
			throw new IndexOutOfBoundsException(
					"couldn't reorder element from index '" + oldIndex
							+ "' to index '" + newIndex + "' in nodeList '"
							+ nodes + "'");
		}
	}
	
	public String generateNodeName() {
		return generateNodeName(nodes);
	}
	
	public Node findNode(String hierarchicalName) {
		return findNode(this, hierarchicalName);
	}

	public static String generateNodeName(List<Node> nodes) {
		String name = null;
		if (nodes == null) {
			name = "1";
		} else {
			int n = 1;
			while (containsName(nodes, Integer.toString(n)))
				n++;
			name = Integer.toString(n);
		}
		return name;
	}
	
	static boolean containsName(List<Node> nodes, String name) {
		Iterator<Node> iter = nodes.iterator();
		while (iter.hasNext()) {
			Node node = (Node) iter.next();
			if (name.equals(node.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public static Node findNode(NodeCollection nodeCollection,
			String hierarchicalName) {
		Node node = null;
		if ((hierarchicalName != null) && (!"".equals(hierarchicalName.trim()))) {
			StringTokenizer tokenizer = new StringTokenizer(hierarchicalName, "/");
			while (tokenizer.hasMoreElements()) {
				String namePart = tokenizer.nextToken();
				if ("..".equals(namePart)) {
					if (nodeCollection instanceof ProcessDefinition) {
						throw new JbpmException(
								"couldn't find node '"
										+ hierarchicalName
										+ "' because of a '..' on the process definition.");
					}
				} else if (tokenizer.hasMoreElements()) {
					nodeCollection = (NodeCollection) nodeCollection.getNode(namePart);
				} else {
					node = nodeCollection.getNode(namePart);
				}
			}
		}
		return node;
	}
	
	public GraphElement getParent() {
		return null;
	}
	
	// module definitions ///////////////////////////////////////////////////////

	public ModuleDefinition addDefinition(ModuleDefinition moduleDefinition) {
		if (moduleDefinition == null)
			throw new IllegalArgumentException("can't add a null moduleDefinition to a process definition");
		if (definitions == null)
			definitions = new HashMap<String, ModuleDefinition>();
		definitions.put(moduleDefinition.getClass().getName(), moduleDefinition);
		moduleDefinition.setProcessDefinition(this);
		moduleDefinition.setName(moduleDefinition.getClass().getName());
		return moduleDefinition;
	}

	public ModuleDefinition removeDefinition(ModuleDefinition moduleDefinition) {
		ModuleDefinition removedDefinition = null;
		if (moduleDefinition == null)
			throw new IllegalArgumentException("can't remove a null moduleDefinition from a process definition");
		if (definitions != null) {
			removedDefinition = (ModuleDefinition) definitions.remove(moduleDefinition.getClass().getName());
			if (removedDefinition != null) {
				moduleDefinition.setProcessDefinition(null);
			}
		}
		return removedDefinition;
	}

	public ModuleDefinition getDefinition(Class clazz) {
		ModuleDefinition moduleDefinition = null;
		if (definitions != null) {
			moduleDefinition = (ModuleDefinition) definitions.get(clazz.getName());
		}
		return moduleDefinition;
	}

	public ContextDefinition getContextDefinition() {
		return (ContextDefinition) getDefinition(ContextDefinition.class);
	}

	public FileDefinition getFileDefinition() {
		return (FileDefinition) getDefinition(FileDefinition.class);
	}

	public Map<String, ModuleDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(Map<String, ModuleDefinition> definitions) {
		this.definitions = definitions;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getVersion() {
		return version;
	}
	
	public String getFullName() {
		return name + "." + version;
	}
	
	public void setStartState(Node startState) {
		this.startState = startState;
	}

	public Node getStartState() {
		return startState;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	public void setIsTerminationImplicit(Boolean isTerminationImplicit) {
		this.isTerminationImplicit = isTerminationImplicit ?  "Y" : "N";
	}

	public Boolean getIsTerminationImplicit() {
		return "Y".equalsIgnoreCase(this.isTerminationImplicit) ? true : false; 
	}

	public void setWfParameters(List<WFParameter> wfParameters) {
		if (wfParameters != null) {
			for (int i = 0; i < wfParameters.size(); i++) {
				wfParameters.get(i).setSeqNo(i);
			}
		}
		this.wfParameters = wfParameters;
	}

	public List<WFParameter> getWfParameters() {
		return wfParameters;
	}

	public void setEngOwner(String engOwner) {
		this.engOwner = engOwner;
	}

	public String getEngOwner() {
		return engOwner;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}
	
	public void setFlowDocument(String flowDocument) {
		this.flowDocument = flowDocument;
	}

	public String getFlowDocument() {
		return flowDocument;
	}
	
	public void setFlowContent(String flowContent) {
		this.flowContent = flowContent;
	}

	public String getFlowContent() {
		return flowContent;
	}
	
	public void setCopyFrom(Long copyFrom) {
		this.copyFrom = copyFrom;
	}

	public Long getCopyFrom() {
		return copyFrom;
	}
	
	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public List<Node> getChildren() {
		return children;
	}
	
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}
	
	public String initFlowDocument(String processName){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("<process-definition name=\"" + processName + "\" xmlns=\"urn:jbpm.org:jpdl-3.2\"></process-definition>");	
		return buffer.toString();
	}
	
	public String initFlowContent(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("<root-container></root-container>");	
		return buffer.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ProcessDefinition pf = (ProcessDefinition)super.clone();
		
		List<WFParameter> wfParameters = new ArrayList<WFParameter>();
		if (this.getWfParameters() != null) {
			for (WFParameter variable : this.getWfParameters()) {
				wfParameters.add((WFParameter)variable.clone());
			}
		}
		pf.setWfParameters(wfParameters);
		
		return pf;
	}


	public Long getWorkcenterRrn() {
		return workcenterRrn;
	}

	public void setWorkcenterRrn(Long workcenterRrn) {
		this.workcenterRrn = workcenterRrn;
	}
	
	public String getWorkCenterName() {
		if (this.workCenter != null) {
			return this.workCenter.getName();
		}
		return "";
	}

}

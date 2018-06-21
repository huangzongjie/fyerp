package com.graly.mes.prd.workflow.instantiation;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.jpdl.xml.JpdlXmlReader;
import com.graly.mes.prd.workflow.jpdl.xml.Parsable;
import com.graly.mes.prd.workflow.util.ClassLoaderUtil;

@Entity
@Table(name="WF_DELEGATION")
public class Delegation extends ADUpdatable implements Parsable, Serializable {

	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(Delegation.class);
	
	protected static Map<String, Instantiator> instantiatorCache = new HashMap<String, Instantiator>();
	static {
		instantiatorCache.put(null, new FieldInstantiator());
		instantiatorCache.put("field", new FieldInstantiator());
		instantiatorCache.put("bean", new BeanInstantiator());
		instantiatorCache.put("constructor", new ConstructorInstantiator());
		instantiatorCache.put("configuration-property", new ConfigurationPropertyInstantiator());
	}

	@Column(name = "CLASS_NAME")
	protected String className = null;
	
	@Column(name = "CONFIGURATION")
	protected String configuration = null;
	
	@Column(name = "CONFIG_TYPE")
	protected String configType = null;
	
	@ManyToOne
	@JoinColumn(name = "PROCESS_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	protected ProcessDefinition processDefinition = null;

	transient Object instance = null;

	public Delegation() {
	}

	public Delegation(Object instance) {
		this.instance = instance;
	}

	public Delegation(String className) {
		this.className = className;
	}

	public void read(Element delegateElement, JpdlXmlReader jpdlReader) {
		processDefinition = jpdlReader.getProcessDefinition();
		className = delegateElement.attributeValue("class");
		if (className == null) {
			jpdlReader.addWarning("no class specified in " + delegateElement.asXML());
		}

		configType = delegateElement.attributeValue("config-type");
		if (delegateElement.hasContent()) {
			try {
				StringWriter stringWriter = new StringWriter();
				// when parsing, it could be to store the config in the
				// database, so we want to make the configuration compact
				XMLWriter xmlWriter = new XMLWriter(stringWriter, OutputFormat.createCompactFormat());
				Iterator iter = delegateElement.content().iterator();
				while (iter.hasNext()) {
					Object node = iter.next();
					xmlWriter.write(node);
				}
				xmlWriter.flush();
				configuration = stringWriter.toString();
			} catch (IOException e) {
				jpdlReader.addWarning("io problem while parsing the configuration of " + delegateElement.asXML());
			}
		}
	}

	public void write(Element element) {
		element.addAttribute("class", className);
		element.addAttribute("config-type", configType);
		String configuration = this.configuration;
		if (configuration != null) {
			try {
				Element actionElement = DocumentHelper.parseText(
						"<action>" + configuration + "</action>").getRootElement();
				Iterator iter = new ArrayList(actionElement.content()).iterator();
				while (iter.hasNext()) {
					Node node = (Node) iter.next();
					node.setParent(null);
					element.add(node);
				}
			} catch (DocumentException e) {
				logger.error("couldn't create dom-tree for action configuration '" + configuration + "'", e);
			}
		}
	}

	public Object getInstance() {
		if (instance == null) {
			instance = instantiate();
		}
		return instance;
	}

	public Object instantiate() {

		Object newInstance = null;

		// find the classloader to use
		ClassLoader classLoader = ClassLoaderUtil.getProcessClassLoader(processDefinition);

		// load the class that needs to be instantiated
		Class clazz = null;
		try {
			clazz = classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			logger.error("couldn't load delegation class '" + className + "'", e);
		}

		Instantiator instantiator = null;
		try {
			// find the instantiator
			instantiator = (Instantiator) instantiatorCache.get(configType);
			if (instantiator == null) {
				// load the instantiator class
				Class instantiatorClass = classLoader.loadClass(configType);
				// instantiate the instantiator with the default constructor
				instantiator = (Instantiator) instantiatorClass.newInstance();
				instantiatorCache.put(configType, instantiator);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JbpmException("couldn't instantiate custom instantiator '" + configType + "'", e);
		}

		try {
			// instantiate the object
			newInstance = instantiator.instantiate(clazz, configuration);
		} catch (RuntimeException e) {
			logger.error("couldn't instantiate delegation class '" + className + "'", e);
		}

		return newInstance;
	}

	// equals
	// ///////////////////////////////////////////////////////////////////
	// hack to support comparing hibernate proxies against the real objects
	// since this always falls back to ==, we don't need to overwrite the
	// hashcode
//	public boolean equals(Object o) {
//		return EqualsUtil.equals(this, o);
//	}

	// getters and setters
	// //////////////////////////////////////////////////////

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String instantiatorType) {
		this.configType = instantiatorType;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}
}

package com.graly.mes.prd.workflow.graph.node;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.graly.mes.prd.workflow.JbpmConfiguration;
import com.graly.mes.prd.workflow.util.ClassLoaderUtil;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class NodeTypes {

	static final Logger logger = Logger.getLogger(NodeTypes.class);
	
	public static Set<String> getNodeTypes() {
		return nodeTypes.keySet();
	}

	public static Set<Class> getNodeNames() {
		return nodeNames.keySet();
	}

	public static Class getNodeType(String name) {
		return (Class) nodeTypes.get(name);
	}

	public static String getNodeName(Class type) {
		return (String) nodeNames.get(type);
	}

	static Map<String, Class> nodeTypes = initialiseNodeTypes();
	static Map<Class, String> nodeNames = createInverseMapping(nodeTypes);

	static Map<String, Class> initialiseNodeTypes() {
		Map<String, Class> types = new HashMap<String, Class>();

		String resource = JbpmConfiguration.Configs.getString("resource.node.types");
		InputStream actionTypesStream = ClassLoaderUtil.getStream(resource);
		Element nodeTypesElement = XmlUtil.parseXmlInputStream(
				actionTypesStream).getDocumentElement();
		Iterator nodeTypeIterator = XmlUtil.elementIterator(nodeTypesElement, "node-type");
		while (nodeTypeIterator.hasNext()) {
			Element nodeTypeElement = (Element) nodeTypeIterator.next();

			String elementTag = nodeTypeElement.getAttribute("element");
			String className = nodeTypeElement.getAttribute("class");
			try {
				Class nodeClass = ClassLoaderUtil.getClassLoader().loadClass(
						className);
				types.put(elementTag, nodeClass);

			} catch (Exception e) {
				if (!"org.jboss.seam.jbpm.Page".equals(className)) {
					logger.debug("node '" + elementTag
							+ "' will not be available. class '" + className
							+ "' couldn't be loaded");
				}
			}
		}
		return types;
	}

	public static Map<Class, String> createInverseMapping(Map<String, Class> map) {
		Map<Class, String> names = new HashMap<Class, String>();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Class> entry = (Map.Entry<String, Class>) iter.next();
			names.put(entry.getValue(), entry.getKey());
		}
		return names;
	}
}

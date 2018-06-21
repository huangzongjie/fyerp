package com.graly.mes.prd.workflow.configuration;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.util.XmlUtil;

public class ObjectFactoryParser implements Serializable {

	private static final long serialVersionUID = 1L;

	static Map<String, Constructor<ObjectInfo>> defaultMappings = null;

	public static Map<String, Constructor<ObjectInfo>> getDefaultMappings() {
		if (defaultMappings == null) {
			defaultMappings = new HashMap<String, Constructor<ObjectInfo>>();
			addMapping(defaultMappings, "bean", BeanInfo.class);
			addMapping(defaultMappings, "ref", RefInfo.class);
			addMapping(defaultMappings, "list", ListInfo.class);
			addMapping(defaultMappings, "map", MapInfo.class);
			addMapping(defaultMappings, "string", StringInfo.class);
			addMapping(defaultMappings, "int", IntegerInfo.class);
			addMapping(defaultMappings, "integer", IntegerInfo.class);
			addMapping(defaultMappings, "long", LongInfo.class);
			addMapping(defaultMappings, "float", FloatInfo.class);
			addMapping(defaultMappings, "double", DoubleInfo.class);
			addMapping(defaultMappings, "char", CharacterInfo.class);
			addMapping(defaultMappings, "character", CharacterInfo.class);
			addMapping(defaultMappings, "boolean", BooleanInfo.class);
			addMapping(defaultMappings, "true", BooleanInfo.class);
			addMapping(defaultMappings, "false", BooleanInfo.class);
			addMapping(defaultMappings, "null", NullInfo.class);
			addMapping(defaultMappings, "jbpm-type",    JbpmTypeObjectInfo.class);
		}
		return defaultMappings;
	}

	static final Class[] constructorParameterTypes = new Class[] {Element.class, ObjectFactoryParser.class };

	static void addMapping(Map<String, Constructor<ObjectInfo>> mappings, String elementTagName,
			Class objectInfoClass) {
		try {
			Constructor<ObjectInfo> constructor = objectInfoClass.getDeclaredConstructor(constructorParameterTypes);
			mappings.put(elementTagName, constructor);
		} catch (Exception e) {
			throw new JbpmException("couldn't add mapping for element '"
					+ elementTagName + "': constructor(" + Element.class.getName() + ","
					+ ObjectFactoryParser.class.getName()
					+ ") was missing for class '" + objectInfoClass.getName() + "'", e);
		}
	}

	public static ObjectFactoryImpl parseXmlString(String xml) {
		Element rootElement = XmlUtil.parseXmlText(xml).getDocumentElement();
		return createObjectFactory(rootElement);
	}

	public static ObjectFactoryImpl parseInputStream(InputStream xmlInputStream) {
		Element rootElement = XmlUtil.parseXmlInputStream(xmlInputStream)
				.getDocumentElement();
		return createObjectFactory(rootElement);
	}

	public static ObjectFactoryImpl parseResource(String resource) {
		Element rootElement = XmlUtil.parseXmlResource(resource).getDocumentElement();
		return createObjectFactory(rootElement);
	}

	public static ObjectFactoryImpl createObjectFactory(Element rootElement) {
		ObjectFactoryParser objectFactoryParser = new ObjectFactoryParser();
		List<ObjectInfo> objectInfos = new ArrayList<ObjectInfo>();
		List<Node> topLevelElements = XmlUtil.elements(rootElement);
		for (int i = 0; i < topLevelElements.size(); i++) {
			Element topLevelElement = (Element) topLevelElements.get(i);
			ObjectInfo objectInfo = objectFactoryParser.parse(topLevelElement);
			objectInfos.add(objectInfo);
		}
		return new ObjectFactoryImpl(objectFactoryParser.namedObjectInfos, objectInfos);
	}

	public void parseElementsFromResource(String resource, ObjectFactoryImpl objectFactoryImpl) {
		Element rootElement = XmlUtil.parseXmlResource(resource).getDocumentElement();
		parseElements(rootElement, objectFactoryImpl);
	}

	public void parseElementsStream(InputStream inputStream,
			ObjectFactoryImpl objectFactoryImpl) {
		Element rootElement = XmlUtil.parseXmlInputStream(inputStream).getDocumentElement();
		parseElements(rootElement, objectFactoryImpl);
	}

	public void parseElements(Element element, ObjectFactoryImpl objectFactoryImpl) {
		List<Node> objectInfoElements = XmlUtil.elements(element);
		for (int i = 0; i < objectInfoElements.size(); i++) {
			Element objectInfoElement = (Element) objectInfoElements.get(i);
			ObjectInfo objectInfo = parse(objectInfoElement);
			objectFactoryImpl.addObjectInfo(objectInfo);
		}
	}

	Map<String, Constructor<ObjectInfo>> mappings = null;
	Map<String, ObjectInfo> namedObjectInfos = null;

	public ObjectFactoryParser() {
		this(getDefaultMappings());
	}

	public ObjectFactoryParser(Map<String, Constructor<ObjectInfo>> mappings) {
		this.mappings = mappings;
		this.namedObjectInfos = new HashMap<String, ObjectInfo>();
	}

	public ObjectInfo parse(Element element) {
		ObjectInfo objectInfo = null;
		String elementTagName = element.getTagName().toLowerCase();
		Constructor<ObjectInfo> constructor = (Constructor<ObjectInfo>) mappings.get(elementTagName);
		if (constructor == null) {
			throw new JbpmException("no ObjectInfo class specified for element '" + elementTagName + "'");
		}
		try {
			objectInfo = (ObjectInfo)constructor.newInstance(new Object[] {element, this});
		} catch (Exception e) {
			throw new JbpmException("couldn't parse '" + elementTagName
					+ "' into a '" + constructor.getDeclaringClass().getName()
					+ "': " + XmlUtil.toString(element), e);
		}
		return objectInfo;
	}

	public void addNamedObjectInfo(String name, ObjectInfo objectInfo) {
		namedObjectInfos.put(name, objectInfo);
	}

	public void addMapping(String elementName, Class objectInfoClass) {
		if (mappings == getDefaultMappings()) {
			mappings = new HashMap(getDefaultMappings());
		}
		addMapping(mappings, elementName, objectInfoClass);
	}
}

package com.graly.mes.prd.designer.common.xml;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import com.graly.mes.prd.designer.common.Logger;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.model.SemanticElementFactory;
import com.graly.mes.prd.designer.common.registry.RegistryRegistry;
import com.graly.mes.prd.designer.common.registry.XmlAdapterRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XmlAdapterFactory extends AbstractAdapterFactory {

	private SemanticElementFactory semanticElementFactory;
	private XmlAdapterRegistry xmlAdapterRegistry;
	private Map adapterRegistry = new HashMap();	
	private Document document;
	
	public XmlAdapterFactory(Document document, SemanticElementFactory elementFactory) {
		super();
		this.document = document;
		this.semanticElementFactory = elementFactory;
		xmlAdapterRegistry = RegistryRegistry.getXmlAdapterRegistry(elementFactory.getEditorId());
		setAdapterKey(this);
		setShouldRegisterAdapter(true);
	}
	
	public XmlAdapter adapt(Node node) {
		return (XmlAdapter)super.adapt((INodeNotifier)node);
	}
	
	protected INodeAdapter createAdapter(INodeNotifier target) {
		if (document == null) throw new RuntimeException("The document property of the JpdlElementDomAdapterFactory is not initialized");
		XmlAdapter result = null;
		try {
			IConfigurationElement element = xmlAdapterRegistry.getConfigurationElementByXmlNode((Node)target);
			if (element != null) {
				result = (XmlAdapter)element.createExecutableExtension("adapterClass");
			} else if (((Node)target).getNodeType() != Node.TEXT_NODE){
				result = new GenericElementXmlAdapter();
			}
			if (result != null) {
				result.setNode((Node)target);
				result.setFactory(this);
			}
		} catch (CoreException e) {
			throw new RuntimeException("Creation of executable extension failed", e);
		}
		return result;
	}
	
	private String calculateElementName(IConfigurationElement configurationElement, SemanticElement semanticElement) {
		String elementName = configurationElement.getAttribute("xmlElement");
		if (elementName == null) {
			String nameProviderClass = configurationElement.getAttribute("nameProvider");
			if (nameProviderClass != null) {
				try {
					XmlAdapterNameProvider nameProvider = (XmlAdapterNameProvider)configurationElement.createExecutableExtension("nameProvider");
					if (nameProvider != null) {
						elementName = nameProvider.getName(semanticElement);
					}
				}
				catch (CoreException e) {
					Logger.logError("Problem creating nameProvider for " + semanticElement.getElementId(), e);
				}
			}
		}
		return elementName;
	}
	
	private XmlAdapter createAdapter(IConfigurationElement configurationElement, String elementName) {
		XmlAdapter result = null;
		try {
			result = (XmlAdapter) configurationElement.createExecutableExtension("adapterClass");
			INodeNotifier element = (INodeNotifier)document.createElement(elementName);
			element.addAdapter(result);
			result.setFactory(this);
			result = (XmlAdapter)adapt(element);
			result.setNode((Node)element);
		} catch (CoreException e) {
		    Logger.logError("Unable to create XML Adapter for " + elementName, e);
		}
		return result;
	}
	
	public XmlAdapter createAdapterFromModel(SemanticElement semanticElement) {
		IConfigurationElement configurationElement = 
			xmlAdapterRegistry.getConfigurationElementBySemanticElementId(semanticElement.getElementId());
		String elementName = calculateElementName(configurationElement, semanticElement);
		if (elementName != null) {
			return createAdapter(configurationElement, elementName);
		} else {
			return null;
		}
	}
	
	void register(XmlAdapter jpdlElementDomAdapter) {
		adapterRegistry.put(jpdlElementDomAdapter.getSemanticElement(), jpdlElementDomAdapter);
	}
	
	void unregister(XmlAdapter jpdlElementDomAdapter) {
		adapterRegistry.remove(jpdlElementDomAdapter.getSemanticElement());
	}
	
	XmlAdapter getRegisteredAdapterFor(SemanticElement jpdlElement) {
		return (XmlAdapter)adapterRegistry.get(jpdlElement);
	}
	
	SemanticElementFactory getSemanticElementFactory() {
		return semanticElementFactory;
	}
	
	XmlAdapterRegistry getXmlAdapterRegistry() {
		return xmlAdapterRegistry;
	}
	
}

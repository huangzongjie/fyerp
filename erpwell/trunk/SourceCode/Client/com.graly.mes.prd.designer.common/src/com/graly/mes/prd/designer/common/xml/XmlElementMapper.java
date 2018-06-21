package com.graly.mes.prd.designer.common.xml;

import org.w3c.dom.Node;

/**
 * Interface for acceptance filters to determine if a given Dom node can map to
 * a mapper's associated Semantic Element.
 * 
 * @author Matthew Sandoz
 */
public interface XmlElementMapper {

	/**
	 * Checks whether a given Dom Node conforms to the requirements for a
	 * specific Semantic Element type.
	 * 
	 * @param node Dom Node to check
	 * @return whether or not the mapper instance accepts the current node
	 */
	public boolean accept(Node node);

}

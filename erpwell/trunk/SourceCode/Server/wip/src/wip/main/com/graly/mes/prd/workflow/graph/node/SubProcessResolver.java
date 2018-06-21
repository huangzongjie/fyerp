package com.graly.mes.prd.workflow.graph.node;

import java.io.Serializable;

import org.dom4j.Element;

import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;

public interface SubProcessResolver extends Serializable {

	ProcessDefinition findSubProcess(Node parent, Element subProcessElement);

}

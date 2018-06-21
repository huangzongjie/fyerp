package com.graly.mes.prd.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.graly.framework.core.exception.ClientException;
import com.graly.mes.prd.model.Part;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.Process;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.def.Step;
import com.graly.mes.prd.workflow.graph.exe.ProcessInstance;
import com.graly.mes.prd.workflow.graph.exe.Token;
import com.graly.mes.prd.workflow.graph.node.StepState;
import com.graly.mes.wip.model.Lot;

public interface PrdManager {
	ProcessDefinition saveProcessDefinition(ProcessDefinition pf, long userRrn) throws ClientException;
	ProcessDefinition getLastVersionProcessDefinition(ProcessDefinition pf) throws ClientException;
	ProcessDefinition getActiveProcessDefinition(ProcessDefinition pf) throws JbpmException;
	void deleteProcessDefinition(ProcessDefinition pf) throws ClientException;
	ProcessDefinition getProcessDefinition(ProcessDefinition pf) throws ClientException;
	List<Node> getProcessDefinitionChildern(ProcessDefinition pf) throws ClientException;
	List<StepState> getStepChildren(ProcessDefinition pf) throws ClientException;
	
	ProcessDefinition frozen(ProcessDefinition pf, long userRrn) throws ClientException;
	ProcessDefinition unFrozen(ProcessDefinition pf, long userRrn) throws ClientException;
	ProcessDefinition active(ProcessDefinition pf, long userRrn) throws ClientException;
	
	Part savePart(Part part, long userRrn) throws ClientException;
	Part getLastVersionPart(Part part) throws ClientException;
	Part getActivePart(Part part) throws ClientException;
	Part frozen(Part part, long userRrn) throws ClientException;
	Part unFrozen(Part part, long userRrn) throws ClientException;
	Part active(Part part, long userRrn) throws ClientException;
	
	Process getPartProcess(Part part) throws ClientException;
	ProcessInstance startProcess(Lot lot) throws ClientException;
	ProcessInstance getProcessInstance(long processInstanceRrn) throws ClientException;
	void signalProcess(long processInstanceRrn) throws ClientException;
	ProcessInstance createProcessInstance(long processRrn, List<Node> nodeList, long instanceKey) throws ClientException;
	Stack<Token> getProcessTokenStack(long processInstanceRrn) throws ClientException;
	Token getCurrentToken(ProcessInstance processInstance) throws ClientException;
	ProcessInstance cloneProcessInstance(long processInstanceRrn) throws ClientException;
	List<Node> getProcessFlowList(long processInstanceId) throws ClientException;
	Map<String, Object> getCurrentParameter(long processInstanceRrn) throws ClientException;
	
	List<String> getActiveStepNames(long orgRrn) throws ClientException;
	Set<String> getStepNamesByUser(long orgRrn, long userRrn) throws ClientException;
	List<String> getStepNamesByUserGroup(long userGroupRrn) throws ClientException;
	List<Step> getActiveStepsByStage(long orgId, long stageRrn, long userId) throws ClientException;
	
	void assignId(Object object);
	BigDecimal getEquipmentByMould(BigDecimal objecrRrn) throws ClientException;
	List<Object[]> getMouldByEquipment(long orgRrn, long objecrRrn) throws ClientException; 
	BigDecimal getWorkCenterByEquipment(BigDecimal objecrRrn) throws ClientException; 
}

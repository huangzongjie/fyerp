package com.graly.mes.prd.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.security.model.ADUser;
import com.graly.framework.security.model.ADUserGroup;
import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.model.Part;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.context.exe.VariableContainer;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.Procedure;
import com.graly.mes.prd.workflow.graph.def.Process;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.graph.def.Step;
import com.graly.mes.prd.workflow.graph.def.Transition;
import com.graly.mes.prd.workflow.graph.exe.ProcessInstance;
import com.graly.mes.prd.workflow.graph.exe.Token;
import com.graly.mes.prd.workflow.graph.node.EndState;
import com.graly.mes.prd.workflow.graph.node.ProcedureState;
import com.graly.mes.prd.workflow.graph.node.StartState;
import com.graly.mes.prd.workflow.graph.node.StepState;
import com.graly.mes.prd.workflow.module.def.ModuleDefinition;
import com.graly.mes.prd.workflow.save.CascadeSaveOperation;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotParameter;

@Stateless
@Local(PrdManager.class)
@Remote(PrdManager.class)
public class PrdManagerBean implements PrdManager {
	 
	private static final Logger logger = Logger.getLogger(PrdManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB 
	private ADManager adManager;

	
	public ProcessDefinition saveProcessDefinition(ProcessDefinition pf, long userRrn) throws ClientException{
		try{
			Date now = new Date();
			pf.setIsActive(true);
			pf.setUpdatedBy(userRrn);
			if (pf instanceof Process || pf instanceof Procedure){
				pf = ProcessDefinition.parseXmlString(pf.getFlowDocument(), pf);
				pf.getFileDefinition().addFile(ProcessDefinition.FLOW_DOCUMENT, pf.getFlowDocument().getBytes());
				pf.getFileDefinition().addFile(ProcessDefinition.FLOW_CONTENT, pf.getFlowContent().getBytes());
			} else if (pf instanceof Step) {
				pf = ProcessDefinition.parseXmlString(((Step)pf).initStepDocument(), pf);
			}
			List<Node> nodes = pf.getNodes();
			
			if(nodes == null || nodes.size() == 0){
				throw new ClientException("error.flow_not_define");
			}
			
			if (pf.getObjectRrn() == null) {
				pf.setCreatedBy(userRrn);
				pf.setCreated(now);
				pf.setStatus(pf.STATUS_UNFROZNE);
				ProcessDefinition lastPf = getLastVersionProcessDefinition(pf);
				if (lastPf != null) {
					pf.setVersion(lastPf.getVersion() + 1);
				} else {
					pf.setVersion(1L);
				}
				em.persist(pf);
			} else {
				if (nodes != null) {
					for (Node node : nodes) {
						em.persist(node);
					}
				}
				Collection<ModuleDefinition> definitions = pf.getDefinitions().values();
				if (definitions != null) {
					for (ModuleDefinition definition : definitions) {
						em.persist(definition);
					}
				}
				em.merge(pf);
			}
			return pf;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}  
	}
	
	public ProcessDefinition getLastVersionProcessDefinition(ProcessDefinition pf) throws ClientException{
		StringBuffer sql = new StringBuffer();
		if (pf instanceof Process) {
			sql.append("SELECT pf FROM Process as pf");
		} else if (pf instanceof Procedure) {
			sql.append("SELECT pf FROM Procedure as pf");
		} else if (pf instanceof Step) {
			sql.append("SELECT pf FROM Step as pf");
		} else {
			return null;
		}
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND pf.name = ? ");
		sql.append(" ORDER BY pf.version DESC ");    
		logger.debug(sql);
		try{
			if (pf != null){
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, pf.getOrgRrn());
				query.setParameter(2, pf.getName());
				List<ProcessDefinition> pfList = (List<ProcessDefinition>)query.getResultList();
				if (pfList != null && pfList.size() > 0){
					return pfList.get(0);
				}
			} 
		} catch (NoResultException e){
			return null;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return null;
	}
	
	public ProcessDefinition getActiveProcessDefinition(ProcessDefinition pf) throws JbpmException {
		StringBuffer sql = new StringBuffer();
		if (pf instanceof Process) {
			sql.append("SELECT pf FROM Process as pf");
		} else if (pf instanceof Procedure) {
			sql.append("SELECT pf FROM Procedure as pf");
		} else if (pf instanceof Step) {
			sql.append("SELECT pf FROM Step as pf");
		} else {
			throw new JbpmException("Unsupport ProcessDefinition" + pf);
		}
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND pf.name = ? ");  
		sql.append(" AND pf.status = '");  
		sql.append(ProcessDefinition.STATUS_ACTIVE);
		sql.append("' ");
		logger.debug(sql);
		try {
			if (pf != null){
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, pf.getOrgRrn());
				query.setParameter(2, pf.getName());
				pf = (ProcessDefinition)query.getSingleResult();
				pf.getWfParameters().size();
				return pf;
			} 
		} catch (NoResultException e){
			return null;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new JbpmException(e);
		}
		return null;
	}
	
	public void deleteProcessDefinition(ProcessDefinition pf) throws ClientException{
		try{
			if (pf != null && pf.getObjectRrn() != null) {
				em.remove(pf);
			}
		} catch (EntityExistsException e){
			if (e.getCause() instanceof ConstraintViolationException) {
				logger.error(e.getMessage(), e);
				throw new ClientException("error.constraintviolation");
			}
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public ProcessDefinition getProcessDefinition(ProcessDefinition pf) throws ClientException {
		try{
			if (pf != null && pf.getObjectRrn() != null){
				pf = em.find(pf.getClass(), pf.getObjectRrn());
				if (pf == null) {
					throw new ClientException("ProcessDefinition object can not be null!");
				}
				if (pf instanceof Process || pf instanceof Procedure){
					pf.setFlowDocument(new String(pf.getFileDefinition().getBytes(ProcessDefinition.FLOW_DOCUMENT)));
					pf.setFlowContent(new String(pf.getFileDefinition().getBytes(ProcessDefinition.FLOW_CONTENT)));
					pf.setChildren(getProcessDefinitionChildern(pf));
				} else if (pf instanceof Step) {
					Step step = (Step)pf;
					if (step.getOperations() != null) {
						step.getOperations().size();
					}
				}
				pf.getWfParameters().size();
				return pf;
			} else {
				throw new ClientException("ProcessDefinition object can not be null!");
			}
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Node> getProcessDefinitionChildern(ProcessDefinition pf) throws ClientException {
		try{
			List<Node> children = new ArrayList<Node>();
			if (pf != null){
				if (!em.contains(pf)) {
					pf = getProcessDefinition(pf);
				}
				List<Node> nodes = pf.getNodes();
				StartState start = null;
				for (Node node : nodes) {
					if (node instanceof StartState) {
						start = (StartState)node;
					}
				}
				if (start == null) {
					throw new ClientException("Can not find start node!");
				} 
				children.add(start);
				Node current = start;
				while (!(current instanceof EndState)) {
					Transition transition = current.getDefaultLeavingTransition();
					if (transition == null) {
						throw new ClientException("Can not find transition!");
					} 
					current = transition.getTo();
					if (children.contains(current)) {
						throw new ClientException("Already contains node!" + current.getObjectRrn());
					}
					if (children instanceof ProcedureState) {
						ProcedureState procedureState = (ProcedureState)children;
						Procedure procedure = new Procedure();
						procedure.setOrgRrn(pf.getOrgRrn());
						procedure.setName(procedureState.getProcedureName());
						procedureState.setUsedProcedure((Procedure)getActiveProcessDefinition(procedure));
					} else if (children instanceof StepState) {
						StepState stepState = (StepState)children;
						Step step = new Step();
						step.setOrgRrn(pf.getOrgRrn());
						step.setName(stepState.getStepName());
						stepState.setUsedStep((Step)getActiveProcessDefinition(step));
					} 
					children.add(current);
				}
				return children;
			} else {
				throw new ClientException("Entity object can not be null!");
			}
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<StepState> getStepChildren(ProcessDefinition pf) throws ClientException {
		List<StepState> allChildren = new ArrayList<StepState>();
		if (pf instanceof Process || pf instanceof Procedure) {
			List<Node> children = getProcessDefinitionChildern(pf);
			for (Node node : children) {
				internalGetStepChildern(node, allChildren);
			}
		} 
		return allChildren;
	}
	
	protected void internalGetStepChildern(Node current, List<StepState> allChildren) throws ClientException {
		if (current instanceof StepState) {
			allChildren.add((StepState)current);
			return;
		}
		if (current instanceof ProcedureState) {
			List<Node> children = getProcessDefinitionChildern(((ProcedureState)current).getUsedProcedure());
			if (children != null) {
				for (int i = 0; i < children.size(); i++) {
					internalGetStepChildern(children.get(i), allChildren);
				}
			}
		}
	}
	
	public ProcessDefinition frozen(ProcessDefinition pf, long userRrn) throws ClientException {
		try{
			pf.setStatus(ProcessDefinition.STATUS_FROZNE);
			return saveProcessDefinition(pf, userRrn);
		} catch (Exception e){
			if (e instanceof ClientException) {
				throw (ClientException)e;
			}
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public ProcessDefinition unFrozen(ProcessDefinition pf, long userRrn) throws ClientException {
		try {
			pf.setUpdatedBy(userRrn);
			pf.setStatus(ProcessDefinition.STATUS_UNFROZNE);
			em.merge(pf);
			return pf;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public ProcessDefinition active(ProcessDefinition pf, long userRrn) throws ClientException {
		try {
			ProcessDefinition activePf = getActiveProcessDefinition(pf);
			if (activePf != null) {
				activePf.setStatus(ProcessDefinition.STATUS_INACTIVE);
				activePf.setUpdatedBy(userRrn);
				em.merge(activePf);
			}
			pf.setStatus(ProcessDefinition.STATUS_ACTIVE);
			pf.setUpdatedBy(userRrn);
			em.merge(pf);
			return pf;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Part savePart(Part part, long userRrn) throws ClientException {
		try{
			Date now = new Date();
			part.setIsActive(true);
			part.setUpdatedBy(userRrn);
			if (part.getObjectRrn() == null) {
				part.setCreatedBy(userRrn);
				part.setCreated(now);
				part.setStatus(Part.STATUS_UNFROZNE);
				Part lastPart = getLastVersionPart(part);
				if (lastPart != null) {
					part.setVersion(lastPart.getVersion() + 1);
				} else {
					part.setVersion(1L);
				}
				em.persist(part);
			} else {
				part = em.merge(part);
			}
			return part;
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Part getLastVersionPart(Part part) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT part FROM Part as part");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND part.name = ? ");
		sql.append(" ORDER BY part.version DESC ");    
		logger.debug(sql);
		try{
			if (part != null){
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, part.getOrgRrn());
				query.setParameter(2, part.getName());
				List<Part> partList = (List<Part>)query.getResultList();
				if (partList != null && partList.size() > 0){
					return partList.get(0);
				}
			} 
		} catch (NoResultException e){
			return null;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return null;
	}
	
	public Part getActivePart(Part part) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT part FROM Part as part");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND part.name = ? ");  
		sql.append(" AND part.status = '");  
		sql.append(ProcessDefinition.STATUS_ACTIVE);
		sql.append("' ");
		logger.debug(sql);
		try {
			if (part != null){
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, part.getOrgRrn());
				query.setParameter(2, part.getName());
				part = (Part)query.getSingleResult();
				return part;
			} 
		} catch (NoResultException e){
			return null;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new JbpmException(e);
		}
		return null;
	}
	
	public Part frozen(Part part, long userRrn) throws ClientException {
		try{
			part.setStatus(Part.STATUS_FROZNE);
			return savePart(part, userRrn);
		} catch (Exception e){
			if (e instanceof ClientException) {
				throw (ClientException)e;
			}
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Part unFrozen(Part part, long userRrn) throws ClientException {
		try {
			part.setUpdatedBy(userRrn);
			part.setStatus(Part.STATUS_UNFROZNE);
			em.merge(part);
			return part;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Part active(Part part, long userRrn) throws ClientException {
		try {
			Part activePart = getActivePart(part);
			if (activePart != null) {
				activePart.setStatus(Part.STATUS_INACTIVE);
				activePart.setUpdatedBy(userRrn);
				em.merge(activePart);
			}
			part.setStatus(Part.STATUS_ACTIVE);
			part.setUpdatedBy(userRrn);
			return em.merge(part);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Process getPartProcess(Part part) throws ClientException {
		Process process = null;
		if (part != null) {
			if (part.getProcessRrn() == null) {
				if (part.getProcessName() == null) {
					throw new ClientException("error.no_process_found");
				}
				process = new Process();
				process.setOrgRrn(part.getOrgRrn());
				process.setName(part.getProcessName());
				process = (Process)getActiveProcessDefinition(process);
			} else {
				process = em.find(Process.class, part.getProcessRrn());
			}
		}
		return process;
	}
	
	public ProcessInstance startProcess(Lot lot) throws ClientException {
		try{
			Part part = em.find(Part.class, lot.getPartRrn());
			Process process = getPartProcess(part);
			if (process == null) {
				throw new ClientException("no process found!");
			}
			Map<String, Object> paraMap = new HashMap<String, Object>();
			if (lot.getLotParameters() != null) {
				for (LotParameter para : lot.getLotParameters()){
					paraMap.put(para.getVariableName(), para.getDefaultValue());
				}
			}
			ProcessInstance processInstance = new ProcessInstance(process, paraMap, lot.getObjectRrn());
			processInstance.init();
			processInstance.signal();

			return savaProcess(processInstance);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	private ProcessInstance savaProcess(ProcessInstance processInstance) {
		CascadeSaveOperation save = new CascadeSaveOperation();
		save.save(processInstance, em);
		return processInstance;
	}
	
	public ProcessInstance getProcessInstance(long processInstanceRrn) throws ClientException {
		try {
			ProcessInstance processInstance = em.find(ProcessInstance.class, processInstanceRrn);
			return processInstance;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void signalProcess(long processInstanceRrn) throws ClientException {
		try {
			ProcessInstance processInstance = getProcessInstance(processInstanceRrn);

			Token currentToken = getCurrentToken(processInstance);
			currentToken.signal();
			savaProcess(processInstance);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public ProcessInstance createProcessInstance(long processRrn, List<Node> nodeList, long instanceKey) throws ClientException {
		try {
			ProcessDefinition processDefinition = em.find(ProcessDefinition.class, processRrn);
			ProcessInstance newInstance = new ProcessInstance(processDefinition, null, instanceKey);
			newInstance.init();
			ProcessInstance superInstance = newInstance;
			ProcessInstance subInstance = newInstance;
			ProcessDefinition currentPf = processDefinition;
			for (int i = 0 ; i < nodeList.size(); i++) {
				Node node = (Node)nodeList.get(i);
				if (node != null) {
					if (node instanceof ProcedureState) {
						currentPf = new Procedure();
						currentPf.setOrgRrn(processDefinition.getOrgRrn());
						currentPf.setName(((ProcedureState)node).getProcedureName());
						currentPf = getActiveProcessDefinition(currentPf);
					} else if (node instanceof StepState) {
						currentPf = new Step();
						currentPf.setOrgRrn(processDefinition.getOrgRrn());
						currentPf.setName(((StepState)node).getStepName());
						currentPf = getActiveProcessDefinition(currentPf);
					}
					if (currentPf == null) {
						throw new ClientException("couldn't create process instance currentPf is null; node type is " + node);
					}
					superInstance.getRootToken().setNode(node);
					superInstance.getRootToken().setNodeEnter(new Date());
					subInstance = superInstance.getRootToken().createSubProcessInstance(currentPf, 
							superInstance.getContextInstance().getVariables(), instanceKey);
					superInstance = subInstance;
				} else {
					throw new ClientException("couldn't create process instance " + node.getObjectRrn() + " is null" );
				}
			}
			subInstance.signal();
			newInstance = savaProcess(newInstance);
			return newInstance;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		}  catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Stack<Token> getProcessTokenStack(long processInstanceRrn) throws ClientException {
		try {
			Stack<Token> stack = new Stack<Token>();
			Stack<Token> inStack = new Stack<Token>();
			ProcessInstance parentInstance = (ProcessInstance) em.find(ProcessInstance.class, processInstanceRrn);
			Token rootToken = parentInstance.getRootToken();
			while (rootToken.getSubProcessInstance() != null) {
				rootToken.getNode().getObjectRrn();
				rootToken.getProcessInstance().getProcessDefinition().getObjectRrn();
				stack.push(rootToken);
				rootToken = rootToken.getSubProcessInstance().getRootToken();
			}
			if (rootToken != null) {
				rootToken.getNode().getObjectRrn();
				rootToken.getProcessInstance().getProcessDefinition().getObjectRrn();
				stack.push(rootToken);
			}
			while (!stack.isEmpty()) {
				inStack.push(stack.pop());
			}
			return inStack;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Node> getProcessFlowList(long processInstanceId) throws ClientException {
		try{
			List<Node> flowList = new ArrayList<Node>();
			Stack<Token> stack = getProcessTokenStack(processInstanceId);
			while (!stack.isEmpty()) {
				Token token = stack.pop();
				Node node = token.getNode();
				flowList.add(node);
			}
			return flowList;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	public Token getCurrentToken(ProcessInstance processInstance) throws ClientException{
		try {
			Token rootToken = processInstance.getRootToken();
			if (rootToken.getSubProcessInstance() != null){
				processInstance.addCascadeProcessInstance(rootToken.getSubProcessInstance());
				return getCurrentToken(rootToken.getSubProcessInstance());
			} else {
				return rootToken;
			}
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public ProcessInstance cloneProcessInstance(long processInstanceRrn) throws ClientException {
		try {
			ProcessInstance parentInstance = em.find(ProcessInstance.class, processInstanceRrn);
			ProcessDefinition processDefinition = em.find(ProcessDefinition.class, parentInstance.getProcessDefinition().getObjectRrn());
			
			ProcessInstance newInstance = new ProcessInstance(processDefinition, null, parentInstance.getInstanceKey());
			newInstance.init();
			if (newInstance.getContextInstance().getVariables() != null) {
				newInstance.getContextInstance().getVariables().clear();
			}
			newInstance.getContextInstance().setVariables(parentInstance.getContextInstance().getVariables());
			Token rootToken = parentInstance.getRootToken();
			newInstance.getRootToken().setNode(rootToken.getNode());
			newInstance.getRootToken().setNodeEnter(rootToken.getNodeEnter());
			ProcessInstance superInstance = newInstance;
			ProcessInstance subInstance = newInstance;
			while (rootToken.getSubProcessInstance() != null) {
				subInstance = superInstance.getRootToken().createSubProcessInstance(
						rootToken.getSubProcessInstance().getProcessDefinition(),  null, parentInstance.getInstanceKey());
				if (subInstance.getContextInstance().getVariables() != null) {
					subInstance.getContextInstance().getVariables().clear();
				}
				subInstance.getContextInstance().setVariables(rootToken.getSubProcessInstance().getContextInstance().getVariables());
				rootToken = rootToken.getSubProcessInstance().getRootToken();
				subInstance.getRootToken().setNode(rootToken.getNode());
				subInstance.getRootToken().setNodeEnter(rootToken.getNodeEnter());
				superInstance = subInstance;
			}
			return savaProcess(newInstance);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		}  catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Map<String, Object> getCurrentParameter(long processInstanceRrn) throws ClientException {
		try {
			ProcessInstance processInstance = em.find(ProcessInstance.class, processInstanceRrn);;
			Token currentToken = getCurrentToken(processInstance);
			VariableContainer container = currentToken.getProcessInstance().getContextInstance().getTokenVariableMap(currentToken);
			if (container == null) {
				return new HashMap<String, Object>();
			}
			return (Map<String, Object>)container.getVariablesLocally();
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<String> getActiveStepNames(long orgRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Step.name FROM Step Step");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION); 
		sql.append(" AND status = '");
		sql.append(ProcessDefinition.STATUS_ACTIVE);
		sql.append("' ORDER BY Step.name ");  
		logger.debug(sql);
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			List<String> list = query.getResultList();
			return list;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Set<String> getStepNamesByUser(long orgRrn, long userRrn) throws ClientException {
		Set<String> stepNameSet = new HashSet<String>();
		try {
			ADUser user = new ADUser();
			user.setObjectRrn(userRrn);
			user = (ADUser)adManager.getEntity(user);
			List<ADUserGroup> userGroups = user.getUserGroups();
			for (ADUserGroup userGroup : userGroups) {
				if (userGroup.getOrgRrn().equals(orgRrn)) {
					List<String> stepNames = getStepNamesByUserGroup(userGroup.getObjectRrn());
					stepNameSet.addAll(stepNames);
				}
			}
			return stepNameSet;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<String> getStepNamesByUserGroup(long userGroupRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT UserGroupStep.stepName FROM UserGroupStep UserGroupStep");
		sql.append(" WHERE ");
		sql.append(" userGroupRrn = ? ");
		sql.append(" ORDER BY stepName ");  
		logger.debug(sql);
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, userGroupRrn);
			List<String> list = query.getResultList();
			return list;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	public List<Step> getActiveStepsByStage(long orgId, long stageRrn, long userId) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Step FROM Step Step");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION); 
		sql.append(" AND stageRrn = ? ");
		sql.append(" AND status = '");
		sql.append(ProcessDefinition.STATUS_ACTIVE);
		sql.append("' ORDER BY name ");  
		logger.debug(sql);
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, stageRrn);
			List<Step> list = query.getResultList();
			//Get Step By User
//			Set<String> userStepName = getStepNamesByUser(orgId, userId);
//			List<Step> stepNames = new ArrayList<Step>(); 
//			for (Step step : list) {
//				if (userStepName.contains(step.getName())) {
//					stepNames.add(step);
//				}
//			}
			
			//Get All Step
			List<Step> stepNames = new ArrayList<Step>(); 
			for (Step step : list) {
				stepNames.add(step);
			}
			return stepNames;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void assignId(Object object) {
		em.persist(object);
	}
	
	@SuppressWarnings("unchecked")
	public BigDecimal getEquipmentByMould(BigDecimal objecrRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		//BigDecimal eqp_Rrn;
		sql.append("SELECT e.equipment_rrn FROM WIP_Equipment_Mould e");
		sql.append(" WHERE 1=1");
		sql.append(" AND e.mould_rrn = ? "); 
		logger.debug(sql);
		try {
			if (objecrRrn != null){
				Query query = em.createNativeQuery(sql.toString());
				query.setParameter(1, objecrRrn);
				List<BigDecimal> eqp_Rrn = query.getResultList();
				if(eqp_Rrn != null && eqp_Rrn.size()>0)
					return eqp_Rrn.get(0);
				else return null;
			} 
		} catch (NoResultException e){
			return null;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new JbpmException(e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getMouldByEquipment(long orgRrn, long objecrRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT m.object_rrn,m.mould_id, m.mould_name FROM WIP_Equipment_Mould w, WIP_MOULD m");
		sql.append(" WHERE " + ADBase.SQL_BASE_CONDITION);
		sql.append(" AND m.object_rrn = w.mould_rrn AND w.equipment_rrn = ? "); 
		logger.debug(sql);
		try {
				Query query = em.createNativeQuery(sql.toString());
				query.setParameter(1, orgRrn);
				query.setParameter(2, objecrRrn);
				List<Object[]> eqp_Rrn = query.getResultList();
				return eqp_Rrn;
		} catch (NoResultException e){
			return null;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new JbpmException(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public BigDecimal getWorkCenterByEquipment(BigDecimal objecrRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		//BigDecimal eqp_Rrn;
		sql.append("SELECT w.workcenter_rrn FROM WIP_WorkCenter_Equipment w");
		sql.append(" WHERE 1=1");
		sql.append(" AND w.equipment_rrn = ? "); 
		logger.debug(sql);
		try {
			if (objecrRrn != null){
				Query query = em.createNativeQuery(sql.toString());
				query.setParameter(1, objecrRrn);
				List<BigDecimal> workcenter_Rrn = query.getResultList();
				if(workcenter_Rrn != null && workcenter_Rrn.size()>0)
					return workcenter_Rrn.get(0);
				else return null;
			} 
		} catch (NoResultException e){
			return null;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new JbpmException(e);
		}
		return null;
	}
	
}

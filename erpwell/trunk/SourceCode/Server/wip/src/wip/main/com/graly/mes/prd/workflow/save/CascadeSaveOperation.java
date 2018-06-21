package com.graly.mes.prd.workflow.save;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.graly.mes.prd.workflow.graph.exe.ProcessInstance;
import com.graly.mes.prd.workflow.jpdl.xml.JpdlXmlReader;

public class CascadeSaveOperation implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(JpdlXmlReader.class);

	public void save(ProcessInstance processInstance, EntityManager em) {
		logger.debug("cascading save of '" + processInstance + "'");
		Set<ProcessInstance> cascadedProcessInstances = new HashSet<ProcessInstance>();
		if (processInstance.getObjectRrn() == null) {
			em.persist(processInstance);
		} else {
			em.merge(processInstance);
		}
		cascadedProcessInstances.add(processInstance);
		cascadeSave(processInstance.removeCascadeProcessInstances(),
				em, cascadedProcessInstances);
	}

	void cascadeSave(Collection<ProcessInstance> cascadeProcessInstances,
			EntityManager em, Set<ProcessInstance> cascadedProcessInstances) {
		if (cascadeProcessInstances != null) {
			Iterator<ProcessInstance> iter = cascadeProcessInstances.iterator();
			while (iter.hasNext()) {
				ProcessInstance cascadeInstance = (ProcessInstance) iter.next();
				saveCascadeInstance(cascadeInstance, em, cascadedProcessInstances);
			}
		}
	}

	void saveCascadeInstance(ProcessInstance cascadeInstance,
			EntityManager em, Set<ProcessInstance> cascadedProcessInstances) {
		if (!cascadedProcessInstances.contains(cascadeInstance)) {
			Collection<ProcessInstance> cascadeProcessInstances = cascadeInstance.removeCascadeProcessInstances();
			logger.debug("cascading save to process instance '" + cascadeInstance + "'");
			if (cascadeInstance.getObjectRrn() == null) {
				em.persist(cascadeInstance);
			} else {
				em.merge(cascadeInstance);
			}
			cascadedProcessInstances.add(cascadeInstance);
			cascadeSave(cascadeProcessInstances, em, cascadedProcessInstances);
		}
	}
}

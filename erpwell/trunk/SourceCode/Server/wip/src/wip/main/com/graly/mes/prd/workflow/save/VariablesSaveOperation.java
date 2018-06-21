package com.graly.mes.prd.workflow.save;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import com.graly.mes.prd.workflow.context.exe.VariableContainer;
import com.graly.mes.prd.workflow.context.exe.VariableInstance;
import com.graly.mes.prd.workflow.context.exe.variableinstance.UnpersistableInstance;
import com.graly.mes.prd.workflow.graph.exe.ProcessInstance;

public class VariablesSaveOperation implements Serializable {

	private static final long serialVersionUID = 1L;

	public void save(ProcessInstance processInstance, EntityManager em) {
		Collection<VariableContainer> updatedVariableContainers = VariableContainer.getUpdatedVariableContainers(processInstance);
		if (updatedVariableContainers != null) {
			// loop over all updated variable containers
			Iterator<VariableContainer> iter = updatedVariableContainers.iterator();
			while (iter.hasNext()) {
				VariableContainer variableContainer = (VariableContainer)iter.next();
				Map<String, VariableInstance> variableInstances = variableContainer.getVariableInstances();
				if (variableInstances != null) {
					// loop over all variable instances in the container
					Iterator<Entry<String, VariableInstance>> varInstancesIter = variableInstances.entrySet().iterator();
					while (varInstancesIter.hasNext()) {
						Entry<String, VariableInstance> entry = varInstancesIter.next();
						String name = (String) entry.getKey();
						VariableInstance variableInstance = (VariableInstance) entry.getValue();

						if (variableInstance instanceof UnpersistableInstance) {
							Object value = variableInstance.getValue();
						} else {
							
						}
					}
				}
			}
		}
	}

	// private static Log log = LogFactory.getLog(CheckUnpersistableVariablesOperation.class);
}

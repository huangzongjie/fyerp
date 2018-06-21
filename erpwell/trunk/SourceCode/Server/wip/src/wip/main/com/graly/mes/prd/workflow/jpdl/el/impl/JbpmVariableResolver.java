package com.graly.mes.prd.workflow.jpdl.el.impl;


import com.graly.mes.prd.workflow.JbpmConfiguration;
import com.graly.mes.prd.workflow.context.exe.ContextInstance;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.prd.workflow.graph.exe.Token;
import com.graly.mes.prd.workflow.jpdl.el.ELException;
import com.graly.mes.prd.workflow.jpdl.el.VariableResolver;

public class JbpmVariableResolver implements VariableResolver {

  public Object resolveVariable(String name) throws ELException {
    ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
    Object value = null;
    
    if ("processInstance".equals(name)) {
      value = executionContext.getProcessInstance();

    } else if ("processDefinition".equals(name)) {
      value = executionContext.getProcessDefinition();

    } else if ("token".equals(name)) {
      value = executionContext.getToken();

    } else if ("contextInstance".equals(name)) {
      value = executionContext.getContextInstance();

    } else {
      ContextInstance contextInstance = executionContext.getContextInstance();
      Token token = executionContext.getToken();
      
      if ( (contextInstance!=null)
           && (contextInstance.hasVariable(name))
         ) {
        value = contextInstance.getVariable(name, token);

      } else if ( (contextInstance!=null)
                  && (contextInstance.hasTransientVariable(name))
                ) {
        value = contextInstance.getTransientVariable(name);
        
      } else if ( (contextInstance!=null)
                  && (contextInstance.hasTransientVariable(name))
                ) {
        value = contextInstance.getTransientVariable(name);
        
      } else if (JbpmConfiguration.Configs.hasObject(name)) {
        value = JbpmConfiguration.Configs.getObject(name);
      }
    }

    return value;
  }
}

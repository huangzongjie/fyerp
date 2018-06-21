package com.graly.mes.prd.workflow.graph.def;

import java.io.Serializable;

import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;

public interface ActionHandler extends Serializable {
  
  void execute( ExecutionContext executionContext ) throws Exception;
}

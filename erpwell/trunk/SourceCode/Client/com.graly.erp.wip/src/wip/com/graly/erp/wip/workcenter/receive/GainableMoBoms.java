package com.graly.erp.wip.workcenter.receive;

import java.util.List;

import com.graly.erp.wip.model.ManufactureOrderBom;

public interface GainableMoBoms {
	
	List<ManufactureOrderBom> getReceivedFinishedMoBoms();
}

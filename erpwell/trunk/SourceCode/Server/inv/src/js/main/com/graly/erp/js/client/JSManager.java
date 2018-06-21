package com.graly.erp.js.client;

import java.util.List;

import com.graly.erp.js.model.JSMaterialQtyQuery;
import com.graly.framework.core.exception.ClientException;

public interface JSManager {
	List<JSMaterialQtyQuery> getMaterialQtyQueryList(int maxResult, String whereClause, String orderBy) throws ClientException;
}

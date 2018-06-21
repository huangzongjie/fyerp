package com.graly.erp.product.client;


import java.util.List;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.model.InternalOrder;
import com.graly.erp.product.model.CanaInnerOrder;
import com.graly.erp.product.model.CanaProduct;
import com.graly.framework.core.exception.ClientException;

public interface CANAManager {
	CanaProduct getCanaProduct(String materialId) throws ClientException;
	CanaProduct updateCanaProduct(CanaProduct product) throws ClientException;
	
	void importBomFromCrm(Material parentMaterial,long orgRrn,long userRrn) throws ClientException;
	InternalOrder createInternalOrderFromCanaIO(long orgRrn, String canaInnerOrderId, long userRrn) throws ClientException ;
	List<CanaInnerOrder> getCanaInnerOrderList(int maxResult, String whereClause, String orderBy) throws ClientException;
	List<CanaInnerOrder> getDisCanaInnerOrderList(int maxResult, String whereClause, String orderBy) throws ClientException;
}

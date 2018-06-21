package com.graly.erp.sal.client;

import java.util.List;

import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.sal.model.SalesOrder;
import com.graly.framework.core.exception.ClientException;

public interface SALManager {

	MovementOut createMovementOutFromSo(long orgRrn, String soId, long userRrn) throws ClientException;
//	BigDecimal getQtySo(String materialId) throws ClientException;
	void approveSo(String soId, long outRrn, String outId, String deliverDate) throws ClientException;
	void adjustSo(String soId, long outRrn, String outId , String deliverDate) throws ClientException;
	
	List<SalesOrder> getSelesOrderList(int maxResult, String whereClause, String orderBy) throws ClientException;
}

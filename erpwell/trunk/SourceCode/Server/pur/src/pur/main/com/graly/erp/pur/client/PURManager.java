package com.graly.erp.pur.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.graly.erp.inv.model.VPoAlarmIqcLine;
import com.graly.erp.inv.model.VPoAlarmMovenetLine;
import com.graly.erp.inv.model.VPoAlarmReceiptLine;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.core.exception.ClientException;
import com.graly.mes.wip.model.Lot;

public interface PURManager {
	
	
	RequisitionLine newPRLine(Requisition pr) throws ClientException;
	RequisitionLine savePRLine(RequisitionLine prLine , long userRrn) throws ClientException;
	Requisition savePRLine(Requisition pr, List<RequisitionLine> prLines , boolean batchFlag, long userRrn) throws ClientException;
	void closePR(Requisition pr, long userRrn) throws ClientException;
	void approvePR(Requisition pr, long userRrn) throws ClientException;
	void closePRLine(RequisitionLine prLine , long userRrn) throws ClientException;
	void deletePR(Requisition pr, long userRrn)throws ClientException;
	void deletePRLine(RequisitionLine prLine, long userRrn)throws ClientException;
	List<Requisition> getCanMergePr(long orgRrn) throws ClientException;
	Requisition mergePr(long orgRrn, List<Requisition> prs, boolean ignoreDateEnd, long userRrn) throws ClientException;
	
	PurchaseOrderLine newPOLine(PurchaseOrder po) throws ClientException;
	PurchaseOrderLine getPOLineByPRLine(RequisitionLine prLine) throws ClientException;
	PurchaseOrder createPOFromPR(PurchaseOrder po, List<RequisitionLine> prLines, long userRrn) throws ClientException;
	PurchaseOrder savePO(PurchaseOrder po, long userRrn) throws ClientException;
	PurchaseOrder updatePOFinancialNote(PurchaseOrder po, String note, long userRrn) throws ClientException;
	PurchaseOrderLine savePOLine(PurchaseOrder po, PurchaseOrderLine poLine , long userRrn) throws ClientException;
	
	PurchaseOrder preApprovePO(PurchaseOrder po, long userRrn) throws ClientException;
	PurchaseOrder approvePO(PurchaseOrder po, long userRrn) throws ClientException;
	PurchaseOrder cancelApprovedPO(PurchaseOrder po, long userRrn) throws ClientException;
	PurchaseOrder cancelPreApprovedPO(PurchaseOrder po, long userRrn) throws ClientException;
	
	PurchaseOrder closePO(PurchaseOrder po, long userRrn) throws ClientException;
	PurchaseOrderLine closePOLine(PurchaseOrderLine poLine , long userRrn) throws ClientException;
	void deletePO(PurchaseOrder po, long userRrn)throws ClientException;
	void deletePOLine(PurchaseOrderLine poLine, long userRrn)throws ClientException;
	
	List<PurchaseOrderLine> getPoLineByMaterial(long orgRrn, long materialRrn) throws ClientException;
	List<RequisitionLine> getPrLineByMaterial(long orgRrn, long prRrn, long materialRrn) throws ClientException;
	Requisition generatePr(long orgRrn, long userRrn) throws ClientException;
	Requisition generatePr(long orgRrn, long userRrn, Long mpsRrn, String mpsId) throws ClientException;
	List<ManufactureOrder> getMoListByPrLine(long orgRrn, long prLineRrn) throws ClientException;
	
	Requisition generatePrByMin(long orgRrn, List<RequisitionLine> prLines, long userRrn) throws ClientException;
	List<Lot> getMerchandiseLots (String lotId, long orgRrn) throws ClientException;
	BigDecimal getPoLineByMaxObject(long orgRrn, long materialRrn) throws ClientException;
	String queryVendorPurGoal(Map<String, String> params, long orgRrn, String whereClause) throws ClientException;
	List<BigDecimal> getVendorByPurchaser(long orgRrn, String purchaser) throws ClientException;
	
	public long getAlarmReceiptCount(long orgRrn,String purchaser) throws ClientException;
	public List<VPoAlarmReceiptLine> getAlarmReceipts(long orgRrn,String purchaser) throws ClientException;
	public long getAlarmIqcCount(long orgRrn,String purchaser) throws ClientException;
	public List<VPoAlarmIqcLine> getAlarmIqcs(long orgRrn,String purchaser) throws ClientException;
	public long getAlarmInvCount(long orgRrn,String purchaser) throws ClientException;
	public List<VPoAlarmMovenetLine> getAlarmInvs(long orgRrn,String purchaser) throws ClientException;
	public PurchaseOrder sparesApprovePO(PurchaseOrder po, long userRrn) throws ClientException;
	PurchaseOrder createPOFromPRXZ(PurchaseOrder po, List<RequisitionLine> prLines, long userRrn) throws ClientException;
	void generatePOXZ(long orgRrn,long userRrn,String whereClause) throws ClientException;
	void unApprovePR(Requisition pr, long userRrn) throws ClientException;
	void generateMovementOutXZ(long orgRrn,long userRrn,String whereClause) throws ClientException;
	
	RequisitionLine saveXZPRLine(RequisitionLine prLine, long userRrn) throws ClientException;

	PurchaseOrder savePOLine(PurchaseOrder po, List<PurchaseOrderLine> poLines, long userRrn) throws ClientException;

	void generateYn(Mps mps, MpsLine line, Requisition pr, long userRrn) throws ClientException;
	void generateYnTempMps(long orgRrn,long warehouseRrn,long userRrn) throws ClientException;
	void generateYnTempMps() throws ClientException;
	
}

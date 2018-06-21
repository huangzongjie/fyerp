package com.graly.mes.wip.client;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.LotStorage;
import com.graly.erp.inv.model.RacKMovementLot;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.ppm.model.TpsLine;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.wip.model.DailyMoMaterial;
import com.graly.erp.wip.model.LargeLot;
import com.graly.erp.wip.model.LargeWipLot;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.erp.wip.model.MaterialUsed;
import com.graly.erp.wip.model.WCTLotStorage;
import com.graly.erp.wip.model.WCTMovement;
import com.graly.erp.wip.model.WCTMovementLine;
import com.graly.erp.wip.model.WCTMovementLineLot;
import com.graly.erp.wip.model.WCTMaterialStorage;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotComponent;
import com.graly.mes.wiphis.model.LotHis;

public interface WipManager {
	
	List<ManufactureOrderBom> getMoBom(long orgRrn, long materialRrn) throws ClientException;
	List<ManufactureOrderBom> generateMoBomDetail(ManufactureOrder mo, List<ManufactureOrderBom> moBoms) throws ClientException;
	List<ManufactureOrderBom> generateMoBomDetail(
			ManufactureOrder mo, List<ManufactureOrderBom> moBoms, boolean batchFlag) throws ClientException;
	List<ManufactureOrderBom> getMoBomDetailFromDB(ManufactureOrder mo) throws ClientException;
//	List<ManufactureOrderBom> getMoBomDetail(ManufactureOrder mo) throws ClientException;
	List<DocumentationLine> getMoLine(ManufactureOrder mo, List<ManufactureOrderBom> moBoms, Long userRrn) throws ClientException;
	List<DocumentationLine> generateMoLine(ManufactureOrder mo, List<ManufactureOrderBom> moBoms, boolean batchFlag, Long userRrn) throws ClientException ;
	void addMoBatch(ManufactureOrder mo, List<DocumentationLine> moLines, 
			List<ManufactureOrderBom> moBoms, Requisition pr, long userRrn) throws ClientException;
	ManufactureOrder saveMo(ManufactureOrder mo, List<DocumentationLine> moLines, 
			List<ManufactureOrderBom> moBoms, long userRrn) throws ClientException;
	void deleteMo(ManufactureOrder mo, long userRrn) throws ClientException;
	void approveMo(ManufactureOrder mo, long userRrn) throws ClientException;
	void closeMo(ManufactureOrder mo, long userRrn) throws ClientException;
	Requisition mergePrLine(Requisition pr) throws ClientException;
	
	List<Material> getMaterialByWorkCenter(long orgRrn, long workCenterRrn) throws ClientException;
	MaterialSum getMaterialSum(long orgRrn, long materialRrn, boolean batchFlag, boolean soFlag) throws ClientException;
	List<MaterialSum> getInvMaterialByWorkCenter(long orgRrn, long workCenterRrn) throws ClientException;
	
	ManufactureOrderLine runMoLine(ManufactureOrderLine moLine , BigDecimal manpower) throws ClientException;
	ManufactureOrderLine suspendMoLine(ManufactureOrderLine moLine) throws ClientException;
	List<ManufactureOrderBom> getMoLineBom(long moLineRrn) throws ClientException;
	List<ManufactureOrderBom> getLotBom(Lot lot) throws ClientException;
	Lot receiveLot(WorkCenter wc, Lot parentLot, List<Lot> childLots, long userRrn) throws ClientException;
	//���������ʱʹ��Ԥ��ӡ���������
	Lot receiveLot(WorkCenter wc, Lot parentLot, List<Lot> childLots, Lot genLot, long userRrn) throws ClientException;
//	List<Lot> getNextChildLot(Lot parentLot, List<Lot> childLots) throws ClientException;
	Lot getWipLotByLotId(String lotId, Long orgRrn) throws ClientException;
	ManufactureOrderLine addMoLine(ManufactureOrderLine moLine, long userRrn) throws ClientException;
	void closeMoLine(ManufactureOrderLine moLine, long userRrn) throws ClientException;
	void changeWorkCenter(ManufactureOrderLine moLine, long userRrn) throws ClientException;
	
	RequisitionLine generatePrLineByMin(long orgRrn, long materialRrn) throws ClientException;
	
	List<ManufactureOrderLine> getMoLineByWorkCenter(long orgRrn, long workCenterRrn, String whereClause) throws ClientException;
//	List<ManufactureOrderLine> getMoLineByWorkCenter(long orgRrn,String whereClause) throws ClientException;
	void disassembleLot(long parentLotRrn, long userRrn) throws ClientException;
	void disassembleMoLine(long moLineRrn, long userRrn) throws ClientException;
//	List<Lot> getAssembleLot(long parentLotRrn) throws ClientException;
	
	List<Lot> getAvailableLot4In(long moRrn) throws ClientException;
//	List<Lot> getCompanentLots(long orgRrn, Lot lot) throws ClientException ;
	List<LotComponent> getLotComponent(long parentLotRrn) throws ClientException;
	List<LotComponent> getLotComponent(long parentLotRrn, long moLineRrn) throws ClientException;
	List<LotComponent> getLotUsages(long parentLotRrn) throws ClientException;
	
	List<MaterialUsed> getMoMaterialUsed(long orgRrn, long materialRrn, String status, String whereClause) throws ClientException;
	List<ManufactureOrderLine> getMoLineChildrenForMo(long orgRrn, long moRrn) throws ClientException;
	List<LotStorage> getLotAllStorage(long orgRrn, long lotRrn) throws ClientException;
	void receiveMultiSerialLot(WorkCenter wc, Lot parentLot, List<String> lotIds, int qty, List<Lot> childLots, long userRrn) throws ClientException;
	void deleteMoByMps(ManufactureOrder mo, long userRrn) throws ClientException;
	ManufactureOrderBom alternateMoBom(ManufactureOrder mo, ManufactureOrderBom moBom, ManufactureOrderBom alternateMoBom, long userRrn) throws ClientException;
	List<ManufactureOrderBom> getMoLineAllChildBom(long moLineRrn) throws ClientException;
	List<ManufactureOrderBom> getMoBomAllChildBom(long moBomRrn) throws ClientException;
	
	ManufactureOrder getMoById(long orgRrn, String moId) throws ClientException;
	
	List<DailyMoMaterial> getDailyMoMaterials(long orgRrn, String whereClause) throws ClientException;
	List<DailyMoMaterial> getDailyMoMaterials(long orgRrn, String materialId, String fromDate, String endDate, String whereClause) throws ClientException;
	
	WCTMovementLine newMovementLine(WCTMovement wctmovement) throws ClientException;
	/**
	 * ���ֲ�����Σ�ֻ��������쳵�������ֻ�޸Ŀ���������޸ĺ�����
	 * @param orgRrn
	 * @param parentLot
	 * @param subLots
	 * @param userRrn
	 * @return ��ֺ����,��������,������������
	 * @throws ClientException
	 */
	Lot partlyDisassembleLot(long orgRrn, Lot parentLot, List<Lot> subLots, long userRrn) throws ClientException;
	List<LotComponent> receiveDetail(long orgRrn, long moLineRrn) throws ClientException;
	List<LotComponent> receiveDetail(long orgRrn, long moLineRrn, String parentLotId) throws ClientException;
	
	List<LotHis> getWipHisByUsedLot(long orgRrn, String lotId, Long materialChildRrn, String otherWhereClause) throws ClientException;
	
	public List<Lot> getGenLotIds(Lot lot) throws ClientException;
	
	public List<Lot> getOutStorageLot(Lot lot) throws ClientException;
	
	/*
	 * ������ѡ����ӹ����� �õ������������ϲ����ӹ�����,ͬʱ�ж��Ƿ��ܺϲ�
	 */
	public List<ManufactureOrderLine> getCanMergeMoLines(ManufactureOrderLine moLine) throws ClientException;
	
	public void mergeMoLines(ManufactureOrderLine srcLine,List<ManufactureOrderLine> dstLines, Long userRrn) throws ClientException;
	
	public List<ManufactureOrderLine> findMoLinesByChildMaterial(long orgRrn, long materialRrn) throws ClientException;
	
	public WCTMovement saveWCTMovementLine(WCTMovement wctMovement, WCTMovementLine wctMovementLine, long orgRrn, long userRrn) throws ClientException;
	public WCTMovement saveWCTMovementLine(WCTMovement wctMovement,List<WCTMovementLine> wctMovementLines, long orgRrn,long userRRn) throws ClientException;
	void deleteWCTMovement(WCTMovement wctMovement, long userRrn)throws ClientException;
	
	//����workcenter_rrn��ѯ��֮�������豸objectRrn��name
	public List<Object[]> getEquipmentByWorkCenter(long orgRrn, long objecrRrn) throws ClientException;
	
	public void saveWCTMovementLineLots(WCTMovement wctMovement, List<WCTMovementLineLot> lineLots, Long userRrn) throws ClientException;
	public void saveWCTMovementLineLots(WCTMovementLine wctMovementLine, List<WCTMovementLineLot> lineLots, Long userRrn) throws ClientException;
	public void saveWCTMovementLineLots(List<WCTMovementLine> wctMovementLines, List<WCTMovementLineLot> lineLots, Long userRrn) throws ClientException;
	
	public WCTMaterialStorage getWCTMaterialStorage(Material material, long workcenterRrn) throws ClientException;
	public WCTMaterialStorage updateWCTMaterialStorage(Material material, long workcenterRrn, BigDecimal qty) throws ClientException;
	
	public void approveWCTMovement(WCTMovement movement, long userRrn) throws ClientException;
	
	public List<ManufactureOrderLine> getCanDissolveMoLines(ManufactureOrderLine moLine) throws ClientException;
	public void dissolveMoLines(ManufactureOrderLine srcLine,List<ManufactureOrderLine> dstLines, Long userRrn) throws ClientException;
	public void closeMoByMps(ManufactureOrder mo, long userRrn) throws ClientException;
	
	public WorkCenter getWorkCenterByMaterial(long orgRrn, long materialRrn) throws ClientException;
	public void packageLots(LargeLot largerlot, List<LargeWipLot> lwLots, long orgRrn, long userRrn) throws ClientException;
	public Boolean validateDelete(Lot lot, long orgRrn, long userRrn) throws ClientException;
	public Boolean validateLLDelete(LargeLot ll, long orgRrn, long userRrn) throws ClientException;
	List<Object[]> getMouldsByMaterial(long orgRrn, long objecrRrn) throws ClientException;
	public MaterialSum getMaterialSum2(long orgRrn, long materialRrn, boolean batchFlag, boolean soFlag, boolean calcAllFlag) throws ClientException;
	
	/**���Ԥ��������
	 * 1.��������״̬����prepare
	 * 2.�ӹ�����״̬��Approved
	 * */
	public List<ManufactureOrderBom> generateFirstPrepareMoBomDetail(ManufactureOrder mo, List<ManufactureOrderBom> moBoms) throws ClientException;
	List<ManufactureOrderBom> generatePrepareMoBomDetail(ManufactureOrder mo, List<ManufactureOrderBom> moBoms) throws ClientException;
	List<ManufactureOrderBom> generatePrepareMoBomDetail(ManufactureOrder mo, List<ManufactureOrderBom> moBoms, boolean batchFlag) throws ClientException;
	List<ManufactureOrderBom> getPerpareMoBomDetailFromDB(ManufactureOrder mo,List<ManufactureOrderBom> boms) throws ClientException;
	/**
	 * ���ܻ�ȡMOBOM
	 * 1.�½���ֱ�Ӵ����ݿ��л�ȡ
	 * 2.�༭�����ȴ����ݿ��л�ȡ��������MO�Ǵ�����������ȡWIP_MO_BOM����BOM�Ƿ��Ǵ�������������
	 * */
	List<ManufactureOrderBom> updateMoBomCana(long orgRrn, List<ManufactureOrderBom> updateBoms,ManufactureOrder mo) throws ClientException;
	ManufactureOrder addMoPrepare(ManufactureOrder mo,List<ManufactureOrderBom> moBoms, long userRrn) throws ClientException;
	
	List<ManufactureOrderBom> getPrepareBoms(long orgRrn,String whereClause) throws ClientException;
	boolean verifyPrepareTps(long orgRrn,long userRrn,TpsLine tpsLine) throws ClientException;
	List<ManufactureOrderBom> getMoChildrenBom(long orgRrn, long materialRrn) throws ClientException;
	List<ManufactureOrderBom> getMoBomFromPrepareTpsLine(long orgRrn,long userRrn,TpsLine tpsLine) throws ClientException;

	void addMoBatchTime(ManufactureOrder mo, List<DocumentationLine> moLines, 
			List<ManufactureOrderBom> moBoms, Requisition pr, long userRrn,Date startTime,Date endTime) throws ClientException;
	
	ManufactureOrder updateMoComments(ManufactureOrder mo ,long userRrn) throws ClientException;
	public void updateMoAndLinePlanAlarm(long orgRrn,long userRrn,String whereClause) throws ClientException;
	void runSchedulePurchase(long orgRrn,long userRrn) throws ClientException;
	
	List<ManufactureOrderLine> getMoLineByWorkCenter2(long orgRrn, long workCenterRrn, String whereClause) throws ClientException;
	ManufactureOrderLine getMoLineByWorkCenter2Qty(long orgRrn, long workCenterRrn, String whereClause,ManufactureOrderLine moline) throws ClientException;
	
	void runSchedulePurchase2(long orgRrn,long userRrn) throws ClientException;
	void runSchedulePurchase3(long orgRrn,long userRrn) throws ClientException;
	void runSchedulePurchase4(long orgRrn,long userRrn) throws ClientException;
}

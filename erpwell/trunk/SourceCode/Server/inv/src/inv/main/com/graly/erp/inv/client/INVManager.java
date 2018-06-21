package com.graly.erp.inv.client;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.graly.erp.base.model.Material;
import com.graly.erp.base.model.Storage;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.erp.inv.model.LotOutSerial;
import com.graly.erp.inv.model.LotStorage;
import com.graly.erp.inv.model.MaterialLocator;
import com.graly.erp.inv.model.MaterialTrace;
import com.graly.erp.inv.model.MaterialTraceDetail;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementLineOutSerial;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.model.MovementWorkShop;
import com.graly.erp.inv.model.MovementWorkShopDelivery;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.MovementWorkShopReclaim;
import com.graly.erp.inv.model.MovementWorkShopRequestion;
import com.graly.erp.inv.model.MovementWorkShopServices;
import com.graly.erp.inv.model.MovementWorkShopUnqualified;
import com.graly.erp.inv.model.MovementWorkShopVirtualHouse;
import com.graly.erp.inv.model.MovementWriteOff;
import com.graly.erp.inv.model.RacKMovementLot;
import com.graly.erp.inv.model.RackLotStorage;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.model.ReceiptLine;
import com.graly.erp.inv.model.SparesMaterialUse;
import com.graly.erp.inv.model.StockIn;
import com.graly.erp.inv.model.VInvNoTransfer;
import com.graly.erp.inv.model.VMaterialStorageList;
import com.graly.erp.inv.model.VMovementLineTempEstimate;
import com.graly.erp.inv.model.VOutDetail;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.model.WarehouseRack;
import com.graly.erp.inv.model.WorkShopLotStorage;
import com.graly.erp.inv.model.WorkShopStorage;
import com.graly.erp.inv.model.MovementIn.InType;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.wip.model.LargeLot;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.MaterialMoveSum;
import com.graly.framework.core.exception.ClientException;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotConsume;


public interface INVManager {
	
	Warehouse getDefaultWarehouse(long orgRrn) throws ClientException;
	Warehouse getWriteOffWarehouse(long orgRrn) throws ClientException;
	Warehouse getWarehouseById(String warehouseId, long orgRrn) throws ClientException;
	
	List<ReceiptLine> getUnInspectedReceiptLines(long orgRrn) throws ClientException;
	List<ReceiptLine> getArrivedReceiptLines(long orgRrn) throws ClientException;
	
	ReceiptLine newReceiptLine(Receipt receipt) throws ClientException;
	Receipt createReceiptFromPO(Receipt receipt, List<PurchaseOrderLine> poLines, long userRrn,String wmsWarehouse) throws ClientException;
	Receipt saveReceipt(Receipt receipt, long userRrn) throws ClientException;
	ReceiptLine saveReceiptLine(Receipt receipt, ReceiptLine receiptLine , long userRrn) throws ClientException;
	Receipt approveReceipt(Receipt receipt, long userRrn) throws ClientException;
	Receipt closeReceipt(Receipt receipt, long userRrn) throws ClientException;
	void deleteReceipt(Receipt receipt, long userRrn)throws ClientException;
	void deleteReceiptLine(ReceiptLine receiptLine, long userRrn)throws ClientException;
	
	IqcLine newIqcLine(Iqc iqc) throws ClientException;
	Iqc createIqcFromReceipt(Iqc iqc, Receipt receipt, long userRrn) throws ClientException;
	Iqc createIqcFromReceiptLines(Iqc iqc, Receipt receipt, List<ReceiptLine> receiptLines, long userRrn) throws ClientException;
	Iqc saveIqc(Iqc iqc, long userRrn) throws ClientException;
	IqcLine saveIqcLine(Iqc iqc, IqcLine iqcLine , long userRrn) throws ClientException;
	Iqc approveIqc(Iqc iqc, long userRrn) throws ClientException;
	Iqc closeIqc(Iqc iqc, long userRrn) throws ClientException;
	void deleteIqc(Iqc iqc, long userRrn)throws ClientException;
	void deleteIqcLine(IqcLine iqcLine, long userRrn)throws ClientException;
	
	MovementIn createInFromIqc(MovementIn in, Iqc iqc, List<IqcLine> lines, List<Lot> lots, long userRrn) throws ClientException;
	MovementIn createInFromPo(MovementIn in, PurchaseOrder po, List<PurchaseOrderLine> lines, List<Lot> lots, long userRrn) throws ClientException;
	MovementIn saveMovementInLine(MovementIn in, List<MovementLine> lines, MovementIn.InType inType, long userRrn) throws ClientException;
	MovementIn approvePinMovementIn(MovementIn in, boolean writeOffFlag, long userRrn) throws ClientException;
	
	MovementIn approveMovementIn(MovementIn in, MovementIn.InType inType, boolean writeOffFlag, boolean seniorApprove,long userRrn) throws ClientException;
	MovementIn approveMovementIn(MovementIn in, InType inType, long userRrn, PurchaseOrder po)throws ClientException;
	MovementIn approveMovementIn(MovementIn in, MovementIn.InType inType, boolean seniorApprove,long userRrn) throws ClientException;
	MovementIn approveMovementIn(MovementIn in, MovementIn.InType inType, long userRrn) throws ClientException;
	MovementIn approveMovementIn(MovementIn in, MovementIn.InType inType, long userRrn, boolean isWriteOff) throws ClientException;
	
	void deleteMovementIn(MovementIn movementIn, MovementIn.InType inType, long userRrn)throws ClientException;
	void deleteMovementInLine(MovementLine movementLine, MovementIn.InType inType, long userRrn)throws ClientException;
	MovementIn writeOffMovementIn(MovementIn in, long userRrn) throws ClientException;
	
	MovementLine newMovementLine(Movement movement) throws ClientException;
	MovementOut approveMovementOut(MovementOut out, MovementOut.OutType outType, long userRrn) throws ClientException;
	MovementOut approveMovementOut(MovementOut out, MovementOut.OutType outType, long userRrn, boolean isWriteOff) throws ClientException;
	//isOnHand是否改变营运库存 isWriteOff是否改变财务库存
	MovementOut approveDevelopMovementOut(MovementOut out, long userRrn, boolean isOnHand, boolean isWriteOff) throws ClientException;
	MovementOut approveMovementOut(MovementOut out, MovementOut.OutType outType, long userRrn, boolean isOnHand, boolean isWriteOff) throws ClientException;
	MovementOut approveSalesMovementOut(MovementOut out, long userRrn, boolean isWriteOff) throws ClientException ;
	MovementLine saveMovementOutLine(MovementOut out, MovementLine outLine, MovementOut.OutType outType, long userRrn) throws ClientException;
	MovementOut saveMovementOutLine(MovementOut out, List<MovementLine> lines, MovementOut.OutType outType, long userRrn) throws ClientException;
	MovementLine saveSalesMovementOutLine(MovementOut out, MovementLine outLine , long userRrn) throws ClientException;
	MovementOut saveSalesMovementOutLine(MovementOut out, List<MovementLine> lines , long userRrn) throws ClientException;
	void deleteMovementOut(MovementOut out, long userRrn)throws ClientException;
	void deleteMovementOutLine(MovementLine movementLine, long userRrn)throws ClientException;
	
	MovementLine saveMovementTransferLine(MovementTransfer transfer, MovementLine line, long userRrn) throws ClientException;
	MovementTransfer saveMovementTransferLine(MovementTransfer transfer, List<MovementLine> lines, long userRrn) throws ClientException;
	void deleteMovementTransfer(MovementTransfer transfer, long userRrn) throws ClientException;
	void deleteMovementTransferLine(MovementLine movementLine, long userRrn) throws ClientException;
	MovementTransfer approveMovementTransfer(MovementTransfer transfer, long userRrn) throws ClientException;
	
	Storage getMaterialStorage(long orgRrn, long materialRrn, long warehouseRrn, long userRrn)  throws ClientException;
	void updateStorage(long orgRrn, long materialRrn, long warehouseRrn, BigDecimal qty, boolean writeOffFlag, long userRrn) throws ClientException;
	List<Lot> getOptionalOutLot(MovementLine outLine) throws ClientException;
	List<Lot> getOptionalOutLot(MovementLine outLine , String lotid) throws ClientException;
	List<Lot> getOptionalOutLot(MovementLine outLine , String lotid ,String position) throws ClientException;
	
	void saveIqcLot(IqcLine line, List<Lot> lots, long userRrn) throws ClientException;
	String generateNextNumber(long orgRrn, Material material) throws ClientException;
	List<Lot> generateSerialLot(long orgRrn, Material material, int qty, long userRrn) throws ClientException;
	List<Lot> generateSerialLot(Iqc iqc, IqcLine line, long userRrn) throws ClientException;
	List<Lot> generateBatchLot(long orgRrn, Material material, BigDecimal qty, int batchNumber, long userRrn) throws ClientException;
	List<Lot> generateBatchLot(Iqc iqc, IqcLine line, int batchNumber, long userRrn) throws ClientException;
	void saveGenLot(long orgRrn, Material material, List<Lot> lots, long userRrn) throws ClientException;
	
	Lot getLotByLotId(long orgRrn, String lotId) throws ClientException ;
	Lot getLotByLotId(long orgRrn, String lotId, long warehouseRrn) throws ClientException ;
	List<Lot> getLotsById(long orgRrn, String lotId) throws ClientException;
	LargeLot getLargeLotById(long orgRrn, String lotId) throws ClientException;
	
	void modifyLotId(String oldLotId,String newLotId, long userRrn, long orgRrn) throws ClientException;
	LotStorage getLotStorage(long orgRrn, long lotRrn, long warehouseRrn, long userRrn)  throws ClientException;
	Lot getMaterialLot(long orgRrn, Material material, long userRrn) throws ClientException;
	void updateLotStorage(long orgRrn, long lotRrn, long warehouseRrn, BigDecimal qty, long userRrn) throws ClientException;
	List<Lot> getLotStorage(long warehouseRrn, long materialRrn)  throws ClientException;
	List<Lot> getLotStorage(long materialRrn)  throws ClientException;
	
	List<Lot> splitLot(Lot sourceLot, BigDecimal splitQty, int spiltCount, Long userRrn) throws ClientException;
//	Lot mergeLot(Lot parentLot, List<Lot> childLots, Long userRrn) throws ClientException;
	
	List<LotConsume> getMaterialConsume(long lotRrn) throws ClientException;
	List<LotConsume> getMaterialBomConsume(long moRrn, String path) throws ClientException;
	List<LotConsume> getMaterialConsumeByMo(long orgRrn, Long moRrn, String materialId, 
			String toStartDate, String toEndDate) throws ClientException;
	List<LotConsume> getMaterialConsumeByMo(long orgRrn, Long moRrn, String materialId, 
			String toStartDate, String toEndDate, String isManual) throws ClientException;

	String generateNextOutNumber(long orgRrn, String lotId) throws ClientException;
	List<MovementLineOutSerial> generateMovementLineOutSerials(long orgRrn, MovementOut out, MovementLine outLine, long userRrn) throws ClientException;
	List<MovementLineOutSerial> getMovementLineOutSerials(String outSerialId) throws ClientException;
	LotOutSerial generateLotOutSeral(long orgRrn, String lotId, long lotRrn, long userRrn) throws ClientException;
	List<PurchaseOrder> getUnInCompletedPoList(long orgRrn, int maxResult, String whereClause, String orderByClause) throws ClientException;
	List<Material> getNoMoveMaterialList(long orgRrn, Date dateApproved,
			String whereClause, String orderByClause, int maxResult) throws ClientException;
	List<Material> getNoMoveMaterialList(long orgRrn, Date dateApproved, Long warehouseRrn, String whereClause, String orderByClause,
			int maxResult) throws ClientException;
	List<MaterialMoveSum> getMaterialMoveSumList(long orgRrn, Date approvedStartDate, Date approvedEndDate,
			String whereClause, String orderByClause, int maxResult) throws ClientException;
	
	MaterialLocator saveMaterialLocator(MaterialLocator ml, long userRrn) throws ClientException;
	void manualWriteOff(MovementWriteOff mw, List<MovementLine> lines, long userRrn) throws ClientException;
	//手动核销，不挂批次
	void manualWriteOff(MovementWriteOff mw, long userRrn) throws ClientException;
	
	MaterialTrace traceMaterial(long materialRrn, String fromDate, String toDate) throws ClientException;
	List<MaterialTraceDetail> traceMaterialDetail(long materialRrn, String fromDate, String toDate, String detailType) throws ClientException;
	
	List<VOutDetail> getOutDetails(Map<String,Object> queryKeys, String whereClause) throws ClientException;
	
	List<Material> queryMaterialQtys(Long orgRrn, Long materialRrn) throws ClientException;
	List<Material> queryMaterialQtys(Long orgRrn, Long materialRrn, String catalog2) throws ClientException;
	List<Material> queryMaterialQtys(Long orgRrn, Long materialRrn, String catalog2, String whereClause) throws ClientException;
	
	List<String> getOutedLotsByMaterialId(String materialId, Long orgRrn) throws ClientException;
	
	List<MovementIn> writeOffMovementIns(List<MovementIn> ins, long userRrn) throws ClientException;
	
	List<MovementLine> getMovementLines(MovementIn in) throws ClientException;
	
	String getVehicleNameById(String id) throws ClientException;
	String getVehicleIdByName(String name) throws ClientException;
	List<MovementLineLot> getLineLots(Long movementLineRrn) throws ClientException;
	List<MovementLine> getMovementLines(Long movementRrn) throws ClientException;
	List<Lot> attachLotsToIqc(List<Lot> lots, Iqc iqc, IqcLine line, long userRrn) throws ClientException;
	
	//自动从仓库中选择批次,先进先出
	List<Lot> getAutoMatchOutLots(MovementLine outLine) throws ClientException;
	WarehouseRack saveWarehouseRack(long orgRrn, WarehouseRack rack, long userRrn) throws ClientException;
	
	void batchSaveRacKMovementLot(long orgRrn, List<RacKMovementLot> rLots, long userRrn, boolean approve) throws ClientException;
	RacKMovementLot saveRacKMovementLot(long orgRrn, RacKMovementLot rLot, long userRrn, boolean approve) throws ClientException;
	void batchApproveRackMovementLot(long orgRrn, List<RacKMovementLot> rLots,long userRrn) throws ClientException;
	void approveRackMovementLot(long orgRrn, RacKMovementLot rLot, long userRrn) throws ClientException;
	List<PurchaseOrderLine> getPoLineForReceive(long orgRrn, int maxResult, String whereClause) throws ClientException;
	
	List<RackLotStorage> getRackStorage(long orgRrn, String whereClause) throws ClientException;
	WarehouseRack getWarehouseRackById(long orgRrn, String rackId) throws ClientException;
    BigDecimal getWarehouseRackQtyonhand(long lotRrn,long rackRrn) throws ClientException;
    List<RacKMovementLot> getRackLots(MovementOut mo) throws ClientException;
    Lot getMaterialLotForMoLine(long orgRrn, Material material, long userRrn, ManufactureOrderLine moLine) throws ClientException;
    
    List<Lot> generateBarCodeLot(long orgRrn, Material material, BigDecimal qty, int batchNumber, long userRrn) throws ClientException;
    
    List<VInvNoTransfer> getVInvNoTransfer(String whereClause) throws ClientException;
    
    List<Material> queryMaterialQtysAlarm(Long orgRrn,String whereClause,String whereClause2,String whereClause3) throws ClientException;
    List<VMovementLineTempEstimate> getReversalTempEstimate(Long orgRrn,String whereClause) throws ClientException;
    List<Material> getServiceMaterialAlarm(Long orgRrn,String whereClause) throws ClientException;
    MovementTransfer approveMovementTransferToCana(MovementTransfer transfer, long userRrn) throws ClientException ;
    MovementLine saveMovementTransferLineToCana(MovementTransfer transfer, MovementLine line, long userRrn) throws ClientException;
    MovementTransfer saveMovementTransferLineToCana(MovementTransfer transfer, List<MovementLine> lines, long userRrn) throws ClientException;
    List<VMaterialStorageList> getMaterialStorageList(Long orgRrn,String whereClause) throws ClientException;
    MovementIn bjApproveMovementIn(MovementIn in, MovementIn.InType inType, boolean writeOffFlag, boolean seniorApprove, long userRrn, boolean isWriteOff) throws ClientException;
    List<SparesMaterialUse> getSparesMaterialUse(long orgRrn,String whereClause) throws ClientException;
    List<SparesMaterialUse> getSparesMaterialUseExport(long orgRrn,String whereClause) throws ClientException;
	MovementOut approveXZMovementOut(MovementOut out,List<MovementLine> outLines, MovementOut.OutType outType, long userRrn, 
			boolean isOnHand, boolean isWriteOff) throws ClientException;
	MovementOut approveSalesMovementOutBT(MovementOut out, long userRrn, boolean isWriteOff) throws ClientException;
	MovementOut saveMovementOutLineBT(MovementOut out, List<MovementLine> lines, MovementOut.OutType outType, long userRrn) throws ClientException; 
	
	MovementIn bjApproveMovementIn2(MovementIn in, MovementIn.InType inType, boolean writeOffFlag, boolean seniorApprove, long userRrn, boolean isWriteOff) throws ClientException;

	List<Material> queryMaterialQtysKeGong(Long orgRrn, Long materialRrn, String catalog2, String whereClause) throws ClientException;


	//----------------------------------车间库存-----线边仓-----------------------------------
	MovementWorkShopLine newMovementWorkShopLine(MovementWorkShop movementWS) throws ClientException;
	void deleteMovementWorkShop(MovementWorkShop workShop, long userRrn) throws ClientException;
	void deleteMovementWorkShopLine(MovementWorkShopLine movementLine, boolean allFlag, long userRrn) throws ClientException;
	
	//车间领料
	MovementWorkShopLine saveMovementWorkShopRequestionLine(MovementWorkShopRequestion wsRequestion, MovementWorkShopLine line, long userRrn) throws ClientException;
	MovementWorkShopRequestion saveMovementWorkShopRequestionLine(MovementWorkShopRequestion wsRequestion, List<MovementWorkShopLine> lines, long userRrn) throws ClientException;
	MovementWorkShopRequestion approveMovementWorkShopRequestion(MovementWorkShopRequestion wsRequestion, long userRrn) throws ClientException;

	
	MovementWorkShop saveMovementWorkShopLineLot(MovementWorkShop workShop, List<MovementWorkShopLine> lines, long userRrn) throws ClientException;
	void updateWorkShopLotStorage(long orgRrn, long lotRrn, long warehouseRrn, BigDecimal qty, long userRrn) throws ClientException;
	List<Lot> getWorkShopLotStorage(long warehouseRrn, long materialRrn)  throws ClientException;
	WorkShopLotStorage getWorkShopLotStorage(long orgRrn, long lotRrn, long warehouseRrn, long userRrn) throws ClientException;
	
	//车间配送
	MovementWorkShopLine saveMovementWorkShopDeliveryLine(MovementWorkShopDelivery workShop, MovementWorkShopLine line, long userRrn) throws ClientException;
	MovementWorkShopDelivery saveMovementWorkShopDeliveryLine(MovementWorkShopDelivery workShop, List<MovementWorkShopLine> lines, long userRrn) throws ClientException;
	MovementWorkShopDelivery approveMovementWorkShopDelivery(MovementWorkShopDelivery wsTransfer, long userRrn) throws ClientException;
	
	//车间回料
	MovementWorkShopLine saveMovementWorkShopReclaim(MovementWorkShopReclaim workShop, MovementWorkShopLine line, long userRrn) throws ClientException;
	MovementWorkShopReclaim saveMovementWorkShopReclaimLine(MovementWorkShopReclaim workShop, List<MovementWorkShopLine> lines, long userRrn) throws ClientException;
	MovementWorkShopReclaim approveMovementWorkShopReclaim(MovementWorkShopReclaim workShop, long userRrn) throws ClientException;

	void updateWorkShopStorage(long orgRrn, long materialRrn, long warehouseRrn, BigDecimal qty, boolean onHandFlag, boolean writeOffFlag, long userRrn) throws ClientException;
	void updateWorkShopStorage(long orgRrn, long materialRrn, long warehouseRrn, BigDecimal qty, boolean writeOffFlag, long userRrn) throws ClientException;
	WorkShopStorage getMaterialWorkShopStorage(long orgRrn, long materialRrn, long warehouseRrn, long userRrn)  throws ClientException;
	MovementTransfer moNeedTransferYS(long orgRrn, ManufactureOrder mo,BigDecimal qty,long userRrn) throws ClientException;
	
	void runSpGetQtyAllocation(Date startDate,Date endDate,String monthDate,String operUser,String operDate) throws ClientException;
	//华宇待品库为暂借库,程裕民修改名字
	MovementLine saveMovementTransferLineDpk(MovementTransfer transfer, MovementLine line, long userRrn) throws ClientException;
	MovementTransfer saveMovementTransferLineDpk(MovementTransfer transfer, List<MovementLine> lines, long userRrn) throws ClientException;
	
	MovementWorkShopUnqualified approveMovementTransferUnqualified(MovementWorkShopUnqualified   transfer, long userRrn) throws ClientException;
	MovementWorkShopLine saveMovementWorkShopUnqualifiedLine(MovementWorkShopUnqualified workShop, MovementWorkShopLine line, long userRrn) throws ClientException;
	MovementWorkShopUnqualified saveMovementWorkShopUnqualifiedLine(MovementWorkShopUnqualified workShop, List<MovementWorkShopLine> lines, long userRrn) throws ClientException;
	
	List<VStorageMaterial> getWmsStorage(String materialId,String warehouseId) throws ClientException;
	
	List<Lot> getOptionalOutLotNoWms(MovementLine outLine) throws ClientException;
	Lot getLotByLotIdNoWms(long orgRrn, String lotId) throws ClientException;
	Lot getLotByLotIdInWms(long orgRrn, String lotId) throws ClientException;
	List<Lot> getOptionalOutLotInWms(MovementLine outLine) throws ClientException;
	BigDecimal getQtyInWmsStorage(String materialId,String warehouseId) throws ClientException;
	List<Material> getServiceMaterialAlarmYS(Long orgRrn,String whereClause) throws ClientException;
	
	BigDecimal getLotStorageWms(long orgRrn, long lotRrn, long warehouseRrn, long userRrn,Lot lot)  throws ClientException;
	BigDecimal getLotStorageNew(long orgRrn, long lotRrn, long warehouseRrn, long userRrn,Lot lot)  throws ClientException;
	
	List<String> getMovementInByWms(String whereClause) throws ClientException;
	MovementIn saveMovementInByWms(MovementIn in, List<MovementLine> lines, MovementIn.InType inType, long userRrn,List<StockIn> stockIns) throws ClientException;

	MovementWorkShopServices approveMovementServicesStorage(MovementWorkShopServices workShopServices, long userRrn) throws ClientException ;
	MovementWorkShopLine saveMovementWorkShopServicesLine(MovementWorkShopServices workShop, MovementWorkShopLine line, long userRrn) throws ClientException;

	MovementWorkShopLine saveMovementVirtualHouseLine(MovementWorkShopVirtualHouse virtualHouse, MovementWorkShopLine line, long userRrn) throws ClientException;
	MovementWorkShopVirtualHouse saveMovementVirtualHouseLine(MovementWorkShopVirtualHouse virtualHouse, List<MovementWorkShopLine> lines, long userRrn) throws ClientException;
void deleteMovementWorkShopVirtualHouse(MovementWorkShopVirtualHouse virtualHouse,boolean completeFlag, long userRrn)throws ClientException;
public List<Material> queryMaterialQtysAlarmYn(Long orgRrn,String whereClause,String whereClause2,String whereClause3) throws ClientException;
}

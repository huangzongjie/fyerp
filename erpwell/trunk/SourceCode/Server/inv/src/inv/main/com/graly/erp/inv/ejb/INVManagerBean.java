package com.graly.erp.inv.ejb;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.CodeWarehouse;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.model.Storage;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.AlarmData;
import com.graly.erp.inv.model.AlarmTarget;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.erp.inv.model.Locator;
import com.graly.erp.inv.model.LotOutSerial;
import com.graly.erp.inv.model.LotStorage;
import com.graly.erp.inv.model.MaterialLocator;
import com.graly.erp.inv.model.MaterialTrace;
import com.graly.erp.inv.model.MaterialTraceDetail;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementHis;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementLineOutSerial;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.model.MovementWorkShop;
import com.graly.erp.inv.model.MovementWorkShopDelivery;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.MovementWorkShopLineLot;
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
import com.graly.erp.inv.model.SalesOrderSum;
import com.graly.erp.inv.model.ServicesStorage;
import com.graly.erp.inv.model.SparesMaterialUse;
import com.graly.erp.inv.model.StockIn;
import com.graly.erp.inv.model.StockOut;
import com.graly.erp.inv.model.StockSpecial;
import com.graly.erp.inv.model.VInvNoTransfer;
import com.graly.erp.inv.model.VMaterialStorageList;
import com.graly.erp.inv.model.VMovementLineTempEstimate;
import com.graly.erp.inv.model.VOutDetail;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.model.WorkShopLotStorage;
import com.graly.erp.inv.model.WorkShopStorage;
import com.graly.erp.inv.model.MovementIn.InType;
import com.graly.erp.inv.model.WarehouseRack;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.sal.client.SALManager;
import com.graly.erp.vdm.model.Vendor;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.erp.wip.model.LargeLot;
import com.graly.erp.wip.model.LargeWipLot;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.ManufactureOrderLineLot;
import com.graly.erp.wip.model.MaterialMoveSum;
import com.graly.erp.wiphis.model.AdinLotHis;
import com.graly.erp.wiphis.model.AdouLotHis;
import com.graly.erp.wiphis.model.AouLotHis;
import com.graly.erp.wiphis.model.DouLotHis;
import com.graly.erp.wiphis.model.ESpiltLotHis;
import com.graly.erp.wiphis.model.GenerateLotHis;
import com.graly.erp.wiphis.model.IqcLotHis;
import com.graly.erp.wiphis.model.OinLotHis;
import com.graly.erp.wiphis.model.OouLotHis;
import com.graly.erp.wiphis.model.PinLotHis;
import com.graly.erp.wiphis.model.RinLotHis;
import com.graly.erp.wiphis.model.SouLotHis;
import com.graly.erp.wiphis.model.TransferLotHis;
import com.graly.erp.wiphis.model.WinLotHis;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADMessage;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;
import com.graly.framework.security.model.ADUser;
import com.graly.framework.security.model.ADUserGroup;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotComponent;
import com.graly.mes.wip.model.LotConsume;
import com.graly.mes.wiphis.model.LotHis;

@Stateless
@Remote(INVManager.class)
@Local(INVManager.class)
public class INVManagerBean implements INVManager {
	
	private static final Logger logger = Logger.getLogger(INVManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private ADManager adManager;
	
	@EJB
	private BASManager basManager;
	
	@EJB
	private SALManager salManager;
	
	@EJB
	private PDMManager pdmManager;
	
	@Resource(mappedName="java:/OracleDS")
	DataSource ds;
	
	public ReceiptLine newReceiptLine(Receipt receipt) throws ClientException {
		ReceiptLine receiptLine = new ReceiptLine();
		try{
			if (receipt != null && receipt.getObjectRrn() != null) {
				receipt = em.find(Receipt.class, receipt.getObjectRrn());
				long maxLineNo = 1;
				for (ReceiptLine line : receipt.getReceiptLines()) {
					maxLineNo = maxLineNo < line.getLineNo() ? line.getLineNo() : maxLineNo;
				}
				receiptLine.setOrgRrn(receipt.getOrgRrn());
				receiptLine.setLineNo((long)Math.ceil(maxLineNo / 10) * 10 + 10);
			} else {
				receiptLine.setLineNo(10L);
			}
			receiptLine.setLineStatus(Receipt.STATUS_DRAFTED);
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return receiptLine;
	}
	
	public Receipt createReceiptFromPO(Receipt receipt, List<PurchaseOrderLine> poLines, long userRrn,String wmsWarehouse) throws ClientException {
		try{
			List<ReceiptLine> receiptLines = new ArrayList<ReceiptLine>();
			long orgRrn = poLines.get(0).getOrgRrn();
			long startLineNo = 10;
			if (receipt == null) {//add
				receipt = new Receipt();
				receipt.setOrgRrn(orgRrn);
				receipt.setWmsWarehouse(wmsWarehouse);
			} else {//modify
				startLineNo = newReceiptLine(receipt).getLineNo();
			}
			Long poRrn = receipt.getPoRrn();
			if(poLines.get(0).getPoRrn()!=null){
				PurchaseOrder po = em.find(PurchaseOrder.class, poLines.get(0).getPoRrn());
				if(po!=null && po.getObjectRrn() !=null){
					receipt.setVendorId(po.getVendorId());
					receipt.setVendorName(po.getVendorName());
					receipt.setPurchaser(po.getPurchaser());
					receipt.setPiId(po.getPiId());
					receipt.setInternalOrderId(po.getInternalOrderId());
				}
			}

			Long warehouseRrn = receipt.getWarehouseRrn();
			int i = 0;
			for (PurchaseOrderLine poLine : poLines) {
				if (poRrn == null) {//add
					poRrn = poLine.getPoRrn();
					receipt.setPoId(poLine.getPoId());
					receipt.setPoRrn(poRrn);
				} else if (!poRrn.equals(poLine.getPoRrn())){
					throw new ClientException("inv.different_po");
				}
				if (warehouseRrn == null) {//add
					warehouseRrn = poLine.getWarehouseRrn();

				} else if (!warehouseRrn.equals(poLine.getWarehouseRrn())){
					throw new ClientException("inv.different_warehouse");
				}
				ReceiptLine receiptLine = new ReceiptLine();
				receiptLine.setOrgRrn(orgRrn);
				receiptLine.setMaterialRrn(poLine.getMaterialRrn());
				receiptLine.setLineNo(startLineNo + i * 10);
				
				BigDecimal qtyReceipt = poLine.getQty().subtract(poLine.getQtyDelivered() == null ? BigDecimal.ZERO :  poLine.getQtyDelivered());
				if (qtyReceipt.doubleValue() < 0) {
					qtyReceipt = BigDecimal.ZERO;
				}
				receiptLine.setQtyReceipt(qtyReceipt);
				receiptLine.setPoLineRrn(poLine.getObjectRrn());
				receiptLine.setUomId(poLine.getUomId());
				receiptLine.setUnitPrice(poLine.getUnitPrice());
				receiptLine.setLineTotal(receiptLine.getQtyReceipt().multiply(receiptLine.getUnitPrice()));
				receiptLines.add(receiptLine);
				i++;
			}
			if (warehouseRrn != null) {
				Warehouse warehouse = em.find(Warehouse.class, warehouseRrn);
				receipt.setWarehouseRrn(warehouseRrn);
				receipt.setWarehouseId(warehouse.getWarehouseId());
			}
			receipt.setReceiptLines(receiptLines);
			return saveReceipt(receipt, userRrn);
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Receipt saveReceipt(Receipt receipt, long userRrn) throws ClientException {
		return saveReceiptLine(receipt, receipt.getReceiptLines(), userRrn);
	}
	
	public ReceiptLine saveReceiptLine(Receipt receipt, ReceiptLine receiptLine , long userRrn) throws ClientException {
		List<ReceiptLine> receiptLines = new ArrayList<ReceiptLine>();
		receiptLines.add(receiptLine);
		receipt = saveReceiptLine(receipt, receiptLines, userRrn);
		return receipt.getReceiptLines().get(0);
	}
	
	private Receipt saveReceiptLine(Receipt receipt, List<ReceiptLine> receiptLines , long userRrn) throws ClientException {
		try{
			if (receipt.getObjectRrn() == null) {
				//未被持久的receipt,initialize receipt
				receipt.setIsActive(true);
				receipt.setCreatedBy(userRrn);
				receipt.setCreated(new Date());
				receipt.setDateCreated(new Date());
				receipt.setTotalLines(0L);
				receipt.setTotal(BigDecimal.ZERO);
				receipt.setDocStatus(Receipt.STATUS_DRAFTED);
				receipt.setDocType(Receipt.DOCTYPE_REC);
				
				String docId = receipt.getDocId();
				if (docId == null || docId.length() == 0) {
					receipt.setDocId(generateReceiptCode(receipt));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<Receipt> receipts = adManager.getEntityList(receipt.getOrgRrn(), Receipt.class, 2, whereClause, "");
					if (receipts.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				
				ADUser user = em.find(ADUser.class, userRrn);
				receipt.setUserCreated(user.getUserName());
				em.persist(receipt);
			} 
			for (ReceiptLine receiptLine : receiptLines) {
				PurchaseOrderLine poLine = null;
				if (receiptLine.getPoLineRrn() != null) {
					poLine = em.find(PurchaseOrderLine.class, receiptLine.getPoLineRrn());
				}
				
				if (receiptLine.getObjectRrn() == null) {
					//未被持久的receiptLine,initialize receiptLine
					receiptLine.setIsActive(true);
					receiptLine.setCreatedBy(userRrn);
					receiptLine.setCreated(new Date());
					receiptLine.setLineStatus(Requisition.STATUS_DRAFTED);
					receipt.setTotalLines(receipt.getTotalLines() + 1);
					receipt.setTotal(receipt.getTotal().add(receiptLine.getLineTotal()));
					if (poLine != null) {
						BigDecimal oldDelivered = poLine.getQtyDelivered() == null ? BigDecimal.ZERO : poLine.getQtyDelivered();
						poLine.setQtyDelivered(oldDelivered.add(receiptLine.getQtyReceipt()));
					}
				} else {
					//update
					ReceiptLine oldLine = em.find(ReceiptLine.class, receiptLine.getObjectRrn());
					if (poLine != null) {
						BigDecimal oldDelivered = poLine.getQtyDelivered() == null ? BigDecimal.ZERO : poLine.getQtyDelivered();
						poLine.setQtyDelivered(oldDelivered.subtract(oldLine.getQtyReceipt()).add(receiptLine.getQtyReceipt()));
					}
					receiptLine.setLineTotal(receiptLine.getQtyReceipt().multiply(receiptLine.getUnitPrice()));
					receipt.setTotal(receipt.getTotal().subtract(oldLine.getLineTotal().add(receiptLine.getLineTotal())));
				}
				receiptLine.setUpdatedBy(userRrn);
				receipt.setUpdatedBy(userRrn);
				if (receiptLine.getObjectRrn() == null) {
					receiptLine.setReceiptRrn(receipt.getObjectRrn());
					receiptLine.setReceiptId(receipt.getDocId());
					em.persist(receiptLine);
				} else {
					em.merge(receiptLine);
				}
				if (poLine != null) {
					em.merge(poLine);
				}
			}
			receipt.setUpdatedBy(userRrn);
			em.merge(receipt);
			receipt.setReceiptLines(receiptLines);
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return receipt;
	}
	
	public Receipt approveReceipt(Receipt receipt, long userRrn) throws ClientException {
		try{
			receipt.setDocStatus(Receipt.STATUS_APPROVED);
			receipt.setUpdatedBy(userRrn);
			receipt.setDateApproved(new Date());
			ADUser user = em.find(ADUser.class, userRrn);
			receipt.setUserApproved(user.getUserName());
			em.merge(receipt);
			List<AlarmData> alarmDatas = new ArrayList<AlarmData>();
			
			boolean isIqcAlarm =false;//iqc警报
			long iqcTime =9999L;//检验周期
			PurchaseOrder po = em.find(PurchaseOrder.class, receipt.getPoRrn());
			Vendor vendor = em.find(Vendor.class, po.getVendorRrn());
			for (ReceiptLine receiptLine : receipt.getReceiptLines()) {
				receiptLine.setLineStatus(Receipt.STATUS_APPROVED);
				receiptLine.setUpdatedBy(userRrn);
				em.merge(receiptLine);
				//如果isInspe..为false代表，不免检
				if(receiptLine!=null){
					Material material = em.find(Material.class, receiptLine.getMaterialRrn());
					PurchaseOrderLine poline = em.find(PurchaseOrderLine.class, receiptLine.getPoLineRrn());
					if(material!=null && material.getObjectRrn()!=null){
						if(poline.getIsInspectionFree()!=null){//祁椅要求采用订单上面的是否免检
							if(!poline.getIsInspectionFree()){
								isIqcAlarm=true;
							}
						}else{//防止功能提交时之前的数据为空 采用物料上面的是否免检
							if(!material.getIsInspectionFree()){
								isIqcAlarm=true;
							}
						}
						if(material.getIqcLeadTime()!=null &&material.getIqcLeadTime() < iqcTime){
							iqcTime = material.getIqcLeadTime();
						}
					}
					
					//添加信息
					if(receipt.getWmsWarehouse()!=null && !"".equals(receipt.getWmsWarehouse())){
						StockIn stockIn = new StockIn();
						stockIn.setOrgRrn(receipt.getOrgRrn());
						stockIn.setIsActive(true);
						stockIn.setReceiptId(receipt.getDocId());
						stockIn.setReceiptTime(new Date());
						stockIn.setReceiptType("REC");
						stockIn.setMaterialCode(material.getMaterialId());
						if(Lot.LOTTYPE_MATERIAL.equals(material.getLotType())){
							stockIn.setBatch(material.getMaterialId());
						}else{
							stockIn.setBatch(poline.getBarCode());
						}
						stockIn.setQuality(receiptLine.getQtyReceipt());
						stockIn.setErpWrite(1L);
						stockIn.setErpWriteTime(new Date());
						stockIn.setWmsRead(0L);
						stockIn.setSupplierName(poline.getVendorId());
						stockIn.setUserName(user.getUserName());
						em.persist(stockIn);
					}
					
					
					if(receiptLine!=null && receiptLine.getOrgRrn().equals(139420L)){
						AlarmData alarmData = new AlarmData();
						alarmData.setOrgRrn(receipt.getOrgRrn());
						alarmData.setIsActive(true);
						alarmData.setCreated(new Date());
						alarmData.setCreatedBy(userRrn);
						alarmData.setUpdated(new Date());
						alarmData.setUpdatedBy(userRrn);
						alarmData.setOwner(user.getUserName());
						alarmData.setStartTime(new Date());
						alarmData.setField1(receipt.getDocId());//收货单号
						alarmData.setStatus(AlarmData.STATUS_OPEN);//是否关闭
						alarmData.setField2(receipt.getPoId());//采购订单号
						alarmData.setField3(null);//检验单号
						alarmData.setField4(iqcTime+"");//检验周期
						alarmData.setField5(material.getMaterialId());//物料编号
						alarmData.setField6(material.getName());//物料名称	
						alarmData.setField7(null);//是否同意入库
						alarmData.setField8(receiptLine.getQtyReceipt()+"");//收货数量
						if(vendor!=null){
							alarmData.setField22(vendor.getVendorId());//供应商
							alarmData.setField26(vendor.getCompanyName());//供应商名称
						}
						alarmData.setField9(po.getComments());
						alarmData.setField10(null);
						alarmData.setField11(receipt.getObjectRrn());//receiptObjectRrn
						alarmData.setField12(receipt.getPoRrn());//poRrn
						alarmData.setField13(receipt.getDateCreated());//制单时间
						alarmData.setField17(receipt.getCreatedBy());//创建人
						alarmData.setField25(poline.getUrgency());//紧急程度
						alarmData.setField27(poline.getQty()+"");//采购数量
						alarmData.setField31(receiptLine.getObjectRrn());//收货行rrn
						alarmData.setAlarmType(AlarmTarget.TARGET_TYPE_IQC);
						alarmDatas.add(alarmData);
					}

				}
			}
			//警报处理：收货审核的时候会发送IQC警报、仓库警报
			if(receipt!=null){
				if(iqcTime == 9999L){
					//都没有检验周期
					iqcTime= 0L;
				}
				if(isIqcAlarm){
					for(AlarmData alarmData : alarmDatas){
						em.persist(alarmData);
					}
				}else{
					//对于免检收货单，系统自动创建检验单，并且自动审核(原业务是：李嘉怡手动创建检验单，自己审核)
					Iqc iqc = createIqcFromReceiptLines(null, receipt, receipt.getReceiptLines(), userRrn);
					approveIqc(iqc, userRrn);
				}
			}
			return receipt;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Receipt closeReceipt(Receipt receipt, long userRrn) throws ClientException {
		try{
			receipt.setDocStatus(Receipt.STATUS_CLOSED);
			receipt.setUpdatedBy(userRrn);
			em.merge(receipt);
			for (ReceiptLine receiptLine : receipt.getReceiptLines()) {
				receiptLine.setLineStatus(Receipt.STATUS_CLOSED);
				receiptLine.setUpdatedBy(userRrn);
				em.merge(receiptLine);
				//撤销收货单,关闭IQC警报
				if(receiptLine.getOrgRrn()!=null && receiptLine.getOrgRrn().equals(139420L)){
					List<AlarmData> alarmDatas = adManager.getEntityList(receipt.getOrgRrn(), AlarmData.class,
							Integer.MAX_VALUE,"field1 = '"+receipt.getDocId()+"' and alarmType ='"+AlarmTarget.TARGET_TYPE_IQC+
							"' and status='"+AlarmData.STATUS_OPEN+"' and field31 ="+receiptLine.getObjectRrn(),null);
					if(alarmDatas!=null && alarmDatas.size() >0){
						for(AlarmData alarmData :alarmDatas){
							alarmData.setUpdated(new Date());
							alarmData.setUpdatedBy(userRrn);
							alarmData.setCloser(userRrn);
							alarmData.setCloseTime(new Date());
							alarmData.setStatus(AlarmData.STATUS_CLOSE);
							em.merge(alarmData);
						}
					}
				}
			}
			return receipt;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteReceipt(Receipt receipt, long userRrn)throws ClientException {
		try{
			if(receipt != null && receipt.getObjectRrn() != null) {
				receipt = em.find(Receipt.class, receipt.getObjectRrn());
				for (int i=0; i< receipt.getReceiptLines().size(); i++){
					ReceiptLine line= receipt.getReceiptLines().get(i);
					deleteReceiptLine(line, userRrn);
				}
				em.remove(receipt);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteReceiptLine(ReceiptLine receiptLine, long userRrn)throws ClientException {
		try {
			if(receiptLine != null && receiptLine.getObjectRrn() != null) {
				receiptLine = em.find(ReceiptLine.class, receiptLine.getObjectRrn());
				// 更新receipt
				if(receiptLine.getReceiptRrn() != null) {
					Receipt receipt = em.find(Receipt.class, receiptLine.getReceiptRrn());					
					receipt.setTotalLines(receipt.getTotalLines() - 1);
					em.merge(receipt);
				}
				// 若对应着采购订单行，则更新采购订单行已收货数
				if(receiptLine.getPoLineRrn() != null) {
					PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, receiptLine.getPoLineRrn());
					BigDecimal oldDelivered = poLine.getQtyDelivered() == null ? BigDecimal.ZERO : poLine.getQtyDelivered();
					poLine.setQtyDelivered(oldDelivered.subtract(receiptLine.getQtyReceipt()));
					em.merge(poLine);
				}
				em.remove(receiptLine);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public IqcLine newIqcLine(Iqc iqc) throws ClientException {
		IqcLine iqcLine = new IqcLine();
		try{
			if (iqc != null && iqc.getObjectRrn() != null) {
				iqc = em.find(Iqc.class, iqc.getObjectRrn());
				long maxLineNo = 1;
				for (IqcLine line : iqc.getIqcLines()) {
					maxLineNo = maxLineNo < line.getLineNo() ? line.getLineNo() : maxLineNo;
				}
				iqcLine.setLineNo((long)Math.ceil(maxLineNo / 10) * 10 + 10);
			} else {
				iqcLine.setLineNo(10L);
			}
			iqcLine.setLineStatus(PurchaseOrder.STATUS_DRAFTED);
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return iqcLine;
	}
	
	public Iqc createIqcFromReceipt(Iqc iqc, Receipt receipt, long userRrn) throws ClientException {
		try{
			List<IqcLine> iqcLines = new ArrayList<IqcLine>();
			long startLineNo = 10;
			if (iqc == null) {//add
				iqc = new Iqc();
				iqc.setOrgRrn(receipt.getOrgRrn());
			} else {//modify
				startLineNo = newIqcLine(iqc).getLineNo();
			}
			iqc.setPoRrn(receipt.getPoRrn());
			iqc.setPoId(receipt.getPoId());
			iqc.setReceiptRrn(receipt.getObjectRrn());
			iqc.setReceiptId(receipt.getDocId());
			iqc.setWarehouseRrn(receipt.getWarehouseRrn());
			iqc.setWarehouseId(receipt.getWarehouseId());
			iqc.setWmsWarehouse(receipt.getWmsWarehouse());
			int i = 0;
			for (ReceiptLine receiptLine : receipt.getReceiptLines()) {
				IqcLine iqcLine = new IqcLine();
				iqcLine.setOrgRrn(receipt.getOrgRrn());
				iqcLine.setMaterialRrn(receiptLine.getMaterialRrn());
				iqcLine.setLineNo(startLineNo + i * 10);
				iqcLine.setQtyIqc(receiptLine.getQtyReceipt());
				iqcLine.setQtyQualified(receiptLine.getQtyReceipt());
				iqcLine.setUomId(receiptLine.getUomId());
				iqcLine.setPoLineRrn(receiptLine.getPoLineRrn());
				iqcLine.setReceiptLineRrn(receiptLine.getObjectRrn());
				iqcLines.add(iqcLine);
				i++;
			} 
			iqc.setIqcLines(iqcLines);
			receipt.setIsIqc(true);
			em.merge(receipt);
			
			return saveIqc(iqc, userRrn);
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public Iqc createIqcFromReceiptLines(Iqc iqc, Receipt receipt, List<ReceiptLine> receiptLines, long userRrn)
			throws ClientException {
		try{
			List<IqcLine> iqcLines = new ArrayList<IqcLine>();
			long startLineNo = 10;
			if (iqc == null) {//add
				iqc = new Iqc();
				iqc.setOrgRrn(receipt.getOrgRrn());
			} else {//modify
				startLineNo = newIqcLine(iqc).getLineNo();
			}
			iqc.setPoRrn(receipt.getPoRrn());
			PurchaseOrder po = em.find(PurchaseOrder.class, receipt.getPoRrn());
			if(po!=null){//仓库贠向刚希望质检单看到采购订单备注
				iqc.setPoComments(po.getComments());
			}
			iqc.setPoId(receipt.getPoId());
			iqc.setReceiptRrn(receipt.getObjectRrn());
			iqc.setReceiptId(receipt.getDocId());
			iqc.setWarehouseRrn(receipt.getWarehouseRrn());
			iqc.setWarehouseId(receipt.getWarehouseId());
			iqc.setPiId(receipt.getPiId());
			iqc.setInternalOrderId(receipt.getInternalOrderId());
			iqc.setWmsWarehouse(receipt.getWmsWarehouse());
			int i = 0;
			for (ReceiptLine receiptLine : receiptLines) {
				IqcLine iqcLine = new IqcLine();
				iqcLine.setOrgRrn(receipt.getOrgRrn());
				iqcLine.setMaterialRrn(receiptLine.getMaterialRrn());
				iqcLine.setLineNo(startLineNo + i * 10);
				iqcLine.setQtyIqc(receiptLine.getQtyReceipt());
				iqcLine.setQtyQualified(receiptLine.getQtyReceipt());
				iqcLine.setUomId(receiptLine.getUomId());
				iqcLine.setPoLineRrn(receiptLine.getPoLineRrn());
				iqcLine.setReceiptLineRrn(receiptLine.getObjectRrn());
				receiptLine.setIsIqc(true);
				em.merge(receiptLine);
				iqcLines.add(iqcLine);
				i++;
				//根据收货行,关闭每一行的警报
				if(iqcLine.getOrgRrn()!=null && iqcLine.getOrgRrn().equals(139420L)){
					List<AlarmData> alarmDatas = adManager.getEntityList(receipt.getOrgRrn(), AlarmData.class,
							Integer.MAX_VALUE,"field1 = '"+receipt.getDocId()+"' and alarmType ='"+AlarmTarget.TARGET_TYPE_IQC+
							"' and status='"+AlarmData.STATUS_OPEN+"' and field31 ="+receiptLine.getObjectRrn(),null);
					if(alarmDatas!=null && alarmDatas.size() >0){
						for(AlarmData alarmData :alarmDatas){
							alarmData.setUpdated(new Date());
							alarmData.setUpdatedBy(userRrn);
							alarmData.setCloser(userRrn);
							alarmData.setCloseTime(new Date());
							alarmData.setStatus(AlarmData.STATUS_CLOSE);
							em.merge(alarmData);
						}
					}
				}
			}
			iqc.setIqcLines(iqcLines);
			em.flush();
			receipt = (Receipt) adManager.getEntity(receipt);
			List<ReceiptLine> unIqcReceiptLines = new ArrayList<ReceiptLine>();
			for(ReceiptLine receiptLine : receipt.getReceiptLines()){//获得receipt中所有未产生检验行的receiptLine
				if(!receiptLine.getIsIqc()){
					unIqcReceiptLines.add(receiptLine);
				}
			}
			if(unIqcReceiptLines.size() == 0){//如果所有的receiptLine都产生了检验行则将receipt的isIqc状态置为'Y'
				receipt.setIsIqc(true);
				em.merge(receipt);
			}
			//警报处理,关闭IQC警报
			if(receipt!=null && receipt.getOrgRrn().equals(139420L)){
				//收货行全部通过检验,才能关闭警报
				if(receipt.getIsIqc()){
					List<AlarmData> alarmDatas = adManager.getEntityList(receipt.getOrgRrn(), AlarmData.class,
							Integer.MAX_VALUE,"field1 = '"+receipt.getDocId()+"' and alarmType ='"+AlarmTarget.TARGET_TYPE_IQC+
							"' and status='"+AlarmData.STATUS_OPEN+"' and field31 is null",null);
					if(alarmDatas!=null && alarmDatas.size() >0){
						for(AlarmData alarmData :alarmDatas){
							alarmData.setUpdated(new Date());
							alarmData.setUpdatedBy(userRrn);
							alarmData.setCloser(userRrn);
							alarmData.setCloseTime(new Date());
							alarmData.setStatus(AlarmData.STATUS_CLOSE);
							em.merge(alarmData);
						}
					}
				}
			}

			return saveIqc(iqc, userRrn);
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Iqc saveIqc(Iqc iqc, long userRrn) throws ClientException {
		return saveIqcLine(iqc, iqc.getIqcLines(), userRrn);
	}
	
	public IqcLine saveIqcLine(Iqc iqc, IqcLine iqcLine , long userRrn) throws ClientException {
		List<IqcLine> iqcLines = new ArrayList<IqcLine>();
		iqcLines.add(iqcLine);
		iqc = saveIqcLine(iqc, iqcLines, userRrn);
		return iqc.getIqcLines().get(0);
	}
	
	private Iqc saveIqcLine(Iqc iqc, List<IqcLine> iqcLines , long userRrn) throws ClientException {
		try{
			if (iqc.getObjectRrn() == null) {
				//未被持久的iqc,initialize receipt
				iqc.setIsActive(true);
				iqc.setCreatedBy(userRrn);
				iqc.setCreated(new Date());
				iqc.setDateCreated(new Date());
				iqc.setTotalLines(0L);
				iqc.setDocType(Iqc.DOCTYPE_IQC);
				iqc.setDocStatus(Requisition.STATUS_DRAFTED);
				String docId = iqc.getDocId();
				if (docId == null || docId.length() == 0) {
					iqc.setDocId(generateIqcCode(iqc));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<Receipt> receipts = adManager.getEntityList(iqc.getOrgRrn(), Receipt.class, 2, whereClause, "");
					if (receipts.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				
				ADUser user = em.find(ADUser.class, userRrn);
				iqc.setUserCreated(user.getUserName());
				em.persist(iqc);
			}
			for (IqcLine iqcLine : iqcLines) {
				PurchaseOrderLine poLine = null;
				if (iqcLine.getPoLineRrn() != null) {
					poLine = em.find(PurchaseOrderLine.class, iqcLine.getPoLineRrn());
				}
				
				if (iqcLine.getObjectRrn() == null) {
					//未被持久的iqcLine,initialize iqcLine
					iqcLine.setIsActive(true);
					iqcLine.setCreatedBy(userRrn);
					iqcLine.setCreated(new Date());
					iqcLine.setLineStatus(Requisition.STATUS_DRAFTED);
					iqc.setTotalLines(iqc.getTotalLines() + 1);
					if (poLine != null) {
						BigDecimal oldTest = poLine.getQtyTested() == null ? BigDecimal.ZERO : poLine.getQtyTested();
						poLine.setQtyTested(oldTest.add(iqcLine.getQtyIqc()));
					}
				} else {
					//update 
					IqcLine oldLine = em.find(IqcLine.class, iqcLine.getObjectRrn());
					if (poLine != null) {
						BigDecimal oldQualified = poLine.getQtyQualified() == null ? BigDecimal.ZERO : poLine.getQtyQualified();
						poLine.setQtyQualified(oldQualified.subtract(oldLine.getQtyQualified().add(iqcLine.getQtyQualified())));
					}
				}
				iqcLine.setUpdatedBy(userRrn);
				iqc.setUpdatedBy(userRrn);
				if (iqcLine.getObjectRrn() == null) {
					iqcLine.setIqcRrn(iqc.getObjectRrn());
					iqcLine.setIqcId(iqc.getDocId());
					em.persist(iqcLine);
				} else {
					em.merge(iqcLine);
				}
				if (poLine != null) {
					em.merge(poLine);
				}
			}
			iqc.setUpdatedBy(userRrn);
			em.merge(iqc);
			iqc.setIqcLines(iqcLines);
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return iqc;
	}
	
	public Iqc approveIqc(Iqc iqc, long userRrn) throws ClientException {
		try{
			iqc.setDocStatus(Receipt.STATUS_APPROVED);
			iqc.setUpdatedBy(userRrn);
			iqc.setDateApproved(new Date());
			ADUser user = em.find(ADUser.class, userRrn);
			iqc.setUserApproved(user.getUserName());
			em.merge(iqc);
			PurchaseOrder po = em.find(PurchaseOrder.class, iqc.getPoRrn());
			Vendor vendor = em.find(Vendor.class, po.getVendorRrn());
			for (IqcLine iqcLine : iqc.getIqcLines()) {
				if(iqcLine.getQtyQualified().compareTo(BigDecimal.ZERO) == 0){
					iqcLine.setLineStatus(Receipt.STATUS_COMPLETED);
					iqcLine.setUpdatedBy(userRrn);
				}else{
					iqcLine.setLineStatus(Receipt.STATUS_APPROVED);
					iqcLine.setUpdatedBy(userRrn);
				}
				if(iqc!=null && (iqc.getOrgRrn().equals(12644730L)||iqc.getOrgRrn().equals(41673024L)||iqc.getOrgRrn().equals(63506125L))		){
					//对于bareCode系统在采购订单就生成批次，无需手动去创建,找到批次设置批次已经创建
					Material material = em.find(Material.class, iqcLine.getMaterialRrn());
					PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, iqcLine.getPoLineRrn());
					if(material!=null){
						if(!Lot.LOTTYPE_MATERIAL.equals(material.getLotType())){
							if(poLine !=null && poLine.getBarCode()!=null){
								List<Lot> lots = null;
								if(Lot.LOTTYPE_SERIAL.equals(material.getLotType())){
									//有barcode代表肯定有批次，无需查询lots，下面代码可以注释
									iqcLine.setIsGenerateLot(true);
								}else{
									lots = adManager.getEntityList(iqc.getOrgRrn(), Lot.class,Integer.MAX_VALUE,"lotId = '"+poLine.getBarCode()+"'",null);
								}
								if(lots!=null && lots.size()>0 ){
									iqcLine.setIsGenerateLot(true);//检验单已经有批次了
								}
							}
						}					
					}
				}
				if(iqc!=null && iqc.getOrgRrn().equals(139420L)){
					//对于bareCode系统在采购订单就生成批次，无需手动去创建,找到批次设置批次已经创建
					Material material = em.find(Material.class, iqcLine.getMaterialRrn());
					PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, iqcLine.getPoLineRrn());
					if(material!=null){
						if(!Lot.LOTTYPE_MATERIAL.equals(material.getLotType())){
							if(poLine !=null && poLine.getBarCode()!=null){
								List<Lot> lots = null;
								if(Lot.LOTTYPE_SERIAL.equals(material.getLotType())){
									//有barcode代表肯定有批次，无需查询lots，下面代码可以注释
									iqcLine.setIsGenerateLot(true);
								}else{
									lots = adManager.getEntityList(iqc.getOrgRrn(), Lot.class,Integer.MAX_VALUE,"lotId = '"+poLine.getBarCode()+"'",null);
								}
								if(lots!=null && lots.size()>0 ){
									iqcLine.setIsGenerateLot(true);//检验单已经有批次了
								}
							}
						}					
					}

					AlarmData alarmData = new AlarmData();
					alarmData.setOrgRrn(iqc.getOrgRrn());
					alarmData.setIsActive(true);
					alarmData.setCreated(new Date());
					alarmData.setCreatedBy(userRrn);
					alarmData.setUpdated(new Date());
					alarmData.setUpdatedBy(userRrn);
					alarmData.setOwner(user.getUserName());
					alarmData.setStartTime(new Date());
					alarmData.setField1(iqc.getReceiptId());//收货单号
					alarmData.setStatus(AlarmData.STATUS_OPEN);//是否关闭
					alarmData.setField2(iqc.getPoId());//采购订单好
					alarmData.setField3(iqc.getDocId());//收货检验编号
					alarmData.setField4(null);
					alarmData.setField5(material.getMaterialId());//物料编号
					alarmData.setField6(material.getName());//物料名称
					ReceiptLine receiptLine = em.find(ReceiptLine.class, iqcLine.getReceiptLineRrn());
					if(receiptLine!=null){
						alarmData.setField8(receiptLine.getQtyReceipt()+"");//收货数量
					}
//					    alarmData.setField8(iqcLine.getReceiptLineQtyReceipt()+"");
					if(vendor!=null){
						alarmData.setField22(vendor.getVendorId());//供应商
						alarmData.setField26(vendor.getCompanyName());//供应商名称
					}
					alarmData.setField23(iqcLine.getQtyIqc()+"");//检验数量
					alarmData.setField24(iqcLine.getQtyQualified()!=null?iqcLine.getQtyQualified().toString():"");
					alarmData.setField9(po.getComments());//采购订单备注
					alarmData.setField10(null);
					alarmData.setField11(iqc.getReceiptRrn());//receiptObjectRrn
					alarmData.setField12(iqc.getPoRrn());//poRrn
					alarmData.setField13(iqc.getDateCreated());//制单时间
					alarmData.setField14(iqc.getObjectRrn());//检验Rrn
					alarmData.setField25(poLine.getUrgency());
					alarmData.setField27(poLine.getQty()+"");
					alarmData.setField31(iqcLine.getObjectRrn());
					alarmData.setAlarmType(AlarmTarget.TARGET_TYPE_WAREHOUSE);
					em.persist(alarmData);
				}
				em.merge(iqcLine);
			}
			return iqc;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteIqc(Iqc iqc, long userRrn) throws ClientException {
		try{
			if(iqc != null && iqc.getObjectRrn() != null) {
				iqc = em.find(Iqc.class, iqc.getObjectRrn());
				for (int i=0;i< iqc.getIqcLines().size();i++){
					IqcLine line= iqc.getIqcLines().get(i);
					deleteIqcLine(line, userRrn);
				}
				if (iqc.getReceiptRrn() != null) {
					Receipt receipt = em.find(Receipt.class, iqc.getReceiptRrn());
					if (receipt != null) {
						receipt.setIsIqc(false);
						em.merge(receipt);
					}
				}
				em.remove(iqc);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Iqc closeIqc(Iqc iqc, long userRrn) throws ClientException {
		try{
			iqc.setDocStatus(Documentation.STATUS_CLOSED);
			iqc.setUpdatedBy(userRrn);
			em.merge(iqc);
			for (IqcLine iqcLine : iqc.getIqcLines()) {
				iqcLine.setLineStatus(Documentation.STATUS_CLOSED);
				iqcLine.setUpdatedBy(userRrn);
				em.merge(iqcLine);
				//撤销时减少检验数
				PurchaseOrderLine poline = em.find(PurchaseOrderLine.class, iqcLine.getPoLineRrn());
				if(poline!=null){
					BigDecimal qtyTested = poline.getQtyTested() ==null ? BigDecimal.ZERO:poline.getQtyTested();
					BigDecimal qtyIqc = iqcLine.getQtyIqc() ==null ? BigDecimal.ZERO:iqcLine.getQtyIqc();
					poline.setQtyTested(qtyTested.subtract(qtyIqc));
					em.merge(poline);
				}
				//撤销时候关闭仓库警报
				if(iqc !=null && iqc.getOrgRrn().equals(139420L)){
					List<AlarmData> alarmDatas = adManager.getEntityList(iqc.getOrgRrn(), AlarmData.class,
							Integer.MAX_VALUE,"field1 = '"+iqc.getReceiptId()+"' and alarmType ='"+AlarmTarget.TARGET_TYPE_WAREHOUSE+
							"' and status = '"+AlarmData.STATUS_OPEN+"' and field3 = '"+iqc.getDocId()+"' and field31="+iqcLine.getObjectRrn()  ,null);
					if(alarmDatas!=null && alarmDatas.size() >0){
						for(AlarmData alarmData :alarmDatas){
							alarmData.setUpdated(new Date());
							alarmData.setUpdatedBy(userRrn);
							alarmData.setCloser(userRrn);
							alarmData.setCloseTime(new Date());
							alarmData.setStatus(AlarmData.STATUS_CLOSE);
							em.merge(alarmData);
						}
					}
				}
			}
			if (iqc.getReceiptRrn() != null) {
				Receipt receipt = em.find(Receipt.class, iqc.getReceiptRrn());
				if (receipt != null) {
//					receipt.setDocStatus(Documentation.STATUS_CLOSED);
					receipt.setIsIqc(false);
					receipt.setUpdatedBy(userRrn);
					em.merge(receipt);
					for (ReceiptLine receiptLine : receipt.getReceiptLines()) {
//						receiptLine.setLineStatus(Documentation.STATUS_CLOSED);
						receiptLine.setIsIqc(false);
						receiptLine.setUpdatedBy(userRrn);
						em.merge(receiptLine);
					}
				}
			}
			return iqc;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteIqcLine(IqcLine iqcLine, long userRrn)throws ClientException {
		try {
			if(iqcLine != null && iqcLine.getObjectRrn() != null) {
				iqcLine = em.find(IqcLine.class, iqcLine.getObjectRrn());
				// 更新receipt
				if(iqcLine.getIqcRrn() != null) {
					Iqc iqc = em.find(Iqc.class, iqcLine.getIqcRrn());					
					iqc.setTotalLines(iqc.getTotalLines() - 1);
					em.merge(iqc);
				}
				// 若对应着采购订单行，则更新采购订单行检验数及合格数
				if(iqcLine.getPoLineRrn() != null) {
					PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, iqcLine.getPoLineRrn());
					BigDecimal oldQualified = poLine.getQtyQualified() == null ? BigDecimal.ZERO : poLine.getQtyQualified();
					BigDecimal oldTest = poLine.getQtyTested() == null ? BigDecimal.ZERO : poLine.getQtyTested();
					poLine.setQtyQualified(oldQualified.subtract(iqcLine.getQtyQualified()));
					poLine.setQtyTested(oldTest.subtract(iqcLine.getQtyIqc()));
					
					em.merge(poLine);
				}
				//若对应着收货行，则将收货行中isIqc置为false以使该收货行可以重新产生检验单
				if(iqcLine.getReceiptLineRrn() != null){
					ReceiptLine receiptLine = em.find(ReceiptLine.class, iqcLine.getReceiptLineRrn());
					receiptLine.setIsIqc(false);
					
					em.merge(receiptLine);
				}
				em.remove(iqcLine);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	} 
	
	public void saveIqcLot(IqcLine line, List<Lot> lots, long userRrn) throws ClientException {
		try{
			Material material = line.getMaterial();
			Iqc iqc = em.find(Iqc.class, line.getIqcRrn());
			
			long transSeq = basManager.getHisSequence();
			if (material.getIsLotControl()) {
				BigDecimal qtyLine = line.getQtyQualified();
				BigDecimal qtyTotal = BigDecimal.ZERO;
				for (Lot lot : lots) {
					qtyTotal = qtyTotal.add(lot.getQtyCurrent());
				}
				if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
					throw new ClientException("inv.iqcline_lot_qty_different");
				}
				for (Lot lot : lots) {
					lot.setUserQc(iqc.getUserApproved());
					lot.setPosition(Lot.POSITION_IQC);
					if (lot.getObjectRrn() == null) {
						String whereClause = " lotId = '" + lot.getLotId() + "'";
						List<Lot> eLots = adManager.getEntityList(line.getOrgRrn(), Lot.class, 2, whereClause, "");
						if (eLots.size() > 0) {
							throw new ClientParameterException("error.object_duplicate", lot.getLotId());
						}
						em.persist(lot);
					} else {
						em.merge(lot);
					}
				}
				
				for (Lot lot : lots) {
					IqcLotHis his = new IqcLotHis(lot);
					his.setHisSeq(transSeq);
					em.persist(his);
				}
				line.setIsGenerateLot(true);
				em.merge(line);
			} else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void saveGenLot(long orgRrn, Material material, List<Lot> lots, long userRrn) throws ClientException {
		try{
			
			if (material.getIsLotControl()) {
				for (Lot lot : lots) {
					lot.setPosition(Lot.POSITION_GEN);
					if (lot.getObjectRrn() == null) {
						String whereClause = " lotId = '" + lot.getLotId() + "'";
						List<Lot> eLots = adManager.getEntityList(orgRrn, Lot.class, 2, whereClause, "");
						if (eLots.size() > 0) {
							throw new ClientParameterException("error.object_duplicate", lot.getLotId());
						}
						em.persist(lot);
					} else {
						em.merge(lot);
					}
				}
				long transSeq = basManager.getHisSequence();
				for (Lot lot : lots) {
					GenerateLotHis his = new GenerateLotHis(lot);
					his.setHisSeq(transSeq);
					em.persist(his);
				}
			} else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementLine newMovementLine(Movement movement) throws ClientException {
		MovementLine movementLine = new MovementLine();
		try{
			if (movement != null && movement.getObjectRrn() != null) {
				movement = em.find(Movement.class, movement.getObjectRrn());
				long maxLineNo = 1;
				for (MovementLine line : movement.getMovementLines()) {
					maxLineNo = maxLineNo < line.getLineNo() ? line.getLineNo() : maxLineNo;
				}
				movementLine.setLineNo((long)Math.ceil(maxLineNo / 10) * 10 + 10);
			} else {
				movementLine.setLineNo(10L);
			}
			movementLine.setLineStatus(Movement.STATUS_DRAFTED);
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return movementLine;
	}
	
	public MovementIn createInFromIqc(MovementIn in, Iqc iqc, List<IqcLine> lines, List<Lot> lots, long userRrn) throws ClientException {
		try{
			List<MovementLine> movementLines = new ArrayList<MovementLine>();
			long startLineNo = 10;
			if (in == null) {//add
				in = new MovementIn();
				in.setOrgRrn(iqc.getOrgRrn());
			} else {//modify
				startLineNo = newMovementLine(in).getLineNo();
			}
			in.setOrgRrn(iqc.getOrgRrn());
			in.setPoRrn(iqc.getPoRrn());
			in.setPoId(iqc.getPoId());
			in.setReceiptRrn(iqc.getReceiptRrn());
			in.setReceiptId(iqc.getReceiptId());
			in.setIqcRrn(iqc.getObjectRrn());
			in.setIqcId(iqc.getDocId());
			in.setPiId(iqc.getPiId());
			in.setInternalOrderId(iqc.getInternalOrderId());
			if(in.getWarehouseRrn() != null) {
				Warehouse wh = em.find(Warehouse.class, in.getWarehouseRrn());
				if(wh != null) {
					in.setWarehouseRrn(wh.getObjectRrn());
					in.setWarehouseId(wh.getWarehouseId());					
				}				
			}
			in.setUserIqc(iqc.getUserApproved());
			
			if (in.getPoRrn() != null) {
				PurchaseOrder po = em.find(PurchaseOrder.class, in.getPoRrn());
				in.setVendorRrn(po.getVendorRrn());
				in.setVendorId(po.getVendorId());
			}
			int i = 0;
			
			for (IqcLine iqcLine : lines) {
				MovementLine movementLine = new MovementLine();
				movementLine.setOrgRrn(iqc.getOrgRrn());
				movementLine.setMaterialRrn(iqcLine.getMaterialRrn());
				
				Material material = em.find(Material.class, iqcLine.getMaterialRrn());
				movementLine.setMaterialId(material.getMaterialId());
				movementLine.setMaterialName(material.getName());
				movementLine.setLotType(material.getLotType());
				movementLine.setUomId(material.getInventoryUom());
				
				movementLine.setLineNo(startLineNo + i * 10);
				movementLine.setIqcLineRrn(iqcLine.getObjectRrn());
				
				if (iqcLine.getPoLineRrn() != null) {
					PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, iqcLine.getPoLineRrn());
					movementLine.setPoLineRrn(poLine.getObjectRrn());
					movementLine.setUnitPrice(poLine.getUnitPrice());
				}
				BigDecimal qtyMovement = BigDecimal.ZERO;
				Date date = new Date();
				List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
//				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
//					movementLine.setMovementLots(lineLots);
//					movementLine.setQtyMovement(iqcLine.getQtyQualified());
//				} else {
//				}
				for (Lot lot : lots) {
					if (iqcLine.getObjectRrn().equals(lot.getIqcLineRrn())) {
						MovementLineLot movementLot = new MovementLineLot();
						movementLot.setOrgRrn(iqc.getOrgRrn());
						movementLot.setIsActive(true);
						movementLot.setCreated(date);
						movementLot.setCreatedBy(userRrn);
						movementLot.setUpdated(date);
						movementLot.setUpdatedBy(userRrn);						
						movementLot.setLotRrn(lot.getObjectRrn());
						movementLot.setLotId(lot.getLotId());
						movementLot.setMaterialRrn(lot.getMaterialRrn());
						movementLot.setMaterialId(lot.getMaterialId());
						movementLot.setMaterialName(lot.getMaterialName());
						movementLot.setQtyMovement(lot.getQtyCurrent());
						lineLots.add(movementLot);
						qtyMovement = qtyMovement.add(lot.getQtyCurrent());
					}
				}
				movementLine.setMovementLots(lineLots);
				movementLine.setQtyMovement(qtyMovement);
				
				movementLines.add(movementLine);
				i++;
				//关闭警报
				if(iqc !=null && iqc.getOrgRrn().equals(139420L)){
					List<AlarmData> alarmDatas = adManager.getEntityList(iqc.getOrgRrn(), AlarmData.class,
							Integer.MAX_VALUE,"field1 = '"+iqc.getReceiptId()+"' and alarmType ='"+AlarmTarget.TARGET_TYPE_WAREHOUSE+
							"' and status = '"+AlarmData.STATUS_OPEN+"' and field3 = '"+iqc.getDocId()+"' and field31="+iqcLine.getObjectRrn()  ,null);
					if(alarmDatas!=null && alarmDatas.size() >0){
						for(AlarmData alarmData :alarmDatas){
							alarmData.setUpdated(new Date());
							alarmData.setUpdatedBy(userRrn);
							alarmData.setCloser(userRrn);
							alarmData.setCloseTime(new Date());
							alarmData.setStatus(AlarmData.STATUS_CLOSE);
							em.merge(alarmData);
						}
					}
				}
			} 
			

			em.merge(iqc);
			if(iqc !=null && iqc.getOrgRrn().equals(139420L)){
				//警报处理,关闭WareHouse警报(根据收货单、检验单号关闭)
				String  receiptId = iqc.getReceiptId();
				if(receiptId!=null){
					List<AlarmData> alarmDatas = adManager.getEntityList(iqc.getOrgRrn(), AlarmData.class,
							Integer.MAX_VALUE,"field1 = '"+receiptId+"' and alarmType ='"+AlarmTarget.TARGET_TYPE_WAREHOUSE+
							"' and status = '"+AlarmData.STATUS_OPEN+"' and field3 = '"+iqc.getDocId()+"' and field31 is null" ,null);
					if(alarmDatas!=null && alarmDatas.size() >0){
						for(AlarmData alarmData :alarmDatas){
							alarmData.setUpdated(new Date());
							alarmData.setUpdatedBy(userRrn);
							alarmData.setCloser(userRrn);
							alarmData.setCloseTime(new Date());
							alarmData.setStatus(AlarmData.STATUS_CLOSE);
							em.merge(alarmData);
						}
					}
				}
			}
			return saveMovementInLine(in, movementLines, MovementIn.InType.PIN, userRrn);
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementIn createInFromPo(MovementIn in, PurchaseOrder po, List<PurchaseOrderLine> lines, List<Lot> lots, long userRrn) throws ClientException {
		try{
			List<MovementLine> movementLines = new ArrayList<MovementLine>();
			long startLineNo = 10;
			if (in == null) {//add
				in = new MovementIn();
				in.setOrgRrn(po.getOrgRrn());
			} else {//modify
				startLineNo = newMovementLine(in).getLineNo();
			}
			po = em.find(PurchaseOrder.class, po.getObjectRrn());
			in.setOrgRrn(po.getOrgRrn());
			in.setPoRrn(po.getObjectRrn());
			in.setPoId(po.getDocId());
			in.setVendorRrn(po.getVendorRrn());
			in.setVendorId(po.getVendorId());
			in.setPiId(po.getPiId());
			in.setInternalOrderId(po.getInternalOrderId());
			int i = 0;
			for (PurchaseOrderLine poLine : lines) {
				if (i == 0) {
					in.setWarehouseRrn(poLine.getWarehouseRrn());
					in.setWarehouseId(poLine.getWarehouseId());
				}
				MovementLine movementLine = new MovementLine();
				movementLine.setOrgRrn(po.getOrgRrn());
				movementLine.setMaterialRrn(poLine.getMaterialRrn());
				
				Material material = em.find(Material.class, poLine.getMaterialRrn());
				movementLine.setMaterialId(material.getMaterialId());
				movementLine.setMaterialName(material.getName());
				movementLine.setLotType(material.getLotType());
				movementLine.setUomId(material.getInventoryUom());
				
				movementLine.setLineNo(startLineNo + i * 10);
				movementLine.setPoLineRrn(poLine.getObjectRrn());
				movementLine.setUnitPrice(poLine.getUnitPrice());
				
				BigDecimal qtyMovement = BigDecimal.ZERO;
				Date date = new Date();
				List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					movementLine.setMovementLots(lineLots);
					movementLine.setQtyMovement(poLine.getQty());
				} else {
					for (Lot lot : lots) {
						if (poLine.getObjectRrn().equals(lot.getPoLineRrn())) {
							MovementLineLot inLineLot = new MovementLineLot();
							inLineLot.setOrgRrn(po.getOrgRrn());
							inLineLot.setIsActive(true);
							inLineLot.setCreated(date);
							inLineLot.setCreatedBy(userRrn);
							inLineLot.setUpdated(date);
							inLineLot.setUpdatedBy(userRrn);
							inLineLot.setLotRrn(lot.getObjectRrn());
							inLineLot.setLotId(lot.getLotId());
							inLineLot.setMaterialRrn(lot.getMaterialRrn());
							inLineLot.setMaterialId(lot.getMaterialId());
							inLineLot.setMaterialName(lot.getMaterialName());
							inLineLot.setQtyMovement(lot.getQtyCurrent());
							lineLots.add(inLineLot);
							qtyMovement = qtyMovement.add(lot.getQtyCurrent());
						}
					}
					movementLine.setMovementLots(lineLots);
					movementLine.setQtyMovement(qtyMovement);
				}
				movementLines.add(movementLine);
				i++;
			} 
		
			return saveMovementInLine(in, movementLines, MovementIn.InType.PIN, userRrn);
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementIn saveMovementInLine(MovementIn in, List<MovementLine> lines, MovementIn.InType inType, long userRrn) throws ClientException {
		try {
			if (in.getObjectRrn() == null) {
				in.setIsActive(true);
				in.setCreatedBy(userRrn);
				in.setCreated(new Date());
				in.setDateCreated(new Date());
				in.setTotalLines(0L);
				switch (inType) {
					case PIN:
						in.setDocType(MovementIn.DOCTYPE_PIN);
						if(in.getOrgRrn().equals(139420L) && in.getReceiptRrn()!=null){
							Receipt receipt = em.find(Receipt.class, in.getReceiptRrn());
							if( receipt.getWmsWarehouse()!=null && !"".equals(receipt.getWmsWarehouse())){
								in.setWmsWarehouse(receipt.getWmsWarehouse());
							}
						}
						break;
					case WIN:
						in.setDocType(MovementIn.DOCTYPE_WIN);
						break;
					case OIN:
						in.setDocType(MovementIn.DOCTYPE_OIN);
						break;
					case RIN:
						in.setDocType(MovementIn.DOCTYPE_RIN);
						break;
					case ADIN:
						in.setDocType(MovementIn.DOCTYPE_ADIN);
						break;
				}
				in.setDocStatus(MovementIn.STATUS_DRAFTED);
				
				String docId = in.getDocId();
				if (docId == null || docId.length() == 0) {
					in.setDocId(generateInCode(in));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<Movement> movements = adManager.getEntityList(in.getOrgRrn(), Movement.class, 2, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				ADUser user = em.find(ADUser.class, userRrn);
				in.setUserCreated(user.getUserName());
				em.persist(in);
			}
			if (in.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Warehouse house = em.find(Warehouse.class, in.getWarehouseRrn());
			in.setWarehouseId(house.getWarehouseId());
			
			
			
			List<MovementLine> savaLine = new ArrayList<MovementLine>();

			for (MovementLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				StringBuffer hql = new StringBuffer();
				hql.append(" FROM MaterialLocator l WHERE l.materialRrn = ? AND l.warehouseRrn = ? ");
				Query query = em.createQuery(hql.toString());
				query.setParameter(1, line.getMaterialRrn());
				query.setParameter(2, in.getWarehouseRrn());
				List<MaterialLocator> mLocators = (List<MaterialLocator>) query.getResultList();
				if (mLocators.size() > 0) {
					MaterialLocator mLocator = mLocators.get(0);
					line.setLocatorRrn(mLocator.getLocatorRrn());
					line.setLocatorId(mLocator.getLocatorId());
					in.setLocatorRrn(mLocator.getLocatorRrn());
				}
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (MovementIn.IN_TYPE_AMOUNT_ADJUST.equals(in.getInType())) {
					line.setQtyMovement(BigDecimal.ZERO);
				} else if (line.getQtyMovement().doubleValue() == 0) {
					continue;
				}
				
				Locator locator = null;
				if (line.getLocatorRrn() != null) {
					locator = em.find(Locator.class, line.getLocatorRrn());
					line.setLocatorId(locator.getLocatorId());
				}
				if (line.getUnitPrice() != null) {
					if (!MovementIn.IN_TYPE_AMOUNT_ADJUST.equals(in.getInType())) {
						line.setLineTotal(line.getQtyMovement().multiply(line.getUnitPrice()));
					} else {
						line.setLineTotal(line.getUnitPrice());
					}
				}
				
				//如果是Update，则将原记录删除
				if (line.getObjectRrn() != null) {
					MovementLine oldLine = new MovementLine();
					oldLine.setObjectRrn(line.getObjectRrn());
					oldLine.setMovementRrn(line.getMovementRrn());
					
					em.merge(in);
					deleteMovementInLine(oldLine, inType, userRrn);
					em.flush();
					in = (MovementIn)em.find(MovementIn.class, in.getObjectRrn());
					in.setMovementLines(null);
					
					line.setObjectRrn(null);
				}
				//修改相关的PR, MO等数量
				if (MovementIn.InType.PIN == inType) {
					if (line.getPoLineRrn() != null) {
						PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, line.getPoLineRrn());
						BigDecimal qtyIn = (poLine.getQtyIn() == null ? BigDecimal.ZERO : poLine.getQtyIn()).add(line.getQtyMovement());
						if (qtyIn.compareTo(poLine.getQty()) > 0) {
							throw new ClientException("inv.in_larger_than_order");
						} 
					} 
					if (line.getIqcLineRrn() != null) {
						IqcLine iqcLine = em.find(IqcLine.class, line.getIqcLineRrn());
						iqcLine.setQtyIn((iqcLine.getQtyIn() == null ? BigDecimal.ZERO : iqcLine.getQtyIn()).add(line.getQtyMovement()));
						if (iqcLine.getQtyIn().compareTo(iqcLine.getQtyQualified()) > 0) {
							throw new ClientException("inv.in_larger_than_qualify");
						} 
						iqcLine.setUpdatedBy(userRrn);
						em.merge(iqcLine);
						if (iqcLine.getQtyIn().compareTo(iqcLine.getQtyQualified()) == 0) {
							boolean completeFlag = true;
							Iqc iqc = em.find(Iqc.class, iqcLine.getIqcRrn());
							for (IqcLine qcLine : iqc.getIqcLines()){
								if (!(qcLine.getQtyIn().compareTo(qcLine.getQtyQualified()) == 0)) {
									completeFlag = false;
									break;
								}
							}
							if (completeFlag) {
								iqc.setIsIn(true);
								em.merge(iqc);
							}
						}
					}
				}
//				else if (MovementIn.InType.WIN == inType) {
//					ManufactureOrder mo = em.find(ManufactureOrder.class, in.getMoRrn());
//					BigDecimal qtyIn = (mo.getQtyIn() == null ? BigDecimal.ZERO : mo.getQtyIn()).add(line.getQtyMovement());
//					if (qtyIn.compareTo(mo.getQtyReceive()) > 0) {
//						throw new ClientException("inv.in_larger_than_receive");
//					} 
//				}
				
				List<MovementLineLot> addLineLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
				if (line.getObjectRrn() != null) {
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(in.getObjectRrn());
					line.setMovementId(in.getDocId());
					in.setTotalLines(in.getTotalLines() + 1);
					em.persist(line);
					
					if (!Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
						List<Lot> lots = line.getLots();
						if(lots != null && lots.size() > 0) {
							for(Lot lot : lots) {
								if (lot.getObjectRrn() == null) {
									em.persist(lot);
								} else {
									em.merge(lot);
								}
								MovementLineLot lineLot = new MovementLineLot();
								lineLot.setOrgRrn(lot.getOrgRrn());
								lineLot.setIsActive(true);
								lineLot.setCreated(new Date());
								lineLot.setCreatedBy(userRrn);
								lineLot.setUpdated(new Date());
								lineLot.setUpdatedBy(userRrn);						
								lineLot.setLotRrn(lot.getObjectRrn());
								lineLot.setLotId(lot.getLotId());
								lineLot.setMaterialRrn(lot.getMaterialRrn());
								lineLot.setMaterialId(lot.getMaterialId());
								lineLot.setMaterialName(lot.getMaterialName());
								lineLot.setQtyMovement(lot.getQtyCurrent());
								addLineLots.add(lineLot);
							}
						}
					}
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					for (MovementLineLot addLineLot : addLineLots) {
						if(addLineLot.getObjectRrn() == null) {
							addLineLot.setMovementRrn(in.getObjectRrn());
							addLineLot.setMovementId(in.getDocId());
							addLineLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(addLineLot);
						} else {
							addLineLot.setMovementRrn(in.getObjectRrn());
							addLineLot.setMovementId(in.getDocId());
							addLineLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(addLineLot);
						}
					}
				} else {
					for (MovementLineLot addLineLot : addLineLots) {
						if (addLineLot.getLotRrn() == null) {
							List<Lot> lots = line.getLots();
							for (Lot lot : lots) {
								if (lot.getLotId().equals(addLineLot.getLotId())) {
									em.persist(lot);
									addLineLot.setLotRrn(lot.getObjectRrn());
									break;
								}
							}
						}
						Lot addLot = em.find(Lot.class, addLineLot.getLotRrn());
						//检查Lot位置
							switch (inType) {
							case PIN:
								if(addLot.getReverseField10()==null){//其它类型field10不为空，代表有barcode跳出判断，因为一个批次会存在多次收货的情况
									if (!Lot.POSITION_IQC.equals(addLot.getPosition())) {
										throw new ClientParameterException("inv.lot_already_in", addLot.getLotId());
									};
								}
								break;
							case WIN:
								if (addLot.getIsUsed()) {
									throw new ClientParameterException("wip.lot_is_used", addLot.getLotId());
								};
								ManufactureOrder mo = in.getMo();
								if(mo!=null){
									addLot.setReverseField9(mo.getCustomerName());//WMS需要知道外发和内发，以免发错货
								}
//								ManufactureOrder mo = em.find(ManufactureOrder.class, in.getMoRrn());
//								if (mo.getWorkCenterRrn() != null) {
//									WorkCenter cw = em.find(WorkCenter.class, mo.getWorkCenterRrn());
//									LotStorage lotStorage = this.getLotStorage(addLot.getOrgRrn(), addLot.getObjectRrn(), cw.getWarehouseRrn(), userRrn);
//									BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(addLineLot.getQtyMovement());
//									if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
//										throw new ClientException("inv.not_sufficient_quantity");
//									}
//								}
								Warehouse writeOffWarehouse = getWriteOffWarehouse(in.getOrgRrn());
								LotStorage lotStorage = this.getLotStorage(addLot.getOrgRrn(), addLot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), userRrn);
								BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(addLineLot.getQtyMovement());
								if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
									throw new ClientException("inv.not_sufficient_quantity");
								}

								break;
							case OIN:
								if (!(Lot.POSITION_IQC.equals(addLot.getPosition()) 
										|| Lot.POSITION_GEN.equals(addLot.getPosition())
										|| Lot.POSITION_WIP.equals(addLot.getPosition()))) {
									throw new ClientParameterException("inv.lot_already_in", addLot.getLotId());
								};
								if (Lot.POSITION_WIP.equals(addLot.getPosition()) && addLot.getIsUsed()) {
									throw new ClientParameterException("wip.lot_is_used", addLot.getLotId());
								};
								break;
							case RIN:
								if (Lot.LOTTYPE_SERIAL.equals(addLot.getLotType())) {
									if (!Lot.POSITION_OUT.equals(addLot.getPosition())) {
										throw new ClientParameterException("wip.lot_is_used", addLot.getLotId());
									}
								}
								break;
							}
							
							//设置Lot新位置
							switch (inType) {
								case PIN:
									addLot.setPosition(Lot.POSITION_PIN);
									break;
								case WIN:
//									addLot.setPosition(Lot.POSITION_WIN);
									break;
								case OIN:
									addLot.setPosition(Lot.POSITION_OIN);
									break;
								case RIN:
									break;
							}
//						}
							addLot.setInId(in.getDocId());
							addLot.setInRrn(in.getObjectRrn());
							addLot.setInLineRrn(line.getObjectRrn());					
							em.merge(addLot);
						
						if(addLineLot.getObjectRrn() == null) {
							addLineLot.setMovementRrn(in.getObjectRrn());
							addLineLot.setMovementId(in.getDocId());
							addLineLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(addLineLot);
						} else {
							addLineLot.setMovementRrn(in.getObjectRrn());
							addLineLot.setMovementId(in.getDocId());
							addLineLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(addLineLot);
						}
					}
				}
				
				/*
				//修改相关的PR, MO等数量
				BigDecimal qtyMovement = line.getQtyMovement();
				if (line.getObjectRrn() != null) {
					MovementLine oldLine = em.find(MovementLine.class, line.getObjectRrn());
					qtyMovement = line.getQtyMovement().subtract(oldLine.getQtyMovement());
				}
				if (MovementIn.InType.PIN == inType) {
					if (line.getPoLineRrn() != null) {
						PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, line.getPoLineRrn());
						poLine.setQtyIn((poLine.getQtyIn() == null ? BigDecimal.ZERO : poLine.getQtyIn()).add(qtyMovement));
						if (poLine.getQtyIn().compareTo(poLine.getQty()) > 0) {
							throw new ClientException("inv.in_larger_than_order");
						} 
						poLine.setUpdatedBy(userRrn);
						em.merge(poLine);
					} 
					if (line.getIqcLineRrn() != null) {
						IqcLine iqcLine = em.find(IqcLine.class, line.getIqcLineRrn());
						iqcLine.setQtyIn((iqcLine.getQtyIn() == null ? BigDecimal.ZERO : iqcLine.getQtyIn()).add(qtyMovement));
						iqcLine.setUpdatedBy(userRrn);
						em.merge(iqcLine);
					}
				} else if (MovementIn.InType.WIN == inType) {
					ManufactureOrder mo = em.find(ManufactureOrder.class, in.getMoRrn());
					mo.setQtyIn(mo.getQtyIn().add(qtyMovement));
					if (mo.getQtyIn().compareTo(mo.getQtyReceive()) > 0) {
						throw new ClientException("inv.in_larger_than_receive");
					} 
					mo.setUpdatedBy(userRrn);
					em.merge(mo);
				}
				
				//对应SERIAL和BATCH类型，采购入库、生产、其它都必须是整批，退货是老批号
				List<MovementLineLot> addLineLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
				if (line.getObjectRrn() != null) {
					StringBuffer sql = new StringBuffer("");
					sql.append(" SELECT MovementLineLot FROM MovementLineLot MovementLineLot ");
					sql.append(" WHERE MovementLineLot.movementLineRrn = '" + line.getObjectRrn() + "' ");
					Query query = em.createQuery(sql.toString());
					List<MovementLineLot> oldLineLots = (List<MovementLineLot>)query.getResultList();
					for (MovementLineLot oldLineLot : oldLineLots) {
						if (!Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
							Lot removeLot = em.find(Lot.class, oldLineLot.getLotRrn());
							switch (inType) {
								case PIN:
									removeLot.setPosition(Lot.POSITION_IQC);
									break;
								case WIN:
									removeLot.setPosition(Lot.POSITION_WIP);
									break;
								case OIN:
									if (removeLot.getIqcLineRrn() != null) {
										removeLot.setPosition(Lot.POSITION_IQC);
									} else if (removeLot.getMoRrn() != null) {
										removeLot.setPosition(Lot.POSITION_WIP);
									} else {
										removeLot.setPosition(Lot.POSITION_GEN);
									}
									break;
								case RIN:
									break;
							}
							removeLot.setInId(null);
							removeLot.setInRrn(null);
							removeLot.setInLineRrn(null);
							em.merge(removeLot);
						}

						em.remove(oldLineLot);
						
					}
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(in.getObjectRrn());
					line.setMovementId(in.getDocId());
					in.setTotalLines(in.getTotalLines() + 1);
					em.persist(line);
					
					if (!Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
						List<Lot> lots = line.getLots();
						if(lots != null && lots.size() > 0) {
							addLineLots = new ArrayList<MovementLineLot>(); 
							for(Lot lot : lots) {
								if (lot.getObjectRrn() == null) {
									em.persist(lot);
								} else {
									em.merge(lot);
								}
								MovementLineLot lineLot = new MovementLineLot();
								lineLot.setOrgRrn(lot.getOrgRrn());
								lineLot.setIsActive(true);
								lineLot.setCreated(new Date());
								lineLot.setCreatedBy(userRrn);
								lineLot.setUpdated(new Date());
								lineLot.setUpdatedBy(userRrn);						
								lineLot.setLotRrn(lot.getObjectRrn());
								lineLot.setLotId(lot.getLotId());
								lineLot.setMaterialRrn(lot.getMaterialRrn());
								lineLot.setMaterialId(lot.getMaterialId());
								lineLot.setMaterialName(lot.getMaterialName());
								lineLot.setQtyMovement(lot.getQtyCurrent());
								addLineLots.add(lineLot);
							}
						}
					}
				}
				if (!Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					for (MovementLineLot addLineLot : addLineLots) {
						if (addLineLot.getLotRrn() == null) {
							List<Lot> lots = line.getLots();
							for (Lot lot : lots) {
								if (lot.getLotId().equals(addLineLot.getLotId())) {
									em.persist(lot);
									addLineLot.setLotRrn(lot.getObjectRrn());
									break;
								}
							}
						}
						Lot addLot = em.find(Lot.class, addLineLot.getLotRrn());
						
						//检查Lot位置
						switch (inType) {
						case PIN:
							if (!Lot.POSITION_IQC.equals(addLot.getPosition())) {
								throw new ClientParameterException("inv.lot_already_in", addLot.getLotId());
							};
							break;
						case WIN:
							if (!Lot.POSITION_WIP.equals(addLot.getPosition())) {
								throw new ClientParameterException("wip.lot_not_in_wip", addLot.getLotId());
							}
							if (addLot.getIsUsed()) {
								throw new ClientParameterException("wip.lot_is_used", addLot.getLotId());
							};
							break;
						case OIN:
							if (!(Lot.POSITION_IQC.equals(addLot.getPosition()) 
									|| Lot.POSITION_GEN.equals(addLot.getPosition())
									|| Lot.POSITION_WIP.equals(addLot.getPosition()))) {
								throw new ClientParameterException("inv.lot_already_in", addLot.getLotId());
							};
							if (Lot.POSITION_WIP.equals(addLot.getPosition()) && addLot.getIsUsed()) {
								throw new ClientParameterException("wip.lot_is_used", addLot.getLotId());
							};
							break;
						case RIN:
							if (Lot.LOTTYPE_SERIAL.equals(addLot.getLotType())) {
								if (!Lot.POSITION_OUT.equals(addLot.getPosition())) {
									throw new ClientParameterException("wip.lot_is_used", addLot.getLotId());
								}
							}
							break;
						}
						//设置Lot新位置
						switch (inType) {
							case PIN:
								addLot.setPosition(Lot.POSITION_PIN);
								break;
							case WIN:
								addLot.setPosition(Lot.POSITION_WIN);
								break;
							case OIN:
								addLot.setPosition(Lot.POSITION_OIN);
								break;
							case RIN:
								break;
						}
						
						addLot.setInId(in.getDocId());
						addLot.setInRrn(in.getObjectRrn());
						addLot.setInLineRrn(line.getObjectRrn());					
						em.merge(addLot);
						
						if(addLineLot.getObjectRrn() == null) {
							addLineLot.setMovementRrn(in.getObjectRrn());
							addLineLot.setMovementId(in.getDocId());
							addLineLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(addLineLot);
						} else {
							em.merge(addLineLot);
						}
					}
				} else {
					for (MovementLineLot addLineLot : addLineLots) {
						if(addLineLot.getObjectRrn() == null) {
							addLineLot.setMovementRrn(in.getObjectRrn());
							addLineLot.setMovementId(in.getDocId());
							addLineLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(addLineLot);
						} else {
							em.merge(addLineLot);
						}
					}
				}*/
				
				line.setUpdatedBy(userRrn);				
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			in.setUpdatedBy(userRrn);
			in.setMovementLines(null);

			em.merge(in);
			in.setMovementLines(savaLine);
			return in;
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementIn approvePinMovementIn(MovementIn in, boolean writeOffFlag, long userRrn) throws ClientException {
		return approveMovementIn(in, MovementIn.InType.PIN, writeOffFlag, userRrn);
	}
		
	public MovementIn approveMovementIn(MovementIn in, MovementIn.InType inType, long userRrn) throws ClientException  {
		return approveMovementIn(in, inType, false, false, userRrn);
	}
	
	@Override
	public MovementIn approveMovementIn(MovementIn in, InType inType,
			long userRrn, boolean isWriteOff) throws ClientException {
		return approveMovementIn(in, inType, false, false, userRrn, isWriteOff);
	}
	
	public MovementIn approveMovementIn(MovementIn in, MovementIn.InType inType, boolean seniorApprove,long userRrn) throws ClientException  {
		return approveMovementIn(in, inType, false, seniorApprove, userRrn);
	}
	@Override
	public MovementIn approveMovementIn(MovementIn in, InType inType,
			boolean writeOffFlag, boolean seniorApprove, long userRrn)
			throws ClientException {
		return approveMovementIn(in, inType, writeOffFlag, seniorApprove, userRrn, true);
	}
	
	public MovementIn approveMovementIn(MovementIn in,InType inType, long userRrn, PurchaseOrder po ) throws ClientException {
		try{
			in = approveMovementIn(in,inType, userRrn);
			if(po!=null && po.getObjectRrn() != null){
				po = (PurchaseOrder) adManager.getEntity(po);					
				List<PurchaseOrderLine> poLines = po.getPoLines();
				List<MovementLine> movementLines = in.getMovementLines();
				boolean completeFlag = true;
				for(PurchaseOrderLine poline : poLines){
					for(MovementLine line:movementLines){
						if(poline.getMaterialRrn() != null && line.getMaterialRrn() != null 
								&& poline.getMaterialRrn().longValue() == line.getMaterialRrn()){
							if(poline.getQtyIn() == null){
								poline.setQtyIn(BigDecimal.ZERO);
							}
							poline.setQtyIn(poline.getQtyIn().add(line.getQtyMovement()));
							BigDecimal qty1 = poline.getQtyIn();
							BigDecimal qty2 = poline.getQty();
							BigDecimal qtyLoss = poline.getQtyLoss();
							if(qty1.add(qtyLoss).compareTo(qty2) >= 0){
								poline.setLineStatus("COMPLETED");
							}
							em.merge(poline);
							break;
						}
					}
					if (!PurchaseOrder.STATUS_COMPLETED.equals(poline.getLineStatus()) && 
							!PurchaseOrder.STATUS_CLOSED.equals(poline.getLineStatus())) {
						completeFlag = false;
					}
				}
				
				if (completeFlag) {
					po.setDocStatus(PurchaseOrder.STATUS_COMPLETED);
					em.merge(po);
				}
			}
			return in;
		}catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementIn approveMovementIn(MovementIn in, MovementIn.InType inType, boolean writeOffFlag, boolean seniorApprove, long userRrn, boolean isWriteOff) throws ClientException {
		try{
			Date now = new Date(); 
			if (in.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Long warehouseRrn = in.getWarehouseRrn();
			ADUser user = em.find(ADUser.class, userRrn);
			in.setUserApproved(user.getUserName());
			Warehouse house = em.find(Warehouse.class, warehouseRrn);
			in.setWarehouseId(house.getWarehouseId());
			if (writeOffFlag) {
				in.setDateWriteOff(now);
				in.setDocStatus(PurchaseOrder.STATUS_COMPLETED);
			} else {
				in.setDocStatus(PurchaseOrder.STATUS_APPROVED);
			}
			in.setUpdatedBy(userRrn);
			in.setDateApproved(now);
			
			em.merge(in);
			in = em.getReference(in.getClass(), in.getObjectRrn());
			
			if (in.getMovementLines().size() == 0) {
				throw new ClientException("inv.in_quantity_zero"); 
			}
			
			Date dateIn = new Date();
			long transSeq = basManager.getHisSequence();
			
			List<MovementLine> inLines = new ArrayList<MovementLine>();
			BigDecimal accessLineTotal=new BigDecimal(0);
			BigDecimal invoiceLineTotal=new BigDecimal(0);
			for (MovementLine inLine : in.getMovementLines()) {
				Material material = em.find(Material.class, inLine.getMaterialRrn());
				
				Locator locator = null;
				if (inLine.getLocatorRrn() != null) {
					locator = em.find(Locator.class, inLine.getLocatorRrn());
					inLine.setLocatorId(locator.getLocatorId());
				}
				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(inLine.getLineStatus())) {
					inLine.setLineStatus(DocumentationLine.LINESTATUS_APPROVED);
					inLine.setUpdatedBy(userRrn);
					em.merge(inLine);
				}
				
				if (MovementIn.InType.PIN == inType) {
					if (inLine.getPoLineRrn() != null) {
						PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, inLine.getPoLineRrn());
						PurchaseOrder po = em.find(PurchaseOrder.class, poLine.getPoRrn());
						poLine.setQtyIn((poLine.getQtyIn() == null ? BigDecimal.ZERO : poLine.getQtyIn()).add(inLine.getQtyMovement()));
						if (poLine.getQtyIn().compareTo(poLine.getQty()) > 0) {
							throw new ClientException("inv.in_larger_than_order");
						} 
						poLine.setUpdatedBy(userRrn);
						em.merge(poLine);
						if (poLine.getQtyIn().compareTo(poLine.getQty()) == 0) {
							poLine.setLineStatus(PurchaseOrder.STATUS_COMPLETED);
							em.merge(poLine);
							if (PurchaseOrder.STATUS_COMPLETED.equals(poLine.getLineStatus())) {
								boolean completeFlag = true;
								boolean closeFlag = false;
								for (PurchaseOrderLine line : po.getPoLines()){
									if (!PurchaseOrder.STATUS_COMPLETED.equals(line.getLineStatus()) && 
											!PurchaseOrder.STATUS_CLOSED.equals(line.getLineStatus())) {
										completeFlag = false;
										closeFlag = false;
										break;
									}
									if (PurchaseOrder.STATUS_CLOSED.equals(line.getLineStatus())) {
										closeFlag = true;
									}
								}
								if (closeFlag) {
									po.setDocStatus(PurchaseOrder.STATUS_CLOSED);
									em.merge(po);
								} else if (completeFlag) {
									po.setDocStatus(PurchaseOrder.STATUS_COMPLETED);
									em.merge(po);
								}
							}
						} 
						
						//更新暂估金额
						if (PurchaseOrder.INVOICE_TYPE_VAT.equals(po.getInvoiceType())) {
							BigDecimal vatRate = BigDecimal.ZERO;
							if (po.getVatRate() != null) {
								vatRate = po.getVatRate();
							}
							inLine.setVatRate(po.getVatRate());
							inLine.setAssessLineTotal(inLine.getQtyMovement().multiply(poLine.getUnitPrice())
									.divide(vatRate.add(BigDecimal.ONE), 2, RoundingMode.HALF_UP/*向最接近数字方向舍入的舍入模式，如果与两个相邻数字的距离相等，则向上舍入*/));
							accessLineTotal = accessLineTotal.add(inLine.getAssessLineTotal()==null?BigDecimal.ZERO:inLine.getAssessLineTotal());
							invoiceLineTotal = invoiceLineTotal.add(inLine.getInvoiceLineTotal()==null?BigDecimal.ZERO:inLine.getInvoiceLineTotal());
						} else {
							inLine.setVatRate(null);
							inLine.setAssessLineTotal(inLine.getQtyMovement().multiply(poLine.getUnitPrice()));
						}
						em.merge(inLine);
					}
					
					if (inLine.getIqcLineRrn() != null) {
						IqcLine iqcLine = em.find(IqcLine.class, inLine.getIqcLineRrn());
//						iqcLine.setQtyIn((iqcLine.getQtyIn() == null ? BigDecimal.ZERO : iqcLine.getQtyIn()).add(inLine.getQtyMovement()));
						iqcLine.setUpdatedBy(userRrn);
						em.merge(iqcLine);
						if (iqcLine.getQtyIn().compareTo(iqcLine.getQtyQualified()) == 0) {
							iqcLine.setLineStatus(Iqc.STATUS_COMPLETED);
							em.merge(iqcLine);
							if (Iqc.STATUS_COMPLETED.equals(iqcLine.getLineStatus())) {
								Iqc iqc = em.find(Iqc.class, iqcLine.getIqcRrn());
								boolean completeFlag = true;
								for (IqcLine line : iqc.getIqcLines()){
									if (!Iqc.STATUS_COMPLETED.equals(line.getLineStatus())) {
										completeFlag = false;
										break;
									}
								}
								if (completeFlag) {
									iqc.setDocStatus(Iqc.STATUS_COMPLETED);
									em.merge(iqc);
								}
							}
							if (iqcLine.getReceiptLineRrn() != null) {
								ReceiptLine receiptLine = em.find(ReceiptLine.class, iqcLine.getReceiptLineRrn());
								receiptLine.setLineStatus(Receipt.STATUS_COMPLETED);
								em.merge(receiptLine);
								if (Receipt.STATUS_COMPLETED.equals(receiptLine.getLineStatus())) {
									Receipt receipt = em.find(Receipt.class, receiptLine.getReceiptRrn());
									boolean completeFlag = true;
									for (ReceiptLine line : receipt.getReceiptLines()){
										if (!Iqc.STATUS_COMPLETED.equals(line.getLineStatus())) {
											completeFlag = false;
											break;
										}
									}
									if (completeFlag) {
										receipt.setDocStatus(Receipt.STATUS_COMPLETED);
										em.merge(receipt);
									}
								}
							}
						}
					}
				} else if (MovementIn.InType.WIN == inType) {
					ManufactureOrder mo = em.find(ManufactureOrder.class, in.getMoRrn());
					if(mo.getDocStatus().equalsIgnoreCase(Movement.STATUS_CLOSED)){
						throw new ClientException("inv.mo_has_closed");
					}
					
					
					mo.setQtyIn(mo.getQtyIn().add(inLine.getQtyMovement()));
//					if (mo.getQtyIn().compareTo(mo.getQtyReceive()) > 0) {
//						throw new ClientException("inv.in_larger_than_receive");
//					} 
					if (mo.getQtyIn().compareTo(mo.getQtyProduct()) > 0) {
						throw new ClientException("inv.in_larger_than_product");
					}
					mo.setUpdatedBy(userRrn);
					em.merge(mo);
					if (ManufactureOrder.STATUS_APPROVED.equals(mo.getDocStatus()) 
							&& mo.getQtyProduct().equals(mo.getQtyIn())) {
						mo.setDocStatus(ManufactureOrder.STATUS_COMPLETED);
						em.merge(mo);
					}else if(ManufactureOrder.STATUS_COMPLETED.equals(mo.getDocStatus())
							 && mo.getQtyProduct().compareTo(mo.getQtyIn()) > 0 ){//如果入库数量小于生产数量将工作令状态变回approved
						mo.setDocStatus(ManufactureOrder.STATUS_APPROVED);
						em.merge(mo);
					}
					
					//核销物料
					Warehouse writeOffWarehouse = getWriteOffWarehouse(mo.getOrgRrn());
					List<Material> inSufficientMaterial = new ArrayList<Material>();
					//得到核销的物料，最末一级的物料，原料并且是批次管控的物料 减少核销物料,核销核销仓库的物料
					List<LotConsume> lotComsumes = this.getMaterialBomConsume(mo.getObjectRrn(), "");
					for (LotConsume lotConsume : lotComsumes) {
						lotConsume.setOrgRrn(mo.getOrgRrn());
						lotConsume.setIsActive(true);
						lotConsume.setCreatedBy(userRrn);
						lotConsume.setUpdatedBy(userRrn);
						lotConsume.setCreated(new Date());
						lotConsume.setMoRrn(mo.getObjectRrn());
						lotConsume.setMoId(mo.getDocId());
						lotConsume.setInRrn(in.getObjectRrn());
						lotConsume.setInId(in.getDocId());
						lotConsume.setQtyProduct(inLine.getQtyMovement());
						lotConsume.setQtyConsume(inLine.getQtyMovement().multiply(lotConsume.getUnitConsume()));
						lotConsume.setIsWin(false);
						lotConsume.setDateIn(now);
						lotConsume.setWarehouseRrn(writeOffWarehouse.getObjectRrn());
						lotConsume.setWarehouseId(writeOffWarehouse.getWarehouseId());
						lotConsume.setIsManual(false);
						em.merge(lotConsume);
							
						Storage storage = getMaterialStorage(mo.getOrgRrn(), lotConsume.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), userRrn);
						BigDecimal oldQtyWriteOff = storage.getQtyWriteOff();
						BigDecimal newQtyWriteOff = oldQtyWriteOff.add(lotConsume.getQtyConsume().negate());
						if(newQtyWriteOff.compareTo(BigDecimal.ZERO) < 0){//如果核销后数量为负,将这个物料放入inSufficientMaterial中去
							Material m = em.find(Material.class, storage.getMaterialRrn());
							inSufficientMaterial.add(m);
						}
						storage.setQtyWriteOff(oldQtyWriteOff.add(lotConsume.getQtyConsume().negate()));
						storage.setUpdatedBy(userRrn);
						em.merge(storage);
					}
					
					if(!seniorApprove && inSufficientMaterial.size() > 0){//如果不是高级审核且有物料核销数量不足 抛出异常信息
//						throw new ClientException("inv.insufficient_writeoff_qty");
						StringBuffer ids = new StringBuffer();
						int i = 0;
						for(Material m : inSufficientMaterial){
							if(i++ > 0){
								ids.append(",");
							}
							ids.append(m.getMaterialId());
						}
						throw new ClientParameterException("inv.insufficient_writeoff_qty", writeOffWarehouse.getWarehouseId(), ids.toString());
					}
				}
				
				//更新库存
				if(warehouseRrn.equals(42803113L)){
					//滤芯区域的生产入库，直接入到开能环保良品库存 如果是生产入库isWriteOff为true warehousRrn为生产入库的长裤
					Material ltMaterial = em.find(Material.class, inLine.getMaterialRrn()) ;
					Long wtOrgRrn =139420L;
					List<Material> wtMaterials = adManager.getEntityList(wtOrgRrn, Material.class,Integer.MAX_VALUE,"materialId ='"+ltMaterial.getMaterialId()+"' ",null);
					if(wtMaterials ==null || wtMaterials.size() == 0){
						throw new ClientParameterException("开能不存在该物料:"+ltMaterial.getMaterialId());
					}
					Material wtMaterial = wtMaterials.get(0);
					updateStorage(wtOrgRrn, wtMaterial.getObjectRrn(), 151043L, inLine.getQtyMovement(), isWriteOff, userRrn);
				}else{
					updateStorage(in.getOrgRrn(), inLine.getMaterialRrn(), warehouseRrn, inLine.getQtyMovement(), isWriteOff, userRrn);
					if(warehouseRrn.equals(151046L)&&"返修入库".equals(in.getInType())){
						//范总采购部库
						updateWorkShopStorage(in.getOrgRrn(), inLine.getMaterialRrn(), 92175029L, inLine.getQtyMovement().negate(), false, userRrn);
					}
				}
				if (MovementIn.InType.WIN == inType) {
					//生产入库需要从原有仓库中扣除
//					ManufactureOrder mo = em.find(ManufactureOrder.class, in.getMoRrn());
//					if (mo.getWorkCenterRrn() != null) {
//						WorkCenter cw = em.find(WorkCenter.class, mo.getWorkCenterRrn());
//						updateStorage(in.getOrgRrn(), inLine.getMaterialRrn(), cw.getWarehouseRrn(), inLine.getQtyMovement().negate(), true, userRrn);
//					}
					Warehouse writeOffWarehouse = getWriteOffWarehouse(in.getOrgRrn());
					updateStorage(in.getOrgRrn(), inLine.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), inLine.getQtyMovement().negate(), false, userRrn);
				}
				
				if (MovementIn.IN_TYPE_AMOUNT_ADJUST.equals(in.getInType())
					&& inLine.getQtyMovement().doubleValue() == 0) {
				} else {
					if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
						Lot lot = this.getMaterialLot(in.getOrgRrn(), material, userRrn);
						//商品对应批次在默认仓库的库存增加,
						if(warehouseRrn.equals(42803113L)){
							Long wtOrgRrn =139420L;
							List<Lot> wtLots = adManager.getEntityList(wtOrgRrn, Lot.class,Integer.MAX_VALUE,"lotId ='"+lot.getLotId()+"' ",null);
							Lot wtLot =null;
							if(wtLots!=null&&wtLots.size() >0){
								wtLot = wtLots.get(0);
							}
							if(wtLot==null || wtLot.getObjectRrn() ==null){
								Material ltMaterial = em.find(Material.class, inLine.getMaterialRrn()) ;
								List<Material> wtMaterials = adManager.getEntityList(wtOrgRrn, Material.class,Integer.MAX_VALUE,"materialId ='"+ltMaterial.getMaterialId()+"' ",null);
								if(wtMaterials ==null || wtMaterials.size() == 0){
									throw new ClientParameterException("开能不存在该物料:"+ltMaterial.getMaterialId());
								}
								Material wtMaterial = wtMaterials.get(0);
								wtLot =  new Lot();
								wtLot.setOrgRrn(wtOrgRrn);
								wtLot.setIsActive(true);
								wtLot.setCreated(new Date());
								wtLot.setCreatedBy(userRrn);
								wtLot.setUpdated(new Date());
								wtLot.setUpdatedBy(userRrn);
								wtLot.setLotId(lot.getLotId());
								wtLot.setLotType(wtMaterial.getLotType());
								wtLot.setMaterialRrn(wtMaterial.getObjectRrn());
								wtLot.setMaterialId(wtMaterial.getMaterialId());
								wtLot.setMaterialName(wtMaterial.getName());
								wtLot.setWarehouseId("环保-良品");
								wtLot.setWarehouseRrn(151043L);
								em.persist(wtLot);
							}
							this.updateLotStorage(wtOrgRrn, wtLot.getObjectRrn(), 151043L, inLine.getQtyMovement(), userRrn);
						}else{
							this.updateLotStorage(in.getOrgRrn(), lot.getObjectRrn(), warehouseRrn, inLine.getQtyMovement(), userRrn);
							//wms入库
							switch (inType){
							case PIN:
								if(in.getOrgRrn().equals(139420L) && in.getWmsWarehouse()!=null && !"".equals(in.getWmsWarehouse())){
									StockSpecial stockSpecial = new StockSpecial();
									stockSpecial.setOrgRrn(lot.getOrgRrn());
									stockSpecial.setIsActive(true);
									stockSpecial.setSpecialId(in.getDocId());
									stockSpecial.setSpecialTime(new Date());
									stockSpecial.setSpecialType(in.getDocType());						
									stockSpecial.setReceiptId(in.getReceiptId());
									stockSpecial.setMaterialCode(material.getMaterialId());
									stockSpecial.setBatch(lot.getLotId());
									stockSpecial.setQuality(inLine.getQtyMovement());
									stockSpecial.setErpWrite(1L);
									stockSpecial.setErpWriteTime(new Date());
									stockSpecial.setWmsRead(0L);
									stockSpecial.setSupplierName(in.getVendorId());
									stockSpecial.setUserName(in.getUserCreated());
									em.persist(stockSpecial);
								}
								break;
							case OIN:
								if(in.getOrgRrn().equals(139420L) && in.getWmsWarehouse()!=null && !"".equals(in.getWmsWarehouse())){
									StockIn stockIn = new StockIn();
									stockIn.setOrgRrn(in.getOrgRrn());
									stockIn.setIsActive(true);
									stockIn.setReceiptId(in.getDocId());
									stockIn.setReceiptTime(new Date());
									stockIn.setReceiptType(in.getDocType());
									stockIn.setMaterialCode(material.getMaterialId());
									stockIn.setBatch(lot.getLotId());
									stockIn.setQuality(inLine.getQtyMovement());
									stockIn.setErpWrite(1L);
									stockIn.setErpWriteTime(new Date());
									stockIn.setWmsRead(0L);
									stockIn.setSupplierName(in.getVendorId());
									stockIn.setUserName(user.getUserName());
									em.persist(stockIn);
								}
								break;
							}
						}
						
						// 扣除核销仓库中的批次库存
						switch (inType) {
						case WIN:
							//商品对应的批次在核销仓库的库存需要减少
							Warehouse writeOffWarehouse = getWriteOffWarehouse(in.getOrgRrn());
							updateLotStorage(in.getOrgRrn(), lot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), inLine.getQtyMovement().negate(), userRrn);
							break;
						}
					} else {
						//检查Lot的数量与Line中的数量是否相等
						String whereClause = " movementLineRrn = '" + inLine.getObjectRrn() + "' ";
						List<MovementLineLot> lineLots = adManager.getEntityList(inLine.getOrgRrn(), MovementLineLot.class,
								Integer.MAX_VALUE, whereClause, null);
						//material类型无需校验movementLot因为无需手动挂批次，
						BigDecimal qtyLine = inLine.getQtyMovement();
						BigDecimal qtyTotal = BigDecimal.ZERO;
						for (MovementLineLot lineLot : lineLots) {
							qtyTotal = qtyTotal.add(lineLot.getQtyMovement());
						}
						if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
							throw new ClientException("inv.in_lot_qty_different");
						}
					
						for(MovementLineLot movementLot : lineLots) {
							if(warehouseRrn.equals(42803113L)){
								Long wtOrgRrn =139420L;
								List<Lot> wtLots = adManager.getEntityList(wtOrgRrn, Lot.class,Integer.MAX_VALUE,"lotId ='"+movementLot.getLotId()+"' ",null);
								Lot wtLot = null;
								if(wtLots!=null&&wtLots.size() >0){
									wtLot = wtLots.get(0);
								}
								if(wtLot==null || wtLot.getObjectRrn() ==null){
									Material ltMaterial = em.find(Material.class, inLine.getMaterialRrn()) ;
									List<Material> wtMaterials = adManager.getEntityList(wtOrgRrn, Material.class,Integer.MAX_VALUE,"materialId ='"+ltMaterial.getMaterialId()+"' ",null);
									if(wtMaterials ==null || wtMaterials.size() == 0){
										throw new ClientParameterException("开能不存在该物料:"+ltMaterial.getMaterialId());
									}
									Material wtMaterial = wtMaterials.get(0);
									wtLot =  new Lot();
									wtLot.setOrgRrn(wtOrgRrn);
									wtLot.setIsActive(true);
									wtLot.setCreated(new Date());
									wtLot.setCreatedBy(userRrn);
									wtLot.setUpdated(new Date());
									wtLot.setUpdatedBy(userRrn);
									wtLot.setLotId(movementLot.getLotId());
									wtLot.setLotType(wtMaterial.getLotType());
									wtLot.setMaterialRrn(wtMaterial.getObjectRrn());
									wtLot.setMaterialId(wtMaterial.getMaterialId());
									wtLot.setMaterialName(wtMaterial.getName());
									wtLot.setWarehouseId("环保-良品");
									wtLot.setWarehouseRrn(151043L);
									em.persist(wtLot);
								}
								this.updateLotStorage(139420, wtLot.getObjectRrn(), 151043L, movementLot.getQtyMovement(), userRrn);
							}else{
								this.updateLotStorage(in.getOrgRrn(), movementLot.getLotRrn(), warehouseRrn, movementLot.getQtyMovement(), userRrn);
							
							}
							Lot lot = em.find(Lot.class, movementLot.getLotRrn());
							if(warehouseRrn.equals(42803113L)){
								lot.setWarehouseRrn(151043L);
								lot.setWarehouseId("环保-良品");
							}else{
								lot.setWarehouseRrn(house.getObjectRrn());
								lot.setWarehouseId(house.getWarehouseId());
							}
		
							if (locator != null) {
								lot.setLocatorRrn(locator.getObjectRrn());
								lot.setLocatorId(locator.getLocatorId());
							}
							switch (inType) {
								case PIN:
									lot.setDateIn(dateIn);
									lot.setPosition(Lot.POSITION_INSTOCK);
									//WMS入库
									if(in.getOrgRrn().equals(139420L) && in.getWmsWarehouse()!=null && !"".equals(in.getWmsWarehouse())){
										StockSpecial stockSpecial = new StockSpecial();
										stockSpecial.setOrgRrn(movementLot.getOrgRrn());
										stockSpecial.setIsActive(true);
										stockSpecial.setSpecialId(in.getDocId());
										stockSpecial.setSpecialTime(new Date());
										stockSpecial.setSpecialType(in.getDocType());						
										stockSpecial.setReceiptId(in.getReceiptId());
										stockSpecial.setMaterialCode(material.getMaterialId());
										stockSpecial.setBatch(movementLot.getLotId());
										stockSpecial.setQuality(movementLot.getQtyMovement());
										stockSpecial.setErpWrite(1L);
										stockSpecial.setErpWriteTime(new Date());
										stockSpecial.setWmsRead(0L);
										stockSpecial.setSupplierName(in.getVendorId());
										stockSpecial.setUserName(in.getUserCreated());
										em.persist(stockSpecial);
									}
									break;
								case WIN:
									lot.setDateIn(dateIn);
									lot.setPosition(Lot.POSITION_INSTOCK);
									// 扣除核销仓库中的批次库存
									Warehouse writeOffWarehouse = getWriteOffWarehouse(in.getOrgRrn());
									this.updateLotStorage(in.getOrgRrn(), movementLot.getLotRrn(), writeOffWarehouse.getObjectRrn(), movementLot.getQtyMovement().negate(), userRrn);
									//生产入库原物料已被核销
									break;
								case OIN:
									lot.setDateIn(dateIn);
									lot.setPosition(Lot.POSITION_INSTOCK);
									//WMS入库
									if(in.getOrgRrn().equals(139420L) && in.getWmsWarehouse()!=null && !"".equals(in.getWmsWarehouse())){
										StockIn stockIn = new StockIn();
										stockIn.setOrgRrn(in.getOrgRrn());
										stockIn.setIsActive(true);
										stockIn.setReceiptId(in.getDocId());
										stockIn.setReceiptTime(new Date());
										stockIn.setReceiptType(in.getDocType());
										stockIn.setMaterialCode(material.getMaterialId());
										stockIn.setBatch(movementLot.getLotId());
										stockIn.setQuality(movementLot.getQtyMovement());
										stockIn.setErpWrite(1L);
										stockIn.setErpWriteTime(new Date());
										stockIn.setWmsRead(0L);
										stockIn.setSupplierName(in.getVendorId());
										stockIn.setUserName(user.getUserName());
										em.persist(stockIn);
									}
									break;
								case RIN:
									lot.setDateIn(dateIn);
									lot.setPosition(Lot.POSITION_INSTOCK);
									break;
								case ADIN:
									lot.setDateIn(dateIn);
									lot.setPosition(Lot.POSITION_INSTOCK);
									break;
							}
							em.merge(lot);
							LotHis his = null;
							switch (inType) {
								case PIN:
									his = new PinLotHis(lot);
									break;
								case WIN:
									his = new WinLotHis(lot);
									break;
								case OIN:
									his = new OinLotHis(lot);
									break;
								case RIN:
									his = new RinLotHis(lot);
									break;
								case ADIN:
									his = new AdinLotHis(lot);
									break;
							}
							if (his != null) {
								his.setHisSeq(transSeq);
								em.persist(his);
							}
						}	
					}
				}
				
				inLines.add(inLine);
			}
			in.setAccessLineTotal(accessLineTotal);
			in.setMovementLines(inLines);
			em.merge(in);
			in = em.getReference(in.getClass(), in.getObjectRrn());
			return in;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementIn writeOffMovementIn(MovementIn in, long userRrn) throws ClientException {
		try{
			List<MovementLine> inLines = in.getMovementLines();
			Boolean allFlag = true;
			in.setDateWriteOff(new Date());
			BigDecimal invoiceLineTotal=new BigDecimal(0);
			//对于不在当月核销的入库单，系统自动进行调整,暂估>发票 加一个负数，否则加一个正数
			Date dateApproved = in.getDateApproved();
			Date currentDate = new Date();
			List<MovementLine> moneyMovementLines = new ArrayList<MovementLine>();
			for(MovementLine ml : inLines){//只有全部都冲销了之后才将状态设成completed
				if(ml.getInvoiceLineTotal() == null){
					allFlag = false;
					break;
				}
				invoiceLineTotal=invoiceLineTotal.add(ml.getInvoiceLineTotal()==null?BigDecimal.ZERO:ml.getInvoiceLineTotal());
				if(ml.getInvoiceLineTotal() != null){
					if(dateApproved.getMonth()!=currentDate.getMonth() && (in.getOrgRrn()==139420L || in.getOrgRrn()== 12644730L || in.getOrgRrn()== 68088906L)){
						MovementLine oldMovementLine = em.find(MovementLine.class, ml.getObjectRrn());
						if(oldMovementLine.getInvoiceLineTotal()!=null){
							//对于已经存在冲销的物料,系统不在进行冲销
							continue;
						}
						MovementLine moneyMovementLine = new MovementLine();
						moneyMovementLine.setOrgRrn(ml.getOrgRrn());
						moneyMovementLine.setCreated(new Date());
						moneyMovementLine.setCreatedBy(userRrn);
						moneyMovementLine.setUpdated(new Date());
						moneyMovementLine.setUpdatedBy(userRrn);
						moneyMovementLine.setLineNo(ml.getLineNo());
						moneyMovementLine.setMaterialId(ml.getMaterialId());
						moneyMovementLine.setMaterialName(ml.getMaterialName());
						moneyMovementLine.setMaterialRrn(ml.getMaterialRrn());
						moneyMovementLine.setUomId(ml.getUomId());
						moneyMovementLine.setLotType(ml.getLotType());
						if(ml.getAssessLineTotal().compareTo(ml.getInvoiceLineTotal())>0){
							BigDecimal unitPrice = ml.getInvoiceLineTotal().subtract(ml.getAssessLineTotal());
							moneyMovementLine.setUnitPrice(unitPrice);
							moneyMovementLines.add(moneyMovementLine);
						}else if(ml.getAssessLineTotal().compareTo(ml.getInvoiceLineTotal())<0){
							BigDecimal unitPrice = ml.getInvoiceLineTotal().subtract(ml.getAssessLineTotal());
							moneyMovementLine.setUnitPrice(unitPrice);
							moneyMovementLines.add(moneyMovementLine);
						}
					}
				}
			}
			if(allFlag){
				in.setDocStatus(PurchaseOrder.STATUS_COMPLETED);
			}
			in.setUpdatedBy(userRrn);
			in.setInvoiceLineTotal(invoiceLineTotal);
			em.merge(in);
			
			if(dateApproved.getMonth()!=currentDate.getMonth()&& (in.getOrgRrn()==139420L || in.getOrgRrn()== 12644730L || in.getOrgRrn()== 68088906L )){
				if(moneyMovementLines!=null && moneyMovementLines.size()>0){
					MovementIn moneyMovementIn =new MovementIn();//入库调整
					moneyMovementIn.setOrgRrn(in.getOrgRrn());
					moneyMovementIn.setCreated(new Date());
					moneyMovementIn.setCreatedBy(userRrn);
					moneyMovementIn.setUpdated(new Date());
					moneyMovementIn.setUpdatedBy(userRrn);
					moneyMovementIn.setWarehouseRrn(in.getWarehouseRrn());
					moneyMovementIn.setWarehouseId(in.getWarehouseId());
					moneyMovementIn.setInType("金额调整");
					moneyMovementIn.setDbaMark("单个核销入库单号:"+in.getDocId()+",采购订单号:"+in.getPoId());
					moneyMovementIn = saveMovementInLine(moneyMovementIn, moneyMovementLines, MovementIn.InType.OIN, userRrn);
					approveMovementIn(moneyMovementIn,MovementIn.InType.OIN,userRrn);
				}
			}
			
			for(MovementLine ml : inLines){
				if(ml.getObjectRrn() != null) em.merge(ml);
			}
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return in;
	}
	
	public List<MovementIn> writeOffMovementIns(List<MovementIn> ins, long userRrn) throws ClientException {
		try{
			List<MovementLine> inLines = new ArrayList<MovementLine>();
			for(MovementIn in : ins){
				Boolean allFlag = true;
				//对于不在当月核销的入库单，系统自动进行调整,暂估>发票 加一个负数，否则加一个正数
				Date dateApproved = in.getDateApproved();
				Date currentDate = new Date();
				List<MovementLine> moneyMovementLines = new ArrayList<MovementLine>();
				BigDecimal invoiceLineTotal=new BigDecimal(0);//发票总额
				for(MovementLine ml : in.getMovementLines()){//只有全部都冲销了之后才将状态设成completed
					invoiceLineTotal = invoiceLineTotal.add(ml.getInvoiceLineTotal()==null?BigDecimal.ZERO:ml.getInvoiceLineTotal());
					if(ml.getInvoiceLineTotal() != null){
						inLines.add(ml);
						if(dateApproved.getMonth()!=currentDate.getMonth() && (in.getOrgRrn()==139420L || in.getOrgRrn()== 12644730L || in.getOrgRrn()== 68088906L)){
							MovementLine oldMovementLine = em.find(MovementLine.class, ml.getObjectRrn());
							if(oldMovementLine.getInvoiceLineTotal()!=null){
								//对于已经存在冲销的物料,系统不在进行冲销
								continue;
							}
							MovementLine moneyMovementLine = new MovementLine();
							moneyMovementLine.setOrgRrn(ml.getOrgRrn());
							moneyMovementLine.setCreated(new Date());
							moneyMovementLine.setCreatedBy(userRrn);
							moneyMovementLine.setUpdated(new Date());
							moneyMovementLine.setUpdatedBy(userRrn);
							moneyMovementLine.setLineNo(ml.getLineNo());
							moneyMovementLine.setMaterialId(ml.getMaterialId());
							moneyMovementLine.setMaterialName(ml.getMaterialName());
							moneyMovementLine.setMaterialRrn(ml.getMaterialRrn());
							moneyMovementLine.setUomId(ml.getUomId());
							moneyMovementLine.setLotType(ml.getLotType());
							if(ml.getAssessLineTotal().compareTo(ml.getInvoiceLineTotal())>0){
								BigDecimal unitPrice = ml.getInvoiceLineTotal().subtract(ml.getAssessLineTotal());
								moneyMovementLine.setUnitPrice(unitPrice);
								moneyMovementLines.add(moneyMovementLine);
							}else if(ml.getAssessLineTotal().compareTo(ml.getInvoiceLineTotal())<0){
								BigDecimal unitPrice = ml.getInvoiceLineTotal().subtract(ml.getAssessLineTotal());
								moneyMovementLine.setUnitPrice(unitPrice);
								moneyMovementLines.add(moneyMovementLine);
							}
						}
					}else{
						allFlag = false;
					}
				}
				in.setDateWriteOff(new Date());
				if(allFlag){
					in.setDocStatus(PurchaseOrder.STATUS_COMPLETED);
				}
				in.setUpdatedBy(userRrn);
				in.setInvoiceLineTotal(invoiceLineTotal);
				em.merge(in);
				if(dateApproved.getMonth()!=currentDate.getMonth()&& (in.getOrgRrn()==139420L || in.getOrgRrn()== 12644730L || in.getOrgRrn()== 68088906L)){
					if(moneyMovementLines!=null && moneyMovementLines.size()>0){
						MovementIn moneyMovementIn =new MovementIn();//入库调整
						moneyMovementIn.setOrgRrn(in.getOrgRrn());
						moneyMovementIn.setCreated(new Date());
						moneyMovementIn.setCreatedBy(userRrn);
						moneyMovementIn.setUpdated(new Date());
						moneyMovementIn.setUpdatedBy(userRrn);
						moneyMovementIn.setWarehouseRrn(in.getWarehouseRrn());
						moneyMovementIn.setWarehouseId(in.getWarehouseId());
						moneyMovementIn.setInType("金额调整");
						moneyMovementIn.setDbaMark("入库单号:"+in.getDocId()+",采购订单号:"+in.getPoId());
						moneyMovementIn = saveMovementInLine(moneyMovementIn, moneyMovementLines, MovementIn.InType.OIN, userRrn);
						approveMovementIn(moneyMovementIn,MovementIn.InType.OIN,userRrn);
					}
				}
			}
			
			for(MovementLine ml : inLines){
				if(ml.getObjectRrn() != null) em.merge(ml);
			}
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return ins;
	}
	
	public MovementLine saveMovementTransferLine(MovementTransfer transfer, MovementLine line, long userRrn) throws ClientException {
		List<MovementLine> list = new ArrayList<MovementLine>();
		list.add(line);
		transfer = saveMovementTransferLine(transfer, list, userRrn);
		return transfer.getMovementLines().get(0);
	}	
	
	public MovementTransfer saveMovementTransferLine(MovementTransfer transfer, List<MovementLine> lines, long userRrn) throws ClientException {
		try{
			if (transfer.getObjectRrn() == null) {
				transfer.setIsActive(true);
				transfer.setCreatedBy(userRrn);
				transfer.setCreated(new Date());
				transfer.setTotalLines(0L);
				transfer.setDocStatus(MovementTransfer.STATUS_DRAFTED);
				transfer.setDocType(MovementTransfer.DOCTYPE_TRF);
				String docId = transfer.getDocId();
				if (docId == null || docId.length() == 0) {
					transfer.setDocId(generateTransferCode(transfer));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<Movement> movements = adManager.getEntityList(transfer.getOrgRrn(), Movement.class, 2, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}

				ADUser user = em.find(ADUser.class, userRrn);
				transfer.setUserCreated(user.getUserName());
				em.persist(transfer);
			} 
			if (transfer.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (transfer.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			if (transfer.getWarehouseRrn().equals(transfer.getTargetWarehouseRrn())) {
				throw new ClientException("inv.warehouse_target_can_not_equal");
			}
			Warehouse house = em.find(Warehouse.class, transfer.getWarehouseRrn());
			transfer.setWarehouseId(house.getWarehouseId());
			Warehouse targetHouse = em.find(Warehouse.class, transfer.getTargetWarehouseRrn());
			transfer.setTargetWarehouseId(targetHouse.getWarehouseId());
			
			List<MovementLine> savaLine = new ArrayList<MovementLine>();
			for (MovementLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				//根据denny和刘总要求,环保到制造保存 入库数可以不填写，但审核需要填写
				if(line.getOrgRrn() == 139420L){
					if ("环保-良品".equals(transfer.getWarehouseId())&& "制造车间良品".equals(transfer.getTargetWarehouseId())) {
						//不做比较
					}else{
						if (line.getQtyMovement().doubleValue() == 0) {
							continue;
						}
					}
				}else{
					if (line.getQtyMovement().doubleValue() == 0) {
						continue;
					}
				}
//				if (line.getQtyMovement().doubleValue() == 0) {
//					continue;
//				}
				
				//如果是Update，则将原记录删除
				if (line.getObjectRrn() != null) {
					MovementLine oldLine = new MovementLine();
					oldLine.setObjectRrn(line.getObjectRrn());
					oldLine.setMovementRrn(line.getMovementRrn());
					
					em.merge(transfer);
					deleteMovementTransferLine(oldLine, false, userRrn);
					em.flush();
					transfer = (MovementTransfer)em.find(Movement.class, transfer.getObjectRrn());
					transfer.setMovementLines(null);
					
					line.setObjectRrn(null);
				}
				
				if (line.getLocatorRrn() != null) {
					Locator locator = em.find(Locator.class, line.getLocatorRrn());
					line.setLocatorId(locator.getLocatorId());
				}
				
				if (line.getObjectRrn() != null) {
					
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(transfer.getObjectRrn());
					line.setMovementId(transfer.getDocId());
					transfer.setTotalLines(transfer.getTotalLines() + 1);
					em.persist(line);
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
					for (MovementLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				} else {
					List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
					for (MovementLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (!(Lot.POSITION_INSTOCK.equals(lot.getPosition()) || Lot.POSITION_GEN.equals(lot.getPosition()))) {
							throw new ClientParameterException("inv.lot_not_in", lot.getLotId());
						} 
						if (lot.getIsUsed()) {
							throw new ClientParameterException("inv.lot_already_used", lot.getLotId());
						}
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
							}
						} else {
							LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						//调拨完后将目标仓库设为批次的当前仓库(此处不需要，放到approveMovementTransfer中去了)						
//						lot.setWarehouseRrn(transfer.getTargetWarehouseRrn());
//						lot.setWarehouseId(transfer.getTargetWarehouseId());
						lot.setTransferLineRrn(line.getObjectRrn());
						em.merge(lot);

						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			transfer.setUpdatedBy(userRrn);
			transfer.setMovementLines(null);
			em.merge(transfer);
			transfer.setMovementLines(savaLine);
			
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return transfer;
	}
	
	public void deleteMovementTransfer(MovementTransfer transfer, long userRrn) throws ClientException {
		try{
			if(transfer != null && transfer.getObjectRrn() != null) {
				transfer = em.find(MovementTransfer.class, transfer.getObjectRrn());
				for (int i=0; i< transfer.getMovementLines().size(); i++){
					MovementLine line= transfer.getMovementLines().get(i);
					deleteMovementTransferLine(line, true, userRrn);
				}
				em.remove(transfer);
				
				MovementHis his = new MovementHis(transfer);
				his.setCreatedBy(userRrn);
				his.setUpdatedBy(userRrn);
				his.setActionType(MovementHis.ACTION_TYPE_DELETE);
				his.setDateAction(new Date());
				em.persist(his);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteMovementTransferLine(MovementLine movementLine, long userRrn) throws ClientException {
		deleteMovementTransferLine(movementLine, false, userRrn);
	}
	
	public void deleteMovementTransferLine(MovementLine movementLine, boolean allFlag, long userRrn) throws ClientException {
		try {
			if(movementLine != null && movementLine.getObjectRrn() != null) {
				movementLine = em.find(MovementLine.class, movementLine.getObjectRrn());
				// 更新movement
				MovementTransfer transfer = em.find(MovementTransfer.class, movementLine.getMovementRrn());		
				if (!allFlag) {
					transfer.setTotalLines(transfer.getTotalLines() - 1);
					em.merge(transfer);
				}
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" SELECT MovementLineLot FROM MovementLineLot MovementLineLot ");
				sql.append(" WHERE  movementLineRrn = ? ");
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, movementLine.getObjectRrn());
				List<MovementLineLot> movementLots = query.getResultList();
				for (MovementLineLot movementLot : movementLots) {
					Lot lot = em.find(Lot.class, movementLot.getLotRrn());
					lot.setTransferLineRrn(null);
					lot.setUpdatedBy(userRrn);
					em.merge(lot);
					
					em.remove(movementLot);
				}
				em.remove(movementLine);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementTransfer approveMovementTransfer(MovementTransfer transfer, long userRrn) throws ClientException {
		try{
			if (transfer.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (transfer.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			Long targetWarehouseRrn = transfer.getTargetWarehouseRrn();
			
			ADUser user = em.find(ADUser.class, userRrn);
			transfer.setUserApproved(user.getUserName());
			Warehouse house = em.find(Warehouse.class, transfer.getWarehouseRrn());
			Warehouse targetHouse = em.find(Warehouse.class, targetWarehouseRrn);
			transfer.setTargetWarehouseId(targetHouse.getWarehouseId());
			transfer.setDocStatus(MovementTransfer.STATUS_APPROVED);
			transfer.setDateApproved(new Date());
			transfer.setUpdatedBy(userRrn);
			em.merge(transfer);
			
			if (transfer.getMovementLines().size() == 0) {
				throw new ClientException("inv.transfer_quantity_zero"); 
			}
			
			long transSeq = basManager.getHisSequence();
			for (MovementLine line : transfer.getMovementLines()) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				
				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(Movement.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				//更新库存
				updateStorage(transfer.getOrgRrn(), line.getMaterialRrn(), transfer.getWarehouseRrn(), line.getQtyMovement().negate(), true, userRrn);
				updateStorage(transfer.getOrgRrn(), line.getMaterialRrn(), transfer.getTargetWarehouseRrn(), line.getQtyMovement(), true, userRrn);
				if(transfer.getIsServicesOut()){//环保-服务公司其他仓库 数量减少反加
					if(transfer.getWarehouseRrn()==151043L){
						updateServicesStorage(transfer.getOrgRrn(), line.getMaterialRrn(), transfer.getWarehouseRrn(), line.getQtyMovement().negate(),true, false, userRrn);
					}
					if(transfer.getTargetWarehouseRrn() == 151043L){
						updateServicesStorage(transfer.getOrgRrn(), line.getMaterialRrn(), transfer.getWarehouseRrn(), line.getQtyMovement(),true, false, userRrn);
					}
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					Lot lot = this.getMaterialLot(transfer.getOrgRrn(), material, userRrn);
					LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), userRrn);
					BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());
					if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
						throw new ClientParameterException("inv.not_sufficient_quantity",material.getMaterialId());
					}
					if(transfer.getOrgRrn()==139420L && transfer.getWarehouseRrn() ==151043L ){
						//Storage erpStorage = getMaterialStorage(transfer.getOrgRrn(), line.getMaterialRrn(), transfer.getWarehouseRrn(),userRrn);
						BigDecimal wmsQty =  getQtyInWmsStorage(material.getMaterialId(),"环保良品");
						wmsQty= wmsQty==null?BigDecimal.ZERO:wmsQty;
						BigDecimal erpQty =lotStorage.getQtyOnhand()==null?BigDecimal.ZERO:lotStorage.getQtyOnhand();
						if(erpQty.subtract(wmsQty).compareTo(line.getQtyMovement())<0){
							if(transfer.getWmsWarehouse()==null || "".equals(transfer.getWmsWarehouse())){
								throw new ClientException("该物料总仓无库存，需从立体库出货"+material.getMaterialId()); 
							}
							
						}
					}
					this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), line.getQtyMovement().negate(), userRrn);
					this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getTargetWarehouseRrn(), line.getQtyMovement(), userRrn);
					if(transfer.getOrgRrn().equals(139420L) && transfer.getWmsWarehouse()!=null && !"".equals(transfer.getWmsWarehouse())){
						//wms出库
						StockOut stockOut = new StockOut();
						stockOut.setOrgRrn(transfer.getOrgRrn());
						stockOut.setIsActive(true);
						stockOut.setReceiptId(transfer.getDocId());
						stockOut.setReceiptTime(new Date());
						stockOut.setReceiptType(transfer.getDocType());
						stockOut.setMaterialCode(lot.getMaterialId());
						stockOut.setBatch(lot.getLotId());
						stockOut.setQuality(line.getQtyMovement());
						stockOut.setErpWrite(1L);
						stockOut.setErpWriteTime(new Date());
						stockOut.setWmsRead(0L);
//						stockOut.setSupplierName();
						stockOut.setUserName(transfer.getUserCreated());
						em.persist(stockOut);
					}
				} else {
					String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
					List<MovementLineLot> movementLots = adManager.getEntityList(transfer.getOrgRrn(), MovementLineLot.class, Integer.MAX_VALUE, whereClause, "");
					//检查Lot的数量与Line中的数量是否相等
					BigDecimal qtyLine = line.getQtyMovement();
					BigDecimal qtyTotal = BigDecimal.ZERO;
					for (MovementLineLot movementLot : movementLots) {
						qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
					}
					if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
						throw new ClientParameterException("inv.transfer_lot_qty_different",material.getMaterialId());
					}
				
					for (MovementLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
							}
						} else {
							LotStorage lotStorage = this.getLotStorage(transfer.getOrgRrn(), movementLot.getLotRrn(), transfer.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientParameterException("inv.not_sufficient_quantity",material.getMaterialId());
							}
						}
						
						this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), movementLot.getQtyMovement().negate(), userRrn);
						this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getTargetWarehouseRrn(), movementLot.getQtyMovement(), userRrn);

						if(transfer.getOrgRrn().equals(139420L) && transfer.getWmsWarehouse()!=null && !"".equals(transfer.getWmsWarehouse())){
							//wms出库
							StockOut stockOut = new StockOut();
							stockOut.setOrgRrn(transfer.getOrgRrn());
							stockOut.setIsActive(true);
							stockOut.setReceiptId(transfer.getDocId());
							stockOut.setReceiptTime(new Date());
							stockOut.setReceiptType(transfer.getDocType());
							stockOut.setMaterialCode(movementLot.getMaterialId());
							stockOut.setBatch(movementLot.getLotId());
							stockOut.setQuality(movementLot.getQtyMovement());
							stockOut.setErpWrite(1L);
							stockOut.setErpWriteTime(new Date());
							stockOut.setWmsRead(0L);
//							stockOut.setSupplierName();
							stockOut.setUserName(transfer.getUserCreated());
							em.persist(stockOut);
						}
						
						lot.setWarehouseRrn(transfer.getTargetWarehouseRrn());
						lot.setWarehouseId(transfer.getTargetWarehouseId());
						em.merge(lot);
	
						LotHis his = new TransferLotHis(lot);
						his.setHisSeq(transSeq);
						em.persist(his);
					}
				}
			}
			return transfer;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteMovementIn(MovementIn movementIn, MovementIn.InType inType, long userRrn)throws ClientException {
		try {
			if (movementIn != null && movementIn.getObjectRrn() != null) {
				movementIn = em.find(MovementIn.class, movementIn.getObjectRrn());
				for (int i = 0; i < movementIn.getMovementLines().size(); i++) {
					MovementLine line = movementIn.getMovementLines().get(i);
					deleteMovementInLine(line, inType, true, userRrn);
				}
				em.remove(movementIn);
				if (movementIn.getIqcRrn() != null) {
					Iqc iqc = em.find(Iqc.class, movementIn.getIqcRrn());
					if (iqc != null) {
						iqc.setIsIn(false);
						em.merge(iqc);
					}
				}
				MovementHis his = new MovementHis(movementIn);
				his.setCreatedBy(userRrn);
				his.setUpdatedBy(userRrn);
				his.setActionType(MovementHis.ACTION_TYPE_DELETE);
				his.setDateAction(new Date());
				em.persist(his);
			}
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteMovementInLine(MovementLine movementLine, MovementIn.InType inType, long userRrn)throws ClientException {
		deleteMovementInLine(movementLine, inType, false, userRrn);
	}
	
	public void deleteMovementInLine(MovementLine movementLine, MovementIn.InType inType, boolean allFlag, long userRrn)throws ClientException {
		try {
			if(movementLine != null && movementLine.getObjectRrn() != null) {
				movementLine = em.find(MovementLine.class, movementLine.getObjectRrn());
				// 更新movement
				Movement in = em.find(Movement.class, movementLine.getMovementRrn());	
				if(!allFlag) {
					in.setTotalLines(in.getTotalLines() - 1);
					em.merge(in);					
				}
							
				// 若对应着采购订单行，则更新采购订单行入库数量及lot
				StringBuffer sql = new StringBuffer("");
				sql.append(" SELECT MovementLineLot FROM MovementLineLot MovementLineLot ");
				sql.append(" WHERE MovementLineLot.movementLineRrn = '" + movementLine.getObjectRrn() + "' ");
				Query query = em.createQuery(sql.toString());
				List<MovementLineLot> lineLots = (List<MovementLineLot>)query.getResultList();
				List<Lot> removeLots = new ArrayList<Lot>();
				for(MovementLineLot lineLot : lineLots) {
					Lot lot = em.find(Lot.class, lineLot.getLotRrn());
					if(movementLine.getObjectRrn().equals(lot.getInLineRrn())) {
						removeLots.add(lot);
					}
					em.remove(lineLot);
				}
				
				//修改相关的PR, MO等数量
				if (MovementIn.InType.PIN == inType) {
					if (movementLine.getIqcLineRrn() != null) {
						IqcLine iqcLine = em.find(IqcLine.class, movementLine.getIqcLineRrn());
						iqcLine.setQtyIn(iqcLine.getQtyIn().subtract(movementLine.getQtyMovement()));
						iqcLine.setUpdatedBy(userRrn);
						em.merge(iqcLine);
					}
				}
				
				for (Lot removeLot : removeLots) {
					switch (inType) {
						case PIN:
							if(removeLot.getReverseField10()==null){
								removeLot.setPosition(Lot.POSITION_IQC);
							}
							break;
						case WIN:
//							removeLot.setPosition(Lot.POSITION_WIP);
							break;
						case OIN:
							if (removeLot.getIqcLineRrn() != null) {
								removeLot.setPosition(Lot.POSITION_IQC);
							} else if (removeLot.getMoRrn() != null) {
								removeLot.setPosition(Lot.POSITION_WIP);
							} else {
								removeLot.setPosition(Lot.POSITION_GEN);
							}
							break;
						case RIN:
							break;
					}
					removeLot.setInId(null);
					removeLot.setInRrn(null);
					removeLot.setInLineRrn(null);
					removeLot.setUpdatedBy(userRrn);
					em.merge(removeLot);
				}
				
				em.remove(movementLine);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Warehouse getDefaultWarehouse(long orgRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Warehouse FROM Warehouse as Warehouse ");
		sql.append(" WHERE orgRrn = ? AND isDefault = 'Y' ");
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			List<Warehouse> list = query.getResultList();
			if (list == null || list.size() == 0) {
				throw new ClientException("inv.no_defalut_warehouse_found");
			} 
			return list.get(0);
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Warehouse getWriteOffWarehouse(long orgRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Warehouse FROM Warehouse as Warehouse ");
		sql.append(" WHERE orgRrn = ? AND isWriteOff = 'Y' ");
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			List<Warehouse> list = query.getResultList();
			if (list == null || list.size() == 0) {
				return null;
			} 
			return list.get(0);
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//恢复最原始的版本，可以从数据库自动选择批次
	public List<Lot> getOptionalOutLot(MovementLine outLine) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Lot, LotStorage FROM Lot as Lot, LotStorage as LotStorage ");
		sql.append(" WHERE Lot.objectRrn = LotStorage.lotRrn" );
		sql.append(" AND Lot.materialRrn = ? ");
		sql.append(" AND LotStorage.warehouseRrn = ? ");
		sql.append(" AND LotStorage.qtyOnhand <> 0 ");
		sql.append(" ORDER BY Lot.dateIn ASC ");
		
		logger.debug(sql);
		List<Lot> optionalLots = new ArrayList<Lot>();
		try {
			Movement movement = em.find(Movement.class, outLine.getMovementRrn());
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, outLine.getMaterialRrn());
			query.setParameter(2, movement.getWarehouseRrn());
			List list = query.getResultList();
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[])list.get(i);
				Lot lot = (Lot)obj[0];
				LotStorage lotStorage = (LotStorage)obj[1];
				lot.setQtyTransaction(lotStorage.getQtyOnhand());
				optionalLots.add(lot);
			}
			

			Material material = em.find(Material.class, outLine.getMaterialRrn());
			
			if (material.getIsLotControl()) {
				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
					if (outLine.getQtyMovement().doubleValue() > optionalLots.size()) {
						throw new ClientException("inv.not_sufficient_quantity");
					} else {
						List<Lot> lots = new ArrayList<Lot>();
						for (int i = 0; i < outLine.getQtyMovement().intValue(); i++) {
							lots.add(optionalLots.get(i));
						}
						return lots;
					}
				} else {
					BigDecimal qtyOut = BigDecimal.ZERO;
					List<Lot> lots = new ArrayList<Lot>();
					for (Lot optionalLot : optionalLots) {
						qtyOut = qtyOut.add(optionalLot.getQtyTransaction()); 
						if (qtyOut.compareTo(outLine.getQtyMovement()) >= 0) {
							optionalLot.setQtyTransaction(
									optionalLot.getQtyTransaction().subtract(qtyOut.subtract(outLine.getQtyMovement())));
							lots.add(optionalLot);
							break;
						} else {
							lots.add(optionalLot);
						}
					}
					return lots;
				}
			} else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Lot> getOptionalOutLot(MovementLine outLine , String lotid) throws ClientException {
		return getOptionalOutLot(outLine, lotid, null);
	}
	
	public List<Lot> getOptionalOutLot(MovementLine outLine , String lotid,String position) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Lot, LotStorage FROM Lot as Lot, LotStorage as LotStorage ");
		sql.append(" WHERE LotStorage.qtyOnhand <> 0 " );
		sql.append(" AND LotStorage.warehouseRrn = ? ");
		sql.append(" AND LotStorage.lotRrn = Lot.objectRrn ");
		sql.append(" AND Lot.lotId like ? ");
		sql.append(" AND Lot.materialRrn = ? ");
		if(position != null && !"".equals(position)){
			sql.append(" AND Lot.position = '" + position + "' ");
		}
		sql.append(" ORDER BY Lot.dateIn , Lot.lotId ASC ");
		logger.debug(sql);
		List<Lot> optionalLots = new ArrayList<Lot>();
		try {
			Movement movement = em.find(Movement.class, outLine.getMovementRrn());
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, movement.getWarehouseRrn());
			query.setParameter(2, lotid);
			query.setParameter(3, outLine.getMaterialRrn());	
			List<?> list = query.getResultList();
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[])list.get(i);
				Lot lot = (Lot)obj[0];
				LotStorage lotStorage = (LotStorage)obj[1];
				lot.setQtyTransaction(lotStorage.getQtyOnhand());
				optionalLots.add(lot);
			}
			

			Material material = em.find(Material.class, outLine.getMaterialRrn());
			
			if (material.getIsLotControl()) {
//				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
					//2012-03-23 Simon 不做数量检查，待保存时再检查
//					if (outLine.getQtyMovement().doubleValue() > optionalLots.size()) {
//						throw new ClientException("inv.not_sufficient_quantity");
//					} 
//					else {
//						/* 2012-03-20 选择批次时，列出所有供选择
//						List<Lot> lots = new ArrayList<Lot>();
//						for (int i = 0; i < outLine.getQtyMovement().intValue(); i++) {
//							lots.add(optionalLots.get(i));
//						}
//						return lots;
//						*/
//						return optionalLots;
//					}
//				} 
//				else {
					/* 2012-03-20 返回所有查到的批次，而不是随机给出批号
					BigDecimal qtyOut = BigDecimal.ZERO;
					List<Lot> lots = new ArrayList<Lot>();
					for (Lot optionalLot : optionalLots) {
						qtyOut = qtyOut.add(optionalLot.getQtyTransaction()); 
						if (qtyOut.compareTo(outLine.getQtyMovement()) >= 0) {
							optionalLot.setQtyTransaction(
									optionalLot.getQtyTransaction().subtract(qtyOut.subtract(outLine.getQtyMovement())));
							lots.add(optionalLot);
							break;
						} else {
							lots.add(optionalLot);
						}
					}
					return lots;
					*/
					//判断库存数量和出库数量的大小
//					BigDecimal qtyOut = BigDecimal.ZERO;
//					for (Lot optionalLot : optionalLots) {
//						qtyOut = qtyOut.add(optionalLot.getQtyTransaction()); 
//					}
//					if(qtyOut.compareTo(outLine.getQtyMovement()) < 0){
//						//2012-03-23 Simon 不做数量检查，待保存时再检查
//						//throw new ClientException("inv.not_sufficient_quantity");
//					}
//					else{
						return optionalLots;
//					}
//				}
			} 
			else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementLine saveMovementOutLine(MovementOut out, MovementLine outLine, MovementOut.OutType outType, long userRrn) throws ClientException {
		List<MovementLine> list = new ArrayList<MovementLine>();
		list.add(outLine);
		out = saveMovementOutLine(out, list, outType, userRrn);
		return out.getMovementLines().get(0);
	}
	
	public MovementOut saveMovementOutLine(MovementOut out, List<MovementLine> lines, MovementOut.OutType outType, long userRrn) throws ClientException {
		try{
			//如果是新建的单子，那么先保存出库单到movement_out表中
			if (out.getObjectRrn() == null) {
				out.setIsActive(true);
				out.setCreatedBy(userRrn);
				out.setCreated(new Date());
				out.setDateCreated(new Date());
				out.setTotalLines(0L);
				switch (outType) {
					case SOU:
						out.setDocType(MovementOut.DOCTYPE_SOU);
						break;
					case OOU:
						out.setDocType(MovementOut.DOCTYPE_OOU);
						break;
					case AOU://财务调整
						out.setDocType(MovementOut.DOCTYPE_AOU);
						break;
					case ADOU://营运调整出库
						out.setDocType(MovementOut.DOCTYPE_ADOU);
						break;
				}
				out.setDocStatus(MovementOut.STATUS_DRAFTED);
				String docId = out.getDocId();
				if (docId == null || docId.length() == 0) {
					out.setDocId(generateOutCode(out));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<Movement> movements = adManager.getEntityList(out.getOrgRrn(), Movement.class, 1, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}

				ADUser user = em.find(ADUser.class, userRrn);
				out.setUserCreated(user.getUserName());
				em.persist(out);
			}
			if (out.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}

			Warehouse house = em.find(Warehouse.class, out.getWarehouseRrn());
			out.setWarehouseId(house.getWarehouseId());
			
			List<MovementLine> savaLine = new ArrayList<MovementLine>();
			for (MovementLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getQtyMovement().doubleValue() == 0) {
					continue;
				}
				if (line.getUnitPrice() != null) {
					if(MovementOut.OutType.AOU.equals(outType) && (MovementOut.OUT_TYPE_IN_ADJUST.equals(out.getOutType()) || MovementOut.OUT_TYPE_RD_ADJUST.equals(out.getOutType()))){
						line.setLineTotal(line.getQtyMovement().negate().multiply(line.getUnitPrice()));
					}
					else{
						line.setLineTotal(line.getQtyMovement().multiply(line.getUnitPrice()));
					}
				}
				else{
					line.setLineTotal(null);
				}
				
				//如果是Update，则将原记录删除
				if (line.getObjectRrn() != null) {
					MovementLine oldLine = new MovementLine();
					oldLine.setObjectRrn(line.getObjectRrn());
					oldLine.setMovementRrn(line.getMovementRrn());
					
					em.merge(out);
					deleteMovementOutLine(oldLine, false, userRrn);
					em.flush();
					out = (MovementOut)em.find(Movement.class, out.getObjectRrn());
					out.setMovementLines(null);
					
					line.setObjectRrn(null);
				}
				
				if (line.getObjectRrn() != null) {	
				} 
				else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(MovementOut.STATUS_DRAFTED);
					line.setMovementRrn(out.getObjectRrn());
					line.setMovementId(out.getDocId());
					out.setTotalLines(out.getTotalLines() + 1);
					em.persist(line);
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
					for (MovementLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} 
						else {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				else {
					List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
					for (MovementLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						
						if (lot.getIsUsed()) {
							throw new ClientParameterException("inv.lot_already_used", lot.getLotId());
						}
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
							}
							
							if(MovementOut.DOCTYPE_AOU.equals(out.getDocType()) && (MovementOut.OUT_TYPE_OUT_ADJUST.equals(out.getOutType()) || MovementOut.OUT_TYPE_SALE_ADJUST.equals(out.getOutType()))){//调整出库中除采购调整外 要求批次已经出库
								if (!Lot.POSITION_OUT.equals(lot.getPosition())) {
									throw new ClientParameterException("inv.lot_not_out", lot.getLotId());
								}
							}
							else if (!Lot.POSITION_INSTOCK.equals(lot.getPosition())) {
								//调整出库中的采购调整和一般的出库 这两种情况要求批次在仓库中
								throw new ClientParameterException("inv.lot_not_in", lot.getLotId());
							} 
						} 
						else {
							//调整的不需要检查库存
							if(!(MovementOut.DOCTYPE_AOU.equals(out.getDocType()) || MovementOut.DOCTYPE_ADOU.equals(out.getDocType()))){
								LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
								BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
	//							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
	//								throw new ClientException("inv.not_sufficient_quantity");
	//							}
								if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {//如果出库后库存减少 且是负数则不允许出库
									throw new ClientException("inv.not_sufficient_quantity");
								}
							}
						}

						lot.setOutId(out.getDocId());
						lot.setOutRrn(out.getObjectRrn());
						lot.setOutLineRrn(line.getObjectRrn());
						
						em.merge(lot);
						if(movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} 
						else {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}	
				}
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			out.setUpdatedBy(userRrn);
			
			em.merge(out);
			out.setMovementLines(savaLine);
			return out;

		} 
		catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		}
		catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/*
	 * 销售出库单保存
	 */
	public MovementLine saveSalesMovementOutLine(MovementOut out, MovementLine outLine , long userRrn) throws ClientException{
		List<MovementLine> list = new ArrayList<MovementLine>();
		list.add(outLine);
		try{
			out = saveSalesMovementOutLine(out, list, userRrn);
			return out.getMovementLines().get(0);
		}
		catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}		
	}
	/*
	 * 销售出库单保存
	 */
	public MovementOut saveSalesMovementOutLine(MovementOut out, List<MovementLine> lines, long userRrn) throws ClientException {
		try{
			//如果是新建的单子，那么先保存出库单到movement_out表中
			if (out.getObjectRrn() == null) {
				out.setIsActive(true);
				out.setCreatedBy(userRrn);
				out.setCreated(new Date());
				out.setDateCreated(new Date());
				out.setTotalLines(0L);
				out.setDocType(MovementOut.DOCTYPE_SOU);
				out.setDocStatus(MovementOut.STATUS_DRAFTED);
				String docId = out.getDocId();
				if (docId == null || docId.length() == 0) {
					out.setDocId(generateOutCode(out));
				} 
				else {
					String whereClause = " docId = '" + docId + "'";
					List<Movement> movements = adManager.getEntityList(out.getOrgRrn(), Movement.class, 1, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				ADUser user = em.find(ADUser.class, userRrn);
				out.setUserCreated(user.getUserName());
				em.persist(out);
			}
			
			if (out.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Warehouse house = em.find(Warehouse.class, out.getWarehouseRrn());
			out.setWarehouseId(house.getWarehouseId());
			
			List<MovementLine> savaLine = new ArrayList<MovementLine>();
			for (MovementLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getQtyMovement().doubleValue() == 0) {
					continue;
				}
				if (line.getUnitPrice() != null) {
					line.setLineTotal(line.getQtyMovement().multiply(line.getUnitPrice()));
				} 
				else{
					line.setLineTotal(null);
				}
				
				//如果是Update，则将原记录删除
				List<MovementLine> oldLineList = out.getMovementLines();
				if (line.getObjectRrn() != null) {
					//更新批次数据
					StringBuffer sql = new StringBuffer("");
					sql.append(" UPDATE WIP_LOT SET OUT_RRN = NULL, OUT_ID = NULL, OUT_LINE_RRN = NULL ");
					sql.append(" WHERE  OUT_LINE_RRN = ? ");
					Query query = em.createNativeQuery(sql.toString());
					query.setParameter(1, line.getObjectRrn());
					query.executeUpdate();
					
					//删除LineLot
//					sql = new StringBuffer("");
//					sql.append(" SELECT MovementLineLot FROM MovementLineLot MovementLineLot ");
//					sql.append(" WHERE  movementLineRrn = ? ");
//					query = em.createQuery(sql.toString());
//					query.setParameter(1, line.getObjectRrn());
//					List<MovementLineLot> tempLineLot = query.getResultList();
//					if(tempLineLot.size() >= 1 ){
						sql = new StringBuffer("");
						sql.append(" DELETE FROM MovementLineLot MovementLineLot ");
						sql.append(" WHERE  movementLineRrn = ? ");
						query = em.createQuery(sql.toString());
						query.setParameter(1, line.getObjectRrn());
						query.executeUpdate();		
//					}
					MovementLine oldLine = em.find(MovementLine.class, line.getObjectRrn());
					em.remove(oldLine);
					em.flush();
					
					oldLineList.remove(line);
					out.setMovementLines(oldLineList);
					line.setObjectRrn(null);
				}
				else{
					out.setTotalLines(out.getTotalLines() + 1);
				}
				//创建新行
				line.setIsActive(true);
				line.setCreatedBy(userRrn);
				line.setCreated(new Date());
				line.setLineStatus(MovementOut.STATUS_DRAFTED);
				line.setMovementRrn(out.getObjectRrn());
				line.setMovementId(out.getDocId());				
				em.persist(line);
				
				List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					for (MovementLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} 
						else {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				else if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
						//对于Serial类型的物料,在两个地方进行检查,
						//1,在界面上输入条码时
						//2,在Approve时
					    //为了性能考虑在保存时不在在此进行检查
						String sqlLot = null;
						String sqlLineLot = null;
						for (MovementLineLot movementLot : movementLots) {
							if (movementLot.getObjectRrn() == null) {
								movementLot.setMovementRrn(out.getObjectRrn());
								movementLot.setMovementId(out.getDocId());
								movementLot.setMovementLineRrn(line.getObjectRrn());
								em.persist(movementLot);						
							} 
							else {
								if (sqlLineLot == null) {
									sqlLineLot = "" + movementLot.getObjectRrn();
								} 
								else {
									sqlLineLot += ", " + movementLot.getObjectRrn();
								}
							}
							if (sqlLot == null) {
								sqlLot = "" + movementLot.getLotRrn();
							} 
							else {
								sqlLot += ", " + movementLot.getLotRrn();
							}
						}
						//更新Lot信息
						StringBuffer sql = new StringBuffer("");
						sql.append(" UPDATE WIP_LOT SET OUT_RRN = ?, OUT_ID = ?, OUT_LINE_RRN = ? ");
						sql.append(" WHERE ");
						sql.append(getSqlIn(sqlLot, "OBJECT_RRN"));
						Query query = em.createNativeQuery(sql.toString());
						query.setParameter(1, out.getObjectRrn());
						query.setParameter(2, out.getDocId());
						query.setParameter(3, line.getObjectRrn());
						query.executeUpdate();
						
						if (sqlLineLot != null) {//不是新插入的批次，则更新批次信息
							//更新LineLot信息
							sql = new StringBuffer("");
							sql.append(" UPDATE INV_MOVEMENT_LINE_LOT SET MOVEMENT_RRN = ?, MOVEMENT_ID = ?, MOVEMENT_LINE_RRN = ? ");
							sql.append(" WHERE ");
							sql.append(getSqlIn(sqlLineLot, "OBJECT_RRN"));
							query = em.createNativeQuery(sql.toString());
							query.setParameter(1, out.getObjectRrn());
							query.setParameter(2, out.getDocId());
							query.setParameter(3, line.getObjectRrn());
							query.executeUpdate();
						}
				} 
				else {
					//对于Batch类型的物料
					for (MovementLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());						
						if (lot.getIsUsed()) {
							throw new ClientParameterException("inv.lot_already_used", lot.getLotId());
						}
						LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
						BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
						if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {//如果出库后库存减少 且是负数则不允许出库
							throw new ClientException("inv.not_sufficient_quantity");
						}
						lot.setOutId(out.getDocId());
						lot.setOutRrn(out.getObjectRrn());
						lot.setOutLineRrn(line.getObjectRrn());
						em.merge(lot);
						
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} 
						else {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			out.setUpdatedBy(userRrn);
			
			em.merge(out);
			out.getMovementLines().addAll(savaLine);
			return out;
		} 
		catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} 
		catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/**
	 * 将长度多于１０００的in 语句分割成多个小于１０００的in语句
	 * @param sqlParam
	 * @param columnName
	 * @return
	 */
	private String getSqlIn( String sqlParam, String columnName ){
		String[] vals = sqlParam.split(",");
		StringBuffer sb = new StringBuffer();
		boolean loop = true;
		int j = 0;
		while(loop){
			loop = false;
			String temp = "";
			for(int i= j*999; i < (j+1)*999 && i < vals.length; i++){
				loop = true;
				if(!"".equals(temp)){
					temp = temp +",";
				}
				temp = temp+vals[i];
			}
			if(loop){
				if(j == 0){
					sb.append(columnName + " in (" + temp + ") ");
				}else{
					sb.append(" OR " + columnName + " in (" + temp + ") ");
				}
			}
			j++;
		}
		
		return sb.toString();
    } 
	
	public void deleteMovementOut(MovementOut out, long userRrn)throws ClientException {
		try{
			if(out != null && out.getObjectRrn() != null) {
				out = em.find(MovementOut.class, out.getObjectRrn());
				for (int i=0; i< out.getMovementLines().size(); i++){
					MovementLine line= out.getMovementLines().get(i);
					deleteMovementOutLine(line, true, userRrn);
				}
				em.remove(out);
				
				MovementHis his = new MovementHis(out);
				his.setCreatedBy(userRrn);
				his.setUpdatedBy(userRrn);
				his.setActionType(MovementHis.ACTION_TYPE_DELETE);
				his.setDateAction(new Date());
				em.persist(his);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteMovementOutLine(MovementLine movementLine, long userRrn)throws ClientException {
		deleteMovementOutLine(movementLine, false, userRrn);
	}
	
	public void deleteMovementOutLine(MovementLine movementLine, boolean allFlag, long userRrn)throws ClientException {
		try {
			if(movementLine != null && movementLine.getObjectRrn() != null) {
				movementLine = em.find(MovementLine.class, movementLine.getObjectRrn());
				// 更新movement
				Movement out = em.find(Movement.class, movementLine.getMovementRrn());		
				if (!allFlag) {
					out.setTotalLines(out.getTotalLines() - 1);
					em.merge(out);
				}

				StringBuffer sql = new StringBuffer("");
				sql.append(" SELECT MovementLineLot FROM MovementLineLot MovementLineLot ");
				sql.append(" WHERE  movementLineRrn = ? ");
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, movementLine.getObjectRrn());
				List<MovementLineLot> movementLots = query.getResultList();
				for (MovementLineLot movementLot : movementLots) {
					Lot lot = em.find(Lot.class, movementLot.getLotRrn());
					lot.setOutId(null);
					lot.setOutRrn(null);
					lot.setOutLineRrn(null);
					lot.setUpdatedBy(userRrn);
					em.merge(lot);
					em.remove(movementLot);
				}
				em.remove(movementLine);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementOut approveMovementOut(MovementOut out, MovementOut.OutType outType, long userRrn) throws ClientException {
		return approveMovementOut(out, outType, userRrn, true);
	}
	
	public MovementOut approveMovementOut(MovementOut out, MovementOut.OutType outType, long userRrn, boolean isWriteOff) throws ClientException {
		return approveMovementOut(out, outType, userRrn, true, isWriteOff);
	}
	
	//isOnHand是否改变营运库存 isWriteOff是否改变财务库存
	public MovementOut approveMovementOut(MovementOut out, MovementOut.OutType outType, long userRrn, boolean isOnHand, boolean isWriteOff) throws ClientException {
		try{
			if (out.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Long warehouseRrn = out.getWarehouseRrn();
			//得到审核人
			ADUser user = em.find(ADUser.class, userRrn);
			out.setUserApproved(user.getUserName());
			//得到仓库
			Warehouse house = em.find(Warehouse.class, warehouseRrn);
			out.setWarehouseId(house.getWarehouseId());
			
			out.setDocStatus(MovementOut.STATUS_APPROVED);
			out.setUpdatedBy(userRrn);
			out.setDateApproved(new Date());
			em.merge(out);
			
			if (out.getMovementLines().size() == 0) {
				throw new ClientException("inv.out_quantity_zero"); 
			}
			
			Date dateOut = new Date();
			long transSeq = basManager.getHisSequence();
			for (MovementLine line : out.getMovementLines()) {
				//得到出库单行的物料信息
				Material material = em.find(Material.class, line.getMaterialRrn());

				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(PurchaseOrder.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				//更新库存
				updateStorage(out.getOrgRrn(), line.getMaterialRrn(), out.getWarehouseRrn(), line.getQtyMovement().negate(), isOnHand, isWriteOff, userRrn);
				if(warehouseRrn.equals(151046L)&&"返修出库".equals(out.getOutType())){
					//范总采购部库
					updateWorkShopStorage(out.getOrgRrn(), line.getMaterialRrn(), 92175029L, line.getQtyMovement(), false, userRrn);
				}
				
				//改变营运数时才要涉及条码
				if(isOnHand){
					//条码的处理。。
					if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
						Lot lot = this.getMaterialLot(out.getOrgRrn(), material, userRrn);
						LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
						BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());
						//调整单不检查库存
						if(!(MovementOut.OutType.AOU.equals(outType) || MovementOut.OutType.ADOU.equals(outType))){
							if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientParameterException("inv.not_sufficient_quantity",material.getMaterialId());
							}
						}
						
						this.updateLotStorage(out.getOrgRrn(), lot.getObjectRrn(), warehouseRrn, line.getQtyMovement().negate(), userRrn);
						if("SOU".equals(out.getDocType()) || "OOU".equals(out.getDocType())){
							if(out.getOrgRrn().equals(139420L) && out.getWmsWarehouse()!=null && !"".equals(out.getWmsWarehouse())){
								//wms出库
								StockOut stockOut = new StockOut();
								stockOut.setOrgRrn(out.getOrgRrn());
								stockOut.setIsActive(true);
								stockOut.setReceiptId(out.getDocId());
								stockOut.setReceiptTime(new Date());
								stockOut.setReceiptType(out.getDocType());
								stockOut.setMaterialCode(lot.getMaterialId());
								stockOut.setBatch(lot.getLotId());
								stockOut.setQuality(line.getQtyMovement());
								stockOut.setErpWrite(1L);
								stockOut.setErpWriteTime(new Date());
								stockOut.setWmsRead(0L);
//								stockOut.setSupplierName();
								stockOut.setUserName(out.getUserCreated());
								em.persist(stockOut);
							}
						}
					} 
					else {
						//检查Lot的数量与Line中的数量是否相等
						String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
						List<MovementLineLot> movementLots = adManager.getEntityList(out.getOrgRrn(), MovementLineLot.class, Integer.MAX_VALUE, whereClause, "");
						BigDecimal qtyLine = line.getQtyMovement();
						BigDecimal qtyTotal = BigDecimal.ZERO;
						for (MovementLineLot movementLot : movementLots) {
							qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
						}
						if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
							throw new ClientException("inv.out_lot_qty_different");
						}
						
						for (MovementLineLot movementLot : movementLots) {
							Lot lot = em.find(Lot.class, movementLot.getLotRrn());
							LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
							if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
								if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
									throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
								}
							} else {
								//调整单不检查库存
								if(!(MovementOut.OutType.AOU.equals(outType) || MovementOut.OutType.ADOU.equals(outType))){
									BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
									if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
										throw new ClientParameterException("inv.not_sufficient_quantity",material.getMaterialId());
									}
								}
							}
							this.updateLotStorage(out.getOrgRrn(), movementLot.getLotRrn(), warehouseRrn, movementLot.getQtyMovement().negate(), userRrn);
							
							if (Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {//如果是serial类型的批次需将position改为instock
								if(MovementOut.OutType.AOU.equals(outType) || MovementOut.OutType.ADOU.equals(outType)){
									if(MovementOut.OUT_TYPE_OUT_ADJUST.equals(out.getOutType()) || MovementOut.OUT_TYPE_SALE_ADJUST.equals(out.getOutType())){//如果是调整出库且是出库调整将position置为INSTOCK
										lot.setDateIn(dateOut);//出库调整相当于入库
										lot.setPosition(Lot.POSITION_INSTOCK);
									}else if(MovementOut.OUT_TYPE_IN_ADJUST.equals(out.getOutType())){//如果是调整出库且是入库调整将position置为OUT
										lot.setDateOut(dateOut);//采购调整相当于出库
										lot.setPosition(Lot.POSITION_OUT);
									}
								}else{
									lot.setDateOut(dateOut);
									lot.setPosition(Lot.POSITION_OUT);
								}
							}
							
							em.merge(lot);
							
							LotHis his = null;
							switch (outType) {
							case SOU:
								his = new SouLotHis(lot);
								if(out.getOrgRrn().equals(139420L) && out.getWmsWarehouse()!=null && !"".equals(out.getWmsWarehouse())){
									//wms出库
									StockOut stockOut = new StockOut();
									stockOut.setOrgRrn(out.getOrgRrn());
									stockOut.setIsActive(true);
									stockOut.setReceiptId(out.getDocId());
									stockOut.setReceiptTime(new Date());
									stockOut.setReceiptType(out.getDocType());
									stockOut.setMaterialCode(movementLot.getMaterialId());
									stockOut.setBatch(movementLot.getLotId());
									stockOut.setQuality(movementLot.getQtyMovement());
									stockOut.setErpWrite(1L);
									stockOut.setErpWriteTime(new Date());
									stockOut.setWmsRead(0L);
//									stockOut.setSupplierName();
									stockOut.setUserName(out.getUserCreated());
									em.persist(stockOut);
								}
								break;
							case OOU:
								his = new OouLotHis(lot);
								if(out.getOrgRrn().equals(139420L) && out.getWmsWarehouse()!=null && !"".equals(out.getWmsWarehouse())){
									//wms出库
									StockOut stockOut = new StockOut();
									stockOut.setOrgRrn(out.getOrgRrn());
									stockOut.setIsActive(true);
									stockOut.setReceiptId(out.getDocId());
									stockOut.setReceiptTime(new Date());
									stockOut.setReceiptType(out.getDocType());
									stockOut.setMaterialCode(movementLot.getMaterialId());
									stockOut.setBatch(movementLot.getLotId());
									stockOut.setQuality(movementLot.getQtyMovement());
									stockOut.setErpWrite(1L);
									stockOut.setErpWriteTime(new Date());
									stockOut.setWmsRead(0L);
//									stockOut.setSupplierName();
									stockOut.setUserName(out.getUserCreated());
									em.persist(stockOut);
								}
								break;
							case AOU:
								his = new AouLotHis(lot);
								break;
							case ADOU:
								his = new AdouLotHis(lot);
								break;
							case DOU:
								his = new DouLotHis(lot);
								break;
							}
							if (his != null) {
								his.setHisSeq(transSeq);
								em.persist(his);
							}
						}
					}
				}
			}
			
			if (MovementOut.OutType.SOU == outType) {//销售出库
				try {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String approveDate = df.format(out.getDateApproved());
					//设置crm销货单中的出库单号
					salManager.approveSo(out.getSoId(), out.getObjectRrn(), out.getDocId(), approveDate);
				} 
				catch (Exception e) {
					throw new ClientException(e);
				}
				for (MovementLine line : out.getMovementLines()) {
					String whereClause = " materialId = '" + line.getMaterialId() + "'";
					List<SalesOrderSum> soSums = adManager.getEntityList(out.getOrgRrn(), SalesOrderSum.class, Integer.MAX_VALUE, whereClause, "");
					if (soSums.size() > 0) {
						SalesOrderSum soSum = soSums.get(0);
						soSum.setQtySo(soSum.getQtySo().subtract(line.getQtyMovement()));
						em.merge(soSum);
					}
				}
			}
			else if(MovementOut.OutType.AOU == outType && MovementOut.OUT_TYPE_SALE_ADJUST.equals(out.getOutType())){//销售调整

				try {
					if(out.getSoId() != null && out.getSoId().trim().length() > 0){
						//如果是销售调整，就把调整单编号记到crm里去
						salManager.adjustSo(out.getSoId(), out.getObjectRrn(), out.getDocId(), null);
					}
				} 
				catch (Exception e) { 
					throw new ClientException(e);
				}
//				for (MovementLine line : out.getMovementLines()) {
//					String whereClause = " materialId = '" + line.getMaterialId() + "'";
//					List<SalesOrderSum> soSums = adManager.getEntityList(out.getOrgRrn(), SalesOrderSum.class, Integer.MAX_VALUE, whereClause, "");
//					if (soSums.size() > 0) {
//						SalesOrderSum soSum = soSums.get(0);
//						soSum.setQtySo(soSum.getQtySo().add(line.getQtyMovement()));
//						em.merge(soSum);
//					}
//				}
			}
			
			return out;
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} 
		catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//isOnHand是否改变营运库存 isWriteOff是否改变财务库存
	public MovementOut approveDevelopMovementOut(MovementOut out, long userRrn, boolean isOnHand, boolean isWriteOff) throws ClientException {
		try{
			if (out.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Long warehouseRrn = out.getWarehouseRrn();
			//得到审核人
			ADUser user = em.find(ADUser.class, userRrn);
			out.setUserApproved(user.getUserName());
			//得到仓库
			Warehouse house = em.find(Warehouse.class, warehouseRrn);
			out.setWarehouseId(house.getWarehouseId());
			
			out.setDocStatus(MovementOut.STATUS_APPROVED);
			out.setUpdatedBy(userRrn);
			out.setDateApproved(new Date());
			em.merge(out);
			
			if (out.getMovementLines().size() == 0) {
				 new ClientException("inv.out_quantity_zero"); 
			}
			
			for (MovementLine line : out.getMovementLines()) {
				String whereClause1 = " movementLineRrn = '" + line.getObjectRrn() + "'";
				List<MovementLineLot> movementLineLots = adManager.getEntityList(line.getOrgRrn(),MovementLineLot.class, Integer.MAX_VALUE, whereClause1, "");
				if(movementLineLots.size()==0){
					throw new ClientException("出库单行没有批次");
				}
				//得到出库单行的物料信息
				Material material = em.find(Material.class, line.getMaterialRrn());

				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(PurchaseOrder.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				List<Lot> lista = addlist(line);
				List<Lot> listc = panduan(lista);
				for(Lot lot:listc){
					//更新库存
					updateStorage(out.getOrgRrn(), lot.getMaterialRrn(), out.getWarehouseRrn(), lot.getQtyCurrent().negate(), false, isWriteOff, userRrn);
					}
				updateStorage(out.getOrgRrn(), line.getMaterialRrn(), out.getWarehouseRrn(), line.getQtyMovement().negate(), isOnHand, false, userRrn);
				
				//条码的处理。。
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					Lot lot = this.getMaterialLot(out.getOrgRrn(), material, userRrn);
					LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
					BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());
					if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
						throw new ClientException("inv.not_sufficient_quantity");
					}
					this.updateLotStorage(out.getOrgRrn(), lot.getObjectRrn(), warehouseRrn, line.getQtyMovement().negate(), userRrn);
				} 
				else {
					//检查Lot的数量与Line中的数量是否相等
					String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
					List<MovementLineLot> movementLots = adManager.getEntityList(out.getOrgRrn(), MovementLineLot.class, Integer.MAX_VALUE, whereClause, "");
					BigDecimal qtyLine = line.getQtyMovement();
					BigDecimal qtyTotal = BigDecimal.ZERO;
					for (MovementLineLot movementLot : movementLots) {
						qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
					}
					if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
						throw new ClientException("inv.out_lot_qty_different");
					}
					
					for (MovementLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
							}
						} else {
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						this.updateLotStorage(out.getOrgRrn(), movementLot.getLotRrn(), warehouseRrn, movementLot.getQtyMovement().negate(), userRrn);
						
						em.merge(lot);
					}
				}
				}
					
					return out;
				}
				catch (OptimisticLockException e){
					logger.error(e.getMessage(), e);
					throw new ClientException("error.optimistic_lock");
				} 
				catch (Exception e){ 
					logger.error(e.getMessage(), e);
					throw new ClientException(e);
				}
			}
	
	public List<Lot> addlist(MovementLine line) throws ClientException {
		List<Lot> lista = new ArrayList<Lot>();
		Lot lot = new Lot();
		try{
			String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
			List<MovementLineLot> movementLineLots = adManager.getEntityList(line.getOrgRrn(),MovementLineLot.class, Integer.MAX_VALUE, whereClause, "");
			for(MovementLineLot movementLineLot:movementLineLots){
				lot.setMaterialRrn(movementLineLot.getMaterialRrn());
				lot.setLotId(movementLineLot.getLotId());
				lot.setQtyCurrent(movementLineLot.getQtyMovement());
				lista.add(lot);
			}
		return lista;
		}
		
			catch (Exception e){ 
				logger.error(e.getMessage(), e);
				throw new ClientException(e);
			}}
	
	public List<Lot> panduan(List<Lot> list) throws ClientException {
		try{
			List<Lot> listb = null;
			List<Lot> listc = new ArrayList<Lot>();
			//判断采购或生产
			for (Lot lot : list) {
				if(lot.getMoLineRrn() == null){// 采购
					listc.add(lot);
				} else {
					BigDecimal qty = lot.getQtyCurrent();
					listb = schy(lot, qty);
					listc.addAll(panduan(listb));
				}
			}
			return listc;
		}catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Lot> schy(Lot lot, BigDecimal qty) throws ClientException {
		try{ 
			String lotId = lot.getLotId();
			StringBuffer sql = new StringBuffer("");
			List<Lot> listb = null;
			sql.append( "select * from wip_lot_component t where t.lot_parent_id = ？");			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, lotId);
			List<LotComponent> lotscomponent = query.getResultList();
			for(LotComponent lotComponent:lotscomponent){
				Lot lotadd = new Lot();
				lotadd.setMaterialId(lotComponent.getMaterialChildId());
				lotadd.setLotId(lotComponent.getLotChildId());
				lotadd.setQtyCurrent((lotComponent.getQtyConsume()).multiply(qty));
				listb.add(lotadd);
			}
		return listb;
		}
		catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}}
	
	/*
	 * 专用于销售出库单的审核
	 */
	public MovementOut approveSalesMovementOut(MovementOut out, long userRrn, boolean isWriteOff) throws ClientException {
		try{
			if (out.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Long warehouseRrn = out.getWarehouseRrn();
			//得到审核人
			ADUser user = em.find(ADUser.class, userRrn);
			out.setUserApproved(user.getUserName());
			//得到仓库
			Warehouse house = em.find(Warehouse.class, warehouseRrn);
			out.setWarehouseId(house.getWarehouseId());
			
			out.setDocStatus(MovementOut.STATUS_APPROVED);
			out.setUpdatedBy(userRrn);
			out.setDateApproved(new Date());
			em.merge(out);
			
			if (out.getMovementLines().size() == 0) {
				throw new ClientException("inv.out_quantity_zero"); 
			}
			
			long transSeq = basManager.getHisSequence();
			for (MovementLine line : out.getMovementLines()) {
				//得到出库单行的物料信息
				Material material = em.find(Material.class, line.getMaterialRrn());

				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(PurchaseOrder.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				//更新物料库存
				updateStorage(out.getOrgRrn(), line.getMaterialRrn(), out.getWarehouseRrn(), line.getQtyMovement().negate(), isWriteOff, userRrn);
				if(out.getIsServicesOut()){
					updateServicesStorage(out.getOrgRrn(), line.getMaterialRrn(), out.getWarehouseRrn(), line.getQtyMovement().negate(),true, false, userRrn);
				}
				//条码的处理。。
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					Lot lot = this.getMaterialLot(out.getOrgRrn(), material, userRrn);
					LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
					BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());
					if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
						throw new ClientParameterException("inv.not_sufficient_quantity",material.getMaterialId());
					}
					this.updateLotStorage(out.getOrgRrn(), lot.getObjectRrn(), warehouseRrn, line.getQtyMovement().negate(), userRrn);
					if(out.getOrgRrn().equals(139420L) && out.getWmsWarehouse()!=null && !"".equals(out.getWmsWarehouse())){
						//wms出库
						StockOut stockOut = new StockOut();
						stockOut.setOrgRrn(out.getOrgRrn());
						stockOut.setIsActive(true);
						stockOut.setReceiptId(out.getDocId());
						stockOut.setReceiptTime(new Date());
						stockOut.setReceiptType(out.getDocType());
						stockOut.setMaterialCode(lot.getMaterialId());
						stockOut.setBatch(lot.getLotId());
						stockOut.setQuality(line.getQtyMovement());
						stockOut.setErpWrite(1L);
						stockOut.setErpWriteTime(new Date());
						stockOut.setWmsRead(0L);
//						stockOut.setSupplierName();
						stockOut.setUserName(out.getUserCreated());
						em.persist(stockOut);
					}
				} 
				else {
					//检查Lot的数量与Line中的数量是否相等
					String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
					List<MovementLineLot> movementLots = adManager.getEntityList(out.getOrgRrn(), MovementLineLot.class, Integer.MAX_VALUE, whereClause, "");
					BigDecimal qtyLine = line.getQtyMovement();
					BigDecimal qtyTotal = BigDecimal.ZERO;
					for (MovementLineLot movementLot : movementLots) {
						qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
						if(out.getOrgRrn().equals(139420L) && out.getWmsWarehouse()!=null && !"".equals(out.getWmsWarehouse())){
							//wms出库
							StockOut stockOut = new StockOut();
							stockOut.setOrgRrn(out.getOrgRrn());
							stockOut.setIsActive(true);
							stockOut.setReceiptId(out.getDocId());
							stockOut.setReceiptTime(new Date());
							stockOut.setReceiptType(out.getDocType());
							stockOut.setMaterialCode(movementLot.getMaterialId());
							stockOut.setBatch(movementLot.getLotId());
							stockOut.setQuality(movementLot.getQtyMovement());
							stockOut.setErpWrite(1L);
							stockOut.setErpWriteTime(new Date());
							stockOut.setWmsRead(0L);
//							stockOut.setSupplierName();
							stockOut.setUserName(out.getUserCreated());
							em.persist(stockOut);
						}
					}
					if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
						throw new ClientParameterException("inv.out_lot_qty_different",material.getMaterialId());
					}
					
					String newPosistion = Lot.POSITION_OUT;//对于serial类型 的出库后position变为OUT,对于batch类型的不改变position
					
					if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
						//对物料进行检查,判断是否在仓库中
						StringBuffer sql = new StringBuffer("");
						sql.append(" SELECT Lot FROM Lot Lot, MovementLineLot LineLot ");
						sql.append(" WHERE Lot.objectRrn = LineLot.lotRrn ");
						sql.append(" AND LineLot.movementLineRrn = ? "); 
						sql.append(" AND (Lot.warehouseRrn != ? ");
						sql.append(" OR Lot.position != '");
						sql.append(Lot.POSITION_INSTOCK);
						sql.append("')");
						Query query = em.createQuery(sql.toString());
						query.setParameter(1, line.getObjectRrn());
						query.setParameter(2, house.getObjectRrn());
						query.setMaxResults(1);
						List<Lot> lots = query.getResultList();
						if (lots.size() > 0) {
							throw new ClientParameterException("inv.lot_not_in_warehouse", lots.get(0).getLotId(), house.getWarehouseId());
						}
					} 
					else {
						//对于Batch类型物料,检查数量是否足够
						for (MovementLineLot movementLot : movementLots) {
							Lot lot = em.find(Lot.class, movementLot.getLotRrn());
							LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientParameterException("inv.not_sufficient_quantity",material.getMaterialId());
							}
							
							newPosistion = lot.getPosition();//Batch类型不改变批次position
						}
					}
					
					//更新批次库存(重点测试)
					StringBuffer sql = new StringBuffer("");
					sql.append(" UPDATE INV_LOT_STORAGE S ");
					sql.append(" SET (S.UPDATED_BY, S.QTY_ONHAND) = ");
					sql.append(" (SELECT 1, S.QTY_ONHAND - M.QTY_MOVEMENT FROM INV_MOVEMENT_LINE_LOT M WHERE M.LOT_RRN = S.LOT_RRN  ");
					sql.append(" AND M.MOVEMENT_LINE_RRN = ?) ");
					sql.append(" WHERE S.WAREHOUSE_RRN = ? ");
					sql.append(" AND S.LOT_RRN = (SELECT K.LOT_RRN FROM INV_MOVEMENT_LINE_LOT K WHERE K.MOVEMENT_LINE_RRN = ? AND K.LOT_RRN = S.LOT_RRN) ");
					Query query = em.createNativeQuery(sql.toString());
					query.setParameter(1, line.getObjectRrn());
					query.setParameter(2, warehouseRrn);
					query.setParameter(3, line.getObjectRrn());
					query.executeUpdate();
										
					//更新批次信息
					sql = new StringBuffer("");
					sql.append(" UPDATE WIP_LOT SET DATE_OUT = SYSDATE, POSITION = ? ");
					sql.append(" WHERE OUT_LINE_RRN = ? ");
					query = em.createNativeQuery(sql.toString());
					query.setParameter(1, newPosistion);
					query.setParameter(2, line.getObjectRrn());
					query.executeUpdate();
										
					//保存批次历史		
					sql = new StringBuffer("");
					sql.append(" INSERT INTO WIPHIS_LOT(");
					sql.append(" TRANS_TYPE,OBJECT_RRN,LOT_RRN,LOT_ID,HISTORY_SEQ,ACTION_CODE,ACTION_COMMENT,ACTION_REASON,");
					sql.append(" COM_CLASS,CREATE_TIME,CREATED,CREATED_BY,CUSTOMER_LOT_ID,CUSTOMER_NAME,CUSTOMER_ORDER,CUSTOMER_PART_ID,");
					sql.append(" DATE_IN,DATE_OUT,DATE_PRODUCT,DESCRIPTION,DUE_DATE,END_MAIN_QTY,END_SUB_QTY,END_TIME,ENGINEER,");
					sql.append(" EQUIPMENT_ID,EQUIPMENT_RRN,IN_ID,IN_LINE_RRN,IN_RRN,IQC_ID,IQC_LINE_RRN,IQC_RRN,IS_ACTIVE,IS_USED,");
					sql.append(" LOCATION,LOCATOR_ID,LOCATOR_RRN,LOCK_VERSION,LOT_COMMENT,LOT_TYPE,MAIN_QTY,MATERIAL_ID,MATERIAL_NAME,");
					sql.append(" MATERIAL_RRN,MO_ID,MO_LINE_RRN,MO_RRN,MOLD_ID,OPERATOR_NAME,OPERATOR_RRN,ORG_RRN,OUT_ID,OUT_LINE_RRN,OUT_RRN,");
					sql.append(" PARENT_LOT_RRN,PARENT_UNIT_RRN,PART_NAME,PART_RRN,PART_TYPE,PART_VERSION,PLAN_START_DATE,PO_ID,PO_LINE_RRN,");
					sql.append(" PO_RRN,POSITION,PRE_COM_CLASS,PRE_STATE,PRE_STATE_ENTRY_TIME,PRE_SUB_STATE,PRE_TRANS_TYPE,PRIORITY,");
					sql.append(" PROCEDURE_NAME,PROCEDURE_RRN,PROCEDURE_VERSION,QTY_CURRENT,QTY_INITIAL,QTY_TRANSACTION,QTY_USED,");
					sql.append(" RECEIPT_ID,RECEIPT_RRN,REQUITED_DATE,REVERSE_FIELD1,REVERSE_FIELD10,REVERSE_FIELD2,REVERSE_FIELD3,REVERSE_FIELD4,");
					sql.append(" REVERSE_FIELD5,REVERSE_FIELD6,REVERSE_FIELD7,REVERSE_FIELD8,REVERSE_FIELD9,START_MAIN_QTY,START_SUB_QTY,");
					sql.append(" START_TIME,STATE,STATE_ENTRY_TIME,STEP_NAME,STEP_RRN,STEP_VERSION,SUB_QTY,SUB_STATE,SUB_UNIT_TYPE,");
					sql.append(" TRACK_IN_TIME,TRACK_OUT_TIME,UPDATED,UPDATED_BY,USED_LOT_RRN,USER_QC,WAREHOUSE_ID,WAREHOUSE_RRN,WORKCENTER_ID,WORKCENTER_RRN ");

					sql.append(" ) SELECT 'SOU',OBJECT_RRN.NEXTVAL,L.OBJECT_RRN,L.LOT_ID," + transSeq + ",NULL,NULL,NULL,");
					sql.append(" L.COM_CLASS,SYSDATE,NULL,NULL,L.CUSTOMER_LOT_ID,L.CUSTOMER_NAME,L.CUSTOMER_ORDER,L.CUSTOMER_PART_ID,");
					sql.append(" L.DATE_IN,L.DATE_OUT,L.DATE_PRODUCT,L.DESCRIPTION,L.DUE_DATE,L.END_MAIN_QTY,L.END_SUB_QTY,L.END_TIME,L.ENGINEER,");
					sql.append(" L.EQUIPMENT_ID,L.EQUIPMENT_RRN,L.IN_ID,L.IN_LINE_RRN,L.IN_RRN,L.IQC_ID,L.IQC_LINE_RRN,L.IQC_RRN,L.IS_ACTIVE,L.IS_USED,");
					sql.append(" L.LOCATION,L.LOCATOR_ID,L.LOCATOR_RRN,L.LOCK_VERSION,L.LOT_COMMENT,L.LOT_TYPE,L.MAIN_QTY,L.MATERIAL_ID,L.MATERIAL_NAME,");
					sql.append(" L.MATERIAL_RRN,L.MO_ID,L.MO_LINE_RRN,L.MO_RRN,L.MOLD_ID,L.OPERATOR_NAME,L.OPERATOR_RRN,L.ORG_RRN,L.OUT_ID,L.OUT_LINE_RRN,L.OUT_RRN,");
					sql.append(" L.PARENT_LOT_RRN,L.PARENT_UNIT_RRN,L.PART_NAME,L.PART_RRN,L.PART_TYPE,L.PART_VERSION,L.PLAN_START_DATE,L.PO_ID,L.PO_LINE_RRN,");
					sql.append(" L.PO_RRN,L.POSITION,L.PRE_COM_CLASS,L.PRE_STATE,L.PRE_STATE_ENTRY_TIME,L.PRE_SUB_STATE,L.PRE_TRANS_TYPE,L.PRIORITY,");
					sql.append(" L.PROCEDURE_NAME,L.PROCEDURE_RRN,L.PROCEDURE_VERSION,L.QTY_CURRENT,L.QTY_INITIAL,L.QTY_WAITINGIN,NULL,");
					sql.append(" L.RECEIPT_ID,L.RECEIPT_RRN,L.REQUITED_DATE,L.REVERSE_FIELD1,L.REVERSE_FIELD10,L.REVERSE_FIELD2,L.REVERSE_FIELD3,L.REVERSE_FIELD4,");
					sql.append(" L.REVERSE_FIELD5,L.REVERSE_FIELD6,L.REVERSE_FIELD7,L.REVERSE_FIELD8,L.REVERSE_FIELD9,L.START_MAIN_QTY,L.START_SUB_QTY,");
					sql.append(" L.START_TIME,L.STATE,L.STATE_ENTRY_TIME,L.STEP_NAME,L.STEP_RRN,L.STEP_VERSION,L.SUB_QTY,L.SUB_STATE,L.SUB_UNIT_TYPE,");
					sql.append(" L.TRACK_IN_TIME,L.TRACK_OUT_TIME,NULL,L.UPDATED_BY,L.USED_LOT_RRN,L.USER_QC,L.WAREHOUSE_ID,L.WAREHOUSE_RRN,L.WORKCENTER_ID,L.WORKCENTER_RRN ");
					sql.append(" FROM WIP_LOT L ");
					sql.append(" WHERE L.OUT_LINE_RRN = ? ");
					query = em.createNativeQuery(sql.toString());
					query.setParameter(1, line.getObjectRrn());
					query.executeUpdate();
				}				
				String whereClause = " materialId = '" + line.getMaterialId() + "'";
				List<SalesOrderSum> soSums = adManager.getEntityList(out.getOrgRrn(), SalesOrderSum.class, Integer.MAX_VALUE, whereClause, "");
				if (soSums.size() > 0) {
					SalesOrderSum soSum = soSums.get(0);
					soSum.setQtySo(soSum.getQtySo().subtract(line.getQtyMovement()));
					em.merge(soSum);
				}
			}
			
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String approveDate = df.format(out.getDateApproved());
				//设置crm销货单中的出库单号
				salManager.approveSo(out.getSoId(), out.getObjectRrn(), out.getDocId(), approveDate);
			} 
			catch (Exception e) {
				throw new ClientException(e);
			}
			return out;
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} 
		catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//onHandFlag=true计算营运库存，writeOffFlag=true计算核销数量
	public void updateStorage(long orgRrn, long materialRrn, long warehouseRrn, BigDecimal qty, boolean onHandFlag, boolean writeOffFlag, long userRrn) throws ClientException {
		try{
			Storage storage = getMaterialStorage(orgRrn, materialRrn, warehouseRrn, userRrn);
			if(onHandFlag){
				storage.setQtyOnhand(storage.getQtyOnhand().add(qty));//修改营运库存
			}
			if (writeOffFlag) {
				BigDecimal newQtyOnhand = storage.getQtyWriteOff().add(qty);
				//如果需要修改核销数需要判断一下财务库存是否够，如果不够不允许
				if(newQtyOnhand.compareTo(storage.getQtyWriteOff()) < 0 && newQtyOnhand.compareTo(BigDecimal.ZERO) < 0){
					throw new RuntimeException(storage.getMaterialId() + " WriteOff qty is not enough! ");
				}
				storage.setQtyWriteOff(newQtyOnhand);//修改核销库存
			}
			storage.setUpdatedBy(userRrn);
			em.merge(storage);
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} 
		catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//writeOffFlag=true核销数量增加，否则不变
	public void updateStorage(long orgRrn, long materialRrn, long warehouseRrn, BigDecimal qty, boolean writeOffFlag, long userRrn) throws ClientException {
		updateStorage(orgRrn, materialRrn, warehouseRrn, qty, true, writeOffFlag, userRrn);
	}
	
	//核销工作令(未使用)
	private void updateWriteOff(long orgRrn, long lotRrn, long warehouseRrn, boolean writeOffFlag, long userRrn) throws ClientException {
		try{
			List<LotConsume> lotConsumes = getMaterialConsume(lotRrn);
			for (LotConsume lotConsume : lotConsumes) {
				Storage storage = getMaterialStorage(orgRrn, lotConsume.getMaterialRrn(), warehouseRrn, userRrn);	
				if (!writeOffFlag) {
					storage.setQtyWriteOff(storage.getQtyWriteOff().add(lotConsume.getQtyConsume()));
//					lotConsume.setIsWin(false);
				} else {
					storage.setQtyWriteOff(storage.getQtyWriteOff().add(lotConsume.getQtyConsume().negate()));
//					lotConsume.setIsWin(true);
				}
				storage.setUpdatedBy(userRrn);
				em.merge(storage);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public String generateNextNumber(long orgRrn, Material material) throws ClientException {
		try{
			String dateSuffix = basManager.getCurrentDateCode(orgRrn);
			
			long seq = adManager.getNextSequence(material.getOrgRrn(), material.getMaterialId() + dateSuffix);
			String seqSuffix = String.format("%04d", seq);
			return material.getMaterialId() + dateSuffix + seqSuffix;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
//	public String generateNextNumber(long orgRrn, Material material) throws ClientException {
//		try{
//			String dateSuffix = basManager.getCurrentDateCode(orgRrn);
//			String sql = "SELECT MAX(SUBSTR(LOT_ID, LENGTH(?) + 1))  FROM " +
//					"(SELECT LOT_ID FROM WIP_LOT T WHERE ORG_RRN = ? AND T.LOT_ID LIKE '%?%') L";
//			
//			Query query = em.createNativeQuery(sql);
//			query.setParameter(1, material.getMaterialId() + dateSuffix);
//			query.setParameter(2, orgRrn);
//			query.setParameter(3, material.getMaterialId() + dateSuffix);
//			BigDecimal qtyMax = ((BigDecimal)query.getSingleResult());
//			long seq = 0;
//			if (qtyMax == null) {
//				seq = 1;
//			} else {
//				seq = qtyMax.longValue();
//			}
//			
//			String seqSuffix = String.format("%04d", seq);
//			return material.getMaterialId() + dateSuffix + seqSuffix;
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
	
	/**
	 * 对于工作令接受时产生的MATERIAL类型的条码需要追加工作令rrn
	 */
	private Lot getMaterialLot(long orgRrn, Material material, long userRrn, ManufactureOrderLine moLine) throws ClientException {
		try{
			if (material.getIsLotControl()) {
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					Lot lot = null;
					try {
						lot = getLotByLotId(orgRrn, material.getMaterialId());
					} catch(ClientException e) {
						
					}
					if (lot == null) {
						lot = new Lot();
						lot.setIsActive(true);
						lot.setCreatedBy(userRrn);
						lot.setCreated(new Date());
						lot.setUpdatedBy(userRrn);
						lot.setOrgRrn(orgRrn);
						lot.setLotId(material.getMaterialId());
						lot.setPosition(Lot.POSITION_GEN);
						lot.setLotType(material.getLotType());
						lot.setMaterialRrn(material.getObjectRrn());
						lot.setMaterialId(material.getMaterialId());
						lot.setMaterialName(material.getName());
						lot.setQtyInitial(BigDecimal.ZERO);
						lot.setQtyCurrent(BigDecimal.ZERO);
						lot.setIsUsed(false);
						em.persist(lot);
					}
					
					if(moLine != null){
						ManufactureOrderLineLot moLineLot = null;
						StringBuffer hql = new StringBuffer();
						hql.append(" FROM ManufactureOrderLineLot Lot ");
						hql.append(" WHERE Lot.moLineUid = ? ");
						
						Query query = em.createQuery(hql.toString());
						query.setParameter(1, moLine.getUid());
						
						List lst = query.getResultList();
						
						if(lst != null && lst.size() > 0){
							moLineLot = (ManufactureOrderLineLot) lst.get(0);
						}else{
							moLineLot = new ManufactureOrderLineLot();
							moLineLot.setMoLineUid(moLine.getUid());
							moLineLot.setLot(lot);
							moLineLot.setLotId(material.getMaterialId()+"$"+moLine.getUid());
							
							em.persist(moLineLot);
						}
						
						lot.setMoLineLot(moLineLot);
					}
					return lot;
				} 
			} else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
			return null;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Lot> generateSerialLot(long orgRrn, Material material, int qty, long userRrn) throws ClientException {
		List<Lot> lots = new ArrayList<Lot>();
		try{
			if (material.getIsLotControl()) {
				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
					for (int i = 0; i < qty; i++) {
						Lot lot = new Lot();
						lot.setIsActive(true);
						lot.setCreatedBy(userRrn);
						lot.setCreated(new Date());
						lot.setUpdatedBy(userRrn);
						lot.setOrgRrn(orgRrn);
						lot.setLotId(generateNextNumber(orgRrn, material));
						lot.setLotType(material.getLotType());
						lot.setPosition(Lot.POSITION_GEN);
						lot.setMaterialRrn(material.getObjectRrn());
						lot.setMaterialId(material.getMaterialId());
						lot.setMaterialName(material.getName());
						lot.setQtyInitial(BigDecimal.ONE);
						lot.setQtyCurrent(BigDecimal.ONE);
						lot.setIsUsed(false);
						lots.add(lot);
					}
				} 
			} else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
			return lots;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Lot> generateSerialLot(Iqc iqc, IqcLine line, long userRrn) throws ClientException {
		Material material = line.getMaterial();
		BigDecimal qtyLine = line.getQtyQualified();
		int intQtyLine = qtyLine.intValue();
		if (qtyLine.doubleValue() != intQtyLine) {
			throw new ClientException("inv.lot_serical_double");
		}
		List<Lot> lots = generateSerialLot(iqc.getOrgRrn(), material, intQtyLine, userRrn);
		for (Lot lot : lots) {
			lot.setPoRrn(iqc.getPoRrn());
			lot.setPoId(iqc.getPoId());
			lot.setPoLineRrn(line.getPoLineRrn());
			lot.setReceiptRrn(iqc.getReceiptRrn());
			lot.setReceiptId(iqc.getReceiptId());
			lot.setIqcRrn(iqc.getObjectRrn());
			lot.setIqcId(iqc.getDocId());
			lot.setIqcLineRrn(line.getObjectRrn());
		}
		return lots;
	}
	
	public List<Lot> generateBatchLot(long orgRrn, Material material, BigDecimal qty, int batchNumber, long userRrn) throws ClientException {
		List<Lot> lots = new ArrayList<Lot>();
		try{
			if (material.getIsLotControl()) {
				if (Lot.LOTTYPE_BATCH_A.equals(material.getLotType()) ||
						Lot.LOTTYPE_BATCH.equals(material.getLotType())) {
					BigDecimal batchSize;
					
					double qtyLine = qty.doubleValue();
					int intQtyLine = qty.intValue();
					if (qtyLine == intQtyLine) {
						batchSize = qty.divideToIntegralValue(new BigDecimal(batchNumber));
					} else {
						batchSize = qty.divide(new BigDecimal(batchNumber), Constants.DIVIDE_SCALE, RoundingMode.FLOOR);
					}
					for (int i = 0; i < batchNumber; i++) {
						Lot lot = new Lot();
						lot.setIsActive(true);
						lot.setCreatedBy(userRrn);
						lot.setCreated(new Date());
						lot.setUpdatedBy(userRrn);
						lot.setOrgRrn(orgRrn);
						lot.setLotId(generateNextNumber(orgRrn, material));
						lot.setLotType(material.getLotType());
						lot.setMaterialRrn(material.getObjectRrn());
						lot.setMaterialId(material.getMaterialId());
						lot.setMaterialName(material.getName());
						lot.setQtyInitial(batchSize);
						lot.setQtyCurrent(batchSize);
						lot.setIsUsed(false);
						lots.add(lot);
					}
				}
			} else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
			return lots;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Lot> generateBatchLot(Iqc iqc, IqcLine line, int batchNumber, long userRrn) throws ClientException {
		Material material = line.getMaterial();
		List<Lot> lots = generateBatchLot(iqc.getOrgRrn(), material, line.getQtyQualified(), batchNumber, userRrn);
		for (Lot lot : lots) {
			lot.setPoRrn(iqc.getPoRrn());
			lot.setPoId(iqc.getPoId());
			lot.setPoLineRrn(line.getPoLineRrn());
			lot.setReceiptRrn(iqc.getReceiptRrn());
			lot.setReceiptId(iqc.getReceiptId());
			lot.setIqcRrn(iqc.getObjectRrn());
			lot.setIqcId(iqc.getDocId());
			lot.setIqcLineRrn(line.getObjectRrn());
		}
		return lots;
	}
	
	public Lot getLotByLotId(long orgRrn, String lotId) throws ClientException {
		try{
			String moLineUid = null;
			if(lotId != null && lotId.contains("$")){
				String[] strs = lotId.split("\\$");
				lotId = strs[0];
				moLineUid = strs[1];
			}
			List<Lot> lotList = new ArrayList<Lot>();
			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot as Lot ");
			sql.append("WHERE");
			sql.append(ADBase.BASE_CONDITION);
			sql.append("AND lotId = ?");
			logger.debug(sql);
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, lotId);
			lotList = query.getResultList();
			if (lotList == null || lotList.size() == 0) {
				List<Material> ls = pdmManager.getMaterialById(lotId, orgRrn);//如果是material类型的则自动创建批次
				if(ls != null && ls.size() > 0){
					Material material = ls.get(0);
					if(material != null){
						if (material.getIsLotControl()) {
							if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
								Lot lot = new Lot();
								lot.setIsActive(true);
								lot.setCreatedBy(0L);
								lot.setCreated(new Date());
								lot.setUpdatedBy(0L);
								lot.setOrgRrn(orgRrn);
								lot.setLotId(material.getMaterialId());
								lot.setPosition(Lot.POSITION_GEN);
								lot.setLotType(material.getLotType());
								lot.setMaterialRrn(material.getObjectRrn());
								lot.setMaterialId(material.getMaterialId());
								lot.setMaterialName(material.getName());
								lot.setQtyInitial(BigDecimal.ZERO);
								lot.setQtyCurrent(BigDecimal.ZERO);
								lot.setSoId(getSoIdByLotId(orgRrn,lotId));
								lot.setIsUsed(false);
								em.persist(lot);
								return lot;
							}
						}else{
							throw new ClientException("inv.material_is_not_lotcontrol");
						}
					}
				}
				throw new ClientException("inv.lotnotexist");
			}
			Lot lot = lotList.get(0);
			if(moLineUid != null && moLineUid.length() > 0){
				ManufactureOrderLine moLine = getMoLineByUid(Long.valueOf(moLineUid));
				if(moLine != null){
					lot.setMoLine(moLine);
					lot.setMoLineRrn(moLine.getObjectRrn());
				}
			}else{
				ManufactureOrderLine moLine = null;
				if(lot != null && lot.getMoLineRrn() != null){
					moLine = em.find(ManufactureOrderLine.class, lot.getMoLineRrn());
					if(moLine != null){
						lot.setMoLine(moLine);
					}
				}
			}
			lot.setSoId(getSoIdByLotId(orgRrn,lotId));
			return lot;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public String getSoIdByLotId(long orgRrn,String lotId){
		String soId="";
		try{
			StringBuffer sql = new StringBuffer(" SELECT I.SO_ID FROM WIP_LOT T,INV_MOVEMENT I WHERE T.OUT_RRN=I.OBJECT_RRN(+) ");
			sql.append(" AND T.LOT_ID= ? ");
			sql.append(" AND T.ORG_RRN=? ");
			logger.debug(sql);
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, lotId);
			query.setParameter(2, orgRrn);
			
			Object obj = query.getSingleResult();
			if(obj != null){
				soId=(String)obj;
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return soId;
	}
	
	//根据批号和仓库获得该仓库中的批次
	public Lot getLotByLotId(long orgRrn, String lotId, long warehouseRrn) throws ClientException {
		try{
			List<Lot> lotList = new ArrayList<Lot>();
			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot as Lot ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND lotId = ? ");
			sql.append(" AND warehouseRrn = ? ");
			logger.debug(sql);
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, lotId);
			query.setParameter(3, warehouseRrn);
			lotList = query.getResultList();
			if (lotList == null || lotList.size() == 0) {
				throw new ClientException("inv.lotnotexist");
			}
			Lot lot = lotList.get(0);
			return lot;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	public void modifyLotId(String oldLotId,String newLotId, long userRrn, long orgRrn) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot as Lot ");
			sql.append("WHERE");
			sql.append(ADBase.BASE_CONDITION);
			sql.append("AND lotId = ?");
			logger.debug(sql);
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, newLotId);
			List<Lot> lotList = query.getResultList();
			if (lotList == null || lotList.size() == 0) {
				Lot lot=getLotByLotId(orgRrn, oldLotId);
				lot.setLotId(newLotId);
				lot.setUpdatedBy(userRrn);
				em.persist(lot);
			}
			else{
				throw new ClientException("inv.lot_is_exist");
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Storage getMaterialStorage(long orgRrn, long materialRrn, long warehouseRrn, long userRrn)  throws ClientException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Storage FROM Storage as Storage ");
		sql.append(" WHERE materialRrn = ? AND warehouseRrn = ? ");
		Storage storage;
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, materialRrn);
			query.setParameter(2, warehouseRrn);
			List<Storage> storages = query.getResultList();
			if (storages.size() == 0) {
				storage = new Storage();
				storage.setOrgRrn(orgRrn);
				storage.setMaterialRrn(materialRrn);
				storage.setWarehouseRrn(warehouseRrn);
				storage.setIsActive(true);
				storage.setCreatedBy(userRrn);
				storage.setCreated(new Date());
				storage.setUpdatedBy(userRrn);
				em.persist(storage);
			} else {
				storage = storages.get(0);
			}
			return storage;
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	
//	public BigDecimal getQtyOnHand(long orgRrn, long materialRrn) throws ClientException {
//		//统计所有参与MRP的仓库的数量
//		StringBuffer sqlOnHand = new StringBuffer();
//		sqlOnHand.append(" SELECT NVL(SUM(QTY_ONHAND), 0) QTY_ONHAND FROM INV_STORAGE S, ");
//		sqlOnHand.append(" (SELECT * FROM INV_WAREHOUSE WHERE ");
//		sqlOnHand.append(ADBase.SQL_BASE_CONDITION);
//		sqlOnHand.append(" ) W");
//		sqlOnHand.append(" WHERE S.WAREHOUSE_RRN = W.OBJECT_RRN AND W.IS_MRP = 'Y' ");
//		sqlOnHand.append(" AND S.MATERIAL_RRN = ? ");
//		
//		//统计还未Approve的入库-出库数量
//		StringBuffer sqlMove = new StringBuffer();
//		sqlMove.append(" SELECT (I.QTY_MOVEMENT - O.QTY_MOVEMENT) QTY_MOVEMENT FROM "); 
//		sqlMove.append(" (SELECT NVL(SUM(QTY_MOVEMENT), 0) QTY_MOVEMENT FROM INV_MOVEMENT_LINE L, ");
//		sqlMove.append(" 	(SELECT * FROM INV_MOVEMENT WHERE " );
//		sqlMove.append(ADBase.SQL_BASE_CONDITION);
//		sqlMove.append(" ) W");
//		sqlMove.append(" WHERE L.MOVEMENT_RRN = W.OBJECT_RRN AND W.MOVEMENT_TYPE = 'I' ");
//		sqlMove.append(" AND L.LINE_STATUS = '" + Movement.STATUS_DRAFTED + "'" );
//		sqlMove.append("  AND L.MATERIAL_RRN = ? ");
//		sqlMove.append(" ) I, ");
//		sqlMove.append(" (SELECT NVL(SUM(QTY_MOVEMENT), 0) QTY_MOVEMENT FROM INV_MOVEMENT_LINE L, ");
//		sqlMove.append(" 	(SELECT * FROM INV_MOVEMENT WHERE " );
//		sqlMove.append(ADBase.SQL_BASE_CONDITION);
//		sqlMove.append(" ) W");
//		sqlMove.append(" WHERE L.MOVEMENT_RRN = W.OBJECT_RRN AND W.MOVEMENT_TYPE = 'O' ");
//		sqlMove.append(" AND L.LINE_STATUS = '" + Movement.STATUS_DRAFTED + "'" );
//		sqlMove.append("  AND L.MATERIAL_RRN = ? ");
//		sqlMove.append(" ) O ");
//		
//		try{
//			Query query = em.createNativeQuery(sqlOnHand.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, materialRrn);
//			BigDecimal qtyOnHand = ((BigDecimal)query.getSingleResult());
//			
//			query = em.createNativeQuery(sqlMove.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, materialRrn);
//			query.setParameter(3, orgRrn);
//			query.setParameter(4, materialRrn);
//			BigDecimal qtyMove = ((BigDecimal)query.getSingleResult());
//			
//			//将库存数量与未审Movement都记为Onhand数量
//			return qtyOnHand.add(qtyMove);
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
	
	private Map<Movement.LotAction, List<Lot>> filterInLot(long orgRrn, long inLineRrn, List<Lot> lots) throws ClientException {
		String whereClause = " inLineRrn = '" + inLineRrn + "'";
		List<Lot> oldLots = adManager.getEntityList(orgRrn, Lot.class, Integer.MAX_VALUE, whereClause, "");
		return filterLot(oldLots, lots);
	}
	
	private Map<Movement.LotAction, List<Lot>> filterOutLot(long orgRrn, long outLineRrn, List<Lot> lots) throws ClientException {
		String whereClause = " outLineRrn = '" + outLineRrn + "'";
		List<Lot> oldLots = adManager.getEntityList(orgRrn, Lot.class, Integer.MAX_VALUE, whereClause, "");
		return filterLot(oldLots, lots);
	}
	
	private Map<Movement.LotAction, List<Lot>> filterTransferLot(long orgRrn, long transferLineRrn, List<Lot> lots) throws ClientException {
		String whereClause = " transferLineRrn = '" + transferLineRrn + "'";
		List<Lot> oldLots = adManager.getEntityList(orgRrn, Lot.class, Integer.MAX_VALUE, whereClause, "");
		return filterLot(oldLots, lots);
	}
	
	private Map<Movement.LotAction, List<Lot>> filterLot(List<Lot> oldLots, List<Lot> lots) throws ClientException {
		List<Lot> addLots = new ArrayList<Lot>();
		List<Lot> removeLots = new ArrayList<Lot>();
		for (Lot lot : lots) {
			if (!oldLots.contains(lot)) {
				addLots.add(lot);
			}
		}
		for (Lot lot : oldLots) {
			if (!lots.contains(lot)) {
				removeLots.add(lot);
			}
		}
		Map<Movement.LotAction, List<Lot>> mappedLot = new HashMap<Movement.LotAction, List<Lot>>();
		mappedLot.put(Movement.LotAction.ADD, addLots);
		mappedLot.put(Movement.LotAction.REMOVE, removeLots);
		return mappedLot;
	}
	
	private Map<Movement.LotAction, List<MovementLineLot>> filterMovementLineLot(long orgRrn, long movementLineRrn,
			List<MovementLineLot> lineLots) throws ClientException {
		String whereClause = " movementLineRrn = '" + movementLineRrn + "'";
		List<MovementLineLot> oldLineLots = adManager.getEntityList(orgRrn, MovementLineLot.class, Integer.MAX_VALUE, whereClause, "");
		return filterMovementLineLot(oldLineLots, lineLots);
	}
	
	
	private Map<Movement.LotAction, List<MovementLineLot>> filterMovementLineLot(List<MovementLineLot> oldLineLots,
			List<MovementLineLot> lineLots) throws ClientException {
		List<MovementLineLot> addLots = new ArrayList<MovementLineLot>();
		List<MovementLineLot> removeLots = new ArrayList<MovementLineLot>();
		for (MovementLineLot lineLot : lineLots) {
			if (!oldLineLots.contains(lineLot)) {
				addLots.add(lineLot);
			}
		}
		for (MovementLineLot oldLineLot : oldLineLots) {
			if (!lineLots.contains(oldLineLot)) {
				removeLots.add(oldLineLot);
			}
		}
		Map<Movement.LotAction, List<MovementLineLot>> mappedLot = new HashMap<Movement.LotAction, List<MovementLineLot>>();
		mappedLot.put(Movement.LotAction.ADD, addLots);
		mappedLot.put(Movement.LotAction.REMOVE, removeLots);
		return mappedLot;
	}
	
	private String generateReceiptCode(Receipt receipt) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(receipt.getOrgRrn(), receipt.getDocType()));
		moCode.append(basManager.generateCodeSuffix(receipt.getOrgRrn(), receipt.getDocType(), receipt.getCreated()));
		return moCode.toString();
	}
	
	private String generateIqcCode(Iqc iqc) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(iqc.getOrgRrn(), iqc.getDocType()));
		moCode.append(basManager.generateCodeSuffix(iqc.getOrgRrn(), iqc.getDocType(), iqc.getCreated()));
		return moCode.toString();
	}
	
	private String generateInCode(MovementIn in) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(in.getOrgRrn(), in.getDocType()));
		moCode.append(basManager.generateCodeSuffix(in.getOrgRrn(), in.getDocType(), in.getCreated()));
		return moCode.toString();
	}
	
	private String generateOutCode(MovementOut out) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(out.getOrgRrn(), out.getDocType()));
		moCode.append(basManager.generateCodeSuffix(out.getOrgRrn(), out.getDocType(), out.getCreated()));
		return moCode.toString();
	}
	
	private String generateTransferCode(MovementTransfer transfer) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(transfer.getOrgRrn(), transfer.getDocType()));
		moCode.append(basManager.generateCodeSuffix(transfer.getOrgRrn(), transfer.getDocType(), transfer.getCreated()));
		return moCode.toString();
	}
	
	private String generateWriteOffCode(MovementWriteOff mw) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(mw.getOrgRrn(), mw.getDocType()));
		moCode.append(basManager.generateCodeSuffix(mw.getOrgRrn(), mw.getDocType(), mw.getCreated()));
		return moCode.toString();
	}
	
	public List<Lot> splitLot(Lot sourceLot, BigDecimal splitQty, int spiltCount, Long userRrn) throws ClientException {
		try {
			if(sourceLot != null && BigDecimal.ZERO.compareTo(splitQty) < 0) {
				List<Lot> lots = new ArrayList<Lot>();
				if (Lot.LOTTYPE_SERIAL.equals(sourceLot.getLotType())
						|| Lot.LOTTYPE_MATERIAL.equals(sourceLot.getLotType())) {
					throw new ClientException("inv.lot_mustbe_unused_and_batch");
				}
				if (!(Lot.POSITION_IQC.equals(sourceLot.getPosition())
						|| Lot.POSITION_GEN.equals(sourceLot.getPosition()))) {
					throw new ClientParameterException("inv.lot_already_in", sourceLot.getLotId());
				};
				BigDecimal totalQty = splitQty.multiply(BigDecimal.valueOf(spiltCount));
				if(totalQty.compareTo(sourceLot.getQtyCurrent()) > 0) {
					throw new ClientParameterException("inv.currentQty_smaller_splitQty",
							sourceLot.getQtyCurrent().toString(), splitQty.toString());
				}
				Date date = new Date();
				sourceLot.setUpdated(date);
				sourceLot.setUpdatedBy(userRrn);
				sourceLot.setQtyCurrent(sourceLot.getQtyCurrent().subtract(totalQty));
				em.merge(sourceLot);

				long transSeq = basManager.getHisSequence();
				ESpiltLotHis his = new ESpiltLotHis(sourceLot);
				his.setHisSeq(transSeq);
				em.persist(his);
				
				for (int i = 0; i < spiltCount; i++) {
					Lot newLot = (Lot)sourceLot.clone();
					newLot.setCreated(date);
					newLot.setCreatedBy(userRrn);
					newLot.setUpdated(date);
					newLot.setUpdatedBy(userRrn);
					newLot.setParentLotRrn(sourceLot.getObjectRrn());
					// 生成lotId
					Material material = em.find(Material.class, newLot.getMaterialRrn());
					newLot.setLotId(generateNextNumber(newLot.getOrgRrn(), material));
					// 设置lot数量
					newLot.setQtyCurrent(splitQty);
					newLot.setQtyInitial(splitQty);
					newLot.setLotParameters(null);
					em.persist(newLot);
					lots.add(newLot);
					
					transSeq = basManager.getHisSequence();
					ESpiltLotHis newHis = new ESpiltLotHis(newLot);
					newHis.setHisSeq(transSeq);
					em.persist(newHis);
				}
				return lots;
			}
			return null;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//根据实际用量得到消耗量
	public List<LotConsume> getMaterialConsume(long lotRrn) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT LotConsume FROM LotConsume LotConsume ");
		sql.append(" WHERE  lotRrn = ? ");
		sql.append(" AND isWin = 'N' ");
		try {
			Lot lot = em.find(Lot.class, lotRrn);
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, lotRrn);
			return (List<LotConsume>)query.getResultList();
			
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//根据BOM得到消耗量
	public List<LotConsume> getMaterialBomConsume(long moRrn, String path) throws ClientException {
		StringBuffer sqlMo = new StringBuffer(" SELECT ManufactureOrderBom FROM ManufactureOrderBom ManufactureOrderBom ");
		sqlMo.append(" WHERE ");
		sqlMo.append(" moRrn = ? ");
		if (path == null || path.trim().length() == 0) {
		} else {
			sqlMo.append(" AND path like ? "); 
		}
		
		List<LotConsume> lotConsumes = new ArrayList<LotConsume>();
		try {
			Query query = em.createQuery(sqlMo.toString());
			query.setParameter(1, moRrn);
			if (path == null || path.trim().length() == 0) {
			} else {
				query.setParameter(2, path + "%");
			}
			List<ManufactureOrderBom> boms = (List<ManufactureOrderBom>)query.getResultList();
			Set<String> parentPath = new HashSet<String>();
			for (ManufactureOrderBom bom : boms) {
				parentPath.add(bom.getPath());
			}
			for (ManufactureOrderBom bom : boms) {
				String currentPath = (bom.getPath() != null ? bom.getPath() : "") + bom.getMaterialRrn() + "/";
				if (parentPath.contains(currentPath)) {
					continue;
				}
				Material material = em.find(Material.class, bom.getMaterialRrn());
				if (!material.getIsLotControl()) {
					continue;
				}
				
				LotConsume lotConsume = new LotConsume();
				lotConsume.setMaterialRrn(bom.getMaterialRrn());
				lotConsume.setMaterialId(bom.getMaterialId());
				lotConsume.setMaterialName(bom.getMaterialName());
				lotConsume.setUnitConsume(bom.getQtyBom());
				lotConsume.setIsWin(false);
				lotConsumes.add(lotConsume);
			}
			return lotConsumes;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<LotConsume> getMaterialConsumeByMo(long orgRrn, Long moRrn, String materialId, 
			String toStartDate, String toEndDate) throws ClientException {
		return getMaterialConsumeByMo(orgRrn, moRrn, materialId, toStartDate, toEndDate, null);
	}
	
	public List<LotConsume> getMaterialConsumeByMo(long orgRrn, Long moRrn, String materialId,  
			String toStartDate, String toEndDate, String isManual) throws ClientException {
		List<LotConsume> lcs = new ArrayList<LotConsume>();
		StringBuffer sql = new StringBuffer(" SELECT P.MATERIAL_ID, P.NAME, S.QTY_CONSUME_TOTAL, P.BOM_PRICE,NVL(MO.DOC_ID,''), S.CONSUME_DATE,S.CREATED FROM ");
		
		sql.append(" (SELECT C.MO_RRN,C.MATERIAL_RRN, NVL(SUM(C.QTY_CONSUME), 0) QTY_CONSUME_TOTAL, MAX(C.CREATEDD) CONSUME_DATE, MAX(C.CREATED) CREATED ");
		sql.append("  FROM (SELECT CC.*,WM_CONCAT(TO_CHAR(CC.CREATED,'YYYY-MM-DD')) OVER(PARTITION BY MO_RRN,MATERIAL_RRN ORDER BY MO_RRN,MATERIAL_RRN,CREATED DESC) CREATEDD FROM WIP_LOT_CONSUME CC ");
		sql.append(" WHERE ");
		sql.append( " ORG_RRN = " + orgRrn);
		if (moRrn != null) {
			sql.append(" AND MO_RRN = ");
			sql.append(moRrn);
		}
		if (materialId != null || materialId.trim().length() > 0) {
			sql.append(" AND MATERIAL_ID LIKE '");
			sql.append(materialId);
			sql.append("%' ");
		}
		if (isManual != null) {
			sql.append(" AND IS_MANUAL = ");
			sql.append(isManual);
		}
		String datePattern = "'YYYY-MM-DD'";
		if(toStartDate != null) {
			sql.append(" AND TRUNC(DATE_IN) >= TO_DATE('" + toStartDate + "', "
					+ datePattern + ") ");
		}
		if(toEndDate != null) {
			sql.append(" AND TRUNC(DATE_IN) <= TO_DATE('" + toEndDate + "', "
					+ datePattern + ") ");
		}
		sql.append(") C");
		sql.append(" GROUP BY C.MO_RRN,C.MATERIAL_RRN) S, PDM_MATERIAL P, WIP_MO MO ");
		sql.append(" WHERE S.MATERIAL_RRN = P.OBJECT_RRN ");
		sql.append(" AND S.MO_RRN = MO.OBJECT_RRN ");
		sql.append(" ORDER BY DOC_ID,MATERIAL_ID ");

		Query query = em.createNativeQuery(sql.toString());
		List<Object[]> result = (List<Object[]>)query.getResultList();				
		for (Object[] row : result) {
			LotConsume lc = new LotConsume();
			lc.setMaterialId(String.valueOf(row[0]));
			lc.setMaterialName(String.valueOf(row[1]));
			lc.setQtyConsume((BigDecimal)row[2]);
			lc.setBomPrice((BigDecimal)row[3]);
			lc.setLotId(String.valueOf(row[4]));//注意:这里MO_ID赋到了lotId属性中是因为前台配字段时工作令编号对应的是lotId,不知为什么要这么配,可能有特殊需要
			lc.setConsumeDate(String.valueOf(row[5]));
			lc.setCreated((Date)row[6]);
			lcs.add(lc);
		}
		return lcs;
	}
	
	public void updateLotStorage(long orgRrn, long lotRrn, long warehouseRrn, BigDecimal qty, long userRrn) throws ClientException {
		try{
			LotStorage storage = getLotStorage(orgRrn, lotRrn, warehouseRrn, userRrn);
			storage.setQtyOnhand(storage.getQtyOnhand().add(qty));
			storage.setUpdatedBy(userRrn);
			em.merge(storage);
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public LotStorage getLotStorage(long orgRrn, long lotRrn, long warehouseRrn, long userRrn)  throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT LotStorage FROM LotStorage as LotStorage ");
		sql.append(" WHERE lotRrn = ? AND warehouseRrn = ? ");
		LotStorage storage;
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, lotRrn);
			query.setParameter(2, warehouseRrn);
			List<LotStorage> storages = query.getResultList();
			if (storages.size() == 0) {
				storage = new LotStorage();
				storage.setOrgRrn(orgRrn);
				storage.setLotRrn(lotRrn);
				storage.setWarehouseRrn(warehouseRrn);
				storage.setIsActive(true);
				storage.setCreatedBy(userRrn);
				storage.setCreated(new Date());
				storage.setUpdatedBy(userRrn);
				em.persist(storage);
			} else {
				storage = storages.get(0);
			}
			return storage;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Lot> getLotStorage(long warehouseRrn, long materialRrn)  throws ClientException {
		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT Lot, LotStorage.qtyOnhand FROM Lot Lot, LotStorage LotStorage ");
//		sql.append(" WHERE Lot.materialRrn = ? ");
//		sql.append(" AND LotStorage.warehouseRrn = ? ");
//		sql.append(" AND Lot.objectRrn = LotStorage.lotRrn ");
//		sql.append(" AND LotStorage.qtyOnhand <> 0 ");
		sql.append(" SELECT W.LOT_ID,W.MATERIAL_ID,W.MATERIAL_NAME,T.QTY_ONHAND,W.DATE_PRODUCT, W.DATE_IN,W.REVERSE_FIELD9,W.REVERSE_FIELD8,W.REVERSE_FIELD7 ");
		sql.append(" FROM WIP_LOT W,INV_LOT_STORAGE T");
		sql.append(" WHERE W.IS_ACTIVE='Y' ");
		sql.append(" AND T.LOT_RRN = W.OBJECT_RRN");
		sql.append(" AND W.MATERIAL_RRN = ?");
		sql.append(" AND T.WAREHOUSE_RRN = ?");		
		sql.append(" AND T.QTY_ONHAND <> 0");
		sql.append(" ORDER BY W.LOT_ID");

		try{
			List<Lot> lotStorages = new ArrayList<Lot>();
//			Query query = em.createQuery(sql.toString());
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, materialRrn);
			query.setParameter(2, warehouseRrn);
			List<Object[]> result = (List<Object[]>)query.getResultList();
			for (Object[] row : result) {
				Lot lot = new Lot();
				String lotId = (String)row[0];
				String materialId = (String)row[1];
				String materialName = (String)row[2];
				BigDecimal qtyCurrent = (BigDecimal)row[3];
				Date dateProduct = (row[4]==null?null:(Date) row[4]);
				Date dateIn = (row[5]==null?null:(Date) row[5]);
				String reverseField9 = (String)row[6];
				String reverseField8 = (String)row[7];
				String reverseField7 = (String)row[8];
				lot.setLotId(lotId);
				lot.setMaterialId(materialId);
				lot.setMaterialName(materialName);
				lot.setQtyCurrent(qtyCurrent);
				lot.setDateIn(dateIn);
				lot.setDateProduct(dateProduct);
				lot.setReverseField9(reverseField9);//WMS需要知道外发和内发，以免发错货
				lot.setReverseField8(reverseField8);
				lot.setReverseField7(reverseField7);
				lotStorages.add(lot);
			}
			return lotStorages;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Lot> getLotStorage(long materialRrn)  throws ClientException {
		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT Lot, LotStorage.qtyOnhand FROM Lot Lot, LotStorage LotStorage ");
//		sql.append(" WHERE Lot.materialRrn = ? ");
//		sql.append(" AND LotStorage.warehouseRrn = ? ");
//		sql.append(" AND Lot.objectRrn = LotStorage.lotRrn ");
//		sql.append(" AND LotStorage.qtyOnhand <> 0 ");
		sql.append(" SELECT W.LOT_ID,W.MATERIAL_ID,W.MATERIAL_NAME,T.QTY_ONHAND");
		sql.append(" FROM WIP_LOT W,INV_LOT_STORAGE T");
		sql.append(" WHERE W.IS_ACTIVE='Y' ");
		sql.append(" AND T.LOT_RRN = W.OBJECT_RRN");
		sql.append(" AND W.MATERIAL_RRN = ?");
		sql.append(" AND T.WAREHOUSE_RRN in (151043,151046,2501936,2501939)");		
		sql.append(" AND T.QTY_ONHAND <> 0");
		sql.append(" ORDER BY W.LOT_ID");

		try{
			List<Lot> lotStorages = new ArrayList<Lot>();
//			Query query = em.createQuery(sql.toString());
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, materialRrn);
			List<Object[]> result = (List<Object[]>)query.getResultList();
			for (Object[] row : result) {
				Lot lot = new Lot();
				String lotId = (String)row[0];
				String materialId = (String)row[1];
				String materialName = (String)row[2];
				BigDecimal qtyCurrent = (BigDecimal)row[3];
				lot.setLotId(lotId);
				lot.setMaterialId(materialId);
				lot.setMaterialName(materialName);
				lot.setQtyCurrent(qtyCurrent);
				lotStorages.add(lot);
			}
			return lotStorages;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<MovementLineOutSerial> generateMovementLineOutSerials(long orgRrn, MovementOut out, MovementLine outLine, long userRrn) throws ClientException {
		try {
			List<MovementLineOutSerial> outSerials = new ArrayList<MovementLineOutSerial>();
			
			Material material = em.find(Material.class, outLine.getMaterialRrn());
			if(Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
				throw new ClientParameterException("inv.should_not_gen_outserial", material.getMaterialId());
			}
			BigDecimal qtyOut = outLine.getQtyMovement();
			if(BigDecimal.ZERO.compareTo(qtyOut) >= 0) {
				throw new ClientException("inv.out_qty_can_not_zero");
			}
			boolean isMaterial = false;
			Lot lot = null;
			if(Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
				isMaterial = true;
				lot = this.getMaterialLot(orgRrn, material, userRrn);
			}
			BigDecimal integer = outLine.getQtyMovement().round(new MathContext(1, RoundingMode.UP));
			if(BigDecimal.ONE.compareTo(outLine.getQtyMovement()) > 0 
					|| outLine.getQtyMovement().compareTo(integer) != 0) {
				throw new ClientException("inv.float_qty_mvoement_can't_gen_outserial");
			}
			
			String whereClause = " movementLineRrn = " + outLine.getObjectRrn() + " ";
			List<MovementLineLot> list = adManager.getEntityList(orgRrn, MovementLineLot.class, Integer.MAX_VALUE, whereClause, null);
			Date now = new Date();
			int j = -1, k = 1;
			for(int i = 0; i < outLine.getQtyMovement().intValue(); i++) {
				String outSerialId = null, lotId = null;
				Long lotRrn = null;
				if(isMaterial) {
					lotRrn = lot.getObjectRrn();
					lotId = lot.getLotId();
				} else {
					if(j == -1 || k > list.get(j).getQtyMovement().intValue()) {
						j++;
						k = 1;
						BigDecimal temp = list.get(j).getQtyMovement().round(new MathContext(1, RoundingMode.UP));
						if(BigDecimal.ONE.compareTo(list.get(j).getQtyMovement()) > 0 
								|| list.get(j).getQtyMovement().compareTo(temp) != 0) {
							throw new ClientParameterException("inv.float_qty_can't_gen_outserial", list.get(j).getLotId());
						}
					}
					lotRrn = list.get(j).getLotRrn();
					lotId = list.get(j).getLotId();
				}
//				outSerialId = generateLotOutSeral(orgRrn, lotId, lotRrn, userRrn).getOutSerialId();
				outSerialId = generateNextNumber(orgRrn, material);
				MovementLineOutSerial outSerial = new MovementLineOutSerial();
				outSerial.setOrgRrn(orgRrn);
				outSerial.setIsActive(true);
				outSerial.setCreated(now);
				outSerial.setCreatedBy(userRrn);
				outSerial.setUpdated(now);
				outSerial.setUpdatedBy(userRrn);
				outSerial.setMovementRrn(out.getObjectRrn());
				outSerial.setMovementId(out.getDocId());
				outSerial.setMovementLineRrn(outLine.getObjectRrn());
				outSerial.setLotRrn(lotRrn);
				outSerial.setOutSerialId(outSerialId);
				em.persist(outSerial);
				outSerials.add(outSerial);
				
				k++;
			}
			return outSerials;
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public LotOutSerial generateLotOutSeral(long orgRrn, String lotId, long lotRrn, long userRrn) throws ClientException {
		Date now = new Date();
		String outSerialId = generateNextOutNumber(orgRrn, lotId);
		LotOutSerial lotOutSerial = new LotOutSerial();
		lotOutSerial.setOrgRrn(orgRrn);
		lotOutSerial.setIsActive(true);
		lotOutSerial.setCreated(now);
		lotOutSerial.setCreatedBy(userRrn);
		lotOutSerial.setUpdated(now);
		lotOutSerial.setUpdatedBy(userRrn);
		lotOutSerial.setLotRrn(lotRrn);
		lotOutSerial.setLotId(lotId);
		lotOutSerial.setOutSerialId(outSerialId);
		em.persist(lotOutSerial);
		
		return lotOutSerial;
	}
	
	public String generateNextOutNumber(long orgRrn, String lotId) throws ClientException {
		try{
			long seq = adManager.getNextSequence(orgRrn, lotId);
			String seqSuffix = String.format("%d", seq);
			return lotId + "-" + seqSuffix;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<PurchaseOrder> getUnInCompletedPoList(long orgRrn, int maxResult, String whereClause, String orderByClause) throws ClientException {
		List<PurchaseOrder> list = null;
		try {
			StringBuffer sql = new StringBuffer(" SELECT PurchaseOrder ");
			sql.append(" FROM PurchaseOrder PurchaseOrder ");
			sql.append(" WHERE ");
			sql.append(" PurchaseOrder.isActive = 'Y' ");
			sql.append(" AND (PurchaseOrder.orgRrn = ? OR PurchaseOrder.orgRrn = 0) ");
			sql.append(" AND PurchaseOrder.docStatus = 'APPROVED' ");
			sql.append(" AND PurchaseOrder.objectRrn IN ( ");
				sql.append(" SELECT DISTINCT(PurchaseOrderLine.poRrn) ");
				sql.append(" FROM PurchaseOrderLine PurchaseOrderLine ");
				sql.append(" WHERE PurchaseOrderLine.lineStatus = 'APPROVED' ");
				sql.append(" AND (PurchaseOrderLine.qty >=  PurchaseOrderLine.qtyDelivered OR PurchaseOrderLine.qtyDelivered IS NULL)");
			sql.append(")");
			if (whereClause != null && !"".equalsIgnoreCase(whereClause.trim())){
				sql.append(" AND ");
				sql.append(whereClause);
			}
			if (orderByClause != null && !"".equalsIgnoreCase(orderByClause.trim())){
				sql.append(" ORDER BY ");
				sql.append(orderByClause);			
			}
			Query query = em.createQuery(sql.toString());
			query.setMaxResults(maxResult);
			query.setParameter(1, orgRrn);
			list = query.getResultList();
			return list;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<Material> getNoMoveMaterialList(long orgRrn, Date dateApproved,
			String whereClause, String orderByClause, int maxResult) throws ClientException {
		return getNoMoveMaterialList(orgRrn, dateApproved, null, whereClause, orderByClause, maxResult);
	}
	
	@Override
	public List<Material> getNoMoveMaterialList(long orgRrn, Date dateApproved, Long warehouseRrn,
			String whereClause, String orderByClause, int maxResult) throws ClientException {
		try {
			List<Material> materials = new ArrayList<Material>();
			StringBuffer sql = new StringBuffer(" SELECT M.OBJECT_RRN, M.MATERIAL_ID, M.NAME," +
					"M.MATERIAL_CATEGORY1, M.MATERIAL_CATEGORY2, M.MATERIAL_CATEGORY3, M.MATERIAL_CATEGORY4, INS.qty_onhand, INS.qty_writeoff ");
			sql.append(" FROM PDM_MATERIAL M left join (SELECT I.material_rrn,NVL(SUM(I.qty_onhand),0) qty_onhand,NVL(SUM(I.QTY_WRITE_OFF),0) qty_writeoff FROM inv_storage I ");
			if(warehouseRrn == null){
				sql.append(" GROUP BY I.material_rrn ) INS");
			}else{
				sql.append(" WHERE I.warehouse_rrn = :warehouseRrn GROUP BY I.material_rrn ) INS");
			}
			sql.append(" on INS.material_rrn = M.object_rrn ");
			sql.append(" WHERE NOT EXISTS (");
				sql.append(" SELECT NULL FROM (");
					sql.append(" SELECT L.MATERIAL_RRN FROM INV_MOVEMENT_LINE L, ");
						sql.append(" (SELECT OBJECT_RRN FROM INV_MOVEMENT T ");
						sql.append("  WHERE T.ORG_RRN = :orgRrn AND T.DATE_APPROVED > TRUNC(:dateApproved) ");
						sql.append(" 	AND T.OBJECT_RRN NOT IN (5444538,5465939,5460848,5466217) ");//未出入库查询(不包括IWTG031164(rrn为5444538)和IWTG031169(rrn为5465939)和UWTG030823(rrn为5460848)和UWTG030828(rrn为5466217))
						if(warehouseRrn != null){
							sql.append(" AND ( T.WAREHOUSE_RRN = :warehouseRrn  OR T.TARGET_WAREHOUSE_RRN = :warehouseRrn ) ");
						}
						sql.append(" ) S ");
					sql.append(" WHERE S.OBJECT_RRN = L.MOVEMENT_RRN ");
				sql.append(") S ");
			sql.append(" WHERE M.OBJECT_RRN = S.MATERIAL_RRN) ");
			if(whereClause != null && !"".equalsIgnoreCase(whereClause.trim())) {
				sql.append(" AND M.ORG_RRN = :orgRrn AND ");
				sql.append(whereClause);
			}
			if(orderByClause != null && !"".equalsIgnoreCase(orderByClause.trim())) {
				sql.append(" ORDER BY ");
				sql.append(orderByClause);
			}
			Query query = em.createNativeQuery(sql.toString());
			query.setMaxResults(maxResult);
			query.setParameter("orgRrn", orgRrn);
			query.setParameter("dateApproved", dateApproved);
			if(warehouseRrn != null){
				query.setParameter("warehouseRrn", warehouseRrn);
			}
			List<Object[]> list = (List<Object[]>)query.getResultList();
			Material material = null;
			for(Object[] objs : list) {
				material = new Material();
				material.setObjectRrn(((BigDecimal)objs[0]).longValue());
				material.setMaterialId((String)objs[1]);
				material.setName((String)objs[2]);
				material.setMaterialCategory1((String)objs[3]);
				material.setMaterialCategory2((String)objs[4]);
				material.setMaterialCategory3((String)objs[5]);
				material.setMaterialCategory4((String)objs[6]);
				material.setQtyOnHand((BigDecimal)objs[7]);
				material.setQtyWriteOff((BigDecimal)objs[8]);
				materials.add(material);
			}
			return materials;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public List<MovementLineOutSerial> getMovementLineOutSerials(String outSerialId)
			throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT M FROM MovementLineOutSerial M ");
		sql.append(" WHERE M.outSerialId = ? ");
		Query query = em.createQuery(sql.toString());
		query.setParameter(1, outSerialId);
		List<MovementLineOutSerial> rzlt = query.getResultList();
		return rzlt;
	}
	
	public List<MaterialMoveSum> getMaterialMoveSumList(long orgRrn, Date approvedStartDate, Date approvedEndDate,
			String whereClause, String orderByClause, int maxResult) throws ClientException {
		try {
			List<MaterialMoveSum> materialSums = new ArrayList<MaterialMoveSum>();
			StringBuffer sql = new StringBuffer(" SELECT S.MATERIAL_RRN, M.MATERIAL_ID, M.NAME, M.QTY_MIN, " +
					" M.MATERIAL_CATEGORY1, M.MATERIAL_CATEGORY2, M.MATERIAL_CATEGORY3, M.MATERIAL_CATEGORY4, " +
					" S.WAREHOUSE_RRN, W.WAREHOUSE_ID, S.QTY_IN, S.QTY_OUT, S.QTY_IN - S.QTY_OUT QTY_TOAL ");
			sql.append(" FROM PDM_MATERIAL M, INV_WAREHOUSE W, ");
			sql.append(" (");
				sql.append(" SELECT MATERIAL_RRN, WAREHOUSE_RRN, SUM(QTY_IN) QTY_IN, SUM(QTY_OUT) QTY_OUT  FROM ");
				sql.append(" (");
					sql.append(" SELECT MATERIAL_RRN, WAREHOUSE_RRN, SUM(QTY_MOVEMENT) QTY_IN, 0 QTY_OUT ");
					sql.append(" FROM V_IN_DETAIL ");
					sql.append(" WHERE ORG_RRN = ? AND (TRUNC(DATE_APPROVED) BETWEEN TRUNC(?) AND TRUNC(?)) ");
					sql.append(" 	AND MOVMENT_RRN NOT IN (5444538,5465939) ");//出入库汇总 不包括IWTG031164(rrn为5444538)和IWTG031169(rrn为5465939)
					sql.append(" GROUP BY MATERIAL_RRN, WAREHOUSE_RRN ");
					
					sql.append(" UNION ALL ");
					
					sql.append(" SELECT MATERIAL_RRN, WAREHOUSE_RRN, 0 QTY_IN, SUM(QTY_MOVEMENT) QTY_OUT ");
					sql.append(" FROM V_OUT_DETAIL ");
					sql.append(" WHERE ORG_RRN = ? AND (TRUNC(DATE_APPROVED) BETWEEN TRUNC(?) AND TRUNC(?)) ");
					sql.append(" 	AND MOVMENT_RRN NOT IN (5460848,5466217) ");//和UWTG030823(rrn为5444538)和UWTG030828(rrn为5466217)
					sql.append(" GROUP BY MATERIAL_RRN, WAREHOUSE_RRN ");
					
					sql.append(" UNION ALL ");
					
					sql.append(" SELECT MATERIAL_RRN, WAREHOUSE_RRN, 0 QTY_IN, SUM(QTY_CONSUME) QTY_MOVEMENT FROM WIP_LOT_CONSUME T ");
					sql.append("  WHERE ORG_RRN = ? ");
					sql.append("    AND TRUNC(CREATED) BETWEEN TRUNC(?) AND TRUNC(?) ");
					sql.append("    AND QTY_CONSUME <> 0 ");
					sql.append("  GROUP BY WAREHOUSE_RRN, MATERIAL_RRN ");
					
			          
				sql.append(" ) ");
			sql.append(" GROUP BY MATERIAL_RRN, WAREHOUSE_RRN ");
			sql.append(" ) S ");
			sql.append(" WHERE M.OBJECT_RRN = S.MATERIAL_RRN AND W.OBJECT_RRN = S.WAREHOUSE_RRN ");
			if(whereClause != null && !"".equalsIgnoreCase(whereClause.trim())) {
				sql.append(" AND ");
				sql.append(whereClause);
			}
			if(orderByClause != null && !"".equalsIgnoreCase(orderByClause.trim())) {
				sql.append(" ORDER BY ");
				sql.append(orderByClause);
			}
			Query query = em.createNativeQuery(sql.toString());
			query.setMaxResults(maxResult);
			query.setParameter(1, orgRrn);
			query.setParameter(2, approvedStartDate);
			query.setParameter(3, approvedEndDate);
			query.setParameter(4, orgRrn);
			query.setParameter(5, approvedStartDate);
			query.setParameter(6, approvedEndDate);
			query.setParameter(7, orgRrn);
			query.setParameter(8, approvedStartDate);
			query.setParameter(9, approvedEndDate);
			List<Object[]> list = (List<Object[]>)query.getResultList();
			MaterialMoveSum moveSum = null;
			for(Object[] objs : list) {
				moveSum = new MaterialMoveSum();
				moveSum.setObjectRrn(((BigDecimal)objs[0]).longValue());
				moveSum.setMaterialId((String)objs[1]);
				moveSum.setMaterialName((String)objs[2]);
				moveSum.setQtyMin(((BigDecimal)objs[3]));
				moveSum.setMaterialCategory1((String)objs[4]);
				moveSum.setMaterialCategory2((String)objs[5]);
				moveSum.setMaterialCategory3((String)objs[6]);
				moveSum.setMaterialCategory4((String)objs[7]);
				moveSum.setWarehouseRrn(((BigDecimal)objs[8]).longValue());
				moveSum.setWarehouseId((String)objs[9]);
				moveSum.setQtyIn(((BigDecimal)objs[10]));
				moveSum.setQtyOut(((BigDecimal)objs[11]));
				moveSum.setQtyTotal(((BigDecimal)objs[12]));
				materialSums.add(moveSum);
			}
			return materialSums;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public MaterialLocator saveMaterialLocator(MaterialLocator ml, long userRrn) throws ClientException {
		long materialRrn = ml.getMaterialRrn();
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM Material M WHERE M.objectRrn = ? ");
		Query query = em.createQuery(hql.toString());
		query.setParameter(1, materialRrn);
		Material m = (Material) query.getSingleResult();
		if(m != null){
			if(m.getMaterialId() != null)
				ml.setMaterialId(m.getMaterialId());
			if(m.getName() != null)
				ml.setMaterialName(m.getName());
		}
		
		hql = new StringBuffer();
		hql.append(" FROM Warehouse w WHERE w.objectRrn = ? ");
		query = em.createQuery(hql.toString());
		query.setParameter(1, ml.getWarehouseRrn());
		Warehouse w = (Warehouse) query.getSingleResult();
		if(w != null && w.getWarehouseId() != null)
			ml.setWarehouseId(w.getWarehouseId());
		
		hql = new StringBuffer();
		hql.append(" FROM Locator l WHERE l.objectRrn = ? ");
		query = em.createQuery(hql.toString());
		query.setParameter(1, ml.getLocatorRrn());
		Locator l = (Locator) query.getSingleResult();
		if(l != null && l.getLocatorId() != null)
			ml.setLocatorId(l.getLocatorId());
		
		if(ml.getObjectRrn() == null){
			ml.setIsActive(true);
			ml.setCreated(new Date());
			ml.setCreatedBy(userRrn);
			em.persist(ml);
		}else{
			ml.setUpdated(new Date());
			ml.setUpdatedBy(userRrn);
			em.merge(ml);
		}
		return ml;
	}
	
	
	//手动核销,直接审核
	public void manualWriteOff(MovementWriteOff mw, List<MovementLine> lines, long userRrn) throws ClientException {
		try {
			Date now = new Date();
			mw.setIsActive(true);
			mw.setCreatedBy(userRrn);
			mw.setCreated(new Date());
			mw.setDateCreated(new Date());
			mw.setTotalLines(0L);
			mw.setDocType(MovementWriteOff.DOCTYPE_MWO);
			mw.setDocStatus(MovementWriteOff.STATUS_APPROVED);
			mw.setDocId(generateWriteOffCode(mw));
			ADUser user = em.find(ADUser.class, userRrn);
			mw.setUserCreated(user.getUserName());
			mw.setUserApproved(user.getUserName());
			mw.setDateApproved(now);
			Warehouse house = this.getWriteOffWarehouse(mw.getOrgRrn());
			mw.setWarehouseRrn(house.getObjectRrn());
			mw.setWarehouseId(house.getWarehouseId());	
			em.persist(mw);
			
			List<MovementLine> savaLine = new ArrayList<MovementLine>();
			for (MovementLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getQtyMovement().doubleValue() == 0) {
					continue;
				}
				
				List<MovementLineLot> lineLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
				line.setIsActive(true);
				line.setCreatedBy(userRrn);
				line.setCreated(new Date());
				line.setLineStatus(MovementWriteOff.STATUS_APPROVED);
				line.setMovementRrn(mw.getObjectRrn());
				line.setMovementId(mw.getDocId());
				mw.setTotalLines(mw.getTotalLines() + 1);
				em.persist(line);
				
//注释A：更新库存(qtyMovement为负时增加,为正时减少)
				updateStorage(mw.getOrgRrn(), line.getMaterialRrn(), mw.getWarehouseRrn(), line.getQtyMovement().negate(), true, userRrn);

				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					for (MovementLineLot lineLot : lineLots) {
						if(lineLot.getObjectRrn() == null) {
							lineLot.setMovementRrn(mw.getObjectRrn());
							lineLot.setMovementId(mw.getDocId());
							lineLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(lineLot);
						} else {
							lineLot.setMovementRrn(mw.getObjectRrn());
							lineLot.setMovementId(mw.getDocId());
							lineLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(lineLot);
						}
					}
					//更新批次数量(qtyMovement为负时增加,为正时减少),允许为负
					Lot lot = this.getMaterialLot(mw.getOrgRrn(), material, userRrn);
					this.updateLotStorage(mw.getOrgRrn(), lot.getObjectRrn(), mw.getWarehouseRrn(), line.getQtyMovement().negate(), userRrn);
				} else {
					for (MovementLineLot lineLot : lineLots) {
						if (lineLot.getLotRrn() == null) {
							List<Lot> lots = line.getLots();
							for (Lot lot : lots) {
								if (lot.getLotId().equals(lineLot.getLotId())) {
									em.persist(lot);
									lineLot.setLotRrn(lot.getObjectRrn());
									break;
								}
							}
						}
						Lot lot = em.find(Lot.class, lineLot.getLotRrn());
						//检查Lot位置
						
						 if (!Lot.POSITION_INSTOCK.equals(lot.getPosition())
								 && !Lot.POSITION_WIP.equals(lot.getPosition())) {
							throw new ClientParameterException("inv.lot_not_in", lot.getLotId());
						} 
						if (lot.getIsUsed()) {
							throw new ClientParameterException("inv.lot_already_used", lot.getLotId());
						}			
						
						if(lineLot.getObjectRrn() == null) {
							lineLot.setMovementRrn(mw.getObjectRrn());
							lineLot.setMovementId(mw.getDocId());
							lineLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(lineLot);
						} else {
							lineLot.setMovementRrn(mw.getObjectRrn());
							lineLot.setMovementId(mw.getDocId());
							lineLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(lineLot);
						}
						
						//更新批次数量(qtyMovement为负时增加,为正时减少),允许为负
						this.updateLotStorage(mw.getOrgRrn(), lineLot.getLotRrn(), mw.getWarehouseRrn(), lineLot.getQtyMovement().negate(), userRrn);
						
						if (Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
							lot.setIsUsed(true);
						}
						em.merge(lot);
					}
				}
				
				LotConsume lotConsume = new LotConsume();
				lotConsume.setOrgRrn(mw.getOrgRrn());
				lotConsume.setIsActive(true);
				lotConsume.setCreatedBy(userRrn);
				lotConsume.setUpdatedBy(userRrn);
				lotConsume.setCreated(new Date());
				lotConsume.setMaterialRrn(material.getObjectRrn());
				lotConsume.setMaterialId(material.getMaterialId());
				lotConsume.setMaterialName(material.getName());
				lotConsume.setMoRrn(mw.getMoRrn());
				lotConsume.setMoId(mw.getMoId());
				lotConsume.setInRrn(mw.getObjectRrn());
				lotConsume.setInId(mw.getDocId());
				lotConsume.setQtyConsume(line.getQtyMovement());
				lotConsume.setIsWin(false);
				lotConsume.setDateIn(now);
				lotConsume.setWarehouseRrn(mw.getWarehouseRrn());
				lotConsume.setWarehouseId(mw.getWarehouseId());
				lotConsume.setIsManual(true);
				lotConsume.setWriteoffType(mw.getWriteoffType());
				em.merge(lotConsume);
					
//注释A：处已经自动核销过了此处再算就重复了
//				Storage storage = getMaterialStorage(mw.getOrgRrn(), lotConsume.getMaterialRrn(), mw.getWarehouseRrn(), userRrn);
//				storage.setQtyWriteOff(storage.getQtyWriteOff().add(lotConsume.getQtyConsume().negate()));
//				storage.setUpdatedBy(userRrn);
//				em.merge(storage);
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			mw.setUpdatedBy(userRrn);
			mw.setMovementLines(null);
			em.merge(mw);
			mw.setMovementLines(savaLine);
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public List<ReceiptLine> getArrivedReceiptLines(long orgRrn)
			throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT rl ");
		sql.append(" FROM ReceiptLine rl,Receipt r ");
		sql.append(" WHERE rl.isActive = 'Y' ");
		sql.append(" AND r.orgRrn = :orgRrn ");
		sql.append(" AND r.objectRrn = rl.receiptRrn ");
		sql.append(" AND r.docStatus in ('APPROVED','DRAFTED') ");
		logger.debug(sql);
		
		Query query = em.createQuery(sql.toString());
		query.setParameter("orgRrn", orgRrn);
		return query.getResultList();
	}

	@Override
	public List<ReceiptLine> getUnInspectedReceiptLines(long orgRrn)
			throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT rl ");
		sql.append(" FROM ReceiptLine rl,Receipt r ");
		sql.append(" WHERE rl.isActive = 'Y' ");
		sql.append(" AND r.orgRrn = :orgRrn ");
		sql.append(" AND r.objectRrn = rl.receiptRrn ");
		sql.append(" AND r.isIqc='N' ");
		logger.debug(sql);
		
		Query query = em.createQuery(sql.toString());
		query.setParameter("orgRrn", orgRrn);
		return query.getResultList();
	}

	@Override
	public MaterialTrace traceMaterial(long materialRrn, String fromDate,
			String toDate) throws ClientException {

		  try{
		   MaterialTrace mt = new MaterialTrace();
		   
//入库
		   StringBuffer sql = new StringBuffer();
		   sql.append("  SELECT SUM(L.QTY_MOVEMENT) ");
		   sql.append("  FROM    INV_MOVEMENT_LINE L ");
		   sql.append("     INNER JOIN ");
		   sql.append("      INV_MOVEMENT M ");
		   sql.append("     ON  L.MOVEMENT_RRN = M.OBJECT_RRN ");
		   sql.append("      AND M.DOC_TYPE = ?1 ");
		   sql.append("      AND M.MOVEMENT_TYPE = 'I' ");
		   sql.append("  WHERE  L.MATERIAL_RRN = ?2 ");
		   sql.append("     AND L.LINE_STATUS = 'APPROVED' ");
		   
		   Query query = em.createNativeQuery(sql.toString());
		   /*
		    * 采购入库
		    */
		   query.setParameter(1, "PIN");
		   query.setParameter(2, materialRrn);
		   BigDecimal qtyPin = (BigDecimal) query.getSingleResult();
		   mt.setQtyPin(qtyPin);
		   
		   /*
		    * 生产入库
		    */
		   query.setParameter(1, "WIN");
		   query.setParameter(2, materialRrn);
		   BigDecimal qtyWin = (BigDecimal) query.getSingleResult();
		   mt.setQtyWin(qtyWin);
		   
		   /*
		    * 退库
		    */
		   query.setParameter(1, "RIN");
		   query.setParameter(2, materialRrn);
		   BigDecimal qtyRin = (BigDecimal) query.getSingleResult();
		   mt.setQtyRin(qtyRin);
		   
		   /*
		    * 其他入库
		    */
		   query.setParameter(1, "OIN");
		   query.setParameter(2, materialRrn);
		   BigDecimal qtyOin = (BigDecimal) query.getSingleResult();
		   mt.setQtyOin(qtyOin);
		   
		   /*
		    * 营运调整入库
		    */
		   query.setParameter(1, "ADIN");
		   query.setParameter(2, materialRrn);
		   BigDecimal qtyAdIn = (BigDecimal) query.getSingleResult();
		   mt.setQtyAdIn(qtyAdIn);
		   
//出库
		   sql = new StringBuffer();
		   sql.append("  SELECT SUM(L.QTY_MOVEMENT) ");
		   sql.append("  FROM    INV_MOVEMENT_LINE L ");
		   sql.append("     INNER JOIN ");
		   sql.append("      INV_MOVEMENT M ");
		   sql.append("     ON  L.MOVEMENT_RRN = M.OBJECT_RRN ");
		   sql.append("      AND M.DOC_TYPE = ?1 ");
		   sql.append("      AND M.MOVEMENT_TYPE = 'O' ");
		   sql.append("  WHERE  L.MATERIAL_RRN = ?2 ");
		   sql.append("     AND L.LINE_STATUS = 'APPROVED' ");
		   
		   query = em.createNativeQuery(sql.toString());
		   
		   /*
		    * 销售出库
		    */
		   query.setParameter(1, "SOU");
		   query.setParameter(2, materialRrn);
		   BigDecimal qtySou = (BigDecimal) query.getSingleResult();
		   mt.setQtySou(qtySou);
		   
		   /*
		    * 其他出库
		    */
		   query.setParameter(1, "OOU");
		   query.setParameter(2, materialRrn);
		   BigDecimal qtyOou = (BigDecimal) query.getSingleResult();
		   mt.setQtyOou(qtyOou);
		   

		   /*
		    * 营运调整出库
		    */
		   query.setParameter(1, "ADOU");
		   query.setParameter(2, materialRrn);
		   BigDecimal qtyAdOu = (BigDecimal) query.getSingleResult();
		   mt.setQtyAdOu(qtyAdOu);
		   
//财务调整
		   sql = new StringBuffer();
		   sql.append("  SELECT ABS(SUM(L.QTY_MOVEMENT)) ");
		   sql.append("  FROM    INV_MOVEMENT_LINE L ");
		   sql.append("     INNER JOIN ");
		   sql.append("      INV_MOVEMENT M ");
		   sql.append("     ON  L.MOVEMENT_RRN = M.OBJECT_RRN ");
		   sql.append("      AND M.DOC_TYPE = ?1 ");
		   sql.append("      AND M.MOVEMENT_TYPE = 'O' ");
		   sql.append("      AND M.OUT_TYPE = ?2 ");
		   sql.append("  WHERE  L.MATERIAL_RRN = ?3 ");
		   sql.append("     AND L.LINE_STATUS = 'APPROVED' ");
		   
		   query = em.createNativeQuery(sql.toString());
		   
		   /*
		    * 入库调整
		    */
		   query.setParameter(1, "AOU");
		   query.setParameter(2, MovementOut.OUT_TYPE_IN_ADJUST);
		   query.setParameter(3, materialRrn);
		   BigDecimal qtyAouIn = (BigDecimal) query.getSingleResult();
		   mt.setQtyAouIn(qtyAouIn);
		   
		   /*
		    * 出库调整
		    */
		   query.setParameter(1, "AOU");
		   query.setParameter(2, MovementOut.OUT_TYPE_OUT_ADJUST);
		   query.setParameter(3, materialRrn);
		   BigDecimal qtyAouOu = (BigDecimal) query.getSingleResult();
		   mt.setQtyAouOu(qtyAouOu);
		   
		//手工核销
		   sql = new StringBuffer();
		   sql.append("  SELECT SUM(L.QTY_MOVEMENT) ");
		   sql.append("  FROM    INV_MOVEMENT_LINE L ");
		   sql.append("     INNER JOIN ");
		   sql.append("      INV_MOVEMENT M ");
		   sql.append("     ON  L.MOVEMENT_RRN = M.OBJECT_RRN ");
		   sql.append("      AND M.DOC_TYPE = ?1 ");
		   sql.append("      AND M.MOVEMENT_TYPE = 'W' ");
		   sql.append("  WHERE  L.MATERIAL_RRN = ?2 ");
		   sql.append("     AND L.LINE_STATUS = 'APPROVED' ");
		   
		   query = em.createNativeQuery(sql.toString());
		   query.setParameter(1, "MWO");
		   query.setParameter(2, materialRrn);
		   BigDecimal qtyMwo = (BigDecimal) query.getSingleResult();
		   mt.setQtyMwo(qtyMwo);
		   
		 //生产接受
		   sql = new StringBuffer();
		   sql.append(" SELECT sum(qty_receive) ");
		   sql.append(" FROM wip_mo_line ");
		   sql.append(" WHERE material_rrn = ? ");
		   
		   query = em.createNativeQuery(sql.toString());
		   query.setParameter(1, materialRrn);
		   BigDecimal qtyMo = (BigDecimal) query.getSingleResult();
		   if(qtyMo != null && qtyWin != null){
			   qtyMo = qtyMo.subtract(qtyWin);
		   }
		   mt.setQtyMo(qtyMo);
		   
		//生产消耗
		   sql = new StringBuffer();
		   sql.append(" SELECT sum(qty_consume) ");
		   sql.append(" FROM wip_lot_component ");
		   sql.append(" WHERE material_child_rrn = ? AND qty_consume > 0 ");

		   query = em.createNativeQuery(sql.toString());
		   query.setParameter(1, materialRrn);
		   BigDecimal qtyConsume = (BigDecimal) query.getSingleResult();
		   mt.setQtyConsume(qtyConsume);
		   
		//拆分  
		   
		   /*
		    * 拆分出
		    */
		   sql = new StringBuffer();
		   sql.append("  SELECT SUM(NVL(H.QTY_TRANSACTION,H.QTY_CURRENT)) ");
		   sql.append("  FROM   WIPHIS_LOT H ");
		   sql.append("  WHERE  H.TRANS_TYPE = 'DISASSEMBLE' ");
		   sql.append(" 		  AND H.MATERIAL_RRN = ?1 ");
		   
		   query = em.createNativeQuery(sql.toString());
		   query.setParameter(1, materialRrn);
		   BigDecimal qtyDisOu = (BigDecimal) query.getSingleResult();
		   mt.setQtyDisassembleOu(qtyDisOu);
		   
		   /*
		    * 拆分入
		    */
		   sql = new StringBuffer();
		   sql.append("  SELECT ABS(SUM(C.QTY_CONSUME)) ");
		   sql.append("  FROM   WIP_LOT_COMPONENT C ");
		   sql.append("  WHERE  C.MATERIAL_CHILD_RRN = ?1 ");
		   sql.append(" 		  AND C.QTY_CONSUME < 0 ");

		   query = em.createNativeQuery(sql.toString());
		   query.setParameter(1, materialRrn);
		   BigDecimal qtyDisIn = (BigDecimal) query.getSingleResult();
		   mt.setQtyDisassembleIn(qtyDisIn);

		   return mt;
		  }catch(Exception e){
		   throw new ClientException(e);
		  }
	}

	@Override
	public List<MaterialTraceDetail> traceMaterialDetail(long materialRrn,
			String fromDate, String toDate, String detailType)
			throws ClientException {

		  try{
		   List<MaterialTraceDetail> resultList = new ArrayList<MaterialTraceDetail>();
		   
		   if( MaterialTraceDetail.DETAIL_TYPE_PIN.equals(detailType)
		    || MaterialTraceDetail.DETAIL_TYPE_WIN.equals(detailType)
		    || MaterialTraceDetail.DETAIL_TYPE_RIN.equals(detailType)
		    || MaterialTraceDetail.DETAIL_TYPE_OIN.equals(detailType)
		    || MaterialTraceDetail.DETAIL_TYPE_ADIN.equals(detailType)){

		//入库
		    StringBuffer sql = new StringBuffer();
		    sql.append("  SELECT M.DOC_ID, ");
		    sql.append("     M.PO_ID, ");
		    sql.append("     M.RECEIPT_ID, ");
		    sql.append("     L.QTY_MOVEMENT ");
		    sql.append("  FROM    INV_MOVEMENT_LINE L ");
		    sql.append("     INNER JOIN ");
		    sql.append("      INV_MOVEMENT M ");
		    sql.append("     ON  L.MOVEMENT_RRN = M.OBJECT_RRN ");
		    sql.append("      AND M.DOC_TYPE = ?1 ");
		    sql.append("      AND M.MOVEMENT_TYPE = 'I' ");
		    sql.append("  WHERE  L.MATERIAL_RRN = ?2 ");
		    sql.append("     AND L.LINE_STATUS = 'APPROVED' ");
		    
		    Query query = em.createNativeQuery(sql.toString());

		    query.setParameter(1, detailType);
		    query.setParameter(2, materialRrn);
		    List<Object[]> rslt = query.getResultList();
		    
		    for(Object[] v : rslt){
		    	MaterialTraceDetail mtd = new MaterialTraceDetail();
		    	if(v[0] != null) mtd.setMovementId((String) v[0]);
		    	if(v[1] != null) mtd.setPoId((String) v[1]);
		    	if(v[2] != null) mtd.setReceiptId((String) v[2]);
		    	if(v[3] != null) mtd.setQty((BigDecimal) v[3]);
		    	resultList.add(mtd);
		    }
		   }else if(MaterialTraceDetail.DETAIL_TYPE_SOU.equals(detailType)
				   || MaterialTraceDetail.DETAIL_TYPE_OOU.equals(detailType)
				   || MaterialTraceDetail.DETAIL_TYPE_ADOU.equals(detailType)){
//出库
			   StringBuffer sql = new StringBuffer();
			   sql.append("  SELECT M.DOC_ID, ");
			   sql.append("     M.PO_ID, ");
			   sql.append("     M.RECEIPT_ID, ");
			   sql.append("     L.QTY_MOVEMENT ");
			   sql.append("  FROM    INV_MOVEMENT_LINE L ");
			   sql.append("     INNER JOIN ");
			   sql.append("      INV_MOVEMENT M ");
			   sql.append("     ON  L.MOVEMENT_RRN = M.OBJECT_RRN ");
			   sql.append("      AND M.DOC_TYPE = ?1 ");
			   sql.append("      AND M.MOVEMENT_TYPE = 'O' ");
			   sql.append("  WHERE  L.MATERIAL_RRN = ?2 ");
			   sql.append("     AND L.LINE_STATUS = 'APPROVED' ");
			   
			   Query query = em.createNativeQuery(sql.toString());

				query.setParameter(1, detailType);
				query.setParameter(2, materialRrn);
				List<Object[]> rslt = query.getResultList();
				
				for(Object[] v : rslt){
					MaterialTraceDetail mtd = new MaterialTraceDetail();
					if(v[0] != null) mtd.setMovementId((String) v[0]);
					if(v[1] != null) mtd.setPoId((String) v[1]);
					if(v[2] != null) mtd.setReceiptId((String) v[2]);
					if(v[3] != null) mtd.setQty((BigDecimal) v[3]);
					resultList.add(mtd);
				}

		   }else if(MaterialTraceDetail.DETAIL_TYPE_AOUIN.equals(detailType)
				   || MaterialTraceDetail.DETAIL_TYPE_AOUOU.equals(detailType)){
//财务调整
			   StringBuffer sql = new StringBuffer();
			   sql.append("  SELECT M.DOC_ID, ");
			   sql.append("     M.PO_ID, ");
			   sql.append("     M.RECEIPT_ID, ");
			   sql.append("     L.QTY_MOVEMENT ");
			   sql.append("  FROM    INV_MOVEMENT_LINE L ");
			   sql.append("     INNER JOIN ");
			   sql.append("      INV_MOVEMENT M ");
			   sql.append("     ON  L.MOVEMENT_RRN = M.OBJECT_RRN ");
			   sql.append("      AND M.DOC_TYPE = ?1 ");
			   sql.append("      AND M.MOVEMENT_TYPE = 'O' ");
			   sql.append("      AND M.OUT_TYPE = ?2 ");
			   sql.append("  WHERE  L.MATERIAL_RRN = ?3 ");
			   sql.append("     AND L.LINE_STATUS = 'APPROVED' ");
			   
			   Query query = em.createNativeQuery(sql.toString());
			   
			   
			   if(MaterialTraceDetail.DETAIL_TYPE_AOUIN.equals(detailType)){
				   /*
				    * 入库调整
				    */
				   query.setParameter(1, "AOU");
				   query.setParameter(2, MovementOut.OUT_TYPE_IN_ADJUST);
				   query.setParameter(3, materialRrn);
			   }else{
				   /*
				    * 出库调整
				    */
				   query.setParameter(1, "AOU");
				   query.setParameter(2, MovementOut.OUT_TYPE_OUT_ADJUST);
				   query.setParameter(3, materialRrn);
			   }
			   
			   List<Object[]> rslt = query.getResultList();
				
				for(Object[] v : rslt){
					MaterialTraceDetail mtd = new MaterialTraceDetail();
					if(v[0] != null) mtd.setMovementId((String) v[0]);
					if(v[1] != null) mtd.setPoId((String) v[1]);
					if(v[2] != null) mtd.setReceiptId((String) v[2]);
					if(v[3] != null) mtd.setQty((BigDecimal) v[3]);
					resultList.add(mtd);
				}
		   }else if(MaterialTraceDetail.DETAIL_TYPE_MWO.equals(detailType)){
			   StringBuffer sql = new StringBuffer();
			   sql.append("  SELECT M.DOC_ID, ");
			   sql.append("     M.PO_ID, ");
			   sql.append("     M.RECEIPT_ID, ");
			   sql.append("     L.QTY_MOVEMENT ");
			   sql.append("  FROM    INV_MOVEMENT_LINE L ");
			   sql.append("     INNER JOIN ");
			   sql.append("      INV_MOVEMENT M ");
			   sql.append("     ON  L.MOVEMENT_RRN = M.OBJECT_RRN ");
			   sql.append("      AND M.DOC_TYPE = ?1 ");
			   sql.append("      AND M.MOVEMENT_TYPE = 'W' ");
			   sql.append("  WHERE  L.MATERIAL_RRN = ?2 ");
			   sql.append("     AND L.LINE_STATUS = 'APPROVED' ");
			   
			   Query query = em.createNativeQuery(sql.toString());

			    query.setParameter(1, detailType);
			    query.setParameter(2, materialRrn);
			    List<Object[]> rslt = query.getResultList();
			    
			    for(Object[] v : rslt){
			    	MaterialTraceDetail mtd = new MaterialTraceDetail();
			    	if(v[0] != null) mtd.setMovementId((String) v[0]);
			    	if(v[1] != null) mtd.setPoId((String) v[1]);
			    	if(v[2] != null) mtd.setReceiptId((String) v[2]);
			    	if(v[3] != null) mtd.setQty((BigDecimal) v[3]);
			    	resultList.add(mtd);
			    }
		   }
		   
		   
		   return resultList;
		  }catch (Exception e){
		   throw new ClientException(e);
		  }
	}

	@Override
	public List<VOutDetail> getOutDetails(Map<String,Object> queryKeys, String whereClause)
			throws ClientException {
		Connection conn = null;
		Statement stmt = null;
		try{
			String sql = "SELECT ADMessage FROM ADMessage ADMessage WHERE ADMessage.key like 'OUTQUERY%' ORDER BY ADMessage.key";
			Query msgQuery = em.createQuery(sql);
			List<ADMessage> messages = msgQuery.getResultList();
			
			Map<String, Map<String, String>> columnMap = new HashMap<String, Map<String, String>>();
			
			for (ADMessage msg : messages){
				String originalKey = msg.getKey();
				String[] subKeys = originalKey.split("\\.");
				if(!columnMap.containsKey(subKeys[1])){
					Map<String, String> columnNames = new HashMap<String, String>();
					columnNames.put(subKeys[2], msg.getMessage());
					columnMap.put(subKeys[1], columnNames);
				}else{
					Map<String, String> columnNames = columnMap.get(subKeys[1]);
					columnNames.put(subKeys[2], msg.getMessage());
				}
			}
			
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
			StringBuffer viewSql = new StringBuffer();
			viewSql.append( " SELECT ROWNUM OBJECT_RRN, RS.*, V.COMPANY_NAME VENDOR_NAME "); 
			 viewSql.append( " FROM (SELECT M.ORG_RRN ORG_RRN, "); 
			 viewSql.append( " M.IS_ACTIVE IS_ACTIVE, "); 
			 viewSql.append( " M.DOC_ID, "); 
			 viewSql.append( " M.OBJECT_RRN MOVMENT_RRN, "); 
			 viewSql.append( " M.DOC_STATUS, "); 
			 viewSql.append( " M.DOC_TYPE, "); 
			 viewSql.append( " M.DATE_CREATED, "); 
			 viewSql.append( " M.DATE_APPROVED, "); 
			 viewSql.append( " M.WAREHOUSE_RRN, "); 
			 viewSql.append( " M.WAREHOUSE_ID, "); 
			 viewSql.append( " M.CUSTOMER_NAME, "); 
			 viewSql.append( " M.SELLER, "); 
			 viewSql.append( " M.USER_CREATED, "); 
			 viewSql.append( " M.USER_APPROVED, "); 
			 viewSql.append( " M.USER_IQC, "); 
			 viewSql.append( " M.DESCRIPTION, "); 
			 viewSql.append( " M.OUT_TYPE, "); 
			 viewSql.append( " TRUNC(M.DATE_APPROVED) MOVEMENT_DATE, "); 
			 viewSql.append( " M.KIND, "); 
			 viewSql.append( " M.SO_ID, "); 
			 viewSql.append( " M.VENDOR_RRN, "); 
			 viewSql.append( " M.VENDOR_ID, "); 
			 viewSql.append( " L.MATERIAL_RRN, "); 
			 viewSql.append( " L.MATERIAL_ID, "); 
			 viewSql.append( " L.MATERIAL_NAME, "); 
			 viewSql.append( " L.UOM_ID, "); 
			 viewSql.append( " L.QTY_MOVEMENT, "); 
			 viewSql.append( " L.UNIT_PRICE, "); 
			 viewSql.append( " L.LINE_TOTAL, "); 
			 viewSql.append( " L.LOCATOR_ID "); 
			 viewSql.append( " FROM INV_MOVEMENT M INNER JOIN INV_MOVEMENT_LINE L "); 
			 viewSql.append( " ON ((M.MOVEMENT_TYPE IN ('O', 'T') AND M.Doc_Type <> 'AOU') OR (M.Movement_Type = 'O' AND M.Doc_Type = 'AOU' AND M.Out_Type IN ('出库调整','销售红冲'))) "); 
			 viewSql.append( " AND L.MOVEMENT_RRN = M.OBJECT_RRN "); 
			 
			 StringBuffer localWhereClause1 = new StringBuffer();
			 Map<String, String> queryColumns = columnMap.get("Movement");
			 if(queryColumns != null && !queryColumns.isEmpty()){
				 for(String key : queryKeys.keySet()){
					 String s = queryColumns.get(key);
					 if(s != null && s.trim().length() != 0){
						 Object val = queryKeys.get(key);
						 if(val instanceof String){
							 String str = (String)val;
							 if(str.indexOf("%") != -1){
								 localWhereClause1.append(" AND " + s + " like '" + queryKeys.get(key) + "' ");
							 }else{
								 localWhereClause1.append(" AND " + s + " = '" + queryKeys.get(key) + "' ");
							 }
						 }else if(val instanceof Map){//FromToDate
							 Map m = (Map)val;
								Date from = (Date) m.get("from");
								Date to = (Date) m.get("to");
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						        formatter.setLenient(false);
						        
								StringBuffer sb = new StringBuffer();
								if(from != null) {
									sb.append(" AND trunc(");
									sb.append(s);
									sb.append(") >= TO_DATE('" + formatter.format(from) +"', 'yyyy-MM-dd') ");
								}
								
								if(to != null){
									sb.append(" AND trunc(");
									sb.append(s);
									sb.append(") <= TO_DATE('" + formatter.format(to) + "', 'yyyy-MM-dd') ");
								}
								localWhereClause1.append(sb.toString());
						 }
					 }
				 }
			 }
			 
			 viewSql.append( localWhereClause1.toString()); 
			 
			 if(queryKeys.isEmpty() || !(queryKeys.containsKey("docType") 
					 && queryKeys.get("docType") != null)){
	
				 viewSql.append( " UNION ALL "); 
				 viewSql.append( " SELECT LM.ORG_RRN ORG_RRN, "); 
				 viewSql.append( " LM.IS_ACTIVE IS_ACTIVE, "); 
				 viewSql.append( " ' ' AS DOC_ID, "); 
				 viewSql.append( " NULL AS MOVMENT_RRN, "); 
				 viewSql.append( " ' ' AS DOC_STATUS, "); 
				 viewSql.append( " 'COU' AS DOC_TYPE, "); 
				 viewSql.append( " LM.CREATED AS DATE_CREATED, "); 
				 viewSql.append( " LM.CREATED AS DATE_APPROVED, "); 
				 viewSql.append( " NULL AS WAREHOUSE_RRN, "); 
				 viewSql.append( " ' ' AS WAREHOUSE_ID, "); 
				 viewSql.append( " ' ' AS CUSTOMER_NAME, "); 
				 viewSql.append( " ' ' AS SELLER, "); 
				 viewSql.append( " U1.USER_NAME USER_CREATED, "); 
				 viewSql.append( " U2.USER_NAME USER_APPROVED, "); 
				 viewSql.append( " ' ' AS USER_IQC, "); 
				 viewSql.append( " ' ' AS DESCRIPTION, "); 
				 viewSql.append( " 'RECEIVE' OUT_TYPE, "); 
				 viewSql.append( " TRUNC(LM.CREATED) MOVEMENT_DATE, "); 
				 viewSql.append( " ' ' AS KIND, "); 
				 viewSql.append( " ' ' AS SO_ID, "); 
				 viewSql.append( " NULL AS VENDOR_RRN, "); 
				 viewSql.append( " ' ' AS VENDOR_ID, "); 
				 viewSql.append( " LM.MATERIAL_CHILD_RRN MATERIAL_RRN, "); 
				 viewSql.append( " LM.MATERIAL_CHILD_ID MATERIAL_ID, "); 
				 viewSql.append( " LM.MATERIAL_CHILD_NAME MATERIAL_NAME, "); 
				 viewSql.append( " ' ' AS UOM_ID, "); 
				 viewSql.append( " LM.QTY_CONSUME, "); 
				 viewSql.append( " NULL AS UNIT_PRICE, "); 
				 viewSql.append( " NULL AS LINE_TOTAL, "); 
				 viewSql.append( " ' ' AS LOCATOR_ID "); 
				 viewSql.append( " "); 
				 viewSql.append( " FROM (SELECT IS_ACTIVE, "); 
				 viewSql.append( " ORG_RRN, "); 
				 viewSql.append( " MAX(CREATED) CREATED, "); 
				 viewSql.append( " CREATED_BY, "); 
				 viewSql.append( " UPDATED_BY, "); 
				 viewSql.append( " MATERIAL_CHILD_RRN, "); 
				 viewSql.append( " MATERIAL_CHILD_ID, "); 
				 viewSql.append( " MATERIAL_CHILD_NAME, "); 
				 viewSql.append( " MO_LINE_RRN, "); 
				 viewSql.append( " SUM(QTY_CONSUME) QTY_CONSUME "); 
				 viewSql.append( " FROM WIP_LOT_COMPONENT "); 
				 viewSql.append( " GROUP BY IS_ACTIVE, "); 
				 viewSql.append( " ORG_RRN, "); 
				 viewSql.append( " CREATED_BY, "); 
				 viewSql.append( " UPDATED_BY, "); 
				 viewSql.append( " MATERIAL_CHILD_RRN, "); 
				 viewSql.append( " MATERIAL_CHILD_ID, "); 
				 viewSql.append( " MATERIAL_CHILD_NAME, "); 
				 viewSql.append( " MO_LINE_RRN) LM "); 
				 viewSql.append( " INNER JOIN AD_USER U1 "); 
				 viewSql.append( " ON U1.OBJECT_RRN = LM.CREATED_BY "); 
				 viewSql.append( " INNER JOIN AD_USER U2 "); 
				 viewSql.append( " ON U2.OBJECT_RRN = LM.UPDATED_BY "); 
				 viewSql.append( " WHERE 1=1 "); 
	
				 StringBuffer localWhereClause2 = new StringBuffer();
				 queryColumns = columnMap.get("Component");
				 if(queryColumns != null && !queryColumns.isEmpty()){
					 for(String key : queryKeys.keySet()){
						 String s = queryColumns.get(key);
						 if(s != null && s.trim().length() != 0){
							 s = "LM." + s;
							 Object val = queryKeys.get(key);
							 if(val instanceof String){
								 String str = (String)val;
								 if(str.indexOf("%") != -1){
									 localWhereClause2.append(" AND " + s + " like '" + queryKeys.get(key) + "' ");
								 }else{
									 localWhereClause2.append(" AND " + s + " = '" + queryKeys.get(key) + "' ");
								 }
							 }else if(val instanceof Map){//FromToDate
								 Map m = (Map)val;
									Date from = (Date) m.get("from");
									Date to = (Date) m.get("to");
									SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							        formatter.setLenient(false);
							        
									StringBuffer sb = new StringBuffer();
									if(from != null) {
										sb.append(" AND trunc(");
										sb.append(s);
										sb.append(") >= TO_DATE('" + formatter.format(from) +"', 'yyyy-MM-dd') ");
									}
									
									if(to != null){
										sb.append(" AND trunc(");
										sb.append(s);
										sb.append(") <= TO_DATE('" + formatter.format(to) + "', 'yyyy-MM-dd') ");
									}
									localWhereClause2.append(sb.toString());
							 }
						 }
					 }
				 }
				 
				 viewSql.append( localWhereClause2.toString()); 
			 
			 
				 viewSql.append( " UNION ALL "); 
				 viewSql.append( " SELECT LC.ORG_RRN ORG_RRN, "); 
				 viewSql.append( " LC.IS_ACTIVE IS_ACTIVE, "); 
				 viewSql.append( " LC.MO_ID AS DOC_ID, "); 
				 viewSql.append( " NULL AS MOVMENT_RRN, "); 
				 viewSql.append( " ' ' AS DOC_STATUS, "); 
				 viewSql.append( " 'NOU' AS DOC_TYPE, "); 
				 viewSql.append( " LC.CREATED AS DATE_CREATED, "); 
				 viewSql.append( " LC.CREATED AS DATE_APPROVED, "); 
				 viewSql.append( " LC.WAREHOUSE_RRN WAREHOUSE_RRN, "); 
				 viewSql.append( " LC.WAREHOUSE_ID WAREHOUSE_ID, "); 
				 viewSql.append( " ' ' AS CUSTOMER_NAME, "); 
				 viewSql.append( " ' ' AS SELLER, "); 
				 viewSql.append( " U1.USER_NAME USER_CREATED, "); 
				 viewSql.append( " U2.USER_NAME USER_APPROVED, "); 
				 viewSql.append( " ' ' AS USER_IQC, "); 
				 viewSql.append( " ' ' AS DESCRIPTION, "); 
				 viewSql.append( " 'WRITEOFF' OUT_TYPE, "); 
				 viewSql.append( " TRUNC(LC.CREATED) MOVEMENT_DATE, "); 
				 viewSql.append( " ' ' AS KIND, "); 
				 viewSql.append( " ' ' AS SO_ID, "); 
				 viewSql.append( " NULL AS VENDOR_RRN, "); 
				 viewSql.append( " ' ' AS VENDOR_ID, "); 
				 viewSql.append( " LC.MATERIAL_RRN MATERIAL_RRN, "); 
				 viewSql.append( " LC.MATERIAL_ID MATERIAL_ID, "); 
				 viewSql.append( " LC.MATERIAL_NAME MATERIAL_NAME, "); 
				 viewSql.append( " ' ' AS UOM_ID, "); 
				 viewSql.append( " LC.QTY_CONSUME, "); 
				 viewSql.append( " NULL AS UNIT_PRICE, "); 
				 viewSql.append( " NULL AS LINE_TOTAL, "); 
				 viewSql.append( " ' ' AS LOCATOR_ID "); 
				 viewSql.append( " FROM (SELECT IS_ACTIVE, "); 
				 viewSql.append( " ORG_RRN, "); 
				 viewSql.append( " MO_ID, "); 
				 viewSql.append( " MAX(CREATED) CREATED, "); 
				 viewSql.append( " CREATED_BY, "); 
				 viewSql.append( " UPDATED_BY, "); 
				 viewSql.append( " MATERIAL_RRN, "); 
				 viewSql.append( " MATERIAL_ID, "); 
				 viewSql.append( " MATERIAL_NAME, "); 
				 viewSql.append( " T.MO_RRN, "); 
				 viewSql.append( " SUM(QTY_CONSUME) QTY_CONSUME, "); 
				 viewSql.append( " WAREHOUSE_RRN, "); 
				 viewSql.append( " WAREHOUSE_ID "); 
				 viewSql.append( " FROM WIP_LOT_CONSUME T "); 
				 viewSql.append( " WHERE 1=1 "); 
	
				 StringBuffer localWhereClause3 = new StringBuffer();
				 queryColumns = columnMap.get("Consume");
				 if(queryColumns != null && !queryColumns.isEmpty()){
					 for(String key : queryKeys.keySet()){
						 String s = queryColumns.get(key);
						 if(s != null && s.trim().length() != 0){
							 Object val = queryKeys.get(key);
							 if(val instanceof String){
								 String str = (String)val;
								 if(str.indexOf("%") != -1){
									 localWhereClause3.append(" AND " + s + " like '" + queryKeys.get(key) + "' ");
								 }else{
									 localWhereClause3.append(" AND " + s + " = '" + queryKeys.get(key) + "' ");
								 }
							 }else if(val instanceof Map){//FromToDate
								 Map m = (Map)val;
									Date from = (Date) m.get("from");
									Date to = (Date) m.get("to");
									SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							        formatter.setLenient(false);
							        
									StringBuffer sb = new StringBuffer();
									if(from != null) {
										sb.append(" AND trunc(");
										sb.append(s);
										sb.append(") >= TO_DATE('" + formatter.format(from) +"', 'yyyy-MM-dd') ");
									}
									
									if(to != null){
										sb.append(" AND trunc(");
										sb.append(s);
										sb.append(") <= TO_DATE('" + formatter.format(to) + "', 'yyyy-MM-dd') ");
									}
									localWhereClause3.append(sb.toString());
							 }
						 }
					 }
				 }
				 
				 viewSql.append( localWhereClause3.toString()); 

				 viewSql.append( " GROUP BY IS_ACTIVE, "); 
				 viewSql.append( " ORG_RRN, "); 
				 viewSql.append( " MO_ID, "); 
				 viewSql.append( " CREATED_BY, "); 
				 viewSql.append( " UPDATED_BY, "); 
				 viewSql.append( " MATERIAL_RRN, "); 
				 viewSql.append( " MATERIAL_ID, "); 
				 viewSql.append( " MATERIAL_NAME, "); 
				 viewSql.append( " MO_RRN, "); 
				 viewSql.append( " WAREHOUSE_RRN, "); 
				 viewSql.append( " WAREHOUSE_ID) LC "); 
				 viewSql.append( " INNER JOIN AD_USER U1 "); 
				 viewSql.append( " ON U1.OBJECT_RRN = LC.CREATED_BY "); 
				 viewSql.append( " INNER JOIN AD_USER U2 "); 
				 viewSql.append( " ON U2.OBJECT_RRN = LC.UPDATED_BY "); 
			 }
			 viewSql.append( " ) RS "); 
			 viewSql.append( " LEFT JOIN VDM_VENDOR V ON V.OBJECT_RRN = RS.VENDOR_RRN "); 
			 
			 
			 StringBuffer selectSql = new StringBuffer();
			 selectSql.append(" SELECT * ");
			 selectSql.append(" FROM ( ");
			 selectSql.append( viewSql );
			 selectSql.append( " ) WHERE 1 = 1 " );
			 selectSql.append( " AND " + whereClause );

			//因为ejb查询慢，换成jdbc
			//Query query = em.createNativeQuery(selectSql.toString());
			 
//			List<Object[]> objs = query.getResultList();
			ResultSet rs = stmt.executeQuery(selectSql.toString());
			
			List<VOutDetail> vods = new ArrayList<VOutDetail>();
//			for(Object[] obj : objs){
//				VOutDetail vod = new VOutDetail();
//				vod.setObjectRrn(((BigDecimal) obj[0]).longValue());
//				if(obj[1] != null) vod.setOrgRrn(((BigDecimal) obj[1]).longValue());
//				if(obj[2] != null) vod.setIsActive("Y".equalsIgnoreCase((String) obj[2]));
//				if(obj[3] != null) vod.setDocId((String) obj[3]);
//				if(obj[4] != null) vod.setMovmentRrn(((BigDecimal) obj[4]).longValue());
//				if(obj[5] != null) vod.setDocStatus((String) obj[5]);
//				if(obj[6] != null) vod.setDocType((String) obj[6]);
//				if(obj[7] != null) vod.setDateCreated((Date) obj[7]);
//				if(obj[8] != null) vod.setDateApproved((Date) obj[8]);
//				if(obj[9] != null) vod.setWarehouseRrn(((BigDecimal) obj[9]).longValue());
//				if(obj[10] != null) vod.setWarehouseId((String) obj[10]);
//				if(obj[11] != null) vod.setCustomerName((String) obj[11]);
//				if(obj[12] != null) vod.setSeller((String) obj[12]);
//				if(obj[13] != null) vod.setUserCreated((String) obj[13]);
//				if(obj[14] != null) vod.setUserApproved((String) obj[14]);
//				if(obj[15] != null) vod.setUserIqc((String) obj[15]);
//				if(obj[16] != null) vod.setDescription((String) obj[16]);
//				if(obj[17] != null) vod.setOutType((String) obj[17]);
//				if(obj[18] != null) vod.setMovementDate((Date) obj[18]);
//				if(obj[19] != null) vod.setKind((String) obj[19]);
//				if(obj[20] != null) vod.setSoId((String) obj[20]);
//				if(obj[21] != null) vod.setVendorRrn(((BigDecimal) obj[21]).longValue());
//				if(obj[22] != null) vod.setVendorId((String) obj[22]);
//				if(obj[23] != null) vod.setMaterialRrn(((BigDecimal) obj[23]).longValue());
//				if(obj[24] != null) vod.setMaterialId((String) obj[24]);
//				if(obj[25] != null) vod.setMaterialName((String) obj[25]);
//				if(obj[26] != null) vod.setUomId((String) obj[26]);
//				if(obj[27] != null) vod.setQtyMovement((BigDecimal) obj[27]);
//				if(obj[28] != null) vod.setUnitPrice((BigDecimal) obj[28]);
//				if(obj[29] != null) vod.setLineTotal((BigDecimal) obj[29]);
//				if(obj[30] != null) vod.setLocatorId((String) obj[30]);
//				if(obj[31] != null) vod.setVendorName((String) obj[31]);
//				
//				vods.add(vod);
//			}
			while(rs.next()){
				VOutDetail vod = new VOutDetail();
				vod.setObjectRrn(rs.getLong(1));
				vod.setOrgRrn(rs.getLong(2));
				vod.setIsActive("Y".equalsIgnoreCase(rs.getString(3)));
				vod.setDocId(rs.getString(4));
				vod.setMovmentRrn(rs.getLong(5));
				vod.setDocStatus(rs.getString(6));
				vod.setDocType(rs.getString(7));
				vod.setDateCreated(rs.getDate(8));
				vod.setDateApproved(rs.getDate(9));
				vod.setWarehouseRrn(rs.getLong(10));
				vod.setWarehouseId(rs.getString(11));
				vod.setCustomerName(rs.getString(12));
				vod.setSeller(rs.getString(13));
				vod.setUserCreated(rs.getString(15));
				vod.setUserApproved(rs.getString(15));
				vod.setUserIqc(rs.getString(16));
				vod.setDescription(rs.getString(17));
				vod.setOutType(rs.getString(18));
				vod.setMovementDate(rs.getDate(19));
				vod.setKind(rs.getString(20));
				vod.setSoId(rs.getString(21));
				vod.setVendorRrn(rs.getLong(22));
				vod.setVendorId(rs.getString(23));
				vod.setMaterialRrn(rs.getLong(24));
				vod.setMaterialId(rs.getString(25));
				vod.setMaterialName(rs.getString(26));
				vod.setUomId(rs.getString(27));
				vod.setQtyMovement(BigDecimal.valueOf(rs.getDouble(28)));
				vod.setUnitPrice(BigDecimal.valueOf(rs.getDouble(29)));
				vod.setLineTotal(BigDecimal.valueOf(rs.getLong(30)));
				vod.setLocatorId(rs.getString(31));
				vod.setVendorName(rs.getString(32));
				
				vods.add(vod);
			}
			
			rs.close();
			stmt.close();
			conn.close();
			
			return vods;
		} catch (Exception e){
			throw new ClientException(e);
		}
	}

	public List<Material> queryMaterialQtys(Long orgRrn, Long materialRrn) throws ClientException {
		try {
			return queryMaterialQtys(orgRrn, materialRrn, "外购");
		} catch (ClientException e) {
			throw e;
		}
	}
	
	public List<Material> queryMaterialQtys(Long orgRrn, Long materialRrn, String catalog2) throws ClientException{
		return queryMaterialQtys(orgRrn, materialRrn, catalog2, null);
	}
	
	public List<Material> queryMaterialQtys(Long orgRrn, Long materialRrn, String catalog2, String whereClause)
			throws ClientException {
		try {
			StringBuffer sql = new StringBuffer();
//			sql.append("  SELECT IV.MATERIAL_RRN, ");
//			sql.append("           IV.ORG_RRN, ");
//			sql.append("		   PM.MATERIAL_ID, ");
//			sql.append("           PM.NAME, ");
//			sql.append("           IV.QTY_ONHAND, ");
//			sql.append("           QTY_ALLOCATION, ");
//			sql.append("           ALLONWAY, ");
//			sql.append("           PURCHASER ");
//			sql.append("  FROM   PDM_MATERIAL PM ");
//			sql.append("           INNER JOIN ( SELECT    MATERIAL_RRN, ORG_RRN, ");
//			sql.append("                                         SUM(NVL(QTY_ONHAND,0)+NVL(QTY_DIFF,0)) AS QTY_ONHAND ");
//			sql.append("                             FROM        INV_STORAGE ");
//			sql.append("                             WHERE         WAREHOUSE_RRN = 151046 ");//制造车间良品
//			sql.append("                                         OR WAREHOUSE_RRN = 151043 ");//环保-良品
//			sql.append("                                         OR WAREHOUSE_RRN = 12644746 ");//奔泰-良品
//			sql.append("                             GROUP BY MATERIAL_RRN,ORG_RRN) IV ");
//			sql.append("               ON        PM.OBJECT_RRN = IV.MATERIAL_RRN ");
//			sql.append("                   AND PM.ORG_RRN = ?1 ");
//			sql.append("                   AND PM.MATERIAL_CATEGORY2 = '" + catalog2 +"' ");
//			sql.append("                   AND QTY_ONHAND < QTY_ALLOCATION ");
//			sql.append("           LEFT JOIN ( SELECT   MATERIAL_RRN, ");
//			sql.append("                                       SUM(ONWAY) AS ALLONWAY ");
//			sql.append("                           FROM      ( SELECT     MATERIAL_RRN, ");
//			sql.append("                                                      SUM(QTY - (CASE WHEN QTY_ORDERED IS NULL THEN 0 ELSE QTY_ORDERED END)) AS ONWAY ");
//			sql.append("                                          FROM      PUR_REQUISITION_LINE ");
//			sql.append("                                          WHERE     ORG_RRN = ?2 ");
//			sql.append("                                                      AND LINE_STATUS <> 'CLOSED' ");
//			sql.append("                                          GROUP BY MATERIAL_RRN ");
//			sql.append("                                         UNION ALL ");
//			sql.append("                                          SELECT     MATERIAL_RRN, ");
//			sql.append("                                                      SUM(QTY - (CASE WHEN QTY_IN IS NULL THEN 0 ELSE QTY_IN END)) AS ONWAY ");
//			sql.append("                                          FROM      PUR_PO_LINE ");
//			sql.append("                                          WHERE     ORG_RRN = ?3 ");
//			sql.append("                                                      AND LINE_STATUS <> 'CLOSED' ");
//			sql.append("                                          GROUP BY MATERIAL_RRN) S ");
//			sql.append("                           GROUP BY MATERIAL_RRN) T ");
//			sql.append("               ON PM.OBJECT_RRN = T.MATERIAL_RRN ");
//			sql.append("           LEFT JOIN ( SELECT DISTINCT MATERIAL_RRN, ");
//			sql.append("                                                 PURCHASER ");
//			sql.append("                           FROM    VDM_VENDOR_MATERIAL) T2 ");
//			sql.append("               ON PM.OBJECT_RRN = T2.MATERIAL_RRN ");



//			if(materialRrn != null){
//				sql.append(" WHERE IV.MATERIAL_RRN = " + materialRrn);
//			}
			
			//2012.5.10修改
			sql.append("SELECT DISTINCT PM.OBJECT_RRN AS MATERIAL_RRN,");
			sql.append("                PM.Org_Rrn AS ORG_RRN,");
			sql.append("                PM.MATERIAL_ID AS MATERIAL_ID,");
			sql.append("                PM.NAME AS NAME,");
			sql.append("                PM.reference_Doc5 AS reference_Doc5,");
			sql.append("                IVS.QTY_ONHAND AS QTY_ONHAND,");
			sql.append("                C.NEEDQTY AS QTY_ALLOCATION,");
			sql.append("                IVS.QTY_ONHAND - C.NEEDQTY AS CANNEED,");
			sql.append("                CASE WHEN WV.ONWAY IS NULL THEN 0 ELSE WV.ONWAY END AS ALLONWAY,");
			sql.append("                CPP.PURCHASER, CPP.PROMISED");
			sql.append("  FROM PDM_MATERIAL PM");
			sql.append(" INNER JOIN (SELECT MATERIAL_RRN, SUM(NVL(QTY_ONHAND,0)+NVL(QTY_DIFF,0)) AS QTY_ONHAND");
			sql.append("               FROM INV_STORAGE");
			sql.append("              WHERE ORG_RRN = ?1");
			sql.append("                AND WAREHOUSE_RRN IN (151046, 151043)");
			sql.append("              GROUP BY MATERIAL_RRN) IVS");
			sql.append("    ON PM.OBJECT_RRN = IVS.MATERIAL_RRN");
			sql.append("   AND PM.ORG_RRN = ?2");
//			sql.append(" INNER JOIN (SELECT WB.MATERIAL_RRN,");
//			sql.append("                    SUM(CASE WHEN WM.QTY_RECEIVE IS NULL THEN 0 ELSE SQTY END) AS NEEDQTY");
//			sql.append("               FROM (SELECT C.MO_RRN,");
//			sql.append("                            B.MATERIAL_RRN,");
//			sql.append("                            SUM(C.QTY * B.QTY_UNIT) SQTY");
//			sql.append("                       FROM WIP_MO_BOM B");
//			sql.append("                      INNER JOIN (SELECT M.MO_RRN,");
//			sql.append("                                        M.MATERIAL_RRN,");
//			sql.append("                                        M.PATH,");
//			sql.append("                                        T.QTY");
//			sql.append("                                   FROM WIP_MO_BOM M");
//			sql.append("                                  INNER JOIN (SELECT T.OBJECT_RRN,");
//			sql.append("                                                    (T.QTY - T.QTY_RECEIVE) QTY");
//			sql.append("                                               FROM WIP_MO_LINE T");
//			sql.append("                                              WHERE T.LINE_STATUS IN");
//			sql.append("                                                    ('DRAFTED', 'APPROVED')) T");
//			sql.append("                                     ON M.MO_LINE_RRN = T.OBJECT_RRN");
//			sql.append("                                    AND T.QTY <> 0) C");
//			sql.append("                         ON B.MO_RRN = C.MO_RRN");
//			sql.append("                        AND B.MATERIAL_PARENT_RRN = C.MATERIAL_RRN");
//			sql.append("                        AND B.PATH = C.PATH || C.MATERIAL_RRN || '/'");
//			sql.append("                      GROUP BY C.MO_RRN, B.MATERIAL_RRN) WB");
//			sql.append("              INNER JOIN WIP_MO WM");
//			sql.append("                 ON WB.MO_RRN = WM.OBJECT_RRN");
//			sql.append("              INNER JOIN PDM_MATERIAL PM");
//			sql.append("                 ON WB.MATERIAL_RRN = PM.OBJECT_RRN");
//			sql.append("              INNER JOIN PDM_MATERIAL P");
//			sql.append("                 ON WM.MATERIAL_RRN = P.OBJECT_RRN");
//			sql.append("              INNER JOIN WIP_WORKCENTER WW");
//			sql.append("                 ON WM.WORKCENTER_RRN = WW.OBJECT_RRN");
//			sql.append("                AND WW.ORG_RRN = ?3");
//			sql.append("                AND WM.ORG_RRN = ?4");
//			sql.append("              GROUP BY WB.MATERIAL_RRN) C");
			//bug修复，已分配数与物料统计查询已分配数不一致的BUG上面注释掉的为已分配数
			sql.append(" LEFT JOIN (SELECT WB.MATERIAL_RRN,NVL(SUM(WB.SQTY),  ");
			sql.append("                0) AS NEEDQTY            ");
			sql.append("       FROM (SELECT C.MO_RRN, B.MATERIAL_RRN, SUM(C.QTY * B.QTY_UNIT) SQTY  ");
			sql.append("               FROM WIP_MO_BOM B  ");
			sql.append("              INNER JOIN (SELECT M.MO_RRN, M.MATERIAL_RRN, M.PATH, T.QTY  ");
			sql.append("                           FROM WIP_MO_BOM M  ");
			sql.append("                          INNER JOIN (SELECT T.OBJECT_RRN,  ");
			sql.append("                                            (T.QTY - T.QTY_RECEIVE) QTY  ");
			sql.append("                                       FROM WIP_MO_LINE T  ");
			sql.append("                                      WHERE T.LINE_STATUS IN  ");
			sql.append("                                            ('DRAFTED', 'APPROVED')) T  ");
			sql.append("                             ON (M.MO_LINE_RRN = T.OBJECT_RRN OR M.Mo_Rrn=T.Object_Rrn)  ");
			sql.append("                            AND T.QTY <> 0  ");
			sql.append("                           ) C  ");
			sql.append("                 ON B.MO_RRN = C.MO_RRN  ");
			sql.append("                AND B.MATERIAL_PARENT_RRN = C.MATERIAL_RRN  ");
			sql.append("                AND B.PATH = C.PATH || C.MATERIAL_RRN || '/'  ");
			sql.append("              GROUP BY C.MO_RRN, B.MATERIAL_RRN) WB  ");
			sql.append("      INNER JOIN PDM_MATERIAL PM  ");
			sql.append("         ON WB.MATERIAL_RRN = PM.OBJECT_RRN  ");
			sql.append("      GROUP BY WB.MATERIAL_RRN ) C ");			
			sql.append("    ON PM.OBJECT_RRN = C.MATERIAL_RRN");
			sql.append("  LEFT JOIN (SELECT PC.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                    QTY,");
			sql.append("                    (QTY - CASE");
			sql.append("                      WHEN QM IS NULL THEN");
			sql.append("                       0");
			sql.append("                      ELSE");
			sql.append("                       QM");
			sql.append("                    END) AS ONWAY");
			sql.append("               FROM (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                            SUM(CASE WHEN PP.DOC_STATUS = 'CLOSED' THEN PPL.QTY_IN ELSE PPL.QTY END) AS QTY");
			sql.append("                       FROM PUR_PO PP");
			sql.append("                      INNER JOIN PUR_PO_LINE PPL");
			sql.append("                         ON PP.OBJECT_RRN = PPL.PO_RRN");
			sql.append("                        AND PP.ORG_RRN = ?5");
			sql.append("                      GROUP BY PPL.MATERIAL_RRN) PC");
			sql.append("               LEFT JOIN (SELECT MATERIAL_RRN, SUM(QTY_MOVEMENT) AS QM");
			sql.append("                           FROM INV_MOVEMENT IM");
			sql.append("                          INNER JOIN INV_MOVEMENT_LINE IML");
			sql.append("                             ON IM.DOC_ID = IML.MOVEMENT_ID");
			sql.append("                            AND IM.DOC_STATUS IN ('APPROVED', 'COMPLETED')");
			sql.append("                          WHERE IM.ORG_RRN = ?6");
			sql.append("                            AND DOC_TYPE = 'PIN'");
			sql.append("                          GROUP BY MATERIAL_RRN) CC");
			sql.append("                 ON PC.MATERIAL_RRN = CC.MATERIAL_RRN) WV");
			sql.append("    ON PM.OBJECT_RRN = WV.MATERIAL_RRN");
			sql.append("  LEFT JOIN (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                    PP.PURCHASER     AS PURCHASER,");
			sql.append("  					PPL.DATE_PROMISED AS PROMISED");
			sql.append("               FROM PUR_PO PP");
			sql.append("              INNER JOIN PUR_PO_LINE PPL");
			sql.append("                 ON PP.DOC_ID = PPL.PO_ID");
			sql.append("                AND PP.ORG_RRN = ?7");
			sql.append("                AND PP.DOC_STATUS <> 'CLOSED'");
			sql.append("              INNER JOIN (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                                MAX(PP.DATE_APPROVED) AS LAST_TIME");
			sql.append("                           FROM PUR_PO PP");
			sql.append("                          INNER JOIN PUR_PO_LINE PPL");
			sql.append("                             ON PP.DOC_ID = PPL.PO_ID");
			sql.append("                            AND PP.ORG_RRN = ?8");
			sql.append("                            AND PP.DOC_STATUS <> 'CLOSED'");
			sql.append("                          GROUP BY PPL.MATERIAL_RRN) C");
			sql.append("                 ON PPL.MATERIAL_RRN = C.MATERIAL_RRN");
			sql.append("                AND PP.DATE_APPROVED = C.LAST_TIME) CPP");
			sql.append("    ON PM.OBJECT_RRN = CPP.MATERIAL_RRN");
			sql.append(" WHERE PM.IS_PURCHASE = 'Y'");
			if(whereClause == null || whereClause.trim().length() > 0){
				sql.append("   AND nvl(IVS.QTY_ONHAND,0) < nvl(C.NEEDQTY,0)");
			}else{
				sql.append("   AND " + whereClause);
			}


			
			if(materialRrn != null){
				sql.append(" AND IVS.MATERIAL_RRN = " + materialRrn);
			}
			
			StringBuffer sql2 = new StringBuffer("");
			sql2.append("   SELECT t.material_rrn, ");
			sql2.append("          t.org_rrn, ");
			sql2.append("          t.material_id, ");
			sql2.append("          t.name, ");
			sql2.append("          t.reference_Doc5, ");
			sql2.append("          t.qty_onhand, ");
			sql2.append("          t.qty_allocation, ");
			sql2.append("          t.allonway, ");
			sql2.append("          wmsys.wm_concat (t.purchaser) purchaser, ");
			sql2.append("		   wmsys.wm_concat (to_char(t.promised,'yyyy-mm-dd')) promised");
			sql2.append(" FROM (");
			sql2.append(sql);
			sql2.append(") t");
			sql2.append(" GROUP BY t.material_rrn, ");
			sql2.append("          t.org_rrn, ");
			sql2.append("          t.material_id, ");
			sql2.append("          t.name, ");
			sql2.append("          t.reference_Doc5, ");
			sql2.append("          t.qty_onhand, ");
			sql2.append("          t.qty_allocation, ");
			sql2.append("          t.allonway ");
			
			//处理最新到货日期
			StringBuffer sql3 = new StringBuffer();
			sql3.append(" select min(line.date_promised) date_promised,line.material_rrn from pur_po_line line WHERE  ORG_RRN = ?9 ");
			sql3.append(" and line_status in ('DRAFTED','APPROVED') AND line.Qty-(case when line.QTY_IN is null then 0 else line.QTY_IN end)>0 ");
			sql3.append(" group by line.material_rrn");

			StringBuffer sql4 = new StringBuffer();
			sql4.append("   SELECT sql2.material_rrn, ");
			sql4.append("      	   sql2.org_rrn, ");
			sql4.append("          sql2.material_id, ");
			sql4.append("          sql2.name, ");
			sql4.append("          sql2.qty_onhand, ");
			sql4.append("          sql2.qty_allocation, ");
			sql4.append("          sql2.allonway, ");
			sql4.append("          sql2.purchaser, ");
			sql4.append("	to_char(sql3.date_promised,'yyyy-mm-dd') promised,");
			sql4.append("          sql2.reference_Doc5 ");
			sql4.append(" FROM (");
			sql4.append(sql2.toString());
			sql4.append(" ) sql2");
			sql4.append(" left join (");
			sql4.append(sql3);
			sql4.append(" ) sql3");
			sql4.append(" on ");
			sql4.append(" sql2.material_rrn = sql3.material_rrn ");
			
			
			Query query = em.createNativeQuery(sql4.toString());
			
			query.setParameter(1, orgRrn);
			query.setParameter(2, orgRrn);
//			query.setParameter(3, orgRrn);
//			query.setParameter(4, orgRrn);
			query.setParameter(5, orgRrn);
			query.setParameter(6, orgRrn);
			query.setParameter(7, orgRrn);
			query.setParameter(8, orgRrn);
			query.setParameter(9, orgRrn);
			
			List<Object[]> objs = query.getResultList();
			List<Material> materials = new ArrayList<Material>();
			
			for(Object[] obj : objs){
				Material m = new Material();
				m.setObjectRrn(((BigDecimal) obj[0]).longValue());
				m.setOrgRrn(((BigDecimal) obj[1]).longValue());
				m.setMaterialId((String)obj[2]);
				m.setName((String)obj[3]);
				m.setQtyOnHand((BigDecimal) obj[4]);
				m.setQtyAllocation((BigDecimal) obj[5]);
				m.setQtyTransit((BigDecimal) obj[6]);
				m.setPlannerId((String)obj[7]);//借用此字段保存采购员信息
				m.setPromised((String)obj[8]);//最新到货日期
				m.setReferenceDoc5((String)obj[9]);//借用此字段保存备注信息
				materials.add(m);
			}

			return materials;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public List<String> getOutedLotsByMaterialId(String materialId, Long orgRrn)
			throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT DISTINCT lot_id ");
			sql.append(" FROM inv_movement_line_lot ");
			sql.append(" WHERE  ");
			sql.append(ADBase.SQL_BASE_CONDITION);
			sql.append(" AND material_id = ? ");
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, materialId);
			
			return query.getResultList();
		}catch (Exception e){
			throw new ClientException(e);
		}
	}

	@Override
	public List<MovementLine> getMovementLines(MovementIn in)
			throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT MovementLine FROM MovementLine MovementLine ");
			sql.append(" WHERE MovementLine.movementRrn = ? ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, in.getObjectRrn());
			
			return query.getResultList();
		}catch(Exception e){
			throw new ClientException(e);
		}
	}

	@Override
	public String getVehicleIdByName(String name) throws ClientException {
		try {
			if(name == null || name.trim().length() == 0){
				throw new ClientException("Name is invaild");
			}
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT id ");
			sb.append("   FROM inv_vehicle_name_id v ");
			sb.append("  WHERE v.name = ? ");

			Query query = em.createNativeQuery(sb.toString());
			query.setParameter(1, name);
			
			List rst = query.getResultList();
			if(rst == null || rst.size() == 0){
				return null;
			}
			return (String) rst.get(0);
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public String getVehicleNameById(String id) throws ClientException {
		if(id == null || id.trim().length() == 0){
			throw new ClientException("Id is invaild");
		}
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT name ");
			sb.append("   FROM inv_vehicle_name_id v ");
			sb.append("  WHERE v.id = ? ");

			Query query = em.createNativeQuery(sb.toString());
			query.setParameter(1, id);
			
			List rst = query.getResultList();
			if(rst == null || rst.size() == 0){
				return null;
			}
			return (String) rst.get(0);
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public Warehouse getWarehouseById(String warehouseId, long orgRrn)
			throws ClientException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT Warehouse ");
			sb.append("   FROM Warehouse Warehouse ");
			sb.append("  WHERE Warehouse.warehouseId = ? AND Warehouse.orgRrn = ? ");

			Query query = em.createQuery(sb.toString());
			query.setParameter(1, warehouseId);
			query.setParameter(2, orgRrn);
			
			List rst = query.getResultList();
			if(rst == null || rst.size() == 0){
				return null;
			}
			
			return (Warehouse) rst.get(0);
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public List<Lot> attachLotsToIqc(List<Lot> lots, Iqc iqc, IqcLine line,
			long userRrn) throws ClientException {
		for (Lot lot : lots) {
			lot.setPoRrn(iqc.getPoRrn());
			lot.setPoId(iqc.getPoId());
			lot.setPoLineRrn(line.getPoLineRrn());
			lot.setReceiptRrn(iqc.getReceiptRrn());
			lot.setReceiptId(iqc.getReceiptId());
			lot.setIqcRrn(iqc.getObjectRrn());
			lot.setIqcId(iqc.getDocId());
			lot.setIqcLineRrn(line.getObjectRrn());
		}
		return lots;
	}

	public void manualWriteOff(MovementWriteOff mw, long userRrn) throws ClientException {
		try {
			Date now = new Date();
			mw.setIsActive(true);
			mw.setCreatedBy(userRrn);
			mw.setCreated(now);
			mw.setDateCreated(now);
			mw.setTotalLines(0L);
			mw.setDocType(MovementWriteOff.DOCTYPE_MWO);
			mw.setDocStatus(MovementWriteOff.STATUS_APPROVED);
			mw.setDocId(generateWriteOffCode(mw));
			ADUser user = em.find(ADUser.class, userRrn);
			mw.setUserCreated(user.getUserName());
			mw.setUserApproved(user.getUserName());
			mw.setDateApproved(now);
			Warehouse house = this.getWriteOffWarehouse(mw.getOrgRrn());
			mw.setWarehouseRrn(house.getObjectRrn());
			mw.setWarehouseId(house.getWarehouseId());	
			em.persist(mw);
			List<MovementLine> lines = mw.getMovementLines()==null?new ArrayList<MovementLine>():mw.getMovementLines();
			List<MovementLine> savaLine = new ArrayList<MovementLine>();
			for (MovementLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setOrgRrn(mw.getOrgRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getQtyMovement().doubleValue() == 0) {
					continue;
				}
				
				line.setIsActive(true);
				line.setCreatedBy(userRrn);
				line.setCreated(now);
				line.setLineStatus(MovementWriteOff.STATUS_APPROVED);
				line.setMovementRrn(mw.getObjectRrn());
				line.setMovementId(mw.getDocId());
				mw.setTotalLines(mw.getTotalLines() + 1);
				em.persist(line);
				
				//只更新财务库存,不更新营运库存
				updateStorage(mw.getOrgRrn(), line.getMaterialRrn(), mw.getWarehouseRrn(), line.getQtyMovement().negate(), false, true, userRrn);
				
				
				LotConsume lotConsume = new LotConsume();
				lotConsume.setOrgRrn(mw.getOrgRrn());
				lotConsume.setIsActive(true);
				lotConsume.setCreatedBy(userRrn);
				lotConsume.setUpdatedBy(userRrn);
				lotConsume.setCreated(now);
				lotConsume.setMaterialRrn(material.getObjectRrn());
				lotConsume.setMaterialId(material.getMaterialId());
				lotConsume.setMaterialName(material.getName());
				lotConsume.setMoRrn(mw.getMoRrn());
				lotConsume.setMoId(mw.getMoId());
				lotConsume.setInRrn(mw.getObjectRrn());
				lotConsume.setInId(mw.getDocId());
				lotConsume.setQtyConsume(line.getQtyMovement());
				lotConsume.setIsWin(false);
				lotConsume.setDateIn(now);
				lotConsume.setWarehouseRrn(mw.getWarehouseRrn());
				lotConsume.setWarehouseId(mw.getWarehouseId());
				lotConsume.setIsManual(true);
				lotConsume.setWriteoffType(mw.getWriteoffType());
				em.merge(lotConsume);
				
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			mw.setUpdatedBy(userRrn);
			mw.setMovementLines(null);
			em.merge(mw);
			mw.setMovementLines(savaLine);
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	
	public List<Lot> getAutoMatchOutLots(MovementLine outLine) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT Lot, LotStorage FROM Lot as Lot, LotStorage as LotStorage ");
		sql.append(" WHERE Lot.objectRrn = LotStorage.lotRrn" );
		sql.append(" AND Lot.isActive = 'Y' AND LotStorage.isActive = 'Y' ");
		sql.append(" AND Lot.materialRrn = ? ");
		sql.append(" AND LotStorage.warehouseRrn = ? ");
		sql.append(" AND LotStorage.qtyOnhand > 0 ");
//		sql.append(" AND Lot.position = ? ");
		sql.append(" ORDER BY Lot.dateIn ASC ");
		
		MovementOut out = new MovementOut();
		out = em.find(MovementOut.class, outLine.getMovementRrn());
		if(out == null){
			throw new ClientException("There is no movement out match to out line");
		}
		
		String position = Lot.POSITION_INSTOCK;
		
		//财务的出库调整和销售红冲需要position=OUT
		if(MovementOut.OutType.AOU.equals(out.getDocType()) 
			&& (MovementOut.OUT_TYPE_OUT_ADJUST.equals(out.getOutType()) || MovementOut.OUT_TYPE_SALE_ADJUST.equals(out.getOutType()))){
			position = Lot.POSITION_OUT;
		}
		
		logger.debug(sql);
		List<Lot> optionalLots = new ArrayList<Lot>();
		try {
			Movement movement = em.find(Movement.class, outLine.getMovementRrn());
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, outLine.getMaterialRrn());
			query.setParameter(2, movement.getWarehouseRrn());
//			query.setParameter(3, position);
			List list = query.getResultList();
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[])list.get(i);
				Lot lot = (Lot)obj[0];
				LotStorage lotStorage = (LotStorage)obj[1];
				lot.setQtyTransaction(lotStorage.getQtyOnhand());
				optionalLots.add(lot);
			}
			

			Material material = em.find(Material.class, outLine.getMaterialRrn());
			
			if (material.getIsLotControl()) {
				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
					if (outLine.getQtyMovement().doubleValue() > optionalLots.size()) {
						throw new ClientException("inv.not_sufficient_quantity");
					} else {
						List<Lot> lots = new ArrayList<Lot>();
						for (int i = 0; i < outLine.getQtyMovement().intValue(); i++) {
							lots.add(optionalLots.get(i));
						}
						return lots;
					}
				} else {
					BigDecimal qtyOut = BigDecimal.ZERO;
					List<Lot> lots = new ArrayList<Lot>();
					for (Lot optionalLot : optionalLots) {
						qtyOut = qtyOut.add(optionalLot.getQtyTransaction()); 
						if (qtyOut.compareTo(outLine.getQtyMovement()) >= 0) {
							optionalLot.setQtyTransaction(
									optionalLot.getQtyTransaction().subtract(qtyOut.subtract(outLine.getQtyMovement())));
							lots.add(optionalLot);
							break;
						} else {
							lots.add(optionalLot);
						}
					}
					return lots;
				}
			} else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	

	private String generateRackCode(long warehouseRrn, String areaId, long x, long y) throws Exception{
		try {
			String rackCode = "";
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM CodeWarehouse CodeWarehouse ");
			hql.append(" WHERE CodeWarehouse.warehouseRrn = ? ");
			
			Query query = em.createQuery(hql.toString());
			query.setParameter(1, warehouseRrn);
			Object obj = query.getSingleResult();
			if(obj == null){
				return null;
			}
			CodeWarehouse cw = (CodeWarehouse) obj;
			String prefix = cw.getHouseCode();
			
			rackCode = prefix + areaId + String.format("%1$02d", x) + String.format("%1$d", y);
			
			return rackCode;
		} catch (Exception e) {
			throw e;
		}
	}

public List<MovementLineLot> getLineLots(Long movementLineRrn) throws ClientException{
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT MovementLineLot FROM MovementLineLot as MovementLineLot ");
			sql.append(" WHERE MovementLineLot.movementLineRrn = ?" );
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, movementLineRrn);
			return query.getResultList();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Override
	public List<MovementLine> getMovementLines(Long movementRrn)
			throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT MovementLine FROM MovementLine MovementLine ");
			sql.append(" WHERE MovementLine.movementRrn = ? ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, movementRrn);
			
			return query.getResultList();
		}catch(Exception e){
			throw new ClientException(e);
		}
	}
	
	public WarehouseRack saveWarehouseRack(long orgRrn, WarehouseRack rack, long userRrn) throws ClientException{
		try {
			Date now = new Date();
			
			if(rack.getObjectRrn() == null){
				rack.setIsActive(true);
				rack.setOrgRrn(orgRrn);
				rack.setCreated(now);
				rack.setCreatedBy(userRrn);
				rack.setUpdated(now);
				rack.setUpdatedBy(userRrn);
				
				Long warehouseRrn = rack.getWarehouseRrn();
				Warehouse wh = new Warehouse();
				wh.setObjectRrn(warehouseRrn);
				
				wh = (Warehouse) adManager.getEntity(wh);
				rack.setWarehouseId(wh.getWarehouseId());
				
				String rackId = generateRackCode(rack.getWarehouseRrn(), rack.getAreaId(), rack.getX(), rack.getY());
				rack.setRackId(rackId);
				em.persist(rack);
			}else{
				rack.setUpdated(now);
				rack.setUpdatedBy(userRrn);
				em.merge(rack);
			}
			
			return rack;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	public WarehouseRack getWarehouseRackById(long orgRrn, String rackId) throws ClientException{
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM WarehouseRack R ");
			hql.append(" WHERE " + ADBase.BASE_CONDITION);
			hql.append(" AND R.rackId = ? ");
			
			Query query = em.createQuery(hql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, rackId);
			List lst = query.getResultList();
			if(lst == null || lst.size() == 0){
				return null;
			}else{
				return (WarehouseRack) lst.get(0);
			}
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	public BigDecimal getWarehouseRackQtyonhand(long lotRrn,long rackRrn) throws ClientException{
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" select t.qty_onhand ");
			sql.append(" from inv_rack_lot_storage t ");
			sql.append(" where t.lot_rrn = ? ");
			sql.append(" and  t.rack_rrn = ? ");
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, lotRrn);
			query.setParameter(2, rackRrn);
			List lst = query.getResultList();
			if(lst == null || lst.size() == 0){
				return null;
			}else{
				return (BigDecimal) lst.get(0);
			}
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	public void batchSaveRacKMovementLot(long orgRrn, List<RacKMovementLot> rLots, long userRrn, boolean approve) throws ClientException{
		try {
			for(RacKMovementLot rLot : rLots){
				saveRacKMovementLot(orgRrn, rLot, userRrn, approve);
			}
		} catch (ClientException e) {
			throw e;
		}
	}
	
	public RacKMovementLot saveRacKMovementLot(long orgRrn, RacKMovementLot rLot, long userRrn, boolean approve) throws ClientException{
		try {
			Date now = new Date();
			if(rLot == null){
				throw new Exception("RLot to save is null");
			}
			
			if(rLot.getIsLargeLot()){
				//如果是大批次，走此流程
				if(rLot.getObjectRrn() == null){
					rLot.setCreated(now);
					rLot.setCreatedBy(userRrn);
					rLot.setUpdated(now);
					rLot.setUpdatedBy(userRrn);
					rLot.setIsActive(true);
					
					WarehouseRack rack = new WarehouseRack();
					rack.setObjectRrn(rLot.getRackRrn());
					rack = (WarehouseRack) adManager.getEntity(rack);
					if(rack == null) throw new Exception("WarehouseRack with id: " + rLot.getRackRrn() + " not exists");
					
					rLot.setOrgRrn(rack.getOrgRrn());
					rLot.setRackRrn(rack.getObjectRrn());
					rLot.setRackId(rack.getRackId());
					
					Long warehouseRrn = rack.getWarehouseRrn();
					Warehouse h = new Warehouse();
					h.setObjectRrn(warehouseRrn);
					h = (Warehouse) adManager.getEntity(h);
					if(h == null) throw new Exception("Warehouse with id: " + rack.getWarehouseRrn() + " not exists");
					
					rLot.setWarehouseRrn(h.getObjectRrn());
					rLot.setWarehouseId(h.getWarehouseId());
					
					LargeLot lLot = getLargeLotById(orgRrn, rLot.getLotId());
					if(lLot == null) throw new Exception("LargeLot with id: " + rLot.getLotId() + " not exists");
					
					rLot.setLotRrn(lLot.getObjectRrn());
					rLot.setLotId(lLot.getLotId());
					rLot.setMaterialRrn(lLot.getMaterialRrn());
					rLot.setMaterialId(lLot.getMaterialId());
					
					em.persist(rLot);
				}else{
					rLot.setUpdated(now);
					rLot.setUpdatedBy(userRrn);
					em.merge(rLot);
				}
			}else{
				//如果不是大批次，走原有流程
				if(rLot.getObjectRrn() == null){
					rLot.setCreated(now);
					rLot.setCreatedBy(userRrn);
					rLot.setUpdated(now);
					rLot.setUpdatedBy(userRrn);
					rLot.setIsActive(true);
					
					WarehouseRack rack = new WarehouseRack();
					rack.setObjectRrn(rLot.getRackRrn());
					rack = (WarehouseRack) adManager.getEntity(rack);
					if(rack == null) throw new Exception("WarehouseRack with id: " + rLot.getRackRrn() + " not exists");
					
					rLot.setOrgRrn(rack.getOrgRrn());
					rLot.setRackRrn(rack.getObjectRrn());
					rLot.setRackId(rack.getRackId());
					
					Long warehouseRrn = rack.getWarehouseRrn();
					Warehouse h = new Warehouse();
					h.setObjectRrn(warehouseRrn);
					h = (Warehouse) adManager.getEntity(h);
					if(h == null) throw new Exception("Warehouse with id: " + rack.getWarehouseRrn() + " not exists");
					
					rLot.setWarehouseRrn(h.getObjectRrn());
					rLot.setWarehouseId(h.getWarehouseId());
					
					Lot lot = getLotByLotId(orgRrn, rLot.getLotId());
					if(lot == null) throw new Exception("Lot with id: " + rLot.getLotId() + " not exists");
					
					rLot.setLotRrn(lot.getObjectRrn());
					rLot.setLotId(lot.getLotId());
					rLot.setMaterialRrn(lot.getMaterialRrn());
					rLot.setMaterialId(lot.getMaterialId());
					rLot.setMaterialName(lot.getMaterialName());
					
					em.persist(rLot);
				}else{
					rLot.setUpdated(now);
					rLot.setUpdatedBy(userRrn);
					em.merge(rLot);
				}
			}
			if(approve){
				approveRackMovementLot(orgRrn, rLot, userRrn);
			}
			
			return rLot;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	public void batchApproveRackMovementLot(long orgRrn, List<RacKMovementLot> rLots, long userRrn) throws ClientException{
		try {
			for(RacKMovementLot rLot : rLots){
				approveRackMovementLot(orgRrn, rLot, userRrn);
			}
		} catch (ClientException e) {
			throw e;
		}
	}

	public void approveRackMovementLot(long orgRrn, RacKMovementLot rLot, long userRrn) throws ClientException {
		try {
		Date now = new Date();
		if(rLot == null || rLot.getObjectRrn() == null){
			throw new ClientException("RackMovementLot was not saved!");
		}
		rLot.setLotStatus(RacKMovementLot.STATUS_APPROVED);
		rLot.setDateApproved(now);
		em.merge(rLot);
		
		//更新货架批次库存
		if(RacKMovementLot.IO_TYPE_IN.equals(rLot.getIoType())){
			//如果是盘点入库，就要把原来的库存做盘点出库，再做盘点入库
			if(RacKMovementLot.MOVEMENT_TYPE_COUNT.equals(rLot.getMovementType())){
				RackLotStorage storage = getRackLotStorage(orgRrn, rLot.getLotRrn(), rLot.getWarehouseRrn(), rLot.getRackRrn());
				if(storage != null){
					RacKMovementLot outLot = new RacKMovementLot();
					outLot.setCreated(now);
					outLot.setCreatedBy(userRrn);
					outLot.setUpdated(now);
					outLot.setUpdatedBy(userRrn);
					outLot.setIsActive(true);
					outLot.setIoType(RacKMovementLot.IO_TYPE_OUT);
					outLot.setMovementType(RacKMovementLot.MOVEMENT_TYPE_COUNT);
					outLot.setLotRrn(rLot.getLotRrn());
					outLot.setLotId(rLot.getLotId());
					outLot.setMaterialRrn(rLot.getMaterialRrn());
					outLot.setMaterialId(rLot.getMaterialId());
					outLot.setMaterialName(rLot.getMaterialName());
					outLot.setQty(storage.getQtyOnhand());
					outLot.setWarehouseRrn(rLot.getWarehouseRrn());
					outLot.setWarehouseId(rLot.getLotId());
					outLot.setRackRrn(rLot.getRackRrn());
					outLot.setRackId(rLot.getRackId());
					outLot.setLotStatus(RacKMovementLot.STATUS_APPROVED);
					outLot.setDateApproved(now);
					em.persist(outLot);
					updateRackLotStorage(orgRrn, userRrn, outLot.getLotRrn(), outLot.getLotId(), outLot.getWarehouseRrn(), outLot.getRackRrn(), outLot.getQty().negate(), outLot.getMaterialRrn());
				}
			}
			updateRackLotStorage(orgRrn, userRrn, rLot.getLotRrn(), rLot.getLotId(), rLot.getWarehouseRrn(), rLot.getRackRrn(), rLot.getQty(), rLot.getMaterialRrn());
		}else if(RacKMovementLot.IO_TYPE_OUT.equals(rLot.getIoType())){
			updateRackLotStorage(orgRrn, userRrn, rLot.getLotRrn(), rLot.getLotId(), rLot.getWarehouseRrn(), rLot.getRackRrn(), rLot.getQty().negate(), rLot.getMaterialRrn());
		}
		} catch (Exception e) {
		throw new ClientException(e);
	}
	}
	
	public List<RacKMovementLot> getRackLots(MovementOut mo) throws ClientException{
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" select t ");
			sql.append(" from RacKMovementLot t ");
			sql.append(" where t.movementRrn = ? ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, mo.getObjectRrn());
			List lst = query.getResultList();
			if(lst == null || lst.size() == 0){
				return null;
			}else{
				return lst;
			}
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<PurchaseOrderLine> getPoLineForReceive(long orgRrn, int maxResult, String whereClause) throws ClientException{
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM PurchaseOrderLine PurchaseOrderLine ");
			hql.append(" WHERE " + ADBase.BASE_CONDITION);
			hql.append(" AND " + whereClause);
			
			Query query = em.createQuery(hql.toString());
			query.setMaxResults(maxResult);
			query.setParameter(1, orgRrn);
			
			List<PurchaseOrderLine> poLines = query.getResultList();
			
			for(PurchaseOrderLine line : poLines){
				Object[] objs = getQtysFromIqcLine(orgRrn, line.getObjectRrn());
				line.setQtyReceived((BigDecimal) objs[0]);
				line.setQtyIqced((BigDecimal) objs[1]);
				line.setQtyIqcing((BigDecimal) objs[2]);
				line.setQtyQualified((BigDecimal) objs[3]);
//				line.setQtyIn((BigDecimal) objs[4]);
			}
			return poLines;
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException(e);
		}
	}
	
	/**
	 * 
	 * @param orgRrn
	 * @param poLineRrn
	 * @return 1.已收货总数(包括不合格的),2.已检验数,3.正在检验数,4.检验合格数,5.已入库数
	 * @throws Exception
	 */
	private Object[] getQtysFromIqcLine(long orgRrn, long poLineRrn) throws Exception{
		try {
			StringBuffer sql = new StringBuffer();
			//根据采购单行查已检验数　合格数　在检数　已收货总数 
			sql.append("SELECT SUM(L.QTY_RECEIPT) QTY_RECEIPT,"); 
			sql.append("		SUM(CASE"); 
			sql.append("             WHEN L.LINE_STATUS IN ('APPROVED', 'COMPLETED') THEN"); 
			sql.append("              L.QTY_IQC"); 
			sql.append("             ELSE"); 
			sql.append("              0"); 
			sql.append("           END) QTY_IQCED,"); 
			sql.append("       SUM(CASE"); 
			sql.append("             WHEN L.LINE_STATUS IN ('DRAFTED') THEN"); 
			sql.append("              L.QTY_IQC"); 
			sql.append("             ELSE"); 
			sql.append("              0"); 
			sql.append("           END) QTY_IQCING,"); 
			sql.append("       SUM(CASE"); 
			sql.append("             WHEN L.LINE_STATUS IN ('APPROVED', 'COMPLETED') THEN"); 
			sql.append("              L.QTY_QUALIFIED"); 
			sql.append("             ELSE"); 
			sql.append("              0"); 
			sql.append("           END) QTY_QUALIFIED,"); 
			sql.append("       SUM(L.QTY_IN) QTY_IN");
			sql.append("  FROM (SELECT Q.*, R.QTY_RECEIPT"); 
			sql.append("          FROM INV_IQC_LINE Q"); 
			sql.append("         RIGHT JOIN (SELECT * from INV_RECEIPT_LINE WHERE PO_LINE_RRN = ?) R"); 
			sql.append("            ON Q.PO_LINE_RRN = R.PO_LINE_RRN");
			sql.append("           AND Q.IS_ACTIVE = 'Y' AND (Q.ORG_RRN = ? OR Q.ORG_RRN = 0) "); 
			sql.append("           AND Q.LINE_STATUS IN ('DRAFTED', 'APPROVED', 'COMPLETED')"); 
			sql.append("           AND Q.RECEIPT_LINE_RRN = R.OBJECT_RRN) L");

			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, poLineRrn);
			query.setParameter(2, orgRrn);
			
			List rlt = query.getResultList();
			return (Object[]) rlt.get(0);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	private void updateRackLotStorage(long orgRrn, long userRrn, long lotRrn, String lotId, long warehouseRrn, long rackRrn, BigDecimal qty, long materialRrn) throws Exception{
		try {
			Date now = new Date();
			
			RackLotStorage storage = getRackLotStorage(orgRrn, lotRrn, warehouseRrn, rackRrn);
			if(storage == null){
				storage = new RackLotStorage();
				storage.setIsActive(true);
				storage.setCreated(now);
				storage.setCreatedBy(userRrn);
				storage.setUpdated(now);
				storage.setUpdatedBy(userRrn);
				storage.setWarehouseRrn(warehouseRrn);
				storage.setRackRrn(rackRrn);
				storage.setLotRrn(lotRrn);
				storage.setLotId(lotId);
				storage.setMaterialRrn(materialRrn);
				storage.setQtyOnhand(storage.getQtyOnhand().add(qty));
				
				if(storage.getQtyOnhand().compareTo(BigDecimal.ZERO)<0){
					throw new ClientException("QTYONHAND_NOT_ENOUGH");
				}
				
				em.persist(storage);
			}else{
				storage.setUpdated(now);
				storage.setUpdatedBy(userRrn);
				storage.setQtyOnhand(storage.getQtyOnhand().add(qty));
				
				if(storage.getQtyOnhand().compareTo(BigDecimal.ZERO)<0){
					throw new ClientException("QTYONHAND_NOT_ENOUGH");
				}
				em.merge(storage);
			}
			
			//如果库存为0了就删掉这条记录
			if(storage.getQtyOnhand().compareTo(BigDecimal.ZERO)==0){
				em.remove(storage);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	private RackLotStorage getRackLotStorage(long orgRrn, long lotRrn, long warehouseRrn, long rackRrn) throws Exception{
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM RackLotStorage RackLotStorage ");
			hql.append(" WHERE " + ADBase.BASE_CONDITION);
			hql.append(" AND RackLotStorage.warehouseRrn = ? ");
			hql.append(" AND RackLotStorage.rackRrn = ? ");
			hql.append(" AND RackLotStorage.lotRrn = ? ");
			
			Query query = em.createQuery(hql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, warehouseRrn);
			query.setParameter(3, rackRrn);
			query.setParameter(4, lotRrn);
			
			List lst = query.getResultList();
			if(lst == null || lst.size() == 0){
				return null;
			}else{
				return (RackLotStorage) lst.get(0);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<RackLotStorage> getRackStorage(long orgRrn, String whereClause) throws ClientException{
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" SELECT RackLotStorage,Lot FROM RackLotStorage RackLotStorage, Lot Lot");
			hql.append(" WHERE Lot.objectRrn = RackLotStorage.lotRrn ");
			hql.append(" AND " + whereClause);
			hql.append(" AND RackLotStorage.isActive = 'Y' AND (RackLotStorage.orgRrn = ? OR RackLotStorage.orgRrn = 0) ");
			
			Query query = em.createQuery(hql.toString());
			query.setParameter(1, orgRrn);
			
			List lst = query.getResultList();
			List<RackLotStorage> returnLst = new ArrayList<RackLotStorage>();
			if(lst == null || lst.size() == 0){
			}else{
				for(Object obj : lst){
					if(obj != null){
						Object[] objs = (Object[]) obj;
						RackLotStorage r = (RackLotStorage) objs[0];
						Lot l = (Lot) objs[1];
						r.setLotId(l == null ? "" : l.getLotId());
						
						StringBuffer sql = new StringBuffer();
						sql.append(" SELECT W.WAREHOUSE_ID, R.RACK_ID "); 
						sql.append(" FROM INV_WAREHOUSE W, INV_WAREHOUSE_RACK R "); 
						sql.append(" WHERE R.OBJECT_RRN = :rackRrn "); 
						sql.append("  AND W.OBJECT_RRN = R.WAREHOUSE_RRN ");
						
						Query query2 = em.createNativeQuery(sql.toString());
						query2.setParameter("rackRrn", r.getRackRrn());

						List ls = query2.getResultList();
						Object o = ls.get(0);
						Object[] os = (Object[]) o;
						r.setWarehouseId((String) os[0]);
						r.setRackId((String) os[1]);

						returnLst.add(r);
					}
					
				}
			}
			return returnLst;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	public List<Lot> getLotsById(long orgRrn, String lotId) throws ClientException{
		List<Lot> lots = new ArrayList<Lot>();
		if(lotId != null && lotId.startsWith("LL")){
			LargeLot ll = getLargeLotById(orgRrn, lotId);
			if(ll != null){
				List<LargeWipLot> lwLots = ll.getLargeWipLots();
				for(LargeWipLot lwl : lwLots){
					Lot lot = lwl.getLot();
					lot.setQtyTransaction(lwl.getQty());
					lots.add(lot);
				}
			}
		}else{
			Lot lot = getLotByLotId(orgRrn, lotId);
			lots.add(lot);
		}
		return lots;
	}
	
	public LargeLot getLargeLotById(long orgRrn, String lotId) throws ClientException{
		try {
			StringBuffer hql = new StringBuffer(" SELECT LargeLot FROM LargeLot as LargeLot ");
			hql.append("WHERE");
			hql.append(ADBase.BASE_CONDITION);
			hql.append("AND lotId = ?");
			logger.debug(hql);
			Query query = em.createQuery(hql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, lotId);
			
			List lst = query.getResultList();
			
			if(lst != null && lst.size() > 0){
				LargeLot ll = (LargeLot) lst.get(0);
				ll.getLargeWipLots().size();
				return ll;
			}
			
			return null;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	public Lot getMaterialLot(long orgRrn, Material material, long userRrn) throws ClientException{
		return getMaterialLot(orgRrn, material, userRrn, null);
	}
	
	/**
	 * 工作令接受时生成批号的方法
	 * 对于MATERIAL类型的物料 要附加 子工作令的Object_RRN
	 * 供仓库入货架时自动追踪到工作令并自动做生产入库
	 */
	public Lot getMaterialLotForMoLine(long orgRrn, Material material, long userRrn, ManufactureOrderLine moLine) throws ClientException{
		return getMaterialLot(orgRrn, material, userRrn, moLine);
	}
	
	private ManufactureOrderLine getMoLineByUid(long uid) throws ClientException {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ManufactureOrderLine line ");
		hql.append(" WHERE line.lineUid = ? ");
		
		Query query = em.createQuery(hql.toString());
		query.setParameter(1, uid);
		
		List rslt = query.getResultList();
		if(rslt != null && rslt.size() > 0){
			return (ManufactureOrderLine) rslt.get(0);
		}
		return null;
	}
	
	/*
	 *采购订单行保存的时候,会去记录barcode,规则是物料编号+年月日+序列号*/
	public List<Lot> generateBarCodeLot(long orgRrn, Material material, BigDecimal qty, int batchNumber, long userRrn) throws ClientException {
		List<Lot> lots = new ArrayList<Lot>();
		try{
			BigDecimal batchSize;
			
			double qtyLine = qty.doubleValue();
			int intQtyLine = qty.intValue();
			if (qtyLine == intQtyLine) {
				batchSize = qty.divideToIntegralValue(new BigDecimal(batchNumber));
			} else {
				batchSize = qty.divide(new BigDecimal(batchNumber), Constants.DIVIDE_SCALE, RoundingMode.FLOOR);
			}
			for (int i = 0; i < batchNumber; i++) {
				Lot lot = new Lot();
				lot.setIsActive(true);
				lot.setCreatedBy(userRrn);
				lot.setCreated(new Date());
				lot.setUpdatedBy(userRrn);
				lot.setOrgRrn(orgRrn);
				lot.setLotId(generateNextNumber(orgRrn, material));
				lot.setLotType(material.getLotType());
				lot.setMaterialRrn(material.getObjectRrn());
				lot.setMaterialId(material.getMaterialId());
				lot.setMaterialName(material.getName());
				lot.setQtyInitial(batchSize);
				lot.setQtyCurrent(batchSize);
				lot.setIsUsed(false);
				lots.add(lot);
			}
			return lots;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<VInvNoTransfer> getVInvNoTransfer(String whereClause) throws ClientException{
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" select imline.*,tline.*,rline.qty_receipt from ( ");
			sql.append(" select iml.object_rrn,iml.org_rrn,iml.is_active,iml.created,iml.created_by, ");
			sql.append(" iml.updated,iml.updated_by,iml.lock_version,iml.movement_id, ");
			sql.append(" iml.qty_movement,iml.material_rrn,iml.material_id,iml.material_name, ");
			sql.append(" iml.po_line_rrn,im.receipt_id,im.date_approved,im.receipt_date_approved ");
			sql.append(" from inv_movement_line iml  , ( ");
			sql.append(" select im.doc_id,im.receipt_id,im.date_approved, ");
			sql.append(" r.date_approved receipt_date_approved from  inv_movement im ,inv_receipt r  ");
			sql.append(" where im.org_rrn =139420 and im.doc_status = 'APPROVED'  ");
			sql.append(" and im.doc_type = 'PIN' ");
			sql.append(" and im.receipt_id = r.doc_id ");
			if(whereClause!=null && !"".equals(whereClause)){
				sql.append(" AND ");
				sql.append(whereClause);
			}
//			sql.append(" /*and im.date_approved 时间处理*/ ");
			sql.append(" and r.being_transfer = 'Y') im  ");
			sql.append(" where iml.org_rrn=139420 and iml.movement_id = im.doc_id ");
			sql.append(" /*and iml.updated >=  im.date_approved*/ ");
			sql.append(" ) imline , ( ");
//			sql.append(" /*调拨单的调拨数量，调拨日期*/ ");
			sql.append(" select iml.movement_id trf_movement_id,iml.qty_movement trf_qty_movement, ");
			sql.append(" im.date_approved trf_date_approved,iml.material_rrn trf_material_rrn  ");
			sql.append(" from inv_movement_line iml,inv_movement im  ");
			sql.append(" where im.org_rrn = 139420  ");
			sql.append(" and im.doc_type = 'TRF' and im.warehouse_rrn = 151043 and target_warehouse_rrn = 151046 ");
			sql.append(" and im.doc_status =  'APPROVED'  ");
			sql.append(" and iml.movement_id =  im.doc_id ");
			sql.append(" ) tline, ");
			sql.append(" inv_receipt_line rline ");
			sql.append(" where imline.material_rrn = tline.trf_material_rrn ");
			sql.append(" and imline.date_approved <= tline.trf_date_approved ");
			sql.append(" and imline.receipt_id  = rline.receipt_id ");
			sql.append(" and imline.po_line_rrn  = rline.po_line_rrn ");
			sql.append(" and imline.material_rrn = rline.material_rrn ");
			sql.append(" order by imline.material_id asc ");
			
			Query query = em.createNativeQuery(sql.toString(),VInvNoTransfer.class);
			List<VInvNoTransfer>  results = query.getResultList();
			return results;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}

	}
	/**
	 * 1.库存高位监控范围： A、B类物料
	 * 2.外购件的库存
	 * 库存过高：库存 > 1..15* 安全库存 且 库存 > 1.15* 已分配数
	 *库存略高：1.15* 安全库存 > 库存 > 0.8* 安全库存 且 库存 > 1.3* 已分配数
	 *库存不足：库存 < 已分配数
	 *库存适中：除去以上三种状态的剩余物料
	 * */
	public List<Material> queryMaterialQtysAlarm(Long orgRrn,String whereClause,String whereClause2,String whereClause3) throws ClientException {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT DISTINCT PM.OBJECT_RRN AS MATERIAL_RRN,");
			sql.append("                PM.Org_Rrn AS ORG_RRN,");
			sql.append("                PM.MATERIAL_ID AS MATERIAL_ID,");
			sql.append("                PM.NAME AS NAME,");
			sql.append("                IVS.QTY_ONHAND AS QTY_ONHAND,");
			sql.append("                NVL(C.NEEDQTY,0) AS QTY_ALLOCATION,");
			sql.append("                IVS.QTY_ONHAND - C.NEEDQTY AS CANNEED,");
			sql.append("                CASE WHEN WV.ONWAY IS NULL THEN 0 ELSE WV.ONWAY END AS ALLONWAY,");
			sql.append("                CPP.PURCHASER, CPP.PROMISED,");
			sql.append("                PM.INVENTORY_UOM AS INVENTORY_UOM,");
			sql.append("                PM.QTY_MIN AS QTY_MIN,");
			sql.append("                CASE WHEN PM.IS_HIM ='Y' THEN ");
			sql.append("                CASE WHEN NVL(IVS.QTY_ONHAND,0) > 1.15*NVL(PM.QTY_MIN,0) and NVL(IVS.QTY_ONHAND,0) > 1.15*NVL(C.NEEDQTY,0)");
			sql.append("                THEN '库存过高' ");
			sql.append("                WHEN 1.15*NVL(PM.QTY_MIN,0) > NVL(IVS.QTY_ONHAND,0) and NVL(IVS.QTY_ONHAND,0) > 0.8*NVL(PM.QTY_MIN,0)  ");
			sql.append("                and NVL(IVS.QTY_ONHAND,0) >1.3*NVL(C.NEEDQTY,0)");
			sql.append("                THEN '库存略高'");
			sql.append("                WHEN NVL(IVS.QTY_ONHAND,0) < NVL(C.NEEDQTY,0)");
			sql.append("                THEN '库存不足'");
			sql.append("                ELSE '库存适中'");
			sql.append("                END");
			sql.append("                ELSE");
			sql.append("                CASE WHEN NVL(IVS.QTY_ONHAND,0) < NVL(C.NEEDQTY,0) THEN '库存不足'");
			sql.append("                ELSE '' END ");
			sql.append("                END AS ALARM_LEVEL,");
			sql.append("                PM.IS_HIM AS IS_HIM,");
			sql.append("                PM.LAST_PRICE AS LAST_PRICE");
			sql.append("  FROM PDM_MATERIAL PM");
			sql.append(" INNER JOIN (SELECT MATERIAL_RRN, SUM(NVL(QTY_ONHAND,0)+NVL(QTY_DIFF,0)) AS QTY_ONHAND");
			sql.append("               FROM INV_STORAGE");
			sql.append("              WHERE ORG_RRN = ?1");
			sql.append("                AND WAREHOUSE_RRN IN (151046, 151043)");
			sql.append("                AND UPDATED  >= ADD_MONTHS(SYSDATE,-24)" );//排除二年内无进出库记录的物料
			sql.append("              GROUP BY MATERIAL_RRN) IVS");
			sql.append("    ON PM.OBJECT_RRN = IVS.MATERIAL_RRN");
			sql.append("   AND PM.ORG_RRN = ?2");
//			sql.append(" INNER JOIN (SELECT WB.MATERIAL_RRN,");
//			sql.append("                    SUM(CASE WHEN WM.QTY_RECEIVE IS NULL THEN 0 ELSE SQTY END) AS NEEDQTY");
//			sql.append("               FROM (SELECT C.MO_RRN,");
//			sql.append("                            B.MATERIAL_RRN,");
//			sql.append("                            SUM(C.QTY * B.QTY_UNIT) SQTY");
//			sql.append("                       FROM WIP_MO_BOM B");
//			sql.append("                      INNER JOIN (SELECT M.MO_RRN,");
//			sql.append("                                        M.MATERIAL_RRN,");
//			sql.append("                                        M.PATH,");
//			sql.append("                                        T.QTY");
//			sql.append("                                   FROM WIP_MO_BOM M");
//			sql.append("                                  INNER JOIN (SELECT T.OBJECT_RRN,");
//			sql.append("                                                    (T.QTY - T.QTY_RECEIVE) QTY");
//			sql.append("                                               FROM WIP_MO_LINE T");
//			sql.append("                                              WHERE T.LINE_STATUS IN");
//			sql.append("                                                    ('DRAFTED', 'APPROVED','COMPLETED')) T");
//			sql.append("                                     ON M.MO_LINE_RRN = T.OBJECT_RRN");
//			sql.append("                                      ) C");
////			sql.append("                                    AND T.QTY <> 0) C");原业务为这句话，刘小童反馈需显示已分配为0
//			sql.append("                         ON B.MO_RRN = C.MO_RRN");
//			sql.append("                        AND B.MATERIAL_PARENT_RRN = C.MATERIAL_RRN");
//			sql.append("                        AND B.PATH = C.PATH || C.MATERIAL_RRN || '/'");
//			sql.append("                      GROUP BY C.MO_RRN, B.MATERIAL_RRN) WB");
//			sql.append("              INNER JOIN WIP_MO WM");
//			sql.append("                 ON WB.MO_RRN = WM.OBJECT_RRN");
//			sql.append("              INNER JOIN PDM_MATERIAL PM");
//			sql.append("                 ON WB.MATERIAL_RRN = PM.OBJECT_RRN");
//			sql.append("              INNER JOIN PDM_MATERIAL P");
//			sql.append("                 ON WM.MATERIAL_RRN = P.OBJECT_RRN");
//			sql.append("              INNER JOIN WIP_WORKCENTER WW");
//			sql.append("                 ON WM.WORKCENTER_RRN = WW.OBJECT_RRN");
//			sql.append("                AND WW.ORG_RRN = ?3");
//			sql.append("                AND WM.ORG_RRN = ?4");
//			sql.append("              GROUP BY WB.MATERIAL_RRN) C");
			//bug修复，已分配数与物料统计查询已分配数不一致的BUG上面注释掉的为已分配数
			sql.append(" LEFT JOIN (SELECT WB.MATERIAL_RRN,NVL(SUM(WB.SQTY),  ");
			sql.append("                0) AS NEEDQTY            ");
			sql.append("       FROM (SELECT C.MO_RRN, B.MATERIAL_RRN, SUM(C.QTY * B.QTY_UNIT) SQTY  ");
			sql.append("               FROM WIP_MO_BOM B  ");
			sql.append("              INNER JOIN (SELECT M.MO_RRN, M.MATERIAL_RRN, M.PATH, T.QTY  ");
			sql.append("                           FROM WIP_MO_BOM M  ");
			sql.append("                          INNER JOIN (SELECT T.OBJECT_RRN,  ");
			sql.append("                                            (T.QTY - T.QTY_RECEIVE) QTY  ");
			sql.append("                                       FROM WIP_MO_LINE T  ");
			sql.append("                                      WHERE T.LINE_STATUS IN  ");
			sql.append("                                            ('DRAFTED', 'APPROVED')) T  ");
			sql.append("                             ON (M.MO_LINE_RRN = T.OBJECT_RRN OR M.Mo_Rrn=T.Object_Rrn)  ");
			sql.append("                            AND T.QTY <> 0  ");
			sql.append("                           ) C  ");
			sql.append("                 ON B.MO_RRN = C.MO_RRN  ");
			sql.append("                AND B.MATERIAL_PARENT_RRN = C.MATERIAL_RRN  ");
			sql.append("                AND B.PATH = C.PATH || C.MATERIAL_RRN || '/'  ");
			sql.append("              GROUP BY C.MO_RRN, B.MATERIAL_RRN) WB  ");
			sql.append("      INNER JOIN PDM_MATERIAL PM  ");
			sql.append("         ON WB.MATERIAL_RRN = PM.OBJECT_RRN  ");
			sql.append("      GROUP BY WB.MATERIAL_RRN ) C ");			
			sql.append("    ON PM.OBJECT_RRN = C.MATERIAL_RRN");
			sql.append("  LEFT JOIN (SELECT PC.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                    QTY,");
			sql.append("                    (QTY - CASE");
			sql.append("                      WHEN QM IS NULL THEN");
			sql.append("                       0");
			sql.append("                      ELSE");
			sql.append("                       QM");
			sql.append("                    END) AS ONWAY");
			sql.append("               FROM (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                            SUM(CASE WHEN PP.DOC_STATUS = 'CLOSED' THEN PPL.QTY_IN ELSE PPL.QTY END) AS QTY");
			sql.append("                       FROM PUR_PO PP");
			sql.append("                      INNER JOIN PUR_PO_LINE PPL");
			sql.append("                         ON PP.OBJECT_RRN = PPL.PO_RRN");
			sql.append("                        AND PP.ORG_RRN = ?5");
			sql.append("                      GROUP BY PPL.MATERIAL_RRN) PC");
			sql.append("               LEFT JOIN (SELECT MATERIAL_RRN, SUM(QTY_MOVEMENT) AS QM");
			sql.append("                           FROM INV_MOVEMENT IM");
			sql.append("                          INNER JOIN INV_MOVEMENT_LINE IML");
			sql.append("                             ON IM.DOC_ID = IML.MOVEMENT_ID");
			sql.append("                            AND IM.DOC_STATUS IN ('APPROVED', 'COMPLETED')");
			sql.append("                          WHERE IM.ORG_RRN = ?6");
			sql.append("                            AND DOC_TYPE = 'PIN'");
			sql.append("                          GROUP BY MATERIAL_RRN) CC");
			sql.append("                 ON PC.MATERIAL_RRN = CC.MATERIAL_RRN) WV");
			sql.append("    ON PM.OBJECT_RRN = WV.MATERIAL_RRN");
			sql.append("  LEFT JOIN (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                    PP.PURCHASER     AS PURCHASER,");
			sql.append("  					PPL.DATE_PROMISED AS PROMISED");
			sql.append("               FROM PUR_PO PP");
			sql.append("              INNER JOIN PUR_PO_LINE PPL");
			sql.append("                 ON PP.DOC_ID = PPL.PO_ID");
			sql.append("                AND PP.ORG_RRN = ?7");
			sql.append("                AND PP.DOC_STATUS <> 'CLOSED'");
			sql.append("              INNER JOIN (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                                MAX(PP.DATE_APPROVED) AS LAST_TIME");
			sql.append("                           FROM PUR_PO PP");
			sql.append("                          INNER JOIN PUR_PO_LINE PPL");
			sql.append("                             ON PP.DOC_ID = PPL.PO_ID");
			sql.append("                            AND PP.ORG_RRN = ?8");
			sql.append("                            AND PP.DOC_STATUS <> 'CLOSED'");
			sql.append("                          GROUP BY PPL.MATERIAL_RRN) C");
			sql.append("                 ON PPL.MATERIAL_RRN = C.MATERIAL_RRN");
			sql.append("                AND PP.DATE_APPROVED = C.LAST_TIME) CPP");
			sql.append("    ON PM.OBJECT_RRN = CPP.MATERIAL_RRN");
			sql.append(" WHERE PM.IS_PURCHASE = 'Y'");
			sql.append(" and pm.material_category2 in ('自制','外购') ");
			sql.append(" AND PM.QTY_MIN <> 0");//排除安全库存为零的物料
			sql.append(" AND PM.abc_category in ('A','B') ");
			if(whereClause3 != null && whereClause3.trim().length() > 0){
				sql.append("   AND " + whereClause3);
			}
			if(whereClause != null && whereClause.trim().length() > 0){
				sql.append(whereClause);
			}
			
			
			StringBuffer sql2 = new StringBuffer("");
			sql2.append("   SELECT t.material_rrn, ");
			sql2.append("          t.org_rrn, ");
			sql2.append("          t.material_id, ");
			sql2.append("          t.name, ");
			sql2.append("          t.qty_onhand, ");
			sql2.append("          t.qty_allocation, ");
			sql2.append("          t.allonway, ");
			sql2.append("          t.inventory_uom, ");
			sql2.append("          t.qty_min, ");
			sql2.append("          t.alarm_level, ");
			sql2.append("          t.is_him, ");
			sql2.append("          t.last_price, ");
			sql2.append("          wmsys.wm_concat (t.purchaser) purchaser, ");
			sql2.append("		   wmsys.wm_concat (to_char(t.promised,'yyyy-mm-dd')) promised");
			sql2.append(" FROM (");
			sql2.append(sql);
			sql2.append(") t where 1=1 ");
			if(whereClause2 != null && whereClause2.trim().length() > 0){
				sql2.append(whereClause2);
			}
			sql2.append(" GROUP BY t.material_rrn, ");
			sql2.append("          t.org_rrn, ");
			sql2.append("          t.material_id, ");
			sql2.append("          t.name, ");
			sql2.append("          t.qty_onhand, ");
			sql2.append("          t.qty_allocation, ");
			sql2.append("          t.allonway, ");
			sql2.append("          t.inventory_uom, ");
			sql2.append("          t.qty_min, ");
			sql2.append("          t.alarm_level, ");
			sql2.append("          t.is_him, ");
			sql2.append("          t.last_price ");
			
			//处理最新到货日期
			StringBuffer sql3 = new StringBuffer();
			sql3.append(" select min(line.date_promised) date_promised,line.material_rrn from pur_po_line line WHERE  ORG_RRN = ?9 ");
			sql3.append(" and line_status in ('DRAFTED','APPROVED') AND line.Qty-(case when line.QTY_IN is null then 0 else line.QTY_IN end)>0 ");
			sql3.append(" group by line.material_rrn");
			
			StringBuffer sql5 = new StringBuffer();
			sql5.append(" select distinct vvm.material_rrn,vvm.least_quantity from vdm_vendor_material vvm ");
			sql5.append("  where vvm.org_rrn = 139420 and vvm.is_primary = 'Y'");
		
			StringBuffer sql4 = new StringBuffer();
			sql4.append("   SELECT sql2.material_rrn, ");
			sql4.append("      	   sql2.org_rrn, ");
			sql4.append("          sql2.material_id, ");
			sql4.append("          sql2.name, ");
			sql4.append("          sql2.qty_onhand, ");
			sql4.append("          sql2.qty_allocation, ");
			sql4.append("          sql2.allonway, ");
			sql4.append("          sql2.purchaser, ");
			sql4.append("	to_char(sql3.date_promised,'yyyy-mm-dd') promised,");
			sql4.append("          sql2.inventory_uom, ");
			sql4.append("          sql2.qty_min, ");
			sql4.append("          sql2.alarm_level, ");
			sql4.append("          sql2.is_him, ");
			sql4.append("          sql2.last_price, ");
			sql4.append("          sql5.least_quantity ");
			sql4.append(" FROM (");
			sql4.append(sql2.toString());
			sql4.append(" ) sql2");
			sql4.append(" left join (");
			sql4.append(sql3);
			sql4.append(" ) sql3");
			sql4.append(" on ");
			sql4.append(" sql2.material_rrn = sql3.material_rrn ");
			sql4.append(" left join (");
			sql4.append(sql5);
			sql4.append(" ) sql5");
			sql4.append(" on ");
			sql4.append(" sql2.material_rrn = sql5.material_rrn ");
			
			
			Query query = em.createNativeQuery(sql4.toString());
			
			query.setParameter(1, orgRrn);
			query.setParameter(2, orgRrn);
//			query.setParameter(3, orgRrn);
//			query.setParameter(4, orgRrn);
			query.setParameter(5, orgRrn);
			query.setParameter(6, orgRrn);
			query.setParameter(7, orgRrn);
			query.setParameter(8, orgRrn);
			query.setParameter(9, orgRrn);
			
			List<Object[]> objs = query.getResultList();
			List<Material> materials = new ArrayList<Material>();
			
			for(Object[] obj : objs){
				Material m = new Material();
				m.setObjectRrn(((BigDecimal) obj[0]).longValue());
				m.setOrgRrn(((BigDecimal) obj[1]).longValue());
				m.setMaterialId((String)obj[2]);
				m.setName((String)obj[3]);
				m.setQtyOnHand((BigDecimal) obj[4]);
				m.setQtyAllocation((BigDecimal) obj[5]);
				m.setQtyTransit((BigDecimal) obj[6]);
				m.setPlannerId((String)obj[7]);//借用此字段保存采购员信息
				m.setPromised((String)obj[8]);//最新到货日期
				m.setInventoryUom((String) obj[9]);//单位
				m.setQtyMin((BigDecimal) obj[10]);//安全库存
				m.setAlarmLevel((String) obj[11]);//报警程度
				m.setIsHIM((String) obj[12]);//库存高位监控
				m.setLastPrice((BigDecimal) obj[13]);//单价
				m.setQtyMinService((BigDecimal) obj[14]);//最低采购批量
				BigDecimal lastPrice =  m.getLastPrice()!=null?m.getLastPrice():BigDecimal.ZERO;
				BigDecimal qtyOnHand = m.getQtyOnHand()!=null?m.getQtyOnHand():BigDecimal.ZERO;
				BigDecimal totalPrice = lastPrice.multiply(qtyOnHand);//总价
				m.setTotalPrice(totalPrice);
				materials.add(m);
			}
		
			return materials;
		} catch (Exception e) {
			throw new ClientException(e);
		}
		}	
	/**
	 * 采购入库冲销暂估清单
	 * 
	 * */
	public List<VMovementLineTempEstimate> getReversalTempEstimate(Long orgRrn,String whereClause) throws ClientException{
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("  select  imline.object_rrn,imline.org_rrn,imline.is_active,imline.created,imline.created_by,  ");
			sql.append("  imline.updated,imline.updated_by,imline.lock_version,trunc(im.date_approved) date_approved,  ");
			sql.append("  imline.movement_id,imline.material_id,imline.material_name,imline.uom_id,  ");
			sql.append("  imline.qty_movement,imline.unit_price,imline.assess_line_total,v.company_name vendor_name,  ");
			sql.append("  imline.invoice_line_total-imline.assess_line_total reversal_total,  ");
			sql.append("  im.warehouse_id,im.date_write_off  ");
			sql.append("   from inv_movement_line imline ,(  ");
			sql.append("   select im.* from inv_movement im  where im.org_rrn =?1 and doc_type ='PIN' and im.date_write_off is not null   ");
			if(whereClause!=null && whereClause.trim().length()>0){
				sql.append(whereClause);
//				sql.append("  and trunc(im.date_write_off) >= to_date('2013-07-01','YYYY-MM-DD')  ");
//				sql.append("  and trunc(im.date_write_off) <= to_date('2013-07-31','YYYY-MM-DD')  ");
			}
			sql.append("   ) im ,  ");
			sql.append("   vdm_vendor v   ");
			sql.append("   where imline.org_rrn =?2  ");
			sql.append("  and imline.movement_id  =im.doc_id  ");
			sql.append("  and im.vendor_rrn = v.object_rrn   ");
			sql.append("  order by  im.date_approved asc ");
			
			Query query = em.createNativeQuery(sql.toString(),VMovementLineTempEstimate.class);
			query.setParameter(1, orgRrn);
			query.setParameter(2, orgRrn);
			List<VMovementLineTempEstimate>  results = query.getResultList();
			return results;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/**
	 *服务公司提醒
	 * 1.当服务公司物料的当前库存小于安全库存的时候弹出提醒
	 * */
	public List<Material> getServiceMaterialAlarm(Long orgRrn,String whereClause) throws ClientException{
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("   SELECT PM1.OBJECT_RRN,S1.QTY_ONHAND,PM1.Material_Id,pm1.name,pm1.qty_min_service FROM ( ");
			sql.append("     SELECT MATERIAL_RRN, SUM(NVL(QTY_ONHAND,0)+NVL(QTY_DIFF,0)) AS QTY_ONHAND ");
			sql.append("        FROM INV_STORAGE S/*,PDM_MATERIAL M */ ");
			sql.append("    WHERE S.ORG_RRN = ?1  ");
			sql.append("    AND S.MATERIAL_RRN IN ( SELECT PM.OBJECT_RRN FROM PDM_MATERIAL PM WHERE  ");
			sql.append("     PM.ORG_RRN=?2 AND PM.QTY_MIN_SERVICE IS NOT NULL ) ");
			sql.append("    AND WAREHOUSE_RRN IN (151049,151055, 151043) ");
			sql.append("     group by MATERIAL_RRN ");
			sql.append("   ) S1,( SELECT PM.OBJECT_RRN,pm.material_id,pm.name,PM.QTY_MIN_SERVICE FROM PDM_MATERIAL PM WHERE  ");
			sql.append("   PM.ORG_RRN=?3 AND PM.QTY_MIN_SERVICE IS NOT NULL ) PM1 ");
			sql.append("   WHERE S1.MATERIAL_RRN = PM1.OBJECT_RRN ");
			sql.append("   AND S1.QTY_ONHAND < PM1.QTY_MIN_SERVICE ");
			if(whereClause!=null && whereClause.trim().length() >0 ){
				sql.append(whereClause );
			}
			sql.append("   ORDER BY PM1.Material_id ");
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, orgRrn);
			query.setParameter(3, orgRrn);
			
			List<Object[]> objs = query.getResultList();
			List<Material> materials = new ArrayList<Material>();
			for(Object[] obj : objs){
				Material m = new Material();
				m.setObjectRrn(((BigDecimal) obj[0]).longValue());
				m.setQtyOnHand((BigDecimal) obj[1]);
				m.setMaterialId((String)obj[2]);
				m.setName((String)obj[3]);
				m.setQtyMinService((BigDecimal) obj[4]);
				materials.add(m);
			}
			return materials;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/**
	 * 滤芯调拨到开能车间
	 * */
	public MovementTransfer approveMovementTransferToCana(MovementTransfer transfer, long userRrn) throws ClientException {
		try{
			if (transfer.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (transfer.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			Long targetWarehouseRrn = transfer.getTargetWarehouseRrn();
			
			ADUser user = em.find(ADUser.class, userRrn);
			transfer.setUserApproved(user.getUserName());
			Warehouse house = em.find(Warehouse.class, transfer.getWarehouseRrn());
			Warehouse targetHouse = em.find(Warehouse.class, targetWarehouseRrn);
			transfer.setTargetWarehouseId(targetHouse.getWarehouseId());
			transfer.setDocStatus(MovementTransfer.STATUS_APPROVED);
			transfer.setDateApproved(new Date());
			transfer.setUpdatedBy(userRrn);
			em.merge(transfer);
			
			if (transfer.getMovementLines().size() == 0) {
				throw new ClientException("inv.transfer_quantity_zero"); 
			}
			
			long transSeq = basManager.getHisSequence();
			for (MovementLine line : transfer.getMovementLines()) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				
				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(Movement.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				//更新库存
				updateStorage(transfer.getOrgRrn(), line.getMaterialRrn(), transfer.getWarehouseRrn(), line.getQtyMovement().negate(), true, userRrn);
				
				//更新开能库存
				List<Material> canaMaterials =  adManager.getEntityList(139420L, Material.class,Integer.MAX_VALUE, "materialId = '"+material.getMaterialId()+"'",null);
				if(canaMaterials==null || canaMaterials.size()==0){
					throw new ClientException("开能不存在该物料");
				}
				Material canaMaterial = canaMaterials.get(0);
				updateStorage(139420L,canaMaterial.getObjectRrn(), 151043L, line.getQtyMovement(), true, userRrn);

				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					Lot lot = this.getMaterialLot(transfer.getOrgRrn(), material, userRrn);
					LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), userRrn);
					BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());
					if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
						throw new ClientException("inv.not_sufficient_quantity");
					}
					this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), line.getQtyMovement().negate(), userRrn);
					//开能批次处理
					Lot canaLot = this.getMaterialLot(139420L, material, userRrn);
					this.updateLotStorage(139420L, canaLot.getObjectRrn(), 151043L, line.getQtyMovement(), userRrn);
				} else {
					String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
					List<MovementLineLot> movementLots = adManager.getEntityList(transfer.getOrgRrn(), MovementLineLot.class, Integer.MAX_VALUE, whereClause, "");
					//检查Lot的数量与Line中的数量是否相等
					BigDecimal qtyLine = line.getQtyMovement();
					BigDecimal qtyTotal = BigDecimal.ZERO;
					for (MovementLineLot movementLot : movementLots) {
						qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
					}
					if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
						throw new ClientException("inv.transfer_lot_qty_different");
					}
				
					for (MovementLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
							}
						} else {
							LotStorage lotStorage = this.getLotStorage(transfer.getOrgRrn(), movementLot.getLotRrn(), transfer.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						
						this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), movementLot.getQtyMovement().negate(), userRrn);
						//开能业务处理
						List<Lot> canaLots =  adManager.getEntityList(139420L, Lot.class,Integer.MAX_VALUE, "lotId = '"+lot.getLotId()+"'",null);
						Lot canaLot =null;
						if(canaLots==null || canaLots.size()==0){
							canaLot = new Lot();
							canaLot.setOrgRrn(139420L);
							canaLot.setIsActive(true);
							canaLot.setCreated(new Date());
							canaLot.setCreatedBy(userRrn);
							canaLot.setUpdated(new Date());
							canaLot.setUpdatedBy(userRrn);
							canaLot.setDescription("滤芯调拨至开能的物料使用");
							canaLot.setPosition(Lot.POSITION_GEN);
							canaLot.setLotId(lot.getLotId());
							canaLot.setLotType(material.getLotType());
							canaLot.setMaterialRrn(material.getObjectRrn());
							canaLot.setMaterialId(material.getMaterialId());
							canaLot.setMaterialName(material.getName());
							em.persist(canaLot);
						}else{
							canaLot = canaLots.get(0);
						}
						
						this.updateLotStorage(139420L, canaLot.getObjectRrn(), 151043L, movementLot.getQtyMovement(), userRrn);
//						this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getTargetWarehouseRrn(), movementLot.getQtyMovement(), userRrn);

						lot.setWarehouseRrn(transfer.getTargetWarehouseRrn());
						lot.setWarehouseId(transfer.getTargetWarehouseId());
						em.merge(lot);
	
						LotHis his = new TransferLotHis(lot);
						his.setHisSeq(transSeq);
						em.persist(his);
					}
				}
			}
			return transfer;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementLine saveMovementTransferLineToCana(MovementTransfer transfer, MovementLine line, long userRrn) throws ClientException {
		List<MovementLine> list = new ArrayList<MovementLine>();
		list.add(line);
		transfer = saveMovementTransferLineToCana(transfer, list, userRrn);
		return transfer.getMovementLines().get(0);
	}	
	
	public MovementTransfer saveMovementTransferLineToCana(MovementTransfer transfer, List<MovementLine> lines, long userRrn) throws ClientException {
		try{
			if (transfer.getObjectRrn() == null) {
				transfer.setIsActive(true);
				transfer.setCreatedBy(userRrn);
				transfer.setCreated(new Date());
				transfer.setTotalLines(0L);
				transfer.setDocStatus(MovementTransfer.STATUS_DRAFTED);
				transfer.setDocType(MovementTransfer.DOCTYPE_TRF);
				transfer.setDbaMark(MovementTransfer.DBA_MARK_TRANSFER_CANA);
				String docId = transfer.getDocId();
				if (docId == null || docId.length() == 0) {
					transfer.setDocId(generateTransferCode(transfer));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<Movement> movements = adManager.getEntityList(transfer.getOrgRrn(), Movement.class, 2, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}

				ADUser user = em.find(ADUser.class, userRrn);
				transfer.setUserCreated(user.getUserName());
				em.persist(transfer);
			} 
			if (transfer.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (transfer.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			if (transfer.getWarehouseRrn().equals(transfer.getTargetWarehouseRrn())) {
				throw new ClientException("inv.warehouse_target_can_not_equal");
			}
			Warehouse house = em.find(Warehouse.class, transfer.getWarehouseRrn());
			transfer.setWarehouseId(house.getWarehouseId());
			Warehouse targetHouse = em.find(Warehouse.class, transfer.getTargetWarehouseRrn());
			transfer.setTargetWarehouseId(targetHouse.getWarehouseId());
			
			List<MovementLine> savaLine = new ArrayList<MovementLine>();
			for (MovementLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getQtyMovement().doubleValue() == 0) {
					continue;
				}
				
				//如果是Update，则将原记录删除
				if (line.getObjectRrn() != null) {
					MovementLine oldLine = new MovementLine();
					oldLine.setObjectRrn(line.getObjectRrn());
					oldLine.setMovementRrn(line.getMovementRrn());
					
					em.merge(transfer);
					deleteMovementTransferLine(oldLine, false, userRrn);
					em.flush();
					transfer = (MovementTransfer)em.find(Movement.class, transfer.getObjectRrn());
					transfer.setMovementLines(null);
					
					line.setObjectRrn(null);
				}
				
				if (line.getLocatorRrn() != null) {
					Locator locator = em.find(Locator.class, line.getLocatorRrn());
					line.setLocatorId(locator.getLocatorId());
				}
				
				if (line.getObjectRrn() != null) {
					
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(transfer.getObjectRrn());
					line.setMovementId(transfer.getDocId());
					transfer.setTotalLines(transfer.getTotalLines() + 1);
					em.persist(line);
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
					for (MovementLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				} else {
					List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
					for (MovementLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (!(Lot.POSITION_INSTOCK.equals(lot.getPosition()) || Lot.POSITION_GEN.equals(lot.getPosition()))) {
							throw new ClientParameterException("inv.lot_not_in", lot.getLotId());
						} 
						if (lot.getIsUsed()) {
							throw new ClientParameterException("inv.lot_already_used", lot.getLotId());
						}
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
							}
						} else {
							LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						//调拨完后将目标仓库设为批次的当前仓库(此处不需要，放到approveMovementTransfer中去了)						
//						lot.setWarehouseRrn(transfer.getTargetWarehouseRrn());
//						lot.setWarehouseId(transfer.getTargetWarehouseId());
						lot.setTransferLineRrn(line.getObjectRrn());
						em.merge(lot);

						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			transfer.setUpdatedBy(userRrn);
			transfer.setMovementLines(null);
			em.merge(transfer);
			transfer.setMovementLines(savaLine);
			
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return transfer;
	}
	

	
	/**
	 *原料库存清单
	 * */
	public List<VMaterialStorageList> getMaterialStorageList(Long orgRrn,String whereClause) throws ClientException{
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" select rownum object_rrn,ma.org_rrn,ma.is_active,ma.created,ma.created_by, ");
			sql.append(" ma.updated,ma.updated_by,ma.lock_version,ma.name,ma.material_id,ma.material_category2, ");
			sql.append(" ma.qty_min,ma.package_spec,ma.qty_diff,ma.m_qty_onhand,ma.e_qty_onhand ");
			sql.append(" from ( ");
			sql.append(" select m.object_rrn,m.org_rrn,m.is_active,m.created,m.created_by, ");
			sql.append(" m.updated,m.updated_by,m.lock_version,m.name,m.material_id,m.material_category2, ");
			sql.append(" m.qty_min,m.package_spec,s.qty_diff, ");
			sql.append(" sum(decode(s.warehouse_rrn,151046,s.qty_onhand,null)) m_qty_onhand, ");
			sql.append(" sum(decode(s.warehouse_rrn,151043,s.qty_onhand,null)) e_qty_onhand ");
			sql.append(" from pdm_material m inner join inv_storage s on m.object_rrn = s.material_rrn ");
			sql.append(" where m.org_rrn =?1 ");
			if(whereClause!=null && whereClause.trim().length() >0 ){
				sql.append(whereClause );
			}
			sql.append(" and m.is_purchase = 'Y' ");
			sql.append(" and s.org_rrn = ?2 ");
			sql.append(" and s.warehouse_rrn in (151046,151043) ");
			sql.append(" group by m.object_rrn,m.org_rrn,m.is_active,m.created,m.created_by, ");
			sql.append(" m.updated,m.updated_by,m.lock_version,m.name,m.material_id,m.material_category2, ");
			sql.append(" m.qty_min,m.package_spec,s.qty_diff ");
			sql.append( " ) ma" );
			sql.append(" ORDER BY material_id asc");
			
			Query query = em.createNativeQuery(sql.toString(),VMaterialStorageList.class);
			query.setParameter(1, orgRrn);
			query.setParameter(2, orgRrn);
			
			List<VMaterialStorageList> results = query.getResultList();
			return results;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/**
	 * 备件采购入库(目的是为了更新供应商的价格,因为需要支持采购入库修改价格和采购订单的价格一样，因此需要重写)
	 * */
	public MovementIn bjApproveMovementIn(MovementIn in, MovementIn.InType inType, boolean writeOffFlag, boolean seniorApprove, long userRrn, boolean isWriteOff) throws ClientException {
		try{
			Date now = new Date(); 
			if (in.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Long warehouseRrn = in.getWarehouseRrn();
			ADUser user = em.find(ADUser.class, userRrn);
			in.setUserApproved(user.getUserName());
			Warehouse house = em.find(Warehouse.class, warehouseRrn);
			in.setWarehouseId(house.getWarehouseId());
			if (writeOffFlag) {
				in.setDateWriteOff(now);
				in.setDocStatus(PurchaseOrder.STATUS_COMPLETED);
			} else {
				in.setDocStatus(PurchaseOrder.STATUS_APPROVED);
			}
			in.setUpdatedBy(userRrn);
			in.setDateApproved(now);
			
			em.merge(in);
			in = em.getReference(in.getClass(), in.getObjectRrn());
			
			if (in.getMovementLines().size() == 0) {
				throw new ClientException("inv.in_quantity_zero"); 
			}
			
			Date dateIn = new Date();
			long transSeq = basManager.getHisSequence();
			
			List<MovementLine> inLines = new ArrayList<MovementLine>();
			BigDecimal accessLineTotal=new BigDecimal(0);
			BigDecimal invoiceLineTotal=new BigDecimal(0);
			for (MovementLine inLine : in.getMovementLines()) {
				Material material = em.find(Material.class, inLine.getMaterialRrn());
				if(inLine.getPoLineRrn()!=null){
					PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, inLine.getPoLineRrn());
					PurchaseOrder po = em.find(PurchaseOrder.class, poLine.getPoRrn());
					//查找供应商
					StringBuffer vmSql = new StringBuffer();
					vmSql.append(" SELECT VendorMaterial FROM VendorMaterial as VendorMaterial ");
					vmSql.append(" WHERE vendorRrn=? ");
					vmSql.append(" AND materialRrn=? ");
					VendorMaterial vendorMaterial = null;
					Query query = em.createQuery(vmSql.toString());
					query.setParameter(1, po.getVendorRrn());
					query.setParameter(2, poLine.getMaterialRrn());
					List<VendorMaterial> vendorMaterials = (List<VendorMaterial>)query.getResultList();
					if (vendorMaterials.size() > 0) {
						vendorMaterial = vendorMaterials.get(0);
						if (vendorMaterial.getLowestPrice() == null || vendorMaterial.getLowestPrice().compareTo(inLine.getUnitPrice()) > 0) {
							vendorMaterial.setLowestPrice(inLine.getUnitPrice());
						}
						vendorMaterial.setLastPrice(inLine.getUnitPrice());
						vendorMaterial.setReferencedPrice(inLine.getUnitPrice());
						em.merge(vendorMaterial);
						material.setLastPrice(poLine.getUnitPrice());//物料设置最近采购价
						em.merge(material);
					}
				}
				
				Locator locator = null;
				if (inLine.getLocatorRrn() != null) {
					locator = em.find(Locator.class, inLine.getLocatorRrn());
					inLine.setLocatorId(locator.getLocatorId());
				}
				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(inLine.getLineStatus())) {
					inLine.setLineStatus(DocumentationLine.LINESTATUS_APPROVED);
					inLine.setUpdatedBy(userRrn);
					em.merge(inLine);
				}
				
				if (MovementIn.InType.PIN == inType) {
					if (inLine.getPoLineRrn() != null) {
						PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, inLine.getPoLineRrn());
						PurchaseOrder po = em.find(PurchaseOrder.class, poLine.getPoRrn());
						poLine.setQtyIn((poLine.getQtyIn() == null ? BigDecimal.ZERO : poLine.getQtyIn()).add(inLine.getQtyMovement()));
						if (poLine.getQtyIn().compareTo(poLine.getQty()) > 0) {
							throw new ClientException("inv.in_larger_than_order");
						} 
						poLine.setUpdatedBy(userRrn);
						em.merge(poLine);
						if (poLine.getQtyIn().compareTo(poLine.getQty()) == 0) {
							poLine.setLineStatus(PurchaseOrder.STATUS_COMPLETED);
							em.merge(poLine);
							if (PurchaseOrder.STATUS_COMPLETED.equals(poLine.getLineStatus())) {
								boolean completeFlag = true;
								boolean closeFlag = false;
								for (PurchaseOrderLine line : po.getPoLines()){
									if (!PurchaseOrder.STATUS_COMPLETED.equals(line.getLineStatus()) && 
											!PurchaseOrder.STATUS_CLOSED.equals(line.getLineStatus())) {
										completeFlag = false;
										closeFlag = false;
										break;
									}
									if (PurchaseOrder.STATUS_CLOSED.equals(line.getLineStatus())) {
										closeFlag = true;
									}
								}
								if (closeFlag) {
									po.setDocStatus(PurchaseOrder.STATUS_CLOSED);
									em.merge(po);
								} else if (completeFlag) {
									po.setDocStatus(PurchaseOrder.STATUS_COMPLETED);
									em.merge(po);
								}
							}
						} 
						
						//更新暂估金额
						if (PurchaseOrder.INVOICE_TYPE_VAT.equals(po.getInvoiceType())) {
							BigDecimal vatRate = BigDecimal.ZERO;
							if (po.getVatRate() != null) {
								vatRate = po.getVatRate();
							}
							inLine.setVatRate(po.getVatRate());
							inLine.setAssessLineTotal(inLine.getQtyMovement().multiply(poLine.getUnitPrice())
									.divide(vatRate.add(BigDecimal.ONE), 2, RoundingMode.HALF_UP/*向最接近数字方向舍入的舍入模式，如果与两个相邻数字的距离相等，则向上舍入*/));
							accessLineTotal = accessLineTotal.add(inLine.getAssessLineTotal()==null?BigDecimal.ZERO:inLine.getAssessLineTotal());
							invoiceLineTotal = invoiceLineTotal.add(inLine.getInvoiceLineTotal()==null?BigDecimal.ZERO:inLine.getInvoiceLineTotal());
						} else {
							inLine.setVatRate(null);
//							inLine.setAssessLineTotal(inLine.getQtyMovement().multiply(poLine.getUnitPrice()));
							BigDecimal linePrice = inLine.getQtyMovement().multiply(poLine.getUnitPrice());
							BigDecimal assTotal = linePrice.divide(new BigDecimal("1.17"),2,RoundingMode.HALF_UP);//财务顾洁要求保留2位有效数字
							inLine.setAssessLineTotal(assTotal);
							accessLineTotal = accessLineTotal.add(inLine.getAssessLineTotal()==null?BigDecimal.ZERO:inLine.getAssessLineTotal());
						}
						em.merge(inLine);
					}
				}
				
				//更新库存
				updateStorage(in.getOrgRrn(), inLine.getMaterialRrn(), warehouseRrn, inLine.getQtyMovement(), isWriteOff, userRrn);
				
				if (MovementIn.IN_TYPE_AMOUNT_ADJUST.equals(in.getInType())
					&& inLine.getQtyMovement().doubleValue() == 0) {
				} else {
					if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
						Lot lot = this.getMaterialLot(in.getOrgRrn(), material, userRrn);
						//商品对应批次在默认仓库的库存增加,
						this.updateLotStorage(in.getOrgRrn(), lot.getObjectRrn(), warehouseRrn, inLine.getQtyMovement(), userRrn);
						
						// 扣除核销仓库中的批次库存
						switch (inType) {
						case WIN:
							//商品对应的批次在核销仓库的库存需要减少
							Warehouse writeOffWarehouse = getWriteOffWarehouse(in.getOrgRrn());
							updateLotStorage(in.getOrgRrn(), lot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), inLine.getQtyMovement().negate(), userRrn);
							break;
						}
					}
				}
				
				inLines.add(inLine);
			}
			in.setAccessLineTotal(accessLineTotal);
			in.setMovementLines(inLines);
			em.merge(in);
			in = em.getReference(in.getClass(), in.getObjectRrn());
			return in;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	/**
	 * 查询备件原材料用量的信息
	 * 业务:按维修的设备或用途统计原材料用量及金额
	 * */
	public List<SparesMaterialUse> getSparesMaterialUse(long orgRrn,String whereClause) throws ClientException{
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" 	select rownum object_rrn,42939913 org_rrn,'Y' is_active,sysdate updated,	 ");
			sql.append(" 	'' movement_id,iml2.material_rrn,iml2.material_id,iml2.material_name,iml2.qty,	 ");
			sql.append(" 	iml2.equipment_rrn,iml2.equipment_id,iml2.equipment_name,	 ");
			sql.append(" 	vvm2.referenced_price,iml2.qty * vvm2.referenced_price total_price from (	 ");
			sql.append(" 	select iml.material_rrn,iml.material_id,iml.material_name,sum(iml.qty_movement) qty,	 ");
			sql.append(" 	iml.equipment_rrn,iml.equipment_id,iml.equipment_name from inv_movement_line iml	 ");
			sql.append(" 	where iml.org_rrn = 42939913	 ");
			sql.append(" 	and iml.line_status = 'APPROVED'	 ");
			if(whereClause!=null && whereClause.length() > 0 ){
				sql.append(whereClause);
			}
			sql.append(" 	and iml.movement_id like 'U%'	 ");
			sql.append(" 	group by iml.material_rrn,iml.material_id,iml.material_name,iml.equipment_rrn,iml.equipment_id,iml.equipment_name	 ");
			sql.append(" 	) iml2	 ");
			sql.append(" 	left join (select vvm.material_rrn,vvm.referenced_price from vdm_vendor_material vvm where vvm.org_rrn = 42939913	 ");
			sql.append(" 	and vvm.is_primary = 'Y'	 ");
			sql.append(" 	) vvm2	 ");
			sql.append(" 	on iml2.material_rrn = vvm2.material_rrn	 ");

			Query query = em.createNativeQuery(sql.toString(),SparesMaterialUse.class);
			
			List<SparesMaterialUse> results = query.getResultList();
			return results;
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}

	}

	/**
	 * 导出查询备件原材料用量的信息,根据查询条件查询出详细信息
	 * */
	public List<SparesMaterialUse> getSparesMaterialUseExport(long orgRrn,String whereClause) throws ClientException{
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" 	select iml2.object_rrn,42939913 org_rrn,'Y' is_active,iml2.updated,	 ");
			sql.append(" 	iml2.movement_id,iml2.material_rrn,iml2.material_id,iml2.material_name,iml2.qty,	 ");
			sql.append(" 	iml2.equipment_rrn,iml2.equipment_id,iml2.equipment_name,	 ");
			sql.append(" 	vvm2.referenced_price,iml2.qty * vvm2.referenced_price total_price from (	 ");
			sql.append(" 	  select iml.object_rrn,iml.movement_id,iml.updated,iml.material_rrn,iml.material_id,iml.material_name,iml.qty_movement qty,	 ");
			sql.append(" 	  iml.equipment_rrn,iml.equipment_id,iml.equipment_name from inv_movement_line iml	 ");
			sql.append(" 	  where iml.org_rrn = 42939913	 ");
			sql.append(" 	  and iml.line_status = 'APPROVED'	 ");
			if(whereClause!=null && whereClause.trim().length() > 0 ){
				sql.append(whereClause);
			}
			sql.append(" 	  and iml.movement_id like 'U%'	 ");
			sql.append(" 		 ");
			sql.append(" 	) iml2	 ");
			sql.append(" 	left join (select vvm.material_rrn,vvm.referenced_price from vdm_vendor_material vvm where vvm.org_rrn = 42939913	 ");
			sql.append(" 	and vvm.is_primary = 'Y'	 ");
			sql.append(" 	) vvm2	 ");
			sql.append(" 	on iml2.material_rrn = vvm2.material_rrn	 ");


			Query query = em.createNativeQuery(sql.toString(),SparesMaterialUse.class);
			
			List<SparesMaterialUse> results = query.getResultList();
			return results;
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}

	}
	
	/**
	 * isOnHand是否改变营运库存 isWriteOff是否改变财务库存,领用单就是ERP的出库单
	 * 行政领用单审核:只审核出库单行,所有出库行审核后，才能审核领用单
	 * */
	public MovementOut approveXZMovementOut(MovementOut out,List<MovementLine> outLines, MovementOut.OutType outType, long userRrn, 
			boolean isOnHand, boolean isWriteOff) throws ClientException {
		try{
			if (out.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Long warehouseRrn = out.getWarehouseRrn();
			Warehouse house = em.find(Warehouse.class, warehouseRrn);//得到仓库
			//得到审核人
			
			if (out.getMovementLines().size() == 0) {
				throw new ClientException("inv.out_quantity_zero"); 
			}
			
			Date dateOut = new Date();
			long transSeq = basManager.getHisSequence();
			
			for (MovementLine line : outLines) {
				//得到出库单行的物料信息
				Material material = em.find(Material.class, line.getMaterialRrn());

				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(PurchaseOrder.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				//更新库存
				updateStorage(out.getOrgRrn(), line.getMaterialRrn(), out.getWarehouseRrn(), line.getQtyMovement().negate(), isOnHand, isWriteOff, userRrn);

				//改变营运数时才要涉及条码
				if(isOnHand){
					//条码的处理。。
					if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
						Lot lot = this.getMaterialLot(out.getOrgRrn(), material, userRrn);
						LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
						BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());
						//调整单不检查库存
						if(!(MovementOut.OutType.AOU.equals(outType) || MovementOut.OutType.ADOU.equals(outType))){
							if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						
						this.updateLotStorage(out.getOrgRrn(), lot.getObjectRrn(), warehouseRrn, line.getQtyMovement().negate(), userRrn);
					} 
					else {
						//检查Lot的数量与Line中的数量是否相等
						String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
						List<MovementLineLot> movementLots = adManager.getEntityList(out.getOrgRrn(), MovementLineLot.class, Integer.MAX_VALUE, whereClause, "");
						BigDecimal qtyLine = line.getQtyMovement();
						BigDecimal qtyTotal = BigDecimal.ZERO;
						for (MovementLineLot movementLot : movementLots) {
							qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
						}
						if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
							throw new ClientException("inv.out_lot_qty_different");
						}
						
						for (MovementLineLot movementLot : movementLots) {
							Lot lot = em.find(Lot.class, movementLot.getLotRrn());
							LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
							if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
								if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
									throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
								}
							} else {
								//调整单不检查库存
								if(!(MovementOut.OutType.AOU.equals(outType) || MovementOut.OutType.ADOU.equals(outType))){
									BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
									if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
										throw new ClientException("inv.not_sufficient_quantity");
									}
								}
							}
							this.updateLotStorage(out.getOrgRrn(), movementLot.getLotRrn(), warehouseRrn, movementLot.getQtyMovement().negate(), userRrn);
							
							if (Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {//如果是serial类型的批次需将position改为instock
								if(MovementOut.OutType.AOU.equals(outType) || MovementOut.OutType.ADOU.equals(outType)){
									if(MovementOut.OUT_TYPE_OUT_ADJUST.equals(out.getOutType()) || MovementOut.OUT_TYPE_SALE_ADJUST.equals(out.getOutType())){//如果是调整出库且是出库调整将position置为INSTOCK
										lot.setDateIn(dateOut);//出库调整相当于入库
										lot.setPosition(Lot.POSITION_INSTOCK);
									}else if(MovementOut.OUT_TYPE_IN_ADJUST.equals(out.getOutType())){//如果是调整出库且是入库调整将position置为OUT
										lot.setDateOut(dateOut);//采购调整相当于出库
										lot.setPosition(Lot.POSITION_OUT);
									}
								}else{
									lot.setDateOut(dateOut);
									lot.setPosition(Lot.POSITION_OUT);
								}
							}
							
							em.merge(lot);
							
							LotHis his = null;
							switch (outType) {
							case SOU:
								his = new SouLotHis(lot);
								break;
							case OOU:
								his = new OouLotHis(lot);
								break;
							case AOU:
								his = new AouLotHis(lot);
								break;
							case ADOU:
								his = new AdouLotHis(lot);
								break;
							case DOU:
								his = new DouLotHis(lot);
								break;
							}
							if (his != null) {
								his.setHisSeq(transSeq);
								em.persist(his);
							}
						}
					}
				}
			}
			em.flush();
			ADUser user = em.find(ADUser.class, userRrn);
			out = em.find(MovementOut.class, out.getObjectRrn());
			List<MovementLine> movementLines = out.getMovementLines();
			boolean flag = true;//是否全部审核
			for(MovementLine movementLine : movementLines){
				if(!PurchaseOrder.STATUS_APPROVED.equals(movementLine.getLineStatus())){
					flag = false;
					break;
				}
			}
			if(flag){
				out.setUserApproved(user.getUserName());
				out.setWarehouseId(house.getWarehouseId());
				out.setDocStatus(MovementOut.STATUS_APPROVED);
				out.setUpdatedBy(userRrn);
				out.setDateApproved(new Date());
				em.merge(out);				
			}

			if (MovementOut.OutType.SOU == outType) {//销售出库
				try {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String approveDate = df.format(out.getDateApproved());
					//设置crm销货单中的出库单号
					salManager.approveSo(out.getSoId(), out.getObjectRrn(), out.getDocId(), approveDate);
				} 
				catch (Exception e) {
					throw new ClientException(e);
				}
				for (MovementLine line : out.getMovementLines()) {
					String whereClause = " materialId = '" + line.getMaterialId() + "'";
					List<SalesOrderSum> soSums = adManager.getEntityList(out.getOrgRrn(), SalesOrderSum.class, Integer.MAX_VALUE, whereClause, "");
					if (soSums.size() > 0) {
						SalesOrderSum soSum = soSums.get(0);
						soSum.setQtySo(soSum.getQtySo().subtract(line.getQtyMovement()));
						em.merge(soSum);
					}
				}
			}
			else if(MovementOut.OutType.AOU == outType && MovementOut.OUT_TYPE_SALE_ADJUST.equals(out.getOutType())){//销售调整

				try {
					if(out.getSoId() != null && out.getSoId().trim().length() > 0){
						//如果是销售调整，就把调整单编号记到crm里去
						salManager.adjustSo(out.getSoId(), out.getObjectRrn(), out.getDocId(), null);
					}
				} 
				catch (Exception e) { 
					throw new ClientException(e);
				}
			}
			
			return out;
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} 
		catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/**
	 * 专用于奔泰销售出库单的审核
	 * 罗小华需求，审核不需要挂批次 减少批次数量，
	 */
	public MovementOut approveSalesMovementOutBT(MovementOut out, long userRrn, boolean isWriteOff) throws ClientException {
		try{
			if (out.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Long warehouseRrn = out.getWarehouseRrn();
			//得到审核人
			ADUser user = em.find(ADUser.class, userRrn);
			out.setUserApproved(user.getUserName());
			//得到仓库
			Warehouse house = em.find(Warehouse.class, warehouseRrn);
			out.setWarehouseId(house.getWarehouseId());
			
			out.setDocStatus(MovementOut.STATUS_APPROVED);
			out.setUpdatedBy(userRrn);
			out.setDateApproved(new Date());
			out.setBtLotAlarm(true);
			em.merge(out);
			
			if (out.getMovementLines().size() == 0) {
				throw new ClientException("inv.out_quantity_zero"); 
			}
			
			long transSeq = basManager.getHisSequence();
			for (MovementLine line : out.getMovementLines()) {
				//得到出库单行的物料信息
				Material material = em.find(Material.class, line.getMaterialRrn());

				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(PurchaseOrder.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				//更新物料库存
				updateStorage(out.getOrgRrn(), line.getMaterialRrn(), out.getWarehouseRrn(), line.getQtyMovement().negate(), isWriteOff, userRrn);

//				//条码的处理。。
//				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
//					Lot lot = this.getMaterialLot(out.getOrgRrn(), material, userRrn);
//					LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
//					BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());
//					if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
//						throw new ClientException("inv.not_sufficient_quantity");
//					}
//					this.updateLotStorage(out.getOrgRrn(), lot.getObjectRrn(), warehouseRrn, line.getQtyMovement().negate(), userRrn);
//				} 
//				else {
//					//检查Lot的数量与Line中的数量是否相等
//					String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
//					List<MovementLineLot> movementLots = adManager.getEntityList(out.getOrgRrn(), MovementLineLot.class, Integer.MAX_VALUE, whereClause, "");
//					BigDecimal qtyLine = line.getQtyMovement();
//					BigDecimal qtyTotal = BigDecimal.ZERO;
//					for (MovementLineLot movementLot : movementLots) {
//						qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
//					}
//					if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
//						throw new ClientException("inv.out_lot_qty_different");
//					}
//					
//					String newPosistion = Lot.POSITION_OUT;//对于serial类型 的出库后position变为OUT,对于batch类型的不改变position
//					
//					if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
//						//对物料进行检查,判断是否在仓库中
//						StringBuffer sql = new StringBuffer("");
//						sql.append(" SELECT Lot FROM Lot Lot, MovementLineLot LineLot ");
//						sql.append(" WHERE Lot.objectRrn = LineLot.lotRrn ");
//						sql.append(" AND LineLot.movementLineRrn = ? "); 
//						sql.append(" AND (Lot.warehouseRrn != ? ");
//						sql.append(" OR Lot.position != '");
//						sql.append(Lot.POSITION_INSTOCK);
//						sql.append("')");
//						Query query = em.createQuery(sql.toString());
//						query.setParameter(1, line.getObjectRrn());
//						query.setParameter(2, house.getObjectRrn());
//						query.setMaxResults(1);
//						List<Lot> lots = query.getResultList();
//						if (lots.size() > 0) {
//							throw new ClientParameterException("inv.lot_not_in_warehouse", lots.get(0).getLotId(), house.getWarehouseId());
//						}
//					} 
//					else {
//						//对于Batch类型物料,检查数量是否足够
//						for (MovementLineLot movementLot : movementLots) {
//							Lot lot = em.find(Lot.class, movementLot.getLotRrn());
//							LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
//							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
//							if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
//								throw new ClientException("inv.not_sufficient_quantity");
//							}
//							
//							newPosistion = lot.getPosition();//Batch类型不改变批次position
//						}
//					}
//					
//					//更新批次库存(重点测试)
//					StringBuffer sql = new StringBuffer("");
//					sql.append(" UPDATE INV_LOT_STORAGE S ");
//					sql.append(" SET (S.UPDATED_BY, S.QTY_ONHAND) = ");
//					sql.append(" (SELECT 1, S.QTY_ONHAND - M.QTY_MOVEMENT FROM INV_MOVEMENT_LINE_LOT M WHERE M.LOT_RRN = S.LOT_RRN  ");
//					sql.append(" AND M.MOVEMENT_LINE_RRN = ?) ");
//					sql.append(" WHERE S.WAREHOUSE_RRN = ? ");
//					sql.append(" AND S.LOT_RRN = (SELECT K.LOT_RRN FROM INV_MOVEMENT_LINE_LOT K WHERE K.MOVEMENT_LINE_RRN = ? AND K.LOT_RRN = S.LOT_RRN) ");
//					Query query = em.createNativeQuery(sql.toString());
//					query.setParameter(1, line.getObjectRrn());
//					query.setParameter(2, warehouseRrn);
//					query.setParameter(3, line.getObjectRrn());
//					query.executeUpdate();
//										
//					//更新批次信息
//					sql = new StringBuffer("");
//					sql.append(" UPDATE WIP_LOT SET DATE_OUT = SYSDATE, POSITION = ? ");
//					sql.append(" WHERE OUT_LINE_RRN = ? ");
//					query = em.createNativeQuery(sql.toString());
//					query.setParameter(1, newPosistion);
//					query.setParameter(2, line.getObjectRrn());
//					query.executeUpdate();
//										
//					//保存批次历史		
//					sql = new StringBuffer("");
//					sql.append(" INSERT INTO WIPHIS_LOT(");
//					sql.append(" TRANS_TYPE,OBJECT_RRN,LOT_RRN,LOT_ID,HISTORY_SEQ,ACTION_CODE,ACTION_COMMENT,ACTION_REASON,");
//					sql.append(" COM_CLASS,CREATE_TIME,CREATED,CREATED_BY,CUSTOMER_LOT_ID,CUSTOMER_NAME,CUSTOMER_ORDER,CUSTOMER_PART_ID,");
//					sql.append(" DATE_IN,DATE_OUT,DATE_PRODUCT,DESCRIPTION,DUE_DATE,END_MAIN_QTY,END_SUB_QTY,END_TIME,ENGINEER,");
//					sql.append(" EQUIPMENT_ID,EQUIPMENT_RRN,IN_ID,IN_LINE_RRN,IN_RRN,IQC_ID,IQC_LINE_RRN,IQC_RRN,IS_ACTIVE,IS_USED,");
//					sql.append(" LOCATION,LOCATOR_ID,LOCATOR_RRN,LOCK_VERSION,LOT_COMMENT,LOT_TYPE,MAIN_QTY,MATERIAL_ID,MATERIAL_NAME,");
//					sql.append(" MATERIAL_RRN,MO_ID,MO_LINE_RRN,MO_RRN,MOLD_ID,OPERATOR_NAME,OPERATOR_RRN,ORG_RRN,OUT_ID,OUT_LINE_RRN,OUT_RRN,");
//					sql.append(" PARENT_LOT_RRN,PARENT_UNIT_RRN,PART_NAME,PART_RRN,PART_TYPE,PART_VERSION,PLAN_START_DATE,PO_ID,PO_LINE_RRN,");
//					sql.append(" PO_RRN,POSITION,PRE_COM_CLASS,PRE_STATE,PRE_STATE_ENTRY_TIME,PRE_SUB_STATE,PRE_TRANS_TYPE,PRIORITY,");
//					sql.append(" PROCEDURE_NAME,PROCEDURE_RRN,PROCEDURE_VERSION,QTY_CURRENT,QTY_INITIAL,QTY_TRANSACTION,QTY_USED,");
//					sql.append(" RECEIPT_ID,RECEIPT_RRN,REQUITED_DATE,REVERSE_FIELD1,REVERSE_FIELD10,REVERSE_FIELD2,REVERSE_FIELD3,REVERSE_FIELD4,");
//					sql.append(" REVERSE_FIELD5,REVERSE_FIELD6,REVERSE_FIELD7,REVERSE_FIELD8,REVERSE_FIELD9,START_MAIN_QTY,START_SUB_QTY,");
//					sql.append(" START_TIME,STATE,STATE_ENTRY_TIME,STEP_NAME,STEP_RRN,STEP_VERSION,SUB_QTY,SUB_STATE,SUB_UNIT_TYPE,");
//					sql.append(" TRACK_IN_TIME,TRACK_OUT_TIME,UPDATED,UPDATED_BY,USED_LOT_RRN,USER_QC,WAREHOUSE_ID,WAREHOUSE_RRN,WORKCENTER_ID,WORKCENTER_RRN ");
//
//					sql.append(" ) SELECT 'SOU',OBJECT_RRN.NEXTVAL,L.OBJECT_RRN,L.LOT_ID," + transSeq + ",NULL,NULL,NULL,");
//					sql.append(" L.COM_CLASS,SYSDATE,NULL,NULL,L.CUSTOMER_LOT_ID,L.CUSTOMER_NAME,L.CUSTOMER_ORDER,L.CUSTOMER_PART_ID,");
//					sql.append(" L.DATE_IN,L.DATE_OUT,L.DATE_PRODUCT,L.DESCRIPTION,L.DUE_DATE,L.END_MAIN_QTY,L.END_SUB_QTY,L.END_TIME,L.ENGINEER,");
//					sql.append(" L.EQUIPMENT_ID,L.EQUIPMENT_RRN,L.IN_ID,L.IN_LINE_RRN,L.IN_RRN,L.IQC_ID,L.IQC_LINE_RRN,L.IQC_RRN,L.IS_ACTIVE,L.IS_USED,");
//					sql.append(" L.LOCATION,L.LOCATOR_ID,L.LOCATOR_RRN,L.LOCK_VERSION,L.LOT_COMMENT,L.LOT_TYPE,L.MAIN_QTY,L.MATERIAL_ID,L.MATERIAL_NAME,");
//					sql.append(" L.MATERIAL_RRN,L.MO_ID,L.MO_LINE_RRN,L.MO_RRN,L.MOLD_ID,L.OPERATOR_NAME,L.OPERATOR_RRN,L.ORG_RRN,L.OUT_ID,L.OUT_LINE_RRN,L.OUT_RRN,");
//					sql.append(" L.PARENT_LOT_RRN,L.PARENT_UNIT_RRN,L.PART_NAME,L.PART_RRN,L.PART_TYPE,L.PART_VERSION,L.PLAN_START_DATE,L.PO_ID,L.PO_LINE_RRN,");
//					sql.append(" L.PO_RRN,L.POSITION,L.PRE_COM_CLASS,L.PRE_STATE,L.PRE_STATE_ENTRY_TIME,L.PRE_SUB_STATE,L.PRE_TRANS_TYPE,L.PRIORITY,");
//					sql.append(" L.PROCEDURE_NAME,L.PROCEDURE_RRN,L.PROCEDURE_VERSION,L.QTY_CURRENT,L.QTY_INITIAL,L.QTY_WAITINGIN,NULL,");
//					sql.append(" L.RECEIPT_ID,L.RECEIPT_RRN,L.REQUITED_DATE,L.REVERSE_FIELD1,L.REVERSE_FIELD10,L.REVERSE_FIELD2,L.REVERSE_FIELD3,L.REVERSE_FIELD4,");
//					sql.append(" L.REVERSE_FIELD5,L.REVERSE_FIELD6,L.REVERSE_FIELD7,L.REVERSE_FIELD8,L.REVERSE_FIELD9,L.START_MAIN_QTY,L.START_SUB_QTY,");
//					sql.append(" L.START_TIME,L.STATE,L.STATE_ENTRY_TIME,L.STEP_NAME,L.STEP_RRN,L.STEP_VERSION,L.SUB_QTY,L.SUB_STATE,L.SUB_UNIT_TYPE,");
//					sql.append(" L.TRACK_IN_TIME,L.TRACK_OUT_TIME,NULL,L.UPDATED_BY,L.USED_LOT_RRN,L.USER_QC,L.WAREHOUSE_ID,L.WAREHOUSE_RRN,L.WORKCENTER_ID,L.WORKCENTER_RRN ");
//					sql.append(" FROM WIP_LOT L ");
//					sql.append(" WHERE L.OUT_LINE_RRN = ? ");
//					query = em.createNativeQuery(sql.toString());
//					query.setParameter(1, line.getObjectRrn());
//					query.executeUpdate();
//				}				
				String whereClause = " materialId = '" + line.getMaterialId() + "'";
				List<SalesOrderSum> soSums = adManager.getEntityList(out.getOrgRrn(), SalesOrderSum.class, Integer.MAX_VALUE, whereClause, "");
				if (soSums.size() > 0) {
					SalesOrderSum soSum = soSums.get(0);
					soSum.setQtySo(soSum.getQtySo().subtract(line.getQtyMovement()));
					em.merge(soSum);
				}
			}
			
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String approveDate = df.format(out.getDateApproved());
				//设置crm销货单中的出库单号
				salManager.approveSo(out.getSoId(), out.getObjectRrn(), out.getDocId(), approveDate);
			} 
			catch (Exception e) {
				throw new ClientException(e);
			}
			return out;
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} 
		catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	

	/**
	 * 专用于奔泰销售出库单的审核
	 * 罗小华需求，批次审核减少批次数量，
	 * 审核专门针对批次的处理（因为审核无需对批次进行处理，只有在保存时候才会对批次进行处理）
	 */
	private MovementOut approveSalesMovementOutLotBT(MovementOut out, long userRrn, boolean isWriteOff) throws ClientException {
		try{
			if (out.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Long warehouseRrn = out.getWarehouseRrn();
			//得到审核人
			ADUser user = em.find(ADUser.class, userRrn);
			//得到仓库
			Warehouse house = em.find(Warehouse.class, warehouseRrn);
			
			if (out.getMovementLines().size() == 0) {
				throw new ClientException("inv.out_quantity_zero"); 
			}
			
			long transSeq = basManager.getHisSequence();
			for (MovementLine line : out.getMovementLines()) {
				//得到出库单行的物料信息
				Material material = em.find(Material.class, line.getMaterialRrn());

				//条码的处理。。
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					Lot lot = this.getMaterialLot(out.getOrgRrn(), material, userRrn);
					LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
					BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());
					if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
						throw new ClientException("inv.not_sufficient_quantity");
					}
					this.updateLotStorage(out.getOrgRrn(), lot.getObjectRrn(), warehouseRrn, line.getQtyMovement().negate(), userRrn);
				} 
				else {
					//检查Lot的数量与Line中的数量是否相等
					String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
					List<MovementLineLot> movementLots = adManager.getEntityList(out.getOrgRrn(), MovementLineLot.class, Integer.MAX_VALUE, whereClause, "");
					BigDecimal qtyLine = line.getQtyMovement();
					BigDecimal qtyTotal = BigDecimal.ZERO;
					for (MovementLineLot movementLot : movementLots) {
						qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
					}
					if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
						throw new ClientException("inv.out_lot_qty_different");
					}
					
					String newPosistion = Lot.POSITION_OUT;//对于serial类型 的出库后position变为OUT,对于batch类型的不改变position
					
					if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {//仓库位置不做判断
						//对物料进行检查,判断是否在仓库中
//						StringBuffer sql = new StringBuffer("");
//						sql.append(" SELECT Lot FROM Lot Lot, MovementLineLot LineLot ");
//						sql.append(" WHERE Lot.objectRrn = LineLot.lotRrn ");
//						sql.append(" AND LineLot.movementLineRrn = ? "); 
//						sql.append(" AND (Lot.warehouseRrn != ? ");
//						sql.append(" OR Lot.position != '");
//						sql.append(Lot.POSITION_INSTOCK);
//						sql.append("')");
//						Query query = em.createQuery(sql.toString());
//						query.setParameter(1, line.getObjectRrn());
//						query.setParameter(2, house.getObjectRrn());
//						query.setMaxResults(1);
//						List<Lot> lots = query.getResultList();
//						if (lots.size() > 0) {
//							throw new ClientParameterException("inv.lot_not_in_warehouse", lots.get(0).getLotId(), house.getWarehouseId());
//						}
					} 
					else {
						//对于Batch类型物料,检查数量是否足够
						for (MovementLineLot movementLot : movementLots) {
							Lot lot = em.find(Lot.class, movementLot.getLotRrn());
							LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
							
							newPosistion = lot.getPosition();//Batch类型不改变批次position
						}
					}
					
					//更新批次库存(重点测试)
					StringBuffer sql = new StringBuffer("");
					sql.append(" UPDATE INV_LOT_STORAGE S ");
					sql.append(" SET (S.UPDATED_BY, S.QTY_ONHAND) = ");
					sql.append(" (SELECT 1, S.QTY_ONHAND - M.QTY_MOVEMENT FROM INV_MOVEMENT_LINE_LOT M WHERE M.LOT_RRN = S.LOT_RRN  ");
					sql.append(" AND M.MOVEMENT_LINE_RRN = ?) ");
					sql.append(" WHERE S.WAREHOUSE_RRN = ? ");
					sql.append(" AND S.LOT_RRN = (SELECT K.LOT_RRN FROM INV_MOVEMENT_LINE_LOT K WHERE K.MOVEMENT_LINE_RRN = ? AND K.LOT_RRN = S.LOT_RRN) ");
					Query query = em.createNativeQuery(sql.toString());
					query.setParameter(1, line.getObjectRrn());
					query.setParameter(2, warehouseRrn);
					query.setParameter(3, line.getObjectRrn());
					query.executeUpdate();
										
					//更新批次信息
					sql = new StringBuffer("");
					sql.append(" UPDATE WIP_LOT SET DATE_OUT = SYSDATE, POSITION = ? ");
					sql.append(" WHERE OUT_LINE_RRN = ? ");
					query = em.createNativeQuery(sql.toString());
					query.setParameter(1, newPosistion);
					query.setParameter(2, line.getObjectRrn());
					query.executeUpdate();
										
					//保存批次历史		
					sql = new StringBuffer("");
					sql.append(" INSERT INTO WIPHIS_LOT(");
					sql.append(" TRANS_TYPE,OBJECT_RRN,LOT_RRN,LOT_ID,HISTORY_SEQ,ACTION_CODE,ACTION_COMMENT,ACTION_REASON,");
					sql.append(" COM_CLASS,CREATE_TIME,CREATED,CREATED_BY,CUSTOMER_LOT_ID,CUSTOMER_NAME,CUSTOMER_ORDER,CUSTOMER_PART_ID,");
					sql.append(" DATE_IN,DATE_OUT,DATE_PRODUCT,DESCRIPTION,DUE_DATE,END_MAIN_QTY,END_SUB_QTY,END_TIME,ENGINEER,");
					sql.append(" EQUIPMENT_ID,EQUIPMENT_RRN,IN_ID,IN_LINE_RRN,IN_RRN,IQC_ID,IQC_LINE_RRN,IQC_RRN,IS_ACTIVE,IS_USED,");
					sql.append(" LOCATION,LOCATOR_ID,LOCATOR_RRN,LOCK_VERSION,LOT_COMMENT,LOT_TYPE,MAIN_QTY,MATERIAL_ID,MATERIAL_NAME,");
					sql.append(" MATERIAL_RRN,MO_ID,MO_LINE_RRN,MO_RRN,MOLD_ID,OPERATOR_NAME,OPERATOR_RRN,ORG_RRN,OUT_ID,OUT_LINE_RRN,OUT_RRN,");
					sql.append(" PARENT_LOT_RRN,PARENT_UNIT_RRN,PART_NAME,PART_RRN,PART_TYPE,PART_VERSION,PLAN_START_DATE,PO_ID,PO_LINE_RRN,");
					sql.append(" PO_RRN,POSITION,PRE_COM_CLASS,PRE_STATE,PRE_STATE_ENTRY_TIME,PRE_SUB_STATE,PRE_TRANS_TYPE,PRIORITY,");
					sql.append(" PROCEDURE_NAME,PROCEDURE_RRN,PROCEDURE_VERSION,QTY_CURRENT,QTY_INITIAL,QTY_TRANSACTION,QTY_USED,");
					sql.append(" RECEIPT_ID,RECEIPT_RRN,REQUITED_DATE,REVERSE_FIELD1,REVERSE_FIELD10,REVERSE_FIELD2,REVERSE_FIELD3,REVERSE_FIELD4,");
					sql.append(" REVERSE_FIELD5,REVERSE_FIELD6,REVERSE_FIELD7,REVERSE_FIELD8,REVERSE_FIELD9,START_MAIN_QTY,START_SUB_QTY,");
					sql.append(" START_TIME,STATE,STATE_ENTRY_TIME,STEP_NAME,STEP_RRN,STEP_VERSION,SUB_QTY,SUB_STATE,SUB_UNIT_TYPE,");
					sql.append(" TRACK_IN_TIME,TRACK_OUT_TIME,UPDATED,UPDATED_BY,USED_LOT_RRN,USER_QC,WAREHOUSE_ID,WAREHOUSE_RRN,WORKCENTER_ID,WORKCENTER_RRN ");

					sql.append(" ) SELECT 'SOU',OBJECT_RRN.NEXTVAL,L.OBJECT_RRN,L.LOT_ID," + transSeq + ",NULL,NULL,NULL,");
					sql.append(" L.COM_CLASS,SYSDATE,NULL,NULL,L.CUSTOMER_LOT_ID,L.CUSTOMER_NAME,L.CUSTOMER_ORDER,L.CUSTOMER_PART_ID,");
					sql.append(" L.DATE_IN,L.DATE_OUT,L.DATE_PRODUCT,L.DESCRIPTION,L.DUE_DATE,L.END_MAIN_QTY,L.END_SUB_QTY,L.END_TIME,L.ENGINEER,");
					sql.append(" L.EQUIPMENT_ID,L.EQUIPMENT_RRN,L.IN_ID,L.IN_LINE_RRN,L.IN_RRN,L.IQC_ID,L.IQC_LINE_RRN,L.IQC_RRN,L.IS_ACTIVE,L.IS_USED,");
					sql.append(" L.LOCATION,L.LOCATOR_ID,L.LOCATOR_RRN,L.LOCK_VERSION,L.LOT_COMMENT,L.LOT_TYPE,L.MAIN_QTY,L.MATERIAL_ID,L.MATERIAL_NAME,");
					sql.append(" L.MATERIAL_RRN,L.MO_ID,L.MO_LINE_RRN,L.MO_RRN,L.MOLD_ID,L.OPERATOR_NAME,L.OPERATOR_RRN,L.ORG_RRN,L.OUT_ID,L.OUT_LINE_RRN,L.OUT_RRN,");
					sql.append(" L.PARENT_LOT_RRN,L.PARENT_UNIT_RRN,L.PART_NAME,L.PART_RRN,L.PART_TYPE,L.PART_VERSION,L.PLAN_START_DATE,L.PO_ID,L.PO_LINE_RRN,");
					sql.append(" L.PO_RRN,L.POSITION,L.PRE_COM_CLASS,L.PRE_STATE,L.PRE_STATE_ENTRY_TIME,L.PRE_SUB_STATE,L.PRE_TRANS_TYPE,L.PRIORITY,");
					sql.append(" L.PROCEDURE_NAME,L.PROCEDURE_RRN,L.PROCEDURE_VERSION,L.QTY_CURRENT,L.QTY_INITIAL,L.QTY_WAITINGIN,NULL,");
					sql.append(" L.RECEIPT_ID,L.RECEIPT_RRN,L.REQUITED_DATE,L.REVERSE_FIELD1,L.REVERSE_FIELD10,L.REVERSE_FIELD2,L.REVERSE_FIELD3,L.REVERSE_FIELD4,");
					sql.append(" L.REVERSE_FIELD5,L.REVERSE_FIELD6,L.REVERSE_FIELD7,L.REVERSE_FIELD8,L.REVERSE_FIELD9,L.START_MAIN_QTY,L.START_SUB_QTY,");
					sql.append(" L.START_TIME,L.STATE,L.STATE_ENTRY_TIME,L.STEP_NAME,L.STEP_RRN,L.STEP_VERSION,L.SUB_QTY,L.SUB_STATE,L.SUB_UNIT_TYPE,");
					sql.append(" L.TRACK_IN_TIME,L.TRACK_OUT_TIME,NULL,L.UPDATED_BY,L.USED_LOT_RRN,L.USER_QC,L.WAREHOUSE_ID,L.WAREHOUSE_RRN,L.WORKCENTER_ID,L.WORKCENTER_RRN ");
					sql.append(" FROM WIP_LOT L ");
					sql.append(" WHERE L.OUT_LINE_RRN = ? ");
					query = em.createNativeQuery(sql.toString());
					query.setParameter(1, line.getObjectRrn());
					query.executeUpdate();
				}				
			}
			return out;
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} 
		catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/**
	 * 奔泰罗小华需求
	 * 功能:销售出库可以先审核后挂批次，因此挂批次保存时调用 先调用保存的方式同时还需调用批次审核功能的
	 * 
	 * */
	
	public MovementOut saveMovementOutLineBT(MovementOut out, List<MovementLine> lines, MovementOut.OutType outType, long userRrn) throws ClientException {
		try{
			//如果是新建的单子，那么先保存出库单到movement_out表中
			if (out.getObjectRrn() == null) {
				out.setIsActive(true);
				out.setCreatedBy(userRrn);
				out.setCreated(new Date());
				out.setDateCreated(new Date());
				out.setTotalLines(0L);
				switch (outType) {
					case SOU:
						out.setDocType(MovementOut.DOCTYPE_SOU);
						break;
					case OOU:
						out.setDocType(MovementOut.DOCTYPE_OOU);
						break;
					case AOU://财务调整
						out.setDocType(MovementOut.DOCTYPE_AOU);
						break;
					case ADOU://营运调整出库
						out.setDocType(MovementOut.DOCTYPE_ADOU);
						break;
				}
				out.setDocStatus(MovementOut.STATUS_DRAFTED);
				String docId = out.getDocId();
				if (docId == null || docId.length() == 0) {
					out.setDocId(generateOutCode(out));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<Movement> movements = adManager.getEntityList(out.getOrgRrn(), Movement.class, 1, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}

				ADUser user = em.find(ADUser.class, userRrn);
				out.setUserCreated(user.getUserName());
				em.persist(out);
			}
			if (out.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}

			Warehouse house = em.find(Warehouse.class, out.getWarehouseRrn());
			out.setWarehouseId(house.getWarehouseId());
			
			List<MovementLine> savaLine = new ArrayList<MovementLine>();
			for (MovementLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getQtyMovement().doubleValue() == 0) {
					continue;
				}
				if (line.getUnitPrice() != null) {
					if(MovementOut.OutType.AOU.equals(outType) && (MovementOut.OUT_TYPE_IN_ADJUST.equals(out.getOutType()) || MovementOut.OUT_TYPE_RD_ADJUST.equals(out.getOutType()))){
						line.setLineTotal(line.getQtyMovement().negate().multiply(line.getUnitPrice()));
					}
					else{
						line.setLineTotal(line.getQtyMovement().multiply(line.getUnitPrice()));
					}
				}
				else{
					line.setLineTotal(null);
				}
				
				//如果是Update，则将原记录删除
				if (line.getObjectRrn() != null) {
					MovementLine oldLine = new MovementLine();
					oldLine.setObjectRrn(line.getObjectRrn());
					oldLine.setMovementRrn(line.getMovementRrn());
					
					em.merge(out);
					deleteMovementOutLine(oldLine, false, userRrn);
					em.flush();
					out = (MovementOut)em.find(Movement.class, out.getObjectRrn());
					out.setMovementLines(null);
					
					line.setObjectRrn(null);
				}
				
				if (line.getObjectRrn() != null) {	
				} 
				else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(MovementOut.STATUS_DRAFTED);
					line.setMovementRrn(out.getObjectRrn());
					line.setMovementId(out.getDocId());
					out.setTotalLines(out.getTotalLines() + 1);
					em.persist(line);
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
					for (MovementLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} 
						else {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				else {
					List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
					for (MovementLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						
//						if (lot.getIsUsed()) {奔泰取消判断isused判断
//							throw new ClientParameterException("inv.lot_already_used", lot.getLotId());
//						}
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
//							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {奔泰取消仓库判断
//								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
//							}
							
							if(MovementOut.DOCTYPE_AOU.equals(out.getDocType()) && (MovementOut.OUT_TYPE_OUT_ADJUST.equals(out.getOutType()) || MovementOut.OUT_TYPE_SALE_ADJUST.equals(out.getOutType()))){//调整出库中除采购调整外 要求批次已经出库
								if (!Lot.POSITION_OUT.equals(lot.getPosition())) {
									throw new ClientParameterException("inv.lot_not_out", lot.getLotId());
								}
							}
//							奔泰取消判断position
//							else if (!Lot.POSITION_INSTOCK.equals(lot.getPosition())) {
//								//调整出库中的采购调整和一般的出库 这两种情况要求批次在仓库中
//								throw new ClientParameterException("inv.lot_not_in", lot.getLotId());
//							} 
						} 
						else {
							//调整的不需要检查库存
							if(!(MovementOut.DOCTYPE_AOU.equals(out.getDocType()) || MovementOut.DOCTYPE_ADOU.equals(out.getDocType()))){
								LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), out.getWarehouseRrn(), userRrn);
								BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
	//							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
	//								throw new ClientException("inv.not_sufficient_quantity");
	//							}
								if (qtyCurrent.compareTo(lotStorage.getQtyOnhand()) < 0 && qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {//如果出库后库存减少 且是负数则不允许出库
									throw new ClientException("inv.not_sufficient_quantity");
								}
							}
						}

						lot.setOutId(out.getDocId());
						lot.setOutRrn(out.getObjectRrn());
						lot.setOutLineRrn(line.getObjectRrn());
						
						em.merge(lot);
						if(movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} 
						else {
							movementLot.setMovementRrn(out.getObjectRrn());
							movementLot.setMovementId(out.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}	
				}
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			out.setUpdatedBy(userRrn);
			out.setBtLotAlarm(false);
			em.merge(out);
			out.setMovementLines(savaLine);
			em.flush();
			approveSalesMovementOutLotBT(out, userRrn, true);//审核
			return out;

		} 
		catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		}
		catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/**
	 * 和普通采购入库不同的地方
	 * 1，只负责采购入库
	 * 2.前台设置普通发票因此后台 暂估需除以1、17 
	 * 3.保留2位有效数字
	 * 4.备件库全部是material类型
	 * */
	public MovementIn bjApproveMovementIn2(MovementIn in, MovementIn.InType inType, boolean writeOffFlag, boolean seniorApprove, long userRrn, boolean isWriteOff) throws ClientException {
		try{
			Date now = new Date(); 
			if (in.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Long warehouseRrn = in.getWarehouseRrn();
			ADUser user = em.find(ADUser.class, userRrn);
			in.setUserApproved(user.getUserName());
			Warehouse house = em.find(Warehouse.class, warehouseRrn);
			in.setWarehouseId(house.getWarehouseId());
			if (writeOffFlag) {
				in.setDateWriteOff(now);
				in.setDocStatus(PurchaseOrder.STATUS_COMPLETED);
			} else {
				in.setDocStatus(PurchaseOrder.STATUS_APPROVED);
			}
			in.setUpdatedBy(userRrn);
			in.setDateApproved(now);
			
			em.merge(in);
			in = em.getReference(in.getClass(), in.getObjectRrn());
			
			if (in.getMovementLines().size() == 0) {
				throw new ClientException("inv.in_quantity_zero"); 
			}
			
			Date dateIn = new Date();
			long transSeq = basManager.getHisSequence();
			
			List<MovementLine> inLines = new ArrayList<MovementLine>();
			BigDecimal accessLineTotal=new BigDecimal(0);
			BigDecimal invoiceLineTotal=new BigDecimal(0);
			for (MovementLine inLine : in.getMovementLines()) {
				Material material = em.find(Material.class, inLine.getMaterialRrn());
				if(inLine.getPoLineRrn()!=null){
					PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, inLine.getPoLineRrn());
					PurchaseOrder po = em.find(PurchaseOrder.class, poLine.getPoRrn());
					//查找供应商
					StringBuffer vmSql = new StringBuffer();
					vmSql.append(" SELECT VendorMaterial FROM VendorMaterial as VendorMaterial ");
					vmSql.append(" WHERE vendorRrn=? ");
					vmSql.append(" AND materialRrn=? ");
					VendorMaterial vendorMaterial = null;
					Query query = em.createQuery(vmSql.toString());
					query.setParameter(1, po.getVendorRrn());
					query.setParameter(2, poLine.getMaterialRrn());
					List<VendorMaterial> vendorMaterials = (List<VendorMaterial>)query.getResultList();
					if (vendorMaterials.size() > 0) {
						vendorMaterial = vendorMaterials.get(0);
						if (vendorMaterial.getLowestPrice() == null || vendorMaterial.getLowestPrice().compareTo(inLine.getUnitPrice()) > 0) {
							vendorMaterial.setLowestPrice(inLine.getUnitPrice());
						}
						vendorMaterial.setLastPrice(inLine.getUnitPrice());
						vendorMaterial.setReferencedPrice(inLine.getUnitPrice());
						em.merge(vendorMaterial);
						material.setLastPrice(poLine.getUnitPrice());//物料设置最近采购价
						em.merge(material);
					}
				}
				
				Locator locator = null;
				if (inLine.getLocatorRrn() != null) {
					locator = em.find(Locator.class, inLine.getLocatorRrn());
					inLine.setLocatorId(locator.getLocatorId());
				}
				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(inLine.getLineStatus())) {
					inLine.setLineStatus(DocumentationLine.LINESTATUS_APPROVED);
					inLine.setUpdatedBy(userRrn);
					em.merge(inLine);
				}
				
				if (MovementIn.InType.PIN == inType) {
					if (inLine.getPoLineRrn() != null) {
						PurchaseOrderLine poLine = em.find(PurchaseOrderLine.class, inLine.getPoLineRrn());
						PurchaseOrder po = em.find(PurchaseOrder.class, poLine.getPoRrn());
						poLine.setQtyIn((poLine.getQtyIn() == null ? BigDecimal.ZERO : poLine.getQtyIn()).add(inLine.getQtyMovement()));
						if (poLine.getQtyIn().compareTo(poLine.getQty()) > 0) {
							throw new ClientException("inv.in_larger_than_order");
						} 
						poLine.setUpdatedBy(userRrn);
						em.merge(poLine);
						if (poLine.getQtyIn().compareTo(poLine.getQty()) == 0) {
							poLine.setLineStatus(PurchaseOrder.STATUS_COMPLETED);
							em.merge(poLine);
							if (PurchaseOrder.STATUS_COMPLETED.equals(poLine.getLineStatus())) {
								boolean completeFlag = true;
								boolean closeFlag = false;
								for (PurchaseOrderLine line : po.getPoLines()){
									if (!PurchaseOrder.STATUS_COMPLETED.equals(line.getLineStatus()) && 
											!PurchaseOrder.STATUS_CLOSED.equals(line.getLineStatus())) {
										completeFlag = false;
										closeFlag = false;
										break;
									}
									if (PurchaseOrder.STATUS_CLOSED.equals(line.getLineStatus())) {
										closeFlag = true;
									}
								}
								if (closeFlag) {
									po.setDocStatus(PurchaseOrder.STATUS_CLOSED);
									em.merge(po);
								} else if (completeFlag) {
									po.setDocStatus(PurchaseOrder.STATUS_COMPLETED);
									em.merge(po);
								}
							}
						} 
						
						//更新暂估金额保留2位有效数字
						if (PurchaseOrder.INVOICE_TYPE_VAT.equals(po.getInvoiceType())) {
							BigDecimal vatRate = BigDecimal.ZERO;
							if (po.getVatRate() != null) {
								vatRate = po.getVatRate();
							}
							inLine.setVatRate(po.getVatRate());
							inLine.setAssessLineTotal(inLine.getQtyMovement().multiply(poLine.getUnitPrice())
									.divide(vatRate.add(BigDecimal.ONE), 2, RoundingMode.HALF_UP/*向最接近数字方向舍入的舍入模式，如果与两个相邻数字的距离相等，则向上舍入*/));
							accessLineTotal = accessLineTotal.add(inLine.getAssessLineTotal()==null?BigDecimal.ZERO:inLine.getAssessLineTotal());
							invoiceLineTotal = invoiceLineTotal.add(inLine.getInvoiceLineTotal()==null?BigDecimal.ZERO:inLine.getInvoiceLineTotal());
						} else {
							inLine.setVatRate(null);
							BigDecimal linePrice = inLine.getQtyMovement().multiply(poLine.getUnitPrice());
							BigDecimal assTotal = linePrice.divide(new BigDecimal("1.17"),2,RoundingMode.HALF_UP);
							inLine.setAssessLineTotal(assTotal);
						}
						em.merge(inLine);
					}
				}
				
				//更新库存
				updateStorage(in.getOrgRrn(), inLine.getMaterialRrn(), warehouseRrn, inLine.getQtyMovement(), isWriteOff, userRrn);
				
				if (MovementIn.IN_TYPE_AMOUNT_ADJUST.equals(in.getInType())
					&& inLine.getQtyMovement().doubleValue() == 0) {
				} else {
					if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
						Lot lot = this.getMaterialLot(in.getOrgRrn(), material, userRrn);
						//商品对应批次在默认仓库的库存增加,
						this.updateLotStorage(in.getOrgRrn(), lot.getObjectRrn(), warehouseRrn, inLine.getQtyMovement(), userRrn);
					}
				}
				
				inLines.add(inLine);
			}
			in.setAccessLineTotal(accessLineTotal);
			in.setMovementLines(inLines);
			em.merge(in);
			in = em.getReference(in.getClass(), in.getObjectRrn());
			return in;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//客供物料需求紧报
	public List<Material> queryMaterialQtysKeGong(Long orgRrn, Long materialRrn, String catalog2, String whereClause) throws ClientException {
try {
	StringBuffer sql = new StringBuffer();
	sql.append("SELECT DISTINCT PM.OBJECT_RRN AS MATERIAL_RRN,");
	sql.append("                PM.Org_Rrn AS ORG_RRN,");
	sql.append("                PM.MATERIAL_ID AS MATERIAL_ID,");
	sql.append("                PM.NAME AS NAME,");
	sql.append("                PM.reference_Doc5 AS reference_Doc5,");
	sql.append("                IVS.QTY_ONHAND AS QTY_ONHAND,");
	sql.append("                C.NEEDQTY AS QTY_ALLOCATION,");
	sql.append("                IVS.QTY_ONHAND - C.NEEDQTY AS CANNEED,");
	sql.append("                CASE WHEN WV.ONWAY IS NULL THEN 0 ELSE WV.ONWAY END AS ALLONWAY,");
	sql.append("                CPP.PURCHASER, CPP.PROMISED,");
	sql.append("                PM.QTY_MIN");
	sql.append("  FROM PDM_MATERIAL PM");
	sql.append(" INNER JOIN (SELECT MATERIAL_RRN, SUM(NVL(QTY_ONHAND,0)+NVL(QTY_DIFF,0)) AS QTY_ONHAND");
	sql.append("               FROM INV_STORAGE");
	sql.append("              WHERE ORG_RRN = ?1");
	sql.append("                AND WAREHOUSE_RRN IN (151046, 151043)");
	sql.append("              GROUP BY MATERIAL_RRN) IVS");
	sql.append("    ON PM.OBJECT_RRN = IVS.MATERIAL_RRN");
	sql.append("   AND PM.ORG_RRN = ?2");
	sql.append(" LEFT JOIN (SELECT WB.MATERIAL_RRN,NVL(SUM(WB.SQTY),  ");
	sql.append("                0) AS NEEDQTY            ");
	sql.append("       FROM (SELECT C.MO_RRN, B.MATERIAL_RRN, SUM(C.QTY * B.QTY_UNIT) SQTY  ");
	sql.append("               FROM WIP_MO_BOM B  ");
	sql.append("              INNER JOIN (SELECT M.MO_RRN, M.MATERIAL_RRN, M.PATH, T.QTY  ");
	sql.append("                           FROM WIP_MO_BOM M  ");
	sql.append("                          INNER JOIN (SELECT T.OBJECT_RRN,  ");
	sql.append("                                            (T.QTY - T.QTY_RECEIVE) QTY  ");
	sql.append("                                       FROM WIP_MO_LINE T  ");
	sql.append("                                      WHERE T.LINE_STATUS IN  ");
	sql.append("                                            ('DRAFTED', 'APPROVED')) T  ");
	sql.append("                             ON (M.MO_LINE_RRN = T.OBJECT_RRN OR M.Mo_Rrn=T.Object_Rrn)  ");
	sql.append("                            AND T.QTY <> 0  ");
	sql.append("                           ) C  ");
	sql.append("                 ON B.MO_RRN = C.MO_RRN  ");
	sql.append("                AND B.MATERIAL_PARENT_RRN = C.MATERIAL_RRN  ");
	sql.append("                AND B.PATH = C.PATH || C.MATERIAL_RRN || '/'  ");
	sql.append("              GROUP BY C.MO_RRN, B.MATERIAL_RRN) WB  ");
	sql.append("      INNER JOIN PDM_MATERIAL PM  ");
	sql.append("         ON WB.MATERIAL_RRN = PM.OBJECT_RRN  ");
	sql.append("      GROUP BY WB.MATERIAL_RRN ) C ");			
	sql.append("    ON PM.OBJECT_RRN = C.MATERIAL_RRN");
	sql.append("  LEFT JOIN (SELECT PC.MATERIAL_RRN AS MATERIAL_RRN,");
	sql.append("                    QTY,");
	sql.append("                    (QTY - CASE");
	sql.append("                      WHEN QM IS NULL THEN");
	sql.append("                       0");
	sql.append("                      ELSE");
	sql.append("                       QM");
	sql.append("                    END) AS ONWAY");
	sql.append("               FROM (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
	sql.append("                            SUM(CASE WHEN PP.DOC_STATUS = 'CLOSED' THEN PPL.QTY_IN ELSE PPL.QTY END) AS QTY");
	sql.append("                       FROM PUR_PO PP");
	sql.append("                      INNER JOIN PUR_PO_LINE PPL");
	sql.append("                         ON PP.OBJECT_RRN = PPL.PO_RRN");
	sql.append("                        AND PP.ORG_RRN = ?5");
	sql.append("                      GROUP BY PPL.MATERIAL_RRN) PC");
	sql.append("               LEFT JOIN (SELECT MATERIAL_RRN, SUM(QTY_MOVEMENT) AS QM");
	sql.append("                           FROM INV_MOVEMENT IM");
	sql.append("                          INNER JOIN INV_MOVEMENT_LINE IML");
	sql.append("                             ON IM.DOC_ID = IML.MOVEMENT_ID");
	sql.append("                            AND IM.DOC_STATUS IN ('APPROVED', 'COMPLETED')");
	sql.append("                          WHERE IM.ORG_RRN = ?6");
	sql.append("                            AND DOC_TYPE = 'PIN'");
	sql.append("                          GROUP BY MATERIAL_RRN) CC");
	sql.append("                 ON PC.MATERIAL_RRN = CC.MATERIAL_RRN) WV");
	sql.append("    ON PM.OBJECT_RRN = WV.MATERIAL_RRN");
	sql.append("  LEFT JOIN (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
	sql.append("                    PP.PURCHASER     AS PURCHASER,");
	sql.append("  					PPL.DATE_PROMISED AS PROMISED");
	sql.append("               FROM PUR_PO PP");
	sql.append("              INNER JOIN PUR_PO_LINE PPL");
	sql.append("                 ON PP.DOC_ID = PPL.PO_ID");
	sql.append("                AND PP.ORG_RRN = ?7");
	sql.append("                AND PP.DOC_STATUS <> 'CLOSED'");
	sql.append("              INNER JOIN (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
	sql.append("                                MAX(PP.DATE_APPROVED) AS LAST_TIME");
	sql.append("                           FROM PUR_PO PP");
	sql.append("                          INNER JOIN PUR_PO_LINE PPL");
	sql.append("                             ON PP.DOC_ID = PPL.PO_ID");
	sql.append("                            AND PP.ORG_RRN = ?8");
	sql.append("                            AND PP.DOC_STATUS <> 'CLOSED'");
	sql.append("                          GROUP BY PPL.MATERIAL_RRN) C");
	sql.append("                 ON PPL.MATERIAL_RRN = C.MATERIAL_RRN");
	sql.append("                AND PP.DATE_APPROVED = C.LAST_TIME) CPP");
	sql.append("    ON PM.OBJECT_RRN = CPP.MATERIAL_RRN");
	sql.append(" WHERE PM.Material_Category2='客供' ");
//	if(whereClause == null || whereClause.trim().length() > 0){
//		sql.append("   AND IVS.QTY_ONHAND < C.NEEDQTY");
//	}else{
//		sql.append("   AND " + whereClause);
//	}


	
	if(materialRrn != null){
		sql.append(" AND IVS.MATERIAL_RRN = " + materialRrn);
	}
	//计算公式 已分配数+最小库存-安全库存-在途>0
	StringBuffer sql2 = new StringBuffer("");
	sql2.append("   SELECT t.material_rrn, ");
	sql2.append("          t.org_rrn, ");
	sql2.append("          t.material_id, ");
	sql2.append("          t.name, ");
	sql2.append("          t.reference_Doc5, ");
	sql2.append("          t.qty_onhand, ");
	sql2.append("          t.qty_allocation, ");
	sql2.append("          t.allonway, ");
	sql2.append("          wmsys.wm_concat (t.purchaser) purchaser, ");
	sql2.append("		   wmsys.wm_concat (to_char(t.promised,'yyyy-mm-dd')) promised");
	sql2.append(" FROM (");
	sql2.append(sql);
	sql2.append(") t");
	sql2.append(" where NVL(t.qty_allocation,0)+ NVL(t.qty_min,0)-NVL(t.qty_onhand,0)-NVL(t.allonway,0)>0 ");
	sql2.append(" GROUP BY t.material_rrn, ");
	sql2.append("          t.org_rrn, ");
	sql2.append("          t.material_id, ");
	sql2.append("          t.name, ");
	sql2.append("          t.reference_Doc5, ");
	sql2.append("          t.qty_onhand, ");
	sql2.append("          t.qty_allocation, ");
	sql2.append("          t.allonway ");
	
	//处理最新到货日期
	StringBuffer sql3 = new StringBuffer();
	sql3.append(" select min(line.date_promised) date_promised,line.material_rrn from pur_po_line line WHERE  ORG_RRN = ?9 ");
	sql3.append(" and line_status in ('DRAFTED','APPROVED') AND line.Qty-(case when line.QTY_IN is null then 0 else line.QTY_IN end)>0 ");
	sql3.append(" group by line.material_rrn");

	StringBuffer sql4 = new StringBuffer();
	sql4.append("   SELECT sql2.material_rrn, ");
	sql4.append("      	   sql2.org_rrn, ");
	sql4.append("          sql2.material_id, ");
	sql4.append("          sql2.name, ");
	sql4.append("          sql2.qty_onhand, ");
	sql4.append("          sql2.qty_allocation, ");
	sql4.append("          sql2.allonway, ");
	sql4.append("          sql2.purchaser, ");
	sql4.append("	to_char(sql3.date_promised,'yyyy-mm-dd') promised,");
	sql4.append("          sql2.reference_Doc5 ");
	sql4.append(" FROM (");
	sql4.append(sql2.toString());
	sql4.append(" ) sql2");
	sql4.append(" left join (");
	sql4.append(sql3);
	sql4.append(" ) sql3");
	sql4.append(" on ");
	sql4.append(" sql2.material_rrn = sql3.material_rrn ");
	
	
	Query query = em.createNativeQuery(sql4.toString());
	
	query.setParameter(1, orgRrn);
	query.setParameter(2, orgRrn);
//	query.setParameter(3, orgRrn);
//	query.setParameter(4, orgRrn);
	query.setParameter(5, orgRrn);
	query.setParameter(6, orgRrn);
	query.setParameter(7, orgRrn);
	query.setParameter(8, orgRrn);
	query.setParameter(9, orgRrn);
	
	List<Object[]> objs = query.getResultList();
	List<Material> materials = new ArrayList<Material>();
	
	for(Object[] obj : objs){
		Material m = new Material();
		m.setObjectRrn(((BigDecimal) obj[0]).longValue());
		m.setOrgRrn(((BigDecimal) obj[1]).longValue());
		m.setMaterialId((String)obj[2]);
		m.setName((String)obj[3]);
		m.setQtyOnHand((BigDecimal) obj[4]);
		m.setQtyAllocation((BigDecimal) obj[5]);
		m.setQtyTransit((BigDecimal) obj[6]);
		m.setPlannerId((String)obj[7]);//借用此字段保存采购员信息
		m.setPromised((String)obj[8]);//最新到货日期
		m.setReferenceDoc5((String)obj[9]);//借用此字段保存备注信息
		materials.add(m);
	}

	return materials;
} catch (Exception e) {
	throw new ClientException(e);
}
}
	
	
	
	//--------------------车间仓库-----------------线边仓-------------------------

	//新增车间单据行
	public MovementWorkShopLine newMovementWorkShopLine(MovementWorkShop movementWS) throws ClientException {
		MovementWorkShopLine movementWSLine = new MovementWorkShopLine();
		try{
			if (movementWS != null && movementWS.getObjectRrn() != null) {
				movementWS = em.find(MovementWorkShop.class, movementWS.getObjectRrn());
				long maxLineNo = 1;
				for (MovementWorkShopLine line : movementWS.getMovementWorkShopLines()) {
					maxLineNo = maxLineNo < line.getLineNo() ? line.getLineNo() : maxLineNo;
				}
				movementWSLine.setLineNo((long)Math.ceil(maxLineNo / 10) * 10 + 10);
			} else {
				movementWSLine.setLineNo(10L);
			}
			movementWSLine.setLineStatus(Movement.STATUS_DRAFTED);
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return movementWSLine;
	}
	//生成docID
	private String generateMovementWorkShopCode(MovementWorkShop ws) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(ws.getOrgRrn(), ws.getDocType()));
		moCode.append(basManager.generateCodeSuffix(ws.getOrgRrn(), ws.getDocType(), ws.getCreated()));
		return moCode.toString();
	}
	
	//车间领料，制造库存为 lotStorage ,
	public MovementWorkShopLine saveMovementWorkShopRequestionLine(MovementWorkShopRequestion wsRequestion, MovementWorkShopLine line, long userRrn) throws ClientException {
		List<MovementWorkShopLine> list = new ArrayList<MovementWorkShopLine>();
		list.add(line);
		wsRequestion = saveMovementWorkShopRequestionLine(wsRequestion, list, userRrn);
		return wsRequestion.getMovementWorkShopLines().get(0);
	}	
	
	public MovementWorkShopRequestion saveMovementWorkShopRequestionLine(MovementWorkShopRequestion wsRequestion, List<MovementWorkShopLine> lines, long userRrn) throws ClientException {
		try{
			if (wsRequestion.getObjectRrn() == null) {
				wsRequestion.setIsActive(true);
				wsRequestion.setCreatedBy(userRrn);
				wsRequestion.setCreated(new Date());
				wsRequestion.setTotalLines(0L);
				wsRequestion.setDocStatus(MovementTransfer.STATUS_DRAFTED);
				String docId = wsRequestion.getDocId();
				if (docId == null || docId.length() == 0) {
					wsRequestion.setDocId(generateMovementWorkShopCode(wsRequestion));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<MovementWorkShopRequestion> movements = adManager.getEntityList(wsRequestion.getOrgRrn(), MovementWorkShopRequestion.class, 2, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				em.persist(wsRequestion);
			} 
			if (wsRequestion.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (wsRequestion.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			if (wsRequestion.getWarehouseRrn().equals(wsRequestion.getTargetWarehouseRrn())) {
				throw new ClientException("inv.warehouse_target_can_not_equal");
			}
			Warehouse house = em.find(Warehouse.class, wsRequestion.getWarehouseRrn());
			wsRequestion.setWarehouseId(house.getWarehouseId());
			Warehouse targetHouse = em.find(Warehouse.class, wsRequestion.getTargetWarehouseRrn());
			wsRequestion.setTargetWarehouseId(targetHouse.getWarehouseId());
			
			List<MovementWorkShopLine> savaLine = new ArrayList<MovementWorkShopLine>();
			for (MovementWorkShopLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getObjectRrn() != null) {
					
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(wsRequestion.getObjectRrn());
					line.setMovementId(wsRequestion.getDocId());
					wsRequestion.setTotalLines(wsRequestion.getTotalLines() + 1);
					em.persist(line);
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(wsRequestion.getObjectRrn());
							movementLot.setMovementId(wsRequestion.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(wsRequestion.getObjectRrn());
							movementLot.setMovementId(wsRequestion.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				} else {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
						} else {
							LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), wsRequestion.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(wsRequestion.getObjectRrn());
							movementLot.setMovementId(wsRequestion.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(wsRequestion.getObjectRrn());
							movementLot.setMovementId(wsRequestion.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			wsRequestion.setUpdatedBy(userRrn);
			wsRequestion.setMovementWorkShopLines(null);
			em.merge(wsRequestion);
			wsRequestion.setMovementWorkShopLines(savaLine);
			
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return wsRequestion;
	}
	
	public MovementWorkShopRequestion approveMovementWorkShopRequestion(MovementWorkShopRequestion wsRequestion, long userRrn) throws ClientException {
		try{
			if (wsRequestion.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (wsRequestion.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			Long targetWarehouseRrn = wsRequestion.getTargetWarehouseRrn();
			
			ADUser user = em.find(ADUser.class, userRrn);
			wsRequestion.setUserApproved(user.getUserName());
			Warehouse house = em.find(Warehouse.class, wsRequestion.getWarehouseRrn());
			Warehouse targetHouse = em.find(Warehouse.class, targetWarehouseRrn);
			wsRequestion.setTargetWarehouseId(targetHouse.getWarehouseId());
			wsRequestion.setDocStatus(MovementTransfer.STATUS_APPROVED);
			wsRequestion.setDateApproved(new Date());
			wsRequestion.setUpdatedBy(userRrn);
			em.merge(wsRequestion);
			
			if (wsRequestion.getMovementWorkShopLines().size() == 0) {
				throw new ClientException("inv.transfer_quantity_zero"); 
			}
			
			long transSeq = basManager.getHisSequence();
			for (MovementWorkShopLine line : wsRequestion.getMovementWorkShopLines()) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				
				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(Movement.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				//更新车间物料仓库库存
				updateWorkShopStorage(wsRequestion.getOrgRrn(), line.getMaterialRrn(), wsRequestion.getTargetWarehouseRrn(), line.getQtyMovement(), false, userRrn);
				//更新车间物料批次仓库库存
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					Lot lot = this.getMaterialLot(wsRequestion.getOrgRrn(), material, userRrn);
//					LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), wsRequestion.getWarehouseRrn(), userRrn);
//					BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());
//					if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
//						throw new ClientException("inv.not_sufficient_quantity");
//					}
					this.updateWorkShopLotStorage(wsRequestion.getOrgRrn(), lot.getObjectRrn(), wsRequestion.getTargetWarehouseRrn(), line.getQtyMovement(), userRrn);
				} else {
					String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
					List<MovementWorkShopLineLot> movementLots = adManager.getEntityList(wsRequestion.getOrgRrn(), MovementWorkShopLineLot.class, Integer.MAX_VALUE, whereClause, "");
					//检查Lot的数量与Line中的数量是否相等
					BigDecimal qtyLine = line.getQtyMovement();
					BigDecimal qtyTotal = BigDecimal.ZERO;
					for (MovementWorkShopLineLot movementLot : movementLots) {
						qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
					}
					if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
						throw new ClientException("inv.transfer_lot_qty_different");
					}
				
					for (MovementWorkShopLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
							}
						} else {
							LotStorage lotStorage = this.getLotStorage(wsRequestion.getOrgRrn(), movementLot.getLotRrn(), wsRequestion.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						
						this.updateWorkShopLotStorage(wsRequestion.getOrgRrn(), lot.getObjectRrn(), wsRequestion.getTargetWarehouseRrn(), movementLot.getQtyMovement(), userRrn);
					}
				}
			}
			return wsRequestion;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	//查询车间批次库存
	public WorkShopLotStorage getWorkShopLotStorage(long orgRrn, long lotRrn, long warehouseRrn, long userRrn)  throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT WorkShopLotStorage FROM WorkShopLotStorage as WorkShopLotStorage ");
		sql.append(" WHERE   orgRrn = ? AND lotRrn = ? AND warehouseRrn = ?  ");
		WorkShopLotStorage storage;
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, lotRrn);
			query.setParameter(3, warehouseRrn);
			List<WorkShopLotStorage> storages = query.getResultList();
			if (storages.size() == 0) {
				storage = new WorkShopLotStorage();
				storage.setOrgRrn(orgRrn);
				storage.setLotRrn(lotRrn);
				storage.setWarehouseRrn(warehouseRrn);
				storage.setIsActive(true);
				storage.setCreatedBy(userRrn);
				storage.setCreated(new Date());
				storage.setUpdatedBy(userRrn);
				em.persist(storage);
			} else {
				storage = storages.get(0);
			}
			return storage;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//仓库库存查询车间批次
	public List<Lot> getWorkShopLotStorage(long warehouseRrn, long materialRrn)  throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT W.LOT_ID,W.MATERIAL_ID,W.MATERIAL_NAME,T.QTY_ONHAND,W.DATE_PRODUCT, W.DATE_IN ");
		sql.append(" FROM WIP_LOT W,INV_WORKSHOP_LOT_STORAGE T");
		sql.append(" WHERE W.IS_ACTIVE='Y' ");
		sql.append(" AND T.LOT_RRN = W.OBJECT_RRN");
		sql.append(" AND W.MATERIAL_RRN = ?");
		sql.append(" AND T.WAREHOUSE_RRN = ?");		
		sql.append(" AND T.QTY_ONHAND <> 0");
		sql.append(" ORDER BY W.LOT_ID");

		try{
			List<Lot> lotStorages = new ArrayList<Lot>();
//			Query query = em.createQuery(sql.toString());
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, materialRrn);
			query.setParameter(2, warehouseRrn);
			List<Object[]> result = (List<Object[]>)query.getResultList();
			for (Object[] row : result) {
				Lot lot = new Lot();
				String lotId = (String)row[0];
				String materialId = (String)row[1];
				String materialName = (String)row[2];
				BigDecimal qtyCurrent = (BigDecimal)row[3];
				Date dateProduct = (row[4]==null?null:(Date) row[4]);
				Date dateIn = (row[5]==null?null:(Date) row[5]);
				lot.setLotId(lotId);
				lot.setMaterialId(materialId);
				lot.setMaterialName(materialName);
				lot.setQtyCurrent(qtyCurrent);
				lot.setDateIn(dateIn);
				lot.setDateProduct(dateProduct);
				lotStorages.add(lot);
			}
			return lotStorages;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//车间批次库存
	public void updateWorkShopLotStorage(long orgRrn, long lotRrn, long warehouseRrn, BigDecimal qty, long userRrn) throws ClientException {
		try{
			WorkShopLotStorage storage = getWorkShopLotStorage(orgRrn, lotRrn, warehouseRrn, userRrn);
			storage.setQtyOnhand(storage.getQtyOnhand().add(qty));
			storage.setUpdatedBy(userRrn);
			em.merge(storage);
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//保存车间配送单据行 
	public MovementWorkShopLine saveMovementWorkShopDeliveryLine(MovementWorkShopDelivery workShop, MovementWorkShopLine line, long userRrn) throws ClientException {
		List<MovementWorkShopLine> list = new ArrayList<MovementWorkShopLine>();
		list.add(line);
		workShop = saveMovementWorkShopDeliveryLine(workShop, list, userRrn);
		return workShop.getMovementWorkShopLines().get(0);
	}	
	
	//保存车间单据行
	public MovementWorkShopDelivery saveMovementWorkShopDeliveryLine(MovementWorkShopDelivery workShop, List<MovementWorkShopLine> lines, long userRrn) throws ClientException {
		try{
			if (workShop.getObjectRrn() == null) {
				workShop.setIsActive(true);
				workShop.setCreatedBy(userRrn);
				workShop.setCreated(new Date());
				workShop.setTotalLines(0L);
				workShop.setDocStatus(MovementWorkShop.STATUS_DRAFTED);
				String docId = workShop.getDocId();
				if (docId == null || docId.length() == 0) {
					workShop.setDocId(generateMovementWorkShopCode(workShop));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<MovementWorkShopDelivery> movements = adManager.getEntityList(workShop.getOrgRrn(), MovementWorkShopDelivery.class, 2, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				em.persist(workShop);
			} 
			if (workShop.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (workShop.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			if (workShop.getWarehouseRrn().equals(workShop.getTargetWarehouseRrn())) {
				throw new ClientException("inv.warehouse_target_can_not_equal");
			}
			Warehouse house = em.find(Warehouse.class, workShop.getWarehouseRrn());
			workShop.setWarehouseId(house.getWarehouseId());
			Warehouse targetHouse = em.find(Warehouse.class, workShop.getTargetWarehouseRrn());
			workShop.setTargetWarehouseId(targetHouse.getWarehouseId());
			
			List<MovementWorkShopLine> savaLine = new ArrayList<MovementWorkShopLine>();
			for (MovementWorkShopLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getObjectRrn() != null) {
					
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(workShop.getObjectRrn());
					line.setMovementId(workShop.getDocId());
					workShop.setTotalLines(workShop.getTotalLines() + 1);
					em.persist(line);
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				} else {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
						} else {
							WorkShopLotStorage lotStorage = this.getWorkShopLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), workShop.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			workShop.setUpdatedBy(userRrn);
			workShop.setMovementWorkShopLines(null);
			em.merge(workShop);
			workShop.setMovementWorkShopLines(savaLine);
			
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return workShop;
	}
	
	
	//审核车间配送单 跟 车间领料不一样 这个是需要 源车间减少 目标车间增加
	public MovementWorkShopDelivery approveMovementWorkShopDelivery(MovementWorkShopDelivery workShop, long userRrn) throws ClientException {
		try{
			if (workShop.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (workShop.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			Long targetWarehouseRrn = workShop.getTargetWarehouseRrn();
			
			ADUser user = em.find(ADUser.class, userRrn);
			workShop.setUserApproved(user.getUserName());
			Warehouse house = em.find(Warehouse.class, workShop.getWarehouseRrn());
			Warehouse targetHouse = em.find(Warehouse.class, targetWarehouseRrn);
			workShop.setDocStatus(MovementTransfer.STATUS_APPROVED);
			workShop.setDateApproved(new Date());
			workShop.setUpdatedBy(userRrn);
			em.merge(workShop);
			
			if (workShop.getMovementWorkShopLines().size() == 0) {
				throw new ClientException("inv.transfer_quantity_zero"); 
			}
			
			long transSeq = basManager.getHisSequence();
			for (MovementWorkShopLine line : workShop.getMovementWorkShopLines()) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				
				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(Movement.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				//更新车间物料仓库库存
				updateWorkShopStorage(workShop.getOrgRrn(), line.getMaterialRrn(), workShop.getWarehouseRrn(), line.getQtyMovement().negate(), false, userRrn);
				updateWorkShopStorage(workShop.getOrgRrn(), line.getMaterialRrn(), workShop.getTargetWarehouseRrn(), line.getQtyMovement(), false, userRrn);
				//更新车间物料批次仓库库存
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					Lot lot = this.getMaterialLot(workShop.getOrgRrn(), material, userRrn);
					//批次库存
					WorkShopLotStorage lotStorage = this.getWorkShopLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), workShop.getWarehouseRrn(), userRrn);
					BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());
					if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
						throw new ClientException("inv.not_sufficient_quantity");
					}
					
					this.updateWorkShopLotStorage(workShop.getOrgRrn(), lot.getObjectRrn(), workShop.getWarehouseRrn(), line.getQtyMovement().negate(), userRrn);
					this.updateWorkShopLotStorage(workShop.getOrgRrn(), lot.getObjectRrn(), workShop.getTargetWarehouseRrn(), line.getQtyMovement(), userRrn);
				} else {
					String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
					List<MovementWorkShopLineLot> movementLots = adManager.getEntityList(workShop.getOrgRrn(), MovementWorkShopLineLot.class, Integer.MAX_VALUE, whereClause, "");
					//检查Lot的数量与Line中的数量是否相等
					BigDecimal qtyLine = line.getQtyMovement();
					BigDecimal qtyTotal = BigDecimal.ZERO;
					for (MovementWorkShopLineLot movementLot : movementLots) {
						qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
					}
					if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
						throw new ClientException("inv.transfer_lot_qty_different");
					}
				
					for (MovementWorkShopLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
							}
						} else {
							//批次库存
							WorkShopLotStorage lotStorage = this.getWorkShopLotStorage(workShop.getOrgRrn(), movementLot.getLotRrn(), workShop.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						
						this.updateWorkShopLotStorage(workShop.getOrgRrn(), lot.getObjectRrn(), workShop.getWarehouseRrn(), movementLot.getQtyMovement().negate(), userRrn);
						this.updateWorkShopLotStorage(workShop.getOrgRrn(), lot.getObjectRrn(), workShop.getTargetWarehouseRrn(), movementLot.getQtyMovement(), userRrn);
					}
				}
			}
			return workShop;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	//保存行的批次
	public MovementWorkShop saveMovementWorkShopLineLot(MovementWorkShop workShop, List<MovementWorkShopLine> lines, long userRrn) throws ClientException {
		try{
			for (MovementWorkShopLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				} else {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
						} else {
							//workShopLotStorage
							WorkShopLotStorage lotStorage = this.getWorkShopLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), workShop.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				line.setUpdatedBy(userRrn);
				em.merge(line);
			}
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return workShop;
	}
	
	//删除车间单据
	public void deleteMovementWorkShop(MovementWorkShop workShop, long userRrn) throws ClientException {
		try{
			if(workShop != null && workShop.getObjectRrn() != null) {
				workShop = em.find(MovementWorkShop.class, workShop.getObjectRrn());
				for (int i=0; i< workShop.getMovementWorkShopLines().size(); i++){
					MovementWorkShopLine line= workShop.getMovementWorkShopLines().get(i);
					deleteMovementWorkShopLine(line, true, userRrn);
				}
				em.remove(workShop);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	//删除车间单据行
	public void deleteMovementWorkShopLine(MovementWorkShopLine movementLine, boolean allFlag, long userRrn) throws ClientException {
		try {
			if(movementLine != null && movementLine.getObjectRrn() != null) {
				movementLine = em.find(MovementWorkShopLine.class, movementLine.getObjectRrn());
				// 更新movement
				MovementWorkShop ws = em.find(MovementWorkShop.class, movementLine.getMovementRrn());		
				if (!allFlag) {
					ws.setTotalLines(ws.getTotalLines() - 1);
					em.merge(ws);
				}
				StringBuffer sql = new StringBuffer("");
				sql.append(" SELECT MovementWorkShopLineLot FROM MovementWorkShopLineLot MovementWorkShopLineLot ");
				sql.append(" WHERE  movementLineRrn = ? ");
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, movementLine.getObjectRrn());
				List<MovementWorkShopLineLot> movementLots = query.getResultList();
				for (MovementWorkShopLineLot movementLot : movementLots) {
					em.remove(movementLot);
				}
				em.remove(movementLine);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	//保存车间回料单据行
	public MovementWorkShopLine saveMovementWorkShopReclaim(MovementWorkShopReclaim workShop, MovementWorkShopLine line, long userRrn) throws ClientException {
		List<MovementWorkShopLine> list = new ArrayList<MovementWorkShopLine>();
		list.add(line);
		workShop = saveMovementWorkShopReclaimLine(workShop, list, userRrn);
		return workShop.getMovementWorkShopLines().get(0);
	}	
	
	//保存回料车间单据行
	public MovementWorkShopReclaim saveMovementWorkShopReclaimLine(MovementWorkShopReclaim workShop, List<MovementWorkShopLine> lines, long userRrn) throws ClientException {
		try{
			if (workShop.getObjectRrn() == null) {
				workShop.setIsActive(true);
				workShop.setCreatedBy(userRrn);
				workShop.setCreated(new Date());
				workShop.setTotalLines(0L);
				workShop.setDocStatus(MovementWorkShop.STATUS_DRAFTED);
				String docId = workShop.getDocId();
				if (docId == null || docId.length() == 0) {
					workShop.setDocId(generateMovementWorkShopCode(workShop));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<MovementWorkShopReclaim> movements = adManager.getEntityList(workShop.getOrgRrn(), MovementWorkShopReclaim.class, 2, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				em.persist(workShop);
			} 
			if (workShop.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Warehouse house = em.find(Warehouse.class, workShop.getWarehouseRrn());
			workShop.setWarehouseId(house.getWarehouseId());
			workShop.setTargetWarehouseRrn(house.getObjectRrn());
			workShop.setTargetWarehouseId(house.getWarehouseId());
			
			List<MovementWorkShopLine> savaLine = new ArrayList<MovementWorkShopLine>();
			for (MovementWorkShopLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				
				if (line.getObjectRrn() != null) {
					
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(workShop.getObjectRrn());
					line.setMovementId(workShop.getDocId());
					workShop.setTotalLines(workShop.getTotalLines() + 1);
					em.persist(line);
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				} else {
					throw new ClientParameterException("回料只允许materi类型","回料只允许materi类型的物料");
				}
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			workShop.setUpdatedBy(userRrn);
			workShop.setMovementWorkShopLines(null);
			em.merge(workShop);
			workShop.setMovementWorkShopLines(savaLine);
			
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return workShop;
	}
	
	
	//审核车间回料 回料库库存增加
	public MovementWorkShopReclaim approveMovementWorkShopReclaim(MovementWorkShopReclaim workShop, long userRrn) throws ClientException {
		try{
			if (workShop.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (workShop.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			ADUser user = em.find(ADUser.class, userRrn);
			workShop.setUserApproved(user.getUserName());
			workShop.setDocStatus(MovementTransfer.STATUS_APPROVED);
			workShop.setDateApproved(new Date());
			workShop.setUpdatedBy(userRrn);
			em.merge(workShop);
			
			if (workShop.getMovementWorkShopLines().size() == 0) {
				throw new ClientException("inv.transfer_quantity_zero"); 
			}
			
			for (MovementWorkShopLine line : workShop.getMovementWorkShopLines()) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				
				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(Movement.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				//更新回料仓库库存
				updateWorkShopStorage(workShop.getOrgRrn(), line.getMaterialRrn(), workShop.getWarehouseRrn(), line.getQtyMovement(), false, userRrn);
				//更新车间物料批次仓库库存
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					Lot lot = this.getMaterialLot(workShop.getOrgRrn(), material, userRrn);
					this.updateWorkShopLotStorage(workShop.getOrgRrn(), lot.getObjectRrn(), workShop.getWarehouseRrn(), line.getQtyMovement(), userRrn);
				} else { 
					throw new ClientParameterException("回料只允许materi类型","回料只允许materi类型的物料");
				}
			}
			return workShop;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//onHandFlag=true计算营运库存，writeOffFlag=true计算核销数量
	public void updateWorkShopStorage(long orgRrn, long materialRrn, long warehouseRrn, BigDecimal qty, boolean onHandFlag, boolean writeOffFlag, long userRrn) throws ClientException {
		try{
			WorkShopStorage wsstorage = getMaterialWorkShopStorage(orgRrn, materialRrn, warehouseRrn, userRrn);
			if(onHandFlag){
				wsstorage.setQtyOnhand(wsstorage.getQtyOnhand().add(qty));//修改营运库存
			}
			wsstorage.setUpdatedBy(userRrn);
			em.merge(wsstorage);
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} 
		catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//writeOffFlag=true核销数量增加，否则不变
	public void updateWorkShopStorage(long orgRrn, long materialRrn, long warehouseRrn, BigDecimal qty, boolean writeOffFlag, long userRrn) throws ClientException {
		updateWorkShopStorage(orgRrn, materialRrn, warehouseRrn, qty, true, writeOffFlag, userRrn);
	}
	
	public WorkShopStorage getMaterialWorkShopStorage(long orgRrn, long materialRrn, long warehouseRrn, long userRrn)  throws ClientException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT WorkShopStorage FROM WorkShopStorage as WorkShopStorage ");
		sql.append(" WHERE materialRrn = ? AND warehouseRrn = ? ");
		WorkShopStorage wsstorage;
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, materialRrn);
			query.setParameter(2, warehouseRrn);
			List<WorkShopStorage> storages = query.getResultList();
			if (storages.size() == 0) {
				wsstorage = new WorkShopStorage();
				wsstorage.setOrgRrn(orgRrn);
				wsstorage.setMaterialRrn(materialRrn);
				wsstorage.setWarehouseRrn(warehouseRrn);
				wsstorage.setIsActive(true);
				wsstorage.setCreatedBy(userRrn);
				wsstorage.setCreated(new Date());
				wsstorage.setUpdatedBy(userRrn);
				em.persist(wsstorage);
			} else {
				wsstorage = storages.get(0);
			}
			return wsstorage;
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	
	
	
	
	
	
	//工作令需求转为调拨单
	public MovementTransfer moNeedTransferYS(long orgRrn, ManufactureOrder mo,BigDecimal qty,long userRrn) throws ClientException {
		
		StringBuffer sql = new StringBuffer("   ");
		sql.append("  SELECT TREE.MB_MATERIAL_RRN,sum(MB_QTY_BOM*");
		sql.append(qty);
		sql.append(") ");
		sql.append("  From  ");
		sql.append("  (  ");
		sql.append("  SELECT  level,rs.*  ");
		sql.append("  FROM  ");
		sql.append("    (  ");
		sql.append("    SELECT  ");
		sql.append("      MB.QTY_BOM                  MB_QTY_BOM,  ");
		sql.append("      MB.PATH_LEVEL                MB_PATH_LEVEL,  ");
		sql.append("      MB.MATERIAL_RRN                MB_MATERIAL_RRN,  ");
		sql.append("      MB.MATERIAL_PARENT_RRN            MB_MATERIAL_PARENT_RRN,  ");
		sql.append("      MB.QTY_UNIT                  MB_QTY_UNIT  ");
		sql.append("    FROM    ");
		sql.append("      WIP_MO      MO,  ");
		sql.append("      WIP_MO_BOM    MB,  ");
		sql.append("      PDM_MATERIAL  M  ");
		sql.append("    WHERE  MO.OBJECT_RRN =  ");
		sql.append(mo.getObjectRrn());
		sql.append("      and MB.MO_RRN = MO.OBJECT_RRN  ");
		sql.append("      and MB.MATERIAL_PARENT_RRN not in (  ");
		sql.append("                        select MA.OBJECT_RRN from PDM_MATERIAL MA   ");
		sql.append("                        where MA.MATERIAL_ID in ('07010004','07010006','07010013',  ");
		sql.append("                        '07010014','07010042','07010044','07010045','07010053',  ");
		sql.append("                        '07010058','07010062')  ");
		sql.append("                        )  ");
		sql.append("      and not ( MB.PATH_LEVEL > 1 and MB.MATERIAL_PARENT_RRN in (  ");
		sql.append("                        select MAT.OBJECT_RRN from PDM_MATERIAL MAT   ");
		sql.append("                        where  MAT.MATERIAL_ID like '0750%'  ");
		sql.append("                          or MAT.MATERIAL_ID like '22018%'  ");
		sql.append("                          or MAT.MATERIAL_ID like '22050%'  ");
		sql.append("                          or MAT.MATERIAL_ID like '0215%'  ");
		sql.append("                        ))  ");
		sql.append("      and (MB.MATERIAL_RRN in ( select MAT1.OBJECT_RRN from PDM_MATERIAL MAT1   ");
		sql.append("                    where MAT1.MATERIAL_ID in ('17090016')) OR MB.PATH_LEVEL = 1 OR MB.MATERIAL_RRN not in (  ");
		sql.append("                    select MAT.OBJECT_RRN from PDM_MATERIAL MAT   ");
		sql.append("                    where MAT.MATERIAL_ID like '1709%'                          ");
		sql.append("                    ))  ");
		sql.append("      and  M.OBJECT_RRN = MB.MATERIAL_RRN  ");
		sql.append("    ) rs  ");
		sql.append("  START WITH rs.MB_path_level = 1  ");
		sql.append("  CONNECT BY PRIOR rs.MB_material_rrn = rs.MB_material_parent_rrn) TREE  ");
		sql.append("  Where TREE.MB_PATH_LEVEL > 0 and (TREE.MB_PATH_LEVEL <=2 OR (TREE.MB_MATERIAL_PARENT_RRN IN (select MAT.OBJECT_RRN from PDM_MATERIAL MAT   ");
		sql.append("                        where  MAT.MATERIAL_ID = '01090001') AND TREE.MB_PATH_LEVEL<=3))  ");
		sql.append("   and MB_MATERIAL_RRN not in (  ");
		sql.append("   SELECT  ");
		sql.append("      distinct MB.MATERIAL_PARENT_RRN   ");
		sql.append("    FROM    ");
		sql.append("      WIP_MO      MO,  ");
		sql.append("      WIP_MO_BOM    MB,  ");
		sql.append("      PDM_MATERIAL  M  ");
		sql.append("    WHERE  MO.OBJECT_RRN =   ");
		sql.append(mo.getObjectRrn());
		sql.append("      and MB.MO_RRN = MO.OBJECT_RRN  ");
		sql.append("      and MB.MATERIAL_PARENT_RRN not in (  ");
		sql.append("                        select MA.OBJECT_RRN from PDM_MATERIAL MA   ");
		sql.append("                        where MA.MATERIAL_ID in ('07010004','07010006','07010013',  ");
		sql.append("                        '07010014','07010042','07010044','07010045','07010053',  ");
		sql.append("                        '07010058','07010062')  ");
		sql.append("                        )  ");
		sql.append("      and not ( MB.PATH_LEVEL > 1 and MB.MATERIAL_PARENT_RRN in (  ");
		sql.append("                        select MAT.OBJECT_RRN from PDM_MATERIAL MAT   ");
		sql.append("                        where  MAT.MATERIAL_ID like '0750%'  ");
		sql.append("                          or MAT.MATERIAL_ID like '22018%'  ");
		sql.append("                          or MAT.MATERIAL_ID like '22050%'  ");
		sql.append("                          or MAT.MATERIAL_ID like '0215%'  ");
		sql.append("                        ))  ");
		sql.append("      and (MB.MATERIAL_RRN in ( select MAT1.OBJECT_RRN from PDM_MATERIAL MAT1   ");
		sql.append("                    where MAT1.MATERIAL_ID in ('17090016')) OR MB.PATH_LEVEL = 1 OR MB.MATERIAL_RRN not in (  ");
		sql.append("                    select MAT.OBJECT_RRN from PDM_MATERIAL MAT   ");
		sql.append("                    where MAT.MATERIAL_ID like '1709%'                          ");
		sql.append("                    )) ");
		sql.append("      and  M.OBJECT_RRN = MB.MATERIAL_RRN  ");
		sql.append("  )   ");
		sql.append("  group by MB_MATERIAL_RRN  ");
		
		MovementTransfer transfer = new MovementTransfer();
		transfer.setOrgRrn(orgRrn);
		transfer.setCreated(new Date());
		transfer.setCreatedBy(userRrn);
		transfer.setUpdated(new Date());
		transfer.setUpdatedBy(userRrn);
		Warehouse defaultWarehouse =getDefaultWarehouse(orgRrn);
		Warehouse writeOffWarehouse =getWriteOffWarehouse(orgRrn);
		transfer.setWarehouseRrn(defaultWarehouse.getObjectRrn());//饮水良品
		transfer.setTargetWarehouseRrn(writeOffWarehouse.getObjectRrn());//饮水制造良品
		transfer.setMoId(mo.getDocId());
		transfer.setMoRrn(mo.getObjectRrn());
		List<MovementLine> lines = new ArrayList<MovementLine>();
		
		Query querysql = em.createNativeQuery(sql.toString());
		List<Object[]> result = querysql.getResultList();
		int i = 1;
		for(Object[] row : result){
			long materialRrn = Long.parseLong(String.valueOf(row[0]));
			BigDecimal qtyUnit = (BigDecimal) row[1];
			Material material = em.find(Material.class, materialRrn);
			
			if("虚拟".equals(material.getMaterialCategory3())){
				continue;
			}
			MovementLine moLine = new MovementLine();
			moLine.setOrgRrn(orgRrn);
			moLine.setMaterialRrn(materialRrn);
			moLine.setQtyMovement(qtyUnit);
			moLine.setLineNo(i * 10L);

			if(!Lot.LOTTYPE_MATERIAL.equals(material.getLotType())){
				List<Lot> lots = getOptionalOutLotYS(material,qtyUnit,defaultWarehouse.getObjectRrn());
				List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
				if(lots != null || lots.size() > 0){
					for(Lot lot :lots){
						MovementLineLot lineLot = new MovementLineLot();
						lineLot.setOrgRrn(orgRrn);
						lineLot.setIsActive(true);
						lineLot.setCreated(new Date());
						lineLot.setLotRrn(lot.getObjectRrn());
						lineLot.setLotId(lot.getLotId());
						lineLot.setMaterialId(lot.getMaterialId());
						lineLot.setMaterialName(lot.getMaterialName());
						lineLot.setQtyMovement(lot.getQtyTransaction());
						lineLots.add(lineLot);
					}
				}
//				if(lots == null || lots.size() == 0){
//					throw new ClientException("inv.material_no_lot");
//				}
//				moLine.setLots(lots);
				moLine.setMovementLots(lineLots);
			}
			lines.add(moLine);
			i++;
		}
		return saveMovementTransferLine(transfer,lines,1L);
	}
	
	//恢复最原始的版本，可以从数据库自动选择批次
	public List<Lot> getOptionalOutLotYS(Material material,BigDecimal qty,long warehouseRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Lot, LotStorage FROM Lot as Lot, LotStorage as LotStorage ");
		sql.append(" WHERE Lot.objectRrn = LotStorage.lotRrn" );
		sql.append(" AND Lot.materialRrn = ? ");
		sql.append(" AND LotStorage.warehouseRrn = ? ");
		sql.append(" AND LotStorage.qtyOnhand > 0 ");//只去数据大于0的
		sql.append(" ORDER BY Lot.dateIn ASC ");
		
		logger.debug(sql);
		List<Lot> optionalLots = new ArrayList<Lot>();
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, material.getObjectRrn());
			query.setParameter(2, warehouseRrn);//饮水良品
			List list = query.getResultList();
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[])list.get(i);
				Lot lot = (Lot)obj[0];
				LotStorage lotStorage = (LotStorage)obj[1];
				lot.setQtyTransaction(lotStorage.getQtyOnhand());
				optionalLots.add(lot);
			}
			
			if (material.getIsLotControl()) {
				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
					if (qty.doubleValue() > optionalLots.size()) {
						throw new ClientException("inv.not_sufficient_quantity");
					} else {
						List<Lot> lots = new ArrayList<Lot>();
						for (int i = 0; i < qty.intValue(); i++) {
							lots.add(optionalLots.get(i));
						}
						return lots;
					}
				} else if(Lot.LOTTYPE_BATCH.equals(material.getLotType())) {
					BigDecimal qtyOut = BigDecimal.ZERO;
					List<Lot> lots = new ArrayList<Lot>();
					for (Lot optionalLot : optionalLots) {
						qtyOut = qtyOut.add(optionalLot.getQtyTransaction()); 
						if (qtyOut.compareTo(qty) >= 0) {
							optionalLot.setQtyTransaction(
									optionalLot.getQtyTransaction().subtract(qtyOut.subtract(qty)));
							lots.add(optionalLot);
							break;
						} else {
							lots.add(optionalLot);
						}
					}
					return lots;
				}else{
					return null;
				}
			} else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//华宇或者廊坊运算月存表
	public void runSpGetQtyAllocation(Date startDate, Date endDate,String monthDate, String operUser, String operDate)throws ClientException {
		try {
			Session session = (Session) em.getDelegate();
			Connection conn = session.connection();
			CallableStatement call = conn
					.prepareCall("{CALL sp_month_storage(?,?,?,?,?)}");
			java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
			java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
			call.setDate(1, sqlStartDate);
			call.setDate(2, sqlEndDate);
			call.setString(3, monthDate);
			call.setString(4, operDate);
			call.setString(5, operDate);
			// call.registerOutParameter(3, Types.NUMERIC);
			// call.registerOutParameter(4, Types.NUMERIC);
			// call.registerOutParameter(5, Types.NUMERIC);
			// call.registerOutParameter(6, Types.NUMERIC);

			call.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public MovementLine saveMovementTransferLineDpk(MovementTransfer transfer, MovementLine line, long userRrn) throws ClientException {
		List<MovementLine> list = new ArrayList<MovementLine>();
		list.add(line);
		transfer = saveMovementTransferLineDpk(transfer, list, userRrn);
		return transfer.getMovementLines().get(0);
	}	
	
	public MovementTransfer saveMovementTransferLineDpk(MovementTransfer transfer, List<MovementLine> lines, long userRrn) throws ClientException {
		try{
			if (transfer.getObjectRrn() == null) {
				transfer.setIsActive(true);
				transfer.setCreatedBy(userRrn);
				transfer.setCreated(new Date());
				transfer.setTotalLines(0L);
				transfer.setDocStatus(MovementTransfer.STATUS_DRAFTED);
				transfer.setDocType("DPKTRF");
				String docId = transfer.getDocId();
				if (docId == null || docId.length() == 0) {
					transfer.setDocId(generateTransferCode(transfer));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<Movement> movements = adManager.getEntityList(transfer.getOrgRrn(), Movement.class, 2, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}

				ADUser user = em.find(ADUser.class, userRrn);
				transfer.setUserCreated(user.getUserName());
				em.persist(transfer);
			} 
			if (transfer.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (transfer.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			if (transfer.getWarehouseRrn().equals(transfer.getTargetWarehouseRrn())) {
				throw new ClientException("inv.warehouse_target_can_not_equal");
			}
			Warehouse house = em.find(Warehouse.class, transfer.getWarehouseRrn());
			transfer.setWarehouseId(house.getWarehouseId());
			Warehouse targetHouse = em.find(Warehouse.class, transfer.getTargetWarehouseRrn());
			transfer.setTargetWarehouseId(targetHouse.getWarehouseId());
			
			List<MovementLine> savaLine = new ArrayList<MovementLine>();
			for (MovementLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				//根据denny和刘总要求,环保到制造保存 入库数可以不填写，但审核需要填写
				if(line.getOrgRrn() == 139420L){
					if ("环保-良品".equals(transfer.getWarehouseId())&& "制造车间良品".equals(transfer.getTargetWarehouseId())) {
						//不做比较
					}else{
						if (line.getQtyMovement().doubleValue() == 0) {
							continue;
						}
					}
				}else{
					if (line.getQtyMovement().doubleValue() == 0) {
						continue;
					}
				}
	
				//如果是Update，则将原记录删除
				if (line.getObjectRrn() != null) {
					MovementLine oldLine = new MovementLine();
					oldLine.setObjectRrn(line.getObjectRrn());
					oldLine.setMovementRrn(line.getMovementRrn());
					
					em.merge(transfer);
					deleteMovementTransferLine(oldLine, false, userRrn);
					em.flush();
					transfer = (MovementTransfer)em.find(Movement.class, transfer.getObjectRrn());
					transfer.setMovementLines(null);
					
					line.setObjectRrn(null);
				}
				
				if (line.getLocatorRrn() != null) {
					Locator locator = em.find(Locator.class, line.getLocatorRrn());
					line.setLocatorId(locator.getLocatorId());
				}
				
				if (line.getObjectRrn() != null) {
					
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(transfer.getObjectRrn());
					line.setMovementId(transfer.getDocId());
					transfer.setTotalLines(transfer.getTotalLines() + 1);
					em.persist(line);
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
					for (MovementLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				} else {
					List<MovementLineLot> movementLots = line.getMovementLots() == null ? new ArrayList<MovementLineLot>() : line.getMovementLots();
					for (MovementLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (!(Lot.POSITION_INSTOCK.equals(lot.getPosition()) || Lot.POSITION_GEN.equals(lot.getPosition()))) {
							throw new ClientParameterException("inv.lot_not_in", lot.getLotId());
						} 
						if (lot.getIsUsed()) {
							throw new ClientParameterException("inv.lot_already_used", lot.getLotId());
						}
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
							}
						} else {
							LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						//调拨完后将目标仓库设为批次的当前仓库(此处不需要，放到approveMovementTransfer中去了)						
//						lot.setWarehouseRrn(transfer.getTargetWarehouseRrn());
//						lot.setWarehouseId(transfer.getTargetWarehouseId());
						lot.setTransferLineRrn(line.getObjectRrn());
						em.merge(lot);

						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(transfer.getObjectRrn());
							movementLot.setMovementId(transfer.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			transfer.setUpdatedBy(userRrn);
			transfer.setMovementLines(null);
			em.merge(transfer);
			transfer.setMovementLines(savaLine);
			
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return transfer;
	}
	
	public MovementWorkShopUnqualified approveMovementTransferUnqualified(MovementWorkShopUnqualified   transfer, long userRrn) throws ClientException {
		try{
			if (transfer.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (transfer.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			Long targetWarehouseRrn = transfer.getTargetWarehouseRrn();
			
			ADUser user = em.find(ADUser.class, userRrn);
			transfer.setUserApproved(user.getUserName());
			Warehouse house = em.find(Warehouse.class, transfer.getWarehouseRrn());
			Warehouse targetHouse = em.find(Warehouse.class, targetWarehouseRrn);
			transfer.setTargetWarehouseId(targetHouse.getWarehouseId());
			transfer.setDocStatus(MovementTransfer.STATUS_APPROVED);
			transfer.setDateApproved(new Date());
			transfer.setUpdatedBy(userRrn);
			em.merge(transfer);
			
			if (transfer.getMovementWorkShopLines().size() == 0) {
				throw new ClientException("inv.transfer_quantity_zero"); 
			}
			if (house.getWarehouseId().equals(targetHouse.getWarehouseId())) {
				throw new ClientException("仓库不能一致"); 
			}
			
			String flag=null;
			if("车间虚拟库".equals(house.getWarehouseType()) && "车间不良品库".equals(targetHouse.getWarehouseId())){
				flag = "A";//A代表当前仓库为车间虚拟库，车间虚拟库-制造车间-不良品+
			}else if("车间虚拟库".equals(targetHouse.getWarehouseType()) && "车间不良品库".equals(house.getWarehouseId())){
				flag = "B";//B代表当前仓库为不良品仓库
			}
			long transSeq = basManager.getHisSequence();
			for (MovementWorkShopLine line : transfer.getMovementWorkShopLines()) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				
				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(Movement.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
				
				if("A".equals(flag)){
					//更新库存当前库存减少 目标仓库增加
					updateStorage(transfer.getOrgRrn(), line.getMaterialRrn(), 151046L, line.getQtyMovement().negate(), false, userRrn);
					updateWorkShopStorage(transfer.getOrgRrn(), line.getMaterialRrn(), transfer.getWarehouseRrn(), line.getQtyMovement().negate(), false, userRrn);
					updateWorkShopStorage(transfer.getOrgRrn(), line.getMaterialRrn(), transfer.getTargetWarehouseRrn(), line.getQtyMovement(), false, userRrn);

				}else if("B".equals(flag)){
					//更新库存当前库存减少 目标仓库增加
					updateWorkShopStorage(transfer.getOrgRrn(), line.getMaterialRrn(), transfer.getWarehouseRrn(), line.getQtyMovement().negate(), false, userRrn);
					updateStorage(transfer.getOrgRrn(), line.getMaterialRrn(), 151046L, line.getQtyMovement(), false, userRrn);
					updateWorkShopStorage(transfer.getOrgRrn(), line.getMaterialRrn(), transfer.getTargetWarehouseRrn(), line.getQtyMovement(), false, userRrn);
				}else{
					throw new ClientException("仓库选择错误，只能是车间不良品库与车间良品库"); 
				}
				

				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					Lot lot = this.getMaterialLot(transfer.getOrgRrn(), material, userRrn);
					if("A".equals(flag)){
						LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), 151046L, userRrn);
						BigDecimal qtyCurrentWT = lotStorage.getQtyOnhand().subtract(line.getQtyMovement());//制造
						WorkShopLotStorage wslotStorage = this.getWorkShopLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), userRrn);
						BigDecimal qtyCurrentWS = wslotStorage.getQtyOnhand().subtract(line.getQtyMovement());//车间虚拟库
						if (qtyCurrentWT.compareTo(BigDecimal.ZERO) < 0) {
							throw new ClientException("inv.not_sufficient_quantity");
						}
						if (qtyCurrentWS.compareTo(BigDecimal.ZERO) < 0) {
							throw new ClientException("inv.not_sufficient_quantity");
						}
					}else if("B".equals(flag)){
						WorkShopLotStorage whlotStorage = this.getWorkShopLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), userRrn);
						BigDecimal qtyCurrentWS = whlotStorage.getQtyOnhand().subtract(line.getQtyMovement());
						if (qtyCurrentWS.compareTo(BigDecimal.ZERO) < 0) {
							throw new ClientException("inv.not_sufficient_quantity");
						}
					}
					

					
					if("A".equals(flag)){
						this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), 151046L, line.getQtyMovement().negate(), userRrn);
						this.updateWorkShopLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), line.getQtyMovement().negate(), userRrn);
						this.updateWorkShopLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getTargetWarehouseRrn(), line.getQtyMovement(), userRrn);
					}else if("B".equals(flag)){
						this.updateWorkShopLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), line.getQtyMovement().negate(), userRrn);
						this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), 151046L, line.getQtyMovement(), userRrn);
						this.updateWorkShopLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getTargetWarehouseRrn(), line.getQtyMovement(), userRrn);
					}else{
						throw new ClientException("仓库选择错误，只能是车间不良品库与车间良品库");
					}
					

				} else {
					String whereClause = " movementLineRrn = '" + line.getObjectRrn() + "'";
					List<MovementWorkShopLineLot> movementLots = adManager.getEntityList(transfer.getOrgRrn(), MovementWorkShopLineLot.class, Integer.MAX_VALUE, whereClause, "");
					//检查Lot的数量与Line中的数量是否相等
					BigDecimal qtyLine = line.getQtyMovement();
					BigDecimal qtyTotal = BigDecimal.ZERO;
					for (MovementWorkShopLineLot movementLot : movementLots) {
						qtyTotal = qtyTotal.add(movementLot.getQtyMovement());
					}
					if (qtyLine.doubleValue() != qtyTotal.doubleValue()) {
						throw new ClientException("inv.transfer_lot_qty_different");
					}
				
					for (MovementWorkShopLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
							if (!lot.getWarehouseRrn().equals(house.getObjectRrn())) {
								throw new ClientParameterException("inv.lot_not_in_warehouse", lot.getLotId(), house.getWarehouseId());
							}
						} else {
							if("A".equals(flag)){
								LotStorage lotStorage = this.getLotStorage(transfer.getOrgRrn(), movementLot.getLotRrn(), 151046L, userRrn);
								BigDecimal qtyCurrentWT = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());//制造
								WorkShopLotStorage wslotStorage = this.getWorkShopLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), userRrn);
								BigDecimal qtyCurrentWS = wslotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());//车间虚拟库
								if (qtyCurrentWT.compareTo(BigDecimal.ZERO) < 0) {
									throw new ClientException("inv.not_sufficient_quantity");
								}
								if (qtyCurrentWS.compareTo(BigDecimal.ZERO) < 0) {
									throw new ClientException("inv.not_sufficient_quantity");
								}
							}else if("B".equals(flag)){
								WorkShopLotStorage whlotStorage = this.getWorkShopLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), userRrn);
								BigDecimal qtyCurrentWS = whlotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
								if (qtyCurrentWS.compareTo(BigDecimal.ZERO) < 0) {
									throw new ClientException("inv.not_sufficient_quantity");
								}
							}
						}
						
						if("A".equals(flag)){
							this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), 151046L, movementLot.getQtyMovement().negate(), userRrn);
							this.updateWorkShopLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), movementLot.getQtyMovement().negate(), userRrn);
							this.updateWorkShopLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getTargetWarehouseRrn(), movementLot.getQtyMovement(), userRrn);
						}else if("B".equals(flag)){
							this.updateWorkShopLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getWarehouseRrn(), movementLot.getQtyMovement().negate(), userRrn);
							this.updateLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), 151046L, movementLot.getQtyMovement(), userRrn);
							this.updateWorkShopLotStorage(transfer.getOrgRrn(), lot.getObjectRrn(), transfer.getTargetWarehouseRrn(), movementLot.getQtyMovement(), userRrn);
						}
						
//						lot.setWarehouseRrn(transfer.getTargetWarehouseRrn());
//						lot.setWarehouseId(transfer.getTargetWarehouseId());
//						em.merge(lot);
//	
//						LotHis his = new TransferLotHis(lot);
//						his.setHisSeq(transSeq);
//						em.persist(his);
					}
				}
			}
			return transfer;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//保存车间配送单据行 
	public MovementWorkShopLine saveMovementWorkShopUnqualifiedLine(MovementWorkShopUnqualified workShop, MovementWorkShopLine line, long userRrn) throws ClientException {
		List<MovementWorkShopLine> list = new ArrayList<MovementWorkShopLine>();
		list.add(line);
		workShop = saveMovementWorkShopUnqualifiedLine(workShop, list, userRrn);
		return workShop.getMovementWorkShopLines().get(0);
	}	
	
	//保存车间单据行
	public MovementWorkShopUnqualified saveMovementWorkShopUnqualifiedLine(MovementWorkShopUnqualified workShop, List<MovementWorkShopLine> lines, long userRrn) throws ClientException {
		try{
			if (workShop.getObjectRrn() == null) {
				workShop.setIsActive(true);
				workShop.setCreatedBy(userRrn);
				workShop.setCreated(new Date());
				workShop.setTotalLines(0L);
				workShop.setDocStatus(MovementWorkShop.STATUS_DRAFTED);
				String docId = workShop.getDocId();
				if (docId == null || docId.length() == 0) {
					workShop.setDocId(generateMovementWorkShopCode(workShop));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<MovementWorkShopDelivery> movements = adManager.getEntityList(workShop.getOrgRrn(), MovementWorkShopDelivery.class, 2, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				em.persist(workShop);
			} 
			if (workShop.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			if (workShop.getTargetWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_target_warehouse");
			}
			if (workShop.getWarehouseRrn().equals(workShop.getTargetWarehouseRrn())) {
				throw new ClientException("inv.warehouse_target_can_not_equal");
			}
			Warehouse house = em.find(Warehouse.class, workShop.getWarehouseRrn());
			workShop.setWarehouseId(house.getWarehouseId());
			Warehouse targetHouse = em.find(Warehouse.class, workShop.getTargetWarehouseRrn());
			workShop.setTargetWarehouseId(targetHouse.getWarehouseId());
			
			List<MovementWorkShopLine> savaLine = new ArrayList<MovementWorkShopLine>();
			for (MovementWorkShopLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getObjectRrn() != null) {
					
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(workShop.getObjectRrn());
					line.setMovementId(workShop.getDocId());
					workShop.setTotalLines(workShop.getTotalLines() + 1);
					em.persist(line);
				}
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				} else {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
						} else {
							WorkShopLotStorage lotStorage = this.getWorkShopLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), workShop.getWarehouseRrn(), userRrn);
							BigDecimal qtyCurrent = lotStorage.getQtyOnhand().subtract(movementLot.getQtyMovement());
							if (qtyCurrent.compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(workShop.getObjectRrn());
							movementLot.setMovementId(workShop.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			workShop.setUpdatedBy(userRrn);
			workShop.setMovementWorkShopLines(null);
			em.merge(workShop);
			workShop.setMovementWorkShopLines(savaLine);
			
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return workShop;
	}
	
	public List<VStorageMaterial> getWmsStorage(String materialId,String warehouseId) throws ClientException {
		List<VStorageMaterial> wmsStorages = new ArrayList<VStorageMaterial>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			StringBuffer sf = new StringBuffer();
			sf.append(" select * from V_StockPile ");
			if (materialId != null) {
				sf.append(" where MaterialCode ='");
				sf.append(materialId);
				sf.append("'");
				sf.append(" and quantity >0 ");
				sf.append(" and MaterialStatus =1 ");
				sf.append(" and DeptName = '");
				sf.append(warehouseId);
				sf.append("'");
			}
//			String sql = " select * from V_StockPile";
			ResultSet rs;

			rs = stmt.executeQuery(sf.toString());

			long i = 1;
			while (rs.next()) {
				com.graly.erp.inv.model.VStorageMaterial wmsStorage = new VStorageMaterial();
				wmsStorage.setObjectRrn(i);
				wmsStorage.setMaterialId(rs.getString(1));
				wmsStorage.setName(rs.getString(2));
				wmsStorage.setModel(rs.getString(3));//批次
				BigDecimal qtyOnhand = rs.getString(4) != null ? new BigDecimal(rs.getString(4)) : BigDecimal.ZERO;
				qtyOnhand = qtyOnhand.setScale(6,BigDecimal.ROUND_DOWN);
				wmsStorage.setQtyOnhand(qtyOnhand);
				wmsStorage.setInventoryUom(rs.getString(5));
				wmsStorages.add(wmsStorage);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new ClientException(e);
		}
		return wmsStorages;
	}
	
	private static Connection getConnection() {
    	Connection con = null;
    	try {
    		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
    		con = DriverManager.getConnection("jdbc:sqlserver://10.1.22.103:1433;DatabaseName=KN20150118", "erpuser", "erpuser");
    	} catch (Exception ee) {
    		ee.printStackTrace();
    	}
    	return con;
	}
	
	//出库调用批次不能选择立体库的批次
	public Lot getLotByLotIdNoWms(long orgRrn, String lotId) throws ClientException {
		Connection conn =null;
		Statement stmt=null;
		ResultSet rs=null;
		try{
			List<Lot> lotList = new ArrayList<Lot>();
			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot as Lot ");
			sql.append("WHERE");
			sql.append(ADBase.BASE_CONDITION);
			sql.append("AND lotId = ?");
			logger.debug(sql);
			StringBuffer sql2 = new StringBuffer(" select count(*)  from V_StockPile ");
			sql2.append("WHERE");
			sql2.append(" Batch='");
			sql2.append(lotId);
			sql2.append("'");
 
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql2.toString());

			while (rs.next()) {
				 int i = rs.getInt(1);
				 if(i>0){
					 throw new ClientException("不能使用立体库批次");
				 }
			}
		 
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, lotId);
			lotList = query.getResultList();
			if (lotList == null || lotList.size() == 0) {
				throw new ClientException("inv.lotnotexist");
			}
			Lot lot = lotList.get(0);
			return lot;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}finally{
			try {
				if(rs!=null){rs.close();}
				if(stmt!=null){stmt.close();};
				if(conn!=null){conn.close();};
				} catch (SQLException e) {
					throw new ClientException(e);
				}
		}
	}
	//出库调用批次不能选择立体库的批次
	public Lot getLotByLotIdInWms(long orgRrn, String lotId) throws ClientException {
		Connection conn =null;
		Statement stmt=null;
		ResultSet rs=null;
		try{
			List<Lot> lotList = new ArrayList<Lot>();
			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot as Lot ");
			sql.append("WHERE");
			sql.append(ADBase.BASE_CONDITION);
			sql.append("AND lotId = ?");
			logger.debug(sql);
			StringBuffer sql2 = new StringBuffer(" select count(*)  from V_StockPile ");
			sql2.append("WHERE");
			sql2.append(" Batch='");
			sql2.append(lotId);
			sql2.append("'");
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql2.toString());

			while (rs.next()) {
				 int i = rs.getInt(1);
				 if(i<=0){
					 throw new ClientException("inv.lotnotexist");
				 }
			}
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, lotId);
			lotList = query.getResultList();
			if (lotList == null || lotList.size() == 0) {
				throw new ClientException("inv.lotnotexist");
			}
			Lot lot = lotList.get(0);
			return lot;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}finally{
			try {
				if(rs!=null){rs.close();}
				if(stmt!=null){stmt.close();};
				if(conn!=null){conn.close();};
				} catch (SQLException e) {
//					throw new ClientException(e);
				}
		}
	}
//	
//	//恢复最原始的版本，可以从数据库自动选择批次，不能选中立体库中的批次
//	public List<Lot> getOptionalOutLotNoWms(MovementLine outLine) throws ClientException {
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT Lot, LotStorage FROM Lot as Lot, LotStorage as LotStorage ");
//		sql.append(" WHERE Lot.objectRrn = LotStorage.lotRrn" );
//		sql.append(" AND Lot.materialRrn = ? ");
//		sql.append(" AND LotStorage.warehouseRrn = ? ");
//		sql.append(" AND LotStorage.qtyOnhand > 0 ");
//		String wmsLot = getWmsLotByMaterialId(outLine.getMaterialId());
//		if(wmsLot!=null && wmsLot.length() > 0 ){
//			sql.append(" AND Lot.lotId not in ( ");
//			sql.append(wmsLot);
//			sql.append(" )");
//		}
//		
//		sql.append(" ORDER BY Lot.dateIn ASC ");
//		
//		logger.debug(sql);
//		List<Lot> optionalLots = new ArrayList<Lot>();
//		try {
//			Movement movement = em.find(Movement.class, outLine.getMovementRrn());
//			Query query = em.createQuery(sql.toString());
//			query.setParameter(1, outLine.getMaterialRrn());
//			query.setParameter(2, movement.getWarehouseRrn());
//			List list = query.getResultList();
//			for (int i = 0; i < list.size(); i++) {
//				Object[] obj = (Object[])list.get(i);
//				Lot lot = (Lot)obj[0];
//				LotStorage lotStorage = (LotStorage)obj[1];
//				lot.setQtyTransaction(lotStorage.getQtyOnhand());
//				optionalLots.add(lot);
//			}
//			
//
//			Material material = em.find(Material.class, outLine.getMaterialRrn());
//			
//			if (material.getIsLotControl()) {
//				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
//					if (outLine.getQtyMovement().doubleValue() > optionalLots.size()) {
//						throw new ClientException("inv.not_sufficient_quantity");
//					} else {
//						List<Lot> lots = new ArrayList<Lot>();
//						for (int i = 0; i < outLine.getQtyMovement().intValue(); i++) {
//							lots.add(optionalLots.get(i));
//						}
//						return lots;
//					}
//				} else {
//					BigDecimal qtyOut = BigDecimal.ZERO;
//					List<Lot> lots = new ArrayList<Lot>();
//					for (Lot optionalLot : optionalLots) {
//						qtyOut = qtyOut.add(optionalLot.getQtyTransaction()); 
//						if (qtyOut.compareTo(outLine.getQtyMovement()) >= 0) {
//							optionalLot.setQtyTransaction(
//									optionalLot.getQtyTransaction().subtract(qtyOut.subtract(outLine.getQtyMovement())));
//							lots.add(optionalLot);
//							break;
//						} else {
//							lots.add(optionalLot);
//						}
//					}
//					return lots;
//				}
//			} else {
//				throw new ClientException("inv.material_is_not_lotcontrol");
//			}
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
//	
//	//自动选批，必须选择WMS库中的批次，如果WMS中物料批次不存在，提示
//	public List<Lot> getOptionalOutLotInWms(MovementLine outLine) throws ClientException {
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT Lot, LotStorage FROM Lot as Lot, LotStorage as LotStorage ");
//		sql.append(" WHERE Lot.objectRrn = LotStorage.lotRrn" );
//		sql.append(" AND Lot.materialRrn = ? ");
//		sql.append(" AND LotStorage.warehouseRrn = ? ");
//		sql.append(" AND LotStorage.qtyOnhand > 0 ");
//		String wmsLot = getWmsLotByMaterialId(outLine.getMaterialId());
//		if(wmsLot!=null && wmsLot.length() > 0 ){
//			sql.append(" AND Lot.lotId in ( ");
//			sql.append(wmsLot);
//			sql.append(" )");
//		}else{
//			throw new ClientException("WMS没有足够的批次");
//		}
//
//		sql.append(" ORDER BY Lot.dateIn ASC ");
//		
//		logger.debug(sql);
//		List<Lot> optionalLots = new ArrayList<Lot>();
//		try {
//			Movement movement = em.find(Movement.class, outLine.getMovementRrn());
//			Query query = em.createQuery(sql.toString());
//			query.setParameter(1, outLine.getMaterialRrn());
//			query.setParameter(2, movement.getWarehouseRrn());
//			List list = query.getResultList();
//			for (int i = 0; i < list.size(); i++) {
//				Object[] obj = (Object[])list.get(i);
//				Lot lot = (Lot)obj[0];
//				LotStorage lotStorage = (LotStorage)obj[1];
//				lot.setQtyTransaction(lotStorage.getQtyOnhand());
//				optionalLots.add(lot);
//			}
//			
//
//			Material material = em.find(Material.class, outLine.getMaterialRrn());
//			
//			if (material.getIsLotControl()) {
//				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
//					if (outLine.getQtyMovement().doubleValue() > optionalLots.size()) {
//						throw new ClientException("inv.not_sufficient_quantity");
//					} else {
//						List<Lot> lots = new ArrayList<Lot>();
//						for (int i = 0; i < outLine.getQtyMovement().intValue(); i++) {
//							lots.add(optionalLots.get(i));
//						}
//						return lots;
//					}
//				} else {
//					BigDecimal qtyOut = BigDecimal.ZERO;
//					List<Lot> lots = new ArrayList<Lot>();
//					for (Lot optionalLot : optionalLots) {
//						qtyOut = qtyOut.add(optionalLot.getQtyTransaction()); 
//						if (qtyOut.compareTo(outLine.getQtyMovement()) >= 0) {
//							optionalLot.setQtyTransaction(
//									optionalLot.getQtyTransaction().subtract(qtyOut.subtract(outLine.getQtyMovement())));
//							lots.add(optionalLot);
//							break;
//						} else {
//							lots.add(optionalLot);
//						}
//					}
//					return lots;
//				}
//			} else {
//				throw new ClientException("inv.material_is_not_lotcontrol");
//			}
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
	
	public String getWmsLotByMaterialId(String materialId) throws ClientException {//好像取消用了
		StringBuffer result = new StringBuffer();
		Connection conn =null;
		Statement stmt=null;
		ResultSet rs=null;
		try {
			 conn = getConnection();
			 stmt = conn.createStatement();
			StringBuffer sf = new StringBuffer();
			sf.append(" select distinct Batch from V_StockPile ");
			if (materialId != null) {
				sf.append(" where MaterialCode ='");
				sf.append(materialId);
				sf.append("'");
				sf.append(" and quantity >0 ");
				sf.append(" and MaterialStatus =1 ");
			}
			rs = stmt.executeQuery(sf.toString());

			long i = 1;
			while (rs.next()) {
				com.graly.erp.inv.model.VStorageMaterial wmsStorage = new VStorageMaterial();
				wmsStorage.setObjectRrn(i);
				wmsStorage.setMaterialId(rs.getString(1));
				result.append("'");
				result.append(rs.getString(1));
				result.append("',");
				++i;
			}
		 
		} catch (Exception e) {
			throw new ClientException(e);
		}finally{
			try {
			if(rs!=null){rs.close();}
			if(stmt!=null){stmt.close();};
			if(conn!=null){conn.close();};
			} catch (SQLException e) {
				throw new ClientException(e);
		}};
		
		if(result!=null&&result.length()>0){
			return result.substring(0, result.length()-1);
		}
		return result.toString();
	}
	
	//出库调用批次不能选择立体库的批次
	public BigDecimal getQtyInWmsStorage(String materialId,String warehouseId) throws ClientException {
		if(warehouseId==null || "".equals(warehouseId)){
			throw new ClientException("仓库不能为空");
		}
		Connection conn =null;
		Statement stmt=null;
		ResultSet rs=null;
		BigDecimal wmsQty = BigDecimal.ZERO;
		try{
			 
			StringBuffer sql2 = new StringBuffer(" select sum(quantity) as qty from V_StockPile ");
			sql2.append(" WHERE   quantity >0");
			sql2.append(" and MaterialStatus =1 ");
			sql2.append(" and  MaterialCode='");
			sql2.append(materialId);
			sql2.append("'  and DeptName = '");
			sql2.append(warehouseId);
			sql2.append("'");//			DeptName = '环保良品'

			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql2.toString());

			while (rs.next()) {
				 String i = rs.getString(1);
				 wmsQty = new BigDecimal(i==null||"".equals(i)?"0":i);
				 wmsQty = wmsQty.setScale(6,BigDecimal.ROUND_DOWN);
			}
			return wmsQty;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}finally{
			try {
				if(rs!=null){rs.close();}
				if(stmt!=null){stmt.close();};
				if(conn!=null){conn.close();};
				} catch (SQLException e) {
					throw new ClientException(e);
				}
		}
	}
	
	/**
	 *服务公司提醒
	 * 1.当服务公司物料的当前库存小于安全库存的时候弹出提醒
	 * */
	public List<Material> getServiceMaterialAlarmYS(Long orgRrn,String whereClause) throws ClientException{
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("   SELECT PM1.OBJECT_RRN,S1.QTY_ONHAND,PM1.Material_Id,pm1.name,pm1.qty_min_service,nvL(pl.plqty,0)+nvl(po.poqty,0) qtyTransit FROM ( ");
			sql.append("     SELECT MATERIAL_RRN, SUM(NVL(QTY_ONHAND,0)+NVL(QTY_DIFF,0)) AS QTY_ONHAND ");
			sql.append("        FROM INV_STORAGE S");
			sql.append("    WHERE S.ORG_RRN = ?1  ");
			sql.append("    AND S.MATERIAL_RRN IN ( SELECT PM.OBJECT_RRN FROM PDM_MATERIAL PM WHERE  ");
			sql.append("     PM.ORG_RRN=?2 AND PM.QTY_MIN_SERVICE IS NOT NULL ) ");
			sql.append("    AND WAREHOUSE_RRN IN (63741535) ");
			sql.append("     group by MATERIAL_RRN ");
			sql.append("   ) S1,( SELECT PM.OBJECT_RRN,pm.material_id,pm.name,PM.QTY_MIN_SERVICE FROM PDM_MATERIAL PM WHERE  ");
			sql.append("   PM.ORG_RRN=?3 AND PM.QTY_MIN_SERVICE IS NOT NULL ) PM1,( ");
			sql.append("          select plqty,material_rrn from ( ");
			sql.append("             SELECT NVL(SUM(QTY), 0) - NVL(SUM(QTY_ORDERED), 0) plqty,material_rrn ");
			sql.append("             FROM PUR_REQUISITION_LINE ");
			sql.append("             WHERE ORG_RRN = ?4 ");
			sql.append("             AND LINE_STATUS IN ('DRAFTED', 'APPROVED') ");
			sql.append("             group by material_rrn ");
			sql.append("             ) ");
			sql.append("          ) pl,( ");
			sql.append("             SELECT NVL(SUM(QTY), 0) - NVL(SUM(QTY_IN), 0) poqty,material_rrn ");
			sql.append("             FROM PUR_PO_LINE ");
			sql.append("             WHERE ORG_RRN = ?5  ");
			sql.append("             AND LINE_STATUS IN ('DRAFTED', 'APPROVED') ");
			sql.append("             group by material_rrn ");
			sql.append("           ");
			sql.append("          )po ");


			sql.append("   WHERE S1.MATERIAL_RRN = PM1.OBJECT_RRN ");
			sql.append("   AND S1.QTY_ONHAND < PM1.QTY_MIN_SERVICE ");
			sql.append("             and  s1.MATERIAL_RRN = pl.MATERIAL_RRN(+) ");
			sql.append("             and s1.MATERIAL_RRN = po.MATERIAL_RRN(+) ");
			if(whereClause!=null && whereClause.trim().length() >0 ){
				sql.append(whereClause );
			}
			sql.append("   ORDER BY PM1.Material_id ");
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, orgRrn);
			query.setParameter(3, orgRrn);
			query.setParameter(4, orgRrn);
			query.setParameter(5, orgRrn);
			
			List<Object[]> objs = query.getResultList();
			List<Material> materials = new ArrayList<Material>();
			for(Object[] obj : objs){
				Material m = new Material();
				m.setObjectRrn(((BigDecimal) obj[0]).longValue());
				m.setQtyOnHand((BigDecimal) obj[1]);
				m.setMaterialId((String)obj[2]);
				m.setName((String)obj[3]);
				m.setQtyMinService((BigDecimal) obj[4]);
				m.setQtyTransit((BigDecimal) obj[5]);
				materials.add(m);
			}
			return materials;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	
	public List<Lot> getSumWmsLotByLotId(String materialId,String lotId,String warehouseId) throws ClientException {
		List<Lot> wmsLots = new ArrayList<Lot>();
		Connection conn =null;
		Statement stmt=null;
		ResultSet rs=null;
		try {
			 conn = getConnection();
			 stmt = conn.createStatement();
			StringBuffer sf = new StringBuffer();
			sf.append(" select batch,quantity  from V_StockPile where 1=1");
			sf.append(" and  MaterialCode ='");
			sf.append(materialId);
			sf.append("'");
			sf.append(" and quantity >0 ");
			sf.append(" and MaterialStatus =1 ");
			sf.append(" and DeptName = '");
			sf.append(warehouseId);
			sf.append("'");
			sf.append(" and batch ='");
			sf.append(lotId);
			sf.append("' ");
			//sf.append("group by MaterialCode,Batch");
			
			rs = stmt.executeQuery(sf.toString());
			long i = 1;
			Lot wmsLot = new Lot();
			wmsLot.setObjectRrn(i);
			wmsLot.setLotId(lotId);
			BigDecimal sumQty = BigDecimal.ZERO;//求和函数导致小数点99999
			while (rs.next()) {
				BigDecimal qtyOnhand = rs.getString(2) != null ? new BigDecimal(rs.getString(2)) : BigDecimal.ZERO;
				sumQty = sumQty.add(qtyOnhand);
				++i;
			}
			wmsLot.setQtyTransaction(sumQty);
			wmsLots.add(wmsLot);
		} catch (Exception e) {
			throw new ClientException(e);
		}finally{
			try {
			if(rs!=null){rs.close();}
			if(stmt!=null){stmt.close();};
			if(conn!=null){conn.close();};
			} catch (SQLException e) {
				throw new ClientException(e);
		}};
		return wmsLots;
	}
	public List<Lot> getSumWmsLotByMaterialId(String materialId) throws ClientException {
		List<Lot> wmsLots = new ArrayList<Lot>();
		Connection conn =null;
		Statement stmt=null;
		ResultSet rs=null;
		try {
			 conn = getConnection();
			 stmt = conn.createStatement();
			StringBuffer sf = new StringBuffer();
			sf.append(" select batch,sum(quantity) quantity  from V_StockPile where 1=1");
			sf.append(" and  MaterialCode ='");
			sf.append(materialId);
			sf.append("'");
			sf.append(" and quantity >0 ");
			sf.append(" and MaterialStatus =1 ");
			sf.append("group by MaterialCode,Batch");
			rs = stmt.executeQuery(sf.toString());

			long i = 1;
			while (rs.next()) {
				Lot wmsLot = new Lot();
				wmsLot.setObjectRrn(i);
				wmsLot.setLotId(rs.getString(1));
				wmsLot.setQtyTransaction(new BigDecimal(rs.getString(2)));
				++i;
				wmsLots.add(wmsLot);
			}
		 
		} catch (Exception e) {
			throw new ClientException(e);
		}finally{
			try {
			if(rs!=null){rs.close();}
			if(stmt!=null){stmt.close();};
			if(conn!=null){conn.close();};
			} catch (SQLException e) {
				throw new ClientException(e);
		}};
		return wmsLots;
	}
	
	//得到WMS的库存
	public BigDecimal getLotStorageWms(long orgRrn, long lotRrn, long warehouseRrn, long userRrn,Lot lot)  throws ClientException {
		LotStorage lotStorage  = getLotStorage(orgRrn,lotRrn,warehouseRrn,userRrn);
		BigDecimal erpQty = BigDecimal.ZERO;
		erpQty= lotStorage.getQtyOnhand()!=null?lotStorage.getQtyOnhand():BigDecimal.ZERO;
		String warehouse ="";
		if(warehouseRrn ==151043L){
			warehouse = "环保良品";
		}else if(warehouseRrn ==151046L){
			warehouse = "制造车间";
		}
		List<Lot> wmsLots = getSumWmsLotByLotId(lot.getMaterialId(), lot.getLotId(),warehouse);
		if(erpQty!=null && erpQty.compareTo(BigDecimal.ZERO) >0){
			if(wmsLots!=null && wmsLots.size() > 0){
				Lot wmsLot = wmsLots.get(0);
				return  wmsLot.getQtyTransaction();
			}else{
				return BigDecimal.ZERO;
			}
		}
		return BigDecimal.ZERO;
	}
	//总仓良品新方法,总仓可用数量=环保良品-立体库
	public BigDecimal getLotStorageNew(long orgRrn, long lotRrn, long warehouseRrn, long userRrn,Lot lot)  throws ClientException {
		LotStorage lotStorage  = getLotStorage(orgRrn,lotRrn,warehouseRrn,userRrn);
		BigDecimal erpQty = BigDecimal.ZERO;
		erpQty = lotStorage.getQtyOnhand()!=null?lotStorage.getQtyOnhand():BigDecimal.ZERO;
		String warehouse ="";
		if(warehouseRrn ==151043L){
			warehouse = "环保良品";
		}else if(warehouseRrn ==151046L){
			warehouse = "制造车间";
		}
		List<Lot> wmsLots = getSumWmsLotByLotId(lot.getMaterialId(), lot.getLotId(),warehouse);
		if(erpQty!=null && erpQty.compareTo(BigDecimal.ZERO) >0){
			if(wmsLots!=null && wmsLots.size() > 0){
				Lot wmsLot = wmsLots.get(0);
				return erpQty.subtract(wmsLot.getQtyTransaction());
			}
			return erpQty;
		} 
		return BigDecimal.ZERO;
	}
	//不能取WMS批次 
	public List<Lot> getOptionalOutLotNoWms(MovementLine outLine) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Lot, LotStorage FROM Lot as Lot, LotStorage as LotStorage ");
		sql.append(" WHERE Lot.objectRrn = LotStorage.lotRrn" );
		sql.append(" AND Lot.materialRrn = ? ");
		sql.append(" AND LotStorage.warehouseRrn = ? ");
		sql.append(" AND LotStorage.qtyOnhand > 0 ");
		//String wmsLot = getWmsLotByMaterialId(outLine.getMaterialId());
		//if(wmsLot!=null && wmsLot.length() > 0 ){
		//	sql.append(" AND Lot.lotId not in ( ");
		//	sql.append(wmsLot);
		//	sql.append(" )");
		//}
		
		sql.append(" ORDER BY Lot.dateIn ASC ");
		
		logger.debug(sql);
		List<Lot> optionalLots = new ArrayList<Lot>();
		try {
			Movement movement = em.find(Movement.class, outLine.getMovementRrn());
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, outLine.getMaterialRrn());
			query.setParameter(2, movement.getWarehouseRrn());
			List list = query.getResultList();
			StringBuffer sfLotId = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[])list.get(i);
				Lot lot = (Lot)obj[0];
				LotStorage lotStorage = (LotStorage)obj[1];
				lot.setQtyTransaction(lotStorage.getQtyOnhand());
				sfLotId.append(lotStorage.getLotId()+",");
				optionalLots.add(lot);
			}
			
			List<Lot> wmsLots = getSumWmsLotByMaterialId(outLine.getMaterialId());//wms批次与数量
			Material material = em.find(Material.class, outLine.getMaterialRrn());
			Map<String,Lot> map = new HashMap<String,Lot>();
			if(wmsLots!=null && wmsLots.size()>0){
				for(Lot wmsLot : wmsLots){
					map.put(wmsLot.getLotId(), wmsLot);
				}
			}
			if (material.getIsLotControl()) {
				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
					if (outLine.getQtyMovement().doubleValue() > optionalLots.size()) {
						throw new ClientException("inv.not_sufficient_quantity");
					} else {
						List<Lot> lots = new ArrayList<Lot>();
						for (int i = 0; i < outLine.getQtyMovement().intValue(); i++) {
							lots.add(optionalLots.get(i));
						}
						return lots;
					}
				} else {
					BigDecimal qtyOut = BigDecimal.ZERO;
					List<Lot> lots = new ArrayList<Lot>();
					for (Lot optionalLot : optionalLots) {
						if(wmsLots!=null && wmsLots.size() > 0 ){//WMS存在则ERP批次-WMS批次 = 总仓批次数
							Lot wmsLot= map.get(optionalLot.getLotId());
							if(wmsLot!=null){
								BigDecimal erpQty = optionalLot.getQtyTransaction()!=null?optionalLot.getQtyTransaction():BigDecimal.ZERO;
								BigDecimal wmsQty = wmsLot.getQtyTransaction()!=null? wmsLot.getQtyTransaction():BigDecimal.ZERO;
								BigDecimal sunQty = erpQty.subtract(wmsQty); 
								optionalLot.setQtyTransaction(sunQty);//ERP可用数量,除去WMS可用数量
								if(sunQty.compareTo(BigDecimal.ZERO)<=0){
									continue;
								}
							}
						}
						qtyOut = qtyOut.add(optionalLot.getQtyTransaction()); 
						if (qtyOut.compareTo(outLine.getQtyMovement()) >= 0) {
							optionalLot.setQtyTransaction(
									optionalLot.getQtyTransaction().subtract(qtyOut.subtract(outLine.getQtyMovement())));
							lots.add(optionalLot);
							break;
						} else {
							lots.add(optionalLot);
						}
					}
					return lots;
				}
			} else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}





//自动选批，必须选择WMS库中的批次，如果WMS中物料批次不存在，提示
	public List<Lot> getOptionalOutLotInWms(MovementLine outLine) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Lot, LotStorage FROM Lot as Lot, LotStorage as LotStorage ");
		sql.append(" WHERE Lot.objectRrn = LotStorage.lotRrn" );
		sql.append(" AND Lot.materialRrn = ? ");
		sql.append(" AND LotStorage.warehouseRrn = ? ");
		sql.append(" AND LotStorage.qtyOnhand > 0 ");
		List<Lot> wmsLots = getSumWmsLotByMaterialId(outLine.getMaterialId());
		Map<String,Lot> wmsMap = new HashMap<String,Lot>();
		StringBuffer sf = new StringBuffer();
		if(wmsLots!=null && wmsLots.size()> 0 ){
			for(Lot wmsLot:wmsLots){//拼接WMS批次
				sf.append("'");
				sf.append(wmsLot.getLotId());
				sf.append("',");
				wmsMap.put(wmsLot.getLotId(), wmsLot);
			}
			String lotId = sf.substring(0,sf.length()-1);
			sql.append(" AND Lot.lotId in ( ");
			sql.append(lotId);
			sql.append(" )");
		}else{
			throw new ClientException("WMS没有足够的批次");
		}

		sql.append(" ORDER BY Lot.dateIn ASC ");
		
		logger.debug(sql);
		List<Lot> optionalLots = new ArrayList<Lot>();
		try {
			Movement movement = em.find(Movement.class, outLine.getMovementRrn());
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, outLine.getMaterialRrn());
			query.setParameter(2, movement.getWarehouseRrn());
			List list = query.getResultList();
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[])list.get(i);
				Lot lot = (Lot)obj[0];
				LotStorage lotStorage = (LotStorage)obj[1];
				//lot.setQtyTransaction(lotStorage.getQtyOnhand());
				Lot wmsLot = wmsMap.get(lot.getLotId());//取得WMS的批次
				lot.setQtyTransaction(wmsLot.getQtyTransaction());
				optionalLots.add(lot);
			}

			Material material = em.find(Material.class, outLine.getMaterialRrn());
			
			if (material.getIsLotControl()) {
				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
					if (outLine.getQtyMovement().doubleValue() > optionalLots.size()) {
						throw new ClientException("inv.not_sufficient_quantity");
					} else {
						List<Lot> lots = new ArrayList<Lot>();
						for (int i = 0; i < outLine.getQtyMovement().intValue(); i++) {
							lots.add(optionalLots.get(i));
						}
						return lots;
					}
				} else {
					BigDecimal qtyOut = BigDecimal.ZERO;
					List<Lot> lots = new ArrayList<Lot>();
					for (Lot optionalLot : optionalLots) {
						qtyOut = qtyOut.add(optionalLot.getQtyTransaction()); 
						if (qtyOut.compareTo(outLine.getQtyMovement()) >= 0) {
							optionalLot.setQtyTransaction(
									optionalLot.getQtyTransaction().subtract(qtyOut.subtract(outLine.getQtyMovement())));
							lots.add(optionalLot);
							break;
						} else {
							lots.add(optionalLot);
						}
					}
					return lots;
				}
			} else {
				throw new ClientException("inv.material_is_not_lotcontrol");
			}
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<String> getMovementInByWms(String whereClause) throws ClientException {
		 
		List<String> mo = new ArrayList<String>();
	
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" select distinct receiptId from StockIn where 1=1");
			if (whereClause != null) {
				sql.append(" and ");
				sql.append(whereClause);
			}
			Query query = em.createQuery(sql.toString());
			mo = query.getResultList();
		} catch (Exception e) {
			throw new ClientException(e);
		};
		return mo;
	}
	
	public MovementIn saveMovementInByWms(MovementIn in, List<MovementLine> lines, MovementIn.InType inType, long userRrn,List<StockIn> stockIns) throws ClientException {
		 MovementIn mi =saveMovementInLine(in, lines, inType, userRrn);
			for(StockIn stockIn: stockIns){
				stockIn = em.find(StockIn.class, stockIn.getObjectRrn());
				stockIn.setErpMovement(mi.getDocId());
				em.merge(stockIn);
			}
		return mi;
	}
	
	public MovementWorkShopServices approveMovementServicesStorage(MovementWorkShopServices workShopServices, long userRrn) throws ClientException {
		try{
			workShopServices.setDocStatus(MovementTransfer.STATUS_APPROVED);
			workShopServices.setDateApproved(new Date());
			workShopServices.setUpdatedBy(userRrn);
			em.merge(workShopServices);
			
			if (workShopServices.getMovementWorkShopLines().size() == 0) {
				throw new ClientException("inv.transfer_quantity_zero"); 
			}
			
			long transSeq = basManager.getHisSequence();
			for (MovementWorkShopLine line : workShopServices.getMovementWorkShopLines()) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				
				//1.更新movementLine状态
				if (MovementIn.STATUS_DRAFTED.equals(line.getLineStatus())) {
					line.setLineStatus(Movement.STATUS_APPROVED);
					line.setUpdatedBy(userRrn);
					em.merge(line);
				}
	
				updateServicesStorage(workShopServices.getOrgRrn(), line.getMaterialRrn(), workShopServices.getTargetWarehouseRrn(), line.getQtyMovement(),true, false, userRrn);
			 
			}
			return workShopServices;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	public void updateServicesStorage(long orgRrn, long materialRrn, long warehouseRrn, BigDecimal qty, boolean onHandFlag, boolean writeOffFlag, long userRrn) throws ClientException {
		try{
			ServicesStorage servicesStorage = getMaterialServicesStorage(orgRrn, materialRrn, warehouseRrn, userRrn);
			if(onHandFlag){
				servicesStorage.setQtyOnhand(servicesStorage.getQtyOnhand().add(qty));//修改营运库存
			}
			servicesStorage.setUpdatedBy(userRrn);
			em.merge(servicesStorage);
		} 
		catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} 
		catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	public ServicesStorage getMaterialServicesStorage(long orgRrn, long materialRrn, long warehouseRrn, long userRrn)  throws ClientException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ServicesStorage FROM ServicesStorage as ServicesStorage ");
		sql.append(" WHERE materialRrn = ?  ");
		ServicesStorage serStorage;
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, materialRrn);
//			query.setParameter(2, warehouseRrn);
			List<ServicesStorage> storages = query.getResultList();
			if (storages.size() == 0) {
				serStorage = new ServicesStorage();
				serStorage.setOrgRrn(orgRrn);
				serStorage.setMaterialRrn(materialRrn);
//				wsstorage.setWarehouseRrn(warehouseRrn);
				serStorage.setIsActive(true);
				serStorage.setCreatedBy(userRrn);
				serStorage.setCreated(new Date());
				serStorage.setUpdatedBy(userRrn);
				em.persist(serStorage);
			} else {
				serStorage = storages.get(0);
			}
			return serStorage;
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//保存车间配送单据行 
	public MovementWorkShopLine saveMovementWorkShopServicesLine(MovementWorkShopServices workShop, MovementWorkShopLine line, long userRrn) throws ClientException {
		List<MovementWorkShopLine> list = new ArrayList<MovementWorkShopLine>();
		list.add(line);
		workShop = saveMovementWorkShopServicesLine(workShop, list, userRrn);
		return workShop.getMovementWorkShopLines().get(0);
	}	
	
	//保存车间单据行
	public MovementWorkShopServices saveMovementWorkShopServicesLine(MovementWorkShopServices workShop, List<MovementWorkShopLine> lines, long userRrn) throws ClientException {
		try{
			if (workShop.getObjectRrn() == null) {
				workShop.setIsActive(true);
				workShop.setCreatedBy(userRrn);
				workShop.setCreated(new Date());
				workShop.setTotalLines(0L);
				workShop.setDocStatus(MovementWorkShop.STATUS_DRAFTED);
				String docId = workShop.getDocId();
				if (docId == null || docId.length() == 0) {
					workShop.setDocId(generateMovementWorkShopCode(workShop));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<MovementWorkShopServices> movements = adManager.getEntityList(workShop.getOrgRrn(), MovementWorkShopServices.class, 2, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				em.persist(workShop);
			}
			List<MovementWorkShopLine> savaLine = new ArrayList<MovementWorkShopLine>();
			for (MovementWorkShopLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getObjectRrn() != null) {
					
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(workShop.getObjectRrn());
					line.setMovementId(workShop.getDocId());
					workShop.setTotalLines(workShop.getTotalLines() + 1);
					em.persist(line);
				}
				
			 
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			workShop.setUpdatedBy(userRrn);
			workShop.setMovementWorkShopLines(null);
			em.merge(workShop);
			workShop.setMovementWorkShopLines(savaLine);
			
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return workShop;
	}
	
	
	//车间领料，制造库存为 lotStorage ,
	public MovementWorkShopLine saveMovementVirtualHouseLine(MovementWorkShopVirtualHouse virtualHouse, MovementWorkShopLine line, long userRrn) throws ClientException {
		List<MovementWorkShopLine> list = new ArrayList<MovementWorkShopLine>();
		list.add(line);
		virtualHouse = saveMovementVirtualHouseLine(virtualHouse, list, userRrn);
		return virtualHouse.getMovementWorkShopLines().get(0);
	}	
	
	public MovementWorkShopVirtualHouse saveMovementVirtualHouseLine(MovementWorkShopVirtualHouse virtualHouse, List<MovementWorkShopLine> lines, long userRrn) throws ClientException {
		try{
			if (virtualHouse.getObjectRrn() == null) {
				virtualHouse.setIsActive(true);
				virtualHouse.setCreatedBy(userRrn);
				virtualHouse.setCreated(new Date());
				virtualHouse.setTotalLines(0L);
				virtualHouse.setDocType(MovementWorkShop.DOCTYPE_VIR);
				virtualHouse.setDocStatus(MovementWorkShopVirtualHouse.STATUS_APPROVED);
				String docId = virtualHouse.getDocId();
				if (docId == null || docId.length() == 0) {
					virtualHouse.setDocId(generateMovementWorkShopCode(virtualHouse));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<MovementWorkShopRequestion> movements = adManager.getEntityList(virtualHouse.getOrgRrn(), MovementWorkShopRequestion.class, 2, whereClause, "");
					if (movements.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				em.persist(virtualHouse);
			} 
			if (virtualHouse.getWarehouseRrn() == null) {
				throw new ClientException("inv.must_select_warehouse");
			}
			Warehouse house = em.find(Warehouse.class, virtualHouse.getWarehouseRrn());
			virtualHouse.setWarehouseId(house.getWarehouseId());
			
			List<MovementWorkShopLine> savaLine = new ArrayList<MovementWorkShopLine>();
			for (MovementWorkShopLine line : lines) {
				Material material = em.find(Material.class, line.getMaterialRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
				if (!material.getIsLotControl()) {
					throw new ClientException("inv.material_must_lotcontrol");
				}
				if (line.getObjectRrn() != null) {
					
				} else {
					line.setIsActive(true);
					line.setCreatedBy(userRrn);
					line.setCreated(new Date());
					line.setLineStatus(Requisition.STATUS_DRAFTED);
					line.setMovementRrn(virtualHouse.getObjectRrn());
					line.setMovementId(virtualHouse.getDocId());
					virtualHouse.setTotalLines(virtualHouse.getTotalLines() + 1);
					em.persist(line);
				}
				//更新库存
				updateStorage(virtualHouse.getOrgRrn(), line.getMaterialRrn(), virtualHouse.getWarehouseRrn(), line.getQtyMovement(), true, false, userRrn);
				
				
				if (Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						Lot lot = this.getMaterialLot(virtualHouse.getOrgRrn(), material, userRrn);
						LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), virtualHouse.getWarehouseRrn(), userRrn);
						BigDecimal qtyOnhand =lotStorage.getQtyOnhand()!=null?lotStorage.getQtyOnhand():BigDecimal.ZERO;
						lotStorage.setQtyOnhand(qtyOnhand.add(movementLot.getQtyMovement()));
						if (lotStorage.getQtyOnhand().compareTo(BigDecimal.ZERO) < 0) {
							throw new ClientException("inv.not_sufficient_quantity");
						}
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(virtualHouse.getObjectRrn());
							movementLot.setMovementId(virtualHouse.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(virtualHouse.getObjectRrn());
							movementLot.setMovementId(virtualHouse.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				} else {
					List<MovementWorkShopLineLot> movementLots = line.getMovementWorkShopLots() == null ? new ArrayList<MovementWorkShopLineLot>() : line.getMovementWorkShopLots();
					for (MovementWorkShopLineLot movementLot : movementLots) {
						Lot lot = em.find(Lot.class, movementLot.getLotRrn());
						if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
						} else {
							LotStorage lotStorage = this.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), virtualHouse.getWarehouseRrn(), userRrn);
							BigDecimal qtyOnhand =lotStorage.getQtyOnhand()!=null?lotStorage.getQtyOnhand():BigDecimal.ZERO;
							lotStorage.setQtyOnhand(qtyOnhand.add(movementLot.getQtyMovement()));
							if (lotStorage.getQtyOnhand().compareTo(BigDecimal.ZERO) < 0) {
								throw new ClientException("inv.not_sufficient_quantity");
							}
						}
						if (movementLot.getObjectRrn() == null) {
							movementLot.setMovementRrn(virtualHouse.getObjectRrn());
							movementLot.setMovementId(virtualHouse.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.persist(movementLot);						
						} else {
							movementLot.setMovementRrn(virtualHouse.getObjectRrn());
							movementLot.setMovementId(virtualHouse.getDocId());
							movementLot.setMovementLineRrn(line.getObjectRrn());
							em.merge(movementLot);
						}
					}
				}
				
				line.setUpdatedBy(userRrn);
				em.merge(line);
				savaLine.add(line);
			}
			if (savaLine.size() == 0) {
				throw new ClientException("inv.in_quantity_zero");
			}
			virtualHouse.setUpdatedBy(userRrn);
			virtualHouse.setMovementWorkShopLines(null);
			em.merge(virtualHouse);
			virtualHouse.setMovementWorkShopLines(savaLine);
			
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return virtualHouse;
	}
	
	public void deleteMovementWorkShopVirtualHouse(MovementWorkShopVirtualHouse virtualHouse,boolean completeFlag, long userRrn)throws ClientException {
		try{
			if(virtualHouse != null && virtualHouse.getObjectRrn() != null) {
				virtualHouse = em.find(MovementWorkShopVirtualHouse.class, virtualHouse.getObjectRrn());
				if(!completeFlag){
					virtualHouse.setDocStatus(MovementWorkShopVirtualHouse.STATUS_CLOSED);
				}else{
					virtualHouse.setDocStatus(MovementWorkShopVirtualHouse.STATUS_COMPLETED);
				}
				
				for (int i=0; i< virtualHouse.getMovementWorkShopLines().size(); i++){
					MovementWorkShopLine line= virtualHouse.getMovementWorkShopLines().get(i);
					updateStorage(virtualHouse.getOrgRrn(), line.getMaterialRrn(), virtualHouse.getWarehouseRrn(), line.getQtyMovement().negate(), true, false, userRrn);
					deleteMovementWorkShopVirtualHouseLine(line, true,completeFlag, userRrn);
				}
				em.merge(virtualHouse);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteMovementWorkShopVirtualHouseLine(MovementWorkShopLine workShopLine, boolean allFlag,boolean completeFlag, long userRrn)throws ClientException {
		try {
			if(workShopLine != null && workShopLine.getObjectRrn() != null) {
				workShopLine = em.find(MovementWorkShopLine.class, workShopLine.getObjectRrn());
				if(!completeFlag){
					workShopLine.setLineStatus(MovementWorkShopVirtualHouse.STATUS_CLOSED);
				}else{
					workShopLine.setLineStatus(MovementWorkShopVirtualHouse.STATUS_COMPLETED);
				}
				// 更新movement
				MovementWorkShopVirtualHouse virtualHouse = em.find(MovementWorkShopVirtualHouse.class, workShopLine.getMovementRrn());		
				if (!allFlag) {
					virtualHouse.setTotalLines(virtualHouse.getTotalLines() - 1);
					em.merge(virtualHouse);
				}

				StringBuffer sql = new StringBuffer("");
				sql.append(" SELECT MovementWorkShopLineLot FROM MovementWorkShopLineLot MovementWorkShopLineLot ");
				sql.append(" WHERE  movementLineRrn = ? ");
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, workShopLine.getObjectRrn());
				List<MovementWorkShopLineLot> movementLots = query.getResultList();
				for (MovementWorkShopLineLot movementLot : movementLots) {
					LotStorage lotStorage = this.getLotStorage(movementLot.getOrgRrn(), movementLot.getLotRrn(), virtualHouse.getWarehouseRrn(), userRrn);
					BigDecimal qtyOnhand =lotStorage.getQtyOnhand()!=null?lotStorage.getQtyOnhand():BigDecimal.ZERO;
					lotStorage.setQtyOnhand(qtyOnhand.subtract(movementLot.getQtyMovement()));
					if (lotStorage.getQtyOnhand().compareTo(BigDecimal.ZERO) < 0) {
						throw new ClientException("inv.not_sufficient_quantity");
					}
					
//					Lot lot = em.find(Lot.class, movementLot.getLotRrn());
//					lot.setOutId(null);
//					lot.setOutRrn(null);
//					lot.setOutLineRrn(null);
//					lot.setUpdatedBy(userRrn);
//					em.merge(lot);
//					movementLot.setIsActive(false);
//					em.remove(movementLot);
				}
				em.merge(workShopLine);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Material> queryMaterialQtysAlarmYn(Long orgRrn,String whereClause,String whereClause2,String whereClause3) throws ClientException {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT DISTINCT PM.OBJECT_RRN AS MATERIAL_RRN,");
			sql.append("                PM.Org_Rrn AS ORG_RRN,");
			sql.append("                PM.MATERIAL_ID AS MATERIAL_ID,");
			sql.append("                PM.NAME AS NAME,");
			sql.append("                pm.reference_Doc5 AS reference_Doc5,");
			sql.append("                IVS.QTY_ONHAND AS QTY_ONHAND,");
			sql.append("                round(pm.QTY_MIN*0.3,2) AS QTY_ALLOCATION,");
			sql.append("                IVS.QTY_ONHAND -  round(pm.QTY_MIN*0.3,2) AS CANNEED,");
			sql.append("                CASE WHEN WV.ONWAY IS NULL THEN 0 ELSE WV.ONWAY END AS ALLONWAY,");
			sql.append("                CPP.PURCHASER, CPP.PROMISED,pm.inventory_uom,nvl(QTY_DELIVERED,0) QTY_DELIVERED");
			sql.append("  FROM PDM_MATERIAL PM");
			sql.append(" INNER JOIN (SELECT MATERIAL_RRN, SUM(NVL(QTY_ONHAND,0)+NVL(QTY_DIFF,0)) AS QTY_ONHAND");
			sql.append("               FROM INV_STORAGE");
			sql.append("              WHERE ORG_RRN = ?1");
			sql.append("                AND WAREHOUSE_RRN IN (68088940, 70219951)");
			sql.append("              GROUP BY MATERIAL_RRN) IVS");
			sql.append("    ON PM.OBJECT_RRN = IVS.MATERIAL_RRN");
			sql.append("   AND PM.ORG_RRN = ?2");
			sql.append("  LEFT JOIN (SELECT PC.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                    QTY,");
			sql.append("                    (QTY - CASE");
			sql.append("                      WHEN QM IS NULL THEN");
			sql.append("                       0");
			sql.append("                      ELSE");
			sql.append("                       QM");
			sql.append("                    END) AS ONWAY,QTY_DELIVERED");
			sql.append("               FROM (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                            SUM(CASE WHEN PP.DOC_STATUS = 'CLOSED' THEN PPL.QTY_IN ELSE PPL.QTY END) AS QTY,");
			sql.append("                            SUM(CASE WHEN PP.DOC_STATUS = 'DRAFTED' or PP.DOC_STATUS = 'APPROVED' THEN PPL.QTY_DELIVERED ELSE 0 END) AS QTY_DELIVERED");
			sql.append("                       FROM PUR_PO PP");
			sql.append("                      INNER JOIN PUR_PO_LINE PPL");
			sql.append("                         ON PP.OBJECT_RRN = PPL.PO_RRN");
			sql.append("                        AND PP.ORG_RRN = ?5");
			sql.append("                      GROUP BY PPL.MATERIAL_RRN) PC");
			sql.append("               LEFT JOIN (SELECT MATERIAL_RRN, SUM(QTY_MOVEMENT) AS QM");
			sql.append("                           FROM INV_MOVEMENT IM");
			sql.append("                          INNER JOIN INV_MOVEMENT_LINE IML");
			sql.append("                             ON IM.DOC_ID = IML.MOVEMENT_ID");
			sql.append("                            AND IM.DOC_STATUS IN ('APPROVED', 'COMPLETED')");
			sql.append("                          WHERE IM.ORG_RRN = ?6");
			sql.append("                            AND DOC_TYPE = 'PIN'");
			sql.append("                          GROUP BY MATERIAL_RRN) CC");
			sql.append("                 ON PC.MATERIAL_RRN = CC.MATERIAL_RRN) WV");
			sql.append("    ON PM.OBJECT_RRN = WV.MATERIAL_RRN");
			sql.append("  LEFT JOIN (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                    PP.PURCHASER     AS PURCHASER,");
			sql.append("  					PPL.DATE_PROMISED AS PROMISED");
			sql.append("               FROM PUR_PO PP");
			sql.append("              INNER JOIN PUR_PO_LINE PPL");
			sql.append("                 ON PP.DOC_ID = PPL.PO_ID");
			sql.append("                AND PP.ORG_RRN = ?7");
			sql.append("                AND PP.DOC_STATUS <> 'CLOSED'");
			sql.append("              INNER JOIN (SELECT PPL.MATERIAL_RRN AS MATERIAL_RRN,");
			sql.append("                                MAX(PP.DATE_APPROVED) AS LAST_TIME");
			sql.append("                           FROM PUR_PO PP");
			sql.append("                          INNER JOIN PUR_PO_LINE PPL");
			sql.append("                             ON PP.DOC_ID = PPL.PO_ID");
			sql.append("                            AND PP.ORG_RRN = ?8");
			sql.append("                            AND PP.DOC_STATUS <> 'CLOSED'");
			sql.append("                          GROUP BY PPL.MATERIAL_RRN) C");
			sql.append("                 ON PPL.MATERIAL_RRN = C.MATERIAL_RRN");
			sql.append("                AND PP.DATE_APPROVED = C.LAST_TIME) CPP");
			sql.append("    ON PM.OBJECT_RRN = CPP.MATERIAL_RRN");
			sql.append(" WHERE PM.IS_PURCHASE = 'Y'");
			sql.append(" AND PM.QTY_MIN <> 0");//排除安全库存为零的物料
			sql.append(" and IVS.QTY_ONHAND -  round(pm.QTY_MIN*0.3,2) <=0 ");
			if(whereClause3 != null && whereClause3.trim().length() > 0){
				sql.append("   AND " + whereClause3);
			}
			if(whereClause != null && whereClause.trim().length() > 0){
				sql.append(whereClause);
			}
			
			
			StringBuffer sql2 = new StringBuffer("");
			sql2.append("   SELECT t.material_rrn, ");
			sql2.append("          t.org_rrn, ");
			sql2.append("          t.material_id, ");
			sql2.append("          t.name, ");
			sql2.append("          t.qty_onhand, ");
			sql2.append("          t.qty_allocation, ");
			sql2.append("          t.allonway, ");
			sql2.append("          t.inventory_uom, ");

			sql2.append("          wmsys.wm_concat (t.purchaser) purchaser, ");
			sql2.append("		   wmsys.wm_concat (to_char(t.promised,'yyyy-mm-dd')) promised,t.QTY_DELIVERED,t.reference_Doc5");
			sql2.append(" FROM (");
			sql2.append(sql);
			sql2.append(") t where 1=1 ");
			if(whereClause2 != null && whereClause2.trim().length() > 0){
				sql2.append(whereClause2);
			}
			sql2.append(" GROUP BY t.material_rrn, ");
			sql2.append("          t.org_rrn, ");
			sql2.append("          t.material_id, ");
			sql2.append("          t.name, ");
			sql2.append("          t.qty_onhand, ");
			sql2.append("          t.qty_allocation, ");
			sql2.append("          t.allonway, ");
			sql2.append("          t.inventory_uom,t.QTY_DELIVERED,t.reference_Doc5 ");
			
			//处理最新到货日期
			StringBuffer sql3 = new StringBuffer();
			sql3.append(" select min(line.date_promised) date_promised,line.material_rrn from pur_po_line line WHERE  ORG_RRN = ?9 ");
			sql3.append(" and line_status in ('DRAFTED','APPROVED') AND line.Qty-(case when line.QTY_IN is null then 0 else line.QTY_IN end)>0 ");
			sql3.append(" group by line.material_rrn");
			
			StringBuffer sql5 = new StringBuffer();
			sql5.append(" select distinct vvm.material_rrn,vvm.least_quantity from vdm_vendor_material vvm ");
			sql5.append("  where vvm.org_rrn = 68088906 and vvm.is_primary = 'Y'");
		
			StringBuffer sql4 = new StringBuffer();
			sql4.append("   SELECT sql2.material_rrn, ");
			sql4.append("      	   sql2.org_rrn, ");
			sql4.append("          sql2.material_id, ");
			sql4.append("          sql2.name, ");
			sql4.append("          sql2.qty_onhand, ");
			sql4.append("          sql2.qty_allocation, ");
			sql4.append("          sql2.allonway, ");
			sql4.append("          sql2.purchaser, ");
			sql4.append("	to_char(sql3.date_promised,'yyyy-mm-dd') promised,");
			sql4.append("          sql2.inventory_uom,sql2.QTY_DELIVERED,sql2.reference_Doc5 ");
			sql4.append(" FROM (");
			sql4.append(sql2.toString());
			sql4.append(" ) sql2");
			sql4.append(" left join (");
			sql4.append(sql3);
			sql4.append(" ) sql3");
			sql4.append(" on ");
			sql4.append(" sql2.material_rrn = sql3.material_rrn ");
			sql4.append(" left join (");
			sql4.append(sql5);
			sql4.append(" ) sql5");
			sql4.append(" on ");
			sql4.append(" sql2.material_rrn = sql5.material_rrn ");
			
			
			Query query = em.createNativeQuery(sql4.toString());
			
			query.setParameter(1, orgRrn);
			query.setParameter(2, orgRrn);
//			query.setParameter(3, orgRrn);
//			query.setParameter(4, orgRrn);
			query.setParameter(5, orgRrn);
			query.setParameter(6, orgRrn);
			query.setParameter(7, orgRrn);
			query.setParameter(8, orgRrn);
			query.setParameter(9, orgRrn);
			
			List<Object[]> objs = query.getResultList();
			List<Material> materials = new ArrayList<Material>();
			
			for(Object[] obj : objs){
				Material m = new Material();
				m.setObjectRrn(((BigDecimal) obj[0]).longValue());
				m.setOrgRrn(((BigDecimal) obj[1]).longValue());
				m.setMaterialId((String)obj[2]);
				m.setName((String)obj[3]);
				m.setQtyOnHand((BigDecimal) obj[4]);
				m.setQtyAllocation((BigDecimal) obj[5]);
				m.setQtyTransit((BigDecimal) obj[6]);
				m.setPlannerId((String)obj[7]);//借用此字段保存采购员信息
				m.setPromised((String)obj[8]);//最新到货日期
				m.setInventoryUom((String) obj[9]);//单位
				m.setQtyOut((BigDecimal) obj[10]);
				m.setReferenceDoc5((String)obj[11]);
//				BigDecimal lastPrice =  m.getLastPrice()!=null?m.getLastPrice():BigDecimal.ZERO;
				BigDecimal qtyOnHand = m.getQtyOnHand()!=null?m.getQtyOnHand():BigDecimal.ZERO;
//				BigDecimal totalPrice = lastPrice.multiply(qtyOnHand);//总价
//				m.setTotalPrice(totalPrice);
				materials.add(m);
			}
		
			return materials;
		} catch (Exception e) {
			throw new ClientException(e);
		}
		}	
}

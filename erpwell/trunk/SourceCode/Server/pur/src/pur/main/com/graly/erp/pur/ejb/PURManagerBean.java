package com.graly.erp.pur.ejb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.model.Storage;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.VPoAlarmIqcLine;
import com.graly.erp.inv.model.VPoAlarmMovenetLine;
import com.graly.erp.inv.model.VPoAlarmReceiptLine;
import com.graly.erp.inv.model.VUserWarehouse;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.Vendor;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.util.DataFmtUtil;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;
import com.graly.framework.security.model.ADUser;
import com.graly.mes.wip.model.Lot;

@Stateless
@Remote(PURManager.class)
@Local(PURManager.class)
public class PURManagerBean implements PURManager{
	private static final Logger logger = Logger.getLogger(PURManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private ADManager adManager;
	
	@EJB
	private PDMManager pdmManager;
	
	@EJB
	private VDMManager vdmManager;
	
	@EJB
	private BASManager basManager;
	
	@EJB
	
	private INVManager invManager;
	
	public RequisitionLine newPRLine(Requisition pr) throws ClientException {
		RequisitionLine prLine = new RequisitionLine();
		try{
			if (pr != null && pr.getObjectRrn() != null) {
				pr = em.find(Requisition.class, pr.getObjectRrn());
				long maxLineNo = 1;
				for (RequisitionLine line : pr.getPrLines()) {
					maxLineNo = maxLineNo < line.getLineNo() ? line.getLineNo() : maxLineNo;
				}
				prLine.setOrgRrn(pr.getOrgRrn());
				prLine.setLineNo((long)Math.ceil(maxLineNo / 10) * 10 + 10);
			} else {
				prLine.setLineNo(10L);
			}
			prLine.setLineStatus(Documentation.STATUS_DRAFTED);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return prLine;
	}
	
	public RequisitionLine savePRLine(RequisitionLine prLine, long userRrn) throws ClientException {
		Requisition pr = null;
		if (prLine.getRequisitionRrn() != null) {
			pr = em.find(Requisition.class, prLine.getRequisitionRrn());
			pr.setPrLines(new ArrayList<RequisitionLine>());
		}
		if (pr == null) {
			pr = new Requisition();
			pr.setOrgRrn(prLine.getOrgRrn());
		}
		List<RequisitionLine> prLines = new ArrayList<RequisitionLine>();
		prLines.add(prLine);
		pr = savePRLine(pr, prLines, false, userRrn);
		return pr.getPrLines().get(0);
	}
	
	public Requisition savePRLine(Requisition pr, List<RequisitionLine> prLines , boolean batchFlag, long userRrn) throws ClientException {
		try {
			if (pr.getObjectRrn() == null) {
				pr.setIsActive(true);
				pr.setCreatedBy(userRrn);
				pr.setCreated(new Date());
				pr.setTotalLines(0L);
				pr.setTotal(BigDecimal.ZERO);
				pr.setDocStatus(Documentation.STATUS_DRAFTED);
				pr.setDocType(Documentation.DOCTYPE_TPR);
				
				String docId = pr.getDocId();
				if (docId == null || docId.length() == 0) {
					pr.setDocId(generatePrCode(pr));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<Requisition> prs = adManager.getEntityList(pr.getOrgRrn(), Requisition.class, 1, whereClause, "");
					if (prs.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}

				ADUser user = em.find(ADUser.class, userRrn);
				pr.setUserCreated(user.getUserName());
				pr.setDateCreated(new Date());
				em.persist(pr);
			} else if (batchFlag) {
				pr = em.find(Requisition.class, pr.getObjectRrn());
			}
			
			for (RequisitionLine prLine : prLines) {
				//设置参考供应商
				VendorMaterial primaryVendor = vdmManager.getPrimaryVendor(prLine.getMaterialRrn());
				if (primaryVendor != null) {
					prLine.setRefVendorRrn(primaryVendor.getVendorRrn());
				}
				//设置参考价格
				VendorMaterial currentVendor;
				if (prLine.getVendorRrn() == null) {
					if (primaryVendor == null) {
						throw new ClientException("pur.no_vendor_found");
					}
					//如果没有选定供应商，则默认为主供应商
					currentVendor = primaryVendor;
					prLine.setVendorRrn(primaryVendor.getVendorRrn());
				} else if (primaryVendor != null && prLine.getVendorRrn().equals(primaryVendor.getObjectRrn())) {
					currentVendor = primaryVendor;
				} else {
					currentVendor = vdmManager.getVendorMaterial(prLine.getVendorRrn(), prLine.getMaterialRrn());
				}
				if (currentVendor != null) {
					if (currentVendor.getLastPrice() != null) {
						prLine.setRefUnitPrice(currentVendor.getLastPrice());
					} else {
						prLine.setRefUnitPrice(currentVendor.getReferencedPrice());
					}
					//如果采购单价为null，则将上次采购价(参考价不为null)或参考价赋给它
					if(prLine.getUnitPrice() == null) {
						if(currentVendor.getLastPrice() != null) {
							prLine.setUnitPrice(currentVendor.getLastPrice());							
						} else {
							prLine.setUnitPrice(currentVendor.getReferencedPrice());
						}
					}
					//通过除的方式保留2位小数（四舍五入）
					BigDecimal lineTotal = (prLine.getQty().multiply(prLine.getUnitPrice())).divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);
					prLine.setLineTotal(lineTotal);

					prLine.setQtyEconomicSize(currentVendor.getLeastQuantity());
					prLine.setQtyIncreaseSize(currentVendor.getIncreaseQuantity());
				}
				
				if (prLine.getWarehouseRrn() != null) {
					Warehouse house = em.find(Warehouse.class, prLine.getWarehouseRrn());
					prLine.setWarehouseId(house.getWarehouseId());
				}
				
				if (prLine.getObjectRrn() == null) {
					prLine.setIsActive(true);
					prLine.setCreatedBy(userRrn);
					prLine.setCreated(new Date());
					prLine.setLineStatus(Documentation.STATUS_DRAFTED);
					pr.setTotalLines(pr.getTotalLines() + 1);
					pr.setTotal(pr.getTotal().add(prLine.getLineTotal()));
				} else {
					RequisitionLine oldLine = em.find(RequisitionLine.class, prLine.getObjectRrn());
					pr.setTotal(pr.getTotal().subtract(oldLine.getLineTotal()).add(prLine.getLineTotal()));
				}
				
//				Material material = em.find(Material.class, prLine.getMaterialRrn());
//				BigDecimal convert = pdmManager.getConvertOfUom(prLine.getMaterialRrn(), Uom.CONTYPE_PUR2INV);
//				prLine.setUomId(material.getPurchaseUom());
//				prLine.setQtyInventoty(convert.multiply(prLine.getQty()));
							
				prLine.setUpdatedBy(userRrn);
				if (prLine.getObjectRrn() == null) {
					prLine.setRequisitionRrn(pr.getObjectRrn());
					prLine.setRequisitionId(pr.getDocId());
					em.persist(prLine);
				} else {
					prLine = em.merge(prLine);
				}
			}
			pr.setUpdatedBy(userRrn);
			pr = em.merge(pr);
			pr.setPrLines(prLines);
			em.flush();
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
		return pr;
	}
	
	public void closePR(Requisition pr, long userRrn) throws ClientException {
		try{
			pr.setDocStatus(Documentation.STATUS_CLOSED);
			pr.setUpdatedBy(userRrn);
			em.merge(pr);
			for (RequisitionLine prLine : pr.getPrLines()) {
				prLine.setLineStatus(Documentation.STATUS_CLOSED);
				prLine.setUpdatedBy(userRrn);
				em.merge(prLine);
			}
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void approvePR(Requisition pr, long userRrn) throws ClientException {
		try{
			pr.setDocStatus(Documentation.STATUS_APPROVED);
			pr.setUpdatedBy(userRrn);
			ADUser user = em.find(ADUser.class, userRrn);
			pr.setUserApproved(user.getUserName());
			pr.setDateApproved(new Date());
			em.merge(pr);
			for (RequisitionLine prLine : pr.getPrLines()) {
				prLine.setLineStatus(Documentation.STATUS_APPROVED);
				prLine.setUpdatedBy(userRrn);
				em.merge(prLine);
			}
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void closePRLine(RequisitionLine prLine , long userRrn) throws ClientException {
		try{
			Requisition pr = null;
			prLine.setLineStatus(Documentation.STATUS_CLOSED);
			prLine.setUpdatedBy(userRrn);
			em.merge(prLine);
			if (prLine.getRequisitionRrn() != null) {
				pr = em.find(Requisition.class, prLine.getRequisitionRrn());
				for (RequisitionLine line : pr.getPrLines()) {
					if (!Documentation.STATUS_CLOSED.equals(line.getLineStatus())) {
						return;
					}
				}
				pr.setDocStatus(Documentation.STATUS_CLOSED);
				pr.setUpdatedBy(userRrn);
				em.merge(pr);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deletePR(Requisition pr, long userRrn)throws ClientException {
		try{
			if(pr != null && pr.getObjectRrn() != null) {
				Requisition req = em.find(Requisition.class, pr.getObjectRrn());
				for(RequisitionLine prLine : req.getPrLines()) {
					em.remove(prLine);
				}
				em.remove(req);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deletePRLine(RequisitionLine prLine, long userRrn) throws ClientException {
		try {
			if(prLine != null && prLine.getObjectRrn() != null) {
				RequisitionLine reqLine = em.find(RequisitionLine.class, prLine.getObjectRrn());
				if(reqLine.getRequisitionRrn() != null) {
					Requisition pr = em.find(Requisition.class, reqLine.getRequisitionRrn());
					pr.setPrLines(new ArrayList<RequisitionLine>());
					pr.setTotal(pr.getTotal().subtract(reqLine.getLineTotal()));
					pr.setTotalLines(pr.getTotalLines() - 1);
					em.merge(pr);
				}
				em.remove(reqLine);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public PurchaseOrderLine newPOLine(PurchaseOrder po) throws ClientException {
		PurchaseOrderLine poLine = new PurchaseOrderLine();
		try{
			if (po != null && po.getObjectRrn() != null) {
				po = em.find(PurchaseOrder.class,po.getObjectRrn());
				long maxLineNo = 1;
				for (PurchaseOrderLine line : po.getPoLines()) {
					maxLineNo = maxLineNo < line.getLineNo() ? line.getLineNo() : maxLineNo;
				}
				poLine.setLineNo((long)Math.ceil(maxLineNo / 10) * 10 + 10);
			} else {
				poLine.setLineNo(10L);
			}
			poLine.setLineStatus(Documentation.STATUS_DRAFTED);
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return poLine;
	}
	
	public PurchaseOrder createPOFromPR(PurchaseOrder po, List<RequisitionLine> prLines, long userRrn) throws ClientException {
		try{
			List<PurchaseOrderLine> poLines = new ArrayList<PurchaseOrderLine>();
			long orgRrn = prLines.get(0).getOrgRrn();
			long startLineNo = 10;
			if (po == null) {
				po = new PurchaseOrder();
				po.setOrgRrn(orgRrn);
			} else {
				startLineNo = newPOLine(po).getLineNo();
			}
			Long vendorRrn = po.getVendorRrn();
			String purchaser = null;
			int i = 0;
			// 根据第一个prline的warehouse决定po的warehouse
			po.setWarehouseRrn(prLines.get(0).getWarehouseRrn());
			po.setWarehouseId(prLines.get(0).getWarehouseId());
			for (RequisitionLine prLine : prLines) {
				po.setRequisitionRrn(prLine.getRequisitionRrn());
				po.setRequisitionId(prLine.getRequisitionId());
				if (vendorRrn == null) {
					vendorRrn = prLine.getVendorRrn();
				} else if (!vendorRrn.equals(prLine.getVendorRrn())){
					throw new ClientException("pur.different_vendor");
				}
				purchaser = prLine.getPurchaser();
				
				PurchaseOrderLine poLine = new PurchaseOrderLine();
				poLine.setOrgRrn(po.getOrgRrn());
				poLine.setMaterialRrn(prLine.getMaterialRrn());
				poLine.setLineNo(startLineNo + i * 10);
				poLine.setLineTotal(prLine.getLineTotal());
				poLine.setQty(prLine.getQty().subtract((prLine.getQtyOrdered() == null ? BigDecimal.ZERO :  prLine.getQtyOrdered())));
				poLine.setDateEnd(prLine.getDateEnd());
				poLine.setRequisitionLineRrn(prLine.getObjectRrn());
				poLine.setUnitPrice(prLine.getUnitPrice());
				poLine.setUomId(prLine.getUomId());
				poLine.setWarehouseRrn(prLine.getWarehouseRrn());
				poLine.setWarehouseId(prLine.getWarehouseId());
				poLine.setPackageSpec(prLine.getPackageSpec());//从prline中获得包装规格
				poLine.setProductNo(prLine.getProductNo());
				Material material = em.find(Material.class, prLine.getMaterialRrn());
				poLine.setIsInspectionFree(material.getIsInspectionFree());
				poLines.add(poLine);
				i++;
			}
			po.setPurchaser(purchaser);
			po.setVendorRrn(vendorRrn);
			if(vendorRrn != null) {
				Vendor vendor = em.find(Vendor.class, vendorRrn);
				po.setDeliveryRule(vendor.getShipmentCode());
				po.setPaymentRule11(vendor.getIsIssueInvoice() ? "Y" : "N");
				if(vendor.getIsIssueInvoice()) {
					po.setInvoiceType(vendor.getInvoiceType());
					po.setVatRate(vendor.getVatRate());
				}
			}
			po.setPoLines(poLines);
			return savePO(po, userRrn);
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
	
	public PurchaseOrder savePO(PurchaseOrder po, long userRrn) throws ClientException {
		return savePOLine(po, po.getPoLines(), userRrn);
	}
	
	public PurchaseOrderLine savePOLine(PurchaseOrder po, PurchaseOrderLine poLine, long userRrn) throws ClientException {
		List<PurchaseOrderLine> poLines = new ArrayList<PurchaseOrderLine>();
		poLines.add(poLine);
		po = savePOLine(po, poLines, userRrn);
		return po.getPoLines().get(0);
	}
	
	public PurchaseOrder savePOLine(PurchaseOrder po, List<PurchaseOrderLine> poLines, long userRrn) throws ClientException {
		try{
			if (po.getObjectRrn() == null) {
				po.setIsActive(true);
				po.setCreatedBy(userRrn);
				po.setCreated(new Date());
				po.setTotalLines(0L);
				po.setTotal(BigDecimal.ZERO);
				po.setDocStatus(Documentation.STATUS_DRAFTED);
				po.setDocType(Documentation.DOCTYPE_TPO);
				
				String docId = po.getDocId();
				if (docId == null || docId.length() == 0) {
					po.setDocId(generatePoCode(po));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<PurchaseOrder> pos = adManager.getEntityList(po.getOrgRrn(), PurchaseOrder.class, 1, whereClause, "");
					if (pos.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}
				
				ADUser user = em.find(ADUser.class, userRrn);
				po.setUserCreated(user.getUserName());
				po.setDateCreated(new Date());
				em.persist(po);
			}
			
			if (po.getWarehouseRrn() != null) {
				Warehouse house = em.find(Warehouse.class, po.getWarehouseRrn());
				po.setWarehouseId(house.getWarehouseId());
			}
			for (PurchaseOrderLine poLine : poLines) {
				//poline的object_rrn等于null，是新建的保存操作；不等于null，是修改的保存操作
//				if (poLine.getObjectRrn()== null) {
					//设置参考供应商
					VendorMaterial primaryVendor = vdmManager.getPrimaryVendor(poLine.getMaterialRrn());
					if (primaryVendor != null) {
						poLine.setRefVendorRrn(primaryVendor.getVendorRrn());
					}
					//设置参考价格
					VendorMaterial currentVendor;
					Material material = new Material();
					material.setObjectRrn(poLine.getMaterialRrn());
					material = (Material) adManager.getEntity(material);
					if (primaryVendor != null && po.getVendorRrn().equals(primaryVendor.getObjectRrn())) {
						currentVendor = primaryVendor;
					} else {
						currentVendor = vdmManager.getVendorMaterial(po.getVendorRrn(), poLine.getMaterialRrn());
					}
					if(material != null){
						if(material.getLastPrice() != null){//用物料的最近采购价作为参考价
							poLine.setRefUnitPrice(material.getLastPrice());
						}else{
							if (currentVendor != null) {
								if (currentVendor.getReferencedPrice() != null) {//否则就用供应商的参考价作为参考价
									poLine.setRefUnitPrice(currentVendor.getReferencedPrice());
								} else {
									poLine.setRefUnitPrice(BigDecimal.ZERO);
								}
							}
						}
					}
					
//					if (currentVendor != null) {
//						if (currentVendor.getLastPrice() != null) {
//							poLine.setRefUnitPrice(currentVendor.getLastPrice());
//						} else {
//							poLine.setRefUnitPrice(currentVendor.getReferencedPrice());
//						}
//					}
//				}
				
				poLine.setWarehouseRrn(po.getWarehouseRrn());
				poLine.setWarehouseId(po.getWarehouseId());
//				if (poLine.getWarehouseRrn() != null) {
//					Warehouse house = em.find(Warehouse.class, poLine.getWarehouseRrn());
//					poLine.setWarehouseId(house.getWarehouseId());
//				}
				
				RequisitionLine prLine = null;
				if (poLine.getRequisitionLineRrn() != null) {
					prLine = em.find(RequisitionLine.class, poLine.getRequisitionLineRrn());
//					poLine.setQtyInventoty(poLine.getQty().multiply(prLine.getQtyInventoty().divide(prLine.getQty())));
				} else {
//					BigDecimal convert = pdmManager.getConvertOfUom(poLine.getMaterialRrn(), Uom.CONTYPE_PUR2INV);
//					poLine.setQtyInventoty(convert.multiply(poLine.getQty()));
				}
				
				if (poLine.getObjectRrn() == null) {
					poLine.setIsActive(true);
					poLine.setCreatedBy(userRrn);
					poLine.setCreated(new Date());
					poLine.setLineStatus(Documentation.STATUS_DRAFTED);
					po.setTotalLines(po.getTotalLines() + 1);
					po.setTotal(po.getTotal().add(poLine.getLineTotal()));
					if (prLine != null) {
						//修改已订购数量
						BigDecimal oldOrdered = prLine.getQtyOrdered() == null ? BigDecimal.ZERO : prLine.getQtyOrdered();
						prLine.setQtyOrdered(oldOrdered.add(poLine.getQty()));
						if (prLine.getQty().doubleValue() < prLine.getQtyOrdered().doubleValue()) {
							throw new ClientException("pur.po_large_pr");
						}
					}
				} else {
					PurchaseOrderLine oldLine = em.find(PurchaseOrderLine.class, poLine.getObjectRrn());
					po.setTotal(po.getTotal().subtract(oldLine.getLineTotal()).add(poLine.getLineTotal()));
					if (prLine != null) {
						//修改已订购数量
						BigDecimal oldOrdered = prLine.getQtyOrdered() == null ? BigDecimal.ZERO : prLine.getQtyOrdered();
						prLine.setQtyOrdered(oldOrdered.subtract(oldLine.getQty()).add(poLine.getQty()));
						if (prLine.getQty().doubleValue() < prLine.getQtyOrdered().doubleValue()) {
							throw new ClientException("pur.po_large_pr");
						}
					}
				}
				
				poLine.setUpdatedBy(userRrn);
				
				Lot batchLot = null;
				if (poLine.getObjectRrn() == null) {
					//开能才生成条码号,(开能委外不需要条码)条码:物料编号+年月日+序列号
					if((po.getOrgRrn().equals(139420L) || po.getOrgRrn().equals(41673024L) || po.getOrgRrn().equals(12644730L) || po.getOrgRrn().equals(63506125L) ) && !Constants.KEY_MATERIAL_CATEGORY2.equals(material.getMaterialCategory2())){
						String lotId = null;
						//serial类型的物料有多个批次用;拼接,其他的只有一个批次
						List<Lot> list =null;
						if(Lot.LOTTYPE_SERIAL.equals(material.getLotType())){
							 list = invManager.generateBarCodeLot(material.getOrgRrn(), material,
									BigDecimal.ONE, poLine.getQty().intValue(), userRrn);
						}else{
							 list = invManager.generateBarCodeLot(material.getOrgRrn(), material,
									poLine.getQty(), 1, userRrn);
						}

						if(list != null && list.size() > 0) {
							if(Lot.LOTTYPE_SERIAL.equals(material.getLotType())){
								StringBuffer sf = new StringBuffer();
								for(Lot lot :list){
									sf.append(lot.getLotId());
									sf.append(";");
								}
								lotId = sf.substring(0, sf.length()-1);
							}else{
								lotId = list.get(0).getLotId();
							}
						}
						poLine.setBarCode(lotId);
						//material类型的物料，去寻找lot，找到更新reserved10和po相关信息，找不到的情况下，新建一条
						//其它类型的物料新建一条lot,记录相关信息
						if(Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
							Lot lot = null;
							try {
								List<Lot> lots = adManager.getEntityList(po.getOrgRrn(), Lot.class,Integer.MAX_VALUE,"lotId = '"+material.getMaterialId()+"'",null);
								if(lots==null || lots.size()==0){
									lot = new Lot();
									lot.setIsActive(true);
									lot.setCreatedBy(userRrn);
									lot.setCreated(new Date());
									lot.setUpdatedBy(userRrn);
									lot.setOrgRrn(po.getOrgRrn());
									lot.setMaterialId(material.getMaterialId());
									lot.setMaterialName(material.getName());
									lot.setMaterialRrn(material.getObjectRrn());
									lot.setLotType(material.getLotType());
									lot.setPosition(Lot.POSITION_GEN);
									lot.setLotId(material.getMaterialId());
									lot.setQtyInitial(BigDecimal.ZERO);
									lot.setQtyCurrent(BigDecimal.ZERO);
									lot.setIsUsed(false);
									lot.setPoLineRrn(poLine.getObjectRrn());
									lot.setPoId(po.getDocId());
									lot.setPoRrn(po.getObjectRrn());
									lot.setReverseField10(lotId);
									lot.setReverseField9(po.getComments());
									em.persist(lot);
								}else{
									lot = em.find(Lot.class, lots.get(0).getObjectRrn());
									lot.setPoLineRrn(poLine.getObjectRrn());
									lot.setPoId(po.getDocId());
									lot.setPoRrn(po.getObjectRrn());
									lot.setReverseField10(lotId);
									lot.setReverseField9(po.getComments());
									em.merge(lot);
								}
							} catch(ClientException e) {
								throw new ClientException("不能生成barcode");
							}
						}else{		
							if(Lot.LOTTYPE_SERIAL.equals(material.getLotType())){
								if(list!=null && list.size() >0 ){
									for(Lot batchLot2 : list){
	//									batchLot = list.get(0);
										batchLot2.setPosition(Lot.POSITION_GEN);
										batchLot2.setReverseField10(lotId);
										batchLot2.setReverseField9(po.getComments());
										batchLot2.setPoLineRrn(poLine.getObjectRrn());
										batchLot2.setPoId(po.getDocId());
										batchLot2.setPoRrn(po.getObjectRrn());
										batchLot2.setQtyCurrent(BigDecimal.ONE);
										batchLot2.setQtyInitial(BigDecimal.ONE);
										em.persist(batchLot2);
									}
								}
							}else{
								if(list!=null && list.size() >0 ){
									batchLot = list.get(0);
									batchLot.setPosition(Lot.POSITION_GEN);
									batchLot.setReverseField10(lotId);
									batchLot.setReverseField9(po.getComments());
									batchLot.setPoLineRrn(poLine.getObjectRrn());
									batchLot.setPoId(po.getDocId());
									batchLot.setPoRrn(po.getObjectRrn());
									batchLot.setQtyCurrent(BigDecimal.ZERO);
									batchLot.setQtyInitial(BigDecimal.ZERO);
									em.persist(batchLot);
								}
							}
						}
					}
					poLine.setPoRrn(po.getObjectRrn());
					poLine.setPoId(po.getDocId());
					em.persist(poLine);
					if(batchLot!=null){
						batchLot = em.find(Lot.class, batchLot.getObjectRrn());
						batchLot.setPoLineRrn(poLine.getObjectRrn());
						em.merge(batchLot);
					}
				} else {
					poLine.setPoId(po.getDocId());
					poLine = em.merge(poLine);
				}
				if (prLine != null) {
					em.merge(prLine);
				}
			}
			po.setUpdatedBy(userRrn);
			em.merge(po);
			po.setPoLines(poLines);
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
		return po;
	}
	
	public PurchaseOrder preApprovePO(PurchaseOrder po, long userRrn) throws ClientException {
		try{
			//改变PO状态
			po.setIsPreApproved(true);
			po.setUpdatedBy(userRrn);
			ADUser user = em.find(ADUser.class, userRrn);
			po.setPreApproved(user.getUserName());
			Date now = new Date();
			po.setDatePreApproved(now);
			em.merge(po);
			for (PurchaseOrderLine poLine : po.getPoLines()) {
				if (Documentation.STATUS_DRAFTED.equals(poLine.getLineStatus())) {
					poLine.setIsPreApproved(true);
					poLine.setPreApproved(user.getUserName());
					poLine.setDatePreApproved(now);
					poLine.setWarehouseRrn(po.getWarehouseRrn());
					poLine.setWarehouseId(po.getWarehouseId());
					poLine.setUpdatedBy(userRrn);
					em.merge(poLine);
				}
			}
			em.flush();
			return em.find(po.getClass(), po.getObjectRrn());
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public PurchaseOrder approvePO(PurchaseOrder po, long userRrn) throws ClientException {
		try{
			//改变PO状态
			po.setDocStatus(Documentation.STATUS_APPROVED);
			po.setUpdatedBy(userRrn);
			ADUser user = em.find(ADUser.class, userRrn);
			po.setUserApproved(user.getUserName());
			po.setDateApproved(new Date());
			po = em.merge(po);
			
			//记录付款方式信息
			Vendor vendor = new Vendor();
			vendor.setObjectRrn(po.getVendorRrn());
			vendor = (Vendor) adManager.getEntity(vendor);
			String paymentRule1 = po.getPaymentRule1();
			String paymentRule2 = po.getPaymentRule2();
			String paymentRule3 = po.getPaymentRule3();
			String paymentRule4 = po.getPaymentRule4();
			String paymentRule5 = po.getPaymentRule5();
			String paymentRule6 = po.getPaymentRule6();
			String paymentRule7 = po.getPaymentRule7();
			String paymentRule8 = po.getPaymentRule8();
			String paymentRule9 = po.getPaymentRule9();
			String paymentRule10 = po.getPaymentRule10();
			String paymentRule11 = po.getPaymentRule11();//是否开具发票
			String paymentRule12 = po.getPaymentRule12();
			String paymentRule13 = po.getPaymentRule13();
			String paymentRule14 = po.getPaymentRule14();
			String paymentRule15 = po.getPaymentRule15();
			String isIssueInvoice = po.getPaymentRule11();//是否开具发票
			String invoiceType = po.getInvoiceType();//发票类型
			BigDecimal vatRate = po.getVatRate();//税率

			if( paymentRule1 != null && paymentRule1.trim().length() >0){
				vendor.setPaymentRule1(paymentRule1);  
			}
			if( paymentRule2 != null && paymentRule2.trim().length() >0){
				vendor.setPaymentRule2(paymentRule2);  
			}
			if( paymentRule3 != null && paymentRule3.trim().length() >0){
				vendor.setPaymentRule3(paymentRule3);  
			}
			if( paymentRule4 != null && paymentRule4.trim().length() >0){
				vendor.setPaymentRule4(paymentRule4);  
			}
			if( paymentRule5 != null && paymentRule5.trim().length() >0){
				vendor.setPaymentRule5(paymentRule5);  
			}
			if( paymentRule6 != null && paymentRule6.trim().length() >0){
				vendor.setPaymentRule6(paymentRule6);  
			}
			if( paymentRule7 != null && paymentRule7.trim().length() >0){
				vendor.setPaymentRule7(paymentRule7);  
			}
			if( paymentRule8 != null && paymentRule8.trim().length() >0){
				vendor.setPaymentRule8(paymentRule8);  
			}
			if( paymentRule9 != null && paymentRule9.trim().length() >0){
				vendor.setPaymentRule9(paymentRule9);  
			}
			if( paymentRule10 != null && paymentRule10.trim().length() >0){
				vendor.setPaymentRule10(paymentRule10);  
			}
			if( paymentRule11 != null && paymentRule11.trim().length() >0){
				vendor.setPaymentRule11(paymentRule11);  
			}
			if( paymentRule12 != null && paymentRule12.trim().length() >0){
				vendor.setPaymentRule12(paymentRule12);  
			}
			if( paymentRule13 != null && paymentRule13.trim().length() >0){
				vendor.setPaymentRule13(paymentRule13);  
			}
			if( paymentRule14 != null && paymentRule14.trim().length() >0){
				vendor.setPaymentRule14(paymentRule14);  
			}
			if( paymentRule15 != null && paymentRule15.trim().length() >0){
				vendor.setPaymentRule15(paymentRule15);  
			}
			if( isIssueInvoice != null && isIssueInvoice.trim().length() >0){
				vendor.setIsIssueInvoice2(isIssueInvoice);  
			}
			if( invoiceType != null && invoiceType.trim().length() >0){
				vendor.setInvoiceType2(invoiceType);  
			}
			if( vatRate != null){
				vendor.setVatRate2(vatRate); 
			}
			em.merge(vendor);
			
			for (PurchaseOrderLine poLine : po.getPoLines()) {
				if (Documentation.STATUS_DRAFTED.equals(poLine.getLineStatus())) {
					poLine.setLineStatus(Documentation.STATUS_APPROVED);
					poLine.setWarehouseRrn(po.getWarehouseRrn());
					poLine.setWarehouseId(po.getWarehouseId());
					poLine.setUpdatedBy(userRrn);
					em.merge(poLine);
				}
				
				if (poLine.getRequisitionLineRrn() != null) {
					RequisitionLine prLine = em.find(RequisitionLine.class, poLine.getRequisitionLineRrn());
					//如果PRLine数量已经完成，则将PRLine状态改为COMPLETED
					if (prLine.getQty().equals(prLine.getQtyOrdered())) {
						prLine.setLineStatus(Documentation.STATUS_COMPLETED);
					}
					em.merge(prLine);
					if (Documentation.STATUS_COMPLETED.equals(prLine.getLineStatus())) {
						Requisition pr = em.find(Requisition.class, prLine.getRequisitionRrn());
						boolean completeFlag = true;
						boolean closeFlag = false;
						for (RequisitionLine line : pr.getPrLines()){
							if (!Documentation.STATUS_COMPLETED.equals(line.getLineStatus()) && 
									!Documentation.STATUS_CLOSED.equals(line.getLineStatus())) {
								completeFlag = false;
								closeFlag = false;
								break;
							}
							if (Documentation.STATUS_CLOSED.equals(line.getLineStatus())) {
								closeFlag = true;
							}
						}
						if (closeFlag) {
							pr.setDocStatus(Documentation.STATUS_CLOSED);
							em.merge(pr);
						} else if (completeFlag) {
							pr.setDocStatus(Documentation.STATUS_COMPLETED);
							em.merge(pr);
						}
					}
				}
			}
			em.flush();
			
			for (PurchaseOrderLine poLine : po.getPoLines()) {
				updateVendorMaterialPrice(po.getVendorRrn(), poLine);
				Material mtrl = poLine.getMaterial();
				mtrl.setHasPurchased(true);//对应的物料是否采购过字段设为是
				mtrl.setLastPrice(poLine.getUnitPrice());//物料设置最近采购价
				em.merge(mtrl);
			}

			po = em.find(po.getClass(), po.getObjectRrn());
			
			return po;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public PurchaseOrder closePO(PurchaseOrder po, long userRrn) throws ClientException {
		try{
			po.setDocStatus(Documentation.STATUS_CLOSED);
			po.setUpdatedBy(userRrn);
			em.merge(po);
			for (PurchaseOrderLine poLine : po.getPoLines()) {
				poLine.setLineStatus(Documentation.STATUS_CLOSED);
				poLine.setUpdatedBy(userRrn);
				em.merge(poLine);
			}
			return po;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public PurchaseOrderLine closePOLine(PurchaseOrderLine poLine , long userRrn) throws ClientException {
		try{
			PurchaseOrder po = null;
			poLine.setLineStatus(Documentation.STATUS_CLOSED);
			poLine.setUpdatedBy(userRrn);
			em.merge(poLine);
			if (poLine.getPoRrn() != null) {
				po = em.find(PurchaseOrder.class, poLine.getPoRrn());
				for (PurchaseOrderLine line : po.getPoLines()) {
					if (!Documentation.STATUS_CLOSED.equals(line.getLineStatus())
							&& !Documentation.STATUS_COMPLETED.equals(line.getLineStatus())) {
						return poLine;
					}
				}
				po.setDocStatus(Documentation.STATUS_CLOSED);
				po.setUpdatedBy(userRrn);
				em.merge(po);
			}
			return poLine;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deletePO(PurchaseOrder po, long userRrn)throws ClientException {
		try{
			if(po != null && po.getObjectRrn() != null) {
				po = em.find(PurchaseOrder.class, po.getObjectRrn());
				List<PurchaseOrderLine> poLines = po.getPoLines();
				int size = poLines.size();
				for(int i = 0; i < size; i++) {
					PurchaseOrderLine poLine = poLines.get(i);
					deletePOLine(poLine, userRrn);
				}
				em.remove(po);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deletePOLine(PurchaseOrderLine poLine, long userRrn)throws ClientException {
		try {
			if(poLine != null && poLine.getObjectRrn() != null) {
				poLine = em.find(PurchaseOrderLine.class, poLine.getObjectRrn());
				// 更新PO
				if(poLine.getPoRrn() != null) {
					PurchaseOrder po = em.find(PurchaseOrder.class, poLine.getPoRrn());					
					po.setTotal(po.getTotal().subtract(poLine.getLineTotal()));
					po.setTotalLines(po.getTotalLines() - 1);
					em.merge(po);
				}
				// 若对应着采购申请行，则更新采购申请行已订购数
				if(poLine.getRequisitionLineRrn() != null) {
					RequisitionLine prLine = em.find(RequisitionLine.class, poLine.getRequisitionLineRrn());
					BigDecimal oldOrdered = prLine.getQtyOrdered() == null ? BigDecimal.ZERO : prLine.getQtyOrdered();
					prLine.setQtyOrdered(oldOrdered.subtract(poLine.getQty()));
					em.merge(prLine);
				}
				//删除barCode,对应material类型不变,Batch类型删掉该物料的批次
				if(poLine!=null&& (poLine.getOrgRrn().equals(139420L) || poLine.getOrgRrn().equals(41673024L) || poLine.getOrgRrn().equals(12644730L) || poLine.getOrgRrn().equals(63506125L) ) && !Lot.LOTTYPE_MATERIAL.equals(poLine.getLotType())){
					//serial类型、batch类型删除
					String whereClause ="";
	    			if(Lot.LOTTYPE_SERIAL.equals(poLine.getLotType())){
        				String[] spiltBarCode = poLine.getBarCode().split("\\;");
        				if(spiltBarCode.length==1){
        					StringBuffer whereLotId = new StringBuffer();
        					whereLotId.append("'");
        					whereLotId.append(poLine.getBarCode());
        					whereLotId.append("'");
        					whereClause= whereLotId.toString();
        				}else{
        					StringBuffer whereLotId = new StringBuffer();
        					for(String barCode : spiltBarCode){
        						whereLotId.append("'");
            					whereLotId.append(barCode);
            					whereLotId.append("',");
        					}
        					whereClause= whereLotId.substring(0, whereLotId.length()-1);
        				}
	    			}else{
    					StringBuffer whereLotId = new StringBuffer();
    					whereLotId.append("'");
    					whereLotId.append(poLine.getBarCode());
    					whereLotId.append("'");
    					whereClause= whereLotId.toString();
	    			}
	    			List<Lot> lots = adManager.getEntityList(poLine.getOrgRrn(), Lot.class,Integer.MAX_VALUE,"lotId in ("+whereClause+")",null);
					if(lots!=null && lots.size() >0){
						//Material类型不能删除批次,因为批次唯一,BACTH,SERIAL不唯一
						if(!Lot.LOTTYPE_MATERIAL.equals(poLine.getLotType())){
							for(Lot lot :lots){
								lot = em.find(Lot.class, lot.getObjectRrn());
								em.remove(lot);
							}
						}
					}
				}
				em.remove(poLine);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<PurchaseOrderLine> getPoLineByMaterial(long orgRrn, long materialRrn) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT PurchaseOrderLine FROM PurchaseOrderLine PurchaseOrderLine ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND materialRrn = ? "); 
		sql.append(" AND (lineStatus = '" + Documentation.STATUS_DRAFTED + "' OR lineStatus = '" +  Documentation.STATUS_APPROVED + "')" );
		sql.append(" ORDER BY dateEnd ");
		logger.debug(sql);
		Query query = em.createQuery(sql.toString());
		query.setParameter(1, orgRrn);
		query.setParameter(2, materialRrn);
		List<PurchaseOrderLine> poLineList = query.getResultList();
		return poLineList;
	}
	
	public List<RequisitionLine> getPrLineByMaterial(long orgRrn, long prRrn, long materialRrn) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT RequisitionLine FROM RequisitionLine RequisitionLine ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND requisitionRrn = ? "); 
		sql.append(" AND materialRrn = ? "); 
		sql.append(" ORDER BY dateEnd ");
		logger.debug(sql);
		Query query = em.createQuery(sql.toString());
		query.setParameter(1, orgRrn);
		query.setParameter(2, prRrn);
		query.setParameter(3, materialRrn);
		List<RequisitionLine> prLineList = query.getResultList();
		return prLineList;
	}
	
	public List<Requisition> getCanMergePr(long orgRrn) throws ClientException {
		List<Requisition> canMergePrs = new ArrayList<Requisition>();
		try {
			String whereClause = " docStatus = '" + Requisition.STATUS_DRAFTED + "'";
			List<Requisition> prs = adManager.getEntityList(orgRrn, Requisition.class, Integer.MAX_VALUE , whereClause, "");
			for (Requisition pr : prs) {
				if (pr.getMoRrn() != null) {
					ManufactureOrder mo = new ManufactureOrder();
					mo.setObjectRrn(pr.getMoRrn());
					mo = (ManufactureOrder)adManager.getEntity(mo);
					if (!ManufactureOrder.STATUS_DRAFTED.equals(mo.getDocStatus())) {
						canMergePrs.add(pr);
					}
				} else if (pr.getMpsRrn() != null) {
					boolean mergeFlag = true;
					whereClause = " mpsRrn = '" + pr.getMpsRrn() + "'";
					List<ManufactureOrder> mos = adManager.getEntityList(orgRrn, ManufactureOrder.class, 1, whereClause, "");
					for (ManufactureOrder mo : mos) {
						if (ManufactureOrder.STATUS_DRAFTED.equals(mo.getDocStatus())) {
							mergeFlag = false;
							break;
						}
					}
					if (mergeFlag) {
						canMergePrs.add(pr);
					}
				} else {
					canMergePrs.add(pr);
				}
			}
			return canMergePrs;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Requisition generatePrByMin(long orgRrn, List<RequisitionLine> prLines, long userRrn) throws ClientException {
		Requisition newPr = generatePr(orgRrn, userRrn);
		
		long totalLines = 0;
		BusinessCalendar prCalendar = basManager.getCalendarByDay(orgRrn, BusinessCalendar.CALENDAR_PURCHASE);
		VendorMaterial vendorMaterial = null;
		Material material = null;
		
		for (RequisitionLine prLine : prLines) {
			vendorMaterial = vdmManager.getPrimaryVendor(prLine.getMaterialRrn());
			prLine.setCreatedBy(userRrn);
			prLine.setCreated(new Date());
			prLine.setUpdatedBy(userRrn);
			prLine.setUnitPrice(null);
			prLine.setLineNo((totalLines + 1) * 10);
			prLine.setPurchaser(vendorMaterial.getPurchaser());//带出采购员
			totalLines++;
			
			//计算到货日期
			Date dateStart = prCalendar.findStartOfNextDay(new Date());
			prLine.setDateStart(dateStart);
			
			material = em.find(Material.class, prLine.getMaterialRrn());
			if(material == null)
				continue;
			if (material.getIsJit()) {
				prLine.setDateEnd(dateStart);	//立即到货
			} else {
				int leadTime = vendorMaterial.getLeadTime() == null ? 0 : vendorMaterial.getLeadTime().intValue();
				Date dateEnd = prCalendar.addDay(dateStart, leadTime);
				prLine.setLeadTime(new Long(leadTime));
				prLine.setDateEnd(dateEnd);
			}
		}
		
		savePRLine(newPr, prLines, false, userRrn);
		
		return newPr;
	}
	
	public Requisition mergePr(long orgRrn, List<Requisition> prs, boolean ignoreDateEnd, long userRrn) throws ClientException {
		try {
			Requisition newPr = generatePr(orgRrn, userRrn);
			StringBuffer sql = new StringBuffer(" SELECT RequisitionLine FROM RequisitionLine RequisitionLine ");
			sql.append(" WHERE ");
			sql.append(" requisitionRrn IN ( "); 
			for (Requisition pr : prs) {
				if(pr.getMpsRrn() != null){
					newPr.setMpsRrn(pr.getMpsRrn());
					newPr.setMpsId(pr.getMpsId());
				}
				sql.append("'");
				sql.append(pr.getObjectRrn());
				sql.append("', ");
			}
			sql.delete(sql.length() - 2, sql.length());
			sql.append(" )");
			Query query = em.createQuery(sql.toString());
			List<RequisitionLine> prLines = query.getResultList();
			
			Set<String> mergeKeySet = new HashSet<String>();
			long totalLines = 0;
			BigDecimal total = BigDecimal.ZERO;
			for (RequisitionLine prLine : prLines) {
				String mergeKey = "";
				if (ignoreDateEnd) {
					mergeKey = prLine.getMaterialRrn() + "_" + prLine.getVendorRrn()+ "_" + prLine.getUnitPrice();
				} else {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String dateEnd = "";
					if (prLine.getDateEnd() != null) {
						dateEnd = dateFormat.format(prLine.getDateEnd());
					}
					mergeKey = prLine.getMaterialRrn() + "_" + prLine.getVendorRrn()+ "_" + prLine.getUnitPrice() + "_" + dateEnd;
				}
				
				if (!mergeKeySet.contains(mergeKey)) {
					List<RequisitionLine> mergeLines;
					if (ignoreDateEnd) {
						mergeLines = filterMergedPrLine(prLines, prLine.getMaterialRrn(), prLine.getVendorRrn(), prLine.getUnitPrice());
					} else {
						mergeLines = filterMergedPrLine(prLines, prLine.getMaterialRrn(), prLine.getVendorRrn(), prLine.getUnitPrice(), prLine.getDateEnd());
					}
					
					String inString = "";
					BigDecimal qtyMPS = BigDecimal.ZERO;
					BigDecimal qtyTheory = BigDecimal.ZERO;
					BigDecimal qty = BigDecimal.ZERO;
					Date firstDate = null;
					for (RequisitionLine mergeLine : mergeLines) {
						if (mergeLine.getQtyMPS() != null) {
							qtyMPS = qtyMPS.add(mergeLine.getQtyMPS());
						}
						if (mergeLine.getQtyTheory() != null) {
							qtyTheory = qtyTheory.add(mergeLine.getQtyTheory());
						}
						if (mergeLine.getQty() != null) {
							qty = qty.add(mergeLine.getQty());
						}
						if (firstDate == null) {
							firstDate = mergeLine.getDateEnd();
						} else {
							if (firstDate.compareTo(mergeLine.getDateEnd()) > 0) {
								firstDate = mergeLine.getDateEnd();
							}
						}
						inString = inString + " '" + mergeLine.getObjectRrn() + "',";
					}
					inString = inString.substring(0, inString.length() - 1);
					
					RequisitionLine lastLine = this.getLastPrLine(mergeLines);//找到合并的PRLine中最后创建的那个PRLine
					RequisitionLine newLine = (RequisitionLine)lastLine.clone();
					newLine.setCreatedBy(userRrn);
					newLine.setCreated(new Date());
					newLine.setUpdatedBy(userRrn);
					
					newLine.setRequisitionRrn(newPr.getObjectRrn());
					newLine.setRequisitionId(newPr.getDocId());
					newLine.setQtyMPS(qtyMPS);
					newLine.setQtyTheory(qtyTheory);
					newLine.setQty(qty);
					newLine.setDateEnd(firstDate);
					newLine.setLineTotal(qty.multiply(newLine.getUnitPrice()));
					if (newLine.getAdvanceRatio() != null) {
						newLine.setAdvancePayment(newLine.getLineTotal().multiply(newLine.getAdvanceRatio()).divide(new BigDecimal(100)));
					}
					newLine.setLineNo((totalLines + 1) * 10);
					
					//库存、已分配数取最新PRLine,在途数需重新计算
					if (newLine.getQtyTransit() != null) {
						//新在途数=最近一次在途数+最近PR-合并的PR之和
						newLine.setQtyTransit(newLine.getQtyTransit().add(lastLine.getQty()).subtract(qty));
					}
					
					newLine.setObjectRrn(null);
					em.persist(newLine);
					
					sql = new StringBuffer(" UPDATE WIP_MO_BOM SET REQUISITION_LINE_RRN = ? WHERE REQUISITION_LINE_RRN IN(");
					sql.append(inString);
					sql.append(")");
					query = em.createNativeQuery(sql.toString());
					query.setParameter(1, newLine.getObjectRrn());
					query.executeUpdate();
					
					totalLines++;
					total = total.add(newLine.getLineTotal());
					mergeKeySet.add(mergeKey);
				}
				
			}
			newPr.setIsActive(true);
			newPr.setTotalLines(totalLines);
			newPr.setTotal(total);
			em.merge(newPr);
			
			for (Requisition pr : prs) {
				deletePR(pr, userRrn);
			}
			
			return newPr;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	private RequisitionLine getLastPrLine(List<RequisitionLine> lines) {
		RequisitionLine lastPrLine = null;
		Date lastDate = null;
		for (RequisitionLine line : lines) {
			if (lastDate == null) {
				lastDate = line.getCreated();
				lastPrLine = line;
			} else {
				if (lastDate.compareTo(line.getCreated()) < 0) {
					lastDate = line.getCreated();
					lastPrLine = line;
				} else if (lastDate.compareTo(line.getCreated()) == 0) {
					if (lastPrLine.getObjectRrn().compareTo(line.getObjectRrn()) < 0) {
						lastPrLine = line;
					}
				}
			}
		}
		return lastPrLine;
	}
	
	public Requisition generatePr(long orgRrn, long userRrn) throws ClientException {
		return generatePr(orgRrn, userRrn, null, null);
	}
	
	public Requisition generatePr(long orgRrn, long userRrn, Long mpsRrn, String mpsId) throws ClientException {
		Requisition pr = new Requisition();
		pr.setOrgRrn(orgRrn);
		pr.setIsActive(true);
		pr.setCreatedBy(userRrn);
		pr.setCreated(new Date());
		pr.setTotalLines(0L);
		pr.setTotal(BigDecimal.ZERO);
		pr.setDocStatus(Requisition.STATUS_DRAFTED);
		pr.setDocType(Requisition.DOCTYPE_TPR);
		pr.setDocId(generatePrCode(pr));
		pr.setMpsRrn(mpsRrn);
		pr.setMpsId(mpsId);
		ADUser user = em.find(ADUser.class, userRrn);
		pr.setUserCreated(user.getUserName());
		pr.setDateCreated(new Date());
		em.persist(pr);
		
		return pr;
	}
	
	private String generatePrCode(Requisition pr) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(pr.getOrgRrn(), pr.getDocType()));
		moCode.append(basManager.generateCodeSuffix(pr.getOrgRrn(), pr.getDocType(), pr.getCreated()));
		return moCode.toString();
	}
	
	private String generatePoCode(PurchaseOrder po) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(po.getOrgRrn(), po.getDocType()));
		moCode.append(basManager.generateCodeSuffix(po.getOrgRrn(), po.getDocType(), po.getCreated()));
		return moCode.toString();
	}
	
	private List<RequisitionLine> filterMergedPrLine(List<RequisitionLine> prLines, Long materialRrn, Long vendorRrn, BigDecimal unitPrice) {
		//根据物料+供应商+单价才可以合并
		List<RequisitionLine> mergePrLines = new ArrayList<RequisitionLine>();
		for (RequisitionLine prLine : prLines) {
			if (materialRrn.equals(prLine.getMaterialRrn()) 
					&& vendorRrn.equals(prLine.getVendorRrn())
					&& unitPrice.compareTo(prLine.getUnitPrice()) == 0) {
				mergePrLines.add(prLine);
			}
		}
		return mergePrLines;
	}
	
	private List<RequisitionLine> filterMergedPrLine(List<RequisitionLine> prLines, Long materialRrn, Long vendorRrn, BigDecimal unitPrice, Date dateEnd) {
		//根据物料+供应商+单价+到货日期才可以合并
		List<RequisitionLine> mergePrLines = new ArrayList<RequisitionLine>();
		for (RequisitionLine prLine : prLines) {
			if (materialRrn.equals(prLine.getMaterialRrn()) 
					&& vendorRrn.equals(prLine.getVendorRrn())
					&& unitPrice.compareTo(prLine.getUnitPrice()) == 0
					&& dateEnd.compareTo(prLine.getDateEnd()) == 0) {
				mergePrLines.add(prLine);
			}
		}
		return mergePrLines;
	}
	
	public List<ManufactureOrder> getMoListByPrLine(long orgRrn, long prLineRrn) throws ClientException {
		List<ManufactureOrder> list = null;
		StringBuffer sql = new StringBuffer("");
		sql.append(" SELECT Mo FROM ManufactureOrder Mo ");
		sql.append(" WHERE Mo.orgRrn = " + orgRrn);
		sql.append(" AND Mo.objectRrn IN (");
		sql.append(" SELECT DISTINCT(MoBom.moRrn) FROM ManufactureOrderBom MoBom ");
		sql.append(" WHERE MoBom.orgRrn = " + orgRrn);
		sql.append(" AND MoBom.requsitionLineRrn = " + prLineRrn);
		sql.append(" ) ");		
		logger.debug(sql.toString());
		
		Query query = em.createQuery(sql.toString());
		list = query.getResultList();
		return list;
	}
	
	public void updateVendorMaterialPrice(Long vendorRrn, PurchaseOrderLine poLine) throws ClientException {
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT SUM(LINE_TOTAL)/SUM(QTY) AVG_PRICE FROM PUR_PO P, PUR_PO_LINE L "); 
			sql.append("  WHERE P.OBJECT_RRN = L.PO_RRN "); 
			sql.append("	AND P.VENDOR_RRN = ? "); 
			sql.append("	AND L.MATERIAL_RRN = ? "); 
			sql.append("	AND P.DOC_STATUS IN ('APPROVED', 'COMPLETED') "); 
			
			VendorMaterial vm = vdmManager.getVendorMaterial(vendorRrn, poLine.getMaterialRrn());
			if(vm == null){
				throw new Exception("Material " + poLine.getMaterialId() + " has no Vendor!");
			}
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, vendorRrn);
			query.setParameter(2, poLine.getMaterialRrn());
			BigDecimal avgPrice = (BigDecimal)query.getSingleResult();
			vm.setAveragePrice(avgPrice);
			
			if (vm.getLowestPrice() == null || vm.getLowestPrice().compareTo(poLine.getUnitPrice()) > 0) {
				vm.setLowestPrice(poLine.getUnitPrice());
			}
			vm.setLastPrice(poLine.getUnitPrice());
			vm.setReferencedPrice(poLine.getUnitPrice());
			
			em.merge(vm);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public PurchaseOrderLine getPOLineByPRLine(RequisitionLine prLine)
			throws ClientException {
		StringBuffer sb = new StringBuffer();
		sb.append(" select PoLine from PurchaseOrderLine PoLine ");
		sb.append(" where PoLine.requisitionLineRrn = ? ");
		sb.append(" and PoLine.materialRrn = ? ");
		
		Long materialRrn = prLine.getMaterialRrn();
		Query query = em.createQuery(sb.toString());
		query.setParameter(1, prLine.getObjectRrn());
		query.setParameter(2, materialRrn);
		List rslt = query.getResultList();
		return (rslt.size() == 0) ? null : (PurchaseOrderLine)rslt.get(0);
	}
	
	@Override
	public PurchaseOrder updatePOFinancialNote(PurchaseOrder po, String note,
			long userRrn) throws ClientException {
		try {
			note = (note == null ? "" : note);
			String oldNote = (po.getFinancialNote() == null ? "" : po.getFinancialNote());
			String oldIspaymentFull="N";
			String newIspaymentFull="Y";
			if(note.equals(oldNote) && newIspaymentFull.equals(oldIspaymentFull)){
				return po;
			}else{
				StringBuffer sql = new StringBuffer();
				sql.append(" UPDATE PUR_PO po SET po.FINANCIAL_NOTE = '");
				sql.append(note);
				sql.append("' ");
				sql.append(", po.ISPAYMENT_FULL = '");
				sql.append(newIspaymentFull);
				sql.append("'");
				sql.append(" WHERE po.OBJECT_RRN = ");
				sql.append(po.getObjectRrn());
				Query query = em.createNativeQuery(sql.toString());
				query.executeUpdate();
				
				po.setFinancialNote(note);
				return po;
			}
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public PurchaseOrder cancelApprovedPO(PurchaseOrder po, long userRrn)
			throws ClientException {
		try{
			if(Documentation.STATUS_APPROVED.equals(po.getDocStatus())){
				List<PurchaseOrderLine> poLines = po.getPoLines();
				for(PurchaseOrderLine line : poLines){
					if(line.getQtyDelivered() != null && line.getQtyDelivered().compareTo(BigDecimal.ZERO) > 0){//如果收货数不为空且大于0
						throw new ClientException("pur.already_has_receipt");
					}
					if(line.getQtyIn() != null && line.getQtyIn().compareTo(BigDecimal.ZERO) > 0){//如果入库数不为空且大于0
						throw new ClientException("pur.already_has_in");
					}
					//改变订单行的状态
					line.setLineStatus(Documentation.STATUS_DRAFTED);
					em.merge(line);
				}
				//改变PO状态
				po.setDocStatus(Documentation.STATUS_DRAFTED);
				
				//改变PR状态,如果有关联PR的话
				if(po.getRequisitionRrn() != null){
					Requisition pr = em.find(Requisition.class, po.getRequisitionRrn());
					if(Documentation.STATUS_COMPLETED.equals(pr.getDocStatus())){
						pr.setDocStatus(Documentation.STATUS_APPROVED);
						em.merge(pr);
					}
				}
				
				po.setUpdated(new Date());
				po.setUpdatedBy(userRrn);
				em.merge(po);
				return em.find(po.getClass(), po.getObjectRrn());
			}
			return po;
		}catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
}

	@Override
	public PurchaseOrder cancelPreApprovedPO(PurchaseOrder po, long userRrn)
			throws ClientException {
		try{
			if(Documentation.STATUS_DRAFTED.equals(po.getDocStatus())){
				po.setIsPreApproved(false);
				po.setUpdated(new Date());
				po.setUpdatedBy(userRrn);
				em.merge(po);
				return em.find(po.getClass(), po.getObjectRrn());
			}
			return po;
		}catch(Exception e){
			throw new ClientException(e);
		}
	}
	@Override
	public List<Lot> getMerchandiseLots (String lotId, long orgRrn) throws ClientException  {
		try {
			List<Lot> lots = null;
			StringBuffer sql = new StringBuffer();
			sql.append("select * from wip_lot lot ");
			sql.append("where lot.object_rrn in (");
				sql.append("select t.lot_rrn from inv_movement_line_lot  t ");
				sql.append("where  exists (");
					sql.append("select null from (");
					sql.append("select wlc.lot_parent_id ,level,connect_by_isleaf from wip_lot_component wlc ");
					sql.append("where connect_by_isleaf ='1' and " + ADBase.SQL_BASE_CONDITION + " start with wlc.lot_child_id= '"+ lotId + "'" );
					sql.append(" connect by prior  wlc.lot_parent_id = wlc.lot_child_id) aaa where t.lot_id = aaa.lot_parent_id");
				sql.append(")");
			sql.append(") and lot.is_active = 'Y' ");
			Query query = em.createNativeQuery(sql.toString(), Lot.class);
			query.setParameter(1, new Long(orgRrn));
			lots = query.getResultList();
			return lots;
		} catch (Exception e) {
			throw new ClientException(e);
		}
		
		
	} 
	@Override
	public BigDecimal getPoLineByMaxObject(long orgRrn, long materialRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();		
		sql.append(" select max(t.object_rrn) from PUR_PO_LINE t ");			
		sql.append(" where t.org_rrn = ? ");
		sql.append(" and t.material_rrn = ? ");
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter(1, orgRrn);
		query.setParameter(2, materialRrn);
		Object rslt = query.getSingleResult();
		BigDecimal objrrn = (BigDecimal)rslt;
		return objrrn;
	}
	
	@Override
	public String queryVendorPurGoal(Map<String, String> params, long orgRrn, String whereClause){
		if(whereClause == null || whereClause.trim().length() == 0){
			whereClause = " 1 = 1 ";
		}
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT * FROM ( "); 
		sql.append(" SELECT DISTINCT V.OBJECT_RRN VENDOR_RRN,"); 
		sql.append("        V.VENDOR_ID,"); 
		sql.append("        NVL(VT.PO_YEAR_TARGET,0) PO_YEAR_TARGET,"); 
		sql.append("        ROUND(NVL(VT.PO_YEAR_TARGET,0)/12,2) YEAR_AVE_AMOUNT,"); 
		sql.append("        V.COMPANY_NAME,"); 
		sql.append("        REPLACE(WMSYS.WM_CONCAT(DISTINCT P.PURCHASER) OVER(PARTITION BY TO_CHAR(NVL(P.DATE_APPROVED,P.CREATED), 'yyyy'),P.VENDOR_RRN),',','+') PURCHASER,"); 
		sql.append("        SUM(L.UNIT_PRICE * CASE"); 
		sql.append("              WHEN L.LINE_STATUS = 'CLOSED' THEN"); 
		sql.append("               NVL(L.QTY_IN, 0)"); 
		sql.append("              WHEN L.LINE_STATUS = 'DRAFTED' THEN"); 
		sql.append("               NVL(L.QTY, 0)"); 
		sql.append("              WHEN L.LINE_STATUS = 'APPROVED' THEN"); 
		sql.append("               NVL(L.QTY, 0)"); 
		sql.append("              WHEN L.LINE_STATUS = 'COMPLETED' THEN"); 
		sql.append("               NVL(L.QTY_IN, 0)"); 
		sql.append("            END) OVER(PARTITION BY P.VENDOR_RRN,TO_CHAR(NVL(P.DATE_APPROVED,P.CREATED), 'yyyy')) REAL_YEAR_AMOUNT,"); 
		sql.append("        TO_CHAR(NVL(P.DATE_APPROVED,P.CREATED), 'yyyy') YEAR,"); 
		
		sql.append("        SUM(L.UNIT_PRICE * CASE WHEN TO_CHAR(P.DATE_APPROVED, 'yyyy-MM') = to_char(sysdate, 'yyyy-MM') THEN "); 
		sql.append("              CASE WHEN L.LINE_STATUS = 'CLOSED' THEN"); 
		sql.append("               NVL(L.QTY_IN, 0)"); 
		sql.append("              WHEN L.LINE_STATUS = 'DRAFTED' THEN"); 
		sql.append("               0"); 
		sql.append("              WHEN L.LINE_STATUS = 'APPROVED' THEN"); 
		sql.append("               NVL(L.QTY, 0)"); 
		sql.append("              WHEN L.LINE_STATUS = 'COMPLETED' THEN"); 
		sql.append("               NVL(L.QTY_IN, 0)"); 
		sql.append("            END ELSE 0 END) OVER(PARTITION BY TO_CHAR(NVL(P.DATE_APPROVED,P.CREATED), 'yyyy'),P.VENDOR_RRN) REAL_MONTH_AMOUNT "); 
		sql.append(" FROM (SELECT * FROM PUR_PO WHERE " + ADBase.SQL_BASE_CONDITION + ") P, PUR_PO_LINE L, VDM_VENDOR V, VDM_YEAR_TARGET VT"); 
		sql.append(" WHERE " + whereClause); 
		if(params.containsKey("VENDOR_RRN")){
			sql.append("       AND P.VENDOR_RRN = '" + params.get("VENDOR_RRN") + "' "); 
		}

		if(params.containsKey("PURCHASER")){
			try {
				List<BigDecimal> vrrnlist = getVendorByPurchaser(orgRrn, params.get("PURCHASER"));
				if(vrrnlist.size() == 0){
					sql.append(" AND 1 != 1 ");
				}else{
					sql.append(" AND ( ");
					int i=0;
					for(BigDecimal vrrn : vrrnlist){
						if((i++)>0){
							sql.append(" OR ");
						}
						sql.append(" P.VENDOR_RRN = ");
						sql.append(" '"+vrrn+"' ");
					}
					sql.append(" ) ");
				}
			} catch (Exception e) {
				sql.append(" AND 1 != 1 ");
			}
		}

		if(params.containsKey("YEAR")){
			sql.append("       AND TO_CHAR(P.DATE_APPROVED, 'yyyy') = '" + params.get("YEAR") + "' "); 
		}

		sql.append("           AND L.PO_RRN = P.OBJECT_RRN"); 
		sql.append("           AND V.OBJECT_RRN = P.VENDOR_RRN"); 
		
		String joinSymbol = "(+)";
		if(params.containsKey("HAVE_TARGET")){
			if("Y".equals(params.get("HAVE_TARGET"))){
				joinSymbol = "";
			}
		}
		
		sql.append("           AND P.VENDOR_RRN = VT.VENDOR_RRN"+joinSymbol); 
		sql.append("           AND TO_CHAR(P.DATE_APPROVED, 'yyyy') = VT.TARGET_YEAR"+joinSymbol); 
		sql.append("         ORDER BY V.VENDOR_ID) T ");


		Query query = em.createNativeQuery(sql.toString());
		query.setParameter(1, orgRrn);
		
		List result = query.getResultList();
		
		StringBuffer sb = new StringBuffer();
		String[] columnNames = new String[]{"VENDOR_RRN","VENDOR_ID","PO_YEAR_TARGET","YEAR_AVE_AMOUNT","COMPANY_NAME","PURCHASER","REAL_YEAR_AMOUNT","YEAR","REAL_MONTH_AMOUNT","MONTH"};
		if(result != null){
			int i=0;
			for(Object obj : result){
				if((i++)>0){
					sb.append("&");
				}
				if(obj instanceof Object[]){
					String s = DataFmtUtil.array2String((Object[]) obj, columnNames);
					sb.append(s);
				}
			}
			
		}
		return sb.toString();
	}
	
	@Override
	public List<BigDecimal> getVendorByPurchaser(long orgRrn, String purchaser) throws ClientException {
		StringBuffer sql = new StringBuffer();		
		sql.append(" SELECT DISTINCT T.VENDOR_RRN FROM VDM_VENDOR_MATERIAL T ");			
		sql.append(" WHERE T.ORG_RRN = ? ");
		sql.append(" AND UPPER(T.PURCHASER) = UPPER(?) ");
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter(1, orgRrn);
		query.setParameter(2, purchaser);
		List<BigDecimal> vrrnlist = query.getResultList();
		return vrrnlist;
	}
	
	public long getAlarmReceiptCount(long orgRrn,String purchaser) throws ClientException {
		try{
		StringBuffer sql = new StringBuffer(" SELECT COUNT(*) FROM INV_RECEIPT Receipt ");
		sql.append(" WHERE ");
		sql.append(" is_Active = 'Y' AND (org_Rrn = ? OR org_Rrn = 0) ");
		sql.append(" AND doc_Status IN ('APPROVED','COMPLETED')  ");
		sql.append(" AND Receipt.po_Rrn in ( ");
		sql.append(" SELECT PO.object_Rrn FROM PUR_PO PO where  ");
		sql.append(" is_Active = 'Y' AND (org_Rrn = ? OR org_Rrn = 0) ");
		sql.append(" AND doc_Status = 'APPROVED' ");
		sql.append(" AND created >= ADD_MONTHS(sysdate, -12) ");
		sql.append(" AND created <= sysdate ");
		sql.append(" AND purchaser = ? ) ");
		
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter(1, Long.valueOf(orgRrn));
		query.setParameter(2, Long.valueOf(orgRrn));
		query.setParameter(3, purchaser);
		
		return ((BigDecimal) query.getSingleResult()).longValue();
		
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	public List<VPoAlarmReceiptLine> getAlarmReceipts(long orgRrn,String purchaser) throws ClientException {
		try{
		StringBuffer sql = new StringBuffer(" select receiptline2.*,m.material_id,m.name material_name,  ");
		sql.append(" poline2.qty po_line_qty,poline2.po_id,vd.vendor_id,vd.company_name vendor_name ");
		sql.append(" from (select * from  inv_receipt_line receiptline where ");
		sql.append(" receiptline.po_line_rrn in (   ");
		sql.append(" SELECT poline.object_rrn FROM v_po_alarm_poline poline  ");
		sql.append("  where poline.purchaser = ? ");
		sql.append(" )) receiptline2,  ");
		sql.append(" pdm_material m,pur_po_line poline2,vdm_vendor vd  ");
		sql.append(" where receiptline2.material_rrn =   m.object_rrn ");
		sql.append(" and receiptline2.po_line_rrn = poline2.object_rrn  ");
		sql.append(" and poline2.ref_vendor_rrn = vd.object_rrn(+)  ");
		sql.append(" order by receiptline2.receipt_id desc   ");
		
		Query query = this.em.createNativeQuery(sql.toString(),VPoAlarmReceiptLine.class);
		query.setParameter(1, purchaser);
		List<VPoAlarmReceiptLine> result = query.getResultList();
		return result;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	public long getAlarmIqcCount(long orgRrn,String purchaser) throws ClientException {
		try{
		StringBuffer sql = new StringBuffer(" SELECT COUNT(*) FROM INV_Iqc Iqc ");
		sql.append(" WHERE ");
		sql.append(" is_Active = 'Y' AND (org_Rrn = ? OR org_Rrn = 0) ");
		sql.append(" AND doc_Status IN ('APPROVED','COMPLETED')  ");
		sql.append(" AND Iqc.po_Rrn in ( ");
		sql.append(" SELECT PO.object_Rrn FROM PUR_PO PO where  ");
		sql.append(" is_Active = 'Y' AND (org_Rrn = ? OR org_Rrn = 0) ");
		sql.append(" AND doc_Status = 'APPROVED' ");
		sql.append(" AND created >= ADD_MONTHS(sysdate, -12) ");
		sql.append(" AND created <= sysdate ");
		sql.append(" AND purchaser = ? ) ");
		
		Query query = this.em.createNativeQuery(sql.toString());
		query.setParameter(1, Long.valueOf(orgRrn));
		query.setParameter(2, Long.valueOf(orgRrn));
		query.setParameter(3, purchaser);
		return ((BigDecimal) query.getSingleResult()).longValue();
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	public List<VPoAlarmIqcLine> getAlarmIqcs(long orgRrn,String purchaser) throws ClientException {
		try{
		StringBuffer sql = new StringBuffer(" select iqcline2.*,m.material_id,m.name material_name, ");
		sql.append(" poline2.qty po_line_qty,poline2.po_id,receiptline.qty_receipt receipt_Line_Qty,vd.vendor_id,vd.company_name vendor_name");
		sql.append(" from (select * from  inv_iqc_line iqcline where  ");
		sql.append(" iqcline.po_line_rrn in (  ");
		sql.append(" SELECT poline.object_rrn FROM v_po_alarm_poline poline  ");
		sql.append("   where poline.purchaser = ?  ");
		sql.append(" )) iqcline2,  ");
		sql.append(" pdm_material m,pur_po_line poline2,inv_receipt_line receiptline,vdm_vendor vd  ");
		sql.append(" where iqcline2.material_rrn =   m.object_rrn ");
		sql.append(" and iqcline2.po_line_rrn = poline2.object_rrn ");
		sql.append(" and iqcline2.receipt_line_rrn = receiptline.object_rrn ");
		sql.append(" and poline2.ref_vendor_rrn = vd.object_rrn ");
		sql.append(" order by iqcline2.iqc_Id desc  ");
 
		Query query = this.em.createNativeQuery(sql.toString(),VPoAlarmIqcLine.class);
		query.setParameter(1, purchaser);
		List<VPoAlarmIqcLine> results = query.getResultList();			
			
		return results;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	public long getAlarmInvCount(long orgRrn,String purchaser) throws ClientException {
		try{
		StringBuffer sql = new StringBuffer(" SELECT COUNT(*) FROM inv_movement MovementIn ");
		sql.append(" WHERE ");
		sql.append(" is_Active = 'Y' AND (org_Rrn = ? OR org_Rrn = 0) ");
		sql.append(" AND doc_Status IN ('APPROVED','COMPLETED') AND  doc_Type='PIN'  ");
		sql.append(" AND MovementIn.po_Rrn in ( ");
		sql.append(" SELECT PO.object_Rrn FROM PUR_PO PO where  ");
		sql.append(" is_Active = 'Y' AND (org_Rrn = ? OR org_Rrn = 0) ");
		sql.append(" AND doc_Status = 'APPROVED' ");
		sql.append(" AND created >= ADD_MONTHS(sysdate, -12) ");
		sql.append(" AND created <= sysdate ");
		sql.append(" AND purchaser = ? ) ");
 
		Query query = this.em.createNativeQuery(sql.toString());
		query.setParameter(1, Long.valueOf(orgRrn));
		query.setParameter(2, Long.valueOf(orgRrn));
		query.setParameter(3, purchaser);
		return ((BigDecimal) query.getSingleResult()).longValue();
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	public List<VPoAlarmMovenetLine> getAlarmInvs(long orgRrn,String purchaser) throws ClientException {
		try{
		StringBuffer sql = new StringBuffer(" SELECT invline2.*, ");
		sql.append(" poline2.po_id, ");
		sql.append(" poline2.qty PO_LINE_QTY, ");
		sql.append(" poline2.qty_delivered PO_LINE_QTY_DELIVERED, ");
		sql.append(" poline2.qty_in PO_LINE_QTY_IN, ");
		sql.append(" poline2.qty_rejected PO_LINE_QTY_REJECTED, ");
		sql.append(" poline2.qty_tested PO_LINE_QTY_TESTED, ");
		sql.append(" poline2.qty_qualified PO_LINE_QTY_QUALIFIED, ");
		sql.append(" poline2.created_by PO_LINE_CREATE_BY,vd.vendor_id new_vendor_id,vd.company_name new_vendor_name ");
		sql.append(" FROM ( ");
		sql.append(" SELECT * FROM INV_MOVEMENT_LINE invline ");
		sql.append(" where invline.po_line_rrn in ( ");
		sql.append(" SELECT poline.object_rrn FROM v_po_alarm_poline poline ");
		sql.append(" where poline.purchaser = ? ");
		sql.append(" ) ");
		sql.append(" and invline.line_status in ('APPROVED','COMPLETED') ");
		sql.append(" )invline2,pur_po_line poline2 ,vdm_vendor vd  ");
		sql.append(" where invline2.po_line_rrn = poline2.object_rrn ");
		sql.append(" and poline2.ref_vendor_rrn = vd.object_rrn ");
		Query query = this.em.createNativeQuery(sql.toString(),VPoAlarmMovenetLine.class);
		query.setParameter(1, purchaser);
		List<VPoAlarmMovenetLine> results = query.getResultList();
		return results;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	/**
	 * 备件审核采购订单
	 * 将预审核和审核功能合二为一
	 * */
	public PurchaseOrder sparesApprovePO(PurchaseOrder po, long userRrn) throws ClientException {
		try{
			//改变PO状态
			po.setIsPreApproved(true);
			po.setUpdatedBy(userRrn);
			ADUser user = em.find(ADUser.class, userRrn);
			po.setPreApproved(user.getUserName());
			Date now = new Date();
			po.setDatePreApproved(now);
			em.merge(po);
			for (PurchaseOrderLine poLine : po.getPoLines()) {
				if (Documentation.STATUS_DRAFTED.equals(poLine.getLineStatus())) {
					poLine.setIsPreApproved(true);
					poLine.setPreApproved(user.getUserName());
					poLine.setDatePreApproved(now);
					poLine.setWarehouseRrn(po.getWarehouseRrn());
					poLine.setWarehouseId(po.getWarehouseId());
					poLine.setUpdatedBy(userRrn);
					em.merge(poLine);
				}
			}
//			em.flush();
			return approvePO(po, userRrn);
//			return em.find(po.getClass(), po.getObjectRrn());
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/**
	 * 行政从领用计划申请到创建采购订单
	 * */
	public PurchaseOrder createPOFromPRXZ(PurchaseOrder po, List<RequisitionLine> prLines, long userRrn) throws ClientException {
		try{
			List<PurchaseOrderLine> poLines = new ArrayList<PurchaseOrderLine>();
			long orgRrn = prLines.get(0).getOrgRrn();
			long startLineNo = 10;
			if (po == null) {
				po = new PurchaseOrder();
				po.setOrgRrn(orgRrn);
			} else {
				startLineNo = newPOLine(po).getLineNo();
			}
			Long vendorRrn = po.getVendorRrn();
			String purchaser = null;
			int i = 0;
			// 根据第一个prline的warehouse决定po的warehouse
			po.setWarehouseRrn(prLines.get(0).getWarehouseRrn());
			po.setWarehouseId(prLines.get(0).getWarehouseId());
			for (RequisitionLine prLine : prLines) {
				po.setRequisitionRrn(prLine.getRequisitionRrn());
				po.setRequisitionId(prLine.getRequisitionId());
				if (vendorRrn == null) {
					vendorRrn = prLine.getVendorRrn();
				} else if (!vendorRrn.equals(prLine.getVendorRrn())){
					throw new ClientException("pur.different_vendor");
				}
				purchaser = prLine.getPurchaser();
				
				PurchaseOrderLine poLine = new PurchaseOrderLine();
				poLine.setOrgRrn(po.getOrgRrn());
				poLine.setMaterialRrn(prLine.getMaterialRrn());
				poLine.setLineNo(startLineNo + i * 10);
				poLine.setLineTotal(prLine.getLineTotal());
				poLine.setQty(prLine.getQty().subtract((prLine.getQtyOrdered() == null ? BigDecimal.ZERO :  prLine.getQtyOrdered())));
				poLine.setDateEnd(prLine.getDateEnd());
				poLine.setRequisitionLineRrn(prLine.getObjectRrn());
				poLine.setUnitPrice(prLine.getUnitPrice());
				poLine.setUomId(prLine.getUomId());
				poLine.setWarehouseRrn(prLine.getWarehouseRrn());
				poLine.setWarehouseId(prLine.getWarehouseId());
				poLine.setPackageSpec(prLine.getPackageSpec());//从prline中获得包装规格
				poLine.setXzUserRrn(prLine.getXzUserRrn());//行政用户rrn
				poLine.setXzUserName(prLine.getXzUserName());//行政用户名
				poLine.setXzDepartment(prLine.getXzDepartment());//行政部门
				poLine.setXzCompany(prLine.getXzCompany());//行政公司
				poLines.add(poLine);
				i++;
			}
			po.setPurchaser(purchaser);
			po.setVendorRrn(vendorRrn);
			if(vendorRrn != null) {
				Vendor vendor = em.find(Vendor.class, vendorRrn);
				po.setDeliveryRule(vendor.getShipmentCode());
				po.setPaymentRule11(vendor.getIsIssueInvoice() ? "Y" : "N");
				if(vendor.getIsIssueInvoice()) {
					po.setInvoiceType(vendor.getInvoiceType());
					po.setVatRate(vendor.getVatRate());
				}
			}
			po.setPoLines(poLines);
			return savePO(po, userRrn);
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
	
	/**
	 * 行政从领用计划申请到创建采购订单
	 * 一次扫描所有Approved并且做过生成采购订单操作的领用计划，统一进行处理
	 * 满足的不生成采购订单，不满足的先按供应商分在按公司分成不同的采购订单
	 * */
	public void generatePOXZ(long orgRrn,long userRrn,String whereClause) throws ClientException{
		try{
			List<Requisition>  requisitions = adManager.getEntityList(orgRrn, Requisition.class,Integer.MAX_VALUE,whereClause,"created asc");
			HashMap<Long,Storage> storageMap = new LinkedHashMap<Long,Storage>();//统计所有需求数量<仓库编号,仓库类>
			LinkedHashMap<Long, ArrayList<RequisitionLine>> verndorMap = new LinkedHashMap<Long, ArrayList<RequisitionLine>>();//所有供应商
			for(Requisition requisition : requisitions){
				requisition = (Requisition) adManager.getEntity(requisition);
				for(RequisitionLine requisitionLine : requisition.getPrLines()){
					Warehouse warehouse = invManager.getDefaultWarehouse(orgRrn);
					Storage storage = invManager.getMaterialStorage(orgRrn, requisitionLine.getMaterialRrn(),warehouse.getObjectRrn() ,userRrn);
					if(storage!=null && storageMap.get(storage.getObjectRrn())!=null){
						storage = storageMap.get(storage.getObjectRrn());
					}else{
						storageMap.put(storage.getObjectRrn(), storage);
					}
					BigDecimal qtyOnhand = storage.getQtyOnhand();
					if(qtyOnhand.compareTo(BigDecimal.ZERO)<=0){
						//库存不足//库存小于申请数
						storage.setQtyOnhand(BigDecimal.ZERO);
						RequisitionLine reqline = (RequisitionLine) requisitionLine.clone();
						reqline.setObjectRrn(null);
						reqline.setQty(requisitionLine.getQty().subtract(qtyOnhand));
						//供应商在HashMap中不存在则新建立
						if(verndorMap.get(requisitionLine.getVendorRrn())!=null){
							List<RequisitionLine> reqLines = verndorMap.get(requisitionLine.getVendorRrn());
							reqLines.add(reqline);
						}else{
							ArrayList<RequisitionLine> reqLines = new ArrayList<RequisitionLine>();
							reqLines.add(reqline);
							verndorMap.put(requisitionLine.getVendorRrn(), reqLines);
						}
					}else if(qtyOnhand.compareTo(BigDecimal.ZERO)==1){
						if(qtyOnhand.subtract(requisitionLine.getQty()).compareTo(BigDecimal.ZERO) >=0){
							//库存大于等于申请数
							storage.setQtyOnhand(qtyOnhand.subtract(requisitionLine.getQty()));
						}else{
							//库存小于申请数
							storage.setQtyOnhand(BigDecimal.ZERO);
							RequisitionLine reqline = (RequisitionLine) requisitionLine.clone();
							reqline.setObjectRrn(null);
							reqline.setQty(requisitionLine.getQty().subtract(qtyOnhand));
							//供应商在HashMap中不存在则新建立
							if(verndorMap.get(requisitionLine.getVendorRrn())!=null){
								List<RequisitionLine> reqLines = verndorMap.get(requisitionLine.getVendorRrn());
								reqLines.add(reqline);
							}else{
								ArrayList<RequisitionLine> reqLines = new ArrayList<RequisitionLine>();
								reqLines.add(reqline);
								verndorMap.put(requisitionLine.getVendorRrn(), reqLines);
							}
						}
					}
					
				}
				requisition.setMpsId("Y"); //已经统计采购不在统计
				em.merge(requisition);
			}
			
			Iterator iteVer = verndorMap.keySet().iterator();//遍历供应商
			while (iteVer.hasNext()) {
				Long vendorRrn = (Long) iteVer.next();
				ArrayList<RequisitionLine> reqLines = verndorMap.get(vendorRrn);
				if(reqLines!=null && reqLines.size() > 0 ){
					//按公司开采购单
					LinkedHashMap<String, ArrayList<RequisitionLine>> companyMap = new LinkedHashMap<String, ArrayList<RequisitionLine>>();
					for(RequisitionLine reqLine : reqLines){
						if(companyMap.get(reqLine.getXzCompany())!=null){
							List<RequisitionLine> comReqLines = companyMap.get(reqLine.getXzCompany());
							comReqLines.add(reqLine);
						}else{
							ArrayList<RequisitionLine> comReqLines = new ArrayList<RequisitionLine>();
							comReqLines.add(reqLine);
							companyMap.put(reqLine.getXzCompany(), comReqLines);
						}
					}
					Iterator iteCompany = companyMap.keySet().iterator();//遍历供应商下面的公司
					while (iteCompany.hasNext()) {
						String company = (String) iteCompany.next();
						ArrayList<RequisitionLine> comReqLines = companyMap.get(company);
						PurchaseOrder po = new PurchaseOrder();
						po.setOrgRrn(orgRrn);
						po.setIsActive(true);
						po.setCreated(new Date());
						po.setVendorRrn(vendorRrn);
						po.setDescription(company);//占用字段记录公司名称
						createPOFromPRXZ(po, comReqLines, userRrn);
					}
				}
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
	/**
	 * 行政领用计划取消审核
	 * */
	public void unApprovePR(Requisition pr, long userRrn) throws ClientException {
		try{
			pr.setDocStatus(Documentation.STATUS_DRAFTED);
			pr.setUpdatedBy(userRrn);
			ADUser user = em.find(ADUser.class, userRrn);
			pr.setUserApproved(user.getUserName());
			pr.setDateApproved(new Date());
			em.merge(pr);
			for (RequisitionLine prLine : pr.getPrLines()) {
				prLine.setLineStatus(Documentation.STATUS_DRAFTED);
				prLine.setUpdatedBy(userRrn);
				em.merge(prLine);
			}
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/**
	 * 行政-仓库库存查询-生成领用单按钮
	 * 一条领料计划单对应一条出库单
	 * */
	public void generateMovementOutXZ(long orgRrn,long userRrn,String whereClause) throws ClientException{
		try{
			List<Requisition>  requisitions = adManager.getEntityList(orgRrn, 
					Requisition.class,Integer.MAX_VALUE,
					whereClause,"created asc");
			HashMap<Long,Storage> storageMap = new LinkedHashMap<Long,Storage>();//统计所有需求数量
			for(Requisition requisition : requisitions){
				boolean isGenFlag =true;//如果有一个物料库存不足则不生成
				
				requisition = (Requisition) adManager.getEntity(requisition);
				/*2013-12-10刘蕙需求修改:取消逻辑：每条领料计划单所有物料必须全部满足，才能生成出库领料单
				 * 库存不必满足，审核时才判断库存
				for(RequisitionLine requisitionLine : requisition.getPrLines()){
					Warehouse warehouse = invManager.getDefaultWarehouse(orgRrn);
					Storage storage = invManager.getMaterialStorage(orgRrn, requisitionLine.getMaterialRrn(), warehouse.getObjectRrn(), userRrn);
					if(storage!=null && storageMap.get(storage.getObjectRrn())!=null){
						storage = storageMap.get(storage.getObjectRrn());
					}else{
						storageMap.put(storage.getObjectRrn(), storage);
					}
					BigDecimal qtyOnhand = storage.getQtyOnhand();
					if(qtyOnhand.compareTo(BigDecimal.ZERO)<=0){
						//库存不足//库存小于申请数
						isGenFlag= false;
//						break;
 
					}else if(qtyOnhand.compareTo(BigDecimal.ZERO)==1){
						if(qtyOnhand.subtract(requisitionLine.getQty()).compareTo(BigDecimal.ZERO) >=0){
							//库存大于等于申请数
							storage.setQtyOnhand(qtyOnhand.subtract(requisitionLine.getQty()));
						}else{
							//库存小于申请数
							storage.setQtyOnhand(BigDecimal.ZERO);
							isGenFlag= false;
//							break;
						}
					}
					
				}
				*/
				if(isGenFlag){
					MovementOut movementOut = new MovementOut();
					movementOut.setOrgRrn(orgRrn);
					movementOut.setCreatedBy(userRrn);
					movementOut.setDocStatus(MovementOut.STATUS_DRAFTED);
					Warehouse warehouse = invManager.getDefaultWarehouse(orgRrn);
					ADUser user  = em.find(ADUser.class, userRrn);
					movementOut.setWarehouseId(warehouse.getWarehouseId());
					movementOut.setWarehouseRrn(warehouse.getObjectRrn());
					movementOut.setUserCreated(user.getUserName());
					movementOut.setDocType(MovementOut.DOCTYPE_OOU);
					movementOut.setTotalLines(1L);
					movementOut.setDateCreated(new Date());
					movementOut.setOutType("领用");
					
					movementOut.setIqcId(requisition.getDocId());
					movementOut.setIqcRrn(requisition.getObjectRrn());
					movementOut.setLinkMan(requisition.getRequisitionUserId());//申请人
					
					List<MovementLine>  movementLines = new ArrayList<MovementLine>();
					for(RequisitionLine requestLine : requisition.getPrLines()){
						MovementLine movementLine = new MovementLine();
						movementLine.setOrgRrn(orgRrn);
						movementLine.setIsActive(true);
						movementLine.setCreated(new Date());
						movementLine.setCreatedBy(userRrn);
						movementLine.setUpdated(new Date());
						movementLine.setUpdatedBy(userRrn);
						movementLine.setEquipmentRrn(requestLine.getObjectRrn());//采购申请Rrn
						movementLine.setEquipmentId(requestLine.getRequisitionId());//采购申请ID
						movementLine.setMaterialId(requestLine.getMaterialId());
						movementLine.setMaterialRrn(requestLine.getMaterialRrn());
						movementLine.setMaterialName(requestLine.getMaterialName());
						movementLine.setQtyMovement(requestLine.getQty());
						movementLine.setUomId(requestLine.getUomId());
						movementLine.setLineNo(requestLine.getLineNo());
						movementLine.setLineStatus(Movement.STATUS_DRAFTED);
						movementLine.setLotType(requestLine.getLotType());
						movementLine.setLocatorId(MovementOut.DOCTYPE_OOU);//该字段用于记录备件ERP、行政ERP的出库类型
						movementLine.setXzUserRrn(requestLine.getXzUserRrn());
						movementLine.setXzUserName(requestLine.getXzUserName());
						movementLine.setXzDepartment(requestLine.getXzDepartment());
						movementLine.setXzCompany(requestLine.getXzCompany());
						movementLines.add(movementLine);
					}
					invManager.saveMovementOutLine(movementOut, movementLines, MovementOut.OutType.OOU, userRrn);
					requisition.setDescription("Y");//已经生成领用单据
					em.merge(requisition);
				}
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	/**
	 * 行政保存领用计划
	 * */
	public RequisitionLine saveXZPRLine(RequisitionLine prLine, long userRrn) throws ClientException {
		Requisition pr = null;
		if (prLine.getRequisitionRrn() != null) {
			pr = em.find(Requisition.class, prLine.getRequisitionRrn());
			pr.setPrLines(new ArrayList<RequisitionLine>());
		}
		if (pr == null) {
			pr = new Requisition();
			pr.setOrgRrn(prLine.getOrgRrn());
		}
		List<RequisitionLine> prLines = new ArrayList<RequisitionLine>();
		prLines.add(prLine);
		pr = saveXZPRLine(pr, prLines, false, userRrn);
		return pr.getPrLines().get(0);
	}
	/**
	 * 行政保存领用计划
	 * */
	public Requisition saveXZPRLine(Requisition pr, List<RequisitionLine> prLines , boolean batchFlag, long userRrn) throws ClientException {
		try {
			if (pr.getObjectRrn() == null) {
				pr.setIsActive(true);
				pr.setCreatedBy(userRrn);
				pr.setCreated(new Date());
				pr.setTotalLines(0L);
				pr.setTotal(BigDecimal.ZERO);
				pr.setDocStatus(Documentation.STATUS_DRAFTED);
				pr.setDocType(Documentation.DOCTYPE_TPR);
				
				String docId = pr.getDocId();
				if (docId == null || docId.length() == 0) {
					pr.setDocId(generatePrCode(pr));
				} else {
					String whereClause = " docId = '" + docId + "'";
					List<Requisition> prs = adManager.getEntityList(pr.getOrgRrn(), Requisition.class, 1, whereClause, "");
					if (prs.size() > 0) {
						throw new ClientParameterException("error.object_duplicate", docId);
					}
				}

				ADUser user = em.find(ADUser.class, userRrn);
				pr.setUserCreated(user.getUserName());
				pr.setDateCreated(new Date());
				pr.setRequisitionUserId(user.getDescription());//记录人姓名
				em.persist(pr);
			} else if (batchFlag) {
				pr = em.find(Requisition.class, pr.getObjectRrn());
			}
			
			for (RequisitionLine prLine : prLines) {
				//设置参考供应商
				VendorMaterial primaryVendor = vdmManager.getPrimaryVendor(prLine.getMaterialRrn());
				if (primaryVendor != null) {
					prLine.setRefVendorRrn(primaryVendor.getVendorRrn());
				}
				//设置参考价格
				VendorMaterial currentVendor;
				if (prLine.getVendorRrn() == null) {
					if (primaryVendor == null) {
						throw new ClientException("pur.no_vendor_found");
					}
					//如果没有选定供应商，则默认为主供应商
					currentVendor = primaryVendor;
					prLine.setVendorRrn(primaryVendor.getVendorRrn());
				} else if (primaryVendor != null && prLine.getVendorRrn().equals(primaryVendor.getObjectRrn())) {
					currentVendor = primaryVendor;
				} else {
					currentVendor = vdmManager.getVendorMaterial(prLine.getVendorRrn(), prLine.getMaterialRrn());
				}
				if (currentVendor != null) {
					if (currentVendor.getLastPrice() != null) {
						prLine.setRefUnitPrice(currentVendor.getLastPrice());
					} else {
						prLine.setRefUnitPrice(currentVendor.getReferencedPrice());
					}
					//如果采购单价为null，则将上次采购价(参考价不为null)或参考价赋给它
					if(prLine.getUnitPrice() == null) {
						if(currentVendor.getLastPrice() != null) {
							prLine.setUnitPrice(currentVendor.getLastPrice());							
						} else {
							prLine.setUnitPrice(currentVendor.getReferencedPrice());
						}
					}
					//通过除的方式保留2位小数（四舍五入）
					BigDecimal lineTotal = (prLine.getQty().multiply(prLine.getUnitPrice())).divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);
					prLine.setLineTotal(lineTotal);

					prLine.setQtyEconomicSize(currentVendor.getLeastQuantity());
					prLine.setQtyIncreaseSize(currentVendor.getIncreaseQuantity());
				}
				
				if (prLine.getWarehouseRrn() != null) {
					Warehouse house = em.find(Warehouse.class, prLine.getWarehouseRrn());
					prLine.setWarehouseId(house.getWarehouseId());
				}
				
				if (prLine.getObjectRrn() == null) {
					prLine.setIsActive(true);
					prLine.setCreatedBy(userRrn);
					prLine.setCreated(new Date());
					prLine.setLineStatus(Documentation.STATUS_DRAFTED);
					pr.setTotalLines(pr.getTotalLines() + 1);
					pr.setTotal(pr.getTotal().add(prLine.getLineTotal()));
				} else {
					RequisitionLine oldLine = em.find(RequisitionLine.class, prLine.getObjectRrn());
					pr.setTotal(pr.getTotal().subtract(oldLine.getLineTotal()).add(prLine.getLineTotal()));
				}
				
//				Material material = em.find(Material.class, prLine.getMaterialRrn());
//				BigDecimal convert = pdmManager.getConvertOfUom(prLine.getMaterialRrn(), Uom.CONTYPE_PUR2INV);
//				prLine.setUomId(material.getPurchaseUom());
//				prLine.setQtyInventoty(convert.multiply(prLine.getQty()));
							
				prLine.setUpdatedBy(userRrn);
				if (prLine.getObjectRrn() == null) {
					prLine.setRequisitionRrn(pr.getObjectRrn());
					prLine.setRequisitionId(pr.getDocId());
					em.persist(prLine);
				} else {
					prLine = em.merge(prLine);
				}
			}
			pr.setUpdatedBy(userRrn);
			pr = em.merge(pr);
			pr.setPrLines(prLines);
			em.flush();
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
		return pr;
	}
	
	//原能主计划管理
	public void generateYn(Mps mps, MpsLine line, Requisition pr, long userRrn) throws ClientException {
		try {
			Session session = (Session) em.getDelegate();  
	        Connection conn = session.connection();  
            CallableStatement call = conn.prepareCall("{CALL SP_GET_YNQTY(?,?,?,?,?,?,?)}");  
            call.setLong(1, mps.getOrgRrn());  
            call.setLong(2, line.getMaterialRrn());  
            call.registerOutParameter(3, Types.NUMERIC);  
            call.registerOutParameter(4, Types.NUMERIC);  
            call.registerOutParameter(5, Types.NUMERIC);  
            call.registerOutParameter(6, Types.NUMERIC);  
            call.registerOutParameter(7, Types.NUMERIC);  
            call.execute();  
            
            BigDecimal qtyOnHand = call.getBigDecimal(3);
            BigDecimal qtyWriteOff = call.getBigDecimal(4);
            BigDecimal qtyDiff = call.getBigDecimal(5);
            BigDecimal qtyTransitPr = call.getBigDecimal(6);
            BigDecimal qtyTransitPo = call.getBigDecimal(7);

            BigDecimal qtyOnHandTransit  = qtyOnHand.add(qtyTransitPr).add(qtyTransitPo);//库存+在途
//            BigDecimal result = new BigDecimal("1.3").multiply(qtyOnHandTransit);
            
            BigDecimal qty =null;
    		BusinessCalendar prCalendar = basManager.getCalendarByDay(mps.getOrgRrn(), BusinessCalendar.CALENDAR_PURCHASE);
			Date dateStart = prCalendar.findStartOfNextDay(new Date());
			Material material = line.getMaterial();
			BigDecimal qtyMin = material.getQtyMin()!=null?material.getQtyMin():BigDecimal.ZERO;
			if(line.getQtyMps().compareTo(qtyMin)<=0){//--需求<=安全库存， 则  采购量=2*安全库存-（库存+在途）
				qty = new BigDecimal("2").multiply(qtyMin).subtract(qtyOnHandTransit);
			}else{// 需求>安全库存，  则  采购量=安全库存+需求-（库存+在途）
				qty = qtyMin.add(line.getQtyMps()).subtract(qtyOnHandTransit);
			}
            if(qty.compareTo(BigDecimal.ZERO)>0){
            	RequisitionLine rl = new RequisitionLine();
            	rl.setOrgRrn(line.getOrgRrn());
            	rl.setMaterial(line.getMaterial());
            	rl.setMaterialRrn(line.getMaterialRrn());
            	rl.setUomId(line.getMaterial().getInventoryUom());
            	rl.setRequisitionId(pr.getDocId());
            	rl.setRequisitionRrn(pr.getObjectRrn());
            	rl.setQty(qty);
            	rl.setQtyMin(qtyMin);
            	rl.setQtyHandOn(qtyOnHand);
//            	rl.setPackageSpec(material.getPackageSpec());
            	BigDecimal qtyTransit = qtyTransitPr.add(qtyTransitPo);
            	rl.setQtyTransit(qtyTransit);
//            	line = em.find(MpsLine.class, line.getObjectRrn());
//            	line.setQtyHandOn(qtyOnHand);
//            	line.setQtyTransit(qtyTransit);
//            	line.setQtyMin(qtyMin);
//            	em.merge(line);
            	String whereClause =" isPrimary = 'Y' and materialRrn ="+line.getMaterialRrn();
            	List<VendorMaterial>  vvms = adManager.getEntityList(mps.getOrgRrn(), VendorMaterial.class,Integer.MAX_VALUE,whereClause,null);
            	VendorMaterial   vendorMaterial =null;
            	if(vvms!=null && vvms.size() >0 ){
            		vendorMaterial = vvms.get(0);
            		rl.setVendorRrn(vendorMaterial.getVendorRrn());
            		rl.setVendor(vendorMaterial.getVendor());
            		rl.setProductNo(vendorMaterial.getProductNo());
            		rl.setPackageSpec(vendorMaterial.getPackageSpec());//包装规格
            		if(rl.getPurchaser() == null || rl.getPurchaser().trim().length() == 0){
            			rl.setPurchaser(vendorMaterial.getPurchaser());//设置采购员
					}
            		if(rl.getUnitPrice() == null || rl.getUnitPrice().compareTo(BigDecimal.ZERO) == 0){
						if (vendorMaterial.getLastPrice() != null) {
							rl.setUnitPrice(vendorMaterial.getLastPrice());// 带出上次价格
						} else if (vendorMaterial.getReferencedPrice() != null) {//带出参考价格
							rl.setUnitPrice(vendorMaterial.getReferencedPrice());
						}
					}
            		if(rl.getQtyEconomicSize() == null || rl.getQtyEconomicSize().compareTo(BigDecimal.ZERO) == 0){
						if (vendorMaterial.getLeastQuantity() != null) {
							rl.setQtyEconomicSize(vendorMaterial.getLeastQuantity());//经济批量
						}
					}
            		String warehouseWhereClause = "VUserWarehouse.userRrn = " + userRrn;
            		List<VUserWarehouse> wHouses = adManager.getEntityList(mps.getOrgRrn(), VUserWarehouse.class, Integer.MAX_VALUE, warehouseWhereClause, null);
            		for(VUserWarehouse whouse : wHouses){
            			if("Y".equals(whouse.getIsDefault())){
            				rl.setWarehouseRrn(whouse.getObjectRrn());
            				rl.setWarehouseId(whouse.getWarehouseId());
            				break;
            			}
            		}
            	}
            	if (line.getMaterial().getIsJit()) {
					//立即到货
            		rl.setDateEnd(dateStart);
				}else {
					int leadTime = vendorMaterial.getLeadTime() == null ? 0 : vendorMaterial.getLeadTime().intValue();
					Date dateEnd = prCalendar.addDay(dateStart, leadTime);
					rl.setDateEnd(dateEnd);
				}
            	savePRLine(rl,userRrn);
            }
		} catch (ClientParameterException e){
			throw e;	
		} catch (Exception e) {
			logger.error("generateManufactureOrder MpsLine=" + line.getObjectRrn() 
					+ " Material=" + line.getMaterialRrn() + " Message: " , e);
			throw new ClientException(e);
		}
	}
	
	public void generateYnTempMps(long orgRrn,long warehouseRrn,long userRrn) throws ClientException {
		try {
			
			StringBuffer sql = new StringBuffer();
			sql.append("  select mps.material_rrn,mps.material_id,mps.name,mps.qty_min,mps.vendor,");
			sql.append("nvl(mps.qty_min,0)-(nvl(mps.qty_prl,0)+nvl(mps.qty_ppl,0)+nvl(mps.qty_onhand,0))  qtymps");
			sql.append(" from YN_TEMP_MPS mps ");
			sql.append(" where nvl(mps.qty_prl,0) ");
			sql.append(" +nvl(mps.qty_ppl,0)+nvl(mps.qty_onhand,0)-nvl(mps.qty_min,0) <0   ");
			Query query = em.createNativeQuery(sql.toString());
			List<Object[]> result = (List<Object[]>)query.getResultList();
			
			List<RequisitionLine> lines = new ArrayList<RequisitionLine>();
			
			BusinessCalendar prCalendar = basManager.getCalendarByDay(orgRrn, BusinessCalendar.CALENDAR_PURCHASE);
			Date dateStart = prCalendar.findStartOfNextDay(new Date());
			int i=0;
			for (Object[] row : result) {
				RequisitionLine line = new RequisitionLine();
				Material material = em.find(Material.class, Long.parseLong(String.valueOf(row[0])));
				VendorMaterial   vendorMaterial = em.find(VendorMaterial.class,Long.parseLong(String.valueOf(row[4])));
				line.setMaterial(material);
				line.setOrgRrn(orgRrn);
				line.setIsActive(true);
				line.setMaterialRrn(material.getObjectRrn());
				line.setDateEnd(new Date());
				//line.setQty(material.getQtyMin());
				line.setQty(new BigDecimal(String.valueOf(row[5])));
				line.setLineNo(new Long((i + 1)*10));
				line.setUomId(material.getInventoryUom());
				line.setPackageSpec(vendorMaterial.getPackageSpec());
				line.setProductNo(vendorMaterial.getProductNo());
				line.setUnitPrice(vendorMaterial.getReferencedPrice());
				line.setRefUnitPrice(vendorMaterial.getReferencedPrice());
				if (material.getIsJit()) {
					//立即到货
					line.setDateEnd(dateStart);
				}else {
					int leadTime = vendorMaterial.getLeadTime() == null ? 0 : vendorMaterial.getLeadTime().intValue();
					Date dateEnd = prCalendar.addDay(dateStart, leadTime);
					line.setDateEnd(dateEnd);

				}
				line.setVendorRrn(vendorMaterial.getVendorRrn());
				line.setWarehouseRrn(warehouseRrn);
//				material.setMaterialId(String.valueOf(row[1]));
//				material.setName(String.valueOf(row[2]));
//				material.setQtyMin(new BigDecimal(String.valueOf(row[3])));
				lines.add(line);
			}
			
			Requisition pr = new Requisition();
			pr.setOrgRrn(orgRrn);
			pr.setIsActive(true);
			
			savePRLine(pr, lines , false, userRrn);
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	//原能主计划管理
	public void generateYnTempMps() throws ClientException {
		try {
			Session session = (Session) em.getDelegate();  
	        Connection conn = session.connection();  
            CallableStatement call = conn.prepareCall("{CALL SP_YN_TEMPMPS()}");  
            call.execute();  
		} catch (Exception e) {
			logger.error("generateYnTempMps Message: " , e);
			throw new ClientException(e);
		}
	}
}

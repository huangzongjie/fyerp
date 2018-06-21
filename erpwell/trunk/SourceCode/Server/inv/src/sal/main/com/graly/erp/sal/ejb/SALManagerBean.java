package com.graly.erp.sal.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote; 
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.model.WarehouseMap;
import com.graly.erp.sal.client.SALManager;
import com.graly.erp.sal.model.SalesOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;

@Stateless
@Remote(SALManager.class)
@Local(SALManager.class)
public class SALManagerBean implements SALManager {
	
	private static final Logger logger = Logger.getLogger(SALManagerBean.class);

	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private ADManager adManager;
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public MovementOut createMovementOutFromSo(long orgRrn, String soId, long userRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SalesOrder FROM SalesOrder as SalesOrder ");
		sql.append(" WHERE serialNumber = ? ");
		sql.append(" AND auditing LIKE '同意%' and status='0'  ");
		sql.append(" AND outSerialNumber = '' ");
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, soId);
			List<SalesOrder> sos = query.getResultList();
			if (sos.size() == 0) {
				throw new ClientException("sal.so_not_found");
			}
			MovementOut out = new MovementOut();
			out.setOrgRrn(orgRrn);
			List<MovementLine> lines = new ArrayList<MovementLine>();
			int i = 0;
			for (SalesOrder so : sos) {
				String salWarehouseId = so.getStorage();
				if (salWarehouseId == null || salWarehouseId.trim().length() == 0) {
					throw new ClientException("sal.warehouseid_is_null");
				}
				String whereClause = " salWarehouseId = '" + salWarehouseId.trim() + "' ";
				
				List<WarehouseMap> warehouseMaps = adManager.getEntityList(orgRrn, WarehouseMap.class, 1, whereClause, "");
				if (warehouseMaps.size() == 0) {
					throw new ClientParameterException("sal.warehouseid_is_not_exist", salWarehouseId);
				}
				WarehouseMap warehouseMap = warehouseMaps.get(0);
				
				String warehouseId = warehouseMap.getErpWarehouseId();
				if (warehouseId == null || warehouseId.trim().length() == 0) {
					throw new ClientException("sal.warehouseid_is_null");
				}
				whereClause = " warehouseId = '" + warehouseId.trim() + "' ";
				List<Warehouse> warehouses = adManager.getEntityList(orgRrn, Warehouse.class, 1, whereClause, "");
				if (warehouses.size() == 0) {
					throw new ClientParameterException("sal.warehouseid_is_not_exist", warehouseId);
				}
				Warehouse warehouse = warehouses.get(0);
				
				String materialId = so.getProductSerialNumber();
				if (materialId == null || materialId.trim().length() == 0) {
					throw new ClientException("sal.materialid_is_null");
				}
				whereClause = " materialId = '" + materialId.trim() + "' ";
				List<Material> materials = adManager.getEntityList(orgRrn, Material.class, 1, whereClause, "");
				if (materials.size() == 0) {
					throw new ClientParameterException("sal.materialid_is_not_exist", materialId);
				}
				Material material = materials.get(0);
				if (i == 0) {
					out.setWarehouseRrn(warehouse.getObjectRrn());
					out.setWarehouseId(warehouse.getWarehouseId());
					out.setDescription(so.getRemark());
					out.setCustomerName(so.getCustomerName());
					out.setSeller(so.getSeller());
					out.setSoId(so.getSerialNumber());
					out.setKind(so.getKind());
					out.setDeliverAddress(so.getDeliverAddress());
					out.setLinkMan(so.getLinkman());
				}
				MovementLine line = new MovementLine();
				line.setOrgRrn(orgRrn);
				line.setLineNo(new Long((i + 1)*10));
				line.setMaterialRrn(material.getObjectRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setLotType(material.getLotType());
				line.setUomId(material.getInventoryUom());
	
				line.setMovementId(material.getMaterialId());
				line.setQtyMovement(new BigDecimal(so.getProductNumber()));
				line.setUnitPrice(new BigDecimal(so.getProductPrice()));
				line.setLineTotal(new BigDecimal(so.getProductTotalPrices()));
				line.setUomId(so.getProductUnit());
				lines.add(line);
				i++;
			}
			out.setMovementLines(lines);
			return out;
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void approveSo(String soId, long outRrn, String outId , String deliverDate) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE CANA_ORDER SET OUTSTORAGE_SERIAL_NUMBER = OUTSTORAGE_SERIAL_NUMBER + '+' + :outId ");
		if(deliverDate != null && deliverDate.trim().length() != 0){
			sql.append(" , DELIVERDATE = :deliverDate ");
		}
		sql.append(" WHERE SERIAL_NUMBER = :soId ");
		try{
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter("outId", outId);
			query.setParameter("soId", soId);
			if(deliverDate != null && deliverDate.trim().length() != 0){
				query.setParameter("deliverDate", deliverDate);
			}
			query.executeUpdate();
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void adjustSo(String soId, long outRrn, String outId , String deliverDate) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE CANA_ORDER SET OUTSTORAGE_SERIAL_NUMBER = OUTSTORAGE_SERIAL_NUMBER + '+' + :outId ");
		if(deliverDate != null && deliverDate.trim().length() != 0){
			sql.append(" , DELIVERDATE = :deliverDate ");
		}
		sql.append(" WHERE SERIAL_NUMBER = :soId ");
		try{
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter("outId", outId);
			query.setParameter("soId", soId);
			if(deliverDate != null && deliverDate.trim().length() != 0){
				query.setParameter("deliverDate", deliverDate);
			}
			query.executeUpdate();
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
//	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
//	public BigDecimal getQtySo(String materialId) throws ClientException  {
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT SUM(productNumber) FROM SalesOrder as SalesOrder ");
//		sql.append(" WHERE productSerialNumber = ? ");
//		sql.append(" AND auditing LIKE '同锟斤拷%' ");
//		sql.append(" AND outSerialNumber = '' ");
//		try {
//			Query query = em.createQuery(sql.toString());
//			query.setParameter(1, materialId);
//			Object obj = query.getSingleResult();
//			if(!(obj instanceof String)) 
//				return BigDecimal.ZERO;
//			BigDecimal qtySo = new BigDecimal((String)obj);
//			if (qtySo == null) {
//				return BigDecimal.ZERO;
//			}
//			return qtySo;
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
	
	public List<SalesOrder> getSelesOrderList(int maxResult, String whereClause, String orderBy) throws ClientException {
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT SalesOrder FROM SalesOrder as SalesOrder ");
		sql.append(" WHERE auditing LIKE '同意%' and status='0' ");
		sql.append(" AND outSerialNumber = '' ");
		if(whereClause != null && !"".equals(whereClause)) {
			sql.append(" AND ");
			sql.append(whereClause);
		}
		if(orderBy != null && !"".equals(orderBy)) {
			sql.append(" ORDER BY ");
			sql.append(orderBy);
		}
		Query query = em.createQuery(sql.toString());
		query.setMaxResults(maxResult);
		return query.getResultList();
	}
}

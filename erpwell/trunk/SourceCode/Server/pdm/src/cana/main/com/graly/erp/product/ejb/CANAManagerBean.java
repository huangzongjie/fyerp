package com.graly.erp.product.ejb;



import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.hibernate.Session;

import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.ppm.model.InternalOrder;
import com.graly.erp.ppm.model.InternalOrderLine;
import com.graly.erp.product.client.CANAManager;
import com.graly.erp.product.model.CanaBomRequest;
import com.graly.erp.product.model.CanaInnerOrder;
import com.graly.erp.product.model.CanaProduct;
import com.graly.erp.product.model.CanaTransfer;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;

@Stateless
@Remote(CANAManager.class)
@Local(CANAManager.class)
public class CANAManagerBean implements CANAManager {
	private static final Logger logger = Logger.getLogger(CANAManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private ADManager adManager;
	
	@EJB
	private BASManager basManager;
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public CanaProduct getCanaProduct(String materialId) throws ClientException{
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" FROM CanaProduct CanaProduct WHERE CanaProduct.serialNumber = ? ");
			logger.debug(sql);
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, materialId);
			List<CanaProduct> result = query.getResultList();
			if(result == null || result.size() == 0){
				throw new ClientException("No CanaProduct found");
			}
			return result.get(0);
		}catch (Exception e){
			logger.error(e);
			throw new ClientException(e);
		}
	}

	@Override
	public CanaProduct updateCanaProduct(CanaProduct product)
			throws ClientException {
		try{
			CanaProduct oldProduct = this.getCanaProduct(product.getSerialNumber());
			StringBuffer info = new StringBuffer();
			info.append(" Update CanaProduct(serialNumber:" + oldProduct.getSerialNumber() );
			info.append(" , price2Low:" + oldProduct.getPrice2Low().toString() );
			info.append(" ) --> " );
			info.append(" CanaProduct(serialNumber:" + product.getSerialNumber() );
			info.append(" , price2Low:" + product.getPrice2Low().toString() );
			info.append(" ) " );
			logger.debug(info.toString());
			return em.merge(product);
		}catch (Exception e){
			throw new ClientException(e);
		}
	}
	
	//保存单个BOM记录
	public void importBomFromCrm(Material parentMaterial,long orgRrn,long userRrn) throws ClientException {
		try{
			Date now = new Date();
			if (parentMaterial == null) {
				throw new ClientException("Material not exist");
			}
			StringBuffer sql = new StringBuffer();
			sql.append(" FROM CanaBomRequest CanaBomRequest WHERE CanaBomRequest.status =0 and CanaBomRequest.approveStatus like '同意%' " +
					" and CanaBomRequest.materialId = ? and (CanaBomRequest.isImportedToErp is null or CanaBomRequest.isImportedToErp<>'Y') ");
			logger.debug(sql);
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, parentMaterial.getMaterialId());
			List<CanaBomRequest> result = query.getResultList();
			if(result == null || result.size() == 0){
				throw new ClientException("No CanaBomRequest found");
			}
			
//			StringBuffer sqlBom = new StringBuffer();
//			sqlBom.append(" FROM Bom Bom WHERE Bom.orgRrn = ? and bom.parentRrn = ? ");
//			logger.debug(sql);
//			Query bomQuery = em.createQuery(sqlBom.toString());
//			bomQuery.setParameter(1, orgRrn);
//			bomQuery.setParameter(2, parentMaterial.getObjectRrn());
			List<Bom> boms = adManager.getEntityList(orgRrn, Bom.class, 1,"parentRrn="+parentMaterial.getObjectRrn(), "");
//			List<Bom> boms = bomQuery.getResultList();
			if(boms.size() > 0){
				throw new ClientException("Bom has exist");
			}
			int i = 1;
			for(CanaBomRequest bomRequest:result){
//				List<Material> materials = getMaterialById(bomRequest.getMaterialId(), orgRrn);
				List<Material> materials = adManager.getEntityList(orgRrn, Material.class, 1,"materialId='"+bomRequest.getBomId()+"'", "");
				if(materials==null || materials.size() ==0){
					throw new ClientException("Material not exist"+bomRequest.getMaterialId());
				}
				if("Y".equals(bomRequest.getIsImportedToErp())){
					throw new ClientException("Bom has import");
				}else{
					Bom bom = new Bom();
					bom.setOrgRrn(orgRrn);
					bom.setIsActive(true);
					bom.setCreated(now);
					bom.setCreatedBy(userRrn);
					bom.setUpdated(now);
					bom.setUpdatedBy(userRrn);
					bom.setParentRrn(parentMaterial.getObjectRrn());
					bom.setParentVersion(1L);
					bom.setChildRrn(materials.get(0).getObjectRrn());
					bom.setSeqNo(new Long(i * 10));
					bom.setUnitQty(new BigDecimal(bomRequest.getQtyUnit()));
					adManager.saveEntity(bom, userRrn);
					bomRequest.setIsImportedToErp("Y");
					em.merge(bomRequest);
					
					if(i==1){
						StringBuffer sql2 = new StringBuffer();
						sql2.append(" FROM CanaTransfer CanaTransfer WHERE CanaTransfer.selfField79 ='");
						sql2.append(bomRequest.getSerialNumber());
						sql2.append("'");
						logger.debug(sql2);
						
						Query query2 = em.createQuery(sql2.toString());
						List<CanaTransfer> canaTransfers = query2.getResultList();
						if(canaTransfers != null || canaTransfers.size() > 0){
							for(CanaTransfer ct : canaTransfers){
								ct.setSelfField81("已导入");
								em.merge(ct);
							}
						}
					}
	
				}
				i++;
			}
		}catch (Exception e){
			logger.error(e);
			throw new ClientException(e);
		}
		 
	}
	
//	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public InternalOrder createInternalOrderFromCanaIO(long orgRrn, String canaInnerOrderId, long userRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CanaInnerOrder FROM CanaInnerOrder as CanaInnerOrder ");
		sql.append(" WHERE serialNumber = ? ");
		sql.append(" AND status = 0 ");
		sql.append(" AND selfField12 like '同意%' ");
		sql.append(" AND (selfField30 is null or selfField30 <> 'Y') ");
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, canaInnerOrderId);
			List<CanaInnerOrder> canaInnerOrders = query.getResultList();
			if (canaInnerOrders.size() == 0) {
				throw new ClientException("sal.so_not_found");
			}
			InternalOrder internalOrder = new InternalOrder();
			internalOrder.setOrgRrn(orgRrn);
			internalOrder.setIsActive(true);
			internalOrder.setCreated(new Date());
			internalOrder.setCreatedBy(userRrn);
			internalOrder.setUpdated(new Date());
			internalOrder.setDocId(canaInnerOrderId);
			internalOrder.setDocStatus(InternalOrder.STATUS_APPROVED);
			List<InternalOrderLine> lines = new ArrayList<InternalOrderLine>();
			int i = 0;
			for (CanaInnerOrder canaInnerOrder : canaInnerOrders) {
			   
				String whereClause = " materialId = '" + canaInnerOrder.getMaterialId().trim() + "' ";
				List<Material> materials = adManager.getEntityList(orgRrn, Material.class, 1, whereClause, "");
				if (materials.size() == 0) {
					throw new ClientParameterException("sal.materialid_is_not_exist", canaInnerOrder.getMaterialId());
				}
				Material material = materials.get(0);
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date d1 =df.parse(canaInnerOrder.getSelfField1());
				internalOrder.setDateDelivered(d1);//交货期换到行上面
				if (i == 0) {
//					out.setSoId(so.getSerialNumber());
//					out.setKind(so.getKind());
//					out.setDeliverAddress(so.getDeliverAddress());
//					out.setLinkMan(so.getLinkman());
					internalOrder.setDocType(InternalOrder.DOC_TYPE_PPM);
					internalOrder.setPiNo(canaInnerOrder.getPiNo());//PI
					internalOrder.setCustomId(canaInnerOrder.getCustomId());//客户编号
					internalOrder.setCustomName(canaInnerOrder.getCustomName());//客户名称	
					internalOrder.setOrderId(canaInnerOrder.getSelfField23());//订单编号
					internalOrder.setSelfField2(canaInnerOrder.getSelfField2());
					internalOrder.setSeller(canaInnerOrder.getSeller());
					internalOrder.setSellerName(canaInnerOrder.getSellerName());
					internalOrder =(InternalOrder) adManager.saveEntity(internalOrder, userRrn);
				}
				
				Session session = (Session) basManager.getEntityManager().getDelegate();  
		        Connection conn = session.connection();  
	            CallableStatement call = conn.prepareCall("{CALL SP_GET_QTYALLOCATION(?,?,?,?,?,?,?,?,?,?,?,?)}");  
	            call.setLong(1, orgRrn);  
	            call.setLong(2, material.getObjectRrn());  
	            call.registerOutParameter(3, Types.NUMERIC);  
	            call.registerOutParameter(4, Types.NUMERIC);  
	            call.registerOutParameter(5, Types.NUMERIC);  
	            call.registerOutParameter(6, Types.NUMERIC);  
	            call.registerOutParameter(7, Types.NUMERIC);  
	            call.registerOutParameter(8, Types.NUMERIC);  
	            call.registerOutParameter(9, Types.NUMERIC);  
	            call.registerOutParameter(10, Types.NUMERIC);  
	            call.registerOutParameter(11, Types.NUMERIC);  
	            call.registerOutParameter(12, Types.NUMERIC); 
	            
	            call.execute();  
	            
	            BigDecimal qtyOnHand = call.getBigDecimal(3);
	            BigDecimal qtyWriteOff = call.getBigDecimal(4);
	            BigDecimal qtyDiff = call.getBigDecimal(5);
	            BigDecimal qtyTransitPr = call.getBigDecimal(6);
	            BigDecimal qtyTransitPo = call.getBigDecimal(7);
	            BigDecimal qtyMoLine = call.getBigDecimal(8);
	            BigDecimal qtyMoLineReceive = call.getBigDecimal(9);
	            BigDecimal qtyMoLineWip = call.getBigDecimal(10);
	            BigDecimal qtyAllocation = call.getBigDecimal(11);
	            BigDecimal qtySo = call.getBigDecimal(12);
				
				
				
				
				
				InternalOrderLine line = new InternalOrderLine();
				line.setOrgRrn(orgRrn);
				line.setIsActive(true);
				line.setCreated(new Date());
				line.setCreatedBy(userRrn);
				line.setUpdated(new Date());
				line.setUpdatedBy(userRrn);
				line.setLineNo(new Long((i + 1)*10));
				line.setMaterialRrn(material.getObjectRrn());
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				line.setUomId(material.getInventoryUom());
				line.setLineStatus(InternalOrderLine.LINESTATUS_APPROVED);
				line.setQtyOnhand(qtyOnHand);
				line.setQtyWip(qtyMoLineWip);
				line.setQty(canaInnerOrder.getQty());
				line.setQtyOrder(canaInnerOrder.getQty());//订单数量这个值不能改变
				line.setIsPurchase(material.getIsPurchase());
				line.setQtyMin(material.getQtyMin());//安全库存
				line.setIoId(internalOrder.getDocId());
				line.setIoRrn(internalOrder.getObjectRrn());
				line.setComments(canaInnerOrder.getSelfField2());
				line.setDateDelivery(d1);
				line.setCustomerManager(canaInnerOrder.getSelfField29());
				adManager.saveEntity(line, userRrn);
				lines.add(line);
				i++;
				canaInnerOrder.setSelfField30("Y");//CRM该字段设置为Y代表已经处理过
				em.merge(canaInnerOrder);
			}
			internalOrder.setIoLines(lines);
			return internalOrder;
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	
	
	public List<CanaInnerOrder> getCanaInnerOrderList(int maxResult, String whereClause, String orderBy) throws ClientException {
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT CanaInnerOrder FROM CanaInnerOrder as CanaInnerOrder ");
		sql.append(" WHERE 1=1 ");
		sql.append(" AND status = 0 ");
		sql.append(" AND selfField12 like '同意%' ");
		sql.append(" AND (selfField30 is null or selfField30 <> 'Y') ");
		
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
	
	public List<CanaInnerOrder> getDisCanaInnerOrderList(int maxResult, String whereClause, String orderBy) throws ClientException {
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT distinct serialNumber,piNo,customName,selfField23,seller,sellerName,customId FROM CanaInnerOrder as CanaInnerOrder ");
		sql.append(" WHERE 1=1 ");
		sql.append(" AND status = 0 ");
		sql.append(" AND selfField12 like '同意%' ");
		sql.append(" AND (selfField30 is null or selfField30 <> 'Y') ");
		
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
		List<Object[]> result = query.getResultList();
		List<CanaInnerOrder> innerOrders = new ArrayList<CanaInnerOrder>();
		for(Object[] row : result){
			CanaInnerOrder io = new CanaInnerOrder();
			io.setSerialNumber(String.valueOf(row[0]));
			io.setPiNo(String.valueOf(row[1]));
			io.setCustomName(String.valueOf(row[2]));
//			io.setSelfField1(String.valueOf(row[3]));
			io.setSelfField23(String.valueOf(row[3]));
			io.setSeller(String.valueOf(row[4]));
			io.setSellerName(String.valueOf(row[5]));
			io.setCustomId(String.valueOf(row[6]));
			innerOrders.add(io);
		}
		return innerOrders;
	}
}

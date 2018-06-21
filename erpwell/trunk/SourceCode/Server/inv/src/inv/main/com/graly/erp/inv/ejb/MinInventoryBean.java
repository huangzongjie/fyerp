package com.graly.erp.inv.ejb;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.ResourceAdapter;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
//import com.graly.alm.model.AlarmMessage;
//import com.graly.alm.model.AlarmType;

import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
//import com.graly.alm.helper.AlarmHelper;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "cronTrigger", propertyValue = "0 0 0 * * ?")
})
@ResourceAdapter("quartz-ra.rar")
public class MinInventoryBean implements Job {

	private static final Logger logger = Logger.getLogger(MinInventoryBean.class);
	
	public static final String SUBJECT_MESSAGE = "GLORY/ALARM/MESSAGE";
	
	@PersistenceContext
	private EntityManager em;
	
	public void execute(JobExecutionContext context) throws org.quartz.JobExecutionException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT OBJECT_RRN, ORG_RRN, QTY_MIN, MATERIAL_ID FROM PDM_MATERIAL ");
		sql.append(" WHERE IS_LOT_CONTROL = 'Y' ");
		
		StringBuffer sqlOnHand = new StringBuffer();
		sqlOnHand.append(" SELECT NVL(SUM(QTY_ONHAND), 0) FROM INV_STORAGE S, INV_WAREHOUSE W ");
		sqlOnHand.append(" WHERE S.WAREHOUSE_RRN = W.OBJECT_RRN ");
		sqlOnHand.append(" AND W.IS_MRP = 'Y' ");
		sqlOnHand.append(" AND S.ORG_RRN = ? ");
		sqlOnHand.append(" AND S.MATERIAL_RRN = ? ");
		try{
			Query query = em.createNativeQuery(sql.toString());
			List<Object[]> materials = query.getResultList();
			for (Object[] objs : materials) {
				BigDecimal qtyMin = (BigDecimal)objs[2];
//				if (qtyMin != null && qtyMin.compareTo(BigDecimal.ZERO) > 0) {
//					BigDecimal materialRrn = (BigDecimal)objs[0];
//					BigDecimal orgRrn = (BigDecimal)objs[1];
//					String materialId = (String)objs[3];
//					query = em.createNativeQuery(sqlOnHand.toString());
//					query.setParameter(1, orgRrn);
//					query.setParameter(2, materialRrn);
//					BigDecimal qtyOnHand = (BigDecimal)query.getSingleResult();
//					if (qtyOnHand != null && qtyOnHand.compareTo(qtyMin) < 0) {
//						AlarmMessage message = new AlarmMessage();
//						message.setIsActive(true);
//						message.setOrgRrn(orgRrn.longValue());
//						message.setObjectType(AlarmType.OBJECT_TYPE_MATERIAL);
//						message.setAlarmType(AlarmType.ALARM_TYPE_MIN_INVENTORY);
//						message.setObjectId(materialId);
//						message.setDateAlarm(new Date());
//						message.setAlarmText("test1111");
//						AlarmHelper.sendObjectMessage(SUBJECT_MESSAGE, message);
//					}
//				}
			}
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
		}
	}
}

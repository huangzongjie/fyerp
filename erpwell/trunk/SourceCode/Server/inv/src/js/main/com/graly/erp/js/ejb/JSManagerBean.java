package com.graly.erp.js.ejb;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.graly.erp.js.client.JSManager;
import com.graly.erp.js.model.JSMaterialQtyQuery;
import com.graly.framework.core.exception.ClientException;

@Stateless
@Remote(JSManager.class)
@Local(JSManager.class)
public class JSManagerBean implements JSManager {
	
	private static final Logger logger = Logger.getLogger(JSManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	/*金石物料需求警报（库存小于工作令需求）
	 *
	 * */
	@Override
	public List<JSMaterialQtyQuery> getMaterialQtyQueryList(int maxResult,
			String whereClause, String orderBy) throws ClientException {
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT JSMaterialQtyQuery FROM JSMaterialQtyQuery as JSMaterialQtyQuery ");
		sql.append(" WHERE 1=1 and L3<L4 ");
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

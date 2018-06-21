package com.graly.erp.ppm.client;

import java.util.List;
import java.util.Map;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.model.InternalOrder;
import com.graly.erp.ppm.model.InternalOrderLine;
import com.graly.erp.ppm.model.Lading;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.MpsLineBom;
import com.graly.erp.ppm.model.MpsLineDelivery;
import com.graly.erp.ppm.model.MpsStatistcLine;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.model.SalePlanLine;
import com.graly.erp.ppm.model.TpsLine;
import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.core.exception.ClientException;

public interface PPMManager {
	
	Mps savePlanSetup(long tableRrn, Mps planSetup, long userRrn) throws ClientException;
	SalePlanLine saveSalePlanLine(long tableRrn, SalePlanLine salePlanLine, long userRrn) throws ClientException;
	Lading saveLading(long tableRrn, Lading lading, long userRrn) throws ClientException;
	MpsLine saveMpsLine(long tableRrn, MpsLine mpsLine, long userRrn) throws ClientException;
	
	List<SalePlanLine> getSalePlanLineSum(Mps mps) throws ClientException;
	void generateMpsLine(Mps mps, long materialRrn, List<SalePlanLine> salePlanLines, long userRrn) throws ClientException;
	
	List<MpsLine> getSortedMpsLine(long orgRrn, String mpsId) throws ClientException;
	void generateMaterialBatchSum(long orgRrn) throws ClientException;
	void generateManufactureOrder(Mps mps, MpsLine line, Requisition pr, long userRrn) throws ClientException;
	
	List<MpsLineBom> getMpsLineBom(MpsLine line) throws ClientException;
	void deleteMpsLineBom(MpsLine line) throws ClientException;
	void saveMpsLineBom(MpsLine line, List<MpsLineBom> boms) throws ClientException;
	void saveErrorLog(PasErrorLog errLog, long orgRrn) throws ClientException;
	void saveMpsLineBom(MpsLineBom mpsLineBom) throws ClientException;
	void deleteMpsLineBom(MpsLine line, List<MpsLineBom> toSaves, List<MpsLineBom> toDeletes) throws ClientException;
	
	boolean verifyMpsLine(MpsLine line) throws ClientException;
	int computePlanSum(long orgRrn, String nextMonth) throws ClientException;
	
	/**
	 * @param orgRrn 工作区域
	 * @param whereClause 附加查询条件(由用户输入)
	 * @param isIncludeTransit 是否包含在途(若包含 库存+在制品<安全库存；若不包含 库存<1/2安全库存)
	 * @return	返回库存不足的物料
	 * @throws ClientException
	 */
	List<Material> getShortageMaterials(long orgRrn, String whereClause, boolean isIncludeTransit) throws ClientException;
	
	/**
	 * @param orgRrn 工作区域
	 * @param month	要查询的月份
	 * @param whereClause	用户输入的额外的条件
	 * @return	返回超计划提货的物料(用SalePlanLine作为保存数据的栽体，只为方便在前台显示)
	 * @throws ClientException
	 */
	List<SalePlanLine> getOverOutedMaterials(long orgRrn, String month,String whereClause) throws ClientException;
	/**
	 * 主计划统计查询
	 * 计算公式：①库存数+②在制数-③待提数-④需求数+⑤生产数+⑥临时数量-⑦内部采购
		①生成主计划时的库存数，主计划中有字段记录了
		②查看报表时的当前在制数
		③销售计划中的提留数
		④销售计划中的销售数
		⑤主计划中的生产数量
		⑥主计划中的临时数量
		⑦主计划中的内部订单数理
	 * @param orgRrn
	 * @param mpsId
	 * @return
	 * @throws ClientException
	 */
	List<MpsStatistcLine> statisticMps(long orgRrn, String mpsId, Long materialRrn) throws ClientException;
	
	public void updateMpsLineByStatisticLine(long orgRrn, long userRrn, MpsStatistcLine line) throws ClientException;
	
	
	
	/**
	 * 主计划管理-保存 ppm_mps_line
	 * ppm_mps_statistc_line里头是否存在ppm_mps_line
	 	 1.不存在
	  		1.1 查询rep_temp_plan_sum是否存在ppm_mps_line
	  			1.1.1 存在 copy数据到 ppm_mps_statistc_line
	 			1.1.2 不存在 调用wipManager.getMaterialSum().
	 	 2.存在 不做处理
	 * @param orgRrn
	 * @param mpsId
	 * @param materialRrn
	 * @return
	 * @throws ClientException
	 */
	public boolean validateMpsLine(long orgRrn,MpsLine line) throws ClientException;
	List<TpsLinePrepare> approveTpsLinePrepare(long orgRrn, long userRrn, List<TpsLinePrepare> tpsLinePrepares) throws ClientException;
	
	void importMpsLine(long orgRrn,long userRrn,Mps mps,Map<Long,MpsLine> mpsLinesMap, List<MpsLine> perMpsLines ) throws ClientException;

	void generateManufactureOrderTime(Mps mps, MpsLine line, Requisition pr, long userRrn) throws ClientException;
	void addMpsLineDelivery(long orgRrn, long userRrn,MpsStatistcLine staticLine,MpsLineDelivery lineDelivery) throws ClientException;
	boolean getMoByMpsStatistcLine(Long orgRrn, MpsStatistcLine msl) throws ClientException;

	public List<TpsLinePrepare> saveTpsLinePrepareFromIO(String tpsId,InternalOrder io,List<InternalOrderLine> selectIOLines,long userRrn) throws ClientException;
	public InternalOrder saveInternalOrderPoFromIO(InternalOrder io,List<InternalOrderLine> selectIOLines,long userRrn) throws ClientException;
	public void generateManufactureOrderTpsLine2(TpsLine tpsLine, long userRrn) throws ClientException;
}

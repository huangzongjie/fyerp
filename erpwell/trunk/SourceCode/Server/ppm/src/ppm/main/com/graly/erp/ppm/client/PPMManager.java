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
	 * @param orgRrn ��������
	 * @param whereClause ���Ӳ�ѯ����(���û�����)
	 * @param isIncludeTransit �Ƿ������;(������ ���+����Ʒ<��ȫ��棻�������� ���<1/2��ȫ���)
	 * @return	���ؿ�治�������
	 * @throws ClientException
	 */
	List<Material> getShortageMaterials(long orgRrn, String whereClause, boolean isIncludeTransit) throws ClientException;
	
	/**
	 * @param orgRrn ��������
	 * @param month	Ҫ��ѯ���·�
	 * @param whereClause	�û�����Ķ��������
	 * @return	���س��ƻ����������(��SalePlanLine��Ϊ�������ݵ����壬ֻΪ������ǰ̨��ʾ)
	 * @throws ClientException
	 */
	List<SalePlanLine> getOverOutedMaterials(long orgRrn, String month,String whereClause) throws ClientException;
	/**
	 * ���ƻ�ͳ�Ʋ�ѯ
	 * ���㹫ʽ���ٿ����+��������-�۴�����-��������+��������+����ʱ����-���ڲ��ɹ�
		���������ƻ�ʱ�Ŀ���������ƻ������ֶμ�¼��
		�ڲ鿴����ʱ�ĵ�ǰ������
		�����ۼƻ��е�������
		�����ۼƻ��е�������
		�����ƻ��е���������
		�����ƻ��е���ʱ����
		�����ƻ��е��ڲ���������
	 * @param orgRrn
	 * @param mpsId
	 * @return
	 * @throws ClientException
	 */
	List<MpsStatistcLine> statisticMps(long orgRrn, String mpsId, Long materialRrn) throws ClientException;
	
	public void updateMpsLineByStatisticLine(long orgRrn, long userRrn, MpsStatistcLine line) throws ClientException;
	
	
	
	/**
	 * ���ƻ�����-���� ppm_mps_line
	 * ppm_mps_statistc_line��ͷ�Ƿ����ppm_mps_line
	 	 1.������
	  		1.1 ��ѯrep_temp_plan_sum�Ƿ����ppm_mps_line
	  			1.1.1 ���� copy���ݵ� ppm_mps_statistc_line
	 			1.1.2 ������ ����wipManager.getMaterialSum().
	 	 2.���� ��������
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

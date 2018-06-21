package com.graly.erp.wip.workcenter.schedule.into2;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.WorkShopSchedule;
import com.graly.erp.wip.model.WorkShopSchedule2;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class WorkScheduleImportTwoProgress  implements IRunnableWithProgress {
	private static final Logger logger = Logger.getLogger(WorkScheduleImportTwoProgress.class);
//	protected String tpsId;
	private boolean isFinished = false;
	protected static int TASK_NUMBER = 32;
	protected static int INIT_TASK = 0;
//	protected Mps mps;
	protected String fileUrl;
	protected ADTable adTable;
	protected List<PasErrorLog> errLogs;
	protected String errDetial;
	protected String materialId;
	protected boolean flag = true;
	protected ADManager adManager;
	protected WorkScheduleImportTwoSection importSection;
	protected List<WorkShopSchedule2> workShopSchedules = new ArrayList<WorkShopSchedule2>();
	
	protected HSSFWorkbook hssfWb;
	public WorkScheduleImportTwoProgress(Object mps, String fileUrl, ADTable adTable,
			WorkScheduleImportTwoSection importSection) {
		this.fileUrl = fileUrl;
		this.adTable =adTable;
		this.importSection = importSection;
	}
	
	protected void executeOneWorked(HSSFRow row, IProgressMonitor monitor, int index) {
		WorkShopSchedule2 workShopSchedule2 = new WorkShopSchedule2();
		workShopSchedule2.setOrgRrn(Env.getOrgRrn());
		workShopSchedule2.setDocStatus("START");
		workShopSchedule2.setWorkcenterRrn(importSection.getWorkCenterRrn());
		Date now = Env.getSysDate();
		try {
			// ��excel�е�i�е�������ݶ��뵽salePlanLine��
			readExcelRow(row, workShopSchedule2);
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_sale_data"), TASK_NUMBER, index - 1, materialId));
			
			// ��flagΪtrue, ��ʾ���ڵ���, ������ʾ, ���������Լ� 1
			if (flag) {
				if (workShopSchedule2.getScheduleDate()!=null && workShopSchedule2 != null) {
//					if(adManager == null)
//						adManager = Framework.getService(ADManager.class);
//					adManager.saveEntity(adTable.getObjectRrn(), workShopSchedule, Env.getUserRrn());
					workShopSchedules.add(workShopSchedule2);
				} else {
					PasErrorLog elog = new PasErrorLog();
					elog.setMpsId(null);
					elog.setMaterialId("");
					elog.setErrMessage(errDetial);
					elog.setErrDate(now);
					errLogs.add(elog);
				}
			} else {
				PasErrorLog elog = new PasErrorLog();
				elog.setMpsId(null);
				elog.setMaterialId(materialId);
				elog.setErrMessage(errDetial);
				elog.setErrDate(now);
				errLogs.add(elog);
			}
		} catch(Exception e) {
			String msg = e.getMessage();
			if (e instanceof ClientException) {
				String ec = ((ClientException)e).getErrorCode();
				if(ec != null && !"".equals(ec))
					msg = ec;
			}
			PasErrorLog log = new PasErrorLog();
			log.setMpsId(null);
			log.setMaterialId(materialId);
			log.setErrMessage(msg);
			log.setErrDate(now);
			errLogs.add(log);
			logger.error("Error at TempSaleDataImportProgress executeOneWorked(): " , e);
		}
	}
	
	protected void readExcelRow(HSSFRow row, WorkShopSchedule2 workShopSchedule2) {
		int j = 0;
		boolean ladingFlag = true;
		try {
			if (row != null) {
				for (; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);
					if(j == 1||j==2||j==3||j==5||j==7){
						
					}else{
						if(cell==null){
							continue;
						}
						
					}
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						if(j == 0) {						//��0��Ϊ�Ų�����
							String excelDate = cell.getStringCellValue();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy��");
							SimpleDateFormat compare = new SimpleDateFormat("yyyy-MM");
							DateFormat dd=new SimpleDateFormat("yyyy��MM��dd");
							String scheduleDate = sdf.format(importSection.getScheduleDate());
							if (cell != null) {
								String dateString = scheduleDate+excelDate;
								Date date = dd.parse(dateString);
								String selectDate =compare.format(importSection.getScheduleDate());
								String selectDate2 = compare.format(date);
								if(selectDate.compareTo(selectDate2)!=0){
									flag = false;
									errDetial = "����: " + materialId + "����������С��ѡ����Ų����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + ", ��: " + (j + 1) + ".";
								}else{
									workShopSchedule2.setScheduleDate(date);
								}
								
							}
							else {
								flag = false;
								errDetial = "����: " + materialId + "����������Ϊ�գ�"
								+ "���У�" + (row.getRowNum() + 1) + ", ��: " + (j + 1) + ".";
							}
						}else if (j == 1) {				//��1��Ϊ��̨1���ϱ��
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName1(material.getName());
									workShopSchedule2.setMaterialId1(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==2) {					//��2��Ϊ��̨1��������
						} else if (j == 3) {				//��3�л�̨1����
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty1(qty);
							}
						}else if (j == 4) {				//��̨2
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName2(material.getName());
									workShopSchedule2.setMaterialId2(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==5) {					
						} else if (j == 6) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty2(qty);
							}
						}else if (j == 7) {				//��̨3
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName3(material.getName());
									workShopSchedule2.setMaterialId3(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==8) {					
						} else if (j == 9) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty3(qty);
							}
						}else if (j == 10) {				//��̨4
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName4(material.getName());
									workShopSchedule2.setMaterialId4(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==11) {					
						} else if (j == 12) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty4(qty);
							}
						}else if (j == 13) {				//��̨5
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName5(material.getName());
									workShopSchedule2.setMaterialId5(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==14) {					
						} else if (j == 15) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty5(qty);
							}
						}else if (j == 16) {				//��̨6
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName6(material.getName());
									workShopSchedule2.setMaterialId6(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==17) {					
						} else if (j == 18) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty6(qty);
							}
						}else if (j == 19) {				//��̨7
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName7(material.getName());
									workShopSchedule2.setMaterialId7(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==20) {					
						} else if (j == 21) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty7(qty);
							}
						}else if (j ==22) {				//��̨8
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName8(material.getName());
									workShopSchedule2.setMaterialId8(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==23) {					
						} else if (j == 24) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty8(qty);
							}
						}else if (j == 25) {				//��̨9
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName9(material.getName());
									workShopSchedule2.setMaterialId9(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==26) {					
						} else if (j == 27) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty9(qty);
							}
						}else if (j == 28) {				//��̨10
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName10(material.getName());
									workShopSchedule2.setMaterialId10(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==29) {					
						} else if (j == 30) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty10(qty);
							}
						}else if (j == 31) {				//��̨11
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName11(material.getName());
									workShopSchedule2.setMaterialId11(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==32) {					
						} else if (j == 33) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty11(qty);
							}
						}else if (j == 34) {				//��̨12
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName12(material.getName());
									workShopSchedule2.setMaterialId12(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==35) {					
						} else if (j == 36) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty12(qty);
							}
						}else if (j == 37) {				//��̨13
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName13(material.getName());
									workShopSchedule2.setMaterialId13(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==38) {					
						} else if (j == 39) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty13(qty);
							}
						}else if (j == 40) {				//��̨14
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName14(material.getName());
									workShopSchedule2.setMaterialId14(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==41) {					
						} else if (j == 42) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty14(qty);
							}
						}else if (j == 43) {				//��̨15
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName15(material.getName());
									workShopSchedule2.setMaterialId15(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==44) {					
						} else if (j == 45) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty15(qty);
							}
						}else if (j == 46) {				//��̨16
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName16(material.getName());
									workShopSchedule2.setMaterialId16(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==47) {					
						} else if (j == 48) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty16(qty);
							}
						}else if (j == 49) {				//��̨17
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName17(material.getName());
									workShopSchedule2.setMaterialId17(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==50) {					
						} else if (j == 51) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty17(qty);
							}
						}else if (j == 52) {				//��̨18
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName18(material.getName());
									workShopSchedule2.setMaterialId18(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==53) {					
						} else if (j == 54) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty18(qty);
							}
						}else if (j == 55) {				//��̨19
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName19(material.getName());
									workShopSchedule2.setMaterialId19(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==56) {					
						} else if (j == 57) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty19(qty);
							}
						}else if (j == 58) {				//��̨20
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName20(material.getName());
									workShopSchedule2.setMaterialId20(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==59) {					
						} else if (j == 60) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty20(qty);
							}
						}else if (j == 61) {				//��̨21
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName21(material.getName());
									workShopSchedule2.setMaterialId21(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==62) {					
						} else if (j == 63) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty21(qty);
							}
						}else if (j == 64) {				//��̨22
							materialId = cell.getStringCellValue();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialName22(material.getName());
									workShopSchedule2.setMaterialId22(material.getMaterialId());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==65) {					
						} else if (j == 66) {				
							String planQty = cell.getStringCellValue();
							if (planQty != null ){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule2.setMaterialQty22(qty);
							}
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if(j == 0) {						//��0��Ϊ�Ų�����
//							String excelDate = cell.getStringCellValue();
//							SimpleDateFormat sdf = new SimpleDateFormat("yyyy��");
//							SimpleDateFormat compare = new SimpleDateFormat("yyyy-MM");
//							DateFormat dd=new SimpleDateFormat("yyyy��MM��dd");
//							String scheduleDate = sdf.format(importSection.getScheduleDate());
//							if (cell != null) {
//								String dateString = scheduleDate+excelDate;
//								Date date = dd.parse(dateString);
//								String selectDate =compare.format(importSection.getScheduleDate());
//								String selectDate2 = compare.format(date);
//								if(selectDate.compareTo(selectDate2)!=0){
//									flag = false;
//									errDetial = "����: " + materialId + "����������С��ѡ����Ų����ڣ�"
//									+ "���У�" + (row.getRowNum() + 1) + ", ��: " + (j + 1) + ".";
//								}else{
//									workShopSchedule2.setScheduleDate(date);
//								}
//								
//							}
//							else {
//								flag = false;
//								errDetial = "����: " + materialId + "����������Ϊ�գ�"
//								+ "���У�" + (row.getRowNum() + 1) + ", ��: " + (j + 1) + ".";
//							}
						}else if (j == 1) {				//��1��Ϊ��̨1���ϱ��
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId1(material.getMaterialId());
									workShopSchedule2.setMaterialName1(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==2) {					//��2��Ϊ��̨1��������
						} else if (j == 3) {				//��3�л�̨1����
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty1(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 4) {				//��̨2
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId2(material.getMaterialId());
									workShopSchedule2.setMaterialName2(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==5) {					
						} else if (j == 6) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty2(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 7) {				//��̨3
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId3(material.getMaterialId());
									workShopSchedule2.setMaterialName3(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==8) {					
						} else if (j == 9) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty3(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 10) {				//��̨4
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId4(material.getMaterialId());
									workShopSchedule2.setMaterialName4(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==11) {					
						} else if (j == 12) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty4(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 13) {				//��̨5
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId5(material.getMaterialId());
									workShopSchedule2.setMaterialName5(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==14) {					
						} else if (j == 15) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty5(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 16) {				//��̨6
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId6(material.getMaterialId());
									workShopSchedule2.setMaterialName6(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==17) {					
						} else if (j == 18) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty6(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 19) {				//��̨7
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId7(material.getMaterialId());
									workShopSchedule2.setMaterialName7(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==20) {					
						} else if (j == 21) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty7(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j ==22) {				//��̨8
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId8(material.getMaterialId());
									workShopSchedule2.setMaterialName8(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==23) {					
						} else if (j == 24) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty8(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 25) {				//��̨9
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId9(material.getMaterialId());
									workShopSchedule2.setMaterialName9(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==26) {					
						} else if (j == 27) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty9(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 28) {				//��̨10
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId10(material.getMaterialId());
									workShopSchedule2.setMaterialName10(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==29) {					
						} else if (j == 30) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty10(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 31) {				//��̨11
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId11(material.getMaterialId());
									workShopSchedule2.setMaterialName11(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==32) {					
						} else if (j == 33) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty11(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 34) {				//��̨12
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId12(material.getMaterialId());
									workShopSchedule2.setMaterialName12(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==35) {					
						} else if (j == 36) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty12(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 37) {				//��̨13
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId13(material.getMaterialId());
									workShopSchedule2.setMaterialName13(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==38) {					
						} else if (j == 39) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty13(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 40) {				//��̨14
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId14(material.getMaterialId());
									workShopSchedule2.setMaterialName14(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==41) {					
						} else if (j == 42) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty14(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 43) {				//��̨15
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId15(material.getMaterialId());
									workShopSchedule2.setMaterialName15(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==44) {					
						} else if (j == 45) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty15(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 46) {				//��̨16
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId16(material.getMaterialId());
									workShopSchedule2.setMaterialName16(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==47) {					
						} else if (j == 48) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty16(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 49) {				//��̨17
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId17(material.getMaterialId());
									workShopSchedule2.setMaterialName17(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==50) {					
						} else if (j == 51) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty17(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 52) {				//��̨18
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId18(material.getMaterialId());
									workShopSchedule2.setMaterialName18(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==53) {					
						} else if (j == 54) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty18(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 55) {				//��̨19
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId19(material.getMaterialId());
									workShopSchedule2.setMaterialName19(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==56) {					
						} else if (j == 57) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty19(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 58) {				//��̨20
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId20(material.getMaterialId());
									workShopSchedule2.setMaterialName20(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==59) {					
						} else if (j == 60) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty20(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 61) {				//��̨21
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId21(material.getMaterialId());
									workShopSchedule2.setMaterialName21(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==62) {					
						} else if (j == 63) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty21(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if (j == 64) {				//��̨22
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// ����8λ������(0)������8λ
							if(initId.length() < 8) {
								int length = 8 - initId.length();
								StringBuffer prefix = new StringBuffer("");
								for(int i = 0; i < length; i++) {
									prefix.append("0");
								}
								sb.append(prefix);
							}
							materialId = sb.append(initId).toString();
							if(materialId!=null && !"".equals(materialId.trim())){
								Material material = getMaterialById(materialId);
								if (material != null) {
									workShopSchedule2.setMaterialId22(material.getMaterialId());
									workShopSchedule2.setMaterialName22(material.getName());
								} else {
									flag = false;
									errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}
						} else if(j ==65) {					
						} else if (j == 66) {				
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule2.setMaterialQty22(qty);
								} 							
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}
						
						 
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						if(j == 0) {//��Ԫ������Ϊ�ջ���ø÷���
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "������������Ϊ�ա�";							
						}
//						else if(j == 3) {
//							flag = false;
//							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "�������ϱ��Ϊ�ա�";	
//						}
//						else if(j == 5) {
//							flag = false;
//							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "��������Ϊ�ա�";	
//						}
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
//			if(j == 10){
//				//����Ǳ�עΪ�տ��Ժ���
//			}else if(j == 9){
//				//����ǽ�������Ϊ�տ��Ժ���
//			}else{
//				flag = false;
//				errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "���ĵ�Ԫ����ֵΪ�գ��뽫�õ�Ԫ��ɾ����";
//			}
			if(j == 0){
				flag = false;
				errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "���ĵ�Ԫ����ֵΪ�գ��뽫�õ�Ԫ��ɾ����";
			} 
		}
		
		
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		FileInputStream fis = null;
		try {
			errLogs = new ArrayList<PasErrorLog>();
			fis = new FileInputStream(fileUrl);
			HSSFSheet sheet = initExcelread(fis);
			// ������������
			TASK_NUMBER = sheet.getPhysicalNumberOfRows() - 1;
			monitor.beginTask("  ", TASK_NUMBER);

			int finishedWorked = INIT_TASK;
			for (int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {//�ӵ����п�ʼ����1��2���Ǳ�ͷ
				flag = true;
				errDetial = null;
				materialId = null;
				HSSFRow row = sheet.getRow(i);
				// ִ�е�i������
				executeOneWorked(row, monitor, i);
				monitor.worked(1);
				finishedWorked++;
			}
			fis.close();
			if (finishedWorked < TASK_NUMBER)
				monitor.worked(TASK_NUMBER - finishedWorked);
			monitor.done();
			this.isFinished = true;
		} catch (Exception e) {
			logger.error("Error at MpsDataImportProgress : run() ", e);
			return;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
			}
		}
	}
	
	protected Material getMaterialById(String materialId) {
		try {
			if(adManager == null)
				adManager = Framework.getService(ADManager.class);
			List<Material> list = adManager.getEntityList(Env.getOrgRrn(), Material.class, 2, " materialId = '" + materialId + "' ", "");
			if (list != null || list.size() != 0) {
				Material material = (Material) list.get(0);
				return material;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public HSSFSheet initExcelread(FileInputStream fis) {
		try {
			POIFSFileSystem fs = new POIFSFileSystem(fis);
			HSSFWorkbook wb = new HSSFWorkbook(fs); // ��ȡexcel������
			this.hssfWb = wb;
			HSSFSheet sheet = wb.getSheetAt(0); // ��ȡexcel��sheet
			return sheet;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<PasErrorLog> getErrLogs() {
		return errLogs;
	}

	public boolean isFinished() {
		return isFinished;
	}
	
	public boolean isSuccess() {
		if(this.errLogs == null || errLogs.size() == 0)
			return true;
		return false;
	}

	public List<WorkShopSchedule2> getWorkShopSchedules() {
		return workShopSchedules;
	}

	public void setWorkShopSchedules(List<WorkShopSchedule2> workShopSchedules) {
		this.workShopSchedules = workShopSchedules;
	}

}

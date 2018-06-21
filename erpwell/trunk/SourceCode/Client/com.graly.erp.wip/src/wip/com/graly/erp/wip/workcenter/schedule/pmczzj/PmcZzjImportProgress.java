package com.graly.erp.wip.workcenter.schedule.pmczzj;

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
import com.graly.erp.wip.model.PmcZzjImport;
import com.graly.erp.wip.model.WorkShopSchedule;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class PmcZzjImportProgress  implements IRunnableWithProgress {
	private static final Logger logger = Logger.getLogger(PmcZzjImportProgress.class);
	private boolean isFinished = false;
	protected static int TASK_NUMBER = 32;
	protected static int INIT_TASK = 0;
	protected String fileUrl;
	protected ADTable adTable;
	protected List<PasErrorLog> errLogs;
	protected String errDetial;
	protected String materialId;
	protected boolean flag = true;
	protected ADManager adManager;
	protected PmcZzjImportSection importSection;
	protected List<PmcZzjImport> chfImports = new ArrayList<PmcZzjImport>();
	
	protected HSSFWorkbook hssfWb;
	public PmcZzjImportProgress(Object mps, String fileUrl, ADTable adTable,
			PmcZzjImportSection importSection) {
		this.fileUrl = fileUrl;
		this.adTable =adTable;
		this.importSection = importSection;
	}
	
	protected void executeOneWorked(HSSFRow row, IProgressMonitor monitor, int index) {
		PmcZzjImport chfImport = new PmcZzjImport();
		chfImport.setOrgRrn(Env.getOrgRrn());
		Date now = Env.getSysDate();
		try {
			// ��excel�е�i�е�������ݶ��뵽salePlanLine��
			readExcelRow(row, chfImport);
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_sale_data"), TASK_NUMBER, index - 1, materialId));
			
			// ��flagΪtrue, ��ʾ���ڵ���, ������ʾ, ���������Լ� 1
			if (flag) {
				if ( chfImport.getMaterialId()!=null) {
					chfImports.add(chfImport);
				} else {
					PasErrorLog elog = new PasErrorLog();
					elog.setMpsId(null);
					elog.setMaterialId(materialId);
					elog.setErrMessage("��");
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
	
	protected void readExcelRow(HSSFRow row, PmcZzjImport chfImport) {
		int j = 0;
		boolean ladingFlag = true;
		try {
			if (row != null) {
				for (; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);
//					if(j == 1||j==2||j==3||j==5||j==7){
//						
//					}else{
//						if(cell==null){
//							continue;
//						}
//						
//					}
					if(cell==null && j==1){
						continue;
					}
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						if(j == 0) {					
							materialId = cell.getStringCellValue();
							Material readMaterial = getMaterialById(materialId);
							if (readMaterial != null) {
								chfImport.setMaterialId(readMaterial.getMaterialId());
								chfImport.setMaterialName(readMaterial.getName());
							} else {
								flag = false;
								errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						} else if (j == 2) {				//��1��Ϊ��������
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(chfImport != null && chfImport!=null){
									BigDecimal qty = BigDecimal.valueOf(l);
									chfImport.setQty(qty);
								}else{
									flag = false;
									errDetial = "����: " + materialId + "������������ֵ���ͣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							} catch (Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "������������ֵ���ͣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if(j == 3) {			//��5��Ϊ��������,����Ϊ�ַ�������
							try{
								String inputDateStr=cell.getStringCellValue();
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								Date inputDate = format.parse(inputDateStr);
								if (inputDate != null) {						
									chfImport.setScheduleDate(inputDate);							
								}else{
									flag = false;
									errDetial = "Material: " + materialId + "'s Date is null"
									+ " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							}catch(Exception e){
								flag = false;
								errDetial = "Material: " + materialId + "'s Date Format is error"
								+ " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if(j == 4){
							String moId = cell.getStringCellValue();
							if (moId != null) {
								chfImport.setMoId(moId);
							} else {
//								flag = false;
//								errDetial = "����: " + moId + "��ϵͳ�в����ڣ�"
//								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if(j == 3){			//��������
							double d = cell.getNumericCellValue();
							try {
								Date inputDate = HSSFDateUtil.getJavaDate(d);
								if (inputDate != null) {
									chfImport.setScheduleDate(inputDate);
								} else {
									flag = false;
									errDetial = "���������: " + materialId + "������Ϊ�գ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							} catch(Exception e) {
								flag = false;
								errDetial = "���������: " + materialId + "�����ڸ�ʽ����"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						} else if(j == 2){
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									chfImport.setQty(qty);
								} else {
									flag = false;
									errDetial = "����: " + materialId + "����ʱ�ƻ�����Ϊ�գ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}								
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "����ʱ�ƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						} else if(j ==0){
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

							Material readMaterial = getMaterialById(materialId);
							if (readMaterial != null) {
								chfImport.setMaterialId(readMaterial.getMaterialId());
								chfImport.setMaterialName(readMaterial.getName());
							} else {
								flag = false;
								errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if(j == 4){
							double d = cell.getNumericCellValue();
							String moId = String.valueOf(d);
							if (moId != null) {
								chfImport.setMoId(moId);
							} else {
//								flag = false;
//								errDetial = "����: " + moId + "��ϵͳ�в����ڣ�"
//								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}   
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						if(j == 3) {//��Ԫ������Ϊ�ջ���ø÷���
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "��������Ϊ�ա�";							
						}
						else if(j == 0) {
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "�������ϱ��Ϊ�ա�";	
						}
						else if(j == 2) {
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "��������Ϊ�ա�";	
						}
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			if(j == 1||j==2||j==3||j==4||j==5){
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
			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {//�ӵڶ��п�ʼ����һ���Ǳ�ͷ
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

	public List<PmcZzjImport> getChfImports() {
		return chfImports;
	}

	public void setChfImports(List<PmcZzjImport> chfImports) {
		this.chfImports = chfImports;
	}
}

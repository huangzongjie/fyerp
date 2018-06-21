package com.graly.erp.wip.workcenter.schedule.purchase2;

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
import com.graly.erp.wip.model.TmpChfImport;
import com.graly.erp.wip.model.WorkShopSchedule;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class PurImportProgress  implements IRunnableWithProgress {
	private static final Logger logger = Logger.getLogger(PurImportProgress.class);
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
	protected PurImportSection importSection;
	protected List<TmpChfImport> chfImports = new ArrayList<TmpChfImport>();
	
	protected HSSFWorkbook hssfWb;
	public PurImportProgress(Object mps, String fileUrl, ADTable adTable,
			PurImportSection importSection) {
		this.fileUrl = fileUrl;
		this.adTable =adTable;
		this.importSection = importSection;
	}
	
	protected void executeOneWorked(HSSFRow row, IProgressMonitor monitor, int index) {
		TmpChfImport chfImport = new TmpChfImport();
		chfImport.setOrgRrn(Env.getOrgRrn());
		Date now = Env.getSysDate();
		try {
			// 将excel中第i行的相关数据读入到salePlanLine中
			readExcelRow(row, chfImport);
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_sale_data"), TASK_NUMBER, index - 1, materialId));
			
			// 若flag为true, 提示正在导入, 否则不提示, 但任务条仍加 1
			if (flag) {
				if ( chfImport.getMaterialId()!=null) {
					chfImports.add(chfImport);
				} else {
					PasErrorLog elog = new PasErrorLog();
					elog.setMpsId(null);
					elog.setMaterialId(materialId);
					elog.setErrMessage("空");
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
	
	protected void readExcelRow(HSSFRow row, TmpChfImport chfImport) {
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
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						if(j == 0) {					
							materialId = cell.getStringCellValue();
							Material readMaterial = getMaterialById(materialId);
							if (readMaterial != null) {
								chfImport.setMaterialId(readMaterial.getMaterialId());
							} else {
								flag = false;
								errDetial = "物料: " + materialId + "在系统中不存在；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						} else if (j == 1) {				//第1列为生产日期
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(chfImport != null && chfImport!=null){
									BigDecimal qty = BigDecimal.valueOf(l);
									chfImport.setQty(qty);
								}else{
									flag = false;
									errDetial = "物料: " + materialId + "的数量不是数值类型；"
									+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
								}
							} catch (Exception e) {
								flag = false;
								errDetial = "物料: " + materialId + "的数量不是数值类型；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						}else if(j == 2) {			//第5列为交货日期,不能为字符串类型
							flag = false;
							errDetial = "导入的物料: " + materialId + "的计划日期为不是为日期类型；"
							+ "在行：" + (row.getRowNum() + 1) + "， 列: " + (j + 1) + "。";
						} 
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if(j == 2){			//交货日期
							double d = cell.getNumericCellValue();
							try {
								Date inputDate = HSSFDateUtil.getJavaDate(d);
								if (inputDate != null) {
									chfImport.setScheduleDate(inputDate);
								} else {
									flag = false;
									errDetial = "临时计划的物料: " + materialId + "的交货日期为空；"
									+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
								}
							} catch(Exception e) {
								flag = false;
								errDetial = "临时计划的物料: " + materialId + "的交货日期格式错误；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						} else if(j == 1){
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									chfImport.setQty(qty);
								} else {
									flag = false;
									errDetial = "物料: " + materialId + "的临时计划数量为空；"
									+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
								}								
							} catch(Exception e) {
								flag = false;
								errDetial = "物料: " + materialId + "的临时计划数量错误；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						} else if(j ==0){
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							StringBuffer sb = new StringBuffer();
							// 不足8位的用零(0)补足至8位
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
							} else {
								flag = false;
								errDetial = "物料: " + materialId + "在系统中不存在；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						}   
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						if(j == 1) {//单元格内容为空会调用该方法
							flag = false;
							errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的生产日期为空。";							
						}
						else if(j == 3) {
							flag = false;
							errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的物料编号为空。";	
						}
						else if(j == 5) {
							flag = false;
							errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的数量为空。";	
						}
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
//			if(j == 1||j==2||j==3||j==5||j==7){
//				flag = false;
//				errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的单元格中值为空，请将该单元格删除。";
//			} 
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
			// 设置总任务数
			TASK_NUMBER = sheet.getPhysicalNumberOfRows() - 1;
			monitor.beginTask("  ", TASK_NUMBER);

			int finishedWorked = INIT_TASK;
			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {//从第二行开始，第一行是表头
				flag = true;
				errDetial = null;
				materialId = null;
				HSSFRow row = sheet.getRow(i);
				// 执行第i个任务
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
			HSSFWorkbook wb = new HSSFWorkbook(fs); // 读取excel工作簿
			this.hssfWb = wb;
			HSSFSheet sheet = wb.getSheetAt(0); // 读取excel的sheet
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

	public List<TmpChfImport> getChfImports() {
		return chfImports;
	}

	public void setChfImports(List<TmpChfImport> chfImports) {
		this.chfImports = chfImports;
	}
}

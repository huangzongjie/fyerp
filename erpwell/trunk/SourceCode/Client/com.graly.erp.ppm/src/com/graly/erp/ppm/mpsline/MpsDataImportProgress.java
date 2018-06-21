package com.graly.erp.ppm.mpsline;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class MpsDataImportProgress implements IRunnableWithProgress {
	private static final Logger logger = Logger.getLogger(MpsDataImportProgress.class);
	private MpsEntityBlock parentBlock;
	private boolean isFinished = false;
	protected static int TASK_NUMBER = 32;
	protected static int INIT_TASK = 0;
	protected Mps mps;
	protected String fileUrl;
	protected ADTable adTable;
	protected List<PasErrorLog> errLogs;
	protected String errDetial;
	protected String materialId;
	protected boolean flag = true;
	protected ADManager adManager;

	public MpsDataImportProgress() {
	}

	public MpsDataImportProgress(Mps mps, String fileUrl, ADTable adTable, MpsEntityBlock parentBlock) {
		this.mps = mps;
		this.fileUrl = fileUrl;
		this.adTable = adTable;
		this.parentBlock = parentBlock;
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
	
	protected void executeOneWorked(HSSFRow row, IProgressMonitor monitor, int index) {
		MpsLine mpsLine = new MpsLine();
		mpsLine.setMpsId(mps.getMpsId());
		mpsLine.setOrgRrn(Env.getOrgRrn());
		Date now = Env.getSysDate();
		try {
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_mps_data"), TASK_NUMBER, index - 1, materialId));
			// 将excel中第i行的相关数据读入到mpsLine中
			readExcelRow(row, mpsLine);
			// 若flag为true, 提示正在导入, 否则不提示, 但任务条仍加 1
			if (flag) {
				if (mpsLine.getMaterialRrn() != null && mpsLine.getPriority() != null
						&& mpsLine.getQtyMps() != null && mpsLine.getDateDelivered() != null) {
					PPMManager ppmManager = Framework.getService(PPMManager.class);
					ppmManager.saveMpsLine(adTable.getObjectRrn(), mpsLine, Env.getUserRrn());
				}
			} else {
				PasErrorLog elog = new PasErrorLog();
				elog.setMpsId(mps.getMpsId());
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
			log.setMpsId(mps.getMpsId());
			log.setMaterialId(materialId);
			log.setErrMessage(msg);
			log.setErrDate(now);
			errLogs.add(log);
			logger.error("Error at SaleDataImportProgress executeOneWorked(): " , e);
		}
	}

	public HSSFSheet initExcelread(FileInputStream fis) {
		try {
			POIFSFileSystem fs = new POIFSFileSystem(fis);
			HSSFWorkbook wb = new HSSFWorkbook(fs); // 读取excel工作簿
			HSSFSheet sheet = wb.getSheetAt(0); // 读取excel的sheet
			return sheet;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void readExcelRow(HSSFRow row, MpsLine mpsLine) {
		try {
			if (row != null) {
				for (int j = 0; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);

					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						if (j == 0) {
							materialId = cell.getStringCellValue();
							Material material = getMaterialById(materialId);
							if (material != null) {
								mpsLine.setMaterialRrn(material.getObjectRrn());
								mpsLine.setUomId(material.getInventoryUom());
							} else {
								flag = false;
								errDetial = "Material: " + materialId + " is not exist"
								 + " at line: " + row.getRowNum() + ", column: " + (j + 1) + ".";
							}
						} 
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if (j == 0) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							materialId = String.valueOf(parse.format(d));

							Material material = getMaterialById(materialId);
							if (material != null) {
								mpsLine.setMaterialRrn(material.getObjectRrn());
								mpsLine.setUomId(material.getInventoryUom());
							} else {
								flag = false;
								errDetial = "Material: " + materialId + " not exist"
								+ " at line: " + row.getRowNum() + ", column: " + (j + 1) + ".";
							}
						}else if (j == 1) {
							BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
							if (qty != null) {
								mpsLine.setQtyMps(qty);
							} else {
								flag = false;
								errDetial = "Material: " + materialId + "'s quantity is null"
								+ " at line: " + row.getRowNum() + ", column: " + (j + 1) + ".";
							}
						}else if (j == 2) {
							double d = cell.getNumericCellValue();
							Date inputDate = HSSFDateUtil.getJavaDate(d);
							if (inputDate != null) {
								if(inputDate.compareTo(mps.getDateStart()) >= 0 && inputDate.compareTo(mps.getDateEnd()) <= 0){
									mpsLine.setDateDelivered(inputDate);
								}else{
									flag = false;
									errDetial = "Material: " + materialId + "'s date is slop over"
									+ " at line: " + row.getRowNum() + ", column: " + (j + 1) + ".";
								}
							} else {
								flag = false;
								errDetial = "Material: " + materialId + "'s Date Format is error"
								+ " at line: " + row.getRowNum() + ", column: " + (j + 1) + ".";
							}
						}else if (j == 3) {
							Double d = cell.getNumericCellValue();
							if (d != null) {
								mpsLine.setPriority(d.longValue());
							} else {
								flag = false;
								errDetial = "Material: " + materialId + "'s saleType is null"
								+ " at line: " + row.getRowNum() + ", column: " + (j + 1) + ".";
							}
						}
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			flag = false;
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

}

package com.graly.erp.ppm.lading;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.eclipse.core.runtime.IProgressMonitor;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Lading;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.mpsline.MpsDataImportProgress;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class LadingDataImportProgress extends MpsDataImportProgress {
	private static final Logger logger = Logger.getLogger(LadingDataImportProgress.class);
	LadingEntityBlock parentBlock;
	
	public LadingDataImportProgress(Mps mps, String fileUrl, ADTable adTable, LadingEntityBlock parentBlock) {
		super(mps, fileUrl, adTable, null);
		this.parentBlock = parentBlock;
	}

	protected void executeOneWorked(HSSFRow row, IProgressMonitor monitor, int index) {
		Lading lading = new Lading();
		lading.setMpsId(mps.getMpsId());
		lading.setOrgRrn(Env.getOrgRrn());
		Date now = Env.getSysDate();
		try {
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_lading_data"), TASK_NUMBER, index - 1, materialId));
			// 将excel中第i行的相关数据读入到salePlanLine中
			readExcelRow(row, lading);
			// 若flag为true, 提示正在导入, 否则不提示, 但任务条仍加 1
			if (flag) {
				if(lading.getMaterialRrn() != null && lading.getQtyLading() != null){
					PPMManager ppmManager = Framework.getService(PPMManager.class);
					ppmManager.saveLading(adTable.getObjectRrn(), lading, Env.getUserRrn());
				}
			}else {
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

	private void readExcelRow(HSSFRow row, Lading lading) {
		int j = 0;
		try {
			if (row != null) {
				for (; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);

					switch (cell.getCellType()) {
			        case HSSFCell.CELL_TYPE_STRING:
			        	if (j == 0) {
							materialId = cell.getStringCellValue();
							Material material = getMaterialById(materialId);
							if (material != null) {
								lading.setMaterialRrn(material.getObjectRrn());
								lading.setUomId(material.getInventoryUom());
							} else {
								flag = false;
								errDetial = "Material: " + materialId + " is not exist"
								 + " at line: " + row.getRowNum() + ", column: " + (j + 1) + ".";
							}
						} 
			            break;   
			        case HSSFCell.CELL_TYPE_NUMERIC:   
			            if (j == 1) {
			            	Double d = cell.getNumericCellValue();
			            	Long qty = d.longValue();
							if(qty != null){
								lading.setQtyLading(qty);
							}else{
								flag = false;
								errDetial = "Material: " + materialId + "'s lading quantity is null"
								+ " at line: " + row.getRowNum() + ", column: " + (j + 1) + ".";
							}
						} else if (j == 0) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							materialId = String.valueOf(parse.format(d));

							Material material = getMaterialById(materialId);
							if (material != null) {
								lading.setMaterialRrn(material.getObjectRrn());
								lading.setUomId(material.getInventoryUom());
							} else {
								flag = false;
								errDetial = "Material: " + materialId + " is not exist"
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
			errDetial = "The cell is null at line: " + row.getRowNum() + ", column: " + (j + 1);
			flag = false;
			logger.error("Error at LadingDataImportProgress : readExcelRow() ", e);
		}
	}
}
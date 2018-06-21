package com.graly.erp.ppm.saleplan;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.eclipse.core.runtime.IProgressMonitor;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.model.SalePlanLine;
import com.graly.erp.ppm.mpsline.MpsDataImportProgress;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class SaleDataImportProgress extends MpsDataImportProgress {
	private static final Logger logger = Logger.getLogger(SaleDataImportProgress.class);
	SalePlanEntityBlock parentBlock;
	
	public SaleDataImportProgress(Mps mps, String fileUrl, ADTable adTable, SalePlanEntityBlock parentBlock) {
		super(mps, fileUrl, adTable, null);
		this.parentBlock = parentBlock;
	}

	protected void executeOneWorked(HSSFRow row, IProgressMonitor monitor, int index) {
		SalePlanLine salePlanLine = new SalePlanLine();
		salePlanLine.setMpsId(mps.getMpsId());
		salePlanLine.setOrgRrn(Env.getOrgRrn());
		Date now = Env.getSysDate();
		try {
			// 将excel中第i行的相关数据读入到salePlanLine中
			readExcelRow(row, salePlanLine);
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_sale_data"), TASK_NUMBER, index - 1, materialId));
			
			// 若flag为true, 提示正在导入, 否则不提示, 但任务条仍加 1
			if (flag) {
				if (salePlanLine.getMaterialRrn() != null && salePlanLine.getQtySalePlan() != null
						&& salePlanLine.getSalePlanType() != null && salePlanLine.getDateDelivered() != null) {
					PPMManager ppmManager = Framework.getService(PPMManager.class);
					ppmManager.saveSalePlanLine(adTable.getObjectRrn(), salePlanLine, Env.getUserRrn());
				}else{
					PasErrorLog elog = new PasErrorLog();
					elog.setMpsId(mps.getMpsId());
					elog.setMaterialId(materialId);
					elog.setErrMessage(errDetial);
					elog.setErrDate(now);
					errLogs.add(elog);
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
	
	protected void readExcelRow(HSSFRow row, SalePlanLine salePlanLine) {
		int j = 0;
		boolean ladingFlag = true;
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
								salePlanLine.setMaterialRrn(material.getObjectRrn());
								salePlanLine.setUomId(material.getInventoryUom());
								salePlanLine.setMaterialCategory1(material.getMaterialCategory1());
								salePlanLine.setMaterialCategory2(material.getMaterialCategory2());
								salePlanLine.setMaterialCategory3(material.getMaterialCategory3());
							} else {
								flag = false;
								errDetial = "Material: " + materialId + " is not exist"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if(j == 1){
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(l != null){
									BigDecimal qtySalePlan = BigDecimal.valueOf(l);
									salePlanLine.setQtySalePlan(qtySalePlan);
								}else{
									flag = false;
									errDetial = "Material: " + materialId + "'s qtySalePlan must be number!"
									 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							} catch (Exception e) {
								flag = false;
								errDetial = "Material: " + materialId + "'s qtySalePlan must be number!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if (j == 2) {
							String type = cell.getStringCellValue();
							if (type != null) {
								salePlanLine.setSalePlanType(type);
							} else {
								flag = false;
								errDetial = "Material: " + materialId + "'s saleType is null"
								+ " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if(j == 3){
							flag = false;
							errDetial = "Material: " + materialId + "'s deliver date must be a date type!"
							 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
						}else if(j == 4){
							try {
								Double l = Double.valueOf(cell.getStringCellValue());
								if(l != null){
									BigDecimal lading = BigDecimal.valueOf(l);
									salePlanLine.setQtyLading(lading);
								}else{
									flag = false;
									ladingFlag = false;
									errDetial = "Material: " + materialId + "'s qtyLading must be number!"
									 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							} catch (Exception e) {
								flag = false;
								ladingFlag = false;
								errDetial = "Material: " + materialId + "'s qtySalePlan must be number!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if (j == 5) {
							String comments = cell.getStringCellValue();
							if (comments != null) {
								salePlanLine.setComments(comments);
							} else{
								flag = false;
								ladingFlag = false;
								errDetial = "Material: " + materialId + "'s qtyLading must be number!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if(j == 5){
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String comments = String.valueOf(parse.format(d));
							salePlanLine.setComments(comments);
						}else if(j == 4) {
							Double d = cell.getNumericCellValue();
							if (d != null) {
								BigDecimal qtyLading = BigDecimal.valueOf(d);
								salePlanLine.setQtyLading(qtyLading);
							} 
						}else if (j == 3) {
							double d = cell.getNumericCellValue();
							Date inputDate = HSSFDateUtil.getJavaDate(d);
							if (inputDate != null) {
								if(inputDate.compareTo(mps.getDateStart()) >= 0 && inputDate.compareTo(mps.getDateEnd()) <= 0){
									salePlanLine.setDateDelivered(inputDate);
								}else{
									flag = false;
									errDetial = "Material: " + materialId + "'s deliver date is out of the plan time scrope"
									+ " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							} else {
								flag = false;
								errDetial = "Material: " + materialId + "'s Date Format is error"
								+ " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						} else if(j == 2){
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String salePlanType = String.valueOf(parse.format(d));
							salePlanLine.setSalePlanType(salePlanType);
						} else if (j == 1) {
							BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
							if (qty != null) {
								salePlanLine.setQtySalePlan(qty);
							} else {
								flag = false;
								errDetial = "Material: " + materialId + "'s sale quantity is null"
								+ " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						} else if (j == 0) {
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

							Material material = getMaterialById(materialId);
							if (material != null) {
								salePlanLine.setMaterialRrn(material.getObjectRrn());
								salePlanLine.setUomId(material.getInventoryUom());
								salePlanLine.setMaterialCategory1(material.getMaterialCategory1());
								salePlanLine.setMaterialCategory2(material.getMaterialCategory2());
								salePlanLine.setMaterialCategory3(material.getMaterialCategory3());
							} else {
								flag = false;
								errDetial = "Material: " + materialId + " is not exist"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
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
			if(j == 5){
			}else if(j == 4 && ladingFlag){
				flag = true;
			}else{
				errDetial = "The cell is null at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1);
				flag = false;
			}
		}
	}

}

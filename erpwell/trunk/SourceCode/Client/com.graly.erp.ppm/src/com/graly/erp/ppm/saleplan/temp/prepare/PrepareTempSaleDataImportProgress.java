package com.graly.erp.ppm.saleplan.temp.prepare;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.eclipse.core.runtime.IProgressMonitor;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.model.SalePlanLine;
import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.erp.ppm.saleplan.SaleDataImportProgress;
import com.graly.erp.ppm.saleplan.SalePlanEntityBlock;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class PrepareTempSaleDataImportProgress extends SaleDataImportProgress {
	private static final Logger logger = Logger.getLogger(PrepareTempSaleDataImportProgress.class);
	protected String tpsId;

	public PrepareTempSaleDataImportProgress(Mps mps, String fileUrl, ADTable adTable,
			SalePlanEntityBlock parentBlock) {
		super(mps, fileUrl, adTable, parentBlock);
	}
	
	protected void executeOneWorked(HSSFRow row, IProgressMonitor monitor, int index) {
		TpsLinePrepare tpsLine = new TpsLinePrepare();
		tpsLine.setOrgRrn(Env.getOrgRrn());
		tpsLine.setTpsStatus(TpsLinePrepare.TPSSTATUS_DRAFTED);
		Date now = Env.getSysDate();
		try {
			// 将excel中第i行的相关数据读入到salePlanLine中
			readExcelRow(row, tpsLine);
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_sale_data"), TASK_NUMBER, index - 1, materialId));
			
			// 若flag为true, 提示正在导入, 否则不提示, 但任务条仍加 1
			if (flag) {
				if (tpsLine.getMaterialRrn() != null && tpsLine.getQtyTps() != null
						&& tpsLine.getDateDelivered() != null) {
					if(adManager == null)
						adManager = Framework.getService(ADManager.class);
					adManager.saveEntity(adTable.getObjectRrn(), tpsLine, Env.getUserRrn());
				} else {
					
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
	
	protected void readExcelRow(HSSFRow row, TpsLinePrepare tpsLine) {
		int j = 0;
		boolean ladingFlag = true;
		try {
			if (row != null) {
				for (; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);

					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						if(j == 0) {						//第0列为临时计划编号
							tpsId = cell.getStringCellValue();
							if (tpsId != null) {
								tpsLine.setTpsId(tpsId);
							}
//							else {
//								flag = false;
//								errDetial = "物料: " + materialId + "的临时计划编号为空；"
//								+ "在行：" + (row.getRowNum() + 1) + ", 列: " + (j + 1) + ".";
//							}
						} else if (j == 1) {				//第1列为物料编号
							materialId = cell.getStringCellValue();
							Material material = getMaterialById(materialId);
							if (material != null) {
								tpsLine.setMaterialRrn(material.getObjectRrn());
								tpsLine.setMaterialId(material.getMaterialId());
								tpsLine.setMaterialName(material.getName());
								tpsLine.setUomId(material.getInventoryUom());
							} else {
								flag = false;
								errDetial = "物料: " + materialId + "在系统中不存在；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						} else if(j == 3) {					//第3列为生产数量,不导入第2列,第二列是物料名称,是给导入人员参考用的,以便筛选数据
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(l != null){
									BigDecimal qtyTps = BigDecimal.valueOf(l);
									tpsLine.setQtyTps(qtyTps);
								}else{
									flag = false;
									errDetial = "物料: " + materialId + "的生产数量不是数值类型；"
									+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
								}
							} catch (Exception e) {
								flag = false;
								errDetial = "物料: " + materialId + "的生产数量不是数值类型；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						} else if (j == 4) {				//第4列为销售类型
							String type = cell.getStringCellValue();
							if (type != null) {
								tpsLine.setSalePlanType(type);
							}
//							else {
//								flag = false;
//								errDetial = "物料: " + materialId + "的销售类型为空；"
//								+ "在行：" + (row.getRowNum() + 1) + ", 列: " + (j + 1) + ".";
//							}
						}else if(j == 5){					//第5列为订单编号
							String orderId = cell.getStringCellValue();
							if (orderId != null){
								tpsLine.setOrderId(orderId);
							}
						}else if(j == 6){					//第6列为客户名称
							String customerName = cell.getStringCellValue();
							if (customerName != null){
								tpsLine.setCustomerName(customerName);
							}
						}else if(j == 7){					//第7列为业务员
							String saler = cell.getStringCellValue();
							if (saler != null){
								tpsLine.setSaler(saler);
							}
						} else if(j == 8) {					//第8列为下单日期
//							flag = false;
//							errDetial = "物料: " + materialId + "的下单日期为不是为日期类型；"
//							+ "在行：" + (row.getRowNum() + 1) + ", 列: " + (j + 1) + ".";
						} else if(j == 9) {					//第9列为交货日期
							flag = false;
							errDetial = "物料: " + materialId + "的交货日期为不是为日期类型；"
							+ "在行：" + (row.getRowNum() + 1) + "， 列: " + (j + 1) + "。";
						} else if (j == 10) {				//第10列为备注
							String comments = cell.getStringCellValue();
							if (comments != null) {
								tpsLine.setComments(comments);
							}
						} else if (j == 11){
							String isStockUp = cell.getStringCellValue();
							if (isStockUp != null){
								if("Y".equals(isStockUp)){
									tpsLine.setIsStockUp(true);
								}
							}
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if(j == 11){
							
						} else if(j == 10) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String comments = String.valueOf(parse.format(d));
							tpsLine.setComments(comments);
						} else if(j == 9){
							double d = cell.getNumericCellValue();
							try {
								Date inputDate = HSSFDateUtil.getJavaDate(d);
								if (inputDate != null) {
									tpsLine.setDateDelivered(inputDate);
								} else {
									flag = false;
									errDetial = "物料: " + materialId + "的交货日期为空；"
									+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
								}
							} catch(Exception e) {
								flag = false;
								errDetial = "物料: " + materialId + "的交货日期格式错误；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						} else if(j == 8) {
							double d = cell.getNumericCellValue();
							try {
								Date inputDate = HSSFDateUtil.getJavaDate(d);
								if (inputDate != null) {
									tpsLine.setDateCreated(inputDate);
								}
							} catch(Exception e) {
								flag = false;
								errDetial = "物料: " + materialId + "的下单日期格式错误；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						} else if (j == 7) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String saler = String.valueOf(parse.format(d));
							tpsLine.setSaler(saler);
						} else if (j == 6) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String customerName = String.valueOf(parse.format(d));
							tpsLine.setCustomerName(customerName);
						} else if (j == 5) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String orderId = String.valueOf(parse.format(d));
							tpsLine.setOrderId(orderId);
						} else if (j == 4) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String salePlanType = String.valueOf(parse.format(d));
							tpsLine.setSalePlanType(salePlanType);
						} else if(j == 3){
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									tpsLine.setQtyTps(qty);
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
						} else if (j == 1) {
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
								tpsLine.setMaterialRrn(material.getObjectRrn());
								tpsLine.setMaterialId(material.getMaterialId());
								tpsLine.setMaterialName(material.getName());
								tpsLine.setUomId(material.getInventoryUom());
							} else {
								flag = false;
								errDetial = "物料: " + materialId + "在系统中不存在；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						} else if (j == 0) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String initId = String.valueOf(parse.format(d));
							if(initId != null)
								tpsLine.setTpsId(initId);
						}
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						if(j == 1) {
							flag = false;
							errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的物料编号为空。";							
						}
						else if(j == 3) {
							flag = false;
							errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的临时计划数量为空。";	
						}
						else if(j == 9) {
							flag = false;
							errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的交货日期为空。";	
						}
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			if(j == 10){
				//如果是备注为空可以忽略
			}else if(j == 9){
				//如果是交货日期为空可以忽略
			}else if(j == 8 && ladingFlag){
				flag = true;
			}else{
				flag = false;
				errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的单元格中值为空，请将该单元格删除。";
			}
		}
		
		
	}
}

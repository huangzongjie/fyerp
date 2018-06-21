package com.graly.erp.ppm.yn.mpsline;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.eclipse.core.runtime.IProgressMonitor;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.mpsline.MpsDataImportProgress;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;

public class YnMpsChangeQtyImportProgress extends MpsDataImportProgress {
	private static final Logger logger = Logger.getLogger(YnMpsChangeQtyImportProgress.class);
	protected String mpsId;
	protected List<MpsLine> mpsLines= new ArrayList<MpsLine>();//EXCEL中与系统中一致的主计划行
	protected List<MpsLine> perMpsLines = new ArrayList<MpsLine>();//EXCEL中存在，数据库中不存在
	

	public YnMpsChangeQtyImportProgress(Mps mps, String fileUrl, ADTable adTable,
			YnMpsEntityBlock mpsEntityBlock) {
//		super(mps, fileUrl, adTable, parentBlock);
		super(mps, fileUrl, adTable, null);
	}
	
	protected void executeOneWorked(HSSFRow row, IProgressMonitor monitor, int index) {
		MpsLine rowMpsLine = null;//主计划行
		Date now = Env.getSysDate();
		try {
			// 将excel中第i行的相关数据读入到salePlanLine中
			rowMpsLine = thisReadExcelRow(row, rowMpsLine);
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_sale_data"), TASK_NUMBER, index - 1, materialId));
			
			// 若flag为true, 提示正在导入, 否则不提示, 但任务条仍加 1
			if (flag) {
				if(rowMpsLine.getObjectRrn() != null){
					mpsLines.add(rowMpsLine);
				}else{
					perMpsLines.add(rowMpsLine);
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
			log.setMpsId(null);
			log.setMaterialId(materialId);
			log.setErrMessage(msg);
			log.setErrDate(now);
			errLogs.add(log);
			logger.error("Error at TempSaleDataImportProgress executeOneWorked(): " , e);
		}
	}
	
 
	protected MpsLine thisReadExcelRow(HSSFRow row, MpsLine rowMpsLine) {
		int j = 0;
		boolean ladingFlag = true;
		try {
			if (row != null) {
				for (; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);
					String mpsId =this.mps.getMpsId();
					Long materialRrn =null;
					
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						if (j == 0) {				//第0列为物料编号
							materialId = cell.getStringCellValue();
							Material material = getMaterialById(materialId);
							String whereClause = "mpsId = '"+mpsId+"' " +
									" AND materialRrn = "+(material!=null ?material.getObjectRrn() :0);
							List<MpsLine> mpsLines =  adManager.getEntityList(Env.getOrgRrn(),MpsLine.class ,Integer.MAX_VALUE,
									whereClause,null);
							if (material != null && mpsLines.size() > 0 ) {
								rowMpsLine = mpsLines.get(0);
								rowMpsLine.setQtyMps(BigDecimal.ZERO);
							} else if(material != null && mpsLines.size() ==0){
								rowMpsLine = new MpsLine();
								rowMpsLine.setMpsId(mpsId);
								rowMpsLine.setMaterialRrn(material.getObjectRrn());
								rowMpsLine.setMaterial(material);
								rowMpsLine.setQtyMps(BigDecimal.ZERO);
							} else {
								flag = false;
								errDetial = "临时计划的物料: " + materialId + "的在系统中不存在；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						} else if(j == 1) {			//第二列是物料名称,是给导入人员参考用的,以便筛选数据,不导入
						
						} else if(j == 2) {			//第3列为生产数量,不导入第2列,第二列是物料名称,是给导入人员参考用的,以便筛选数据
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(l != null && rowMpsLine!=null){
									BigDecimal qtyMps = BigDecimal.valueOf(l);
									rowMpsLine.setQtyMps(qtyMps);
								}else{
									flag = false;
									errDetial = "临时计划的物料: " + materialId + "的生产数量不是数值类型；"
									+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
								}
							} catch (Exception e) {
								flag = false;
								errDetial = "临时计划的物料: " + materialId + "的生产数量不是数值类型；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						}else if(j == 3) {			//第4列为订单编号，
							String orderId = cell.getStringCellValue();
							if(orderId != null){
								rowMpsLine.setOrderId(orderId);
							} 
						}else if(j == 4) {			//第5列为交货日期,不能为字符串类型
							flag = false;
							errDetial = "临时计划的物料: " + materialId + "的交货日期为不是为日期类型；"
							+ "在行：" + (row.getRowNum() + 1) + "， 列: " + (j + 1) + "。";
						}else if(j == 5) {			//第6列为备注
							String comments = cell.getStringCellValue();
							if(comments != null){
								rowMpsLine.setComments(comments);
							}
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if(j == 5){					//备注
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String comments = String.valueOf(parse.format(d));
							rowMpsLine.setComments(comments);
						}else if(j == 4){			//交货日期
							double d = cell.getNumericCellValue();
							try {
								Date inputDate = HSSFDateUtil.getJavaDate(d);
								if (inputDate != null) {
									rowMpsLine.setDateDelivered(inputDate);
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
						}else if(j == 3){			//订单编号
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String orderId = String.valueOf(parse.format(d));
							rowMpsLine.setComments(orderId);
						}else if(j == 2){			//数量
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									rowMpsLine.setQtyMps(qty);
								} else {
									flag = false;
									errDetial = "临时计划的物料: " + materialId + "的数量为空；"
									+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
								}								
							} catch(Exception e) {
								flag = false;
								errDetial = "临时计划的物料: " + materialId + "的数量错误；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						}else if(j == 1){
							 //物料名称
						}else if (j ==0) {
							try{
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
								
								String whereClause = "mpsId = '"+mpsId+"' " +
								" AND materialRrn = "+(material!=null ?material.getObjectRrn() :0);
								List<MpsLine> mpsLines =  adManager.getEntityList(Env.getOrgRrn(),MpsLine.class ,
										Integer.MAX_VALUE,whereClause,null);
								
								if (material != null && mpsLines.size() >0) {//童庆飞要求，如果不存在则添加,否则设置0，用EXCEL数量取代
									rowMpsLine = mpsLines.get(0);
									rowMpsLine.setQtyMps(BigDecimal.ZERO);
								} else if(material != null && mpsLines.size() ==0){
									rowMpsLine = new MpsLine();
									rowMpsLine.setMpsId(mpsId);
									rowMpsLine.setMaterialRrn(material.getObjectRrn());
									rowMpsLine.setMaterial(material);
									rowMpsLine.setQtyMps(BigDecimal.ZERO);
								}else {
									flag = false;
									errDetial = "临时计划的物料: " + materialId + "的在系统中不存在；"
									+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
								}
							}catch(Exception e){
								flag = false;
								errDetial = "临时计划的物料: " + materialId + "的物料编号错误；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						}
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						if(j == 0) {
							flag = false;
							errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的临时计划物料编号为空。";							
						}else if(j == 1) {
							//物料名称
						}else if(j == 2) {
							flag = false;
							errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的临时计划数量为空。";	
						}else if(j == 3) {
							//订单编号
						}else if(j == 4) {
							flag = false;
							errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的临时计划交货日期为空。";	
						}else if(j == 5) {
							//备注
						}
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			if(j == 5){		  //备注
			}else if(j == 3){ //订单编号
			}else if(j == 1){ //物料名称
			}else{
				flag = false;
				errDetial = "在行：" + (row.getRowNum() + 1) + "，列：" + (j + 1) + "处的单元格中不能为空。";
			}
		}
		return rowMpsLine;
	}

	public List<MpsLine> getMpsLines() {
		return mpsLines;
	}

	public void setMpsLines(List<MpsLine> mpsLines) {
		this.mpsLines = mpsLines;
	}

	public List<MpsLine> getPerMpsLines() {
		return perMpsLines;
	}

	public void setPerMpsLines(List<MpsLine> perMpsLines) {
		this.perMpsLines = perMpsLines;
	}
	
/**备份代码，第一行为计划编号，第二行为物料编号，第三行为数量
	protected void readExcelRow(HSSFRow row, Mps mps) {
		int j = 0;
		boolean ladingFlag = true;
		try {
			if (row != null) {
				for (; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);
					String mpsId =null;
					Long materialRrn =null;
					
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						if(j == 0) {						//第0列为导入计划编号
							mpsId = cell.getStringCellValue();
							if (mpsId != null) {
								ADManager adManager = Framework.getService(ADManager.class);
								List<Mps> mpses =  adManager.getEntityList(Env.getOrgRrn(),
										Mps.class ,Integer.MAX_VALUE,"mpsId = '"+mpsId+"'",null);
								if( mpses!=null && mpses.size() > 0 ){
									mps = mpses.get(0);
								}
							}else {
								flag = false;
								errDetial = "物料: " + materialId + "的计划编号为空；"
								+ "在行：" + (row.getRowNum() + 1) + ", 列: " + (j + 1) + ".";
							}
						} else if (j == 1) {				//第1列为物料编号
							materialId = cell.getStringCellValue();
							Material material = getMaterialById(materialId);
							String whereClause = "mpsId = '"+mpsId+"' " +
									" AND materialRrn = "+(material!=null ?material.getObjectRrn() :0);
							List<MpsLine> mpsLines =  adManager.getEntityList(Env.getOrgRrn(),MpsLine.class ,Integer.MAX_VALUE,
									whereClause,null);
							if (material != null && mpsLines.size() > 0 ) {
								mpsLine = mpsLines.get(0);
							} else {
								flag = false;
								errDetial = "物料: " + materialId + "在系统中不存在；"
								+ "在行：" + (row.getRowNum() + 1) + "， 列：" + (j + 1) + "。";
							}
						} else if(j == 2) {					//第3列为生产数量,不导入第2列,第二列是物料名称,是给导入人员参考用的,以便筛选数据
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(l != null && mpsLine!=null){
									BigDecimal qtyMps = BigDecimal.valueOf(l);
									mpsLine.setQtyMps(qtyMps);
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
						} 
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if(j == 2){
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									mpsLine.setQtyMps(qty);
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
							
							String whereClause = "mpsId = '"+mpsId+"' " +
							" AND materialRrn = "+(material!=null ?material.getObjectRrn() :0);
							List<MpsLine> mpsLines =  adManager.getEntityList(Env.getOrgRrn(),MpsLine.class ,
									Integer.MAX_VALUE,whereClause,null);
					
							if (material != null && mpsLines.size() >0) {
								mpsLine= mpsLines.get(0);
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
								mpsId = initId;
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
*/
}

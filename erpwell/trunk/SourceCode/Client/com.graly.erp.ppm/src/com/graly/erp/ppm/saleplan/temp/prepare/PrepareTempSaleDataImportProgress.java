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
			// ��excel�е�i�е�������ݶ��뵽salePlanLine��
			readExcelRow(row, tpsLine);
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_sale_data"), TASK_NUMBER, index - 1, materialId));
			
			// ��flagΪtrue, ��ʾ���ڵ���, ������ʾ, ���������Լ� 1
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
						if(j == 0) {						//��0��Ϊ��ʱ�ƻ����
							tpsId = cell.getStringCellValue();
							if (tpsId != null) {
								tpsLine.setTpsId(tpsId);
							}
//							else {
//								flag = false;
//								errDetial = "����: " + materialId + "����ʱ�ƻ����Ϊ�գ�"
//								+ "���У�" + (row.getRowNum() + 1) + ", ��: " + (j + 1) + ".";
//							}
						} else if (j == 1) {				//��1��Ϊ���ϱ��
							materialId = cell.getStringCellValue();
							Material material = getMaterialById(materialId);
							if (material != null) {
								tpsLine.setMaterialRrn(material.getObjectRrn());
								tpsLine.setMaterialId(material.getMaterialId());
								tpsLine.setMaterialName(material.getName());
								tpsLine.setUomId(material.getInventoryUom());
							} else {
								flag = false;
								errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						} else if(j == 3) {					//��3��Ϊ��������,�������2��,�ڶ�������������,�Ǹ�������Ա�ο��õ�,�Ա�ɸѡ����
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(l != null){
									BigDecimal qtyTps = BigDecimal.valueOf(l);
									tpsLine.setQtyTps(qtyTps);
								}else{
									flag = false;
									errDetial = "����: " + materialId + "����������������ֵ���ͣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							} catch (Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "����������������ֵ���ͣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						} else if (j == 4) {				//��4��Ϊ��������
							String type = cell.getStringCellValue();
							if (type != null) {
								tpsLine.setSalePlanType(type);
							}
//							else {
//								flag = false;
//								errDetial = "����: " + materialId + "����������Ϊ�գ�"
//								+ "���У�" + (row.getRowNum() + 1) + ", ��: " + (j + 1) + ".";
//							}
						}else if(j == 5){					//��5��Ϊ�������
							String orderId = cell.getStringCellValue();
							if (orderId != null){
								tpsLine.setOrderId(orderId);
							}
						}else if(j == 6){					//��6��Ϊ�ͻ�����
							String customerName = cell.getStringCellValue();
							if (customerName != null){
								tpsLine.setCustomerName(customerName);
							}
						}else if(j == 7){					//��7��Ϊҵ��Ա
							String saler = cell.getStringCellValue();
							if (saler != null){
								tpsLine.setSaler(saler);
							}
						} else if(j == 8) {					//��8��Ϊ�µ�����
//							flag = false;
//							errDetial = "����: " + materialId + "���µ�����Ϊ����Ϊ�������ͣ�"
//							+ "���У�" + (row.getRowNum() + 1) + ", ��: " + (j + 1) + ".";
						} else if(j == 9) {					//��9��Ϊ��������
							flag = false;
							errDetial = "����: " + materialId + "�Ľ�������Ϊ����Ϊ�������ͣ�"
							+ "���У�" + (row.getRowNum() + 1) + "�� ��: " + (j + 1) + "��";
						} else if (j == 10) {				//��10��Ϊ��ע
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
									errDetial = "����: " + materialId + "�Ľ�������Ϊ�գ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�Ľ������ڸ�ʽ����"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
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
								errDetial = "����: " + materialId + "���µ����ڸ�ʽ����"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
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
									errDetial = "����: " + materialId + "����ʱ�ƻ�����Ϊ�գ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}								
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "����ʱ�ƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						} else if (j == 1) {
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

							Material material = getMaterialById(materialId);
							if (material != null) {
								tpsLine.setMaterialRrn(material.getObjectRrn());
								tpsLine.setMaterialId(material.getMaterialId());
								tpsLine.setMaterialName(material.getName());
								tpsLine.setUomId(material.getInventoryUom());
							} else {
								flag = false;
								errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
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
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "�������ϱ��Ϊ�ա�";							
						}
						else if(j == 3) {
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "������ʱ�ƻ�����Ϊ�ա�";	
						}
						else if(j == 9) {
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "���Ľ�������Ϊ�ա�";	
						}
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			if(j == 10){
				//����Ǳ�עΪ�տ��Ժ���
			}else if(j == 9){
				//����ǽ�������Ϊ�տ��Ժ���
			}else if(j == 8 && ladingFlag){
				flag = true;
			}else{
				flag = false;
				errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "���ĵ�Ԫ����ֵΪ�գ��뽫�õ�Ԫ��ɾ����";
			}
		}
		
		
	}
}

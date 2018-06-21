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
	protected List<MpsLine> mpsLines= new ArrayList<MpsLine>();//EXCEL����ϵͳ��һ�µ����ƻ���
	protected List<MpsLine> perMpsLines = new ArrayList<MpsLine>();//EXCEL�д��ڣ����ݿ��в�����
	

	public YnMpsChangeQtyImportProgress(Mps mps, String fileUrl, ADTable adTable,
			YnMpsEntityBlock mpsEntityBlock) {
//		super(mps, fileUrl, adTable, parentBlock);
		super(mps, fileUrl, adTable, null);
	}
	
	protected void executeOneWorked(HSSFRow row, IProgressMonitor monitor, int index) {
		MpsLine rowMpsLine = null;//���ƻ���
		Date now = Env.getSysDate();
		try {
			// ��excel�е�i�е�������ݶ��뵽salePlanLine��
			rowMpsLine = thisReadExcelRow(row, rowMpsLine);
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_sale_data"), TASK_NUMBER, index - 1, materialId));
			
			// ��flagΪtrue, ��ʾ���ڵ���, ������ʾ, ���������Լ� 1
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
						if (j == 0) {				//��0��Ϊ���ϱ��
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
								errDetial = "��ʱ�ƻ�������: " + materialId + "����ϵͳ�в����ڣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						} else if(j == 1) {			//�ڶ�������������,�Ǹ�������Ա�ο��õ�,�Ա�ɸѡ����,������
						
						} else if(j == 2) {			//��3��Ϊ��������,�������2��,�ڶ�������������,�Ǹ�������Ա�ο��õ�,�Ա�ɸѡ����
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(l != null && rowMpsLine!=null){
									BigDecimal qtyMps = BigDecimal.valueOf(l);
									rowMpsLine.setQtyMps(qtyMps);
								}else{
									flag = false;
									errDetial = "��ʱ�ƻ�������: " + materialId + "����������������ֵ���ͣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							} catch (Exception e) {
								flag = false;
								errDetial = "��ʱ�ƻ�������: " + materialId + "����������������ֵ���ͣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if(j == 3) {			//��4��Ϊ������ţ�
							String orderId = cell.getStringCellValue();
							if(orderId != null){
								rowMpsLine.setOrderId(orderId);
							} 
						}else if(j == 4) {			//��5��Ϊ��������,����Ϊ�ַ�������
							flag = false;
							errDetial = "��ʱ�ƻ�������: " + materialId + "�Ľ�������Ϊ����Ϊ�������ͣ�"
							+ "���У�" + (row.getRowNum() + 1) + "�� ��: " + (j + 1) + "��";
						}else if(j == 5) {			//��6��Ϊ��ע
							String comments = cell.getStringCellValue();
							if(comments != null){
								rowMpsLine.setComments(comments);
							}
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if(j == 5){					//��ע
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String comments = String.valueOf(parse.format(d));
							rowMpsLine.setComments(comments);
						}else if(j == 4){			//��������
							double d = cell.getNumericCellValue();
							try {
								Date inputDate = HSSFDateUtil.getJavaDate(d);
								if (inputDate != null) {
									rowMpsLine.setDateDelivered(inputDate);
								} else {
									flag = false;
									errDetial = "��ʱ�ƻ�������: " + materialId + "�Ľ�������Ϊ�գ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							} catch(Exception e) {
								flag = false;
								errDetial = "��ʱ�ƻ�������: " + materialId + "�Ľ������ڸ�ʽ����"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if(j == 3){			//�������
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String orderId = String.valueOf(parse.format(d));
							rowMpsLine.setComments(orderId);
						}else if(j == 2){			//����
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									rowMpsLine.setQtyMps(qty);
								} else {
									flag = false;
									errDetial = "��ʱ�ƻ�������: " + materialId + "������Ϊ�գ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}								
							} catch(Exception e) {
								flag = false;
								errDetial = "��ʱ�ƻ�������: " + materialId + "����������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if(j == 1){
							 //��������
						}else if (j ==0) {
							try{
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
								
								String whereClause = "mpsId = '"+mpsId+"' " +
								" AND materialRrn = "+(material!=null ?material.getObjectRrn() :0);
								List<MpsLine> mpsLines =  adManager.getEntityList(Env.getOrgRrn(),MpsLine.class ,
										Integer.MAX_VALUE,whereClause,null);
								
								if (material != null && mpsLines.size() >0) {//ͯ���Ҫ����������������,��������0����EXCEL����ȡ��
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
									errDetial = "��ʱ�ƻ�������: " + materialId + "����ϵͳ�в����ڣ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}
							}catch(Exception e){
								flag = false;
								errDetial = "��ʱ�ƻ�������: " + materialId + "�����ϱ�Ŵ���"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						if(j == 0) {
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "������ʱ�ƻ����ϱ��Ϊ�ա�";							
						}else if(j == 1) {
							//��������
						}else if(j == 2) {
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "������ʱ�ƻ�����Ϊ�ա�";	
						}else if(j == 3) {
							//�������
						}else if(j == 4) {
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "������ʱ�ƻ���������Ϊ�ա�";	
						}else if(j == 5) {
							//��ע
						}
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			if(j == 5){		  //��ע
			}else if(j == 3){ //�������
			}else if(j == 1){ //��������
			}else{
				flag = false;
				errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "���ĵ�Ԫ���в���Ϊ�ա�";
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
	
/**���ݴ��룬��һ��Ϊ�ƻ���ţ��ڶ���Ϊ���ϱ�ţ�������Ϊ����
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
						if(j == 0) {						//��0��Ϊ����ƻ����
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
								errDetial = "����: " + materialId + "�ļƻ����Ϊ�գ�"
								+ "���У�" + (row.getRowNum() + 1) + ", ��: " + (j + 1) + ".";
							}
						} else if (j == 1) {				//��1��Ϊ���ϱ��
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
								errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						} else if(j == 2) {					//��3��Ϊ��������,�������2��,�ڶ�������������,�Ǹ�������Ա�ο��õ�,�Ա�ɸѡ����
							try {
								Long l = Long.valueOf(cell.getStringCellValue());
								if(l != null && mpsLine!=null){
									BigDecimal qtyMps = BigDecimal.valueOf(l);
									mpsLine.setQtyMps(qtyMps);
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
							
							String whereClause = "mpsId = '"+mpsId+"' " +
							" AND materialRrn = "+(material!=null ?material.getObjectRrn() :0);
							List<MpsLine> mpsLines =  adManager.getEntityList(Env.getOrgRrn(),MpsLine.class ,
									Integer.MAX_VALUE,whereClause,null);
					
							if (material != null && mpsLines.size() >0) {
								mpsLine= mpsLines.get(0);
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
								mpsId = initId;
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
*/
}

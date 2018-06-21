package com.graly.erp.wip.workcenter.schedule.into;

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
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class WorkScheduleImportProgress2  implements IRunnableWithProgress {
	private static final Logger logger = Logger.getLogger(WorkScheduleImportProgress2.class);
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
	protected WorkScheduleImportSection importSection;
	protected List<WorkShopSchedule> workShopSchedules = new ArrayList<WorkShopSchedule>();
	
	protected HSSFWorkbook hssfWb;
	public WorkScheduleImportProgress2(Object mps, String fileUrl, ADTable adTable,
			WorkScheduleImportSection importSection) {
		this.fileUrl = fileUrl;
		this.adTable =adTable;
		this.importSection = importSection;
	}
	
	protected void executeOneWorked(HSSFRow row, IProgressMonitor monitor, int index) {
		WorkShopSchedule workShopSchedule = new WorkShopSchedule();
		workShopSchedule.setOrgRrn(Env.getOrgRrn());
		workShopSchedule.setDocStatus("START");
		workShopSchedule.setWorkcenterRrn(importSection.getWorkCenterRrn());
		Date now = Env.getSysDate();
		try {
			// ��excel�е�i�е�������ݶ��뵽salePlanLine��
			readExcelRow(row, workShopSchedule);
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_sale_data"), TASK_NUMBER, index - 1, materialId));
			
			// ��flagΪtrue, ��ʾ���ڵ���, ������ʾ, ���������Լ� 1
			if (flag) {
				if (workShopSchedule.getScheduleDate()!=null && workShopSchedule.getMaterialRrn() != null && workShopSchedule.getQtyPlanProcuct() != null
						&& workShopSchedule != null) {
//					if(adManager == null)
//						adManager = Framework.getService(ADManager.class);
//					adManager.saveEntity(adTable.getObjectRrn(), workShopSchedule, Env.getUserRrn());
					workShopSchedules.add(workShopSchedule);
				} else {
					PasErrorLog elog = new PasErrorLog();
					elog.setMpsId(null);
					elog.setMaterialId(workShopSchedule.getMaterialId());
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
	
	protected void readExcelRow(HSSFRow row, WorkShopSchedule workShopSchedule) {
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
						if(j == 0) {						//��0��Ϊװ������
							String date = cell.getStringCellValue();
//							HSSFCellStyle xs = (HSSFCellStyle) cell.getCellStyle();
//							HSSFColor c =hssfWb.getCustomPalette().getColor(xs.getFillBackgroundColor());
//							c.getTriplet();
//							HSSFColor c2 =hssfWb.getCustomPalette().getColor(xs.getFillForegroundColor());
//							short[] s = c2.getTriplet();
							workShopSchedule.setWsDateDelivery(date);
						} else if (j == 1) {				//��1��Ϊ��������
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
									workShopSchedule.setScheduleDate(date);
								}
								
							}
							else {
								flag = false;
								errDetial = "����: " + materialId + "����������Ϊ�գ�"
								+ "���У�" + (row.getRowNum() + 1) + ", ��: " + (j + 1) + ".";
							}
						} else if(j ==2) {					//��2��Ϊ������
//							String moId = cell.getStringCellValue();
//							ADManager adManager = Framework.getService(ADManager.class);
//							List<ManufactureOrder> mos= adManager.getEntityList(Env.getOrgRrn(), ManufactureOrder.class,Integer.MAX_VALUE,"docId='"+moId+"'",null);
//							if (mos != null && mos.size() > 0 ) {
//								ManufactureOrder mo = mos.get(0);
//								workShopSchedule.setMoId(mo.getDocId());
//								workShopSchedule.setMoRrn(mo.getObjectRrn());
//								workShopSchedule.setQtyReceive(mo.getQtyReceive());
//								workShopSchedule.setQtyProcuct(mo.getQtyProduct());
//								workShopSchedule.setWorkcenterRrn(mo.getWorkCenterRrn());
//								workShopSchedule.setWorkcenterId(mo.getWorkCenterId());
//								workShopSchedule.setDatePlan(mo.getDateDelivery());
//							} else {
//								flag = false;
//								errDetial = "����: " + materialId + "�Ĺ�������ڣ�"
//								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
//							}
						} else if (j == 3) {				//��3�����ϱ��
							materialId = cell.getStringCellValue();
							Material material = getMaterialById(materialId);
							if (material != null) {
								workShopSchedule.setMaterialRrn(material.getObjectRrn());
								workShopSchedule.setMaterialId(material.getMaterialId());
								workShopSchedule.setMaterialName(material.getName());
							} else {
								flag = false;
								errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if(j == 4){					//��4Ϊ��������
						}else if(j == 5){					//��5�н�����
							String planQty = cell.getStringCellValue();
							if (planQty != null){
								BigDecimal qty = new BigDecimal(planQty);
								workShopSchedule.setQtyPlanProcuct(qty);
							}else{
								flag = false;
								errDetial = "����: " + materialId + "���������⣻"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						}else if(j == 6){					//��6��Ϊ������
//							String datePlan = cell.getStringCellValue();
//							workShopSchedule.setDatePlan(datePlan);
						} else if(j == 7) {					//��7�пͻ�
							String custom = cell.getStringCellValue();
							workShopSchedule.setCustomer(custom);
						} else if(j == 8) {					//��8������Ͱ
							String workCenter = cell.getStringCellValue();
							workShopSchedule.setWorkCenter(workCenter);
							setColor(workShopSchedule,cell,j);
						} else if (j == 9) {//��9���Ʒ�
							String workCenter2 = cell.getStringCellValue();
							workShopSchedule.setWorkCenter2(workCenter2);
							setColor(workShopSchedule,cell,j);
						} else if (j == 10){//��10���
						 	String workCenter3 = cell.getStringCellValue();
							workShopSchedule.setWorkCenter3(workCenter3);
							setColor(workShopSchedule,cell,j);
						}else if(j == 11){//11��11ֽ��
							String workCenter4 = cell.getStringCellValue();
							workShopSchedule.setWorkCenter4(workCenter4);
							setColor(workShopSchedule,cell,j);
						}else if(j == 12){//��ע
							String comments = cell.getStringCellValue();
							workShopSchedule.setComments(comments);
						}
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if(j == 12){
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String comments = String.valueOf(parse.format(d));
							workShopSchedule.setComments(comments);
						} else if(j == 11) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String workCenter4 = String.valueOf(parse.format(d));
							workShopSchedule.setWorkCenter4(workCenter4);
							setColor(workShopSchedule,cell,j);
						} else if(j == 10){
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String workCenter3 = String.valueOf(parse.format(d));
							workShopSchedule.setWorkCenter3(workCenter3);
							setColor(workShopSchedule,cell,j);
//							double d = cell.getNumericCellValue();
//							try {
//								Date inputDate = HSSFDateUtil.getJavaDate(d);
//								if (inputDate != null) {
//									tpsLine.setDateDelivered(inputDate);
//								} else {
//									flag = false;
//									errDetial = "����: " + materialId + "�Ľ�������Ϊ�գ�"
//									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
//								}
//							} catch(Exception e) {
//								flag = false;
//								errDetial = "����: " + materialId + "�Ľ������ڸ�ʽ����"
//								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
//							}
						}else if(j == 9) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String workCenter2 = String.valueOf(parse.format(d));
							workShopSchedule.setWorkCenter2(workCenter2);
							setColor(workShopSchedule,cell,j);
						}else if(j == 8) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String workCenter = String.valueOf(parse.format(d));
							workShopSchedule.setWorkCenter(workCenter);
							setColor(workShopSchedule,cell,j);
						} else if (j == 7) {
							double d = cell.getNumericCellValue();
							DecimalFormat parse = new DecimalFormat("0");
							String custom = String.valueOf(parse.format(d));
							workShopSchedule.setCustomer(custom);
						} else if (j == 6) {
//							double d = cell.getNumericCellValue();
//							DecimalFormat parse = new DecimalFormat("0");
//							String datePlan = String.valueOf(parse.format(d));
//							workShopSchedule.setDatePlan(datePlan);
						} else if (j == 5) {
							try {
								BigDecimal qty = BigDecimal.valueOf(cell.getNumericCellValue());
								if (qty != null) {
									workShopSchedule.setQtyPlanProcuct(qty);
								} else {
									flag = false;
									errDetial = "����: " + materialId + "�ļƻ�����Ϊ�գ�"
									+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
								}								
							} catch(Exception e) {
								flag = false;
								errDetial = "����: " + materialId + "�ļƻ���������"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						} else if (j == 4) {
						} else if(j == 3){
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
								workShopSchedule.setMaterialRrn(material.getObjectRrn());
								workShopSchedule.setMaterialId(material.getMaterialId());
								workShopSchedule.setMaterialName(material.getName());
							} else {
								flag = false;
								errDetial = "����: " + materialId + "��ϵͳ�в����ڣ�"
								+ "���У�" + (row.getRowNum() + 1) + "�� �У�" + (j + 1) + "��";
							}
						} else if (j == 1) {
						
						} else if (j == 0) {
//							double d = cell.getNumericCellValue();
//							DecimalFormat parse = new DecimalFormat("0");
//							String initId = String.valueOf(parse.format(d));
//							if(initId != null)
//								tpsLine.setTpsId(initId);
						}
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						if(j == 1) {//��Ԫ������Ϊ�ջ���ø÷���
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "������������Ϊ�ա�";							
						}
						else if(j == 3) {
							flag = false;
							errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "�������ϱ��Ϊ�ա�";	
						}
						else if(j == 5) {
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
//			if(j == 10){
//				//����Ǳ�עΪ�տ��Ժ���
//			}else if(j == 9){
//				//����ǽ�������Ϊ�տ��Ժ���
//			}else{
//				flag = false;
//				errDetial = "���У�" + (row.getRowNum() + 1) + "���У�" + (j + 1) + "���ĵ�Ԫ����ֵΪ�գ��뽫�õ�Ԫ��ɾ����";
//			}
			if(j == 1||j==2||j==3||j==5||j==7){
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

	public List<WorkShopSchedule> getWorkShopSchedules() {
		return workShopSchedules;
	}

	public void setWorkShopSchedules(List<WorkShopSchedule> workShopSchedules) {
		this.workShopSchedules = workShopSchedules;
	}
	
	public void setColor(WorkShopSchedule workShopScheduel,HSSFCell cell,int j){
		HSSFCellStyle style = (HSSFCellStyle) cell.getCellStyle();
		HSSFColor color =hssfWb.getCustomPalette().getColor(style.getFillForegroundColor());
		short[] rgb = color.getTriplet();
		boolean blankFlag = true;
		StringBuffer sf = new StringBuffer();
		for(short i : rgb){
			if(i!=0){
				blankFlag = false;
			}
			sf.append(i);
			sf.append(",");
		}
		if (!blankFlag) {// ���ǿհײŴ�ֵ
			switch (j) {
			case 8:
				workShopScheduel.setWcolor1(sf.substring(0, sf.length()-1));
				break;
			case 9:
				workShopScheduel.setWcolor2(sf.substring(0, sf.length()-1));
				break;
			case 10:
				workShopScheduel.setWcolor3(sf.substring(0, sf.length()-1));
				break;
			case 11:
				workShopScheduel.setWcolor4(sf.substring(0, sf.length()-1));
				break;
			}
		}
	}
}

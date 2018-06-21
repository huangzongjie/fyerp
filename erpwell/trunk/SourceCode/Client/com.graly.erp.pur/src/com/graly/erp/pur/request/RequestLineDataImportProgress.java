package com.graly.erp.pur.request;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import com.graly.erp.inv.model.VUserWarehouse;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.model.SalePlanLine;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.PurErrLog;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class RequestLineDataImportProgress implements IRunnableWithProgress {
	private static final Logger logger = Logger.getLogger(RequestLineDataImportProgress.class);
	private RequisitionLineEntityBlock parentBlock;
	private boolean isFinished = false;
	protected static int TASK_NUMBER = 32;
	protected static int INIT_TASK = 0;
	protected Requisition request;
	protected String fileUrl;
	protected ADTable adTable;
	protected List<PurErrLog> errLogs;
	protected String errDetial;
	protected String materialId;
	protected boolean flag = true;
	protected ADManager adManager;

	public RequestLineDataImportProgress() {
	}

	public RequestLineDataImportProgress(Requisition request, String fileUrl, ADTable adTable, RequisitionLineEntityBlock parentBlock) {
		this.request = request;
		this.fileUrl = fileUrl;
		this.adTable = adTable;
		this.parentBlock = parentBlock;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		FileInputStream fis = null;
		try {
			errLogs = new ArrayList<PurErrLog>();
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
			logger.error("Error at RequestLineDataImportProgress : run() ", e);
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
//		RequisitionLine prLine = new RequisitionLine();
//		prLine.setRequisitionRrn(request.getObjectRrn());
//		prLine.setRequisitionId(request.getDocId());
//		prLine.setOrgRrn(Env.getOrgRrn());
		Date now = Env.getSysDate();
		try {
			PURManager purManager = Framework.getService(PURManager.class);
			
			RequisitionLine prLine = purManager.newPRLine(request);
			prLine.setRequisitionRrn(request.getObjectRrn());
			// 将excel中第i行的相关数据读入到salePlanLine中
			readExcelRow(row, prLine);
			Material material = prLine.getMaterial();
			if(material != null && material.getObjectRrn() != null){
				prLine.setMaterialRrn(material.getObjectRrn());
				if(prLine.getUomId() == null || prLine.getUomId().trim().length() == 0){
					prLine.setUomId(material.getInventoryUom());
				}
				
				try {
					VDMManager vdmManager = Framework.getService(VDMManager.class);
					VendorMaterial vendorMaterial = vdmManager.getPrimaryVendor(material.getObjectRrn());//根据物料找主供应商
					if (vendorMaterial != null && vendorMaterial.getObjectRrn() != null) {
						prLine.setVendorRrn(vendorMaterial.getVendorRrn());
						prLine.setVendor(vendorMaterial.getVendor());
						if(prLine.getPurchaser() == null || prLine.getPurchaser().trim().length() == 0){
							prLine.setPurchaser(vendorMaterial.getPurchaser());//设置采购员
						}
						
						if(prLine.getUnitPrice() == null || prLine.getUnitPrice().compareTo(BigDecimal.ZERO) == 0){
							if (vendorMaterial.getLastPrice() != null) {
								prLine.setUnitPrice(vendorMaterial.getLastPrice());// 带出上次价格
							} else if (vendorMaterial.getReferencedPrice() != null) {//带出参考价格
								prLine.setUnitPrice(vendorMaterial.getReferencedPrice());
							}
						}
						
						if(prLine.getQtyEconomicSize() == null || prLine.getQtyEconomicSize().compareTo(BigDecimal.ZERO) == 0){
							if (vendorMaterial.getLeastQuantity() != null) {
								prLine.setQtyEconomicSize(vendorMaterial.getLeastQuantity());//经济批量
							}
						}
					} 
				} catch (Exception e) {
					logger.error("RequestLineDataImportProgress : executeOneWorked()",e);
					ExceptionHandlerManager.asyncHandleException(e);
				}
				if(prLine.getLineTotal() == null || prLine.getLineTotal().compareTo(BigDecimal.ZERO) == 0){
					prLine.setLineTotal(prLine.getUnitPrice().multiply(prLine.getQty()));
				}
			}
			
			VUserWarehouse whouse = getUserDefaultWarehouse(Env.getUserRrn());//仓库自动保存默认仓库
			if(whouse != null){
				prLine.setWarehouseRrn(whouse.getObjectRrn());
				prLine.setWarehouseId(whouse.getWarehouseId());
			}
			
			
			
			monitor.setTaskName(String.format(Message.getString("ppm.is_importing_sale_data"), TASK_NUMBER, index - 1, materialId));
			
			// 若flag为true, 提示正在导入, 否则不提示, 但任务条仍加 1
			if (flag) {
				purManager.savePRLine(prLine, Env.getUserRrn());
			} else {
				PurErrLog elog = new PurErrLog();
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
			PurErrLog log = new PurErrLog();
			log.setErrMessage(msg);
			log.setErrDate(now);
			errLogs.add(log);
			logger.error("Error at RequestLineDataImportProgress executeOneWorked(): " , e);
		}
	}

	/**
	 * @param prLine
	 * @throws ClientException
	 */
	private VUserWarehouse getUserDefaultWarehouse(Long userRrn)
			throws ClientException {
		String whereClause = "VUserWarehouse.userRrn = " + userRrn;
		List<VUserWarehouse> wHouses = adManager.getEntityList(Env.getOrgRrn(), VUserWarehouse.class, Integer.MAX_VALUE, whereClause, null);
		for(VUserWarehouse whouse : wHouses){
			if("Y".equals(whouse.getIsDefault())){
				return whouse;
			}
		}
		return  null;
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

	protected void readExcelRow(HSSFRow row, RequisitionLine requisitionLine) {
		int j = 0;
		boolean ladingFlag = true;
		try {
			if (row != null) {
				for (; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);
					if(cell == null) continue;
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
					if (j == 0) {
						materialId = cell.getStringCellValue();
						Material material = getMaterialById(materialId);
						if (material != null) {
						requisitionLine.setMaterial(material);
						}else{
							flag = false;
							errDetial = "Material: " + materialId + "' is not exist"
							 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
						}
					}else if(j==2){
						String uomid=cell.getStringCellValue();
						if(uomid!=null){
							requisitionLine.setUomId(uomid);
						}else{
							Material material = requisitionLine.getMaterial();
							if(material != null && material.getObjectRrn() != null){
								uomid = material.getInventoryUom();
								if(uomid != null && uomid.trim().length() > 0){
									requisitionLine.setUomId(uomid);
								}else{
									errDetial = "Material: " + materialId + "'s  must have units!"
									 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							}else{
								errDetial = "Material: " + materialId + "'s  must have units!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}						
					}else if(j==3){
						try{
							Long l = Long.valueOf(cell.getStringCellValue());
							if(l != null){
								BigDecimal qty = BigDecimal.valueOf(l);
								requisitionLine.setQty(qty);
							}else{
								flag = false;
								errDetial = "Material: " + materialId + "'s qty must be number!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}catch(Exception e){
							flag = false;
							errDetial = "Material: " + materialId + "'s qty must be number!"
							 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
						}
					}else if(j==4){
						try{
							Double d=Double.valueOf(cell.getStringCellValue());
							if(d!=null){
								BigDecimal unitPrice = BigDecimal.valueOf(d);
								requisitionLine.setUnitPrice(unitPrice);
							}else{
								flag = false;
								errDetial = "Material: " + materialId + "'s unitPrice must be number!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}catch(Exception e){
							flag = false;
							errDetial = "Material: " + materialId + "'s unitPrice must be number!"
							 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
						}
					}else if(j==5){
						try{
						Double d=Double.valueOf(cell.getStringCellValue());
						if(d!=null){
							BigDecimal lineTotal = BigDecimal.valueOf(d);
							requisitionLine.setLineTotal(lineTotal);
						}else{
							flag = false;
							errDetial = "Material: " + materialId + "'s lineTotal must be number!"
							 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
						}
						}catch(Exception e){
							flag = false;
							errDetial = "Material: " + materialId + "'s lineTotal must be number!"
							 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
						}
					}else if(j==6){
						try{
							Long l = Long.valueOf(cell.getStringCellValue());
							if(l != null){
								BigDecimal qtyEconomicSize = BigDecimal.valueOf(l);
								requisitionLine.setQtyEconomicSize(qtyEconomicSize);
							}else{
								flag = false;
								errDetial = "Material: " + materialId + "'s qtyEconomicSize must be number!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}catch(Exception e){
							flag = false;
							errDetial = "Material: " + materialId + "'s qtyEconomicSize must be number!"
							 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
						}
					}else if(j==9){
						String purchaser=cell.getStringCellValue();
						if(purchaser!=null){
							requisitionLine.setPurchaser(purchaser);
						}else{
							errDetial = "Material: " + materialId + "'s  must have purchaser!"
							 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
						}	
					}else if(j==10){
						try{
							String inputDateStr=cell.getStringCellValue();
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							Date inputDate = format.parse(inputDateStr);
							if (inputDate != null) {						
								requisitionLine.setDateEnd(inputDate);							
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
					}
					break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if (j == 0) {							
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
							requisitionLine.setMaterial(material);
							}else{
								flag = false;
								errDetial = "Material: " + materialId + "' is not exist"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if(j==2){							
								flag = false;
								errDetial = "Material: " + materialId + "'s Units can not figure type!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";								
						}else if(j==3){
							try{
								Double d = cell.getNumericCellValue();
								if(d != null){
									BigDecimal qty = BigDecimal.valueOf(d);
									requisitionLine.setQty(qty);
								}else{
									flag = false;
									errDetial = "Material: " + materialId + "'s qty must be number!"
									 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							}catch(Exception e){
								flag = false;
								errDetial = "Material: " + materialId + "'s qty must be number!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if(j==4){
							try{
								Double d=cell.getNumericCellValue();
								if(d!=null){
									BigDecimal unitPrice = BigDecimal.valueOf(d);
									requisitionLine.setUnitPrice(unitPrice);
								}else{
									flag = false;
									errDetial = "Material: " + materialId + "'s unitPrice must be number!"
									 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							}catch(Exception e){
								flag = false;
								errDetial = "Material: " + materialId + "'s unitPrice must be number!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if(j==5){
							try{
								Double d=cell.getNumericCellValue();
								if(d!=null){
									BigDecimal lineTotal = BigDecimal.valueOf(d);
									requisitionLine.setLineTotal(lineTotal);
								}else{
									flag = false;
									errDetial = "Material: " + materialId + "'s lineTotal must be number!"
									 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							}catch(Exception e){
								flag = false;
								errDetial = "Material: " + materialId + "'s lineTotal must be number!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if(j==6){
							try{
								Double d=cell.getNumericCellValue();
								if(d != null){
									BigDecimal qtyEconomicSize = BigDecimal.valueOf(d);
									requisitionLine.setQtyEconomicSize(qtyEconomicSize);
								}else{
									flag = false;
									errDetial = "Material: " + materialId + "'s qtyEconomicSize must be number!"
									 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							}catch(Exception e){
								flag = false;
								errDetial = "Material: " + materialId + "'s qtyEconomicSize must be number!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if(j==9){
							try{
								String purchaser=((Double)cell.getNumericCellValue()).toString();
								if(purchaser!=null){
									requisitionLine.setPurchaser(purchaser);
								}else{
									errDetial = "Material: " + materialId + "'s  must have purchaser!"
									 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							}catch(Exception e){
								errDetial = "Material: " + materialId + "'s purchaser must be a String type!"
								 + " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
							}
						}else if(j==10){
							try{
								double d = cell.getNumericCellValue();
								Date inputDate = HSSFDateUtil.getJavaDate(d);				
								if (inputDate != null) {						
										requisitionLine.setDateEnd(inputDate);							
								} else {
									flag = false;
									errDetial = "Material: " + materialId + "'s Date Format is error"
									+ " at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1) + ".";
								}
							}catch(Exception e){
								e.printStackTrace();
								flag = false;
								errDetial = "Material: " + materialId + "'s Date Format is error"
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
		}catch(Exception e){
			errDetial = "The cell is null at line: " + (row.getRowNum() + 1) + ", column: " + (j + 1);
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

	public List<PurErrLog> getErrLogs() {
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

	public RequisitionLineEntityBlock getParentBlock() {
		return parentBlock;
	}

}

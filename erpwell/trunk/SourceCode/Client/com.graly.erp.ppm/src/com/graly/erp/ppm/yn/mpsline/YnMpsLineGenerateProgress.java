package com.graly.erp.ppm.yn.mpsline;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class YnMpsLineGenerateProgress implements IRunnableWithProgress {
	private static final Logger logger = Logger.getLogger(YnMpsLineGenerateProgress.class);
	private static final int TASK_NUMBER = 36;
	private static final int INIT_TASK = 3;
	private static final int VALIDATE_TASK = 3;
	private Mps mps;
	private PPMManager ppmManager;
	private YnMpsEntityBlock parentBlock;
	private boolean isFinished = false;
	private List<PasErrorLog> errlogs;
	
	private boolean isVerify = true;
	private String verifyErrorInfo;

	public YnMpsLineGenerateProgress(Mps mps, YnMpsEntityBlock parentBlock) {
		this.mps = mps;
		this.parentBlock = parentBlock;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			monitor.beginTask("", TASK_NUMBER);
			// 调用存储过程
			monitor.setTaskName(Message.getString("ppm.is_compute_material_reference_qty"));
			ppmManager = Framework.getService(PPMManager.class);
//			ppmManager.generateMaterialBatchSum(Env.getOrgRrn());
			monitor.worked(INIT_TASK);

			String whereClause = " mpsId = '" + mps.getMpsId() + "' AND qtyMps > 0 ";
			ADManager adManager = Framework.getService(ADManager.class);
			
//			List<MpsLine> mpsLines = ppmManager.getSortedMpsLine(Env.getOrgRrn(), mps.getMpsId());
			 
			List<MpsLine> mpsLines =  adManager.getEntityList(Env.getOrgRrn(), MpsLine.class,Integer.MAX_VALUE,whereClause,null);
			// 校验Bom
			monitor.setTaskName(Message.getString("ppm.is_verifying_bom"));
			monitor.worked(VALIDATE_TASK);
			
			if(mpsLines != null && mpsLines.size() > 0) {
				int workNumber = mpsLines.size();
				int remainder = 0, quotient = 0; // 余数和商初始化为0
				int token = 0;
				if(workNumber > (TASK_NUMBER - INIT_TASK)) {
					quotient = workNumber / (TASK_NUMBER - INIT_TASK);
					remainder = workNumber % (TASK_NUMBER - INIT_TASK);
					token = ((TASK_NUMBER - INIT_TASK) - remainder) * quotient;
				} else {
					quotient = (TASK_NUMBER - INIT_TASK) / workNumber;
					remainder = (TASK_NUMBER - INIT_TASK) % workNumber;
					token = workNumber - remainder;
				}
				
				PURManager purManager = Framework.getService(PURManager.class);
				Requisition pr = purManager.generatePr(Env.getOrgRrn(), Env.getUserRrn(), mps.getObjectRrn(), mps.getMpsId());

				int finishedWorked = INIT_TASK + VALIDATE_TASK;
				errlogs = new ArrayList<PasErrorLog>();
				for (int i = 0; i < workNumber; i++) {
					if (monitor.isCanceled()) {
//						mergePr(pr);
						this.setGeneratePpOut();
						return;
					}
					MpsLine mpsLine = mpsLines.get(i);
					if(ppmManager != null) {
						monitor.setTaskName(String.format(Message.getString("ppm.is_generating_mpsLine"),
								workNumber, i, mpsLine.getMaterialName()));
						generateRequisitionLine(mpsLine, pr);
						pr = (Requisition)adManager.getEntity(pr);
						
						if(workNumber > (TASK_NUMBER - INIT_TASK)) {
							int w = quotient;
							if(i >= token)
								w++;
							if((i + 1) % w == 0) {
								monitor.worked(1);
								finishedWorked++;
							}
						} else {
							if(i < token || token == 0) { // token == 0表示workNumber等于(TASK_NUMBER - INIT_TASK)
								monitor.worked(quotient);
								finishedWorked += quotient;
							} else {
								monitor.worked(quotient + 1);
								finishedWorked += (quotient + 1);
							}							
						}
					}
				}
				// merge PR
				mergePr(pr);
				if(finishedWorked < TASK_NUMBER) monitor.worked(TASK_NUMBER - finishedWorked);
				monitor.done();
				this.isFinished = true;
			} else return;
			
		} catch(Exception e) {
			logger.error("Error at MpsLineGenerateProgress : run() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			setGeneratePpOut();
		}
	}
	
	protected void mergePr(Requisition pr) throws Exception {
		WipManager wipManager = Framework.getService(WipManager.class);
		wipManager.mergePrLine(pr);
	}
	
	private void generateRequisitionLine(MpsLine line, Requisition pr) {
		try {
			PURManager pur = Framework.getService(PURManager.class);
			pur.generateYn(mps, line, pr, Env.getUserRrn());
		 
		} catch(Exception e) {
			String msg = e.getMessage();
			if (e instanceof ClientException) {
				msg = getErrorMessage((ClientException)e);
			}
			logger.error("generateManufactureOrder MpsLine=" + line.getObjectRrn() 
					+ " Material=" + line.getMaterialRrn() + " Message:" + msg , e);
			try {
				PasErrorLog log = new PasErrorLog();
				log.setOrgRrn(mps.getOrgRrn());
				log.setIsActive(true);
				log.setPasType(PasErrorLog.PASTYPE_MPS);
				log.setMpsId(mps.getMpsId());
				log.setMpsLineRrn(line.getObjectRrn());
				log.setMaterialRrn(line.getMaterialRrn());
				Date now = Env.getSysDate();
				log.setErrDate(now);
				log.setErrMessage(msg);
				errlogs.add(log);
				
				Material mt = new Material();
				mt.setObjectRrn(line.getMaterialRrn());
				ADManager manager = Framework.getService(ADManager.class);
				mt = (Material)manager.getEntity(mt);
				log.setMaterialId(mt.getMaterialId());
				ppmManager.saveErrorLog(log, mps.getOrgRrn());
			} catch(Exception e2) {
				logger.error("Error at MpsLineGenerateProgress.generateManufactureOrder save ErrorLog: ", e);
			}
		}
	}
	
	/*
	 * 生产计划生成完成、取消或发生异常后，设置IsProcessingMps为false
	 */
	private void setGeneratePpOut() {
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			this.mps.setIsProcessingPp(false);
			adManager.saveEntity(mps, Env.getUserRrn());
			this.mps = (Mps)adManager.getEntity(mps);
		} catch(Exception e) {
			logger.error("Error at MpsLineGenerateProgress : setGenerateMpsOut", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	public boolean isFinished() {
		return isFinished;
	}
	
	public boolean isSuccess() {
		if(this.errlogs == null || errlogs.size() == 0)
			return true;
		return false;
	}

	public List<PasErrorLog> getErrlogs() {
		return errlogs;
	}
	
	private String getErrorMessage(ClientException e) {
		String errMeg = e.getMessage();
		if (e.getErrorCode() != null && !"".equals(e.getErrorCode().trim())){
			errMeg = e.getErrorCode();
			String errMessage = Message.getString(e.getErrorCode());
			if (errMessage != null || !"".equals(errMessage.trim())){
				if(e instanceof ClientParameterException) {
					ClientParameterException pe = (ClientParameterException)e;
					Object[] parameters = new Object[] {};
					parameters = pe.getParameters();
					errMeg = String.format(errMessage, parameters);
				} else {
					errMeg = errMessage;					
				}
			}
		}
		return errMeg;
	}
	
	protected boolean verifyMpsLineBom(List<MpsLine> mpsLines) {
		isVerify = true;
		verifyErrorInfo = null;
		for(MpsLine mpsLine : mpsLines) {
			try {
				ppmManager.verifyMpsLine(mpsLine);
			} catch(Exception e) {
				isVerify = false;
				String msg = e.getMessage();
				if (e instanceof ClientException) {
					msg = getErrorMessage((ClientException)e);
					msg = msg.substring(0, msg.length() - 1);
				}
				verifyErrorInfo = String.format(Message.getString("ppm.verify_mpsline_bom"),
						mpsLine.getMaterialId(), msg);
				return false;
			}		
		}
		return true;
	}

	public boolean isVerify() {
		return isVerify;
	}

	public String getVerifyErrorInfo() {
		return verifyErrorInfo;
	}
}

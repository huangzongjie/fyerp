package com.graly.erp.ppm.mpsline;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.model.SalePlanLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MpsGenerateProgress implements IRunnableWithProgress {
	private static final Logger logger = Logger.getLogger(MpsGenerateProgress.class);
	private static final int TASK_NUMBER = 32;
	private static final int INIT_TASK = 2;
	private List<List<SalePlanLine>> saleMaterials;
	private Mps mps;
	private PPMManager ppmManager;
	private MpsEntityBlock parentBlock;
	private boolean isFinished = false;
	private List<PasErrorLog> errlogs;

	public MpsGenerateProgress() {
	}

	public MpsGenerateProgress(Mps mps, MpsEntityBlock parentBlock) {
		this.mps = mps;
		this.parentBlock = parentBlock;
//		createPPMManagerService();
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			monitor.beginTask("", TASK_NUMBER);
			// 若有mpsLine,则提示删除原先的主计划
			ADManager manager = Framework.getService(ADManager.class);
			List<MpsLine> list = manager.getEntityList(Env.getOrgRrn(), MpsLine.class, Env.getMaxResult(), " mpsId = '" + mps.getMpsId() + "' ", null);
			if(list != null && list.size() > 0) {
				monitor.setTaskName(Message.getString("ppm.is_deleting_preMpsLine"));
				for (MpsLine mpsLine : list) {
					manager.deleteEntity(mpsLine);
				}
			}
			// 获得该Mps下所有的SalePaleLine
			monitor.setTaskName(Message.getString("ppm.is_gathering_mps"));
			ppmManager = Framework.getService(PPMManager.class);
			List<SalePlanLine> sps = ppmManager.getSalePlanLineSum(mps);				
			if(sps != null && sps.size() > 0) {
				saleMaterials = getSaleMaterials(sps);
			} else return;
			monitor.worked(INIT_TASK);
			
			int workNumber = saleMaterials.size();
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

			int finishedWorked = INIT_TASK;
			errlogs = new ArrayList<PasErrorLog>();
			for (int i = 0; i < workNumber; i++) {
				if (monitor.isCanceled()) {
					this.setGenerateMpsOut();
					return;
				}
				List<SalePlanLine> saleLines = saleMaterials.get(i);
				if(saleLines != null && saleLines.size() > 0) {
					if(ppmManager == null) {
						ppmManager = Framework.getService(PPMManager.class);
					}
					monitor.setTaskName(String.format(Message.getString("ppm.is_generating_mps"), workNumber, i + 1));
					generateMpsLine(saleLines.get(0).getMaterialRrn(), saleLines);
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
			if(finishedWorked < TASK_NUMBER) monitor.worked(TASK_NUMBER - finishedWorked);
			monitor.done();
			this.isFinished = true;
		} catch(Exception e) {
			logger.error("Error at MpsLineGenerateProgress : run() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			setGenerateMpsOut();
		}
	}
	
	private void generateMpsLine(long materialRrn, List<SalePlanLine> salePlanLines) {
		try {
			if(ppmManager == null) {
				ppmManager = Framework.getService(PPMManager.class);
			}
			ppmManager.generateMpsLine(mps, materialRrn, salePlanLines, Env.getUserRrn());
		} catch(Exception e) {
			String msg = e.getMessage();
			if (e instanceof ClientException) {
				msg = getErrorMessage((ClientException)e);
			}
			logger.error("generateMpsLine mps=" + mps.getObjectRrn() 
					+ " Material=" + materialRrn + " Message: "+ msg, e);
			try {
				PasErrorLog log = new PasErrorLog();
//				log.setOrgRrn(mps.getOrgRrn());
//				log.setIsActive(true);
				log.setPasType(PasErrorLog.PASTYPE_MPS);
				log.setMpsId(mps.getMpsId());
				log.setMaterialRrn(materialRrn);
				Date now = Env.getSysDate();
				log.setErrDate(now);
				log.setErrMessage(msg);
				errlogs.add(log);
				
				Material mt = new Material();
				mt.setObjectRrn(materialRrn);
				ADManager manager = Framework.getService(ADManager.class);
				mt = (Material)manager.getEntity(mt);
				log.setMaterialId(mt.getMaterialId());
				ppmManager.saveErrorLog(log, mps.getOrgRrn());
			} catch(Exception e2) {
				logger.error("Error at MpsLineGenerateProgress.generateMpsLine save ErrorLog: ", e);
			}
		}
	}
	
	protected void createPPMManagerService() {
		try {
			ppmManager = Framework.getService(PPMManager.class);
		} catch(Exception e) {
			logger.error("Error at MpsGenerateProgress : createPPMManagerService", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	private List<List<SalePlanLine>> getSaleMaterials(List<SalePlanLine> list) {
		List<List<SalePlanLine>> saleMaterials = new ArrayList<List<SalePlanLine>>();
		List<Long> tempMaterialRrns = new ArrayList<Long>();

		List<SalePlanLine> tempList = null;
		for (SalePlanLine saleLine : list) {
			if (tempMaterialRrns.contains(saleLine.getMaterialRrn()))
				continue;

			tempList = new ArrayList<SalePlanLine>();
			for (SalePlanLine saleLine2 : list) {
				if (saleLine2.getMaterialRrn().equals(saleLine.getMaterialRrn())) {
					tempList.add(saleLine2);
				}
			}
			saleMaterials.add(tempList);
			tempMaterialRrns.add(saleLine.getMaterialRrn());
		}
		return saleMaterials;
	}
	
	/*
	 * 主计划生成完成、取消或发生异常后，设置IsProcessingMps为false
	 */
	private void setGenerateMpsOut() {
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			this.mps.setIsProcessingMps(false);
			adManager.saveEntity(mps, Env.getUserRrn());
			this.mps = (Mps)adManager.getEntity(mps);
//			parentBlock.setParentObject(mps);
		} catch(Exception e) {
			logger.error("Error at MpsGenerateProgress : setGenerateMpsOut", e);
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

}

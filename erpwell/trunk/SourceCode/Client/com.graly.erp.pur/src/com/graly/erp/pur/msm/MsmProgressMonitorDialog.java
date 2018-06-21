package com.graly.erp.pur.msm;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class MsmProgressMonitorDialog extends ProgressMonitorDialog {
	private static final Logger logger = Logger.getLogger(MsmProgressMonitorDialog.class);
	private String title = "";
	private String Suffix = "...";
	private List<RequisitionLine> minStoragePrLines;
	private List<PasErrorLog> errlogs;
	GenerateMsmProgress msmPrg;
	WipManager wipManager = null;

	public MsmProgressMonitorDialog(Shell parent) {
		super(parent);
	}
	
	public MsmProgressMonitorDialog(Shell parent, String title) {
		super(parent);
		this.title = title;
	}
	
	// 使取消按钮可以中英文显示
	protected void createCancelButton(Composite parent) {
		cancel = createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
		if (arrowCursor == null) {
			arrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
		}
		cancel.setCursor(arrowCursor);
		setOperationCancelButtonEnabled(enableCancelButton);
	}
	
	// 使对话框标题可以中英文显示
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}
	
	public IRunnableWithProgress createProgress() {
		msmPrg = new GenerateMsmProgress();
		return msmPrg;
	}
	
	public IRunnableWithProgress createProgress(List<Material> materials) {
		msmPrg = new GenerateMsmProgress(materials);
		return msmPrg;
	}
	
	class GenerateMsmProgress implements IRunnableWithProgress {
		private static final int TASK_NUMBER = 51;
		private static final int INIT_TASK = 1;
		private boolean isFinished = false;
		private List<Material> materials;

		public GenerateMsmProgress() {}
		
		public GenerateMsmProgress(List<Material> materials) {
			this.materials = materials;
		}
		
		public void run(IProgressMonitor monitor) throws InvocationTargetException,
				InterruptedException {
			try {
				monitor.beginTask(" ", TASK_NUMBER);
				if(materials == null || materials.size() == 0) {
					isFinished = false;
					return;
				}
				monitor.worked(INIT_TASK);
				
				long orgRrn = Env.getOrgRrn();
				int workNumber = materials.size();
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
				minStoragePrLines = new ArrayList<RequisitionLine>();
				errlogs = new ArrayList<PasErrorLog>();
				
				RequisitionLine prLine = null;
				for (int i = 0; i < workNumber; i++) {
					if (monitor.isCanceled()) {
						return;
					}
					if(materials.get(i) != null) {
						monitor.setTaskName(String.format(Message.getString("wip.is_generating_prline_for_min_storage"),
								workNumber,  i + 1, materials.get(i).getMaterialId()) + Suffix);
						prLine = generatePrLineByMin(orgRrn, materials.get(i));
						if(prLine != null) {
							minStoragePrLines.add(prLine);							
						}
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
			}
		}
		
		private RequisitionLine generatePrLineByMin(long orgRrn, Material material) {
			try {
				if(wipManager == null) {
					wipManager = Framework.getService(WipManager.class);
				}
				return wipManager.generatePrLineByMin(orgRrn, material.getObjectRrn());
			} catch(Exception e) {
				String msg = e.getMessage();
				if (e instanceof ClientException) {
					msg = getErrorMessage((ClientException)e);
				}
				try {
					PasErrorLog log = new PasErrorLog();
					log.setPasType(PasErrorLog.PASTYPE_MPS);
					log.setOrgRrn(material.getOrgRrn());
					log.setIsActive(true);
					log.setMaterialId(material.getMaterialId());
					log.setErrMessage(msg);
					errlogs.add(log);
				} catch(Exception e2) {
					logger.error("MsmProgressMonitorDialog.generatePrLineByMin() save ErrorLog: ", e);
				}
			}
			return null;
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

		public boolean isFinished() {
			return isFinished;
		}
	}
	
	public boolean isFinished() {
		return msmPrg.isFinished();
	}

	public List<RequisitionLine> getMinStoragePrLines() {
		return minStoragePrLines;
	}

	public List<PasErrorLog> getErrlogs() {
		return errlogs;
	}
}

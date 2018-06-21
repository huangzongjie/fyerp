package com.graly.erp.vdm.vendorassess;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
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

import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.VendorAssessment;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class VendorAssessProgressDialog extends ProgressMonitorDialog {
	private static final Logger logger = Logger.getLogger(VendorAssessProgressDialog.class);
	private String title = "";
	private String Suffix = "...";
	private List<VendorAssessment> vendorAssesses;
	GenerateVdmAssessProgress vdmAssPrg;
	
	public VendorAssessProgressDialog(Shell parent) {
		super(parent);
	}
	
	public VendorAssessProgressDialog(Shell parent, String title) {
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
	
	public IRunnableWithProgress createProgress(Long materialRrn, Long vendorRrn, String purchaser,
			Date startDate, Date endDate) {
		vdmAssPrg = new GenerateVdmAssessProgress(materialRrn, vendorRrn, purchaser, startDate, endDate);
		return vdmAssPrg;
	}
	
	class GenerateVdmAssessProgress implements IRunnableWithProgress {
		private static final int TASK_NUMBER = 32;
		private static final int INIT_TASK = 2;
		private boolean isFinished = false;
		private boolean isNoVendors = false;
		private boolean isNoMaterials = false;
		
		// for progress
		private Long materialRrn;
		private Long vendorRrn;
		private String purchaser;
		private Date startDate;
		private Date endDate;

		public GenerateVdmAssessProgress() {}

		public GenerateVdmAssessProgress(Long materialRrn, Long vendorRrn, String purchaser, Date startDate, Date endDate) {
			this.materialRrn = materialRrn;
			this.vendorRrn = vendorRrn;
			this.purchaser = purchaser;
			this.startDate = startDate;
			this.endDate = endDate;
		}

		public void run(IProgressMonitor monitor) throws InvocationTargetException,
				InterruptedException {
			try {
				monitor.beginTask(" ", TASK_NUMBER);
				// 提示正在根据物料得到供应商列表
				monitor.setTaskName(Message.getString("vdm.get_vendors_by_material") + Suffix);
				List<VendorMaterial> vendors = getVendorMaterials();
				if (vendors == null || vendors.size() == 0) {
					isFinished = false;
					return;
				}
				monitor.worked(INIT_TASK);
				
				int workNumber = vendors.size();
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
				VDMManager vdmManager = null;
				vendorAssesses = new ArrayList<VendorAssessment>();
				long orgRrn = Env.getOrgRrn();
				for (int i = 0; i < workNumber; i++) {
					if (monitor.isCanceled()) {
						isFinished = true;
						return;
					}
					VendorMaterial vm = vendors.get(i);
					if(purchaser != null && !"".equals(purchaser.trim())) {
						if (!purchaser.equals(vm.getPurchaser())) {
							continue;
						}
					}
					
					if(vdmManager == null) {
						vdmManager = Framework.getService(VDMManager.class);
					}
					monitor.setTaskName(String.format(Message.getString("vdm.is_generating_assess"), workNumber, i + 1) + Suffix);
					VendorAssessment ma = vdmManager.generateVendorAssessment(orgRrn,
							vm.getMaterialRrn(), vm.getVendorRrn(), vm.getPurchaser(), startDate, endDate);
					ma.setPurchaser(vm.getPurchaser());
					vendorAssesses.add(ma);
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
				if(finishedWorked < TASK_NUMBER) monitor.worked(TASK_NUMBER - finishedWorked);
				monitor.done();
				this.isFinished = true;
			} catch(Exception e) {
				logger.error("Error at MpsLineGenerateProgress : run() ", e);
				ExceptionHandlerManager.asyncHandleException(e);
			} finally {			
			}
		}

		public boolean isFinished() {
			return isFinished;
		}
		
		public boolean isNoVendors() {
			return isNoVendors;
		}
		
		protected List<VendorMaterial> getVendorMaterials() throws Exception {
			List<VendorMaterial> vms = new ArrayList<VendorMaterial>();
			StringBuffer sb = new StringBuffer(" 1=1 ");
			if(this.materialRrn != null) {
				sb.append(" AND V.MATERIAL_RRN = '" + materialRrn + "' ");
			}
			if(this.vendorRrn != null) {
				sb.append(" AND V.VENDOR_RRN = '" + vendorRrn + "' ");
			}
			if(purchaser != null && !"".equals(purchaser.trim())) {
				sb.append(" AND V.PURCHASER = '" + purchaser + "' ");
			}
			VDMManager vdmManager = Framework.getService(VDMManager.class);
			vms = vdmManager.getVendorMaterialList2(Env.getOrgRrn(), startDate, endDate, sb.toString());
			// 如果没有供应商物料对象，则判断是没有物料还是没有供应商
			if(vms == null || vms.size() == 0) {
				if(materialRrn == null && vendorRrn != null)
					this.isNoMaterials = true;
				else if(vendorRrn == null && materialRrn != null)
					isNoVendors = true;
			}
			return vms;
		}
	}
	
	public boolean isFinished() {
		return vdmAssPrg.isFinished();
	}
	
	public boolean isNoVnedors() {
		return vdmAssPrg.isNoVendors();
	}
	
	public boolean isNoMaterials() {
		return vdmAssPrg.isNoMaterials;
	}

	public List<VendorAssessment> getVendorAssesses() {
		return vendorAssesses;
	}

}

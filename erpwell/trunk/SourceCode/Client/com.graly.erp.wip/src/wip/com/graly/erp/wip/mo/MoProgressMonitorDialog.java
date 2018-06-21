package com.graly.erp.wip.mo;

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
import com.graly.erp.ppm.model.TpsLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

/**
 *校验临时计划是否有预处理的进度条 
 * */
public class MoProgressMonitorDialog extends ProgressMonitorDialog {
	private static final Logger logger = Logger.getLogger(MoProgressMonitorDialog.class);
	private String title = "";
	private String Suffix = "...";
	private List<TpsLine> nomalTpsLines;//正常的临时计划
	GenerateVdmAssessProgress vdmAssPrg;
	// 不能统计的物料(没有参与MRP或不受Lot控制)
	private List<TpsLine> prepareTpsLines;//有预处理物料的临时计划
	private List<Material> errorMaterials = new ArrayList<Material>();
//	private List<TpsLine> allTpsLines;

	public MoProgressMonitorDialog(Shell parent) {
		super(parent);
	}
	
	public MoProgressMonitorDialog(Shell parent, String title) {
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
		vdmAssPrg = new GenerateVdmAssessProgress();
		return vdmAssPrg;
	}
//	
//	public IRunnableWithProgress createProgress(List<Material> materials) {
//		vdmAssPrg = new GenerateVdmAssessProgress(materials);
//		return vdmAssPrg;
//	}
	
	class GenerateVdmAssessProgress implements IRunnableWithProgress {
		private static final int TASK_NUMBER = 51;
		private static final int INIT_TASK = 1;
		private boolean isFinished = false;

		public GenerateVdmAssessProgress() {}
		
		
		public void run(IProgressMonitor monitor) throws InvocationTargetException,
				InterruptedException {
			try {
				monitor.beginTask(" ", TASK_NUMBER);
				// 提示正在获取该区域下的所有物料
				monitor.setTaskName(Message.getString("inv.get_all_material") + Suffix);
				ADManager manager = Framework.getService(ADManager.class);
				String whereClause = "isGenerate = 'N'";
				List<TpsLine> tpsLines = manager.getEntityList(Env.getOrgRrn(), TpsLine.class, Integer.MAX_VALUE,whereClause,null);
				long orgRrn = Env.getOrgRrn();
				// 如果外部没有传出materials，则直接从数据库中取出该区域下符合条件的物料
 
				if(tpsLines == null || tpsLines.size() == 0) {
					isFinished = false;
					return;
				}
				monitor.worked(INIT_TASK);
				
				int workNumber = tpsLines.size();
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
				WipManager wipManager = null;
				nomalTpsLines = new ArrayList<TpsLine>();
				prepareTpsLines = new ArrayList<TpsLine>();
//				allTpsLines = new ArrayList<TpsLine>();
				
				for (int i = 0; i < workNumber; i++) {
					if (monitor.isCanceled()) {
						return;
					}
					if(tpsLines.get(i) != null) {
						if(wipManager == null) {
							wipManager = Framework.getService(WipManager.class);
						}
						monitor.setTaskName(String.format(Message.getString("wip.mo_verify_prepare"),
								workNumber,  i + 1, tpsLines.get(i).getMaterialId()) + Suffix);
						boolean prepareTps = false;
						try{
//							prepareTps = wipManager.verifyPrepareTps(Env.getOrgRrn(), tpsLines.get(i));
						}catch(Exception e ){
							//记录错误，如：EJB连接不上异常导致数据查询失败,但是程序不能中断
//							materials.get(i).setComments(e.getMessage());//记录异常信息
//							errorMaterials.add(materials.get(i));
						}
						Thread.sleep(10);//这里稍微sleep一下,否则会出现上一次连接端口还没有来得及释放,下一次连接已经开始了,会报java.net.BindException: Address already in use: connect的异常
						if(prepareTps) {
//							tpsLines.get(i).setPrepareTps("Y");
							prepareTpsLines.add(tpsLines.get(i));
//							allTpsLines.add(tpsLines.get(i));
						} else {
							nomalTpsLines.add(tpsLines.get(i));
//							allTpsLines.add(tpsLines.get(i));
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

		public boolean isFinished() {
			return isFinished;
		}
	}
	
	public boolean isFinished() {
		return vdmAssPrg.isFinished();
	}

	public List<TpsLine> getNomalTpsLines() {
		return nomalTpsLines;
	}

	public List<TpsLine> getPrepareTpsLines() {
		return prepareTpsLines;
	}
 
	public List<Material> getErrorMaterials() {
		return errorMaterials;
	}

	public void setErrorMaterials(List<Material> errorMaterials) {
		this.errorMaterials = errorMaterials;
	}

//	public List<TpsLine> getAllTpsLines() {
//		return allTpsLines;
//	}
	
}

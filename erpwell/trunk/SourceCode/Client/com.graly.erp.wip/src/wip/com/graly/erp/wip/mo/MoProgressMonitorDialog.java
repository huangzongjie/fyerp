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
 *У����ʱ�ƻ��Ƿ���Ԥ����Ľ����� 
 * */
public class MoProgressMonitorDialog extends ProgressMonitorDialog {
	private static final Logger logger = Logger.getLogger(MoProgressMonitorDialog.class);
	private String title = "";
	private String Suffix = "...";
	private List<TpsLine> nomalTpsLines;//��������ʱ�ƻ�
	GenerateVdmAssessProgress vdmAssPrg;
	// ����ͳ�Ƶ�����(û�в���MRP����Lot����)
	private List<TpsLine> prepareTpsLines;//��Ԥ�������ϵ���ʱ�ƻ�
	private List<Material> errorMaterials = new ArrayList<Material>();
//	private List<TpsLine> allTpsLines;

	public MoProgressMonitorDialog(Shell parent) {
		super(parent);
	}
	
	public MoProgressMonitorDialog(Shell parent, String title) {
		super(parent);
		this.title = title;
	}
	
	// ʹȡ����ť������Ӣ����ʾ
	protected void createCancelButton(Composite parent) {
		cancel = createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
		if (arrowCursor == null) {
			arrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
		}
		cancel.setCursor(arrowCursor);
		setOperationCancelButtonEnabled(enableCancelButton);
	}
	
	// ʹ�Ի�����������Ӣ����ʾ
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
				// ��ʾ���ڻ�ȡ�������µ���������
				monitor.setTaskName(Message.getString("inv.get_all_material") + Suffix);
				ADManager manager = Framework.getService(ADManager.class);
				String whereClause = "isGenerate = 'N'";
				List<TpsLine> tpsLines = manager.getEntityList(Env.getOrgRrn(), TpsLine.class, Integer.MAX_VALUE,whereClause,null);
				long orgRrn = Env.getOrgRrn();
				// ����ⲿû�д���materials����ֱ�Ӵ����ݿ���ȡ���������·�������������
 
				if(tpsLines == null || tpsLines.size() == 0) {
					isFinished = false;
					return;
				}
				monitor.worked(INIT_TASK);
				
				int workNumber = tpsLines.size();
				int remainder = 0, quotient = 0; // �������̳�ʼ��Ϊ0
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
							//��¼�����磺EJB���Ӳ����쳣�������ݲ�ѯʧ��,���ǳ������ж�
//							materials.get(i).setComments(e.getMessage());//��¼�쳣��Ϣ
//							errorMaterials.add(materials.get(i));
						}
						Thread.sleep(10);//������΢sleepһ��,����������һ�����Ӷ˿ڻ�û�����ü��ͷ�,��һ�������Ѿ���ʼ��,�ᱨjava.net.BindException: Address already in use: connect���쳣
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
							if(i < token || token == 0) { // token == 0��ʾworkNumber����(TASK_NUMBER - INIT_TASK)
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

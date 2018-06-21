package com.graly.erp.ppm.setplan;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.mpsline.ErrorLogDisplayDialog;
import com.graly.erp.ppm.mpsline.MpsProgressDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class SetPlanProperties extends EntityProperties {
	private static final Logger logger = Logger
			.getLogger(SetPlanProperties.class);
	protected ToolItem compute;
	protected ToolItem exportReport;
	private Mps mps;
	protected Boolean finishFlag = false;

	public SetPlanProperties() {
		super();
	}

	public SetPlanProperties(EntityBlock masterParent, ADTable table) {
		super(masterParent, table);
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemCompute(tBar);
		createToolItemExportReport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemCompute(ToolBar tBar) {
		compute = new AuthorityToolItem(tBar, SWT.PUSH,
				Constants.KEY_SALEPLAN_COMPUTE);
		compute.setText("����");
		compute.setImage(SWTResourceCache.getImage("computus"));
		compute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				computeAdapter();
			}
		});
	}

	protected void computeAdapter() {
		try {
			Calendar targetTime = Calendar.getInstance();
			Date now = Env.getSysDate();
			targetTime.setTime(now);
			targetTime.set(Calendar.HOUR_OF_DAY, 17);
			if(now.compareTo(targetTime.getTime()) < 0){
				UI.showWarning("�ù��ܺ�ʱ�ϳ���ֻ����ÿ��17���ʹ��");
				return;
			}
			
			
			boolean confirm = UI.showConfirm("  ִ�����㹦�ܿ��ܻỨ���������ӵ�ʱ�䣬\r����ѡ����е�ʱ��ִ�д˹��ܣ��Ƿ�Ҫ������");
			if(!confirm) return;
			
			finishFlag = false;

			ComputeProgressDialog cpd = new ComputeProgressDialog(UI.getActiveShell(), "��������");
			ComputeProgress progress = new ComputeProgress();
			cpd.run(true, true, progress);
			
			if (progress.isFinished()) {
				UI.showInfo("������ɣ����ڿ��Ե���������");
			}else{
				UI.showInfo("����ʧ��");
			}
		} catch (Exception e) {
			logger.error("SetPlanProperties : computeAdapter()", e);
		}
	}

	protected void createToolItemExportReport(ToolBar tBar) {
		exportReport = new AuthorityToolItem(tBar, SWT.PUSH,
				Constants.KEY_SALEPLAN_EXPORTREPORT);
		exportReport.setText("��������");
		exportReport.setImage(SWTResourceCache.getImage("export_report"));
		exportReport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				exportAdapter();
			}
		});
	}

	protected void exportAdapter() {
		ExportDialog ed = new ExportDialog(UI.getActiveShell(),
				(Mps) getAdObject());
		ed.open();
	}

	protected void saveAdapter() {
		boolean saveFlag;
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					ADBase oldBase = getAdObject();
					mps = (Mps) getAdObject();
					Date dateStart = mps.getDateStart();
					Date dateEnd = mps.getDateEnd();
					Date reservedDate = mps.getDateReserved();
					Calendar calender = Calendar.getInstance();
					Date todayDate = calender.getTime();
					if (salePlanDateCompare(reservedDate, todayDate) < 0) {
						UI.showError(Message
								.getString("ppm.reservecomparetotoday"));
						return;
					}
					if (salePlanDateCompare(dateStart, todayDate) < 0) {
						UI.showError(Message
								.getString("ppm.startdatebeforenow"));
						return;
					}
					if (salePlanDateCompare(dateStart, reservedDate) < 0) {
						UI.showError(Message
								.getString("ppm.startdatebeforereservedate"));
						return;
					}
					if (salePlanDateCompare(dateEnd, dateStart) < 0) {
						UI.showError(Message
								.getString("ppm.enddatebeforestartdate"));
						return;
					}
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}

					PPMManager ppmManager = Framework
							.getService(PPMManager.class);
					mps = ppmManager.savePlanSetup(getTable().getObjectRrn(),
							(Mps) getAdObject(), Env.getUserRrn());

					if (mps != null) {
						ADManager adManager = Framework
								.getService(ADManager.class);
						setAdObject(adManager.getEntity(mps));
						UI.showInfo(Message.getString("common.save_successed"));
						refresh();
					} else {
						UI.showInfo(Message.getString("ppm.dateintervalused"));
					}
					ADBase newBase = getAdObject();
					if (oldBase.getObjectRrn() == null) {
						getMasterParent().refreshAdd(newBase);
					} else {
						getMasterParent().refreshUpdate(newBase);
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	public Integer salePlanDateCompare(Date dateOne, Date dateTwo) {
		Integer result = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateOne = dateFormat.parse(dateFormat.format(dateOne));
			dateTwo = dateFormat.parse(dateFormat.format(dateTwo));
			result = dateOne.compareTo(dateTwo);
			return result;
		} catch (Exception e) {
			logger.error("SetPlanProperties : salePlanDateCompare ", e);
			return result;
		}
	}
}

class ComputeProgress implements IRunnableWithProgress{
	private boolean isFinished = false;
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		boolean startFlag = false;
		monitor.beginTask("����ͳ�����ݣ����ܻỨ���������ӵ�ʱ�䣬���Ժ�... ...", 40);
		int i = 0;
		while (!isFinished) {
			if (isFinished) {
				break;
			}
			i++;
			if (i == 40) {
				i = 0;
				monitor.beginTask("����ͳ�����ݣ����ܻỨ���������ӵ�ʱ�䣬���Ժ�... ...", 40);
			}
			
			if(!startFlag){
				Runnable r = new Runnable(){
					@Override
					public void run() {
						try {
							PPMManager ppmManager = Framework.getService(PPMManager.class);
							Date now = Env.getSysDate();
							Calendar cal = Calendar.getInstance();
							cal.setTime(now);
							cal.add(Calendar.MONTH, 1);
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
							String nextMonth = formatter.format(cal.getTime());
							int returnCode = ppmManager.computePlanSum(Env.getOrgRrn(), nextMonth);
							if(returnCode > 0){
								isFinished = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}			
				};		
				Thread computeThread = new Thread(r);
				computeThread.start();
				startFlag = true;
			}
			monitor.worked(1);
			Thread.sleep(2000);
		}
		
		monitor.done();
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	
}

class ComputeProgressDialog extends MpsProgressDialog{

	public ComputeProgressDialog(Shell parent, String title) {
		super(parent, title);
	}
}

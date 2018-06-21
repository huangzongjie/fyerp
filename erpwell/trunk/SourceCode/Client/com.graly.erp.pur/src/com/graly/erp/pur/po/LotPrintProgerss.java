package com.graly.erp.pur.po;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.graly.erp.base.print.PrintUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class LotPrintProgerss implements IRunnableWithProgress {
	protected static int TASK_NUMBER = 32;
	protected static int INIT_TASK = 0;
	protected static int repeatTime = 1;
	protected static boolean doublePrint = true;
	protected List<Lot> lots;
	protected boolean isFinished = false;
	public LotPrintProgerss(){
	}
	
	public LotPrintProgerss(List<Lot> lots){
		this(lots, 1, true);
	}
	
	public LotPrintProgerss(List<Lot> lots, int repeatTime, boolean doublePrint){
		this.lots = lots;
		this.repeatTime = repeatTime;
		this.doublePrint = doublePrint;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try{
			PrintService service = PrintUtil.getDefaultPrintService();
			TASK_NUMBER = lots.size();
			monitor.beginTask("  ", TASK_NUMBER);
			int finishedWorked = INIT_TASK;
			
			List<String> lotIdList = new ArrayList<String>();
			for (int i = 1; i <= lots.size(); i++) {
				for(int j = 1; j <= repeatTime; j++){
					lotIdList.add(lots.get(i-1).getLotId());
				}
			}
			
			if (doublePrint) {
				for(int i = 1; i < lotIdList.size() + 1; i++) {
					if(monitor.isCanceled())
						return;
					if (i % 2 == 0) {
						monitor.setTaskName(String.format(Message.getString("bas.lot_print_hint"), TASK_NUMBER, i - 1, lotIdList.get(i - 1)));
						PrintUtil.print(service, lotIdList.get(i - 2), lotIdList.get(i - 1));
						Thread.currentThread().sleep(1000);
					}
					monitor.worked(1);
					finishedWorked++;
				}
				if (lotIdList.size() % 2 != 0) {
					PrintUtil.print(service, lotIdList.get(lotIdList.size() - 1));
				}
			} else {
				for(int i = 0; i < lotIdList.size(); i++) {
					if(monitor.isCanceled())
						return;
					monitor.setTaskName(String.format(Message.getString("bas.lot_print_hint"), TASK_NUMBER, i, lotIdList.get(i)));
					PrintUtil.print(service, lotIdList.get(i));
					Thread.currentThread().sleep(1000);
					monitor.worked(1);
					finishedWorked++;
				}
			}
			
			if(finishedWorked < TASK_NUMBER) 
				monitor.worked(TASK_NUMBER - finishedWorked);
			monitor.done();
			this.isFinished = true;
			
		}catch(ClientException e){
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

//	protected void executeOneWorked(IProgressMonitor monitor, PrintService service, int index) throws ClientException{
//		Lot lot = lots.get(index-1);
//		monitor.setTaskName(String.format(Message.getString("bas.lot_print_hint"), TASK_NUMBER, index-1, lot.getLotId()));
//		PrintUtil.print(service, lot.getLotId());
//	}
	
	public boolean isFinished() {
		return isFinished;
	}

}

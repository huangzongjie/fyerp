package com.graly.erp.inv.rack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.graly.erp.base.print.PrintUtil;
import com.graly.erp.inv.model.WarehouseRack;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class RackPrintProgress implements IRunnableWithProgress {
	protected static int TASK_NUMBER = 32;
	protected static int INIT_TASK = 0;
	protected static int repeatTime = 1;
	protected static boolean doublePrint = true;
	protected boolean isFinished = false;
	protected List<WarehouseRack> racks;
	
	public RackPrintProgress(List<WarehouseRack> racks, int repeatTime, boolean doublePrint){
		this.racks = racks;
		this.repeatTime = repeatTime;
		this.doublePrint = doublePrint;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
	InterruptedException {
		try{
			PrintService service = PrintUtil.getDefaultPrintService();
			TASK_NUMBER = racks.size();
			monitor.beginTask("  ", TASK_NUMBER);
			int finishedWorked = INIT_TASK;
			
			List<String> rackIdList = new ArrayList<String>();
			for (int i = 1; i <= racks.size(); i++) {
				for(int j = 1; j <= repeatTime; j++){
					rackIdList.add(racks.get(i-1).getRackId());
				}
			}
			
			if (doublePrint) {
				for(int i = 1; i < rackIdList.size() + 1; i++) {
					if(monitor.isCanceled())
						return;
					if (i % 2 == 0) {
						monitor.setTaskName(String.format(Message.getString("bas.lot_print_hint"), TASK_NUMBER, i - 1, rackIdList.get(i - 1)));
						PrintUtil.print(service, rackIdList.get(i - 2), rackIdList.get(i - 1), PrintUtil.PrintType.RACKID);
						Thread.currentThread().sleep(400);
					}
					monitor.worked(1);
					finishedWorked++;
				}
				if (rackIdList.size() % 2 != 0) {
					PrintUtil.print(service, rackIdList.get(rackIdList.size() - 1));
				}
			} else {
				for(int i = 0; i < rackIdList.size(); i++) {
					if(monitor.isCanceled())
						return;
					monitor.setTaskName(String.format(Message.getString("bas.lot_print_hint"), TASK_NUMBER, i, rackIdList.get(i)));
					PrintUtil.print(service, rackIdList.get(i), PrintUtil.PrintType.RACKID);
					Thread.currentThread().sleep(400);
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

	
	public boolean isFinished() {
		return isFinished;
	}
}

package com.graly.erp.pdm.material;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;

public class CalculateVolumeProgress implements IRunnableWithProgress {
	private static final Logger logger = Logger.getLogger(CalculateVolumeProgress.class);
			
	protected static int TASK_NUMBER = 32;
	protected static int INIT_TASK = 0;
	private boolean isFinished = false;
	protected List<Material> materials;
	
	public CalculateVolumeProgress(List<Material> materials) {
		this.materials = materials;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			PDMManager pdmManager = Framework.getService(PDMManager.class);
			// 设置总任务数
			TASK_NUMBER = materials.size();
			monitor.beginTask("  ", TASK_NUMBER);

			int finishedWorked = INIT_TASK;
			int i = 0;
			for (Material m : materials) {
				monitor.setTaskName(String.format(Message.getString("pdm.is_calucate_volume"), TASK_NUMBER, i++ ,m.getMaterialId()));
				m = pdmManager.calculateVolumeByBOM(m);
				monitor.worked(1);
				finishedWorked++;
			}
			if (finishedWorked < TASK_NUMBER)
				monitor.worked(TASK_NUMBER - finishedWorked);
			monitor.done();
			isFinished = true;
		} catch (Exception e) {
			logger.error("Error at CalculateVolumeProgress : run() ", e);
			return;
		}
	}
	
	public boolean isFinished() {
		return isFinished;
	}

}

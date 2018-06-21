package com.graly.erp.wip.workcenter;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class MoLineTableListManager extends TableListManager {
	public MoLineTableListManager(ADTable adTable, int style) {
		super(adTable, style);
	}

	public MoLineTableListManager(ADTable adTable) {
		super(adTable);
	}

	@Override
	public void updateView(StructuredViewer viewer) {
		ADManager adManager = null;
		try {
			adManager = Framework.getService(ADManager.class);
		} catch (Exception e1) {
		}
		Table table = ((TableViewer)viewer).getTable();
		table.setRedraw(false);
		int index = 0;
		for (TableItem tableItem : table.getItems()){
            tableItem.setBackground(((index & 1) == 1) ? oddBackground : evenBackground);
            tableItem.setForeground(((index & 1) == 1) ? oddForeground : evenForeground);
            Object data = tableItem.getData();
            if(data != null && data instanceof ManufactureOrderLine){
            	ManufactureOrderLine moLine = (ManufactureOrderLine) data;
            	Long moRrn = moLine.getMasterMoRrn();
            	if(moRrn != null && moRrn != 0 && !moRrn.equals(moLine.getObjectRrn())){
            		ManufactureOrder mo = null;
            		try {
            			mo = new ManufactureOrder();
            			mo.setObjectRrn(moRrn);
						mo = (ManufactureOrder) adManager.getEntity(mo);
					} catch (ClientException e) {
						e.printStackTrace();
					}
					if(mo != null){
						if(mo.getMaterialRrn().equals(moLine.getMaterialRrn())){
							Font font = new Font(Display.getDefault(),"ËÎÌו",10,SWT.BOLD); 
							tableItem.setFont(font);
							long planAlarmQty = moLine.getPlanNoticeQty()==null?0:moLine.getPlanNoticeQty();
							if(planAlarmQty>0){
								Color color = Display.getDefault().getSystemColor(SWT.COLOR_RED);
								tableItem.setBackground(color);
							}
							table.redraw();
						}
					}
            	}
            }
            index++;
		}
		table.setRedraw(true);
	}
}

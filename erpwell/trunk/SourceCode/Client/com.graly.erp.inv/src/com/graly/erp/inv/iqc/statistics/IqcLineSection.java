package com.graly.erp.inv.iqc.statistics;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.IqcLine;
import com.graly.erp.inv.model.VInDetail;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;

public class IqcLineSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(IqcLineSection.class);
	
	private static final String TABLE_NAME = "INVIqcLineStatistics";
	private ADTable adTable;
	
	public IqcLineSection() {
		super();
	}
	
	public IqcLineSection(EntityTableManager tableManager) {
		super(tableManager);
	}
	
	protected ADTable getADTableByName() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch(Exception e) {
			logger.error("IqcLineSection : getADTableByName()", e);
		}
		return null;
	}
	
	@Override
	public void refresh() {
		super.refresh();
		Table table = ((TableViewer)viewer).getTable();
		IqcLine iqcLine = new IqcLine();
		iqcLine.setIqcId(Message.getString("inv.total"));
		BigDecimal qtyReceipt = BigDecimal.ZERO;
		BigDecimal qtyIqc = BigDecimal.ZERO;
		BigDecimal qtyPass = BigDecimal.ZERO;
		BigDecimal qtyFailed = BigDecimal.ZERO;
		BigDecimal qtyConcession = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof IqcLine) {
				IqcLine line = (IqcLine)obj;
				if (line.getQtyIn() != null) {
					qtyReceipt = qtyReceipt.add(line.getReceiptLineQtyReceipt());
				}
				if (line.getQtyIqc() != null) {
					qtyIqc = qtyIqc.add(line.getQtyIqc());
				}
				
				if (line.getQtyPass() != null) {
					qtyPass = qtyPass.add(line.getQtyPass());
				}
				if (line.getQtyFailed() != null) {
					qtyFailed = qtyFailed.add(line.getQtyFailed());
				}
				if (line.getQtyConcession() != null) {
					qtyConcession = qtyConcession.add(line.getQtyConcession());
				}
			}
		}
		iqcLine.setQtyReceipt(qtyReceipt);
		iqcLine.setQtyIqc(qtyIqc);
		iqcLine.setQtyPass(qtyPass);
		iqcLine.setQtyFailed(qtyFailed);
		iqcLine.setQtyConcession(qtyConcession);
		TableViewer tv = (TableViewer)viewer;
		tv.insert(iqcLine, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"ËÎÌו",10,SWT.BOLD); 
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}
	
	
}

package com.graly.erp.wip.output;

import java.math.BigDecimal;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.graly.erp.inv.model.MovementLine;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Message;
import com.graly.mes.wiphis.model.LotHis;

public class OutputSection extends MasterSection {

	public OutputSection() {
		super();
	}

	public OutputSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	public void refresh() {
		super.refresh();
		doViewerAggregation();
	}
	
	//将合计显示在viewer的最下面
	public void doViewerAggregation(){
		Table table = ((TableViewer)viewer).getTable();
		LotHis wiphisSum = new LotHis();
		wiphisSum.setMoId(Message.getString("inv.total"));
		BigDecimal qtyTransiaction = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof LotHis) {
				LotHis his = (LotHis)obj;
				if (his.getQtyTransaction() != null) {
					qtyTransiaction = qtyTransiaction.add(his.getQtyTransaction());
				}
			}
		}
		wiphisSum.setQtyTransaction(qtyTransiaction);
		TableViewer tv = (TableViewer)viewer;
		tv.insert(wiphisSum, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"宋体",10,SWT.BOLD); 
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}
}

package com.graly.erp.inv.wirteoffselect;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.VInDetail;
import com.graly.erp.inv.model.VPinDetail;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Message;

public class MovementInListSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MovementInListSection.class);
	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;
	protected WirteOffDialog wirteOffDialog;

	protected TableListManager listTableManager;
	private MovementInListEntryPage page;

	public MovementInListSection(EntityTableManager tableManager,MovementInListEntryPage page) {
		super(tableManager);
		this.page = page;
	}

	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		super.createContents(form, parent, sectionStyle);
		section.setText(String.format(Message.getString("common.list"),
				 Message.getString("inv.wirteoff_search")));
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void queryAdapter() {
		if(wirteOffDialog == null){
			wirteOffDialog= new WirteOffDialog(page);
			if (wirteOffDialog.open() == IDialogConstants.OK_ID) {
			}
			wirteOffDialog.setVisible(false);
		}else{
			wirteOffDialog.setVisible(true);
		}		
	}
	
	@Override
	public void refresh(){
		super.refresh();
		Table table = ((TableViewer)viewer).getTable();
		VPinDetail totalDetail = new VPinDetail();
		totalDetail.setDocId(Message.getString("inv.total"));
		BigDecimal totalQty = BigDecimal.ZERO;
		BigDecimal totalPrice = BigDecimal.ZERO;
		BigDecimal accTotal = BigDecimal.ZERO;
		BigDecimal invoiceTotal = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof VPinDetail) {
				VPinDetail inDetail = (VPinDetail)obj;
				if (inDetail.getQtyMovement() != null) {
					totalQty = totalQty.add(inDetail.getQtyMovement());
				}
				if (inDetail.getLineTotal() != null) {
					totalPrice = totalPrice.add(inDetail.getLineTotal());
				}
				if (inDetail.getAssessLineTotal() != null) {
					accTotal = accTotal.add(inDetail.getAssessLineTotal());
				}
				if (inDetail.getInvoiceLineTotal() != null) {
					invoiceTotal = invoiceTotal.add(inDetail.getInvoiceLineTotal());
				}
			}
		}
		totalDetail.setQtyMovement(totalQty);
		totalDetail.setLineTotal(totalPrice);
		totalDetail.setAssessLineTotal(accTotal);
		totalDetail.setInvoiceLineTotal(invoiceTotal);
		TableViewer tv = (TableViewer)viewer;
		tv.insert(totalDetail, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"ËÎÌו",10,SWT.BOLD);
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}

	public WirteOffDialog getWirteOffDialog() {
		return wirteOffDialog;
	}

	public void setWirteOffDialog(WirteOffDialog wirteOffDialog) {
		this.wirteOffDialog = wirteOffDialog;
	}
}

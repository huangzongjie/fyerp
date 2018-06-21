package com.graly.erp.wip.materialused;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.VLotConsumeDetailByMO;
import com.graly.erp.inv.model.VLotConsumeDetailByPM;
import com.graly.erp.inv.model.VLotConsumeSumByMO;
import com.graly.erp.inv.model.VLotConsumeSumByPM;
import com.graly.erp.wip.model.MaterialUsed;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class MaterialUsedSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MaterialUsedSection.class);
	protected TableListManager listTableManager;
	protected MaterialUsedExtendDailog usedDialog;
	
	private List<MaterialUsed> input;
	private Long materialRrn;
	private String materialId;
	private String status;
	private Date dateStart;
	private Date dateEnd;
	
	public MaterialUsedSection() {
		super();
	}

	public MaterialUsedSection(TableListManager tableManager) {
		this();
		this.listTableManager = tableManager;
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		// Add Export button by Bruce 2012-3-3
		createToolItemExport(tBar);
		createToolItemSearch(tBar);
		section.setTextClient(tBar);
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else if(this.usedDialog != null && usedDialog.getEntityQueryDialog() != null) {
			queryDialog = usedDialog.getEntityQueryDialog();
			queryDialog.setVisible(true);
		} else {
			// 此种情况一般不会出现,因为在VendorAssess.open()已将queryDialog设置过来.之所以用
			// VendorAssessDialog(false)表示不创建queryDialog.而是显示调用VendorAssessQueryDialog.
			// 以便传入tableManager,否则会因为在vaDialog无tableId而导致调用getEntityTableManager时出错.
			MaterialUsedExtendDailog vaDialog = new MaterialUsedExtendDailog(false);
			queryDialog = vaDialog.new MaterialUsedQueryDialog(UI.getActiveShell(),
					getADTable(), this);
			vaDialog.setEntityQueryDialog(queryDialog);
			queryDialog.open();
		}
	}
	
	protected void createNewViewer(Composite client, final IManagedForm form){
		viewer = listTableManager.createViewer(client, form.getToolkit());
	}
	
	public void refresh(){
		try{
			if(materialRrn != null) {
				input = new ArrayList<MaterialUsed>();
				WipManager wipManager = Framework.getService(WipManager.class);
//				input = wipManager.getMoMaterialUsed(Env.getOrgRrn(), materialRrn, status, null);
				input = wipManager.getMoMaterialUsed(Env.getOrgRrn(), materialRrn, status, this.getWhereClause());
				if(input == null || input.size() == 0) {
					UI.showError(String.format(Message.getString("wip.material_is_not_used_by_mo"), materialId));
				}
				viewer.setInput(input);
				listTableManager.updateView(viewer);
				createSectionDesc(section);
				doViewerAggregation();
			}
		} catch (Exception e){
			logger.error("EntityBlock : refresh ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void createSectionDesc(Section section) {
		int count = 0;
		if(input != null) count = input.size();
		String text = Message.getString("common.totalshow");
		text = String.format(text, String.valueOf(count), String.valueOf(count));
		section.setDescription("  " + text);
	}
	
	protected ADTable getADTable() {
		return listTableManager.getADTable();
	}
	
	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof MaterialUsedExtendDailog) {
			this.usedDialog = (MaterialUsedExtendDailog)dialog;
		} else {
			this.usedDialog = null;
		}
	}
	
	public Long getMaterialRrn() {
		return materialRrn;
	}
	
	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}
	
	public String getMaterialId() {
		return materialId;
	}
	
	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}
	
	public void doViewerAggregation(){
		Table table = ((TableViewer)viewer).getTable();
		String modelClass = "com.graly.erp.wip.model.MaterialUsed";
		
		BigDecimal qtyUsed = BigDecimal.ZERO;
		BigDecimal qtyMoProduct = BigDecimal.ZERO;
		BigDecimal qtyMoReceive = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof MaterialUsed) {
				MaterialUsed o = (MaterialUsed)obj;
				if (o.getQtyUsed() != null) {
					qtyUsed = qtyUsed.add(o.getQtyUsed());
				}
				if (o.getQtyMoProduct() != null) {
					qtyMoProduct = qtyMoProduct.add(o.getQtyMoProduct());
				}
				if (o.getQtyMoReceive() != null) {
					qtyMoReceive = qtyMoReceive.add(o.getQtyMoReceive());
				}
			}
		}
		
		Object sumObj = null;
		
		if(modelClass.equalsIgnoreCase(MaterialUsed.class.getName())){
			sumObj = new MaterialUsed();
			((MaterialUsed)sumObj).setMoId(Message.getString("inv.total"));
			((MaterialUsed)sumObj).setQtyUsed(qtyUsed);
			((MaterialUsed)sumObj).setQtyMoProduct(qtyMoProduct);
			((MaterialUsed)sumObj).setQtyMoReceive(qtyMoReceive);
		}
		
		if(sumObj == null){
			return;
		}
		
		TableViewer tv = (TableViewer)viewer;
		tv.insert(sumObj, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"宋体",10,SWT.BOLD); 
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}
}

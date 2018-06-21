package com.graly.erp.pur.request.refmo;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.wizard.IWizardContext;
import com.graly.framework.runtime.Framework;

public class MoViewContext implements IWizardContext {
	private static final Logger logger = Logger.getLogger(MoViewContext.class);
	public static final String CAGEGORY_VIEW_MO = "viewMO";
	
	/* 用于可替代料表格各列名称 */
	public static final String MATERIAL_ID = Message.getString("pdm.material_id");
	public static final String NAME = Message.getString("pdm.material_name");
	public static final String MATERIAL_UOM = Message.getString("pdm.material_uom");
	public static final String UNIT_QTY = Message.getString("pdm.material_qtyunit");
	public static final String COMMENTS = Message.getString("pdm.material_comments");
	/* 用于可替代料表格各列属性 */
	public static String MaterialId = "materialId";
	public static String MaterialName = "materialName";
	public static String UomId = "uomId";
	public static String UnitQty = "unitQty";
	public static String Comments = "description";
	
	public static final String[] ColumnHeaders = new String[]{MATERIAL_ID, NAME, MATERIAL_UOM, UNIT_QTY, COMMENTS};
	public static final String[] Columns = new String[]{MaterialId, MaterialName, UomId, UnitQty, Comments};
	
	private String TableName_MOBom = "WIPManufactureOrderBom";
	private String TableName_DOLine = "BASDocumentationLine";
	private ADTable adTable_MO;
	private ADTable adTable_MOBom;
	private ADTable adTable_MOLine;
	
	private ManufactureOrder manufactureOrder;
	private List<ManufactureOrderBom> moBoms;
	private List<DocumentationLine> doLines;
	private String category;
	
	private IManagedForm mangedForm;
	private MoViewDialog dialog;

	public MoViewDialog getDialog() {
		return dialog;
	}

	public void setDialog(MoViewDialog dialog) {
		this.dialog = dialog;
	}

	public IManagedForm getMangedForm() {
		return mangedForm;
	}

	public void setMangedForm(IManagedForm mangedForm) {
		this.mangedForm = mangedForm;
	}

	public ADTable getAdTable_MO() {
		return adTable_MO;
	}

	public void setAdTable_MO(ADTable adTable_MO) {
		this.adTable_MO = adTable_MO;
	}

	public ManufactureOrder getManufactureOrder() {
		return manufactureOrder;
	}

	public void setManufactureOrder(ManufactureOrder manufactureOrder) {
		this.manufactureOrder = manufactureOrder;
	}

	public ADTable getAdTable_MOBom() {
		try {
			if(adTable_MOBom == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable_MOBom = entityManager.getADTable(0L, TableName_MOBom);
			}
		} catch(Exception e) {
			logger.error("MOGenerateContext : getAdTable_MOBom()", e);
		}
		return adTable_MOBom;
	}

	public void setAdTable_MOBom(ADTable adTable_MOBom) {
		this.adTable_MOBom = adTable_MOBom;
	}

	public List<ManufactureOrderBom> getMoBoms() {
		return moBoms;
	}

	public void setMoBoms(List<ManufactureOrderBom> moBoms) {
		this.moBoms = moBoms;
	}

	public ADTable getAdTable_MOLine() {
		try {
			if(adTable_MOLine == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable_MOLine = entityManager.getADTable(0L, TableName_DOLine);
			}
		} catch(Exception e) {
			logger.error("MOGenerateContext : getAdTable_MOBom()", e);
		}
		return adTable_MOLine;
	}

	public void setAdTable_MOLine(ADTable adTable_MOLine) {
		this.adTable_MOLine = adTable_MOLine;
	}

	public List<DocumentationLine> getDoLines() {
		return doLines;
	}

	public void setDoLines(List<DocumentationLine> doLines) {
		this.doLines = doLines;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}

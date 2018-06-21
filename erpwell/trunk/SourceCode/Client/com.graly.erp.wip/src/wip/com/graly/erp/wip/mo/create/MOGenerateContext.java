package com.graly.erp.wip.mo.create;

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

public class MOGenerateContext implements IWizardContext {
	private static final Logger logger = Logger.getLogger(MOGenerateContext.class);
	public static final String CATEGORY_NEW = "generateMO";
	public static final String CAGEGORY_EDIT = "editMO";
	public static final String CAGEGORY_PREPARE_NEW = "prepareGenerateMO";
	public static final String CAGEGORY_PREPARE_EDIT = "editPrepareMO";
	
	/* ���ڿ�����ϱ��������� */
	public static final String MATERIAL_ID = Message.getString("pdm.material_id");
	public static final String NAME = Message.getString("pdm.material_name");
	public static final String MATERIAL_UOM = Message.getString("pdm.material_uom");
	public static final String UNIT_QTY = Message.getString("pdm.material_qtyunit");
	public static final String COMMENTS = Message.getString("pdm.material_comments");
	public static final String IS_PERPARE_MO_LINE = "����������";
	public static final String AGAIN_GEN_MO_LINE = "���ɹ�����";
	/* ���ڿ�����ϱ��������� */
	public static String MaterialId = "materialId";
	public static String MaterialName = "materialName";
	public static String UomId = "uomId";
	public static String UnitQty = "unitQty";
	public static String Comments = "description";
	public static String IsPrepareMoLine = "isPrepareMoLine";
	public static final String AgainGenMoLine = "againGenMoLine";
	
	public static final String[] ColumnHeaders = new String[]{MATERIAL_ID, NAME, MATERIAL_UOM, UNIT_QTY, COMMENTS,IS_PERPARE_MO_LINE,AGAIN_GEN_MO_LINE};
	public static final String[] Columns = new String[]{MaterialId, MaterialName, UomId, UnitQty, Comments,IsPrepareMoLine,AgainGenMoLine};
	
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
	private MOGenerateDialog dialog;

	public MOGenerateDialog getDialog() {
		return dialog;
	}

	public void setDialog(MOGenerateDialog dialog) {
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

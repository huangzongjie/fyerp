package com.graly.erp.wip.mo.create;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.BomDetail;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MOEditBomDialog extends EntityDialog {
	private static final Logger logger = Logger.getLogger(MOEditBomDialog.class);
	private MOAlternateSelectSection section;
	private ManufactureOrderBom selectedBom;//当前选中的树节点
	private boolean isRoot= true;//是否是根节点
	
	public MOEditBomDialog(Shell parent, ADTable table, Bom editorBom, MOAlternateSelectSection section,ManufactureOrderBom selectedBom,boolean isRoot){
		super(parent, table, editorBom);
		this.section = section;
		this.selectedBom = selectedBom;
		this.isRoot =  isRoot;
	}
	
	// 重载该方法实现验证错误时提示信息放在Dialog头部(放在放在各个控件前端会出现滚动条)
	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		managedForm = new ManagedForm(toolkit, sForm);
		final IMessageManager mmng = managedForm.getMessageManager();
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		EntityForm itemForm = new BomEditForm(body, SWT.NONE, adObject, table, mmng, this);
		itemForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		getDetailForms().add(itemForm);
	}
	
 
	@Override
    protected void okPressed() {
		setReturnCode(OK);
		this.close();
    }
	
	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	@Override
	protected boolean saveAdapter() {
		try {
			managedForm.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						setAdObject((Bom)detailForm.getObject());
					}
					
						Bom dialogBom = (Bom)getDetailForms().get(0).getObject();
						List<BomDetail> bomDetails = new ArrayList<BomDetail>();
						List<ManufactureOrderBom> moBoms  = section.getMOBoms();
						//校验选中物料的子物料是否包含新添加的物料
						ADManager adManager = Framework.getService(ADManager.class);
						Material material = new Material();
						material.setOrgRrn(Env.getOrgRrn());
						material.setObjectRrn(dialogBom.getChildRrn());
						material =(Material) adManager.getEntity(material);
						for(ManufactureOrderBom subBom : MOBomItemAdapter.getChildMoBom(selectedBom)){
							if(subBom.getMaterialId().equals(material.getMaterialId())){
								UI.showError("该物料已存在");
								return false;
							}
						}
						//添加的物料有可能是个商品
						if(!isRoot){
							//树节点物料添加物料
							String path = selectedBom.getPath()+dialogBom.getParentRrn() + "/";
							BomDetail bomDetail = new BomDetail();
							bomDetail.setMaterialRrn(dialogBom.getParentRrn());
							bomDetail.setMaterialVersion(dialogBom.getParentVersion());
							bomDetail.setPath(path);
							bomDetail.setRealPath(path);
							bomDetail.setPathLevel(selectedBom.getPathLevel()+1);
							bomDetail.setRealPathLevel(selectedBom.getPathLevel()+1);
							bomDetail.setParentRrn(dialogBom.getParentRrn());
							bomDetail.setChildRrn(dialogBom.getChildRrn());
							bomDetail.setSeqNo(dialogBom.getSeqNo());
							bomDetail.setUnitQty(dialogBom.getUnitQty());
							bomDetail.setQtyBom(selectedBom.getQtyBom().multiply(dialogBom.getUnitQty()));
							bomDetail.setDescription(dialogBom.getDescription());
							 
							bomDetail.setChildMaterial(material);
							internalGetBomDetail(bomDetail, bomDetails,true);
							
							for(BomDetail bomDetail2 : bomDetails){
								ManufactureOrderBom moBom = new ManufactureOrderBom();
								moBom.setOrgRrn(Env.getOrgRrn());
								moBom.setIsActive(true);
								moBom.setMaterialParentRrn(bomDetail2.getParentRrn());
								moBom.setMaterialRrn(bomDetail2.getChildRrn());
								moBom.setPath(bomDetail2.getPath());
								moBom.setRealPath(bomDetail2.getRealPath());//记录不过滤虚拟料的path
								moBom.setPathLevel(bomDetail2.getPathLevel());
								moBom.setRealPathLevel(bomDetail2.getRealPathLevel());
								moBom.setLineNo(bomDetail2.getSeqNo());
								moBom.setUnitQty(bomDetail2.getUnitQty());
								moBom.setQtyBom(bomDetail2.getQtyBom());
								moBom.setMaterial(bomDetail2.getChildMaterial());
								if (bomDetail2.getChildMaterial() != null) {
									moBom.setUomId(bomDetail2.getChildMaterial().getInventoryUom());
								}
								moBom.setDescription(bomDetail2.getDescription());
								moBoms.add(moBom);
							}

						}else{
							//根物料添加物料
							String path = dialogBom.getParentRrn() + "/";
							BomDetail bomDetail = new BomDetail();
							bomDetail.setMaterialRrn(dialogBom.getParentRrn());
							bomDetail.setMaterialVersion(dialogBom.getParentVersion());
							bomDetail.setPath(path);
							bomDetail.setRealPath(path);
							bomDetail.setPathLevel(1L);
							bomDetail.setRealPathLevel(1L);
							bomDetail.setParentRrn(dialogBom.getParentRrn());
							bomDetail.setChildRrn(dialogBom.getChildRrn());
							bomDetail.setSeqNo(dialogBom.getSeqNo());
							bomDetail.setUnitQty(dialogBom.getUnitQty());
							bomDetail.setQtyBom(dialogBom.getUnitQty());
							bomDetail.setDescription(dialogBom.getDescription());
							bomDetail.setChildMaterial(material);
							internalGetBomDetail(bomDetail, bomDetails,true);
							
							for(BomDetail bomDetail2 : bomDetails){
								ManufactureOrderBom moBom = new ManufactureOrderBom();
								moBom.setOrgRrn(Env.getOrgRrn());
								moBom.setIsActive(true);
								moBom.setMaterialParentRrn(bomDetail2.getParentRrn());
								moBom.setMaterialRrn(bomDetail2.getChildRrn());
								moBom.setPath(bomDetail2.getPath());
								moBom.setRealPath(bomDetail2.getRealPath());//记录不过滤虚拟料的path
								moBom.setPathLevel(bomDetail2.getPathLevel());
								moBom.setRealPathLevel(bomDetail2.getRealPathLevel());
								moBom.setLineNo(bomDetail2.getSeqNo());
								moBom.setUnitQty(bomDetail2.getUnitQty());
								moBom.setQtyBom(bomDetail2.getQtyBom());
								moBom.setMaterial(bomDetail2.getChildMaterial());
								if (bomDetail2.getChildMaterial() != null) {
									moBom.setUomId(bomDetail2.getChildMaterial().getInventoryUom());
								}
								moBom.setDescription(bomDetail2.getDescription());
								moBoms.add(moBom);
							}
						}
						
						MOBomItemAdapter.setMoBoms(moBoms);
						section.getViewer().refresh(selectedBom);
						section.refresh();

					UI.showInfo(Message.getString("common.save_successed"));
					return true;
				} return false;
			}
		} catch (Exception e) {
			logger.error("Error at BomEditorDialog saveAdapter() : " + e);
			ExceptionHandlerManager.asyncHandleException(e);
			return false;
		}
		return false;
	}
	
	class BomEditForm extends EntityForm {
		protected MOEditBomDialog dialog;
		public BomEditForm(Composite parent, int style, Object obj, ADTable table,
				IMessageManager mmng, MOEditBomDialog dialog) {
	    	super(parent, style, obj, table, mmng);
	    	this.dialog = dialog;
	    }

		// 重载该方法, 使错误提示信息放Dialog的头部
		@Override
		public boolean validate() {
			this.setErrorMessage(null);
			boolean validFlag = true;
			for (IField f : fields.values()){
				ADField adField = adFields.get(f.getId());
				if(adField != null){
					if (adField.getIsMandatory()){
						Object value = f.getValue();
						boolean isMandatory = false;
						if (value == null){
							isMandatory = true;
						} else {
							if (value instanceof String){
								if ("".equalsIgnoreCase(value.toString().trim())){
									isMandatory = true;
								}
							}
						}
						if (isMandatory){
							validFlag = false;
							this.setErrorMessage(String.format(Message.getString("common.ismandatory"), I18nUtil.getI18nMessage(adField, "label")));
							return validFlag;
						}
					}
					if (adField.getDataType() != null && !"".equalsIgnoreCase(adField.getDataType().trim())){
						if (!(f.getValue() instanceof String)){
							continue;
						}
						String value = (String)f.getValue();
						if (value != null && !"".equalsIgnoreCase(value.trim())){
							if (!ValidatorFactory.isValid(adField.getDataType(), value)){
								validFlag = false;
								this.setErrorMessage(String.format(Message.getString("common.isvalid"), I18nUtil.getI18nMessage(adField, "label"), adField.getDataType()));
								return validFlag;
							} else if (!ValidatorFactory.isInRange(adField.getDataType(), value, adField.getMinValue(), adField.getMaxValue())){
								validFlag = false;
								if ((adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim()))
								 && (adField.getMaxValue() != null && !"".equalsIgnoreCase(adField.getMaxValue().trim()))){
									this.setErrorMessage(String.format(Message.getString("common.between"), I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue(), adField.getMaxValue()));
								} else if (adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim())){
									this.setErrorMessage(String.format(Message.getString("common.largerthan"), I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue()));
								} else {
									this.setErrorMessage(String.format(Message.getString("common.lessthan"), I18nUtil.getI18nMessage(adField, "label"), adField.getMaxValue()));
								}
								return validFlag;
							}
						}
					}
					if (adField.getNamingRule() != null && !"".equalsIgnoreCase(adField.getNamingRule().trim())){
						Object value = f.getValue();
						if (value == null){
							continue;
						}
						if (value instanceof String){
							if (!Pattern.matches(adField.getNamingRule(), value.toString())) {
								validFlag = false;
								this.setErrorMessage(String.format(Message.getString("common.namingrule_error"), I18nUtil.getI18nMessage(adField, "label")));
								return validFlag;
							}
						}
					}
				}
				
			}
			return validFlag;
		}
		
		protected void setErrorMessage(String errMeg) {
			if(dialog != null) {
				dialog.setErrorMessage(errMeg);
			}
		}
	}
	
	
	
	//---
	//递归获得全部BOM，ignoreVirtual表示是否剔除虚拟料
	protected void internalGetBomDetail(BomDetail parent, List<BomDetail> bomDetails, boolean ignoreVirtual) {
		
		String path = new String(parent.getPath());
		String realPath = null;//记录真实的path，包括虚拟父物料的path
		if(parent.getRealPath() == null || parent.getRealPath().trim().length() == 0){
			realPath = new String(parent.getPath());
		}else{
			realPath = new String(parent.getRealPath());
		}
		long level = 0;
		long realLevel = 0;//记录真实的level，包括虚拟父物料
		if(parent.getRealPathLevel() != null){
			realLevel = parent.getRealPathLevel();
		}
		//检查嵌套
		if (path.indexOf(parent.getChildRrn().toString()) > -1) {
			 new ClientException("pdm.bom_nesting");
		}
		if (ignoreVirtual && parent.getChildMaterial().getIsVirtual()) {
			level = parent.getPathLevel();
			realPath += parent.getChildRrn() + "/";
			realLevel = realLevel + 1;
		} else {
			bomDetails.add(parent);
			path += parent.getChildRrn() + "/";
			level = parent.getPathLevel() + 1;
			realPath += parent.getChildRrn() + "/";
			realLevel = realLevel + 1;
		}
		List<Bom> children = new ArrayList<Bom>();
		try {

			PDMManager pdmManager  = Framework.getService(PDMManager.class);
			children  = pdmManager.getChildrenBoms(parent.getChildRrn(), parent.getUnitQty());
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Bom bom : children) {
			BomDetail bomDetail = new BomDetail();
			if (ignoreVirtual && parent.getChildMaterial().getIsVirtual()) {
				bomDetail.setParentRrn(parent.getParentRrn());
				bomDetail.setUnitQty(parent.getUnitQty().multiply(bom.getUnitQty()));
			} else {
				bomDetail.setParentRrn(bom.getParentRrn());
				bomDetail.setUnitQty(bom.getUnitQty());
			}
			bomDetail.setMaterialRrn(parent.getMaterialRrn());
			bomDetail.setMaterialVersion(parent.getMaterialVersion());
			bomDetail.setPath(path);
			bomDetail.setRealPath(realPath);//记录真实的path包括被过滤掉的虚拟父料
			bomDetail.setPathLevel(level);
			bomDetail.setRealPathLevel(realLevel);
			bomDetail.setChildRrn(bom.getChildRrn());
			bomDetail.setSeqNo(bom.getSeqNo());
			bomDetail.setQtyBom(bom.getQtyBom());
			bomDetail.setDescription(bom.getDescription());
			bomDetail.setChildMaterial(bom.getChildMaterial());
			internalGetBomDetail(bomDetail, bomDetails, ignoreVirtual);
		}
 
	}
	
	
}

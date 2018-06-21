package com.graly.erp.wip.workcenter.receive;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.workcenter.receive.ReciveLotInfoForm.FillFullTextField;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.activeentity.model.ADUserRefList;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WIPMould;
import com.graly.mes.wip.client.LotManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotReceiveTemp;

public class MoLineReceiveForm extends EntityForm {
	private static final Logger logger = Logger.getLogger(MoLineReceiveForm.class);
	
	private Lot parentLot;
	private boolean canEdit = true;
	private String lotType;
	private INVManager invManager;
	private String[] qtySetupLabel;
	private boolean isSetup = false;
	private boolean isDisplayLotType = false;
	private boolean isCreateComments = false;
	private String comments;

	public MoLineReceiveForm(Composite parent, int style, Object object,
			IMessageManager mmng, String lotType, boolean isSetup) {
		super(parent, style, object, mmng);
		this.lotType = lotType;
		this.isSetup = isSetup;
		initParentLot();
		initQtySetupLable();
    }
	
	public MoLineReceiveForm(Composite parent, int style, Object object,
			IMessageManager mmng, String lotType, Lot parentLot) {
		super(parent, style, object, mmng);
		this.lotType = lotType;
		this.parentLot = parentLot;
		initQtySetupLable();
    }
	
	@Override
    public void createForm(){}
	
    public void createFormContent(){
        try {
        	super.setGridY(this.getGridY());
        	createADFields();
        } catch (Exception e) {
        	logger.error("MoLineReceiveForm : createForm", e);
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        super.createForm();
        LinkedHashMap<String, IField>  fields = getFields();
		IField field =  fields.get("delayReason");
		if(field!=null){
			field.addValueChangeListener(getDelayDeptChangedListener());
		}

    }
	
	@Override
    public boolean saveToObject() {
		mmng.removeAllMessages();
		if (object instanceof ManufactureOrderLine) {
			if (!validate()){
				return false;
			}
			TextField tfLotId = (TextField)fields.get(TextProvider.FieldName_LotId);
			TextField tfBatchQty = (TextField)fields.get(TextProvider.FieldName_BatchQty);
			RefTableField tfDelayReason = (RefTableField)fields.get(TextProvider.FieldName_DelayReason);
			TextField tfDelayReasonDetail = (TextField)fields.get(TextProvider.FieldName_DelayReasonDetail);
			RefTableField mpsLineDeliveryField = (RefTableField)fields.get(TextProvider.FieldName_Mps_Line_Delivery_Rrn);
//			ComboField cf = (ComboField) fields.get(TextProvider.FieldName_DelayDept);
			RefTableField cf = (RefTableField)fields.get(TextProvider.FieldName_DelayDept);
			ManufactureOrderLine moLine = (ManufactureOrderLine)object;
			
			// 如果为Material类型，则在初始化时就已经从DB中获得，此时只需将当前数量设置进去
			// 如果为Serial类型，在此也是先赋给qtyCurrent，然后在接收保存时将其为BigDecimal.ONE
			if(!Lot.LOTTYPE_MATERIAL.equals(lotType)) {
				if(parentLot == null) {
					parentLot = new Lot();
				}
				parentLot.setOrgRrn(Env.getOrgRrn());
				parentLot.setLotType(moLine.getMaterial().getLotType());
				parentLot.setMaterialRrn(moLine.getMaterialRrn());
				parentLot.setLotId((String)tfLotId.getValue());
			}
			parentLot.setMoRrn(moLine.getMasterMoRrn());
			parentLot.setMoLineRrn(moLine.getObjectRrn());
			parentLot.setQtyCurrent(new BigDecimal(tfBatchQty.getValue().toString()));
			parentLot.setQtyTransaction(parentLot.getQtyCurrent());
			if(tfDelayReason!=null){//可能为空,没在接收日期超过交货日期3天（不包含3天）的商品,就不显示原因栏位
				parentLot.setDelayReason(tfDelayReason.getValue()==null?"":tfDelayReason.getValue().toString());
			}
			if(tfDelayReasonDetail!=null){
				parentLot.setDelayReasonDetail(tfDelayReasonDetail.getValue()==null?"":tfDelayReasonDetail.getValue().toString());
			}
			if(mpsLineDeliveryField!=null){
				long rrn = mpsLineDeliveryField.getValue()==null||"".equals(mpsLineDeliveryField.getValue())?0L:Long.valueOf(mpsLineDeliveryField.getValue().toString());
				parentLot.setMpsLineDeliveryRrn(rrn);
			}
			if(cf!=null){
				parentLot.setDelayDept(cf.getValue()==null?"":cf.getValue().toString());
			}
			
			if (moLine.getQty().compareTo(moLine.getQtyReceive().add(parentLot.getQtyCurrent())) < 0) {
				UI.showError(String.format(Message.getString("wip.receive_lot_larger_than_qty")));
				return false;
			}
			return true;
		}
		return false;
    }
	
	@Override
	public boolean validate() {
		if(Env.getOrgRrn() == 139420L){
			ADField adFieldDelayDetail = adFields.get(TextProvider.FieldName_DelayReasonDetail);
			if(adFieldDelayDetail!=null){
				TextField tfDelayDetail = (TextField)fields.get(TextProvider.FieldName_DelayReasonDetail);
				//校验字符串长度大于3 
				if(tfDelayDetail.getValue()!=null && !"".equals(tfDelayDetail.getValue())){
					String detailValue = (String) tfDelayDetail.getValue();
					int i = detailValue.length();
					if(i<5){
						UI.showError("延误原因内容栏位输入内容太少，请在输入一些...");
						return false;
					}
				}else{
					UI.showError("延误原因内容栏位不能为空，请输入值");
					return false;
				}
			}
		}
		// Material类型接收数量必须为Integer
		if(Lot.LOTTYPE_SERIAL.equals(lotType)) {
			ADField adField = adFields.get(TextProvider.FieldName_BatchQty);
			TextField tf = (TextField)fields.get(TextProvider.FieldName_BatchQty);
			if(tf.getValue() != null && !"".equals(tf.getValue())) {
				if(ValidatorFactory.isValid("integer", tf.getValue().toString())) {
					return true;
				} else {
					mmng.addMessage(adField.getName() + "common.isvalid", 
							String.format(Message.getString("common.isvalid"), I18nUtil.getI18nMessage(adField, "label"), adField.getDataType()), null,
							IMessageProvider.ERROR, tf.getControls()[tf.getControls().length - 1]);
				}
			}
		} else {
			return super.validate();
		}
		return false;
	}

	@Override
    public void loadFromObject() {
		ManufactureOrderLine moLine = (ManufactureOrderLine)object;
		if (object != null && object instanceof ManufactureOrderLine){			
			for (IField f : fields.values()){
				if(TextProvider.FieldName_LotId.equals(f.getId())) {
					if(parentLot == null || parentLot.getLotId() == null) {
						f.setValue(this.getLotId());
					} else f.setValue(parentLot.getLotId());
				} else if(TextProvider.FieldName_BatchQty.equals(f.getId())) {
					if(isSetup) {
						if(moLine.getQtyReceive() != null) {
							f.setValue(String.valueOf(moLine.getQty().subtract(moLine.getQtyReceive()).intValue()));
						} else {
							f.setValue(String.valueOf(moLine.getQty().intValue()));
						}
					} else {
						if(parentLot == null || parentLot.getQtyCurrent() == null
								|| parentLot.getQtyCurrent().compareTo(BigDecimal.ZERO) == 0) {
							f.setValue("1");
						} else f.setValue(String.valueOf(parentLot.getQtyCurrent().intValue()));						
					}
				} else if(TextProvider.FieldName_Comments.equals(f.getId())) {
					f.setValue(comments);
				}else if(TextProvider.FieldName_DelayReason.equals(f.getId())){
					if(parentLot == null ){
						f.setValue("");
					}else{
						f.setValue(parentLot.getDelayReason());
					}
				}else if(TextProvider.FieldName_DelayReasonDetail.equals(f.getId())){
					if(parentLot == null ){
						f.setValue("");
					}else{
						f.setValue(parentLot.getDelayReasonDetail());
					}
				}else if(TextProvider.FieldName_Mps_Line_Delivery_Rrn.equals(f.getId())){
					if(parentLot == null ){
						f.setValue("");
					}else{
						f.setValue(parentLot.getMpsLineDeliveryRrn());
					}
				}else if(TextProvider.FieldName_DelayDept.equals(f.getId())){
					LinkedHashMap<String, String> lmp = new LinkedHashMap<String, String>();
					if(parentLot == null ){
						f.setValue("");
					}else{
//						Object o = PropertyUtil.getPropertyForIField(object, f.getId());
//						f.setValue(o);
						f.setValue(parentLot.getDelayDept());
					}
				}else {
					Object o = PropertyUtil.getPropertyForIField(object, f.getId());
					f.setValue(o);
				}
			}
			refresh();
			setEnabled();
		}
    }
	
	// 如果为Material类型，则直接从DB中获取
	private void initParentLot() {
		try {
			qtySetupLabel = new String[2];
			ManufactureOrderLine moLine = (ManufactureOrderLine)object;
			if(Lot.LOTTYPE_MATERIAL.equals(moLine.getMaterial().getLotType())) {
				if(invManager == null) {
					invManager = Framework.getService(INVManager.class);
				}
				parentLot = invManager.getMaterialLotForMoLine(Env.getOrgRrn(),
						moLine.getMaterial(), Env.getUserRrn(), moLine);
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	private void initQtySetupLable() {
		qtySetupLabel = new String[2];
		if(Lot.LOTTYPE_MATERIAL.equals(lotType) || Lot.LOTTYPE_SERIAL.equals(lotType)) {
			qtySetupLabel[0] = "Receive Quantity";
			qtySetupLabel[1] = "接收数量";
		} else {
			qtySetupLabel[0] = "Quantity Per Lot";
			qtySetupLabel[1] = "每批数量";
		}
	}
	
	private String getLotId() {
		ManufactureOrderLine moLine = (ManufactureOrderLine)object;
		return this.getLotIdByMaterial(moLine.getMaterial());
	}
	
	private String getLotIdByMaterial(Material material) {
		String lotId = null;
		try {
			if(Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
				lotId = material.getMaterialId();
			} else if(Lot.LOTTYPE_BATCH.equals(material.getLotType())){
				
				LotManager lotManager = Framework.getService(LotManager.class);
				ManufactureOrderLine moLine = (ManufactureOrderLine)object;
				LotReceiveTemp lotReceiveTemp= null;
				lotReceiveTemp =lotManager.getLotReceiveTemp(moLine.getObjectRrn(), Env.getOrgRrn());
				//只针对batch类型,拿到存在数据库数据中临时保存的Lot_id 如果不存就自动生成一个
				if(lotReceiveTemp !=null){
					lotId = lotReceiveTemp.getLotID();
				}
				else{
					INVManager invManager = Framework.getService(INVManager.class);
					lotId = invManager.generateNextNumber(Env.getOrgRrn(), material);	
				}
			} else if(Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
				return material.getMaterialId();
			}
		} catch(Exception e) {
			logger.error("Error at MoLineReceiveForm : getLotIdByMaterial() ", e);
		}
		return lotId;
	}
	
	public void createLotId() {
		String lotId = getLotId();
		TextField tf = (TextField)fields.get(TextProvider.FieldName_LotId);
		if(tf != null) {
			tf.setValue(lotId);
			tf.refresh();
		}
	}
	
	private void createADFields() {
		ADField adField = new ADField();
    	adField.setName(TextProvider.FieldName_MaterialId);
    	adField.setIsDisplay(true);
    	adField.setLabel("Material ID");
    	adField.setLabel_zh("物料编号");
    	adField.setDisplayType(FieldType.TEXT);
    	adField.setIsReadonly(true);
    	allADfields.add(adField);
    	
    	adField = new ADField();
    	adField.setName(TextProvider.FieldName_MaterialName);
    	adField.setIsDisplay(true);
    	adField.setLabel("Material Name");
    	adField.setLabel_zh("物料名称");
    	adField.setDisplayType(FieldType.TEXT);
    	adField.setIsReadonly(true);
    	allADfields.add(adField);
    	
    	if(this.isDisplayLotType) {
    		adField = new ADField();
    		adField.setName(TextProvider.FieldName_LotType);
    		adField.setIsDisplay(true);
    		adField.setIsEditable(true);
    		adField.setIsReadonly(true);
    		adField.setLabel("Lot Type");
    		adField.setLabel_zh("批次类型");
    		adField.setDisplayType(FieldType.TEXT);
    		allADfields.add(adField);
    	}
    	
    	adField = new ADField();
    	adField.setName(TextProvider.FieldName_LotId);
    	adField.setIsDisplay(true);
    	adField.setIsEditable(true);
    	if(!canEdit || Lot.LOTTYPE_MATERIAL.equals(lotType) || Lot.LOTTYPE_SERIAL.equals(lotType))	// 
    		adField.setIsReadonly(true);
    	adField.setLabel("Lot ID");
    	adField.setLabel_zh("批号");
    	adField.setDisplayType(FieldType.TEXT);
    	adField.setIsMandatory(true);
    	allADfields.add(adField);
    	
    	adField = new ADField();
    	adField.setName(TextProvider.FieldName_BatchQty);
    	adField.setIsDisplay(true);
    	adField.setIsEditable(true);
    	adField.setIsSameline(true);
    	if(!canEdit) {	// || Lot.LOTTYPE_SERIAL.equals(lotType)
    		adField.setIsReadonly(true);
    	}
    	if(Lot.LOTTYPE_SERIAL.equals(lotType)) {
    		adField.setDataType("integer");
    	} else {
    		adField.setDataType("double");
    	}
    	adField.setMinValue("0.0000001");
    	adField.setLabel(qtySetupLabel[0]);
    	adField.setLabel_zh(qtySetupLabel[1]);
    	adField.setDisplayType(FieldType.TEXT);
    	adField.setIsMandatory(true);
    	allADfields.add(adField);
    	
    	if(this.isCreateComments) {
    		adField = new ADField();
    		adField.setName(TextProvider.FieldName_Comments);
    		adField.setIsDisplay(true);
    		adField.setIsEditable(true);
    		adField.setIsReadonly(true);
    		adField.setLabel("Comments");
    		adField.setLabel_zh("备注");
    		adField.setDisplayType(FieldType.TEXTAREA);
    		allADfields.add(adField);
    	}
    	
    	if(Env.getOrgRrn()==139420L){
    		//在接收日期超过交货日期3天（不包含3天）的商品,添加延误原因     曹惠峰
    		ManufactureOrderLine moLine = (ManufactureOrderLine)object;
    		Date currentDate = Env.getSysDate();
    		if(currentDate.compareTo(moLine.getDateEnd()) ==1 ){
    			if("商品".equals(moLine.getMaterial().getMaterialCategory1())){
        			long receiveDate = currentDate.getTime();
        			long endDate = moLine.getDateEnd().getTime();
        			double delayDays = (double)(receiveDate - endDate)/86400000;
        			if(delayDays >1){
        	    		adField = new ADField();
        	    		adField.setName(TextProvider.FieldName_DelayReason);
        	    		adField.setIsDisplay(true);
        	    		adField.setIsEditable(true);
        	    		adField.setIsReadonly(false);
        	    		adField.setLabel("delayReason");
        	    		adField.setLabel_zh("延误原因");
        	    		adField.setDisplayType(FieldType.USERREFLIST);
        	    		adField.setReftableRrn(14479L);
        	    		adField.setUserReflistName("DelayReason");
        	    		adField.setIsMandatory(true);
        	        	if(!canEdit) {
        	        		adField.setIsReadonly(true);
        	        	}
        	    		allADfields.add(adField);
        	    		
        	    		
          	    		adField = new ADField();
        	    		adField.setName(TextProvider.FieldName_DelayDept);
        	    		adField.setIsDisplay(true);
        	    		adField.setIsEditable(true);
        	    		adField.setIsReadonly(false);
        	    		adField.setLabel("delayReason");
        	    		adField.setLabel_zh("部门");
//        	    		adField.setDisplayType(FieldType.COMBO);
        	    		adField.setDisplayType("dropdownlist");
           	    		adField.setDisplayType(FieldType.USERREFLIST);
        	    		adField.setReftableRrn(14479L);
        	    		adField.setDataType("String");
        	    		adField.setIsMandatory(true);
        	    		if(!canEdit) {
        	        		adField.setIsReadonly(true);
        	        		adField.setUserReflistName("DelayDept");
        	        	}
        	    		allADfields.add(adField);
        	    		
          	    		adField = new ADField();
        	    		adField.setName(TextProvider.FieldName_DelayReasonDetail);
        	    		adField.setIsDisplay(true);
        	    		adField.setIsEditable(true);
        	    		adField.setIsReadonly(false);
        	    		adField.setLabel("delayReason");
        	    		adField.setLabel_zh("延误原因内容");
        	    		adField.setDisplayType(FieldType.TEXT);
        	    		adField.setDataType("String");
        	    		adField.setIsMandatory(true);
        	    		adField.setMinValue("6");
        	    		if(!canEdit) {
        	        		adField.setIsReadonly(true);
        	        	}
        	    		allADfields.add(adField);
        			}
    			}
    		}
    		//-----计划提醒
    		long planAlarmQty = moLine.getPlanNoticeQty()==null?0:moLine.getPlanNoticeQty();
    		if(planAlarmQty>0){
    			adField = new ADField();
	    		adField.setName(TextProvider.FieldName_Mps_Line_Delivery_Rrn);
	    		adField.setIsDisplay(true);
	    		adField.setIsEditable(true);
	    		adField.setIsReadonly(false);
	    		adField.setLabel("计划通知");
	    		adField.setLabel_zh("计划通知");
	    		adField.setDisplayType(FieldType.REFTABLE);
	    		adField.setDataType("String");
	    		adField.setIsSameline(true);
//	    		adField.setIsMandatory(true);
	    		adField.setReftableRrn(43093339L);
	    		allADfields.add(adField);
	    		
    		}
    	}
	}

	
	
	@Override
	public IField getField(ADField adField) {
		String displayText = adField.getDisplayType();
		String name = adField.getName();
		String displayLabel = I18nUtil.getI18nMessage(adField, "label");
		if (adField.getIsMandatory()) {
			displayLabel = displayLabel + "*";
		}
		IField field = null;
		ManufactureOrderLine moLine = (ManufactureOrderLine)object;
		long planNoticeQty = moLine.getPlanNoticeQty()==null?0:moLine.getPlanNoticeQty();
		if (planNoticeQty >0 && FieldType.REFTABLE.equalsIgnoreCase(displayText) && name.equals(TextProvider.FieldName_Mps_Line_Delivery_Rrn)) {
			try {
				
				ADManager entityManager = Framework.getService(ADManager.class);
				ADRefTable refTable = new ADRefTable();
				refTable.setObjectRrn(adField.getReftableRrn());
				refTable = (ADRefTable) entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableRrn() == null) {
					return null;
				}
				ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
				TableListManager tableManager = new TableListManager(adTable);
				TableViewer viewer = (TableViewer) tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
				
				ManufactureOrder mo = new ManufactureOrder();
				mo.setObjectRrn(moLine.getMasterMoRrn());
				mo = (ManufactureOrder) entityManager.getEntity(mo);
				String where = " mpsId = '" + mo.getMpsId()+ "' AND  materialRrn= " + moLine.getMaterialRrn() +" AND docStatus = 'APPROVED'";
				List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), where, refTable
						.getOrderByClause());
//				if (!adField.getIsMandatory()) {
//					String className = adTable.getModelClass();
//					list.add((ADBase) Class.forName(className).newInstance());
//				}
				viewer.setInput(list);
				field = createRefTableFieldList(name, displayLabel, viewer, refTable);
//				field.addValueChangeListener(listener)
				addField(name, field);

			} catch (Exception e) {
				e.printStackTrace();
				ExceptionHandlerManager.asyncHandleException(e);
			}
		} else {
			field = super.getField(adField);
		}
		return field;
	}

	public Lot getParentLot() {
		return parentLot;
	}
	
	public void setParentLot(Lot parentLot) {
		if(!Lot.LOTTYPE_MATERIAL.equals(lotType)) {
			this.parentLot = parentLot;
		}
	}
	
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public boolean isCreateComments() {
		return isCreateComments;
	}

	public void setCreateComments(boolean isCreateComments, String comments) {
		this.isCreateComments = isCreateComments;
		this.comments = comments;
	}
	
	public void setDisplayLotType(boolean isDisplayLotType) {
		this.isDisplayLotType = isDisplayLotType;
	}
	
	private IValueChangeListener getDelayDeptChangedListener(){
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				LinkedHashMap<String, IField>  fields = getFields();
				IField field =  fields.get("delayReason");
				ADManager adManager;
				try {
					adManager = Framework.getService(ADManager.class);
					String whereClause = "description = '"+field.getValue()+"部门' ";
					List<ADUserRefList> userList = adManager.getEntityList(Env.getOrgRrn(),ADUserRefList.class, Env.getMaxResult(), whereClause,"seqNo asc");
					RefTableField deptField = (RefTableField) fields.get("delayDept");
					deptField.setValue(null);
					deptField.setInput(null);
					deptField.refresh();
					LinkedHashMap<String, String> lmp = new LinkedHashMap<String, String>();
					for(ADUserRefList ul : userList){
						lmp.put(ul.getKey(), ul.getValue());
					}
					deptField.setInput(userList);
//					deptField.setValue(userList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	};
}

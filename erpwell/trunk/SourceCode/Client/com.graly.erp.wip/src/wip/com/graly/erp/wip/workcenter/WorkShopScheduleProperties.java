package com.graly.erp.wip.workcenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.WorkShopSchedule;
import com.graly.erp.wip.model.WorkShopScheduleHis;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.activeentity.model.ADUserRefList;
import com.graly.framework.base.entitymanager.forms.ChildEntityForm;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.CalendarField;
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;

public class WorkShopScheduleProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(WorkShopScheduleProperties.class);
	public static final String DELIVER_DATE = "dateDelivered";
	
	
	public WorkShopSchedule newWorkShopSchedule;
	
	public WorkShopScheduleProperties() {
		super();
	}

	public WorkShopScheduleProperties(EntityBlock masterParent, ADTable table,Object parentObject) {
		super(masterParent, table,parentObject);
	}
	@Override
	protected void saveAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			super.saveAdapter();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at TpsLineProperties : saveAdapter() ");
		}
	}
	
	// 验证交货日期必须大于当前日期
	protected boolean validate() {
		IField df = getIFieldById(DELIVER_DATE);
		if(df instanceof CalendarField) {
			CalendarField cf = (CalendarField)df;
			if(cf.getValue() instanceof Date) {
				Date deliver = (Date)cf.getValue();
				Date now = Env.getSysDate();
				if(deliver.compareTo(now) < 0) {
					UI.showError(Message.getString("ppm.deliver_date_before_now"));
				} else {
					return true;
				}
			} else {
				// 如果为空, 默认验证通过, 会在调用父类方法保存时验证不能为空
				return true;
			}
		}
		return false;
	}
	
	protected IField getIFieldById(String id) {
		IField field = null;
		for(Form form : this.getDetailForms()) {
			field = form.getFields().get(id);
			if(field != null) break;
		}
		return field;
	}
	
	@Override
	protected void createSectionContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			ChildEntityForm itemForm = new ChildEntityForm(getTabs(), SWT.NONE, null, tab, mmng, parentObject);
			if(getDetailForms()!=null && getDetailForms().size() >0 ){
				setDetailForms(new ArrayList<Form>());
			}
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		if (parentObject != null) {
			loadFromParent();
		}
		IField field = getIFieldById("delayReason");
		field.addValueChangeListener(getDelayDeptChangedListener());
	}
	
	public ADBase createAdObject() throws Exception {
		WorkShopSchedule wsSchedule = (WorkShopSchedule) getAdObject().getClass().newInstance();
		ManufactureOrderLine moLine = (ManufactureOrderLine) this.getParentObject();
		ADManager adManager = Framework.getService(ADManager.class);
		if(newWorkShopSchedule==null){
			wsSchedule = new WorkShopSchedule();
			wsSchedule.setOrgRrn(Env.getOrgRrn());
			wsSchedule.setUserCreated(Env.getUserName());
			wsSchedule.setMoId(moLine.getMasterMoId());
			wsSchedule.setMoRrn(moLine.getMasterMoRrn());
			wsSchedule.setMoLineRrn(moLine.getObjectRrn());
			wsSchedule.setMaterialRrn(moLine.getMaterialRrn());
			wsSchedule.setMaterialId(moLine.getMaterialId());
			wsSchedule.setMaterialName(moLine.getMaterialName());
			wsSchedule.setQtyProcuct(moLine.getQty());
			wsSchedule.setQtyReceive(moLine.getQtyReceive());
			wsSchedule.setCustomer(moLine.getCustomerName());
//			ManufactureOrder mo  = new ManufactureOrder();
//			mo.setObjectRrn(moLine.getMasterMoRrn());
//			mo = (ManufactureOrder) adManager.getEntity(mo);
//			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//			String date =dateFormat.format(moLine.getDateEnd()); 
//			moLine.setDateEnd(dateFormat.parse(date));
			wsSchedule.setDatePlan(moLine.getDateEnd());
			if(this.getAdObject()!=null&& this.getAdObject().getObjectRrn()!=null){
				WorkShopSchedule selectWsSchedule = (WorkShopSchedule) this.getAdObject();
				wsSchedule.setWorkcenterId(selectWsSchedule.getWorkcenterId());
				wsSchedule.setWorkcenterRrn(selectWsSchedule.getWorkcenterRrn());
				wsSchedule.setWorkCenter(selectWsSchedule.getWorkCenter());
				wsSchedule.setWorkCenter2(selectWsSchedule.getWorkCenter2());
				wsSchedule.setWorkCenter3(selectWsSchedule.getWorkCenter3());
				wsSchedule.setWorkCenter4(selectWsSchedule.getWorkCenter4());
				wsSchedule.setWorkCenter5(selectWsSchedule.getWorkCenter5());
				wsSchedule.setWorkCenter6(selectWsSchedule.getWorkCenter6());
				wsSchedule.setDocStatus(WorkShopSchedule.DOC_STATUS_DRAFTED);
			}else{
				WorkCenter wc = new WorkCenter();;
				wc.setObjectRrn(moLine.getWorkCenterRrn());
				wc = (WorkCenter) adManager.getEntity(wc);
				wsSchedule.setWorkcenterId(wc.getName());
				wsSchedule.setWorkcenterRrn(wc.getObjectRrn());
				wsSchedule.setDocStatus(WorkShopSchedule.DOC_STATUS_DRAFTED);
				
				List<ManufactureOrderBom> moBoms = adManager.getEntityList(Env.getOrgRrn(), ManufactureOrderBom.class,Integer.MAX_VALUE,
						"moRrn ="+moLine.getMasterMoRrn()+" and pathLevel =1",null);
				for(ManufactureOrderBom moBom : moBoms){
					String materialId = moBom.getMaterialId();
					String materialName = moBom.getMaterialName();
					if(materialId.startsWith("0750")){
						wsSchedule.setWorkCenter(materialName);
					}else if(materialId.startsWith("0752")){
						wsSchedule.setWorkCenter(materialId);
					}else if(materialId.startsWith("0754")){
						wsSchedule.setWorkCenter(materialId);
					}else if(materialId.startsWith("0759")){
						wsSchedule.setWorkCenter(materialId);
					}else if(materialId.startsWith("2201")){
						wsSchedule.setWorkCenter2(materialId);
					}else if(materialId.startsWith("0700")){
						wsSchedule.setWorkCenter3(materialId);
					}else if(materialId.startsWith("0701")){
						wsSchedule.setWorkCenter3(materialId);
					}else if(materialId.startsWith("5600")){
						wsSchedule.setWorkCenter4(materialId);
					}else if(materialId.startsWith("5601")){
						wsSchedule.setWorkCenter4(materialId);
					}else if(materialName.indexOf("射流器管")>=0){
//						wsSchedule.setWorkCenter5(materialId);
						wsSchedule.setWorkCenter5(materialName);
					}
				}
			}
			this.newWorkShopSchedule = wsSchedule;
		}else{
			wsSchedule = new WorkShopSchedule();
			wsSchedule.setOrgRrn(Env.getOrgRrn());
			wsSchedule.setUserCreated(Env.getUserName());
			wsSchedule.setMoId(moLine.getMasterMoId());
			wsSchedule.setMoRrn(moLine.getMasterMoRrn());
			wsSchedule.setMoLineRrn(moLine.getObjectRrn());
			wsSchedule.setMaterialRrn(moLine.getMaterialRrn());
			wsSchedule.setMaterialId(moLine.getMaterialId());
			wsSchedule.setMaterialName(moLine.getMaterialName());
			wsSchedule.setQtyProcuct(moLine.getQty());
			wsSchedule.setQtyReceive(moLine.getQtyReceive());
			wsSchedule.setCustomer(moLine.getCustomerName());
//			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//			String date =dateFormat.format(moLine.getDateEnd()); 
			wsSchedule.setDatePlan(moLine.getDateEnd());
			wsSchedule.setWorkcenterId(newWorkShopSchedule.getWorkcenterId());
			wsSchedule.setWorkcenterRrn(newWorkShopSchedule.getWorkcenterRrn());
			wsSchedule.setWorkCenter(newWorkShopSchedule.getWorkCenter());
			wsSchedule.setWorkCenter2(newWorkShopSchedule.getWorkCenter2());
			wsSchedule.setWorkCenter3(newWorkShopSchedule.getWorkCenter3());
			wsSchedule.setWorkCenter4(newWorkShopSchedule.getWorkCenter4());
			wsSchedule.setWorkCenter5(newWorkShopSchedule.getWorkCenter5());
			wsSchedule.setWorkCenter6(newWorkShopSchedule.getWorkCenter6());
			wsSchedule.setDocStatus(WorkShopSchedule.DOC_STATUS_DRAFTED);
		}
		return wsSchedule;
	}

	private IValueChangeListener getDelayDeptChangedListener(){
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				IField field = getIFieldById("delayReason");
				ADManager adManager;
				try {
					adManager = Framework.getService(ADManager.class);
					String whereClause = "description = '"+field.getValue()+"部门' ";
					List<ADUserRefList> userList = adManager.getEntityList(Env.getOrgRrn(),ADUserRefList.class, Env.getMaxResult(), whereClause,null);
					ComboField deptComboField = (ComboField) getIFieldById("delayDept");
					LinkedHashMap<String, String> lmp = new LinkedHashMap<String, String>();
					for(ADUserRefList ul : userList){
						lmp.put(ul.getKey(), ul.getValue());
					}
					deptComboField.setMItems(lmp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	};
	
	@Override
	public boolean save() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					ADManager entityManager = Framework.getService(ADManager.class);
					ADBase obj = entityManager.saveEntity(getTable().getObjectRrn(), getAdObject(), Env.getUserRrn());
					WorkShopScheduleHis scheduleHis = new WorkShopScheduleHis((WorkShopSchedule) obj);
					scheduleHis.setOrgRrn(Env.getOrgRrn());
					scheduleHis.setCreatedBy(Env.getUserRrn());
					scheduleHis.setUpdatedBy(Env.getUserRrn());
					scheduleHis.setTransType(WorkShopScheduleHis.TRANS_TYPE_SAVE);
//					entityManager.saveEntity(getTable().getObjectRrn(), getAdObject(), Env.getUserRrn());
					entityManager.saveEntity(scheduleHis, Env.getUserRrn());//记录历史
					WorkShopSchedule wsSchedule = (WorkShopSchedule) entityManager.getEntity(obj);
					setAdObject(wsSchedule);
					newWorkShopSchedule = wsSchedule;
					UI.showInfo(Message.getString("common.save_successed"));// 弹出提示框
					refresh();
					return true;
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return false;
	}
	@Override
	public boolean delete() {
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (getAdObject().getObjectRrn() != null) {
					ADManager entityManager = Framework.getService(ADManager.class);
					entityManager.deleteEntity(getAdObject());
					
					WorkShopScheduleHis scheduleHis = new WorkShopScheduleHis((WorkShopSchedule) getAdObject());
					scheduleHis.setOrgRrn(Env.getOrgRrn());
					scheduleHis.setCreatedBy(Env.getUserRrn());
					scheduleHis.setUpdatedBy(Env.getUserRrn());
					scheduleHis.setTransType(WorkShopScheduleHis.TRANS_TYPE_DELETE);
					entityManager.saveEntity(scheduleHis, Env.getUserRrn());//记录历史
					
					setAdObject(createAdObject());
					refresh();
					return true;
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
		}
		return false;
	}
}

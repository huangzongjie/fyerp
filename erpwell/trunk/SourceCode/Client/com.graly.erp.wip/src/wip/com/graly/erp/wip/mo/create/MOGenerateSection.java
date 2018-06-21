package com.graly.erp.wip.mo.create;

import java.util.Date;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.CalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MOGenerateSection extends EntitySection {
	private static final Logger logger = Logger.getLogger(MOGenerateSection.class);
	private final String StartDate = "datePlanStart";
	private final String EndDate = "datePlanEnd";
	private final String FieldName_MaterialRrn = "materialRrn";
	private final String FieldName_UomId = "uomId";
	private String errorMessage = Message.getString("wip.mo_end_before_start");
	MOGeneratePage parentPage;
	
	public MOGenerateSection() {
		super();
	}

	public MOGenerateSection(ADTable table, MOGeneratePage parentPage) {
		super(table);
		this.parentPage = parentPage;
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
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
			// 用MoForm取代EntityForm,将保存验证时提示的错误信息放在向导对话框上
			EntityForm itemForm = new MoForm(getTabs(), SWT.NONE, tab, mmng);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		
		// 新建一个MO对象
		try {
			setAdObject(this.createAdObject());
			refresh();
		} catch(Exception e) {
			logger.error("MOGenerateSection : createAdObject() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
		addUomIdListener();
	}
	
	protected void addUomIdListener() {
		IField materialField = this.getIField(FieldName_MaterialRrn);
		materialField.addValueChangeListener(new IValueChangeListener(){
			public void valueChanged(Object sender, Object newValue) {
				IField iField = getIField(FieldName_UomId);
				Material mt = (Material)newValue;
				if(mt != null && mt.getInventoryUom() != null) {
					iField.setValue(mt.getInventoryUom());
				} else {
					iField.setValue(null);
				}
				iField.refresh();
			}
		});
	}
	
	public ADBase createAdObject() throws Exception {
		ManufactureOrder mo = null;
		if(parentPage.getManufactureOrder() == null) {
			mo = new ManufactureOrder();
			mo.setOrgRrn(Env.getOrgRrn());
		} else {
			mo = parentPage.getManufactureOrder();
		}
		return mo;
	}
	
	protected void refreshAdapter() {
		try {
			this.setErrorMessage(null);
			for (Form detailForm : getDetailForms()) {
				detailForm.setObject(getAdObject());
				detailForm.loadFromObject();
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
	
	public boolean isCanSave() {
		try {
			if (getAdObject() != null) {
				boolean saveFlag = true;
				// 验证输入是否正确
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
						return saveFlag;
					}
				}
				// 验证需要生成工作令的物料的Bom是否合法
				if(!validateBom()) {
					saveFlag = false;
					return saveFlag;
				}
				// 验证开始日期应早于结束日期
				if(saveFlag && !dateValidate()) {
					saveFlag = false;
					return saveFlag;
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("Error MOGenerateSection : isCanSave() ", e);
		}
		return false;
	}
	
	private boolean validateBom() {
		try {
			for (Form form : getDetailForms()) {
				IField mf = form.getFields().get(FieldName_MaterialRrn);
				if(mf.getValue() != null) {
					Long materialRrn = Long.parseLong((String)mf.getValue());
					PDMManager pdmManager = Framework.getService(PDMManager.class);
					pdmManager.verifyBOM(materialRrn);
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("Error MOGenerateSection : validateBom() " + e.getStackTrace());
			ExceptionHandlerManager.asyncHandleException(e);
			return false;
		}
	}
	
	private boolean dateValidate() {
		try {
			CalendarField start = (CalendarField)this.getIField(StartDate);
			CalendarField end = (CalendarField)this.getIField(EndDate);
			Date startDate, endDate;
			if(start.getValue() != null && end.getValue() != null) {
				startDate = (Date)start.getValue();
				endDate = (Date)end.getValue();
			} else {
				return false;
			}

			if(startDate.compareTo(endDate) > 0) {
				this.setErrorMessage(errorMessage);
				return false;
			}
		} catch (Exception e) {
			logger.error("Error MOGenerateSection : dateValid() " + e.getStackTrace());
			return false;
		}
		return true;
	}
	
	private IField getIField(String id) {
		IField field = null;
		for(Form form : this.getDetailForms()) {
			field = form.getFields().get(id);
			if(field != null) break;
		}
		return field;
	}
	
	/* MoForm是继承EntityForm的内部类，
	 * 目的是重载valid()方法，将提示的错误信息放在向导对话框上
	 */
	protected class MoForm extends EntityForm {
	    public MoForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
	    	super(parent, style, null, tab, mmng);
	    }
	    
	    @Override
	    public boolean saveToObject() {
			setErrorMessage(null);
			return super.saveToObject();
	    }
	    
		@Override
		public boolean validate() {
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
							setErrorMessage(String.format(Message.getString("common.ismandatory"), I18nUtil.getI18nMessage(adField, "label")));
							break;
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
								setErrorMessage(String.format(Message.getString("common.isvalid"), I18nUtil.getI18nMessage(adField, "label")));
								break;
							} else if (!ValidatorFactory.isInRange(adField.getDataType(), value, adField.getMinValue(), adField.getMaxValue())){
								validFlag = false;
								if ((adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim()))
								 && (adField.getMaxValue() != null && !"".equalsIgnoreCase(adField.getMaxValue().trim()))){
									setErrorMessage(String.format(Message.getString("common.between"), I18nUtil.getI18nMessage(adField, "label"),
											adField.getMinValue(), adField.getMaxValue()));
									break;
								} else if (adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim())){
									setErrorMessage(String.format(Message.getString("common.largerthan"),
											I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue()));
									break;
								} else {
									setErrorMessage(String.format(Message.getString("common.lessthan"),
											I18nUtil.getI18nMessage(adField, "label"), adField.getMaxValue()));
									break;
								}
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
								setErrorMessage(String.format(Message.getString("common.namingrule_error"), I18nUtil.getI18nMessage(adField, "label")));
								break;
							}
						}
					}
				}
				
			}
			return validFlag;
		}
	}

	protected void setErrorMessage(String message) {
		if(parentPage != null) {
			parentPage.setErrorMessage(message);
		}
	}
}

package com.graly.promisone.base.entitymanager.forms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADField;
import com.graly.promisone.activeentity.model.ADRefList;
import com.graly.promisone.activeentity.model.ADRefTable;
import com.graly.promisone.activeentity.model.ADTab;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.entitymanager.views.TableListManager;
import com.graly.promisone.base.ui.forms.Form;
import com.graly.promisone.base.ui.forms.field.FieldType;
import com.graly.promisone.base.ui.forms.field.IField;
import com.graly.promisone.base.ui.forms.field.RefTableField;
import com.graly.promisone.base.ui.forms.field.SeparatorField;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.util.StringUtil;
import com.graly.promisone.base.ui.validator.ValidatorFactory;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.exceptionhandler.ExceptionHandlerManager;

public class EntityForm extends Form {
	
	private static final Logger logger = Logger.getLogger(EntityForm.class);

	protected ADTab tab;
	protected ADTable table;
	protected IMessageManager mmng;
	protected List<ADField> allADfields = new ArrayList<ADField>();
	protected LinkedHashMap<String, ADField> adFields = new LinkedHashMap<String, ADField>(10, (float)0.75, false);

	public EntityForm(Composite parent, int style, Object object, ADTab tab, IMessageManager mmng) {
		super(parent, style, object);
		this.tab = tab;
		this.mmng = mmng;
		createForm();
    }

    public EntityForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
    	this(parent, style, null, tab, mmng);
    }
    
    public EntityForm(Composite parent, int style, ADTable table, IMessageManager mmng) {
    	this(parent, style, null, table, mmng);
    }
    
    public EntityForm(Composite parent, int style, Object object, ADTable table, IMessageManager mmng) {
		super(parent, style, object);
		this.table = table;
		this.mmng = mmng;
		createForm();
    }
    
	@Override
    public void createForm(){
        try {
        	if (tab != null){
				if (tab.getGridY() > 1){
					super.setGridY(tab.getGridY().intValue());
				}
				allADfields = tab.getFields();
			}
        } catch (Exception e) {
        	logger.error("EntityForm : createForm", e);
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        super.createForm();
    }

	@Override
	public void addFields() {
		if (allADfields != null && allADfields.size() > 0){
			for (ADField adField : allADfields) {
	    		if (adField.getIsDisplay()) {
	    			IField field = getField(adField);
	    			if (field == null) {
	    				continue;
	    			}
	    			adFields.put(adField.getName(), adField);
    				if (field != null) {
    					field.setADField(adField);
    				}
	    		}
	    	}
			registeValueChangeListener();
		}
	}
	
	public IField getField(ADField adField){
		String displayText = adField.getDisplayType();
		String name = adField.getName();
		String displayLabel = I18nUtil.getI18nMessage(adField, "label");
		if(adField.getIsMandatry()) {
			displayLabel = displayLabel + "*";
		}
		int displayLength = adField.getDisplayLength() != null ? adField.getDisplayLength().intValue() : 32;
		IField field = null;
		if (FieldType.SEPARATOR.equalsIgnoreCase(displayText)){
			field = createSeparatorField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.TEXT.equalsIgnoreCase(displayText)){
			if (adField.getIsReadonly()){
				field = createReadOnlyText(name, displayLabel, "");
			} else if (adField.getIsUpper()){
				field = createUpperText(name, displayLabel, displayLength);
			} else {
				field = createText(name, displayLabel, displayLength);
			}
			addField(name, field);
		} else if (FieldType.PASSWORD.equalsIgnoreCase(displayText)){
			field = createPassword(name, displayLabel, displayLength);
			addField(name, field);
		} else if (FieldType.BOOLEAN.equalsIgnoreCase(displayText)){
			field = createBooleanField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.DATE.equalsIgnoreCase(displayText)){
			field = createDateField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.DATETIME.equalsIgnoreCase(displayText)){
			field = createDateTimeField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.CALENDAR.equalsIgnoreCase(displayText)) {
			field = createCalendarField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.COMBO.equalsIgnoreCase(displayText) || 
				FieldType.DROPDOWNLIST.equalsIgnoreCase(displayText) ||
				FieldType.LIST.equalsIgnoreCase(displayText) || 
				FieldType.RADIO.equalsIgnoreCase(displayText)){
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			try {
				if (adField.getReferenceName() != null && !"".equalsIgnoreCase(adField.getReferenceName().trim())){
					ADManager entityManager = Framework.getService(ADManager.class);
					List<ADRefList> refList = entityManager.getADRefList(Env.getOrgId(), adField.getReferenceName());
					for (ADRefList listItem : refList){
						map.put(listItem.getValue(), listItem.getName());
					}
					if (!adField.getIsMandatry()) {
						map.put("", "");
					}
				}
	        } catch (Exception e) {
	        	logger.error("EntityForm : Init listItem", e);
	        }
	        if (FieldType.COMBO.equalsIgnoreCase(displayText)){
		        field = createComboField(name, displayLabel, map);
		        addField(name, field);
	        } else if (FieldType.DROPDOWNLIST.equalsIgnoreCase(displayText)){
				field = createDropDownList(name, displayLabel, map);
				addField(name, field);
			} else if (FieldType.RADIO.equalsIgnoreCase(displayText)){
				field = createRadioGroup(name, displayLabel, map);
				addField(name, field);
			}
		} else if ((FieldType.REFTABLE.equalsIgnoreCase(displayText)) || 
			(FieldType.REFTABLECOMBO.equalsIgnoreCase(displayText)) || 
			(FieldType.TABLELIST.equalsIgnoreCase(displayText)) ||
			(FieldType.DUALLIST.equalsIgnoreCase(displayText)) ||
			(FieldType.TABLESELECT.equalsIgnoreCase(displayText)) || 
			(FieldType.USERREFLIST.equalsIgnoreCase(displayText)) || 
			FieldType.SEARCH.equalsIgnoreCase(displayText)){
			try{
				ADManager entityManager = Framework.getService(ADManager.class);
				ADRefTable refTable = new ADRefTable();
				refTable.setObjectId(adField.getRefTableId());
				refTable = (ADRefTable)entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableId() == null){
					return null;
				}
				ADTable adTable = entityManager.getADTable(refTable.getTableId());
				if (FieldType.REFTABLE.equalsIgnoreCase(displayText) ||
						FieldType.REFTABLECOMBO.equalsIgnoreCase(displayText)){
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
					if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
							|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0){
						List<ADBase> list = entityManager.getEntityList(Env.getOrgId(), adTable.getObjectId(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
    					if (!adField.getIsMandatry()) {
							String className = adTable.getModelClass();
							list.add((ADBase)Class.forName(className).newInstance());
						}
    					viewer.setInput(list);
					}
					if (adField.getIsReadonly()){
						field = createRefTableFieldComboReadOnly(name, displayLabel, viewer, refTable);
					} else if (FieldType.REFTABLE.equalsIgnoreCase(displayText)){
						field = createRefTableFieldList(name, displayLabel, viewer, refTable);
					} else {
						field = createRefTableFieldCombo(name, displayLabel, viewer, refTable);
					}
					addField(name, field);
				} else if (FieldType.TABLELIST.equalsIgnoreCase(displayText)){
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
					field = createTableListField(name, displayLabel, viewer);
					addField(name, field);
				} else if (FieldType.DUALLIST.equalsIgnoreCase(displayText)){
					List<ADBase> list = entityManager.getEntityList(Env.getOrgId(), adTable.getObjectId(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
					field = createDualListField(name, displayLabel, adTable, list);
					addField(name, field);
				} else if(FieldType.TABLESELECT.equalsIgnoreCase(displayText)) {
					String whereClause = "";
					if (refTable.getWhereClause() != null && !"".equals(refTable.getWhereClause().trim())) {
						whereClause = refTable.getWhereClause();
					}
					TableListManager tableManager = new TableListManager(adTable);
					tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION |SWT.BORDER 
							| SWT.H_SCROLL | SWT.V_SCROLL | SWT.HIDE_SELECTION);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
					field = createTableItemFilterField(name, displayLabel, viewer, adTable, whereClause);
					addField(name, field);
				} else if(FieldType.SEARCH.equalsIgnoreCase(displayText)) {
					String whereClause = "";
					if (refTable.getWhereClause() != null && !"".equals(refTable.getWhereClause().trim())) {
						whereClause = refTable.getWhereClause();
					}
					field = createSearchField(name, displayLabel, adTable, refTable, whereClause, SWT.BORDER);
					addField(name, field);
				} else if(FieldType.USERREFLIST.equalsIgnoreCase(displayText)) {
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));

					String whereClause = " referenceName = '" + adField.getUserRefName() + "'";
					List<ADBase> list = entityManager.getEntityList(Env.getOrgId(), adTable.getObjectId(), 
							Env.getMaxResult(), whereClause, refTable.getOrderByClause());
					if (!adField.getIsMandatry()) {
						String className = adTable.getModelClass();
						list.add((ADBase)Class.forName(className).newInstance());
					}
					viewer.setInput(list);
					if (adField.getIsReadonly()){
						field = createRefTableFieldComboReadOnly(name, displayLabel, viewer, refTable);
					} else {
						field = createRefTableFieldList(name, displayLabel, viewer, refTable);
					}
					
					addField(name, field);
				}
			} catch (Exception e){
				logger.error("EntityForm : Init tablelist", e);
			}
		}
		return field;
	}
	
	@Override
	public boolean validate() {
		boolean validFlag = true;
		for (IField f : fields.values()){
			ADField adField = adFields.get(f.getId());
			if(adField != null){
				if (adField.getIsMandatry()){
					Object value = f.getValue();
					boolean isMandatry = false;
					if (value == null){
						isMandatry = true;
					} else {
						if (value instanceof String){
							if ("".equalsIgnoreCase(value.toString().trim())){
								isMandatry = true;
							}
						}
					}
					if (isMandatry){
						validFlag = false;
						mmng.addMessage(adField.getName() + "common.ismandatry", 
								String.format(Message.getString("common.ismandatry"), I18nUtil.getI18nMessage(adField, "label")), null,
								IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
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
							mmng.addMessage(adField.getName() + "common.isvalid", 
									String.format(Message.getString("common.isvalid"), I18nUtil.getI18nMessage(adField, "label"), adField.getDataType()), null,
									IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
						} else if (!ValidatorFactory.isInRange(adField.getDataType(), value, adField.getMinValue(), adField.getMaxValue())){
							validFlag = false;
							if ((adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim()))
							 && (adField.getMaxValue() != null && !"".equalsIgnoreCase(adField.getMaxValue().trim()))){
								mmng.addMessage(adField.getName() + "common.between", 
										String.format(Message.getString("common.between"), I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue(), adField.getMaxValue()), null,
											IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
							} else if (adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim())){
								mmng.addMessage(adField.getName() + "common.largerthan", String.format(Message.getString("common.largerthan"), I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue()), null,
										IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);						
							} else {
								mmng.addMessage(adField.getName() + "common.lessthan", String.format(Message.getString("common.lessthan"), I18nUtil.getI18nMessage(adField, "label"), adField.getMaxValue()), null,
										IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);												
							}
						}
					}
				}
			}
		}
		return validFlag;
	}
	
	public void setEnabled(){
		if (object != null && object instanceof ADBase ){
			ADBase base = (ADBase)object;
			for (IField f : fields.values()){
				ADField adField = adFields.get(f.getId());
				if (adField != null && !adField.getIsEditable()){
					if (base.getObjectId() == null || base.getObjectId() == 0){ 
						f.setEnabled(true);
					} else {
						f.setEnabled(false);
					}
				}
			}
		}
	}
	
	@Override
    public boolean saveToObject() {
		if (object != null){
			if (!validate()){
				return false;
			}
			for (IField f : fields.values()){
				if (!(f instanceof SeparatorField)){
					PropertyUtil.setProperty(object, f.getId(), f.getValue());
				}
			}
			return true;
		}
		return false;
    }

	@Override
    public void loadFromObject() {
		if (object != null){
			for (IField f : fields.values()){
				if (!(f instanceof SeparatorField)){
					Object o = PropertyUtil.getPropertyForIField(object, f.getId());
					f.setValue(o);
				}
			}
			refresh();
			setEnabled();
		}
    }
	
	public void registeValueChangeListener(){
		for (IField f : fields.values()){
			if (f instanceof RefTableField){
				RefTableField refField = (RefTableField)f;
				ADRefTable  refTable = (refField).getRefTable();
				if (refTable.getWhereClause() != null && !"".equalsIgnoreCase(refTable.getWhereClause().trim())
						&& StringUtil.parseClauseParam(refTable.getWhereClause()).size() > 0){
					List<String> paramList = StringUtil.parseClauseParam(refTable.getWhereClause());
					for (String param : paramList){
						IField listenField = fields.get(param);
						listenField.addValueChangeListener(refField);
					}
				}
			}
		}
	}

	@Override
	public void refresh() {
		super.refresh();
	}
	
    @Override
    public void dispose() {
        super.dispose();
    }
}

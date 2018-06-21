package com.graly.framework.base.entitymanager.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.RefTextField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

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
	
	public EntityForm(Composite parent, int style, Object object, IMessageManager mmng) {
		super(parent, style, object);
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
        	if (table != null) {
        		allADfields = table.getFields();
        	} else if (tab != null){
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
		if(adField.getIsMandatory()) {
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
		} else if (FieldType.TEXTAREA.equalsIgnoreCase(displayText)){
			if (adField.getIsReadonly()){
				field = createReadOnlyTextArea(name, displayLabel, "");
			} else {
				field = createTextArea(name, displayLabel);
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
		} else if (FieldType.SHORTDATE.equalsIgnoreCase(displayText)){
			field = createShortDateField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.DATETIME.equalsIgnoreCase(displayText)){
			field = createDateTimeField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.CALENDAR.equalsIgnoreCase(displayText)) {
			field = createCalendarField(name, displayLabel);
			addField(name, field);
		}else if(FieldType.FROMTO_CALENDAR.equalsIgnoreCase(displayText)){
			field = createFromToCalendarField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.COMBO.equalsIgnoreCase(displayText) || 
				FieldType.DROPDOWNLIST.equalsIgnoreCase(displayText) ||
				FieldType.LIST.equalsIgnoreCase(displayText) || 
				FieldType.RADIO.equalsIgnoreCase(displayText)){
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			try {
				if (adField.getReferenceName() != null && !"".equalsIgnoreCase(adField.getReferenceName().trim())){
					ADManager entityManager = Framework.getService(ADManager.class);
					List<ADRefList> refList = entityManager.getADRefList(Env.getOrgRrn(), adField.getReferenceName());
					for (ADRefList listItem : refList){
						map.put(listItem.getValue(), listItem.getKey());
					}
					if (!adField.getIsMandatory()) {
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
				map.remove("");
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
				refTable.setObjectRrn(adField.getReftableRrn());
				if(adField.getReftableRrn()==null){
					return null;
				}
				refTable = (ADRefTable)entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableRrn() == null){
					return null;
				}
				refTable.setWhereClause(StringUtil.pareseWhereClause(refTable.getWhereClause()));
				ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
				if (FieldType.REFTABLE.equalsIgnoreCase(displayText) ||
						FieldType.REFTABLECOMBO.equalsIgnoreCase(displayText)){
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
					if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
							|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0){
						List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
    					if (!adField.getIsMandatory()) {
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
					List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
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

					String whereClause = " referenceName = '" + adField.getUserReflistName() + "'";
					List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
							Env.getMaxResult(), whereClause, refTable.getOrderByClause());
					if (!adField.getIsMandatory()) {
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
		} else if(FieldType.IMAGE.equals(displayText)) {
			field = createImageField(name, displayLabel, null);
			addField(name, field);
		} else if (FieldType.URL.equalsIgnoreCase(displayText)) {
			field = createUrlField(name, displayLabel, SWT.BORDER);
			addField(name, field);
		} else if (FieldType.REFTEXT.equalsIgnoreCase(displayText)) {
			field = createRefTextField(name, displayLabel, displayLength);
			addField(name, field);
		} else if (FieldType.HIDDEN.equalsIgnoreCase(displayText)) {
			field = createHiddenField(name);
			addField(name, field);
		}
		return field;
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
						} else if(value instanceof Map) {
							Map<String, Date> map = (Map<String, Date>)value;
							if(map.values().contains(null)) {
								isMandatory = true;
							}
						}
					}
					if (isMandatory){
						validFlag = false;
						mmng.addMessage(adField.getName() + "common.ismandatory", 
								String.format(Message.getString("common.ismandatory"), I18nUtil.getI18nMessage(adField, "label")), null,
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
				if (adField.getNamingRule() != null && !"".equalsIgnoreCase(adField.getNamingRule().trim())){
					Object value = f.getValue();
					if (value == null){
						continue;
					}
					if (value instanceof String){
						if (!Pattern.matches(adField.getNamingRule(), value.toString())) {
							validFlag = false;
							mmng.addMessage(adField.getName() + "common.namingrule_error", 
									String.format(Message.getString("common.namingrule_error"), I18nUtil.getI18nMessage(adField, "label")), null,
									IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
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
					if (base.getObjectRrn() == null || base.getObjectRrn() == 0){ 
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
//				if (!(f instanceof SeparatorField) && !f.isReadOnly()){
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
				if (!(f instanceof SeparatorField || f instanceof RefTextField)){
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
						if(listenField != null) {
							listenField.addValueChangeListener(refField);
						}
					}
				}
			}
			if (f instanceof RefTextField) {
				try {
					ADField adField = (ADField)f.getADField();
					String refName = adField.getReferenceRule();
					String firstName = refName.substring(0, refName.indexOf("."));
					if (fields.containsKey(firstName)) {
						IField listenField = fields.get(firstName);
						if (listenField instanceof SearchField) {
							listenField.addValueChangeListener((RefTextField)f);
						} else if (listenField instanceof RefTableField) {
							listenField.addValueChangeListener((RefTextField)f);
						}
					}
				} catch (Exception e) {
					logger.error(e);
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

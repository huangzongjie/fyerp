package com.graly.framework.base.ui.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.BooleanField;
import com.graly.framework.base.ui.forms.field.CalendarField;
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.DateField;
import com.graly.framework.base.ui.forms.field.DateTimeField;
import com.graly.framework.base.ui.forms.field.DualListField;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.HiddenField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.ImageField;
import com.graly.framework.base.ui.forms.field.RadioField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.RefTextField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.forms.field.TableItemFilterField;
import com.graly.framework.base.ui.forms.field.TableListField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.forms.field.UrlField;
import com.graly.framework.runtime.Framework;

public class ADFieldUtil {
	private static final Logger logger = Logger.getLogger(ADFieldUtil.class);
	
	public static final IField getField(ADField adField, LinkedHashMap<String, IField> fields){
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
			fields.put(name, field);
		} else if (FieldType.TEXT.equalsIgnoreCase(displayText)){
			if (adField.getIsReadonly()){
				field = createReadOnlyText(name, displayLabel, "");
			} else if (adField.getIsUpper()){
				field = createUpperText(name, displayLabel, displayLength);
			} else {
				field = createText(name, displayLabel, displayLength);
			}
			fields.put(name, field);
		} else if (FieldType.TEXTAREA.equalsIgnoreCase(displayText)){
			if (adField.getIsReadonly()){
				field = createReadOnlyTextArea(name, displayLabel, "");
			} else {
				field = createTextArea(name, displayLabel);
			}
			fields.put(name, field);
		} else if (FieldType.PASSWORD.equalsIgnoreCase(displayText)){
			field = createPassword(name, displayLabel, displayLength);
			fields.put(name, field);
		} else if (FieldType.BOOLEAN.equalsIgnoreCase(displayText)){
			field = createBooleanField(name, displayLabel);
			fields.put(name, field);
		} else if (FieldType.DATE.equalsIgnoreCase(displayText)){
			field = createDateField(name, displayLabel);
			fields.put(name, field);
		} else if (FieldType.SHORTDATE.equalsIgnoreCase(displayText)){
			field = createShortDateField(name, displayLabel);
			fields.put(name, field);
		} else if (FieldType.DATETIME.equalsIgnoreCase(displayText)){
			field = createDateTimeField(name, displayLabel);
			fields.put(name, field);
		} else if (FieldType.CALENDAR.equalsIgnoreCase(displayText)) {
			field = createCalendarField(name, displayLabel);
			fields.put(name, field);
		}else if(FieldType.FROMTO_CALENDAR.equalsIgnoreCase(displayText)){
			field = createFromToCalendarField(name, displayLabel);
			fields.put(name, field);
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
		        fields.put(name, field);
	        } else if (FieldType.DROPDOWNLIST.equalsIgnoreCase(displayText)){
				field = createDropDownList(name, displayLabel, map);
				fields.put(name, field);
			} else if (FieldType.RADIO.equalsIgnoreCase(displayText)){
				map.remove("");
				field = createRadioGroup(name, displayLabel, map);
				fields.put(name, field);
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
				refTable = (ADRefTable)entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableRrn() == null){
					return null;
				}
				refTable.setWhereClause(StringUtil.pareseWhereClause(refTable.getWhereClause()));
				ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
				if (FieldType.REFTABLE.equalsIgnoreCase(displayText) ||
						FieldType.REFTABLECOMBO.equalsIgnoreCase(displayText)){
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(UI.getActiveShell(), new FormToolkit(UI.getActiveShell().getDisplay()));
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
					fields.put(name, field);
				} else if (FieldType.TABLELIST.equalsIgnoreCase(displayText)){
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(UI.getActiveShell(), new FormToolkit(UI.getActiveShell().getDisplay()));
					field = createTableListField(name, displayLabel, viewer);
					fields.put(name, field);
				} else if (FieldType.DUALLIST.equalsIgnoreCase(displayText)){
					List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
					field = createDualListField(name, displayLabel, adTable, list);
					fields.put(name, field);
				} else if(FieldType.TABLESELECT.equalsIgnoreCase(displayText)) {
					String whereClause = "";
					if (refTable.getWhereClause() != null && !"".equals(refTable.getWhereClause().trim())) {
						whereClause = refTable.getWhereClause();
					}
					TableListManager tableManager = new TableListManager(adTable);
					tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION |SWT.BORDER 
							| SWT.H_SCROLL | SWT.V_SCROLL | SWT.HIDE_SELECTION);
					TableViewer viewer = (TableViewer)tableManager.createViewer(UI.getActiveShell(), new FormToolkit(UI.getActiveShell().getDisplay()));
					field = createTableItemFilterField(name, displayLabel, viewer, adTable, whereClause);
					fields.put(name, field);
				} else if(FieldType.SEARCH.equalsIgnoreCase(displayText)) {
					String whereClause = "";
					if (refTable.getWhereClause() != null && !"".equals(refTable.getWhereClause().trim())) {
						whereClause = refTable.getWhereClause();
					}
					field = createSearchField(name, displayLabel, adTable, refTable, whereClause, SWT.BORDER);
					fields.put(name, field);
				} else if(FieldType.USERREFLIST.equalsIgnoreCase(displayText)) {
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(UI.getActiveShell(), new FormToolkit(UI.getActiveShell().getDisplay()));

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
					
					fields.put(name, field);
				} 
			} catch (Exception e){
				logger.error("EntityForm : Init tablelist", e);
			}
		} else if(FieldType.IMAGE.equals(displayText)) {
			field = createImageField(name, displayLabel, null);
			fields.put(name, field);
		} else if (FieldType.URL.equalsIgnoreCase(displayText)) {
			field = createUrlField(name, displayLabel, SWT.BORDER);
			fields.put(name, field);
		} else if (FieldType.REFTEXT.equalsIgnoreCase(displayText)) {
			field = createRefTextField(name, displayLabel, displayLength);
			fields.put(name, field);
		} else if (FieldType.HIDDEN.equalsIgnoreCase(displayText)) {
			field = createHiddenField(name);
			fields.put(name, field);
		}
		return field;
	}
	
	public static TextField createText(String id, String label, int limit){
    	return createText(id, label, "", limit);
    }
    
    public static TextField createText(String id, String label, String value, int limit) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value != null ? value : ""); 
        fe.setLength(limit);
        return fe;
    }
    
    public static TextField createUpperText(String id, String label, int limit){
    	return createUpperText(id, label, "", limit);
    }
    
    public static TextField createUpperText(String id, String label, String value, int limit) {
    	TextField fe = new TextField(id, true);
        fe.setLabel(label);
        fe.setValue(value != null ? value : ""); 
        fe.setLength(limit);
        return fe;
    }
    
    public static TextField createPassword(String id, String label, int limit) {
        return createPassword(id, label, "", limit); 
    }

    public static TextField createPassword(String id, String label, String value, int limit) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value);
        fe.appendStyle(SWT.PASSWORD);
        fe.setLength(limit);
        return fe;
    }

    public static TextField createReadOnlyText(String id, String label, String value) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value);
        fe.setReadOnly(true);
        return fe;
    }

    public static TextField createTextArea(String id, String label) {
        return createTextArea(id, label, null);
    }

    public static TextField createTextArea(String id, String label, String value) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value);
        fe.setMultiLine(true);
        return fe;
    }
    
    public static TextField createReadOnlyTextArea(String id, String label, String value) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value);
        fe.setMultiLine(true);
        fe.setReadOnly(true);
        return fe;
    }
    
    public static TableItemFilterField createTableItemFilterField(String id, String label,
    	TableViewer viewer, ADTable adTable, String whereClause) {
    	TableItemFilterField bt = new TableItemFilterField(id, viewer, adTable, SWT.CHECK, whereClause);
        bt.setLabel(label);
        bt.setValue(null);
        return bt;
    }
    
    public TableItemFilterField createTableItemFilterField(String id, String label,
        	TableViewer viewer, ADTable adTable) {
        	TableItemFilterField bt = new TableItemFilterField(id, viewer, adTable, SWT.CHECK, null);
            bt.setLabel(label);
            bt.setValue(null);
            return bt;
    }
    
    public TextField createReadOnlyMultiLineText(String id, String label, String value) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value);
        fe.setReadOnly(true);
        fe.setMultiLine(true);
        return fe;
    }
    
    public static SeparatorField createSeparatorField(String id, String label) {
    	SeparatorField fe = new SeparatorField(id);
        fe.setLabel(label);
        return fe;
    }
    
    public RefTextField createRefTextField(String id, String label) {
    	return createRefTextField(id, label, 32);
    }
    
    public static RefTextField createRefTextField(String id, String label, int limit) {
    	RefTextField fe = new RefTextField(id);
        fe.setLabel(label);
        fe.setLength(limit);
        return fe;
    }
    
    public static BooleanField createBooleanField(String id, String label) {
        return createBooleanField(id, label, false);
    }

    public static BooleanField createBooleanField(String id, String label, boolean value) {
    	BooleanField fe = new BooleanField(id);
        fe.setLabel(label);
        fe.setValue(new Boolean(value));
        return fe;
    }
    
    public static ComboField createComboField(String id, String label, LinkedHashMap<String, String> items) {
    	ComboField fe = new ComboField(id, items);
        fe.setLabel(label);
        return fe;
    }

    public ComboField createComboField(String id, String label, LinkedHashMap<String, String> items, String value) {
    	ComboField fe = new ComboField(id, items);
        fe.setLabel(label);
        fe.setValue(value);
        return fe;
    }

    public static ComboField createDropDownList(String id, String label, LinkedHashMap<String, String> items) {
    	ComboField fe = new ComboField(id, items, SWT.BORDER | SWT.READ_ONLY);
        fe.setLabel(label);
        return fe;
    }

    public ComboField createDropDownList(String id, String label, LinkedHashMap<String, String> items, String value) {
    	ComboField fe = new ComboField(id, items, SWT.BORDER | SWT.READ_ONLY);
        fe.setLabel(label);
        fe.setValue(value);
        return fe;
    }
    
    public static RadioField createRadioGroup(String id, String label, LinkedHashMap<String, String> items) {
    	RadioField fe = new RadioField(id, items);
        fe.setLabel(label);
        return fe;
    }

    public RadioField createRadioGroup(String id, String label, LinkedHashMap<String, String> items, int selection) {
    	RadioField fe = new RadioField(id, items);
        fe.setLabel(label);
        fe.setValue(new Integer(selection));
        return fe;
    }
    
    public static DateField createDateField(String id, String label) {
    	DateField fe = new DateField(id);
        fe.setLabel(label);
        fe.setValue(null);
        return fe;
    }
    
    public static DateField createShortDateField(String id, String label) {
    	DateField fe = new DateField(id, SWT.BORDER, SWT.SHORT);
    	fe.setLabel(label);
    	fe.setValue(null);
    	return fe;
    }
    
    public static DateTimeField createDateTimeField(String id, String label) {
    	DateTimeField fe = new DateTimeField(id);
        fe.setLabel(label);
        fe.setValue(null);
        return fe;
    }
    
    public static CalendarField createCalendarField(String id, String label) {
    	CalendarField fe = new CalendarField(id);
        fe.setLabel(label);
        fe.setValue(null);
        return fe;
    }
    
    public static FromToCalendarField createFromToCalendarField(String id, String label) {
    	FromToCalendarField fe = new FromToCalendarField(id);
    	fe.setLabel(label);
    	fe.setValue(null);
    	return fe;
    }
    
    public FromToCalendarField createFromToCalendarField(String id, String label, Date fromDate, Date toDate) {
    	FromToCalendarField fe = new FromToCalendarField(id, fromDate, toDate);
    	fe.setLabel(label);
    	fe.setValue(null);
    	return fe;
    }
    
    public ImageField createImageField(String id, String label) {
    	ImageField imf = new ImageField(id);
    	imf.setLabel(label);
    	imf.setValue("");
    	return imf;
    }
    
    public static ImageField createImageField(String id, String label, String imageUrl) {
    	ImageField imf = new ImageField(id, imageUrl);
    	imf.setLabel(label);
    	return imf;
    }
    
    public static RefTableField createRefTableFieldList(String id, String label, TableViewer viewer, ADRefTable refTable) {
    	int mStyle = SWT.READ_ONLY;
    	RefTableField fe = new RefTableField(id, viewer, refTable, mStyle);
        fe.setLabel(label);
        return fe;
    }
    
    public static RefTableField createRefTableFieldCombo(String id, String label, TableViewer viewer, ADRefTable refTable) {
    	RefTableField fe = new RefTableField(id, viewer, refTable);
        fe.setLabel(label);
        return fe;
    }
    
    public static RefTableField createRefTableFieldComboReadOnly(String id, String label, TableViewer viewer, ADRefTable refTable) {
    	RefTableField fe = new RefTableField(id, viewer, refTable);
        fe.setLabel(label);
        fe.setReadOnly(true);
        return fe;
    }
    
    public static TableListField createTableListField(String id, String label, TableViewer viewer) {
    	TableListField fe = new TableListField(id, viewer);
        fe.setLabel(label);
        return fe;
    }
    
    public static DualListField createDualListField(String id, String label, ADTable adTable, List inputList) {
    	List<Object> list = new ArrayList<Object>();
    	list.addAll(inputList);
    	DualListField fe = new DualListField(id, adTable, list);
        fe.setLabel(label);
        return fe;
    }
    
    public static SearchField createSearchField(String id, String label, ADTable adTable,
    		ADRefTable refTable, String whereClause, int style) {
    	SearchField fe = new SearchField(id, adTable, refTable, whereClause, style);
    	fe.setLabel(label);
    	return fe;
    }
    
    public static UrlField createUrlField(String id, String label, int style) {
    	UrlField fe = new UrlField(id, style);
    	fe.setLabel(label);
    	return fe;
    }
    
    public static HiddenField createHiddenField(String id) {
    	HiddenField fe = new HiddenField(id);
        return fe;
    }
}

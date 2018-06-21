package com.graly.erp.pdm.material;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.ImageField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ImageForm extends Form {
	private static final Logger logger = Logger.getLogger(ImageForm.class);
	protected ADTab tab;
	protected ADTable table;
	protected IMessageManager mmng;
	protected List<ADField> allADfields = new ArrayList<ADField>();
	protected LinkedHashMap<String, ADField> adFields = new LinkedHashMap<String, ADField>(10, (float)0.75, false);

	
	public ImageForm(Composite parent, int style, Object object) {
    	super(parent, style, object);
    }
	
	public ImageForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
    	this(parent, style, null);
    	this.tab = tab;
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
        	logger.error("ImageForm : createForm", e);
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        super.createForm();
    }

	@Override
	public void addFields() {
		if (allADfields != null && allADfields.size() > 0){
			for (ADField adField : allADfields) {
	    		if (adField.getIsDisplay()) {
	    			String displayText = adField.getDisplayType();
	    			String name = adField.getName();
	    			String displayLabel = I18nUtil.getI18nMessage(adField, "label");
	    			if(adField.getIsMandatory()) {
	    				displayLabel = displayLabel + "*";
	    			}
	    			IField field = null;
	    			if(FieldType.IMAGE.equals(displayText)) {
	    				field = createImageField(name, displayLabel, null);
	    				addField(name, field);
	    			}
	    			
	    			adFields.put(adField.getName(), adField);
    				if (field != null) {
    					field.setADField(adField);
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
				if (f instanceof ImageField){
					try {
						PDMManager pdmManager = Framework.getService(PDMManager.class);
						pdmManager.saveMaterialPhoto((Material)object, (byte[])f.getValue());
						return true;
					} catch(Exception e) {
						logger.error("Error At ImageForm : saveToObject() " + e.getMessage());
						ExceptionHandlerManager.asyncHandleException(e);
					}
				}
			}
		}
		return false;
    }

	@Override
    public void loadFromObject() {
		if (object != null){
			for (IField f : fields.values()){
				if (f instanceof ImageField) {
					try {
						PDMManager pdmManager = Framework.getService(PDMManager.class);
						byte[] bytes = pdmManager.getMaterialPhoto((Material)object);
						f.setValue(bytes);
					} catch(Exception e) {
						logger.error("Error At ImageForm : loadFromObject() " + e.getMessage());
						ExceptionHandlerManager.asyncHandleException(e);
					}
				}
			}
			refresh();
			setEnabled();
		}
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
	public boolean validate() {
		return true;
	}
	
    @Override
    public void dispose() {
        super.dispose();
    }

}

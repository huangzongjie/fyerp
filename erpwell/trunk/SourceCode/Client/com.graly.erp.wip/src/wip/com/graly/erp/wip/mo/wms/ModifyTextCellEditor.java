package com.graly.erp.wip.mo.wms;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;

public class ModifyTextCellEditor extends TextCellEditor {
	private Logger logger = Logger.getLogger(EntityPropertyCellModify.class);
	
	private TableViewer tableViewer;
	private String propertyName;
	private String validateType;
	private int colIndex;
	
	public ModifyTextCellEditor(TableViewer tableViewer,
			String propertyName, String validateType, int colIndex) {
        super(tableViewer.getTable());
        this.tableViewer = tableViewer;
        this.propertyName = propertyName;
        this.validateType = validateType;
        this.colIndex = colIndex;
    }

	@Override
	protected Control createControl(Composite parent) {
		super.createControl(parent);
		text.setTextLimit(32);
		text.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
            	TableItem[] items = tableViewer.getTable().getSelection();
            	if (items != null && items.length > 0){
            		TableItem item = items[0];
            		String value = text.getText();
            		
            		if (value != null && value.equals("")) {
            			// do nothing
					} else {
						if(validate(validateType, value)) { 
							setValue(item.getData(), value);
						} else {
							tableViewer.editElement(item.getData(), colIndex);
						}
					}
            	}
            }
        });
		return text;
	}

	public boolean validate(String type, String value) {
		if (value != null) {
//			if (value.startsWith(" ")) {
//    			UI.showError(Message.getString("inv.char_cannot_null"));
//    			return false;
//			}
			if (!ValidatorFactory.isValid(type, value)) {
				UI.showError(Message.getString("common.input_error"),
						Message.getString("common.inputerror_title"));
				return false;
			}
		}
		return true;
	}
	
	public void setValue(Object obj, String value) {
		if(obj != null && propertyName != null) {
			try {
				PropertyUtil.setProperty(obj, propertyName, value);
			} catch(Exception e) {
				logger.error(e);
			}
		}
	}
}

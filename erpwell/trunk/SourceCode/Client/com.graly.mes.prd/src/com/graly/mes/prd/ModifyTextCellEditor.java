package com.graly.mes.prd;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.graly.mes.prd.model.Parameter;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;

public class ModifyTextCellEditor extends TextCellEditor {

	private TableViewer tableViewer;
	
	public ModifyTextCellEditor(TableViewer tableViewer) {
        super(tableViewer.getTable());
        this.tableViewer = tableViewer;
    }
	
	@Override
	protected Control createControl(Composite parent) {
		super.createControl(parent);
		text.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
            	TableItem[] items = tableViewer.getTable().getSelection();
            	if (items != null && items.length > 0){
            		TableItem item = items[0];
            		Object adParam = item.getData();
            		String value = text.getText();
            		if (value != null && value.equals("")) {
            			if(adParam instanceof Parameter) {
        					((Parameter)adParam).setDefValue(null);
        				} 
					} else {						
						if(discernParameter(adParam, value)) { 
							if(adParam instanceof Parameter) {
								((Parameter)adParam).setDefValue(value.trim());
							}
						} else {
							tableViewer.editElement(item.getData(), 2);
						}
					}
            	}
            }
        });
		return text;
	}
	
	public boolean discernParameter(Object adParam, Object object) {
		String value = "";
		String type = "";
		if(adParam instanceof Parameter) {
			Parameter adP = (Parameter)adParam;
			type = (String)adP.getType();
			value = (String)object;
		}
		if(validator(type, value)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean validator(String type, String value) {
		if (value != null){
			if (!ValidatorFactory.isValid(type, value)) {
				UI.showError(Message.getString("common.input_error"), Message.getString("common.inputerror_title"));
				return false;
			}
		}
		return true;
	}
}
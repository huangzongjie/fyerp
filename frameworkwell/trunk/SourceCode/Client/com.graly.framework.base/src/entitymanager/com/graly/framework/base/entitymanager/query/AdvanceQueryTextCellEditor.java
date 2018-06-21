package com.graly.framework.base.entitymanager.query;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;

public class AdvanceQueryTextCellEditor extends TextCellEditor {
	private TableViewer tableViewer;
	private String cloumnName;
	
	public AdvanceQueryTextCellEditor(TableViewer tableViewer) {
		super(tableViewer.getTable());
		this.tableViewer = tableViewer;
	}
	
	public AdvanceQueryTextCellEditor(TableViewer tableViewer, String cloumnName) {
        this(tableViewer);
        this.cloumnName = cloumnName;
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
            		Object obj = item.getData();
            		AdvanceQueryEntity aqe = (AdvanceQueryEntity)obj;
            		String value = text.getText();
            		if (value != null && value.equals("")) {
            			setValue(aqe, value);
					} else {
						if(discernParameter(aqe, value)) {
							setValue(aqe, value);
						} else {
							tableViewer.editElement(item.getData(), 2);
						}
					}
            	}
            }
        });
		return text;
	}

	// 确定要验证的具体的数据类型
	public boolean discernParameter(AdvanceQueryEntity aqe, Object object) {
		if("integer".equalsIgnoreCase(aqe.getDataType())
				|| "double".equalsIgnoreCase(aqe.getDataType())) {
			return validator(aqe.getDataType(), (String)object);
		}
		return validator("String", (String)object);
	}
	
	public boolean validator(String type, String value) {
		if (value != null){
			if (value.startsWith(" ")) {
    			UI.showError(Message.getString("inv.char_cannot_null"));
    			return false;
			}
			if (!ValidatorFactory.isValid(type, value)) {
				UI.showError(Message.getString("common.input_error"),
						Message.getString("common.inputerror_title"));
				return false;
			}
		}
		return true;
	}
	
	public void setValue(AdvanceQueryEntity aqe, String value) {
		aqe.setValue(value);
	}
}

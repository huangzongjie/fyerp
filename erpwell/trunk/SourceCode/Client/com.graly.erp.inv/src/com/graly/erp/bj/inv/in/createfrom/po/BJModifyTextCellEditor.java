package com.graly.erp.bj.inv.in.createfrom.po;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;

public class BJModifyTextCellEditor extends TextCellEditor {
	private Logger logger = Logger.getLogger(BJModifyTextCellEditor.class);
	
	private TableViewer tableViewer;
	private String propertyName;
	private String validateType;
	private int colIndex;
	
	public BJModifyTextCellEditor(TableViewer tableViewer,
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
            		PurchaseOrderLine poLine = (PurchaseOrderLine) item.getData();
            		
            		String value = text.getText();
            		
            		if (value != null && value.equals("")) {    			 
					} 
            		else {
            			if(value!=null){
            				if(poLine.getQty().compareTo(new BigDecimal(value)) <0){
            					UI.showError("收货数不能大于订货数");
            					return;
                				}
            			}
        			
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

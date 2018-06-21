/**
 * 
 */
package com.graly.erp.wip.partlydisassemble;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.erp.wip.workcenter.receive.TextProvider;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

/**
 * @author Denny
 *
 */
public class QtySetupForm extends EntityForm {
	private static final Logger logger = Logger.getLogger(QtySetupForm.class);
	private Lot lot;
	private Text qtyField;
	
	/**
	 * @param parent
	 * @param style
	 * @param object
	 * @param mmng
	 */
	public QtySetupForm(Composite parent, int style, Lot lot,
			IMessageManager mmng) {
		super(parent, style, lot, mmng);
		this.lot = lot;
	}
	
	@Override
	public void createForm(){}
	
	@Override
	protected void createContent() {
		super.createContent();
		Composite body = form.getBody();
    	Label lbl = toolkit.createLabel(body, Message.getString("wip.lot_qty")+ "*");//数量
    	GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
    	gd1.horizontalAlignment = GridData.END;
    	lbl.setLayoutData(gd1);
    	qtyField = toolkit.createText(body, "");
    	GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
    	gd2.horizontalSpan = 1;
    	qtyField.setLayoutData(gd2);
    	if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())){//如果是Serial类型的批不让手动输入数量
    		qtyField.setEditable(false);
    		qtyField.setText("1");
    	}
    	qtyField.setFocus();
	}
	
	public void createFormContent(){
        try {
        	super.setGridY(this.getGridY());
        	createADFields();
        } catch (Exception e) {
        	logger.error("QtySetupForm : createForm", e);
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        super.createForm();
    }

	private void createADFields() {
		ADField adField = new ADField();
    	adField.setName(TextProvider.FieldName_LotId);
    	adField.setIsReadonly(true);
    	adField.setLabel("Lot ID");
    	adField.setLabel_zh("批号");
    	adField.setDisplayType(FieldType.TEXT);
    	adField.setIsMandatory(true);
    	allADfields.add(adField);
    	
    	adField = new ADField();
		adField.setName(TextProvider.FieldName_LotType);
		adField.setIsDisplay(true);
		adField.setIsEditable(true);
		adField.setIsReadonly(true);
		adField.setLabel("Lot Type");
		adField.setLabel_zh("批次类型");
		adField.setDisplayType(FieldType.TEXT);
		allADfields.add(adField);
    	
		adField = new ADField();
    	adField.setName(TextProvider.FieldName_MaterialId);
    	adField.setIsDisplay(true);
    	adField.setLabel("Material ID");
    	adField.setLabel_zh("物料编号");
    	adField.setDisplayType(FieldType.TEXT);
    	adField.setIsReadonly(true);
    	allADfields.add(adField);
    	
    	adField = new ADField();
    	adField.setName(TextProvider.FieldName_MaterialName);
    	adField.setIsDisplay(true);
    	adField.setLabel("Material Name");
    	adField.setLabel_zh("物料名称");
    	adField.setDisplayType(FieldType.TEXT);
    	adField.setIsReadonly(true);
    	allADfields.add(adField);
	}
	
	@Override
	public boolean validate() {
		if(qtyField != null){
			String val = qtyField.getText();
			return (val != null && val.trim().length() > 0);
		}
		return false;
	}
	
	public BigDecimal getQtyDisassemble(){
		if(qtyField != null && qtyField.getText() != null && qtyField.getText().trim().length() > 0){
			return new BigDecimal(qtyField.getText().trim());
		}
		return BigDecimal.ZERO;
	}
}

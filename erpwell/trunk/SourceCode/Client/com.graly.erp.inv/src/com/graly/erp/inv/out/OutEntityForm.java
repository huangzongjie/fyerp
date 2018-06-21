package com.graly.erp.inv.out;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.erp.inv.in.WarehouseEntityForm;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
/*1.对于BT区域，显示送货地址和联系人2个栏位
 *2.对于其他区域，不显示送货地址和联系人2个栏位
 * */
public class OutEntityForm extends WarehouseEntityForm {

	public OutEntityForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
		super(parent, style, tab, mmng);
	}

	public OutEntityForm(Composite parent, int style, Object object, ADTab tab, IMessageManager mmng) {
		super(parent, style, object, tab, mmng);
	}

	public IField getField(ADField adField) {
		//如果字段为deliverAddress,linkMan，并且区域不为BT，返回空
		if(12644730L !=Env.getOrgRrn()&& (adField.getName().equals("deliverAddress")||adField.getName().equals("linkMan"))){
			return null;
		}
		return super.getField(adField); 

	}
	
}

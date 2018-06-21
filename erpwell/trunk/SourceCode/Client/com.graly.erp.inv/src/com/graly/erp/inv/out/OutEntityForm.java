package com.graly.erp.inv.out;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.erp.inv.in.WarehouseEntityForm;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
/*1.����BT������ʾ�ͻ���ַ����ϵ��2����λ
 *2.�����������򣬲���ʾ�ͻ���ַ����ϵ��2����λ
 * */
public class OutEntityForm extends WarehouseEntityForm {

	public OutEntityForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
		super(parent, style, tab, mmng);
	}

	public OutEntityForm(Composite parent, int style, Object object, ADTab tab, IMessageManager mmng) {
		super(parent, style, object, tab, mmng);
	}

	public IField getField(ADField adField) {
		//����ֶ�ΪdeliverAddress,linkMan����������ΪBT�����ؿ�
		if(12644730L !=Env.getOrgRrn()&& (adField.getName().equals("deliverAddress")||adField.getName().equals("linkMan"))){
			return null;
		}
		return super.getField(adField); 

	}
	
}

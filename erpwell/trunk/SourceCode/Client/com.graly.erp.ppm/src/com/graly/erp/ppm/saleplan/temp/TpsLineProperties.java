package com.graly.erp.ppm.saleplan.temp;

import java.util.Date;

import org.apache.log4j.Logger;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.model.TpsLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.CalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class TpsLineProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(TpsLineProperties.class);
	public static final String DELIVER_DATE = "dateDelivered";
	public static final String MATERIAL_ID = "materialRrn";
	
	public TpsLineProperties() {
		super();
	}

	public TpsLineProperties(EntityBlock masterParent, ADTable table) {
		super(masterParent, table, null);
	}

	protected void saveAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				if(!validate()) {
					return;
				}
				saveMaterialIdToTpsLine();
			}
			super.saveAdapter();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at TpsLineProperties : saveAdapter() ");
		}
	}
	
	// 验证交货日期必须大于当前日期
	protected boolean validate() {
		IField df = getIFieldById(DELIVER_DATE);
		if(df instanceof CalendarField) {
			CalendarField cf = (CalendarField)df;
			if(cf.getValue() instanceof Date) {
				Date deliver = (Date)cf.getValue();
				Date now = Env.getSysDate();
				if(deliver.compareTo(now) < 0) {
					UI.showError(Message.getString("ppm.deliver_date_before_now"));
				} else {
					return true;
				}
			} else {
				// 如果为空, 默认验证通过, 会在调用父类方法保存时验证不能为空
				return true;
			}
		}
		return false;
	}
	
	private void saveMaterialIdToTpsLine() {
		TpsLine tpsLine = (TpsLine)getAdObject();
		IField field = getIFieldById(MATERIAL_ID);
		if(field instanceof SearchField) {
			SearchField sf = (SearchField)field;
			if(sf.getData() instanceof Material) {
				tpsLine.setMaterialId(((Material)sf.getData()).getMaterialId());
			}
		}
	}
	
	protected IField getIFieldById(String id) {
		IField field = null;
		for(Form form : this.getDetailForms()) {
			field = form.getFields().get(id);
			if(field != null) break;
		}
		return field;
	}

}

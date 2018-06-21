package com.graly.erp.wip.mo;

import java.util.Date;

import com.graly.erp.wip.mo.create.MOGenerateSection;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;

public class MoCommentsSection extends MOGenerateSection {
	static final String FIELD_COMMENTS = "comments";
	static final String FIELD_DATE_ACTUAL = "dateActual";
	static final String FIELD_REASON_ACTUAL = "reasonActual";
	ManufactureOrder mo;

	public MoCommentsSection(ADTable table, ManufactureOrder mo) {
		super(table, null);
		this.mo = mo;
		initAllADFieldEnable(false);
	}
	
	protected void initAllADFieldEnable(boolean enabled) {
		for(ADField adField : table.getFields()) {
			if(!FIELD_COMMENTS.equals(adField.getName())) {
				adField.setIsEditable(false);
			}
			if(FIELD_DATE_ACTUAL.equals(adField.getName())) {
				adField.setIsEditable(true);
			}
			if(FIELD_REASON_ACTUAL.equals(adField.getName())) {
				adField.setIsEditable(true);
			}
		}
	}
	
	public ADBase createAdObject() throws Exception {
		return mo;
	}
	
	public ManufactureOrder getUpdateCommentsMo() {
		for (Form detailForm : getDetailForms()) {
			IField field = detailForm.getFields().get(FIELD_COMMENTS);
			IField fieldDateActual = detailForm.getFields().get(FIELD_DATE_ACTUAL);
			IField fieldReasonActual = detailForm.getFields().get(FIELD_REASON_ACTUAL);
			if(field != null) {
				mo.setComments(field.getValue() == null ? "" : field.getValue().toString());
//				break;
			}
			if(fieldReasonActual != null) {
				mo.setReasonActual(fieldReasonActual.getValue() == null ? "" : fieldReasonActual.getValue().toString());
			}
			if(fieldDateActual!=null){
				Date d = (Date) fieldDateActual.getValue();
				mo.setDateActual(d);
				break;
			}
		}
		return mo;
	}
}

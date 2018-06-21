package com.graly.erp.wip.lothis;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wiphis.model.LotHis;

public class ByUsedLotWipHisQueryDialog extends EntityQueryDialog {

	public ByUsedLotWipHisQueryDialog(Shell parent,
			EntityTableManager tableManager, WipHisQuerySection refresh) {
		super(parent, tableManager, refresh);
	}
	
	@Override
	protected void okPressed() {
		if(!validateQueryKey()) return;
		setReturnCode(OK);
		WipHisQuerySection wipHisQuerySection = (WipHisQuerySection)iRefresh;
		wipHisQuerySection.refreshViewer(getViewerContents());
        this.setVisible(false);
    }
	
	private List<LotHis> getViewerContents(){
		try {
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			Long materialChildRrn = null;
			String lotChildId = null;
			StringBuffer otherWhere = new StringBuffer("");
			
			for(IField f : fields.values()){
				Object t = f.getValue();
				if("materialChildRrn".equals(f.getId())){
					if(t instanceof String){
						materialChildRrn = Long.parseLong((String)t);
					}
				}
				if("lotChildId".equals(f.getId())){
					lotChildId = (String) t;
				}
				if("dateProduct".equals(f.getId())){
					if(t instanceof Map){//只可能是FromToCalendarField
						Map m = (Map)t;
						Date from = (Date) m.get(FromToCalendarField.DATE_FROM);
						Date to = (Date) m.get(FromToCalendarField.DATE_TO);
						if(from != null) {
							otherWhere.append(" AND trunc(");
							otherWhere.append(" h.date_product ");
							otherWhere.append(") >= TO_DATE('" + I18nUtil.formatDate(from) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
						}
						
						if(to != null){
							otherWhere.append(" AND trunc(");
							otherWhere.append(" h.date_product ");
							otherWhere.append(") <= TO_DATE('" + I18nUtil.formatDate(to) + "', '" + I18nUtil.getDefaultDatePattern() + "') ");
						}
					}
				}
			}
			WipManager wipManager = Framework.getService(WipManager.class);
			return wipManager.getWipHisByUsedLot(Env.getOrgRrn(), lotChildId, materialChildRrn, otherWhere.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

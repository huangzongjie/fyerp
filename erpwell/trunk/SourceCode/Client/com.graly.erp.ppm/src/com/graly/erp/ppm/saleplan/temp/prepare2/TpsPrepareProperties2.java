package com.graly.erp.ppm.saleplan.temp.prepare2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityForm;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.CalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class TpsPrepareProperties2 extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(TpsPrepareProperties2.class);
	public static final String DELIVER_DATE = "dateDelivered";
	public static final String MATERIAL_ID = "materialRrn";
	
	public TpsPrepareProperties2() {
		super();
	}

	public TpsPrepareProperties2(EntityBlock masterParent, ADTable table) {
		super(masterParent, table, null);
	}
	@Override
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
		TpsLinePrepare tpsLinePrepare = (TpsLinePrepare)getAdObject();
		tpsLinePrepare.setTpsStatus(TpsLinePrepare.TPSSTATUS_DRAFTED);//预处理临时计划保存
		IField field = getIFieldById(MATERIAL_ID);
		if(field instanceof SearchField) {
			SearchField sf = (SearchField)field;
			if(sf.getData() instanceof Material) {
				tpsLinePrepare.setMaterialId(((Material)sf.getData()).getMaterialId());
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
	
	@Override
	protected void createSectionContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			ChildEntityForm itemForm = new ChildEntityForm(getTabs(), SWT.NONE, null, tab, mmng, parentObject);
			if(getDetailForms()!=null && getDetailForms().size() >0 ){
				setDetailForms(new ArrayList<Form>());
			}
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		if (parentObject != null) {
			loadFromParent();
		}
	}

}

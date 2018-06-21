package com.graly.erp.pur.po.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.po.model.PurchaseOrder;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.validator.ValidatorFactory;

public class GroupFieldForm extends EntityForm {
	List<ADField> groupFields;
	int mLeftPadding = 5;
	int mTopPadding = 0;
	int mRightPadding = 5;
	int mBottomPadding = 0;
	int mHorizSpacing = 5;
	int mVertSpacing = 5;
	// 每行显示列数数组
	int[] lineGrids;

	List<ADField> bhFilds;
	public static String Payment7 = "paymentRule7"; // 日支付
	public static String Payment9 = "paymentRule9"; // %
	public static String Payment10 = "paymentRule10"; // 日后支付
	public static String Payment11 = "paymentRule11"; // 是否开具发票
	public static String Payment14 = "paymentRule14"; // 收货后
	public static String Payment15 = "paymentRule15"; // __日支付
	public static String InvoiceType = "invoiceType"; // 发票类型
	private static final String VatRate = "vatRate"; // 增值税率(默认0.17)
	private String previousInvoiceType = PurchaseOrder.INVOICE_TYPE_REGULAR;//默认是普通发票
	private String previousVatRate = "0.17";//增值税率(默认0.17)

	public GroupFieldForm(Composite parent, int style, Object object,
			IMessageManager mmng, List<ADField> groupFields, int[] lineGrids) {
		super(parent, style, object, mmng);
		this.groupFields = groupFields;
		allADfields = groupFields;
		this.lineGrids = lineGrids;
		initGrid();
		super.createForm();
	}

	@Override
	public void createForm() {
	}

	public IField getField(ADField adField) {
		IField field = null;
		if (bhFilds.contains(adField)) {
			String displayText = adField.getDisplayType();
			String name = adField.getName();
			String displayLabel = I18nUtil.getI18nMessage(adField, "label");
			if (adField.getIsMandatory()) {
				displayLabel = displayLabel + "*";
			}
			int displayLength = adField.getDisplayLength() != null ? adField
					.getDisplayLength().intValue() : 32;
			if (FieldType.TEXT.equalsIgnoreCase(displayText)) {
				if (adField.getIsReadonly()) {
					field = createBhReadOnlyText(name, displayLabel, "");
				} else if (adField.getIsUpper()) {
					field = createUpperBhText(name, displayLabel, displayLength);
				} else {
					field = createBhText(name, displayLabel, displayLength);
				}
				addField(name, field);
			}
		} else {
			field = super.getField(adField);
		}
		return field;
	}

	@Override
	protected void createContent() {
		if (lineGrids != null && lineGrids.length != 0) {
			toolkit = new FormToolkit(getDisplay());
			setLayout(new FillLayout());
			form = toolkit.createScrolledForm(this);

			Composite body = form.getBody();
			GridLayout layout = new GridLayout();
			layout.verticalSpacing = mVertSpacing;
			layout.horizontalSpacing = mHorizSpacing;
			layout.marginLeft = mLeftPadding;
			layout.marginRight = mRightPadding;
			layout.marginTop = mTopPadding;
			layout.marginBottom = mBottomPadding;
			body.setLayout(layout);

			int cols = 1;
			int maxColumn = this.getMaxGrid();
			for (IField f : fields.values()) {
				f.createContent(body, toolkit);

				Control[] ctrls = f.getControls();
				int c = f.getColumnsCount();
				c = c > ctrls.length ? c : ctrls.length;
				if (cols < c) {
					cols = c;
				}
			}
			layout.numColumns = cols * maxColumn;

			// 设置各个控件的布局管理器的所占网格数
			int i = 0;
			int lineNo = 1; // 网格布局中的行数
			int grid = getColumnsByLineNo(lineNo); // 获得第一行的列数
			for (IField f : fields.values()) {
				Control[] ctrls = f.getControls();
				if (ctrls.length == 0) {
					continue;
				}
				i++;
				if (i % grid == 0 && grid != 1) {
					GridData gd = (GridData) ctrls[0].getLayoutData();
					if (gd == null) {
						gd = new GridData();
						ctrls[0].setLayoutData(gd);
					}
					// gd.horizontalIndent = 10;
				}

				// 如果该控件组列数小于最大的控件列数, 则用控件组的最后一个控件充满此行该列的多余的网格
				int r = ctrls.length % cols;
				if (r > 0) {
					GridData gd = (GridData) ctrls[ctrls.length - 1]
							.getLayoutData();
					if (gd == null) {
						gd = new GridData();
						ctrls[ctrls.length - 1].setLayoutData(gd);
					}
					gd.horizontalSpan = cols - r + 1;
				}

				// 如果到了列尾, 若该行列数小于最大列数则用l充满
				int rd = i % grid;
				if (rd == 0) {
					if (grid != maxColumn) {
						Label l = toolkit.createLabel(ctrls[0].getParent(), "");
						GridData gd = new GridData();
						gd.horizontalSpan = cols * (maxColumn - grid);
						l.setLayoutData(gd);
						l.moveBelow(ctrls[ctrls.length - 1]);
					}
					i = 0;
					grid = getColumnsByLineNo(++lineNo);
				}
			}
		} else {
			super.createContent();
		}
		GridData gd = new GridData();
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalAlignment = GridData.FILL;
		
		final IField isInvoiceField = fields.get(Payment11);
		final IField invoiceTypeField = fields.get(InvoiceType);
		if(invoiceTypeField != null)
			invoiceTypeField.getControls()[0].setLayoutData(gd);
		final IField vatRateField = fields.get(VatRate);
		if(vatRateField != null)
			vatRateField.getControls()[1].setLayoutData(gd);
		
		if(isInvoiceField != null && invoiceTypeField != null && vatRateField != null){
			isInvoiceField.addValueChangeListener(new IValueChangeListener(){
				@Override
				public void valueChanged(Object sender, Object newValue) {
					if(newValue == null || newValue instanceof Boolean){
						boolean enabled = (newValue == null ? false : ((Boolean)newValue).booleanValue());
						invoiceTypeField.getControls()[0].setEnabled(enabled);
						if (!enabled){
							vatRateField.getControls()[0].setVisible(false);
							vatRateField.getControls()[1].setVisible(false);
							vatRateField.setValue(null);
							invoiceTypeField.setValue(null);//如果不需要发票则发票类型是空的
						}else{
							invoiceTypeField.setValue(previousInvoiceType);
						}
					}
					checkEnabled();
					invoiceTypeField.refresh();
				}				
			});
			
			invoiceTypeField.addValueChangeListener(new IValueChangeListener(){
				@Override
				public void valueChanged(Object sender, Object newValue) {
					boolean flag = false;//是否开具发票
					if(newValue instanceof String){
						if(((String)newValue).trim().length() == 0){
							newValue = null;
						}
					}
					if(isInvoiceField.getValue() == null || isInvoiceField.getValue() instanceof Boolean){
						flag = (isInvoiceField.getValue() == null ? false : ((Boolean)isInvoiceField.getValue()).booleanValue());
					}
					if(newValue != null){
						if(newValue.equals(PurchaseOrder.INVOICE_TYPE_VAT)){
							previousInvoiceType = String.valueOf(newValue);
							vatRateField.getControls()[0].setVisible(true);
							vatRateField.getControls()[1].setVisible(true);
							if(previousVatRate != null)
								vatRateField.setValue(previousVatRate);
						}else if(newValue.equals(PurchaseOrder.INVOICE_TYPE_REGULAR)){
							previousInvoiceType = String.valueOf(newValue);
							vatRateField.getControls()[0].setVisible(false);
							vatRateField.getControls()[1].setVisible(false);
							vatRateField.setValue(null);
						}
					}else{						
						if(flag){
							invoiceTypeField.setValue(PurchaseOrder.INVOICE_TYPE_REGULAR);
						}
					}
					checkEnabled();
					vatRateField.refresh();
				}
				
			});
			
			vatRateField.addValueChangeListener(new IValueChangeListener(){
				@Override
				public void valueChanged(Object sender, Object newValue) {
					if(newValue != null && String.valueOf(newValue).trim().length() != 0){
						previousVatRate = String.valueOf(newValue);
					}
					checkEnabled();
				}
				
			});
		}
	}
	
	@Override
	public void loadFromObject() {
		super.loadFromObject();
//		checkEnabled();
	}
	
	private void checkEnabled(){
		final IField isInvoiceField = fields.get(Payment11);
		final IField invoiceTypeField = fields.get(InvoiceType);
		final IField vatRateField = fields.get(VatRate);
		boolean flag1 = false;
		boolean flag2 = false;
		if(isInvoiceField.getValue() == null || isInvoiceField.getValue() instanceof Boolean){
			flag1 = (isInvoiceField.getValue() == null ? false : ((Boolean)isInvoiceField.getValue()).booleanValue());
		}
		
			flag2 = (invoiceTypeField.getValue() == null); 
		
		if(!flag1){
			invoiceTypeField.setEnabled(false);
		}
		
		if(flag2){
			vatRateField.getControls()[0].setVisible(false);
			vatRateField.getControls()[1].setVisible(false);
		}
		
	}

	private void initGrid() {
		if (lineGrids == null || lineGrids.length == 0) {
			this.setGridY(4);
		}
		// 添加需要后置Label文本的ADField
		bhFilds = new ArrayList<ADField>();
		for (ADField adField : groupFields) {
			String id = adField.getName();
			if (Payment7.equals(id) || Payment9.equals(id)
					|| Payment10.equals(id) || Payment15.equals(id)) {
				bhFilds.add(adField);
			}
		}
	}

	private int getMaxGrid() {
		int maxGrid = 1;
		for (int grid : lineGrids) {
			maxGrid = maxGrid < grid ? grid : maxGrid;
		}
		return maxGrid;
	}

	private int getColumnsByLineNo(int lineNo) {
		if (lineGrids.length > (lineNo - 1)) {
			return lineGrids[lineNo - 1];
		} else {
			return lineGrids[0];
		}
	}

	public TextBehindField createBhReadOnlyText(String id, String label,
			String value) {
		TextBehindField fe = new TextBehindField(id);
		fe.setLabel(label);
		fe.setValue(value);
		fe.setReadOnly(true);
		return fe;
	}

	public TextBehindField createUpperBhText(String id, String label, int limit) {
		return createUpperBhText(id, label, "", limit);
	}

	public TextBehindField createUpperBhText(String id, String label,
			String value, int limit) {
		TextBehindField fe = new TextBehindField(id, true);
		fe.setLabel(label);
		fe.setValue(value != null ? value : "");
		fe.setLength(limit);
		return fe;
	}

	public TextBehindField createBhText(String id, String label, int limit) {
		return createBhText(id, label, "", limit);
	}

	public TextBehindField createBhText(String id, String label, String value,
			int limit) {
		TextBehindField fe = new TextBehindField(id);
		fe.setLabel(label);
		fe.setValue(value != null ? value : "");
		fe.setLength(limit);
		return fe;
	}
	
	@Override
	public boolean validate() {
		boolean validFlag = true;
		for (IField f : fields.values()){
			ADField adField = adFields.get(f.getId());
			if(adField != null){
				if (adField.getIsMandatory() && f.getControls()[0].isVisible() && f.getControls()[1].isVisible()){
					Object value = f.getValue();
					boolean isMandatory = false;
					if (value == null){
						isMandatory = true;
					} else {
						if (value instanceof String){
							if ("".equalsIgnoreCase(value.toString().trim())){
								isMandatory = true;
							}
						} else if(value instanceof Map) {
							Map<String, Date> map = (Map<String, Date>)value;
							if(map.values().contains(null)) {
								isMandatory = true;
							}
						}
					}
					if (isMandatory){
						validFlag = false;
						mmng.addMessage(adField.getName() + "common.ismandatory", 
								String.format(Message.getString("common.ismandatory"), I18nUtil.getI18nMessage(adField, "label")), null,
								IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
					}
				}
				if (adField.getDataType() != null && !"".equalsIgnoreCase(adField.getDataType().trim())){
					if (!(f.getValue() instanceof String)){
						continue;
					}
					String value = (String)f.getValue();
					if (value != null && !"".equalsIgnoreCase(value.trim())){
						if (!ValidatorFactory.isValid(adField.getDataType(), value)){
							validFlag = false;
							mmng.addMessage(adField.getName() + "common.isvalid", 
									String.format(Message.getString("common.isvalid"), I18nUtil.getI18nMessage(adField, "label"), adField.getDataType()), null,
									IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
						} else if (!ValidatorFactory.isInRange(adField.getDataType(), value, adField.getMinValue(), adField.getMaxValue())){
							validFlag = false;
							if ((adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim()))
							 && (adField.getMaxValue() != null && !"".equalsIgnoreCase(adField.getMaxValue().trim()))){
								mmng.addMessage(adField.getName() + "common.between", 
										String.format(Message.getString("common.between"), I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue(), adField.getMaxValue()), null,
											IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
							} else if (adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim())){
								mmng.addMessage(adField.getName() + "common.largerthan", String.format(Message.getString("common.largerthan"), I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue()), null,
										IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);						
							} else {
								mmng.addMessage(adField.getName() + "common.lessthan", String.format(Message.getString("common.lessthan"), I18nUtil.getI18nMessage(adField, "label"), adField.getMaxValue()), null,
										IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);												
							}
						}
					}
				}
				if (adField.getNamingRule() != null && !"".equalsIgnoreCase(adField.getNamingRule().trim())){
					Object value = f.getValue();
					if (value == null){
						continue;
					}
					if (value instanceof String){
						if (!Pattern.matches(adField.getNamingRule(), value.toString())) {
							validFlag = false;
							mmng.addMessage(adField.getName() + "common.namingrule_error", 
									String.format(Message.getString("common.namingrule_error"), I18nUtil.getI18nMessage(adField, "label")), null,
									IMessageProvider.ERROR, f.getControls()[f.getControls().length - 1]);
						}
					}
				}
			}
			
		}
		return validFlag;
	}
}

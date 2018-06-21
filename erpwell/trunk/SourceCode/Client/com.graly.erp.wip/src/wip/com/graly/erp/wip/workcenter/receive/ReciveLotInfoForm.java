package com.graly.erp.wip.workcenter.receive;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.field.AbstractField;
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.model.WIPMould;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;

public class ReciveLotInfoForm extends EntityForm {
	protected WorkCenter workCenter;
	private Object obj;
	
	public ReciveLotInfoForm(Composite parent, int style, Object object,
			IMessageManager mmng) {
		super(parent, style, object, mmng);
	}

	public ReciveLotInfoForm(Composite parent, int style, ADTab tab,
			IMessageManager mmng) {
		super(parent, style, null, tab, mmng);
	}

	public ReciveLotInfoForm(Composite parent, int style, Object obj,
			ADTable table, IMessageManager mmng) {
		super(parent, style, obj, table, mmng);
	}

	public ReciveLotInfoForm(Composite parent, int style,
			WorkCenter workCenter, Object obj, ADTable table,
			IMessageManager mmng) {
		super(parent, style, obj, table, mmng);
		this.workCenter = workCenter;
		this.obj = obj;
	}

	@Override
	public void createForm() {
	}

	public void createFormContent() {
		super.createForm();
	}

	@Override
	public boolean saveToObject() {
		if (object != null){
			if (!validate()){
				return false;
			}
			for (IField f : fields.values()){
				if(f instanceof ComboField && f.getId().equals(TextProvider.FieldName_EquipmentRrn)){
					ComboField equipmentCombo = (ComboField) f;
					try{
						//空值和字符串类型会进入异常,section保存的时候，会根据equipmentRrn查找设备
						long equipmentRrn = Long.parseLong(f.getValue()+"");
						PropertyUtil.setProperty(object, f.getId(), f.getValue());	
					}catch(NumberFormatException e){
						if(object instanceof Lot){
							Lot lot = (Lot) object;
							if(equipmentCombo.getValue()==null){
								lot.setEquipmentId(null);
								lot.setEquipmentRrn(null);
							}else{
								lot.setEquipmentId(equipmentCombo.getValue().toString());
								lot.setEquipmentRrn(null);
							}
						}
					}
				}else if(!(f instanceof SeparatorField)){
					PropertyUtil.setProperty(object, f.getId(), f.getValue());
				}
			}
			return true;
		}
		return false;
//		return super.saveToObject();
	}

	@Override
	public void loadFromObject() {
		super.loadFromObject();
	}

	protected void createADFields() {
		ADField adField = new ADField();
		adField.setName(TextProvider.FieldName_UserQc);
		adField.setIsDisplay(true);
		adField.setIsEditable(true);
		adField.setLabel("Inspector ID");
		adField.setLabel_zh("检验员");
		adField.setDisplayType(FieldType.TEXT);
		allADfields.add(adField);

		adField = new ADField();
		adField.setName(TextProvider.FieldName_EquipmentRrn);
		adField.setIsDisplay(true);
		adField.setIsEditable(true);
		adField.setLabel("Equipment ID");
		adField.setLabel_zh("设备编号");
		adField.setDisplayType(FieldType.TEXT);
		allADfields.add(adField);

		adField = new ADField();
		adField.setName(TextProvider.FieldName_Comments);
		adField.setIsDisplay(true);
		adField.setIsEditable(true);
		adField.setIsSameline(true);
		adField.setLabel("Comments");
		adField.setLabel_zh("备注");
		adField.setDisplayType(FieldType.TEXTAREA);
		allADfields.add(adField);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IField getField(ADField adField) {
		IField field = null;
		if (TextProvider.FieldName_Comments.equals(adField.getName())) {
			String name = adField.getName();
			field = new FillFullTextField(name);
			field.setLabel(I18nUtil.getI18nMessage(adField, "label"));
			((FillFullTextField) field).setMultiLine(true);
			addField(name, field);
		} else if (TextProvider.FieldName_EquipmentId.equals(adField.getName())) {
			if(workCenter.getMustEqp1()){
				adField.setIsMandatory(true);
			}
			try {
				WipManager wipManager = Framework.getService(WipManager.class);
				LinkedHashMap<String, String> lmp = new LinkedHashMap<String, String>();
				List<Object[]> equipments = wipManager.getEquipmentByWorkCenter(this.workCenter.getOrgRrn(),this.workCenter.getObjectRrn());
				for(Object[] objs : equipments) {
					lmp.put(objs[0].toString(), objs[1].toString());
				}
				field = new ComboField(TextProvider.FieldName_EquipmentRrn,lmp);
				field.setLabel(super.getField(adField).getLabel());
//				field.addValueChangeListener(getIsRepeatChangedListener());
				addField(adField.getName(), field);			
			} catch (Exception e) {
				e.printStackTrace();
			}			
		} else if (TextProvider.FieldName_MoldId.equals(adField.getName())) {
			if(workCenter.getMustMold1()){
				adField.setIsMandatory(true);
			}
			field = super.getField(adField);
			Lot wipLot = (Lot) obj;
			Long wipMaterialRrn = wipLot.getMaterialRrn();
			try {
				ADManager adManager = Framework.getService(ADManager.class);
				Material wipMaterial = new Material();
				wipMaterial.setObjectRrn(wipMaterialRrn);
				wipMaterial = (Material) adManager.getEntity(wipMaterial);
				
				ComboField mouldField = (ComboField)field;
				LinkedHashMap<String, String> lmp = new LinkedHashMap<String, String>();
				WIPMould mould = new WIPMould();
				mould.setObjectRrn(wipMaterial.getMouldRrn());
				mould = (WIPMould) adManager.getEntity(mould);
				lmp.put(mould.getObjectRrn().toString(), mould.getMouldId());
				mouldField.setMItems(lmp);
				mouldField.setId(TextProvider.FieldName_MoldRrn);
				wipLot.setMoldRrn(wipMaterial.getMouldRrn());
				wipLot.setMoldId(mould.getMouldId());
			} catch (Exception e) {
				e.printStackTrace();
			};
		} else {
			field = super.getField(adField);
			field.setValue(this.workCenter.getObjectRrn().toString());
		}
		return field;
	}
//	public IValueChangeListener getIsRepeatChangedListener() {	
//		return new IValueChangeListener() {
//			public void valueChanged(Object sender, Object newValue) {
//				Object fObject = fields.get(TextProvider.FieldName_MoldId);
//				if(fObject != null && fObject instanceof ComboField){
//					try {
//						ComboField mouldField = (ComboField)fObject;
//						mouldField.refresh();
//						PrdManager prdManager = Framework.getService(PrdManager.class);
//						LinkedHashMap<String, String> lmp = new LinkedHashMap<String, String>();
//						Long selectEquipemntRrn = null;
//						try{
//							selectEquipemntRrn = Long.parseLong((String) newValue);
//						}catch(Exception e){
//							//do nothing
//							//if newValue if not number type, selectEquipment still null
//						}
//						if(selectEquipemntRrn != null){
//							List<Object[]> moulds = prdManager.getMouldByEquipment(workCenter.getOrgRrn(),selectEquipemntRrn);
//							for(Object[] objs : moulds) {
//								lmp.put(objs[0].toString(), objs[1].toString());
//							}
//							mouldField.setMItems(lmp);
//							mouldField.setId(TextProvider.FieldName_MoldRrn);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}		
//				}
//			}
//		};
//	};
	
	class FillFullTextField extends AbstractField {
		private int mStyle = SWT.NONE;
		private int mRows = 4;
		private int mCols = 32;
		private int mLimit = 32;
		private boolean autoUpper = false;
		Text text;

		public FillFullTextField(String id) {
			super(id);
			this.mStyle = SWT.BORDER;
		}

		public void createContent(Composite composite, FormToolkit toolkit) {
			String labelStr = getLabel();
			if (labelStr != null) {
				Label label = toolkit.createLabel(composite, labelStr);
				text = toolkit.createText(composite, (String) getValue(),
						mStyle);
				mControls = new Control[2];
				mControls[0] = label;
				mControls[1] = text;
				if (getToolTipText() != null) {
					text.setToolTipText(getToolTipText());
				}
			} else {
				text = toolkit.createText(composite, (String) getValue(),
						mStyle);
				mControls = new Control[1];
				mControls[0] = text;
				if (getToolTipText() != null) {
					text.setToolTipText(getToolTipText());
				}
			}
			GridData gd = new GridData(GridData.FILL_BOTH);
			text.setLayoutData(gd);
			if ((mStyle & SWT.READ_ONLY) == SWT.READ_ONLY) {
				text.setBackground(text.getDisplay().getSystemColor(
						SWT.COLOR_WIDGET_BACKGROUND));
			}
			FontMetrics fm = getFontMetric(text);
			if ((mStyle & SWT.MULTI) == SWT.MULTI) {
				if (mRows > 0) {
					gd.heightHint = Dialog.convertHeightInCharsToPixels(fm,
							mRows);
				}
			} else {
				text.setTextLimit(mLimit);
			}
			if (mCols > 0) {
				gd.widthHint = Dialog.convertWidthInCharsToPixels(fm, mCols);
			}
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (autoUpper) {
						setValue(text.getText().toUpperCase());
					} else {
						setValue(text.getText());
					}
				}
			});
			text.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
				}

				public void focusLost(FocusEvent e) {
					if (autoUpper) {
						text.setText(text.getText().toUpperCase());
					}
				}
			});
		}

		public Text getTextControl() {
			Control[] ctrls = getControls();
			return (Text) ctrls[ctrls.length - 1];
		}

		public Label getLabelControl() {
			Control[] ctrls = getControls();
			return ctrls.length > 1 ? (Label) ctrls[0] : null;
		}

		public void setReadOnly(boolean readOnly) {
			if (readOnly) {
				mStyle |= SWT.READ_ONLY;
			} else {
				mStyle &= ~SWT.READ_ONLY;
			}
		}

		public void setMultiLine(boolean isMultiLine) {
			if (isMultiLine) {
				mStyle |= SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL;
			} else {
				mStyle &= ~(SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
			}
		}

		public void setWrap(boolean wrapText) {
			if (wrapText) {
				mStyle |= SWT.WRAP;
			} else {
				mStyle &= ~SWT.WRAP;
			}
		}

		public int getStyle() {
			return mStyle;
		}

		public void setStyle(int style) {
			mStyle = style;
		}

		public void appendStyle(int style) {
			mStyle |= style;
		}

		public String getText() {
			return getTextControl().getText();
		}

		public void setText(String text) {
			if (text == null)
				text = "";
			if (autoUpper) {
				text = text.toUpperCase();
			}
			getTextControl().setText(text);
		}

		public void setHeight(int rows) {
			mRows = rows;
		}

		public void setWidth(int cols) {
			mCols = cols;
		}

		public void setLength(int limit) {
			mLimit = limit;
		}

		public void refresh() {
			setText((String) getValue());
		}

		public String getFieldType() {
			return "text";
		}

		@Override
		public void enableChanged(boolean enabled) {
			if ((mStyle & SWT.READ_ONLY) != SWT.READ_ONLY) {
				text.setEnabled(enabled);
			}
			super.enableChanged(enabled);
		}
	}
}

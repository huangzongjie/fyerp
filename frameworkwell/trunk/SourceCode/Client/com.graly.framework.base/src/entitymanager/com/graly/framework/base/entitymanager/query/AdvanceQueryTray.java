package com.graly.framework.base.entitymanager.query;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.validator.DataType;
import com.graly.framework.base.ui.validator.ValidatorFactory;

public class AdvanceQueryTray extends DialogTray {
	
	private TrayDialog dialog;
	private ADTable adTable;
	private List<AdvanceQueryEntity> queryFields;
	private AdvanceQueryTableManager tableManager;
	private TableViewer tableViewer;
	
	public AdvanceQueryTray(TrayDialog dialog) {
		this.dialog = dialog;
	}
	
	public AdvanceQueryTray(TrayDialog dialog, ADTable adTable) {
		this.dialog = dialog;
		this.adTable = adTable;
	}
	
	protected Control createContents(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		configureBody(content);
		FormToolkit toolkit = new FormToolkit(content.getDisplay());		
		ScrolledForm sForm = toolkit.createScrolledForm(content);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		// 创建Table
		tableManager = new AdvanceQueryTableManager(adTable);
		tableViewer = (TableViewer)tableManager.createViewer(body, toolkit, 150);
		// 为Table充值
		tableViewer.setInput(getQueryFields());
		tableManager.updateView(tableViewer);
		return content;
	}
	
	protected List<AdvanceQueryEntity> getQueryFields() {
		queryFields = new ArrayList<AdvanceQueryEntity>();
		AdvanceQueryEntity aqe = null;
		int i = 1;
		for(ADField adField : adTable.getFields()) {
			if(i > 10) break;
			if(adField.getIsAdvanceQuery()) {
				aqe = new AdvanceQueryEntity();
//				aqe.setField(I18nUtil.getI18nMessage(adField, "label"));
				aqe.setDataType(adField.getDataType());
				aqe.setData(adField);
				queryFields.add(aqe);
				i++;
			}
		}
		return queryFields;
	}
	
	/*
	 * 全部用AND连接，1.若为Time，则当日起类型来处理,
	 * 若值中含有时间(例'2009-07-09 01:44:33')，则查不出日期为2009-07-09的结果集
	 * 2.当对应的ADField的display type为boolean时，输入的查询值必须为Y或者N
	 * 3.当display type为radio, dropList等时, 输入的查询值必须为参考值而非显示名,例如Female和Man,对应F和M
	 * 4.当display type为refTable等时,当参考表定义的显示的栏位也关键字栏位不同时,输入的显示栏位中的值
	 * 会导致查不出任何结果集
	 */
	public String getAdvaceWhereClause() {
		StringBuffer advance = new StringBuffer("");
		String modelName = adTable.getModelName() + ".";
		String id = null;
		for(AdvanceQueryEntity aqe : getValueableFields()) {
			id = ((ADField)aqe.getData()).getName();
			if (EntityComparator.ID_IS_NULL.equals(aqe.getComparator()) || EntityComparator.ID_NOT_NULL.equals(aqe.getComparator())) {
				advance.append(" AND ");
				advance.append(modelName);
				advance.append(id);
				advance.append(" " + aqe.getComparator() + " ");
			} else if (DataType.DATE.equals(aqe.getDataType())) {
				advance.append(" AND ");
				advance.append("TO_CHAR(");
				advance.append(modelName);
				advance.append(id);
				advance.append(", '" + I18nUtil.getDefaultDatePattern() + "')");
				advance.append(" " + aqe.getComparator() + " '");
				advance.append(aqe.getValue());
				advance.append("'");
			} else if (DataType.TIME.equals(aqe.getDataType())) {
				advance.append(" AND ");
				advance.append("TO_CHAR(");
				advance.append(modelName);
				advance.append(id);
				advance.append(", '" + I18nUtil.getDefaultTimePattern() + "')");
				advance.append(" " + aqe.getComparator() + " '");
				advance.append(aqe.getValue());
				advance.append("'");
			} else if (DataType.DOUBLE.equals(aqe.getDataType())
					|| DataType.INTEGER.equals(aqe.getDataType())) {
				if (!EntityComparator.ID_LIKE.equals(aqe.getComparator())) {
					advance.append(" AND ");
					advance.append(modelName);
					advance.append(id);
					advance.append(" " + aqe.getComparator() + " ");
					advance.append(aqe.getValue().trim());
					advance.append(" ");
				}
			} else {
				advance.append(" AND ");
				advance.append(modelName);
				advance.append(id);
				advance.append(" " + aqe.getComparator() + " '");
				advance.append(aqe.getValue());
				advance.append("'");
			}
		}
		return advance.toString();
	}
	
	protected boolean validate() {
		for(AdvanceQueryEntity aqe : getValueableFields()) {
			if(aqe.getDataType() != null) {
				if (EntityComparator.ID_IS_NULL.equals(aqe.getComparator()) || EntityComparator.ID_NOT_NULL.equals(aqe.getComparator())) {
					continue;
				}
				if (!ValidatorFactory.isValid(aqe.getDataType(), aqe.getValue())) {
					((EntityQueryDialog)dialog).setErrorMessage(
							String.format(Message.getString("common.isvalid"), aqe.getField(), aqe.getDataType()));
					return false;
				}
			}
		}
		return true;
	}
	
	// 返回设置了查询条件的aqes(即comparator和value都不为空,并且field不能重复设置)
	protected List<AdvanceQueryEntity> getValueableFields() {
		List<AdvanceQueryEntity> fs = new ArrayList<AdvanceQueryEntity>();
		Object obj = tableViewer.getInput();
		if(obj instanceof List) {
			for(AdvanceQueryEntity aqe : (List<AdvanceQueryEntity>)obj) {
				if(aqe.getField() != null && aqe.getComparator() != null && !"".equals(aqe.getComparator().trim())) {
					if (EntityComparator.ID_IS_NULL.equals(aqe.getComparator()) || EntityComparator.ID_NOT_NULL.equals(aqe.getComparator())) {
						if(!contains(fs, aqe)) {
							fs.add(aqe);
						}
					} else if (aqe.getValue() != null && !"".equals(aqe.getValue().trim())) {
						if(!contains(fs, aqe)) {
							fs.add(aqe);
						}
					}
				}
			}
		}
		return fs;
	}
	
	// 若aqe已经存在fs中(field相等表示已存在,不能用重载aqe的equals()实现,则返回true
	private boolean contains(List<AdvanceQueryEntity> fs, AdvanceQueryEntity aqe) {
		for(AdvanceQueryEntity exsited : fs) {
			if(exsited.getField().equals(aqe.getField())) 
				return true;
		}
		return false;
	}

	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
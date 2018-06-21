package com.graly.erp.ppm.setplan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.ppm.model.Mps;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADUserRefList;
import com.graly.framework.base.ui.forms.field.RadioField;
import com.graly.framework.base.ui.util.ADFieldUtil;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class ExportDialog extends TitleAreaDialog {
	private static final Logger logger = Logger.getLogger(ExportDialog.class);
	protected LinkedHashMap<String, String> radioItems = new LinkedHashMap<String, String>() ;
	protected RadioField rf;
	protected Mps mps;
	protected String REF_TABLE_NAME = "UserReferenceByName";
	protected String REFERENCE_NAME = "PlanCategory";
	protected String reportName = "plansum_report.rptdesign";
	
	public ExportDialog(Shell parentShell, Mps mps) {
		super(parentShell);
		this.mps = mps;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("statistics_title"));
        setTitle("选择报表类型");
        setMessage("重要提醒：\n\t查看报表前请确定本月已经执行过\"运算\"功能了\r,否则导出的数据仍为上一次执行\"运算\"功能时生成的数据,\"运算\"功能一般每月只需执行一次", IMessageProvider.WARNING);

        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		Composite radioGroup = toolkit.createComposite(body);
		GridLayout gl = new GridLayout();
		gl.marginHeight = 20;
		gl.marginLeft = 10;
		radioGroup.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_BOTH);
		radioGroup.setLayoutData(gd);
		
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADRefTable refTable = entityManager.getADRefTable(Env.getOrgRrn(), REF_TABLE_NAME);
			String whereClause = " referenceName = '" + REFERENCE_NAME + "'";
			List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), refTable.getTableRrn(), 
					Env.getMaxResult(), whereClause, refTable.getOrderByClause());
			for (ADBase adBase : list){
				ADUserRefList listItem = (ADUserRefList) adBase;
				radioItems.put(listItem.getValue(), listItem.getKey());
			}
			ADFieldUtil.createRadioGroup("planCategory", "分类", radioItems);
			rf = new RadioField("catalog", radioItems);
			rf.createContent(radioGroup, toolkit);
		} catch (Exception e) {
			logger.error("ExportDialog : createDialogArea()", e);
		}
		return body;
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
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}
	
	@Override
	protected void okPressed() {
		if(rf != null){
			String planCategory = (String) rf.getValue();
			try {
				planCategory=java.net.URLEncoder.encode(planCategory,"UTF-8");
				planCategory=java.net.URLEncoder.encode(planCategory,"UTF-8");//不知道为什么这里要encode两次才有用
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			
			if(mps == null || mps.getObjectRrn() == null){
				UI.showError("选择一个计划!");
				return;
			}
			
			Date startDate = mps.getDateStart();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
			String year = formatter.format(startDate);
			formatter = new SimpleDateFormat("MM");
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
//			cal.add(Calendar.MONTH, 1);
			String month = formatter.format(cal.getTime());
			
			userParams.put("YEAR", year);
			userParams.put("NEXT_MONTH", month);
			userParams.put("PLAN_CATEGORY", planCategory);
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), reportName, params, userParams);
			dialog.open();
		}
	}
}

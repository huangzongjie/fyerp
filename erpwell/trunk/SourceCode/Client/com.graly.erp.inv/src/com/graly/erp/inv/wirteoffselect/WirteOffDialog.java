package com.graly.erp.inv.wirteoffselect;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.inv.material.online.OnlineSection;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class WirteOffDialog extends ExtendDialog {
	private static final Logger logger = Logger.getLogger(WirteOffDialog.class);
	private int MIN_DIALOG_WIDTH = 250;
	private int MIN_DIALOG_HEIGHT = 150;
	private String ID3 = "dateApproved";
	private String ID4 = "dateWriteOff";
	protected ManagedForm managedForm;
	private Button writeOff_current;
	private Button writeOff_all;
	private Button writeOff_assault;
	private Button writeOff_fromto;
	private FromToCalendarField fromToField3;
	private FromToCalendarField fromToField4;
	private MovementInListEntryPage page;
	public WirteOffDialog() {
		super();
	}
	
	public WirteOffDialog(MovementInListEntryPage page) {
		super();
		this.page = page;
	}
	

	@Override
	protected Control createDialogArea(Composite parent) {
		 	setTitleImage(SWTResourceCache.getImage("search-dialog"));
			setTitle(Message.getString("inv.wirteoff_search"));
	        Composite composite = (Composite) super.createDialogArea(parent);
	        
	        FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
			ScrolledForm sForm = toolkit.createScrolledForm(composite);
			sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
			managedForm = new ManagedForm(toolkit, sForm);
			Composite body = sForm.getForm().getBody();
			configureBody(body);
			
	        Composite client = toolkit.createComposite(body, SWT.BORDER);
	        client.setLayout(new GridLayout(3, false));
	        client.setLayoutData(new GridData(GridData.FILL_BOTH));
	        
	        writeOff_current = toolkit.createButton(client, "", SWT.RADIO);
	        writeOff_current.setLayoutData(new GridData(GridData.CENTER));
	        writeOff_current.addSelectionListener(getFromToField3DisableListener());
	        writeOff_current.addSelectionListener(getFromToField4DisableListener());
	        Label lbl_1 = toolkit.createLabel(client, Message.getString("inv.wirteoff_current_month"));
	        GridData lblGd = new GridData();
	        lblGd.horizontalSpan = 2;//占两列
	        lbl_1.setLayoutData(lblGd);
	        writeOff_all = toolkit.createButton(client, "", SWT.RADIO);
	        writeOff_all.setLayoutData(new GridData(GridData.CENTER));
	        writeOff_all.addSelectionListener(getFromToField3DisableListener());
	        writeOff_all.addSelectionListener(getFromToField4DisableListener());
	        Label lbl_2 = toolkit.createLabel(client, Message.getString("inv.wirteoff_of_all"));
	        lbl_2.setLayoutData(lblGd);
	        writeOff_fromto = toolkit.createButton(client, "", SWT.RADIO);
	        GridData gd = new GridData(GridData.CENTER);
	        gd.verticalIndent = 7;//填充top空间相当于margin
	        writeOff_fromto.setLayoutData(gd);
	        writeOff_fromto.addSelectionListener(getFromToField3EnableListener());
	        writeOff_fromto.addSelectionListener(getFromToField4DisableListener());
	        Label lbl_3 = toolkit.createLabel(client, Message.getString("inv.wirteoff_search"));
	        fromToField3 = new FromToCalendarField(ID3);
	        fromToField3.createContent(client, toolkit);
	        
	        writeOff_assault = toolkit.createButton(client, "", SWT.RADIO);
	        writeOff_assault.setLayoutData(gd);
	        writeOff_assault.addSelectionListener(getFromToField3DisableListener());
	        writeOff_assault.addSelectionListener(getFromToField4EnableListener());
	        Label lbl_4 = toolkit.createLabel(client, Message.getString("inv.wirteoff_current_month_of_assault"));
	        fromToField4 = new FromToCalendarField(ID4);
	        fromToField4.createContent(client, toolkit);
//	        lbl_4.setLayoutData(lblGd);
	        return composite;
	}

	private SelectionListener getFromToField3EnableListener(){
		SelectionListener sl = new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(fromToField3 != null){
					fromToField3.setEnabled(true);
				}
			}        	
        };
        return sl;
	}
	
	private SelectionListener getFromToField3DisableListener(){
		SelectionListener sl = new SelectionListener(){
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(fromToField3 != null){
					fromToField3.setEnabled(false);
				}
			}        	
		};
		return sl;
	}
	private SelectionListener getFromToField4EnableListener(){
		SelectionListener sl = new SelectionListener(){
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(fromToField4 != null){
					fromToField4.setEnabled(true);
				}
			}        	
		};
		return sl;
	}
	
	private SelectionListener getFromToField4DisableListener(){
		SelectionListener sl = new SelectionListener(){
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(fromToField4 != null){
					fromToField4.setEnabled(false);
				}
			}        	
		};
		return sl;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		Date now = Env.getSysDate();
		if (IDialogConstants.OK_ID == buttonId) {
			int writeoff = 1;
			if(writeOff_all.getSelection()){
				writeoff = 2;
			}else if(writeOff_fromto.getSelection()){
				writeoff = 3;
			}else if(writeOff_assault.getSelection()){
				writeoff = 4;
			}
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				StringBuffer whereClause = new StringBuffer(" 1 = 1 ");
//				whereCause.append(" ( docType LIKE 'PIN' ) ");
				switch(writeoff){
				case 1://本月暂估清单
					Calendar calendarCurrent = Calendar.getInstance();  
					calendarCurrent.setTime(now);
					calendarCurrent.set(Calendar.DATE, 1); 
					Date calCurrent = dateFormat.parse(dateFormat.format(calendarCurrent.getTime()));
					whereClause.append(" AND ( docStatus LIKE 'APPROVED' ) AND dateApproved >= to_date('" + calCurrent.toLocaleString()
							+ "','YYYY-MM-DD hh24:mi:ss')");
					break;
				case 2://累计暂估清单
					whereClause.append(" AND ( docStatus LIKE 'APPROVED' )");
					break;
				case 3://按时间区间查询暂估清单
					Map dateMap3 = (Map) fromToField3.getValue();
					Date from3 = (Date) dateMap3.get(FromToCalendarField.DATE_FROM);
					Date to3 = (Date) dateMap3.get(FromToCalendarField.DATE_TO);
					whereClause.append(" AND (docStatus LIKE 'APPROVED') ");
					if(from3 != null) {
						whereClause.append(" AND trunc(dateApproved) >= ");
						whereClause.append(" TO_DATE('" + I18nUtil.formatDate(from3) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
					if(to3 != null){
						whereClause.append(" AND trunc(dateApproved) <= ");
						whereClause.append(" TO_DATE('" + I18nUtil.formatDate(to3) + "', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
					break;
				case 4://按时间区间查询冲销清单
					Map dateMap4 = (Map) fromToField4.getValue();
					Date from4 = (Date) dateMap4.get(FromToCalendarField.DATE_FROM);
					Date to4 = (Date) dateMap4.get(FromToCalendarField.DATE_TO);
					whereClause.append(" AND ( docStatus LIKE 'COMPLETED' ) ");
					if(from4 != null) {
						whereClause.append(" AND trunc(dateWriteOff) >= ");
						whereClause.append(" TO_DATE('" + I18nUtil.formatDate(from4) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
					
					if(to4 != null){
						whereClause.append(" AND trunc(dateWriteOff) <= ");
						whereClause.append(" TO_DATE('" + I18nUtil.formatDate(to4) + "', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
					break;
				}
				if(page == null){
					MovementInListEditor editor = (MovementInListEditor) getParent();
					page = (MovementInListEntryPage) editor.getActivePageInstance();
				}
				page.getInListSection().setWhereClause(whereClause.toString());
				((MovementInListSection)page.getInListSection()).setWirteOffDialog(this);
				page.getInListSection().refresh();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			setVisible(false);
			return;
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			setVisible(false);
			return;
		}
	}
	
	public void setVisible(Boolean visible){
		getShell().setVisible(visible);
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.search"), false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
	}
}

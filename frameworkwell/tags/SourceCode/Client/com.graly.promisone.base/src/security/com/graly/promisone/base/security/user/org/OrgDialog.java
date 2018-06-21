package com.graly.promisone.base.security.user.org;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADRefList;
import com.graly.promisone.base.ui.forms.field.TextField;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.util.UI;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.security.model.ADOrg;
import com.graly.promisone.security.model.ADUser;

public class OrgDialog extends TitleAreaDialog {
	private static final Logger logger = Logger.getLogger(OrgDialog.class);
	public static final String DIALOG_ID = "com.graly.promisone.base.security.user.org.OrgDialog";

	protected CCombo defaultOrg;
	protected CCombo defaultLanguage;
	protected String REF_LANGUAGE = "Language";
	protected CCombo defaultView;
	protected String REF_VIEW = "View";
	protected Button isShowLauncher;
	private ADUser user;
	private static String ORGS = "orgs";
	private static String IS_SHOWLAUNCHER = "isShowLauncher";
	private static String DEFAULTLANGUAGE = "defLanguage";
	
	LinkedHashMap<String, String> orgMap;
	LinkedHashMap<String, String> languageMap;
	LinkedHashMap<String, String> viewMap;
	private String tableId;
	
	public OrgDialog(Shell parentShell, String tableId) {
		super(parentShell);
		this.tableId = tableId;
		initUserInfo();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Message.getString("common.sectionTitle_defaultOrg"));
		setTitle(Message.getString("common.sectionTitle_defaultOrg"));
		
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Composite client = (Composite) super.createDialogArea(parent);
		Composite content = toolkit.createComposite(client);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		content.setLayout(layout);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 20;
		
		toolkit.createLabel(content, Message.getString("common.defaultOrg"));

		defaultOrg = new CCombo(content, SWT.BORDER | SWT.READ_ONLY);
        toolkit.adapt(defaultOrg);
        toolkit.paintBordersFor(defaultOrg);
		defaultOrg.setLayoutData(gd);
		
		toolkit.createLabel(content, Message.getString("common.defaultLanguage"));
		
		defaultLanguage = new CCombo(content, SWT.BORDER | SWT.READ_ONLY);
		toolkit.adapt(defaultLanguage);
        toolkit.paintBordersFor(defaultLanguage);
		defaultLanguage.setLayoutData(gd);
		
		toolkit.createLabel(content, Message.getString("common.defaultView"));
		
		defaultView = new CCombo(content, SWT.BORDER | SWT.READ_ONLY);
		toolkit.adapt(defaultView);
        toolkit.paintBordersFor(defaultView);
		defaultView.setLayoutData(gd);
		
		toolkit.createLabel(content, Message.getString("common.isShowLauncher"));
		isShowLauncher = toolkit.createButton(content, "", SWT.CHECK);
		isShowLauncher.setLayoutData(gd);
		
		initContent();
		return content;
	}

	protected void initContent() {
		if(user != null) {
			Object obj = PropertyUtil.getPropertyForIField(user, ORGS);
			List<ADOrg> list = (List<ADOrg>)obj;
			orgMap = new LinkedHashMap<String, String>();
			for(ADOrg org : list) {
				orgMap.put(org.getName(), org.getObjectId().toString());
			}
			defaultOrg.setItems(orgMap.keySet().toArray(new String[]{}));
			if(user.getDefaultOrgId() != null) {
				String defOrgId = user.getDefaultOrgId().toString();
				String defaultOrgName = null;
				for(Entry<String,String> entry : orgMap.entrySet()){
					if(entry.getValue().equals(defOrgId)){
						defaultOrgName = entry.getKey();
					}
				}
				defaultOrg.setText(defaultOrgName);
			}
			
			languageMap = new LinkedHashMap<String, String>();
			viewMap = new LinkedHashMap<String, String>();
			try {
				ADManager entityManager = Framework.getService(ADManager.class);
				List<ADRefList> refList = entityManager.getADRefList(Env.getOrgId(),
						REF_LANGUAGE);
				for (ADRefList listItem : refList) {
					languageMap.put(listItem.getValue(), listItem.getName());
				}				
				defaultLanguage.setItems(languageMap.values().toArray(new String[] {}));
				if(user.getDefLanguage() != null) {
					defaultLanguage.setText(languageMap.get(user.getDefLanguage()));
				}
				
				refList = entityManager.getADRefList(Env.getOrgId(),
						REF_VIEW);
				for (ADRefList listItem : refList) {
					viewMap.put(listItem.getValue(), listItem.getName());
				}
				defaultView.setItems(viewMap.values().toArray(new String[] {}));
				if(user.getDefaultView() != null){
					defaultView.setText(viewMap.get(user.getDefaultView()));
				}
			} catch (Exception e) {
				logger.error("OrgDialog : initContent()", e);
			}
						
			
			obj = PropertyUtil.getPropertyForIField(user, IS_SHOWLAUNCHER);
			if(obj != null) {
				isShowLauncher.setSelection((Boolean)obj);					
			}
		}
	}
	
	public void initUserInfo() {
		try {
			user = new ADUser();
			user.setObjectId(Env.getUserId());
			ADManager entityManager = Framework.getService(ADManager.class);
			user = (ADUser)entityManager.getEntity(user);
		} catch(Exception e) {
			logger.error("Error at OrgSection InitUserInfo() : " + e);
		}
	}

	@Override
	protected void okPressed() {
		if(validate()) {
			if(user != null ) {
				try {
					if(orgMap != null && orgMap.size() > 0 && defaultOrg.getText() != null) {
						String id = orgMap.get(defaultOrg.getText());
						if(id != null ) {
							long defOrgId = Long.parseLong(id);
							user.setDefaultOrgId(defOrgId);
						}
					} else {
						return;
					}
					
					if(languageMap != null && languageMap.size() > 0 && defaultLanguage.getText() != null){
						String defLanguage = defaultLanguage.getText();
						for(String key : languageMap.keySet()){
							if(defLanguage.equals(languageMap.get(key))){
								user.setDefLanguage(key);
							}
						}						
					}
					
					if(viewMap != null && viewMap.size() > 0 && defaultView.getText() != null){
						String defView = defaultView.getText();
						for(String key : viewMap.keySet()){
							if(defView.equals(viewMap.get(key))){
								user.setDefaultView(key);
							}
						}
					}
					
					user.setIsShowLauncher(isShowLauncher.getSelection());
					ADManager entityManager = Framework.getService(ADManager.class);
					entityManager.saveEntity(Long.valueOf(tableId), user, Env.getUserId());
					UI.showInfo(Message.getString("common.setDefOrgId_success"));
				} catch(Exception e) {
					logger.error("InitUserInfo Error: " + e);
					UI.showError(Message.getString("common.setDefOrgId_failure"));
				}
				refresh();
			}
		}
	}
	
	public void refresh(){
		initUserInfo();
		initContent();
		setFocus();
	}
	
	public boolean validate(){
//		isShowLauncher.getSelection();
//		boolean launcherIsNull = GenericValidator.isBlankOrNull(isShowLauncher.getText());
//		if(launcherIsNull){
//			mmng.addMessage(isShowLauncher, 
//					String.format(Message.getString("common.ismandatry"),
//					Message.getString("common.oldPassword")), null, IMessageProvider.ERROR, isShowLauncher);
//			return false;
//		}
		return true;
	}
	
	public void setFocus(){
		defaultOrg.setFocus();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
}

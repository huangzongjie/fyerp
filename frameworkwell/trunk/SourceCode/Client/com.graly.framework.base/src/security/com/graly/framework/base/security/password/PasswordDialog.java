package com.graly.framework.base.security.password;

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.security.util.PasswordUtil;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.GenericValidator;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.model.ADUser;

public class PasswordDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(PasswordDialog.class);
	public static final String DIALOG_ID = "com.graly.framework.base.security.password.PasswordDialog";
	protected Text txtOldPw, txtNewPw, txtConfirmPw;
	private final static String OLD = "Old";
	private final static String NEW = "New";
	private final static String CONFIRM = "Confirm";
	private final static String NONE = "";
	
	private String tableId;
	protected String errorSource = "";
	private ADUser user;
	
	public PasswordDialog(Shell parentShell, String tableId) {
		super(parentShell);
		this.tableId = tableId;
		initUserInfo();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		int TextLimit = 32;
		getShell().setText(Message.getString("common.title_changePassword"));
		setTitle(Message.getString("common.title_changePassword"));
		
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Composite client = (Composite) super.createDialogArea(parent);
		Composite content = toolkit.createComposite(client);
		GridLayout layout = new GridLayout();
//		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;	
		content.setLayout(layout);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
		Label lbName = toolkit.createLabel(content, "", SWT.NULL);
		lbName.setText(Message.getString("common.username"));
//		lbName.setFont(SWTResourceCache.getFont("Verdana"));
		GridData gd = new GridData();
//		gd.horizontalSpan = 2;
		gd.horizontalAlignment = GridData.FILL_HORIZONTAL;
		gd.minimumWidth = 80;
//		gd.widthHint = 100;
		lbName.setLayoutData(gd);
		lbName.setParent(content);
		
		GridData txtGd = new GridData(GridData.FILL_HORIZONTAL);
		
		Label lbNameVal = toolkit.createLabel(content, "", SWT.NULL);
		lbNameVal.setText(Env.getUserName());
		lbNameVal.setFont(SWTResourceCache.getFont("Verdana"));
		lbNameVal.setEnabled(false);
		
		Label lbOldPass = toolkit.createLabel(content, "", SWT.NULL);
		lbOldPass.setText(Message.getString("common.oldPassword"));
//		lbOldPass.setFont(SWTResourceCache.getFont("Verdana"));
		lbOldPass.setLayoutData(gd);
		lbOldPass.setParent(content);
		

		txtOldPw = toolkit.createText(content, "", SWT.BORDER | SWT.PASSWORD);
		txtOldPw.setTextLimit(TextLimit);
		txtOldPw.setParent(content);
		txtOldPw.setLayoutData(txtGd);
		
		Label lbNewPass = toolkit.createLabel(content, "", SWT.NULL);
		lbNewPass.setText(Message.getString("common.newPassword"));
//		lbNewPass.setFont(SWTResourceCache.getFont("Verdana"));
		lbNewPass.setLayoutData(gd);
		lbNewPass.setParent(content);
		
		txtNewPw = toolkit.createText(content, "", SWT.BORDER | SWT.PASSWORD);
		txtNewPw.setTextLimit(TextLimit);
		txtNewPw.setParent(content);
		txtNewPw.setLayoutData(txtGd);
				
		Label lbConfirmPass = toolkit.createLabel(content, "", SWT.NULL);
		lbConfirmPass.setText(Message.getString("common.confirmPassword"));
//		lbConfirmPass.setFont(SWTResourceCache.getFont("Verdana"));
		lbConfirmPass.setLayoutData(gd);
		lbConfirmPass.setParent(content);
		
		txtConfirmPw = toolkit.createText(content, "", SWT.BORDER | SWT.PASSWORD);
		txtConfirmPw.setTextLimit(TextLimit);
		txtConfirmPw.setParent(content);
		txtConfirmPw.setLayoutData(txtGd);
				
		txtOldPw.addFocusListener(getOldPwListenter());
		txtNewPw.addFocusListener(getNewPwListener());
		txtConfirmPw.addFocusListener(getConfirmPwListener());
		
		return content;
	}
	
	protected FocusListener getOldPwListenter() {
		return new FocusListener() {
			public void focusGained(FocusEvent e) {
				if(OLD.equals(errorSource)){
					setMessage("");
				}
			}
			public void focusLost(FocusEvent e) {
				validate();
			}
		};
	}
	
	protected FocusListener getNewPwListener() {
		return new FocusListener() {
			public void focusGained(FocusEvent e) {
				if(NEW.equals(errorSource)){
					setMessage("");
				}
			}
			public void focusLost(FocusEvent e) {
				validate();
			}
		};
	}
	
	protected FocusListener getConfirmPwListener() {
		return new FocusListener() {
			public void focusGained(FocusEvent e) {
				if(CONFIRM.equals(errorSource)){
					setMessage("");
				}
			}
			public void focusLost(FocusEvent e) {
				validate();
			}
		};
	}
	
	private boolean judgeEqualsOldPassword() {
		String oldPw = txtOldPw.getText();
		if(user != null) {
			if(!user.getPassword().equals(oldPw)) {					
				setMessage(Message.getString("common.oldPw_incorrect"),IMessageProvider.ERROR);
				errorSource = OLD;
				return false;
			} else {
				setMessage("");
			}
		}
		return true;
	}
	
	private boolean judgeEqualsNewPassword() {
		String newPw = txtNewPw.getText();
		String confirmPw = txtConfirmPw.getText();
		
		if(!confirmPw.equals(newPw)) {
			setMessage(Message.getString("common.password_inconsistent"), IMessageProvider.ERROR);
			errorSource = CONFIRM;
			return false;
		}
		return true;
	};
	
	public boolean validate(){
		boolean oldIsNull = GenericValidator.isBlankOrNull(txtOldPw.getText());
		boolean newIsNull = GenericValidator.isBlankOrNull(txtNewPw.getText());
		boolean confirmIsNull = GenericValidator.isBlankOrNull(txtConfirmPw.getText());
		
		if(oldIsNull){
			setMessage(String.format(Message.getString("common.ismandatory"),
							Message.getString("common.oldPassword")), IMessageProvider.ERROR);
			errorSource = OLD;
			return false;
		} else {
			if(!judgeEqualsOldPassword()) {
				return false;
			}
		}
		if(newIsNull){
			setMessage(String.format(Message.getString("common.ismandatory"),
							Message.getString("common.newPassword")), IMessageProvider.ERROR);
			errorSource = NEW;
			return false;		
		} else {
			if(confirmIsNull) {
				setMessage(String.format(Message.getString("common.ismandatory"),
						Message.getString("common.confirmPassword")), IMessageProvider.ERROR);
				errorSource = CONFIRM;
				return false;		
			} else {
				if(!judgeEqualsNewPassword()) {
					return false;
				}
			}
		}
		errorSource = NONE;
		return true;
	}
	
	public boolean isChangedPassword() {
		if(!txtOldPw.getText().equals(txtNewPw.getText())) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void constrainShellSize() {
		Shell parent = getParentShell();
		//使窗口位于屏幕中央显示
		int width = parent.getBounds().width;
		int height = parent.getBounds().height;
		int x = Math.abs((getShell().getBounds().width - width))/2;
		int y = Math.abs((getShell().getBounds().height - height))/2;
		
		getShell().setLocation(x, y);
	}

	@Override
	protected void okPressed() {
		if(validate() && checkPWD()) {
			if(user != null ) {
				user.setPassword(txtNewPw.getText());
				if(isChangedPassword()) {
					Date now = Env.getSysDate();
					user.setPwdChanged(now);
					try {
						ADManager entityManager = Framework.getService(ADManager.class);
						entityManager.saveEntity(user, Env.getUserRrn());
						UI.showInfo(Message.getString("common.changePassword_success"));
					} catch(Exception e) {
						logger.error("InitUserInfo Error: " + e);
						UI.showError(Message.getString("common.changePassword_failure"));
					}
					setReturnCode(OK);
					close();
				}
			}
		}
	}
	
	private boolean checkPWD() {
		String pwd = txtNewPw.getText();
		StringBuffer msg = new StringBuffer();
		boolean flag = PasswordUtil.checkPWD(pwd, msg);
		if(!flag){
			setMessage(msg.toString(), IMessageProvider.ERROR);
		}
		return flag;
	}

	public void refresh(){
		initUserInfo();
		txtOldPw.setText("");
		txtNewPw.setText("");
		txtConfirmPw.setText("");
		setMessage("");
		setFocus();
	}
	
	public void initUserInfo() {
		try {
			user = new ADUser();
			user.setObjectRrn(Env.getUserRrn());
			ADManager entityManager = Framework.getService(ADManager.class);
			user = (ADUser)entityManager.getEntity(user);
		} catch(Exception e) {
			logger.error("Error at PasswordDialog InitUserInfo() : " + e);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
	
	public void setFocus(){
		txtOldPw.setFocus();
	}
}

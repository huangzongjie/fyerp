package com.graly.promisone.base.security.login;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.util.SWTResourceCache;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.security.model.ADOrg;
import com.graly.promisone.security.model.ADUser;

public class ChangeAreaDialog extends TitleAreaDialog {
	private final static Logger logger = Logger.getLogger(ChangeAreaDialog.class);
	private CCombo defOrgCombo;
	private ADUser user;
	private static String ORGS = "orgs";
	LinkedHashMap<String, String> map;
	private String changedArea; 
	
	public ChangeAreaDialog(Shell parent) {
		super(parent);
	}
	
	public ChangeAreaDialog(Shell parent, ADUser user) {
		super(parent);
		this.user = user;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
        setTitleImage(SWTResourceCache.getImage("search-dialog"));
        setTitle(Message.getString("common.title_areaSelection"));
        setMessage(Message.getString("common.info_areaSelection"));
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite content = new Composite(composite, SWT.BORDER);
        GridLayout gl = new GridLayout(1, true);
        gl.verticalSpacing = 40;
        content.setLayout(gl);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Composite comp = new Composite(content, SWT.NULL);
        GridLayout compGl = new GridLayout(2, false);
        compGl.horizontalSpacing = 10;
        comp.setLayout(compGl);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Label label = new Label(comp, SWT.NULL);
        label.setText(Message.getString("common.select_area"));
        defOrgCombo = new CCombo(comp, SWT.BORDER | SWT.READ_ONLY);
        defOrgCombo.setLayoutData(new GridData(GridData.FILL_BOTH));
        toolkit.adapt(defOrgCombo);
        toolkit.paintBordersFor(defOrgCombo);

        initContent();
        
        return composite;
    }
	
	protected void initContent() {
		try {
    		ADManager manager = Framework.getService(ADManager.class);
    		user = new ADUser();
    		user.setObjectId(Env.getUserId());
    		user = (ADUser)manager.getEntity(user);
		} catch(Exception e) {
			logger.error("ChangeAreaDialog : initContent() " + e);
		}
		if(user != null) {
			Object obj = PropertyUtil.getPropertyForIField(user, ORGS);
			List<ADOrg> list = (List<ADOrg>)obj;
			List<String> orgNames = new ArrayList<String>();
			Long currentOrgId = Env.getOrgId();
			map = new LinkedHashMap<String, String>();
			int i = 0, index = 0;
			for(ADOrg org : list) {
				map.put(org.getName(), org.getObjectId().toString());
				orgNames.add(org.getName());
				if(currentOrgId != null) {
					if(currentOrgId.equals(org.getObjectId())) {
						index = i;
					}
				}
				i++;
			}
			defOrgCombo.setItems(orgNames.toArray(new String[]{}));
			defOrgCombo.setText(orgNames.get(index));
		}
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.OK_ID) {
			changedArea = defOrgCombo.getText();
		}
		super.buttonPressed(buttonId);
	}

	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }
	
	public Long getChangedAreaObjectId() {
		if(changedArea != null && map != null) {
			String id = map.get(changedArea);
			return new Long(Long.parseLong(id));
		}
		return null;
	}
}
